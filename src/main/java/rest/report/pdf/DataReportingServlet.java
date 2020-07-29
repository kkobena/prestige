/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.report.pdf;

import dal.TUser;
import enumeration.MargeEnum;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import toolkits.parameters.commonparameter;

/**
 *
 * @author DICI
 */
public class DataReportingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    @EJB
    DataReporting dataReporting;

    private enum ActionDataReporting {
        MARGE_PRODUITS, UNITES_VENDUES, UNITES_GAMME, UNITES_LABORATOIRES, ARTICLES_NON_VENDUES, ARTICLES_SUR_STOCK,
        COMPARAISON_STOCK, COMPARAISON_STOCK_DETAIL
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        HttpSession session = request.getSession();
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        String action = request.getParameter("mode");
        String dtStart = request.getParameter("dtStart");
        String dtEnd = request.getParameter("dtEnd");
        String codeFamile,
                codeRayon;
        String codeGrossiste;
        String query;
        String file = "";
        switch (ActionDataReporting.valueOf(action)) {
            case MARGE_PRODUITS:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                Integer critere = null;
                if (!StringUtils.isEmpty(request.getParameter("critere"))) {

                    critere = Integer.valueOf(request.getParameter("critere"));
                }
                MargeEnum filtre = MargeEnum.valueOf(request.getParameter("filtre"));
                file = dataReporting.margeProduitsVendus(dtStart, dtEnd, codeFamile, critere, query, OTUser, codeRayon, codeGrossiste, filtre);
                break;
            case UNITES_VENDUES:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                file = dataReporting.statsUnintesVendues(dtStart, dtEnd, codeFamile, query, OTUser, codeRayon, codeGrossiste);
                break;
            case UNITES_GAMME:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                String gammeId = request.getParameter("gammeId");
                file = dataReporting.statsUnintesVenduesparGamme(dtStart, dtEnd, codeFamile, query, OTUser, codeRayon, codeGrossiste, gammeId);
                break;
            case UNITES_LABORATOIRES:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                String laboratoireId = request.getParameter("laboratoireId");
                file = dataReporting.statsUnintesVenduesparLaboratoire(dtStart, dtEnd, codeFamile, query, OTUser, codeRayon, codeGrossiste, laboratoireId);
                break;
            case ARTICLES_NON_VENDUES:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                Integer stock = 0;
                if (!StringUtils.isEmpty(request.getParameter("stock"))) {
                    try {
                        stock = Integer.valueOf(request.getParameter("stock"));
                    } catch (Exception e) {
                    }

                }
                MargeEnum stockFiltre = MargeEnum.valueOf(request.getParameter("stockFiltre"));
                file = dataReporting.statsArticlesInvendus(dtStart, dtEnd, codeFamile, query, OTUser, codeRayon, codeGrossiste, stock, stockFiltre);
                break;
            case ARTICLES_SUR_STOCK:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");

                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                Integer nbreMois = 0,
                 nbreConsommation = 3;
                if (!StringUtils.isEmpty(request.getParameter("nbreMois"))) {
                    try {
                        nbreMois = Integer.valueOf(request.getParameter("nbreMois"));
                    } catch (Exception e) {
                    }

                }
                if (!StringUtils.isEmpty(request.getParameter("nbreConsommation"))) {
                    try {
                        nbreConsommation = Integer.valueOf(request.getParameter("nbreConsommation"));
                    } catch (Exception e) {
                    }

                }
                file = dataReporting.articleSurStock(codeFamile, query, OTUser, codeRayon, codeGrossiste, nbreMois, nbreConsommation);
                break;

            case COMPARAISON_STOCK:
                codeFamile = request.getParameter("codeFamile");
                codeRayon = request.getParameter("codeRayon");
                MargeEnum filtreStock = null;
                MargeEnum filtreSeuil = null;
                try {
                    filtreStock = MargeEnum.valueOf(request.getParameter("filtreStock"));
                } catch (Exception e) {
                }
                try {
                    filtreSeuil = MargeEnum.valueOf(request.getParameter("filtreSeuil"));
                } catch (Exception e) {
                }
                codeGrossiste = request.getParameter("codeGrossiste");
                query = request.getParameter("query");
                stock = 0;
                int seuil = 0;
                try {
                    seuil = Integer.valueOf(request.getParameter("seuil"));
                } catch (NumberFormatException e) {
                }
                try {
                    stock = Integer.valueOf(request.getParameter("stock"));
                } catch (NumberFormatException e) {
                }
                file = dataReporting.comparaisonStock(OTUser, query, filtreStock, filtreSeuil, codeFamile, codeRayon, codeGrossiste, stock, seuil);
                break;

            case COMPARAISON_STOCK_DETAIL:
                dtStart = request.getParameter("dtStart");
                dtEnd = request.getParameter("dtEnd");
                query = request.getParameter("query");
                String id = request.getParameter("id");
                String libelle = request.getParameter("libelle");
                String cip = request.getParameter("cip");
                file = dataReporting.produitConsomamation(OTUser, query, dtStart, dtEnd, id, libelle, cip);
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
