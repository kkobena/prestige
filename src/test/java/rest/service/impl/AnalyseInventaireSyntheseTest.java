package rest.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import rest.service.dto.AnalyseInvDTO;
import rest.service.dto.BalanceEditionLigneDTO;

/**
 * La synthese d'un inventaire (retours du 13/09) : taux d'ecart, valorisation avant / apres, contribution de chaque
 * emplacement, articles critiques et recommandations, du plus gros ecart au plus petit.
 */
public class AnalyseInventaireSyntheseTest {

    private static AnalyseInvDTO ligne(String emplacement, int qteInit, int qteSaisie, double pa, double pv,
            String nom) {
        return AnalyseInvDTO.builder().codeCip("CIP" + nom).nom(nom).emplacement(emplacement).qteInitiale(qteInit)
                .qteSaisie(qteSaisie).prixAchat(pa).prixVente(pv).invName("INV TEST").build();
    }

    /** Quatre lignes, deux en ecart : une demarque dans DETAIL CH, un surplus dans DETAIL. */
    private static AnalyseInventaireSynthese jeu() {
        return AnalyseInventaireSynthese.calculer(Arrays.asList(ligne("DETAIL CH", 10, 8, 1000, 1500, "PERIKABIVEN"),
                ligne("DETAIL CH", 5, 5, 2000, 3000, "SERUM"), ligne("DETAIL", 4, 6, 500, 750, "TRIBIODERM"),
                ligne(null, 1, 1, 100, 120, "LAIT")));
    }

    @Test
    public void chiffresGlobaux() {
        AnalyseInventaireSynthese s = jeu();
        assertEquals(4, s.getTotalArticles());
        assertEquals(2, s.getArticlesEcart());
        assertEquals(2, s.getArticlesConformes());
        assertEquals(50.0, s.getTauxEcart(), 0.001);
        assertEquals(50.0, s.getTauxConforme(), 0.001);
        assertEquals(22100.0, s.getValeurAchatMachine(), 0.001);
        assertEquals(21100.0, s.getValeurAchatInventaire(), 0.001);
        assertEquals(-1000.0, s.getEcartAchat(), 0.001);
        assertEquals(33120.0, s.getValeurVenteMachine(), 0.001);
        assertEquals(31620.0, s.getValeurVenteInventaire(), 0.001);
        assertEquals(-1500.0, s.getEcartVente(), 0.001);
        assertEquals(-1000.0 / 22100 * 100, s.getTauxEvolutionAchat(), 0.001);
        assertEquals(21100.0 / 4, s.getValeurMoyenneAchat(), 0.001);
        assertEquals(31620.0 / 4, s.getValeurMoyenneVente(), 0.001);
        assertEquals("INV TEST", s.getNomInventaire());
    }

    @Test
    public void emplacementsDuPlusGrosEcartAuPlusPetit() {
        AnalyseInventaireSynthese s = jeu();
        List<AnalyseInventaireSynthese.Emplacement> e = s.getEmplacements();
        assertEquals(3, e.size());
        assertEquals("DETAIL CH", e.get(0).getNom());
        assertEquals("DETAIL", e.get(1).getNom());
        assertEquals("Non défini", e.get(2).getNom());
        // contribution = part dans la somme des ecarts absolus (2000 + 1000)
        assertEquals(-2000.0, e.get(0).getEcartAchat(), 0.001);
        assertEquals(-3000.0, e.get(0).getEcartVente(), 0.001);
        assertEquals(2000.0 / 3000 * 100, e.get(0).getContributionPct(), 0.001);
        assertEquals(1000.0 / 3000 * 100, e.get(1).getContributionPct(), 0.001);
        assertEquals(0.0, e.get(2).getContributionPct(), 0.001);
        assertEquals(1.5, e.get(0).getRatioVA(), 0.001);
        assertEquals(1, e.get(0).getArticlesEcart());
        assertEquals(2, e.get(0).getArticles());

        assertEquals("DETAIL CH", s.getEmplacementCritique().getNom());
        assertEquals("Non défini", s.getEmplacementFaibleMarge().getNom());
        assertEquals(1, s.emplacementsNegatifs(7).size());
        assertEquals("DETAIL CH", s.emplacementsNegatifs(7).get(0).getNom());
        assertEquals(1, s.emplacementsPositifs(5).size());
        assertEquals("DETAIL", s.emplacementsPositifs(5).get(0).getNom());
    }

    @Test
    public void articlesCritiquesTriesParEcart() {
        AnalyseInventaireSynthese s = jeu();
        List<AnalyseInventaireSynthese.Article> a = s.getArticlesEnEcart();
        assertEquals(2, a.size());
        assertEquals("PERIKABIVEN", a.get(0).getNom());
        assertEquals(-2000.0, a.get(0).getEcartValeurAchat(), 0.001);
        assertEquals(-2, a.get(0).getEcartQte());
        assertEquals("DETAIL CH", a.get(0).getEmplacement());
        assertEquals("TRIBIODERM", a.get(1).getNom());
        assertEquals(1000.0, a.get(1).getEcartValeurAchat(), 0.001);
        assertEquals(2, s.articlesCritiques(10).size());
        assertEquals(1, s.articlesCritiques(1).size());
    }

