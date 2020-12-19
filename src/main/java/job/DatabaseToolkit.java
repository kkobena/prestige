/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package job;

import dal.Notification;
import dal.NotificationClient;
import dal.TParameters;
import dal.enumeration.Canal;
import dal.enumeration.Statut;
import dal.enumeration.TypeNotification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.json.JSONObject;
import shedule.DailyStockTask;
import util.DateConverter;
import util.SmsParameters;

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
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @Resource
    private TimerService timerService;
    @Inject
    private UserTransaction userTransaction;

    void runTask() {
        DailyStockTask dailyStockTask = new DailyStockTask();
        dailyStockTask.setDateStock(LocalDate.now());
        dailyStockTask.setEntityManager(em);
        dailyStockTask.setUserTransaction(userTransaction);
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
        createTimer();

    }

//    @Schedule(second = "*/30", minute = "*", hour = "*", dayOfMonth = "*", year = "*", persistent = true)
    public void manageSms() {
      
        if (checkParameterByKey(DateConverter.KEY_SMS_CLOTURE_CAISSE)) {
           
            mes.submit(() -> {
                try {
                    List<Notification> notifications = findAllByCanal();
                    notifications.forEach(n -> {
                        sendSMS(n);
                    });
                    TimeUnit.SECONDS.sleep(6);
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            });
        }

    }

    public void createTimer() {
        final TimerConfig email = new TimerConfig("email", false);
        timerService.createCalendarTimer(new ScheduleExpression()
                //                                .minute("*/2")
                //                                .hour("*")
                .hour(findScheduledValues())
                .dayOfMonth("*")
                .year("*"), email
        );

        final TimerConfig sms = new TimerConfig("sms", false);
        timerService.createCalendarTimer(new ScheduleExpression()
//                .second("*/30")
                .minute("*/2")
                .hour("*")
                .dayOfMonth("*")
                .year("*"), sms
        );
    }

    public void manageEmail() {
        List<Notification> data = findByStatut(Statut.NOT_SEND).stream().filter(e -> e.getNotificationClients().isEmpty()).collect(Collectors.toList());
        boolean result = sendMail(buildEmailContent(data), null, "Resumé activité prestige 2");
        if (result) {
            try {
                userTransaction.begin();
                data.stream().forEach(e -> {
                    e.setStatut(Statut.SENT);
                    e.setModfiedAt(LocalDateTime.now());
                    em.merge(e);
                });
                userTransaction.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendSMS(Notification notification) {
        try {
            Client client = ClientBuilder.newClient();
            SmsParameters sp = SmsParameters.getInstance();
            List<NotificationClient> toClients = findNotificationClients(notification);
            String address = null;
            if (!toClients.isEmpty()) {// a revoir pour les envois multiples
                address = toClients.get(0).getClient().getStrADRESSE();
            }

            if (StringUtils.isEmpty(address)) {
                address = sp.mobile;
            }
            JSONObject jSONObject = new JSONObject();
            JSONObject outboundSMSMessageRequest = new JSONObject();
            outboundSMSMessageRequest.put("address", "tel:+225" + address);
            outboundSMSMessageRequest.put("senderAddress", sp.senderAddress);
            JSONObject outboundSMSTextMessage = new JSONObject();
            outboundSMSTextMessage.put("message", notification.getMessage());
            outboundSMSMessageRequest.put("outboundSMSTextMessage", outboundSMSTextMessage);
            jSONObject.put("outboundSMSMessageRequest", outboundSMSMessageRequest);
            WebTarget myResource = client.target(sp.pathsmsapisendmessageurl);
            Response response = myResource.request().header("Authorization", "Bearer ".concat(sp.accesstoken))
                    .post(Entity.entity(jSONObject.toString(), MediaType.APPLICATION_JSON_TYPE));
//            LOG.log(Level.INFO, "*******************************>>> {0} {1} {2}", new Object[]{response.getStatus(), response.readEntity(String.class), address});
            userTransaction.begin();
            if (response.getStatus() == 201) {
                notification.setStatut(Statut.SENT);

            } else {
                notification.setNumberAttempt(notification.getNumberAttempt() + 1);
                if (notification.getNumberAttempt() >= 3) {
                    notification.setModfiedAt(LocalDateTime.now());
                    notification.setStatut(Statut.LOCK);
                }
            }
            em.merge(notification);
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public List<Notification> findByCreatedAtAndStatut() {
        try {
            TypedQuery<Notification> q = em.createNamedQuery("Notification.findAllByCreatedAtAndStatus", Notification.class);
            q.setParameter("createdAt", LocalDateTime.parse(LocalDate.now().toString() + " " + "00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            q.setParameter("statut", Statut.NOT_SEND);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public List<Notification> findByStatut(Statut statut) {
        try {
            TypedQuery<Notification> q = em.createNamedQuery("Notification.findAllByStatus", Notification.class);
            q.setParameter("statut", statut);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public List<Notification> findAllByCanal() {
        try {
            TypedQuery<Notification> q = em.createNamedQuery("Notification.findAllByCreatedAtAndStatusAndCanal", Notification.class);
            q.setParameter("createdAt", LocalDateTime.parse(LocalDate.now().minusMonths(3).toString() + " " + "00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            q.setParameter("statut", Statut.NOT_SEND);
            q.setParameter("canaux", EnumSet.of(Canal.SMS));
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public void sendSMS() {
        try {
            Client client = ClientBuilder.newClient();
            SmsParameters sp = SmsParameters.getInstance();

            String address = null;

            if (StringUtils.isEmpty(address)) {
                address = sp.mobile;
            }
            JSONObject jSONObject = new JSONObject();
            JSONObject outboundSMSMessageRequest = new JSONObject();
            outboundSMSMessageRequest.put("address", "tel:+225" + address);
            outboundSMSMessageRequest.put("senderAddress", sp.senderAddress);
            JSONObject outboundSMSTextMessage = new JSONObject();
            outboundSMSTextMessage.put("message", "teste ***  ");
            outboundSMSMessageRequest.put("outboundSMSTextMessage", outboundSMSTextMessage);
            jSONObject.put("outboundSMSMessageRequest", outboundSMSMessageRequest);
            WebTarget myResource = client.target(sp.pathsmsapisendmessageurl);
            Response response = myResource.request().header("Authorization", "Bearer ".concat(sp.accesstoken))
                    .post(Entity.entity(jSONObject.toString(), MediaType.APPLICATION_JSON_TYPE));
            LOG.log(Level.INFO, "*******************************>>> {0} {1} {2}", new Object[]{response.getStatus(), response.readEntity(String.class), address});
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public boolean checkParameterByKey(String key) {
        try {
            TParameters parameters = em.find(TParameters.class, key);
            return (Integer.valueOf(parameters.getStrVALUE().trim()) == 1);
        } catch (Exception e) {
            return false;
        }
    }

    public String findScheduledValues() {
        try {
            TParameters parameters = em.find(TParameters.class, DateConverter.KEY_HEURE_EMAIL);
            return parameters.getStrVALUE();
        } catch (Exception e) {
            return "12,20";
        }
    }

    @Timeout
    public void timeout(Timer timer) {
        if ("sms".equals(timer.getInfo())) {
            manageSms();
        } else if ("email".equals(timer.getInfo())) {
            manageEmail();
        }
    }

    public List<NotificationClient> findNotificationClients(Notification n) {
        try {
            TypedQuery<NotificationClient> q = em.createNamedQuery("NotificationClient.findByNotificationId", NotificationClient.class);
            q.setParameter("notificationId", n.getId());
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public boolean sendMail(String content, String email, String subject) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }
        SmsParameters sp = SmsParameters.getInstance();
        Properties props = new Properties();
        props.put("mail.smtp.host", sp.smtpHost);
        props.put("mail.transport.protocol", sp.protocol);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);

        try {

            if (StringUtils.isEmpty(email)) {
                email = sp.mailOfficine;
            }
            Address sender = new InternetAddress(sp.email);
            Address recipient = new InternetAddress(email);
            msg.setContent(content, "text/html; charset=utf-8");
            msg.setFrom(sender);
            msg.setRecipient(Message.RecipientType.TO, recipient);
            msg.setSubject(subject);
            Transport.send(msg, sp.email, sp.password);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace(System.err);
            return false;
        }
    }

    public String buildEmailContent(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Map<TypeNotification, List<Notification>> map = notifications.stream().collect(Collectors.groupingBy(Notification::getTypeNotification));
        sb.append("<html><body>");
        map.forEach((key, values) -> {
            sb.append("<h2 style='margin: 10px;padding: 5px;'>");
            sb.append(key.getValue()).append("</h2><ol>");
            values.forEach(e -> {
                sb.append("<li>").append(e.getMessage());
                sb.append("</li>");
            });
            sb.append("</ol>");
        });
        sb.append("</body></html>");
        return sb.toString();
    }
}
