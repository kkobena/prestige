/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.report.pdf.excel;

import commonTasks.dto.Params;
import commonTasks.dto.TableauBaordPhDTO;
import commonTasks.dto.TableauBaordSummary;
import dal.TOfficine;
import dal.TUser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import rest.report.ReportUtil;
import rest.service.CaisseService;
import toolkits.parameters.commonparameter;
import toolkits.utils.jdom;

/**
 *
 * @author koben
 */
@WebServlet(name = "ExcelExporter", urlPatterns = {"/ExcelExporter"})
public class ExcelExporter extends HttpServlet {

    private static final long serialVersionUID = 1L;
    DateFormat df = new SimpleDateFormat("dd_MM_YYYY_HH_mm_ss");
    @EJB
    CaisseService caisseService;
    @EJB
    ReportUtil reportUtil;

    private enum Action {
        TABLEAU
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        String action = request.getParameter("mode");
        String dtStart = request.getParameter("dtStart");
        String dtEnd = request.getParameter("dtEnd");
        Params params = new Params();
        params.setOperateur(OTUser);

        if (dtEnd != null && !"".equals(dtEnd)) {
            params.setDtEnd(dtEnd);
        }
        if (dtStart != null && !"".equals(dtStart)) {
            params.setDtStart(dtStart);
        }

        switch (Action.valueOf(action)) {
            case TABLEAU:
                boolean ration = Boolean.valueOf(request.getParameter("ration"));
                boolean monthly = Boolean.valueOf(request.getParameter("monthly"));
                exportToxlsx(response, new File( tableauBordPharmation(params, ration, monthly)));
                break;
            default:
                break;
        }
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

    private void exportToxlsx(HttpServletResponse response, File filetoExport) {
        OutputStream out = null;
        FileInputStream inStream = null;
        try {
            out = response.getOutputStream();
            inStream = new FileInputStream(filetoExport);
            String filename = filetoExport.getName() + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setContentLengthLong(filetoExport.length());
            response.setHeader("Content-disposition", "inline; filename=" + filename);
            OutputStream outStream = response.getOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException ex) {
            Logger.getLogger(ExcelExporter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (inStream != null) {
                try {
                    if (out != null) {
                        out.flush();
                    }
                    inStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExcelExporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    private String tableauBordPharmation(Params parasm, boolean ratio, boolean monthly) {
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(parasm.getDtStart());
            dtEn = LocalDate.parse(parasm.getDtEnd());
        } catch (Exception e) {
        }
        TUser tu = parasm.getOperateur();
        TOfficine oTOfficine = caisseService.findOfficine();
        String scr_report_file = "rp_pharma_dashboard";
        Map<String, Object> parameters = reportUtil.officineData(oTOfficine, tu);
        String P_PERIODE = "PERIODE DU " + dtSt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dtEn.isEqual(dtSt)) {
            P_PERIODE += " AU " + dtEn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        }
        parameters.put("P_H_CLT_INFOS", "TABLEAU DE BORD DU PHARMACIEN \nARRETE " + P_PERIODE);
        String report_generate_file = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + ".xlsx";
        List<TableauBaordPhDTO> datas = new ArrayList<>();
        Map<TableauBaordSummary, List<TableauBaordPhDTO>> map;
        if (monthly) {
            map = caisseService.tableauBoardDatasMonthly(dtSt, dtEn, Boolean.TRUE, tu, 0, 0, 0, true);
        } else {
            map = caisseService.tableauBoardDatas(dtSt, dtEn, Boolean.TRUE, tu, 0, 0, 0, true);
        }

        if (!map.isEmpty()) {
            map.forEach((k, v) -> {
                datas.addAll(v);
                parameters.put("montantEsp", k.getMontantEsp());
                parameters.put("montantNet", k.getMontantNet());
                parameters.put("ration", ratio);
                parameters.put("montantRemise", k.getMontantRemise());
                parameters.put("montantCredit", k.getMontantCredit());
                parameters.put("nbreVente", k.getNbreVente());
                parameters.put("montantAchatOne", k.getMontantAchatOne());
                parameters.put("montantAchatTwo", k.getMontantAchatTwo());
                parameters.put("montantAchatThree", k.getMontantAchatThree());
                parameters.put("montantAchatFour", k.getMontantAchatFour());
                parameters.put("montantAchatFive", k.getMontantAchatFive());
                parameters.put("montantAchat", k.getMontantAchat());
                parameters.put("montantAvoir", k.getMontantAvoir());
                parameters.put("ratioVA", k.getRatioVA());
                parameters.put("rationAV", k.getRationAV());

            });
        }
        String finalFilePath = jdom.scr_report_pdf + "tableau_de_bord_" + report_generate_file;
        reportUtil.buildReportExcelSinglePage(parameters, scr_report_file, jdom.scr_report_file, finalFilePath, datas);
        return finalFilePath;
    }
}
