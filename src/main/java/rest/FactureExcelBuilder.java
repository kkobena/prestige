package rest.report.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.lang3.StringUtils;

/**
 * Classeur d'une facture tiers payant, construit A PARTIR DES DONNEES.
 *
 * L'export precedent passait par JRXlsxExporter, qui transpose le DESSIN du PDF dans des cellules : mise en page
 * reproduite a coups de cellules fusionnees, et surtout montants deja transformes en texte par le modele Jasper. Le
 * fichier ressemblait a la facture mais n'etait pas exploitable : aucun montant n'etait un nombre, donc ni modifiable,
 * ni sommable, ni triable.
 *
 * Ici, une ligne par bon, des montants en VRAIES cellules numeriques, des dates en vraies dates, l'en-tete figee, un
 * filtre automatique, et une ligne de total en FORMULE : si l'organisme corrige une ligne, le total suit.
 *
 * La classe ne connait ni JPA ni JAX-RS : elle recoit des donnees simples, ce qui la rend testable sans base.
 */
public final class FactureExcelBuilder {

    /** Une ligne du classeur = un bon de la facture. */
    public static class Ligne {
        public Date dateBon;
        public String refBon = "";
        public String nomComplet = "";
        public String matricule = "";
        public String refVente = "";
        public Integer taux;
        public long montantBrut;
        public long remise;
        public long partClient;
        public long partTiersPayant;
    }

    /** Identification de la facture, reprise en tete de feuille. */
    public static class Entete {
        public String officine = "";
        public String codeFacture = "";
        public String tiersPayant = "";
        public String periode = "";
        public String mentionAvoir = "";
    }

    private static final String[] COLONNES = { "N°", "Date", "N° Bon", "Nom et prénom(s)", "Matricule", "Réf. vente",
            "Taux (%)", "Montant brut", "Remise", "Part client", "Part tiers payant" };

    /** Largeurs en 1/256e de caractere, dans l'ordre des colonnes ci-dessus. */
    private static final int[] LARGEURS = { 6, 12, 16, 32, 18, 16, 9, 15, 13, 13, 17 };

    /** Index des colonnes portant un montant : total en formule et format monetaire. */
    private static final int[] COLONNES_MONTANT = { 7, 8, 9, 10 };

    private FactureExcelBuilder() {
    }

