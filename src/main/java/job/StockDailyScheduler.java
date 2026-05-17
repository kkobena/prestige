
package job;

import java.time.LocalDate;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import rest.service.impl.DailyStockService;
import config.AppConfig;

/**
 *
 * @author koben
 */
@Singleton
public class StockDailyScheduler {

    @Inject
    private DailyStockService dailyStockService;

    @Inject
    private AppConfig appConfig;

    public void runOnStartup() {
        dailyStockService.processAsync(LocalDate.now());
    }

    @Schedule(hour = "0", minute = "5", second = "0", persistent = false)
    public void run() {
        if (!appConfig.isServerMode()) {
            return;
        }
        dailyStockService.updateStockDailyValueAsync();
    }

}
