package rest.report.pdf;

import dal.TOfficine;
import dal.TUser;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import rest.service.CommonService;
import rest.service.EtatControlBonService;
import rest.service.dto.EtatControlAnnuelDTO;
import rest.service.dto.EtatControlAnnuelWrapperDTO;
import rest.service.dto.EtatControlBon;
import toolkits.parameters.commonparameter;

/**
 *
 * @author koben
 */
public class EtatControlStockServlet extends HttpServlet {

    @EJB
    private EtatControlBonService etatControlBonService;
    @EJB
    private ReportUtil reportUtil;
    @EJB
    private CommonService commonService;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        String mode = request.getParameter("mode");
        if ("etatAnnuel".equals(mode)) {
            response.sendRedirect(request.getContextPath() + buildReportAnnuel(request));
        } else {
            response.sendRedirect(request.getContextPath() + buildReport(request));
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

    public String buildReport(HttpServletRequest request) {
        HttpSession session = request.getSession();
        TUser user = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

        String dtStart = request.getParameter("dtStart");
        String dtEnd = request.getParameter("dtEnd");
        String grossisteId = request.getParameter("grossisteId");

        String search = request.getParameter("search");
        TOfficine oTOfficine = commonService.findOfficine();
        LocalDate dtSt = LocalDate.parse(dtStart);
        LocalDate dtd = LocalDate.parse(dtEnd);
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, user);
        String periode = dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtSt.isEqual(dtd)) {
            periode += " AU " + dtd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        String reportName = "rp_etat_control_achats";

        parameters.put("P_H_CLT_INFOS", "LISTE DES ETATS DE CONTRÔLE D'ACHATS\n DU  " + periode);
        List<EtatControlBon> datas = this.etatControlBonService.list(true, search, dtStart, dtEnd, grossisteId, 0, 0,
                true);

        return reportUtil.buildReport(parameters, reportName, datas);

    }

    public String buildReportAnnuel(HttpServletRequest request) {
        HttpSession session = request.getSession();
        TUser user = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

        String dtStart = request.getParameter("dtStart");
        String dtEnd = request.getParameter("dtEnd");
        String grossisteId = request.getParameter("grossisteId");

        Integer groupeId = StringUtils.isNotEmpty(request.getParameter("groupeId"))
                ? Integer.valueOf(request.getParameter("groupeId")) : null;

        String groupBy = request.getParameter("groupBy");
        TOfficine oTOfficine = commonService.findOfficine();
        LocalDate dtSt = LocalDate.parse(dtStart);
        LocalDate dtd = LocalDate.parse(dtEnd);
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, user);
        String periode = dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtSt.isEqual(dtd)) {
            periode += " AU " + dtd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        String reportName = "rp_etat_control_achats_annuel";

        parameters.put("P_H_CLT_INFOS", "LISTE DES ETATS DE CONTRÔLE D'ACHATS ANNUEL \n DU  " + periode);
        EtatControlAnnuelWrapperDTO annuelSummary = this.etatControlBonService.listBonAnnuel(groupBy, dtStart, dtEnd,
                grossisteId, groupeId);
        List<EtatControlAnnuelDTO> annuels = annuelSummary.getEtatControlAnnuels();
        EtatControlAnnuelWrapperDTO.EtatControlAnnuelSummary summary = annuelSummary.getSummary();

        parameters.put("totalVenteTtc", summary.getTotalVenteTtc());
        parameters.put("totalMarge", summary.getTotalMarge());
        parameters.put("totalNbreBon", summary.getTotalNbreBon());
        parameters.put("totaltHtaxe", summary.getTotaltHtaxe());
        parameters.put("totalTaxe", summary.getTotalTaxe());
        parameters.put("totalTtc", summary.getTotalTtc());

        return reportUtil.buildReport(parameters, reportName, annuels);

    }

}
