/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.NotificationDTO;
import dal.CategorieNotification;
import dal.Notification;
import dal.NotificationClient;
import dal.Notification_;
import dal.TClient;
import dal.TUser;
import dal.enumeration.Canal;
import dal.enumeration.Statut;
import dal.enumeration.TypeNotification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import rest.service.NotificationService;
import util.FunctionUtils;
import util.SmsParameters;

/**
 *
 * @author koben
 */
@Stateless
public class NotificationImpl implements NotificationService {

    private static final Logger LOG = Logger.getLogger(NotificationImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public JSONObject findAll(String typeNotification, Canal canal, Statut statut, String dtStart, String dtEnd,
            int start, int limit) {
        var locatDateStart = StringUtils.isNotEmpty(dtStart) ? LocalDate.parse(dtStart) : LocalDate.now().minusYears(1);
        var locatDateEnd = StringUtils.isNotEmpty(dtEnd) ? LocalDate.parse(dtEnd) : LocalDate.now();
        var count = count(locatDateStart, locatDateEnd, canal, typeNotification);
        var data = getList(locatDateStart, locatDateEnd, canal, typeNotification, start, limit, false).stream()
                .map(NotificationDTO::new).collect(Collectors.toList());
        return FunctionUtils.returnData(data, count);
    }

    @Override
    public void save(Notification notification) {
        getEntityManager().merge(notification);
    }

    @Override
    public void save(Notification notification, TClient client) {
        notification.addNotificationClients(new NotificationClient(client, notification));
        getEntityManager().persist(notification);

    }

    @Override
    public Notification buildNotification(NotificationDTO notificationDto, TUser user) {
        Notification notification = new Notification();
        CategorieNotification categorieNotification = this.getOneByName(TypeNotification.MASSE);
        notification.setCategorieNotification(categorieNotification);
        // notification.setCanal(Canal.SMS_MASSE);
        notification.setMessage(notificationDto.getMessage());
        // notification.setTypeNotification(TypeNotification.MASSE);
        notification.setUser(user);
        if (CollectionUtils.isNotEmpty(notificationDto.getClients())) {
            notificationDto.getClients().stream().forEach(u -> {
                TClient client = getEntityManager().find(TClient.class, u.getClientId());
                notification.getNotificationClients().add(new NotificationClient(client, notification));
            });
        }
        getEntityManager().persist(notification);
        return notification;

    }

    private List<Predicate> listPredicates(CriteriaBuilder cb, Root<Notification> root, LocalDate dtStart,
            LocalDate dtEnd, Canal canal, String typeNotification) {
        List<Predicate> predicates = new ArrayList<>();
        // CategorieNotification_.typeNotification
        Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(Notification_.CREATED_AT)),
                java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
        predicates.add(btw);
        if (StringUtils.isNotEmpty(typeNotification)) {
            predicates.add(cb.equal(root.get(Notification_.categorieNotification).get("name"),
                    TypeNotification.valueOf(typeNotification).name()));
        }
        if (Objects.nonNull(canal)) {
            predicates.add(cb.equal(root.get(Notification_.categorieNotification).get("canal"), canal));
        }
        return predicates;
    }

