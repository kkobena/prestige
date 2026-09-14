package rest.report;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import rest.service.dto.AnalyseInvDTO;
import rest.service.impl.AnalyseInventaireSynthese;

/**
 * Les deux editions de l'analyse d'inventaire (retours du 13/09) se compilent et se remplissent : en-tete de
 * l'officine, sections dans l'ordre, valorisation avant / apres, emplacements critiques, articles et recommandations.
 */
public class AnalyseInventaireJrxmlTest {

    private static JasperReport modele(String nom) throws Exception {
        try (InputStream in = AnalyseInventaireJrxmlTest.class.getResourceAsStream("/reports/" + nom + ".jrxml")) {
            return JasperCompileManager.compileReport(in);
        }
    }

    private static void textes(List<JRPrintElement> elements, List<String> sortie) {
        for (JRPrintElement e : elements) {
            if (e instanceof JRPrintText) {
                sortie.add(((JRPrintText) e).getFullText());
            } else if (e instanceof JRPrintFrame) {
                textes(((JRPrintFrame) e).getElements(), sortie);
            }
        }
    }

    private static AnalyseInventaireSynthese jeu() {
        return AnalyseInventaireSynthese.calculer(Arrays.asList(
                AnalyseInvDTO.builder().codeCip("0001").nom("PERIKABIVEN").emplacement("DETAIL CH").qteInitiale(10)
                        .qteSaisie(8).prixAchat(1000D).prixVente(1500D).invName("INVENTAIRE GENERAL").build(),
                AnalyseInvDTO.builder().codeCip("0002").nom("TRIBIODERM").emplacement("DETAIL").qteInitiale(4)
                        .qteSaisie(6).prixAchat(500D).prixVente(750D).invName("INVENTAIRE GENERAL").build()));
    }

    private static List<String> remplir(String modele, List<?> lignes, String titre) throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("P_H_INSTITUTION", "PHARMACIE TEST");
        p.put("P_AUTRE_DESC", "Dr KONAN");
        p.put("P_INSTITUTION_ADRESSE", "Abidjan");
        p.put("P_PRINTED_BY", "KGA3");
        p.put("P_TITRE", titre);
        p.put("P_INVENTAIRE", "INVENTAIRE GENERAL");
        p.put("P_RESUME", "2 articles, 2 en écart");
        p.put("P_ENTETES", AnalyseInventaireSynthese.entetes());
        JasperPrint print = JasperFillManager.fillReport(modele(modele), p, new JRBeanCollectionDataSource(lignes));
        List<String> t = new ArrayList<>();
        print.getPages().forEach(page -> textes(page.getElements(), t));
        List<String> sans = new ArrayList<>();
        for (String x : t) {
            sans.add(x == null ? "" : x.replace(" ", " "));
        }
        return sans;
    }

    @Test
    public void editionSimple() throws Exception {
        List<String> t = remplir("analyse_inventaire", jeu().lignesEditionSimple(5, 10), "ANALYSE DE L'INVENTAIRE");
        assertTrue(t.contains("PHARMACIE TEST"), t.toString());
        assertTrue(t.contains("Dr KONAN"));
        assertTrue(t.contains("ANALYSE DE L'INVENTAIRE"));
        assertTrue(t.contains(AnalyseInventaireSynthese.S_GLOBAL), t.toString());
        assertTrue(t.contains(AnalyseInventaireSynthese.S_VALORISATION));
        assertTrue(t.contains(AnalyseInventaireSynthese.S_VIGILANCE_EMPL));
        assertTrue(t.contains(AnalyseInventaireSynthese.S_ARTICLES));
        assertTrue(t.contains(AnalyseInventaireSynthese.S_EMPLACEMENTS));
        assertTrue(t.contains("Nombre total d'articles"));
        assertTrue(t.contains("Valorisation avant inventaire"));
        assertTrue(t.contains("Valorisation après inventaire"));
        assertTrue(t.contains("Écart global"));
        assertTrue(t.contains("12 000 CFA"), t.toString());
        assertTrue(t.contains("0001 - PERIKABIVEN"), t.toString());
        assertTrue(t.contains("TOTAL GÉNÉRAL"));
        assertTrue(t.stream().anyMatch(x -> x.startsWith("Imprimé le") && x.contains("par KGA3")), t.toString());
    }

    @Test
    public void editionSynthese() throws Exception {
        List<String> t = remplir("analyse_inventaire_synthese", jeu().lignesEditionSynthese(),
                "SYNTHÈSE ET RECOMMANDATIONS");
        assertTrue(t.contains("SYNTHÈSE ET RECOMMANDATIONS"), t.toString());
        assertTrue(t.contains(AnalyseInventaireSynthese.S_GLOBAL));
        assertTrue(t.contains(AnalyseInventaireSynthese.S_VIGILANCE_EMPL));
        assertTrue(t.contains(AnalyseInventaireSynthese.S_ECARTS_POSITIFS));
        assertTrue(t.contains(AnalyseInventaireSynthese.S_ARTICLES));
        assertTrue(t.contains(AnalyseInventaireSynthese.S_RECO));
        assertTrue(t.contains("Valeur machine / théorique"));
        assertTrue(t.contains("Valeur inventaire / finale"));
        assertTrue(t.contains("Taux d'évolution du stock"));
        assertTrue(t.contains("Valeur moyenne par article"));
        assertTrue(t.stream().anyMatch(x -> x.startsWith("1. Audit ciblé sur DETAIL CH")), t.toString());
        assertTrue(t.stream().anyMatch(x -> x.contains("Commentaire")), "en-tetes de colonnes presents");
    }

    @Test
    public void inventaireVide() throws Exception {
        List<String> t = remplir("analyse_inventaire",
                AnalyseInventaireSynthese.calculer(new ArrayList<>()).lignesEditionSimple(5, 10),
                "ANALYSE DE L'INVENTAIRE");
        // l'etat sort quand meme : les sections de resume existent toujours
        assertTrue(t.contains("Nombre total d'articles"), t.toString());
        assertTrue(t.contains("Aucun emplacement en écart"), t.toString());
    }
}
