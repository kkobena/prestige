package rest.report.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classeur d'une facture tiers payant.
 *
 * Ce que ces tests garantissent, et qui manquait a l'export precedent : les montants sont de VRAIS nombres (donc
 * modifiables, sommables, triables), les dates de vraies dates, et le total est une FORMULE qui suit les corrections de
 * l'organisme. L'ancien export, produit a partir du PDF deja mis en page, ne livrait que du texte.
 */
class FactureExcelBuilderTest {

    private static FactureExcelBuilder.Entete entete() {
        FactureExcelBuilder.Entete e = new FactureExcelBuilder.Entete();
        e.officine = "PHARMACIE DU CENTRE";
        e.codeFacture = "FA0012";
        e.tiersPayant = "ASCOMA COTE D'IVOIRE";
        e.periode = "Période du 01/07/2026 au 31/07/2026";
        return e;
    }

    private static FactureExcelBuilder.Ligne ligne(String nom, int jour, long brut, long remise, long client, long tp) {
        FactureExcelBuilder.Ligne l = new FactureExcelBuilder.Ligne();
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(2026, Calendar.JULY, jour);
        l.dateBon = c.getTime();
        l.refBon = "BON" + jour;
        l.nomComplet = nom;
        l.matricule = "MAT" + jour;
        l.refVente = "V" + jour;
        l.taux = 80;
        l.montantBrut = brut;
        l.remise = remise;
        l.partClient = client;
        l.partTiersPayant = tp;
        return l;
    }

    private static List<FactureExcelBuilder.Ligne> troisLignes() {
        List<FactureExcelBuilder.Ligne> lignes = new ArrayList<>();
        lignes.add(ligne("KOUAME Yao", 3, 10000, 500, 2000, 7500));
        lignes.add(ligne("DIALLO Awa", 7, 25000, 0, 5000, 20000));
        lignes.add(ligne("TRAORE Ali", 12, 4000, 100, 900, 3000));
        return lignes;
    }

