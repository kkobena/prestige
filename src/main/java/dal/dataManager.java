/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import toolkits.parameters.commonparameter;
import toolkits.utils.jdom;


/**
 *
 * @author Administrator
 */
public class dataManager {

    public boolean isConected = false;
    private EntityManagerFactory emf;
    private EntityManager em;
    private String PERSISTENCE_UNIT_NAME = commonparameter.PERSISTENCE_UNIT_NAME_DAL;
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
   /*public static void main(String[]ars) {
	   dataManager m=new dataManager();
	   m.initEntityManager();
	   System.exit(0);
	
}*/
    public void initEntityManager() {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("javax.persistence.jdbc.user", jdom.ars_database_user_name);
        parameters.put("javax.persistence.jdbc.password", jdom.ars_database_user_password);
        parameters.put("javax.persistence.jdbc.url", "jdbc:mysql://" + jdom.ars_database_host + ":" + jdom.ars_database_port + "/" + jdom.ars_database_name);
//        parameters.put("javax.persistence.jdbc.url", "jdbc:mysql://" + jdom.ars_database_host + ":" + jdom.ars_database_port + "/" + jdom.ars_database_name+"?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC");
        parameters.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
      
//        parameters.put(PersistenceUnitProperties.WEAVING, "true");
//           parameters.put(PersistenceUnitProperties.WEAVING_LAZY, "true");//-javaagent:"./lib/eclipselink/eclipselink.jar"
//        parameters.put("eclipselink.query-results-cache", "false");
//        parameters.put("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
//        parameters.put("javax.persistence.lock.timeout", "10000");

//        parameters.put("hibernate.show_sql", "true");
//          parameters.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_ACTION);
        setEmf(Persistence.createEntityManagerFactory(getPERSISTENCE_UNIT_NAME(), parameters));
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
        EntityManager em = this.em;

        return emf;

    }

    /**
     * @param emf the emf to set
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
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }

    /**
     * @return the PERSISTENCE_UNIT_NAME
     */
    public String getPERSISTENCE_UNIT_NAME() {
        return PERSISTENCE_UNIT_NAME;
    }

    /**
     * @param PERSISTENCE_UNIT_NAME the PERSISTENCE_UNIT_NAME to set
     */
    public void setPERSISTENCE_UNIT_NAME(String PERSISTENCE_UNIT_NAME) {
        this.PERSISTENCE_UNIT_NAME = PERSISTENCE_UNIT_NAME;
    }
}
