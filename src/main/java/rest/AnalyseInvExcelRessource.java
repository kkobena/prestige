/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

/**
 *
 * @author airman
 */
@Path("v1/analyse-inventaire-excel")
public class AnalyseInvExcelRessource {

    // Classe interne pour mapper les données JSON envoyées par le frontend.
    // Elle doit avoir un getter pour chaque champ utilisé dans la grille ExtJS.
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
    @Produces("application/vnd.ms-excel")
    public Response generateExcel(@FormParam("data") String jsonData,
            @FormParam("inventaireName") String inventaireName) {
        try {
            // 1. Désérialiser les données filtrées envoyées par le frontend
            ObjectMapper mapper = new ObjectMapper();
            List<EmplacementSummary> summaryList = mapper.readValue(jsonData,
                    new TypeReference<List<EmplacementSummary>>() {
                    });

            // 2. Créer le classeur Excel
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Analyse Synthétique");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            // 3. Créer l'en-tête avec toutes les colonnes
            String[] headers = { "Emplacement", "V.Achat Machine", "V.Achat Inventaire", "Écart V.Achat",
                    "V.Vente Machine", "V.Vente Inventaire", "Écart V.Vente", "% Écart Global", "Ratio V/A" };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 4. Remplir les données
            int rowNum = 1;
            for (EmplacementSummary summary : summaryList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(summary.getEmplacement());
                row.createCell(1).setCellValue(summary.getValeurAchatMachine());
                row.createCell(2).setCellValue(summary.getValeurAchatRayon());
                row.createCell(3).setCellValue(summary.getEcartValeurAchat());
                row.createCell(4).setCellValue(summary.getValeurVenteMachine());
                row.createCell(5).setCellValue(summary.getValeurVenteRayon());
                row.createCell(6).setCellValue(summary.getEcartValeurVente());
                row.createCell(7).setCellValue(summary.getPourcentageEcartGlobal());
                row.createCell(8).setCellValue(summary.getRatioVA());
            }

            // Ajustement de la largeur des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 5. Générer le fichier et renvoyer la réponse
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
            String timestamp = LocalDateTime.now().format(formatter);
            String filename = "analyse_synthetique_" + inventaireName.replace(" ", "_") + "_" + timestamp + ".xls";

            return Response.ok(baos.toByteArray())
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Erreur interne lors de la génération du fichier Excel: " + e.getMessage()).build();
        }
    }
}
