/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.report.pdf;

import commonTasks.dto.ReglementCarnetDTO;
import commonTasks.dto.VenteTiersPayantsDTO;
import dal.TOfficine;
import dal.TUser;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import rest.report.ReportUtil;
import rest.service.CaisseService;
import rest.service.TiersPayantExclusService;
import toolkits.parameters.commonparameter;
import toolkits.utils.jdom;

/**
 *
 * @author koben
 */
public class TiersPayantExcludServlet extends HttpServlet {

    @EJB
    private CaisseService caisseService;
    @EJB
    private ReportUtil reportUtil;
    @EJB
    private TiersPayantExclusService tiersPayantExclusService;

    private enum Action {
        VENTE, REGLEMENTS, RETOUR
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        HttpSession session = request.getSession();
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        String action = request.getParameter("mode");
        String dtStart = request.getParameter("dtStart");
        String dtEnd = request.getParameter("dtEnd");
        String tiersPayantId = request.getParameter("tiersPayantId");
        String file = "";
        switch (Action.valueOf(action)) {
            case REGLEMENTS:
                file = reglements(tiersPayantId, dtStart, dtEnd, OTUser);
                break;
            case VENTE:
                file = fetchVente(tiersPayantId, dtStart, dtEnd, OTUser);
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
    }// </editor-fold>

    public String fetchVente(String tiersPayantId, String dtStart, String dtEnd, TUser tu) throws IOException {
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }

        TOfficine oTOfficine = caisseService.findOfficine();
        String P_H_CLT_INFOS = "EXTRAIT COMPTE CLIENT ";
        String tiersPayant = " ";
        String scr_report_file = "rp_ventes_carnet_tp";
        if (StringUtils.isEmpty(tiersPayantId)) {
            scr_report_file = "rp_vente_tp_carnet";
        } else {
            tiersPayant = tiersPayantExclusService.getTiersPayantName(tiersPayantId);
        }

        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS + tiersPayant + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_dd_MM_HH_mm_ss")) + ".pdf";
        List<VenteTiersPayantsDTO> datas = tiersPayantExclusService.fetchVente(tiersPayantId, dtSt, dtEn, 0, 0, true);
        datas.sort(Comparator.comparing(VenteTiersPayantsDTO::getLibelleTiersPayant));
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "extrait_compte_vente_" + report_generate_file, datas);
        return "/data/reports/pdf/extrait_compte_vente_" + report_generate_file;
    }

    public String reglements(String tiersPayantId, String dtStart, String dtEnd, TUser tu) throws IOException {
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }

        TOfficine oTOfficine = caisseService.findOfficine();
        String P_H_CLT_INFOS = "EXTRAIT COMPTE CLIENT ";
        String tiersPayant = " ";
        String scr_report_file = "rp_reglement_carnet_tp";
        if (StringUtils.isEmpty(tiersPayantId)) {
            scr_report_file = "rp_reglement_carnet";
        } else {
            tiersPayant = tiersPayantExclusService.getTiersPayantName(tiersPayantId)+" ";
        }

        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS + tiersPayant + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_dd_MM_HH_mm_ss")) + ".pdf";
        List<ReglementCarnetDTO> datas = tiersPayantExclusService.reglementsCarnet(tiersPayantId, dtStart, dtEnd, 0, 0, true);
        datas.sort(Comparator.comparing(ReglementCarnetDTO::getTiersPayant));
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "extrait_compte_" + report_generate_file, datas);
        return "/data/reports/pdf/extrait_compte_" + report_generate_file;
    }
}
