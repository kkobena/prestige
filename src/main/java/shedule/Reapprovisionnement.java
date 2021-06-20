/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shedule;

import dal.TCalendrier;
import dal.TFamille;
import dal.TParameters;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TPreenregistrementDetail_;
import dal.TPreenregistrement_;
import dal.TTypeVente_;
import dal.TUser_;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.json.JSONObject;
import util.DateConverter;

/**
 *
 * @author kkoffi
 */
@Singleton
@Startup
@TransactionManagement(value = TransactionManagementType.BEAN)
public class Reapprovisionnement {

//    private static final Logger LOG = Logger.getLogger(Reapprovisionnement.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @Inject
    private UserTransaction userTransaction;

    public List<TFamille> loadArticle(int start, int limit) {
        TypedQuery<TFamille> q = em.createQuery("SELECT o FROM TFamille o WHERE o.strSTATUT='enable' AND o.boolDECONDITIONNE=?1 ", TFamille.class);
        q.setParameter(1, (short) 0);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    public List<TFamille> loadArticle(String id) {
        TypedQuery<TFamille> q = em.createQuery("SELECT o FROM TFamille o WHERE o.lgFAMILLEID=?1 ", TFamille.class);
        q.setParameter(1, id);
        return q.getResultList();
    }

    public List<TFamille> loadArticleDeconditionne(String parentId) {
        try {
            TypedQuery<TFamille> q = em.createQuery("SELECT o FROM TFamille o WHERE o.strSTATUT='enable' AND o.lgFAMILLEPARENTID=?1 ", TFamille.class);
            q.setParameter(1, parentId);
            return q.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Integer deconditionneConsommation(LocalDate dtSart, LocalDate dtEnd, String parentId, double qteDetail) {
        try {
            List<TFamille> l = loadArticleDeconditionne(parentId);
            LongAdder adder = new LongAdder();
            l.stream().forEach(f -> {
                int conso = consommationProduits(dtSart, dtEnd, f);
                adder.add(conso);

            });

            int qte = adder.intValue();
            if (qte > 0) {
                return (int) Math.ceil(qteDetail / adder.intValue());
            }
            return 0;

        } catch (Exception e) {
            return 0;
        }
    }

    public long loadArticleCount() {
        Query q = em.createQuery("SELECT COUNT(o) FROM TFamille o WHERE o.strSTATUT='enable' AND o.boolDECONDITIONNE=?1 ");
        q.setParameter(1, (short) 0);
        return (long) q.getSingleResult();
    }

    public TParameters findParameters(String key) {
        try {
            TParameters tp = em.find(TParameters.class, key);
            return tp;
        } catch (Exception e) {
            return null;
        }
    }

    public List<LocalDate> nombreMoisPleinsConsommation(int nombre) {
        LocalDate now = LocalDate.now();
        List<LocalDate> nombreMois = new ArrayList<>();
        try {
            for (int i = 1; i <= nombre; i++) {
                TypedQuery<TCalendrier> tq = em.createQuery("SELECT o FROM TCalendrier o WHERE   FUNCTION('MONTH', o.dtDay) =?1 AND  FUNCTION('YEAR', o.dtDay)=?2 AND o.intNUMBERJOUR >=20 ", TCalendrier.class);
                tq.setParameter(1, now.minusMonths(i).getMonthValue());
                tq.setParameter(2, now.minusMonths(i).getYear());
                tq.setMaxResults(1);
                TCalendrier calendrier = tq.getSingleResult();
                if (calendrier != null) {
                    nombreMois.add(LocalDate.of(calendrier.getDtDay().getYear(), calendrier.getDtDay().getMonth(), 1));
                }
            }

        } catch (Exception e) {
//            e.printStackTrace(System.err);
        }
        return nombreMois;
    }

    public Integer consommationProduits(LocalDate dtSart, LocalDate dtEnd, TFamille tf) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.sumAsLong(root.get(TPreenregistrementDetail_.intQUANTITY))).groupBy(root.get(TPreenregistrementDetail_.lgFAMILLEID));
            predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID), tf));
            Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(dtSart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            predicates.add(cb.equal(st.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.isFalse(st.get(TPreenregistrement_.bISCANCEL)));
            predicates.add(cb.greaterThan(st.get(TPreenregistrement_.intPRICE), 0));
            predicates.add(cb.notEqual(st.get(TPreenregistrement_.lgTYPEVENTEID).get(TTypeVente_.lgTYPEVENTEID), DateConverter.DEPOT_EXTENSION));
            predicates.add(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get("lgEMPLACEMENTID"), DateConverter.OFFICINE));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
//            e.printStackTrace(System.err);
            return 0;
        }
    }

    public void calculStockReappro() {
        int start = 0, limit = 10, Q1 = 4, Q2 = 2, Q3 = 3;
        long total = loadArticleCount();
        TParameters q1 = findParameters(DateConverter.Q1);
        if (q1 != null) {
            Q1 = Integer.valueOf(q1.getStrVALUE().trim());
        }
        TParameters q2 = findParameters(DateConverter.Q2);
        if (q2 != null) {
            Q2 = Integer.valueOf(q2.getStrVALUE().trim());
        }
        TParameters q3 = findParameters(DateConverter.Q3);
        if (q3 != null) {
            Q3 = Integer.valueOf(q3.getStrVALUE().trim());
        }

        List<LocalDate> nombreMois = nombreMoisPleinsConsommation(Q3).stream().sorted().collect(Collectors.toList());

        if (!nombreMois.isEmpty()) {
            JSONObject json;
            for (int i = start; i <= total; i += limit) {
                List<TFamille> list = loadArticle(i, limit);
                try {
                    userTransaction.begin();
                    for (TFamille tf : list) {
                        int conso = consommationProduits(nombreMois.get(0), LocalDate.of(nombreMois.get(nombreMois.size() - 1).getYear(), nombreMois.get(nombreMois.size() - 1).getMonth(), nombreMois.get(nombreMois.size() - 1).lengthOfMonth()), tf);
                        int seuiCalule = 0;
                        int qteCalule = 0;

                        if (conso > 0) {
                            if (tf.getBoolDECONDITIONNEEXIST() == 1) {
                                int consonDetail = deconditionneConsommation(nombreMois.get(0), LocalDate.of(nombreMois.get(nombreMois.size() - 1).getYear(), nombreMois.get(nombreMois.size() - 1).getMonth(), nombreMois.get(nombreMois.size() - 1).lengthOfMonth()), tf.getLgFAMILLEID(), Double.valueOf(tf.getIntNUMBERDETAIL()));
                                if (consonDetail > 0) {
                                    conso += consonDetail;
                                }

                            }
                            json = DateConverter.calculSeuiQteReappro(Q1, Q2, conso, Q3);
                            seuiCalule = json.getInt("seuilReappro");
                            qteCalule = json.getInt("qteReappro");
                        }
                        tf.setIntSEUILMIN(seuiCalule);
                        tf.setIntSTOCKREAPROVISONEMENT(seuiCalule);
                        tf.setIntQTEREAPPROVISIONNEMENT(qteCalule);
                        em.merge(tf);
                    }
                    userTransaction.commit();

                } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                    try {
                        if (userTransaction.getStatus() == Status.STATUS_ACTIVE
                                || userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                            userTransaction.rollback();
                        }
                    } catch (SystemException ex1) {
                        Logger.getLogger(Reapprovisionnement.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }
    }

//    @PostConstruct
    public void init() {
        try {
            TParameters semois = em.find(TParameters.class, "SEMOIS");
            if (semois != null && Integer.valueOf(semois.getStrVALUE()) == 1) {
                exec();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(System.err);
        }

    }

    public void exec() {
        try {
            TParameters p = em.find(TParameters.class, "KEY_DAY_SEUIL_REAPPRO");//derniere date de mise a jour stock reappro
            LocalDate date = LocalDate.parse(p.getStrVALUE());
            if (date.getMonthValue() != LocalDate.now().getMonthValue()) {
                calculStockReappro();
                userTransaction.begin();
                p.setStrVALUE(LocalDate.now().toString());
                p.setDtUPDATED(new Date());
                em.merge(p);
                userTransaction.commit();
            }
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            e.printStackTrace(System.err);
        }
    }
}
