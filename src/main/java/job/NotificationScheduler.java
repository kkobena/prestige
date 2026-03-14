package job;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

/**
 *
 * @author koben
 */
@Singleton
@Startup
public class NotificationScheduler {

    @Inject
    private NotificationScheduledService notificationScheduledService;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void init() {
        timerService.createCalendarTimer(new ScheduleExpression().minute("*/2").hour("*").dayOfMonth("*").year("*"),
                new TimerConfig("sms", false));

        timerService.createCalendarTimer(new ScheduleExpression().hour("12,20").dayOfMonth("*").year("*"),
                new TimerConfig("email", false));
    }

    @Timeout
    public void onTimeout(Timer timer) {
        var timerInfo = (String) timer.getInfo();
        if ("sms".equals(timerInfo)) {
            notificationScheduledService.sendPendingSmsAsync();
        } else if ("email".equals(timerInfo)) {
            notificationScheduledService.sendPendingEmailsAsync();
        }

    }
}
