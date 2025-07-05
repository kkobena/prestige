package job.ejb;

import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedExecutorService;

import rest.service.StockReapproService;

/**
 *
 * @author koben
 */
@Singleton
@Startup
public class StockReapproJob {

    @Resource(name = "concurrent/__defaultManagedExecutorService")
    private ManagedExecutorService mes;

    @EJB
    private StockReapproService stockReapproService;
    private boolean dejaExcute = false;
    @Resource
    private TimerService timerService;

    @PostConstruct
    public void init() {
        TimerConfig reapproTmer = new TimerConfig("reapproTmer", false);
        timerService.createCalendarTimer(new ScheduleExpression().minute("*/2").hour("*").dayOfMonth("*").year("*"),
                reapproTmer);

    }

    private void execute() {

        this.stockReapproService.execute();

    }

    private void cancelTimers() {
        for (Timer timer : timerService.getTimers()) {
            if (timer.getInfo().equals("reapproTmer")) {
                timer.cancel();
                return;
            }

        }
    }

    @Timeout
    public void timeout(Timer timer) {
        if (!dejaExcute && "reapproTmer".equals(timer.getInfo())) {
            mes.submit(this::execute);
            dejaExcute = true;
            cancelTimers();

        }

    }
}
