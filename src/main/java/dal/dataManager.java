/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import toolkits.utils.jdom;

/**
 *
 * @author Administrator
 */
public class dataManager {

    public boolean isConected = false;
    private EntityManagerFactory emf;
    private EntityManager em;
    private static final String PERSISTENCE_UNIT_NAME = "DALPU";
    private EntityTransaction Transaction;
    private boolean bTransactionGroupe = false;
    // début transaction

    public boolean isTransactionGroupe() {
        return bTransactionGroupe;
    }

    public void setTransactionGroupe(boolean pbTransactionGroupe) {
        this.bTransactionGroupe = pbTransactionGroupe;
    }

    public dataManager() {
        jdom.InitRessource();
        jdom.LoadRessource();
    }

    /*
     * public static void main(String[]ars) { dataManager m=new dataManager(); m.initEntityManager(); System.exit(0);
     *
     * }
     */
    public void initEntityManager() {
        // Map<String,Object> parameters = new HashMap<>();
        // parameters.put("javax.persistence.jdbc.user", jdom.ars_database_user_name);
        // parameters.put("javax.persistence.jdbc.password", jdom.ars_database_user_password);
        // parameters.put("javax.persistence.jdbc.url", "jdbc:mysql://" + jdom.ars_database_host + ":" +
        // jdom.ars_database_port + "/" + jdom.ars_database_name);
        //// parameters.put("javax.persistence.jdbc.url", "jdbc:mysql://" + jdom.ars_database_host + ":" +
        // jdom.ars_database_port + "/" +
        // jdom.ars_database_name+"?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC");
        // parameters.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        //

        setEmf(Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME));
        setEm(getEmf().createEntityManager());
        isConected = true;

    }

    public void BeginTransaction() {
        // début transaction
        Transaction = em.getTransaction();

        Transaction.begin();
        // affichage personnes
    }

    public void beginTransaction() {

        Transaction = em.getTransaction();
        if (!Transaction.isActive()) {
            Transaction.begin();
        }

    }

    public void closeTransaction() {
        if (Transaction.isActive()) {
            Transaction.commit();
        }

    }

    public void CloseTransaction() {
        // début transaction
        Transaction.commit();
        // affichage personnes
    }

    public void RejectTransaction() {
        // début transaction
        Transaction.rollback();
        // affichage personnes
    }

    public void closeEntityManager() {
        getEm().close();
        getEmf().close();
        isConected = false;
    }

    /**
     * @return the emf
     */
    public EntityManagerFactory getEmf() {

        return emf;

    }

    /**
     * @param emf
     *            the emf to set
     */
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * @return the em
     */
    public EntityManager getEm() {
        return em;
    }

    /**
     * @param em
     *            the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }

}
