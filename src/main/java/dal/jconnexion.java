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
import java.sql.DriverManager;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import toolkits.parameters.commonparameter;
import toolkits.utils.date;
import toolkits.utils.jdom;
import toolkits.utils.logger;

/**
 *
 * @author Administrateur
 */
public class jconnexion {

    private String Host;
    private String User;
    private String Password;
    private String Database;
    private String DatabaseSID;
    private String Driver;
    private Connection StringConnexion;
    private String UrlConnexion;
    private ResultSet resultat;
    public String message;
    private int port;

    /**
     * Creates a new instance of jconnexion
     */
    public jconnexion() {
        if (this.StringConnexion != null) {
        } else {
            this.Driver = null;
            this.Database = null;
            this.StringConnexion = null;
            this.UrlConnexion = null;
        }

    }

    public void initConnexion() {
        jdom.InitRessource();
        jdom.LoadRessource();
        set_Default_param_connexion();
        set_driver(get_mysql_driver_JDBC());
        LoadDriver();
    }

    private void set_Default_param_connexion() {
        Host = jdom.ars_database_host;
        Password = jdom.ars_database_user_password;
        User = jdom.ars_database_user_name;
        Database = jdom.ars_database_name;
        DatabaseSID = "iutacad";
        port = commonparameter.port_databases;
    }

    public Connection get_StringConnexion() {
        return StringConnexion;
    }

    private void set_Connection(Connection StringConnexion) {
        this.StringConnexion = StringConnexion;
    }
    //accesseur des propriete privee

    public void set_port(int myport) {
        port = myport;

    }

    public void set_host(String Myhost) {
        Host = Myhost;
    }

    public void set_user(String Myuser) {
        User = Myuser;
    }

    public void set_password(String Mypassword) {
        Password = Mypassword;
    }

    public void set_database(String Mydatabase) {
        Database = Mydatabase;
    }

    public void set_databaseSID(String MydatabaseSID) {
        DatabaseSID = MydatabaseSID;
    }

    public void set_driver(String Mydriver) {
        Driver = Mydriver;
    }

    public String get_message() {
        return message;
    }

    public void set_Request(String MyRequest) {

        String Request = new String(MyRequest);
        try {
            Statement stmt = StringConnexion.createStatement();
            // new logger().OCategory.info(MyRequest);
            resultat = stmt.executeQuery(Request);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void set_Request(Vector<String> VMyRequest) {

        try {
            Statement stmt = StringConnexion.createStatement();
            stmt.execute("SET UNIQUE_CHECKS=0; ");
            stmt.execute("ALTER TABLE t_sms_customer DISABLE KEYS");
            stmt.execute("ALTER TABLE t_sms_not_send DISABLE KEYS");

            for (int k = 0; k < VMyRequest.size(); k++) {
                stmt.addBatch(VMyRequest.get(k));
            }
            stmt.executeBatch();
            this.StringConnexion.commit();
            stmt.execute("ALTER TABLE t_sms_customer ENABLE KEYS");
            stmt.execute("ALTER TABLE t_sms_not_send ENABLE KEYS");
            stmt.execute("SET UNIQUE_CHECKS=1; ");

            stmt.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void set_Request_Light(Vector<String> VMyRequest) {

        try {
            Statement stmt = StringConnexion.createStatement();
            // stmt.execute("SET UNIQUE_CHECKS=0; ");
            // stmt.execute("ALTER TABLE t_winning DISABLE KEYS");

            for (int k = 0; k < VMyRequest.size(); k++) {
                stmt.addBatch(VMyRequest.get(k));
            }
            stmt.executeBatch();

            this.StringConnexion.commit();

            for (int k = 0; k < VMyRequest.size(); k++) {
                //   stmt.addBatch(VMyRequest.get(k));

                new logger().OCategory.info(VMyRequest.get(k));
            }

            // stmt.execute("ALTER TABLE t_winning ENABLE KEYS");
            // stmt.execute("SET UNIQUE_CHECKS=1; ");
            stmt.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public ResultSet get_resultat() {
        return resultat;
    }
    // pilote JDBC Mysql

    public String get_mysql_driver_JDBC() {
        UrlConnexion = "jdbc:mysql://" + Host + "/"
                + Database + "?user=" + User
                + "&password=" + Password;
        return "com.mysql.jdbc.Driver";

    }
    // pilote JDBC SQL SERVER 2005

    public String get_sqlserver_2005_driver_JDBC() {
        UrlConnexion = "jdbc:sqlserver://" + Host + ";databaseName="
                + Database + ";user=" + User + ";password=" + Password;
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    }
    // pilote JDBC Oracle

    public String get_oracle_driver_JDBC() {
        UrlConnexion = "jdbc:oracle:thin:@" + Host + ":" + port + ":" + DatabaseSID;
        return "oracle.jdbc.driver.OracleDriver";
    }
    //pilote ODBC_JDBC

    public String get_driver_ODBC_JDBC() {
        UrlConnexion = "jdbc:odbc:" + Database;
        return "sun.jdbc.odbc.JdbcOdbcDriver";
    }
    //pilote odbc postgrelsql

    public String get_driver_ODBC_JDBC_Postgrel() {
        return "postgrelsql.Driver";
    }
    //chargement du pilotes

    public void LoadDriver() {
        try {
            Class.forName(Driver).newInstance();
            message = "Chargement Reussi";
            System.out.println(message);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            message = ex.getMessage();
            new logger().OCategory.warn(date.GetDateNow(date.formatterUI) + " " + message);
        }
    }

    public void OpenConnexion() {
        try {
            if (Driver.equals("com.mysql.jdbc.Driver")) {
                StringConnexion = DriverManager.getConnection(UrlConnexion);
                message = "Connexion reussi";
            } else if (Driver.equals("oracle.jdbc.driver.OracleDriver")) {
                StringConnexion = DriverManager.getConnection(UrlConnexion, User, Password);
                message = "Connexion reussi";
            } else if (Driver.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
                StringConnexion = DriverManager.getConnection(UrlConnexion, User, Password);
                message = "Connexion reussi";
            } else {
                message = "pas de connexion le driver n'est pas reconnue";
                new logger().OCategory.warn(date.GetDateNow(date.formatterUI) + " " + message);
            }

            System.out.println(message);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            message = ex.getMessage();
            new logger().OCategory.warn(date.GetDateNow(date.formatterUI) + " " + message);

        }
    }
//fermeture de la connexion

    public void CloseConnexion() {
        try {

            this.StringConnexion.close();
            resultat.close();
            Driver = null;
            Database = null;
            StringConnexion = null;
            UrlConnexion = null;
            message = "Connexion fermer";
            System.out.println(message);
        } catch (Exception MysqlEx) {
            message = "Impossible de fermer " + MysqlEx.getMessage();
            // ars_logger.oCategory.warn(date.GetDateNow(date.formatterUI)+ " "+message);
            System.out.println(message);
        }
    }
    public Connection getConnection(){
        try {
            StringConnexion=   DriverManager.getConnection(UrlConnexion);
        } catch (SQLException ex) {
            Logger.getLogger(jconnexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return StringConnexion;
    }
}
