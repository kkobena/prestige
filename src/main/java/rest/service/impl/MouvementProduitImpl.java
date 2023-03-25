/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.AjustementDTO;
import commonTasks.dto.AjustementDetailDTO;
import commonTasks.dto.Params;
import commonTasks.dto.SalesStatsParams;
import dal.HMvtProduit;
import dal.Notification;
import dal.TAjustement;
import dal.TAjustementDetail;
import dal.TAjustementDetail_;
import dal.TAjustement_;
import dal.TCodeTva;
import dal.TDeconditionnement;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TFamille_;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TUser;
import dal.TUser_;
import dal.Typemvtproduit;
import dal.enumeration.Canal;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.LogService;
import rest.service.MouvementProduitService;
import rest.service.NotificationService;
import rest.service.SuggestionService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author DICI
 */
@Stateless
public class MouvementProduitImpl implements MouvementProduitService {

    private static final Logger LOG = Logger.getLogger(MouvementProduitImpl.class.getName());
    @EJB
    SuggestionService suggestionService;
    @EJB
    LogService logService;
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    NotificationService notificationService;

    public EntityManager getEmg() {
        return em;
    }

    @Override
    public void updateStockDepot(TUser user, TPreenregistrement tp, TEmplacement OTEmplacement, EntityManager emg) throws Exception {
        List<TPreenregistrementDetail> list = getTPreenregistrementDetail(tp, emg);
        user = (user == null) ? tp.getLgUSERID() : user;
        for (TPreenregistrementDetail d : list) {
            TFamille tFamille = d.getLgFAMILLEID();
            updateStockDepot(user, tFamille, d.getIntQUANTITYSERVED(), OTEmplacement, emg);
        }
    }

    @Override
    public Typemvtproduit getTypemvtproduitByID(String id, EntityManager emg) {
        return emg.find(Typemvtproduit.class, id);
    }

    @Override
    public void saveMvtProduit(String pkey, Typemvtproduit typemvtproduit, TFamille famille, TUser lgUSERID, TEmplacement emplacement, Integer qteMvt, Integer qteDebut, Integer qteFinale, EntityManager emg, Integer valeurTva, boolean checked) {
        HMvtProduit h = new HMvtProduit();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(LocalDateTime.now());
        h.setEmplacement(emplacement);
        h.setLgUSERID(lgUSERID);
        h.setFamille(famille);
        h.setMvtDate(LocalDate.now());
        h.setValeurTva(valeurTva);
        h.setTypemvtproduit(typemvtproduit);
        h.setPrixUn(famille.getIntPRICE());
        h.setPrixAchat(famille.getIntPAF());
        h.setQteMvt(qteMvt);
        h.setQteDebut(qteDebut);
        h.setChecked(checked);
        h.setPkey(pkey);
        h.setQteFinale(qteFinale);
        emg.persist(h);
    }

    @Override
    public void saveMvtProduit(Integer prixUn, TPreenregistrementDetail preenregistrementDetail, Typemvtproduit typemvtproduit, TFamille famille, TUser lgUSERID, TEmplacement emplacement, Integer qteMvt, Integer qteDebut, Integer qteFinale, EntityManager emg, Integer valeurTva, boolean checked, int ug) {
        HMvtProduit h = new HMvtProduit();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(LocalDateTime.now());
        h.setEmplacement(emplacement);
        h.setLgUSERID(lgUSERID);
        h.setFamille(famille);
        h.setMvtDate(LocalDate.now());
        h.setTypemvtproduit(typemvtproduit);
        h.setQteMvt(qteMvt);
        h.setValeurTva(valeurTva);
        h.setQteDebut(qteDebut);
        h.setPrixUn(prixUn);
        h.setChecked(checked);
        h.setPrixAchat(famille.getIntPAF());
        h.setPkey(preenregistrementDetail.getLgPREENREGISTREMENTDETAILID());
        h.setQteFinale(qteFinale);
        h.setPreenregistrementDetail(preenregistrementDetail);
        h.setUg(ug);
        emg.persist(h);
    }

    @Override
    public void saveMvtProduit2(Integer prixUn, String pkey, Typemvtproduit typemvtproduit, TFamille famille, TUser lgUSERID, TEmplacement emplacement, Integer qteMvt, Integer qteDebut, Integer qteFinale, EntityManager emg, Integer valeurTva, boolean checked, int ug) {
        HMvtProduit h = new HMvtProduit();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(LocalDateTime.now());
        h.setEmplacement(emplacement);
        h.setLgUSERID(lgUSERID);
        h.setFamille(famille);
        h.setMvtDate(LocalDate.now());
        h.setTypemvtproduit(typemvtproduit);
        h.setQteMvt(qteMvt);
        h.setValeurTva(valeurTva);
        h.setQteDebut(qteDebut);
        h.setPrixUn(prixUn);
        h.setChecked(checked);
        h.setPrixAchat(famille.getIntPAF());
        h.setPkey(pkey);
        h.setQteFinale(qteFinale);
        h.setUg(ug);
        emg.persist(h);
    }

