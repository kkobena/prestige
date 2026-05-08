
package job;

import java.time.LocalDate;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import rest.service.impl.DailyStockService;

/**
 *
 * @author koben
 */
@Singleton
@Startup
public class StockDailyScheduler {

    @Inject
    private DailyStockService dailyStockService;

    @PostConstruct
    public void init() {

        // Au demarrage
        dailyStockService.processAsync(LocalDate.now());

    }

    @Schedule(hour = "0", minute = "5", second = "0", persistent = false)
    public void run() {
        dailyStockService.updateStockDailyValueAsync();
    }

}
