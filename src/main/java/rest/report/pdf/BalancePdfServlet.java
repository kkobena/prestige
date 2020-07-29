/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.report.pdf;

import bll.common.Parameter;
import commonTasks.dto.CaisseParamsDTO;
import commonTasks.dto.Params;
import commonTasks.dto.SalesStatsParams;
import dal.TPrivilege;
import dal.TUser;
import enumeration.Peremption;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author DICI
 */
public class BalancePdfServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    @EJB
    Balance balance;

    private enum Action {
        BALANCE, GESTION_CAISSE, TABLEAU, TVA, REPORT, LISTECAISSE, SUIVIMVT, TABLEAUOLD, RECAP, TVA_JOUR,
        STAT_FAMILLE_ARTICLE, EDITION20_80, PERIMES, STAT_RAYONS_ARTICLE, STAT_PROVIDER_ARTICLE, UNITES_AVOIRS
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        HttpSession session = request.getSession();
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        String action = request.getParameter("mode");
        String dtStart = request.getParameter("dtStart");
        String dtEnd = request.getParameter("dtEnd");
        Params params = new Params();
        params.setOperateur(OTUser);
        String codeFamile,
                codeRayon;
        String codeGrossiste;
        String query;
        String file = "";
        if (dtEnd != null && !"".equals(dtEnd)) {
            params.setDtEnd(dtEnd);
        }
        if (dtStart != null && !"".equals(dtStart)) {
            params.setDtStart(dtStart);
        }

        switch (Action.valueOf(action)) {
            case BALANCE:
                file = balance.generatepdf(params);
                break;
            case GESTION_CAISSE:
                String userId = request.getParameter("userId");
                if (!"".equals(userId)) {
                    params.setRef(userId);
                }
                List<TPrivilege> LstTPrivilege = (List<TPrivilege>) session.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
                file = balance.gestionCaissepdf(params, LstTPrivilege);
                break;
            case TABLEAU:
                boolean ration = Boolean.valueOf(request.getParameter("ration"));
                file = balance.tableauBordPharmation(params, ration);
                break;
            case TABLEAUOLD:
                boolean _ration = Boolean.valueOf(request.getParameter("ration"));
                file = balance.tableauBordPharmationOld(params, _ration);
                break;

            case TVA:
                file = balance.tvapdf(params);
                break;
            case TVA_JOUR:
                file = balance.tvaJourpdf(params);
                break;
            case REPORT:
                file = balance.reportGestion(params);
                break;
            case LISTECAISSE:
                String lg_USER_ID = request.getParameter("user"),
                 reglement = request.getParameter("reglement");
                String startDate = request.getParameter("startDate"),
                 endDate = request.getParameter("endDate");
                String startH = request.getParameter("startH"),
                 endH = request.getParameter("endH");
                CaisseParamsDTO caisseParams = new CaisseParamsDTO();
                caisseParams.setEmplacementId(OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
                if (endDate != null && !endDate.equals("")) {
                    caisseParams.setEnd(LocalDate.parse(endDate));
                }
                if (startDate != null && !startDate.equals("")) {
                    caisseParams.setStartDate(LocalDate.parse(startDate));
                }
                if (startH != null && !startH.equals("")) {
                    caisseParams.setStartHour(LocalTime.parse(startH));
                }
                if (endH != null && !endH.equals("")) {
                    caisseParams.setStartEnd(LocalTime.parse(endH));
                }
                if (reglement != null && !reglement.equals("")) {

                    caisseParams.setTypeReglementId(reglement);
                }
                if (lg_USER_ID != null && !lg_USER_ID.equals("")) {
                    caisseParams.setUtilisateurId(lg_USER_ID);
                }
                caisseParams.setFindClient(true);
                file = balance.listeCaisse(caisseParams, OTUser);
                break;
            case SUIVIMVT:
                String dtSt = request.getParameter("dtStart"),
                 dtEn = request.getParameter("dtEnd");
                String produitId = request.getParameter("produitId");
                file = balance.suivMvtArticle(LocalDate.parse(dtSt), LocalDate.parse(dtEn), produitId, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), OTUser);
                break;
            case RECAP:
                file = balance.recap(params);
                break;
            case STAT_FAMILLE_ARTICLE:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                file = balance.familleArticle(dtStart, dtEnd, codeFamile, query, OTUser, codeRayon, codeGrossiste);
                break;
            case EDITION20_80:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                boolean qtyOrCa = Boolean.valueOf(request.getParameter("qtyOrCa"));
                file = balance.geVingtQuatreVingt(dtStart, dtEnd, OTUser, codeFamile, codeRayon, codeGrossiste, qtyOrCa);
                break;
            case PERIMES:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                String _filtre = request.getParameter("filtre");
                Peremption filtre = Peremption.PERIME;
                if (!StringUtils.isEmpty(_filtre)) {
                    filtre = Peremption.valueOf(_filtre);
                }
                file = balance.produitPerimes(query, dtStart, filtre, OTUser, codeFamile, codeRayon, codeGrossiste);
                break;
            case STAT_PROVIDER_ARTICLE:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                file = balance.statistiqueParGrossistes(dtStart, dtEnd, codeFamile, query, OTUser, codeRayon, codeGrossiste);
                break;
            case STAT_RAYONS_ARTICLE:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                file = balance.statistiqueParRayons(dtStart, dtEnd, codeFamile, query, OTUser, codeRayon, codeGrossiste);
                break;
            case UNITES_AVOIRS:
                dtStart = request.getParameter("dtStart");
                dtEnd = request.getParameter("dtEnd");
                String typeVenteId = request.getParameter("typeVenteId"),
                 hEnd = request.getParameter("hEnd"),
                 hStart = request.getParameter("hStart");
                query = request.getParameter("query");
                SalesStatsParams body = new SalesStatsParams();
                LstTPrivilege = (List<TPrivilege>) session.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
                boolean asAuthority = DateConverter.hasAuthorityByName(LstTPrivilege, commonparameter.str_SHOW_VENTE);
                boolean allActivitis = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_SHOW_ALL_ACTIVITY);
                boolean canCancel = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_BT_ANNULER_VENTE);
                boolean modification = DateConverter.hasAuthorityByName(LstTPrivilege, DateConverter.P_BT_MODIFICATION_DE_VENTE);

                body.setCanCancel(canCancel);

                body.setQuery(query);
                body.setTypeVenteId(typeVenteId);
                body.setStatut(commonparameter.statut_is_Closed);
                body.setAll(true);
                body.setUserId(OTUser);
                body.setOnlyAvoir(true);
                 body.setSansBon(false);
                body.setShowAll(asAuthority);
                body.setShowAllActivities(allActivitis);

                body.setModification(modification);
                try {
                    body.sethEnd(LocalTime.parse(hEnd));
                } catch (Exception e) {
                }
                try {
                    body.sethStart(LocalTime.parse(hStart));
                } catch (Exception e) {
                }
                try {
                    body.setDtEnd(LocalDate.parse(dtEnd));
                    body.setDtStart(LocalDate.parse(dtStart));
                } catch (Exception e) {
                }
                file=balance.listeVentes(body);
                break;
            default:
                break;
        }
        response.sendRedirect(request.getContextPath() + file);

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

}