    @Override
    public void saveMvtProduit(String pkey, String typemvtproduit, TFamille famille, TUser lgUSERID, TEmplacement emplacement, Integer qteMvt, Integer qteDebut, Integer qteFinale, EntityManager emg, Integer valeurTva) {
        HMvtProduit h = new HMvtProduit();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(LocalDateTime.now());
        h.setEmplacement(emplacement);
        h.setLgUSERID(lgUSERID);
        h.setFamille(famille);
        h.setMvtDate(LocalDate.now());
        h.setTypemvtproduit(getTypemvtproduitByID(typemvtproduit, emg));
        h.setQteMvt(qteMvt);
        h.setValeurTva(valeurTva);
        h.setQteDebut(qteDebut);
        h.setPrixUn(famille.getIntPRICE());
        h.setPrixAchat(famille.getIntPAF());
        h.setPkey(pkey);
        h.setChecked(true);
        h.setQteFinale(qteFinale);
        emg.persist(h);
    }

    @Override
    public void saveMvtProduit(Integer prixUn, Integer prixAchat, String pkey, String typemvtproduit, TFamille famille, TUser lgUSERID, TEmplacement emplacement, Integer qteMvt, Integer qteDebut, Integer qteFinale, EntityManager emg, Integer valeurTva) {
        HMvtProduit h = new HMvtProduit();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(LocalDateTime.now());
        h.setEmplacement(emplacement);
        h.setLgUSERID(lgUSERID);
        h.setFamille(famille);
        h.setValeurTva(valeurTva);
        h.setMvtDate(LocalDate.now());
        h.setTypemvtproduit(getTypemvtproduitByID(typemvtproduit, emg));
        h.setQteMvt(qteMvt);
        h.setQteDebut(qteDebut);
        h.setPrixUn(prixUn);
        h.setPrixAchat(prixAchat);
        h.setPkey(pkey);
        h.setChecked(true);
        h.setQteFinale(qteFinale);
        emg.persist(h);
    }

    @Override
    public void saveMvtProduit(String pkey, Typemvtproduit typemvtproduit, TFamilleStock familleStock, TUser lgUSERID, TEmplacement emplacement, Integer qteMvt, Integer qteDebut, EntityManager emg, Integer valeurTva) {
        HMvtProduit h = new HMvtProduit();
        TFamille famille = familleStock.getLgFAMILLEID();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(LocalDateTime.now());
        h.setEmplacement(emplacement);
        h.setLgUSERID(lgUSERID);
        h.setPkey(pkey);
        h.setFamille(famille);
        h.setPrixUn(famille.getIntPRICE());
        h.setPrixAchat(famille.getIntPAF());
        h.setMvtDate(LocalDate.now());
        h.setValeurTva(valeurTva);
        h.setTypemvtproduit(typemvtproduit);
        h.setQteMvt(qteMvt);
        h.setQteDebut(qteDebut);
        h.setChecked(true);
        h.setQteFinale(familleStock.getIntNUMBERAVAILABLE());
        emg.persist(h);

    }

    private void ajusterProduitAjustement(Params params, TAjustement ajustement, EntityManager emg) {
        TAjustementDetail OTAjustementDetail = updateAjustementDetail(params, emg);
        if (OTAjustementDetail == null) {
            TEmplacement emplacement = ajustement.getLgUSERID().getLgEMPLACEMENTID();
            TFamilleStock familleStock = findByProduitId(params.getRefTwo(), emplacement.getLgEMPLACEMENTID(), emg);
            Integer currentStock = familleStock.getIntNUMBERAVAILABLE();
            OTAjustementDetail = new TAjustementDetail();
            OTAjustementDetail.setLgAJUSTEMENTDETAILID(UUID.randomUUID().toString());
            OTAjustementDetail.setLgAJUSTEMENTID(ajustement);
            OTAjustementDetail.setLgFAMILLEID(familleStock.getLgFAMILLEID());
            OTAjustementDetail.setIntNUMBER(params.getValue());
            OTAjustementDetail.setIntNUMBERCURRENTSTOCK(currentStock);
            OTAjustementDetail.setIntNUMBERAFTERSTOCK(params.getValue() + currentStock);
            OTAjustementDetail.setDtCREATED(new Date());
            OTAjustementDetail.setDtUPDATED(new Date());
            OTAjustementDetail.setStrSTATUT(commonparameter.statut_is_Process);
            emg.persist(OTAjustementDetail);
        }
    }

    @Override
    public JSONObject creerAjustement(Params params) throws JSONException {
        EntityManager emg = this.getEmg();
        JSONObject json = new JSONObject();
        try {
            String str_NAME = "Ajustement du " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"));
//            emg.getTransaction().begin();
            TAjustement OTAjustement = new TAjustement();
            OTAjustement.setLgAJUSTEMENTID(UUID.randomUUID().toString());
            OTAjustement.setLgUSERID(params.getOperateur());
            OTAjustement.setStrNAME(str_NAME);
            OTAjustement.setStrCOMMENTAIRE(params.getDescription());
            OTAjustement.setDtCREATED(new Date());
            OTAjustement.setDtUPDATED(new Date());
            OTAjustement.setStrSTATUT(commonparameter.statut_is_Process);
            emg.persist(OTAjustement);
            ajusterProduitAjustement(params, OTAjustement, emg);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            json.put("data", new JSONObject().put("lgAJUSTEMENTID", OTAjustement.getLgAJUSTEMENTID()));
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            json.put("success", false).put("msg", "L'opération a échoué");
        }
        return json;
    }

