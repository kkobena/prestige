/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.LogDTO;
import dal.TEventLog;
import dal.TEventLog_;
import dal.TUser;
import dal.enumeration.TypeLog;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.LogService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author DICI
 */
@Stateless
public class LogServiceImpl implements LogService {

    private static final Logger LOG = Logger.getLogger(LogServiceImpl.class.getName());

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void updateLogFile(TUser user, String ref, String desc, TypeLog typeLog, Object T) {
        try {
            TEventLog eventLog = new TEventLog(UUID.randomUUID().toString());
            eventLog.setLgUSERID(user);
            eventLog.setDtCREATED(new Date());
            eventLog.setDtUPDATED(new Date());
            eventLog.setStrCREATEDBY(user.getStrLOGIN());
            eventLog.setStrSTATUT(commonparameter.statut_enable);
            eventLog.setStrTABLECONCERN(T.getClass().getName());
            eventLog.setTypeLog(typeLog);
            eventLog.setStrDESCRIPTION(desc + " référence [" + ref + " ]");
            getEntityManager().persist(eventLog);


        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
    }

    @Override
    public void updateItem(TUser user, String ref, String desc, TypeLog typeLog, Object t, EntityManager em) {
        TEventLog eventLog = new TEventLog(UUID.randomUUID().toString());
        eventLog.setLgUSERID(user);
        eventLog.setDtCREATED(new Date());
        eventLog.setDtUPDATED(eventLog.getDtCREATED());
        eventLog.setStrCREATEDBY(user.getStrLOGIN());
        eventLog.setStrSTATUT(commonparameter.statut_enable);
        eventLog.setStrTABLECONCERN(t.getClass().getName());
        eventLog.setTypeLog(typeLog);
        eventLog.setStrDESCRIPTION(desc + " référence [" + ref + " ]");
        eventLog.setStrTYPELOG(ref);
        em.persist(eventLog);
    }
    Comparator<LogDTO> comparatorOrder = Comparator.comparing(LogDTO::getOrder);
    Comparator<LogDTO> comparatorDate = Comparator.comparing(LogDTO::getOperationDate);

    @Override
    public JSONObject filtres(String query) throws JSONException {
        List<LogDTO> l = Stream.of(TypeLog.values()).map(x -> new LogDTO(x.ordinal(), x.getValue())).collect(Collectors.toList());
        if (query != null && !"".equals(query)) {
            List<LogDTO> _new = l.stream().filter(x -> x.getStrDESCRIPTION().startsWith(query)).sorted(comparatorOrder.reversed()).collect(Collectors.toList());
            _new.add(new LogDTO(-1, "TOUS"));

            return new JSONObject().put("total", _new.size()).put("data", new JSONArray(_new));
        }
        l.add(new LogDTO(-1, "TOUS"));
        l.sort(comparatorOrder.reversed());
        return new JSONObject().put("total", l.size()).put("data", new JSONArray(l));
    }

    public long logs(String search, LocalDate dtStart, LocalDate dtEnd, String userId, int criteria) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TEventLog> root = cq.from(TEventLog.class);
            cq.select(cb.count(root));
            Predicate btw = cb.between(
                    cb.function("DATE", Date.class, root.get(TEventLog_.dtCREATED)),
                    java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (search != null && !"".equals(search)) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TEventLog_.strDESCRIPTION), search + "%"), cb.like(root.get(TEventLog_.strTYPELOG), search + "%")));
                predicates.add(predicate);
            }
            if (criteria > 0) {
                predicates.add(cb.equal(root.get(TEventLog_.typeLog), TypeLog.values()[criteria]));
            }
            if(!StringUtils.isEmpty(userId)){
                 predicates.add(cb.equal(root.get(TEventLog_.lgUSERID).get("lgUSERID"), userId));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = getEntityManager().createQuery(cq);
            return (long) q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public List<LogDTO> logs(String search, LocalDate dtStart, LocalDate dtEnd, int start, int limit, boolean all, String userId, int criteria) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TEventLog> cq = cb.createQuery(TEventLog.class);
            Root<TEventLog> root = cq.from(TEventLog.class);
            cq.select(root).orderBy(cb.desc(root.get(TEventLog_.dtCREATED)));
            Predicate btw = cb.between(
                    cb.function("DATE", Date.class, root.get(TEventLog_.dtCREATED)),
                    java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(cb.and(btw));
            if (search != null && !"".equals(search)) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TEventLog_.strDESCRIPTION), search + "%"), cb.like(root.get(TEventLog_.strTYPELOG), search + "%")));
                predicates.add(predicate);
            }
            if (criteria > 0) {
                predicates.add(cb.equal(root.get(TEventLog_.typeLog), TypeLog.values()[criteria]));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<TEventLog> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            return q.getResultList().stream().map(LogDTO::new).sorted(comparatorDate.reversed()).collect(Collectors.toList());
        } catch (Exception e) {
             LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject logs(String query, LocalDate dtStart, LocalDate dtEnd, int start, int limit, String userId, int criteria) throws JSONException {
        long count = logs(query, dtStart, dtEnd, userId, criteria);
        if (count == 0) {
            return new JSONObject().put("total", count).put("data", new JSONArray());
        }
        List<LogDTO> l = logs(query, dtStart, dtEnd, start, limit, false, userId, criteria);
        return new JSONObject().put("total", count).put("data", new JSONArray(l));

    }
       @Override
 public void updateItem(TUser user, String ref, String desc, TypeLog typeLog, Object T, Date date) {
        TEventLog eventLog = new TEventLog(UUID.randomUUID().toString());
        eventLog.setLgUSERID(user);
        eventLog.setDtCREATED(date);
        eventLog.setDtUPDATED(date);
        eventLog.setStrCREATEDBY(user.getStrLOGIN());
        eventLog.setStrSTATUT(commonparameter.statut_enable);
        eventLog.setStrTABLECONCERN(T.getClass().getName());
        eventLog.setTypeLog(typeLog);
        eventLog.setStrDESCRIPTION(desc + " référence [" + ref + " ]");
        eventLog.setStrTYPELOG(ref);
        getEntityManager().persist(eventLog);
    }
 
   @Override
    public void updateLogFile(TUser user, String ref, String desc, TypeLog typeLog, Object T,String remoteHost,String remoteAddr) {
        try {
            TEventLog eventLog = new TEventLog(UUID.randomUUID().toString());
            eventLog.setLgUSERID(user);
            eventLog.setDtCREATED(new Date());
            eventLog.setDtUPDATED(new Date());
            eventLog.setStrCREATEDBY(user.getStrLOGIN());
            eventLog.setStrSTATUT(commonparameter.statut_enable);
            eventLog.setStrTABLECONCERN(T.getClass().getName());
            eventLog.setTypeLog(typeLog);
            eventLog.setStrDESCRIPTION(desc + " référence [" + ref + " ]");
            eventLog.setRemoteAddr(remoteAddr);
            eventLog.setRemoteHost(remoteHost);
            getEntityManager().persist(eventLog);


        } catch (Exception e) {
            e.printStackTrace(System.err);

        }
    }

}