    private long count(LocalDate dtStart, LocalDate dtEnd, Canal canal, String typeNotification) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Notification> root = cq.from(Notification.class);
        cq.select(cb.count(root));
        List<Predicate> predicates = listPredicates(cb, root, dtStart, dtEnd, canal, typeNotification);
        cq.where(cb.and(predicates.toArray(Predicate[]::new)));
        TypedQuery<Long> q = em.createQuery(cq);
        return Objects.isNull(q.getSingleResult()) ? 0 : q.getSingleResult();

    }

    private List<Notification> getList(LocalDate dtStart, LocalDate dtEnd, Canal canal, String typeNotification,
            int start, int limit, boolean all) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
        Root<Notification> root = cq.from(Notification.class);
        cq.select(root).orderBy(cb.desc(root.get(Notification_.MODFIED_AT)));
        List<Predicate> predicates = listPredicates(cb, root, dtStart, dtEnd, canal, typeNotification);
        cq.where(cb.and(predicates.toArray(Predicate[]::new)));
        TypedQuery<Notification> q = em.createQuery(cq);
        if (!all) {
            q.setFirstResult(start);
            q.setMaxResults(limit);

        }
        return q.getResultList();
    }

    @Override
    public String buildDonnees(Map<String, Object> donneesMap) {
        if (MapUtils.isEmpty(donneesMap)) {
            return null;
        }
        JSONObject json = new JSONObject();
        donneesMap.forEach(json::put);
        return json.toString();
    }

    @Override
    public CategorieNotification getOneByName(TypeNotification typeNotification) {
        TypedQuery<CategorieNotification> typedQuery = this.em.createNamedQuery("CategorieNotification.findOneByName",
                CategorieNotification.class);
        typedQuery.setParameter("name", typeNotification.name());
        return typedQuery.getSingleResult();
    }

    // @Asynchronous
    @Override
    public void sendMail() {
        List<Notification> notifications = findByStatutAndCanal(Set.of(Statut.NOT_SEND, Statut.SENT),
                Set.of(Canal.EMAIL));

        if (!notifications.isEmpty()) {
            StringBuilder html = new StringBuilder();
            html.append(rest.service.notification.template.Mail.beginTag());
            notifications.stream().collect(Collectors.groupingBy(Notification::getCategorieNotification))
                    .forEach((categorie, categorieNotifications) -> {
                        System.err.println("categorie.getName() " + categorie.getName());
                        TypeNotification typeNotification = TypeNotification.fromName(categorie.getName());
                        switch (typeNotification) {
                        case CLOTURE_DE_CAISSE:
                            html.append(
                                    rest.service.notification.template.Mail.buildClotureCaisse(categorieNotifications));
                            break;
                        case MVT_DE_CAISSE:
                            html.append(rest.service.notification.template.Mail.buildMvtCaisse(categorieNotifications));
                            break;
                        case ENTREE_EN_STOCK:
                            html.append(
                                    rest.service.notification.template.Mail.buildBonLivraison(categorieNotifications));
                            break;
                        case RETOUR_FOURNISSEUR:
                            html.append(rest.service.notification.template.Mail
                                    .buildRetourFournisseur(categorieNotifications));
                            break;
                        case SAISIS_PERIMES:
                            html.append(rest.service.notification.template.Mail.buildPerimes(categorieNotifications));
                            break;
                        case QUANTITE_UG:
                            // html.append(rest.service.notification.template.Mail.buildCreationProduit(categorieNotifications));
                            break;
                        case AJOUT_DE_NOUVEAU_PRODUIT:
                            html.append(rest.service.notification.template.Mail
                                    .buildCreationProduit(categorieNotifications));
                            break;
                        case MODIFICATION_VENTE:
                            html.append(rest.service.notification.template.Mail.buildVente(categorieNotifications));
                            break;
                        default:
                            break;
                        }
                    });

            html.append(rest.service.notification.template.Mail.endTag());
            sendMail(html.toString());
        }

    }

    @Asynchronous
    @Override
    public void sendMail(Notification notification) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    // Statut.NOT_SEND

    private List<Notification> findByStatutAndCanal(Set<Statut> statut, Set<Canal> canaux) {
        try {
            TypedQuery<Notification> q = em.createNamedQuery("Notification.findAllByCreatedAtAndStatusAndCanaux",
                    Notification.class);
            q.setParameter("statut", statut);
            q.setParameter("canaux", canaux);
            q.setParameter("createdAt", LocalDateTime.of(LocalDate.of(2024, Month.MAY, 3), LocalTime.of(6, 10)));
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private boolean sendMail(String content) {
        try {
            Session session = Session.getInstance(rest.service.notification.template.Mail.getEmailProperties());
            SmsParameters sp = rest.service.notification.template.Mail.sp;

            MimeMessage msg = new MimeMessage(session);
            List<Address> listadd = new ArrayList<>();

            var email = sp.mailOfficine;

            String[] emails = email.split(";");
            for (String email1 : emails) {
                listadd.add(new InternetAddress(email1));
            }

            Address[] recipient = new InternetAddress[listadd.size()];
            recipient = listadd.toArray(recipient);
            Address sender = new InternetAddress(sp.email);
            msg.setContent(content, "text/html; charset=utf-8");
            msg.setFrom(sender);
            msg.setRecipients(Message.RecipientType.TO, recipient);
            msg.setSubject("Resumé activité prestige 2");
            Transport.send(msg, sp.email, sp.password);
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return false;
    }
}
