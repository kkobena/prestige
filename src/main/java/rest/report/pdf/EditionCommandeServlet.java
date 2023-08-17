package rest.report.pdf;

import dal.TOfficine;
import dal.TUser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import rest.report.ReportUtil;
import rest.service.CommonService;
import rest.service.OrderService;
import rest.service.dto.CommandeEncourDetailDTO;
import rest.service.dto.CommandeFiltre;
import toolkits.parameters.commonparameter;

/**
 *
 * @author koben
 */
@WebServlet(name = "EditionCommandeServlet", urlPatterns = { "/EditionCommandeServlet" })
public class EditionCommandeServlet extends HttpServlet {

    @EJB
    private ReportUtil reportUtil;
    @EJB
    private CommonService commonService;
    @EJB
    private OrderService orderService;

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
        String orderId = request.getParameter("orderId");
        String refCommande = request.getParameter("refCommande");
        TOfficine oTOfficine = commonService.findOfficine();

        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, user);

        String reportName = "rp_order_items";

        parameters.put("P_H_CLT_INFOS", String.format("COMMANDE DE REAPPROVISIONNEMENT [ %s  ]", refCommande));

        List<CommandeEncourDetailDTO> datas = this.orderService.fetchOrderItems(CommandeFiltre.ALL, orderId, null, 0, 0,
                true);

        return reportUtil.buildReport(parameters, reportName, datas);

    }
}
