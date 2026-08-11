package rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.PersistenceException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Traduction d'une troncature MySQL en message comprehensible.
 *
 * Cas rencontre en production sur le recapitulatif par compte d'organisme : la procedure stockee echoue avec "Data too
 * long for column 'CODEORGANISME'". L'ecran affichait alors un tableau VIDE, sans message, et le Centre de Support ne
 * recevait rien - le service repondait "succes". Ces tests garantissent que le champ fautif est bien extrait de la
 * chaine d'exceptions, si profond soit-il.
 */
class ChampTropLongTest {

    /** Reconstitue l'emboitement reel : PersistenceException -> DatabaseException -> MysqlDataTruncation. */
    private static Exception erreurDeProduction() {
        Exception racine = new Exception("Data truncation: Data too long for column 'CODEORGANISME' at row 1");
        Exception intermediaire = new Exception("Internal Exception: com.mysql.jdbc.MysqlDataTruncation", racine);
        return new PersistenceException("Exception [EclipseLink-4002] : DatabaseException", intermediaire);
    }

    @Test
    @DisplayName("Le champ est retrouve au fond de la chaine d'exceptions")
    void champAuFondDeLaChaine() {
        assertEquals("CODEORGANISME", ReglementFactureRessource
                .champTropLong(ReglementFactureRessource.texteDeLaChaine(erreurDeProduction())));
    }

    @Test
    @DisplayName("Message direct, sans emboitement")
    void messageDirect() {
        assertEquals("str_CODE_ORGANISME",
                ReglementFactureRessource.champTropLong("Data too long for column 'str_CODE_ORGANISME' at row 1"));
    }

    @Test
    @DisplayName("Une erreur d'une autre nature ne declenche pas ce message")
    void autreErreur() {
        assertNull(ReglementFactureRessource.champTropLong("Unknown column 'x' in field list"));
        assertNull(ReglementFactureRessource.champTropLong(""));
        assertNull(ReglementFactureRessource.champTropLong(null));
    }

    @Test
    @DisplayName("Message tronque : pas de guillemet fermant, pas de plantage")
    void messageIncomplet() {
        assertNull(ReglementFactureRessource.champTropLong("Data too long for column 'CODEORG"));
    }

    @Test
    @DisplayName("Une chaine circulaire ne boucle pas indefiniment")
    void chaineCirculaire() {
        Exception a = new Exception("premier niveau");
        assertTrue(ReglementFactureRessource.texteDeLaChaine(a).contains("premier niveau"));
        // une exception dont la cause est elle-meme : la lecture doit s'arreter
        assertTrue(ReglementFactureRessource.texteDeLaChaine(new Exception("seul", a)).contains("seul"));
    }
}
