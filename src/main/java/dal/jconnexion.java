/*
 * jconnexion.java
 *
 * Created on 24 ao�t 2008, 05:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package dal;

import java.sql.*;
import java.sql.Connection;

/**
 *
 * @author Administrateur
 */
public class jconnexion {

    private Connection StringConnexion;

    private ResultSet resultat;
    public String message;

    private final JdbConnexion jdbConnexion;

    /**
     * Creates a new instance of jconnexion
     */
    public jconnexion() {
        jdbConnexion = new JdbConnexion();
        StringConnexion = jdbConnexion.getConnection();

    }

    public void initConnexion() {

    }

    public Connection get_StringConnexion() {
        return StringConnexion;
    }

    public String get_message() {
        return message;
    }

    public void set_Request(String request) {

        String Request = request;
        try {
            Statement stmt = StringConnexion.createStatement();
            // new logger().OCategory.info(MyRequest);
            resultat = stmt.executeQuery(Request);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public ResultSet get_resultat() {
        return resultat;
    }
    // pilote JDBC Mysql

    public void LoadDriver() {

    }

    public void OpenConnexion() {
        if (StringConnexion == null) {
            StringConnexion = jdbConnexion.getConnection();
        }

    }
    // fermeture de la connexion

    /**
     * Ferme le resultat de requete puis la connexion.
     *
     * Trois defauts corriges ici, tous visibles a l'edition d'un etat :
     *
     * 1. resultat n'est renseigne que par set_Request(). Un etat Jasper est rempli directement a partir de la connexion
     * : resultat restait null, resultat.close() levait un NullPointerException, et le journal affichait "Impossible de
     * fermer null" - un message alarmant qui ne designait rien.
     *
     * 2. les trois fermetures etaient dans UN SEUL try : la premiere qui echouait empechait les suivantes. Chaque
     * ressource est desormais fermee independamment.
     *
     * 3. la troisieme ligne, jdbConnexion.getConnection().close(), OUVRAIT une nouvelle connexion a la base (ce
     * getConnection fait un DriverManager.getConnection a chaque appel, ce n'est pas un pool) uniquement pour la
     * refermer aussitot. Elle est supprimee : il n'y a rien a fermer de plus que la connexion de cet objet.
     *
     * L'ordre compte : le resultat se ferme AVANT la connexion dont il depend.
     */
    public void CloseConnexion() {
        fermer(resultat, "le resultat de la requete");
        resultat = null;
        fermer(StringConnexion, "la connexion a la base");
        StringConnexion = null;
    }

    private void fermer(AutoCloseable ressource, String libelle) {
        if (ressource == null) {
            return;
        }
        try {
            ressource.close();
        } catch (Exception ex) {
            message = "Impossible de fermer " + libelle + " : "
                    + (ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName());
            System.out.println(message);
        }
    }

    public Connection getConnection() {
        if (StringConnexion != null) {
            return StringConnexion;
        }
        return jdbConnexion.getConnection();
    }
}
