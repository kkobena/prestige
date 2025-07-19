
package rest;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
import rest.service.AnalyseAvanceeService;
import rest.service.AnalyseAvanceeService.*;

/**
 *
 * @author airman
 */
@Path("v1/analyse-avancee-excel")
public class AnalyseAvanceeExcelRessource {

    @EJB
    private AnalyseAvanceeService analyseAvanceeService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/vnd.ms-excel")
    public Response generateExcel(@FormParam("inventaireId") String inventaireId,
            @FormParam("inventaireName") String inventaireName) {
        try {
            // 1. Récupérer l'ensemble des données d'analyse via le service central
            AnalyseAvanceeDTO data = analyseAvanceeService.getAnalyseAvancee(inventaireId, inventaireName);

            // 2. Créer un classeur Excel
            Workbook workbook = new HSSFWorkbook();

            // 3. Créer chaque feuille de calcul avec les données correspondantes
            createSyntheseSheet(workbook, data.getSynthese());
            createAbcSheet(workbook, data.getAnalyseABC());
            createDetailSheet(workbook, data.getDetailProduits());

            // 4. Écrire le classeur dans un flux d'octets en mémoire
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            // 5. Préparer et renvoyer la réponse HTTP avec le nom de fichier horodaté
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
            String timestamp = LocalDateTime.now().format(formatter);
            String filename = "analyse_avancee_" + inventaireName.replace(" ", "_") + "_" + timestamp + ".xls";

            return Response.ok(baos.toByteArray())
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Erreur interne lors de la génération du fichier Excel: " + e.getMessage()).build();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        return headerStyle;
    }

    private void createSyntheseSheet(Workbook workbook, List<SyntheseEmplacementDTO> data) {
        Sheet sheet = workbook.createSheet("Synthèse par Emplacement");
        CellStyle headerStyle = createHeaderStyle(workbook);
        String[] headers = { "Emplacement", "Valeur Stock Machine", "Valeur Stock Inventaire", "Écart Valeur Achat",
                "Taux de Démarque (%)", "Ratio V/A" };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (SyntheseEmplacementDTO summary : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(summary.getEmplacement());
            row.createCell(1).setCellValue(summary.getValeurAchatMachine());
            row.createCell(2).setCellValue(summary.getValeurAchatRayon());
            row.createCell(3).setCellValue(summary.getEcartValeurAchat());
            row.createCell(4).setCellValue(summary.getTauxDemarque());
            row.createCell(5).setCellValue(summary.getRatioVA());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createAbcSheet(Workbook workbook, List<AnalyseAbcDTO> data) {
        Sheet sheet = workbook.createSheet("Analyse ABC");
        CellStyle headerStyle = createHeaderStyle(workbook);
        String[] headers = { "Produit", "Écart Valeur Achat", "% Écart Total", "% Cumulé", "Catégorie" };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (AnalyseAbcDTO item : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getNom());
            row.createCell(1).setCellValue(item.getEcartValeurAchat());
            row.createCell(2).setCellValue(item.getPourcentageEcartTotal());
            row.createCell(3).setCellValue(item.getPourcentageCumule());
            row.createCell(4).setCellValue(item.getCategorie());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDetailSheet(Workbook workbook, List<DetailProduitDTO> data) {
        Sheet sheet = workbook.createSheet("Détail Produits");
        CellStyle headerStyle = createHeaderStyle(workbook);
        String[] headers = { "Produit", "Emplacement", "Qté Machine", "Qté Rayon", "Écart Qté", "Écart V.Achat",
                "Ratio V/A" };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (DetailProduitDTO item : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getNom());
            row.createCell(1).setCellValue(item.getEmplacement());
            row.createCell(2).setCellValue(item.getQteInitiale());
            row.createCell(3).setCellValue(item.getQteSaisie());
            row.createCell(4).setCellValue(item.getEcartQuantite());
            row.createCell(5).setCellValue(item.getEcartValeurAchat());
            double ratio = (item.getPrixAchat() != 0) ? (double) item.getPrixVente() / item.getPrixAchat() : 0;
            row.createCell(6).setCellValue(ratio);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
