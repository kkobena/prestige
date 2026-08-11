package rest.service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Colonne "Date et heure" de la liste des bons par organisme.
 *
 * L'ecran et l'etat PDF n'affichent plus qu'une seule colonne pour l'instant du bon. Ces tests fixent le comportement
 * quand l'une des deux valeurs manque : l'etat ne doit jamais afficher "null", ni un espace isole a la place de
 * l'heure.
 */
class BonsDTODateHeureTest {

    private static BonsDTO bon(String date, String heure) {
        return BonsDTO.builder().dtUPDATED(date).heure(heure).build();
    }

    @Test
    @DisplayName("Date et heure reunies par un espace")
    void dateEtHeure() {
        assertEquals("05/08/2026 10:20", bon("05/08/2026", "10:20").getDateHeure());
    }

    @Test
    @DisplayName("Heure absente : la date seule, sans espace en trop")
    void heureAbsente() {
        assertEquals("05/08/2026", bon("05/08/2026", null).getDateHeure());
        assertEquals("05/08/2026", bon("05/08/2026", "  ").getDateHeure());
    }

    @Test
    @DisplayName("Date absente : jamais le mot null dans l'etat")
    void dateAbsente() {
        assertEquals("10:20", bon(null, "10:20").getDateHeure());
        assertEquals("", bon(null, null).getDateHeure());
    }
}