    public static byte[] construire(Entete entete, List<Ligne> lignes) throws IOException {
        try (Workbook classeur = new XSSFWorkbook(); ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            Sheet feuille = classeur.createSheet("Facture");

            CellStyle styleTitre = style(classeur, true, 14, null, null);
            CellStyle styleSousTitre = style(classeur, true, 11, null, null);
            CellStyle styleInfo = style(classeur, false, 10, null, null);
            CellStyle styleEntete = style(classeur, true, 10, IndexedColors.GREY_25_PERCENT,
                    HorizontalAlignment.CENTER);
            bordurer(styleEntete);
            CellStyle styleTexte = style(classeur, false, 10, null, null);
            bordurer(styleTexte);
            CellStyle styleDate = style(classeur, false, 10, null, HorizontalAlignment.CENTER);
            bordurer(styleDate);
            styleDate.setDataFormat(classeur.createDataFormat().getFormat("dd/mm/yyyy"));
            CellStyle styleEntier = style(classeur, false, 10, null, HorizontalAlignment.CENTER);
            bordurer(styleEntier);
            CellStyle styleMontant = style(classeur, false, 10, null, null);
            bordurer(styleMontant);
            styleMontant.setDataFormat(classeur.createDataFormat().getFormat("#,##0"));
            CellStyle styleTotalTexte = style(classeur, true, 10, IndexedColors.GREY_25_PERCENT, null);
            bordurer(styleTotalTexte);
            CellStyle styleTotalMontant = style(classeur, true, 10, IndexedColors.GREY_25_PERCENT, null);
            bordurer(styleTotalMontant);
            styleTotalMontant.setDataFormat(classeur.createDataFormat().getFormat("#,##0"));

            int ligneCourante = 0;
            ligneCourante = ajouterInfo(feuille, ligneCourante, entete.officine, styleTitre);
            ligneCourante = ajouterInfo(feuille, ligneCourante, "FACTURE N° " + entete.codeFacture, styleSousTitre);
            ligneCourante = ajouterInfo(feuille, ligneCourante, entete.tiersPayant, styleInfo);
            ligneCourante = ajouterInfo(feuille, ligneCourante, entete.periode, styleInfo);
            if (StringUtils.isNotBlank(entete.mentionAvoir)) {
                ligneCourante = ajouterInfo(feuille, ligneCourante, entete.mentionAvoir, styleSousTitre);
            }
            ligneCourante++; // une ligne vide avant le tableau

            int ligneEntete = ligneCourante;
            Row entetes = feuille.createRow(ligneEntete);
            for (int i = 0; i < COLONNES.length; i++) {
                Cell c = entetes.createCell(i);
                c.setCellValue(COLONNES[i]);
                c.setCellStyle(styleEntete);
                feuille.setColumnWidth(i, LARGEURS[i] * 256);
            }

            int numero = 1;
            for (Ligne l : lignes) {
                Row r = feuille.createRow(++ligneCourante);
                cellule(r, 0, styleEntier).setCellValue(numero++);
                Cell cDate = cellule(r, 1, styleDate);
                if (l.dateBon != null) {
                    cDate.setCellValue(l.dateBon);
                }
                cellule(r, 2, styleTexte).setCellValue(StringUtils.defaultString(l.refBon));
                cellule(r, 3, styleTexte).setCellValue(StringUtils.defaultString(l.nomComplet));
                cellule(r, 4, styleTexte).setCellValue(StringUtils.defaultString(l.matricule));
                cellule(r, 5, styleTexte).setCellValue(StringUtils.defaultString(l.refVente));
                Cell cTaux = cellule(r, 6, styleEntier);
                if (l.taux != null) {
                    cTaux.setCellValue(l.taux);
                }
                cellule(r, 7, styleMontant).setCellValue(l.montantBrut);
                cellule(r, 8, styleMontant).setCellValue(l.remise);
                cellule(r, 9, styleMontant).setCellValue(l.partClient);
                cellule(r, 10, styleMontant).setCellValue(l.partTiersPayant);
            }

            int premiereLigneDonnees = ligneEntete + 1;
            int derniereLigneDonnees = ligneCourante;

            // Ligne de total : des FORMULES, pas des valeurs figees. Le total se recalcule si
            // l'organisme corrige un montant ou filtre des lignes.
            Row total = feuille.createRow(++ligneCourante);
            for (int i = 0; i < COLONNES.length; i++) {
                cellule(total, i, styleTotalTexte);
            }
            total.getCell(0).setCellValue("TOTAL");
            if (!lignes.isEmpty()) {
                for (int colonne : COLONNES_MONTANT) {
                    Cell c = total.getCell(colonne);
                    c.setCellStyle(styleTotalMontant);
                    String lettre = nomColonne(colonne);
                    c.setCellFormula("SUM(" + lettre + (premiereLigneDonnees + 1) + ":" + lettre
                            + (derniereLigneDonnees + 1) + ")");
                }
            }

            // En-tete toujours visible au defilement, et filtre sur les colonnes.
            feuille.createFreezePane(0, ligneEntete + 1);
            if (!lignes.isEmpty()) {
                feuille.setAutoFilter(new CellRangeAddress(ligneEntete, derniereLigneDonnees, 0, COLONNES.length - 1));
            }

            classeur.write(sortie);
            return sortie.toByteArray();
        }
    }

    /** Lettre(s) de colonne Excel : 0 -> A, 25 -> Z, 26 -> AA. */
    static String nomColonne(int index) {
        StringBuilder nom = new StringBuilder();
        int reste = index;
        while (reste >= 0) {
            nom.insert(0, (char) ('A' + reste % 26));
            reste = reste / 26 - 1;
        }
        return nom.toString();
    }

    private static int ajouterInfo(Sheet feuille, int index, String texte, CellStyle style) {
        Row r = feuille.createRow(index);
        Cell c = r.createCell(0);
        c.setCellValue(StringUtils.defaultString(texte));
        c.setCellStyle(style);
        return index + 1;
    }

    private static Cell cellule(Row ligne, int colonne, CellStyle style) {
        Cell c = ligne.createCell(colonne);
        c.setCellStyle(style);
        return c;
    }

    private static CellStyle style(Workbook classeur, boolean gras, int taille, IndexedColors fond,
            HorizontalAlignment alignement) {
        CellStyle style = classeur.createCellStyle();
        Font police = classeur.createFont();
        police.setBold(gras);
        police.setFontHeightInPoints((short) taille);
        style.setFont(police);
        if (fond != null) {
            style.setFillForegroundColor(fond.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (alignement != null) {
            style.setAlignment(alignement);
        }
        return style;
    }

    private static void bordurer(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
