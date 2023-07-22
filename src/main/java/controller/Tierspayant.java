/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bll.Util;
import dal.TCompteClient;
import dal.TCompteClientTiersPayant;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementCompteClientTiersPayent_;
import dal.TPrivilege;
import dal.TTiersPayant;
import dal.TTiersPayant_;
import dal.TTypeTiersPayant;
import dal.TTypeTiersPayant_;
import dal.TUser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;
import toolkits.utils.date;
import util.DateConverter;

/**
 *
 * @author user
 */
@WebServlet(name = "Tierspayant", urlPatterns = { "/tierspayant" })
public class Tierspayant extends HttpServlet {

    TUser OTUser = null;
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public Integer getAccount(String tp) {
        Integer account = 0;
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq
                    .from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cmp = root
                    .join("lgCOMPTECLIENTTIERSPAYANTID", JoinType.INNER);
            Predicate criteria = cb.conjunction();
            criteria = cb.and(criteria, cb.equal(cmp.get("lgTIERSPAYANTID").get("lgTIERSPAYANTID"), tp));
            criteria = cb.and(criteria,
                    cb.notEqual(root.get(TPreenregistrementCompteClientTiersPayent_.strSTATUTFACTURE),
                            commonparameter.statut_paid));
            criteria = cb.and(criteria,
                    cb.equal(root.get("lgPREENREGISTREMENTID").get("strSTATUT"), commonparameter.statut_is_Closed));
            criteria = cb.and(criteria, cb.equal(root.get("lgPREENREGISTREMENTID").get("bISCANCEL"), false));
            Predicate ge = cb.greaterThan(root.get("lgPREENREGISTREMENTID").get("intPRICE"), 0);
            cq.select(cb.sum(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICERESTE)));
            cq.where(criteria, ge);
            Query q = em.createQuery(cq);
            account = (Integer) q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        List<TPrivilege> privileges = (List<TPrivilege>) session.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
        String search_value = "", lg_TYPE_TIERS_PAYANT_ID = "";
        try (PrintWriter out = response.getWriter()) {
            if (request.getParameter("search_value") != null) {
                search_value = request.getParameter("search_value");

            }

            if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null) {
                lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID");

            }
            if (request.getParameter("cmb_TYPE_TIERS_PAYANT") != null) {
                lg_TYPE_TIERS_PAYANT_ID = request.getParameter("cmb_TYPE_TIERS_PAYANT");

            }
            if (request.getParameter("query") != null) {
                search_value = request.getParameter("query");

            }
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));

            JSONObject data = new JSONObject();
            List<TTiersPayant> list = ShowAllOrOneTierspayant(search_value, lg_TYPE_TIERS_PAYANT_ID, start, limit);
            int count = ShowAllOrOneTierspayant(search_value, lg_TYPE_TIERS_PAYANT_ID);
            data.put("total", count);
            JSONArray jsonarray = new JSONArray();
            boolean isALLOWED = DateConverter.hasAuthorityById(privileges, Util.ACTIONDELETE);
            boolean P_BTN_DESACTIVER_TIERS_PAYANT = DateConverter.hasAuthorityByName(privileges,
                    DateConverter.P_BTN_DESACTIVER_TIERS_PAYANT);
            for (TTiersPayant tTiersPayant : list) {
                JSONObject json = new JSONObject();
                TCompteClient OTCompteClient = getTCompteClient(tTiersPayant.getLgTIERSPAYANTID());
                json.put("cmu", tTiersPayant.getCmus());
                json.put("lg_TIERS_PAYANT_ID", tTiersPayant.getLgTIERSPAYANTID());
                // str_CODE_ORGANISME
                json.put("str_CODE_ORGANISME", tTiersPayant.getStrCODEORGANISME());
                // str_NAME
                json.put("str_NAME", tTiersPayant.getStrNAME());
                // str_FULLNAME
                json.put("str_FULLNAME", tTiersPayant.getStrFULLNAME());
                // str_ADRESSE
                json.put("str_ADRESSE", tTiersPayant.getStrADRESSE());
                // str_MOBILE
                json.put("str_MOBILE", tTiersPayant.getStrMOBILE());
                // str_TELEPHONE
                json.put("str_TELEPHONE", tTiersPayant.getStrTELEPHONE());
                // str_MAIL
                json.put("str_MAIL", tTiersPayant.getStrMAIL());
                // dbl_PLAFOND_CREDIT
                json.put("dbl_PLAFOND_CREDIT", tTiersPayant.getDblPLAFONDCREDIT());
                // dbl_TAUX_REMBOURSEMENT (à associer à la table TRembourcement
                json.put("dbl_TAUX_REMBOURSEMENT", tTiersPayant.getDblTAUXREMBOURSEMENT());
                // str_NUMERO_CAISSE_OFFICIEL
                json.put("str_NUMERO_CAISSE_OFFICIEL", tTiersPayant.getStrNUMEROCAISSEOFFICIEL());
                // str_CENTRE_PAYEUR
                json.put("str_CENTRE_PAYEUR", tTiersPayant.getStrCENTREPAYEUR());
                // str_CODE_REGROUPEMENT
                json.put("str_CODE_REGROUPEMENT", tTiersPayant.getStrCODEREGROUPEMENT());
                // dbl_SEUIL_MINIMUM
                json.put("dbl_SEUIL_MINIMUM", tTiersPayant.getDblSEUILMINIMUM());
                // bool_INTERDICTION
                json.put("bool_INTERDICTION", tTiersPayant.getBoolINTERDICTION());
                // str_CODE_COMPTABLE
                json.put("str_CODE_COMPTABLE", tTiersPayant.getStrCODECOMPTABLE());
                // bool_PRENUM_FACT_SUBROGATOIRE
                json.put("bool_PRENUM_FACT_SUBROGATOIRE", tTiersPayant.getBoolPRENUMFACTSUBROGATOIRE());
                // int_NUMERO_DECOMPTE
                json.put("int_NUMERO_DECOMPTE", tTiersPayant.getIntNUMERODECOMPTE());
                // str_CODE_PAIEMENT
                json.put("str_CODE_PAIEMENT", tTiersPayant.getStrCODEPAIEMENT());
                // dt_DELAI_PAIEMENT
                json.put("dt_DELAI_PAIEMENT", tTiersPayant.getDtDELAIPAIEMENT());
                // dbl_POURCENTAGE_REMISE
                json.put("dbl_POURCENTAGE_REMISE", tTiersPayant.getDblPOURCENTAGEREMISE());
                // dbl_REMISE_FORFETAIRE
                json.put("dbl_REMISE_FORFETAIRE", tTiersPayant.getDblREMISEFORFETAIRE());
                // str_CODE_EDIT_BORDEREAU
                json.put("str_CODE_EDIT_BORDEREAU", tTiersPayant.getLgMODELFACTUREID().getStrVALUE());
                // int_NBRE_EXEMPLAIRE_BORD
                json.put("int_NBRE_EXEMPLAIRE_BORD", tTiersPayant.getIntNBREEXEMPLAIREBORD());
                // int_PERIODICITE_EDIT_BORD
                json.put("int_PERIODICITE_EDIT_BORD", tTiersPayant.getIntPERIODICITEEDITBORD());
                // int_DATE_DERNIERE_EDITION
                json.put("int_DATE_DERNIERE_EDITION", tTiersPayant.getIntDATEDERNIEREEDITION());
                // str_NUMERO_IDF_ORGANISME
                json.put("str_NUMERO_IDF_ORGANISME", tTiersPayant.getStrNUMEROIDFORGANISME());
                // dbl_MONTANT_F_CLIENT
                json.put("dbl_MONTANT_F_CLIENT", tTiersPayant.getDblMONTANTFCLIENT());
                // dbl_BASE_REMISE
                json.put("dbl_BASE_REMISE", tTiersPayant.getDblBASEREMISE());
                // str_CODE_DOC_COMPTOIRE
                json.put("str_CODE_DOC_COMPTOIRE", tTiersPayant.getStrCODEDOCCOMPTOIRE());
                // bool_ENABLED
                json.put("bool_ENABLED", tTiersPayant.getBoolENABLED());
                json.put("str_COMPTE_CONTRIBUABLE", tTiersPayant.getStrCOMPTECONTRIBUABLE());
                json.put("lgGROUPEID",
                        (tTiersPayant.getLgGROUPEID() != null) ? tTiersPayant.getLgGROUPEID().getStrLIBELLE() : "");

                json.put("lg_CUSTOMER_ID", tTiersPayant.getLgTIERSPAYANTID());
                json.put("str_LIBELLE", tTiersPayant.getStrNAME());
                json.put("lg_VILLE_ID",
                        (tTiersPayant.getLgVILLEID() != null ? tTiersPayant.getLgVILLEID().getStrName() : ""));
                json.put("lg_TYPE_TIERS_PAYANT_ID", (tTiersPayant.getLgTYPETIERSPAYANTID() != null
                        ? tTiersPayant.getLgTYPETIERSPAYANTID().getStrLIBELLETYPETIERSPAYANT() : ""));
                json.put("lg_TYPE_CONTRAT_ID", (tTiersPayant.getLgTYPECONTRATID() != null
                        ? tTiersPayant.getLgTYPECONTRATID().getStrLIBELLETYPECONTRAT() : ""));
                json.put("lg_RISQUE_ID", (tTiersPayant.getLgRISQUEID() != null
                        ? tTiersPayant.getLgRISQUEID().getStrLIBELLERISQUE() : ""));
                json.put("lg_REGIMECAISSE_ID", (tTiersPayant.getLgREGIMECAISSEID() != null
                        ? tTiersPayant.getLgREGIMECAISSEID().getStrCODEREGIMECAISSE() : ""));
                json.put("str_CODE_OFFICINE",
                        tTiersPayant.getStrCODEOFFICINE() != null ? tTiersPayant.getStrCODEOFFICINE() : "");
                json.put("str_REGISTRE_COMMERCE",
                        tTiersPayant.getStrREGISTRECOMMERCE() != null ? tTiersPayant.getStrREGISTRECOMMERCE() : "");
                json.put("groupingByTaux", tTiersPayant.getGroupingByTaux());
                json.put("str_STATUT", tTiersPayant.getStrSTATUT());
                json.put("b_IsAbsolute", tTiersPayant.getBIsAbsolute());
                json.put("db_CONSOMMATION_MENSUELLE", getAccount(tTiersPayant.getLgTIERSPAYANTID()));
                json.put("dbl_PLAFOND_CREDIT", tTiersPayant.getDblPLAFONDCREDIT());
                json.put("nbrbons", (tTiersPayant.getIntNBREBONS() != null)
                        ? (tTiersPayant.getIntNBREBONS() > 0 ? tTiersPayant.getIntNBREBONS() : 0) : 0);
                json.put("montantFact", (tTiersPayant.getIntMONTANTFAC() != null)
                        ? (tTiersPayant.getIntMONTANTFAC() > 0 ? tTiersPayant.getIntMONTANTFAC() : 0) : 0);

                json.put("str_PHOTO", tTiersPayant.getStrPHOTO());

                if (tTiersPayant.getDtCREATED() != null) {
                    json.put("dt_CREATED", date.DateToString(tTiersPayant.getDtCREATED(), date.formatterShort));
                }

                if (tTiersPayant.getDtUPDATED() != null) {
                    json.put("dt_UPDATED", date.DateToString(tTiersPayant.getDtUPDATED(), date.formatterShort));
                }

                if (tTiersPayant.getLgMODELFACTUREID() != null) {
                    json.put("lg_MODEL_FACTURE_ID", tTiersPayant.getLgMODELFACTUREID().getStrVALUE());
                }
                if (OTCompteClient != null) {

                    json.put("dbl_CAUTION", OTCompteClient.getDblCAUTION());
                    json.put("dbl_PLAFOND", OTCompteClient.getDblPLAFOND());
                    json.put("dbl_QUOTA_CONSO_MENSUELLE", OTCompteClient.getDblQUOTACONSOMENSUELLE());
                }

                List<TCompteClientTiersPayant> lstTCompteClientTiersPayant = getTiersPayantsByClient(
                        tTiersPayant.getLgTIERSPAYANTID());

                String str_Product = "";
                for (int k = 0; k < lstTCompteClientTiersPayant.size(); k++) {
                    if (lstTCompteClientTiersPayant.get(k).getLgCOMPTECLIENTID().getLgCLIENTID() != null) {
                        str_Product = "<b><span style='display:inline-block;width: 15%;'>"
                                + (!lstTCompteClientTiersPayant.get(k).getStrNUMEROSECURITESOCIAL().equalsIgnoreCase("")
                                        ? lstTCompteClientTiersPayant.get(k).getStrNUMEROSECURITESOCIAL()
                                        : lstTCompteClientTiersPayant.get(k).getLgCOMPTECLIENTID().getLgCLIENTID()
                                                .getStrCODEINTERNE())
                                + "</span><span style='display:inline-block;width: 15%;'>"
                                + lstTCompteClientTiersPayant.get(k).getLgCOMPTECLIENTID().getLgCLIENTID()
                                        .getStrFIRSTNAME()
                                + "</span><span style='display:inline-block;width: 25%;'>" + lstTCompteClientTiersPayant
                                        .get(k).getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME()
                                + "</span></b><br> " + str_Product;
                    }
                }

                if (str_Product.equalsIgnoreCase("")) {
                    str_Product = "Aucun client associé";
                }
                json.put("BTNDELETE", isALLOWED);
                json.put("P_BTN_DESACTIVER_TIERS_PAYANT", P_BTN_DESACTIVER_TIERS_PAYANT);
                json.put("int_NUMBER_CLIENT", lstTCompteClientTiersPayant.size());
                json.put("str_FAMILLE_ITEM", str_Product);
                jsonarray.put(json);

            }

            data.put("results", jsonarray);
            out.println(data);
        } catch (JSONException ex) {
            Logger.getLogger(Tierspayant.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the
    // code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     *
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     *
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private List<TTiersPayant> ShowAllOrOneTierspayant(String search_value, String lg_TYPE_TIERS_PAYANT_ID, int start,
            int limit) {
        List<TTiersPayant> list = new ArrayList<>();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TTiersPayant> cq = cb.createQuery(TTiersPayant.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            Join<TTiersPayant, TTypeTiersPayant> j = root.join("lgTYPETIERSPAYANTID", JoinType.INNER);
            // root.fetch("tCompteClientTiersPayantCollection", JoinType.INNER);
            cq.select(root)/* .distinct(true).groupBy(root.get(TTiersPayant_.lgTIERSPAYANTID)) */.orderBy(
                    cb.asc(j.get(TTypeTiersPayant_.strLIBELLETYPETIERSPAYANT)),
                    cb.asc(root.get(TTiersPayant_.strNAME)));
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(root.get(TTiersPayant_.strSTATUT), "enable"));

            if (!"".equals(search_value)) {
                predicate = cb.and(predicate,
                        cb.or(cb.like(j.get(TTypeTiersPayant_.strLIBELLETYPETIERSPAYANT), search_value + "%"),
                                cb.like(root.get(TTiersPayant_.strNAME), search_value + "%"),
                                cb.like(root.get(TTiersPayant_.strCODEORGANISME), search_value + "%")));

            }
            if (!"".equals(lg_TYPE_TIERS_PAYANT_ID)) {
                predicate = cb.and(predicate,
                        cb.equal(j.get(TTypeTiersPayant_.lgTYPETIERSPAYANTID), lg_TYPE_TIERS_PAYANT_ID));
            }
            cq.where(predicate);
            TypedQuery<TTiersPayant> q = em.createQuery(cq);

            q.setFirstResult(start);
            q.setMaxResults(limit);

            list = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private int ShowAllOrOneTierspayant(String search_value, String lg_TYPE_TIERS_PAYANT_ID) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TTiersPayant> root = cq.from(TTiersPayant.class);
        Join<TTiersPayant, TTypeTiersPayant> j = root.join("lgTYPETIERSPAYANTID", JoinType.INNER);

        cq.select(cb.count(root));
        Predicate predicate = cb.conjunction();
        predicate = cb.and(predicate, cb.equal(root.get(TTiersPayant_.strSTATUT), "enable"));

        if (!"".equals(search_value)) {
            predicate = cb.and(predicate,
                    cb.or(cb.like(j.get(TTypeTiersPayant_.strLIBELLETYPETIERSPAYANT), search_value + "%"),
                            cb.like(root.get(TTiersPayant_.strNAME), search_value + "%"),
                            cb.like(root.get(TTiersPayant_.strCODEORGANISME), search_value + "%")));

        }
        if (!"".equals(lg_TYPE_TIERS_PAYANT_ID)) {
            predicate = cb.and(predicate,
                    cb.equal(j.get(TTypeTiersPayant_.lgTYPETIERSPAYANTID), lg_TYPE_TIERS_PAYANT_ID));
        }
        cq.where(predicate);
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    private TCompteClient getTCompteClient(String P_KEY) {
        TCompteClient OTCompteClient = null;

        try {
            OTCompteClient = em
                    .createQuery("SELECT t FROM TCompteClient t WHERE t.pKey = ?1 AND t.strSTATUT = ?2",
                            TCompteClient.class)
                    .setParameter(1, P_KEY).setParameter(2, commonparameter.statut_enable).setMaxResults(1)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTCompteClient;
    }

    private List<TCompteClientTiersPayant> getTiersPayantsByClient(String lg_TIERS_PAYANT_ID) {
        List<TCompteClientTiersPayant> lstTCompteClientTiersPayant = new ArrayList<>();
        try {

            lstTCompteClientTiersPayant = em.createQuery(
                    "SELECT t FROM TCompteClientTiersPayant t WHERE  t.lgTIERSPAYANTID.lgTIERSPAYANTID = ?1   ORDER BY t.intPRIORITY ASC")
                    .setParameter(1, lg_TIERS_PAYANT_ID).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstTCompteClientTiersPayant;
    }
}
