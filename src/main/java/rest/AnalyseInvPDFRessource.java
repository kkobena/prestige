package rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import rest.service.AnalyseAvanceeService;
import rest.service.AnalyseAvanceeService.AnalyseAvanceeDTO;

/**
 *
 * @author airman
 */

@Path("v1/analyse-inventaire-pdf")
public class AnalyseInvPDFRessource {

    private static final String REPORTS_PATH = "D:" + File.separator + "CONF" + File.separator + "LABOREX"
            + File.separator + "REPORTS" + File.separator;

    @EJB
    private AnalyseAvanceeService analyseAvanceeService;

    public static class EmplacementSummary {
        private String emplacement;
        private Long valeurAchatMachine, valeurAchatRayon, ecartValeurAchat;
        private Long valeurVenteMachine, valeurVenteRayon, ecartValeurVente;
        private Double pourcentageEcartGlobal, ratioVA;

        // Getters et Setters pour tous les champs
        public String getEmplacement() {
            return emplacement;
        }

        public void setEmplacement(String emplacement) {
            this.emplacement = emplacement;
        }

        public Long getValeurAchatMachine() {
            return valeurAchatMachine;
        }

        public void setValeurAchatMachine(Long v) {
            this.valeurAchatMachine = v;
        }

        public Long getValeurAchatRayon() {
            return valeurAchatRayon;
        }

        public void setValeurAchatRayon(Long v) {
            this.valeurAchatRayon = v;
        }

        public Long getEcartValeurAchat() {
            return ecartValeurAchat;
        }

        public void setEcartValeurAchat(Long v) {
            this.ecartValeurAchat = v;
        }

        public Long getValeurVenteMachine() {
            return valeurVenteMachine;
        }

        public void setValeurVenteMachine(Long v) {
            this.valeurVenteMachine = v;
        }

        public Long getValeurVenteRayon() {
            return valeurVenteRayon;
        }

        public void setValeurVenteRayon(Long v) {
            this.valeurVenteRayon = v;
        }

        public Long getEcartValeurVente() {
            return ecartValeurVente;
        }

        public void setEcartValeurVente(Long v) {
            this.ecartValeurVente = v;
        }

        public Double getPourcentageEcartGlobal() {
            return pourcentageEcartGlobal;
        }

        public void setPourcentageEcartGlobal(Double v) {
            this.pourcentageEcartGlobal = v;
        }

        public Double getRatioVA() {
            return ratioVA;
        }

        public void setRatioVA(Double v) {
            this.ratioVA = v;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/pdf")
    public Response generatePdf(@FormParam("data") String jsonData, @FormParam("inventaireId") String inventaireId,
            @FormParam("inventaireName") String inventaireName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<EmplacementSummary> summaryList = mapper.readValue(jsonData,
                    new TypeReference<List<EmplacementSummary>>() {
                    });
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(summaryList);

            // --- CORRECTION : Appel du service avec les deux paramètres ---
            AnalyseAvanceeDTO fullData = analyseAvanceeService.getAnalyseAvancee(inventaireId, inventaireName);
            String analysisText = fullData.getAnalysisText();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("INVENTAIRE_NAME", inventaireName);
            long modifiedProducts = fullData.getDetailProduits().stream().filter(p -> p.getEcartQuantite() != 0)
                    .count();
            parameters.put("TOTAL_PRODUITS", fullData.getDetailProduits().size());
            parameters.put("PRODUITS_MODIFIES", (int) modifiedProducts);
            parameters.put("ANALYSIS_TEXT", analysisText);

            File reportFile = new File(REPORTS_PATH + "analyse_inventaire.jrxml");
            if (!reportFile.exists()) {
                return Response.serverError()
                        .entity("Le fichier de rapport est introuvable : " + reportFile.getAbsolutePath()).build();
            }

            InputStream reportStream = new FileInputStream(reportFile);
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
            String timestamp = LocalDateTime.now().format(formatter);
            String filename = "analyse_synthetique_" + inventaireName.replace(" ", "_") + "_" + timestamp + ".pdf";

            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Erreur interne lors de la génération du rapport PDF: " + e.getMessage()).build();
        }
    }
}
