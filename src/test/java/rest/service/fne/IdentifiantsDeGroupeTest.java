package rest.service.fne;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Lecture des identifiants d'une facture de groupe ("12_13_14").
 *
 * Regression couverte ici : le parametre arrivait vide au serveur (un POST ExtJS transmet ses parametres dans le corps
 * de la requete, la ressource ne lisait que l'URL). La chaine nulle levait alors un NullPointerException, l'ecran
 * affichait un motif de refus faux, et rien n'etait journalise.
 */
class IdentifiantsDeGroupeTest {

    private static Set<Integer> attendu(Integer... valeurs) {
        return new LinkedHashSet<>(Arrays.asList(valeurs));
    }

    @Test
    @DisplayName("Liste normale")
    void listeNormale() {
        assertEquals(attendu(12, 13, 14), FneServiceImpl.identifiantsDeGroupe("12_13_14"));
    }

    @Test
    @DisplayName("Un seul identifiant")
    void identifiantUnique() {
        assertEquals(attendu(42), FneServiceImpl.identifiantsDeGroupe("42"));
    }

    @Test
    @DisplayName("Chaine nulle : aucune exception, ensemble vide")
    void chaineNulle() {
        assertTrue(FneServiceImpl.identifiantsDeGroupe(null).isEmpty());
    }

    @Test
    @DisplayName("Chaine vide : aucune exception, ensemble vide")
    void chaineVide() {
        assertTrue(FneServiceImpl.identifiantsDeGroupe("").isEmpty());
    }

    @Test
    @DisplayName("Separateurs en trop : ils sont ignores")
    void separateursEnTrop() {
        assertEquals(attendu(12, 13), FneServiceImpl.identifiantsDeGroupe("_12__13_"));
    }

    @Test
    @DisplayName("Identifiant non numerique : ignore, les autres sont conserves")
    void identifiantNonNumerique() {
        assertEquals(attendu(12, 14), FneServiceImpl.identifiantsDeGroupe("12_abc_14"));
    }

    @Test
    @DisplayName("Doublons : dedoublonnes")
    void doublons() {
        assertEquals(attendu(12, 13), FneServiceImpl.identifiantsDeGroupe("12_13_12"));
    }
}
