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
import rest.report.ReportUtil;
import rest.service.CommonService;
import rest.service.EtatControlBonService;
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

        response.sendRedirect(request.getContextPath() + buildReport(request));
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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
        List<EtatControlBon> datas = this.etatControlBonService.list(search, dtStart, dtEnd, grossisteId, 0, 0, true);

        return reportUtil.buildReport(parameters, reportName, datas);

    }

}
