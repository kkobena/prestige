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
import commonTasks.dto.MedecinDTO;
import commonTasks.dto.MontantAPaye;
import commonTasks.dto.QueryDTO;
import commonTasks.dto.SalesParams;
import commonTasks.dto.SearchDTO;
import commonTasks.dto.TiersPayantParams;
import commonTasks.dto.VenteDetailsDTO;
import commonTasks.dto.VenteRequest;
import dal.AnnulationRecette;
import dal.AnnulationSnapshot;
import dal.Medecin;
import dal.MvtTransaction;
import dal.Notification;
import dal.Reference;
import dal.TAyantDroit;
import dal.TCashTransaction;
import dal.TClient;
import dal.TCodeTva;
import dal.TCompteClient;
import dal.TCompteClientTiersPayant;
import dal.TEmplacement;
import dal.TEventLog;
import dal.TFactureDetail;
import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TFamilleStock;
import dal.TFamilleStock_;
import dal.TFamille_;
import dal.TGrilleRemise;
import dal.TModeReglement;
import dal.TMotifReglement;
import dal.TMouvement;
import dal.TMouvementSnapshot;
import dal.TNatureVente;
import dal.TParameters;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClient;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementDetail;
import dal.TPreenregistrementDetail_;
import dal.TPreenregistrement_;
import dal.TRecettes;
import dal.TReglement;
import dal.TRemise;
import dal.TResumeCaisse;
import dal.TTiersPayant;
import dal.TTypeMvtCaisse;
import dal.TTypeRecette;
import dal.TTypeReglement;
import dal.TTypeVente;
import dal.TUser;
import dal.TWorkflowRemiseArticle;
import dal.Typemvtproduit;
import dal.enumeration.Canal;
import dal.enumeration.CategoryTransaction;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;
import dal.enumeration.TypeTransaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.QueryHints;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.qualifier.SalesPrimary;
import rest.service.*;
import toolkits.parameters.commonparameter;
import toolkits.utils.StringComplexUtils.DataStringManager;
import util.Afficheur;
import util.DateConverter;

/**
 * @author Kobena
 */
@Stateless

@SalesPrimary
public class SalesServiceImpl implements SalesService {
//qry.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.
//BYPASS);
//    ypedQuery<Object[]> qry = em.createQuery("select a.name, a.genre,
//a.description " +
//"from BookStore s JOIN TREAT(s.categories as ItCategory) a",
//Object[].class);
//    

    private static final Logger LOG = Logger.getLogger(SalesServiceImpl.class.getName());
    @Inject
    JMSContext ctx;
    @Resource(lookup = "java:global/queue/mvtstock")
    Queue mvtStock;
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    LogService logService;
    @EJB
    MvtProduitService mvtProduitService;
    @EJB
    MouvementProduitService mouvementProduitService;
    @EJB
    SalesStatsService salesStatsService;
    @Resource(lookup = "java:global/queue/clientjms")
    Queue clientjms;
    @EJB
    CommonService commonService;
    @EJB
    MedecinService medecinService;
    @EJB
    ClientService clientService;
    @EJB
    NotificationService notificationService;
    
    public EntityManager getEm() {
        return em;
    }
    
    public void sendMessageMvtsStockQueue(String msg) {
        ctx.createProducer().send(mvtStock, msg);
    }
    
    void sendMessageClientJmsQueue(String venteId) {
        ctx.createProducer().send(clientjms, venteId);
    }
    
    public void sendSms(String msg) {
        if (checkParameterByKey(DateConverter.KEY_SMS_CLOTURE_CAISSE)) {
//            ctx.createProducer().send(amqsms, msg);
        }
        
    }
    
    public void addTransaction(TClient client, TUser ooTUser, TUser caisse, String pkey,
            Integer montant, Integer voidAmount, Integer montantNet, Integer montantVerse, Boolean checked,
            CategoryTransaction categoryTransaction, TypeTransaction typeTransaction,
            TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse, EntityManager emg,
            Integer montantPaye, Integer montantTva, Integer marge, String reference, int montantAcc, LocalDateTime dateTime, LocalDate localDate) throws Exception {
        MvtTransaction _new = new MvtTransaction();
        int compare = montantNet.compareTo(montantVerse);
        Integer montantPaid, montantRestant = 0;
        if (compare <= 0) {
            montantPaid = montantNet;
        } else {
            montantPaid = montantVerse;
            montantRestant = montantNet - montantVerse;
        }
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(dateTime);
        _new.setPkey(pkey);
        _new.setMvtDate(localDate);
        _new.setAvoidAmount(voidAmount);
        _new.setMontant(montant);
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(caisse);
        _new.setMontantCredit(0);
        _new.setMontantVerse(montantVerse);
        _new.setMontantRegle(montantPaid);
        _new.setMontantNet(montantNet);
        _new.settTypeMvtCaisse(tTypeMvtCaisse);
        _new.setReglement(reglement);
        _new.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        _new.setMarge(marge);
        _new.setReference(reference);
        _new.setMontantPaye(montantPaye);
        _new.setMontantRemise(montant - montantNet);
        _new.setCategoryTransaction(categoryTransaction);
        _new.setTypeTransaction(typeTransaction);
        _new.setChecked(checked);
        _new.setMontantTva(montantTva);
        _new.setMontantAcc(montantAcc);
        if (client != null) {
            _new.setOrganisme(client.getLgCLIENTID());
        }
        
        emg.persist(_new);
        
    }
    
    public void addTransaction(TUser ooTUser, TUser caisse,
            String pkey, Integer montant, Integer voidAmount, Integer montantNet, Integer discount,
            Integer montantVerse, Boolean checked,
            CategoryTransaction categoryTransaction,
            TypeTransaction typeTransaction, TTypeReglement reglement,
            TTypeMvtCaisse tTypeMvtCaisse, Integer montantCredit,
            EntityManager emg, Integer montantPaye, Integer montantTva, Integer marge, String reference, TClient client, boolean diff, Integer montantClient, String typeReglement, LocalDateTime dateTime, LocalDate localDate) throws Exception {
        MvtTransaction _new = new MvtTransaction();
        Integer montantPaid = 0, montantRestant = montantClient;
        if (typeReglement.equals(DateConverter.MODE_ESP) || typeReglement.equals(DateConverter.REGL_DIFF)) {
            if (montantVerse > 0 && montantClient > 0) {
                int compare = montantClient.compareTo(montantVerse);
                if (compare <= 0) {
                    montantPaid = montantClient;
                } else {
                    montantPaid = montantVerse;
                    montantRestant = montantClient - montantVerse;
                }
            }
        } else {
//            montantPaid = montantNet;
//            montantPaye = montantNet;
            montantPaid = montantPaye;
            
        }

//        LOG.log(Level.INFO, "---- montant {0} montantNet {1} discount {2} montantVerse {3} montantCredit {4} montantRestant,{5}", new Object[]{montant, montantNet, discount, montantVerse, montantCredit, montantRestant});
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(dateTime);
        _new.setPkey(pkey);
        _new.setMvtDate(localDate);
        _new.setAvoidAmount(voidAmount);
        _new.setMontant(montant);
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(caisse);
        _new.setReference(reference);
        _new.setMontantCredit(montantCredit);
        _new.setMontantVerse(montantVerse);
        _new.setMontantRegle(montantPaid);//09032020
        _new.setMontantPaye(montantPaye);
        _new.setMontantNet(montantNet);
//        _new.setMontantNet(montantNet + montantCredit);
        _new.settTypeMvtCaisse(tTypeMvtCaisse);
        _new.setReglement(reglement);
        _new.setMontantRestant(0);
        if (diff && typeReglement.equals(DateConverter.MODE_ESP)) {
            _new.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        }
        
        _new.setMontantRemise(discount);
        _new.setMontantTva(montantTva);
        _new.setMarge(marge);
        _new.setCategoryTransaction(categoryTransaction);
        _new.setTypeTransaction(typeTransaction);
        _new.setChecked(checked);
        if (client != null) {
            _new.setOrganisme(client.getLgCLIENTID());
        }
        _new.setMontantAcc(voidAmount);
        emg.persist(_new);
    }
    
    public void addTransactionDepot(TUser ooTUser, TUser caisse, String pkey,
            Integer montant, Integer voidAmount, Integer montantNet,
            TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse, EntityManager emg,
            Integer montantTva, Integer marge, String reference, TClient client) {
        MvtTransaction _new = new MvtTransaction();
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(LocalDateTime.now());
        _new.setPkey(pkey);
        _new.setMvtDate(LocalDate.now());
        _new.setAvoidAmount(voidAmount);
        _new.setMontant(montant);
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(caisse);
        _new.setMontantCredit(montantNet);
        _new.setMontantVerse(0);
        _new.setMontantRegle(0);
        _new.setMontantNet(montantNet);
        _new.settTypeMvtCaisse(tTypeMvtCaisse);
        _new.setReglement(reglement);
        _new.setMontantRestant(0);
        _new.setMarge(marge);
        _new.setReference(reference);
        _new.setMontantPaye(0);
        _new.setMontantRemise(montant - montantNet);
        _new.setCategoryTransaction(CategoryTransaction.CREDIT);
        _new.setTypeTransaction(TypeTransaction.VENTE_CREDIT);
        _new.setChecked(false);
        _new.setMontantTva(montantTva);
        if (client != null) {
            _new.setOrganisme(client.getLgCLIENTID());
        }
        emg.persist(_new);
        
    }
    
    public void addTransactionCopy(TUser ooTUser, TUser caisse,
            String pkey, MvtTransaction old, EntityManager emg, String ref, LocalDateTime localDateTime, LocalDate localDate) {
        MvtTransaction _new = new MvtTransaction();
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(localDateTime);
        _new.setPkey(pkey);
        _new.setMvtDate(localDate);
        _new.setAvoidAmount((-1) * old.getAvoidAmount());
        _new.setMontant((-1) * old.getMontant());
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(caisse);
        _new.setReference(ref);
        _new.setMontantCredit((-1) * old.getMontantCredit());
        _new.setMontantVerse((-1) * old.getMontantVerse());
        _new.setMontantRegle((-1) * old.getMontantRegle());
        _new.setMontantPaye((-1) * old.getMontantPaye());
        _new.setMontantNet((-1) * old.getMontantNet());
        _new.settTypeMvtCaisse(old.gettTypeMvtCaisse());
        _new.setReglement(old.getReglement());
        _new.setMontantRestant((-1) * old.getMontantRestant());
        _new.setMontantRemise((-1) * old.getMontantRemise());
        _new.setMontantTva((-1) * old.getMontantTva());
        _new.setMarge((-1) * old.getMarge());
        _new.setCategoryTransaction(CategoryTransaction.DEBIT);
        _new.setTypeTransaction(old.getTypeTransaction());
        _new.setOrganisme(old.getOrganisme());
        _new.setChecked(false);
        emg.persist(_new);
    }
    
