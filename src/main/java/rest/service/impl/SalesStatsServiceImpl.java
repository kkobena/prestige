/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import bll.common.Parameter;
import commonTasks.dto.AyantDroitDTO;
import commonTasks.dto.ClientDTO;
import commonTasks.dto.ClotureVenteParams;
import commonTasks.dto.MagasinDTO;
import commonTasks.dto.Params;
import commonTasks.dto.SalesStatsParams;
import commonTasks.dto.TicketDTO;
import commonTasks.dto.TiersPayantParams;
import commonTasks.dto.TvaDTO;
import commonTasks.dto.VenteDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.HMvtProduit;
import dal.Medecin_;
import dal.MvtTransaction;
import dal.MvtTransaction_;
import dal.TAyantDroit;
import dal.TClient;
import dal.TClient_;
import dal.TCompteClientTiersPayant;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamille_;
import dal.TGroupeTierspayant;
import dal.TParameters;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClient;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementCompteClientTiersPayent_;
import dal.TPreenregistrementDetail;
import dal.TPreenregistrementDetail_;
import dal.TPreenregistrement_;
import dal.TReglement;
import dal.TTiersPayant;
import dal.TTypeReglement_;
import dal.TUser_;
import dal.enumeration.TypeTransaction;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.CaisseService;
import rest.service.SalesStatsService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author Kobena
 */
@Stateless
public class SalesStatsServiceImpl implements SalesStatsService {

    private static final Logger LOG = Logger.getLogger(SalesStatsServiceImpl.class.getName());

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    CaisseService caisseService;

    public EntityManager getEntityManager() {
        return em;
    }

    private TEmplacement findEmplacementById(String id) {
        return getEntityManager().find(TEmplacement.class, id);
    }

    private TPreenregistrement findById(String id) {
        TPreenregistrement tp = getEntityManager().find(TPreenregistrement.class, id);
        getEntityManager().refresh(tp);
        return tp;
    }