    @Test
    public void miseEnFormeEtTextes() {
        AnalyseInventaireSynthese s = jeu();
        assertEquals("-1 000 CFA", AnalyseInventaireSynthese.montant(-1000));
        assertEquals("108 323 109 CFA", AnalyseInventaireSynthese.montant(108323109));
        assertEquals("32,97 %", AnalyseInventaireSynthese.pourcentage(32.9715));
        assertEquals("1,52", AnalyseInventaireSynthese.ratio(1.5234));
        assertEquals("N/A", AnalyseInventaireSynthese.ratio(0));
        assertEquals("Près de 1 article sur 2", s.articleSur());

        String texte = s.syntheseGlobaleTexte();
        assertTrue(texte.contains("-1 000 CFA"), texte);
        assertTrue(texte.contains("une démarque de"), texte);
        assertTrue(texte.contains("50,00 %"), texte);

        List<String> recos = s.recommandations();
        assertTrue(recos.size() >= 5, String.valueOf(recos.size()));
        assertTrue(recos.get(0).contains("DETAIL CH"), recos.get(0));
        assertTrue(recos.get(1).contains("PERIKABIVEN"), recos.get(1));
        assertTrue(recos.stream().anyMatch(x -> x.contains("écarts positifs") && x.contains("DETAIL")));
        assertTrue(recos.stream().anyMatch(x -> x.contains("ratio V/A de 1,20")));
    }

    @Test
    public void ongletHtmlParlant() {
        String html = jeu().html();
        assertTrue(html.contains("1. Récapitulatif global"));
        assertTrue(html.contains("2. Points de vigilance par emplacement"));
        assertTrue(html.contains("3. Articles les plus critiques"));
        assertTrue(html.contains("4. Recommandations"));
        assertTrue(html.contains("Valeur machine / théorique"));
        assertTrue(html.contains("Valeur moyenne par article"));
        assertTrue(html.contains("Emplacement critique :"));
        assertTrue(html.contains("Marge à surveiller :"));
        assertTrue(html.contains("DETAIL CH"));
        assertTrue(html.contains("CIPPERIKABIVEN - PERIKABIVEN"));
        assertTrue(html.contains("-2 000 CFA"));
        assertFalse(html.contains("<script"), "pas de contenu executable");
    }

    @Test
    public void lignesDesEditions() {
        AnalyseInventaireSynthese s = jeu();
        List<BalanceEditionLigneDTO> simple = s.lignesEditionSimple(5, 10);
        assertTrue(simple.stream()
                .anyMatch(l -> "Nombre total d'articles".equals(l.getLibelle()) && "4".equals(l.getC1())));
        assertTrue(simple.stream().anyMatch(l -> "Articles en écart".equals(l.getLibelle()) && "2".equals(l.getC1())));
        assertTrue(simple.stream().anyMatch(l -> "Valorisation avant inventaire".equals(l.getLibelle())
                && "22 100 CFA".equals(l.getC1()) && "33 120 CFA".equals(l.getC2())));
        assertTrue(simple.stream().anyMatch(
                l -> "Valorisation après inventaire".equals(l.getLibelle()) && "21 100 CFA".equals(l.getC1())));
        assertTrue(simple.stream()
                .anyMatch(l -> "Écart global".equals(l.getLibelle()) && "-1 000 CFA".equals(l.getC1()) && l.isTotal()));
        assertTrue(simple.stream().anyMatch(l -> "TOTAL GÉNÉRAL".equals(l.getLibelle())));
        // sections dans l'ordre de lecture
        List<String> sections = new ArrayList<>();
        simple.forEach(l -> {
            if (!sections.contains(l.getCleSection())) {
                sections.add(l.getCleSection());
            }
        });
        assertEquals(Arrays.asList("01|" + AnalyseInventaireSynthese.S_GLOBAL,
                "02|" + AnalyseInventaireSynthese.S_VALORISATION, "03|" + AnalyseInventaireSynthese.S_VIGILANCE_EMPL,
                "04|" + AnalyseInventaireSynthese.S_ARTICLES, "05|" + AnalyseInventaireSynthese.S_EMPLACEMENTS),
                sections);

        List<BalanceEditionLigneDTO> synthese = s.lignesEditionSynthese();
        assertTrue(synthese.stream().anyMatch(l -> "Écart de valeur".equals(l.getLibelle())));
        assertTrue(synthese.stream().anyMatch(l -> "Valeur moyenne par article".equals(l.getLibelle())));
        assertTrue(synthese.stream().anyMatch(l -> l.getSection().equals(AnalyseInventaireSynthese.S_ECARTS_POSITIFS)
                && "DETAIL".equals(l.getLibelle())));
        assertTrue(synthese.stream().anyMatch(
                l -> l.getSection().equals(AnalyseInventaireSynthese.S_RECO) && l.getLibelle().startsWith("1. ")));
        assertNotNull(AnalyseInventaireSynthese.entetes().get(AnalyseInventaireSynthese.S_GLOBAL));
        assertEquals("Achat", AnalyseInventaireSynthese.entetes().get(AnalyseInventaireSynthese.S_GLOBAL)[0]);
    }

    @Test
    public void inventaireVideNeCasseRien() {
        AnalyseInventaireSynthese s = AnalyseInventaireSynthese.calculer(new ArrayList<>());
        assertEquals(0, s.getTotalArticles());
        assertEquals(0.0, s.getTauxEcart(), 0.001);
        assertEquals(0.0, s.getEcartAchat(), 0.001);
        assertEquals("Aucun écart constaté", s.articleSur());
        assertTrue(s.getEmplacements().isEmpty());
        assertTrue(s.articlesCritiques(5).isEmpty());
        assertTrue(s.html().contains("Aucun écart négatif"));
        assertTrue(s.lignesEditionSimple(5, 10).stream()
                .anyMatch(l -> "Aucun emplacement en écart".equals(l.getLibelle())));
        assertTrue(s.lignesEditionSynthese().stream().anyMatch(l -> "Aucun écart positif".equals(l.getLibelle())));
        assertEquals(null, s.getEmplacementCritique());
        assertEquals(null, s.getEmplacementFaibleMarge());
    }
}
