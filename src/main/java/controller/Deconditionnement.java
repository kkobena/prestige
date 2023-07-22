/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bll.commandeManagement.suggestionManagement;
import dal.Notification;
import dal.TDeconditionnement;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TMouvement;
import dal.TMouvementSnapshot;
import dal.TUser;
import dal.dataManager;
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
import toolkits.parameters.commonparameter;
import toolkits.utils.date;
import util.DateConverter;

@WebServlet(name = "Deconditionnement", urlPatterns = { "/Deconditionnement" })
public class Deconditionnement extends HttpServlet {

    private final static Logger LOGGER = Logger.getLogger(Deconditionnement.class.getName());
    @EJB
    private MouvementProduitService mouvementProduitService;
    @EJB
    private LogService logService;
    @EJB
    private NotificationService notificationService;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        dataManager OdataManager = new dataManager();
        OdataManager.initEntityManager();
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        JSONObject json = new JSONObject();
        try (PrintWriter out = response.getWriter()) {
            String mode = "";
            Integer int_NUMBER = Integer.valueOf(request.getParameter("int_NUMBER_AVAILABLE"));
            String lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
            if (request.getParameter("mode") != null) {
                mode = request.getParameter("mode");

            }
            try {

                switch (mode) {
                case "deconditionarticle":
                    boolean _result = doDeconditionnementStock(lg_FAMILLE_ID, int_NUMBER, OTUser, OdataManager);
                    json.put("success", (_result ? 1 : 0));
                    json.put("errors_code", (_result ? 1 : 0));
                    json.put("ref", lg_FAMILLE_ID);
                    json.put("errors", (_result ? " Opération effectuée avec succès" : " Erreur système !!!! "));

                    break;

                default:

                    boolean result = doDeconditionnement(lg_FAMILLE_ID, int_NUMBER, OTUser, OdataManager);
                    json.put("success", (result ? 1 : 0));
                    json.put("errors_code", (result ? 1 : 0));
                    json.put("ref", lg_FAMILLE_ID);
                    json.put("errors", (result ? " Opération effectuée avec succès" : " Erreur système !!!! "));
                    break;

                }

            } catch (Exception e) {
                LOGGER.log(Level.ALL, null, e);
                try {
                    json.put("success", 0);
                    json.put("errors_code", 0);
                    json.put("ref", lg_FAMILLE_ID);
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

    private TFamilleStock getTProductItemStock(String lg_FAMILLE_ID, String lg_EMPLACEMENT_ID, EntityManager em) {

        TFamilleStock OTProductItemStock = (TFamilleStock) em.createQuery(
                "SELECT t FROM TFamilleStock t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable'")
                .setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_EMPLACEMENT_ID).setFirstResult(0).setMaxResults(1)
                .getSingleResult();
        em.refresh(OTProductItemStock);

        return OTProductItemStock;
    }

    private boolean doDeconditionnement(String lg_FAMILLE_ID, int int_NUMBER, TUser user, dataManager manager) {
        boolean result = true;

        int numberToDecondition = 1;
        EntityManager em = manager.getEm();
        TEmplacement OTEmplacement = user.getLgEMPLACEMENTID();
        try {

            TFamille OTFamilleChild = em.find(TFamille.class, lg_FAMILLE_ID);
            TFamille OTFamilleParent = em.find(TFamille.class, OTFamilleChild.getLgFAMILLEPARENTID());
            Integer qtyDetail = OTFamilleParent.getIntNUMBERDETAIL();

            TFamilleStock OTFamilleStockParent = getTProductItemStock(OTFamilleParent.getLgFAMILLEID(),
                    OTEmplacement.getLgEMPLACEMENTID(), em);
            TFamilleStock OTFamilleStockChild = getTProductItemStock(OTFamilleChild.getLgFAMILLEID(),
                    OTEmplacement.getLgEMPLACEMENTID(), em);
            Integer stockInitDetail = OTFamilleStockChild.getIntNUMBERAVAILABLE();
            Integer stockInit = OTFamilleStockParent.getIntNUMBERAVAILABLE();
            Integer x = stockInitDetail + qtyDetail;
            while (int_NUMBER > x) {
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
            em.getTransaction().begin();
            em.merge(OTFamilleStockParent);
            em.merge(OTFamilleStockChild);
            createDecondtionne(OTFamilleParent, numberToDecondition, em, user);
            createDecondtionne(OTFamilleChild, (numberToDecondition * qtyDetail), em, user);
            String desc = "Déconditionnement du produit " + OTFamilleParent.getIntCIP() + " "
                    + OTFamilleParent.getStrNAME() + " nombre de boîtes = " + numberToDecondition
                    + " nombre de détails =" + (numberToDecondition * qtyDetail) + " opérateur "
                    + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
            logService.updateItem(user, OTFamilleParent.getIntCIP(), desc, TypeLog.DECONDITIONNEMENT, OTFamilleParent,
                    new Date());
            try {
                TMouvement mouvement = findByDay(OTFamilleChild, OTEmplacement.getLgEMPLACEMENTID(), em);
                updateTMouvement(mouvement, (numberToDecondition * qtyDetail), em);
                mouvement = findByDay(OTFamilleParent, OTEmplacement.getLgEMPLACEMENTID(), em);
                updateTMouvement(mouvement, numberToDecondition, em);
            } catch (Exception e) {
                createTMouvement(OTFamilleChild, OTEmplacement, commonparameter.ADD,
                        commonparameter.str_ACTION_DECONDITIONNEMENT, (numberToDecondition * qtyDetail), user, em);
                createTMouvement(OTFamilleParent, OTEmplacement, commonparameter.REMOVE,
                        commonparameter.str_ACTION_DECONDITIONNEMENT, numberToDecondition, user, em);

            }
            try {
                TMouvementSnapshot mouvementSnapshot = findMouvementSnapshotByDay(OTFamilleChild,
                        OTEmplacement.getLgEMPLACEMENTID(), em);
                updateSnapshotMouvementArticle(mouvementSnapshot, (numberToDecondition * qtyDetail), em);
            } catch (Exception e) {
                createSnapshotMouvementArticle(OTFamilleChild, OTFamilleStockChild.getIntNUMBERAVAILABLE(),
                        stockInitDetail, OTEmplacement, em);
            }
            try {
                TMouvementSnapshot mouvementSnapshot = findMouvementSnapshotByDay(OTFamilleParent,
                        OTEmplacement.getLgEMPLACEMENTID(), em);
                updateSnapshotMouvementArticle(mouvementSnapshot, numberToDecondition, em);
            } catch (Exception e) {
                createSnapshotMouvementArticle(OTFamilleParent, OTFamilleStockParent.getIntNUMBERAVAILABLE(), stockInit,
                        OTEmplacement, em);
            }
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();

            }
            if (OTEmplacement.getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS)
                    && OTFamilleParent.getBCODEINDICATEUR() == 0) {
                new suggestionManagement(manager, user).makeSuggestionAuto(OTFamilleStockParent, OTFamilleParent);
            }

        } catch (Exception e) {
            LOGGER.log(Level.ALL, null, e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();

            }
            return false;
        }

        return result;
    }
    // fin deconditionnement d'un article

    // liste des articles
    private TDeconditionnement createDecondtionne(TFamille OTFamille, int int_NUMBER, EntityManager em, TUser tUser) {
        date Odate = new date();
        TDeconditionnement OTDeconditionnement = new TDeconditionnement();
        OTDeconditionnement.setLgDECONDITIONNEMENTID(Odate.getComplexId());
        OTDeconditionnement.setLgFAMILLEID(OTFamille);
        OTDeconditionnement.setLgUSERID(tUser);
        OTDeconditionnement.setIntNUMBER(int_NUMBER);
        OTDeconditionnement.setDtCREATED(new Date());
        OTDeconditionnement.setStrSTATUT(commonparameter.statut_enable);
        em.persist(OTDeconditionnement);
        return OTDeconditionnement;
    }

    private TMouvement findByDay(TFamille OTFamille, String lgEmpl, EntityManager em) throws Exception {
        TypedQuery<TMouvement> query = em.createQuery(
                "SELECT o FROM TMouvement o  WHERE o.dtDAY =?1 AND o.lgFAMILLEID.lgFAMILLEID =?2 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?3 AND o.strACTION =?4",
                TMouvement.class);
        query.setParameter(1, new Date(), TemporalType.DATE);
        query.setParameter(2, OTFamille.getLgFAMILLEID());
        query.setParameter(3, lgEmpl);
        query.setParameter(4, commonparameter.str_ACTION_DECONDITIONNEMENT);
        query.setFirstResult(0).setMaxResults(1);
        return query.getSingleResult();

    }

    public void createTMouvement(TFamille OTFamille, TEmplacement OTEmplacement, String str_TYPE_ACTION,
            String str_ACTION, Integer int_NUMBER, TUser user, EntityManager em) throws Exception {
        date Odate = new date();
        TMouvement OTMouvement = new TMouvement();
        OTMouvement.setLgMOUVEMENTID(Odate.getComplexId());
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
        em.persist(OTMouvement);

    }

    public void updateTMouvement(TMouvement OTMouvement, Integer int_NUMBER, EntityManager em) throws Exception {
        OTMouvement.setStrSTATUT(commonparameter.statut_enable);
        OTMouvement.setDtUPDATED(new Date());
        OTMouvement.setIntNUMBERTRANSACTION(OTMouvement.getIntNUMBERTRANSACTION() + 1);
        OTMouvement.setIntNUMBER(OTMouvement.getIntNUMBER() + int_NUMBER);
        em.merge(OTMouvement);

    }

    private TMouvementSnapshot findMouvementSnapshotByDay(TFamille OTFamille, String lgEmpl, EntityManager em)
            throws Exception {
        TypedQuery<TMouvementSnapshot> query = em.createQuery(
                "SELECT o FROM TMouvementSnapshot o  WHERE o.dtDAY =?1 AND o.lgFAMILLEID.lgFAMILLEID =?2 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?3 AND o.strSTATUT='enable' ",
                TMouvementSnapshot.class);
        query.setParameter(1, new Date(), TemporalType.DATE);
        query.setParameter(2, OTFamille.getLgFAMILLEID());
        query.setParameter(3, lgEmpl);
        query.setFirstResult(0).setMaxResults(1);
        return query.getSingleResult();

    }

    public void createSnapshotMouvementArticle(TFamille OTFamille, int int_NUMBER, int int_STOCK_DEBUT,
            TEmplacement OTEmplacement, EntityManager em) throws Exception {
        date Odate = new date();
        TMouvementSnapshot OTMouvementSnapshot = new TMouvementSnapshot();
        OTMouvementSnapshot.setLgMOUVEMENTSNAPSHOTID(Odate.getComplexId());
        OTMouvementSnapshot.setLgFAMILLEID(OTFamille);
        OTMouvementSnapshot.setDtDAY(new Date());
        OTMouvementSnapshot.setDtCREATED(new Date());
        OTMouvementSnapshot.setDtUPDATED(new Date());
        OTMouvementSnapshot.setStrSTATUT(commonparameter.statut_enable);
        OTMouvementSnapshot.setIntNUMBERTRANSACTION(1);
        OTMouvementSnapshot.setIntSTOCKJOUR(int_NUMBER);
        OTMouvementSnapshot.setIntSTOCKDEBUT(int_STOCK_DEBUT);
        OTMouvementSnapshot.setLgEMPLACEMENTID(OTEmplacement);
        em.persist(OTMouvementSnapshot);
    }

    public void updateSnapshotMouvementArticle(TMouvementSnapshot OTMouvementSnapshot, int int_NUMBER, EntityManager em)
            throws Exception {
        OTMouvementSnapshot.setDtUPDATED(new Date());
        OTMouvementSnapshot.setIntNUMBERTRANSACTION(OTMouvementSnapshot.getIntNUMBERTRANSACTION() + 1);
        OTMouvementSnapshot.setIntSTOCKJOUR(OTMouvementSnapshot.getIntSTOCKJOUR() + int_NUMBER);
        em.merge(OTMouvementSnapshot);
    }

    private boolean doDeconditionnementStock(String lg_FAMILLE_ID, int int_NUMBER, TUser user, dataManager manager) {
        boolean result = true;
        EntityManager em = manager.getEm();
        TEmplacement OTEmplacement = user.getLgEMPLACEMENTID();
        try {

            TFamille OTFamilleChild = em.find(TFamille.class, lg_FAMILLE_ID);
            TFamille OTFamilleParent = em.find(TFamille.class, OTFamilleChild.getLgFAMILLEPARENTID());
            Integer qtyDetail = OTFamilleParent.getIntNUMBERDETAIL();
            TFamilleStock OTFamilleStockParent = getTProductItemStock(OTFamilleParent.getLgFAMILLEID(),
                    OTEmplacement.getLgEMPLACEMENTID(), em);
            if (OTFamilleStockParent.getIntNUMBERAVAILABLE() < int_NUMBER) {
                return false;
            }

            TFamilleStock OTFamilleStockChild = getTProductItemStock(OTFamilleChild.getLgFAMILLEID(),
                    OTEmplacement.getLgEMPLACEMENTID(), em);
            Integer stockInitDetail = OTFamilleStockChild.getIntNUMBERAVAILABLE();
            Integer stockInit = OTFamilleStockParent.getIntNUMBERAVAILABLE();

            OTFamilleStockParent.setIntNUMBERAVAILABLE(OTFamilleStockParent.getIntNUMBERAVAILABLE() - int_NUMBER);
            OTFamilleStockParent.setIntNUMBER(OTFamilleStockParent.getIntNUMBERAVAILABLE());
            OTFamilleStockParent.setDtUPDATED(new Date());

            OTFamilleStockChild
                    .setIntNUMBERAVAILABLE(OTFamilleStockChild.getIntNUMBERAVAILABLE() + (int_NUMBER * qtyDetail));
            OTFamilleStockChild.setIntNUMBER(OTFamilleStockChild.getIntNUMBERAVAILABLE());
            OTFamilleStockChild.setDtUPDATED(new Date());
            em.getTransaction().begin();
            em.merge(OTFamilleStockParent);
            em.merge(OTFamilleStockChild);
            TDeconditionnement p = createDecondtionne(OTFamilleParent, int_NUMBER, em, user);
            TDeconditionnement child = createDecondtionne(OTFamilleChild, (int_NUMBER * qtyDetail), em, user);
            mouvementProduitService.saveMvtProduit(p.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_NEGATIF,
                    OTFamilleParent, user, OTEmplacement, int_NUMBER, stockInit, stockInit - int_NUMBER, em, 0);
            mouvementProduitService.saveMvtProduit(child.getLgDECONDITIONNEMENTID(),
                    DateConverter.DECONDTIONNEMENT_POSITIF, OTFamilleChild, user, OTEmplacement, int_NUMBER,
                    stockInitDetail, stockInitDetail + (int_NUMBER * qtyDetail), em, 0);
            String desc = "Déconditionnement du produit " + OTFamilleParent.getIntCIP() + " "
                    + OTFamilleParent.getStrNAME() + " nombre de boîtes = " + int_NUMBER + " nombre de détails ="
                    + (int_NUMBER * qtyDetail) + " opérateur " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
            logService.updateItem(user, OTFamilleParent.getIntCIP(), desc, TypeLog.DECONDITIONNEMENT, OTFamilleParent,
                    new Date());
            notificationService.save(new Notification().canal(Canal.SMS_EMAIL)
                    .typeNotification(TypeNotification.DECONDITIONNEMENT).message(desc).addUser(user));
            try {
                TMouvement mouvement = findByDay(OTFamilleChild, OTEmplacement.getLgEMPLACEMENTID(), em);
                updateTMouvement(mouvement, (int_NUMBER * qtyDetail), em);
                mouvement = findByDay(OTFamilleParent, OTEmplacement.getLgEMPLACEMENTID(), em);
                updateTMouvement(mouvement, int_NUMBER, em);
            } catch (Exception e) {
                createTMouvement(OTFamilleChild, OTEmplacement, commonparameter.ADD,
                        commonparameter.str_ACTION_DECONDITIONNEMENT, (int_NUMBER * qtyDetail), user, em);
                createTMouvement(OTFamilleParent, OTEmplacement, commonparameter.REMOVE,
                        commonparameter.str_ACTION_DECONDITIONNEMENT, int_NUMBER, user, em);
                LOGGER.log(Level.SEVERE, null, e);
            }
            try {
                TMouvementSnapshot mouvementSnapshot = findMouvementSnapshotByDay(OTFamilleChild,
                        OTEmplacement.getLgEMPLACEMENTID(), em);
                updateSnapshotMouvementArticle(mouvementSnapshot, (int_NUMBER * qtyDetail), em);
            } catch (Exception e) {
                createSnapshotMouvementArticle(OTFamilleChild, OTFamilleStockChild.getIntNUMBERAVAILABLE(),
                        stockInitDetail, OTEmplacement, em);
                LOGGER.log(Level.SEVERE, null, e);
            }
            try {
                TMouvementSnapshot mouvementSnapshot = findMouvementSnapshotByDay(OTFamilleParent,
                        OTEmplacement.getLgEMPLACEMENTID(), em);
                updateSnapshotMouvementArticle(mouvementSnapshot, int_NUMBER, em);
            } catch (Exception e) {
                createSnapshotMouvementArticle(OTFamilleParent, OTFamilleStockParent.getIntNUMBERAVAILABLE(), stockInit,
                        OTEmplacement, em);
                // LOGGER.log(Level.SEVERE, "---------------------- mouvementSnapshot -------------->>>", e);
            }
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();

            }
            if (OTEmplacement.getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS)
                    && OTFamilleParent.getBCODEINDICATEUR() == 0) {
                new suggestionManagement(manager, user).makeSuggestionAuto(OTFamilleStockParent, OTFamilleParent);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();

            }
            return false;
        }

        return result;
    }

}