    @Override
    public JSONObject annulerVente(TUser ooTUser, String lg_PREENREGISTREMENT_ID) {
        TPreenregistrement OTPreenregistrement = this.getEm().find(TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
        JSONObject json;
        if (OTPreenregistrement == null) {
            try {
                json = new JSONObject();
                json.put("success", false);
                json.put("msg", "L'opération a échoué");
                return json;
            } catch (JSONException ex) {
                return new JSONObject();
            }
        }
        try {
            return annulerVNO(ooTUser, OTPreenregistrement);
        } catch (JSONException ex) {
            return new JSONObject();
        }
        
    }
    
    @Override
    public boolean updateSnapshotVenteSociete(String lg_PREENREGISTREMENT_ID, String lg_COMPTE_CLIENT_ID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Optional<TPreenregistrementCompteClient> findOptionalCmt(TPreenregistrement preenregistrement, EntityManager emg) {
        try {
            TypedQuery<TPreenregistrementCompteClient> query = emg.createQuery("SELECT o FROM TPreenregistrementCompteClient o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID =?1 ", TPreenregistrementCompteClient.class);
            query.setMaxResults(1);
            query.setParameter(1, preenregistrement.getLgPREENREGISTREMENTID());
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Optional checkResumeCaisse(TUser ooTUser, EntityManager emg) {
        try {
            TypedQuery<TResumeCaisse> q = emg.createQuery("SELECT t FROM TResumeCaisse t WHERE t.lgUSERID.lgUSERID = ?1  AND t.strSTATUT = ?2 ", TResumeCaisse.class);
            q.setParameter(1, ooTUser.getLgUSERID())
                    .setParameter(2, DateConverter.STATUT_IS_IN_USE)
                    .setMaxResults(1);
            return Optional.ofNullable(q.getSingleResult());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean checkCaisse(TUser ooTUser) {
        try {
            TypedQuery<TResumeCaisse> q = this.getEm().createQuery("SELECT t FROM TResumeCaisse t WHERE t.lgUSERID.lgUSERID = ?1  AND t.strSTATUT = ?2 ", TResumeCaisse.class);
            q.setParameter(1, ooTUser.getLgUSERID())
                    .setParameter(2, DateConverter.STATUT_IS_IN_USE)
                    .setMaxResults(1);
            return (q.getSingleResult() != null);
        } catch (Exception e) {
            return false;
        }
    }
    
    public JSONObject buildRef__(LocalDate ODate, String KEY_PARAMETER, EntityManager emg) {
        JSONObject result = new JSONObject();
        try {
            TParameters parameters = findByKeyPara(KEY_PARAMETER, emg);
            TParameters OTParameters_KEY_SIZE_ORDER_NUMBER = findByKeyPara("KEY_SIZE_ORDER_NUMBER", emg);
            String jsondata = parameters.getStrVALUE();
            JSONArray jsonArray = new JSONArray(jsondata);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int int_last_code = new Integer(jsonObject.getString("int_last_code"));
            LocalDate dt_last_date = LocalDate.parse(jsonObject.getString("str_last_date"), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            if (!ODate.isEqual(dt_last_date)) {
                int_last_code = 0;
            }
            int intsize = ((int_last_code + 1) + "").length();
            int intsize_tobuild = Integer.valueOf(OTParameters_KEY_SIZE_ORDER_NUMBER.getStrVALUE());
            String str_last_code = "";
            for (int i = 0; i < (intsize_tobuild - intsize); i++) {
                str_last_code = str_last_code + "0";
                
            }
            str_last_code = str_last_code + (int_last_code + 1) + "";
            LocalDate now = LocalDate.now();
            String str_code = (now.getYear() - 2010) + "" + now.getMonthValue() + "" + now.getDayOfMonth() + "_" + str_last_code;
            JSONObject json = new JSONObject();
            JSONArray arrayObj = new JSONArray();
            json.put("int_last_code", str_last_code);
            json.put("str_last_date", ODate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            arrayObj.put(json);
            result.put("code", str_code);
            result.put("params", arrayObj);
            parameters.setDtUPDATED(new Date());
            parameters.setStrVALUE(arrayObj.toString());
            emg.merge(parameters);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return result;
    }
    
    private Optional<TRecettes> findRecette(String id, EntityManager emg) {
        try {
            TypedQuery<TRecettes> query = emg.createQuery("SELECT o FROM TRecettes o WHERE o.strREFFACTURE=?1", TRecettes.class);
            query.setParameter(1, id);
            query.setMaxResults(1);
            TRecettes recettes = query.getSingleResult();
            return recettes != null ? Optional.of(recettes) : Optional.empty();
        } catch (Exception e) {
//            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    private Optional<MvtTransaction> transaction(String idVente, EntityManager emg) {
        try {
            TypedQuery<MvtTransaction> q = emg.createQuery("SELECT o FROM MvtTransaction o WHERE o.pkey =?1 ", MvtTransaction.class).setParameter(1, idVente);
            MvtTransaction mt = q.getSingleResult();
            if (mt != null) {
                return Optional.ofNullable(mt);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
        
    }
    
    public void copyTransaction(TUser ooTUser, MvtTransaction cashTransaction, TPreenregistrement _newP, TPreenregistrement old, EntityManager emg) {
        
        if (cashTransaction.getMvtDate().isEqual(LocalDate.now())) {
            cashTransaction.setChecked(Boolean.FALSE);
            emg.merge(cashTransaction);
            addTransactionCopy(ooTUser, old.getLgUSERCAISSIERID(), _newP.getLgPREENREGISTREMENTID(), cashTransaction, emg, _newP.getStrREF(), LocalDateTime.now(), LocalDate.now());
        } else {
            MvtTransaction _new = new MvtTransaction();
            _new.setUuid(UUID.randomUUID().toString());
            _new.setUser(ooTUser);
            _new.setCreatedAt(LocalDateTime.now());
            _new.setPkey(_newP.getLgPREENREGISTREMENTID());
            _new.setMvtDate(LocalDate.now());
            _new.setAvoidAmount((-1) * cashTransaction.getAvoidAmount());
            _new.setMontant((-1) * cashTransaction.getMontant());
            _new.setMontantNet((-1) * cashTransaction.getMontantNet());
            _new.setMontantRegle((-1) * cashTransaction.getMontantRegle());
            _new.setMontantRestant((-1) * cashTransaction.getMontantRestant());
            _new.setMontantRemise((-1) * cashTransaction.getMontantRemise());
            _new.setMontantCredit((-1) * cashTransaction.getMontantCredit());
            _new.setMontantPaye((-1) * cashTransaction.getMontantPaye());
            _new.setCategoryTransaction(CategoryTransaction.DEBIT);
            _new.setMontantTva((-1) * cashTransaction.getMontantTva());
            _new.setMarge((-1) * cashTransaction.getMarge());
            _new.setChecked(Boolean.TRUE);
            _new.setReference(_newP.getStrREF());
            _new.setOrganisme(cashTransaction.getOrganisme());
            _new.settTypeMvtCaisse(cashTransaction.gettTypeMvtCaisse());
            _new.setReglement(cashTransaction.getReglement());
            _new.setTypeTransaction(cashTransaction.getTypeTransaction());
            _new.setCaisse(cashTransaction.getCaisse());
            _new.setMagasin(cashTransaction.getMagasin());
            emg.persist(_new);
        }
        
    }
    
    private Typemvtproduit findById(String id) {
        return getEm().find(Typemvtproduit.class, id);
    }
    
    private JSONObject annulerVNO(TUser ooTUser, TPreenregistrement tp) throws JSONException {
        EntityManager emg = this.getEm();
        JSONObject json = new JSONObject();
        final boolean checked = tp.getChecked();
        final boolean sameDate = DateConverter.convertDateToLocalDate(tp.getDtUPDATED()).isEqual(LocalDate.now());
        String ref = "";
        try {
            
            if (!checkResumeCaisse(ooTUser, emg).isPresent()) {
                json.put("success", false);
                json.put("msg", "Désolé votre caisse est fermée. Veuillez l'ouvrir avant de proceder à l'annulation");
                return json;
            }
            
            if (checkChargedPreenregistrement(tp.getLgPREENREGISTREMENTID(), emg).isPresent()) {
                json.put("success", false);
                json.put("msg", "Désolé la vente a été facturée");
                return json;
            }
            List<TCashTransaction> cashTransactions = lstTCashTransaction(tp.getLgPREENREGISTREMENTID(), emg);
            Optional<TRecettes> oprectte = findRecette(tp.getLgPREENREGISTREMENTID(), emg);
            List<TPreenregistrementDetail> preenregistrementDetails = getTPreenregistrementDetail(tp, emg);
            String idVente = tp.getLgPREENREGISTREMENTID();
            
            TPreenregistrement _new = createPreventeCopy(ooTUser, tp, emg);
            ref = _new.getLgPREENREGISTREMENTID();
            LongAdder montantRestant = new LongAdder();
            findOptionalCmt(tp, emg).ifPresent(cp -> {
                montantRestant.add(cp.getIntPRICERESTE());
                cp.setIntPRICE(0);
                cp.setIntPRICERESTE(0);
                cp.setStrSTATUT(commonparameter.statut_delete);
                cp.setDtUPDATED(new Date());
                emg.merge(cp);
            });
            if (tp.getStrTYPEVENTE().equals("VO")) {
                copyPreenregistrementTp(_new, idVente, ooTUser, emg);
                if (!cashTransactions.isEmpty()) {
                    Integer amo = 0;
                    String re = "";
                    for (TCashTransaction cashTransaction : cashTransactions) {
                        re = cashTransaction.getLgTYPEREGLEMENTID();
                        if (cashTransaction.getIntAMOUNT() > 0) {
                            amo = cashTransaction.getIntAMOUNT();
                            addTransaction(ooTUser, cashTransaction, _new, tp, !sameDate ? sameDate : checked);
                        } else {
                            addTransactionCredit(ooTUser, cashTransaction, _new, tp, !sameDate ? sameDate : checked);
                        }
                    }
                    
                    if (!sameDate) {
                        createAnnulleSnapshot(tp, montantRestant.intValue(), amo, ooTUser, getEm().find(TTypeReglement.class, re));
                    }
                }
                
            } else {
                Optional<TCashTransaction> cashTransactio = cashTransactions.stream().findFirst();
                cashTransactio.ifPresent(cs -> {
                    addTransaction(ooTUser, cs, _new, tp, !sameDate ? sameDate : checked);
                    
                    if (!sameDate) {
                        createAnnulleSnapshot(tp, montantRestant.intValue(), cs.getIntAMOUNT(), ooTUser, getEm().find(TTypeReglement.class, cs.getLgTYPEREGLEMENTID()));
                    }
                });
            }
            
            transaction(idVente, emg).ifPresent(tr -> {
                
                copyTransaction(ooTUser, tr, _new, tp, emg);
                if (!checkResumeCaisse(tp.getLgUSERCAISSIERID(), emg).isPresent()) {
                    createAnnulationRecette(tp, tr, ooTUser);
                }
            });
            
            oprectte.ifPresent(re -> {
                copyRecette(_new, re, ooTUser, emg);
            });
            
            findClientTiersPayents(tp.getLgPREENREGISTREMENTID(), emg).forEach(action -> {
                action.setStrSTATUT(commonparameter.statut_delete);
                action.setDtUPDATED(new Date());
                emg.merge(action);
            });
            TEmplacement emplacement = ooTUser.getLgEMPLACEMENTID();
            final Typemvtproduit typemvtproduit = checked ? findById(DateConverter.ANNULATION_DE_VENTE) : findById(DateConverter.TMVTP_ANNUL_VENTE_DEPOT_EXTENSION);
            preenregistrementDetails.forEach((e) -> {
                TPreenregistrementDetail _newItem = createItemCopy(ooTUser, e, _new, emg);
                TFamille OTFamille = e.getLgFAMILLEID();
                updateNbreVenteApresAnnulation(OTFamille, ooTUser, _newItem.getIntQUANTITY(), emg);
                TFamilleStock familleStock = findStock(OTFamille.getLgFAMILLEID(), emplacement, emg);
                int initStock = familleStock.getIntNUMBERAVAILABLE();
                mouvementProduitService.saveMvtProduit(_newItem.getIntPRICEUNITAIR(), _newItem.getLgPREENREGISTREMENTDETAILID(),
                        typemvtproduit, OTFamille, ooTUser, emplacement,
                        _newItem.getIntQUANTITY(), initStock, initStock - _newItem.getIntQUANTITY(), emg, _newItem.getValeurTva(), checked);
                
                updateReelStockApresAnnulation(OTFamille, familleStock, ooTUser, _newItem.getIntQUANTITY(), emg);
                if (!tp.getPkBrand().isEmpty()) {
                    updateReelStockAnnulationDepot(OTFamille, _newItem.getIntQUANTITY(), tp.getPkBrand(), emg);
                    
                }
                
            });
            String desc = "Annulation de la [ " + tp.getStrREF() + " montant  " + tp.getIntPRICE() + " ] par " + ooTUser.getStrFIRSTNAME() + " " + ooTUser.getStrLASTNAME();
            logService.updateItem(ooTUser, tp.getStrREF(), desc, TypeLog.ANNULATION_DE_VENTE, tp, emg);
            notificationService.save(new Notification()
                    .canal(Canal.SMS_EMAIL)
                    .typeNotification(TypeNotification.ANNULATION_DE_VENTE)
                    .message(desc)
                    .addUser(ooTUser));
            json.put("success", true);
            json.put("msg", "L'opération effectuée avec success");
            json.put("ref", _new.getLgPREENREGISTREMENTID());
            sendMessageClientJmsQueue(_new.getLgPREENREGISTREMENTID());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            json.put("success", false);
            json.put("msg", "Erreur annulation de la vente ");
            json.put("ref", ref);
            
        }
        return json;
        
    }
    
    private void createAnnulationRecette(TPreenregistrement tp, MvtTransaction mvtTransaction, TUser user) {
        AnnulationRecette annulationRecette = new AnnulationRecette();
        annulationRecette.setCaissier(tp.getLgUSERCAISSIERID());
        annulationRecette.setUser(user);
        annulationRecette.setMontantPaye(mvtTransaction.getMontantPaye());
        annulationRecette.setMontantRegle(mvtTransaction.getMontantRegle());
        annulationRecette.setPreenregistrement(tp);
        annulationRecette.setMontantTiersPayant(mvtTransaction.getMontantCredit());
        annulationRecette.setMontantVente(mvtTransaction.getMontantNet());
        annulationRecette.setMvtDate(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()));
        this.getEm().persist(annulationRecette);
    }
    
    public TPreenregistrementDetail createItemCopy(TUser ooTUser, TPreenregistrementDetail tp, TPreenregistrement p, EntityManager emg) {
        TPreenregistrementDetail _new = new TPreenregistrementDetail();
        _new.setLgPREENREGISTREMENTDETAILID(UUID.randomUUID().toString());
        _new.setLgPREENREGISTREMENTID(p);
        _new.setIntPRICE((-1) * tp.getIntPRICE());
        _new.setIntQUANTITY((-1) * tp.getIntQUANTITY());
        _new.setIntQUANTITYSERVED((-1) * tp.getIntQUANTITYSERVED());
        _new.setMontantTva((-1) * tp.getMontantTva());
        _new.setDtCREATED(new Date());
        _new.setStrSTATUT(commonparameter.statut_is_Closed);
        _new.setDtUPDATED(new Date());
        _new.setBoolACCOUNT(tp.getBoolACCOUNT());
        _new.setLgFAMILLEID(tp.getLgFAMILLEID());
        _new.setIntPRICEUNITAIR(tp.getIntPRICEUNITAIR());
        _new.setValeurTva(tp.getValeurTva());
        _new.setIntUG((-1) * tp.getIntUG());
        _new.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        _new.setIntPRICEDETAILOTHER(tp.getIntPRICEDETAILOTHER());
        _new.setIntFREEPACKNUMBER(tp.getIntFREEPACKNUMBER());
        _new.setIntPRICEREMISE((-1) * tp.getIntPRICEREMISE());
        _new.setIntAVOIR((-1) * tp.getIntAVOIR());
        _new.setIntAVOIRSERVED((-1) * tp.getIntQUANTITYSERVED());
        _new.setBISAVOIR(tp.getBISAVOIR());
        _new.setPrixAchat(tp.getPrixAchat());
        emg.persist(_new);
        return _new;
    }
    
    public TPreenregistrement createPreventeCopy(TUser ooTUser, TPreenregistrement tp, EntityManager emg) {
        TPreenregistrement _new = new TPreenregistrement();
        _new.setLgPREENREGISTREMENTID(UUID.randomUUID().toString());
        _new.setLgUSERID(ooTUser);
        _new.setIntPRICEREMISE((-1) * tp.getIntPRICEREMISE());
        _new.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        _new.setIntACCOUNT((-1) * tp.getIntACCOUNT());
        _new.setIntREMISEPARA((-1) * tp.getIntREMISEPARA());
        _new.setIntPRICE((-1) * tp.getIntPRICE());
        _new.setIntPRICEOTHER((-1) * tp.getIntPRICEOTHER());
        _new.setIntCUSTPART((-1) * tp.getIntCUSTPART());
        _new.setMontantTva((-1) * tp.getMontantTva());
        _new.setDtCREATED(new Date());
        _new.setDtUPDATED(new Date());
        _new.setLgPARENTID(tp.getLgPREENREGISTREMENTID());
        _new.setStrSTATUT(commonparameter.statut_is_Closed);
        _new.setLgUSERVENDEURID(tp.getLgUSERVENDEURID());
        _new.setLgUSERCAISSIERID(tp.getLgUSERCAISSIERID());
        _new.setBISAVOIR(tp.getBISAVOIR());
        _new.setBISCANCEL(tp.getBISCANCEL());
        _new.setLgNATUREVENTEID(tp.getLgNATUREVENTEID());
        _new.setLgREMISEID(tp.getLgREMISEID());
        _new.setStrREFTICKET(DateConverter.getShortId(10));
        _new.setLgTYPEVENTEID(tp.getLgTYPEVENTEID());
        _new.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        _new.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        _new.setStrFIRSTNAMECUSTOMER(tp.getStrFIRSTNAMECUSTOMER());
        _new.setStrREFBON(tp.getStrREFBON());
        _new.setStrPHONECUSTOME(tp.getStrPHONECUSTOME());
        _new.setStrLASTNAMECUSTOMER(tp.getStrLASTNAMECUSTOMER());
        _new.setStrNUMEROSECURITESOCIAL(tp.getStrNUMEROSECURITESOCIAL());
        _new.setStrINFOSCLT(tp.getStrINFOSCLT());
        _new.setIntSENDTOSUGGESTION(0);
        _new.setPkBrand(tp.getPkBrand());
        _new.setClient(tp.getClient());
        _new.setAyantDroit(tp.getAyantDroit());
        _new.setLgREGLEMENTID(tp.getLgREGLEMENTID());
        _new.setLgPREENGISTREMENTANNULEID(tp.getLgPREENREGISTREMENTID());
        _new.setMedecin(tp.getMedecin());
        _new.setStrREF(buildRef(LocalDate.now(), ooTUser.getLgEMPLACEMENTID()).getReference());
//        _new.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_VENTE, emg).getString("code"));

        tp.setBISCANCEL(true);
        tp.setDtANNULER(new Date());
        tp.setLgUSERID(ooTUser);
        _new.setChecked(Boolean.FALSE);
        tp.setChecked(Boolean.FALSE);
        LocalDate dateVente = DateConverter.convertDateToLocalDate(tp.getDtCREATED());
        if (!dateVente.isEqual(LocalDate.now())) {
            _new.setChecked(Boolean.TRUE);
        }
        emg.merge(tp);
        emg.persist(_new);
        return _new;
    }
    
    public void copyPreenregistrementTp(TPreenregistrement preenregistrement, String oldPreenregistrement, TUser o, EntityManager emg) {
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(oldPreenregistrement, emg);
        for (TPreenregistrementCompteClientTiersPayent a : clientTiersPayents) {
            TCompteClientTiersPayant OTCompteClientTiersPayant = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent _new = new TPreenregistrementCompteClientTiersPayent();
            _new.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            _new.setLgPREENREGISTREMENTID(preenregistrement);
            _new.setIntPRICE(a.getIntPRICE() * (-1));
            _new.setLgUSERID(o);
            _new.setStrSTATUT(DateConverter.STATUT_DELETE);
            _new.setDtCREATED(new Date());
            _new.setDtUPDATED(new Date());
            _new.setLgCOMPTECLIENTTIERSPAYANTID(OTCompteClientTiersPayant);
            _new.setStrREFBON(a.getStrREFBON());
            _new.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            _new.setIntPERCENT(a.getIntPERCENT());
            _new.setIntPRICERESTE(0);
            _new.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            _new.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            emg.persist(_new);
            
            TCompteClient OTCompteClient = OTCompteClientTiersPayant.getLgCOMPTECLIENTID();
            if (OTCompteClient != null && OTCompteClientTiersPayant.getDblPLAFOND() != null && OTCompteClientTiersPayant.getDblPLAFOND() != 0) {
                OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE((OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() : 0) + _new.getIntPRICE());
                OTCompteClientTiersPayant.setDtUPDATED(new Date());
                emg.merge(OTCompteClientTiersPayant);
            }
            if (OTCompteClient != null && OTCompteClient.getDblPLAFOND() != null && OTCompteClient.getDblPLAFOND() != 0) {
                OTCompteClient.setDblQUOTACONSOMENSUELLE((OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + _new.getIntPRICE());
                OTCompteClient.setDtUPDATED(new Date());
                emg.merge(OTCompteClient);
            }
        }
        
    }
    
    public List<TPreenregistrementCompteClientTiersPayent> findClientTiersPayents(String preenregistrement, EntityManager emg) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> tq = emg.createQuery("SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID =?1", TPreenregistrementCompteClientTiersPayent.class);
            tq.setParameter(1, preenregistrement);
            return tq.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    public ArrayList<TPreenregistrementDetail> items(TPreenregistrement tp, EntityManager emg) {
        ArrayList<TPreenregistrementDetail> list = new ArrayList<>();
        try {
            
            Query q = emg.
                    createQuery("SELECT t FROM TPreenregistrementDetail t WHERE  t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1").
                    setParameter(1, tp.getLgPREENREGISTREMENTID());
            
            list.addAll(q.getResultList());
            return list;
        } catch (Exception ex) {
            return list;
        }
        
    }
    
    public List<TPreenregistrementDetail> getTPreenregistrementDetail(TPreenregistrement tp, EntityManager emg) {
        
        try {
            
            return emg.
                    createQuery("SELECT t FROM TPreenregistrementDetail t WHERE  t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1").
                    setParameter(1, tp.getLgPREENREGISTREMENTID()).
                    getResultList();
            
        } catch (Exception ex) {
            return Collections.emptyList();
        }
        
    }
    
    public Optional<TFactureDetail> checkChargedPreenregistrement(String str_REF, EntityManager emg) {
        
        try {
            TFactureDetail list = (TFactureDetail) emg.createQuery("SELECT o  FROM TFactureDetail o,TPreenregistrementCompteClientTiersPayent p WHERE p.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 AND p.lgPREENREGISTREMENTCOMPTECLIENTPAYENTID=o.strREF AND   (  o.lgFACTUREID.template= FALSE OR  o.lgFACTUREID.template IS NULL) ").
                    setParameter(1, str_REF).setMaxResults(1).
                    getSingleResult();
            return Optional.ofNullable(list);
        } catch (Exception e) {
            return Optional.empty();
            
        }
        
    }
    
    public Optional<TParameters> findParamettre(String KEY_PARAMETER, EntityManager emg) {
        try {
            return Optional.ofNullable(emg.find(TParameters.class, KEY_PARAMETER));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public TParameters findByKeyPara(String KEY_PARAMETER, EntityManager emg) {
        TParameters tp = emg.find(TParameters.class, KEY_PARAMETER);
        emg.refresh(tp);
        return tp;
    }
    
    private ArrayList<TCashTransaction> lstTCashTransaction(String idVente, EntityManager emg) {
        ArrayList<TCashTransaction> list = new ArrayList<>();
        list.addAll(emg.createQuery("SELECT o FROM TCashTransaction o WHERE o.strRESSOURCEREF=?1 ").setParameter(1, idVente).getResultList());
        return list;
    }
    
    private void createAnnulleSnapshot(TPreenregistrement preenregistrement, int montantRestant, Integer montantPaye, TUser o, TTypeReglement tTypeReglement) {
        AnnulationSnapshot as = new AnnulationSnapshot();
        as.setMontant(preenregistrement.getIntPRICE());
        as.setMontantPaye(Math.abs(montantPaye));
        as.setDateOp(new Date());
        as.setReglement(tTypeReglement);
        as.setEmplacement(o.getLgEMPLACEMENTID());
        as.setPreenregistrement(preenregistrement);
        as.setRemise(preenregistrement.getIntPRICEREMISE());
        as.setUser(o);
        as.setMontantTP(0);
        as.setMontantRestant(montantRestant);
        as.setCaissier(preenregistrement.getLgUSERCAISSIERID());
        if (preenregistrement.getStrTYPEVENTE().equals("VO")) {
            as.setMontantTP(preenregistrement.getIntPRICE() - (preenregistrement.getIntCUSTPART() - preenregistrement.getIntPRICEREMISE()));
        }
        getEm().persist(as);
    }
    
    public void addTransaction(TUser ooTUser, TCashTransaction cashTransaction, TPreenregistrement _newP, TPreenregistrement old, boolean checked) {
        TCashTransaction _new = new TCashTransaction();
        _new.setId(UUID.randomUUID().toString());
        _new.setLgUSERID(ooTUser);
        _new.setDtCREATED(_newP.getDtUPDATED());
        _new.setDtUPDATED(_newP.getDtUPDATED());
        _new.setIntACCOUNT((-1) * cashTransaction.getIntACCOUNT());
        _new.setIntAMOUNT((-1) * cashTransaction.getIntAMOUNT());
        _new.setIntAMOUNT2((-1) * cashTransaction.getIntAMOUNT2());
        _new.setIntAMOUNTCREDIT(0);
        _new.setIntAMOUNTDEBIT(cashTransaction.getIntAMOUNT());
        _new.setStrREFFACTURE(_newP.getLgPREENREGISTREMENTID());
        _new.setStrDESCRIPTION("Annulation de vente ");
        _new.setStrRESSOURCEREF(_new.getStrREFFACTURE());
        _new.setStrTASK("ANNULE_VENTE");
        _new.setStrTYPEVENTE(_newP.getStrTYPEVENTE());
        _new.setStrTRANSACTIONREF("D");
        _new.setBoolCHECKED(checked);
        _new.setStrTYPE(cashTransaction.getStrTYPE());
        _new.setCaissier(old.getLgUSERCAISSIERID());
        _new.setLgTYPEREGLEMENTID(cashTransaction.getLgTYPEREGLEMENTID());
        _new.setStrNUMEROCOMPTE(cashTransaction.getStrNUMEROCOMPTE());
        _new.setLgREGLEMENTID(cashTransaction.getLgREGLEMENTID());
        _new.setStrREFCOMPTECLIENT(cashTransaction.getStrREFCOMPTECLIENT());
        _new.setStrTYPEVENTE(cashTransaction.getStrTYPEVENTE());
        _new.setIntAMOUNTRECU((-1) * cashTransaction.getIntAMOUNTRECU());
        _new.setIntAMOUNTREMIS(cashTransaction.getIntAMOUNTREMIS());
        getEm().persist(_new);
        
    }
    
    public void addTransactionCredit(TUser ooTUser, TCashTransaction cashTransaction, TPreenregistrement _newP, TPreenregistrement old, boolean checked) {
        
        Integer amount = cashTransaction.getIntAMOUNT();
        TCashTransaction _new = new TCashTransaction();
        _new.setId(UUID.randomUUID().toString());
        _new.setLgUSERID(ooTUser);
        _new.setDtCREATED(_newP.getDtUPDATED());
        _new.setDtUPDATED(_newP.getDtUPDATED());
        _new.setIntACCOUNT((-1) * cashTransaction.getIntACCOUNT());
        _new.setIntAMOUNT((-1) * cashTransaction.getIntAMOUNT());
        _new.setIntAMOUNT2((-1) * cashTransaction.getIntAMOUNT2());
        _new.setIntAMOUNTCREDIT((-1) * amount);
        _new.setIntAMOUNTDEBIT(cashTransaction.getIntAMOUNT());
        _new.setStrREFFACTURE(_newP.getLgPREENREGISTREMENTID());
        _new.setStrDESCRIPTION("Annulation de vente ");
        _new.setBoolCHECKED(checked);
        _new.setStrRESSOURCEREF(_new.getStrREFFACTURE());
        _new.setStrTASK("ANNULE_VENTE");
        _new.setStrTYPEVENTE(_newP.getStrTYPEVENTE());
        _new.setStrTRANSACTIONREF("D");
        _new.setCaissier(old.getLgUSERCAISSIERID());
        _new.setStrTYPE(cashTransaction.getStrTYPE());
        _new.setCaissier(old.getLgUSERCAISSIERID());
        _new.setLgTYPEREGLEMENTID(cashTransaction.getLgTYPEREGLEMENTID());
        _new.setStrNUMEROCOMPTE(cashTransaction.getStrNUMEROCOMPTE());
        _new.setLgREGLEMENTID(cashTransaction.getLgREGLEMENTID());
        _new.setStrREFCOMPTECLIENT(cashTransaction.getStrREFCOMPTECLIENT());
        _new.setStrTYPEVENTE(cashTransaction.getStrTYPEVENTE());
        _new.setIntAMOUNTRECU((-1) * cashTransaction.getIntAMOUNTRECU());
        _new.setIntAMOUNTREMIS(cashTransaction.getIntAMOUNTREMIS());
        getEm().persist(_new);
        
    }
    
    private void copyRecette(TPreenregistrement _newP, TRecettes old, TUser o, EntityManager emg) {
        TRecettes tr = old;
        LOG.log(Level.INFO, "tr {0} ", new Object[]{tr});
        tr.setLgUSERID(o);
        tr.setDtCREATED(_newP.getDtUPDATED());
        tr.setDtUPDATED(_newP.getDtUPDATED());
        tr.setStrCREATEDBY(o.getStrLOGIN());
        tr.setStrREFFACTURE(_newP.getLgPREENREGISTREMENTID());
        tr.setIntAMOUNT((-1) * old.getIntAMOUNT());
        tr.setIdRecette(UUID.randomUUID().toString());
        emg.detach(old);
        emg.persist(tr);
    }
    
    public void createSnapshotMouvementArticleApresAnnulation(TFamille OTFamille, int qty, TUser ooTUser, TFamilleStock familleStock, EntityManager emg) {
        
        Optional<TMouvementSnapshot> tm = findTMouvementSnapshot(OTFamille.getLgFAMILLEID(), ooTUser, emg);
        if (tm.isPresent()) {
            TMouvementSnapshot mouvementSnapshot = tm.get();
            mouvementSnapshot.setDtUPDATED(new Date());
            mouvementSnapshot.setIntNUMBERTRANSACTION(mouvementSnapshot.getIntNUMBERTRANSACTION() + 1);
            mouvementSnapshot.setIntSTOCKJOUR(familleStock.getIntNUMBERAVAILABLE() - qty);
            emg.merge(mouvementSnapshot);
            familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - qty);
            familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
            familleStock.setDtUPDATED(new Date());
            emg.merge(familleStock);
        } else {
            TMouvementSnapshot OTMouvementSnapshot = new TMouvementSnapshot();
            OTMouvementSnapshot.setLgMOUVEMENTSNAPSHOTID(UUID.randomUUID().toString());
            OTMouvementSnapshot.setLgFAMILLEID(OTFamille);
            OTMouvementSnapshot.setDtDAY(new Date());
            OTMouvementSnapshot.setDtCREATED(new Date());
            OTMouvementSnapshot.setDtUPDATED(new Date());
            OTMouvementSnapshot.setStrSTATUT(commonparameter.statut_enable);
            OTMouvementSnapshot.setIntNUMBERTRANSACTION(0);
            OTMouvementSnapshot.setIntSTOCKJOUR(familleStock.getIntNUMBERAVAILABLE() - qty);
            OTMouvementSnapshot.setIntSTOCKDEBUT(familleStock.getIntNUMBERAVAILABLE());
            OTMouvementSnapshot.setLgEMPLACEMENTID(ooTUser.getLgEMPLACEMENTID());
            
            emg.persist(OTMouvementSnapshot);
            familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - qty);
            familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
            familleStock.setDtUPDATED(new Date());
            emg.merge(familleStock);
        }
    }
    
    public void saveMvtArticleApresAnnulation(TFamille tf, TUser ooTUser, TFamilleStock familleStock, int qty, EntityManager emg) {
        Optional<TMouvement> tm = findMouvement(tf, commonparameter.ADD, commonparameter.str_ACTION_VENTE, ooTUser, emg);
        if (tm.isPresent()) {
            TMouvement OTMouvement = tm.get();
            OTMouvement.setIntNUMBERTRANSACTION(1 + OTMouvement.getIntNUMBERTRANSACTION());
            OTMouvement.setIntNUMBER(qty + OTMouvement.getIntNUMBER());
            OTMouvement.setLgUSERID(ooTUser);
            OTMouvement.setDtUPDATED(new Date());
            emg.merge(OTMouvement);
        } else {
            TMouvement OTMouvement = new TMouvement();
            OTMouvement.setLgMOUVEMENTID(UUID.randomUUID().toString());
            OTMouvement.setIntNUMBERTRANSACTION(1);
            OTMouvement.setDtDAY(new Date());
            OTMouvement.setStrSTATUT(commonparameter.statut_enable);
            OTMouvement.setIntNUMBER(qty);
            OTMouvement.setLgFAMILLEID(tf);
            OTMouvement.setLgUSERID(ooTUser);
            OTMouvement.setPKey("");
            OTMouvement.setStrACTION(commonparameter.str_ACTION_VENTE);
            OTMouvement.setStrTYPEACTION(commonparameter.ADD);
            OTMouvement.setDtCREATED(new Date());
            OTMouvement.setDtUPDATED(new Date());
            OTMouvement.setLgEMPLACEMENTID(ooTUser.getLgEMPLACEMENTID());
            emg.persist(OTMouvement);
        }
        createSnapshotMouvementArticleApresAnnulation(tf, qty, ooTUser, familleStock, emg);
    }
    
    public void updateNbreVenteApresAnnulation(TFamille OTFamille, TUser ooTUser, int qty, EntityManager emg) {
        OTFamille.setIntNOMBREVENTES((OTFamille.getIntNOMBREVENTES() != null ? OTFamille.getIntNOMBREVENTES() + qty : 0));
        OTFamille.setDtLASTMOUVEMENT(new Date());
        emg.merge(OTFamille);
    }
    
    public boolean updateReelStockApresAnnulation(TFamille OTFamille, TFamilleStock familleStock, TUser ooTUser, int int_qte, EntityManager emg) {
        try {
            
            saveMvtArticleApresAnnulation(OTFamille, ooTUser, familleStock, int_qte, emg);
            return true;
        } catch (Exception e) {
            return false;
        }
        
    }
    
    public Optional<TMouvement> findMouvement(TFamille OTFamille, String action, String typeAction, TUser ooTUser, EntityManager emg) {
        try {
            TypedQuery<TMouvement> query = emg.createQuery("SELECT t FROM TMouvement t WHERE    t.dtDAY  = ?1   AND t.lgFAMILLEID.lgFAMILLEID = ?2 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?3 AND t.strACTION = ?4 AND t.strTYPEACTION = ?5 ", TMouvement.class);
            query.setParameter(1, new Date(), TemporalType.DATE).
                    setParameter(2, OTFamille.getLgFAMILLEID()).
                    setParameter(3, ooTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID()).
                    setParameter(4, action).
                    setParameter(5, typeAction);
            
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Optional<TMouvementSnapshot> findTMouvementSnapshot(String lg_FAMILLE_ID, TUser ooTUser, EntityManager emg) {
        try {
            TypedQuery<TMouvementSnapshot> query = emg.createQuery("SELECT t FROM TMouvementSnapshot t WHERE    t.dtDAY  = ?1   AND t.lgFAMILLEID.lgFAMILLEID = ?2 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?3  ", TMouvementSnapshot.class);
            query.setParameter(1, new Date(), TemporalType.DATE).
                    setParameter(2, lg_FAMILLE_ID).
                    setParameter(3, ooTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public void updateReelStockAnnulationDepot(TFamille OTFamille, int int_qte, String empl, EntityManager emg) {
        try {
            TEmplacement emplacement = emg.find(TEmplacement.class, empl);
            TFamilleStock familleStock = findStock(OTFamille.getLgFAMILLEID(), emplacement, emg);
            familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() + int_qte);
            familleStock.setIntNUMBER(familleStock.getIntNUMBER());
            familleStock.setDtUPDATED(new Date());
            emg.merge(familleStock);
        } catch (Exception e) {
        }
        
    }
    
    public TFamilleStock findStock(String OTFamille, TEmplacement emplacement, EntityManager emg) {
        
        try {
            Query query = emg.createQuery("SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2");
            query.
                    setParameter(1, OTFamille);
            query.
                    setParameter(2, emplacement.getLgEMPLACEMENTID());
            TFamilleStock familleStock = (TFamilleStock) query.getSingleResult();
            LOG.log(Level.INFO, "familleStock {0} ", new Object[]{familleStock});
            return familleStock;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
        
    }
    
    private Optional<TAyantDroit> findAyantDroit(String id, EntityManager emg) {
        try {
            TAyantDroit OTAyantDroit = emg.find(TAyantDroit.class, id);
            
            return (OTAyantDroit != null ? Optional.of(OTAyantDroit) : Optional.empty());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private Optional<TClient> findClientById(String id) {
        if (StringUtils.isEmpty(id)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(getEm().find(TClient.class, id));
        } catch (Exception e) {
//            e.printStackTrace(System.err);
            return Optional.empty();
        }
    }
    
    @Override
    public JSONObject createPreVenteVo(SalesParams salesParams) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            if (salesParams.getTierspayants().isEmpty() && !salesParams.isDepot()) {
                json.put("success", false).put("msg", "Veuillez ajouter au moins un tiers-payant à la vente");
                return json;
            }
            
            TTypeVente OTTypeVente = typeVenteFromId(salesParams.getTypeVenteId(), emg);
            TNatureVente oTNatureVente = natureVenteFromId(salesParams.getNatureVenteId(), emg);
            TRemise OTRemise = remiseFromId(salesParams.getRemiseId(), emg);
            TUser vendeur = userFromId(salesParams.getUserVendeurId(), emg);
            TFamille tf = emg.find(TFamille.class, salesParams.getProduitId());
//            emg.getTransaction().begin();
            TPreenregistrement OTPreenregistrement = new TPreenregistrement(UUID.randomUUID().toString());
            OTPreenregistrement.setLgUSERVENDEURID(vendeur != null ? vendeur : salesParams.getUserId());
            OTPreenregistrement.setLgUSERCAISSIERID(salesParams.getUserId());
            OTPreenregistrement.setLgUSERID(salesParams.getUserId());
            Medecin medecin = findMedecin(salesParams.getMedecinId());
            OTPreenregistrement.setMedecin(medecin);
            OTPreenregistrement.setIntREMISEPARA(0);
            OTPreenregistrement.setLgREMISEID(OTRemise != null ? OTRemise.getLgREMISEID() : "");
            OTPreenregistrement.setRemise(OTRemise);
            OTPreenregistrement.setStrFIRSTNAMECUSTOMER("");
            OTPreenregistrement.setStrLASTNAMECUSTOMER("");
            OTPreenregistrement.setStrPHONECUSTOME("");
            OTPreenregistrement.setStrINFOSCLT("");
            findClientById(salesParams.getClientId()).ifPresent(c -> {
                OTPreenregistrement.setStrFIRSTNAMECUSTOMER(c.getStrFIRSTNAME());
                OTPreenregistrement.setStrLASTNAMECUSTOMER(c.getStrLASTNAME());
                OTPreenregistrement.setStrPHONECUSTOME(c.getStrADRESSE());
                OTPreenregistrement.setClient(c);
            });
            OTPreenregistrement.setDtCREATED(new Date());
            OTPreenregistrement.setDtUPDATED(new Date());
            OTPreenregistrement.setLgNATUREVENTEID(oTNatureVente);
            OTPreenregistrement.setLgTYPEVENTEID(OTTypeVente);
            OTPreenregistrement.setIntPRICE(0);
            OTPreenregistrement.setIntACCOUNT(0);
            OTPreenregistrement.setIntPRICEOTHER(0);
            OTPreenregistrement.setBISCANCEL(false);
            OTPreenregistrement.setBWITHOUTBON(false);
            OTPreenregistrement.setIntCUSTPART(0);
            OTPreenregistrement.setMontantTva(0);
            OTPreenregistrement.setIntPRICEREMISE(0);
            OTPreenregistrement.setCopy(Boolean.FALSE);
            OTPreenregistrement.setIntSENDTOSUGGESTION(0);
//            OTPreenregistrement.setStrSTATUTVENTE(commonparameter.statut_differe);
            OTPreenregistrement.setStrSTATUT(salesParams.getStatut());
            TPreenregistrementDetail dp = addPreenregistrementItem(OTPreenregistrement, tf, salesParams.getQte(), salesParams.getQteServie(), salesParams.getQteUg(), salesParams.getItemPu(), emg);
            if (!salesParams.isDepot()) {
                OTPreenregistrement.setStrTYPEVENTE(Parameter.KEY_VENTE_ORDONNANCE);
                if (!salesParams.getTypeVenteId().equals(Parameter.VENTE_AVEC_CARNET)) {
                    findAyantDroit(salesParams.getAyantDroitId(), emg).ifPresent(a -> {
                        OTPreenregistrement.setStrFIRSTNAMECUSTOMER(a.getStrFIRSTNAME());
                        OTPreenregistrement.setStrLASTNAMECUSTOMER(a.getStrLASTNAME());
                        OTPreenregistrement.setAyantDroit(a);
                    });
                    
                }
                OTPreenregistrement.setPkBrand("");
                if (!salesParams.isDevis()) {
                    OTPreenregistrement.setStrREF(buildRefTmp(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID()).getReferenceTemp());
//                    OTPreenregistrement.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_PREVENTE).getString("code"));
                    emg.persist(OTPreenregistrement);
                    createPreenregistrementTierspayant(salesParams.getTierspayants(), OTPreenregistrement, emg);
                    
                } else {
                    OTPreenregistrement.setStrREF(buildRefDevis(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID()).getReference());
//                    OTPreenregistrement.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_DEVIS, emg).getString("code"));
                    OTPreenregistrement.setStrREFBON(salesParams.getBonRef());
                    OTPreenregistrement.setStrREFTICKET(DateConverter.getShortId(10));
                    emg.persist(OTPreenregistrement);
                    List<TCompteClientTiersPayant> clientTiersPayants = findCompteClientTierspayantByClientId(salesParams.getClientId(), emg);
                    for (TCompteClientTiersPayant fda : clientTiersPayants) {
                        createTPreenregistrementCompteClientTiersPayent(OTPreenregistrement, fda, 100, salesParams.getBonRef(), emg);
                    }
                    
                }
            } else {
                TEmplacement emplacement = emg.find(TEmplacement.class, salesParams.getEmplacementId());
                OTPreenregistrement.setPkBrand(emplacement.getLgEMPLACEMENTID());
                OTPreenregistrement.setLgREMISEID(salesParams.getRemiseDepot() + "");
                OTPreenregistrement.setIntPRICEREMISE(calculRemiseDepot(OTPreenregistrement.getIntPRICE(), salesParams.getRemiseDepot()));
                OTPreenregistrement.setStrTYPEVENTE((salesParams.getTypeDepoId().equals("1") ? Parameter.KEY_VENTE_NON_ORDONNANCEE : Parameter.KEY_VENTE_ORDONNANCE));
//                OTPreenregistrement.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_PREVENTE, emg).getString("code"));
                OTPreenregistrement.setStrREF(buildRefTmp(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID()).getReferenceTemp());
                emg.persist(OTPreenregistrement);
            }
            
            emg.persist(dp);
            
            JSONObject data = new JSONObject();
            data.put("lgPREENREGISTREMENTID", OTPreenregistrement.getLgPREENREGISTREMENTID());
            data.put("strREF", OTPreenregistrement.getStrREF());
            data.put("intPRICE", OTPreenregistrement.getIntPRICE());
            data.put("intPRICEREMISE", OTPreenregistrement.getIntPRICEREMISE());
            json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
            afficheurProduit(dp.getLgFAMILLEID().getStrNAME(), dp.getIntQUANTITY(), dp.getIntPRICEUNITAIR(), dp.getIntPRICE());
            return json;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            LOG.log(Level.SEVERE, "createPreVenteVo", e);
            try {
                json.put("success", false).put("msg", "Erreur :: l'opération a échouée");
            } catch (JSONException ex) {
                
            }
        }
        return json;
    }
    
    @Override
    public JSONObject createPreVente(SalesParams salesParams) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            
            TFamille tf = emg.find(TFamille.class, salesParams.getProduitId());
            TTypeVente OTTypeVente = typeVenteFromId(salesParams.getTypeVenteId(), emg);
            TNatureVente oTNatureVente = natureVenteFromId(salesParams.getNatureVenteId(), emg);
            TRemise OTRemise = remiseFromId(salesParams.getRemiseId(), emg);
            TUser vendeur = userFromId(salesParams.getUserVendeurId(), emg);
            TPreenregistrement OTPreenregistrement = new TPreenregistrement(UUID.randomUUID().toString());
            OTPreenregistrement.setLgUSERVENDEURID(vendeur != null ? vendeur : salesParams.getUserId());
            OTPreenregistrement.setLgUSERCAISSIERID(salesParams.getUserId());
            OTPreenregistrement.setLgUSERID(salesParams.getUserId());
            OTPreenregistrement.setIntREMISEPARA(0);
            OTPreenregistrement.setPkBrand("");
            Medecin medecin = findMedecin(salesParams.getMedecinId());
            OTPreenregistrement.setMedecin(medecin);
            if (!salesParams.isDevis()) {
                OTPreenregistrement.setStrREF(buildRefTmp(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID()).getReferenceTemp());
//                OTPreenregistrement.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_PREVENTE, emg).getString("code"));
            } else {
                findClientById(salesParams.getClientId()).ifPresent(my -> {
                    OTPreenregistrement.setClient(my);
                });
//                OTPreenregistrement.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_DEVIS, emg).getString("code"));
                OTPreenregistrement.setStrREF(buildRefDevis(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID()).getReferenceTemp());
                OTPreenregistrement.setStrREFTICKET(DateConverter.getShortId(10));
            }
            OTPreenregistrement.setLgREMISEID(OTRemise != null ? OTRemise.getLgREMISEID() : "");
            OTPreenregistrement.setRemise(OTRemise);
            OTPreenregistrement.setStrFIRSTNAMECUSTOMER("");
            OTPreenregistrement.setStrLASTNAMECUSTOMER("");
            OTPreenregistrement.setStrPHONECUSTOME("");
            OTPreenregistrement.setStrINFOSCLT("");
            OTPreenregistrement.setDtCREATED(new Date());
            OTPreenregistrement.setDtUPDATED(new Date());
            OTPreenregistrement.setLgNATUREVENTEID(oTNatureVente);
            OTPreenregistrement.setLgTYPEVENTEID(OTTypeVente);
            OTPreenregistrement.setIntPRICE(0);
            OTPreenregistrement.setIntACCOUNT(0);
            OTPreenregistrement.setIntPRICEOTHER(0);
            OTPreenregistrement.setBISCANCEL(false);
            OTPreenregistrement.setBWITHOUTBON(false);
            OTPreenregistrement.setIntCUSTPART(0);
            OTPreenregistrement.setIntPRICEREMISE(0);
            OTPreenregistrement.setIntSENDTOSUGGESTION(0);
            OTPreenregistrement.setMontantTva(0);
            OTPreenregistrement.setCopy(Boolean.FALSE);
            OTPreenregistrement.setStrSTATUTVENTE(commonparameter.statut_nondiffere);
            OTPreenregistrement.setStrSTATUT(salesParams.getStatut());
            OTPreenregistrement.setStrTYPEVENTE(Parameter.KEY_VENTE_NON_ORDONNANCEE);
            TPreenregistrementDetail dt = addPreenregistrementItem(OTPreenregistrement, tf, salesParams.getQte(), salesParams.getQteServie(), salesParams.getQteUg(), salesParams.getItemPu(), emg);
            emg.persist(OTPreenregistrement);
            emg.persist(dt);
            JSONObject data = new JSONObject();
            data.put("lgPREENREGISTREMENTID", OTPreenregistrement.getLgPREENREGISTREMENTID());
            data.put("strREF", OTPreenregistrement.getStrREF());
            data.put("intPRICE", OTPreenregistrement.getIntPRICE());
            json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
            afficheurProduit(dt.getLgFAMILLEID().getStrNAME(), dt.getIntQUANTITY(), dt.getIntPRICEUNITAIR(), dt.getIntPRICE());
            
            return json;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            LOG.log(Level.SEVERE, "createPreVente", e);
            try {
                json.put("success", false).put("msg", "Erreur :: l'opération a échouée");
            } catch (JSONException ex) {
                
            }
        }
        return json;
    }
    
    private TUser userFromId(String id, EntityManager emg) {
        if (id == null || "".equals(id)) {
            return null;
        }
        return emg.find(TUser.class, id);
    }
    
    private TRemise remiseFromId(String id, EntityManager emg) {
        if (id == null || "".equals(id)) {
            return null;
        }
        return emg.find(TRemise.class, id);
//        return new TRemise(id);
    }
    
    private TTypeVente typeVenteFromId(String id, EntityManager emg) {
        if (id == null || "".equals(id)) {
            return null;
        }
        return emg.find(TTypeVente.class, id);
//        return new TTypeVente(id);
    }
    
    private TNatureVente natureVenteFromId(String id, EntityManager emg) {
        if (id == null || "".equals(id)) {
            return null;
        }
        return emg.find(TNatureVente.class, id);
//        return new TNatureVente(id);
    }
    
    public TPreenregistrementDetail addPreenregistrementItem(TPreenregistrement tp, TFamille OTFamille, int qte, int qteServie, int qteUg, Integer pu, EntityManager emg) {
        try {
            TCodeTva tva = OTFamille.getLgCODETVAID();
            Optional<TParameters> KEY_TAKE_INTO_ACCOUNT = findParamettre("KEY_TAKE_INTO_ACCOUNT", emg);
            TPreenregistrementDetail tpd = new TPreenregistrementDetail(UUID.randomUUID().toString());
            tpd.setBoolACCOUNT(false);
            tpd.setLgFAMILLEID(OTFamille);
            tpd.setDtCREATED(new Date());
            tpd.setDtUPDATED(new Date());
            tpd.setIntPRICEUNITAIR(pu);
            tpd.setIntPRICE(pu * qte);
            tpd.setMontantTva(calculeTva(tva, pu * qte));
            tpd.setValeurTva(tva.getIntVALUE());
            tpd.setIntUG(qteUg);
            tpd.setIntQUANTITY(qte);
            tpd.setIntQUANTITYSERVED(qteServie);
            tpd.setIntPRICEOTHER(0);
            tpd.setIntPRICEDETAILOTHER(0);
            tpd.setIntFREEPACKNUMBER(0);
            tpd.setIntPRICEREMISE(0);
            tpd.setIntAVOIR(tpd.getIntQUANTITY() - tpd.getIntQUANTITYSERVED());
            tpd.setIntAVOIRSERVED(tpd.getIntQUANTITYSERVED());
            tpd.setStrSTATUT(commonparameter.statut_is_Process);
            tpd.setBISAVOIR(tpd.getIntAVOIR() > 0);
            tpd.setPrixAchat(OTFamille.getIntPAF());
            tpd.setLgPREENREGISTREMENTID(tp);
            tp.setIntPRICE(tp.getIntPRICE() + tpd.getIntPRICE());
            tp.setMontantTva(tpd.getMontantTva() + tp.getMontantTva());
            tp.setIntPRICEOTHER(tp.getIntPRICEOTHER() + tpd.getIntPRICE());
            if (KEY_TAKE_INTO_ACCOUNT.isPresent()) {
                if (Integer.valueOf(KEY_TAKE_INTO_ACCOUNT.get().getStrVALUE().trim()) == 1) {
                    if ((!OTFamille.getLgZONEGEOID().getBoolACCOUNT() || !OTFamille.getBoolACCOUNT())) {
                        tpd.setBoolACCOUNT(false);
                    }
                } else {
                    tp.setIntACCOUNT(tp.getIntACCOUNT() + tpd.getIntPRICE());
                }
                
            } else {
                tp.setIntACCOUNT(tp.getIntACCOUNT() + tpd.getIntPRICE());
            }
            return tpd;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }
    
    public Optional<TPreenregistrementDetail> findItemByProduitAndVente(String lg_PREENREGISTREMENT_ID, String lg_famille_id, EntityManager emg) {
        try {
            TypedQuery<TPreenregistrementDetail> detail = emg.createQuery("SELECT o FROM TPreenregistrementDetail o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?2 ", TPreenregistrementDetail.class);
            detail.setParameter(1, lg_famille_id);
            detail.setParameter(2, lg_PREENREGISTREMENT_ID);
            detail.setMaxResults(1);
            return Optional.ofNullable(detail.getSingleResult());
        } catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
            return Optional.empty();
        }
    }
    
    private Integer calculeTva(TCodeTva codeTva, Integer amount) {
        if (codeTva == null || codeTva.getIntVALUE() == 0) {
            return 0;
        }
        Double HT = amount / (1 + (Double.valueOf(codeTva.getIntVALUE()) / 100));
        return amount - HT.intValue();
    }
    
    @Override
    public JSONObject addPreenregistrementItem(SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement tp = emg.find(TPreenregistrement.class, params.getVenteId());
            Optional<TPreenregistrementDetail> detailOp = findItemByProduitAndVente(params.getVenteId(), params.getProduitId(), emg);
            TFamille famille = emg.find(TFamille.class, params.getProduitId());
            TCodeTva tva = famille.getLgCODETVAID();
            TPreenregistrementDetail tpd;
            if (detailOp.isPresent()) {
                tpd = detailOp.get();
                Integer int_PRICE_OLD = tpd.getIntPRICE();
                Integer montantTva = tpd.getMontantTva();
                tpd.setIntFREEPACKNUMBER(0);
                tpd.setIntQUANTITY(tpd.getIntQUANTITY() + params.getQte());
                tpd.setIntPRICE(tpd.getIntPRICEUNITAIR() * tpd.getIntQUANTITY());
                tpd.setMontantTva(calculeTva(tva, tpd.getIntPRICE()));
                tpd.setIntQUANTITYSERVED(tpd.getIntQUANTITYSERVED() + params.getQteServie());
                tpd.setIntAVOIRSERVED(tpd.getIntQUANTITYSERVED());
                tpd.setIntAVOIR(tpd.getIntQUANTITY() - tpd.getIntQUANTITYSERVED());
                tpd.setDtUPDATED(new Date());
                tpd.setBISAVOIR(tpd.getIntAVOIR() > 0);
                
                tp.setIntPRICE(tp.getIntPRICE() + tpd.getIntPRICE() - int_PRICE_OLD);
                tp.setMontantTva(tp.getMontantTva() + tpd.getMontantTva() - montantTva);
                if (tpd.getBoolACCOUNT()) {
                    tp.setIntACCOUNT(tp.getIntPRICE());
                }
                emg.merge(tpd);
                
                afficheurProduit(tpd.getLgFAMILLEID().getStrNAME(), tpd.getIntQUANTITY(), tpd.getIntPRICEUNITAIR(), tpd.getIntPRICE());
            } else {
                
                TPreenregistrementDetail dp = addPreenregistrementItem(tp, famille, params.getQte(), params.getQteServie(), params.getQteUg(), params.getItemPu(), emg);
                emg.persist(dp);
                afficheurProduit(dp.getLgFAMILLEID().getStrNAME(), dp.getIntQUANTITY(), dp.getIntPRICEUNITAIR(), dp.getIntPRICE());
            }
            tp = emg.merge(tp);
            
            JSONObject data = new JSONObject();
            data.put("lgPREENREGISTREMENTID", tp.getLgPREENREGISTREMENTID());
            data.put("strREF", tp.getStrREF());
            data.put("intPRICE", tp.getIntPRICE());
            data.put("intPRICEREMISE", tp.getIntPRICEREMISE());
            json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
            
            return json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            
            try {
                json.put("success", false).put("msg", "Erreur :: l'opération a échouée");
            } catch (JSONException ex) {
                
            }
            return json;
        }
    }
    
    @Override
    public JSONObject updateTPreenregistrementDetail(SalesParams params
    ) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrementDetail detail = emg.find(TPreenregistrementDetail.class, params.getItemId());
            Integer int_QUANTITY_SERVED_OLD = detail.getIntQUANTITYSERVED(), oldPrice = detail.getIntPRICE(), montantTva = detail.getMontantTva();
            
            TFamille famille = detail.getLgFAMILLEID();
            
            TPreenregistrement tp = detail.getLgPREENREGISTREMENTID();
            if (detail.getIntPRICEUNITAIR().compareTo(params.getItemPu()) != 0) {
                Optional<TParameters> p = findParamettre("KEY_CHECK_PRICE_UPDATE_AUTH", emg);
                if (p.isPresent()) {
                    TParameters v = p.get();
                    int checkPersmission = Integer.valueOf(v.getStrVALUE());
                    if (checkPersmission == 1) {
                        if (!checkpricevente(famille, params.getItemPu(), emg)) {
//                            emg.getTransaction().rollback();
                            return json.put("success", false).put("decondition", false).put("msg", "Impossible. Vous n'ête pas autorisé à modifier du prix de vente");
                        }
                    }
                    
                }

                //send sms
                detail.setIntPRICEUNITAIR(params.getItemPu());
            }
            detail.setIntQUANTITY(params.getQte());
            detail.setIntPRICE(detail.getIntPRICEUNITAIR() * params.getQte());
            detail.setMontantTva(calculeTva(famille.getLgCODETVAID(), detail.getIntPRICE()));
            detail.setIntQUANTITYSERVED(params.getQteServie());
            int int_AVOIR_SERVED = (params.getQteServie() - int_QUANTITY_SERVED_OLD) + (detail.getIntAVOIRSERVED() != null ? detail.getIntAVOIRSERVED() : 0);
            if (params.getQteServie() != int_QUANTITY_SERVED_OLD) {
                detail.setIntAVOIRSERVED(int_AVOIR_SERVED < 0 ? int_QUANTITY_SERVED_OLD : int_AVOIR_SERVED);
            }
            
            detail.setIntAVOIR(detail.getIntQUANTITY() - detail.getIntQUANTITYSERVED());
            detail.setBISAVOIR(detail.getIntAVOIR() > 0);
            detail.setDtUPDATED(new Date());
            tp.setIntPRICE(tp.getIntPRICE() + (detail.getIntPRICE() - oldPrice));
            tp.setMontantTva(tp.getMontantTva() + (detail.getMontantTva() - montantTva));
            
            if (params.isDepot()) {
                tp.setIntPRICEREMISE(calculRemiseDepot(tp.getIntPRICE(), params.getRemiseDepot()));
                
            }
            tp.setIntACCOUNT(tp.getIntACCOUNT() + (detail.getIntPRICE() - oldPrice));
            tp.setDtUPDATED(new Date());
            emg.merge(tp);
            emg.merge(detail);
            JSONObject data = new JSONObject();
            data.put("lgPREENREGISTREMENTID", tp.getLgPREENREGISTREMENTID());
            data.put("strREF", tp.getStrREF());
            data.put("intPRICE", tp.getIntPRICE());
            data.put("intPRICEREMISE", tp.getIntPRICEREMISE());
            
            afficheurProduit(detail.getLgFAMILLEID().getStrNAME(), detail.getIntQUANTITY(), detail.getIntPRICEUNITAIR(), detail.getIntPRICE());
            return json.put("success", true).put("msg", "Opération effectuée avec success")
                    .put("data", data)/*.put("nets", shownetpayVno(tp))*/;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "updateTPreenregistrementDetail ------>>>", e);
            try {
                json.put("success", false).put("decondition", false).put("msg", "Erreur :: l'opération a échouée");
            } catch (JSONException ex) {
                
            }
        }
        return json;
        
    }
    
    public void updateLogFile(TUser user, String ref,
            String desc, TypeLog typeLog,
            Object T, EntityManager emg
    ) {
        TEventLog eventLog = new TEventLog(UUID.randomUUID().toString());
        eventLog.setLgUSERID(user);
        eventLog.setDtCREATED(new Date());
        eventLog.setDtUPDATED(new Date());
        eventLog.setStrCREATEDBY(user.getStrLOGIN());
        eventLog.setStrSTATUT(commonparameter.statut_enable);
        eventLog.setStrTABLECONCERN(T.getClass().getName());
        eventLog.setTypeLog(typeLog);
        eventLog.setStrTYPELOG(ref);
        eventLog.setStrDESCRIPTION(desc + " référence [" + ref + " ]");
        emg.persist(eventLog);
        
    }
    
    @Override
    public TPreenregistrement removePreenregistrementDetail(String itemId
    ) {
        EntityManager emg = this.getEm();
        try {
//            emg.getTransaction().begin();
            TPreenregistrementDetail tpd = emg.find(TPreenregistrementDetail.class, itemId);
            TPreenregistrement tp = tpd.getLgPREENREGISTREMENTID();
            tp.setIntPRICE(tp.getIntPRICE() - tpd.getIntPRICE());
            tp.setMontantTva(tp.getMontantTva() - tpd.getMontantTva());
            emg.merge(tp);
            emg.remove(tpd);
//            emg.getTransaction().commit();
            return tp;
        } catch (Exception e) {
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            return null;
            
        }
    }
    
    public boolean checkIsVentePossible(TFamilleStock OTFamilleStock, int qte
    ) {
        return OTFamilleStock.getIntNUMBERAVAILABLE() >= qte;
    }
    
    private boolean boonDejaUtilise(String refBon, String cmpt) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q = getEm().createQuery("SELECT o FROM  TPreenregistrementCompteClientTiersPayent o WHERE o.strREFBON=?1 AND o.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID=?2 AND o.strSTATUT =?3 AND o.lgPREENREGISTREMENTID.strSTATUT=?4", TPreenregistrementCompteClientTiersPayent.class);
            q.setParameter(1, refBon);
            q.setParameter(2, cmpt);
            q.setParameter(3, DateConverter.STATUT_IS_CLOSED);
            q.setParameter(4, DateConverter.STATUT_IS_CLOSED);
            q.setMaxResults(1);
            return q.getSingleResult() != null;
        } catch (Exception e) {
            return false;
        }
        
    }
    
    public void createTPreenregistrementCompteClientTiersPayent(TPreenregistrement OTPreenregistrement, TCompteClientTiersPayant OTCompteClientTiersPayant, int taux, String str_REFBON, EntityManager emg) throws Exception {
        Date today = new Date();
        TPreenregistrementCompteClientTiersPayent OTPreenregistrementCompteClientTiersPayent = new TPreenregistrementCompteClientTiersPayent(UUID.randomUUID().toString());
        OTPreenregistrementCompteClientTiersPayent.setLgPREENREGISTREMENTID(OTPreenregistrement);
        OTPreenregistrementCompteClientTiersPayent.setLgCOMPTECLIENTTIERSPAYANTID(OTCompteClientTiersPayant);
        OTPreenregistrementCompteClientTiersPayent.setDtCREATED(today);
        OTPreenregistrementCompteClientTiersPayent.setDtUPDATED(today);
        OTPreenregistrementCompteClientTiersPayent.setIntPERCENT(taux);
        OTPreenregistrementCompteClientTiersPayent.setIntPRICE(0);
        OTPreenregistrementCompteClientTiersPayent.setIntPRICERESTE(0);
        OTPreenregistrementCompteClientTiersPayent.setStrREFBON(str_REFBON);
        OTPreenregistrementCompteClientTiersPayent.setStrSTATUTFACTURE("unpaid");
        OTPreenregistrementCompteClientTiersPayent.setDblQUOTACONSOVENTE(OTCompteClientTiersPayant.getDblQUOTACONSOVENTE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOVENTE() : 0);
        OTPreenregistrementCompteClientTiersPayent.setStrSTATUT(OTPreenregistrement.getStrSTATUT());
        emg.persist(OTPreenregistrementCompteClientTiersPayent);
        OTPreenregistrement.setStrINFOSCLT(OTCompteClientTiersPayant.getLgCOMPTECLIENTID().getLgCOMPTECLIENTID());
        if (OTCompteClientTiersPayant.getIntPRIORITY() == 1) {
            OTPreenregistrement.setStrREFBON(str_REFBON);
            
        }
        
    }
    
    public void createPreenregistrementTierspayant(List<TiersPayantParams> tierspayants, TPreenregistrement OTPreenregistrement, EntityManager emg
    ) throws Exception {
        for (TiersPayantParams v : tierspayants) {
            TCompteClientTiersPayant OTCompteClientTiersPayant = emg.find(TCompteClientTiersPayant.class, v.getCompteTp());
            createTPreenregistrementCompteClientTiersPayent(OTPreenregistrement, OTCompteClientTiersPayant, v.getTaux(), v.getNumBon(), emg);
        }
        
    }
    
    public boolean checkpricevente(TFamille OTFamille, int newPu, EntityManager emg
    ) {
        try {
            TParameters OTParameters = emg.find(TParameters.class, commonparameter.KEY_MAX_PRICE_POURCENT_VENTE);
            int int_PRICE_COEF = (OTFamille.getIntPRICE() * Integer.parseInt(OTParameters.getStrVALUE())) / 100;
            if ((!((OTFamille.getIntPRICE() - int_PRICE_COEF) <= newPu)) || (!(newPu <= (OTFamille.getIntPRICE() + int_PRICE_COEF)))) {
                return false;
                
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    @Override
    public JSONObject transformerVente(SalesParams salesParams) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement tp = emg.find(TPreenregistrement.class, salesParams.getVenteId());
//            emg.getTransaction().begin();
            if (salesParams.getTypeVenteId().equals(Parameter.VENTE_COMPTANT) && !tp.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_COMPTANT)) {
                List<TPreenregistrementCompteClientTiersPayent> list = findClientTiersPayents(tp.getLgPREENREGISTREMENTID(), emg);
                list.forEach((op) -> {
                    emg.remove(op);
                });
            }
            tp.setLgTYPEVENTEID(typeVenteFromId(salesParams.getTypeVenteId(), emg));
            tp.setStrTYPEVENTE(salesParams.getTypeVenteId().equals(Parameter.VENTE_COMPTANT) ? Parameter.KEY_VENTE_NON_ORDONNANCEE : Parameter.KEY_VENTE_ORDONNANCE);
            tp.setDtUPDATED(new Date());
            emg.merge(tp);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            
            try {
                json.put("success", false).put("msg", "Erreur :: l'opération a échouée");
            } catch (JSONException ex) {
                Logger.getLogger(SalesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }
    
    @Override
    public JSONObject updateayantdroit(SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement tp = emg.find(TPreenregistrement.class, params.getVenteId());
            TAyantDroit OTAyantDroit = emg.find(TAyantDroit.class, params.getAyantDroitId());
            
            if (OTAyantDroit != null && tp != null) {
//                emg.getTransaction().begin();
                tp.setStrFIRSTNAMECUSTOMER(OTAyantDroit.getStrFIRSTNAME());
                tp.setStrLASTNAMECUSTOMER(OTAyantDroit.getStrLASTNAME());
                tp.setDtUPDATED(new Date());
                tp.setAyantDroit(OTAyantDroit);
                emg.merge(tp);
//                emg.getTransaction().commit();
            }
            
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            
            try {
                json.put("success", false).put("msg", "Erreur :: l'opération a échouée");
            } catch (JSONException ex) {
                Logger.getLogger(SalesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }
    
    private MontantAPaye sumVenteSansRemise(ArrayList<TPreenregistrementDetail> list) {
        LongAdder montant = new LongAdder();
        LongAdder montantMarge = new LongAdder();
        LongAdder montantTva = new LongAdder();
        LongAdder montantAccount = new LongAdder();
        list.stream().forEach(x -> {
            montant.add(x.getIntPRICE());
            if (x.getLgFAMILLEID().getBoolACCOUNT()) {
                montantAccount.add(x.getIntPRICE());
                montantTva.add(x.getMontantTva());
                montantMarge.add((x.getIntPRICE() - x.getMontantTva()) - (x.getIntQUANTITY() * x.getLgFAMILLEID().getIntPAF()));
            }
            
        });
        Integer amount = montant.intValue();
        return new MontantAPaye(amount,
                amount,
                0, 0,
                montantMarge.intValue(), montantTva.intValue()).montantAccount(montantAccount.intValue());
    }
    
    private boolean checkAvoir(ArrayList<TPreenregistrementDetail> list) {
        java.util.function.Predicate<TPreenregistrementDetail> p = e -> e.getBISAVOIR();
        return list.stream().anyMatch(p);
    }
    
    private boolean checkOrdonnancier(ArrayList<TPreenregistrementDetail> list) {
        java.util.function.Predicate<TPreenregistrementDetail> p = e -> (e.getLgFAMILLEID().isScheduled() && !e.getLgFAMILLEID().getIntT().trim().isEmpty());
        return list.stream().anyMatch(p);
    }
    
    private TTypeReglement findById(String id, EntityManager emg) {
        if (id != null && !"".equals(id)) {
            if ("Especes".equals(id) || "4".equals(id)) {
                return emg.find(TTypeReglement.class, "1");
            } else {
                return emg.find(TTypeReglement.class, id);
            }
        }
        return emg.find(TTypeReglement.class, "1");
    }
    
    private TTypeRecette findTTypeRecetteById(EntityManager emg) {
        return emg.find(TTypeRecette.class, "1");
    }
    
    private TMotifReglement findMotifReglement(EntityManager emg) {
        return emg.find(TMotifReglement.class, DateConverter.MOTIF_VENTE);
    }
    
    private String statutDiff(String v) {
        if (!v.equals("4")) {
            return commonparameter.statut_nondiffere;
        }
        return commonparameter.statut_differe;
    }
    
    private TModeReglement findByIdMod(String id, EntityManager emg) {
        return emg.find(TModeReglement.class, id);
    }
    
    private TModeReglement findModeReglement(String idTypeRegl, EntityManager emg) {
        TModeReglement modeReglement;
        switch (idTypeRegl) {
            case "1":
            case "4":
                modeReglement = findByIdMod("1", emg);
                break;
            case "2":
                modeReglement = findByIdMod("2", emg);
                break;
            case "3":
                modeReglement = findByIdMod("5", emg);
                break;
            case "6":
                modeReglement = findByIdMod("7", emg);
                break;
            case "5":
                modeReglement = findByIdMod("6", emg);
                break;
            case "7":
                modeReglement = findByIdMod(DateConverter.MODE_ORANGE, emg);
                break;
            default:
                modeReglement = findByIdMod(idTypeRegl, emg);
                break;
            
        }
        return modeReglement;
    }
    
    public TReglement createTReglement(TUser user, TModeReglement modeReglement, String str_REF_COMPTE_CLIENT, String str_REF_RESSOURCE, String str_BANQUE, String str_LIEU, String str_COMMENTAIRE, String statut, String str_FIRST_LAST_NAME, EntityManager emg) {
        TReglement OTReglement = new TReglement();
        OTReglement.setLgREGLEMENTID(UUID.randomUUID().toString());
        OTReglement.setStrBANQUE(str_BANQUE);
        OTReglement.setStrCODEMONNAIE("Fr");
        OTReglement.setStrCOMMENTAIRE(str_COMMENTAIRE);
        OTReglement.setStrLIEU(str_LIEU);
        OTReglement.setStrFIRSTLASTNAME(str_FIRST_LAST_NAME);
        OTReglement.setStrREFRESSOURCE(str_REF_RESSOURCE);
        OTReglement.setIntTAUX(0);
        OTReglement.setDtCREATED(new Date());
        OTReglement.setDtUPDATED(new Date());
        OTReglement.setLgMODEREGLEMENTID(modeReglement);
        OTReglement.setDtREGLEMENT(new Date());
        OTReglement.setLgUSERID(user);
        OTReglement.setBoolCHECKED(true);
        OTReglement.setStrSTATUT(statut);
        emg.persist(OTReglement);
        return OTReglement;
    }
    
    public void addDiffere(TCompteClient OTCompteClient, TClient c, TPreenregistrement OTPreenregistrement, Integer int_PRICE, Integer reste, TUser user, EntityManager emg) {
        TPreenregistrementCompteClient oTPreenregistrementCompteClient = new TPreenregistrementCompteClient(UUID.randomUUID().toString());
        oTPreenregistrementCompteClient.setDtCREATED(new Date());
        oTPreenregistrementCompteClient.setDtUPDATED(new Date());
        oTPreenregistrementCompteClient.setLgCOMPTECLIENTID(OTCompteClient);
        oTPreenregistrementCompteClient.setLgPREENREGISTREMENTID(OTPreenregistrement);
        oTPreenregistrementCompteClient.setLgUSERID(user);
        oTPreenregistrementCompteClient.setIntPRICE(int_PRICE);
        oTPreenregistrementCompteClient.setIntPRICERESTE(reste);
//        oTPreenregistrementCompteClient.setClient(c);
        oTPreenregistrementCompteClient.setStrSTATUT(commonparameter.statut_is_Closed);
        emg.persist(oTPreenregistrementCompteClient);
    }
    
    public TRecettes addRecette(Integer MONTANT, String str_DESCRIPTION, String str_REF_FACTURE, TUser user, EntityManager emg) {
        TRecettes OTRecettes = new TRecettes();
        OTRecettes.setIdRecette(UUID.randomUUID().toString());
        OTRecettes.setLgTYPERECETTEID(findTTypeRecetteById(emg));
        OTRecettes.setIntAMOUNT(MONTANT.doubleValue());
        OTRecettes.setDtCREATED(new Date());
        OTRecettes.setDtUPDATED(new Date());
        OTRecettes.setStrDESCRIPTION("");
        OTRecettes.setStrREFFACTURE(str_REF_FACTURE);
        OTRecettes.setStrCREATEDBY(user.getStrLOGIN());
        OTRecettes.setLgUSERID(user);
        emg.persist(OTRecettes);
        
        return OTRecettes;
    }
    
    private TCompteClient findByClientId(String id, EntityManager emg) {
        if (id == null || "".equals(id)) {
            return null;
        }
        try {
            TypedQuery<TCompteClient> tq = emg.createQuery("SELECT o FROM TCompteClient o WHERE o.lgCLIENTID.lgCLIENTID=?1 ", TCompteClient.class);
            tq.setParameter(1, id);
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    public void addtransactionComptant(Optional<TTypeMvtCaisse> optionalCaisse, TPreenregistrement op, boolean KEY_TAKE_INTO_ACCOUNT, Integer int_AMOUNT, TCompteClient compteClient, Integer int_AMOUNT_REMIS, Integer int_AMOUNT_RECU, TReglement OTReglement, String lg_TYPE_REGLEMENT_ID, TUser user, EntityManager emg) {
        Integer para, intAMOUNT = int_AMOUNT;
        para = op.getIntACCOUNT();
        para = (int_AMOUNT > 0 ? para : (-1 * para));
        if (KEY_TAKE_INTO_ACCOUNT) {
            Integer y = op.getIntPRICE() - op.getIntACCOUNT();
            if (y == 0) {
                intAMOUNT = int_AMOUNT;
            } else if (y > 0) {
                Integer x = Math.abs(int_AMOUNT) - (op.getIntACCOUNT() - op.getIntREMISEPARA());
                if (x >= 0) {
                    intAMOUNT = op.getIntACCOUNT() - op.getIntREMISEPARA();
                } else {
                    intAMOUNT = 0;
                }
            }
        }
        
        TCashTransaction cashTransaction = new TCashTransaction(UUID.randomUUID().toString());
        cashTransaction.setBoolCHECKED(Boolean.TRUE);
        cashTransaction.setDtCREATED(new Date());
        cashTransaction.setDtUPDATED(new Date());
        cashTransaction.setIntACCOUNT(para);
        cashTransaction.setIntAMOUNT(int_AMOUNT);
        cashTransaction.setStrTYPE(Boolean.TRUE);
        cashTransaction.setStrDESCRIPTION("Vente " + op.getStrTYPEVENTE());
        cashTransaction.setLgTYPEREGLEMENTID(findById(lg_TYPE_REGLEMENT_ID, emg).getLgTYPEREGLEMENTID());
        cashTransaction.setLgUSERID(user);
        cashTransaction.setIntAMOUNT2(intAMOUNT);
        cashTransaction.setStrTRANSACTIONREF("C");
        cashTransaction.setStrTASK("VENTE");
        optionalCaisse.ifPresent(tp -> {
            cashTransaction.setStrNUMEROCOMPTE(tp.getStrCODECOMPTABLE());
        });
        cashTransaction.setLgREGLEMENTID(OTReglement);
        cashTransaction.setLgMOTIFREGLEMENTID(findMotifReglement(emg));
        cashTransaction.setStrREFFACTURE(op.getLgPREENREGISTREMENTID());
        cashTransaction.setStrRESSOURCEREF(op.getLgPREENREGISTREMENTID());
        cashTransaction.setStrTYPEVENTE(op.getStrTYPEVENTE());
        cashTransaction.setIntAMOUNTRECU(int_AMOUNT_RECU);
        cashTransaction.setIntAMOUNTCREDIT(int_AMOUNT);
        cashTransaction.setIntAMOUNTDEBIT(0);
        cashTransaction.setIntAMOUNTREMIS(int_AMOUNT_RECU - int_AMOUNT);
//        cashTransaction.setIntAMOUNTREMIS(int_AMOUNT_REMIS);
        cashTransaction.setCaissier(user);
        cashTransaction.setStrREFCOMPTECLIENT((compteClient != null ? compteClient.getLgCOMPTECLIENTID() : ""));
        emg.persist(cashTransaction);
        
    }
    
    private Optional<TTypeMvtCaisse> getOne(String id, EntityManager emg) {//Parameter.KEY_PARAM_MVT_VENTE_NON_ORDONNANCEE
        try {
            return Optional.ofNullable(emg.find(TTypeMvtCaisse.class, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public JSONObject updateVenteClotureAssurance(ClotureVenteParams clotureVenteParams) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            final TUser tUser = clotureVenteParams.getUserId();
            if (!checkResumeCaisse(tUser, emg).isPresent()) {
                json.put("success", false);
                json.put("msg", "Désolé votre caisse est fermée. Veuillez l'ouvrir avant de proceder à validation");
                return json;
            }
            
            boolean isDiff = false;
            TPreenregistrement tp = emg.find(TPreenregistrement.class, clotureVenteParams.getVenteId());
            if (tp.getCopy()) {
                TPreenregistrement venteAsupprimer = getEm().find(TPreenregistrement.class, tp.getLgPARENTID());
                if (checkChargedPreenregistrement(venteAsupprimer.getLgPREENREGISTREMENTID()).isPresent()) {
                    json.put("success", false);
                    json.put("msg", "Désolé la vente a été facturée");
                    return json;
                }
                annulerVenteAnterieur(tUser, venteAsupprimer);
            }
            tp.setChecked(Boolean.TRUE);
            TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId(), emg);
            Optional<TTypeMvtCaisse> typeMvtCaisse = getOne(Parameter.KEY_PARAM_MVT_VENTE_ORDONNANCE, emg);
            ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(tp, emg);
            Integer montant = tp.getIntPRICE(), amount;
            boolean isAvoir = checkAvoir(lstTPreenregistrementDetail);
            String statut = statutDiff(clotureVenteParams.getTypeRegleId());
            TUser vendeur = userFromId(clotureVenteParams.getUserVendeurId(), emg);
            TCompteClient compteClient = findByClientId(clotureVenteParams.getClientId(), emg);
            Optional<TParameters> KEY_TAKE_INTO_ACCOUNT = findParamettre("KEY_TAKE_INTO_ACCOUNT", emg);
            Optional<TClient> client = findClientById(clotureVenteParams.getClientId());
            if (tp.getMedecin() == null && gererOrdoncier()) {
                boolean isOrdonnancier = checkOrdonnancier(lstTPreenregistrementDetail);
                if (isOrdonnancier) {
                    json.put("success", false);
                    json.put("msg", "Ajouter le médecin qui prescrit l'ordonnance");
                    json.put("codeError", 1);
                    return json;
                }
                
            }
            JSONObject result = createPreenregistrementCompteClientTierspayant(clotureVenteParams.getTierspayants(), tp, clotureVenteParams.isSansBon(), tUser, emg);
            if (result.has("success")) {
                return result;
            }
            if (clotureVenteParams.getTypeVenteId().equals(Parameter.VENTE_ASSURANCE)) {
                findAyantDroit(clotureVenteParams.getAyantDroitId(), emg).ifPresent(c -> {
                    tp.setStrFIRSTNAMECUSTOMER(c.getStrFIRSTNAME());
                    tp.setStrLASTNAMECUSTOMER(c.getStrLASTNAME());
                    tp.setStrNUMEROSECURITESOCIAL(c.getStrNUMEROSECURITESOCIAL());
                    tp.setAyantDroit(c);
                    tp.setStrPHONECUSTOME(client.get().getStrADRESSE());
                    
                });
                amount = montant - tp.getIntPRICEREMISE();
//                amount = tp.getIntCUSTPART() > 0 ? montant - (tp.getIntCUSTPART() - tp.getIntPRICEREMISE()) : montant - tp.getIntPRICEREMISE();
            } else {
                amount = montant - tp.getIntPRICEREMISE();
                client.ifPresent(c -> {
                    tp.setStrFIRSTNAMECUSTOMER(c.getStrFIRSTNAME());
                    tp.setStrLASTNAMECUSTOMER(c.getStrLASTNAME());
                    tp.setStrNUMEROSECURITESOCIAL(c.getStrNUMEROSECURITESOCIAL());
                    tp.setStrPHONECUSTOME(c.getStrADRESSE());
                    tp.setClient(c);
                });
            }
            
            if (clotureVenteParams.getTypeRegleId().equals(DateConverter.REGL_DIFF)) {
                isDiff = true;
                addDiffere(compteClient, client.orElse(null), tp, amount, amount - clotureVenteParams.getMontantPaye(), clotureVenteParams.getUserId(), emg);
                
            }
            TTypeVente OTTypeVente = typeVenteFromId(clotureVenteParams.getTypeVenteId(), emg);
            TReglement tReglement = createTReglement(clotureVenteParams.getUserId(), modeReglement,
                    "", tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getBanque(), clotureVenteParams.getLieux(), clotureVenteParams.getCommentaire(), commonparameter.statut_is_Closed, "", emg);
            tp.setBWITHOUTBON(clotureVenteParams.isSansBon());
            tp.setLgTYPEVENTEID(OTTypeVente);
            tp.setLgREGLEMENTID(tReglement);
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(commonparameter.statut_is_Closed);
            tp.setStrTYPEVENTE(Parameter.KEY_VENTE_ORDONNANCE);
            tp.setStrSTATUTVENTE(statut);
            tp.setIntPRICEOTHER(tp.getIntPRICE());
            if (!tp.getCopy()) {
                tp.setDtUPDATED(new Date());
//                tp.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_VENTE, emg).getString("code"));
            }
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()), clotureVenteParams.getUserId().getLgEMPLACEMENTID()).getReference());
            java.util.function.Predicate<Optional<TParameters>> test = e -> {
                if (e.isPresent()) {
                    return Integer.valueOf(e.get().getStrVALUE().trim()) == 1;
                }
                return false;
            };
            boolean key_account = test.test(KEY_TAKE_INTO_ACCOUNT);
            if (key_account) {
                tp.setIntPRICEOTHER(tp.getIntACCOUNT());
            }
            cloturerItemsVente(tp.getLgPREENREGISTREMENTID(), emg);
            
            if (amount > 0) {
                addtransactionComptant(typeMvtCaisse, tp, key_account, clotureVenteParams.getMontantPaye(), compteClient, clotureVenteParams.getMontantRemis(), clotureVenteParams.getMontantRecu(), tReglement, clotureVenteParams.getTypeRegleId(), clotureVenteParams.getUserId(), emg);
                addRecette(clotureVenteParams.getMontantPaye(), "Vente VO", tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getUserId(), emg);
            }
            addtransactionAssurance(typeMvtCaisse, tp, key_account, (-1) * clotureVenteParams.getPartTP(), compteClient, tReglement, clotureVenteParams.getTypeRegleId(), tUser, clotureVenteParams, emg);
            addTransaction(tUser, tUser, tp.getLgPREENREGISTREMENTID(), montant, tp.getIntACCOUNT(),
                    amount, tp.getIntPRICEREMISE(), clotureVenteParams.getMontantRecu(),
                    true, CategoryTransaction.CREDIT,
                    TypeTransaction.VENTE_CREDIT,
                    findById(clotureVenteParams.getTypeRegleId(), emg),
                    typeMvtCaisse.get(), clotureVenteParams.getPartTP(),
                    emg, clotureVenteParams.getMontantPaye(), tp.getMontantTva(), clotureVenteParams.getMarge(), tp.getStrREF(), client.orElse(null), isDiff, (tp.getIntCUSTPART() - tp.getIntPRICEREMISE()), clotureVenteParams.getTypeRegleId(), DateConverter.convertDateToLocalDateTime(tp.getDtUPDATED()), DateConverter.convertDateToLocalDate(tp.getDtUPDATED()));
            
            emg.merge(tp);
            json.put("success", true)
                    .put("copy", tp.getCopy())
                    .put("msg", "Opération effectuée avec success")
                    .put("ref", tp.getLgPREENREGISTREMENTID());
            sendMessageMvtsStockQueue(clotureVenteParams.getVenteId());
            sendMessageClientJmsQueue(clotureVenteParams.getVenteId());
            if (clotureVenteParams.getMontantRemis() > 0) {
                afficheurMontantAPayer(clotureVenteParams.getMontantRemis(), " MONNAIE:");
            }
        } catch (Exception e) {
            
            LOG.log(Level.SEVERE, null, e);
            
            try {
                json.put("success", false).put("msg", "Erreur: Echec de validation de la vente");
            } catch (JSONException ex) {
                
            }
        }
        return json;
    }
    
    private boolean gererOrdoncier() {
        try {
            TParameters ordoncier = getEm().find(TParameters.class, "KEY_GERE_ORDONCIER");
            return (Integer.valueOf(ordoncier.getStrVALUE()) == 1);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public JSONObject updateVenteClotureComptant(ClotureVenteParams clotureVenteParams) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            final TUser tUser = clotureVenteParams.getUserId();
            if (!checkResumeCaisse(tUser, emg).isPresent()) {
                json.put("success", false);
                json.put("msg", "Désolé votre caisse est fermée. Veuillez l'ouvrir avant de proceder à validation");
                return json;
            }
            TPreenregistrement tp = emg.find(TPreenregistrement.class, clotureVenteParams.getVenteId());
            if (tp.getCopy()) {
                TPreenregistrement venteAsupprimer = getEm().find(TPreenregistrement.class, tp.getLgPARENTID());
                annulerVenteAnterieur(tUser, venteAsupprimer);
            }
            String old = tp.getLgTYPEVENTEID().getLgTYPEVENTEID();
            if (!old.equals(clotureVenteParams.getTypeVenteId())) {
                json.put("success", false);
                json.put("msg", "Désolé impossible de transformer une vente au comptant");
                json.put("codeError", 0);
                return json;
            }
            ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(tp, emg);
            boolean ordonnancier = gererOrdoncier();
            if (ordonnancier) {
                boolean isOrdonnancier = checkOrdonnancier(lstTPreenregistrementDetail);
                if (tp.getMedecin() == null && isOrdonnancier) {
                    json.put("success", false);
                    json.put("msg", "Ajouter le médecin qui prescrit l'ordonnance");
                    json.put("codeError", 1);
                    return json;
                    
                }
                if (tp.getClient() == null && isOrdonnancier) {
                    json.put("success", false);
                    json.put("msg", "Ajouter un client à la vente");
                    json.put("codeError", 2);
                    return json;
                }
            }
            
            tp.setChecked(Boolean.TRUE);
            TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId(), emg);
            Optional<TTypeMvtCaisse> typeMvtCaisse = getOne(Parameter.KEY_PARAM_MVT_VENTE_NON_ORDONNANCEE, emg);
            
            Integer montant = tp.getIntPRICE(); //sumVente(lstTPreenregistrementDetail);
            boolean isAvoir = checkAvoir(lstTPreenregistrementDetail);
            String statut = statutDiff(clotureVenteParams.getTypeRegleId());
            TUser vendeur = userFromId(clotureVenteParams.getUserVendeurId(), emg);
            Integer amount = montant - tp.getIntPRICEREMISE();
            TCompteClient compteClient = findByClientId(clotureVenteParams.getClientId(), emg);
            Optional<TParameters> KEY_TAKE_INTO_ACCOUNT = findParamettre("KEY_TAKE_INTO_ACCOUNT", emg);
            Optional<TClient> client = findClientById(clotureVenteParams.getClientId());
            client.ifPresent(c -> {
                tp.setStrFIRSTNAMECUSTOMER(c.getStrFIRSTNAME());
                tp.setStrLASTNAMECUSTOMER(c.getStrLASTNAME());
                tp.setStrPHONECUSTOME(c.getStrADRESSE());
                tp.setClient(c);
            });
            if (clotureVenteParams.getTypeRegleId().equals(DateConverter.REGL_DIFF)) {
                client.ifPresent(c -> {
                    addDiffere(compteClient, c, tp, amount, amount - clotureVenteParams.getMontantPaye(), clotureVenteParams.getUserId(), emg);
                });
            }
            TReglement tReglement = createTReglement(clotureVenteParams.getUserId(), modeReglement,
                    "", tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getBanque(), clotureVenteParams.getLieux(), clotureVenteParams.getCommentaire(), commonparameter.statut_is_Closed, "", emg);
            tp.setBWITHOUTBON(false);
            tp.setLgREGLEMENTID(tReglement);
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(commonparameter.statut_is_Closed);
            tp.setStrSTATUTVENTE(statut);
            tp.setIntPRICEOTHER(tp.getIntPRICE());
            if (!tp.getCopy()) {
                tp.setDtUPDATED(new Date());
//                tp.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_VENTE, emg).getString("code"));
            }
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()), clotureVenteParams.getUserId().getLgEMPLACEMENTID()).getReference());
            java.util.function.Predicate<Optional<TParameters>> test = e -> {
                if (e.isPresent()) {
                    return Integer.valueOf(e.get().getStrVALUE().trim()) == 1;
                }
                return false;
            };
            boolean key_account = test.test(KEY_TAKE_INTO_ACCOUNT);
            if (key_account) {
                tp.setIntPRICEOTHER(tp.getIntACCOUNT());
            }
            cloturerItemsVente(tp.getLgPREENREGISTREMENTID(), emg);
            addtransactionComptant(typeMvtCaisse, tp, key_account, clotureVenteParams.getMontantPaye(), compteClient, clotureVenteParams.getMontantRemis(), clotureVenteParams.getMontantRecu(), tReglement, clotureVenteParams.getTypeRegleId(), clotureVenteParams.getUserId(), emg);
            addRecette(clotureVenteParams.getMontantPaye(), "Vente VNO", tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getUserId(), emg);
            addTransaction(client.orElse(null), tUser, tUser,
                    tp.getLgPREENREGISTREMENTID(), montant,
                    tp.getIntACCOUNT(), amount, clotureVenteParams.getMontantRecu(),
                    true, CategoryTransaction.CREDIT, TypeTransaction.VENTE_COMPTANT,
                    findById(clotureVenteParams.getTypeRegleId(), emg), typeMvtCaisse.get(),
                    emg,
                    clotureVenteParams.getMontantPaye(), tp.getMontantTva(), clotureVenteParams.getMarge(), tp.getStrREF(), tp.getIntACCOUNT(), DateConverter.convertDateToLocalDateTime(tp.getDtUPDATED()), DateConverter.convertDateToLocalDate(tp.getDtUPDATED()));
            
            emg.merge(tp);
            json.put("success", true)
                    .put("msg", "Opération effectuée avec success")
                    .put("copy", tp.getCopy())
                    .put("ref", tp.getLgPREENREGISTREMENTID());
            
            sendMessageMvtsStockQueue(clotureVenteParams.getVenteId());
            afficheurMontantAPayer(clotureVenteParams.getMontantRemis(), " MONNAIE:");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
            try {
                json.put("success", false).put("msg", "Erreur: Echec de validation de la vente");
                json.put("codeError", 0);
            } catch (JSONException ex) {
                
            }
        }
        return json;
    }
    
    public void addtransactionAssurance(Optional<TTypeMvtCaisse> optionalCaisse, TPreenregistrement op, boolean KEY_TAKE_INTO_ACCOUNT, Integer int_AMOUNT, TCompteClient compteClient, TReglement OTReglement, String lg_TYPE_REGLEMENT_ID, TUser user, EntityManager emg) {
        Integer para, intAMOUNT = int_AMOUNT;
        para = op.getIntACCOUNT();
        para = (int_AMOUNT > 0 ? para : (-1 * para));
        if (KEY_TAKE_INTO_ACCOUNT) {
            Integer y = op.getIntPRICE() - op.getIntACCOUNT();
            if (y == 0) {
                intAMOUNT = int_AMOUNT;
            } else if (y > 0) {
                Integer x = Math.abs(int_AMOUNT) - (op.getIntACCOUNT() - op.getIntREMISEPARA());
                if (x >= 0) {
                    intAMOUNT = op.getIntACCOUNT() - op.getIntREMISEPARA();
                } else {
                    intAMOUNT = 0;
                }
            }
        }
        
        TCashTransaction cashTransaction = new TCashTransaction(UUID.randomUUID().toString());
        cashTransaction.setBoolCHECKED(Boolean.TRUE);
        cashTransaction.setDtCREATED(new Date());
        cashTransaction.setDtUPDATED(new Date());
        cashTransaction.setIntACCOUNT(para);
        cashTransaction.setIntAMOUNT(int_AMOUNT);
        cashTransaction.setStrTYPE(Boolean.TRUE);
        cashTransaction.setStrDESCRIPTION("Vente " + op.getStrTYPEVENTE());
        cashTransaction.setLgTYPEREGLEMENTID(findById(lg_TYPE_REGLEMENT_ID, emg).getLgTYPEREGLEMENTID());
        cashTransaction.setLgUSERID(user);
        cashTransaction.setIntAMOUNT2(intAMOUNT);
        cashTransaction.setStrTRANSACTIONREF("C");
        cashTransaction.setStrTASK("VENTE");
        optionalCaisse.ifPresent(tp -> {
            cashTransaction.setStrNUMEROCOMPTE(tp.getStrCODECOMPTABLE());
        });
        cashTransaction.setLgREGLEMENTID(OTReglement);
        cashTransaction.setLgMOTIFREGLEMENTID(findMotifReglement(emg));
        cashTransaction.setStrREFFACTURE(op.getLgPREENREGISTREMENTID());
        cashTransaction.setStrRESSOURCEREF(op.getLgPREENREGISTREMENTID());
        cashTransaction.setStrTYPEVENTE(op.getStrTYPEVENTE());
        cashTransaction.setIntAMOUNTRECU(0);
        cashTransaction.setIntAMOUNTCREDIT(0);
        cashTransaction.setIntAMOUNTDEBIT((-1) * int_AMOUNT);
        cashTransaction.setIntAMOUNTREMIS(0);
        cashTransaction.setCaissier(user);
        cashTransaction.setStrREFCOMPTECLIENT((compteClient != null ? compteClient.getLgCOMPTECLIENTID() : ""));
        emg.persist(cashTransaction);
        
    }
    
    public void addtransactionAssurance(Optional<TTypeMvtCaisse> optionalCaisse, TPreenregistrement op, boolean KEY_TAKE_INTO_ACCOUNT, Integer int_AMOUNT, TCompteClient compteClient, TReglement OTReglement, String lg_TYPE_REGLEMENT_ID, TUser user, ClotureVenteParams clotureVenteParams, EntityManager emg) {
        Integer para, intAMOUNT = int_AMOUNT;
        para = op.getIntACCOUNT();
        para = (int_AMOUNT > 0 ? para : (-1 * para));
        if (KEY_TAKE_INTO_ACCOUNT) {
            Integer y = op.getIntPRICE() - op.getIntACCOUNT();
            if (y == 0) {
                intAMOUNT = int_AMOUNT;
            } else if (y > 0) {
                Integer x = Math.abs(int_AMOUNT) - (op.getIntACCOUNT() - op.getIntREMISEPARA());
                if (x >= 0) {
                    intAMOUNT = op.getIntACCOUNT() - op.getIntREMISEPARA();
                } else {
                    intAMOUNT = 0;
                }
            }
        }
        
        TCashTransaction cashTransaction = new TCashTransaction(UUID.randomUUID().toString());
        cashTransaction.setBoolCHECKED(Boolean.TRUE);
        cashTransaction.setDtCREATED(new Date());
        cashTransaction.setDtUPDATED(new Date());
        cashTransaction.setIntACCOUNT(para);
        cashTransaction.setIntAMOUNT(int_AMOUNT);
        cashTransaction.setStrTYPE(Boolean.TRUE);
        cashTransaction.setStrDESCRIPTION("Vente " + op.getStrTYPEVENTE());
        cashTransaction.setLgTYPEREGLEMENTID(findById(lg_TYPE_REGLEMENT_ID, emg).getLgTYPEREGLEMENTID());
        cashTransaction.setLgUSERID(user);
        cashTransaction.setIntAMOUNT2(intAMOUNT);
        cashTransaction.setStrTRANSACTIONREF("C");
        cashTransaction.setStrTASK("VENTE");
        optionalCaisse.ifPresent(tp -> {
            cashTransaction.setStrNUMEROCOMPTE(tp.getStrCODECOMPTABLE());
        });
        cashTransaction.setLgREGLEMENTID(OTReglement);
        cashTransaction.setLgMOTIFREGLEMENTID(findMotifReglement(emg));
        cashTransaction.setStrREFFACTURE(op.getLgPREENREGISTREMENTID());
        cashTransaction.setStrRESSOURCEREF(op.getLgPREENREGISTREMENTID());
        cashTransaction.setStrTYPEVENTE(op.getStrTYPEVENTE());
        cashTransaction.setIntAMOUNTRECU(clotureVenteParams.getMontantRecu());
        cashTransaction.setIntAMOUNTCREDIT(0);
        cashTransaction.setIntAMOUNTDEBIT((-1) * int_AMOUNT);
        cashTransaction.setIntAMOUNTREMIS(clotureVenteParams.getMontantRemis());
        cashTransaction.setCaissier(user);
        cashTransaction.setStrREFCOMPTECLIENT((compteClient != null ? compteClient.getLgCOMPTECLIENTID() : ""));
        emg.persist(cashTransaction);
        
    }
    
    public boolean checkRefBonIsUse(String Ref_Bon, TCompteClientTiersPayant oTCompteClientTiersPayant, EntityManager emg) {
        
        try {
            
            if (!"".equals(Ref_Bon)) {
                TPreenregistrementCompteClientTiersPayent OPreenregistrementCompteClientTiersPayent = (TPreenregistrementCompteClientTiersPayent) emg.createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?1 AND t.strREFBON = ?2 AND t.strSTATUT = ?3")
                        .setParameter(1, oTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID()).setParameter(2, Ref_Bon).setParameter(3, commonparameter.statut_is_Closed).getSingleResult();
                return (OPreenregistrementCompteClientTiersPayent != null);
                
            }
            return false;
            
        } catch (Exception ex) {
            return false;
        }
        
    }
    
    private TPreenregistrementCompteClientTiersPayent getTPreenregistrementCompteClientTiersPayent(String lg_PREENREGISTREMENT_ID, String lg_COMPTE_CLIENT_TIERS_PAYANT_ID, EntityManager emg) {
        
        TypedQuery<TPreenregistrementCompteClientTiersPayent> q = emg.createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 AND t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?2", TPreenregistrementCompteClientTiersPayent.class)
                .setParameter(1, lg_PREENREGISTREMENT_ID).setParameter(2, lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
        q.setMaxResults(1);
        return q.getSingleResult();
    }
    
    public JSONObject createPreenregistrementCompteClientTierspayant(List<TiersPayantParams> tierspayants, TPreenregistrement OTPreenregistrement, final boolean b_WITHOUT_BON, final TUser u, EntityManager emg) {
        JSONObject json = new JSONObject();
        try {
            java.util.function.Predicate<TiersPayantParams> p = e -> {
                if (b_WITHOUT_BON) {
                    return true;
                }
                return (e.getNumBon() != null && !"".equals(e.getNumBon()));
                
            };
            boolean canContinue = tierspayants.stream().anyMatch(p);
            if (canContinue) {
                tierspayants.forEach(params -> {
                    TCompteClientTiersPayant OTCompteClientTiersPayant = emg.find(TCompteClientTiersPayant.class, params.getCompteTp());
                    TTiersPayant payant = OTCompteClientTiersPayant.getLgTIERSPAYANTID();
                    if (boonDejaUtilise(params.getNumBon(), OTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID()) && !OTPreenregistrement.getCopy()) {
                        try {
                            json.putOnce("success", false).putOnce("msg", "Le numéro de  <span style='color:red;font-weight:800;'> " + params.getNumBon() + " </span> est déjà utilisé par l'assureur :: " + payant.getStrFULLNAME());
                        } catch (JSONException ex) {
                        }
                    } else {
                        TPreenregistrementCompteClientTiersPayent item = getTPreenregistrementCompteClientTiersPayent(OTPreenregistrement.getLgPREENREGISTREMENTID(), OTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID(), emg);
                        item.setDtUPDATED(OTPreenregistrement.getDtUPDATED());
                        item.setIntPERCENT(params.getTaux());
                        item.setIntPRICE(params.getTpnet());
                        item.setIntPRICERESTE(params.getTpnet());
                        item.setStrREFBON(params.getNumBon());
                        item.setDblQUOTACONSOVENTE(item.getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOVENTE() != null ? item.getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOVENTE() + item.getIntPRICE() : 0);
                        item.setStrSTATUT(commonparameter.statut_is_Closed);
                        item.setLgUSERID(u);
                        if (params.isPrincipal() || tierspayants.size() == 1) {
                            OTPreenregistrement.setStrREFBON(params.getNumBon());
                        }
                        emg.merge(item);
                        
                    }
                });
                
            } else {
                json.put("success", false).put("msg", "Veuillez saisir les numéros de bon");
            }
        } catch (Exception e) {
            try {
                e.printStackTrace(System.err);
                json.put("success", false).put("msg", "Erreur:: La validation a échouée");
            } catch (JSONException ex) {
            }
        }
        return json;
        
    }
    
    public void cloturerItemsVente(String lg_PREENREGISTREMENT_ID, EntityManager emg) {
        try {
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaUpdate<TPreenregistrementDetail> cq = cb.createCriteriaUpdate(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            cq.set(root.get(TPreenregistrementDetail_.strSTATUT), commonparameter.statut_is_Closed).where(cb.equal(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID).get(TPreenregistrement_.lgPREENREGISTREMENTID), lg_PREENREGISTREMENT_ID));
            emg.createQuery(cq).executeUpdate();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    @Override
    public JSONObject clotureravoir(String lg_PREENREGISTREMENT_ID, TUser tUser) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
//            emg.getTransaction().begin();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaUpdate<TPreenregistrementDetail> cq = cb.createCriteriaUpdate(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            cq.set(root.get(TPreenregistrementDetail_.dtUPDATED), new Date())
                    .set(root.get(TPreenregistrementDetail_.bISAVOIR), false)
                    .set(root.get(TPreenregistrementDetail_.intAVOIR), 0)
                    .set(root.get(TPreenregistrementDetail_.intAVOIRSERVED), root.get(TPreenregistrementDetail_.intQUANTITY))
                    .set(root.get(TPreenregistrementDetail_.intQUANTITYSERVED), root.get(TPreenregistrementDetail_.intQUANTITY))
                    .where(cb.and(cb.equal(root.get(TPreenregistrementDetail_.bISAVOIR), true), cb.equal(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID).get(TPreenregistrement_.lgPREENREGISTREMENTID), lg_PREENREGISTREMENT_ID)));
            emg.createQuery(cq).executeUpdate();
            preenregistrement.setDtUPDATED(new Date());
            preenregistrement.setBISAVOIR(false);
            emg.merge(preenregistrement);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            try {
                json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
            } catch (JSONException ex) {
                Logger.getLogger(SalesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    @Override
    public JSONObject updateVenteBonVente(String idCompteClientItem, String str_REF_BON) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrementCompteClientTiersPayent OPreenregistrement = emg.find(TPreenregistrementCompteClientTiersPayent.class, idCompteClientItem);
//            emg.getTransaction().begin();
            OPreenregistrement.setDtUPDATED(new Date());
            OPreenregistrement.setStrREFBON(str_REF_BON);
            TPreenregistrement preenregistrement = OPreenregistrement.getLgPREENREGISTREMENTID();
            preenregistrement.setStrREFBON(str_REF_BON);
            emg.merge(preenregistrement);
            emg.merge(OPreenregistrement);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            try {
                json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
            } catch (JSONException ex) {
                
            }
            LOG.log(Level.SEVERE, null, e);
            
        }
        return json;
    }
    
    @Override
    public JSONObject closeventeBon(String lg_PREENREGISTREMENT_ID) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
//            emg.getTransaction().begin();
            preenregistrement.setDtUPDATED(new Date());
            preenregistrement.setBWITHOUTBON(false);
            emg.merge(preenregistrement);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            try {
                json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
            } catch (JSONException ex) {
                
            }
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    @Override
    public JSONObject addtierspayant(SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, params.getVenteId());
            TCompteClientTiersPayant clientTiersPayant = emg.find(TCompteClientTiersPayant.class, params.getTierspayants().get(0).getCompteTp());
//            emg.getTransaction().begin();
            TPreenregistrementCompteClientTiersPayent clientTiersPayent
                    = new TPreenregistrementCompteClientTiersPayent(UUID.randomUUID().toString());
            clientTiersPayent.setLgUSERID(params.getUserId());
            clientTiersPayent.setDblQUOTACONSOVENTE(0.0);
            clientTiersPayent.setDtCREATED(new Date());
            clientTiersPayent.setDtUPDATED(new Date());
            clientTiersPayent.setLgPREENREGISTREMENTID(preenregistrement);
            clientTiersPayent.setLgCOMPTECLIENTTIERSPAYANTID(clientTiersPayant);
            clientTiersPayent.setIntPRICE(0);
            clientTiersPayent.setIntPERCENT(params.getTierspayants().get(0).getTaux());
            clientTiersPayent.setStrSTATUT(commonparameter.statut_is_Process);
            clientTiersPayent.setStrSTATUTFACTURE("unpaid");
            emg.persist(clientTiersPayent);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            try {
                json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
            } catch (JSONException ex) {
                
            }
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    @Override
    public JSONObject addtierspayant(String venteId, SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, venteId);
            TCompteClientTiersPayant clientTiersPayant = emg.find(TCompteClientTiersPayant.class, params.getTypeVenteId());
//            emg.getTransaction().begin();
            TPreenregistrementCompteClientTiersPayent clientTiersPayent
                    = new TPreenregistrementCompteClientTiersPayent(UUID.randomUUID().toString());
            clientTiersPayent.setLgUSERID(preenregistrement.getLgUSERID());
            clientTiersPayent.setDblQUOTACONSOVENTE(0.0);
            clientTiersPayent.setDtCREATED(new Date());
            clientTiersPayent.setDtUPDATED(new Date());
            clientTiersPayent.setLgPREENREGISTREMENTID(preenregistrement);
            clientTiersPayent.setLgCOMPTECLIENTTIERSPAYANTID(clientTiersPayant);
            clientTiersPayent.setIntPRICE(0);
            clientTiersPayent.setIntPRICERESTE(0);
            clientTiersPayent.setIntPERCENT(params.getQte());
            clientTiersPayent.setStrSTATUT(commonparameter.statut_is_Process);
            clientTiersPayent.setStrSTATUTFACTURE("unpaid");
            emg.persist(clientTiersPayent);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            try {
                json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
            } catch (JSONException ex) {
                
            }
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    @Override
    public JSONObject removetierspayant(String comptClientTpId, String venteId) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
//            emg.getTransaction().begin();
            TPreenregistrementCompteClientTiersPayent op = (TPreenregistrementCompteClientTiersPayent) emg.createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?1 AND t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?2")
                    .setParameter(1, comptClientTpId).setParameter(2, venteId).getSingleResult();
            emg.remove(op);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            try {
                json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
            } catch (JSONException ex) {
                
            }
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    @Override
    public JSONObject removetierspayant(SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrementCompteClientTiersPayent op = (TPreenregistrementCompteClientTiersPayent) emg.createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?1 AND t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?2")
                    .setParameter(1, params.getTierspayants().get(0).getCompteTp()).setParameter(2, params.getVenteId()).getSingleResult();
            emg.remove(op);
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            
            try {
                json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
            } catch (JSONException ex) {
                
            }
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    @Override
    public JSONObject shownetpayVno(TPreenregistrement p) throws JSONException {
        JSONObject json = new JSONObject();
        
        try {
            MontantAPaye montantAPaye;
            if (p.getLgREMISEID() == null || "".equals(p.getLgREMISEID()) || "0".equals(p.getLgREMISEID())) {
                montantAPaye = sumVenteSansRemise(items(p, getEm()));
                p.setIntPRICE(montantAPaye.getMontant());
                p.setIntACCOUNT(montantAPaye.getMontantAccount());
                getEm().merge(p);
                json.put("success", true).put("msg", "Opération effectuée avec success");
                json.put("data", new JSONObject(montantAPaye));
            } else {
                TRemise remise = p.getRemise();
                montantAPaye = getRemiseVno(p, remise, p.getIntACCOUNT());
                json.put("success", true).put("msg", "Opération effectuée avec success");
                json.put("data", new JSONObject(montantAPaye));
                
            }
            afficheurMontantAPayer(montantAPaye.getMontantNet(), "NET A PAYER: ");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    @Override
    public JSONObject shownetpayVno(SalesParams params) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            MontantAPaye montantAPaye;
            TPreenregistrement p = emg.find(TPreenregistrement.class, params.getVenteId());
            if (params.getRemiseId() == null || "".equals(params.getRemiseId())) {
                montantAPaye = sumVenteSansRemise(items(p, emg));
                p.setIntPRICE(montantAPaye.getMontant());
                p.setIntACCOUNT(montantAPaye.getMontantAccount());
                p.setIntPRICEOTHER(montantAPaye.getMontant());
                
                json.put("success", true).put("msg", "Opération effectuée avec success");
                json.put("data", new JSONObject(montantAPaye));
                
            } else {
                TRemise remise = p.getRemise();
                montantAPaye = getRemiseVno(p, remise, p.getIntACCOUNT());
                
                json.put("success", true).put("msg", "Opération effectuée avec success");
                json.put("data", new JSONObject(montantAPaye));
                
            }
            emg.merge(p);
            afficheurMontantAPayer(montantAPaye.getMontantNet(), "NET A PAYER: ");
        } catch (Exception e) {
            
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    @Override
    public JSONObject addDevisRemisse(SalesParams params) throws JSONException {
        JSONObject json = new JSONObject();
        
        try {
            TRemise remise = findTRemise(params.getRemiseId(), getEm());
            TPreenregistrement preenregistrement = getEm().find(TPreenregistrement.class, params.getVenteId());
            preenregistrement.setLgREMISEID(params.getRemiseId());
            preenregistrement.setRemise(remise);
            MontantAPaye montantAPaye = getRemiseVno(preenregistrement, remise,
                    preenregistrement.getIntACCOUNT());
//            getEm().merge(preenregistrement);

            json.put("success", true)
                    .put("msg", "Opération effectuée avec success")
                    .put("data", new JSONObject().put("montantRemise", montantAPaye.getRemise())
                            .put("montant", montantAPaye.getMontant()));
        } catch (Exception e) {
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    @Override
    public JSONObject addRemisse(SalesParams params) throws JSONException {
        // A revoir pour le calcul du net à payer
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
//            emg.getTransaction().begin();
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, params.getVenteId());
            preenregistrement.setLgREMISEID(params.getRemiseId());
            preenregistrement.setRemise(findTRemise(params.getRemiseId(), emg));
            emg.merge(preenregistrement);
//            emg.getTransaction().commit();
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
//            if (emg.getTransaction().isActive()) {
//                emg.getTransaction().rollback();
//            }
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    @Override
    public JSONObject faireDevis(SalesParams params) throws JSONException {
        if (params.getTypeVenteId().equals(Parameter.VENTE_COMPTANT)) {
            return createPreVente(params);
        }
        return createPreVenteVo(params);
        
    }
    
    private TRemise findTRemise(String id, EntityManager emg) {
        try {
            return emg.find(TRemise.class, id);
        } catch (Exception e) {
            return null;
        }
    }
    
    private TWorkflowRemiseArticle findByArticleRemise(String strCODEREMISE) {
        if (StringUtils.isEmpty(strCODEREMISE)) {
            return null;
        }
        try {
            TypedQuery<TWorkflowRemiseArticle> q = getEm().createQuery("SELECT t FROM TWorkflowRemiseArticle t WHERE t.strCODEREMISEARTICLE = ?1  AND t.strSTATUT = ?2 ", TWorkflowRemiseArticle.class);
            q.setParameter(1, strCODEREMISE).
                    setParameter(2, DateConverter.STATUT_ENABLE);
            q.setMaxResults(1);
            return q.getSingleResult();
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private TGrilleRemise grilleRemiseRemiseFromWorkflow(TPreenregistrement OTPreenregistrement, TFamille OTFamille, String remiseId) {
        int int_code_grille_remise;
        
        TGrilleRemise OTGrilleRemise;
        
        try {
            TWorkflowRemiseArticle OTWorkflowRemiseArticle = findByArticleRemise(OTFamille.getStrCODEREMISE());
            if (OTWorkflowRemiseArticle == null) {
                return null;
            }
            
            if ((OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(bll.common.Parameter.VENTE_ASSURANCE)) || (OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(bll.common.Parameter.VENTE_AVEC_CARNET))) {
                int_code_grille_remise = OTWorkflowRemiseArticle.getStrCODEGRILLEVO();
                OTGrilleRemise = (TGrilleRemise) getEm().createQuery("SELECT t FROM TGrilleRemise t WHERE t.strCODEGRILLE = ?1  AND t.strSTATUT = ?2  AND t.lgREMISEID.lgREMISEID = ?3 ").
                        setParameter(1, int_code_grille_remise).
                        setParameter(2, DateConverter.STATUT_ENABLE).
                        setParameter(3, remiseId).
                        getSingleResult();
                
                return OTGrilleRemise;
            } else {
                int_code_grille_remise = OTWorkflowRemiseArticle.getStrCODEGRILLEVNO();
                OTGrilleRemise = (TGrilleRemise) getEm().createQuery("SELECT t FROM TGrilleRemise t WHERE t.strCODEGRILLE  = ?1  AND t.strSTATUT = ?2 AND t.lgREMISEID.lgREMISEID = ?3 ").
                        setParameter(1, int_code_grille_remise).
                        setParameter(2, DateConverter.STATUT_ENABLE).
                        setParameter(3, remiseId).
                        getSingleResult();
                return OTGrilleRemise;
            }
            
        } catch (Exception e) {
//            e.printStackTrace(System.err);
            return null;
        }
        
    }
    
    public MontantAPaye calculVoNet(TPreenregistrement OTPreenregistrement, List<TiersPayantParams> tierspayants, EntityManager emg) {
        try {
            ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(OTPreenregistrement, emg);
            Integer RemiseCarnet = 0, montantvente = 0;
            Integer totalTp = 0, totalTaux = 0, montantVariable;
            MontantAPaye montantAPaye;
            List<TiersPayantParams> resultat = new ArrayList<>();
            if (OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
                totalTaux = 100;
                TRemise remise = OTPreenregistrement.getRemise();
                remise = remise != null ? remise : OTPreenregistrement.getClient().getRemise();
                if (remise != null) {
                    montantAPaye = getRemiseVno(OTPreenregistrement, remise, OTPreenregistrement.getIntPRICE());
                    montantvente = montantAPaye.getMontant();
                    RemiseCarnet = montantAPaye.getRemise();
                } else {
                    montantAPaye = sumVenteSansRemise(lstTPreenregistrementDetail);
                    montantvente = montantAPaye.getMontant();
                    
                }
                
                TiersPayantParams tp = new TiersPayantParams();
                Integer tpnet = montantvente - RemiseCarnet;
                totalTp += tpnet;
                tp.setCompteTp(tierspayants.get(0).getCompteTp());
                tp.setNumBon(tierspayants.get(0).getNumBon());
                tp.setTpnet(tpnet);
                tp.setDiscount(RemiseCarnet);
                tp.setTaux(100);
                resultat.add(tp);
            } else {
                TRemise remise = OTPreenregistrement.getRemise();
                if (remise != null) {
                    montantAPaye = getRemiseVno(OTPreenregistrement, remise, OTPreenregistrement.getIntPRICE());
                    montantvente = montantAPaye.getMontant();
                    montantVariable = montantvente;
                    RemiseCarnet = montantAPaye.getRemise();
                } else {
                    montantAPaye = sumVenteSansRemise(lstTPreenregistrementDetail);
                    montantvente = montantAPaye.getMontant();
                    montantVariable = montantvente;
                }
                
                for (TiersPayantParams tierspayant : tierspayants) {
                    TiersPayantParams tp = new TiersPayantParams();
                    Integer taux = tierspayant.getTaux();
//                    totalTaux += taux;
                    Double montantTp = montantvente * (Double.valueOf(taux) / 100);
                    Integer tpnet = (int) Math.ceil(montantTp);
                    
                    int _taux = 0;
                    if (montantVariable > tpnet) {
                        montantVariable -= tpnet;
                        _taux = taux;
                        totalTaux += _taux;
                    } else if (montantVariable <= tpnet) {
                        tpnet = montantVariable;
//                        _taux = (int) Math.ceil((tpnet * 100) / montantvente);
                        _taux = 100 - totalTaux;
                        totalTaux += _taux;
                        
                    }
                    totalTp += tpnet;
                    tp.setTaux(_taux);
                    tp.setCompteTp(tierspayant.getCompteTp());
                    tp.setNumBon(tierspayant.getNumBon());
                    tp.setTpnet(tpnet);
                    tp.setDiscount(0);
                    resultat.add(tp);
                }
                
            }
            Integer netCustomer = (montantvente - totalTp) - RemiseCarnet;
            if (totalTaux >= 100) {
                netCustomer = 0;
            }
            MontantAPaye map = new MontantAPaye(netCustomer, montantvente, totalTp,
                    RemiseCarnet, montantAPaye.getMarge(), montantAPaye.getMontantTva());
            map.setTierspayants(resultat);
            return map;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new MontantAPaye();
        }
    }
    
    private MontantAPaye getRemiseVno(TPreenregistrement OTPreenregistrement, TRemise OTRemise, Integer para) {
        Integer int_TOTAL_REMISE, int_REMISE_PARA = 0, montantNet = 0;
        LongAdder totalRemise = new LongAdder();
        LongAdder totalRemisePara = new LongAdder();
        LongAdder totalAmount = new LongAdder();
        LongAdder marge = new LongAdder();
        LongAdder montantTva = new LongAdder();
        LongAdder montantAccount = new LongAdder();
        ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(OTPreenregistrement, getEm());
        lstTPreenregistrementDetail.forEach(x -> {
            totalAmount.add(x.getIntPRICE());
            montantTva.add(x.getMontantTva());
            TFamille famille = x.getLgFAMILLEID();
            Integer remise = 0;
            if (!StringUtils.isEmpty(famille.getStrCODEREMISE()) && !famille.getStrCODEREMISE().equals("2") && !famille.getStrCODEREMISE().equals("3")) {
                TGrilleRemise OTGrilleRemise = grilleRemiseRemiseFromWorkflow(x.getLgPREENREGISTREMENTID(), famille, OTRemise.getLgREMISEID());
                
                if (OTGrilleRemise != null) {
                    remise = (int) ((x.getIntPRICE() * OTGrilleRemise.getDblTAUX()) / 100);
                    if (!x.getBoolACCOUNT()) {
                        totalRemisePara.add(remise);
                    }
                    totalRemise.add(remise);
                    x.setLgGRILLEREMISEID(OTGrilleRemise.getLgGRILLEREMISEID());
                }
                
            }
            
            x.setIntPRICEREMISE(remise);
            getEm().merge(x);
            if (x.getLgFAMILLEID().getBoolACCOUNT()) {
                int _magre = (x.getIntPRICE() - remise - x.getMontantTva()) - (x.getIntQUANTITY() * famille.getIntPAF());
                marge.add(_magre);
                montantAccount.add(x.getIntPRICE());
                montantTva.add(x.getMontantTva());
                
            }
            
        });
        Integer montantTotal = totalAmount.intValue();
        int_TOTAL_REMISE = totalRemise.intValue();
        
        int tva = montantTva.intValue();
        montantNet = montantTotal - int_TOTAL_REMISE;
        OTPreenregistrement.setIntPRICE(montantTotal);
        OTPreenregistrement.setIntACCOUNT(montantAccount.intValue());
        OTPreenregistrement.setIntPRICEREMISE(int_TOTAL_REMISE);
        OTPreenregistrement.setIntREMISEPARA(int_REMISE_PARA);
        OTPreenregistrement.setMontantTva(tva);
        if (int_TOTAL_REMISE > 0 && OTRemise == null) {
            OTPreenregistrement.setRemise(OTRemise);
        }
        return new MontantAPaye(DateConverter.arrondiModuloOfNumber(montantNet, 5),
                montantTotal, 0, DateConverter.arrondiModuloOfNumber(int_TOTAL_REMISE, 5), marge.intValue(), tva);
    }
    
    @Override
    public JSONObject produits(String produitId, String emplacementId) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<SearchDTO> cq = cb.createQuery(SearchDTO.class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.lgFAMILLEID), produitId));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emplacementId));
            cq.select(cb.construct(SearchDTO.class,
                    root.get(TFamille_.lgFAMILLEID),
                    root.get(TFamille_.intCIP),
                    root.get(TFamille_.strNAME),
                    root.get("lgZONEGEOID").get("strLIBELLEE"),
                    root.get(TFamille_.intPRICE),
                    fa.get(TFamilleStock_.intNUMBERAVAILABLE),
                    root.get(TFamille_.intPAF),
                    fa.get(TFamilleStock_.intNUMBER),
                    root.get(TFamille_.intNUMBERDETAIL)
            )).distinct(true);
            cq.where(predicate);
            Query q = emg.createQuery(cq);
            q.setHint(QueryHints.HINT_CACHEABLE, false);
            q.setMaxResults(1);
            SearchDTO list = (SearchDTO) q.getSingleResult();
            json.put("success", true);
            json.put("data", new JSONObject(list));
        } catch (Exception e) {
            json.put("success", false);
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    @Override
    public JSONObject produits(QueryDTO params, Boolean all) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            
            long count = produitsCount(params, emg);
            if (count == 0) {
                json.put("total", count);
                json.put("data", new JSONArray());
                return json;
            }
            
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<SearchDTO> cq = cb.createQuery(SearchDTO.class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleGrossiste> st = root.join("tFamilleGrossisteCollection", JoinType.INNER);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TFamille_.intEAN13), params.getQuery() + "%"), cb.like(st.get("strCODEARTICLE"), params.getQuery() + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), params.getQuery() + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), params.getQuery() + "%")));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), params.getEmplacementId()));
            cq.select(cb.construct(SearchDTO.class,
                    root.get(TFamille_.lgFAMILLEID),
                    root.get(TFamille_.intCIP),
                    root.get(TFamille_.strNAME),
                    root.get("lgZONEGEOID").get("strLIBELLEE"),
                    root.get(TFamille_.intPRICE),
                    fa.get(TFamilleStock_.intNUMBERAVAILABLE),
                    root.get(TFamille_.intPAF),
                    fa.get(TFamilleStock_.intNUMBER),
                    root.get(TFamille_.boolDECONDITIONNE), root.get(TFamille_.lgFAMILLEPARENTID)
            )).orderBy(cb.asc(root.get(TFamille_.strNAME))).distinct(true);
            cq.where(predicate);
            Query q = emg.createQuery(cq);
            q.setHint(QueryHints.HINT_CACHEABLE, false);
            if (!all) {
                q.setFirstResult(params.getStart());
                q.setMaxResults(params.getLimit());
            }
            
            List<SearchDTO> list = q.getResultList();
            json.put("total", count);
            json.put("data", new JSONArray(list));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    public long produitsCount(QueryDTO params, EntityManager emg) {
        
        try {
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleGrossiste> st = root.join("tFamilleGrossisteCollection", JoinType.INNER);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TFamille_.intEAN13), params.getQuery() + "%"), cb.like(st.get("strCODEARTICLE"), params.getQuery() + "%"), cb.like(root.get(TFamille_.lgFAMILLEID), params.getQuery() + "%"), cb.like(root.get(TFamille_.strDESCRIPTION), params.getQuery() + "%")));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), params.getEmplacementId()));
            
            cq.select(cb.countDistinct(root));
            
            cq.where(predicate);
            
            Query q = emg.createQuery(cq);
            return (Long) q.getSingleResult();
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
        
    }
    
    @Override
    public JSONObject detailsVente(QueryDTO params, Boolean all) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            long count = detailsVenteCount(params, emg);
            if (count == 0) {
                json.put("total", count);
                json.put("data", new JSONArray());
                return json;
            }
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            
            CriteriaQuery<TPreenregistrementDetail> cq = cb.createQuery(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> pf = root.join("lgFAMILLEID", JoinType.INNER);
            
            Predicate p = cb.conjunction();
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                p = cb.and(p, cb.or(cb.like(pf.get(TFamille_.strDESCRIPTION), params.getQuery() + "%"), cb.like(pf.get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(pf.get(TFamille_.intEAN13), params.getQuery() + "%")));
            }
            if (params.getStatut() != null && !"".equals(params.getStatut())) {
                p = cb.and(p, cb.equal(join.get(TPreenregistrement_.strSTATUT), params.getStatut()));
            }
            p = cb.and(p, cb.equal(join.get(TPreenregistrement_.lgPREENREGISTREMENTID), params.getVenteId()));
            cq.select(root)
                    .orderBy(cb.desc(root.get(TPreenregistrementDetail_.dtUPDATED)));
            
            cq.where(p);
            
            Query q = emg.createQuery(cq);
            if (!all) {
                q.setFirstResult(params.getStart());
                q.setMaxResults(params.getLimit());
            }
            List<TPreenregistrementDetail> list = q.getResultList();
            List<VenteDetailsDTO> datas = list.stream().map(VenteDetailsDTO::new).collect(Collectors.toList());
            json.put("total", count);
            json.put("data", new JSONArray(datas));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return json;
    }
    
    public long detailsVenteCount(QueryDTO params, EntityManager emg) {
        try {
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join("lgPREENREGISTREMENTID", JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> pf = root.join("lgFAMILLEID", JoinType.INNER);
            
            Predicate p = cb.conjunction();
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                p = cb.and(p, cb.or(cb.like(pf.get(TFamille_.strDESCRIPTION), params.getQuery() + "%"), cb.like(pf.get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(pf.get(TFamille_.intEAN13), params.getQuery() + "%")));
            }
            if (params.getStatut() != null && !"".equals(params.getStatut())) {
                p = cb.and(p, cb.equal(join.get(TPreenregistrement_.strSTATUT), params.getStatut()));
            }
            p = cb.and(p, cb.equal(join.get(TPreenregistrement_.lgPREENREGISTREMENTID), params.getVenteId()));
            cq.select(cb.count(root));
            
            cq.where(p);
            
            Query q = emg.createQuery(cq);
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
        
    }
    
    public List<TCompteClientTiersPayant> findCompteClientTierspayantByClientId(String clientId, EntityManager emg) {
        try {
            TypedQuery<TCompteClientTiersPayant> q = emg.createQuery("SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1  ", TCompteClientTiersPayant.class);
            q.setParameter(1, clientId);
            return q.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public Integer nbreProduitsByVente(String venteId) {
        try {
            Query q = getEm().createQuery("SELECT COUNT(o) FROM TPreenregistrementDetail o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 ");
            q.setParameter(1, venteId);
            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }
    }
    
    @Override
    public Integer productQtyByVente(String venteId) {
        try {
            Query q = getEm().createQuery("SELECT SUM(o.intQUANTITY) FROM TPreenregistrementDetail o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 ");
            q.setParameter(1, venteId);
            return (Integer) q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }
    }
    
    @Override
    public JSONObject updatRemiseVenteDepot(String venteId, int valueRemise) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, venteId);
//            emg.getTransaction().begin();
            Double montant = Math.ceil(((Double.valueOf(preenregistrement.getIntPRICE()) * valueRemise) / 100));
            Integer r = montant.intValue();
            preenregistrement.setIntPRICEREMISE(r);
            json.put("success", true).put("montant", preenregistrement.getIntPRICE()).put("remise", r).
                    put("montantNet", preenregistrement.getIntPRICE() - r);
            
        } catch (Exception e) {
            json.put("success", false).put("msg", "L'opération a échoué");
        }
        return json;
    }
    
    private Integer calculRemiseDepot(Integer montantVente, Integer valeur) {
        
        try {
            if (valeur == 0) {
                return valeur;
            }
            Double montant = Math.ceil(((Double.valueOf(montantVente) * valeur) / 100));
            return montant.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public JSONObject clotureVenteDepot(ClotureVenteParams clotureVenteParams) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            final TUser tUser = clotureVenteParams.getUserId();
            TPreenregistrement tp = emg.find(TPreenregistrement.class, clotureVenteParams.getVenteId());
            tp.setChecked(Boolean.FALSE);
            tp.setCopy(Boolean.FALSE);
            TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId(), emg);
            Optional<TTypeMvtCaisse> typeMvtCaisse = getOne(Parameter.KEY_PARAM_MVT_VENTE_ORDONNANCE, emg);
            ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(tp, emg);
            boolean isAvoir = checkAvoir(lstTPreenregistrementDetail);
            String statut = statutDiff(clotureVenteParams.getTypeRegleId());
            TUser vendeur = userFromId(clotureVenteParams.getUserVendeurId(), emg);
            TCompteClient compteClient = findByClientId(clotureVenteParams.getClientId(), emg);
            Optional<TClient> client = findClientById(clotureVenteParams.getClientId());
            TEmplacement emplacement = emg.find(TEmplacement.class, tp.getPkBrand());
            tp.setStrFIRSTNAMECUSTOMER(emplacement.getStrFIRSTNAME());
            tp.setStrLASTNAMECUSTOMER(emplacement.getStrLASTNAME());
            tp.setStrPHONECUSTOME(emplacement.getStrPHONE());
            client.ifPresent(c -> {
                tp.setClient(c);
            });
            TReglement tReglement = createTReglement(clotureVenteParams.getUserId(), modeReglement,
                    "", tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getBanque(), clotureVenteParams.getLieux(), clotureVenteParams.getCommentaire(), commonparameter.statut_is_Closed, "", emg);
            tp.setLgREGLEMENTID(tReglement);
            tp.setDtUPDATED(new Date());
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(commonparameter.statut_is_Closed);
            tp.setStrSTATUTVENTE(statut);
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()), vendeur.getLgEMPLACEMENTID()).getReference());
//            tp.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_VENTE, emg).getString("code"));
            tp.setIntACCOUNT(tp.getIntPRICE());
            tp.setIntPRICEOTHER(tp.getIntACCOUNT());
            cloturerItemsVente(tp.getLgPREENREGISTREMENTID(), emg);
            emg.merge(tp);
            addTransactionDepot(tUser, tUser, tp.getLgPREENREGISTREMENTID(), tp.getIntPRICE(), tp.getIntPRICE(),
                    tp.getIntPRICE(),
                    findById(DateConverter.MODE_ESP, emg),
                    typeMvtCaisse.get(),
                    emg, tp.getMontantTva(), clotureVenteParams.getMarge(), tp.getStrREF(), client.orElse(null));
            