    @Override
    public JSONObject ajusterProduitAjustement(Params params) throws JSONException {
        EntityManager emg = this.getEmg();
        JSONObject json = new JSONObject();
        try {
            TAjustement ajustement = emg.find(TAjustement.class, params.getRefParent());
//            emg.getTransaction().begin();
            ajusterProduitAjustement(params, ajustement, emg);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            json.put("data", new JSONObject().put("lgAJUSTEMENTID", ajustement.getLgAJUSTEMENTID()));
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            json.put("success", false).put("msg", "L'opération a échoué");

        }
        return json;
    }

    @Override
    public JSONObject modifierProduitAjustement(Params params) throws JSONException {
        EntityManager emg = this.getEmg();
        JSONObject json = new JSONObject();
        try {

            TAjustementDetail ajustementDetail = emg.find(TAjustementDetail.class, params.getRef());
            if (ajustementDetail == null) {

                json.put("success", false).put("msg", "L'opération a échoué");
                return json;
            }
//            emg.getTransaction().begin();
            ajustementDetail.setIntNUMBER(params.getValue());
            ajustementDetail.setIntNUMBERAFTERSTOCK(params.getValue() + params.getValueTwo());
            ajustementDetail.setDtUPDATED(new Date());
            emg.merge(ajustementDetail);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            json.put("data", new JSONObject().put("lgAJUSTEMENTID", ajustementDetail.getLgAJUSTEMENTID().getLgAJUSTEMENTID()));

            return json;

        } catch (Exception e) {
            e.printStackTrace(System.err);
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            json.put("success", false).put("msg", "L'opération a échoué");
            return json;
        }
    }

