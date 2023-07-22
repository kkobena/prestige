/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.SalesParams;
import dal.TDeconditionnement;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TMouvement;
import dal.TMouvementSnapshot;
import dal.TUser;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.DeconditionService;
import rest.service.SuggestionService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author Kobena
 */
@Stateless
public class DeconditionServiceImpl implements DeconditionService {

    private static final Logger LOG = Logger.getLogger(DeconditionServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private SuggestionService suggestionService;

    public EntityManager getEntityManager() {
        return em;
    }

    private Optional<TMouvement> findByDay(TFamille OTFamille, String lgEmpl) {
        try {
            TypedQuery<TMouvement> query = getEntityManager().createQuery(
                    "SELECT o FROM TMouvement o  WHERE o.dtDAY =?1 AND o.lgFAMILLEID.lgFAMILLEID =?2 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?3 AND o.strACTION =?4",
                    TMouvement.class);
            query.setParameter(1, new Date(), TemporalType.DATE);
            query.setParameter(2, OTFamille.getLgFAMILLEID());
            query.setParameter(3, lgEmpl);
            query.setParameter(4, commonparameter.str_ACTION_DECONDITIONNEMENT);
            query.setFirstResult(0).setMaxResults(1);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    private TFamilleStock getTProductItemStock(String lg_FAMILLE_ID, String lg_EMPLACEMENT_ID) {

        TFamilleStock OTProductItemStock = (TFamilleStock) getEntityManager().createQuery(
                "SELECT t FROM TFamilleStock t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable'")
                .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_EMPLACEMENT_ID).setFirstResult(0).setMaxResults(1)
                .getSingleResult();
        getEntityManager().refresh(OTProductItemStock);

        return OTProductItemStock;
    }

    public void updateTMouvement(TMouvement OTMouvement, Integer int_NUMBER) {
        OTMouvement.setStrSTATUT(commonparameter.statut_enable);
        OTMouvement.setDtUPDATED(new Date());
        OTMouvement.setIntNUMBERTRANSACTION(OTMouvement.getIntNUMBERTRANSACTION() + 1);
        OTMouvement.setIntNUMBER(OTMouvement.getIntNUMBER() + int_NUMBER);
        getEntityManager().merge(OTMouvement);

    }

    public void createTMouvement(TFamille OTFamille, TEmplacement OTEmplacement, String str_TYPE_ACTION,
            String str_ACTION, Integer int_NUMBER, TUser user) throws Exception {

        TMouvement OTMouvement = new TMouvement();
        OTMouvement.setLgMOUVEMENTID(UUID.randomUUID().toString());
        OTMouvement.setDtDAY(new Date());
        OTMouvement.setStrSTATUT(commonparameter.statut_enable);
        OTMouvement.setLgFAMILLEID(OTFamille);
        OTMouvement.setLgUSERID(user);
        OTMouvement.setPKey("");
        OTMouvement.setStrACTION(str_ACTION);
        OTMouvement.setStrTYPEACTION(str_TYPE_ACTION);
        OTMouvement.setDtCREATED(new Date());
        OTMouvement.setDtUPDATED(new Date());
        OTMouvement.setLgEMPLACEMENTID(OTEmplacement);
        OTMouvement.setIntNUMBERTRANSACTION(1);
        OTMouvement.setIntNUMBER(int_NUMBER);
        getEntityManager().persist(OTMouvement);

    }

    private Optional<TMouvementSnapshot> findMouvementSnapshotByDay(TFamille OTFamille, String lgEmpl) {
        try {
            TypedQuery<TMouvementSnapshot> query = getEntityManager().createQuery(
                    "SELECT o FROM TMouvementSnapshot o  WHERE o.dtDAY =?1 AND o.lgFAMILLEID.lgFAMILLEID =?2 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?3 AND o.strSTATUT='enable' ",
                    TMouvementSnapshot.class);
            query.setParameter(1, new Date(), TemporalType.DATE);
            query.setParameter(2, OTFamille.getLgFAMILLEID());
            query.setParameter(3, lgEmpl);
            query.setFirstResult(0).setMaxResults(1);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public void updateSnapshotMouvementArticle(TMouvementSnapshot OTMouvementSnapshot, int int_NUMBER) {
        OTMouvementSnapshot.setDtUPDATED(new Date());
        OTMouvementSnapshot.setIntNUMBERTRANSACTION(OTMouvementSnapshot.getIntNUMBERTRANSACTION() + 1);
        OTMouvementSnapshot.setIntSTOCKJOUR(OTMouvementSnapshot.getIntSTOCKJOUR() + int_NUMBER);
        getEntityManager().merge(OTMouvementSnapshot);
    }

    @Override
    public JSONObject deconditionnementVente(SalesParams params) throws JSONException {
        int numberToDecondition = 1;
        JSONObject json = new JSONObject();
        try {
            TUser tu = params.getUserId();
            TEmplacement te = tu.getLgEMPLACEMENTID();
            TFamille OTFamilleChild = getEntityManager().find(TFamille.class, params.getProduitId());
            TFamille OTFamilleParent = getEntityManager().find(TFamille.class, OTFamilleChild.getLgFAMILLEPARENTID());
            Integer qtyDetail = OTFamilleParent.getIntNUMBERDETAIL();
            TFamilleStock OTFamilleStockParent = getTProductItemStock(OTFamilleParent.getLgFAMILLEID(),
                    te.getLgEMPLACEMENTID());
            TFamilleStock OTFamilleStockChild = getTProductItemStock(OTFamilleChild.getLgFAMILLEID(),
                    te.getLgEMPLACEMENTID());
            Integer stockInitDetail = OTFamilleStockChild.getIntNUMBERAVAILABLE();
            Integer stockInit = OTFamilleStockParent.getIntNUMBERAVAILABLE();
            Integer _stockInitDetail = stockInit * qtyDetail;
            if (params.getQte() > _stockInitDetail) {
                json.put("success", false);
                json.put("msg", "L'opération a échoué: Le stock est insuffisant");
                return json;
            }
            Integer x = stockInitDetail + qtyDetail;
            while (params.getQte() > x) {
                numberToDecondition++;
                x += qtyDetail;
            }
            OTFamilleStockParent
                    .setIntNUMBERAVAILABLE(OTFamilleStockParent.getIntNUMBERAVAILABLE() - numberToDecondition);
            OTFamilleStockParent.setIntNUMBER(OTFamilleStockParent.getIntNUMBERAVAILABLE());
            OTFamilleStockParent.setDtUPDATED(new Date());
            OTFamilleStockChild.setIntNUMBERAVAILABLE(
                    OTFamilleStockChild.getIntNUMBERAVAILABLE() + (numberToDecondition * qtyDetail));
            OTFamilleStockChild.setIntNUMBER(OTFamilleStockChild.getIntNUMBERAVAILABLE());
            OTFamilleStockChild.setDtUPDATED(new Date());
            getEntityManager().merge(OTFamilleStockParent);
            getEntityManager().merge(OTFamilleStockChild);
            createDecondtionne(OTFamilleParent, numberToDecondition, params.getUserId());
            createDecondtionne(OTFamilleChild, (numberToDecondition * qtyDetail), params.getUserId());
            Optional<TMouvement> opChild = findByDay(OTFamilleChild, te.getLgEMPLACEMENTID());
            if (opChild.isPresent()) {
                updateTMouvement(opChild.get(), (numberToDecondition * qtyDetail));
            } else {
                createTMouvement(OTFamilleChild, te, commonparameter.ADD, commonparameter.str_ACTION_DECONDITIONNEMENT,
                        (numberToDecondition * qtyDetail), params.getUserId());
            }
            Optional<TMouvement> opParent = findByDay(OTFamilleParent, te.getLgEMPLACEMENTID());
            if (opParent.isPresent()) {
                updateTMouvement(opParent.get(), numberToDecondition);
            } else {
                createTMouvement(OTFamilleParent, te, commonparameter.REMOVE,
                        commonparameter.str_ACTION_DECONDITIONNEMENT, numberToDecondition, params.getUserId());
            }
            Optional<TMouvementSnapshot> mvtChild = findMouvementSnapshotByDay(OTFamilleChild, te.getLgEMPLACEMENTID());
            if (mvtChild.isPresent()) {
                updateSnapshotMouvementArticle(mvtChild.get(), (numberToDecondition * qtyDetail));
            } else {
                createSnapshotMouvementArticle(OTFamilleChild, OTFamilleStockChild.getIntNUMBERAVAILABLE(),
                        stockInitDetail, te);
            }
            Optional<TMouvementSnapshot> mvtparent = findMouvementSnapshotByDay(OTFamilleParent,
                    te.getLgEMPLACEMENTID());
            if (mvtparent.isPresent()) {
                updateSnapshotMouvementArticle(mvtparent.get(), numberToDecondition);
            } else {
                createSnapshotMouvementArticle(OTFamilleParent, OTFamilleStockParent.getIntNUMBERAVAILABLE(), stockInit,
                        te);
            }
            json.put("success", true);
            json.put("msg", "opération effectuée avec success");
            suggestionService.makeSuggestionAuto(OTFamilleStockParent, OTFamilleParent);
            return json;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false);
            json.put("msg", "L'opération a échoué");
            return json;
        }
    }

    public void createSnapshotMouvementArticle(TFamille OTFamille, int int_NUMBER, int int_STOCK_DEBUT,
            TEmplacement OTEmplacement) {

        TMouvementSnapshot OTMouvementSnapshot = new TMouvementSnapshot();
        OTMouvementSnapshot.setLgMOUVEMENTSNAPSHOTID(UUID.randomUUID().toString());
        OTMouvementSnapshot.setLgFAMILLEID(OTFamille);
        OTMouvementSnapshot.setDtDAY(new Date());
        OTMouvementSnapshot.setDtCREATED(new Date());
        OTMouvementSnapshot.setDtUPDATED(new Date());
        OTMouvementSnapshot.setStrSTATUT(commonparameter.statut_enable);
        OTMouvementSnapshot.setIntNUMBERTRANSACTION(1);
        OTMouvementSnapshot.setIntSTOCKJOUR(int_NUMBER);
        OTMouvementSnapshot.setIntSTOCKDEBUT(int_STOCK_DEBUT);
        OTMouvementSnapshot.setLgEMPLACEMENTID(OTEmplacement);
        getEntityManager().persist(OTMouvementSnapshot);
    }

    private void createDecondtionne(TFamille OTFamille, int int_NUMBER, TUser tUser) {

        TDeconditionnement OTDeconditionnement = new TDeconditionnement();
        OTDeconditionnement.setLgDECONDITIONNEMENTID(UUID.randomUUID().toString());
        OTDeconditionnement.setLgFAMILLEID(OTFamille);
        OTDeconditionnement.setLgUSERID(tUser);
        OTDeconditionnement.setIntNUMBER(int_NUMBER);
        OTDeconditionnement.setDtCREATED(new Date());
        OTDeconditionnement.setStrSTATUT(commonparameter.statut_enable);
        getEntityManager().persist(OTDeconditionnement);
    }

}
