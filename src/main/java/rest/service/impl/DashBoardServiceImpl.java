/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.AchatDTO;
import commonTasks.dto.BalanceDTO;
import commonTasks.dto.Params;
import commonTasks.dto.RecapActiviteCreditDTO;
import commonTasks.dto.RecapActiviteDTO;
import commonTasks.dto.RecapActiviteReglementDTO;
import commonTasks.dto.TvaDTO;
import dal.MvtTransaction;
import dal.MvtTransaction_;
import dal.TClient_;
import dal.TCompteClientTiersPayant;
import dal.TCompteClientTiersPayant_;
import dal.TCompteClient_;
import dal.TDossierReglement;
import dal.TDossierReglement_;
import dal.TEmplacement_;
import dal.TFacture_;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementCompteClientTiersPayent_;
import dal.TPreenregistrement_;
import dal.TTiersPayant;
import dal.TTiersPayant_;
import dal.TTypeTiersPayant_;
import dal.TTypeVente_;
import dal.TUser;
import dal.TUser_;
import dal.enumeration.TypeTransaction;
import static dal.enumeration.TypeTransaction.ACHAT;
import static dal.enumeration.TypeTransaction.ENTREE;
import static dal.enumeration.TypeTransaction.SORTIE;
import static dal.enumeration.TypeTransaction.VENTE_COMPTANT;
import static dal.enumeration.TypeTransaction.VENTE_CREDIT;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.BalanceService;
import rest.service.DashBoardService;
import rest.service.dto.BalanceParamsDTO;
import util.Constant;
import util.DateConverter;

/**
 *
 * @author DICI
 */
@Stateless
public class DashBoardServiceImpl implements DashBoardService {

    private static final Logger LOG = Logger.getLogger(DashBoardServiceImpl.class.getName());

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private BalanceService balanceService;

