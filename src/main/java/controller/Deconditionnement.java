/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.Notification;
import dal.TDeconditionnement;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TMouvement;
import dal.TMouvementSnapshot;
import dal.TUser;
import dal.enumeration.Canal;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.LogService;
import rest.service.MouvementProduitService;
import rest.service.NotificationService;
import rest.service.SuggestionService;
import util.Constant;
import util.DateConverter;

@WebServlet(name = "Deconditionnement", urlPatterns = {"/Deconditionnement"})
public class Deconditionnement extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(Deconditionnement.class.getName());
    @EJB
    private MouvementProduitService mouvementProduitService;
    @EJB
    private LogService logService;
    @EJB
    private NotificationService notificationService;
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private SuggestionService suggestionService;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();

        TUser oTUser = (TUser) session.getAttribute(Constant.AIRTIME_USER);
        JSONObject json = new JSONObject();
        try (PrintWriter out = response.getWriter()) {
            String mode = "";
            Integer intNUMBER = Integer.valueOf(request.getParameter("int_NUMBER_AVAILABLE"));
            String lgFAMILLEID = request.getParameter("lg_FAMILLE_ID");
            if (request.getParameter("mode") != null) {
                mode = request.getParameter("mode");

            }
            try {
                if ("deconditionarticle".equalsIgnoreCase(mode)) {
                    boolean success = doDeconditionnementStock(lgFAMILLEID, intNUMBER, oTUser);
                    json.put("success", (success ? 1 : 0));
                    json.put("errors_code", (success ? 1 : 0));
                    json.put("ref", lgFAMILLEID);
                    json.put("errors", (success ? " Opération effectuée avec succès" : " Erreur système !!!! "));
                } else {
                    boolean result = doDeconditionnement(lgFAMILLEID, intNUMBER, oTUser);
                    json.put("success", (result ? 1 : 0));
                    json.put("errors_code", (result ? 1 : 0));
                    json.put("ref", lgFAMILLEID);
                    json.put("errors", (result ? " Opération effectuée avec succès" : " Erreur système !!!! "));
                }

            } catch (Exception e) {
                LOGGER.log(Level.ALL, null, e);
                try {
                    json.put("success", 0);
                    json.put("errors_code", 0);
                    json.put("ref", lgFAMILLEID);
                    json.put("errors", e.getLocalizedMessage());
                } catch (JSONException ex) {
                }

            }

            out.println(json);

        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private TFamilleStock getTProductItemStock(String lgFAMILLEID, String lgEMPLACEMENTID) {

        TFamilleStock productItemStock = (TFamilleStock) em.createQuery(
                "SELECT t FROM TFamilleStock t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable'")
                .setParameter(1, lgFAMILLEID).setParameter(2, lgEMPLACEMENTID).setFirstResult(0).setMaxResults(1)
                .getSingleResult();

        return productItemStock;
    }

    private boolean doDeconditionnement(String lgFAMILLEID, int intNUMBER, TUser user) {
        boolean result = true;

        int numberToDecondition = 1;

        TEmplacement oEmplacement = user.getLgEMPLACEMENTID();
        try {

            TFamille oTFamilleChild = em.find(TFamille.class, lgFAMILLEID);
            TFamille oTFamilleParent = em.find(TFamille.class, oTFamilleChild.getLgFAMILLEPARENTID());
            Integer qtyDetail = oTFamilleParent.getIntNUMBERDETAIL();

            TFamilleStock oamilleStockParent = getTProductItemStock(oTFamilleParent.getLgFAMILLEID(),
                    oEmplacement.getLgEMPLACEMENTID());
            TFamilleStock oFamilleStockChild = getTProductItemStock(oTFamilleChild.getLgFAMILLEID(),
                    oEmplacement.getLgEMPLACEMENTID());
            Integer stockInitDetail = oFamilleStockChild.getIntNUMBERAVAILABLE();
            Integer stockInit = oamilleStockParent.getIntNUMBERAVAILABLE();
            Integer x = stockInitDetail + qtyDetail;
            while (intNUMBER > x) {
                numberToDecondition++;
                x += qtyDetail;
            }

            oamilleStockParent
                    .setIntNUMBERAVAILABLE(oamilleStockParent.getIntNUMBERAVAILABLE() - numberToDecondition);
            oamilleStockParent.setIntNUMBER(oamilleStockParent.getIntNUMBERAVAILABLE());
            oamilleStockParent.setDtUPDATED(new Date());

            oFamilleStockChild.setIntNUMBERAVAILABLE(oFamilleStockChild.getIntNUMBERAVAILABLE() + (numberToDecondition * qtyDetail));
            oFamilleStockChild.setIntNUMBER(oFamilleStockChild.getIntNUMBERAVAILABLE());
            oFamilleStockChild.setDtUPDATED(new Date());

            em.merge(oamilleStockParent);
            em.merge(oFamilleStockChild);
            createDecondtionne(oTFamilleParent, numberToDecondition, user);
            createDecondtionne(oTFamilleChild, (numberToDecondition * qtyDetail), user);
            String desc = "Déconditionnement du produit " + oTFamilleParent.getIntCIP() + " "
                    + oTFamilleParent.getStrNAME() + " nombre de boîtes = " + numberToDecondition
                    + " nombre de détails =" + (numberToDecondition * qtyDetail) + " opérateur "
                    + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
            logService.updateItem(user, oTFamilleParent.getIntCIP(), desc, TypeLog.DECONDITIONNEMENT, oTFamilleParent,
                    new Date());
            try {
                TMouvement mouvement = findByDay(oTFamilleChild, oEmplacement.getLgEMPLACEMENTID());
                updateTMouvement(mouvement, (numberToDecondition * qtyDetail));
                mouvement = findByDay(oTFamilleParent, oEmplacement.getLgEMPLACEMENTID());
                updateTMouvement(mouvement, numberToDecondition);
            } catch (Exception e) {
                createTMouvement(oTFamilleChild, oEmplacement, Constant.ADD,
                        Constant.ACTION_DECONDITIONNEMENT, (numberToDecondition * qtyDetail), user);
                createTMouvement(oTFamilleParent, oEmplacement, Constant.REMOVE,
                        Constant.ACTION_DECONDITIONNEMENT, numberToDecondition, user);

            }
            try {
                TMouvementSnapshot mouvementSnapshot = findMouvementSnapshotByDay(oTFamilleChild,
                        oEmplacement.getLgEMPLACEMENTID());
                updateSnapshotMouvementArticle(mouvementSnapshot, (numberToDecondition * qtyDetail));
            } catch (Exception e) {
                createSnapshotMouvementArticle(oTFamilleChild, oFamilleStockChild.getIntNUMBERAVAILABLE(),
                        stockInitDetail, oEmplacement);
            }
            try {
                TMouvementSnapshot mouvementSnapshot = findMouvementSnapshotByDay(oTFamilleParent,
                        oEmplacement.getLgEMPLACEMENTID());
                updateSnapshotMouvementArticle(mouvementSnapshot, numberToDecondition);
            } catch (Exception e) {
                createSnapshotMouvementArticle(oTFamilleParent, oamilleStockParent.getIntNUMBERAVAILABLE(), stockInit,
                        oEmplacement);
            }

            if (oEmplacement.getLgEMPLACEMENTID().equalsIgnoreCase(Constant.PROCESS_SUCCESS)
                    && oTFamilleParent.getBCODEINDICATEUR() == 0) {
                this.suggestionService.makeSuggestionAuto(oamilleStockParent, oTFamilleParent);

            }

        } catch (Exception e) {
            LOGGER.log(Level.ALL, null, e);

            return false;
        }

        return result;
    }
    // fin deconditionnement d'un article

    // liste des articles
    private TDeconditionnement createDecondtionne(TFamille oTFamille, int intNUMBER, TUser tUser) {
        TDeconditionnement oTDeconditionnement = new TDeconditionnement();
        oTDeconditionnement.setLgDECONDITIONNEMENTID(DateConverter.getComplexId());
        oTDeconditionnement.setLgFAMILLEID(oTFamille);
        oTDeconditionnement.setLgUSERID(tUser);
        oTDeconditionnement.setIntNUMBER(intNUMBER);
        oTDeconditionnement.setDtCREATED(new Date());
        oTDeconditionnement.setStrSTATUT(Constant.STATUT_ENABLE);
        em.persist(oTDeconditionnement);
        return oTDeconditionnement;
    }

    private TMouvement findByDay(TFamille oTFamille, String lgEmpl) throws Exception {
        TypedQuery<TMouvement> query = em.createQuery(
                "SELECT o FROM TMouvement o  WHERE o.dtDAY =?1 AND o.lgFAMILLEID.lgFAMILLEID =?2 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?3 AND o.strACTION =?4",
                TMouvement.class);
        query.setParameter(1, new Date(), TemporalType.DATE);
        query.setParameter(2, oTFamille.getLgFAMILLEID());
        query.setParameter(3, lgEmpl);
        query.setParameter(4, Constant.ACTION_DECONDITIONNEMENT);
        query.setFirstResult(0).setMaxResults(1);
        return query.getSingleResult();

    }

    public void createTMouvement(TFamille oFamille, TEmplacement oEmplacement, String strACTION,
            String strACTION1, Integer intNUMBER, TUser user) throws Exception {

        TMouvement oTMouvement = new TMouvement();
        oTMouvement.setLgMOUVEMENTID(DateConverter.getComplexId());
        oTMouvement.setDtDAY(new Date());
        oTMouvement.setStrSTATUT(Constant.STATUT_ENABLE);
        oTMouvement.setLgFAMILLEID(oFamille);
        oTMouvement.setLgUSERID(user);
        oTMouvement.setPKey("");
        oTMouvement.setStrACTION(strACTION1);
        oTMouvement.setStrTYPEACTION(strACTION);
        oTMouvement.setDtCREATED(new Date());
        oTMouvement.setDtUPDATED(oTMouvement.getDtCREATED());
        oTMouvement.setLgEMPLACEMENTID(oEmplacement);
        oTMouvement.setIntNUMBERTRANSACTION(1);
        oTMouvement.setIntNUMBER(intNUMBER);
        em.persist(oTMouvement);

    }

    public void updateTMouvement(TMouvement oouvement, Integer intNUMBER) throws Exception {
        oouvement.setStrSTATUT(Constant.STATUT_ENABLE);
        oouvement.setDtUPDATED(new Date());
        oouvement.setIntNUMBERTRANSACTION(oouvement.getIntNUMBERTRANSACTION() + 1);
        oouvement.setIntNUMBER(oouvement.getIntNUMBER() + intNUMBER);
        em.merge(oouvement);

    }

    private TMouvementSnapshot findMouvementSnapshotByDay(TFamille oTFamille, String lgEmpl)
            throws Exception {
        TypedQuery<TMouvementSnapshot> query = em.createQuery(
                "SELECT o FROM TMouvementSnapshot o  WHERE o.dtDAY =?1 AND o.lgFAMILLEID.lgFAMILLEID =?2 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?3 AND o.strSTATUT='enable' ",
                TMouvementSnapshot.class);
        query.setParameter(1, new Date(), TemporalType.DATE);
        query.setParameter(2, oTFamille.getLgFAMILLEID());
        query.setParameter(3, lgEmpl);
        query.setFirstResult(0).setMaxResults(1);
        return query.getSingleResult();

    }

    public void createSnapshotMouvementArticle(TFamille oTFamille, int intNUMBER, int stockDebut,
            TEmplacement oEmplacement) throws Exception {

        TMouvementSnapshot oMouvementSnapshot = new TMouvementSnapshot();
        oMouvementSnapshot.setLgMOUVEMENTSNAPSHOTID(DateConverter.getComplexId());
        oMouvementSnapshot.setLgFAMILLEID(oTFamille);
        oMouvementSnapshot.setDtCREATED(new Date());
        oMouvementSnapshot.setDtDAY(oMouvementSnapshot.getDtCREATED());
        oMouvementSnapshot.setDtUPDATED(oMouvementSnapshot.getDtCREATED());
        oMouvementSnapshot.setStrSTATUT(Constant.STATUT_ENABLE);
        oMouvementSnapshot.setIntNUMBERTRANSACTION(1);
        oMouvementSnapshot.setIntSTOCKJOUR(intNUMBER);
        oMouvementSnapshot.setIntSTOCKDEBUT(stockDebut);
        oMouvementSnapshot.setLgEMPLACEMENTID(oEmplacement);
        em.persist(oMouvementSnapshot);
    }

    public void updateSnapshotMouvementArticle(TMouvementSnapshot mouvementSnapshot, int intNUMBER)
            throws Exception {
        mouvementSnapshot.setDtUPDATED(new Date());
        mouvementSnapshot.setIntNUMBERTRANSACTION(mouvementSnapshot.getIntNUMBERTRANSACTION() + 1);
        mouvementSnapshot.setIntSTOCKJOUR(mouvementSnapshot.getIntSTOCKJOUR() + intNUMBER);
        em.merge(mouvementSnapshot);
    }

    private boolean doDeconditionnementStock(String lgFAMILLEID, int intNUMBER, TUser user) {
        boolean result = true;

        TEmplacement oEmplacement = user.getLgEMPLACEMENTID();
        try {

            TFamille oTFamilleChild = em.find(TFamille.class, lgFAMILLEID);
            TFamille oTFamilleParent = em.find(TFamille.class, oTFamilleChild.getLgFAMILLEPARENTID());
            Integer qtyDetail = oTFamilleParent.getIntNUMBERDETAIL();
            TFamilleStock oTFamilleStockParent = getTProductItemStock(oTFamilleParent.getLgFAMILLEID(),
                    oEmplacement.getLgEMPLACEMENTID());
            if (oTFamilleStockParent.getIntNUMBERAVAILABLE() < intNUMBER) {
                return false;
            }

            TFamilleStock oTFamilleStockChild = getTProductItemStock(oTFamilleChild.getLgFAMILLEID(),
                    oEmplacement.getLgEMPLACEMENTID());
            Integer stockInitDetail = oTFamilleStockChild.getIntNUMBERAVAILABLE();
            Integer stockInit = oTFamilleStockParent.getIntNUMBERAVAILABLE();

            oTFamilleStockParent.setIntNUMBERAVAILABLE(oTFamilleStockParent.getIntNUMBERAVAILABLE() - intNUMBER);
            oTFamilleStockParent.setIntNUMBER(oTFamilleStockParent.getIntNUMBERAVAILABLE());
            oTFamilleStockParent.setDtUPDATED(new Date());

            oTFamilleStockChild
                    .setIntNUMBERAVAILABLE(oTFamilleStockChild.getIntNUMBERAVAILABLE() + (intNUMBER * qtyDetail));
            oTFamilleStockChild.setIntNUMBER(oTFamilleStockChild.getIntNUMBERAVAILABLE());
            oTFamilleStockChild.setDtUPDATED(new Date());
            em.merge(oTFamilleStockParent);
            em.merge(oTFamilleStockChild);
            TDeconditionnement p = createDecondtionne(oTFamilleParent, intNUMBER, user);
            TDeconditionnement child = createDecondtionne(oTFamilleChild, (intNUMBER * qtyDetail), user);
            mouvementProduitService.saveMvtProduit(p.getLgDECONDITIONNEMENTID(), Constant.DECONDTIONNEMENT_NEGATIF,
                    oTFamilleParent, user, oEmplacement, intNUMBER, stockInit, stockInit - intNUMBER, 0);
            mouvementProduitService.saveMvtProduit(child.getLgDECONDITIONNEMENTID(),
                    Constant.DECONDTIONNEMENT_POSITIF, oTFamilleChild, user, oEmplacement, intNUMBER,
                    stockInitDetail, stockInitDetail + (intNUMBER * qtyDetail), 0);
            String desc = "Déconditionnement du produit " + oTFamilleParent.getIntCIP() + " "
                    + oTFamilleParent.getStrNAME() + " nombre de boîtes = " + intNUMBER + " nombre de détails ="
                    + (intNUMBER * qtyDetail) + " opérateur " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
            logService.updateItem(user, oTFamilleParent.getIntCIP(), desc, TypeLog.DECONDITIONNEMENT, oTFamilleParent,
                    new Date());
            notificationService.save(new Notification().canal(Canal.SMS_EMAIL)
                    .typeNotification(TypeNotification.DECONDITIONNEMENT).message(desc).addUser(user));
            try {
                TMouvement mouvement = findByDay(oTFamilleChild, oEmplacement.getLgEMPLACEMENTID());
                updateTMouvement(mouvement, (intNUMBER * qtyDetail));
                mouvement = findByDay(oTFamilleParent, oEmplacement.getLgEMPLACEMENTID());
                updateTMouvement(mouvement, intNUMBER);
            } catch (Exception e) {
                createTMouvement(oTFamilleChild, oEmplacement, Constant.ADD,
                        Constant.ACTION_DECONDITIONNEMENT, (intNUMBER * qtyDetail), user);
                createTMouvement(oTFamilleParent, oEmplacement, Constant.REMOVE,
                        Constant.ACTION_DECONDITIONNEMENT, intNUMBER, user);
                LOGGER.log(Level.SEVERE, null, e);
            }
            try {
                TMouvementSnapshot mouvementSnapshot = findMouvementSnapshotByDay(oTFamilleChild,
                        oEmplacement.getLgEMPLACEMENTID());
                updateSnapshotMouvementArticle(mouvementSnapshot, (intNUMBER * qtyDetail));
            } catch (Exception e) {
                createSnapshotMouvementArticle(oTFamilleChild, oTFamilleStockChild.getIntNUMBERAVAILABLE(),
                        stockInitDetail, oEmplacement);
                LOGGER.log(Level.SEVERE, null, e);
            }
            try {
                TMouvementSnapshot mouvementSnapshot = findMouvementSnapshotByDay(oTFamilleParent,
                        oEmplacement.getLgEMPLACEMENTID());
                updateSnapshotMouvementArticle(mouvementSnapshot, intNUMBER);
            } catch (Exception e) {
                createSnapshotMouvementArticle(oTFamilleParent, oTFamilleStockParent.getIntNUMBERAVAILABLE(), stockInit,
                        oEmplacement);
                // LOGGER.log(Level.SEVERE, "---------------------- mouvementSnapshot -------------->>>", e);
            }

            if (oEmplacement.getLgEMPLACEMENTID().equalsIgnoreCase(Constant.PROCESS_SUCCESS)
                    && oTFamilleParent.getBCODEINDICATEUR() == 0) {

                this.suggestionService.makeSuggestionAuto(oTFamilleStockParent, oTFamilleParent);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);

            return false;
        }

        return result;
    }

}
