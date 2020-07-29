/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.report.pdf;

import dal.TUser;
import java.io.IOException;
import java.time.LocalDate;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import toolkits.parameters.commonparameter;

/**
 *
 * @author DICI
 */
public class SockServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    @EJB
    Stock stock;

    private enum Action {
        VALORISATION,RUPTURE_PHARMAML
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        HttpSession session = request.getSession();
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        String action = request.getParameter("mode");
        String dtStart = request.getParameter("dtStart");
        String dtEnd = request.getParameter("dtEnd");

        String file = "";

        switch (SockServlet.Action.valueOf(action)) {
            case VALORISATION:
                int mode = Integer.valueOf(request.getParameter("action"));
                String lgGROSSISTEID = request.getParameter("lgGROSSISTEID");
                String lgZONEGEOID = request.getParameter("lgZONEGEOID");
                String END = request.getParameter("END");
                String BEGIN = request.getParameter("BEGIN");
                String lgFAMILLEARTICLEID = request.getParameter("lgFAMILLEARTICLEID");
                file = stock.valorisation(OTUser, mode, LocalDate.parse(dtStart), lgGROSSISTEID, lgFAMILLEARTICLEID, lgZONEGEOID, END, BEGIN, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
                break;
            case RUPTURE_PHARMAML:
                 String query = request.getParameter("query");
                 String grossisteId=  request.getParameter("grossisteId");
                file=stock.rupturePharmaMl(OTUser, LocalDate.parse(dtStart), LocalDate.parse(dtEnd), query, grossisteId, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
                break;

            default:
                break;
        }
        response.sendRedirect(request.getContextPath() + file);
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

}
