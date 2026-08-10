package rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Decoupage des factures d'un groupe sur l'ecran "Regler la facture".
 *
 * Le service renvoyait TOUTES les factures a chaque appel en ignorant start / limit : la barre annoncait plusieurs
 * pages qui affichaient toutes le meme contenu. Cas reel constate : un groupe de 49 factures, 4 pages annoncees, page 2
 * identique a la page 1.
 */
class PaginationInvoicesReglementTest {

    /** Tranche renvoyee par le service pour une page donnee, avec la meme logique que l'endpoint. */
    private static List<String> tranche(List<String> toutes, int start, int limit) {
        return toutes.subList(PaginationUtil.debut(start, toutes.size()),
                PaginationUtil.fin(start, limit, toutes.size()));
    }

    private static List<String> factures(int nombre) {
        List<String> l = new ArrayList<>();
        for (int i = 1; i <= nombre; i++) {
            l.add("FAC" + i);
        }
        return l;
    }

    @Test
    @DisplayName("Groupe de 49 factures, page de 200 : tout tient sur une seule page")
    void toutSurUnePage() {
        List<String> toutes = factures(49);
        assertEquals(49, tranche(toutes, 0, 200).size());
        // la page suivante n'existe pas : elle ne doit rien renvoyer, surtout pas la page 1
        assertTrue(tranche(toutes, 200, 200).isEmpty());
    }

    @Test
    @DisplayName("Groupe de 49 factures decoupe par 15 : 4 pages, toutes differentes")
    void pagesToutesDifferentes() {
        List<String> toutes = factures(49);
        Set<String> vues = new HashSet<>();
        int[] taillesAttendues = { 15, 15, 15, 4 };
        for (int page = 0; page < 4; page++) {
            List<String> tranche = tranche(toutes, page * 15, 15);
            assertEquals(taillesAttendues[page], tranche.size(), "taille de la page " + (page + 1));
            for (String f : tranche) {
                assertTrue(vues.add(f), "la facture " + f + " apparait sur deux pages differentes");
            }
        }
        assertEquals(49, vues.size(), "aucune facture perdue ni comptee deux fois");
    }

    @Test
    @DisplayName("Aucune limite demandee : tout est renvoye")
    void sansLimite() {
        assertEquals(49, tranche(factures(49), 0, 0).size());
    }
}
