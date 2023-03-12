/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import bll.common.Parameter;
import commonTasks.dto.FlagDTO;
import dal.Flag;
import dal.MvtTransaction;
import dal.TPreenregistrement;
import dal.TPreenregistrement_;
import dal.TReglement;
import dal.TUser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import util.DateConverter;

/**
 *
 * @author koben
 */
@Stateless
public class FlagService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    public List<FlagDTO> listFlags() {
        try {
            TypedQuery<Flag> q = getEntityManager().createNamedQuery("Flag.findAll", Flag.class);
            return q.getResultList().stream().map(FlagDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Pair<Boolean, Flag> updateFlag(int finalPrice, String dt_start, String dt_end) {
        LocalDate dtSt = LocalDate.parse(dt_start);
        LocalDate dtEnd = LocalDate.parse(dt_end);
        List<String> errors = new ArrayList<>();
        StringJoiner joiner = new StringJoiner(", ");
        dtSt.datesUntil(dtEnd).forEach(d -> {
            joiner.add(d.toString());
            if (!verificationDate(d)) {
                errors.add(d.toString());
            }
        });
        joiner.add(dtEnd.toString());
        if (!verificationDate(dtEnd)) {
            errors.add(dtEnd.toString());
        }
        if (!errors.isEmpty()) {
            return Pair.of(true, null);
        }
        Flag flag = new Flag();
        flag.setInterval(joiner.toString());
        flag.setMontant(finalPrice);
        flag.setId(dtSt.format(DateTimeFormatter.ofPattern("yyyyMMdd")).concat(dtEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        flag.setDateStart(Integer.valueOf(dtSt.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        flag.setDateEnd(Integer.valueOf(dtEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        return Pair.of(false, flag);
    }

    private boolean verificationDate(LocalDate dtSt) {
        TypedQuery<Flag> q = getEntityManager().createNamedQuery("Flag.checkDate", Flag.class);
        q.setParameter(1, "%" + dtSt.toString() + "%");
        return q.getResultList().isEmpty();

    }

    private List<TPreenregistrement> getTtVente(String dt_start, String dt_end, String lgEmp, int start, int max) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);
            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            Join<TPreenregistrement, TUser> pu = root.join("lgUSERID", JoinType.INNER);
            Join<TPreenregistrement, TReglement> pr = root.join("lgREGLEMENTID", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(pu.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), lgEmp));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
            predicate = cb.and(predicate, cb.notLike(root.get("lgTYPEVENTEID").get("lgTYPEVENTEID"), Parameter.VENTE_DEPOT_EXTENSION));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.strTYPEVENTE), DateConverter.VENTE_COMPTANT));
            predicate = cb.and(predicate, cb.equal(root.get(TPreenregistrement_.intPRICEREMISE), 0));
            predicate = cb.and(predicate, cb.equal(pr.get("lgMODEREGLEMENTID").get("lgMODEREGLEMENTID"), DateConverter.MODE_ESP));
            Predicate ge = cb.greaterThan(root.get(TPreenregistrement_.intPRICE), 0);
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dt_start), java.sql.Date.valueOf(dt_end));
            cq.select(root).orderBy(cb.desc(root.get(TPreenregistrement_.intPRICE)));
            cq.where(predicate, btw, ge);
            Query q = em.createQuery(cq);
            q.setFirstResult(start);
            q.setMaxResults(max);
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public JSONObject saveFlag(String dt_start, String dt_end, String lgEmp, int virtualAmount) {
        JSONObject json = new JSONObject();
        Pair<Boolean, Flag> pair = updateFlag(virtualAmount, dt_start, dt_end);
        if (pair.getLeft()) {
            json.put("success", 0);
            json.put("msg", " Certaines dates sont incluses dans traitements déjà effectués ");
            json.put("nb", 0 + " ventes impactées ");
            return json;
        }
        Flag flag = pair.getRight();
        getEntityManager().persist(flag);
        int i = 0;
        int start = 0;
        int max = 1000;
        while (virtualAmount > 0) {
            List<TPreenregistrement> list = getTtVente(dt_start, dt_end, lgEmp, start, max);
            if (list.isEmpty()) {
                break;
            }
            try {
                for (TPreenregistrement tPreenregistrement : list) {
                    Integer finalPrice;
                    Integer Net = tPreenregistrement.getIntPRICE();
                    Integer newPrice ;
                    int netPercent = (virtualAmount * 100) / Net;
                    if (netPercent >= 100) {
                        newPrice = (Net * 35) / 100;
                    } else if (netPercent > 6) {
                        newPrice = (Net * 30) / 100;
                    }else{
                        continue;
                    }
                    if (virtualAmount > newPrice) {
                        virtualAmount -= newPrice;
                        finalPrice = newPrice;
                    } else {
                        finalPrice = virtualAmount;
                        virtualAmount = 0;
                    }

                    tPreenregistrement.setIntACCOUNT(finalPrice);
                    tPreenregistrement.setIntPRICEOTHER(finalPrice);
                    MvtTransaction mt = findByVenteId(tPreenregistrement.getLgPREENREGISTREMENTID());
                    mt.setMontantAcc(finalPrice);
                    mt.setFlaged(Boolean.TRUE);
                    mt.setFlag(flag);
                    getEntityManager().merge(mt);
                    getEntityManager().merge(tPreenregistrement);
                    i++;
                    if (virtualAmount == 0) {
                        break;
                    }
                }
                start += max;
//                flag.setMontant(flag.getMontant() - virtualAmount);
//                getEntityManager().merge(flag);
                json.put("success", 1);
                json.put("nb", i + " ventes  impactées ");
                 if (virtualAmount == 0) {
                        break;
                    }
            } catch (Exception e) {
                e.printStackTrace(System.err);
                json.put("success", 0);
                json.put("nb", 0 + " ventes impactées ");
                json.put("msg", " Le traitement a échoué ");
                getEntityManager().remove(flag);
            }

        }
        return json;
    }

    public MvtTransaction findByVenteId(String venteId) {
        try {
            TypedQuery<MvtTransaction> q = getEntityManager().createQuery("SELECT o FROM  MvtTransaction o WHERE o.pkey=?1", MvtTransaction.class);
            q.setMaxResults(1);
            q.setParameter(1, venteId);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private List<MvtTransaction> findByFlagId(String id) {
        try {
            TypedQuery<MvtTransaction> q = getEntityManager().createQuery("SELECT o FROM  MvtTransaction o WHERE o.flag.id= ?1", MvtTransaction.class);
            q.setParameter(1, id);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public void upadte(String id) {
        findByFlagId(id).forEach(e -> {
            e.setFlaged(Boolean.FALSE);
            e.setMontantAcc(0);
            e.setFlag(null);
            getEntityManager().merge(e);
        });
        getEntityManager().remove(getEntityManager().find(Flag.class, id));
    }
}
