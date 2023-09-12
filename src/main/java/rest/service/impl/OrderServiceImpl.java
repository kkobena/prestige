/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import bll.common.Parameter;
import commonTasks.dto.ArticleDTO;
import commonTasks.dto.GenererFactureDTO;
import commonTasks.dto.Params;
import commonTasks.dto.RuptureDTO;
import commonTasks.dto.RuptureDetailDTO;
import dal.*;
import dal.enumeration.Canal;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
import javax.persistence.criteria.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.LogService;
import rest.service.NotificationService;
import rest.service.OrderService;
import rest.service.dto.*;
import toolkits.parameters.commonparameter;
import util.*;

/**
 * @author DICI
 */
@Stateless
public class OrderServiceImpl implements OrderService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    private @EJB LogService logService;
    @EJB
    NotificationService notificationService;

    public EntityManager getEmg() {
        return em;
    }

    private TFamilleStock getTProductItemStock(String produitId, String emp) {
        try {
            TypedQuery<TFamilleStock> q = getEmg().createQuery(
                    "SELECT t FROM TFamilleStock t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgFAMILLEID.strSTATUT='enable'  AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT ='enable' ",
                    TFamilleStock.class).setParameter(1, produitId).setParameter(2, emp);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

    private TFamilleStock getTProductItemStock(String produitId) {
        try {
            TypedQuery<TFamilleStock> q = getEmg().createQuery(
                    "SELECT t FROM TFamilleStock t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1   AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT ='enable' ",
                    TFamilleStock.class).setParameter(1, produitId).setParameter(2, "1");
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public JSONObject creerBonLivraison(Params params) throws JSONException {
        JSONObject json = new JSONObject();
        List<String> erro = new ArrayList<>();
        try {
            TOrder OTOrder = getEmg().find(TOrder.class, params.getRefParent());
            if (OTOrder == null) {
                return json.put("success", false).put("msg", "Echec: La commande n'existe pas");
            }
            TGrossiste grossiste = OTOrder.getLgGROSSISTEID();
            if (isRefBLExistForGrossiste(params.getRef(), grossiste.getLgGROSSISTEID())) {
                return json.put("success", false).put("msg", "Cette référence a déjà été utilisé pour ce grossiste");
            }
            TEmplacement emplacement = params.getOperateur().getLgEMPLACEMENTID();
            String emp = emplacement.getLgEMPLACEMENTID();
            TBonLivraison OTBonLivraison = createBL(OTOrder, params.getOperateur(), params.getRef(),
                    DateConverter.convertLocalDateToDate(LocalDate.parse(params.getDtStart())), params.getValue(),
                    params.getValueTwo());
            List<TOrderDetail> ListTOrderDetail = getTOrderDetail(params.getRefParent(), DateConverter.PASSE);
            LongAdder montant = new LongAdder();
            LongAdder count = new LongAdder();
            LongAdder count2 = new LongAdder();
            ListTOrderDetail.forEach((d) -> {
                TFamille famille = d.getLgFAMILLEID();
                TFamilleStock stock = getTProductItemStock(famille.getLgFAMILLEID(), emp);
                if (stock != null) {

                    createBLDetail(OTBonLivraison, grossiste, famille, d, famille.getLgZONEGEOID(),
                            stock.getIntNUMBERAVAILABLE());
                    d.setStrSTATUT(Parameter.STATUT_ENTREE_STOCK);
                    d.setDtUPDATED(new Date());
                    d.setIntORERSTATUS((short) 4);
                    getEmg().merge(d);
                    count.increment();
                    montant.add(d.getIntPRICE());
                } else {
                    count2.increment();
                    erro.add(famille.getIntCIP());
                }
            });
            OTOrder.setStrSTATUT(Constant.STATUT_IS_CLOSED);
            OTOrder.setIntPRICE(montant.intValue());
            OTOrder.setDtUPDATED(new Date());
            getEmg().merge(OTOrder);
            return json.put("success", true).put("count", count.intValue()).put("nb", count2.intValue())
                    .put("data", new JSONArray(erro)).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", false).put("msg", "Echec de création du BL");
        }
    }

    private TBonLivraisonDetail createBLDetail(TBonLivraison oTBonLivraison, TGrossiste oTGrossiste, TFamille oTFamille,
            TOrderDetail d, TZoneGeographique oTZoneGeographique, int initStock) {
        TBonLivraisonDetail oTBonLivraisonDetail = new TBonLivraisonDetail();
        oTBonLivraisonDetail.setLgBONLIVRAISONDETAIL(UUID.randomUUID().toString());
        oTBonLivraisonDetail.setLgBONLIVRAISONID(oTBonLivraison);
        oTBonLivraisonDetail.setLgGROSSISTEID(oTGrossiste);
        oTBonLivraisonDetail.setLgFAMILLEID(oTFamille);
        oTBonLivraisonDetail.setLgZONEGEOID(oTZoneGeographique);
        oTBonLivraisonDetail.setIntQTECMDE(d.getIntQTEREPGROSSISTE());
        oTBonLivraisonDetail.setIntQTERECUE(d.getIntQTEREPGROSSISTE() - d.getIntQTEMANQUANT());
        oTBonLivraisonDetail.setIntPRIXREFERENCE(d.getIntPRICEDETAIL());
        oTBonLivraisonDetail.setIntPRIXVENTE(d.getIntPRICEDETAIL());
        oTBonLivraisonDetail.setIntPAF(d.getIntPAFDETAIL());
        oTBonLivraisonDetail.setIntPAREEL(d.getIntPAFDETAIL());
        oTBonLivraisonDetail.setPrixUni(d.getPrixUnitaire());
        oTBonLivraisonDetail.setPrixTarif(d.getPrixAchat());
        oTBonLivraisonDetail.setStrETATARTICLE("");
        oTBonLivraisonDetail.setStrLIVRAISONADP("");
        oTBonLivraisonDetail.setStrMANQUEFORCES("");
        oTBonLivraisonDetail.setIntINITSTOCK(initStock);
        oTBonLivraisonDetail.setIntQTEMANQUANT(oTBonLivraisonDetail.getIntQTECMDE());
        oTBonLivraisonDetail.setDtCREATED(new Date());
        oTBonLivraisonDetail.setDtUPDATED(oTBonLivraisonDetail.getDtCREATED());
        oTBonLivraisonDetail.setStrSTATUT(Constant.STATUT_ENABLE);
        getEmg().persist(oTBonLivraisonDetail);
        return oTBonLivraisonDetail;

    }

    private List<TOrderDetail> getTOrderDetail(String orderId, String statut) {

        try {
            TypedQuery<TOrderDetail> q = getEmg()
                    .createQuery("SELECT t FROM TOrderDetail t WHERE t.strSTATUT = ?1 AND t.lgORDERID.lgORDERID = ?2",
                            TOrderDetail.class)
                    .setParameter(1, statut).setParameter(2, orderId);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    private boolean isRefBLExistForGrossiste(String str_REF_LIVRAISON, String lg_GROSSISTE_ID) {

        try {
            TypedQuery<TBonLivraison> q = getEmg().createQuery(
                    "SELECT t FROM TBonLivraison t WHERE t.strREFLIVRAISON = ?1 AND t.lgORDERID.lgGROSSISTEID.lgGROSSISTEID = ?2",
                    TBonLivraison.class);
            q.setParameter(1, str_REF_LIVRAISON).setParameter(2, lg_GROSSISTE_ID).setMaxResults(1);
            return q.getSingleResult() != null;
        } catch (Exception e) {
            return false;
        }

    }

    private TBonLivraison createBL(TOrder OTOrder, TUser user, String str_REF_LIVRAISON, Date dt_DATE_LIVRAISON,
            int int_MHT, int int_TVA) throws Exception {
        TBonLivraison OTBonLivraison = new TBonLivraison(UUID.randomUUID().toString());
        OTBonLivraison.setStrREFLIVRAISON(str_REF_LIVRAISON);
        OTBonLivraison.setDtDATELIVRAISON(dt_DATE_LIVRAISON);
        OTBonLivraison.setIntMHT(int_MHT);
        OTBonLivraison.setLgUSERID(user);
        OTBonLivraison.setIntTVA(int_TVA);
        OTBonLivraison.setLgORDERID(OTOrder);
        OTBonLivraison.setIntHTTC(OTBonLivraison.getIntMHT() + OTBonLivraison.getIntTVA());
        OTBonLivraison.setStrSTATUT(DateConverter.STATUT_ENABLE);
        OTBonLivraison.setDtCREATED(new Date());
        OTBonLivraison.setDtUPDATED(dt_DATE_LIVRAISON);
        getEmg().persist(OTBonLivraison);
        return OTBonLivraison;

    }

    @Override
    public TOrder findByRef(String reference, String idCommande) {
        if (!StringUtils.isEmpty(idCommande)) {
            return getEmg().find(TOrder.class, idCommande);
        }
        TypedQuery<TOrder> q = getEmg().createQuery("SELECT o FROM TOrder o WHERE o.strREFORDER=?1", TOrder.class);
        q.setParameter(1, reference);
        return q.getSingleResult();
    }

    @Override
    public List<TOrderDetail> findByOrderId(String idCommande) {
        TypedQuery<TOrderDetail> q = getEmg()
                .createQuery("SELECT o FROM TOrderDetail o WHERE o.lgORDERID.lgORDERID= ?1", TOrderDetail.class);
        q.setParameter(1, idCommande);
        return q.getResultList();
    }

    @Override
    public TOrderDetail findByCipAndOrderId(String codeCip, String idCommande) {
        try {
            TypedQuery<TOrderDetail> q = getEmg().createQuery(
                    "SELECT o FROM TOrderDetail o WHERE o.lgFAMILLEID.intCIP=?1 AND  o.lgORDERID.lgORDERID= ?2",
                    TOrderDetail.class);
            q.setParameter(1, codeCip);
            q.setParameter(2, idCommande);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Rupture creerRupture(TOrder order) {
        Rupture rupture = new Rupture();
        TGrossiste grossiste = order.getLgGROSSISTEID();
        rupture.setGrossiste(grossiste);
        rupture.setReference(order.getStrREFORDER());
        getEmg().persist(rupture);
        return rupture;

    }

    @Override
    public void creerRuptureItem(Rupture rupture, TFamille famille, int qty) {
        RuptureDetail ruptureDetail = new RuptureDetail();
        ruptureDetail.setProduit(famille);
        ruptureDetail.setRupture(rupture);
        ruptureDetail.setQty(qty);
        ruptureDetail.setPrixAchat(famille.getIntPAF());
        ruptureDetail.setPrixVente(famille.getIntPRICE());
        getEmg().persist(ruptureDetail);
    }

    @Override
    public JSONObject removeRupture(String id) {
        Rupture r = getEmg().find(Rupture.class, id);
        removeRutureItems(r);
        getEmg().remove(r);
        return new JSONObject().put("success", true);
    }

    public void removeRutureItems(Rupture r) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<RuptureDetail> cq = cb.createCriteriaDelete(RuptureDetail.class);
            Root<RuptureDetail> root = cq.from(RuptureDetail.class);
            cq.where(cb.equal(root.get(RuptureDetail_.rupture), r));
            getEmg().createQuery(cq).executeUpdate();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
    }

    @Override
    public List<RuptureDTO> listeRuptures(LocalDate dtStart, LocalDate dtEnd, String query, String grossisteId,
            int start, int limit, boolean all) {

        if (StringUtils.isEmpty(query)) {
            return listeRuptures(dtStart, dtEnd, grossisteId, start, limit, all);
        }
        return listeRupturesByRuptureDetails(dtStart, dtEnd, query, grossisteId, start, limit, all);

    }

    private List<Predicate> predicats(CriteriaBuilder cb, Root<Rupture> root, LocalDate dtStart, LocalDate dtEnd,
            String grossisteId) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate btw = cb.between(root.get(Rupture_.dtCreated), dtStart, dtEnd);
        predicates.add(btw);
        if (!StringUtils.isEmpty(grossisteId)) {
            predicates.add(cb.equal(root.get(Rupture_.grossiste).get(TGrossiste_.lgGROSSISTEID), grossisteId));
        }
        return predicates;
    }

    private List<Predicate> predicats(CriteriaBuilder cb, Root<RuptureDetail> root, LocalDate dtStart, LocalDate dtEnd,
            String grossisteId, String query) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate btw = cb.between(root.get(RuptureDetail_.rupture).get(Rupture_.dtCreated), dtStart, dtEnd);
        predicates.add(btw);
        if (!StringUtils.isEmpty(grossisteId)) {
            predicates.add(
                    cb.equal(root.get(RuptureDetail_.rupture).get(Rupture_.grossiste).get(TGrossiste_.lgGROSSISTEID),
                            grossisteId));
        }
        if (!StringUtils.isEmpty(query)) {
            predicates.add(cb.or(cb.like(root.get(RuptureDetail_.produit).get(TFamille_.intCIP), query + "%"),
                    cb.like(root.get(RuptureDetail_.produit).get(TFamille_.strNAME), query + "%")));
        }
        return predicates;
    }

    long listeRupturesByRuptureDetails(LocalDate dtStart, LocalDate dtEnd, String grossisteId, String query) {
        try {
            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<RuptureDetail> root = cq.from(RuptureDetail.class);
            cq.select(cb.countDistinct(root.get(RuptureDetail_.rupture)));
            List<Predicate> predicates = predicats(cb, root, dtStart, dtEnd, grossisteId, query);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = getEmg().createQuery(cq);
            return (long) q.getSingleResult();
        } catch (Exception e) {
            return 0l;
        }
    }

    List<RuptureDTO> listeRupturesByRuptureDetails(LocalDate dtStart, LocalDate dtEnd, String grossisteId, String query,
            int start, int limit, boolean all) {
        try {
            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<Rupture> cq = cb.createQuery(Rupture.class);
            Root<RuptureDetail> root = cq.from(RuptureDetail.class);
            cq.select(root.get(RuptureDetail_.rupture)).distinct(true)
                    .orderBy(cb.desc(root.get(RuptureDetail_.rupture).get(Rupture_.dtUpdated)));
            List<Predicate> predicates = predicats(cb, root, dtStart, dtEnd, grossisteId, query);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            TypedQuery<Rupture> q = getEmg().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            return q.getResultList().stream().map(x -> new RuptureDTO(x, ruptureDetaisDtoByRupture(x.getId()).stream()
                    .map(RuptureDetailDTO::new).collect(Collectors.toList()))).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    List<RuptureDTO> listeRuptures(LocalDate dtStart, LocalDate dtEnd, String grossisteId, int start, int limit,
            boolean all) {
        try {
            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<Rupture> cq = cb.createQuery(Rupture.class);
            Root<Rupture> root = cq.from(Rupture.class);
            cq.select(root).orderBy(cb.desc(root.get(Rupture_.dtUpdated)));
            List<Predicate> predicates = predicats(cb, root, dtStart, dtEnd, grossisteId);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<Rupture> q = getEmg().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            return q.getResultList().stream()
                    .map(x -> new RuptureDTO(x,
                            ruptureDetaisDtoByRupture(x.getId()).stream().map(RuptureDetailDTO::new)
                                    .collect(Collectors.toList())))
                    .filter(e -> e.getNbreProduit() > 0).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }

    }

    long listeRuptures(LocalDate dtStart, LocalDate dtEnd, String grossisteId) {
        try {
            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Rupture> root = cq.from(Rupture.class);
            cq.select(cb.count(root));
            List<Predicate> predicates = predicats(cb, root, dtStart, dtEnd, grossisteId);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            Query q = getEmg().createQuery(cq);
            return (long) q.getSingleResult();
        } catch (Exception e) {
            return 0l;
        }
    }

    @Override
    public JSONObject listeRuptures(LocalDate dtStart, LocalDate dtEnd, String query, String grossisteId, int start,
            int limit) throws JSONException {
        if (StringUtils.isEmpty(query)) {
            List<RuptureDTO> data = listeRuptures(dtStart, dtEnd, grossisteId, start, limit, true);
            return new JSONObject().put("total", data.size()).put("data", new JSONArray(data));
        }
        return new JSONObject().put("total", listeRupturesByRuptureDetails(dtStart, dtEnd, grossisteId, query)).put(
                "data",
                new JSONArray(listeRupturesByRuptureDetails(dtStart, dtEnd, grossisteId, query, start, limit, false)));
    }

    @Override
    public List<RuptureDetail> ruptureDetaisDtoByRupture(String idRupture) {
        TypedQuery<RuptureDetail> q = getEmg().createQuery("SELECT o FROM RuptureDetail o WHERE o.rupture.id =?1",
                RuptureDetail.class);
        q.setParameter(1, idRupture);
        return q.getResultList();
    }

    @Override
    public RuptureDetail ruptureDetaisByRuptureAndProduitId(String idRupture, String produitId) {
        TypedQuery<RuptureDetail> q = getEmg().createQuery(
                "SELECT o FROM RuptureDetail o WHERE o.rupture.id =?1 AND o.produit.lgFAMILLEID =?2 ",
                RuptureDetail.class);
        q.setParameter(1, idRupture);
        q.setParameter(2, produitId);
        q.setMaxResults(1);
        return q.getSingleResult();
    }

    @Override
    public List<RuptureDetailDTO> listeRuptures(LocalDate dtStart, LocalDate dtEnd, String query, String grossisteId,
            String emplacementId) {
        try {
            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<RuptureDetail> cq = cb.createQuery(RuptureDetail.class);
            Root<RuptureDetail> root = cq.from(RuptureDetail.class);
            cq.select(root).orderBy(cb.asc(root.get(RuptureDetail_.produit).get(TFamille_.strNAME)));
            List<Predicate> predicates = predicats(cb, root, dtStart, dtEnd, grossisteId, query);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            TypedQuery<RuptureDetail> q = getEmg().createQuery(cq);
            return q.getResultList().stream()
                    .map(x -> new RuptureDetailDTO(x, findProduitStock(x.getProduit().getLgFAMILLEID(), emplacementId)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public int findProduitStock(String idProduit, String emplacementId) {
        try {
            Query q = this.getEmg().createQuery(
                    "SELECT o.intNUMBERAVAILABLE FROM TFamilleStock o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?2 AND o.strSTATUT='enable' ");
            q.setParameter(1, idProduit);
            q.setParameter(2, emplacementId);
            q.setMaxResults(1);
            return ((Integer) q.getSingleResult());
        } catch (Exception e) {

            return 0;
        }
    }

    void updateRuptureDetail(Rupture r, String ruptureId) {
        ruptureDetaisDtoByRupture(ruptureId).stream().forEach(e -> {
            e.setRupture(r);
            this.getEmg().merge(e);
        });
    }

    @Override
    public JSONObject creerRupture(GenererFactureDTO datas) throws JSONException {
        try {
            List<RuptureDetail> ruptureDetails = new ArrayList<>();

            TGrossiste grossiste = this.getEmg().find(TGrossiste.class, datas.getOrganismeId());
            Rupture rupture = new Rupture();
            rupture.setGrossiste(grossiste);
            rupture.setReference(genererReferenceCommande());
            getEmg().persist(rupture);
            datas.getDatas().forEach(s -> ruptureDetails.addAll(ruptureDetaisDtoByRupture(s)));
            Map<TFamille, List<RuptureDetail>> map = ruptureDetails.stream()
                    .collect(Collectors.groupingBy(RuptureDetail::getProduit));
            map.forEach((k, v) -> {
                RuptureDetail rd = v.get(0);
                if (v.size() > 1) {
                    int sumQty = v.stream().filter(obj -> !obj.equals(rd)).peek(o -> {
                        this.getEmg().refresh(o);
                    }).map(RuptureDetail::getQty).reduce(0, Integer::sum);
                    rd.setQty(rd.getQty() + sumQty);

                }
                TFamilleGrossiste familleGrossiste = findOrCreateFamilleGrossiste(k, grossiste);
                if (familleGrossiste != null) {
                    rd.setPrixAchat(familleGrossiste.getIntPAF());
                    rd.setPrixVente(familleGrossiste.getIntPRICE());
                    rd.setRupture(rupture);

                } else {
                    rd.setRupture(rupture);
                }

                this.getEmg().merge(rd);
            });

            datas.getDatas().forEach(s -> {

                this.getEmg().remove(this.getEmg().find(Rupture.class, s));
            });
            return new JSONObject().put("success", true).put("ruptureId", rupture.getId());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }

    }

    public String genererReferenceCommande() {
        TParameters oTParameters = this.getEmg().find(TParameters.class, "KEY_LAST_ORDER_COMMAND_NUMBER");
        TParameters param = this.getEmg().find(TParameters.class, "KEY_SIZE_ORDER_NUMBER");
        JSONArray jsonArray = new JSONArray(oTParameters.getStrVALUE());
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        LocalDate date = LocalDate.parse(jsonObject.getString("str_last_date"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        int lastCode = 0;
        if (date.equals(LocalDate.now())) {
            lastCode = Integer.parseInt(jsonObject.getString("int_last_code"));
        } else {
            date = LocalDate.now();
        }
        lastCode++;

        String left = StringUtils.leftPad("" + lastCode, Integer.parseInt(param.getStrVALUE()), '0');
        jsonObject.put("int_last_code", left);
        jsonObject.put("str_last_date", date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
        oTParameters.setStrVALUE(jsonArray.toString());
        this.getEmg().merge(oTParameters);
        return LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")).concat("_") + left;
    }

    @Override
    public TFamilleGrossiste findOrCreateFamilleGrossiste(TFamille famille, TGrossiste grossiste) {
        try {
            TFamilleGrossiste familleGrossiste = finFamilleGrossisteByIdFamilleAndIdGrossiste(famille.getLgFAMILLEID(),
                    grossiste.getLgGROSSISTEID());
            if (familleGrossiste != null) {
                return familleGrossiste;
            }
            familleGrossiste = new TFamilleGrossiste();
            familleGrossiste.setLgFAMILLEID(famille);
            familleGrossiste.setLgGROSSISTEID(grossiste);
            familleGrossiste.setIntPAF(famille.getIntPAF());
            familleGrossiste.setIntPRICE(famille.getIntPRICE());
            familleGrossiste.setStrCODEARTICLE(famille.getIntCIP());
            this.getEmg().persist(familleGrossiste);
            return familleGrossiste;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public TFamilleGrossiste finFamilleGrossisteByFamilleCipAndIdGrossiste(String cip, String grossisteId) {
        try {
            TypedQuery<TFamilleGrossiste> q = this.getEmg().createQuery(
                    "SELECT OBJECT(o) FROM TFamilleGrossiste o WHERE o.lgFAMILLEID.intCIP =?1 AND o.lgGROSSISTEID.lgGROSSISTEID=?2 AND o.strSTATUT='enable' ",
                    TFamilleGrossiste.class);
            q.setParameter(1, cip);
            q.setParameter(2, grossisteId);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {

            return null;
        }
    }

    @Override
    public TFamilleGrossiste finFamilleGrossisteByIdFamilleAndIdGrossiste(String id, String grossisteId) {
        try {
            TypedQuery<TFamilleGrossiste> q = this.getEmg().createQuery(
                    "SELECT OBJECT(o) FROM TFamilleGrossiste o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgGROSSISTEID.lgGROSSISTEID=?2 AND o.strSTATUT='enable' ",
                    TFamilleGrossiste.class);
            q.setParameter(1, id);
            q.setParameter(2, grossisteId);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private static final Logger LOG = Logger.getLogger(OrderServiceImpl.class.getName());

    @Override
    public TOrder createOrder(TGrossiste grossiste, TUser u) {
        TOrder order = new TOrder(RandomStringUtils.randomAlphabetic(20));
        order.setDtCREATED(new Date());
        order.setDtUPDATED(new Date());
        order.setLgGROSSISTEID(grossiste);
        order.setStrREFORDER(genererReferenceCommande());
        order.setStrSTATUT(DateConverter.PASSE);
        order.setIntPRICE(0);
        return order;

    }

    @Override
    public TOrderDetail modificationProduitCommandeEncours(ArticleDTO dto, TUser user) {

        TOrderDetail detail = this.getEmg().find(TOrderDetail.class, dto.getId());
        TFamille f = detail.getLgFAMILLEID();
        TOrder order = detail.getLgORDERID();
        TFamilleGrossiste produitGrossiste = findOrCreateFamilleGrossiste(f, order.getLgGROSSISTEID());
        if (dto.getPrixAchat() != produitGrossiste.getIntPAF()) {
            String desc = "Modification du prix d'achat du produit : " + f.getIntCIP() + " " + f.getStrNAME()
                    + " ancien prix: " + produitGrossiste.getIntPAF() + " nouveau prix :" + dto.getPrixAchat();
            logService.updateItem(user, produitGrossiste.getStrCODEARTICLE(), desc,
                    TypeLog.MODIFICATION_INFO_PRODUIT_COMMANDE, f);
            notificationService.save(new Notification().canal(Canal.SMS_EMAIL)
                    .typeNotification(TypeNotification.MODIFICATION_INFO_PRODUIT_COMMANDE).message(desc).addUser(user));
            saveMouvementPrice(f, dto.getPrixAchat(), produitGrossiste.getIntPAF(), f.getIntCIP(), user);

        }

        detail.setIntNUMBER(dto.getStock());
        detail.setIntQTEREPGROSSISTE(dto.getStock());
        detail.setIntQTEMANQUANT(dto.getStock());
        detail.setIntPRICE(dto.getStock() * dto.getPrixAchat());
        detail.setIntPAFDETAIL(dto.getPrixAchat());

        detail.setStrSTATUT(Constant.STATUT_IS_PROGRESS);
        detail.setDtUPDATED(new Date());
        detail.setPrixAchat(produitGrossiste.getIntPAF());

        this.getEmg().merge(detail);
        order.setDtUPDATED(detail.getDtUPDATED());
        this.getEmg().merge(order);
        return detail;
    }

    private void saveMouvementPrice(TFamille OTFamille, int int_PRICE, int int_PRICE_OLD, String str_REF, TUser u) {

        try {
            TMouvementprice mouvementprice = new TMouvementprice(UUID.randomUUID().toString());
            mouvementprice.setLgUSERID(u);
            mouvementprice.setStrACTION(commonparameter.code_action_commande);
            mouvementprice.setDtUPDATED(new Date());
            mouvementprice.setDtCREATED(new Date());
            mouvementprice.setIntPRICENEW(int_PRICE);
            mouvementprice.setIntPRICEOLD(int_PRICE_OLD);
            mouvementprice.setStrREF(str_REF);
            mouvementprice.setDtDAY(new Date());
            mouvementprice.setStrSTATUT(commonparameter.statut_enable);
            mouvementprice.setLgFAMILLEID(OTFamille);
            this.getEmg().persist(mouvementprice);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }

    }

    @Override
    public TFamilleGrossiste findOrCreateFamilleGrossisteByFamilleAndGrossiste(TFamille famille, TGrossiste grossiste) {
        try {
            TFamilleGrossiste familleGrossiste = finFamilleGrossisteByByFamilleAndIdGrossiste(famille.getLgFAMILLEID(),
                    grossiste.getLgGROSSISTEID());
            if (familleGrossiste != null) {
                return familleGrossiste;
            }

            familleGrossiste = new TFamilleGrossiste();
            familleGrossiste.setLgFAMILLEID(famille);
            familleGrossiste.setLgGROSSISTEID(grossiste);
            familleGrossiste.setIntPAF(famille.getIntPAF());
            familleGrossiste.setIntPRICE(famille.getIntPRICE());
            familleGrossiste.setStrCODEARTICLE(famille.getIntCIP());
            this.getEmg().persist(familleGrossiste);
            return familleGrossiste;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public TFamilleGrossiste finFamilleGrossisteByByFamilleAndIdGrossiste(String idFamille, String grossisteId) {
        try {
            TypedQuery<TFamilleGrossiste> q = this.getEmg().createQuery(
                    "SELECT OBJECT(o) FROM TFamilleGrossiste o WHERE o.lgFAMILLEID.lgFAMILLEID =?1 AND o.lgGROSSISTEID.lgGROSSISTEID=?2 AND o.strSTATUT='enable' ",
                    TFamilleGrossiste.class);
            q.setParameter(1, idFamille);
            q.setParameter(2, grossisteId);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public TGrossiste findGrossiste(String id) {
        return getEmg().find(TGrossiste.class, id);
    }

    @Override
    public JSONObject updateScheduled(String idProduit, boolean scheduled) throws JSONException {
        try {
            TFamille famille = getEmg().find(TFamille.class, idProduit);
            famille.setScheduled(scheduled);
            getEmg().merge(famille);
            return new JSONObject().put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }
    }

    @Override
    public List<CommandeEncourDetailDTO> fetchOrderItems(CommandeFiltre filtre, String orderId, String query, int start,
            int limit, boolean all) {
        try {

            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<TOrderDetail> cq = cb.createQuery(TOrderDetail.class);
            Root<TOrderDetail> root = cq.from(TOrderDetail.class);
            cq.select(root).orderBy(cb.desc(root.get(TOrderDetail_.dtUPDATED)),
                    cb.asc(root.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.strNAME)));
            List<Predicate> predicates = fetchOrderItemsPredicats(cb, root, orderId, filtre, query);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            TypedQuery<TOrderDetail> q = getEmg().createQuery(cq);
            if (!all && filtre != CommandeFiltre.PRIX_VENTE_PLUS_30) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            if (filtre == CommandeFiltre.PRIX_VENTE_PLUS_30) {
                return q.getResultList().stream().filter(FunctionUtils.ECART_PRIX_VENTE_30)
                        .map(CommandeEncourDetailDTO::new).collect(Collectors.toList());
            }
            return q.getResultList().stream().map(
                    e -> new CommandeEncourDetailDTO(e, this.getTProductItemStock(e.getLgFAMILLEID().getLgFAMILLEID())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private List<Predicate> fetchOrderItemsPredicats(CriteriaBuilder cb, Root<TOrderDetail> root, String orderId,
            CommandeFiltre filtre, String query) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get(TOrderDetail_.lgORDERID).get(TOrder_.lgORDERID), orderId));

        CommandeFiltre commandeFiltre = Objects.isNull(filtre) ? CommandeFiltre.ALL : filtre;

        switch (commandeFiltre) {
        case PRIX_VENTE_DIFF:
            predicates.add(cb.notEqual(root.get(TOrderDetail_.intPRICEDETAIL),
                    root.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.intPRICE)));
            break;
        case PRIX_VENTE_PLUS_30:
        case ALL:
            break;
        default:
            break;
        }
        if (StringUtils.isNotEmpty(query)) {
            predicates.add(cb.or(cb.like(root.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.intCIP), query + "%"),
                    cb.like(root.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.strNAME), query + "%")));
        }
        return predicates;
    }

    @Override
    public JSONObject fetchOrderItems(CommandeFiltre filtre, String orderId, String query, int start, int limit) {
        List<CommandeEncourDetailDTO> data = this.fetchOrderItems(filtre, orderId, query, start, limit, false);
        if (filtre == CommandeFiltre.PRIX_VENTE_PLUS_30) {
            return FunctionUtils.returnData(data);
        }
        return FunctionUtils.returnData(data, fetchOrderItemsCount(filtre, orderId, query));
    }

    @Override
    public String modifierProduitPrixVenteCommandeEnCours(ArticleDTO dto, TUser user) {

        TOrderDetail detail = this.getEmg().find(TOrderDetail.class, dto.getId());
        TFamille f = detail.getLgFAMILLEID();
        TOrder order = detail.getLgORDERID();
        TFamilleGrossiste produitGrossiste = findOrCreateFamilleGrossiste(f, order.getLgGROSSISTEID());

        String desc = "Modification du prix de vente du produit :" + f.getStrNAME() + " prix importé: "
                + detail.getIntPRICEDETAIL() + " nouveau prix :" + dto.getPrixVente();
        logService.updateItem(user, produitGrossiste.getStrCODEARTICLE(), desc,
                TypeLog.MODIFICATION_INFO_PRODUIT_COMMANDE, f);
        notificationService.save(new Notification().canal(Canal.SMS_EMAIL)
                .typeNotification(TypeNotification.MODIFICATION_INFO_PRODUIT_COMMANDE).message(desc).addUser(user));
        saveMouvementPrice(f, dto.getPrixVente(), detail.getIntPRICEDETAIL(), f.getIntCIP(), user);
        detail.setIntPRICEDETAIL(dto.getPrixVente());
        detail.setStrSTATUT(DateConverter.STATUT_PROCESS);
        detail.setDtUPDATED(new Date());
        detail.setPrixUnitaire(dto.getPrixVente());
        this.getEmg().merge(detail);
        order.setDtUPDATED(detail.getDtUPDATED());
        order.setLgUSERID(user);
        this.getEmg().merge(order);
        return order.getLgORDERID();
    }

    public long fetchOrderItemsCount(CommandeFiltre filtre, String orderId, String query) {
        try {

            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TOrderDetail> root = cq.from(TOrderDetail.class);
            cq.select(cb.count(root));
            List<Predicate> predicates = fetchOrderItemsPredicats(cb, root, orderId, filtre, query);
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            TypedQuery<Long> q = getEmg().createQuery(cq);

            return q.getSingleResult().intValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public JSONObject fetch(String search, Set<String> status, int start, int limit) {
        long count = count(search, status);
        return FunctionUtils.returnData(getCommandes(search, status, start, limit), count);

    }

    private List<CommandeDTO> getCommandes(String search, Set<String> status, int start, int limit) {
        CriteriaBuilder cb = getEmg().getCriteriaBuilder();
        CriteriaQuery<TOrder> cq = cb.createQuery(TOrder.class);
        Root<TOrder> root = cq.from(TOrder.class);
        Join<TOrder, TOrderDetail> join = root.join(TOrder_.tOrderDetailCollection);
        cq.select(root).distinct(true).orderBy(cb.desc(root.get(TOrder_.dtUPDATED)));
        List<Predicate> predicates = listPredicates(cb, root, join, search, status);
        cq.where(cb.and(predicates.toArray(Predicate[]::new)));
        TypedQuery<TOrder> q = getEmg().createQuery(cq);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList().stream().map(this::buildCommandeDTO).collect(Collectors.toList());
    }

    private CommandeDTO buildCommandeDTO(TOrder order) {
        int montantAchat = 0;
        int montantVente = 0;
        int nbreLigne = 0;
        int totalQty = 0;
        String items = " ";

        for (TOrderDetail item : order.getTOrderDetailCollection()) {
            montantAchat += item.getIntPRICE();
            montantVente += (item.getIntPRICEDETAIL() * item.getIntNUMBER());
            nbreLigne++;
            totalQty += item.getIntNUMBER();

            TFamille famille = item.getLgFAMILLEID();
            TFamilleGrossiste familleGrossiste = findFamilleGrossiste(famille.getLgFAMILLEID(),
                    order.getLgGROSSISTEID().getLgGROSSISTEID());
            items += " <b><span style='display:inline-block;width: 7%;'>"
                    + (familleGrossiste != null ? familleGrossiste.getStrCODEARTICLE() : famille.getIntCIP())
                    + "</span><span style='display:inline-block;width: 25%;'>" + famille.getStrDESCRIPTION()
                    + "</span><span style='display:inline-block;width: 10%;'>(" + item.getIntNUMBER()
                    + ")</span><span style='display:inline-block;width: 15%;'>"
                    + NumberUtils.formatLongToString(item.getIntPAFDETAIL())
                    + " F CFA </span><span style='display:inline-block;width: 15%;'>"
                    + NumberUtils.formatLongToString(item.getIntPRICEDETAIL()) + " F CFA " + "</span></b><br> ";

        }
        return new CommandeDTO(order, items, montantAchat, montantVente, nbreLigne, totalQty);
    }

    private List<Predicate> listPredicates(CriteriaBuilder cb, Root<TOrder> root, Join<TOrder, TOrderDetail> join,
            String search, Set<String> status) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(root.get(TOrder_.strSTATUT).in(status));

        if (StringUtils.isNotEmpty(search)) {
            search = search + "%";
            predicates.add(cb.or(cb.like(root.get(TOrder_.strREFORDER), search),
                    cb.like(join.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.intCIP), search),
                    cb.like(join.get(TOrderDetail_.lgFAMILLEID).get(TFamille_.strNAME), search)));
        }
        return predicates;
    }

    private long count(String search, Set<String> status) {
        CriteriaBuilder cb = getEmg().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TOrder> root = cq.from(TOrder.class);
        Join<TOrder, TOrderDetail> join = root.join(TOrder_.tOrderDetailCollection);
        cq.select(cb.countDistinct(root));
        List<Predicate> predicates = listPredicates(cb, root, join, search, status);
        cq.where(cb.and(predicates.toArray(Predicate[]::new)));
        TypedQuery<Long> q = getEmg().createQuery(cq);
        return Objects.isNull(q.getSingleResult()) ? 0 : q.getSingleResult();

    }

    private TFamilleGrossiste findFamilleGrossiste(String familleId, String grossisteId) {

        try {
            Query qry = getEmg().createQuery(
                    "SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgGROSSISTEID.lgGROSSISTEID = ?2  AND t.strSTATUT = ?3 ")
                    .setParameter(1, familleId).setParameter(2, grossisteId).setParameter(3, Constant.STATUT_ENABLE);
            qry.setMaxResults(1);
            return (TFamilleGrossiste) qry.getSingleResult();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;

        }
    }

    @Override
    public void removeItem(String itemId) {
        TOrderDetail item = getEmg().find(TOrderDetail.class, itemId);
        TOrder order = item.getLgORDERID();
        if (CollectionUtils.isNotEmpty(order.getTOrderDetailCollection())
                && order.getTOrderDetailCollection().size() == 1) {
            getEmg().remove(item);
            getEmg().remove(order);
        } else {
            getEmg().remove(item);
            order.setDtUPDATED(new Date());
            getEmg().persist(order);
        }
    }

    @Override
    public JSONObject getCommandeAmount(String commandeId) {
        long montantAchat = 0;
        long montantVente = 0;
        try {
            TOrder order = getEmg().find(TOrder.class, commandeId);
            for (TOrderDetail item : order.getTOrderDetailCollection()) {
                montantAchat += item.getIntPRICE();
                montantVente += ((long) item.getIntNUMBER() * item.getIntPRICEDETAIL());
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
        return new JSONObject().put("success", true).put("prixAchat", montantAchat).put("prixVente", montantVente);

    }

    @Override
    public JSONObject addItem(OrderDetailDTO orderDetail, TUser user) {
        Objects.requireNonNull(orderDetail.getQte(), "La quantité ne doit pas être null");
        JSONObject json = new JSONObject();
        if (StringUtils.isNotEmpty(orderDetail.getOrderId()) && !orderDetail.getOrderId().equals("0")) {
            find(orderDetail.getOrderId()).ifPresent(order -> {
                createOrUpdate(orderDetail, order);
                json.put("orderId", order.getLgORDERID());
            });
        } else {
            TOrder tOrder = createOrder(orderDetail, user);
            json.put("orderId", tOrder.getLgORDERID());
        }

        return json;
    }

    private TOrder createOrder(OrderDetailDTO orderDetail, TUser user) {

        TGrossiste grossiste = this.getEmg().find(TGrossiste.class, orderDetail.getGrossisteId());
        KeyUtilGen keyUtilGen = new KeyUtilGen();
        try {
            TOrder order = new TOrder();
            order.setLgORDERID(keyUtilGen.getComplexId());
            order.setLgUSERID(user);
            order.setLgGROSSISTEID(grossiste);
            order.setStrREFORDER(this.buildCommandeRef(new Date(), keyUtilGen));
            order.setStrSTATUT(orderDetail.getStatut());
            order.setDtCREATED(new Date());
            order.setDtUPDATED(order.getDtCREATED());
            this.getEmg().persist(order);
            createOrderItem(order, orderDetail, keyUtilGen);
            return order;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            throw e;
        }

    }

    private String buildCommandeRef(Date date, KeyUtilGen keyUtilGen) {
        TParameters parameters = this.getEmg().find(TParameters.class, "KEY_LAST_ORDER_COMMAND_NUMBER");
        TParameters parameters1 = this.getEmg().find(TParameters.class, "KEY_SIZE_ORDER_NUMBER");
        String jsondata = parameters.getStrVALUE();
        int int_last_code = 0;
        int_last_code = int_last_code + 1;

        try {
            JSONArray jsonArray = new JSONArray(jsondata);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int_last_code = Integer.parseInt(jsonObject.getString("int_last_code"));
            Date dt_last_date = keyUtilGen.stringToDate(jsonObject.getString("str_last_date"),
                    keyUtilGen.formatterMysqlShort2);

            String str_lasd = keyUtilGen.dateToString(dt_last_date, keyUtilGen.formatterMysqlShort2);
            String str_actd = keyUtilGen.dateToString(date, keyUtilGen.formatterMysqlShort2);

            if (!str_lasd.equals(str_actd)) {
                int_last_code = 0;
            }

        } catch (Exception e) {

        }

        Calendar now = Calendar.getInstance();
        int hh = now.get(Calendar.HOUR_OF_DAY);
        int mois = now.get(Calendar.MONTH) + 1;
        int jour = now.get(Calendar.DAY_OF_MONTH);
        String mois_tostring = "";

        int intsize = ((int_last_code + 1) + "").length();
        int intsize_tobuild = Integer.parseInt(parameters1.getStrVALUE());
        String str_last_code = "";
        for (int i = 0; i < (intsize_tobuild - intsize); i++) {
            str_last_code = str_last_code + "0";
        }

        str_last_code = str_last_code + (int_last_code + 1) + "";
        if (mois < 10) {
            mois_tostring = "0" + mois;
        } else {
            mois_tostring = String.valueOf(mois);
        }
        String str_code = jour + "" + mois_tostring + "" + keyUtilGen.getYear(date) + "_" + str_last_code;
        JSONObject json = new JSONObject();
        JSONArray arrayObj = new JSONArray();
        json.put("int_last_code", str_last_code);
        json.put("str_last_date", keyUtilGen.dateToString(date, keyUtilGen.formatterMysqlShort2));
        arrayObj.put(json);
        String jsonData = arrayObj.toString();

        parameters.setStrVALUE(jsonData);
        this.getEmg().persist(parameters);

        return str_code;
    }

    private Optional<TOrder> find(String id) {
        try {
            return Optional.ofNullable(this.getEmg().find(TOrder.class, id));
        } catch (Exception e) {
            return Optional.empty();

        }
    }

    private TFamilleGrossiste createIfNotExist(OrderDetailDTO orderDetailDTO, TOrder order) {
        TFamilleGrossiste familleGrossiste = findFamilleGrossiste(orderDetailDTO.getFamilleId(),
                order.getLgGROSSISTEID().getLgGROSSISTEID());
        if (familleGrossiste == null) {
            TFamille famille = this.getEmg().find(TFamille.class, orderDetailDTO.getFamilleId());
            familleGrossiste = new TFamilleGrossiste();
            familleGrossiste.setLgFAMILLEID(famille);
            familleGrossiste.setLgGROSSISTEID(order.getLgGROSSISTEID());
            familleGrossiste.setIntPAF(famille.getIntPAF());
            familleGrossiste.setIntPRICE(famille.getIntPRICE());
            familleGrossiste.setStrCODEARTICLE("");
            getEmg().persist(familleGrossiste);

        }
        return familleGrossiste;
    }

    private void createOrderItem(TOrder order, OrderDetailDTO orderDetailDTO, KeyUtilGen keyUtilGen) {
        TFamilleGrossiste familleGrossiste = createIfNotExist(orderDetailDTO, order);

        try {
            TFamille famille = familleGrossiste.getLgFAMILLEID();
            TOrderDetail detail = new TOrderDetail();
            detail.setLgORDERDETAILID(keyUtilGen.getComplexId());
            detail.setLgORDERID(order);
            detail.setIntNUMBER(orderDetailDTO.getQte());
            detail.setIntQTEREPGROSSISTE(detail.getIntNUMBER());
            detail.setIntQTEMANQUANT(detail.getIntNUMBER());
            detail.setIntPAFDETAIL(familleGrossiste.getIntPAF());
            detail.setIntPRICEDETAIL(familleGrossiste.getIntPRICE());
            detail.setIntPRICE(detail.getIntPAFDETAIL() * detail.getIntNUMBER());
            detail.setLgFAMILLEID(famille);
            detail.setLgGROSSISTEID(order.getLgGROSSISTEID());
            detail.setStrSTATUT(Constant.STATUT_IS_PROGRESS);
            detail.setDtCREATED(new Date());
            detail.setDtUPDATED(detail.getDtCREATED());
            detail.setIntORERSTATUS((short) 2);
            detail.setPrixAchat(familleGrossiste.getIntPAF());
            this.getEmg().persist(detail);
            famille.setBCODEINDICATEUR((short) 1);
            this.getEmg().merge(famille);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            throw e;
        }

    }

    private void updateItem(TOrderDetail detail, int qte) {
        detail.setIntNUMBER(detail.getIntNUMBER() + qte);
        detail.setIntQTEREPGROSSISTE(detail.getIntNUMBER());
        detail.setIntQTEMANQUANT(detail.getIntNUMBER());
        detail.setIntPRICE(detail.getIntPAFDETAIL() * detail.getIntNUMBER());
        detail.setDtUPDATED(new Date());
        this.getEmg().merge(detail);
    }

    private void createOrUpdate(OrderDetailDTO orderDetailDTO, TOrder order) {
        findOne(orderDetailDTO.getFamilleId(), order.getLgORDERID()).ifPresentOrElse(
                it -> updateItem(it, orderDetailDTO.getQte()),
                () -> createOrderItem(order, orderDetailDTO, new KeyUtilGen()));

    }

    private Optional<TOrderDetail> findOne(String lgFamilleId, String orderId) {

        try {
            return Optional.ofNullable(this.getEmg().createQuery(
                    "SELECT t FROM TOrderDetail t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgORDERID.lgORDERID = ?2",
                    TOrderDetail.class).setParameter(1, lgFamilleId).setParameter(2, orderId).setMaxResults(1)
                    .getSingleResult());

        } catch (Exception e) {
            return Optional.empty();

        }

    }

    private CommandeCsvDTO buildFromOrderDetail(TOrderDetail d) {
        TFamille famille = d.getLgFAMILLEID();
        String code = famille.getIntEAN13();
        if (StringUtils.isEmpty(code)) {
            TFamilleGrossiste tfg = findFamilleGrossiste(d.getLgFAMILLEID().getLgFAMILLEID(),
                    d.getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
            if (Objects.nonNull(tfg) && StringUtils.isNotEmpty(tfg.getStrCODEARTICLE())) {
                code = tfg.getStrCODEARTICLE();
            } else {
                code = famille.getIntCIP();
            }

        }
        return new CommandeCsvDTO(code, d.getIntNUMBER());

    }

    @Override
    public Map<String, List<CommandeCsvDTO>> commandeEncoursCsv(String idCommande) {
        TOrder order = this.getEmg().find(TOrder.class, idCommande);
        return Map.of(order.getStrREFORDER(), order.getTOrderDetailCollection().stream().map(this::buildFromOrderDetail)
                .collect(Collectors.toList()));

    }

    @Override
    public void passerLaCommande(String orderId) {
        changeOrderStatuts(this.getEmg().find(TOrder.class, orderId), Constant.STATUT_PASSED, (short) 3);
    }

    @Override
    public void changerEnCommandeEnCours(String orderId) {
        changeOrderStatuts(this.getEmg().find(TOrder.class, orderId), Constant.STATUT_IS_PROGRESS, (short) 2);
    }

    private void changeOrderStatuts(TOrder order) {
        Date toDay = new Date();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<TOrderDetail> cq = cb.createCriteriaUpdate(TOrderDetail.class);
            Root<TOrderDetail> root = cq.from(TOrderDetail.class);
            cq.set(root.get(TOrderDetail_.strSTATUT), Constant.STATUT_PASSED)
                    .set(root.get(TOrderDetail_.dtUPDATED), toDay)
                    .set(root.get(TOrderDetail_.intORERSTATUS), (short) 3);

            cq.where(cb.equal(root.get(TOrderDetail_.lgORDERID), order));
            getEmg().createQuery(cq).executeUpdate();
            order.setStrSTATUT(Constant.STATUT_PASSED);
            order.setDtUPDATED(toDay);
            getEmg().merge(order);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
    }

    private void changeOrderStatuts(TOrder order, String status, short statutOrder) {
        Date toDay = new Date();
        // (short) 3

        order.getTOrderDetailCollection().forEach(it -> {
            updateOrderItemStatut(it, status, statutOrder, toDay);
            updateFamilleStatut(it.getLgFAMILLEID(), statutOrder, toDay);
        });

        // order.setStrSTATUT(Constant.STATUT_PASSED);
        order.setDtUPDATED(toDay);
        order.setStrSTATUT(status);
        getEmg().merge(order);

    }

    private void updateOrderItemStatut(TOrderDetail detail, String status, short statutOrder, Date date) {
        detail.setStrSTATUT(status);
        detail.setIntORERSTATUS(statutOrder);
        detail.setDtUPDATED(date);
        getEmg().merge(detail);

    }

    private void updateFamilleStatut(TFamille famille, short statutOrder, Date date) {
        short st = famille.getIntORERSTATUS();
        if (st < 4) {
            famille.setIntORERSTATUS(statutOrder);
            famille.setDtUPDATED(date);
            getEmg().merge(famille);
        }

    }
}
