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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
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

/**
 *
 * @author koben
 */
@Stateless
public class NotificationImpl implements NotificationService {

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
        CategorieNotification categorieNotification = this.getOneByName(notificationDto.getType());
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

}
