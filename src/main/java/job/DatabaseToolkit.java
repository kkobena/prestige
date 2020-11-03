/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package job;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import shedule.DailyStockTask;

/**
 *
 * @author Kobena
 */
@Singleton
@Startup
@TransactionManagement(value = TransactionManagementType.BEAN)
public class DatabaseToolkit {
    private static final Logger LOG = Logger.getLogger(DatabaseToolkit.class.getName());
    @Resource(mappedName = "jdbc/__laborex_pool")
    private DataSource dataSource;
    @Resource(name = "concurrent/__defaultManagedExecutorService")
    ManagedExecutorService mes;
    void runTask() {
        DailyStockTask dailyStockTask = new DailyStockTask();
        dailyStockTask.setDataSource(dataSource);
         mes.submit(dailyStockTask);
       /* Future f = mes.submit(dailyStockTask);
        while (!f.isDone()) {
            LOG.info("Running..................");
            if(f.isDone()){
                   LOG.info("Is DONE.................."); 
            }
        }*/
    }
    
    @PostConstruct
    public void init() {
        if (dataSource == null) {
            LOG.info("no datasource found to execute the db migrations!");
            throw new EJBException(
                    "no datasource found to execute the db migrations!");
        }
        try {
            Flyway flyway = Flyway.configure().dataSource(dataSource)
                    .baselineOnMigrate(true)
                    .ignoreMissingMigrations(true)
                    .outOfOrder(true)
                    .cleanOnValidationError(true)
                    .validateOnMigrate(false)
                    .ignoreFutureMigrations(true)
                    .load();
            flyway.migrate();
        } catch (FlywayException e) {
            LOG.log(Level.SEVERE, "ini migration", e);
        }
        runTask();
        
    }
}
