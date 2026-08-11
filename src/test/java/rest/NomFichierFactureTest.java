package rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Nom du fichier PDF d'une facture tiers payant.
 *
 * Le fichier s'appelait "Facture_<numero>.pdf" : dans un dossier de factures editees le meme jour, rien ne disait a
 * quel organisme chacune appartenait. Le nom porte desormais l'organisme. Ces tests garantissent que le nom reste
 * utilisable par Windows : ni accent, ni apostrophe, ni slash, ni double separateur.
 */
class NomFichierFactureTest {

    @Test
    @DisplayName("Organisme et numero dans le nom")
    void nomComplet() {
        assertEquals("Facture_ASCOMA_FA0012.pdf", ModelFactureDynamiqueRessource.nomFichierFacture("ASCOMA", "FA0012"));
    }

    @Test
    @DisplayName("Accents, apostrophes et separateurs de chemin sont neutralises")
    void caracteresInterdits() {
        assertEquals("Facture_ASCOMA_COTE_D_IVOIRE_FA_12.pdf",
                ModelFactureDynamiqueRessource.nomFichierFacture("ASCOMA CÔTE D'IVOIRE", "FA/12"));
    }

    @Test
    @DisplayName("Partie manquante : pas de double soulignement ni de nom qui se termine mal")
    void partieManquante() {
        assertEquals("Facture_FA0012.pdf", ModelFactureDynamiqueRessource.nomFichierFacture(null, "FA0012"));
        assertEquals("Facture_ASCOMA.pdf", ModelFactureDynamiqueRessource.nomFichierFacture("ASCOMA", "  "));
        assertEquals("Facture.pdf", ModelFactureDynamiqueRessource.nomFichierFacture(null, null));
    }
}
