/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.AchatDTO;
import commonTasks.dto.Params;
import commonTasks.dto.RecapActiviteDTO;
import dal.MvtTransaction;
import dal.MvtTransaction_;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.DashBoardService;
import util.DateConverter;

/**
 *
 * @author DICI
 */
@Stateless
public class DashBoardServiceImpl implements DashBoardService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public RecapActiviteDTO donneesRecapActivite(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query) {
        RecapActiviteDTO recapActivite = new RecapActiviteDTO();
        List<MvtTransaction> mvtTransactions = findAllsTransaction(dtStart, dtEnd, emplacementId, tu);
        List<Params> reglements = new ArrayList<>();
        List<Params> mvtsCaisse = new ArrayList<>();
        List<AchatDTO> achats = new ArrayList<>();
        LongAdder montantTTC = new LongAdder(),
                montantTotalMvt = new LongAdder(),
                montantTotalAchat = new LongAdder(),
                montantTotalHtAchat = new LongAdder(),
                montantTotalTvaAchat = new LongAdder(),
                montantTVA = new LongAdder();
        LongAdder montantNet = new LongAdder();
        LongAdder montantRemise = new LongAdder();
        LongAdder montantCmptant = new LongAdder();
        LongAdder montantCredit = new LongAdder();
        LongAdder montantMarge = new LongAdder();
        LongAdder montantVir = new LongAdder();
        LongAdder montantCb = new LongAdder();
        LongAdder montantCh = new LongAdder();
        LongAdder montantDiff = new LongAdder();
        LongAdder montantEsp = new LongAdder();
      
