package rest.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.service.SupportEventService;
import util.Constant;

/**
 * Reprise de l'historique : convertit les documents JSON stock_snapshot.stock_journalier en lignes stock_snapshot_day.
 *
 * <p>
 * Le traitement lit la table par pages, n'analyse chaque document qu'une seule fois, ecrit par lots et memorise sa
 * progression : il peut etre arrete (arret du serveur, coupure) et repartir ou il en etait. Il tourne de nuit, jamais
 * dans un thread HTTP : la reprise ne doit pas se declencher parce qu'un utilisateur a ouvert un ecran.
 * </p>
 *
 * <p>
 * Seules les journees conservees par la regle de retention sont reprises. Reprendre les ~190 journees presentes dans
 * les documents reviendrait a ecrire plus de 4 millions de lignes dont la quasi-totalite serait supprimee par la
 * premiere purge.
 * </p>
 *
 * @author koben
 */
@Stateless
public class StockSnapshotBackfillService {

    private static final Logger LOG = Logger.getLogger(StockSnapshotBackfillService.class.getName());

    /** Fenetre glissante conservee au jour le jour, en plus des cloture de mois. */
    public static final int RETENTION_JOURS = 90;

    /** Nombre de documents JSON lus par page. Chaque document pese plusieurs Ko : on garde des pages courtes. */
    private static final int TAILLE_PAGE = 100;

    /**
     * Nombre de documents parmi les plus volumineux servant a reconstituer le calendrier des journees relevees. Un
     * produit present depuis l'origine porte toutes les journees ou le traitement a tourne ; en prendre plusieurs
     * protege du cas ou l'un d'eux serait tronque ou invalide.
     */
    private static final int DOCUMENTS_CALENDRIER = 5;

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @EJB
    private StockSnapshotBackfillService self;

    @EJB
    private StockSnapshotDayService stockSnapshotDayService;

    @EJB
    private SupportEventService supportEventService;

    /**
     * Lance (ou poursuit) la reprise. Sans effet une fois la reprise terminee : le traitement peut donc etre planifie
     * chaque nuit sans precaution particuliere.
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executerAsync() {
        try {
            Map<String, Object> etat = lireEtat();
            if (etat == null) {
                LOG.warning("Reprise historique : table stock_snapshot_backfill absente, reprise ignoree.");
                return;
            }
            if (estTerminee(etat)) {
                return;
            }

            Set<Integer> journees = journeesRetenues(etat);
            if (journees.isEmpty()) {
                LOG.warning("Reprise historique : aucune journee a reprendre (historique JSON vide ?), reprise close.");
                self.cloturer();
                return;
            }
            LOG.log(Level.INFO, "Reprise historique demarree : {0} journees retenues sur les {1} derniers jours + "
                    + "cloture de chaque mois.", new Object[] { journees.size(), RETENTION_JOURS });

            String dernierId = (String) etat.get("dernier_produit_id");
            String suite;
            do {
                suite = self.traiterPage(dernierId, journees);
                if (suite != null) {
                    dernierId = suite;
                }
            } while (suite != null);

            self.cloturer();
            Map<String, Object> bilan = lireEtat();
            LOG.log(Level.INFO, "Reprise historique terminee : {0} documents lus, {1} invalides, {2} lignes importees.",
                    new Object[] { bilan.get("documents_lus"), bilan.get("documents_invalides"),
                            bilan.get("lignes_importees") });
            supportEventService.recordJobRun("REPRISE_SNAPSHOT_DAY");

        } catch (Exception e) {
            // La reprise reprendra a la page suivante au prochain declenchement : la progression est enregistree.
            LOG.log(Level.SEVERE, "Reprise historique interrompue", e);
        }
    }

    /**
     * Traite une page de documents dans une transaction courte dediee.
     *
     * @param dernierId
     *            dernier identifiant traite, {@code null} au premier appel
     * @param journees
     *            journees a reprendre
     *
     * @return l'identifiant du dernier document de la page, ou {@code null} s'il n'y avait plus rien a traiter
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @SuppressWarnings("unchecked")
    public String traiterPage(String dernierId, Set<Integer> journees) {

        List<Object[]> page = em
                .createNativeQuery("SELECT ss.id, ss.produit_id, ss.stock_journalier FROM stock_snapshot ss "
                        + "WHERE ss.id > ?1 ORDER BY ss.id LIMIT " + TAILLE_PAGE)
                .setParameter(1, dernierId == null ? "" : dernierId).getResultList();

        if (page.isEmpty()) {
            return null;
        }

        List<StockSnapshotDayService.Ligne> lignes = new ArrayList<>();
        int invalides = 0;
        String fin = null;

        for (Object[] ligne : page) {
            fin = ligne[0] == null ? fin : ligne[0].toString();
            String produitId = ligne[1] == null ? null : ligne[1].toString();
            String json = enChaine(ligne[2]);
            if (produitId == null || json == null || json.isEmpty()) {
                invalides++;
                continue;
            }
            try {
                lignes.addAll(convertir(produitId, json, journees));
            } catch (Exception e) {
                invalides++;
                LOG.log(Level.WARNING, "Reprise historique : document illisible pour le produit " + produitId, e);
            }
        }

        int importees = stockSnapshotDayService.upsert(lignes);
        enregistrerProgression(fin, page.size(), invalides, importees);
        return fin;
    }

    /** Convertit un document JSON en lignes de releve, en ne retenant que les journees conservees. */
    private List<StockSnapshotDayService.Ligne> convertir(String produitId, String json, Set<Integer> journees) {
        List<StockSnapshotDayService.Ligne> lignes = new ArrayList<>();
        JSONArray tableau = new JSONArray(json);
        for (int i = 0; i < tableau.length(); i++) {
            JSONObject jour = tableau.optJSONObject(i);
            if (jour == null) {
                continue;
            }
            int stockOfDay = jour.optInt("stockOfDay", 0);
            if (!journees.contains(stockOfDay)) {
                continue;
            }
            lignes.add(new StockSnapshotDayService.Ligne(stockOfDay, Constant.OFFICINE, produitId,
                    jour.optInt("qty", 0), jour.optInt("qtyReserve", 0), jour.optInt("prixPaf", 0),
                    jour.optInt("prixUni", 0), jour.optInt("prixMoyentpondere", 0),
                    // Le document JSON ne porte pas le taux de TVA : les lignes reprises le laissent a 0. Les releves
                    // ecrits a partir de maintenant le figent (voir DailyStockService).
                    0));
        }
        return lignes;
    }