    @Override
    public JSONObject donneesRecapActiviteView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu,
            String query) throws JSONException {
        return new JSONObject().put("data", new JSONObject(donneesRecapActivite(dtStart, dtEnd, emplacementId, tu)));
    }

    private List<MvtTransaction> findAllsTransaction(LocalDate dtStart, LocalDate dtEnd, String emplacementId) {

        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<MvtTransaction> cq = cb.createQuery(MvtTransaction.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(root).orderBy(cb.desc(root.get(MvtTransaction_.createdAt)));
            predicates
                    .add(cb.equal(root.get(MvtTransaction_.magasin).get(TEmplacement_.lgEMPLACEMENTID), emplacementId));
            Predicate btw = cb.between(root.get(MvtTransaction_.mvtDate), dtStart, dtEnd);
            predicates.add(btw);
            In<TypeTransaction> in = cb.in(root.get(MvtTransaction_.typeTransaction));
            List<TypeTransaction> status = List.of(TypeTransaction.ACHAT, TypeTransaction.ENTREE,
                    TypeTransaction.SORTIE);
            status.forEach(s -> in.value(s));
            predicates.add(in);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            TypedQuery<MvtTransaction> q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    private List<TTiersPayant> getPayants(String name) {
        return getEntityManager().createQuery(
                "SELECT o FROM TTiersPayant o WHERE (o.strNAME LIKE ?1 OR o.lgTYPETIERSPAYANTID.strLIBELLETYPETIERSPAYANT LIKE ?1) ")
                .setParameter(1, name + "%").getResultList();
    }

    private TTiersPayant findPayantById(String id) {
        try {
            return getEntityManager().find(TTiersPayant.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Params> donneesReglementsTp(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu,
            String query, int start, int limit, boolean all) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Params> cq = cb.createQuery(Params.class);
            Root<TDossierReglement> root = cq.from(TDossierReglement.class);
            // String description, String ref, String refParent, long value, long nbreClient, long nbreBons
            cq.select(cb.construct(Params.class, root.get(TDossierReglement_.lgFACTUREID).get(TFacture_.strCODEFACTURE),
                    root.get(TDossierReglement_.strORGANISMEID), root.get(TDossierReglement_.dblAMOUNT),
                    root.get(TDossierReglement_.lgFACTUREID).get(TFacture_.dblMONTANTCMDE),
                    root.get(TDossierReglement_.lgFACTUREID).get(TFacture_.dblMONTANTRESTANT)));
            // Join<TDossierReglement, TFacture> fac = root.join(TDossierReglement_.lgFACTUREID, JoinType.INNER);
            predicates.add(cb.equal(root.get(TDossierReglement_.lgUSERID).get(TUser_.lgEMPLACEMENTID)
                    .get(TEmplacement_.lgEMPLACEMENTID), emplacementId));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TDossierReglement_.dtCREATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (query != null && !"".equals(query)) {
                List<String> or = getPayants(query).stream().map(TTiersPayant::getLgTIERSPAYANTID)
                        .collect(Collectors.toList());
                predicates.add(root.get(TDossierReglement_.strORGANISMEID).in(or));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<Params> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            List<Params> list = q.getResultList();
            // list.sort(comparator);
            return list.stream().map(x -> new Params(x, findPayantById(x.getRef()))).sorted(comparator)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RecapActiviteCreditDTO> donneesCreditAccordes(LocalDate dtStart, LocalDate dtEnd, String emplacementId,
            TUser tu, String query, int start, int limit, boolean all) {
        try {

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<RecapActiviteCreditDTO> cq = cb.createQuery(RecapActiviteCreditDTO.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq
                    .from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> j = root
                    .join(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(RecapActiviteCreditDTO.class,
                    root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.strNAME),
                    root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTYPETIERSPAYANTID)
                            .get(TTypeTiersPayant_.strLIBELLETYPETIERSPAYANT),
                    cb.sumAsLong(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE)),
                    cb.countDistinct(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID)
                            .get(TClient_.lgCLIENTID)),
                    cb.countDistinct(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID)
                            .get(TPreenregistrement_.lgPREENREGISTREMENTID))))
                    .groupBy(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTIERSPAYANTID));
            List<Predicate> predicates = donneesCreditAccordesPredicat(cb, root, j, dtStart, dtEnd, emplacementId,
                    query);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            TypedQuery<RecapActiviteCreditDTO> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            List<RecapActiviteCreditDTO> list = q.getResultList();
            list.sort(comparatorCredit);
            return list;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private List<Predicate> donneesCreditAccordesPredicat(CriteriaBuilder cb,
            Root<TPreenregistrementCompteClientTiersPayent> root,
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> j, LocalDate dtStart, LocalDate dtEnd,
            String emplacementId, String query) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(
                j.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID),
                emplacementId));

        predicates.add(cb.between(
                cb.function("DATE", Date.class,
                        root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID)
                                .get(TPreenregistrement_.dtUPDATED)),
                java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd)));
        if (StringUtils.isNotEmpty(query)) {
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cp = root
                    .join(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID, JoinType.INNER);
            predicates.add(cb.or(
                    cb.like(cp.get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.strNAME), query + "%"),
                    cb.like(cp.get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTYPETIERSPAYANTID)
                            .get(TTypeTiersPayant_.strLIBELLETYPETIERSPAYANT), query + "%")));

        }
        predicates.add(cb.equal(j.get(TPreenregistrement_.strSTATUT), Constant.STATUT_IS_CLOSED));
        predicates.add(cb.greaterThan(j.get(TPreenregistrement_.intPRICE), 0));
        predicates.add(cb.notEqual(j.get(TPreenregistrement_.lgTYPEVENTEID).get(TTypeVente_.lgTYPEVENTEID),
                Constant.DEPOT_EXTENSION));
        predicates.add(cb.equal(j.get(TPreenregistrement_.strTYPEVENTE), Constant.VENTE_ASSURANCE));
        predicates.add(cb.isFalse(j.get(TPreenregistrement_.bISCANCEL)));
        return predicates;
    }

    private long donneesCreditAccordes(LocalDate dtStart, LocalDate dtEnd, String emplacementId, String query) {
        try {

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq
                    .from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> j = root
                    .join(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.countDistinct(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                    .get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTIERSPAYANTID)));
            List<Predicate> predicates = donneesCreditAccordesPredicat(cb, root, j, dtStart, dtEnd, emplacementId,
                    query);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            Query q = getEntityManager().createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    public long donneesReglementsTp(LocalDate dtStart, LocalDate dtEnd, String emplacementId, String query) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TDossierReglement> root = cq.from(TDossierReglement.class);
            // String description, String ref, String refParent, long value, long nbreClient, long nbreBons
            cq.select(cb.count(root));
            predicates.add(cb.equal(root.get(TDossierReglement_.lgUSERID).get(TUser_.lgEMPLACEMENTID)
                    .get(TEmplacement_.lgEMPLACEMENTID), emplacementId));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TDossierReglement_.dtCREATED)),
                    java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (query != null && !"".equals(query)) {
                List<String> or = getPayants(query).stream().map(TTiersPayant::getLgTIERSPAYANTID)
                        .collect(Collectors.toList());
                predicates.add(root.get(TDossierReglement_.strORGANISMEID).in(or));
            }
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            Query q = getEntityManager().createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    Comparator<Params> comparator = Comparator.comparing(Params::getDescription);
    Comparator<RecapActiviteCreditDTO> comparatorCredit = Comparator
            .comparing(RecapActiviteCreditDTO::getLibelleTiersPayant);

    @Override
    public JSONObject donneesReglementsTpView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu,
            String query, int start, int limit, boolean all) throws JSONException {
        JSONObject json = new JSONObject();
        long count = donneesReglementsTp(dtStart, dtEnd, emplacementId, query);
        return json.put("total", count).put("data",
                new JSONArray(donneesReglementsTp(dtStart, dtEnd, emplacementId, tu, query, start, limit, all)));

    }

    @Override
    public JSONObject donneesCreditAccordesView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu,
            String query, int start, int limit, boolean all) throws JSONException {
        JSONObject json = new JSONObject();
        long count = donneesCreditAccordes(dtStart, dtEnd, emplacementId, query);
        return json.put("total", count).put("data",
                new JSONArray(donneesCreditAccordes(dtStart, dtEnd, emplacementId, tu, query, start, limit, all)));

    }

    RecapActiviteDTO buildVenteData(LocalDate dtStart, LocalDate dtEnd, String emplacementId) {
        RecapActiviteDTO recapActivite = new RecapActiviteDTO();
        List<BalanceDTO> balanceVente = this.balanceService.recapBalance(BalanceParamsDTO.builder()
                .dtStart(dtStart.toString()).dtEnd(dtEnd.toString()).emplacementId(emplacementId).build());
        List<TvaDTO> tvas = this.balanceService.statistiqueTvaPeriodique(BalanceParamsDTO.builder()
                .dtStart(dtStart.toString()).dtEnd(dtEnd.toString()).emplacementId(emplacementId).build());
        for (TvaDTO tva : tvas) {
            recapActivite.setMontantHT(tva.getMontantHt() + recapActivite.getMontantHT());
            recapActivite.setMontantTVA(recapActivite.getMontantTVA() + tva.getMontantTva());
            recapActivite.setMontantTTC(tva.getMontantTtc() + recapActivite.getMontantTTC());
        }
        long montantEsp = 0;
        long montantCb = 0;
        long montantDiff = 0;
        long montantVir = 0;
        long montantMobilePayment = 0;
        long montantCh = 0;
        List<RecapActiviteReglementDTO> reglements = new ArrayList<>();
        for (BalanceDTO b : balanceVente) {
            recapActivite.setMontantNet(b.getMontantNet() + recapActivite.getMontantNet());
            recapActivite.setMontantRemise(b.getMontantRemise() + recapActivite.getMontantRemise());
            recapActivite.setMontantRegle(recapActivite.getMontantRegle() + b.getMontantRegle());
            recapActivite.setMontantEsp(b.getMontantRegle() + recapActivite.getMontantEsp());
            recapActivite.setMontantCredit(recapActivite.getMontantCredit() + b.getMontantDiff() + b.getMontantTp());
            recapActivite.setMarge(b.getMarge() + recapActivite.getMarge());
            montantCb += b.getMontantCB();
            montantEsp += b.getMontantEsp();
            montantDiff += b.getMontantDiff();
            montantMobilePayment += b.getMontantMobilePayment();
            montantCh += b.getMontantCheque();
            montantVir += b.getMontantVirement();

        }
        if (montantEsp != 0) {
            reglements.add(new RecapActiviteReglementDTO("Espèce", montantEsp));
        }
        if (montantMobilePayment != 0) {
            reglements.add(new RecapActiviteReglementDTO("Mobile", montantMobilePayment));
        }
        if (montantCb != 0) {
            reglements.add(new RecapActiviteReglementDTO("Carte bancaire", montantCb));
        }
        if (montantCh != 0) {
            reglements.add(new RecapActiviteReglementDTO("Chèque", montantCh));
        }
        if (montantDiff != 0) {
            reglements.add(new RecapActiviteReglementDTO("Différé", montantDiff));
        }
        if (montantVir != 0) {
            reglements.add(new RecapActiviteReglementDTO("Virement", montantVir));
        }
        recapActivite.setReglements(reglements);
        return recapActivite;
    }

    @Override
    public RecapActiviteDTO donneesRecapActivite(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu) {
        RecapActiviteDTO recapActivite = buildVenteData(dtStart, dtEnd, emplacementId);
        List<MvtTransaction> mvtTransactions = findAllsTransaction(dtStart, dtEnd, emplacementId);

        List<RecapActiviteReglementDTO> mvtsCaisse = new ArrayList<>();
        List<AchatDTO> achats = new ArrayList<>();

        long montantTotalMvt = 0;
        long montantTotalAchat = 0;
        long montantTotalHtAchat = 0;
        long montantTotalTvaAchat = 0;

        for (MvtTransaction v : mvtTransactions) {

            switch (v.getTypeTransaction()) {

            case ENTREE:
            case SORTIE:
                mvtsCaisse.add(new RecapActiviteReglementDTO(v.gettTypeMvtCaisse().getStrNAME(), v.getMontantRegle()));
                montantTotalMvt += v.getMontantRegle();
                break;
            case ACHAT:
                AchatDTO achat = new AchatDTO();
                achat.setLibelleGroupeGrossiste(Objects.nonNull(v.getGrossiste().getGroupeId())
                        ? v.getGrossiste().getGroupeId().getLibelle() : v.getGrossiste().getStrLIBELLE());
                achat.setMontantTTC(v.getMontant());
                achat.setMontantHT(v.getMontantNet());
                achat.setMontantTVA(v.getMontantTva());
                achats.add(achat);
                montantTotalAchat += v.getMontant();
                montantTotalHtAchat += v.getMontantNet();
                montantTotalTvaAchat += v.getMontantTva();

                break;
            default:
                break;
            }
        }

        Map<String, Long> mvts = mvtsCaisse.stream().collect(Collectors.groupingBy(
                RecapActiviteReglementDTO::getLibelle, Collectors.summingLong(RecapActiviteReglementDTO::getMontant)));
        List<RecapActiviteReglementDTO> mvt = new ArrayList<>();
        mvts.forEach((key, value) -> {
            mvt.add(new RecapActiviteReglementDTO(key, value));
        });
        recapActivite.setMvtsCaisse(mvt);
        List<AchatDTO> achatsglobal = new ArrayList<>();
        achats.stream().collect(Collectors.groupingBy(AchatDTO::getLibelleGroupeGrossiste)).forEach((k, v) -> {
            AchatDTO o = new AchatDTO();
            o.setLibelleGroupeGrossiste(k);
            LongAdder montantAchatTTC = new LongAdder();
            LongAdder montantAchatTh = new LongAdder();
            LongAdder montantAchatTva = new LongAdder();
            v.forEach(b -> {
                montantAchatTTC.add(b.getMontantTTC());
                montantAchatTh.add(b.getMontantHT());
                montantAchatTva.add(b.getMontantTVA());
            });
            o.setMontantHT(montantAchatTh.intValue());
            o.setMontantTTC(montantAchatTTC.intValue());
            o.setMontantTVA(montantAchatTva.intValue());
            achatsglobal.add(o);
        });
        recapActivite.setAchats(achatsglobal);
        recapActivite.setMontantTotalMvt(montantTotalMvt);

        long totalAchat = montantTotalAchat;
        if (totalAchat > 0) {
            double ratio = Double.valueOf(recapActivite.getMontantNet()) / totalAchat;
            recapActivite.setRatio(BigDecimal.valueOf(ratio).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        recapActivite.setMontantTotalHT(montantTotalHtAchat);
        recapActivite.setMontantTotalTTC(totalAchat);
        recapActivite.setMontantTotalTVA(montantTotalTvaAchat);
        try {
            int pourEp = (int) Math.ceil(
                    Double.valueOf(recapActivite.getMontantEsp()) * 100 / Math.abs(recapActivite.getMontantNet()));
            int pourCr = (int) Math.ceil(
                    Double.valueOf(recapActivite.getMontantCredit()) * 100 / Math.abs(recapActivite.getMontantNet()));
            recapActivite.setPourcentageEsp(pourEp);
            recapActivite.setPourcentageCredit(pourCr);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

        return recapActivite;

    }

    @Override
    public RecapActiviteCreditDTO donneesRecapTotataux(LocalDate dtStart, LocalDate dtEnd, TUser tu, String query) {
        try {

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<RecapActiviteCreditDTO> cq = cb.createQuery(RecapActiviteCreditDTO.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq
                    .from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> j = root
                    .join(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(RecapActiviteCreditDTO.class,
                    cb.sumAsLong(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE)),
                    cb.countDistinct(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID)
                            .get(TClient_.lgCLIENTID)),
                    cb.countDistinct(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID)
                            .get(TPreenregistrement_.lgPREENREGISTREMENTID))));
            List<Predicate> predicates = donneesCreditAccordesPredicat(cb, root, j, dtStart, dtEnd,
                    tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), query);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            TypedQuery<RecapActiviteCreditDTO> q = getEntityManager().createQuery(cq);

            return q.getSingleResult();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new RecapActiviteCreditDTO(0, 0, 0);
        }
    }

}