        mvtTransactions.forEach(v -> {
            switch (v.getTypeTransaction()) {
                case VENTE_COMPTANT:
                case VENTE_CREDIT:
                    montantTTC.add(v.getMontant());
                    montantNet.add(v.getMontantNet());
                    montantRemise.add(v.getMontantRemise());
                    montantTVA.add(v.getMontantTva());
                    montantCredit.add(v.getMontantCredit() + v.getMontantRestant());
                    montantDiff.add(v.getMontantRestant());
                    montantCmptant.add(v.getMontantRegle());
                    montantMarge.add(v.getMarge());
                    switch (v.getReglement().getLgTYPEREGLEMENTID()) {
                        case DateConverter.MODE_ESP:
                            montantEsp.add(v.getMontantRegle());
                            break;
                        case DateConverter.MODE_CB:
                            montantCb.add(v.getMontantRegle());
                            break;
                        case DateConverter.MODE_CHEQUE:
                            montantCh.add(v.getMontantRegle());
                            break;
                        case DateConverter.MODE_VIREMENT:
                            montantVir.add(v.getMontantRegle());
                            break;

                        default:
                            break;
                    }

                    break;
                case ENTREE:
                case SORTIE:
                    mvtsCaisse.add(new Params(v.gettTypeMvtCaisse().getStrNAME(), v.getMontantRegle()));
                    montantTotalMvt.add(v.getMontantRegle());
                    break;
                case ACHAT:
                    AchatDTO achat = new AchatDTO();
                    achat.setLibelleGroupeGrossiste(v.getGrossiste().getGroupeId().getLibelle());
                    achat.setMontantTTC(v.getMontant());
                    achat.setMontantHT(v.getMontantNet());
                    achat.setMontantTVA(v.getMontantTva());
                    achats.add(achat);
                    montantTotalAchat.add(v.getMontant());
                    montantTotalHtAchat.add(v.getMontantNet());
                    montantTotalTvaAchat.add(v.getMontantTva());
                    break;
                default:
                    break;
            }

        });
        Map<String, Integer> mvts = mvtsCaisse.stream().collect(Collectors.groupingBy(Params::getRef, Collectors.summingInt(Params::getValue)));
        List<Params> mvt = new ArrayList<>();
        mvts.forEach((key, value) -> {
            mvt.add(new Params(key, value));
        });
        recapActivite.setMvtsCaisse(mvt);
        List<AchatDTO> _achats = new ArrayList<>();
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
            _achats.add(o);
        });
        recapActivite.setAchats(_achats);
        recapActivite.setMontantTotalMvt(montantTotalMvt.intValue());
        Integer montCr = montantCredit.intValue();
        Integer montEsp = montantCmptant.intValue();
        recapActivite.setMontantCredit(montCr);
        recapActivite.setMontantEsp(montEsp);
        Integer net = montantNet.intValue();
        recapActivite.setMontantNet(net);
        Integer monttc = montantTTC.intValue();
        Integer montantTva_ = montantTVA.intValue();
        recapActivite.setMontantTVA(montantTva_);
        recapActivite.setMontantHT(monttc - montantTva_);
        recapActivite.setMontantTTC(monttc);
        recapActivite.setMontantRemise(montantRemise.intValue());
        recapActivite.setMarge(montantMarge.intValue());
        Integer totalAchat = montantTotalAchat.intValue();
        if (totalAchat > 0) {
            double ratio = Double.valueOf(net) / totalAchat;
            recapActivite.setRatio(new BigDecimal(ratio).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        recapActivite.setMontantTotalHT(montantTotalHtAchat.intValue());
        recapActivite.setMontantTotalTTC(totalAchat);
        recapActivite.setMontantTotalTVA(montantTotalTvaAchat.intValue());
        try {
            int pourEp = (int) Math.ceil(Double.valueOf(montEsp) * 100 / Math.abs(net));
            int pourCr = (int) Math.ceil(Double.valueOf(montCr) * 100 / Math.abs(net));
            recapActivite.setPourcentageEsp(pourEp);
            recapActivite.setPourcentageCredit(pourCr);
        } catch (Exception e) {
        }
        reglements.add(new Params("Espèce", montantEsp.intValue()));
        reglements.add(new Params("Carte bancaire", montantCb.intValue()));
        reglements.add(new Params("Chèque", montantCh.intValue()));
        reglements.add(new Params("Différé", montantDiff.intValue()));
        reglements.add(new Params("Virement", montantVir.intValue()));

        recapActivite.setReglements(reglements);
        return recapActivite;

    }

    @Override
    public JSONObject donneesRecapActiviteView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query) throws JSONException {
        return new JSONObject().put("data", new JSONObject(donneesRecapActivite(dtStart, dtEnd, emplacementId, tu, query)));
    }

    private List<MvtTransaction> findAllsTransaction(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu) {

        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<MvtTransaction> cq = cb.createQuery(MvtTransaction.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(root).orderBy(cb.desc(root.get(MvtTransaction_.createdAt)));
            predicates.add(cb.and(cb.equal(root.get(MvtTransaction_.magasin).get(TEmplacement_.lgEMPLACEMENTID),
                    emplacementId)));
            Predicate btw = cb.between(
                    root.get(MvtTransaction_.mvtDate),
                    dtStart,
                    dtEnd);
            predicates.add(cb.and(btw));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<MvtTransaction> q = getEntityManager().createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }

    }

    private List<TTiersPayant> getPayants(String name) {
        return getEntityManager().createQuery("SELECT o FROM TTiersPayant o WHERE (o.strNAME LIKE ?1 OR o.lgTYPETIERSPAYANTID.strLIBELLETYPETIERSPAYANT LIKE ?1) ").setParameter(1, name + "%").getResultList();
    }

    private TTiersPayant findPayantById(String id) {
        try {
            return getEntityManager().find(TTiersPayant.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Params> donneesReglementsTp(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query, int start, int limit, boolean all) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Params> cq = cb.createQuery(Params.class);
            Root<TDossierReglement> root = cq.from(TDossierReglement.class);
            //String description, String ref, String refParent, long value, long nbreClient, long nbreBons
            cq.select(cb.construct(Params.class,
                    root.get(TDossierReglement_.lgFACTUREID).get(TFacture_.strCODEFACTURE),
                    root.get(TDossierReglement_.strORGANISMEID),
                    root.get(TDossierReglement_.dblAMOUNT),
                    root.get(TDossierReglement_.lgFACTUREID).get(TFacture_.dblMONTANTCMDE),
                    root.get(TDossierReglement_.lgFACTUREID).get(TFacture_.dblMONTANTRESTANT)
            ));
//            Join<TDossierReglement, TFacture> fac = root.join(TDossierReglement_.lgFACTUREID, JoinType.INNER);
            predicates.add(cb.equal(root.get(TDossierReglement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID),
                    emplacementId));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TDossierReglement_.dtCREATED)), java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (query != null && !"".equals(query)) {
                List<String> or = getPayants(query).stream().map(TTiersPayant::getLgTIERSPAYANTID).collect(Collectors.toList());
                predicates.add(root.get(TDossierReglement_.strORGANISMEID).in(or));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<Params> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            List<Params> list = q.getResultList();
//            list.sort(comparator);
            return list.stream().map(x -> new Params(x, findPayantById(x.getRef()))).sorted(comparator).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Params> donneesCreditAccordes(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query, int start, int limit, boolean all) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Params> cq = cb.createQuery(Params.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> j = root.join(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.construct(Params.class,
                    root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.strNAME),
                    root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTYPETIERSPAYANTID)
                            .get(TTypeTiersPayant_.strLIBELLETYPETIERSPAYANT),
                    cb.sum(j.get(TPreenregistrement_.intPRICE)),
                    cb.countDistinct(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID)),
                    cb.countDistinct(j.get(TPreenregistrement_.lgPREENREGISTREMENTID))
            )).groupBy(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgTIERSPAYANTID));
            predicates.add(cb.equal(j.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID),
                    emplacementId));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementCompteClientTiersPayent_.dtUPDATED)), java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (query != null && !"".equals(query)) {
                Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cp = root.join(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID, JoinType.INNER);
                predicates.add(cb.or(cb.like(cp.get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.strNAME), query + "%"),
                        cb.like(cp.get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTYPETIERSPAYANTID).get(TTypeTiersPayant_.strLIBELLETYPETIERSPAYANT), query + "%")
                ));

            }
            predicates.add(cb.equal(j.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.strSTATUTFACTURE), DateConverter.STATUT_FACTURE_UNPAID));
            predicates.add(cb.greaterThan(j.get(TPreenregistrement_.intPRICE), 0));
            predicates.add(cb.notEqual(j.get(TPreenregistrement_.lgTYPEVENTEID).get(TTypeVente_.lgTYPEVENTEID), DateConverter.DEPOT_EXTENSION));
            predicates.add(cb.equal(j.get(TPreenregistrement_.strTYPEVENTE), DateConverter.VENTE_ASSURANCE));
            predicates.add(cb.isFalse(j.get(TPreenregistrement_.bISCANCEL)));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<Params> q = getEntityManager().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            List<Params> list = q.getResultList();
            list.sort(comparator);
            return list;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    private long donneesCreditAccordes(LocalDate dtStart, LocalDate dtEnd, String emplacementId, String query) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq.from(TPreenregistrementCompteClientTiersPayent.class);
            Join<TPreenregistrementCompteClientTiersPayent, TPreenregistrement> j = root.join(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID, JoinType.INNER);
            cq.select(cb.countDistinct(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID).get(TCompteClientTiersPayant_.lgTIERSPAYANTID)));
            predicates.add(cb.equal(j.get(TPreenregistrement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID),
                    emplacementId));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementCompteClientTiersPayent_.dtUPDATED)), java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (query != null && !"".equals(query)) {
                Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> cp = root.join(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID, JoinType.INNER);
                predicates.add(cb.or(cb.like(cp.get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.strNAME), query + "%"),
                        cb.like(cp.get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTYPETIERSPAYANTID).get(TTypeTiersPayant_.strLIBELLETYPETIERSPAYANT), query + "%")
                ));

            }
            predicates.add(cb.equal(j.get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
            predicates.add(cb.greaterThan(j.get(TPreenregistrement_.intPRICE), 0));
            predicates.add(cb.notEqual(j.get(TPreenregistrement_.lgTYPEVENTEID).get(TTypeVente_.lgTYPEVENTEID), DateConverter.DEPOT_EXTENSION));
            predicates.add(cb.equal(j.get(TPreenregistrement_.strTYPEVENTE), DateConverter.VENTE_ASSURANCE));
            predicates.add(cb.isFalse(j.get(TPreenregistrement_.bISCANCEL)));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }
    }

    public long donneesReglementsTp(LocalDate dtStart, LocalDate dtEnd, String emplacementId, String query) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TDossierReglement> root = cq.from(TDossierReglement.class);
            //String description, String ref, String refParent, long value, long nbreClient, long nbreBons
            cq.select(cb.count(root));
