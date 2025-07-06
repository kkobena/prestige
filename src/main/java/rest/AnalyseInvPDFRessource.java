package rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import rest.service.AnalyseInvDTOService;
import rest.service.dto.AnalyseInvDTO;

@Path("v1/analyse-inventaire-pdf")
public class AnalyseInvPDFRessource {

    private static final String REPORTS_PATH = "D:" + File.separator + "CONF" + File.separator + "LABOREX"
            + File.separator + "REPORTS" + File.separator;

    @EJB
    private AnalyseInvDTOService analyseInvDTOService;

    // Classe interne simple pour contenir les totaux d'un emplacement
    public static class EmplacementSummary {
        private String emplacement;
        private Long valeurAchatMachine;
        private Long valeurAchatRayon;
        private Long valeurVenteMachine;
        private Long valeurVenteRayon;

        // Getters et Setters nécessaires pour JasperReports
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
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/pdf")
    public Response generatePdf(@FormParam("inventaireId") String inventaireId,
            @FormParam("inventaireName") String inventaireName) {
        try {
            List<AnalyseInvDTO> data = analyseInvDTOService.listAnalyseInv(inventaireId);

            // Regroupement et calcul des totaux par emplacement
            Map<String, EmplacementSummary> summaryMap = new HashMap<>();
            for (AnalyseInvDTO item : data) {
                summaryMap.computeIfAbsent(item.getEmplacement(), k -> {
                    EmplacementSummary summary = new EmplacementSummary();
                    summary.setEmplacement(k);
                    summary.setValeurAchatMachine(0L);
                    summary.setValeurAchatRayon(0L);
                    summary.setValeurVenteMachine(0L);
                    summary.setValeurVenteRayon(0L);
                    return summary;
                });

                EmplacementSummary summary = summaryMap.get(item.getEmplacement());
                summary.setValeurAchatMachine(
                        summary.getValeurAchatMachine() + (long) item.getQteInitiale() * item.getPrixAchat());
                summary.setValeurAchatRayon(
                        summary.getValeurAchatRayon() + (long) item.getQteSaisie() * item.getPrixAchat());
                summary.setValeurVenteMachine(
                        summary.getValeurVenteMachine() + (long) item.getQteInitiale() * item.getPrixVente());
                summary.setValeurVenteRayon(
                        summary.getValeurVenteRayon() + (long) item.getQteSaisie() * item.getPrixVente());
            }

            // Conversion de la map en liste pour Jasper
            List<EmplacementSummary> summaryList = new ArrayList<>(summaryMap.values());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(summaryList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("INVENTAIRE_NAME", inventaireName);

            File reportFile = new File(REPORTS_PATH + "analyse_inventaire.jrxml");
            if (!reportFile.exists()) {
                return Response.serverError()
                        .entity("Le fichier de rapport est introuvable : " + reportFile.getAbsolutePath()).build();
            }

            InputStream reportStream = new FileInputStream(reportFile);
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // --- MODIFICATION POUR AJOUTER L'HORODATAGE ---
            // 1. Créer un formateur pour la date et l'heure
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
            String timestamp = LocalDateTime.now().format(formatter);

            // 2. Construire le nom de fichier final
            String sanitizedInventaireName = inventaireName.replace(" ", "_").replaceAll("[^a-zA-Z0-9_]", "");
            String filename = "analyse_par_emplacement_" + sanitizedInventaireName + "_" + timestamp + ".pdf";

            // 3. Utiliser le nouveau nom de fichier dans l'en-tête
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Erreur interne lors de la génération du rapport PDF: " + e.getMessage()).build();
        }
    }
}