    List<Predicate> predicatesVentesAnnulees(SalesStatsParams params, CriteriaBuilder cb, Root<TPreenregistrementDetail> root, Join<TPreenregistrementDetail, TPreenregistrement> st) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.and(cb.isTrue(st.get(TPreenregistrement_.bISCANCEL))));
        Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TPreenregistrement_.dtANNULER)), java.sql.Date.valueOf(params.getDtStart()),
                java.sql.Date.valueOf(params.getDtEnd()));
        predicates.add(cb.and(btw));

        predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.strSTATUT), params.getStatut())));

        if (params.getQuery() != null && !"".equals(params.getQuery())) {
            Predicate predicate = cb.and(cb.or(cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREFTICKET), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREF), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
            predicates.add(predicate);
        }
        if (!params.isShowAll()) {
            predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
        }
        if (!params.isShowAllActivities()) {
            TEmplacement te = params.getUserId().getLgEMPLACEMENTID();
            predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get("lgEMPLACEMENTID"), te.getLgEMPLACEMENTID())));
        }
        return predicates;
    }

    @Override
    public long montantVenteAnnulees(SalesStatsParams params) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cm = cb.createQuery(Long.class);
            Root<MvtTransaction> mv = cm.from(MvtTransaction.class);
            cm.select(cb.sumAsLong(mv.get(MvtTransaction_.montantPaye)));
            Subquery<String> cq = cm.subquery(String.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(st.get(TPreenregistrement_.lgPREENREGISTREMENTID));
            List<Predicate> predicates = predicatesVentesAnnulees(params, cb, root, st);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            cm.where(cb.and(cb.greaterThan(mv.get(MvtTransaction_.montantPaye), 0), cb.equal(mv.get(MvtTransaction_.reglement).get(TTypeReglement_.lgTYPEREGLEMENTID), DateConverter.MODE_ESP), cb.in(mv.get(MvtTransaction_.pkey)).value(cq)));
            Query q = getEntityManager().createQuery(cm);
            Long sumAnnulation = (Long) q.getSingleResult();
            return (sumAnnulation != null ? sumAnnulation : 0);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }

    }

    @Override
    public JSONObject annulations(SalesStatsParams params) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            long count = countListeAnnulations(params);
            if (count == 0) {
                json.put("total", count);
                json.put("data", new JSONArray());
                return json;
            }

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)).distinct(true).orderBy(cb.asc(st.get(TPreenregistrement_.dtUPDATED)));
            List<Predicate> predicates = predicatesVentesAnnulees(params, cb, root, st);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            if (!params.isAll()) {
                q.setFirstResult(params.getStart());
                q.setMaxResults(params.getLimit());
            }
            List<TPreenregistrement> list = q.getResultList();
            List<VenteDTO> data = list.stream().map(v -> new VenteDTO(findById(v.getLgPREENREGISTREMENTID()), findByParent(v.getLgPREENREGISTREMENTID()))).collect(Collectors.toList());

            json.put("total", count);
            json.put("data", new JSONArray(data)).put("metaData", montantVenteAnnulees(params));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }

    private MvtTransaction findByVente(String id) {
        try {
            TypedQuery<MvtTransaction> q = getEntityManager().createQuery("SELECT o FROM MvtTransaction o WHERE o.pkey=?1 ", MvtTransaction.class);
            q.setParameter(1, id);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public List<VenteDTO> annulationVente(SalesStatsParams params) {
        try {

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)).distinct(true).orderBy(cb.asc(st.get(TPreenregistrement_.dtUPDATED)));
            List<Predicate> predicates = predicatesVentesAnnulees(params, cb, root, st);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);

            List<TPreenregistrement> list = q.getResultList();
            List<VenteDTO> data = list.stream().map(v -> new VenteDTO(findById(v.getLgPREENREGISTREMENTID()), findByParent(v.getLgPREENREGISTREMENTID()), findByVente(v.getLgPREENREGISTREMENTID()))).collect(Collectors.toList());
            return data;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public long countListeAnnulations(SalesStatsParams params) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(cb.countDistinct(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)));
            List<Predicate> predicates = predicatesVentesAnnulees(params, cb, root, st);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public JSONObject getListeTPreenregistrement(SalesStatsParams params) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            long count = countListeTPreenregistrement(params);
            if (count == 0) {
                json.put("total", count);
                json.put("data", new JSONArray());
                return json;
            }
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)).distinct(true).orderBy(cb.asc(st.get(TPreenregistrement_.dtUPDATED)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(params.getDtStart()),
                    java.sql.Date.valueOf(params.getDtEnd()));
            predicates.add(cb.and(btw));
            if (params.isDepotOnly()) {
                predicates.add(cb.and(cb.notEqual(st.get(TPreenregistrement_.pkBrand), "")));
            } else {
                predicates.add(cb.and(cb.notEqual(st.get(TPreenregistrement_.lgNATUREVENTEID).get("lgNATUREVENTEID"), Parameter.KEY_NATURE_VENTE_DEPOT)));
            }
            predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.strSTATUT), params.getStatut())));

            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREF), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            if (params.getTypeVenteId() != null && !"".equals(params.getTypeVenteId())) {
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.strTYPEVENTE), params.getTypeVenteId())));
            }
            if (!params.isShowAll()) {
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
            }
            if (!params.isShowAllActivities()) {
                TEmplacement te = params.getUserId().getLgEMPLACEMENTID();
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get("lgEMPLACEMENTID"), te.getLgEMPLACEMENTID())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            if (!params.isAll()) {
                q.setFirstResult(params.getStart());
                q.setMaxResults(params.getLimit());
            }
            List<TPreenregistrement> list = q.getResultList();
            List<VenteDTO> data;
            if (!params.isDepotOnly()) {
                data = list.stream().map(v -> new VenteDTO(findById(v.getLgPREENREGISTREMENTID()), (v.getStrTYPEVENTE().equals("VO") ? findClientTiersPayents(v.getLgPREENREGISTREMENTID()).stream().map(TiersPayantParams::new).collect(Collectors.toList()) : Collections.EMPTY_LIST), (v.getAyantDroit() != null ? new AyantDroitDTO(v.getAyantDroit()) : null), (v.getClient() != null ? new ClientDTO(v.getClient()) : null), findByParent(v.getLgPREENREGISTREMENTID()))).collect(Collectors.toList());
            } else {
                data = list.stream().map(v -> new VenteDTO(findById(v.getLgPREENREGISTREMENTID()), new MagasinDTO(findEmplacementById(v.getPkBrand())), findByParent(v.getLgPREENREGISTREMENTID()))).collect(Collectors.toList());
            }
            json.put("total", count);
            json.put("data", new JSONArray(data));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }

    @Override
    public long countListeTPreenregistrement(SalesStatsParams params) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(cb.countDistinct(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TPreenregistrement_.dtUPDATED)), java.sql.Date.valueOf(params.getDtStart()),
                    java.sql.Date.valueOf(params.getDtEnd()));
            predicates.add(cb.and(btw));
            if (params.isDepotOnly()) {
                predicates.add(cb.and(cb.notEqual(st.get(TPreenregistrement_.pkBrand), "")));
            } else {
                predicates.add(cb.and(cb.notEqual(st.get(TPreenregistrement_.lgNATUREVENTEID).get("lgNATUREVENTEID"), Parameter.KEY_NATURE_VENTE_DEPOT)));
            }
            predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.strSTATUT), params.getStatut())));

            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREF), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            if (params.getTypeVenteId() != null && !"".equals(params.getTypeVenteId())) {
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.strTYPEVENTE), params.getTypeVenteId())));
            }
            if (!params.isShowAll()) {
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
            }
            if (!params.isShowAllActivities()) {
                TEmplacement te = params.getUserId().getLgEMPLACEMENTID();
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get("lgEMPLACEMENTID"), te.getLgEMPLACEMENTID())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }

    }

    private List<TPreenregistrementCompteClientTiersPayent> findClientTiersPayents(String idVente) {
        return getEntityManager().createQuery("SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 ").setParameter(1, idVente).getResultList();
    }

    private List<TPreenregistrementDetail> findByParent(String idVente) {
        return getEntityManager().createQuery("SELECT o FROM TPreenregistrementDetail o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 ").setParameter(1, idVente).getResultList();
    }

    public void deleteItemsBulk(String venteId) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaDelete<TPreenregistrementDetail> cq = cb.createCriteriaDelete(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            cq.where(cb.equal(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID).get("lgPREENREGISTREMENTID"), venteId));
            getEntityManager().createQuery(cq).executeUpdate();
        } catch (Exception e) {
        }
    }

    public void updateItemsBulk(String venteId, String statut) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaUpdate<TPreenregistrementDetail> cq = cb.createCriteriaUpdate(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            cq.set(root.get(TPreenregistrementDetail_.strSTATUT).get("strSTATUT"), statut);
            cq.where(cb.equal(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID).get("lgPREENREGISTREMENTID"), venteId));
            getEntityManager().createQuery(cq).executeUpdate();
        } catch (Exception e) {
        }
    }

    public void deleteCompteClientBulk(String venteId) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaDelete<TPreenregistrementCompteClientTiersPayent> cq = cb.createCriteriaDelete(TPreenregistrementCompteClientTiersPayent.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            cq.where(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID).get("lgPREENREGISTREMENTID"), venteId));
            getEntityManager().createQuery(cq).executeUpdate();
        } catch (Exception e) {
        }
    }

    public void updateCompteClientBulk(String venteId, String statut) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaUpdate<TPreenregistrementCompteClientTiersPayent> cq = cb.createCriteriaUpdate(TPreenregistrementCompteClientTiersPayent.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            cq.set(root.get(TPreenregistrementCompteClientTiersPayent_.strSTATUT), statut);
            cq.where(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID).get("lgPREENREGISTREMENTID"), venteId));
            getEntityManager().createQuery(cq).executeUpdate();
        } catch (Exception e) {
        }
    }

    @Override
    public JSONObject delete(String venteId) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TPreenregistrement tp = getEntityManager().find(TPreenregistrement.class, venteId);
            LOG.log(Level.INFO, "{0} {1}", new Object[]{venteId, tp});
