
package job;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

/**
 *
 * @author koben
 */
@Singleton
@Startup
public class FlywayStartupBean {

    @Resource(mappedName = "jdbc/__laborex_pool")
    private DataSource dataSource;

    private static final Logger LOG = Logger.getLogger(FlywayStartupBean.class.getName());

    @PostConstruct
    public void migrate() {
        if (dataSource == null) {
            LOG.severe("No datasource found to execute the DB migrations!");
            throw new IllegalStateException("Datasource not found");
        }
        try {
            Flyway flyway = Flyway.configure().dataSource(dataSource).baselineOnMigrate(true)
                    .ignoreMissingMigrations(true).outOfOrder(true).cleanOnValidationError(true)
                    .validateOnMigrate(false).ignoreFutureMigrations(true).load();
            // Une migration qui a echoue laisse une trace d'echec dans flyway_schema_history, et
            // cette trace bloque toutes les migrations suivantes tant qu'elle n'est pas retiree.
            // repair() la retire au demarrage : le script corrige est alors rejoue normalement,
            // sans intervention manuelle en base. Les migrations deja reussies ne sont pas
            // touchees, et tous nos scripts sont ecrits pour etre rejouables sans effet de bord.
            //flyway.repair();
            flyway.migrate();
            LOG.info("Flyway migration completed");
        } catch (FlywayException e) {
            LOG.log(Level.SEVERE, "Flyway migration failed", e);
        }
    }

}
