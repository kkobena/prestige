package dal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Fermeture des ressources d'un etat (jconnexion.CloseConnexion).
 *
 * Regression couverte : a chaque edition d'etat, le journal affichait "Impossible de fermer null". resultat n'est
 * renseigne que par set_Request ; un etat Jasper etant rempli depuis la connexion, il restait null et sa fermeture
 * levait un NullPointerException, qui empechait au passage la fermeture des ressources suivantes.
 *
 * Le test n'ouvre aucune connexion : il instancie la classe sans passer par le constructeur (qui contacterait la base)
 * et renseigne les champs directement.
 */
class FermetureConnexionTest {

    /**
     * Faux ResultSet/Connection : on n'a besoin que de savoir si close() a ete appele, et de pouvoir le faire echouer.
     */
    private static class RessourceFactice implements AutoCloseable {
        boolean ferme = false;
        final boolean echoue;

        RessourceFactice(boolean echoue) {
            this.echoue = echoue;
        }

        @Override
        public void close() throws Exception {
            ferme = true;
            if (echoue) {
                throw new IllegalStateException("deja fermee");
            }
        }
    }

    private static jconnexion instanceSansConnexion() throws Exception {
        // Unsafe.allocateInstance equivalent : on evite le constructeur, qui ouvrirait une connexion MySQL
        Field theUnsafe = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        return (jconnexion) Class.forName("sun.misc.Unsafe").getMethod("allocateInstance", Class.class).invoke(unsafe,
                jconnexion.class);
    }

    private static void injecter(jconnexion cible, String champ, Object valeur) throws Exception {
        Field f = jconnexion.class.getDeclaredField(champ);
        f.setAccessible(true);
        f.set(cible, valeur);
    }

    private static String fermerEnCapturantLaSortie(jconnexion cible) {
        PrintStream sortieInitiale = System.out;
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capture));
            cible.CloseConnexion();
        } finally {
            System.setOut(sortieInitiale);
        }
        return capture.toString();
    }

    @Test
    @DisplayName("Etat Jasper : aucun resultat de requete, donc rien a signaler dans le journal")
    void sansResultatAucunMessage() throws Exception {
        jconnexion cible = instanceSansConnexion();
        // resultat volontairement laisse a null, comme lors du remplissage d'un etat Jasper
        assertEquals("", fermerEnCapturantLaSortie(cible));
    }

    @Test
    @DisplayName("Une fermeture en echec n'empeche pas les autres")
    void echecNEmpechePasLesAutres() throws Exception {
        jconnexion cible = instanceSansConnexion();
        RessourceFactice resultat = new RessourceFactice(true);
        RessourceFactice connexion = new RessourceFactice(false);
        injecter(cible, "resultat",
                java.sql.ResultSet.class.cast(java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class<?>[] { java.sql.ResultSet.class }, (proxy, methode, args) -> {
                            if ("close".equals(methode.getName())) {
                                resultat.close();
                            }
                            return null;
                        })));
        injecter(cible, "StringConnexion",
                java.sql.Connection.class.cast(java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class<?>[] { java.sql.Connection.class }, (proxy, methode, args) -> {
                            if ("close".equals(methode.getName())) {
                                connexion.close();
                            }
                            return null;
                        })));

        String journal = fermerEnCapturantLaSortie(cible);

        assertTrue(resultat.ferme, "le resultat doit avoir ete ferme");
        assertTrue(connexion.ferme, "la connexion doit etre fermee MEME si le resultat a echoue");
        assertTrue(journal.contains("Impossible de fermer le resultat de la requete"), journal);
        assertFalse(journal.contains("null"), "le message ne doit plus dire seulement 'null' : " + journal);
    }

    @Test
    @DisplayName("Un second appel ne refait rien et ne signale rien")
    void secondAppelSansEffet() throws Exception {
        jconnexion cible = instanceSansConnexion();
        RessourceFactice connexion = new RessourceFactice(false);
        injecter(cible, "StringConnexion",
                java.sql.Connection.class.cast(java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class<?>[] { java.sql.Connection.class }, (proxy, methode, args) -> {
                            if ("close".equals(methode.getName())) {
                                connexion.close();
                            }
                            return null;
                        })));

        fermerEnCapturantLaSortie(cible);
        assertTrue(connexion.ferme);
        assertEquals("", fermerEnCapturantLaSortie(cible), "le second appel doit etre silencieux");
    }
}