//            Join<TDossierReglement, TFacture> fac = root.join(TDossierReglement_.lgFACTUREID, JoinType.INNER);
            predicates.add(cb.equal(root.get(TDossierReglement_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID),
                    emplacementId));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TDossierReglement_.dtCREATED)), java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (query != null && !"".equals(query)) {
                List<String> or = getPayants(query).stream().map(TTiersPayant::getLgTIERSPAYANTID).collect(Collectors.toList());
                predicates.add(root.get(TDossierReglement_.strORGANISMEID).in(or));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEntityManager().createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }
    }
    Comparator<Params> comparator = Comparator.comparing(Params::getDescription);

    @Override
    public JSONObject donneesReglementsTpView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query, int start, int limit, boolean all) throws JSONException {
        JSONObject json = new JSONObject();
        long count = donneesReglementsTp(dtStart, dtEnd, emplacementId, query);
        return json.put("total", count).put("data", new JSONArray(donneesReglementsTp(dtStart, dtEnd, emplacementId, tu, query, start, limit, all)));

    }

    @Override
    public JSONObject donneesCreditAccordesView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query, int start, int limit, boolean all) throws JSONException {
        JSONObject json = new JSONObject();
        long count = donneesCreditAccordes(dtStart, dtEnd, emplacementId, query);
        return json.put("total", count).put("data", new JSONArray(donneesCreditAccordes(dtStart, dtEnd, emplacementId, tu, query, start, limit, all)));

    }

}
