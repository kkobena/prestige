package rest;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import rest.service.AnalyseAvanceeService;
import rest.service.AnalyseAvanceeService.AnalyseAvanceeDTO;

@Path("v1/analyse-avancee-pdf")
public class AnalyseAvanceePDFRessource {

    private static final String REPORTS_PATH = "D:" + File.separator + "CONF" + File.separator + "LABOREX"
            + File.separator + "REPORTS" + File.separator;

    @EJB
    private AnalyseAvanceeService analyseAvanceeService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/pdf")
    public Response generatePdf(@FormParam("inventaireId") String inventaireId,
            @FormParam("inventaireName") String inventaireName) {
        try {
            // 1. Récupérer l'ensemble des données d'analyse
            AnalyseAvanceeDTO data = analyseAvanceeService.getAnalyseAvancee(inventaireId, inventaireName);
            AnalyseAvanceeDTO datas = analyseAvanceeService.getAnalyseAvancee(inventaireId, inventaireName);
            // --- CORRECTION : Compilation explicite des sous-rapports ---
            // Le moteur Jasper a besoin des fichiers .jasper compilés.
            JasperCompileManager.compileReportToFile(REPORTS_PATH + "subreport_synthese.jrxml");
            JasperCompileManager.compileReportToFile(REPORTS_PATH + "subreport_abc_reco.jrxml");

            // 2. Préparer les sources de données pour les sous-rapports
            JRBeanCollectionDataSource syntheseDataSource = new JRBeanCollectionDataSource(data.getSynthese());
            JRBeanCollectionDataSource abcRecoDataSource = new JRBeanCollectionDataSource(data.getAnalyseABC());

            // 3. Préparer les paramètres du rapport principal
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("INVENTAIRE_NAME", inventaireName);
            parameters.put("SYNTHESE_DATA", syntheseDataSource);
            parameters.put("ABC_RECO_DATA", abcRecoDataSource);
            // parameters.put("ANALYSIS_TEXT", data.getAnalysisText());
            long modifiedProducts = data.getDetailProduits().stream().filter(p -> p.getEcartQuantite() != 0).count();
            parameters.put("TOTAL_PRODUITS", data.getDetailProduits().size());
            parameters.put("PRODUITS_MODIFIES", (int) modifiedProducts);
            parameters.put("ANALYSIS_TEXT", data.getAnalysisText());
            parameters.put("SUBREPORT_DIR", REPORTS_PATH); // Chemin vers les sous-rapports compilés

            // 4. Charger et remplir le rapport principal
            File reportFile = new File(REPORTS_PATH + "analyse_avancee.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportFile.getAbsolutePath());

            // On utilise une source de données vide car les données sont dans les sous-rapports
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // 5. Exporter en PDF et renvoyer la réponse
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
            String timestamp = LocalDateTime.now().format(formatter);
            String filename = "analyse_avancee_" + inventaireName.replace(" ", "_") + "_" + timestamp + ".pdf";

            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Erreur interne lors de la génération du rapport PDF avancé: " + e.getMessage()).build();
        }
    }
}
