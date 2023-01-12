package job.ejb;

import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import rest.service.StockReapproService;

/**
 *
 * @author koben
 */
@Singleton
@Startup

public class StockReapproJob {

    @Resource(name = "concurrent/__defaultManagedScheduledExecutorService")
    private ManagedScheduledExecutorService scheduledExecutorService;

    @EJB
    private StockReapproService stockReapproService;

    @PostConstruct
    public void init() {
     
        scheduledExecutorService.scheduleAtFixedRate(this::execute, 0, 2, TimeUnit.HOURS);
        
    }

    private void execute() {

        this.stockReapproService.execute();

    }

}
