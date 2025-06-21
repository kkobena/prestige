/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.TInventaireFamille;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONObject;

@WebServlet(name = "ImportInventaire", urlPatterns = { "/ImportInventaire" })
@MultipartConfig(fileSizeThreshold = 5242880, maxFileSize = 20971520L, maxRequestSize = 41943040L)
public class ImportInventaire extends HttpServlet {

    private JsonBuilderFactory factory;

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String lg_INVENTAIRE_ID = request.getParameter("lg_INVENTAIRE_ID");
        Part part = request.getPart("str_FILE");
        String fileName = part.getSubmittedFileName();
        String extension = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
        JSONObject _json;
        factory = Json.createBuilderFactory(null);
        JsonObjectBuilder json = factory.createObjectBuilder();
        try (PrintWriter out = response.getWriter()) {
            switch (extension) {
            case "csv":
                _json = bulkUpdate(part, lg_INVENTAIRE_ID);
                json.add("statut", 1);
                json.add("success", "<span style='color:blue;font-weight:800;'>" + _json.getInt("count") + "/"
                        + _json.getInt("ligne") + "</span> produits mis à jour");
                break;

            default:
                _json = bulkUpdateWithExcel(part, lg_INVENTAIRE_ID);
                json.add("statut", 1);

                json.add("success", "<span style='color:blue;font-weight:800;'>" + _json.getInt("count") + "/"
                        + _json.getInt("ligne") + "</span> produits mis à jour");

                break;
            }
            out.println(json.build());
        } catch (Exception ex) {
            Logger.getLogger(ImportInventaire.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the
    // code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     *
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     *
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
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

    private JSONObject bulkUpdate(Part part, String lg_INVENTAIRE_ID) throws Exception {
        int count = 0;
        int i = 0;
        JSONObject json = new JSONObject();

        // CSVParser parser = new CSVParser(new InputStreamReader(part.getInputStream()),
        // CSVFormat.DEFAULT.withDelimiter(';'));
        CSVParser parser = new CSVParser(new InputStreamReader(part.getInputStream()), CSVFormat.EXCEL);

        for (CSVRecord cSVRecord : parser) {
            System.err.println("cSVRecord.get(0)   " + cSVRecord.get(0) + "   " + cSVRecord.get(1)
                    + " lg_INVENTAIRE_ID " + lg_INVENTAIRE_ID);
            TInventaireFamille inventaireFamille = findByArticleAndInventaire(cSVRecord.get(0), lg_INVENTAIRE_ID);
            // i += createTOrderDetailVIACSV(em, grossiste, order, cSVRecord.get(2),
            // Integer.valueOf(cSVRecord.get(5)), Double.valueOf(cSVRecord.get(6)).intValue(),
            // Double.valueOf(cSVRecord.get(7)).intValue());
            inventaireFamille.setIntNUMBER(Integer.valueOf(cSVRecord.get(1)));
            inventaireFamille.setDtUPDATED(new Date());
            em.merge(inventaireFamille);
            i++;

            count++;
        }

        json.put("count", i);
        json.put("ligne", count - 1);

        return json;
    }

    private TInventaireFamille findByArticleAndInventaire(String cipArticle, String idInventaire) throws Exception {
        TypedQuery<TInventaireFamille> query = em.createQuery(
                " SELECT o FROM  TInventaireFamille o WHERE o.lgINVENTAIREID.lgINVENTAIREID =?1 AND o.lgFAMILLEID.intCIP =?2  ",
                TInventaireFamille.class);
        query.setFirstResult(0).setMaxResults(1).setParameter(1, idInventaire).setParameter(2, cipArticle);
        return query.getSingleResult();
    }

    private JSONObject bulkUpdateWithExcel(Part part, String lg_INVENTAIRE_ID) throws Exception {
        int count = 0;
        int i = 0;
        JSONObject json = new JSONObject();

        Workbook workbook = new HSSFWorkbook(part.getInputStream());

        int num = workbook.getNumberOfSheets();

        for (int j = 0; j < num; j++) {
            Sheet sheet = workbook.getSheetAt(j);
            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                if (count > 0) {
                    Row nextrow = (Row) rows.next();
                    Cell id = nextrow.getCell(1);
                    Cell qty = nextrow.getCell(4);
                    TInventaireFamille inventaireFamille = findByArticleAndInventaire(id.getStringCellValue(),
                            lg_INVENTAIRE_ID);
                    inventaireFamille.setIntNUMBER(Double.valueOf(qty.getNumericCellValue()).intValue());
                    inventaireFamille.setDtUPDATED(new Date());
                    em.merge(inventaireFamille);
                    i++;
                }

                count++;
            }

            json.put("count", i);
            json.put("ligne", count - 1);
        }

        json.put("count", i);
        json.put("ligne", count - 1);

        return json;
    }

}
