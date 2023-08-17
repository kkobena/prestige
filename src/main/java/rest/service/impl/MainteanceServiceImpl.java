package rest.service.impl;

import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TGrossiste;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;
import rest.service.MainteanceService;
import rest.service.dto.DoublonsDTO;
import util.DateUtil;
import util.FunctionUtils;

/**
 *
 * @author koben
 */
@Stateless
public class MainteanceServiceImpl implements MainteanceService {

    private static final Logger LOG = Logger.getLogger(MainteanceServiceImpl.class.getName());

    private static final String GROSSISTE_PRODUIT_UNIQUE_CONSTRAINT_REMOVE = " ALTER TABLE t_famille_grossiste DROP CONSTRAINT  un_gros_ci";
    private static final String GROSSISTE_PRODUIT_UNIQUE_CONSTRAINT_QUERY = "ALTER TABLE t_famille_grossiste ADD  CONSTRAINT  const_gros_produit UNIQUE (lg_FAMILLE_ID,lg_GROSSISTE_ID)";

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    private List<DoublonsDTO> familleProduit(Tuple tuple) {

        return this.em.createQuery(
                "SELECT o FROM TFamilleGrossiste o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgGROSSISTEID.lgGROSSISTEID=?2",
                TFamilleGrossiste.class).setParameter(1, tuple.get("produitId", String.class))
                .setParameter(2, tuple.get("grossisteId", String.class)).getResultList().stream()
                .map(this::buildDoublon).collect(Collectors.toList());
    }

    @Override
    public JSONObject getDoublonsFamilleGrossistes() {
        List<DoublonsDTO> list = fetchDoublonsFamilleGrossistes();
        list.sort(Comparator.comparing(DoublonsDTO::getProduitId));
        return FunctionUtils.returnData(list, list.size());
    }

    private List<DoublonsDTO> fetchDoublonsFamilleGrossistes() {
        List<DoublonsDTO> list = new ArrayList<>();
        try {
            ((List<Tuple>) em.createNativeQuery(
                    "SELECT COUNT(f.lg_FAMILLE_ID) AS produit_count, f.lg_FAMILLE_ID AS produitId,f.lg_GROSSISTE_ID AS grossisteId FROM t_famille_grossiste f,t_famille p WHERE f.lg_FAMILLE_ID=p.lg_FAMILLE_ID  GROUP BY f.lg_FAMILLE_ID,f.lg_GROSSISTE_ID HAVING  produit_count>1",
                    Tuple.class).getResultList()).forEach(t -> list.addAll(familleProduit(t)));

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
        return list;
    }

    private DoublonsDTO buildDoublon(TFamilleGrossiste familleGrossiste) {
        TFamille famille = familleGrossiste.getLgFAMILLEID();
        TGrossiste grossiste = familleGrossiste.getLgGROSSISTEID();
        return DoublonsDTO.builder().cip(famille.getIntCIP()).libelle(famille.getStrNAME())
                .produitId(famille.getLgFAMILLEID().concat(grossiste.getLgGROSSISTEID()))
                .statut("enable".equals(familleGrossiste.getStrSTATUT()) ? "Actif" : "Désactivé")
                .codeProduit(familleGrossiste.getStrCODEARTICLE()).id(familleGrossiste.getLgFAMILLEGROSSISTEID())
                .libelleGrossiste(familleGrossiste.getLgGROSSISTEID().getStrLIBELLE())
                .prixAchat(familleGrossiste.getIntPAF()).prixUnitaire(familleGrossiste.getIntPRICE())
                .dateCreation(DateUtil.convertDateToDD_MM_YYYY_HH_mm(familleGrossiste.getDtCREATED()))
                .dateModification(DateUtil.convertDateToDD_MM_YYYY_HH_mm(familleGrossiste.getDtUPDATED())).build();
    }

    @Override
    public void remove(Set<String> ids) {
        ids.stream().forEach(this::remoteFamilleGrossiste);
    }

    @Override
    public void remoteFamilleGrossiste(String id) {
        this.em.remove(this.em.find(TFamilleGrossiste.class, id));
    }

    private void removeOldConstraint() {
        this.em.createNativeQuery(GROSSISTE_PRODUIT_UNIQUE_CONSTRAINT_REMOVE).executeUpdate();

    }

    private void addNexConstraint() {
        this.em.createNativeQuery(GROSSISTE_PRODUIT_UNIQUE_CONSTRAINT_QUERY).executeUpdate();

    }

    @Override
    public void addConstraint() throws Exception {
        List<DoublonsDTO> list = fetchDoublonsFamilleGrossistes();
        if (CollectionUtils.isNotEmpty(list)) {
            throw new Exception("Il existe encore des doublons non supprimés");
        }
        removeOldConstraint();
        addNexConstraint();
    }
}
