/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.FamilleArticleStatDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.HMvtProduit;
import dal.HMvtProduit_;
import dal.TEmplacement;
import dal.TFamille_;
import dal.TFamillearticle_;
import dal.TGrossiste_;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TPreenregistrementDetail_;
import dal.TPreenregistrement_;
import dal.TTypeVente_;
import dal.TUser;
import dal.TUser_;
import dal.TZoneGeographique_;
import dal.Typemvtproduit_;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.FamilleArticleService;
import util.DateConverter;

/**
 *
 * @author DICI
 */
@Stateless
public class FamilleArticleServiceImpl implements FamilleArticleService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }
    Comparator<FamilleArticleStatDTO> comparator = Comparator.comparing(FamilleArticleStatDTO::getCode);
    Comparator<VenteDetailsDTO> comparatorQty = Comparator.comparingInt(VenteDetailsDTO::getIntQUANTITY);
    Comparator<VenteDetailsDTO> comparatorPrice = Comparator.comparingInt(VenteDetailsDTO::getIntPRICE);

    @Override
    public Pair<FamilleArticleStatDTO, List<FamilleArticleStatDTO>> statistiqueParFamilleArticle(String dtStart, String dtEnd, String codeFamile, String query, TUser u, String codeRayon, String codeGrossiste) {
        List<FamilleArticleStatDTO> list = new ArrayList<>();
        FamilleArticleStatDTO summary = new FamilleArticleStatDTO();
        try {
            LongAdder montantTTC = new LongAdder();
            LongAdder montantMarge = new LongAdder();
            LongAdder montantHT = new LongAdder();
            LongAdder montantAchat = new LongAdder();
            final LocalDate periode = LocalDate.parse(dtStart);
            final Integer montanttotalHt = totalMontantHT(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), query, codeFamile, u, codeRayon, codeGrossiste);
            findPreenregistrementDetails(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), query, codeFamile, u, codeRayon, codeGrossiste).stream()
                    .sorted(comparator)
                    .forEach(x -> {
                        montantMarge.add(x.getMontantMarge());
                        montantTTC.add(x.getMontantTTC());
                        montantAchat.add(x.getMontantAchat());
                        montantHT.add(x.getMontantHT());
                        cumulFamilleArticles(periode, x, u);
                        Double p = new BigDecimal(Double.valueOf(x.getMontantHT()) / montanttotalHt).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
                        x.setPourcentageTH(p.intValue());
                        list.add(x);

                    });
            summary.setMontantCumulAchat(montantAchat.intValue());
            summary.setMontantCumulHT(montantHT.intValue());
            summary.setMontantCumulMarge(montantMarge.intValue());
            summary.setMontantCumulTTC(montantTTC.intValue());
            summary.setPourcentageTH((montanttotalHt / montanttotalHt) * 100);
            Double ux = new BigDecimal(Double.valueOf(summary.getMontantCumulMarge()) / summary.getMontantCumulHT()).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
            summary.setPourcentageCumulMage(ux.intValue());
        } catch (Exception e) {
        }
        return Pair.of(summary, list);

    }

    @Override
    public JSONObject statistiqueParFamilleArticleView(String dtStart, String dtEnd, String codeFamile, String query, TUser u, String codeRayon, String codeGrossiste) throws JSONException {
        JSONObject json = new JSONObject();
        Pair<FamilleArticleStatDTO, List<FamilleArticleStatDTO>> pair = statistiqueParFamilleArticle(dtStart, dtEnd, codeFamile, query, u, codeRayon, codeGrossiste);
        List<FamilleArticleStatDTO> l = pair.getRight();
        json.put("success", true);
        json.put("total", l.size());
        json.put("data", new JSONArray(l));
        json.put("metaData", new JSONObject(pair.getLeft()));
        return json;
    }

    List<HMvtProduit> findHMvtProduits(LocalDate dtStart, LocalDate dtEnd, String query, String codeFamillle, TUser u) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<HMvtProduit> cq = cb.createQuery(HMvtProduit.class);
            Root<HMvtProduit> root = cq.from(HMvtProduit.class);
            Predicate btw = cb.between(
                    root.get(HMvtProduit_.mvtDate), dtStart,
                    dtEnd);
            predicates.add(btw);
            predicates.add(cb.equal(root.get(HMvtProduit_.emplacement), emp));
            predicates.add(root.get(HMvtProduit_.typemvtproduit).get(Typemvtproduit_.id).in(Arrays.asList(DateConverter.VENTE, DateConverter.ANNULATION_DE_VENTE)));
            if (!StringUtils.isEmpty(query)) {
                predicates.add(cb.or(cb.like(root.get(HMvtProduit_.famille).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strCODEFAMILLE), query + "%"), cb.like(root.get(HMvtProduit_.famille).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strLIBELLE), query + "%")));
            }
            if (!StringUtils.isEmpty(codeFamillle)) {
                predicates.add(cb.equal(root.get(HMvtProduit_.famille).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.lgFAMILLEARTICLEID), codeFamillle));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<HMvtProduit> q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    private List<Predicate> famillePredicats(CriteriaBuilder cb, Root<TPreenregistrementDetail> root, Join<TPreenregistrementDetail, TPreenregistrement> join, LocalDate dtStart, LocalDate dtEnd, String query, String codeFamillle, TUser u, String codeRayon, String codeGrossiste) {
        List<Predicate> predicates = new ArrayList<>();
        TEmplacement emp = u.getLgEMPLACEMENTID();
        Predicate btw = cb.between(cb.function("DATE", Date.class, join.get(TPreenregistrement_.dtUPDATED)),
                java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
        predicates.add(btw);
        predicates.add(cb.equal(join.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID), emp));
        predicates.add(cb.equal(join.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
        predicates.add(cb.isFalse(join.get(TPreenregistrement_.bISCANCEL)));
        predicates.add(cb.greaterThan(join.get(TPreenregistrement_.intPRICE), 0));
        predicates.add(cb.notLike(join.get(TPreenregistrement_.lgTYPEVENTEID).get(TTypeVente_.lgTYPEVENTEID), DateConverter.DEPOT_EXTENSION));
        if (!StringUtils.isEmpty(query)) {
            predicates.add(cb.or(cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strCODEFAMILLE), query + "%"),
                    cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strLIBELLE), query + "%")));
        }
        if (!StringUtils.isEmpty(codeFamillle) && !codeFamillle.equals("ALL")) {
            predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.lgFAMILLEARTICLEID), codeFamillle));
        }
        if (!StringUtils.isEmpty(codeRayon) && !codeRayon.equals("ALL")) {
            predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.lgZONEGEOID), codeRayon));
        }
        if (!StringUtils.isEmpty(codeGrossiste) && !codeGrossiste.equals("ALL")) {
            predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), codeGrossiste));
        }
        return predicates;
    }

    List<FamilleArticleStatDTO> findPreenregistrementDetails(LocalDate dtStart, LocalDate dtEnd, String query, String codeFamillle, TUser u, String codeRayon, String codeGrossiste) {
        try {

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<FamilleArticleStatDTO> cq = cb.createQuery(FamilleArticleStatDTO.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(FamilleArticleStatDTO.class, root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strCODEFAMILLE),
                    root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strLIBELLE),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICE)),
                    cb.sum(cb.prod(root.get(TPreenregistrementDetail_.prixAchat), root.get(TPreenregistrementDetail_.intQUANTITY))),
                    cb.sum(root.get(TPreenregistrementDetail_.montantTva)),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICEREMISE)),
                    root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.lgFAMILLEARTICLEID)
            )).groupBy(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID));
            List<Predicate> predicates = famillePredicats(cb, root, join, dtStart, dtEnd, query, codeFamillle, u, codeRayon, codeGrossiste);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<FamilleArticleStatDTO> q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    FamilleArticleStatDTO cumulFamilleArticles(LocalDate periode, FamilleArticleStatDTO familleArticleStatDTO, TUser u) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<FamilleArticleStatDTO> cq = cb.createQuery(FamilleArticleStatDTO.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(FamilleArticleStatDTO.class,
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICE)),
                    cb.sum(cb.prod(root.get(TPreenregistrementDetail_.prixAchat), root.get(TPreenregistrementDetail_.intQUANTITY))),
                    cb.sum(root.get(TPreenregistrementDetail_.montantTva)),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICEREMISE))
            ));
            predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.lgFAMILLEARTICLEID), familleArticleStatDTO.getFamilleId()));
            Predicate btw = cb.equal(cb.function("YEAR", Date.class, join.get(TPreenregistrement_.dtUPDATED)),
                    periode.getYear());
            predicates.add(btw);
            predicates.add(cb.equal(join.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID), emp));
            predicates.add(cb.equal(join.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.isFalse(join.get(TPreenregistrement_.bISCANCEL)));
            predicates.add(cb.greaterThan(join.get(TPreenregistrement_.intPRICE), 0));
            predicates.add(cb.notLike(join.get(TPreenregistrement_.lgTYPEVENTEID).get(TTypeVente_.lgTYPEVENTEID), DateConverter.DEPOT_EXTENSION));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<FamilleArticleStatDTO> q = getEntityManager().createQuery(cq);
            q.setMaxResults(1);
            FamilleArticleStatDTO t = q.getSingleResult();
            familleArticleStatDTO.setMontantCumulAchat(t.getMontantCumulAchat());
            familleArticleStatDTO.setMontantCumulTTC(t.getMontantCumulTTC());
            familleArticleStatDTO.setMontantCumulTva(t.getMontantCumulTva());
            familleArticleStatDTO.setMontantCumulHT(t.getMontantCumulHT());
            Double p = new BigDecimal(Double.valueOf(familleArticleStatDTO.getMontantHT()) / familleArticleStatDTO.getMontantCumulHT()).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
            familleArticleStatDTO.setValeurPeriode(p.intValue());
            return familleArticleStatDTO;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return familleArticleStatDTO;
        }
    }

    Integer totalMontantHT(LocalDate dtStart, LocalDate dtEnd, String query, String codeFamillle, TUser u, String codeRayon, String codeGrossiste) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Number> cq = cb.createQuery(Number.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.diff(cb.diff(cb.sum(root.get(TPreenregistrementDetail_.intPRICE)), cb.sum(root.get(TPreenregistrementDetail_.intPRICEREMISE))), cb.sum(root.get(TPreenregistrementDetail_.montantTva)))
            );
            Predicate btw = cb.between(cb.function("DATE", Date.class, join.get(TPreenregistrement_.dtUPDATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            predicates.add(cb.equal(join.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID), emp));
            predicates.add(cb.equal(join.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.isFalse(join.get(TPreenregistrement_.bISCANCEL)));
            predicates.add(cb.greaterThan(join.get(TPreenregistrement_.intPRICE), 0));
            if (!StringUtils.isEmpty(query)) {
                predicates.add(cb.or(cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strCODEFAMILLE), query + "%"),
                        cb.like(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strLIBELLE), query + "%")));
            }
            if (!StringUtils.isEmpty(codeFamillle)) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.lgFAMILLEARTICLEID), codeFamillle));
            }
            if (!StringUtils.isEmpty(codeRayon)) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.lgZONEGEOID), codeRayon));
            }
            if (!StringUtils.isEmpty(codeGrossiste)) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), codeGrossiste));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            q.setMaxResults(1);
            return ((Number) q.getSingleResult()).intValue();
        } catch (Exception e) {
//            e.printStackTrace(System.err);
            return 0;
        }
    }

    @Override
    public List<VenteDetailsDTO> geVingtQuatreVingt(String dtStart, String dtEnd, TUser u, String codeFamile, String codeRayon, String codeGrossiste, int start, int limit, boolean all, boolean qtyOrCa) {
        int valeur = valeurVinghtQuarteVinght(dtStart, dtEnd, u, codeFamile, codeRayon, codeGrossiste, qtyOrCa);
        try {
            List<Predicate> predicates = new ArrayList<>();
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<VenteDetailsDTO> cq = cb.createQuery(VenteDetailsDTO.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            if (qtyOrCa) {
                cq.select(cb.construct(VenteDetailsDTO.class,
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP),
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME),
                        cb.sumAsLong(root.get(TPreenregistrementDetail_.intPRICE)),
                        cb.sumAsLong(root.get(TPreenregistrementDetail_.intQUANTITY)),
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEID),
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID),
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strLIBELLE)
                )).groupBy(root.get(TPreenregistrementDetail_.lgFAMILLEID)).orderBy(cb.desc(cb.sum(root.get(TPreenregistrementDetail_.intQUANTITY))));

            } else {
                cq.select(cb.construct(VenteDetailsDTO.class,
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.intCIP),
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.strNAME),
                        cb.sumAsLong(root.get(TPreenregistrementDetail_.intPRICE)),
                        cb.sumAsLong(root.get(TPreenregistrementDetail_.intQUANTITY)),
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEID),
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID),
                        root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.strLIBELLE)
                )).groupBy(root.get(TPreenregistrementDetail_.lgFAMILLEID)).orderBy(cb.desc(cb.sum(root.get(TPreenregistrementDetail_.intPRICE))));

            }

            Predicate btw = cb.between(cb.function("DATE", Date.class, join.get(TPreenregistrement_.dtUPDATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            predicates.add(cb.equal(join.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID), emp));
            predicates.add(cb.equal(join.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.isFalse(join.get(TPreenregistrement_.bISCANCEL)));
            predicates.add(cb.greaterThan(join.get(TPreenregistrement_.intPRICE), 0));
            if (!StringUtils.isEmpty(codeFamile) && !codeFamile.equals("ALL")) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.lgFAMILLEARTICLEID), codeFamile));
            }
            if (!StringUtils.isEmpty(codeRayon) && !codeRayon.equals("ALL")) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.lgZONEGEOID), codeRayon));
            }
            if (!StringUtils.isEmpty(codeGrossiste) && !codeGrossiste.equals("ALL")) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), codeGrossiste));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<VenteDetailsDTO> q = getEntityManager().createQuery(cq);
            List<VenteDetailsDTO> _f = new ArrayList<>();
            List<VenteDetailsDTO> data = q.getResultList();
            if (qtyOrCa) {

                for (VenteDetailsDTO x : data) {
                    int stock = stockProduit(x.getLgFAMILLEID(), emp.getLgEMPLACEMENTID());
                    x.setIntQUANTITYSERVED(stock);
                    _f.add(x);
                    valeur -= x.getIntQUANTITY();
                    if (valeur <= 0) {
                        break;
                    }
                }
            } else {
                for (VenteDetailsDTO x : data) {
                    int stock = stockProduit(x.getLgFAMILLEID(), emp.getLgEMPLACEMENTID());
                    x.setIntQUANTITYSERVED(stock);
                    _f.add(x);
                    valeur -= x.getIntPRICE();
                    if (valeur <= 0) {
                        break;
                    }
                }
            }

            return _f;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }

    }

    private Integer stockProduit(String idProduit, String empl) {
        try {
            Query q = getEntityManager().createQuery("SELECT o.intNUMBERAVAILABLE FROM TFamilleStock o WHERE o.strSTATUT='enable' AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?1 AND o.lgFAMILLEID.lgFAMILLEID=?2 ");
            q.setMaxResults(1);
            q.setParameter(1, empl);
            q.setParameter(2, idProduit);
            return (Integer) q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }
    }

    @Override
    public JSONObject geVingtQuatreVingt(String dtStart, String dtEnd, TUser u, String codeFamile, String codeRayon, String codeGrossiste, int start, int limit, boolean qtyOrCa) {
        List<VenteDetailsDTO> _f = geVingtQuatreVingt(dtStart, dtEnd, u, codeFamile, codeRayon, codeGrossiste, start, limit, false, qtyOrCa);
        int total = _f.size();
        if (qtyOrCa) {
            _f.sort(comparatorQty.reversed());
        } else {
            _f.sort(comparatorPrice.reversed());
        }

        return new JSONObject().put("total", total).put("data", new JSONArray(_f));
    }

    private int valeurVinghtQuarteVinght(String dtStart, String dtEnd, TUser u, String codeFamile, String codeRayon, String codeGrossiste, boolean qtyOrCa) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            if (qtyOrCa) {
                cq.select(cb.sumAsLong(root.get(TPreenregistrementDetail_.intQUANTITY)));
            } else {
                cq.select(cb.sumAsLong(root.get(TPreenregistrementDetail_.intPRICE)));
            }
            Predicate btw = cb.between(cb.function("DATE", Date.class, join.get(TPreenregistrement_.dtUPDATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            predicates.add(cb.equal(join.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID), emp));
            predicates.add(cb.equal(join.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.isFalse(join.get(TPreenregistrement_.bISCANCEL)));
            predicates.add(cb.greaterThan(join.get(TPreenregistrement_.intPRICE), 0));
            if (!StringUtils.isEmpty(codeFamile) && !codeFamile.equals("ALL")) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEARTICLEID).get(TFamillearticle_.lgFAMILLEARTICLEID), codeFamile));
            }
            if (!StringUtils.isEmpty(codeRayon)&& !codeRayon.equals("ALL")) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.lgZONEGEOID), codeRayon));
            }
            if (!StringUtils.isEmpty(codeGrossiste)&& !codeGrossiste.equals("ALL")) {
                predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), codeGrossiste));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<Long> q = getEntityManager().createQuery(cq);
            q.setMaxResults(1);
            return (int) Math.ceil(q.getSingleResult() * 0.8);
        } catch (Exception e) {
//            e.printStackTrace(System.err);
            return 0;
        }
    }

    List<FamilleArticleStatDTO> fetchDataForStatisticVenteRayons(LocalDate dtStart, LocalDate dtEnd, String query, String codeFamillle, TUser u, String codeRayon, String codeGrossiste) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<FamilleArticleStatDTO> cq = cb.createQuery(FamilleArticleStatDTO.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(FamilleArticleStatDTO.class,
                    root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.strCODE),
                    root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.strLIBELLEE),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICE)),
                    cb.sum(cb.prod(root.get(TPreenregistrementDetail_.prixAchat), root.get(TPreenregistrementDetail_.intQUANTITY))),
                    cb.sum(root.get(TPreenregistrementDetail_.montantTva)),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICEREMISE)),
                    root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.lgZONEGEOID)
            )).groupBy(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID));
            List<Predicate> predicates = famillePredicats(cb, root, join, dtStart, dtEnd, query, codeFamillle, u, codeRayon, codeGrossiste);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<FamilleArticleStatDTO> q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    List<FamilleArticleStatDTO> fetchDataForStatisticVenteGrossistes(LocalDate dtStart, LocalDate dtEnd, String query, String codeFamillle, TUser u, String codeRayon, String codeGrossiste) {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<FamilleArticleStatDTO> cq = cb.createQuery(FamilleArticleStatDTO.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(FamilleArticleStatDTO.class,
                    root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.strCODE),
                    root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.strLIBELLE),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICE)),
                    cb.sum(cb.prod(root.get(TPreenregistrementDetail_.prixAchat), root.get(TPreenregistrementDetail_.intQUANTITY))),
                    cb.sum(root.get(TPreenregistrementDetail_.montantTva)),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICEREMISE)),
                    root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID)
            )).groupBy(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID));
            List<Predicate> predicates = famillePredicats(cb, root, join, dtStart, dtEnd, query, codeFamillle, u, codeRayon, codeGrossiste);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<FamilleArticleStatDTO> q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    @Override
    public Pair<FamilleArticleStatDTO, List<FamilleArticleStatDTO>> statistiqueParRayons(String dtStart, String dtEnd, String codeFamile, String query, TUser u, String codeRayon, String codeGrossiste) {
        List<FamilleArticleStatDTO> list = new ArrayList<>();
        FamilleArticleStatDTO summary = new FamilleArticleStatDTO();
        try {
            LongAdder montantTTC = new LongAdder();
            LongAdder montantMarge = new LongAdder();
            LongAdder montantHT = new LongAdder();
            LongAdder montantAchat = new LongAdder();
            final LocalDate periode = LocalDate.parse(dtStart);
            final Integer montanttotalHt = totalMontantHT(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), query, codeFamile, u, codeRayon, codeGrossiste);
            fetchDataForStatisticVenteRayons(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), query, codeFamile, u, codeRayon, codeGrossiste).stream()
                    .sorted(comparator)
                    .forEach(x -> {
                        montantMarge.add(x.getMontantMarge());
                        montantTTC.add(x.getMontantTTC());
                        montantAchat.add(x.getMontantAchat());
                        montantHT.add(x.getMontantHT());
                        cumulStatisticRayons(periode, x, u);
                        Double p = new BigDecimal(Double.valueOf(x.getMontantHT()) / montanttotalHt).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
                        x.setPourcentageTH(p.intValue());
                        list.add(x);

                    });
            summary.setMontantCumulAchat(montantAchat.intValue());
            summary.setMontantCumulHT(montantHT.intValue());
            summary.setMontantCumulMarge(montantMarge.intValue());
            summary.setMontantCumulTTC(montantTTC.intValue());
            try {
               summary.setPourcentageTH((montanttotalHt / montanttotalHt) * 100); 
            } catch (ArithmeticException e) {
            }
            
            Double ux = new BigDecimal(Double.valueOf(summary.getMontantCumulMarge()) / summary.getMontantCumulHT()).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
            summary.setPourcentageCumulMage(ux.intValue());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return Pair.of(summary, list);

    }

    @Override
    public Pair<FamilleArticleStatDTO, List<FamilleArticleStatDTO>> statistiqueParGrossistes(String dtStart, String dtEnd, String codeFamile, String query, TUser u, String codeRayon, String codeGrossiste) {
        List<FamilleArticleStatDTO> list = new ArrayList<>();
        FamilleArticleStatDTO summary = new FamilleArticleStatDTO();
        try {
            LongAdder montantTTC = new LongAdder();
            LongAdder montantMarge = new LongAdder();
            LongAdder montantHT = new LongAdder();
            LongAdder montantAchat = new LongAdder();
            final LocalDate periode = LocalDate.parse(dtStart);
            final Integer montanttotalHt = totalMontantHT(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), query, codeFamile, u, codeRayon, codeGrossiste);
            fetchDataForStatisticVenteGrossistes(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), query, codeFamile, u, codeRayon, codeGrossiste).stream()
                    .sorted(comparator)
                    .forEach(x -> {
                        montantMarge.add(x.getMontantMarge());
                        montantTTC.add(x.getMontantTTC());
                        montantAchat.add(x.getMontantAchat());
                        montantHT.add(x.getMontantHT());
                        cumulStatisticGrossistes(periode, x, u);
                        Double p = new BigDecimal(Double.valueOf(x.getMontantHT()) / montanttotalHt).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
                        x.setPourcentageTH(p.intValue());
                        list.add(x);

                    });
            summary.setMontantCumulAchat(montantAchat.intValue());
            summary.setMontantCumulHT(montantHT.intValue());
            summary.setMontantCumulMarge(montantMarge.intValue());
            summary.setMontantCumulTTC(montantTTC.intValue());
            summary.setPourcentageTH((montanttotalHt / montanttotalHt) * 100);
            Double ux = new BigDecimal(Double.valueOf(summary.getMontantCumulMarge()) / summary.getMontantCumulHT()).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
            summary.setPourcentageCumulMage(ux.intValue());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return Pair.of(summary, list);

    }

    @Override
    public JSONObject statistiqueParRayonsView(String dtStart, String dtEnd, String codeFamile, String query, TUser u, String codeRayon, String codeGrossiste) throws JSONException {
         JSONObject json = new JSONObject();
        Pair<FamilleArticleStatDTO, List<FamilleArticleStatDTO>> pair = statistiqueParRayons(dtStart, dtEnd, codeFamile, query, u, codeRayon, codeGrossiste);
        List<FamilleArticleStatDTO> l = pair.getRight();
        json.put("success", true);
        json.put("total", l.size());
        json.put("data", new JSONArray(l));
        json.put("metaData", new JSONObject(pair.getLeft()));
        return json;
    }

    @Override
    public JSONObject statistiqueParGrossistesView(String dtStart, String dtEnd, String codeFamile, String query, TUser u, String codeRayon, String codeGrossiste) throws JSONException {
       JSONObject json = new JSONObject();
        Pair<FamilleArticleStatDTO, List<FamilleArticleStatDTO>> pair = statistiqueParGrossistes(dtStart, dtEnd, codeFamile, query, u, codeRayon, codeGrossiste);
        List<FamilleArticleStatDTO> l = pair.getRight();
        json.put("success", true);
        json.put("total", l.size());
        json.put("data", new JSONArray(l));
        json.put("metaData", new JSONObject(pair.getLeft()));
        return json;
    }
  FamilleArticleStatDTO cumulStatisticRayons(LocalDate periode, FamilleArticleStatDTO familleArticleStatDTO, TUser u) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<FamilleArticleStatDTO> cq = cb.createQuery(FamilleArticleStatDTO.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(FamilleArticleStatDTO.class,
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICE)),
                    cb.sum(cb.prod(root.get(TPreenregistrementDetail_.prixAchat), root.get(TPreenregistrementDetail_.intQUANTITY))),
                    cb.sum(root.get(TPreenregistrementDetail_.montantTva)),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICEREMISE))
            ));
            predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgZONEGEOID).get(TZoneGeographique_.lgZONEGEOID), familleArticleStatDTO.getFamilleId()));
            Predicate btw = cb.equal(cb.function("YEAR", Date.class, join.get(TPreenregistrement_.dtUPDATED)),
                    periode.getYear());
            predicates.add(btw);
            predicates.add(cb.equal(join.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID), emp));
            predicates.add(cb.equal(join.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.isFalse(join.get(TPreenregistrement_.bISCANCEL)));
            predicates.add(cb.greaterThan(join.get(TPreenregistrement_.intPRICE), 0));
            predicates.add(cb.notLike(join.get(TPreenregistrement_.lgTYPEVENTEID).get(TTypeVente_.lgTYPEVENTEID), DateConverter.DEPOT_EXTENSION));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<FamilleArticleStatDTO> q = getEntityManager().createQuery(cq);
            q.setMaxResults(1);
            FamilleArticleStatDTO t = q.getSingleResult();
            familleArticleStatDTO.setMontantCumulAchat(t.getMontantCumulAchat());
            familleArticleStatDTO.setMontantCumulTTC(t.getMontantCumulTTC());
            familleArticleStatDTO.setMontantCumulTva(t.getMontantCumulTva());
            familleArticleStatDTO.setMontantCumulHT(t.getMontantCumulHT());
            Double p = new BigDecimal(Double.valueOf(familleArticleStatDTO.getMontantHT()) / familleArticleStatDTO.getMontantCumulHT()).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
            familleArticleStatDTO.setValeurPeriode(p.intValue());
            return familleArticleStatDTO;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return familleArticleStatDTO;
        }
    }
 FamilleArticleStatDTO cumulStatisticGrossistes(LocalDate periode, FamilleArticleStatDTO familleArticleStatDTO, TUser u) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            TEmplacement emp = u.getLgEMPLACEMENTID();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<FamilleArticleStatDTO> cq = cb.createQuery(FamilleArticleStatDTO.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join(TPreenregistrementDetail_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(FamilleArticleStatDTO.class,
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICE)),
                    cb.sum(cb.prod(root.get(TPreenregistrementDetail_.prixAchat), root.get(TPreenregistrementDetail_.intQUANTITY))),
                    cb.sum(root.get(TPreenregistrementDetail_.montantTva)),
                    cb.sum(root.get(TPreenregistrementDetail_.intPRICEREMISE))
            ));
            predicates.add(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), familleArticleStatDTO.getFamilleId()));
            Predicate btw = cb.equal(cb.function("YEAR", Date.class, join.get(TPreenregistrement_.dtUPDATED)),
                    periode.getYear());
            predicates.add(btw);
            predicates.add(cb.equal(join.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID), emp));
            predicates.add(cb.equal(join.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.isFalse(join.get(TPreenregistrement_.bISCANCEL)));
            predicates.add(cb.greaterThan(join.get(TPreenregistrement_.intPRICE), 0));
            predicates.add(cb.notLike(join.get(TPreenregistrement_.lgTYPEVENTEID).get(TTypeVente_.lgTYPEVENTEID), DateConverter.DEPOT_EXTENSION));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<FamilleArticleStatDTO> q = getEntityManager().createQuery(cq);
            q.setMaxResults(1);
            FamilleArticleStatDTO t = q.getSingleResult();
            familleArticleStatDTO.setMontantCumulAchat(t.getMontantCumulAchat());
            familleArticleStatDTO.setMontantCumulTTC(t.getMontantCumulTTC());
            familleArticleStatDTO.setMontantCumulTva(t.getMontantCumulTva());
            familleArticleStatDTO.setMontantCumulHT(t.getMontantCumulHT());
            Double p = new BigDecimal(Double.valueOf(familleArticleStatDTO.getMontantHT()) / familleArticleStatDTO.getMontantCumulHT()).setScale(2, RoundingMode.HALF_UP).doubleValue() * 100;
            familleArticleStatDTO.setValeurPeriode(p.intValue());
            return familleArticleStatDTO;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return familleArticleStatDTO;
        }
    }
}
