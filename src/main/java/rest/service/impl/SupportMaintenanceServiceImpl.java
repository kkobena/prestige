/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rest.service.SupportEventService;
import rest.service.SupportMaintenanceService;

/**
 * Vidages de maintenance : suppressions en SQL natif, details supprimes AVANT les entetes pour respecter les liens
 * entre tables. Les commandes ne sont touchees que si leur statut est 'is_Process' (commandes en cours, jamais les
 * commandes receptionnees / historisees).
 *
 * @author koben
 */
@Stateless
public class SupportMaintenanceServiceImpl implements SupportMaintenanceService {

    private static final Logger LOG = Logger.getLogger(SupportMaintenanceServiceImpl.class.getName());
    private static final String STATUT_COMMANDE_EN_COURS = "is_Process";

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private SupportEventService supportEventService;

    @Override
    public Map<String, Object> counts() {
        Map<String, Object> counts = new LinkedHashMap<>();
        counts.put("etiquettes", countNative("SELECT COUNT(*) FROM t_etiquette"));
        counts.put("suggestions", countNative("SELECT COUNT(*) FROM t_suggestion_order"));
        counts.put("suggestionDetails", countNative("SELECT COUNT(*) FROM t_suggestion_order_details"));
        counts.put("commandesEnCours",
                countNative("SELECT COUNT(*) FROM t_order WHERE str_STATUT = '" + STATUT_COMMANDE_EN_COURS + "'"));
        counts.put("commandeDetails",
                countNative("SELECT COUNT(*) FROM t_order_detail d JOIN t_order o ON o.lg_ORDER_ID = d.lg_ORDER_ID "
                        + "WHERE o.str_STATUT = '" + STATUT_COMMANDE_EN_COURS + "'"));
        return counts;
    }

    @Override
    public Map<String, Object> vider(String action, String utilisateur) {
        Map<String, Object> resultat = new LinkedHashMap<>();
        switch (action == null ? "" : action) {
        case ACTION_ETIQUETTES: {
            int lignes = em.createNativeQuery("DELETE FROM t_etiquette").executeUpdate();
            resultat.put("lignes", lignes);
            supportEventService.recordMaintenance(action,
                    "Maintenance : vidage des étiquettes (" + lignes + " ligne(s) supprimée(s))", utilisateur);
            break;
        }
        case ACTION_SUGGESTIONS: {
            // Les details referencent la suggestion : suppression des details AVANT les entetes.
            int details = em.createNativeQuery("DELETE FROM t_suggestion_order_details").executeUpdate();
            int entetes = em.createNativeQuery("DELETE FROM t_suggestion_order").executeUpdate();
            resultat.put("details", details);
            resultat.put("lignes", entetes);
            supportEventService.recordMaintenance(action, "Maintenance : vidage des suggestions (" + entetes
                    + " suggestion(s) et " + details + " détail(s) supprimé(s))", utilisateur);
            break;
        }
        case ACTION_COMMANDES_EN_COURS: {
            // Uniquement les commandes en cours (statut 'is_Process') : details AVANT entetes.
            int details = em.createNativeQuery(
                    "DELETE d FROM t_order_detail d JOIN t_order o ON o.lg_ORDER_ID = d.lg_ORDER_ID WHERE o.str_STATUT = ?1")
                    .setParameter(1, STATUT_COMMANDE_EN_COURS).executeUpdate();
            int entetes = em.createNativeQuery("DELETE FROM t_order WHERE str_STATUT = ?1")
                    .setParameter(1, STATUT_COMMANDE_EN_COURS).executeUpdate();
            resultat.put("details", details);
            resultat.put("lignes", entetes);
            supportEventService.recordMaintenance(action, "Maintenance : vidage des commandes en cours (" + entetes
                    + " commande(s) et " + details + " détail(s) supprimé(s))", utilisateur);
            break;
        }
        default:
            throw new IllegalArgumentException("Action de maintenance inconnue : " + action);
        }
        return resultat;
    }

    private long countNative(String sql) {
        try {
            return ((Number) em.createNativeQuery(sql).getSingleResult()).longValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "countNative " + sql, e);
            return -1;
        }
    }
}
