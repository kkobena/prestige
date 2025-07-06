/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import java.io.ByteArrayOutputStream;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import rest.service.AnalyseInvDTOService;
import rest.service.dto.AnalyseInvDTO;

/**
 *
 * @author airman
 */
@Path("v1/analyse-inventaire-excel")
public class AnalyseInvExcelRessource {

    @EJB
    private AnalyseInvDTOService analyseInvDTOService;

    // Classe interne simple pour contenir les totaux d'un emplacement
    public static class EmplacementSummary {
        private String emplacement;
        private Long valeurAchatMachine = 0L, valeurAchatRayon = 0L;
        private Long valeurVenteMachine = 0L, valeurVenteRayon = 0L;

        // Getters et Setters nécessaires
        public String getEmplacement() {
            return emplacement;
        }

        public void setEmplacement(String e) {
            this.emplacement = e;
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
    @Produces("application/vnd.ms-excel") // Type MIME pour les anciens fichiers .xls
    public Response generateExcel(@FormParam("inventaireId") String inventaireId,
            @FormParam("inventaireName") String inventaireName) {
        try {
            List<AnalyseInvDTO> data = analyseInvDTOService.listAnalyseInv(inventaireId);

            // Regroupement et calcul des totaux par emplacement
            Map<String, EmplacementSummary> summaryMap = new HashMap<>();
            for (AnalyseInvDTO item : data) {
                summaryMap.computeIfAbsent(item.getEmplacement(), k -> {
                    EmplacementSummary summary = new EmplacementSummary();
                    summary.setEmplacement(k);
                    return summary;
                });
                EmplacementSummary summary = summaryMap.get(item.getEmplacement());
                summary.valeurAchatMachine += (long) item.getQteInitiale() * item.getPrixAchat();
                summary.valeurAchatRayon += (long) item.getQteSaisie() * item.getPrixAchat();
                summary.valeurVenteMachine += (long) item.getQteInitiale() * item.getPrixVente();
                summary.valeurVenteRayon += (long) item.getQteSaisie() * item.getPrixVente();
            }
            List<EmplacementSummary> summaryList = new ArrayList<>(summaryMap.values());

            // Utilisation de HSSFWorkbook pour le format .xls compatible avec les anciennes versions de POI
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Analyse Inventaire");

            // Création des styles pour l'en-tête
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            // Création de la ligne d'en-tête
            String[] headers = { "Emplacement", "V.Achat Machine", "V.Achat Rayon", "Écart V.Achat", "(%) Écart Achat",
                    "V.Vente Machine", "V.Vente Rayon", "Écart V.Vente", "(%) Écart Vente" };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Remplissage des données pour chaque emplacement
            int rowNum = 1;
            for (EmplacementSummary summary : summaryList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(summary.getEmplacement());
                row.createCell(1).setCellValue(summary.getValeurAchatMachine());
                row.createCell(2).setCellValue(summary.getValeurAchatRayon());
                long ecartAchat = summary.getValeurAchatRayon() - summary.getValeurAchatMachine();
                row.createCell(3).setCellValue(ecartAchat);
                double percentAchat = (summary.getValeurAchatMachine() != 0)
                        ? ((double) ecartAchat / summary.getValeurAchatMachine()) * 100 : 0;
                row.createCell(4).setCellValue(percentAchat);

                row.createCell(5).setCellValue(summary.getValeurVenteMachine());
                row.createCell(6).setCellValue(summary.getValeurVenteRayon());
                long ecartVente = summary.getValeurVenteRayon() - summary.getValeurVenteMachine();
                row.createCell(7).setCellValue(ecartVente);
                double percentVente = (summary.getValeurVenteMachine() != 0)
                        ? ((double) ecartVente / summary.getValeurVenteMachine()) * 100 : 0;
                row.createCell(8).setCellValue(percentVente);
            }

            // Ajustement automatique de la largeur des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écriture du classeur dans un flux d'octets en mémoire
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            // Préparation de la réponse HTTP avec le nom de fichier horodaté
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
            String timestamp = LocalDateTime.now().format(formatter);
            String filename = "analyse_par_emplacement_" + inventaireName.replace(" ", "_") + "_" + timestamp + ".xls"; // Extension
                                                                                                                        // .xls

            return Response.ok(baos.toByteArray())
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Erreur interne lors de la génération du fichier Excel: " + e.getMessage()).build();
        }
    }
}
