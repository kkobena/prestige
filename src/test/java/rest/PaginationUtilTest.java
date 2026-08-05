package rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pagination des listes chargees en memoire : c'est le defaut qui faisait afficher les MEMES lignes en page 1 et en
 * page 2 sur l'ecran Generer facture (l'ecran demandait 15 lignes, le serveur decoupait par 20).
 */
class PaginationUtilTest {

    @Test
    @DisplayName("Page 1 : les 15 premieres lignes sur 19")
    void page1() {
        assertEquals(0, PaginationUtil.debut(0, 19));
        assertEquals(15, PaginationUtil.fin(0, 15, 19));
    }

    @Test
    @DisplayName("Page 2 : les 4 lignes restantes, differentes de la page 1")
    void page2() {
        assertEquals(15, PaginationUtil.debut(15, 19));
        assertEquals(19, PaginationUtil.fin(15, 15, 19));
    }

    @Test
    @DisplayName("Derniere page incomplete : on ne depasse jamais la taille de la liste")
    void dernierePageIncomplete() {
        assertEquals(19, PaginationUtil.fin(10, 15, 19));
    }

    @Test
    @DisplayName("Page demandee au-dela des donnees : tranche vide, pas d'erreur")
    void pageAuDelaDesDonnees() {
        assertEquals(19, PaginationUtil.debut(40, 19));
        assertEquals(19, PaginationUtil.fin(40, 15, 19));
    }

    @Test
    @DisplayName("Sans limite : toute la liste est renvoyee")
    void sansLimite() {
        assertEquals(0, PaginationUtil.debut(0, 19));
        assertEquals(19, PaginationUtil.fin(0, 0, 19));
    }

    @Test
    @DisplayName("Liste vide : bornes a zero")
    void listeVide() {
        assertEquals(0, PaginationUtil.debut(0, 0));
        assertEquals(0, PaginationUtil.fin(0, 20, 0));
        assertEquals(0, PaginationUtil.fin(15, 15, 0));
    }

    @Test
    @DisplayName("Toutes les pages couvrent la liste exactement une fois")
    void couvertureComplete() {
        int taille = 19, limite = 15, couvert = 0, precedentDebut = -1;
        for (int start = 0; start < taille; start += limite) {
            int d = PaginationUtil.debut(start, taille);
            int f = PaginationUtil.fin(start, limite, taille);
            assertEquals(start, d, "chaque page commence la ou la precedente s'arrete");
            org.junit.jupiter.api.Assertions.assertTrue(d > precedentDebut, "pas deux fois la meme page");
            precedentDebut = d;
            couvert += (f - d);
        }
        assertEquals(taille, couvert, "aucune ligne perdue ni comptee deux fois");
    }
}
