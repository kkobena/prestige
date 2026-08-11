package rest.service.fne;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Compte rendu des avoirs FNE d'une facture de groupe.
 *
 * Regle verifiee ici : quand RIEN n'a abouti, le message commence par la CAUSE (et pas par un decompte a zero, qui
 * n'apprend rien a l'utilisateur), et TOUS les motifs distincts sont restitues avec les numeros de facture concernes.
 */
class MessageAvoirGroupeTest {

    private static Map<String, List<String>> motifs(Object... paires) {
        Map<String, List<String>> m = new LinkedHashMap<>();
        for (int i = 0; i < paires.length; i += 2) {
            m.put((String) paires[i], Arrays.asList(((String) paires[i + 1]).split(",")));
        }
        return m;
    }

    @Test
    @DisplayName("Echec total : la cause vient en tete, avec le numero de la facture")
    void echecTotalUneCause() {
        String message = FneServiceImpl.messageAvoirGroupe(0, 0, 0,
                motifs("Avoir impossible : la facture n'a pas ete generee avec la version actuelle.", "FA0012"));

        assertTrue(message.startsWith("Aucun avoir émis."), message);
        assertTrue(message.contains("n'a pas ete generee avec la version actuelle"), message);
        assertTrue(message.contains("facture n° FA0012"), message);
    }

    @Test
    @DisplayName("Deux causes differentes : les deux sont restituees, pas seulement la derniere")
    void echecTotalDeuxCauses() {
        String message = FneServiceImpl.messageAvoirGroupe(0, 0, 0,
                motifs("Identifiants FNE introuvables", "FA0012,FA0013", "Facture déjà réglée", "FA0014"));

        assertTrue(message.contains("Identifiants FNE introuvables"), message);
        assertTrue(message.contains("Facture déjà réglée"), message);
        assertTrue(message.contains("factures n° FA0012, FA0013"), message);
        assertTrue(message.contains("facture n° FA0014"), message);
    }

    @Test
    @DisplayName("Succes partiel : le decompte vient d'abord, la cause ensuite")
    void succesPartiel() {
        String message = FneServiceImpl.messageAvoirGroupe(3, 1, 2, motifs("Identifiants FNE introuvables", "FA0012"));

        assertTrue(message.startsWith("3 avoir(s) émis"), message);
        assertTrue(message.contains("1 facture(s) avaient déjà un avoir"), message);
        assertTrue(message.contains("2 non certifiée(s)"), message);
        assertTrue(message.contains("Identifiants FNE introuvables (facture n° FA0012)."), message);
    }

    @Test
    @DisplayName("Succes complet : aucun motif d'echec dans le message")
    void succesComplet() {
        assertEquals("5 avoir(s) émis.", FneServiceImpl.messageAvoirGroupe(5, 0, 0, motifs()));
    }

    @Test
    @DisplayName("Toutes les factures portaient deja un avoir")
    void toutesDejaAvoir() {
        String message = FneServiceImpl.messageAvoirGroupe(0, 4, 0, motifs());

        assertTrue(message.startsWith("Avoir déjà émis :"), message);
        assertTrue(message.contains("Rien n'a été renvoyé."), message);
    }

    @Test
    @DisplayName("Aucune facture certifiee : on dit de certifier d'abord")
    void aucuneCertifiee() {
        String message = FneServiceImpl.messageAvoirGroupe(0, 0, 3, motifs());

        assertTrue(message.contains("ne sont pas certifiées à la FNE"), message);
        assertTrue(message.contains("Certifiez-les d'abord."), message);
    }

    @Test
    @DisplayName("Echec total avec des factures ignorees : la cause d'abord, les ignorees ensuite")
    void echecTotalAvecIgnorees() {
        String message = FneServiceImpl.messageAvoirGroupe(0, 2, 1, motifs("Identifiants FNE introuvables", "FA0012"));

        assertTrue(message.startsWith("Aucun avoir émis. Identifiants FNE introuvables (facture n° FA0012)."), message);
        assertTrue(message.contains("2 facture(s) portaient déjà un avoir"), message);
        assertTrue(message.contains("1 facture(s) ne sont pas certifiées"), message);
    }

    @Test
    @DisplayName("Un motif sans numero de facture connu reste lisible")
    void motifSansNumero() {
        String message = FneServiceImpl.messageAvoirGroupe(0, 0, 0, motifs("Erreur technique", ""));

        assertEquals("Aucun avoir émis. Erreur technique.", message);
    }

    // ----------------------------------------------------------------------------------------
    // Certification de groupe : meme regle d'ecriture que les avoirs
    // ----------------------------------------------------------------------------------------

    @Test
    @DisplayName("Certification, echec total : la cause vient en tete")
    void certificationEchecTotal() {
        String message = FneServiceImpl.messageGroupe(0, 0, motifs("Stock de stickers épuisé", "FA0012,FA0013"));

        assertTrue(message.startsWith("Aucune facture certifiée. Stock de stickers épuisé"), message);
        assertTrue(message.contains("factures n° FA0012, FA0013"), message);
    }

    @Test
    @DisplayName("Certification, succes partiel : le decompte d'abord, puis tous les motifs")
    void certificationSuccesPartiel() {
        String message = FneServiceImpl.messageGroupe(4, 1,
                motifs("Stock de stickers épuisé", "FA0012", "Client sans NCC", "FA0013"));

        assertTrue(message.startsWith("4 facture(s) certifiée(s), 1 déjà certifiée(s) et donc ignorée(s)."), message);
        assertTrue(message.contains("Stock de stickers épuisé (facture n° FA0012)."), message);
        assertTrue(message.contains("Client sans NCC (facture n° FA0013)."), message);
    }

    @Test
    @DisplayName("Certification, groupe deja entierement certifie")
    void certificationDejaFaite() {
        assertTrue(FneServiceImpl.messageGroupe(0, 3, motifs()).startsWith("Facture déjà certifiée :"));
    }
}