//            getEntityManager().getTransaction().begin();
            deleteItemsBulk(venteId);
            deleteCompteClientBulk(venteId);
            getEntityManager().remove(tp);
//            getEntityManager().getTransaction().commit();
            json.put("success", true);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
//            if (getEntityManager().getTransaction().isActive()) {
//                getEntityManager().getTransaction().rollback();
//            }
            json.put("success", false);
        }
        return json;
    }

    @Override
    public JSONObject trash(String venteId, String statut) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TPreenregistrement tp = getEntityManager().find(TPreenregistrement.class, venteId);
//            getEntityManager().getTransaction().begin();
            updateItemsBulk(venteId, statut);
            updateCompteClientBulk(venteId, statut);
            tp.setStrSTATUT(statut);
            getEntityManager().merge(tp);
//            getEntityManager().getTransaction().commit();
            json.put("success", true);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
//            if (getEntityManager().getTransaction().isActive()) {
//                getEntityManager().getTransaction().rollback();
//            }
            json.put("success", false);
        }
        return json;
    }
    Comparator<TiersPayantParams> comparator = Comparator.comparingInt(TiersPayantParams::getOrder);

    @Override
    public JSONObject findVenteById(String venteId) throws JSONException {
        JSONObject json = new JSONObject();
        TPreenregistrement p = findById(venteId);
        json.put("success", true).put("data", new JSONObject(new VenteDTO(p, new MagasinDTO(findEmplacementById(p.getPkBrand())))));
        return json;
    }

    private List<TiersPayantParams> findTiersPayantByClientId(String clientId) {
        try {
            TypedQuery<TCompteClientTiersPayant> query = getEntityManager().createQuery("SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1", TCompteClientTiersPayant.class);
            query.setParameter(1, clientId);
            return query.getResultList().stream().map(TiersPayantParams::new).sorted(comparator).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private List<AyantDroitDTO> findAyantDroitByClientId(String clientId) {
        try {
            TypedQuery<TAyantDroit> query = getEntityManager().createQuery("SELECT o FROM TAyantDroit o WHERE o.lgCLIENTID.lgCLIENTID=?1", TAyantDroit.class);
            query.setParameter(1, clientId);
            return query.getResultList().stream().map(AyantDroitDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject chargerClientLorsModificationVnete(String venteId) throws JSONException {
        JSONObject json = new JSONObject();
        TPreenregistrement p = findById(venteId);
        List<TiersPayantParams> venteTps = findClientTiersPayents(p.getLgPREENREGISTREMENTID()).
                stream().map(TiersPayantParams::new).collect(Collectors.toList());
        TClient cl = p.getClient();

        json.put("success", true).put("data", new JSONObject(new ClientDTO(cl, findTiersPayantByClientId(cl.getLgCLIENTID()), venteTps, findAyantDroitByClientId(cl.getLgCLIENTID()))));
        return json;
    }

    @Override
    public JSONObject reloadVenteById(String venteId) throws JSONException {
        JSONObject json = new JSONObject();
        TPreenregistrement p = findById(venteId);
        ClientDTO o = null;
        TClient cl = p.getClient();

        List<TiersPayantParams> venteTps = Collections.emptyList();
        if (p.getStrTYPEVENTE().equals("VO")) {
            venteTps = findClientTiersPayents(p.getLgPREENREGISTREMENTID()).
                    stream().map(TiersPayantParams::new).collect(Collectors.toList());
        }
        if (cl != null) {
            o = new ClientDTO(cl, findTiersPayantByClientId(cl.getLgCLIENTID()), venteTps, findAyantDroitByClientId(cl.getLgCLIENTID()));
        }
        VenteDTO data = new VenteDTO(p, venteTps,
                (p.getAyantDroit() != null ? new AyantDroitDTO(p.getAyantDroit()) : null),
                o);
        json.put("success", true).put("data", new JSONObject(data));
        return json;
    }

    TPreenregistrementCompteClient findPreenregistrementCompteClient(String id) {
        try {
            TypedQuery<TPreenregistrementCompteClient> q = this.getEntityManager().createQuery("SELECT OBJECT(o) FROM TPreenregistrementCompteClient o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1  ", TPreenregistrementCompteClient.class);
            q.setParameter(1, id);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject listeVentes(SalesStatsParams params) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            long count = countListeVentes(params);
            if (count == 0) {
                json.put("total", count);
                json.put("data", new JSONArray());
                return json;
            }
            List<VenteDTO> datas = listVentes(params);
            json.put("total", count);
            json.put("data", new JSONArray(datas));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }

    private boolean findpermission() {
        try {
            TParameters parameters = getEntityManager().find(TParameters.class, "KEY_EXPORT_VENTE_AS_STOCK");
            return Integer.valueOf(parameters.getStrVALUE().trim()) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    private List<VenteDTO> listVentes(SalesStatsParams params) {
        boolean canexport = findpermission();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)).distinct(true).orderBy(cb.asc(st.get(TPreenregistrement_.dtUPDATED)));
            if (params.isSansBon()) {
                predicates.add(cb.and(cb.isTrue(st.get(TPreenregistrement_.bWITHOUTBON))));
                predicates.add(cb.and(cb.isFalse(st.get(TPreenregistrement_.bISCANCEL))));
            }
            if (params.isOnlyAvoir()) {
                predicates.add(cb.and(cb.isTrue(st.get(TPreenregistrement_.bISAVOIR))));
                predicates.add(cb.and(cb.isFalse(st.get(TPreenregistrement_.bISCANCEL))));
            }
            Predicate btw = cb.between(cb.function("TIMESTAMP", Timestamp.class, st.get(TPreenregistrement_.dtUPDATED)), java.sql.Timestamp.valueOf(LocalDateTime.parse(params.getDtStart().toString() + " " + params.gethStart().toString().concat(":00"), formatter)),
                    java.sql.Timestamp.valueOf(LocalDateTime.parse(params.getDtEnd().toString() + " " + params.gethEnd().toString().concat(":59"), formatter)));
            predicates.add(btw);
            predicates.add(cb.equal(st.get(TPreenregistrement_.strSTATUT), commonparameter.statut_is_Closed));
            if (params.getTypeVenteId() != null && !"".equals(params.getTypeVenteId())) {
                predicates.add(cb.equal(st.get(TPreenregistrement_.strTYPEVENTE), params.getTypeVenteId()));
            }
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREFTICKET), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREF), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            if (!params.isShowAll()) {
                predicates.add(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID()));
            }
            if (!params.isShowAllActivities()) {
                TEmplacement te = params.getUserId().getLgEMPLACEMENTID();
                predicates.add(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get("lgEMPLACEMENTID"), te.getLgEMPLACEMENTID()));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            if (!params.isAll()) {
                q.setFirstResult(params.getStart());
                q.setMaxResults(params.getLimit());
            }
            List<TPreenregistrement> list = q.getResultList();
            return list.stream().map(v -> new VenteDTO(findById(v.getLgPREENREGISTREMENTID()), findByParent(v.getLgPREENREGISTREMENTID()), params.isCanCancel(), params, findPreenregistrementCompteClient(v.getLgPREENREGISTREMENTID()))
                    .canexport(canexport)
            ).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<VenteDTO> listeVentesReport(SalesStatsParams params) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)).distinct(true).orderBy(cb.asc(st.get(TPreenregistrement_.dtUPDATED)));
            if (params.isSansBon()) {
                predicates.add(cb.and(cb.isTrue(st.get(TPreenregistrement_.bWITHOUTBON))));
                predicates.add(cb.and(cb.isFalse(st.get(TPreenregistrement_.bISCANCEL))));
            }
            if (params.isOnlyAvoir()) {
                predicates.add(cb.and(cb.isTrue(st.get(TPreenregistrement_.bISAVOIR))));
                predicates.add(cb.and(cb.isFalse(st.get(TPreenregistrement_.bISCANCEL))));
            }
            Predicate btw = cb.between(cb.function("TIMESTAMP", Timestamp.class, st.get(TPreenregistrement_.dtUPDATED)), java.sql.Timestamp.valueOf(LocalDateTime.parse(params.getDtStart().toString() + " " + params.gethStart().toString().concat(":00"), formatter)),
                    java.sql.Timestamp.valueOf(LocalDateTime.parse(params.getDtEnd().toString() + " " + params.gethEnd().toString().concat(":59"), formatter)));
            predicates.add(btw);
            predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.strSTATUT), commonparameter.statut_is_Closed)));
            if (params.getTypeVenteId() != null && !"".equals(params.getTypeVenteId())) {
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.strTYPEVENTE), params.getTypeVenteId())));
            }
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREFTICKET), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREF), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            if (!params.isShowAll()) {
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
            }
            if (!params.isShowAllActivities()) {
                TEmplacement te = params.getUserId().getLgEMPLACEMENTID();
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get("lgEMPLACEMENTID"), te.getLgEMPLACEMENTID())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            if (!params.isAll()) {
                q.setFirstResult(params.getStart());
                q.setMaxResults(params.getLimit());
            }
            List<TPreenregistrement> list = q.getResultList();
            return list.stream().map(v -> new VenteDTO().buildAvoirs(findById(v.getLgPREENREGISTREMENTID()), findByParent(v.getLgPREENREGISTREMENTID()).stream().map(VenteDetailsDTO::new).collect(Collectors.toList()))).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public long countListeVentes(SalesStatsParams params) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> st = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            cq.select(cb.countDistinct(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)));
            if (params.isSansBon()) {
                predicates.add(cb.and(cb.isTrue(st.get(TPreenregistrement_.bWITHOUTBON))));
            }
            if (params.isOnlyAvoir()) {
                predicates.add(cb.and(cb.isTrue(st.get(TPreenregistrement_.bISAVOIR))));
            }

            Predicate btw = cb.between(cb.function("TIMESTAMP", Timestamp.class, st.get(TPreenregistrement_.dtUPDATED)), java.sql.Timestamp.valueOf(LocalDateTime.parse(params.getDtStart().toString() + " " + params.gethStart().toString().concat(":00"), formatter)),
                    java.sql.Timestamp.valueOf(LocalDateTime.parse(params.getDtEnd().toString() + " " + params.gethEnd().toString().concat(":59"), formatter)));
            predicates.add(btw);

            predicates.add(cb.equal(st.get(TPreenregistrement_.strSTATUT), commonparameter.statut_is_Closed));

            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREFTICKET), params.getQuery() + "%"), cb.like(st.get(TPreenregistrement_.strREF), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            if (params.getTypeVenteId() != null && !"".equals(params.getTypeVenteId())) {
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.strTYPEVENTE), params.getTypeVenteId())));
            }
            if (!params.isShowAll()) {
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
            }
            if (!params.isShowAllActivities()) {
                TEmplacement te = params.getUserId().getLgEMPLACEMENTID();
                predicates.add(cb.and(cb.equal(st.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get("lgEMPLACEMENTID"), te.getLgEMPLACEMENTID())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public JSONObject tvasViewData(Params params) throws JSONException {
        JSONObject json = new JSONObject();
        List<TvaDTO> datas = tvasRapport(params);
        json.put("total", datas.size());
        json.put("data", new JSONArray(datas));
        return json;
    }

    private List<HMvtProduit> donneesTvas(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId) {
        try {
            TypedQuery<HMvtProduit> query = getEntityManager().createQuery(
                    "SELECT o FROM HMvtProduit o WHERE o.mvtDate BETWEEN :dtStart AND :dtEnd AND o.emplacement.lgEMPLACEMENTID=:empl AND o.checked=:checked AND o.typemvtproduit.id IN :categ ",
                    HMvtProduit.class);
            query.setParameter("dtStart", dtStart);
            query.setParameter("dtEnd", dtEnd);
            query.setParameter("empl", emplacementId);
            query.setParameter("checked", checked);
            query.setParameter("categ", Arrays.asList(DateConverter.VENTE, DateConverter.ANNULATION_DE_VENTE));
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }

    }

    @Override
    public List<TvaDTO> tvasRapport(Params params) {
        if (caisseService.key_Take_Into_Account() || caisseService.key_Params()) {
            return tvasRapport0(params);
        }
        List<TvaDTO> datas = new ArrayList<>();

        try {
            List<HMvtProduit> details = donneesTvas(LocalDate.parse(params.getDtStart()), LocalDate.parse(params.getDtEnd()), true, params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            Map<Integer, List<HMvtProduit>> tvamap = details.stream().collect(Collectors.groupingBy(HMvtProduit::getValeurTva));
            tvamap.forEach((k, v) -> {
                TvaDTO otva = new TvaDTO();
                otva.setTaux(k);
                LongAdder ht = new LongAdder();
                LongAdder ttc = new LongAdder();
                LongAdder tva = new LongAdder();
                v.stream().forEach(l -> {
                    Integer mttc = l.getPrixUn() * l.getQteMvt();
                    Double valeurTva = 1 + (Double.valueOf(k) / 100);
                    Integer htAmont = (int) Math.ceil(mttc / valeurTva);
                    Integer montantTva = mttc - htAmont;
                    ht.add(htAmont);
                    ttc.add(mttc);
                    tva.add(montantTva);
                });
                otva.setMontantHt(ht.intValue());
                otva.setMontantTtc(ttc.intValue());
                otva.setMontantTva(tva.intValue());
                datas.add(otva);
            });

            return datas;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<VenteDetailsDTO> venteDetailsByVenteId(String venteId) {
        try {
            TypedQuery<TPreenregistrementDetail> q = getEntityManager().createQuery("SELECT o FROM TPreenregistrementDetail o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1", TPreenregistrementDetail.class);
            q.setParameter(1, venteId);
            List<TPreenregistrementDetail> details = q.getResultList();
            return details.stream().map(VenteDetailsDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public TPreenregistrement findOneById(String venteId) {
        try {
            TPreenregistrement tp = getEntityManager().find(TPreenregistrement.class, venteId);
            return tp;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JSONObject modifiertypevente(String venteId, ClotureVenteParams params) throws JSONException {
        try {
            TPreenregistrement tp = findOneById(venteId);
            final TCompteClientTiersPayant clientTiersPayant = getEntityManager().find(TCompteClientTiersPayant.class, params.getCompteTpNouveau().getCompteTp());
            List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(venteId);
            Optional<TPreenregistrementCompteClientTiersPayent> p = clientTiersPayents.stream().filter(t -> t.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTTIERSPAYANTID().equals(params.getCompteTp().getCompteTp())).findFirst();
            p.ifPresent(x -> {
                x.setLgCOMPTECLIENTTIERSPAYANTID(clientTiersPayant);
                x.setIntPERCENT(params.getCompteTpNouveau().getTaux());
                x.setLgUSERID(params.getUserId());
                getEntityManager().merge(x);

            });
            tp.setLgUSERID(params.getUserId());
            getEntityManager().merge(tp);
            return new JSONObject().put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false).put("msg", "Désolé: l'opération a échoué");
        }
    }

    @Override
    public List<TiersPayantParams> venteTierspayantData(String venteId) {
        return findClientTiersPayents(venteId).stream().map(TiersPayantParams::new).collect(Collectors.toList());
    }

    @Override
    public TicketDTO getVenteById(String venteId) {
        TPreenregistrement p = findById(venteId);

        return new TicketDTO(p, findByParent(venteId).stream().map(VenteDetailsDTO::new).collect(Collectors.toList()), findByVente(venteId), (p.getStrTYPEVENTE().equals(DateConverter.VENTE_ASSURANCE) ? findClientTiersPayents(venteId).stream().map(TiersPayantParams::new).collect(Collectors.toList()) : Collections.emptyList()), (!StringUtils.isEmpty(p.getPkBrand()) ? findEmplacementById(p.getPkBrand()) : null));
    }

    @Override
    public TicketDTO getVenteById(TPreenregistrement p) {
        return new TicketDTO(p, findByParent(p.getLgPREENREGISTREMENTID()).stream().map(VenteDetailsDTO::new).collect(Collectors.toList()), findByVente(p.getLgPREENREGISTREMENTID()), (p.getStrTYPEVENTE().equals(DateConverter.VENTE_ASSURANCE) ? findClientTiersPayents(p.getLgPREENREGISTREMENTID()).stream().map(TiersPayantParams::new).collect(Collectors.toList()) : Collections.emptyList()), (!StringUtils.isEmpty(p.getPkBrand()) ? findEmplacementById(p.getPkBrand()) : null));
    }

    @Override
    public List<TvaDTO> tvasRapportJournalier(Params params) {
        List<TvaDTO> datas = new ArrayList<>();
        List<HMvtProduit> details = donneesTvas(LocalDate.parse(params.getDtStart()), LocalDate.parse(params.getDtEnd()), true, params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        Map<LocalDate, List<HMvtProduit>> datemap = details.stream().collect(Collectors.groupingBy(HMvtProduit::getMvtDate));
        datemap.forEach((key, values) -> {
            Map<Integer, List<HMvtProduit>> tvamap = values.stream().collect(Collectors.groupingBy(HMvtProduit::getValeurTva));
            tvamap.forEach((k, v) -> {
                TvaDTO otva = new TvaDTO();
                otva.setLocalOperation(key);
                otva.setDateOperation(key.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                otva.setTaux(k);
                LongAdder ht = new LongAdder();
                LongAdder ttc = new LongAdder();
                LongAdder tva = new LongAdder();
                v.stream().forEach(l -> {
                    Integer mttc = l.getPrixUn() * l.getQteMvt();
                    Double valeurTva = 1 + (Double.valueOf(k) / 100);
                    Integer htAmont = (int) Math.ceil(mttc / valeurTva);
                    Integer montantTva = mttc - htAmont;
                    ht.add(htAmont);
                    ttc.add(mttc);
                    tva.add(montantTva);
                });
                otva.setMontantHt(ht.intValue());
                otva.setMontantTtc(ttc.intValue());
                otva.setMontantTva(tva.intValue());
                datas.add(otva);
            });
        });
        return datas;

    }

    @Override
    public List<TvaDTO> tvasRapport0(Params params) {
        List<TvaDTO> datas = new ArrayList<>();
        try {
            int montant = caisseService.montantAccount(LocalDate.parse(params.getDtStart()), LocalDate.parse(params.getDtEnd()), true, params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID(), TypeTransaction.VENTE_COMPTANT, DateConverter.MODE_ESP);
            List<HMvtProduit> details = donneesTvas(LocalDate.parse(params.getDtStart()), LocalDate.parse(params.getDtEnd()), true, params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            Map<Integer, List<HMvtProduit>> tvamap = details.stream().collect(Collectors.groupingBy(HMvtProduit::getValeurTva));
            LongAdder adder = new LongAdder();
            tvamap.forEach((k, v) -> {
                TvaDTO otva = new TvaDTO();
                otva.setTaux(k);
                LongAdder ht = new LongAdder();
                LongAdder ttc = new LongAdder();
                LongAdder tva = new LongAdder();
                v.stream().forEach(l -> {
                    Integer mttc = l.getPrixUn() * (l.getQteMvt() - l.getUg());
                    Double valeurTva = 1 + (Double.valueOf(k) / 100);
                    Integer htAmont = (int) Math.ceil(mttc / valeurTva);
                    Integer montantTva = mttc - htAmont;
                    ht.add(htAmont);
                    ttc.add(mttc);
                    adder.add(mttc);
                    tva.add(montantTva);

                });

                otva.setMontantHt(ht.intValue());
                otva.setMontantTtc(ttc.intValue());
                otva.setMontantTva(tva.intValue());
                datas.add(otva);
            });
            int mtn = adder.intValue() - montant;
            ListIterator listIterator = datas.listIterator();
            while (listIterator.hasNext()) {
                TvaDTO next = (TvaDTO) listIterator.next();
                if (next.getTaux() == 0) {
                    TvaDTO e = new TvaDTO();
                    e.setTaux(next.getTaux());
                    e.setMontantHt(next.getMontantHt() - mtn);
                    e.setMontantTtc(next.getMontantTtc() - mtn);
                    e.setMontantTva(next.getMontantTva());
                    listIterator.set(e);
                }

            }
            return datas;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject findAllVenteOrdonnancier(String medecinId, String dtStart, String dtEnd, String query, int start, int limit) throws JSONException {
        try {
            List<VenteDTO> l = findAllVenteOrdonnancier(medecinId, dtStart, dtEnd);
            return new JSONObject().put("total", l.size()).put("data", new JSONArray(l));
        } catch (Exception e) {
            return new JSONObject().put("total", 0).put("data", new JSONArray());
        }
    }

    @Override
    public List<VenteDTO> findAllVenteOrdonnancier(String medecinId, String dtStart, String dtEnd) {
        try {

            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrement> cq = cb.createQuery(TPreenregistrement.class);
            Root<TPreenregistrement> root = cq.from(TPreenregistrement.class);
            cq.select(root).orderBy(cb.asc(root.get(TPreenregistrement_.dtUPDATED)));
            predicates.add(cb.isNotNull(root.get(TPreenregistrement_.medecin).get(Medecin_.id)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrement_.dtUPDATED)),
                    java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            predicates.add(cb.equal(root.get(TPreenregistrement_.strSTATUT), commonparameter.statut_is_Closed));

            if (!StringUtils.isEmpty(medecinId)) {
                predicates.add(cb.equal(root.get(TPreenregistrement_.medecin).get(Medecin_.id), medecinId));
            }

            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);

            List<TPreenregistrement> list = q.getResultList();
            return list.stream().map(v -> new VenteDTO().buildOrdonnanciers(v, findByParent(v.getLgPREENREGISTREMENTID()).stream().filter(el -> {
                return (el.getLgFAMILLEID().isScheduled() && !el.getLgFAMILLEID().getIntT().trim().isEmpty());
            }).map(VenteDetailsDTO::new).collect(Collectors.toList()))).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<TvaDTO> tvaRapport(Params params) {

        if (caisseService.key_Take_Into_Account() || caisseService.key_Params()) {
            return tvasRapport0(params);
        }

        List<TvaDTO> datas = new ArrayList<>();

        try {
            List<HMvtProduit> details = donneesTvas(LocalDate.parse(params.getDtStart()), LocalDate.parse(params.getDtEnd()), true, params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            Map<Integer, List<HMvtProduit>> tvamap = details.stream().collect(Collectors.groupingBy(HMvtProduit::getValeurTva));
            tvamap.forEach((k, v) -> {
                TvaDTO otva = new TvaDTO();
                otva.setTaux(k);
                LongAdder ht = new LongAdder();
                LongAdder ttc = new LongAdder();
                LongAdder tva = new LongAdder();
                v.stream().forEach(l -> {
                    Integer mttc = l.getPrixUn() * (l.getQteMvt() - l.getUg());
                    Double valeurTva = 1 + (Double.valueOf(k) / 100);
                    Integer htAmont = (int) Math.ceil(mttc / valeurTva);
                    Integer montantTva = mttc - htAmont;
                    ht.add(htAmont);
                    ttc.add(mttc);
                    tva.add(montantTva);

                });
                otva.setMontantHt(ht.intValue());
                otva.setMontantTtc(ttc.intValue());
                otva.setMontantTva(tva.intValue());
                datas.add(otva);
            });

            return datas;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<TvaDTO> tvaRapportJournalier(Params params) {
        List<TvaDTO> datas = new ArrayList<>();
        List<HMvtProduit> details = donneesTvas(LocalDate.parse(params.getDtStart()), LocalDate.parse(params.getDtEnd()), true, params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        Map<LocalDate, List<HMvtProduit>> datemap = details.stream().collect(Collectors.groupingBy(HMvtProduit::getMvtDate));
        datemap.forEach((key, values) -> {
            Map<Integer, List<HMvtProduit>> tvamap = values.stream().collect(Collectors.groupingBy(HMvtProduit::getValeurTva));
            tvamap.forEach((k, v) -> {
                TvaDTO otva = new TvaDTO();
                otva.setLocalOperation(key);
                otva.setDateOperation(key.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                otva.setTaux(k);
                LongAdder ht = new LongAdder();
                LongAdder ttc = new LongAdder();
                LongAdder tva = new LongAdder();
                v.stream().forEach(l -> {
                    Integer mttc = l.getPrixUn() * (l.getQteMvt() - l.getUg());
                    Double valeurTva = 1 + (Double.valueOf(k) / 100);
                    Integer htAmont = (int) Math.ceil(mttc / valeurTva);
                    Integer montantTva = mttc - htAmont;
                    ht.add(htAmont);
                    ttc.add(mttc);
                    tva.add(montantTva);
                });
                otva.setMontantHt(ht.intValue());
                otva.setMontantTtc(ttc.intValue());
                otva.setMontantTva(tva.intValue());
                datas.add(otva);
            });
        });
        return datas;

    }

    @Override
    public JSONObject tvasData(Params params) throws JSONException {
        JSONObject json = new JSONObject();
        List<TvaDTO> datas = tvaRapport(params);
        json.put("total", datas.size());
        json.put("data", new JSONArray(datas));
        return json;
    }
}
