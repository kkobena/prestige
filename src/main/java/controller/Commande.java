/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TGrossiste;
import dal.TOrder;
import dal.TOrderDetail;
import dal.TOrder_;
import dal.TUser;
import dal.dataManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;
import toolkits.utils.conversion;
import toolkits.utils.date;

/**
 *
 * @author user
 */
@WebServlet(name = "Commande", urlPatterns = {"/order"})
public class Commande extends HttpServlet {
  
    TUser OTUser = null;
    dataManager OdataManager = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OdataManager = new dataManager();
        OdataManager.initEntityManager();
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        List<TOrder> lstTOrder;
        String search_value = "";

        try (PrintWriter out = response.getWriter()) {
            if (request.getParameter("search_value") != null) {
                search_value = request.getParameter("search_value");

            }

            int start = Integer.valueOf(request.getParameter("start"));
            int limit = Integer.valueOf(request.getParameter("limit"));

            lstTOrder = listeOrder(search_value, start, limit);
            int count = listeOrder(search_value);
            JSONObject data = new JSONObject();
            data.put("total", count);
            JSONArray arrayObj = new JSONArray();
            for (TOrder tOrder : lstTOrder) {
                String str_Product = "";
                int nb = 0, int_TOTAL_ACHAT = 0, int_TOTAL_VENTE = 0;
                List<TOrderDetail> lstTOrderDetail = getTOrderDetail(tOrder.getLgORDERID());
                for (TOrderDetail OrderDetail : lstTOrderDetail) {
                    TFamilleGrossiste OTFamilleGrossiste = findFamilleGrossiste(OrderDetail.getLgFAMILLEID(), tOrder.getLgGROSSISTEID());
                    str_Product = "<b><span style='display:inline-block;width: 7%;'>" + (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : OrderDetail.getLgFAMILLEID().getIntCIP()) + "</span><span style='display:inline-block;width: 25%;'>" + OrderDetail.getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + OrderDetail.getIntQTEREPGROSSISTE() + ")</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(OrderDetail.getIntPAFDETAIL(), '.') + " F CFA " + "</span></b><br> " + str_Product;

                    nb = nb + OrderDetail.getIntQTEREPGROSSISTE();
                    int_TOTAL_ACHAT += OrderDetail.getIntPRICE();
                    int_TOTAL_VENTE += OrderDetail.getIntPRICEDETAIL() * OrderDetail.getIntQTEREPGROSSISTE();
                }
                JSONObject json = new JSONObject();

                json.put("lg_ORDER_ID", tOrder.getLgORDERID());
                json.put("str_REF_ORDER", tOrder.getStrREFORDER());
                json.put("int_LINE", lstTOrderDetail.size());
                json.put("lg_GROSSISTE_ID", tOrder.getLgGROSSISTEID().getLgGROSSISTEID());
                json.put("str_GROSSISTE_LIBELLE", tOrder.getLgGROSSISTEID().getStrLIBELLE());
                json.put("lg_USER_ID", tOrder.getLgUSERID().getStrFIRSTNAME() + " " + tOrder.getLgUSERID().getStrLASTNAME());

                json.put("str_FAMILLE_ITEM", str_Product);

                json.put("int_NBRE_PRODUIT", nb);

                json.put("PRIX_ACHAT_TOTAL", int_TOTAL_ACHAT);
                json.put("PRIX_VENTE_TOTAL", int_TOTAL_VENTE);

                json.put("str_STATUT", tOrder.getStrSTATUT());
                json.put("dt_CREATED", date.DateToString(tOrder.getDtUPDATED(), date.formatterShort));
                json.put("dt_UPDATED", date.DateToString(tOrder.getDtUPDATED(), date.NomadicUiFormat_Time));

                arrayObj.put(json);

            }
            data.put("results", arrayObj);
            out.println(data);
        } catch (JSONException ex) {
            Logger.getLogger(Commande.class.getName()).log(Level.SEVERE, null, ex);
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

    public List<TOrder> listeOrder(String search_value, int start, int limit) {
        List<TOrder> lstTOrder = new ArrayList<>();
        EntityManager em = OdataManager.getEm();
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TOrder> cq = cb.createQuery(TOrder.class);
            Root<TOrder> root = cq.from(TOrder.class);

            cq.select(root).orderBy(cb.desc(root.get(TOrder_.dtCREATED)));
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.or(cb.like(root.get(TOrder_.strSTATUT), commonparameter.statut_is_Waiting), cb.like(root.get(TOrder_.strSTATUT), commonparameter.orderIsPassed)));

            if (!"".equals(search_value)) {
                predicate = cb.and(predicate, cb.like(root.get(TOrder_.strREFORDER), search_value + "%"));

            }
            cq.where(predicate);
            TypedQuery<TOrder> q = em.createQuery(cq);

            q.setFirstResult(start);
            q.setMaxResults(limit);

            lstTOrder= q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lstTOrder;
    }

    public int listeOrder(String search_value) {

        EntityManager em = OdataManager.getEm();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TOrder> root = cq.from(TOrder.class);

        cq.select(cb.count(root));
        Predicate predicate = cb.conjunction();

        predicate = cb.and(predicate, cb.or(cb.like(root.get(TOrder_.strSTATUT), commonparameter.statut_is_Waiting), cb.like(root.get(TOrder_.strSTATUT), commonparameter.orderIsPassed)));

        if (!"".equals(search_value)) {
            predicate = cb.and(predicate, cb.equal(root.get(TOrder_.strREFORDER), search_value + "%"));

        }
        cq.where(predicate);
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    public List<TOrderDetail> getTOrderDetail(String lg_ORDER_ID) {
        List<TOrderDetail> lstT = new ArrayList<>();
        EntityManager em = OdataManager.getEm();
        try {
            lstT = em.
                    createQuery("SELECT t FROM TOrderDetail t WHERE t.lgORDERID.lgORDERID =?1").
                    setParameter(1, lg_ORDER_ID).
                    getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lstT;
    }

    private  TFamilleGrossiste findFamilleGrossiste(TFamille OTFamille, TGrossiste OTGrossiste) {
        TFamilleGrossiste OTFamilleGrossiste ;
        EntityManager em = OdataManager.getEm();
        try {
            Query qry = em.createQuery("SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1 AND (t.lgGROSSISTEID.lgGROSSISTEID = ?2 OR t.lgGROSSISTEID.strDESCRIPTION = ?2) AND t.strSTATUT LIKE ?3 ").
                    setParameter(1, OTFamille.getLgFAMILLEID())
                    .setParameter(2, OTGrossiste.getLgGROSSISTEID())
                    .setParameter(3, commonparameter.statut_enable);
            qry.setMaxResults(1);
            OTFamilleGrossiste = (TFamilleGrossiste) qry.getSingleResult();

        } catch (Exception e) {
             OTFamilleGrossiste=new TFamilleGrossiste();
              OTFamilleGrossiste.setLgFAMILLEID(OTFamille);
              OTFamilleGrossiste.setLgGROSSISTEID(OTGrossiste);
              OTFamilleGrossiste.setIntPAF(OTFamille.getIntPAF());
              OTFamilleGrossiste.setIntPRICE(OTFamille.getIntPRICE());
              OTFamilleGrossiste.setStrCODEARTICLE(OTFamille.getIntCIP());
              em.getTransaction().begin();
              em.persist(OTFamilleGrossiste);
               em.getTransaction().commit();
//            e.printStackTrace();
        }

        return OTFamilleGrossiste;
    }
}