    /**
     * Journees a reprendre : la fenetre glissante des {@value #RETENTION_JOURS} derniers jours, plus, pour chaque mois,
     * la premiere et la derniere journee effectivement relevees.
     *
     * <p>
     * La regle est volontairement exprimee sur les journees REELLEMENT relevees, et non sur des numeros de jour fixes
     * (27 a 3) : la pharmacie ferme, le poste est parfois eteint, et fevrier n'a pas le meme dernier jour que mars.
     * Prendre le dernier releve du mois donne la cloture du mois meme si le 30 et le 31 sont manquants ; prendre le
     * premier releve du mois suivant donne la cloture exacte du mois precedent, puisque le releve de 00:05 decrit le
     * stock a la fermeture de la veille.
     * </p>
     */
    private Set<Integer> journeesRetenues(Map<String, Object> etat) {
        // Une reprise deja commencee rejoue le calendrier calcule au demarrage : la reprise reste homogene meme si
        // elle s'etale sur plusieurs nuits.
        String memorisees = (String) etat.get("journees_retenues");
        if (memorisees != null && !memorisees.trim().isEmpty()) {
            Set<Integer> jours = new TreeSet<>();
            for (String valeur : memorisees.split(",")) {
                try {
                    jours.add(Integer.parseInt(valeur.trim()));
                } catch (NumberFormatException e) {
                    // valeur parasite : ignoree, le calendrier reste exploitable
                }
            }
            if (!jours.isEmpty()) {
                return jours;
            }
        }

        int limite = Integer
                .parseInt(LocalDate.now().minusDays(RETENTION_JOURS).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Set<Integer> retenues = journeesConservees(calendrierReleve(), limite);
        self.memoriserJournees(retenues);
        return retenues;
    }

    /**
     * Regle de retention, isolee de toute base pour rester verifiable : une journee est conservee si elle tombe dans la
     * fenetre glissante, ou si c'est la premiere ou la derniere journee relevee de son mois.
     *
     * <p>
     * {@code jour / 100} donne le mois d'une journee au format yyyyMMdd. Aucun numero de jour n'est ecrit en dur :
     * fevrier, les mois de 30 ou 31 jours et les periodes de fermeture se traitent d'eux-memes, puisque la regle porte
     * sur les journees effectivement relevees et non sur le calendrier theorique.
     * </p>
     *
     * @param calendrier
     *            journees effectivement relevees, au format yyyyMMdd
     * @param limite
     *            premiere journee de la fenetre glissante, au format yyyyMMdd
     *
     * @return journees a conserver, triees
     */
    static Set<Integer> journeesConservees(Set<Integer> calendrier, int limite) {
        Map<Integer, Integer> premiereDuMois = new HashMap<>();
        Map<Integer, Integer> derniereDuMois = new HashMap<>();
        for (Integer jour : calendrier) {
            int mois = jour / 100;
            premiereDuMois.merge(mois, jour, Math::min);
            derniereDuMois.merge(mois, jour, Math::max);
        }

        Set<Integer> retenues = new TreeSet<>();
        for (Integer jour : calendrier) {
            int mois = jour / 100;
            // intValue() : comparer deux Integer par == teste les references, ce qui ne tient pas au-dela du cache
            // des petites valeurs. Une journee vaut plus de 20 000 000.
            if (jour >= limite || jour.intValue() == premiereDuMois.get(mois).intValue()
                    || jour.intValue() == derniereDuMois.get(mois).intValue()) {
                retenues.add(jour);
            }
        }
        return retenues;
    }

    /**
     * Reconstitue l'ensemble des journees relevees a partir des documents les plus volumineux : un produit suivi depuis
     * l'origine porte une entree par journee ou le traitement a tourne.
     */
    @SuppressWarnings("unchecked")
    private Set<Integer> calendrierReleve() {
        Set<Integer> jours = new TreeSet<>();
        List<Object> documents = em.createNativeQuery("SELECT ss.stock_journalier FROM stock_snapshot ss "
                + "WHERE ss.stock_journalier IS NOT NULL ORDER BY LENGTH(ss.stock_journalier) DESC LIMIT "
                + DOCUMENTS_CALENDRIER).getResultList();
        for (Object document : documents) {
            String json = enChaine(document);
            if (json == null || json.isEmpty()) {
                continue;
            }
            try {
                JSONArray tableau = new JSONArray(json);
                for (int i = 0; i < tableau.length(); i++) {
                    JSONObject jour = tableau.optJSONObject(i);
                    if (jour != null && jour.optInt("stockOfDay", 0) > 0) {
                        jours.add(jour.optInt("stockOfDay"));
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Reprise historique : document de reference illisible", e);
            }
        }
        return jours;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> lireEtat() {
        try {
            List<Object[]> lignes = em
                    .createNativeQuery(
                            "SELECT dernier_produit_id, documents_lus, documents_invalides, lignes_importees, "
                                    + "journees_retenues, termine FROM stock_snapshot_backfill WHERE id = 1")
                    .getResultList();
            if (lignes.isEmpty()) {
                return null;
            }
            Object[] l = lignes.get(0);
            Map<String, Object> etat = new HashMap<>();
            etat.put("dernier_produit_id", l[0] == null ? null : l[0].toString());
            etat.put("documents_lus", l[1]);
            etat.put("documents_invalides", l[2]);
            etat.put("lignes_importees", l[3]);
            etat.put("journees_retenues", l[4] == null ? null : l[4].toString());
            etat.put("termine", l[5]);
            return etat;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Reprise historique : etat illisible", e);
            return null;
        }
    }

    private boolean estTerminee(Map<String, Object> etat) {
        Object termine = etat.get("termine");
        return termine instanceof Number && ((Number) termine).intValue() == 1;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cloturer() {
        em.createNativeQuery("UPDATE stock_snapshot_backfill SET termine = 1 WHERE id = 1").executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enregistrerProgression(String dernierId, int lus, int invalides, int importees) {
        em.createNativeQuery("UPDATE stock_snapshot_backfill SET dernier_produit_id = ?1, "
                + "documents_lus = documents_lus + ?2, documents_invalides = documents_invalides + ?3, "
                + "lignes_importees = lignes_importees + ?4, "
                + "started_at = COALESCE(started_at, CURRENT_TIMESTAMP) WHERE id = 1").setParameter(1, dernierId)
                .setParameter(2, lus).setParameter(3, invalides).setParameter(4, importees).executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void memoriserJournees(Set<Integer> journees) {
        StringBuilder liste = new StringBuilder();
        for (Integer jour : journees) {
            if (liste.length() > 0) {
                liste.append(",");
            }
            liste.append(jour);
        }
        em.createNativeQuery("UPDATE stock_snapshot_backfill SET journees_retenues = ?1 WHERE id = 1")
                .setParameter(1, liste.toString()).executeUpdate();
    }

    /** La colonne stock_journalier remonte en String ou en byte[] selon le pilote : on normalise. */
    private String enChaine(Object valeur) {
        if (valeur instanceof Object[]) {
            Object[] tableau = (Object[]) valeur;
            valeur = tableau.length > 0 ? tableau[0] : null;
        }
        if (valeur == null) {
            return null;
        }
        if (valeur instanceof String) {
            return (String) valeur;
        }
        if (valeur instanceof byte[]) {
            return new String((byte[]) valeur, StandardCharsets.UTF_8);
        }
        return valeur.toString();
    }
}
