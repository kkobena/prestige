/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TTypeStock;
import dal.TTypeStockFamille;
import dal.TUser;
import dal.dataManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import toolkits.parameters.commonparameter;

@WebServlet(name = "ImportDepot", urlPatterns = {"/ImportDepot"})
@MultipartConfig(
        fileSizeThreshold = 5242880,
        maxFileSize = 20971520L,
        maxRequestSize = 41943040L
)
public class ImportDepot extends HttpServlet {

    private final static Logger LOGGER = Logger.getLogger(ImportDepot.class.getName());
    TUser OTUser = null;
    private JsonBuilderFactory factory;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        dataManager OdataManager = new dataManager();
        OdataManager.initEntityManager();
        String format = request.getParameter("format");
        Part part = request.getPart("fichier");
        String fileName = part.getSubmittedFileName();
        String extension = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
        JSONObject _json;
        factory = Json.createBuilderFactory(null);
        JsonObjectBuilder json = factory.createObjectBuilder();
        try (PrintWriter out = response.getWriter()) {
            switch (extension) {
                case "csv":
//                    _json = bulkUpdate(part, OdataManager.getEm());
                    json.add("statut", 0);
//                    json.add("success", "<span style='color:blue;font-weight:800;'>" + _json.getInt("count") + "/" + _json.getInt("ligne") + "</span> produits mis à jour");
                    json.add("success", "Veuillez choisir un fichir excel");
                    break;

                default:
                    _json = bulkUpdateWithExcel(part, OdataManager.getEm());
                    json.add("statut", 1);

                    json.add("success", "<span style='color:blue;font-weight:800;'>" + _json.getInt("count") + "/" + _json.getInt("ligne") + "</span> produits mis à jour");

                    break;
            }
            out.println(json.build());
        } catch (Exception ex) {
            Logger.getLogger(ImportDepot.class.getName()).log(Level.SEVERE, null, ex);
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

   
    private JSONObject bulkUpdateWithExcel(Part part, EntityManager em) throws Exception {
        int count = 0;
        int i = 0;
        JSONObject json = new JSONObject();
        TEmplacement emplacement = OTUser.getLgEMPLACEMENTID();
        em.getTransaction().begin();

        Workbook workbook = new HSSFWorkbook(part.getInputStream());

        int num = workbook.getNumberOfSheets();

        for (int j = 0; j < num; j++) {
            Sheet sheet = workbook.getSheetAt(j);

            Iterator rows = sheet.rowIterator();

            while (rows.hasNext()) {
                Row nextrow = (Row) rows.next();
                if (nextrow.getRowNum() == 0) {
                    continue;
                }
                Cell id = nextrow.getCell(0);
                Cell cip = nextrow.getCell(1);
                Cell qty = nextrow.getCell(5);

                TFamille famille;
                if (id != null) {
                    famille = findFamille((id.getCellType() == 1) ? id.getStringCellValue() : String.valueOf(Double.valueOf(id.getNumericCellValue()).intValue()));
                } else {
                    famille = findFamilleByCip((cip.getCellType() == 1) ? cip.getStringCellValue() : String.valueOf(Double.valueOf(cip.getNumericCellValue()).intValue()), em);
                }
                try {
                    if (famille != null) {
                        em.persist(createFamilleStock((qty.getCellType() == 1) ? Integer.valueOf(qty.getStringCellValue()) : Double.valueOf(cip.getNumericCellValue()).intValue(), famille, emplacement));
                        em.persist(createTypeStock((qty.getCellType() == 1) ? Integer.valueOf(qty.getStringCellValue()) : Double.valueOf(cip.getNumericCellValue()).intValue(), famille, emplacement));
                        i++;
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.ALL, null, e);
                }

                if ((count % 100) == 0) {
                    em.getTransaction().commit();
                    em.clear();
                    em.getTransaction().begin();
                }
                count++;
            }
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
                em.clear();
                em.close();
            }
            json.put("count", i);
            json.put("ligne", count);
        }

        json.put("count", i);
        json.put("ligne", count);

        return json;
    }

    private TTypeStockFamille createTypeStock(int qty, TFamille f, TEmplacement emplacement) {
        TTypeStockFamille famille = new TTypeStockFamille(UUID.randomUUID().toString());
        famille.setIntNUMBER(qty);
        famille.setLgFAMILLEID(f);
        famille.setLgEMPLACEMENTID(emplacement);
        famille.setLgTYPESTOCKID(getTTypeStock("3"));
        famille.setStrNAME("");
        famille.setStrDESCRIPTION("");
        return famille;
    }

    private TFamilleStock createFamilleStock(int qty, TFamille famille, TEmplacement emplacement) {
        TFamilleStock stock = new TFamilleStock(UUID.randomUUID().toString());
        stock.setLgFAMILLEID(famille);
        stock.setLgEMPLACEMENTID(emplacement);
        stock.setIntNUMBER(qty);
        stock.setIntNUMBERAVAILABLE(qty);

        return stock;
    }

    private TTypeStock getTTypeStock(String id) {
        return new TTypeStock(id);
    }

    private TFamille findFamille(String id) {
        return new TFamille(id);
    }

    private TFamille findFamilleByCip(String cip, EntityManager em) {

        try {
            TypedQuery<TFamille> query = em.createNamedQuery("TFamille.findByIntCIP", TFamille.class);
            query.setParameter("intCIP", cip);
            query.setMaxResults(1);
            return query.getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }
}
