/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.GenererFactureDTO;
import commonTasks.dto.ReglementCarnetDTO;
import commonTasks.dto.TiersPayantExclusDTO;
import commonTasks.dto.VenteTiersPayantsDTO;
import dal.ReglementCarnet;
import dal.ReglementCarnet_;
import dal.TCompteClientTiersPayant_;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementCompteClientTiersPayent_;
import dal.TPreenregistrement_;
import dal.TTiersPayant;
import dal.TTiersPayant_;
import dal.TUser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.service.CarnetAsDepotService;
import rest.service.RetourCarnetService;
import rest.service.dto.ExtraitCompteClientDTO;
import rest.service.dto.ProduitVenduDTO;

/**
 *
 * @author koben
 */
@Stateless
public class CarnetDepotServiceImpl implements CarnetAsDepotService {

    private static final Logger LOG = Logger.getLogger(CarnetDepotServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private RetourCarnetService retourCarnetService;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<TiersPayantExclusDTO> all(int start, int size, String query, boolean all, Boolean exclude) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TTiersPayant> cq = cb.createQuery(TTiersPayant.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            cq.select(root).orderBy(cb.asc(root.get(TTiersPayant_.strNAME)));
            List<Predicate> predicates = perimePredicatCountAll(cb, root, query, exclude);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<TTiersPayant> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(size);
            }

            return q.getResultList().stream().map(TiersPayantExclusDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Predicate> perimePredicatCountAll(CriteriaBuilder cb, Root<TTiersPayant> root, String query, Boolean exclude) {
        List<Predicate> predicates = new ArrayList<>();
        if (!StringUtils.isEmpty(query)) {
            predicates.add(cb.or(cb.like(root.get(TTiersPayant_.strNAME), query + "%"),
                    cb.like(root.get(TTiersPayant_.strCODEORGANISME), query + "%"),
                    cb.like(root.get(TTiersPayant_.strFULLNAME), query + "%")));
        }
        predicates.add(cb.equal(root.get(TTiersPayant_.strSTATUT), "enable"));
        if (exclude != null) {
            if (exclude) {
                predicates.add(cb.isTrue(root.get(TTiersPayant_.isDepot)));
            } else {
                predicates.add(cb.isFalse(root.get(TTiersPayant_.isDepot)));
            }

        }
        return predicates;
    }

    @Override
    public JSONObject all(int start, int size, String query, Boolean exclude) {
        long count = countAll(query, exclude);
        List<TiersPayantExclusDTO> data = all(start, size, query, false, exclude);
        JSONObject json = new JSONObject().put("total", count).put("data", data);
        return json;
    }

    private long countAll(String query, Boolean exclude) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            cq.select(cb.count(root));
            List<Predicate> predicates = perimePredicatCountAll(cb, root, query, exclude);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<Long> q = getEntityManager().createQuery(cq);
            return q.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void setAsDepot(GenererFactureDTO datas) {
        datas.getDatas().forEach(id -> {
            TTiersPayant payant = getEntityManager().find(TTiersPayant.class, id);
            payant.setIsDepot(Boolean.TRUE);
            getEntityManager().merge(payant);
        });
    }

    @Override
    public void setAsDepot(String id) {
        TTiersPayant payant = getEntityManager().find(TTiersPayant.class, id);
        payant.setIsDepot(Boolean.TRUE);
        getEntityManager().merge(payant);
    }

    @Override
    public void unsetAsDepot(String id) {
        TTiersPayant payant = getEntityManager().find(TTiersPayant.class, id);
        payant.setIsDepot(Boolean.FALSE);
        getEntityManager().merge(payant);
    }

    @Override
    public void update(String id, boolean isDepot) {
        if (isDepot) {
            setAsDepot(id);
        } else {
            unsetAsDepot(id);
        }
    }

    private long countFetchVenteByTiersPayant(String tiersPayantId, String dtStart, String dtEnd) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            cq.select(cb.count(root));
            List<Predicate> predicates = fetchVentePredicat(cb, root, LocalDate.parse(dtStart), LocalDate.parse(dtEnd), tiersPayantId);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<Long> q = getEntityManager().createQuery(cq);
            return q.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public JSONObject fetchVenteByTiersPayant(String tiersPayantId, String dtStart, String dtEnd, int start, int size) {
        TiersPayantExclusDTO metaData = fetchVenteSummary(tiersPayantId, LocalDate.parse(dtStart), LocalDate.parse(dtEnd));
        List<VenteTiersPayantsDTO> data = fetchVente(tiersPayantId, LocalDate.parse(dtStart), LocalDate.parse(dtEnd), start, size, false);
        JSONObject json = new JSONObject();
        json.put("metaData", new JSONObject(metaData));
        json.put("total", countFetchVenteByTiersPayant(tiersPayantId, dtStart, dtEnd));
        json.put("data", new JSONArray(data));

        return json;
    }

    private List<Predicate> fetchVentePredicat(CriteriaBuilder cb, Root<TPreenregistrementCompteClientTiersPayent> root, LocalDate dtStart, LocalDate dtEnd, String tiersPayantId) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isTrue(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.isDepot)));
//        predicates.add(cb.or(cb.isTrue(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.toBeExclude)),cb.isTrue(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.isDepot)))  );
        if (!StringUtils.isEmpty(tiersPayantId)) {
            predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTIERSPAYANTID), tiersPayantId));
        }
        predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID).get(TPreenregistrement_.strSTATUT), "is_Closed"));
        predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID).get(TPreenregistrement_.bISCANCEL), Boolean.FALSE));
        predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.strSTATUT), "is_Closed"));
        predicates.add(cb.greaterThan(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID).get(TPreenregistrement_.intPRICE), 0));
        Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID).get(TPreenregistrement_.dtUPDATED)),
                java.sql.Date.valueOf(dtStart),
                java.sql.Date.valueOf(dtEnd));
        predicates.add(btw);
        return predicates;
    }

    @Override
    public List<VenteTiersPayantsDTO> fetchVente(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd, int start, int size, boolean all) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementCompteClientTiersPayent> cq = cb.createQuery(TPreenregistrementCompteClientTiersPayent.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            cq.select(root).orderBy(cb.desc(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID).get(TPreenregistrement_.dtUPDATED)));
            List<Predicate> predicates = fetchVentePredicat(cb, root, dtStart, dtEnd, tiersPayantId);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(size);
            }
            return q.getResultList().stream().map(VenteTiersPayantsDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }

    }

    @Override
    public TiersPayantExclusDTO fetchVenteSummary(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<TiersPayantExclusDTO> cq = cb.createQuery(TiersPayantExclusDTO.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            cq.select(cb.construct(TiersPayantExclusDTO.class, cb.sumAsLong(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE)),
                    cb.count(root)
            ));
            List<Predicate> predicates = fetchVentePredicat(cb, root, dtStart, dtEnd, tiersPayantId);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<TiersPayantExclusDTO> q = getEntityManager().createQuery(cq);
            return q.getSingleResult();
        } catch (Exception e) {

            return new TiersPayantExclusDTO();
        }
    }

    @Override
    public JSONObject reglementsCarnet(String tiersPayantId, String dtStart, String dtEnd, int start, int size) {
        ReglementCarnetDTO metaData = reglementsCarnetSummary(tiersPayantId, LocalDate.parse(dtStart), LocalDate.parse(dtEnd));
        List<ReglementCarnetDTO> data = reglementsCarnet(tiersPayantId, dtStart, dtEnd, start, size, false);
        JSONObject json = new JSONObject();
        json.put("metaData", new JSONObject(metaData));
        json.put("total", reglementsCarnetCount(tiersPayantId, LocalDate.parse(dtStart), LocalDate.parse(dtEnd)));
        json.put("data", new JSONArray(data));

        return json;
    }

    @Override
    public List<ReglementCarnetDTO> reglementsCarnet(String tiersPayantId, String dtStart, String dtEnd, int start, int size, boolean all) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<ReglementCarnet> cq = cb.createQuery(ReglementCarnet.class);
            Root<ReglementCarnet> root = cq.from(ReglementCarnet.class);
            cq.select(root).orderBy(cb.desc(root.get(ReglementCarnet_.createdAt)));
            List<Predicate> predicates = reglementsCarnetPredicat(cb, root, LocalDate.parse(dtStart), LocalDate.parse(dtEnd), tiersPayantId);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<ReglementCarnet> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(size);
            }
            return q.getResultList().stream().map(ReglementCarnetDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "reglementsCarnet=====>> ", e);
            return Collections.emptyList();
        }
    }

    private List<Predicate> reglementsCarnetPredicat(CriteriaBuilder cb, Root<ReglementCarnet> root, LocalDate dtStart, LocalDate dtEnd, String tiersPayantId) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isTrue(root.get(ReglementCarnet_.tiersPayant).get(TTiersPayant_.isDepot)));
        if (!StringUtils.isEmpty(tiersPayantId)) {
            predicates.add(cb.equal(root.get(ReglementCarnet_.tiersPayant).get(TTiersPayant_.lgTIERSPAYANTID), tiersPayantId));
        }
        Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(ReglementCarnet_.createdAt)),
                java.sql.Date.valueOf(dtStart),
                java.sql.Date.valueOf(dtEnd));
        predicates.add(btw);
        return predicates;
    }

    private long reglementsCarnetCount(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<ReglementCarnet> root = cq.from(ReglementCarnet.class);
            cq.select(cb.count(root));
            List<Predicate> predicates = reglementsCarnetPredicat(cb, root, dtStart, dtEnd, tiersPayantId);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<Long> q = getEntityManager().createQuery(cq);
            return q.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public ReglementCarnetDTO reglementsCarnetSummary(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<ReglementCarnetDTO> cq = cb.createQuery(ReglementCarnetDTO.class);
            Root<ReglementCarnet> root = cq.from(ReglementCarnet.class);
            cq.select(cb.construct(ReglementCarnetDTO.class, cb.sum(root.get(ReglementCarnet_.montantPaye)),
                    cb.count(root)
            ));
            List<Predicate> predicates = reglementsCarnetPredicat(cb, root, dtStart, dtEnd, tiersPayantId);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<ReglementCarnetDTO> q = getEntityManager().createQuery(cq);
            return q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "reglementsCarnetSummary=====>> ", e);
            return new ReglementCarnetDTO();
        }
    }

    @Override
    public JSONObject faireReglement(ReglementCarnetDTO reglementCarnetDTO, TUser user) {
        JSONObject json = new JSONObject();

        TTiersPayant payant = getEntityManager().find(TTiersPayant.class, reglementCarnetDTO.getTiersPayantId());
        if (payant.getAccount().intValue() < reglementCarnetDTO.getMontantPaye()) {
            return json.put("success", false).put("msg", "VEUILLEZ SAISIR UN MONTANT EGAL OU INFERIEUR AU SOLDE");
        }
        ReglementCarnet carnet = new ReglementCarnet();
        carnet.setCreatedAt(LocalDateTime.now());
        carnet.setUser(user);
        carnet.setTiersPayant(payant);
        carnet.setDescription(reglementCarnetDTO.getDescription());
        carnet.setMontantPaye(reglementCarnetDTO.getMontantPaye());
        carnet.setMontantPayer(payant.getAccount().intValue());
        carnet.setMontantRestant(carnet.getMontantPayer() - carnet.getMontantPaye());
        getEntityManager().persist(carnet);
        payant.setAccount(payant.getAccount() - carnet.getMontantPaye());
        carnet.setReference(findLastReference() + 1);
        getEntityManager().persist(carnet);
        getEntityManager().merge(payant);
        json.put("success", true);
        return json;
    }

    @Override
    public String getTiersPayantName(String tiersPayantId) {
        if (StringUtils.isNoneBlank(tiersPayantId)) {
            return getEntityManager().find(TTiersPayant.class, tiersPayantId).getStrFULLNAME();
        }
        return " ";
    }

    private int findLastReference() {
        try {
            TypedQuery<Integer> query = getEntityManager().createQuery("SELECT MAX(o.reference) FROM ReglementCarnet o ", Integer.class);
            return query.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "findLastReference=====>> ", e);
            return 0;
        }
    }

    @Override
    public JSONObject listArticleByTiersPayant(String query, String tierspayantId, String dtStart, String dtEnd) {
        List<ProduitVenduDTO> datas = listeArticleByTiersPayant(query, tierspayantId, dtStart, dtEnd);
        JSONObject json = new JSONObject();
        json.put("total", datas.size());
        json.put("data", new JSONArray(datas));
        return json;
    }

    @Override
    public List<ProduitVenduDTO> listeArticleByTiersPayant(String query, String tierspayantId, String dtStart, String dtEnd) {
        try {
            if (StringUtils.isEmpty(query)) {
                query = "%%";
            } else {
                query = query + "%";
            }

            Query q = getEntityManager().createNativeQuery("SELECT prd.int_CIP,prd.str_NAME,prd.int_PRICE,prd.int_PAF,prd.lg_FAMILLE_ID  FROM   t_preenregistrement_detail d,t_famille prd, t_preenregistrement p,t_preenregistrement_compte_client_tiers_payent cpl,t_compte_client_tiers_payant cp,t_tiers_payant tp "
                    + "WHERE d.lg_PREENREGISTREMENT_ID=p.lg_PREENREGISTREMENT_ID AND d.lg_FAMILLE_ID=prd.lg_FAMILLE_ID AND p.lg_PREENREGISTREMENT_ID=cpl.lg_PREENREGISTREMENT_ID AND p.int_PRICE >0 AND p.b_IS_CANCEL=0 AND "
                    + " cpl.lg_COMPTE_CLIENT_TIERS_PAYANT_ID=cp.lg_COMPTE_CLIENT_TIERS_PAYANT_ID AND  cp.lg_TIERS_PAYANT_ID=tp.lg_TIERS_PAYANT_ID AND tp.lg_TIERS_PAYANT_ID=?1 AND DATE(p.dt_UPDATED) BETWEEN ?3 AND ?4 "
                    + "  AND (prd.int_CIP LIKE ?2 OR prd.str_NAME LIKE ?2) ", Tuple.class);

            q.setParameter(1, tierspayantId);
            q.setParameter(2, query);
            q.setParameter(3, java.sql.Date.valueOf(dtStart), TemporalType.DATE);
            q.setParameter(4, java.sql.Date.valueOf(dtEnd), TemporalType.DATE);
            List<Tuple> list = q.getResultList();
            return list.stream().map(ProduitVenduDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "listeArticleByTiersPayant=====>> ", e);
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject articleByTiersPayantByProduitId(String produitId, String tierspayantId, String dtStart, String dtEnd) {
        List<ProduitVenduDTO> datas = listeArticleByTiersPayantByProduitId(produitId, tierspayantId, dtStart, dtEnd);
        JSONObject json = new JSONObject();
        json.put("total", datas.size());
        json.put("success", datas.size() > 0);
        json.put("data", new JSONArray(datas));
        return json;
    }

    @Override
    public List<ProduitVenduDTO> listeArticleByTiersPayantByProduitId(String produitId, String tierspayantId, String dtStart, String dtEnd) {
        try {

            Query q = getEntityManager().createNativeQuery("SELECT prd.int_CIP,prd.str_NAME,prd.int_PRICE,prd.int_PAF,prd.lg_FAMILLE_ID  FROM   t_preenregistrement_detail d,t_famille prd, t_preenregistrement p,t_preenregistrement_compte_client_tiers_payent cpl,t_compte_client_tiers_payant cp,t_tiers_payant tp "
                    + "WHERE d.lg_PREENREGISTREMENT_ID=p.lg_PREENREGISTREMENT_ID AND d.lg_FAMILLE_ID=prd.lg_FAMILLE_ID AND p.lg_PREENREGISTREMENT_ID=cpl.lg_PREENREGISTREMENT_ID AND p.int_PRICE >0 AND p.b_IS_CANCEL=0 AND "
                    + " cpl.lg_COMPTE_CLIENT_TIERS_PAYANT_ID=cp.lg_COMPTE_CLIENT_TIERS_PAYANT_ID AND   cp.lg_TIERS_PAYANT_ID=tp.lg_TIERS_PAYANT_ID AND tp.lg_TIERS_PAYANT_ID=?1 AND DATE(p.dt_UPDATED) BETWEEN ?3 AND ?4"
                    + "  AND prd.lg_FAMILLE_ID =?2  ", Tuple.class);

            q.setParameter(1, tierspayantId);
            q.setParameter(2, produitId);
            q.setParameter(3, java.sql.Date.valueOf(dtStart), TemporalType.DATE);
            q.setParameter(4, java.sql.Date.valueOf(dtEnd), TemporalType.DATE);
            List<Tuple> list = q.getResultList();
            return list.stream().map(ProduitVenduDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "listeArticleByTiersPayantByProduitId=====>> ", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ExtraitCompteClientDTO> extraitcompteAvecRetour(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd, String query) {
        List<ExtraitCompteClientDTO> datas = new ArrayList<>();
        datas.addAll(reglementsCarnet(tiersPayantId, dtStart.toString(), dtEnd.toString(), 0, 0, true).stream().map(ExtraitCompteClientDTO::new).collect(Collectors.toList()));
        datas.addAll(fetchVente(tiersPayantId, dtStart, dtEnd, 0, 0, true).stream().map(ExtraitCompteClientDTO::new).collect(Collectors.toList()));
        datas.addAll(retourCarnetService.listRetourByTierspayantIdAndPeriode(tiersPayantId, query, dtStart, dtEnd).stream().map(ExtraitCompteClientDTO::new).collect(Collectors.toList()));
        datas.sort(Comparator.comparing(ExtraitCompteClientDTO::getCreatedAt));
        return datas;
    }
}
