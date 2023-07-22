package rest.report.pdf;

import dal.TOfficine;
import dal.TUser;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
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
import rest.service.dto.EtatControlBon;
import rest.service.dto.StatistiqueProduitAnnuelleDTO;
import rest.service.report.StatistiqueProduitService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author koben
 */
public class StatProduitServlet extends HttpServlet {

    @EJB
    private StatistiqueProduitService statistiqueProduitService;
    @EJB
    private ReportUtil reportUtil;
    @EJB
    private CommonService commonService;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");

        response.sendRedirect(request.getContextPath() + buildReport(request));
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

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public String buildReport(HttpServletRequest request) {
        HttpSession session = request.getSession();
        TUser user = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        int period = Year.now().getValue();
        String year = request.getParameter("year");
        if (StringUtils.isNotEmpty(year)) {
            period = Integer.parseInt(year);
        }
        String rayonId = request.getParameter("rayonId");

        String search = request.getParameter("search");
        TOfficine oTOfficine = commonService.findOfficine();
        LocalDate end;
        LocalDate dtSt = LocalDate.of(period, Month.JANUARY, 1);
        if (period == Year.now().getValue()) {
            end = LocalDate.now();
        } else {
            end = LocalDate.of(period, Month.DECEMBER, 31);
        }

        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, user);
        String periode = dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        periode += " AU " + end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String reportName = "rp_qtystat";

        parameters.put("P_H_CLT_INFOS", "QUANTITE PRODUITS VENDUS  DU  " + periode);
        List<StatistiqueProduitAnnuelleDTO> datas = this.statistiqueProduitService.getVenteProduits(period, search,
                user.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), rayonId, 0, 0, true);

        return reportUtil.buildReport(parameters, reportName, datas);

    }

}
