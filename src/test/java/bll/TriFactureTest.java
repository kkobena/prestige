package bll;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bll.configManagement.GroupeTierspayantController;
import bll.tierspayantManagement.tierspayantManagement;
import dal.TFacture;
import dal.TTiersPayant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Regles de tri des factures : mode de tri de la fiche tiers payant et tri du recapitulatif de groupe. */
class TriFactureTest {

    private static TFacture facture(String codeFacture, String nomComplet) {
        TTiersPayant tp = new TTiersPayant();
        tp.setStrFULLNAME(nomComplet);
        TFacture f = new TFacture();
        f.setStrCODEFACTURE(codeFacture);
        f.setTiersPayant(tp);
        return f;
    }

    private static List<String> nomsTries(List<TFacture> factures, String modeTri) {
        List<TFacture> copie = new ArrayList<>(factures);
        copie.sort(GroupeTierspayantController.comparatorTriFacture(modeTri));
        return copie.stream().map(f -> f.getTiersPayant().getStrFULLNAME()).collect(Collectors.toList());
    }

    // ---------------------------------------------------- fiche tiers payant

    @Test
    @DisplayName("Fiche tiers payant : alphabetique par defaut")
    void modeTriParDefaut() {
        assertEquals(tierspayantManagement.MODE_TRI_FACTURE_ALPHABETIQUE,
                tierspayantManagement.normalizeModeTriFacture(null));
        assertEquals(tierspayantManagement.MODE_TRI_FACTURE_ALPHABETIQUE,
                tierspayantManagement.normalizeModeTriFacture(""));
        assertEquals(tierspayantManagement.MODE_TRI_FACTURE_ALPHABETIQUE,
                tierspayantManagement.normalizeModeTriFacture("valeur inconnue"));
    }

    @Test
    @DisplayName("Fiche tiers payant : date de bon reconnue quels que soient casse et espaces")
    void modeTriDateBon() {
        assertEquals(tierspayantManagement.MODE_TRI_FACTURE_DATE_BON,
                tierspayantManagement.normalizeModeTriFacture("DATE_BON"));
        assertEquals(tierspayantManagement.MODE_TRI_FACTURE_DATE_BON,
                tierspayantManagement.normalizeModeTriFacture(" date_bon "));
    }

    // ---------------------------------------------------- recapitulatif de groupe

    @Test
    @DisplayName("Groupe : alphabetique par defaut, insensible a la casse et aux accents")
    void triAlphabetique() {
        List<TFacture> factures = Arrays.asList(facture("3", "zurich"), facture("1", "Étoile"), facture("2", "alpha"));
        assertEquals(Arrays.asList("alpha", "Étoile", "zurich"), nomsTries(factures, null));
    }

    @Test
    @DisplayName("Groupe : tri numerique sur le numero de facture, pas sur son texte")
    void triNumerique() {
        // en tri texte, "10" passerait avant "9" : le tri numerique doit respecter les nombres
        List<TFacture> factures = Arrays.asList(facture("2026_10", "C"), facture("2026_9", "B"),
                facture("2026_1", "A"));
        assertEquals(Arrays.asList("A", "B", "C"), nomsTries(factures, GroupeTierspayantController.MODE_TRI_NUMERIQUE));
    }

    @Test
    @DisplayName("Groupe : mode de tri inconnu ou absent -> alphabetique")
    void normalisationModeTriGroupe() {
        assertEquals(GroupeTierspayantController.MODE_TRI_ALPHABETIQUE,
                GroupeTierspayantController.normalizeModeTri(null));
        assertEquals(GroupeTierspayantController.MODE_TRI_ALPHABETIQUE,
                GroupeTierspayantController.normalizeModeTri("autre chose"));
        assertEquals(GroupeTierspayantController.MODE_TRI_NUMERIQUE,
                GroupeTierspayantController.normalizeModeTri(" numerique "));
    }

    @Test
    @DisplayName("Groupe : un tiers payant sans nom ne fait pas echouer le tri")
    void triSansNom() {
        TFacture sansTiersPayant = new TFacture();
        sansTiersPayant.setStrCODEFACTURE("5");
        List<TFacture> factures = Arrays.asList(facture("1", "Beta"), sansTiersPayant, facture("2", "Alpha"));
        List<TFacture> copie = new ArrayList<>(factures);
        copie.sort(GroupeTierspayantController.comparatorTriFacture(null));
        assertEquals(3, copie.size());
    }
}
