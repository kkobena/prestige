/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TFamilleStock;
import dal.TFamille_;
import dal.TPreenregistrementDetail;
import dal.TPreenregistrementDetail_;
import dal.TSuggestionOrder;
import dal.TSuggestionOrderDetails;
import dal.TSuggestionOrder_;
import dal.TUser;
import dal.dataManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;

/**
 *
 * @author user
 */
public class Suggestion extends HttpServlet {
static final Logger LOGGER = Logger.getLogger(Suggestion.class.getName());
    TUser OTUser = null;
    dataManager OdataManager = null;

    public TFamilleStock getTProductItemStock(String lg_FAMILLE_ID, String lg_EMPLACEMENT_ID) {
        TFamilleStock OTProductItemStock = null;
        EntityManager em = OdataManager.getEm();
        try {
            OTProductItemStock = em.
                    createQuery("SELECT t FROM TFamilleStock t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable'", TFamilleStock.class).
                    setParameter(1, lg_FAMILLE_ID).setParameter(2, lg_EMPLACEMENT_ID).setFirstResult(0).setMaxResults(1).getSingleResult();
            em.refresh(OTProductItemStock);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,null,e);
        }
        return OTProductItemStock;
    }

    public TFamilleGrossiste findFamilleGrossiste(String lg_FAMILLE_ID, String lg_GROSSISTE_ID) {
        TFamilleGrossiste OTFamilleGrossiste = null;
        EntityManager em = OdataManager.getEm();
        try {
            Query qry = em.createQuery("SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgGROSSISTEID.lgGROSSISTEID = ?2  AND t.strSTATUT = ?3 ").
                    setParameter(1, lg_FAMILLE_ID)
                    .setParameter(2, lg_GROSSISTE_ID)
                    .setParameter(3, commonparameter.statut_enable);
            qry.setMaxResults(1);
            OTFamilleGrossiste = (TFamilleGrossiste) qry.getSingleResult();

        } catch (Exception e) {

             LOGGER.log(Level.SEVERE,null,e);
        }

        return OTFamilleGrossiste;
    }

    public int isOnAnotherSuggestion(String lgFamilleID) {
        EntityManager em = OdataManager.getEm();
        int status = 0;
        try {

            long count = (long) em.createQuery("SELECT COUNT(o)  FROM TSuggestionOrderDetails o WHERE o.strSTATUT='is_Process' AND o.lgFAMILLEID.lgFAMILLEID =?1 ").setParameter(1, lgFamilleID)
                    .setMaxResults(1)
                    .getSingleResult();

            if (count > 1) {

                status = 1;

            }
            count = (long) em.createQuery("SELECT COUNT(p) FROM TOrder r,TOrderDetail p WHERE p.lgORDERID.lgORDERID=r.lgORDERID AND (p.lgFAMILLEID.intORERSTATUS =?2 OR p.lgFAMILLEID.intORERSTATUS =?3 OR p.lgFAMILLEID.intORERSTATUS =?4 ) AND  p.lgFAMILLEID.lgFAMILLEID =?1  ORDER BY p.lgFAMILLEID.intORERSTATUS DESC").setParameter(1, lgFamilleID)
                    .setParameter(2, (short) 2)
                    .setParameter(3, (short) 3)
                    .setParameter(4, (short) 4)
                    .setMaxResults(1)
                    .getSingleResult();

            if (count > 0) {
                status = 2;
            }

        } catch (Exception e) {
             LOGGER.log(Level.SEVERE,null,e);
        }

        return status;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OdataManager = new dataManager();
        OdataManager.initEntityManager();
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        String search_value = "", lg_SUGGESTION_ORDER_ID = "";
        if (request.getParameter("search_value") != null) {
            search_value = request.getParameter("search_value");

        }
        if (request.getParameter("lg_SUGGESTION_ORDER_ID") != null) {
            lg_SUGGESTION_ORDER_ID = request.getParameter("lg_SUGGESTION_ORDER_ID");

        }
        int start = Integer.valueOf(request.getParameter("start"));
        int limit = Integer.valueOf(request.getParameter("limit"));
        List<TSuggestionOrderDetails> detailses = listeSuggestionOrderDetails(search_value, lg_SUGGESTION_ORDER_ID, start, limit);
        int count = listeSuggestionOrderDetails(search_value, lg_SUGGESTION_ORDER_ID);
        JSONObject data = new JSONObject();

        JSONArray arrayObj = new JSONArray();
        Integer int_ACHAT = 0;
        Integer int_VENTE = 0;
        String empl = OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID();
        LocalDate today = LocalDate.now();
        LocalDate moisUn = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
        LocalDate nMoinsUn = moisUn.minusMonths(1);
        LocalDate nMoinsDeux = moisUn.minusMonths(2);
        LocalDate nMoinsTrois = moisUn.minusMonths(3);
        try (PrintWriter out = response.getWriter()) {
            data.put("total", count);
            for (TSuggestionOrderDetails order : detailses) {
                JSONObject json = new JSONObject();
                TFamilleStock OTFamillestock = getTProductItemStock(order.getLgFAMILLEID().getLgFAMILLEID(), OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
                TFamilleGrossiste OTFamilleGrossiste = findFamilleGrossiste(order.getLgFAMILLEID().getLgFAMILLEID(), order.getLgSUGGESTIONORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
                json.put("lg_SUGGESTION_ORDER_DETAILS_ID", order.getLgSUGGESTIONORDERDETAILSID());
                json.put("lg_FAMILLE_ID", order.getLgFAMILLEID().getLgFAMILLEID());
                json.put("lg_GROSSISTE_ID", order.getLgGROSSISTEID().getLgGROSSISTEID());
                json.put("str_FAMILLE_CIP", (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : order.getLgFAMILLEID().getIntCIP()));
                json.put("str_FAMILLE_NAME", order.getLgFAMILLEID().getStrDESCRIPTION());
                json.put("int_DATE_BUTOIR_ARTICLE", (order.getLgFAMILLEID().getLgCODEGESTIONID() != null ? order.getLgFAMILLEID().getLgCODEGESTIONID().getIntDATEBUTOIRARTICLE() : 0));
                json.put("int_STOCK", OTFamillestock.getIntNUMBERAVAILABLE());

                json.put("int_NUMBER", order.getIntNUMBER());
                int status = isOnAnotherSuggestion(order.getLgFAMILLEID().getLgFAMILLEID());
                json.put("STATUS", status);

//  
                json.put("int_SEUIL", order.getLgFAMILLEID().getIntSEUILMIN());
                json.put("str_STATUT", order.getStrSTATUT());
                // lg_FAMILLE_PRIX_VENTE
                json.put("lg_FAMILLE_PRIX_VENTE", order.getIntPRICEDETAIL());
                // lg_FAMILLE_PRIX_ACHAT
                json.put("lg_FAMILLE_PRIX_ACHAT", order.getLgFAMILLEID().getIntPAT());
                json.put("int_PAF_SUGG", order.getIntPAFDETAIL());
                json.put("int_PRIX_REFERENCE", order.getLgFAMILLEID().getIntPRICETIPS());

                int int_QTE_REASSORT = 0;
                try {
                    int_QTE_REASSORT = OTFamillestock.getIntNUMBERAVAILABLE() - order.getLgFAMILLEID().getIntSEUILMIN();

                    if (int_QTE_REASSORT < 0) {
                        int_QTE_REASSORT = -1 * int_QTE_REASSORT;
                    } else {
                        int_QTE_REASSORT = 0;
                    }
                } catch (Exception e) {
                }
                json.put("int_QTE_REASSORT", int_QTE_REASSORT);

                int_ACHAT = int_ACHAT + order.getIntPAFDETAIL();

                int_VENTE = int_VENTE + order.getIntPRICEDETAIL();

                json.put("int_ACHAT", int_ACHAT);
                json.put("int_VENTE", int_VENTE);

                json.put("int_VALUE0",quantity(order.getLgFAMILLEID().getLgFAMILLEID(), moisUn.getMonthValue(),moisUn.getYear(), empl));
                json.put("int_VALUE1",quantity(order.getLgFAMILLEID().getLgFAMILLEID(), nMoinsUn.getMonthValue(),nMoinsUn.getYear(), empl));
                json.put("int_VALUE2",quantity(order.getLgFAMILLEID().getLgFAMILLEID(), nMoinsDeux.getMonthValue(),nMoinsDeux.getYear(), empl));
                json.put("int_VALUE3",quantity(order.getLgFAMILLEID().getLgFAMILLEID(), nMoinsTrois.getMonthValue(),nMoinsTrois.getYear(), empl));
                arrayObj.put(json);

            }
            data.put("results", arrayObj);
            out.println(data);
        } catch (JSONException e) {
             LOGGER.log(Level.SEVERE,null,e);
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
    }// </editor-fold>

    private List<TSuggestionOrderDetails> listeSuggestionOrderDetails(String search_value, String lg_SUGGESTION_ORDER_ID, int start, int limit) {
        EntityManager em = OdataManager.getEm();
        List<TSuggestionOrderDetails> detailses = new ArrayList<>();
        try {
            System.out.println("search_value  "+search_value+"  lg_SUGGESTION_ORDER_ID "+lg_SUGGESTION_ORDER_ID);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TSuggestionOrderDetails> cq = cb.createQuery(TSuggestionOrderDetails.class);
            Root<TSuggestionOrderDetails> root = cq.from(TSuggestionOrderDetails.class);
            Join<TSuggestionOrderDetails, TSuggestionOrder> join = root.join("lgSUGGESTIONORDERID", JoinType.INNER);
            Join<TSuggestionOrderDetails, TFamille> f = root.join("lgFAMILLEID", JoinType.INNER);
            cq.select(root).orderBy(cb.asc(f.get(TFamille_.strNAME)));
            Predicate predicate = cb.conjunction();

//            predicate = cb.and(predicate, cb.or(cb.like(join.get(TSuggestionOrder_.strSTATUT), commonparameter.statut_is_Process), cb.like(join.get(TSuggestionOrder_.strSTATUT), commonparameter.statut_is_Auto)));
            predicate = cb.and(predicate, cb.equal(join.get(TSuggestionOrder_.lgSUGGESTIONORDERID), lg_SUGGESTION_ORDER_ID));
            if (!"".equals(search_value)) {

                predicate = cb.and(predicate, cb.or(cb.like(f.get(TFamille_.intCIP), search_value + "%"), cb.like(f.get(TFamille_.strNAME), search_value + "%"), cb.like(f.get(TFamille_.intEAN13), search_value + "%")));

            }
            cq.where(predicate);
            TypedQuery<TSuggestionOrderDetails> q = em.createQuery(cq);

            q.setFirstResult(start);
            q.setMaxResults(limit);

            detailses = q.getResultList();
           

        } catch (Exception e) {
             LOGGER.log(Level.SEVERE,null,e);
        }
        return detailses;
    }

    private int listeSuggestionOrderDetails(String search_value, String lg_SUGGESTION_ORDER_ID) {
        EntityManager em = OdataManager.getEm();
        try {
            
      
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TSuggestionOrderDetails> root = cq.from(TSuggestionOrderDetails.class);
        Join<TSuggestionOrderDetails, TSuggestionOrder> join = root.join("lgSUGGESTIONORDERID", JoinType.INNER);
        Join<TSuggestionOrderDetails, TFamille> f = root.join("lgFAMILLEID", JoinType.INNER);
        cq.select(cb.count(root));
        Predicate predicate = cb.conjunction();

//        predicate = cb.and(predicate, cb.or(cb.like(join.get(TSuggestionOrder_.strSTATUT), commonparameter.statut_is_Process), cb.like(join.get(TSuggestionOrder_.strSTATUT), commonparameter.statut_is_Auto)));
        predicate = cb.and(predicate, cb.equal(join.get(TSuggestionOrder_.lgSUGGESTIONORDERID), lg_SUGGESTION_ORDER_ID));
        if (!"".equals(search_value)) {

            predicate = cb.and(predicate, cb.or(cb.like(f.get(TFamille_.intCIP), search_value + "%"), cb.like(f.get(TFamille_.strNAME), search_value + "%"), cb.like(f.get(TFamille_.intEAN13), search_value + "%")));

        }
        cq.where(predicate);
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
        
          } catch (Exception e) {
              
             LOGGER.log(Level.SEVERE,null,e);
             return 0;
        }
    }

    private int quantity(String lgFamille, int month, int year, String empl) {
        EntityManager em = OdataManager.getEm();
        try {
             CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
        Join<TPreenregistrementDetail, TFamille> prf = root.join("lgFAMILLEID", JoinType.INNER);
        Predicate criteria = cb.conjunction();
        criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
        criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
//            criteria = cb.and(criteria, cb.notLike(root.get("lgPREENREGISTREMENTID").get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
        criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), "is_Closed"));
        criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("lgUSERID").get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), empl));
        Predicate pu = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
        cb.and(criteria, pu);
        Predicate pu2 = cb.greaterThan(root.get(TPreenregistrementDetail_.intQUANTITY), 0);
//            cb.and(criteria,pu2);
        criteria = cb.and(criteria, cb.equal(prf.get(TFamille_.lgFAMILLEID), lgFamille));
        Predicate btw = cb.equal(cb.function("MONTH", Integer.class, root.get("dtCREATED")), month);
//            criteria=cb.and(criteria,btw);
        Predicate btw2 = cb.equal(cb.function("YEAR", Integer.class, root.get("dtCREATED")), year);
//            criteria=cb.and(criteria,btw2);
        cq.select(cb.sumAsLong(root.get(TPreenregistrementDetail_.intQUANTITY)));
        cq.where(criteria, btw, pu2, btw2, pu);
        Query q = em.createQuery(cq);
        Long r = (Long) q.getSingleResult();
        return (r != null ? r.intValue() : 0);
        } catch (Exception e) {
             LOGGER.log(Level.SEVERE,null,e);
            return  0;
        }
       

    }

}