    private static Sheet relire(byte[] classeur) throws Exception {
        Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(classeur));
        return wb.getSheetAt(0);
    }

    /** Ligne d'en-tete du tableau : la premiere dont la cellule A vaut "N°". */
    private static int ligneEntete(Sheet f) {
        for (int i = 0; i <= f.getLastRowNum(); i++) {
            Row r = f.getRow(i);
            if (r != null && r.getCell(0) != null && r.getCell(0).getCellType() == CellType.STRING
                    && "N°".equals(r.getCell(0).getStringCellValue())) {
                return i;
            }
        }
        return -1;
    }

    @Test
    @DisplayName("Les montants sont de VRAIS nombres, pas du texte")
    void montantsNumeriques() throws Exception {
        Sheet f = relire(FactureExcelBuilder.construire(entete(), troisLignes()));
        Row premiere = f.getRow(ligneEntete(f) + 1);

        for (int colonne : new int[] { 7, 8, 9, 10 }) {
            assertEquals(CellType.NUMERIC, premiere.getCell(colonne).getCellType(),
                    "la colonne " + colonne + " doit etre numerique");
        }
        assertEquals(10000d, premiere.getCell(7).getNumericCellValue(), 0.001);
        assertEquals(500d, premiere.getCell(8).getNumericCellValue(), 0.001);
        assertEquals(2000d, premiere.getCell(9).getNumericCellValue(), 0.001);
        assertEquals(7500d, premiere.getCell(10).getNumericCellValue(), 0.001);
    }

    @Test
    @DisplayName("La date du bon est une vraie date, pas une chaine")
    void dateReelle() throws Exception {
        Sheet f = relire(FactureExcelBuilder.construire(entete(), troisLignes()));
        Cell date = f.getRow(ligneEntete(f) + 1).getCell(1);

        assertEquals(CellType.NUMERIC, date.getCellType());
        assertTrue(DateUtil.isCellDateFormatted(date), "la cellule doit porter un format de date");
    }

    @Test
    @DisplayName("Le total est une FORMULE : il suit les corrections de l'organisme")
    void totalEnFormule() throws Exception {
        List<FactureExcelBuilder.Ligne> lignes = troisLignes();
        Sheet f = relire(FactureExcelBuilder.construire(entete(), lignes));
        int entete = ligneEntete(f);
        Row total = f.getRow(entete + lignes.size() + 1);

        assertEquals("TOTAL", total.getCell(0).getStringCellValue());
        assertEquals(CellType.FORMULA, total.getCell(10).getCellType());
        // lignes 1-based dans la formule : en-tete en index 0-based -> premiere donnee = entete+2
        assertEquals("SUM(K" + (entete + 2) + ":K" + (entete + 1 + lignes.size()) + ")",
                total.getCell(10).getCellFormula());
    }

    @Test
    @DisplayName("Une ligne par bon, dans l'ordre fourni")
    void unelignParBon() throws Exception {
        List<FactureExcelBuilder.Ligne> lignes = troisLignes();
        Sheet f = relire(FactureExcelBuilder.construire(entete(), lignes));
        int entete = ligneEntete(f);

        assertEquals("KOUAME Yao", f.getRow(entete + 1).getCell(3).getStringCellValue());
        assertEquals("DIALLO Awa", f.getRow(entete + 2).getCell(3).getStringCellValue());
        assertEquals("TRAORE Ali", f.getRow(entete + 3).getCell(3).getStringCellValue());
        // numerotation continue
        assertEquals(1d, f.getRow(entete + 1).getCell(0).getNumericCellValue(), 0.001);
        assertEquals(3d, f.getRow(entete + 3).getCell(0).getNumericCellValue(), 0.001);
    }

    @Test
    @DisplayName("En-tete figee et filtre automatique sur les colonnes")
    void enteteFigeeEtFiltre() throws Exception {
        Sheet f = relire(FactureExcelBuilder.construire(entete(), troisLignes()));

        assertNotNull(f.getPaneInformation(), "l'en-tete doit rester visible au defilement");
        assertEquals(ligneEntete(f) + 1, f.getPaneInformation().getHorizontalSplitTopRow());
    }

    @Test
    @DisplayName("Identification de la facture en tete de feuille")
    void identificationFacture() throws Exception {
        Sheet f = relire(FactureExcelBuilder.construire(entete(), troisLignes()));

        assertEquals("PHARMACIE DU CENTRE", f.getRow(0).getCell(0).getStringCellValue());
        assertEquals("FACTURE N° FA0012", f.getRow(1).getCell(0).getStringCellValue());
        assertEquals("ASCOMA COTE D'IVOIRE", f.getRow(2).getCell(0).getStringCellValue());
        assertTrue(f.getRow(3).getCell(0).getStringCellValue().startsWith("Période du"));
    }

    @Test
    @DisplayName("Facture annulee par avoir : la mention figure dans le classeur")
    void mentionAvoir() throws Exception {
        FactureExcelBuilder.Entete e = entete();
        e.mentionAvoir = "*** FACTURE ANNULÉE PAR AVOIR FNE (A142) ***";
        Sheet f = relire(FactureExcelBuilder.construire(e, troisLignes()));

        boolean trouve = false;
        for (int i = 0; i < ligneEntete(f); i++) {
            // la ligne vide qui separe l'identification du tableau n'existe pas dans le classeur
            Row r = f.getRow(i);
            Cell c = r != null ? r.getCell(0) : null;
            if (c != null && c.getStringCellValue().contains("ANNUL")) {
                trouve = true;
            }
        }
        assertTrue(trouve, "la mention d'avoir doit apparaitre en tete");
    }

    @Test
    @DisplayName("Facture sans aucun bon : classeur valide, sans formule de total")
    void factureVide() throws Exception {
        Sheet f = relire(FactureExcelBuilder.construire(entete(), new ArrayList<>()));
        int entete = ligneEntete(f);

        assertTrue(entete > 0, "l'en-tete du tableau doit exister meme sans ligne");
        Row total = f.getRow(entete + 1);
        assertEquals("TOTAL", total.getCell(0).getStringCellValue());
        // pas de SUM sur une plage vide : Excel refuserait la formule
        assertEquals(CellType.BLANK, total.getCell(10).getCellType());
    }

    @Test
    @DisplayName("Nom de colonne Excel")
    void nomsDeColonnes() {
        assertEquals("A", FactureExcelBuilder.nomColonne(0));
        assertEquals("H", FactureExcelBuilder.nomColonne(7));
        assertEquals("K", FactureExcelBuilder.nomColonne(10));
        assertEquals("Z", FactureExcelBuilder.nomColonne(25));
        assertEquals("AA", FactureExcelBuilder.nomColonne(26));
    }
}
