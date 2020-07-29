
package controller;

import bll.common.Parameter;
import dal.TCashTransaction;
import dal.TParameters;
import dal.TPreenregistrement;
import dal.TPreenregistrement_;
import dal.TReglement;
import dal.TUser;
import dal.dataManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;

@WebServlet(name = "Custom", urlPatterns = {"/custom"})
public class Custom extends HttpServlet {

    TUser OTUser = null;
    dataManager OdataManager = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OdataManager = new dataManager();
        OdataManager.initEntityManager();
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        String dt_start = LocalDate.now().toString(), dt_end = dt_start;
        Integer virtualAmount = 0, caMontant = 0;

        if (request.getParameter("dt_start") != null && !"".equalsIgnoreCase(request.getParameter("dt_start"))) {

            dt_start = request.getParameter("dt_start");

        }
        if (request.getParameter("dt_end") != null && !"".equalsIgnoreCase(request.getParameter("dt_end"))) {

            dt_end = request.getParameter("dt_end");

        }
        JSONObject json = new JSONObject();
        boolean exclude = false;
        try (PrintWriter out = response.getWriter()) {
            try {
                TParameters OTParameter = OdataManager.getEm().getReference(TParameters.class, "KEY_TAKE_INTO_ACCOUNT");
                if (OTParameter != null) {
                    if (Integer.valueOf(OTParameter.getStrVALUE().trim()) == 1) {
                        exclude = true;
                    }
                }

            } catch (NumberFormatException e) {
            }
            if (request.getParameter("action").equals("getca")) {
                Integer ca = getCA(dt_start, dt_end, OdataManager.getEm(), OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), exclude);
                if (ca != null) {
                    json.put("CA", ca);
                } else {
                    json.put("CA", 0);
                }
            } else if (request.getParameter("action").equals("finish")) {
                json.put("success", 0);
                if (request.getParameter("amount") != null && !"".equalsIgnoreCase(request.getParameter("amount"))) {

                    virtualAmount = Integer.valueOf(request.getParameter("amount"));

                }
                if (request.getParameter("ca") != null && !"".equalsIgnoreCase(request.getParameter("ca"))) {

                    caMontant = Integer.valueOf(request.getParameter("ca"));

                }
                List<TPreenregistrement> variableList = new ArrayList<>();

                List<TPreenregistrement> list = getTtVente(dt_start, dt_end, OdataManager.getEm(), OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), exclude);
                int i = 0;
                for (TPreenregistrement tPreenregistrement : list) {

                    Integer finalPrice = 0;

                    if (virtualAmount > 0) {
                        Integer Net = (exclude ? (tPreenregistrement.getIntACCOUNT() - tPreenregistrement.getIntREMISEPARA()) : (tPreenregistrement.getIntPRICE() - tPreenregistrement.getIntPRICEREMISE()));
                        Integer newPrice = 0;
                        int netPercent = (virtualAmount * 100) / Net;

                        if (netPercent >= 100) {
                            newPrice = (Net * 35) / 100;
                        } else if (netPercent > 6) {
                            newPrice = (Net * 30) / 100;
                        }

                        if (virtualAmount > newPrice) {
                            virtualAmount -= newPrice;
                            finalPrice = Net - newPrice;
                        } else {
                            finalPrice = Net - virtualAmount;
                            virtualAmount = 0;
//                 
                        }
                    } else {
                        break;
                    }
                    tPreenregistrement.setIntPRICEOTHER(finalPrice + (exclude ? tPreenregistrement.getIntREMISEPARA() : tPreenregistrement.getIntPRICEREMISE()));
                    variableList.add(tPreenregistrement);

                    i++;
                }
                save(variableList, OdataManager.getEm());
                json.put("success", 1);
                json.put("nb", i + "/" + list.size() + " ventes ont été affectées ");
            }

            out.println(json);
        } catch (JSONException ex) {
            Logger.getLogger(Custom.class.getName()).log(Level.SEVERE, null, ex);
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

    public void save(List<TPreenregistrement> list, EntityManager em) {
        try {
            em.getTransaction().begin();
            list.forEach((t) -> {
                em.merge(t);
                TCashTransaction tct = getCashTransaction(t.getLgPREENREGISTREMENTID(), em);
                tct.setIntAMOUNT2(t.getIntPRICEOTHER());
                em.merge(tct);
            });
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            em.clear();
//            em.close();
        } finally {
            em.clear();
//            em.close();

        }

    }

    private TCashTransaction getCashTransaction(String ref, EntityManager em) {
        TCashTransaction tct = null;
        try {
            tct = em.createQuery("SELECT o FROM TCashTransaction o WHERE o.strRESSOURCEREF=?1", TCashTransaction.class)
                    .setParameter(1, ref).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
        }
        return tct;
    }

    private Integer getCA(String dt_start, String dt_end, EntityManager em, String lgEmp, boolean exclude) {

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);

            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Join<TPreenregistrement, TReglement> pr = root.join("lgREGLEMENTID", JoinType.INNER);

            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), "5"));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), "VNO"));
            predicate = cb.and(predicate, cb.or(cb.equal(pr.get("lgMODEREGLEMENTID").get("lgMODEREGLEMENTID"), "1"), cb.equal(pr.get("lgMODEREGLEMENTID").get("lgMODEREGLEMENTID"), "2")));
            Predicate ge = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            if (exclude) {
                cq.select(cb.sum(cb.diff(root.get(TPreenregistrement_.intACCOUNT),
                        root.get(TPreenregistrement_.intREMISEPARA))));
            } else {
                cq.select(cb.sum(cb.diff(root.get(TPreenregistrement_.intPRICE),
                        root.get(TPreenregistrement_.intPRICEREMISE))));
            }

            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);

            return (Integer) q.getSingleResult();

        } finally {

        }

    }

    public static List<TPreenregistrement> getTtVente(String dt_start, String dt_end, EntityManager em, String lgEmp, boolean exclude) {

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);

            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Join<TPreenregistrement, TReglement> pr = root.join("lgREGLEMENTID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), Parameter.VENTE_DEPOT_EXTENSION));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strSTATUT), "is_Closed"));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), "VNO"));
            predicate = cb.and(predicate, cb.or(cb.equal(pr.get("lgMODEREGLEMENTID").get("lgMODEREGLEMENTID"), "1"), cb.equal(pr.get("lgMODEREGLEMENTID").get("lgMODEREGLEMENTID"), "2")));
            Predicate ge = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtCREATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            if (exclude) {
                cq.select(root).orderBy(cb.desc(root.get(TPreenregistrement_.intACCOUNT)));
            } else {
                cq.select(root).orderBy(cb.desc(root.get(TPreenregistrement_.intPRICE)));
            }

            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);

            return q.getResultList();

        } finally {

        }

    }

}