    @Override
    public JSONObject cloreAjustement(Params params) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {

            TAjustement ajustement = emg.find(TAjustement.class, params.getRefParent());
            if (ajustement == null) {

                json.put("success", false).put("msg", "L'opération a échoué");
                return json;
            }
            TUser tUser = ajustement.getLgUSERID();
            TEmplacement emplacement = tUser.getLgEMPLACEMENTID();
//            emg.getTransaction().begin();
            List<TAjustementDetail> ajustementDetails = findAjustementDetailsByParenId(ajustement.getLgAJUSTEMENTID(), emg);
            ajustementDetails.forEach(it -> {
                TFamille famille = it.getLgFAMILLEID();
                TFamilleStock familleStock = findByProduitId(famille.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID(), emg);
                Integer initStock = familleStock.getIntNUMBERAVAILABLE();
                familleStock.setIntNUMBERAVAILABLE(it.getIntNUMBERAFTERSTOCK());
                familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
                familleStock.setDtUPDATED(new Date());
                emg.merge(familleStock);
                int compare = initStock.compareTo(it.getIntNUMBERAFTERSTOCK());
                String action = (compare < 0) ? DateConverter.AJUSTEMENT_POSITIF : DateConverter.AJUSTEMENT_NEGATIF;
                saveMvtProduit(it.getLgAJUSTEMENTDETAILID(), getTypemvtproduitByID(action, emg), familleStock, tUser, emplacement, it.getIntNUMBER(), initStock, emg, 0);
                suggestionService.makeSuggestionAuto(familleStock, famille);
                String desc = "Ajustement du produit :[  " + famille.getIntCIP() + "  " + famille.getStrNAME() + " ] : Quantité initiale : [ " + initStock + " ] : Quantité ajustée [ " + it.getIntNUMBER() + " ] :Quantité finale [ " + (initStock + it.getIntNUMBER()) + " ]";
                logService.updateItem(tUser, famille.getIntCIP(), desc, TypeLog.AJUSTEMENT_DE_PRODUIT, famille, emg);
                it.setStrSTATUT(commonparameter.statut_enable);
                it.setDtUPDATED(new Date());
                emg.merge(it);
                notificationService.save(new Notification()
                        .canal(Canal.EMAIL)
                        .typeNotification(TypeNotification.AJUSTEMENT_DE_PRODUIT)
                        .message(desc)
                        .addUser(tUser)
                );

            });
            ajustement.setStrCOMMENTAIRE(params.getDescription());
            ajustement.setDtUPDATED(new Date());
            ajustement.setStrSTATUT(commonparameter.statut_enable);
            emg.merge(ajustement);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            return json;

        } catch (Exception e) {
            e.printStackTrace(System.err);
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            json.put("success", false).put("msg", "L'opération a échoué");
            return json;
        }
    }

    private List<TAjustementDetail> findAjustementDetailsByParenId(String idParent, EntityManager em) {
        return em.createQuery("SELECT o FROM TAjustementDetail o WHERE o.lgAJUSTEMENTID.lgAJUSTEMENTID=?1 ", TAjustementDetail.class).setParameter(1, idParent).getResultList();
    }

    @Override
    public JSONObject findOneAjustement(String idAjustement) throws JSONException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatefamillenbvente(TFamille famille, Integer qty, boolean updatable, EntityManager emg) {
        if (updatable) {
            famille.setDtLASTMOUVEMENT(new Date());
            famille.setIntQTERESERVEE(famille.getIntNBRESORTIE() + qty);
            emg.merge(famille);
        }

    }

    private long countAjustement(SalesStatsParams params, EntityManager emg) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TAjustementDetail> root = cq.from(TAjustementDetail.class);
            Join<TAjustementDetail, TAjustement> st = root.join("lgAJUSTEMENTID", JoinType.INNER);
            cq.select(cb.countDistinct(root.get(TAjustementDetail_.lgAJUSTEMENTID)));

            Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TAjustement_.dtUPDATED)), java.sql.Date.valueOf(params.getDtStart()),
                    java.sql.Date.valueOf(params.getDtEnd()));

            predicates.add(cb.and(btw));
            predicates.add(cb.and(cb.equal(st.get(TAjustement_.strSTATUT), commonparameter.statut_enable)));

            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            if (!params.isShowAll()) {
                predicates.add(cb.and(cb.equal(st.get(TAjustement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public JSONObject ajsutements(SalesStatsParams params) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {
            long count = countAjustement(params, emg);
            if (count == 0) {
                json.put("total", count);
                json.put("data", new JSONArray());
                return json;
            }
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<TAjustement> cq = cb.createQuery(TAjustement.class);
            Root<TAjustementDetail> root = cq.from(TAjustementDetail.class);
            Join<TAjustementDetail, TAjustement> st = root.join("lgAJUSTEMENTID", JoinType.INNER);
            cq.select(root.get(TAjustementDetail_.lgAJUSTEMENTID)).distinct(true).orderBy(cb.asc(st.get(TAjustement_.dtUPDATED)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TAjustement_.dtUPDATED)), java.sql.Date.valueOf(params.getDtStart()),
                    java.sql.Date.valueOf(params.getDtEnd()));
            predicates.add(cb.and(btw));
            predicates.add(cb.and(cb.equal(st.get(TAjustement_.strSTATUT), commonparameter.statut_enable)));

            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            if (!params.isShowAll()) {
                predicates.add(cb.and(cb.equal(st.get(TAjustement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            if (!params.isAll()) {
                q.setFirstResult(params.getStart());
                q.setMaxResults(params.getLimit());
            }
            List<TAjustement> list = q.getResultList();
            List<AjustementDTO> data = list.stream().map(v -> new AjustementDTO(v, findAjustementDetailsByParenId(v.getLgAJUSTEMENTID(), emg), params.isCanCancel())).collect(Collectors.toList());

            json.put("total", count);
            json.put("data", new JSONArray(data));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("total", 0);
            json.put("data", new JSONArray());
        }
        return json;
    }

    public TFamilleStock findByProduitId(String produitId, String emplecementId, EntityManager emg) {
        TFamilleStock familleStock = null;
        try {
            TypedQuery<TFamilleStock> query = emg.createQuery("SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable' ORDER BY t.dtCREATED DESC", TFamilleStock.class);
            query.
                    setParameter(1, produitId);
            query.
                    setParameter(2, emplecementId);
            query.setMaxResults(1);
            familleStock = query.getSingleResult();
        } catch (Exception e) {
//            e.printStackTrace(System.err);
        }
        return familleStock;
    }

    public TFamille findByParent(String parentId, EntityManager emg) {
        TFamille famille = null;
        try {
            TypedQuery<TFamille> query = emg.createQuery("SELECT t FROM TFamille t WHERE  t.lgFAMILLEPARENTID = ?1  ORDER BY t.dtCREATED DESC", TFamille.class);
            query.
                    setParameter(1, parentId);
            query.setMaxResults(1);
            famille = query.getSingleResult();
        } catch (Exception e) {
//            e.printStackTrace(System.err);
        }
        return famille;
    }

    private TFamilleStock createStock(TFamille OTFamille, Integer qte, TEmplacement OTEmplacement, EntityManager emg) {
        TFamilleStock OTFamilleStock = new TFamilleStock();
        OTFamilleStock.setLgFAMILLESTOCKID(UUID.randomUUID().toString());
        OTFamilleStock.setIntNUMBER(qte);
        OTFamilleStock.setIntNUMBERAVAILABLE(qte);
        OTFamilleStock.setLgEMPLACEMENTID(OTEmplacement);
        OTFamilleStock.setLgFAMILLEID(OTFamille);
        OTFamilleStock.setStrSTATUT(commonparameter.statut_enable);
        OTFamilleStock.setDtCREATED(new Date());
        OTFamilleStock.setDtUPDATED(new Date());
        OTFamilleStock.setIntUG(0);
        OTFamilleStock.setLgEMPLACEMENTID(OTEmplacement);
        emg.persist(OTFamilleStock);
        return OTFamilleStock;

    }

    private TAjustementDetail updateAjustementDetail(Params params, EntityManager emg) {
        try {
            if (params.getRef() == null) {
                return null;
            }
            TAjustementDetail ajustementDetail = emg.find(TAjustementDetail.class, params.getRef());
            if (ajustementDetail == null) {
                return null;
            }
            ajustementDetail.setIntNUMBER(ajustementDetail.getIntNUMBER() + params.getValue());
            ajustementDetail.setIntNUMBERAFTERSTOCK(ajustementDetail.getIntNUMBERAFTERSTOCK() + params.getValue());
            ajustementDetail.setDtUPDATED(new Date());
            return emg.merge(ajustementDetail);

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }

    @Override
    public JSONObject removeAjustementDetail(String id) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {

            TAjustementDetail ajustementDetail = emg.find(TAjustementDetail.class, id);
//            emg.getTransaction().begin();
            emg.remove(ajustementDetail);
//            emg.getTransaction().commit();
            return json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            return json.put("success", false).put("msg", "Opération a échoué");
        }
    }

    @Override
    public JSONObject ajsutementsDetails(SalesStatsParams params, String idAjustement) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<TAjustementDetail> cq = cb.createQuery(TAjustementDetail.class);
            Root<TAjustementDetail> root = cq.from(TAjustementDetail.class);
            Join<TAjustementDetail, TAjustement> st = root.join("lgAJUSTEMENTID", JoinType.INNER);
            cq.select(root).orderBy(cb.asc(root.get(TAjustementDetail_.dtUPDATED)));
            predicates.add(cb.and(cb.equal(st.get(TAjustement_.lgAJUSTEMENTID), idAjustement)));
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            if (!params.isAll()) {
                q.setFirstResult(params.getStart());
                q.setMaxResults(params.getLimit());
            }
            List<TAjustementDetail> list = q.getResultList();
            List<AjustementDetailDTO> data = list.stream().map(AjustementDetailDTO::new).collect(Collectors.toList());
            json.put("total", list.size());
            json.put("data", new JSONArray(data));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("total", 0);
            json.put("data", new JSONArray());
        }
        return json;
    }

    @Override
    public JSONObject annulerAjustement(String id) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {

            TAjustement ajustement = emg.find(TAjustement.class, id);
            List<TAjustementDetail> ajustementDetails = findAjustementDetailsByParenId(id, emg);
//            emg.getTransaction().begin();
            ajustementDetails.forEach(c -> {
                emg.remove(c);
            });
            emg.remove(ajustement);
//            emg.getTransaction().commit();
            return json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            return json.put("success", false).put("msg", "Opération a échoué");
        }
    }

    private TFamille findProduitById(String id, EntityManager emg) {

        try {
            return emg.find(TFamille.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean checkIsVentePossible(TFamilleStock OTFamilleStock, int qte
    ) {
        return OTFamilleStock.getIntNUMBERAVAILABLE() >= qte;
    }

    private TDeconditionnement createDecondtionne(TFamille OTFamille, int int_NUMBER, TUser tUser, EntityManager emg) {

        TDeconditionnement OTDeconditionnement = new TDeconditionnement();
        OTDeconditionnement.setLgDECONDITIONNEMENTID(UUID.randomUUID().toString());
        OTDeconditionnement.setLgFAMILLEID(OTFamille);
        OTDeconditionnement.setLgUSERID(tUser);
        OTDeconditionnement.setIntNUMBER(int_NUMBER);
        OTDeconditionnement.setDtCREATED(new Date());
        OTDeconditionnement.setStrSTATUT(commonparameter.statut_enable);
        emg.persist(OTDeconditionnement);
        return OTDeconditionnement;
    }

    private TFamilleStock deconditionner(TUser tu, TEmplacement te, TFamille OTFamilleChild, TFamille OTFamilleParent, TFamilleStock OTFamilleStockParent, TFamilleStock OTFamilleStockChild, Integer qteVendue, EntityManager emg) {
        Integer numberToDecondition = 0;
        Integer qtyDetail = OTFamilleParent.getIntNUMBERDETAIL();
        Integer stockInitDetail = OTFamilleStockChild.getIntNUMBERAVAILABLE();
        Integer stockInit = OTFamilleStockParent.getIntNUMBERAVAILABLE();
        Integer stockVirtuel = stockInitDetail + (stockInit * qtyDetail);
        int compare = stockVirtuel.compareTo(qteVendue);
        if (compare >= 0) {
            while (stockInitDetail < qteVendue) {
                numberToDecondition++;
                stockInitDetail += qtyDetail;
            }
            OTFamilleStockParent.setIntNUMBERAVAILABLE(OTFamilleStockParent.getIntNUMBERAVAILABLE() - numberToDecondition);
            OTFamilleStockParent.setIntNUMBER(OTFamilleStockParent.getIntNUMBERAVAILABLE());
            OTFamilleStockParent.setDtUPDATED(new Date());
            OTFamilleStockChild.setIntNUMBERAVAILABLE(OTFamilleStockChild.getIntNUMBERAVAILABLE() + (numberToDecondition * qtyDetail));
            OTFamilleStockChild.setIntNUMBER(OTFamilleStockChild.getIntNUMBERAVAILABLE());
            OTFamilleStockChild.setDtUPDATED(new Date());
            emg.merge(OTFamilleStockParent);
            emg.merge(OTFamilleStockChild);
            TDeconditionnement parent = createDecondtionne(OTFamilleParent, numberToDecondition, tu, emg);
            TDeconditionnement child = createDecondtionne(OTFamilleChild, (numberToDecondition * qtyDetail), tu, emg);
            saveMvtProduit(child.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_POSITIF, OTFamilleChild, tu, OTFamilleStockParent.getLgEMPLACEMENTID(), (numberToDecondition * qtyDetail), stockInitDetail, stockInitDetail + (numberToDecondition * qtyDetail) - qteVendue, emg, 0);
            saveMvtProduit(parent.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_NEGATIF, OTFamilleParent, tu, OTFamilleStockParent.getLgEMPLACEMENTID(), numberToDecondition, stockInit, stockInit - numberToDecondition, emg, 0);
        }
        return OTFamilleStockChild;
    }

    @Override
    public void updateVenteStock(TPreenregistrement tp, List<TPreenregistrementDetail> list, EntityManager emg) {
        TUser tu = tp.getLgUSERID();
        final TEmplacement emplacement = tu.getLgEMPLACEMENTID();
        final String emplacementId = emplacement.getLgEMPLACEMENTID();
        final boolean isDepot = !("1".equals(emplacementId));
        final Typemvtproduit typemvtproduit = getTypemvtproduitByID(DateConverter.VENTE, emg);
        list.stream().forEach(it -> {
            it.setStrSTATUT(commonparameter.statut_is_Closed);
            TFamille tFamille = it.getLgFAMILLEID();
            TCodeTva codeTva = tFamille.getLgCODETVAID();
            Integer valeurTva = 0;
            if (codeTva != null) {
                valeurTva = codeTva.getIntVALUE();
            }
            TFamilleStock familleStock = findStock(tFamille.getLgFAMILLEID(), emplacement, emg);
            Integer qtyDebut = familleStock.getIntNUMBERAVAILABLE();
            if (tFamille.getBoolDECONDITIONNE() == 1) {
                if (!checkIsVentePossible(familleStock, it.getIntQUANTITY())) {
                    TFamille OTFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                    TFamilleStock stockParent = findByProduitId(OTFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID(), emg);
                    deconditionner(tu, emplacement, tFamille, OTFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);

                } else {
                    familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY());
                    emg.merge(familleStock);
                    saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), typemvtproduit, tFamille, tu, emplacement, it.getIntQUANTITY(), qtyDebut, (qtyDebut - it.getIntQUANTITY()), emg, valeurTva, true);

                }
            } else {
                familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY());
                emg.merge(familleStock);
                saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), typemvtproduit, tFamille, tu, emplacement, it.getIntQUANTITY(), qtyDebut, (qtyDebut - it.getIntQUANTITY()), emg, valeurTva, true);
            }

            updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);

            emg.merge(it);
            suggestionService.makeSuggestionAuto(familleStock, tFamille);
        });

    }

    private List<TPreenregistrementDetail> getTPreenregistrementDetail(TPreenregistrement tp, EntityManager emg) {
        try {
            return emg.
                    createQuery("SELECT t FROM TPreenregistrementDetail t WHERE  t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1").
                    setParameter(1, tp.getLgPREENREGISTREMENTID()).
                    getResultList();

        } catch (Exception ex) {
            return Collections.emptyList();
        }

    }

    @Override
    public void updateVenteStock(String idVente) {
        EntityManager emg = this.getEmg();
        try {
            TPreenregistrement tp = emg.find(TPreenregistrement.class, idVente);
            List<TPreenregistrementDetail> list = getTPreenregistrementDetail(tp, emg);
            TUser tu = tp.getLgUSERID();
            final TEmplacement emplacement = tu.getLgEMPLACEMENTID();
            final String emplacementId = emplacement.getLgEMPLACEMENTID();
            final boolean isDepot = !("1".equals(emplacementId));
            final Typemvtproduit typemvtproduit = getTypemvtproduitByID(DateConverter.VENTE, emg);
            list.stream().forEach(it -> {
                TFamille tFamille = it.getLgFAMILLEID();
                TCodeTva codeTva = tFamille.getLgCODETVAID();
                Integer valeurTva = 0;
                if (codeTva != null) {
                    valeurTva = codeTva.getIntVALUE();
                }
                TFamilleStock familleStock = findStock(tFamille.getLgFAMILLEID(), emplacement, emg);

                Integer qtyDebut = familleStock.getIntNUMBERAVAILABLE();
                if (tFamille.getBoolDECONDITIONNE() == 1) {
                    if (!checkIsVentePossible(familleStock, it.getIntQUANTITY())) {
                        TFamille OTFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                        TFamilleStock stockParent = findByProduitId(OTFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID(), emg);
                        deconditionner(tu, emplacement, tFamille, OTFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);

                    } else {
                        familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY());
                        emg.merge(familleStock);
                        saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), typemvtproduit, tFamille, tu, emplacement, it.getIntQUANTITY(), qtyDebut, (qtyDebut - it.getIntQUANTITY()), emg, valeurTva, true);

                    }
                } else {
                    familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY());
                    emg.merge(familleStock);
                    saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), typemvtproduit, tFamille, tu, emplacement, it.getIntQUANTITY(), qtyDebut, (qtyDebut - it.getIntQUANTITY()), emg, valeurTva, true);

                }

                updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);

                emg.merge(it);

            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
    }

    public TFamilleStock findByParent(String parentId, String emplecementId, EntityManager emg) {
        TFamilleStock familleStock = null;
        try {
            TypedQuery<TFamilleStock> query = emg.createQuery("SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEPARENTID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable' ORDER BY t.dtCREATED DESC", TFamilleStock.class);
            query.
                    setParameter(1, parentId);
            query.
                    setParameter(2, emplecementId);
            query.setMaxResults(1);
            familleStock = query.getSingleResult();
        } catch (Exception e) {
//            e.printStackTrace(System.err);
        }
        return familleStock;
    }

    private void updateStockDepot(TUser ooTUser, TFamille OTFamille, Integer qty, TEmplacement OTEmplacement, EntityManager emg) {
        Integer initStock = 0, qteFinale = 0;
        TFamilleStock familleStock;

        boolean isDetail = (OTFamille.getLgFAMILLEPARENTID() != null && !"".equals(OTFamille.getLgFAMILLEPARENTID()));
        familleStock = findByProduitId(OTFamille.getLgFAMILLEID(), OTEmplacement.getLgEMPLACEMENTID(), emg);
        if (familleStock != null) {
            initStock = familleStock.getIntNUMBERAVAILABLE();
            familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() + qty);
            qteFinale = familleStock.getIntNUMBERAVAILABLE();
            familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
            familleStock.setDtUPDATED(new Date());
            emg.merge(familleStock);
        }
        if (familleStock == null) {
            if (isDetail) {
                familleStock = findByParent(OTFamille.getLgFAMILLEPARENTID(), OTEmplacement.getLgEMPLACEMENTID(), emg);
                if (familleStock == null) {
                    createStock(OTFamille, qty, OTEmplacement, emg);
                    qteFinale = qty;
                } else {
                    initStock = familleStock.getIntNUMBERAVAILABLE();
                    familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() + qty);
                    familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
                    familleStock.setDtUPDATED(new Date());
                    emg.merge(familleStock);
                    qteFinale = familleStock.getIntNUMBERAVAILABLE();
                }
            } else {
                familleStock = createStock(OTFamille, qty, OTEmplacement, emg);
                qteFinale = qty;
                TFamille child = findByParent(OTFamille.getLgFAMILLEID(), emg);
                if (child != null) {
                    familleStock = findByParent(OTFamille.getLgFAMILLEID(), OTEmplacement.getLgEMPLACEMENTID(), emg);
                    if (familleStock == null) {
                        createStock(child, 0, OTEmplacement, emg);
                    }
                }

            }

        }
        saveMvtProduit(familleStock.getLgFAMILLESTOCKID(), getTypemvtproduitByID(DateConverter.ENTREE_EN_STOCK, emg), OTFamille, ooTUser, OTEmplacement, qty, initStock, qteFinale, emg, 0, true);
    }

    @Override
    public void updateVenteStockDepot(TPreenregistrement tp, List<TPreenregistrementDetail> list, EntityManager emg, TEmplacement depot) throws Exception {
        TUser tu = tp.getLgUSERID();
        final TEmplacement emplacement = tu.getLgEMPLACEMENTID();
        final String emplacementId = emplacement.getLgEMPLACEMENTID();
        final boolean isDepot = !("1".equals(emplacementId));
        final Typemvtproduit typemvtproduit = getTypemvtproduitByID(DateConverter.TMVTP_VENTE_DEPOT_EXTENSION, emg);
        list.stream().forEach(it -> {
            it.setStrSTATUT(commonparameter.statut_is_Closed);
            TFamille tFamille = it.getLgFAMILLEID();
            TCodeTva tva = tFamille.getLgCODETVAID();
            Integer valeurTva = 0;
            if (tva != null) {
                valeurTva = tva.getIntVALUE();
            }
            TFamilleStock familleStock = findStock(tFamille.getLgFAMILLEID(), emplacement, emg);
            Integer qtyDebut = familleStock.getIntNUMBERAVAILABLE();
            if (tFamille.getBoolDECONDITIONNE() == 1) {
                if (!checkIsVentePossible(familleStock, it.getIntQUANTITY())) {
                    TFamille OTFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                    TFamilleStock stockParent = findByProduitId(OTFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID(), emg);
                    familleStock = deconditionner(tu, emplacement, tFamille, OTFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);
                    saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), typemvtproduit, tFamille, tu, emplacement, it.getIntQUANTITY(), qtyDebut, (familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY()), emg, valeurTva, false);
                } else {
                    familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY());
                    emg.merge(familleStock);
                    saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), typemvtproduit, tFamille, tu, emplacement, it.getIntQUANTITY(), qtyDebut, (qtyDebut - it.getIntQUANTITY()), emg, valeurTva, false);
                }
            } else {
                familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY());
                emg.merge(familleStock);
                saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), typemvtproduit, tFamille, tu, emplacement, it.getIntQUANTITY(), qtyDebut, (qtyDebut - it.getIntQUANTITY()), emg, valeurTva, false);
            }
            updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
            emg.merge(it);
            updateStockDepot(tu, tFamille, it.getIntQUANTITYSERVED(), depot, emg);
            suggestionService.makeSuggestionAuto(familleStock, tFamille);
        });
    }

    @Override
    public TFamilleStock findStock(String OTFamille, TEmplacement emplacement, EntityManager emg) {
        try {
            Query query = emg.createQuery("SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable' ORDER BY t.dtCREATED DESC");
            query.
                    setParameter(1, OTFamille);
            query.
                    setParameter(2, emplacement.getLgEMPLACEMENTID());
            query.setMaxResults(1);
            TFamilleStock familleStock = (TFamilleStock) query.getSingleResult();
            LOG.log(Level.INFO, "familleStock {0} ", new Object[]{familleStock});
            return familleStock;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }

    }

    private HMvtProduit findByItemVenteId(String idVenteItem) {
        TypedQuery<HMvtProduit> tq = getEmg().createQuery("SELECT o FROM HMvtProduit o WHERE o.pkey=?1 ", HMvtProduit.class);
        tq.setParameter(1, idVenteItem);
        tq.setMaxResults(1);
        return tq.getSingleResult();
    }

    @Override
    public void updateVenteStock(TUser tu, List<TPreenregistrementDetail> list) throws Exception {
        list.stream().forEach(it -> {
            HMvtProduit old = findByItemVenteId(it.getLgPREENREGISTREMENTDETAILID());
            saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), old, tu);
            updatefamillenbvente(old.getFamille(), old.getQteMvt(), true, getEmg());
        });
    }

    public void saveMvtProduit(String pkey, HMvtProduit old, TUser lgUSERID) {
        HMvtProduit h = new HMvtProduit();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(old.getCreatedAt());
        h.setEmplacement(old.getEmplacement());
        h.setLgUSERID(lgUSERID);
        h.setFamille(old.getFamille());
        h.setMvtDate(old.getMvtDate());
        h.setValeurTva(old.getValeurTva());
        h.setTypemvtproduit(old.getTypemvtproduit());
        h.setPrixUn(old.getPrixUn());
        h.setPrixAchat(old.getPrixAchat());
        h.setQteMvt(old.getQteMvt());
        h.setQteDebut(old.getQteDebut());
        h.setChecked(old.getChecked());
        h.setPkey(pkey);
        h.setQteFinale(old.getQteFinale());
        getEmg().persist(h);
    }

    @Override
    public void saveMvtProduit(String venteId, LocalDateTime dateVente, TFamille famille, TUser lgUSERID, TEmplacement emplacement, Integer qteMvt, Integer qteDebut, Integer qteFinale, Integer valeurTva, boolean checked) {
        HMvtProduit h = new HMvtProduit();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(dateVente);
        h.setEmplacement(emplacement);
        h.setLgUSERID(lgUSERID);
        h.setFamille(famille);
        h.setMvtDate(dateVente.toLocalDate());
        h.setValeurTva(valeurTva);
        h.setTypemvtproduit(getTypemvtproduitByID(DateConverter.VENTE, getEmg()));
        h.setPrixUn(famille.getIntPRICE());
        h.setPrixAchat(famille.getIntPAF());
        h.setQteMvt(qteMvt);
        h.setQteDebut(qteDebut);
        h.setChecked(checked);
        h.setPkey(venteId);
        h.setQteFinale(qteFinale);
        getEmg().persist(h);
    }

    @Override
    public void updateVenteStock2(String idVente) {
        EntityManager emg = this.getEmg();
        try {
            TPreenregistrement tp = emg.find(TPreenregistrement.class, idVente);
            List<TPreenregistrementDetail> list = getTPreenregistrementDetail(tp, emg);
            TUser tu = tp.getLgUSERID();
            final TEmplacement emplacement = tu.getLgEMPLACEMENTID();
            final String emplacementId = emplacement.getLgEMPLACEMENTID();
            final boolean isDepot = !("1".equals(emplacementId));
            LocalDateTime dateTime = LocalDateTime.parse("2021-12-17 21:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            list.stream().forEach(it -> {
                TFamille tFamille = it.getLgFAMILLEID();
                TCodeTva codeTva = tFamille.getLgCODETVAID();
                Integer valeurTva = 0;
                if (codeTva != null) {
                    valeurTva = codeTva.getIntVALUE();
                }
                TFamilleStock familleStock = findStock(tFamille.getLgFAMILLEID(), emplacement, emg);
                Integer qtyDebut = familleStock.getIntNUMBERAVAILABLE();
                familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY());
                emg.merge(familleStock);
                saveMvtProduit(it.getLgPREENREGISTREMENTDETAILID(), dateTime, tFamille, tu, emplacement, it.getIntQUANTITY(), qtyDebut, (qtyDebut - it.getIntQUANTITY()), valeurTva, true);
                updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
                emg.merge(it);

            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
    }
}
