package rest.report.pdf;

import commonTasks.dto.Params;
import commonTasks.dto.TvaDTO;
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import rest.report.ReportUtil;
import rest.service.CaisseService;
import rest.service.TvaDataService;
import toolkits.parameters.commonparameter;
import toolkits.utils.jdom;

/**
 *
 * @author koben
 */
@WebServlet(name = "TvaServlet", urlPatterns = {"/TvaServlet"})
public class TvaServlet extends HttpServlet {

    @EJB
    private CaisseService caisseService;
    @EJB
    private ReportUtil reportUtil;
    @EJB
    private TvaDataService tvaDataService;

    private enum TvaAction {
        TVA, TVA_JOUR,
        TVA_WITH_CRITERIA, TVA_JOUR_WITH_CRITERIA
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        HttpSession session = request.getSession();
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        String action = request.getParameter("mode");
        String dtStart = request.getParameter("dtStart");
        String dtEnd = request.getParameter("dtEnd");
        boolean checkug = false;
        try {
            checkug = Boolean.valueOf(request.getParameter("checkug"));
        } catch (Exception e) {
        }
        Params params = new Params();
        params.setOperateur(OTUser);
        String file = "";
        if (dtEnd != null && !"".equals(dtEnd)) {
            params.setDtEnd(dtEnd);
        }
        if (dtStart != null && !"".equals(dtStart)) {
            params.setDtStart(dtStart);
        }
        params.setCheckug(checkug);
        switch (TvaAction.valueOf(action)) {
            case TVA:
                file = tvaPdf(params);
                break;
            case TVA_WITH_CRITERIA:
                file = tvaPdfWithCriteria(params);
                break;
            case TVA_JOUR:
                file = tvaPdfGroupByJour(params);
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

    public String tvaPdfWithCriteria(Params parasm) throws IOException {

        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(parasm.getDtStart());
            dtEn = LocalDate.parse(parasm.getDtEnd());
        } catch (Exception e) {
        }
        TUser tu = parasm.getOperateur();

        TOfficine oTOfficine = caisseService.findOfficine();
        String scr_report_file = "rp_tvastat";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        }
        parameters.put("P_H_CLT_INFOS", "Statistiques des\n Résultats par Taux de TVA  " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + ".pdf";
        List<TvaDTO> datas;

        if (!tvaDataService.isExcludTiersPayantActive()) {
            datas = tvaDataService.statistiqueTvaWithSomeCriteria(parasm);
        } else {
            datas = tvaDataService.statistiqueTvaWithSomeTiersPayantToExclude(parasm);
        }
        datas.sort(Comparator.comparing(TvaDTO::getTaux));
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "tvastat_" + report_generate_file, datas);
        return "/data/reports/pdf/tvastat_" + report_generate_file;
    }

    public String tvaPdf(Params parasm) throws IOException {

        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(parasm.getDtStart());
            dtEn = LocalDate.parse(parasm.getDtEnd());
        } catch (Exception e) {
        }
        TUser tu = parasm.getOperateur();

        TOfficine oTOfficine = caisseService.findOfficine();
        String scr_report_file = "rp_tvastat";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        }
        parameters.put("P_H_CLT_INFOS", "Statistiques des\n Résultats par Taux de TVA  " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + ".pdf";
        List<TvaDTO> datas;
        if (StringUtils.isNotBlank(parasm.getRef()) && !parasm.getRef().equalsIgnoreCase("TOUT")) {
            datas = tvaDataService.tvaVnoData(parasm);
        } else {
            datas = tvaDataService.statistiqueTva(parasm);
        }

        datas.sort(Comparator.comparing(TvaDTO::getTaux));
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "tvastat_" + report_generate_file, datas);
        return "/data/reports/pdf/tvastat_" + report_generate_file;
    }

    public String tvaPdfGroupByJour(Params parasm) throws IOException {

        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(parasm.getDtStart());
            dtEn = LocalDate.parse(parasm.getDtEnd());
        } catch (Exception e) {
        }
        TUser tu = parasm.getOperateur();

        TOfficine oTOfficine = caisseService.findOfficine();
        String scr_report_file = "rp_tvastat";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        }
        parameters.put("P_H_CLT_INFOS", "Statistiques des\n Résultats par Taux de TVA  " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + ".pdf";
        List<TvaDTO> datas;
        if (StringUtils.isNotBlank(parasm.getRef()) && !parasm.getRef().equalsIgnoreCase("TOUT")) {
            datas = tvaDataService.statistiqueTvaVnoGroupByDayTva(parasm);
        } else {
            datas = tvaDataService.statistiqueGroupByDayTva(parasm);
        }

        datas.sort(Comparator.comparing(TvaDTO::getTaux));
        reportUtil.buildReport(parameters, scr_report_file, jdom.scr_report_file, jdom.scr_report_pdf + "tvastat_" + report_generate_file, datas);
        return "/data/reports/pdf/tvastat_" + report_generate_file;
    }
}