            addtransactionAssurance(typeMvtCaisse, tp, false, (-1) * tp.getIntPRICE(), compteClient, tReglement, clotureVenteParams.getTypeRegleId(), tUser, clotureVenteParams, emg);
            mvtProduitService.updateVenteStockDepot(tp, lstTPreenregistrementDetail, emg, emplacement);
            json.put("success", true).put("msg", "Opération effectuée avec success").put("ref", tp.getLgPREENREGISTREMENTID());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Erreur: Echec de validation de la vente");
        }
        return json;
    }
    
    @Override
    public JSONObject clotureVenteDepotAgree(ClotureVenteParams clotureVenteParams) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            final TUser tUser = clotureVenteParams.getUserId();
            if (!checkResumeCaisse(tUser, emg).isPresent()) {
                json.put("success", false);
                json.put("msg", "Désolé votre caisse est fermée. Veuillez l'ouvrir avant de proceder à validation");
                return json;
            }
            TPreenregistrement tp = emg.find(TPreenregistrement.class, clotureVenteParams.getVenteId());
            tp.setChecked(Boolean.TRUE);
            tp.setCopy(Boolean.FALSE);
            TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId(), emg);
            Optional<TTypeMvtCaisse> typeMvtCaisse = getOne(Parameter.KEY_PARAM_MVT_VENTE_NON_ORDONNANCEE, emg);
            ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(tp, emg);
            Integer montant = tp.getIntPRICE();
            boolean isAvoir = checkAvoir(lstTPreenregistrementDetail);
            String statut = statutDiff(clotureVenteParams.getTypeRegleId());
            TUser vendeur = userFromId(clotureVenteParams.getUserVendeurId(), emg);
            Integer amount = montant - tp.getIntPRICEREMISE();
            TCompteClient compteClient = findByClientId(clotureVenteParams.getClientId(), emg);
            TEmplacement emplacement = emg.find(TEmplacement.class, tp.getPkBrand());
            tp.setStrFIRSTNAMECUSTOMER(emplacement.getStrFIRSTNAME());
            tp.setStrLASTNAMECUSTOMER(emplacement.getStrLASTNAME());
            tp.setStrPHONECUSTOME(emplacement.getStrPHONE());
            if (clotureVenteParams.getTypeRegleId().equals(DateConverter.REGL_DIFF)) {
                findClientById(clotureVenteParams.getClientId()).ifPresent(c -> {
                    tp.setClient(c);
                    addDiffere(compteClient, c, tp, amount, amount - clotureVenteParams.getMontantPaye(), clotureVenteParams.getUserId(), emg);
                });
            }
            TReglement tReglement = createTReglement(clotureVenteParams.getUserId(), modeReglement,
                    "", tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getBanque(), clotureVenteParams.getLieux(), clotureVenteParams.getCommentaire(), commonparameter.statut_is_Closed, "", emg);
            tp.setBWITHOUTBON(false);
            tp.setLgREGLEMENTID(tReglement);
            tp.setDtUPDATED(new Date());
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(commonparameter.statut_is_Closed);
            tp.setStrSTATUTVENTE(statut);
            tp.setIntPRICE(montant);
            tp.setIntPRICEOTHER(montant);
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()), vendeur.getLgEMPLACEMENTID()).getReference());
//            tp.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_VENTE, emg).getString("code"));
            cloturerItemsVente(tp.getLgPREENREGISTREMENTID(), emg);
            addtransactionComptant(typeMvtCaisse, tp, false, clotureVenteParams.getMontantPaye(), compteClient, clotureVenteParams.getMontantRemis(), clotureVenteParams.getMontantRecu(), tReglement, clotureVenteParams.getTypeRegleId(), clotureVenteParams.getUserId(), emg);
            addRecette(clotureVenteParams.getMontantPaye(), "Vente VNO", tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getUserId(), emg);
            addTransaction(null, tUser, tUser,
                    tp.getLgPREENREGISTREMENTID(), montant,
                    tp.getIntACCOUNT(), amount, clotureVenteParams.getMontantRecu(),
                    true, CategoryTransaction.CREDIT, TypeTransaction.VENTE_COMPTANT,
                    findById(clotureVenteParams.getTypeRegleId(), emg), typeMvtCaisse.get(),
                    emg,
                    clotureVenteParams.getMontantPaye(), tp.getMontantTva(), clotureVenteParams.getMarge(), tp.getStrREF(), tp.getIntACCOUNT(), DateConverter.convertDateToLocalDateTime(tp.getDtUPDATED()), DateConverter.convertDateToLocalDate(tp.getDtUPDATED()));
            mvtProduitService.updateVenteStockDepot(tp, lstTPreenregistrementDetail, emg, emplacement);
            emg.merge(tp);
            json.put("success", true).put("msg", "Opération effectuée avec success").put("ref", tp.getLgPREENREGISTREMENTID());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
            try {
                json.put("success", false).put("msg", "Erreur: Echec de validation de la vente");
            } catch (JSONException ex) {
                
            }
        }
        return json;
    }
    
    @Override
    public JSONObject shownetpaydepotAgree(SalesParams params) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement p = emg.find(TPreenregistrement.class, params.getVenteId());
            MontantAPaye montantAPaye = sumVenteSansRemise(items(p, emg));
            p.setIntPRICE(montantAPaye.getMontant());
            Integer montantRemise = calculRemiseDepot(montantAPaye.getMontant(), params.getRemiseDepot());
            p.setIntACCOUNT(montantAPaye.getMontantAccount() - montantRemise);
            json.put("success", true).put("msg", "Opération effectuée avec success");
            json.put("data", new JSONObject(new MontantAPaye(
                    DateConverter.arrondiModuloOfNumber(montantAPaye.getMontant() - montantRemise, 5), montantAPaye.getMontant(),
                    0, DateConverter.arrondiModuloOfNumber(montantRemise, 5), montantAPaye.getMarge(), montantAPaye.getMontantTva())));
            p.setIntPRICEREMISE(montantRemise);
            emg.merge(p);
        } catch (Exception e) {
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    @Override
    public JSONObject shownetpaydepotAgree(TPreenregistrement p) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            MontantAPaye montantAPaye = sumVenteSansRemise(items(p, emg));
            p.setIntPRICE(montantAPaye.getMontant());
            
            Integer remiseValue;
            try {
                remiseValue = Integer.valueOf(p.getLgREMISEID());
            } catch (NumberFormatException e) {
                remiseValue = 0;
            }
            Integer montantRemise = calculRemiseDepot(montantAPaye.getMontant(), remiseValue);
            p.setIntACCOUNT(montantAPaye.getMontantAccount() - montantRemise);
            json.put("success", true).put("msg", "Opération effectuée avec success");
            json.put("data", new JSONObject(new MontantAPaye(
                    DateConverter.arrondiModuloOfNumber(montantAPaye.getMontant() - montantRemise, 5), montantAPaye.getMontant(),
                    0, DateConverter.arrondiModuloOfNumber(montantRemise, 5), montantAPaye.getMarge(), montantAPaye.getMontantTva())));
            p.setIntPRICEREMISE(montantRemise);
            emg.merge(p);
            afficheurMontantAPayer(montantAPaye.getMontantNet(), "NET A PAYER: ");
        } catch (Exception e) {
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    @Override
    public JSONObject updateclient(SalesParams params) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement tp = emg.find(TPreenregistrement.class, params.getVenteId());
            TClient client = findClient(params.getClientId(), emg);
            tp.setClient(client);
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    private TClient findClient(String clientId, EntityManager emg) {
        try {
            
            return emg.find(TClient.class, clientId);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public JSONObject findOneproduit(String query, String emplacementId) throws JSONException {
        EntityManager emg = this.getEm();
        JSONObject json = new JSONObject();
        try {
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<SearchDTO> cq = cb.createQuery(SearchDTO.class);
            Root<TFamille> root = cq.from(TFamille.class);
            Join<TFamille, TFamilleGrossiste> st = root.join("tFamilleGrossisteCollection", JoinType.INNER);
            Join<TFamille, TFamilleStock> fa = root.join("tFamilleStockCollection", JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.intCIP), query + "%"), cb.like(st.get("strCODEARTICLE"), query + "%"), cb.like(root.get(TFamille_.intEAN13), query + "%")));
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emplacementId));
            cq.select(cb.construct(SearchDTO.class,
                    root.get(TFamille_.lgFAMILLEID),
                    root.get(TFamille_.intCIP),
                    root.get(TFamille_.strNAME),
                    root.get("lgZONEGEOID").get("strLIBELLEE"),
                    root.get(TFamille_.intPRICE),
                    fa.get(TFamilleStock_.intNUMBERAVAILABLE),
                    root.get(TFamille_.intPAF),
                    fa.get(TFamilleStock_.intNUMBER),
                    root.get(TFamille_.boolDECONDITIONNE), root.get(TFamille_.lgFAMILLEPARENTID)
            )).orderBy(cb.asc(root.get(TFamille_.strNAME))).distinct(true);
            cq.where(predicate);
            Query q = emg.createQuery(cq);
            q.setMaxResults(1);
            q.setHint(QueryHints.HINT_CACHEABLE, false);
            SearchDTO list = (SearchDTO) q.getSingleResult();
            json.put("success", true);
            json.put("data", new JSONObject(list));
        } catch (Exception e) {
            json.put("success", false);
        }
        return json;
    }
    
    @Override
    public JSONObject shownetpayVo(SalesParams params) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TPreenregistrement preenregistrement = getEm().find(TPreenregistrement.class, params.getVenteId());
            MontantAPaye montant = calculVoNet(preenregistrement, params.getTierspayants(), getEm());
            Integer montantNet = montant.getMontantNet();
            preenregistrement.setIntPRICEREMISE(montant.getRemise());
            preenregistrement.setIntCUSTPART(montantNet);
            preenregistrement.setIntPRICE(montant.getMontant());
            preenregistrement.setIntACCOUNT(montant.getMontant());
            getEm().merge(preenregistrement);
            montant.setRemise(DateConverter.arrondiModuloOfNumber(montant.getRemise(), 5));
            montant.setMontantNet(DateConverter.arrondiModuloOfNumber(montant.getMontantNet(), 5));
            json.put("success", true).put("msg", "Opération effectuée avec success");
            json.put("data", new JSONObject(montant));
            afficheurMontantAPayer(montant.getMontantNet(), "NET A PAYER: ");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    @Override
    public JSONObject removeClientToVente(String venteId) throws JSONException {
        try {
            TPreenregistrement op = getEm().find(TPreenregistrement.class, venteId);
            if (op == null) {
                return new JSONObject().put("success", true);
            }
            op.setClient(null);
            op.setAyantDroit(null);
            List<TPreenregistrementCompteClientTiersPayent> list = getTPreenregistrementCompteClientTiersPayent(venteId);
            list.forEach(c -> {
                getEm().remove(c);
            });
            getEm().merge(op);
            return new JSONObject().put("success", true);
        } catch (Exception e) {
            return new JSONObject().put("success", false);
        }
    }
    
    private List<TPreenregistrementCompteClientTiersPayent> getTPreenregistrementCompteClientTiersPayent(String lg_PREENREGISTREMENT_ID) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q
                    = getEm().createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 ", TPreenregistrementCompteClientTiersPayent.class)
                            .setParameter(1, lg_PREENREGISTREMENT_ID);
            return q.getResultList();
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
        
    }
    
    @Override
    public JSONObject modifiertypevente(String venteId, ClotureVenteParams params) throws JSONException {
        try {
            TPreenregistrement p = getEm().find(TPreenregistrement.class, venteId);
            
            TTypeVente oldType = p.getLgTYPEVENTEID();
            if ((p.getStrTYPEVENTE().equals(DateConverter.VENTE_ASSURANCE)) && (params.getTypeVenteId().equals(DateConverter.VENTE_COMPTANT_ID))) {
                return new JSONObject().put("success", false).put("msg", "Imposible de modifier une vente assurance en vente au comptant").put("typeVenteId", oldType.getLgTYPEVENTEID());
            }
            if (oldType.getLgTYPEVENTEID().equals(params.getTypeVenteId())) {
                return new JSONObject().put("success", true).put("typeVenteId", oldType.getLgTYPEVENTEID());
            }
            TTypeVente newype = getEm().find(TTypeVente.class, params.getTypeVenteId());
            p.setLgTYPEVENTEID(newype);
            p.setStrTYPEVENTE(DateConverter.VENTE_ASSURANCE);
            getEm().merge(p);
            return new JSONObject().put("success", true).put("typeVenteId", params.getTypeVenteId());
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new JSONObject().put("success", false).put("msg", "Imposible de modifier la vente ").put("typeVenteId", DateConverter.VENTE_COMPTANT_ID);
        }
    }
    
    @Override
    public JSONObject mettreAjourDonneesClientVenteExistante(String venteId, SalesParams params) throws JSONException {
        try {
            TPreenregistrement OTPreenregistrement = getEm().find(TPreenregistrement.class, venteId);
            findClientById(params.getClientId()).ifPresent(c -> {
                OTPreenregistrement.setStrFIRSTNAMECUSTOMER(c.getStrFIRSTNAME());
                OTPreenregistrement.setStrLASTNAMECUSTOMER(c.getStrLASTNAME());
                OTPreenregistrement.setStrPHONECUSTOME(c.getStrADRESSE());
                OTPreenregistrement.setClient(c);
            });
            if (!OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
                findAyantDroit(params.getAyantDroitId(), getEm()).ifPresent(a -> {
                    OTPreenregistrement.setStrFIRSTNAMECUSTOMER(a.getStrFIRSTNAME());
                    OTPreenregistrement.setStrLASTNAMECUSTOMER(a.getStrLASTNAME());
                    OTPreenregistrement.setAyantDroit(a);
                });
            }
            createPreenregistrementTierspayant(params.getTierspayants(), OTPreenregistrement, getEm());
            getEm().merge(OTPreenregistrement);
            return new JSONObject().put("success", true);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new JSONObject().put("success", false);
        }
    }
    
    private TPreenregistrement createPreventeCopy(TUser ooTUser, TPreenregistrement tp) {
        TPreenregistrement _new = new TPreenregistrement();
        _new.setLgPREENREGISTREMENTID(UUID.randomUUID().toString());
        _new.setLgUSERID(ooTUser);
        _new.setIntPRICEREMISE(tp.getIntPRICEREMISE());
        _new.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        _new.setIntACCOUNT(tp.getIntACCOUNT());
        _new.setIntREMISEPARA(tp.getIntREMISEPARA());
        _new.setIntPRICE(tp.getIntPRICE());
        _new.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        _new.setIntCUSTPART(tp.getIntCUSTPART());
        _new.setMontantTva(tp.getMontantTva());
        _new.setDtCREATED(tp.getDtCREATED());
        _new.setDtUPDATED(tp.getDtUPDATED());
        _new.setLgPARENTID(tp.getLgPREENREGISTREMENTID());
        _new.setLgUSERVENDEURID(tp.getLgUSERVENDEURID());
        _new.setLgUSERCAISSIERID(tp.getLgUSERCAISSIERID());
        _new.setBISAVOIR(tp.getBISAVOIR());
        _new.setBISCANCEL(false);
        _new.setLgNATUREVENTEID(tp.getLgNATUREVENTEID());
        _new.setLgREMISEID(tp.getLgREMISEID());
        _new.setStrREFTICKET(DateConverter.getShortId(10));
        _new.setLgTYPEVENTEID(tp.getLgTYPEVENTEID());
        _new.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        _new.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        _new.setStrFIRSTNAMECUSTOMER(tp.getStrFIRSTNAMECUSTOMER());
        _new.setStrREFBON(tp.getStrREFBON());
        _new.setStrPHONECUSTOME(tp.getStrPHONECUSTOME());
        _new.setStrLASTNAMECUSTOMER(tp.getStrLASTNAMECUSTOMER());
        _new.setStrNUMEROSECURITESOCIAL(tp.getStrNUMEROSECURITESOCIAL());
        _new.setStrINFOSCLT(tp.getStrINFOSCLT());
        _new.setIntSENDTOSUGGESTION(0);
        _new.setPkBrand(tp.getPkBrand());
        _new.setClient(tp.getClient());
        _new.setAyantDroit(tp.getAyantDroit());
        _new.setMedecin(tp.getMedecin());
        _new.setStrSTATUT(DateConverter.STATUT_PROCESS);
//        _new.setLgREGLEMENTID(tp.getLgREGLEMENTID());
        _new.setLgPREENGISTREMENTANNULEID(tp.getLgPREENREGISTREMENTID());
        _new.setStrREF(buildRefTmp(LocalDate.now(), ooTUser.getLgEMPLACEMENTID()).getReferenceTemp());
//        _new.setStrREF(buildRef(LocalDate.now(), Parameter.KEY_LAST_ORDER_NUMBER_PREVENTE, getEm()).getString("code"));
        _new.setStrREF(tp.getStrREF());
        _new.setChecked(Boolean.TRUE);
        _new.setCopy(Boolean.TRUE);
        getEm().persist(_new);
        return _new;
    }
    
    private TPreenregistrement findByParent(String idVente) {
        try {
            TypedQuery<TPreenregistrement> tq = getEm().createQuery("SELECT o FROM TPreenregistrement o WHERE o.lgPARENTID=?1 ", TPreenregistrement.class);
            tq.setParameter(1, idVente);
            return tq.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    private TPreenregistrement findOneById(String idVente) {
        try {
            TPreenregistrement p = getEm().find(TPreenregistrement.class, idVente);
            
            return p;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public JSONObject modificationVenteCloturee(String venteId, TUser u) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            TPreenregistrement tp = getEm().find(TPreenregistrement.class, venteId);
            TPreenregistrement i = findByParent(venteId);
            if (i != null) {
                data.put("lgPREENREGISTREMENTID", i.getLgPREENREGISTREMENTID());
                data.put("strREF", i.getStrREF());
                data.put("intPRICE", i.getIntPRICE());
                data.put("intPRICEREMISE", i.getIntPRICEREMISE());
                return json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
            }
            TPreenregistrement _new = createPreventeCopy(u, tp);
            getTPreenregistrementDetail(tp, getEm()).forEach(z -> {
                createItemCopy(z, _new);
            });
            copyPreenregistrementTp(_new, venteId, u);
            findOptionalCmt(tp, getEm()).ifPresent(cp -> {
                addDiffere(_new, cp);
            });
            
            data.put("lgPREENREGISTREMENTID", _new.getLgPREENREGISTREMENTID());
            data.put("strREF", _new.getStrREF());
            data.put("intPRICE", _new.getIntPRICE());
            data.put("intPRICEREMISE", _new.getIntPRICEREMISE());
            return json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return json.put("success", false).put("msg", "Erreur :: l'opération a échouée");
        }
    }
    
    private TPreenregistrementDetail createItemCopy(TPreenregistrementDetail tp, TPreenregistrement p) {
        TPreenregistrementDetail _new = new TPreenregistrementDetail();
        _new.setLgPREENREGISTREMENTDETAILID(UUID.randomUUID().toString());
        _new.setLgPREENREGISTREMENTID(p);
        _new.setIntPRICE(tp.getIntPRICE());
        _new.setIntQUANTITY(tp.getIntQUANTITY());
        _new.setIntQUANTITYSERVED(tp.getIntQUANTITYSERVED());
        _new.setMontantTva(tp.getMontantTva());
        _new.setDtCREATED(new Date());
        _new.setStrSTATUT(DateConverter.STATUT_PROCESS);
        _new.setDtUPDATED(new Date());
        _new.setBoolACCOUNT(tp.getBoolACCOUNT());
        _new.setLgFAMILLEID(tp.getLgFAMILLEID());
        _new.setIntPRICEUNITAIR(tp.getIntPRICEUNITAIR());
        _new.setValeurTva(tp.getValeurTva());
        _new.setIntUG(tp.getIntUG());
        _new.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        _new.setIntPRICEDETAILOTHER(tp.getIntPRICEDETAILOTHER());
        _new.setIntFREEPACKNUMBER(tp.getIntFREEPACKNUMBER());
        _new.setIntPRICEREMISE(tp.getIntPRICEREMISE());
        _new.setIntAVOIR(tp.getIntAVOIR());
        _new.setIntAVOIRSERVED(tp.getIntQUANTITYSERVED());
        _new.setBISAVOIR(tp.getBISAVOIR());
        getEm().persist(_new);
        return _new;
    }
    
    private void copyPreenregistrementTp(TPreenregistrement preenregistrement, String oldPreenregistrement, TUser o) {
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(oldPreenregistrement, getEm());
        clientTiersPayents.forEach((a) -> {
            TCompteClientTiersPayant OTCompteClientTiersPayant = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent _new = new TPreenregistrementCompteClientTiersPayent();
            _new.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            _new.setLgPREENREGISTREMENTID(preenregistrement);
            _new.setIntPRICE(a.getIntPRICE());
            _new.setLgUSERID(o);
            _new.setStrSTATUT(DateConverter.STATUT_PROCESS);
            _new.setDtCREATED(a.getDtCREATED());
            _new.setDtUPDATED(a.getDtUPDATED());
            _new.setLgCOMPTECLIENTTIERSPAYANTID(OTCompteClientTiersPayant);
            _new.setStrREFBON(a.getStrREFBON());
            _new.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            _new.setIntPERCENT(a.getIntPERCENT());
            _new.setIntPRICERESTE(a.getIntPRICERESTE());
            _new.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            _new.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            getEm().persist(_new);
            TCompteClient OTCompteClient = OTCompteClientTiersPayant.getLgCOMPTECLIENTID();
            if (OTCompteClient != null && OTCompteClientTiersPayant.getDblPLAFOND() != null && OTCompteClientTiersPayant.getDblPLAFOND() != 0) {
                OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE((OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() : 0) + _new.getIntPRICE());
                OTCompteClientTiersPayant.setDtUPDATED(new Date());
                getEm().merge(OTCompteClientTiersPayant);
            }
            if (OTCompteClient != null && OTCompteClient.getDblPLAFOND() != null && OTCompteClient.getDblPLAFOND() != 0) {
                OTCompteClient.setDblQUOTACONSOMENSUELLE((OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + _new.getIntPRICE());
                OTCompteClient.setDtUPDATED(new Date());
                getEm().merge(OTCompteClient);
            }
        });
        
    }
    
    private void addDiffere(TPreenregistrement newP, TPreenregistrementCompteClient old) {
        TPreenregistrementCompteClient oTPreenregistrementCompteClient = new TPreenregistrementCompteClient(UUID.randomUUID().toString());
        oTPreenregistrementCompteClient.setDtCREATED(old.getDtCREATED());
        oTPreenregistrementCompteClient.setDtUPDATED(old.getDtUPDATED());
        oTPreenregistrementCompteClient.setLgCOMPTECLIENTID(old.getLgCOMPTECLIENTID());
        oTPreenregistrementCompteClient.setLgPREENREGISTREMENTID(newP);
        oTPreenregistrementCompteClient.setLgUSERID(newP.getLgUSERID());
        oTPreenregistrementCompteClient.setIntPRICE(old.getIntPRICE());
        oTPreenregistrementCompteClient.setIntPRICERESTE(old.getIntPRICERESTE());
        oTPreenregistrementCompteClient.setStrSTATUT(commonparameter.statut_is_Closed);
        getEm().persist(oTPreenregistrementCompteClient);
    }
    
    private TCompteClientTiersPayant findByClientAndTiersPayant(String clientId, String tierspayentId) {
        
        try {
            TypedQuery<TCompteClientTiersPayant> q = getEm().createQuery("SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1 AND o.lgTIERSPAYANTID.lgTIERSPAYANTID=?2", TCompteClientTiersPayant.class);
            q.setParameter(1, clientId);
            q.setParameter(2, tierspayentId);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    @Override
    public JSONObject modificationVentetierpayantprincipal(String venteId, ClotureVenteParams params) throws JSONException {
        try {
            TPreenregistrement tp = getEm().find(TPreenregistrement.class, venteId);
            TCompteClientTiersPayant olClientTiersPayant = findByClientAndTiersPayant(tp.getClient().getLgCLIENTID(), params.getTypeVenteId());
            TPreenregistrementCompteClientTiersPayent clientTiersPayent = getTPreenregistrementCompteClientTiersPayent(venteId, olClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID(), getEm());
            /**
             * s'il y a modification de tu tiers-payant on rentre dans premiere
             * condition
             */
            if (!params.getTypeVenteId().equals(params.getAyantDroitId())) {
                
                TCompteClientTiersPayant newClientTiersPayant = findByClientAndTiersPayant(tp.getClient().getLgCLIENTID(), params.getAyantDroitId());
                if (clientTiersPayent != null) {
                    clientTiersPayent.setLgCOMPTECLIENTTIERSPAYANTID(newClientTiersPayant);
                    clientTiersPayent.setIntPERCENT(newClientTiersPayant.getIntPOURCENTAGE());
                    
                }
            } else {
                clientTiersPayent.setIntPERCENT(olClientTiersPayant.getIntPOURCENTAGE());
            }
            getEm().merge(clientTiersPayent);
            
            return salesStatsService.chargerClientLorsModificationVnete(venteId);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new JSONObject().put("success", false);
        }
    }
    
    @Override
    public JSONObject shownetpayVoWithEncour(SalesParams params) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TPreenregistrement preenregistrement = getEm().find(TPreenregistrement.class, params.getVenteId());
            MontantAPaye montant = calculVoNetAvecPlafondVente(preenregistrement, params.getTierspayants());
            Integer montantNet = montant.getMontantNet();
            preenregistrement.setIntPRICEREMISE(montant.getRemise());
            preenregistrement.setIntCUSTPART(montantNet);
            preenregistrement.setIntPRICE(montant.getMontant());
            getEm().merge(preenregistrement);
            montant.setRemise(DateConverter.arrondiModuloOfNumber(montant.getRemise(), 5));
            montant.setMontantNet(DateConverter.arrondiModuloOfNumber(montant.getMontantNet(), 5));
            json.put("hasRestructuring", montant.isRestructuring());
            json.put("success", true).put("msg", montant.getMessage());
            json.put("data", new JSONObject(montant));
            afficheurMontantAPayer(montant.getMontantNet(), "NET A PAYER: ");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }
        return json;
    }
    
    private JSONObject chechCustomerTiersPayantConsumption(String compteTp,
            Integer montantToBePaid) {
        
        TCompteClientTiersPayant tc = this.getEm().find(TCompteClientTiersPayant.class, compteTp);
        TTiersPayant tiersPayant = tc.getLgTIERSPAYANTID();
        String tierspayantName = tiersPayant.getStrFULLNAME();
        Integer plafondClient = (tc.getDblPLAFOND() == null || tc.getDblPLAFOND() <= 0 ? Integer.MAX_VALUE : tc.getDblPLAFOND().intValue());
        Integer encoursClient = (tc.getDbPLAFONDENCOURS() == null || tc.getDbPLAFONDENCOURS() <= 0 ? Integer.MAX_VALUE : tc.getDbPLAFONDENCOURS());
        Integer plafondTierPayant = (tiersPayant.getDblPLAFONDCREDIT() == null || tiersPayant.getDblPLAFONDCREDIT() <= 0 ? Integer.MAX_VALUE : tiersPayant.getDblPLAFONDCREDIT().intValue());
        Integer consoMensuelleClient = (tc.getDbCONSOMMATIONMENSUELLE() == null || tc.getDbCONSOMMATIONMENSUELLE() < 0 ? 0 : tc.getDbCONSOMMATIONMENSUELLE());
        Integer consoMensuelleTierPayant = (tiersPayant.getDbCONSOMMATIONMENSUELLE() == null || tiersPayant.getDbCONSOMMATIONMENSUELLE() < 0 ? 0 : tiersPayant.getDbCONSOMMATIONMENSUELLE());
        JSONObject json = chechTiersPayantConsumption(plafondTierPayant, consoMensuelleTierPayant, montantToBePaid, tierspayantName);
        String msg = json.getString("msg");
        boolean hasRestructuring = json.getBoolean("hasRestructuring");
        montantToBePaid = json.getInt("montantToBePaid");
        json = chechCustomerConsumption(plafondClient, encoursClient, consoMensuelleClient, montantToBePaid, tierspayantName);
        montantToBePaid = json.getInt("montantToBePaid");
        if (json.getBoolean("hasRestructuring")) {
            msg = json.getString("msg");
            hasRestructuring = json.getBoolean("hasRestructuring");
        }
        return new JSONObject().put("msg", msg)
                .put("hasRestructuring", hasRestructuring)
                .put("montantToBePaid", montantToBePaid);
    }
    
    private JSONObject chechCustomerConsumption(Integer plafondClient, Integer encoursClient,
            Integer consoMensuelleClient, Integer montantToBePaid, String tierspayantName) {
        boolean hasRestructuring = false;
        String msg = "";
        if ((montantToBePaid > plafondClient) || (encoursClient < consoMensuelleClient + montantToBePaid)) {
            hasRestructuring = true;
            if (encoursClient < consoMensuelleClient + montantToBePaid) {
                montantToBePaid = encoursClient - consoMensuelleClient;
                msg = "Le tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'>" + tierspayantName + "</span> ne peut prendre en compte <span style='font-weight:900;color:blue;text-decoration: underline;'>" + DateConverter.amountFormat(montantToBePaid) + " </span><br/> . Votre plafond est atteint:[ <span style='font-weight:900;color:blue;'> " + DateConverter.amountFormat(encoursClient) + " </span>]<br/> ";
            }
            if (montantToBePaid > plafondClient) {
                montantToBePaid = plafondClient;
                msg = "Le tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'>" + tierspayantName + "</span> ne peut prendre en compte <span style='font-weight:900;color:blue;text-decoration: underline;'>" + DateConverter.amountFormat(montantToBePaid) + " </span><br/> .Votre plafond vente est atteint: [ <span style='font-weight:900;color:blue;'> " + DateConverter.amountFormat(plafondClient) + " </span>]<br/> ";
            }
            
        }
        
        return new JSONObject().put("msg", msg)
                .put("hasRestructuring", hasRestructuring)
                .put("montantToBePaid", montantToBePaid);
    }
    
    private JSONObject chechTiersPayantConsumption(Integer plafondTierPayant,
            Integer consoMensuelleTierPayant, Integer montantToBePaid, String tierspayantName) {
        boolean hasRestructuring = false;
        String msg = "";
        if (plafondTierPayant < (consoMensuelleTierPayant + montantToBePaid)) {
            hasRestructuring = true;
            montantToBePaid = plafondTierPayant - consoMensuelleTierPayant;
            msg = "Le tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'>" + tierspayantName + "</span> ne peut prendre en compte <span style='font-weight:900;color:blue;text-decoration: underline;'>" + DateConverter.amountFormat(montantToBePaid) + " </span><br/> .Son plafond est atteint.<br/> ";
        }
        
        return new JSONObject().put("msg", msg)
                .put("hasRestructuring", hasRestructuring)
                .put("montantToBePaid", montantToBePaid);
    }
    
    public MontantAPaye calculVoNetAvecPlafondVente(TPreenregistrement OTPreenregistrement, List<TiersPayantParams> tierspayants) {
        try {
            String msg = " ";
            boolean hasRestructuring = false;
            ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(OTPreenregistrement, this.getEm());
            Integer RemiseCarnet = 0, montantvente = 0;
            Integer totalTp = 0;
            Integer netCustomer = 0;
            MontantAPaye montantAPaye;
            List<TiersPayantParams> resultat = new ArrayList<>();
            TClient client = OTPreenregistrement.getClient();
            if (OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
                TRemise remise = OTPreenregistrement.getRemise();
                remise = remise != null ? remise : client.getRemise();
                if (remise != null) {
                    montantAPaye = getRemiseVno(OTPreenregistrement, remise, OTPreenregistrement.getIntPRICE());
                    montantvente = montantAPaye.getMontant();
                    RemiseCarnet = montantAPaye.getRemise();
                } else {
                    montantAPaye = sumVenteSansRemise(lstTPreenregistrementDetail);
                    montantvente = montantAPaye.getMontant();
                    
                }
                
                Integer tpnet = montantvente - RemiseCarnet;
                totalTp = tpnet;
                JSONObject json = chechCustomerTiersPayantConsumption(tierspayants.get(0).getCompteTp(), tpnet);
                if (json.getBoolean("hasRestructuring")) {
                    msg = json.getString("msg");
                    hasRestructuring = json.getBoolean("hasRestructuring");
                    totalTp = json.getInt("montantToBePaid");
                }
                
                TiersPayantParams tp = new TiersPayantParams();
                tp.setCompteTp(tierspayants.get(0).getCompteTp());
                tp.setNumBon(tierspayants.get(0).getNumBon());
                tp.setTpnet(totalTp);
                tp.setDiscount(RemiseCarnet);
                tp.setTaux(100);
                if (totalTp.compareTo(tpnet) != 0) {
                    tp.setTaux((int) Math.ceil((Double.valueOf(totalTp) * 100) / tpnet));
                }
                
                resultat.add(tp);
                netCustomer = montantvente - totalTp;
            } else {
                int montantVariable;
                TRemise remise = OTPreenregistrement.getRemise();
                if (remise != null) {
                    montantAPaye = getRemiseVno(OTPreenregistrement, remise, OTPreenregistrement.getIntPRICE());
                    montantvente = montantAPaye.getMontant();
                    montantVariable = montantvente;
                    RemiseCarnet = montantAPaye.getRemise();
                } else {
                    montantAPaye = sumVenteSansRemise(lstTPreenregistrementDetail);
                    montantvente = montantAPaye.getMontant();
                    montantVariable = montantvente;
                }
                
                for (TiersPayantParams tierspayant : tierspayants) {
                    TiersPayantParams tp = new TiersPayantParams();
                    Integer taux = tierspayant.getTaux();
                    Double montantTp = montantvente * (Double.valueOf(taux) / 100);
                    Integer tpnet = (int) Math.ceil(montantTp);
                    int _taux = 0;
                    JSONObject json = chechCustomerTiersPayantConsumption(tierspayant.getCompteTp(), tpnet);
                    if (json.getBoolean("hasRestructuring")) {
                        msg += json.getString("msg") + " ";
                        hasRestructuring = json.getBoolean("hasRestructuring");
                        tpnet = json.getInt("montantToBePaid");
                        if (montantVariable > tpnet) {
                            montantVariable -= tpnet;
                            _taux = taux;
                        } else if (montantVariable <= tpnet) {
                            tpnet = montantVariable;
                            _taux = (int) Math.ceil((Double.valueOf(tpnet) * 100) / montantvente);
                        }
                        
                    } else {
                        if (montantVariable > tpnet) {
                            montantVariable -= tpnet;
                            _taux = taux;
                        } else if (montantVariable <= tpnet) {
                            tpnet = montantVariable;
                            _taux = (int) Math.ceil((montantTp * 100) / montantvente);
                        }
                    }
                    
                    totalTp += tpnet;
                    tp.setTaux(_taux);
                    tp.setCompteTp(tierspayant.getCompteTp());
                    tp.setNumBon(tierspayant.getNumBon());
                    tp.setTpnet(tpnet);
                    tp.setDiscount(0);
                    resultat.add(tp);
                }
                netCustomer = (montantvente - totalTp) - RemiseCarnet;
            }
            
            MontantAPaye map = new MontantAPaye(netCustomer, montantvente, totalTp,
                    RemiseCarnet, montantAPaye.getMarge(), montantAPaye.getMontantTva());
            map.setMessage(msg);
            map.setRestructuring(hasRestructuring);
            map.setTierspayants(resultat);
            return map;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new MontantAPaye();
        }
    }
    
    void afficheurProduit(String libelle, int qty, int prixUnitaire, int montantTotal) {
        if (afficheurActif()) {
            try {
                Afficheur afficheur = Afficheur.getInstance();
                afficheur.affichage(DataStringManager.subStringData(libelle.toUpperCase(), 0, 20));
                afficheur.affichage(DataStringManager.subStringData(qty + "*" + DateConverter.amountFormat(prixUnitaire, '.') + " = " + DateConverter.amountFormat(montantTotal, '.'), 0, 20), "begin");
            } catch (Exception e) {
            }
        }
        
    }
    
    void afficheurMontantAPayer(int montantTotal, String libelle) {
        if (afficheurActif()) {
            try {
                Afficheur afficheur = Afficheur.getInstance();
                afficheur.affichage(DataStringManager.subStringData(libelle, 0, 20));
                afficheur.affichage(DataStringManager.subStringData(DateConverter.amountFormat(montantTotal, '.'), 0, 20), "begin");
            } catch (Exception e) {
            }
        }
        
    }
    
    public boolean afficheurActif() {
        
        try {
            TParameters tp = getEm().find(TParameters.class, "KEY_ACTIVATE_DISPLAYER");
            return (tp != null && tp.getStrVALUE().trim().equals("1"));
        } catch (Exception e) {
            return false;
        }
        
    }
    
    private Medecin findMedecin(String medecinId) {
        if (StringUtils.isEmpty(medecinId)) {
            return null;
        }
        try {
            return getEm().find(Medecin.class, medecinId);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public JSONObject updateMedecin(String idVente, MedecinDTO medecinDTO) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TPreenregistrement p = findOneById(idVente);
            Medecin m = medecinService.save(medecinDTO);
            p.setMedecin(m);
            getEm().merge(p);
            return json.put("success", true).put("clientExist", p.getClient() != null)
                    .put("medecinId", m.getId());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return json.put("success", false);
        }
    }
    
    @Override
    public JSONObject updateMedecin(String idVente, String medecinId) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TPreenregistrement p = findOneById(idVente);
            Medecin m = getEm().find(Medecin.class, medecinId);
            p.setMedecin(m);
            getEm().merge(p);
            return json.put("success", true).put("clientExist", p.getClient() != null)
                    .put("medecinId", m.getId());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return json.put("success", false);
        }
    }
    
    @Override
    public boolean checkParameterByKey(String key) {
        try {
            TParameters parameters = getEm().find(TParameters.class, key);
            return (Integer.valueOf(parameters.getStrVALUE().trim()) == 1);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public JSONObject updateClientOrTierpayant(SalesParams salesParams) throws JSONException {
        try {
            if (!checkResumeCaisse(salesParams.getUserId(), getEm()).isPresent()) {
                return new JSONObject().put("success", false)
                        .put("msg", "Désolé votre caisse est fermée. Veuillez l'ouvrir avant de proceder à l'annulation");
                
            }
            int taux = getTPreenregistrementCompteClientTiersPayent(salesParams.getVenteId()).stream().map(TPreenregistrementCompteClientTiersPayent::getIntPERCENT).reduce(0, Integer::sum);
            int newTaux = salesParams.getTierspayants().stream().map(TiersPayantParams::getTaux).reduce(0, Integer::sum);
            if (taux != newTaux) {
                return new JSONObject().put("success", false)
                        .put("msg", "Les taux sont différents:  Ancien taux : " + taux + " Nouveau taux: " + newTaux);
                
            }
            
            TPreenregistrement tp = updateVenteInfosClientOrtierspayant(salesParams);
            return new JSONObject().put("success", true)
                    .put("msg", "Opération effectuée avec succèss")
                    .put("refId", tp.getLgPREENREGISTREMENTID());
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new JSONObject().put("success", false).put("msg", "l'opération a échoué");
        }
    }
    
    @Override
    public JSONObject findVenteForUpdationg(String venteId) throws JSONException {
        try {
            TPreenregistrement tp = findOneById(venteId);
            MvtTransaction m = transaction(venteId, getEm()).get();
            TRemise remise = tp.getRemise();
            VenteRequest request = VenteRequest.builder()
                    .lgPREENREGISTREMENTID(venteId)
                    .ayantDroit(new AyantDroitDTO(tp.getAyantDroit()))
                    .client(new ClientDTO(tp.getClient()))
                    .dtUPDATED(tp.getDtUPDATED())
                    .intCUSTPART(tp.getIntCUSTPART())
                    .intPRICE(tp.getIntPRICE())
                    .intPRICEREMISE(tp.getIntPRICEREMISE())
                    .strREF(tp.getStrREF())
                    .strREFBON(tp.getStrREFBON())
                    .lgREMISEID((remise != null) ? remise.getLgREMISEID() : null)
                    .lgTYPEVENTEID(tp.getLgTYPEVENTEID().getLgTYPEVENTEID())
                    .strTYPEVENTE(tp.getLgTYPEVENTEID().getStrNAME())
                    .montantPaye(m.getMontantPaye())
                    .montantRegle(m.getMontantRegle())
                    .montantRestant(m.getMontantRestant())
                    .montantCredit(m.getMontantCredit())
                    .tierspayants(getTPreenregistrementCompteClientTiersPayent(venteId).stream().map(TiersPayantParams::new).collect(Collectors.toList()))
                    .build();
            return new JSONObject().put("data", new JSONObject(request)).put("success", true);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new JSONObject().put("data", new JSONObject()).put("success", false);
        }
    }
    
    private TPreenregistrement updateVenteInfosClientOrtierspayant(SalesParams salesParams) throws Exception {
        TPreenregistrement tp = getEm().find(TPreenregistrement.class, salesParams.getVenteId());
//        annulerVNO(salesParams.getUserId(), tp);
        updateVente(salesParams, tp);
//        List<TPreenregistrementDetail> list = getTPreenregistrementDetail(tp, getEm());
//        list.forEach(z -> {
//            cloneItem(z, _new, DateConverter.STATUT_IS_CLOSED);
//        });
        clonePreenregistrementTp(tp, salesParams, DateConverter.STATUT_IS_CLOSED);
//        cloneTransaction(transaction(salesParams.getVenteId(), getEm()).get(), _new);
//        mouvementProduitService.updateVenteStock(salesParams.getUserId(), list);

        return tp;
    }
    
    private TPreenregistrement cloneVente(SalesParams salesParams, TPreenregistrement tp, String statut) {
        TPreenregistrement _new = new TPreenregistrement();
        _new.setLgPREENREGISTREMENTID(UUID.randomUUID().toString());
        _new.setLgUSERID(salesParams.getUserId());
        _new.setIntPRICEREMISE(tp.getIntPRICEREMISE());
        _new.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        _new.setIntACCOUNT(tp.getIntACCOUNT());
        _new.setIntREMISEPARA(tp.getIntREMISEPARA());
        _new.setIntPRICE(tp.getIntPRICE());
        _new.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        _new.setIntCUSTPART(tp.getIntCUSTPART());
        _new.setMontantTva(tp.getMontantTva());
        _new.setDtCREATED(tp.getDtCREATED());
        _new.setDtUPDATED(tp.getDtUPDATED());
        _new.setLgPARENTID(tp.getLgPREENREGISTREMENTID());
        _new.setLgUSERVENDEURID(tp.getLgUSERVENDEURID());
        _new.setLgUSERCAISSIERID(tp.getLgUSERCAISSIERID());
        _new.setBISAVOIR(tp.getBISAVOIR());
        _new.setBISCANCEL(false);
        _new.setLgNATUREVENTEID(tp.getLgNATUREVENTEID());
        _new.setLgREMISEID(tp.getLgREMISEID());
        _new.setStrREFTICKET(DateConverter.getShortId(10));
        _new.setLgTYPEVENTEID(tp.getLgTYPEVENTEID());
        _new.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        _new.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        _new.setBWITHOUTBON(tp.getBWITHOUTBON());
        _new.setLgREGLEMENTID(tp.getLgREGLEMENTID());
        TClient client = findClient(salesParams.getClientId(), getEm());
        _new.setClient(client);
        _new.setStrFIRSTNAMECUSTOMER(client.getStrFIRSTNAME());
        _new.setStrLASTNAMECUSTOMER(client.getStrLASTNAME());
        _new.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        _new.setStrPHONECUSTOME(client.getStrADRESSE());
        _new.setStrINFOSCLT("");
        
        _new.setIntSENDTOSUGGESTION(0);
        _new.setPkBrand(tp.getPkBrand());
        
        findAyantDroit(salesParams.getAyantDroitId(), getEm()).ifPresent(a -> {
            _new.setAyantDroit(a);
            _new.setStrFIRSTNAMECUSTOMER(a.getStrFIRSTNAME());
            _new.setStrLASTNAMECUSTOMER(a.getStrLASTNAME());
            _new.setStrNUMEROSECURITESOCIAL(a.getStrNUMEROSECURITESOCIAL());
            
        });
        _new.setStrREFTICKET(DateConverter.getShortId(10));
        _new.setMedecin(tp.getMedecin());
        _new.setStrSTATUT(statut);
        _new.setLgPREENGISTREMENTANNULEID(tp.getLgPREENREGISTREMENTID());
//        String[] refArray = tp.getStrREF().split("_");
//        String ref = refArray[0] + "_" + StringUtils.leftPad(refArray[1], 6, '0');
        _new.setStrREF(tp.getStrREF());
        _new.setChecked(Boolean.TRUE);
        _new.setCopy(Boolean.FALSE);
        _new.setStrREFBON(salesParams.getTierspayants().get(0).getNumBon());
        getEm().persist(_new);
        return _new;
    }
    
    private TPreenregistrementDetail cloneItem(TPreenregistrementDetail tp, TPreenregistrement p, String statut) {
        TPreenregistrementDetail _new = new TPreenregistrementDetail();
        _new.setLgPREENREGISTREMENTDETAILID(UUID.randomUUID().toString());
        _new.setLgPREENREGISTREMENTID(p);
        _new.setIntPRICE(tp.getIntPRICE());
        _new.setIntQUANTITY(tp.getIntQUANTITY());
        _new.setIntQUANTITYSERVED(tp.getIntQUANTITYSERVED());
        _new.setMontantTva(tp.getMontantTva());
        _new.setDtCREATED(tp.getDtCREATED());
        _new.setStrSTATUT(statut);
        _new.setDtUPDATED(tp.getDtUPDATED());
        _new.setBoolACCOUNT(tp.getBoolACCOUNT());
        _new.setLgFAMILLEID(tp.getLgFAMILLEID());
        _new.setIntPRICEUNITAIR(tp.getIntPRICEUNITAIR());
        _new.setValeurTva(tp.getValeurTva());
        _new.setIntUG(tp.getIntUG());
        _new.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        _new.setIntPRICEDETAILOTHER(tp.getIntPRICEDETAILOTHER());
        _new.setIntFREEPACKNUMBER(tp.getIntFREEPACKNUMBER());
        _new.setIntPRICEREMISE(tp.getIntPRICEREMISE());
        _new.setIntAVOIR(tp.getIntAVOIR());
        _new.setIntAVOIRSERVED(tp.getIntQUANTITYSERVED());
        _new.setBISAVOIR(tp.getBISAVOIR());
        getEm().persist(_new);
        return _new;
    }
    
    private void clonePreenregistrementTp(TPreenregistrement old, SalesParams salesParams, String statut) throws Exception {
        List<TPreenregistrementCompteClientTiersPayent> newList = getTPreenregistrementCompteClientTiersPayent(old.getLgPREENREGISTREMENTID());
        List<TPreenregistrementCompteClientTiersPayent> array = new ArrayList<>();
        TClient client = old.getClient();
        ArrayList<TPreenregistrementDetail> list = items(old, getEm());
        int montant = old.getIntPRICE();
        int montantVariable = montant;
        for (TiersPayantParams b : salesParams.getTierspayants()) {
            TCompteClientTiersPayant payant = null;
            Optional<TCompteClientTiersPayant> op = findOneCompteClientTiersPayantById(b.getCompteTp());
            if (op.isPresent()) {
                payant = op.get();
                TPreenregistrementCompteClientTiersPayent opc = null;
                Optional<TPreenregistrementCompteClientTiersPayent> optc = getTPreenregistrementCompteClientTiersPayent(old.getLgPREENREGISTREMENTID(), b.getCompteTp());
                if (optc.isPresent()) {
                    opc = optc.get();
                    opc.setStrREFBON(b.getNumBon());
                    getEm().merge(opc);
                } else {
                    JSONObject json = calculVoNetAvecPlafondVente(old, montant, montantVariable, b.getTaux(), list);
                    montantVariable = json.getInt("reste");
                    opc = createNewPreenregistrementCompteClientTiersPayant(payant, json, old, salesParams.getUserId(), statut, b.getNumBon());
                }
                
                array.add(opc);
            } else {
                
                TTiersPayant p = getEm().find(TTiersPayant.class, b.getCompteTp());
                payant = clientService.updateOrCreateClientAssurance(client, p, b.getTaux());
                JSONObject json = calculVoNetAvecPlafondVente(old, montant, montantVariable, b.getTaux(), list);
                montantVariable = json.getInt("reste");
                createNewPreenregistrementCompteClientTiersPayant(payant, json, old, salesParams.getUserId(), statut, b.getNumBon());
            }
            
        }
        ListUtils.removeAll(newList, array).forEach(a -> {
            getEm().remove(a);
        });
        
    }
    
    public Optional<TPreenregistrementCompteClientTiersPayent> findOptionalCmtByNumBonAndVenteId(String numBon, String venteId) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> query = getEm().createQuery("SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID =?1 AND o.strREFBON=?2", TPreenregistrementCompteClientTiersPayent.class);
            query.setMaxResults(1);
            query.setParameter(1, venteId);
            query.setParameter(2, numBon);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public void cloneTransaction(MvtTransaction old, TPreenregistrement p) {
        MvtTransaction _new = new MvtTransaction();
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(p.getLgUSERID());
        _new.setCreatedAt(DateConverter.convertDateToLocalDateTime(p.getDtUPDATED()));
        _new.setPkey(p.getLgPREENREGISTREMENTID());
        _new.setMvtDate(DateConverter.convertDateToLocalDate(p.getDtUPDATED()));
        _new.setAvoidAmount(old.getAvoidAmount());
        _new.setMontant(old.getMontant());
        _new.setMagasin(old.getMagasin());
        _new.setCaisse(old.getCaisse());
        _new.setReference(p.getStrREF());
        _new.setMontantCredit(old.getMontantCredit());
        _new.setMontantVerse(old.getMontantVerse());
        _new.setMontantRegle(old.getMontantRegle());//09032020
        _new.setMontantPaye(old.getMontantPaye());
        _new.setMontantNet(old.getMontantNet());
        _new.settTypeMvtCaisse(old.gettTypeMvtCaisse());
        _new.setReglement(old.getReglement());
        _new.setMontantRestant(old.getMontantRestant());
        _new.setMontantRemise(old.getMontantRemise());
        _new.setMontantTva(old.getMontantTva());
        _new.setMarge(old.getMarge());
        _new.setCategoryTransaction(old.getCategoryTransaction());
        _new.setTypeTransaction(old.getTypeTransaction());
        _new.setChecked(old.getChecked());
        _new.setOrganisme(p.getClient().getLgCLIENTID());
        _new.setMontantAcc(old.getMontantAcc());
        getEm().persist(_new);
    }
    
    public JSONObject calculVoNetAvecPlafondVente(TPreenregistrement OTPreenregistrement, int montant, int montantVariable, int taux, ArrayList<TPreenregistrementDetail> list) {
        JSONObject tp = new JSONObject();
        try {
            
            Integer RemiseCarnet = 0, montantvente = 0;
            Integer totalTp = 0;
            
            MontantAPaye montantAPaye;
            
            TClient client = OTPreenregistrement.getClient();
            if (OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
                TRemise remise = OTPreenregistrement.getRemise();
                remise = remise != null ? remise : client.getRemise();
                if (remise != null) {
                    montantAPaye = getRemiseVno(OTPreenregistrement, remise, montant);
                    montantvente = montantAPaye.getMontant();
                    RemiseCarnet = montantAPaye.getRemise();
                } else {
                    montantAPaye = sumVenteSansRemise(list);
                    montantvente = montantAPaye.getMontant();
                    
                }
                
                Integer tpnet = montantvente - RemiseCarnet;
                totalTp = tpnet;
                tp.put("montanttp", totalTp);
                tp.put("taux", taux);
                tp.put("reste", 0);
                
                return tp;
                
            } else {
                
                TRemise remise = OTPreenregistrement.getRemise();
                if (remise != null) {
                    montantAPaye = getRemiseVno(OTPreenregistrement, remise, OTPreenregistrement.getIntPRICE());
                    montantvente = montantAPaye.getMontant();
                    montantVariable = montantvente;
                    RemiseCarnet = montantAPaye.getRemise();
                } else {
                    montantAPaye = sumVenteSansRemise(list);
                    montantvente = montantAPaye.getMontant();
                    montantVariable = montantvente;
                }
                
                Double montantTp = montantvente * (Double.valueOf(taux) / 100);
                Integer tpnet = (int) Math.ceil(montantTp);
                int _taux = 0;
                
                if (montantVariable > tpnet) {
                    montantVariable -= tpnet;
                    _taux = taux;
                } else if (montantVariable <= tpnet) {
                    tpnet = montantVariable;
                    _taux = (int) Math.ceil((montantTp * 100) / montantvente);
                }
                ;
                tp.put("montanttp", tpnet);
                tp.put("taux", _taux);
                tp.put("reste", montantVariable);
                
            }
            
            return tp;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject();
        }
    }
    
    private TPreenregistrement updateVente(SalesParams salesParams, TPreenregistrement _new) {
        _new.setLgUSERID(salesParams.getUserId());
        TClient client = findClient(salesParams.getClientId(), getEm());
        _new.setClient(client);
        _new.setStrFIRSTNAMECUSTOMER(client.getStrFIRSTNAME());
        _new.setStrLASTNAMECUSTOMER(client.getStrLASTNAME());
        _new.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        _new.setStrPHONECUSTOME(client.getStrADRESSE());
        _new.setStrINFOSCLT("");
        findAyantDroit(salesParams.getAyantDroitId(), getEm()).ifPresent(a -> {
            _new.setAyantDroit(a);
            _new.setStrFIRSTNAMECUSTOMER(a.getStrFIRSTNAME());
            _new.setStrLASTNAMECUSTOMER(a.getStrLASTNAME());
            _new.setStrNUMEROSECURITESOCIAL(a.getStrNUMEROSECURITESOCIAL());
        });
        _new.setStrREFBON(salesParams.getTierspayants().get(0).getNumBon());
        getEm().merge(_new);
        return _new;
    }
    
    public void annulerVenteAnterieur(TUser ooTUser, TPreenregistrement tp) throws Exception {
        EntityManager emg = this.getEm();
        final boolean checked = tp.getChecked();
        final boolean sameDate = true;
        try {
            
            List<TCashTransaction> cashTransactions = lstTCashTransaction(tp.getLgPREENREGISTREMENTID(), emg);
            Optional<TRecettes> oprectte = findRecette(tp.getLgPREENREGISTREMENTID(), emg);
            List<TPreenregistrementDetail> preenregistrementDetails = getTPreenregistrementDetail(tp, emg);
            String idVente = tp.getLgPREENREGISTREMENTID();
            TPreenregistrement _new = cloneVente(ooTUser, tp);
            LongAdder montantRestant = new LongAdder();
            findOptionalCmt(tp, emg).ifPresent(cp -> {
                montantRestant.add(cp.getIntPRICERESTE());
                cp.setIntPRICE(0);
                cp.setIntPRICERESTE(0);
                cp.setStrSTATUT(commonparameter.statut_delete);
                cp.setDtUPDATED(_new.getDtUPDATED());
                emg.merge(cp);
            });
            if (tp.getStrTYPEVENTE().equals("VO")) {
                clonePreenregistrementTp(_new, idVente, ooTUser);
                if (!cashTransactions.isEmpty()) {
                    for (TCashTransaction cashTransaction : cashTransactions) {
                        if (cashTransaction.getIntAMOUNT() > 0) {
                            addTransaction(ooTUser, cashTransaction, _new, tp, !sameDate ? sameDate : checked);
                        } else {
                            addTransactionCredit(ooTUser, cashTransaction, _new, tp, !sameDate ? sameDate : checked);
                        }
                    }
                    
                }
                
            } else {
                Optional<TCashTransaction> cashTransactio = cashTransactions.stream().findFirst();
                cashTransactio.ifPresent(cs -> {
                    addTransaction(ooTUser, cs, _new, tp, !sameDate ? sameDate : checked);
                    
                });
            }
            
            transaction(idVente, emg).ifPresent(tr -> {
                cloneMvtTransaction(ooTUser, tr, _new, tp);
            });
            
            oprectte.ifPresent(re -> {
                copyRecette(_new, re, ooTUser, emg);
            });
            
            findClientTiersPayents(tp.getLgPREENREGISTREMENTID(), emg).forEach(action -> {
                action.setStrSTATUT(commonparameter.statut_delete);
                emg.merge(action);
            });
            TEmplacement emplacement = ooTUser.getLgEMPLACEMENTID();
            final Typemvtproduit typemvtproduit = checked ? findById(DateConverter.ANNULATION_DE_VENTE) : findById(DateConverter.TMVTP_ANNUL_VENTE_DEPOT_EXTENSION);
            preenregistrementDetails.forEach((e) -> {
                TPreenregistrementDetail _newItem = createItemCopy(ooTUser, e, _new, emg);
                TFamille OTFamille = e.getLgFAMILLEID();
                updateNbreVenteApresAnnulation(OTFamille, ooTUser, _newItem.getIntQUANTITY(), emg);
                TFamilleStock familleStock = findStock(OTFamille.getLgFAMILLEID(), emplacement, emg);
                int initStock = familleStock.getIntNUMBERAVAILABLE();
                mouvementProduitService.saveMvtProduit(_newItem.getIntPRICEUNITAIR(), _newItem.getLgPREENREGISTREMENTDETAILID(),
                        typemvtproduit, OTFamille, ooTUser, emplacement,
                        _newItem.getIntQUANTITY(), initStock, initStock - _newItem.getIntQUANTITY(), emg, _newItem.getValeurTva(), checked);
                
                updateReelStockApresAnnulation(OTFamille, familleStock, ooTUser, _newItem.getIntQUANTITY(), emg);
                if (!tp.getPkBrand().isEmpty()) {
                    updateReelStockAnnulationDepot(OTFamille, _newItem.getIntQUANTITY(), tp.getPkBrand(), emg);
                    
                }
                
            });
            String desc = "Modification de la vente " + tp.getStrREF() + " montant : " + tp.getIntPRICE() + " par " + ooTUser.getStrFIRSTNAME() + " " + ooTUser.getStrLASTNAME();
            logService.updateItem(ooTUser, tp.getStrREF(), desc, TypeLog.MODIFICATION_INFO_VENTE, tp, _new.getDtUPDATED());
            
            sendMessageClientJmsQueue(_new.getLgPREENREGISTREMENTID());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
        }
    }
    
    public TPreenregistrement cloneVente(TUser ooTUser, TPreenregistrement tp) {
        TPreenregistrement _new = new TPreenregistrement();
        _new.setLgPREENREGISTREMENTID(UUID.randomUUID().toString());
        _new.setLgUSERID(ooTUser);
        _new.setIntPRICEREMISE((-1) * tp.getIntPRICEREMISE());
        _new.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        _new.setIntACCOUNT((-1) * tp.getIntACCOUNT());
        _new.setIntREMISEPARA((-1) * tp.getIntREMISEPARA());
        _new.setIntPRICE((-1) * tp.getIntPRICE());
        _new.setIntPRICEOTHER((-1) * tp.getIntPRICEOTHER());
        _new.setIntCUSTPART((-1) * tp.getIntCUSTPART());
        _new.setMontantTva((-1) * tp.getMontantTva());
        _new.setDtCREATED(tp.getDtUPDATED());
        _new.setDtUPDATED(tp.getDtUPDATED());
        _new.setLgPARENTID(tp.getLgPREENREGISTREMENTID());
        _new.setStrSTATUT(commonparameter.statut_is_Closed);
        _new.setLgUSERVENDEURID(tp.getLgUSERVENDEURID());
        _new.setLgUSERCAISSIERID(tp.getLgUSERCAISSIERID());
        _new.setBISAVOIR(tp.getBISAVOIR());
        _new.setBISCANCEL(tp.getBISCANCEL());
        _new.setLgNATUREVENTEID(tp.getLgNATUREVENTEID());
        _new.setLgREMISEID(tp.getLgREMISEID());
        _new.setStrREFTICKET(DateConverter.getShortId(10));
        _new.setLgTYPEVENTEID(tp.getLgTYPEVENTEID());
        _new.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        _new.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        _new.setStrFIRSTNAMECUSTOMER(tp.getStrFIRSTNAMECUSTOMER());
        _new.setStrREFBON(tp.getStrREFBON());
        _new.setStrPHONECUSTOME(tp.getStrPHONECUSTOME());
        _new.setStrLASTNAMECUSTOMER(tp.getStrLASTNAMECUSTOMER());
        _new.setStrNUMEROSECURITESOCIAL(tp.getStrNUMEROSECURITESOCIAL());
        _new.setStrINFOSCLT(tp.getStrINFOSCLT());
        _new.setIntSENDTOSUGGESTION(0);
        _new.setPkBrand(tp.getPkBrand());
        _new.setClient(tp.getClient());
        _new.setAyantDroit(tp.getAyantDroit());
        _new.setLgREGLEMENTID(tp.getLgREGLEMENTID());
        _new.setLgPREENGISTREMENTANNULEID(tp.getLgPREENREGISTREMENTID());
        _new.setMedecin(tp.getMedecin());
        _new.setStrREF(tp.getStrREF());
        tp.setBISCANCEL(true);
        tp.setDtANNULER(tp.getDtUPDATED());
        tp.setLgUSERID(ooTUser);
        _new.setChecked(Boolean.FALSE);
        tp.setChecked(Boolean.FALSE);
        getEm().merge(tp);
        getEm().persist(_new);
        return _new;
    }
    
    public void clonePreenregistrementTp(TPreenregistrement preenregistrement, String oldPreenregistrement, TUser o) {
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(oldPreenregistrement, getEm());
        for (TPreenregistrementCompteClientTiersPayent a : clientTiersPayents) {
            TCompteClientTiersPayant OTCompteClientTiersPayant = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent _new = new TPreenregistrementCompteClientTiersPayent();
            _new.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            _new.setLgPREENREGISTREMENTID(preenregistrement);
            _new.setIntPRICE(a.getIntPRICE() * (-1));
            _new.setLgUSERID(o);
            _new.setStrSTATUT(DateConverter.STATUT_DELETE);
            _new.setDtCREATED(a.getDtUPDATED());
            _new.setDtUPDATED(a.getDtUPDATED());
            _new.setLgCOMPTECLIENTTIERSPAYANTID(OTCompteClientTiersPayant);
            _new.setStrREFBON(a.getStrREFBON());
            _new.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            _new.setIntPERCENT(a.getIntPERCENT());
            _new.setIntPRICERESTE(0);
            _new.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            _new.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            getEm().persist(_new);
            
            TCompteClient OTCompteClient = OTCompteClientTiersPayant.getLgCOMPTECLIENTID();
            if (OTCompteClient != null && OTCompteClientTiersPayant.getDblPLAFOND() != null && OTCompteClientTiersPayant.getDblPLAFOND() != 0) {
                OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE((OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() : 0) + _new.getIntPRICE());
                OTCompteClientTiersPayant.setDtUPDATED(new Date());
                getEm().merge(OTCompteClientTiersPayant);
            }
            if (OTCompteClient != null && OTCompteClient.getDblPLAFOND() != null && OTCompteClient.getDblPLAFOND() != 0) {
                OTCompteClient.setDblQUOTACONSOMENSUELLE((OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + _new.getIntPRICE());
                OTCompteClient.setDtUPDATED(new Date());
                getEm().merge(OTCompteClient);
            }
        }
        
    }
    
    private void cloneMvtTransaction(TUser ooTUser, MvtTransaction cashTransaction, TPreenregistrement _newP, TPreenregistrement old) {
        cashTransaction.setChecked(Boolean.FALSE);
        getEm().merge(cashTransaction);
        addTransactionCopy(ooTUser, old.getLgUSERCAISSIERID(), _newP.getLgPREENREGISTREMENTID(), cashTransaction, getEm(), _newP.getStrREF(), cashTransaction.getCreatedAt(), cashTransaction.getMvtDate());
        
    }
    
    public Optional<TPreenregistrementCompteClientTiersPayent> checkChargedPreenregistrement(String idVente) {
        
        try {
            TPreenregistrementCompteClientTiersPayent list = (TPreenregistrementCompteClientTiersPayent) getEm().createQuery("SELECT p FROM TPreenregistrementCompteClientTiersPayent p WHERE p.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 AND p.strSTATUTFACTURE <> 'unpaid'").
                    setParameter(1, idVente).setMaxResults(1).
                    getSingleResult();
            return Optional.ofNullable(list);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Optional.empty();
            
        }
    }
    
    private Optional<TCompteClientTiersPayant> findOneCompteClientTiersPayantById(String id) {
        try {
            TCompteClientTiersPayant q = getEm().find(TCompteClientTiersPayant.class, id);
            return Optional.ofNullable(q);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private Optional<TPreenregistrementCompteClientTiersPayent> getTPreenregistrementCompteClientTiersPayent(String lg_PREENREGISTREMENT_ID, String lg_COMPTE_CLIENT_TIERS_PAYANT_ID) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q = getEm().createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 AND t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?2", TPreenregistrementCompteClientTiersPayent.class)
                    .setParameter(1, lg_PREENREGISTREMENT_ID).setParameter(2, lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
            q.setMaxResults(1);
            return Optional.ofNullable(q.getSingleResult());
            
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private TPreenregistrementCompteClientTiersPayent createNewPreenregistrementCompteClientTiersPayant(TCompteClientTiersPayant payant, JSONObject json, TPreenregistrement old, TUser user, String statut, String numBon) throws Exception {
        TPreenregistrementCompteClientTiersPayent _new = new TPreenregistrementCompteClientTiersPayent();
        _new.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
        _new.setLgPREENREGISTREMENTID(old);
        _new.setIntPRICE(json.getInt("montanttp"));
        _new.setLgUSERID(user);
        _new.setStrSTATUT(statut);
        _new.setDtCREATED(old.getDtCREATED());
        _new.setDtUPDATED(old.getDtUPDATED());
        _new.setLgCOMPTECLIENTTIERSPAYANTID(payant);
        _new.setStrREFBON(numBon);
        _new.setDblQUOTACONSOVENTE(0.0);
        _new.setIntPERCENT(json.getInt("taux"));
        _new.setIntPRICERESTE(_new.getIntPERCENT());
        _new.setStrSTATUTFACTURE("unpaid");
        getEm().persist(_new);
        TCompteClient OTCompteClient = payant.getLgCOMPTECLIENTID();
        if (OTCompteClient != null && payant.getDblPLAFOND() != null && payant.getDblPLAFOND() != 0) {
            payant.setDblQUOTACONSOMENSUELLE((payant.getDblQUOTACONSOMENSUELLE() != null ? payant.getDblQUOTACONSOMENSUELLE() : 0) + _new.getIntPRICE());
            payant.setDtUPDATED(old.getDtUPDATED());
            getEm().merge(payant);
        }
        if (OTCompteClient != null && OTCompteClient.getDblPLAFOND() != null && OTCompteClient.getDblPLAFOND() != 0) {
            OTCompteClient.setDblQUOTACONSOMENSUELLE((OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + _new.getIntPRICE());
            OTCompteClient.setDtUPDATED(new Date());
            getEm().merge(OTCompteClient);
        }
        return _new;
    }
    
    private Optional<Reference> getReferenceByDateAndEmplacementId(LocalDate ODate, String emplacementId, boolean isDevis) {
        try {
            TypedQuery<Reference> query = this.getEm().createNamedQuery("Reference.lastReference", Reference.class);
            query.setParameter("id", ODate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            query.setParameter("emplacement", emplacementId);
            query.setParameter("devis", isDevis);
            query.setMaxResults(1);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
//            e.printStackTrace(System.err);
            return Optional.empty();
        }
    }
    
    public Reference buildRefTmp(LocalDate ODate, TEmplacement emplacement) {
        Reference r = null;
        try {
            Optional<Reference> o = getReferenceByDateAndEmplacementId(ODate, emplacement.getLgEMPLACEMENTID(), false);
            if (o.isPresent()) {
                r = o.get();
                
            } else {
                r = new Reference().addEmplacement(emplacement).
                        id(ODate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).lastIntValue(0)
                        .reference(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_" + StringUtils.leftPad(String.valueOf(0), 5, '0'));
            }
            r.setLastIntTmpValue(r.getLastIntTmpValue() + 1);
            r.setReferenceTemp(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_" + StringUtils.leftPad(String.valueOf(r.getLastIntTmpValue()), 5, '0'));
            getEm().merge(r);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return r;
    }
    
    public Reference buildRef(LocalDate ODate, TEmplacement emplacement) {
        Reference r = null;
        try {
            Optional<Reference> o = getReferenceByDateAndEmplacementId(ODate, emplacement.getLgEMPLACEMENTID(), false);
            if (o.isPresent()) {
                r = o.get();
                r.setLastIntValue(r.getLastIntValue() + 1);
                r.setReference(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_" + StringUtils.leftPad(String.valueOf(r.getLastIntValue()), 5, '0'));
            } else {
                r = new Reference().addEmplacement(emplacement).
                        id(ODate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).lastIntValue(1)
                        .lastIntTmpValue(1)
                        .referenceTemp(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_" + StringUtils.leftPad(String.valueOf(1), 5, '0'))
                        .reference(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_" + StringUtils.leftPad(String.valueOf(1), 5, '0'));
            }
            getEm().merge(r);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return r;
    }
    
    public Reference buildRefDevis(LocalDate ODate, TEmplacement emplacement) {
        Reference r = null;
        try {
            Optional<Reference> o = getReferenceByDateAndEmplacementId(ODate, emplacement.getLgEMPLACEMENTID(), true);
            if (o.isPresent()) {
                r = o.get();
                r.setLastIntValue(r.getLastIntValue() + 1);
                r.setReference(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_" + StringUtils.leftPad(String.valueOf(r.getLastIntValue()), 5, '0'));
            } else {
                r = new Reference().addEmplacement(emplacement).
                        id(ODate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).lastIntValue(1)
                        .reference(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_" + StringUtils.leftPad(String.valueOf(1), 5, '0'));
            }
            r.setLastIntTmpValue(r.getLastIntValue());
            r.setReferenceTemp(r.getReference());
            getEm().merge(r);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return r;
    }
}
