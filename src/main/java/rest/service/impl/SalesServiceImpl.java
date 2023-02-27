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
import dal.HMvtProduit;
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
import dal.VenteExclus;
import dal.enumeration.Canal;
import dal.enumeration.CategoryTransaction;
import dal.enumeration.Statut;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;
import dal.enumeration.TypeTransaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    MedecinService medecinService;
    @EJB
    ClientService clientService;
    @EJB
    NotificationService notificationService;
    @EJB
    private TiersPayantExclusService payantExclusService;
    @EJB
    private CarnetAsDepotService carnetAsDepotService;
    private final java.util.function.Predicate<Optional<TParameters>> test = e -> {
        if (e.isPresent()) {
            return Integer.parseInt(e.get().getStrVALUE().trim()) == 1;
        }
        return false;
    };
    private TTiersPayant tiersPayant;

    public TTiersPayant getTiersPayant() {
        return tiersPayant;
    }

    public void setTiersPayant(TTiersPayant tiersPayant) {
        this.tiersPayant = tiersPayant;
    }

    public EntityManager getEm() {
        return em;
    }

    public void sendMessageMvtsStockQueue(String msg) {
        ctx.createProducer().send(mvtStock, msg);
    }

    void sendMessageClientJmsQueue(String venteId) {
        ctx.createProducer().send(clientjms, venteId);
    }

    public MvtTransaction addTransaction(TUser ooTUser, TPreenregistrement tp,
            Integer montant, Integer voidAmount, Integer montantNet, Integer montantVerse, Boolean checked,
            CategoryTransaction categoryTransaction, TypeTransaction typeTransaction,
            TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse,
            Integer montantPaye, Integer marge, int montantAcc, MontantAPaye data) throws Exception {
        MvtTransaction transactionNew = new MvtTransaction();
        int compare = montantNet.compareTo(montantVerse);
        Integer montantPaid;
        Integer montantRestant = 0;
        if (compare <= 0) {
            montantPaid = montantNet;
        } else {
            montantPaid = montantVerse;
            montantRestant = montantNet - montantVerse;
        }
        transactionNew.setUuid(UUID.randomUUID().toString());
        transactionNew.setUser(ooTUser);
        transactionNew.setCreatedAt(DateConverter.convertDateToLocalDateTime(tp.getDtUPDATED()));
        transactionNew.setMvtDate(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()));
        transactionNew.setPkey(tp.getLgPREENREGISTREMENTID());
        transactionNew.setAvoidAmount(voidAmount);
        transactionNew.setMontant(montant);
        transactionNew.setMagasin(ooTUser.getLgEMPLACEMENTID());
        transactionNew.setCaisse(ooTUser);
        transactionNew.setMontantCredit(0);
        transactionNew.setMontantVerse(montantVerse);
        transactionNew.setMontantRegle(montantPaid);
        transactionNew.setMontantNet(montantNet);
        transactionNew.settTypeMvtCaisse(tTypeMvtCaisse);
        transactionNew.setReglement(reglement);
        transactionNew.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        transactionNew.setMarge(marge);
        transactionNew.setReference(tp.getStrREF());
        transactionNew.setMontantPaye(montantPaye);
        transactionNew.setMontantRemise(montant - montantNet);
        transactionNew.setCategoryTransaction(categoryTransaction);
        transactionNew.setTypeTransaction(typeTransaction);
        transactionNew.setChecked(checked);
        transactionNew.setMontantTva(tp.getMontantTva());
        transactionNew.setMontantAcc(montantAcc);
        if (data != null) {
            transactionNew.setMontantnetug(data.getMontantNetUg());
            transactionNew.setMontantttcug(data.getMontantTtcUg());
            transactionNew.setMargeug(data.getMargeUg());
            transactionNew.setMontantTvaUg(data.getMontantTvaUg());
        }
        if (tp.getClient() != null) {
            transactionNew.setOrganisme(tp.getClient().getLgCLIENTID());
        }
        getEm().persist(transactionNew);
        return transactionNew;

    }

    public MvtTransaction addTransaction(TUser ooTUser, TPreenregistrement tp,
            Integer montant, Integer montantNet,
            Integer montantVerse, Boolean checked,
            TTypeReglement reglement,
            TTypeMvtCaisse tTypeMvtCaisse, Integer montantCredit,
            Integer montantPaye, Integer marge, boolean diff, String typeReglement) throws Exception {
        Integer montantClient = tp.getIntCUSTPART() - tp.getIntPRICEREMISE();
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
            montantPaid = montantPaye;

        }
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(DateConverter.convertDateToLocalDateTime(tp.getDtUPDATED()));
        _new.setMvtDate(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()));
        _new.setPkey(tp.getLgPREENREGISTREMENTID());
        _new.setAvoidAmount(tp.getIntACCOUNT());
        _new.setMontant(montant);
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(ooTUser);
        _new.setReference(tp.getStrREF());
        _new.setMontantCredit(montantCredit);
        _new.setMontantVerse(montantVerse);
        _new.setMontantRegle(montantPaid);//09032020
        _new.setMontantPaye(montantPaye);
        _new.setMontantNet(montantNet);
        _new.settTypeMvtCaisse(tTypeMvtCaisse);
        _new.setReglement(reglement);
        _new.setMontantRestant(0);
        _new.setPreenregistrement(tp);
        if (diff && typeReglement.equals(DateConverter.MODE_ESP)) {
            _new.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        }

        _new.setMontantRemise(tp.getIntPRICEREMISE());
        _new.setMontantTva(tp.getMontantTva());
        _new.setMarge(marge);
        _new.setCategoryTransaction(CategoryTransaction.CREDIT);
        _new.setTypeTransaction(TypeTransaction.VENTE_CREDIT);
        _new.setChecked(checked);
        if (tp.getClient() != null) {
            _new.setOrganisme(tp.getClient().getLgCLIENTID());
        }
        _new.setMontantAcc(tp.getIntACCOUNT());
        getEm().persist(_new);
        return _new;
    }

    public MvtTransaction addTransactionDepot(TUser ooTUser, TPreenregistrement tp,
            TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse, EntityManager emg,
            Integer marge, TClient client) {
        MvtTransaction mvtTransac = new MvtTransaction();
        mvtTransac.setUuid(UUID.randomUUID().toString());
        mvtTransac.setUser(ooTUser);
        mvtTransac.setCreatedAt(LocalDateTime.now());
        mvtTransac.setPkey(tp.getLgPREENREGISTREMENTID());
        mvtTransac.setMvtDate(LocalDate.now());
        mvtTransac.setAvoidAmount(tp.getIntPRICE());
        mvtTransac.setMontant(tp.getIntPRICE());
        mvtTransac.setMagasin(ooTUser.getLgEMPLACEMENTID());
        mvtTransac.setCaisse(ooTUser);
        mvtTransac.setMontantCredit(tp.getIntPRICE());
        mvtTransac.setMontantVerse(0);
        mvtTransac.setMontantRegle(0);
        mvtTransac.setMontantNet(tp.getIntPRICE());
        mvtTransac.settTypeMvtCaisse(tTypeMvtCaisse);
        mvtTransac.setReglement(reglement);
        mvtTransac.setMontantRestant(0);
        mvtTransac.setMarge(marge);
        mvtTransac.setReference(tp.getStrREF());
        mvtTransac.setMontantPaye(0);
        mvtTransac.setMontantRemise(tp.getIntPRICE() - tp.getIntPRICE());
        mvtTransac.setCategoryTransaction(CategoryTransaction.CREDIT);
        mvtTransac.setTypeTransaction(TypeTransaction.VENTE_CREDIT);
        mvtTransac.setChecked(false);
        mvtTransac.setMontantTva(tp.getMontantTva());
        mvtTransac.setPreenregistrement(tp);
        if (client != null) {
            mvtTransac.setOrganisme(client.getLgCLIENTID());
        }
        return mvtTransac;

    }

    public void addTransactionCopy(TUser ooTUser, TUser caisse,
            String pkey, MvtTransaction old, EntityManager emg, String ref, LocalDateTime localDateTime, LocalDate localDate) {
        MvtTransaction _new = new MvtTransaction();
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setPreenregistrement(old.getPreenregistrement());
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
        _new.setMarge((-1) * old.getMarge());
        _new.setMontantttcug((-1) * old.getMontantttcug());
        _new.setMontantnetug((-1) * old.getMontantnetug());
        _new.setMargeug((-1) * old.getMargeug());
        _new.setMontantTvaUg((-1) * old.getMontantTvaUg());
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
            _new.setPreenregistrement(_newP);
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
            _new.setMontantttcug((-1) * cashTransaction.getMontantttcug());
            _new.setMontantnetug((-1) * cashTransaction.getMontantnetug());
            _new.setMargeug((-1) * cashTransaction.getMargeug());
            _new.setMontantTvaUg((-1) * cashTransaction.getMontantTvaUg());
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
            TPreenregistrement newItem = createPreventeCopy(ooTUser, tp, emg);
            ref = newItem.getLgPREENREGISTREMENTID();
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
                copyPreenregistrementTp(newItem, idVente, ooTUser, emg);
                if (!cashTransactions.isEmpty()) {
                    Integer amo = 0;
                    String re = "";
                    for (TCashTransaction cashTransaction : cashTransactions) {
                        re = cashTransaction.getLgTYPEREGLEMENTID();
                        if (cashTransaction.getIntAMOUNT() > 0) {
                            amo = cashTransaction.getIntAMOUNT();
                            addTransaction(ooTUser, cashTransaction, newItem, tp, !sameDate ? sameDate : checked);
                        } else {
                            addTransactionCredit(ooTUser, cashTransaction, newItem, tp, !sameDate ? sameDate : checked);
                        }
                    }

                    if (!sameDate) {
                        createAnnulleSnapshot(tp, montantRestant.intValue(), amo, ooTUser, getEm().find(TTypeReglement.class, re));
                    }
                }
                findByVenteId(tp.getLgPREENREGISTREMENTID()).ifPresent(venteExclus -> {
                    venteExclus.setStatus(Statut.DELETE);
                    this.getEm().merge(venteExclus);
                });
            } else {
                Optional<TCashTransaction> cashTransactio = cashTransactions.stream().findFirst();
                cashTransactio.ifPresent(cs -> {
                    addTransaction(ooTUser, cs, newItem, tp, !sameDate ? sameDate : checked);

                    if (!sameDate) {
                        createAnnulleSnapshot(tp, montantRestant.intValue(), cs.getIntAMOUNT(), ooTUser, getEm().find(TTypeReglement.class, cs.getLgTYPEREGLEMENTID()));
                    }
                });
            }

            transaction(idVente, emg).ifPresent(tr -> {

                copyTransaction(ooTUser, tr, newItem, tp, emg);
                if (!checkResumeCaisse(tp.getLgUSERCAISSIERID(), emg).isPresent()) {
                    createAnnulationRecette(tp, tr, ooTUser);
                }
            });

            oprectte.ifPresent(re -> {
                copyRecette(newItem, re, ooTUser, emg);
            });

            findClientTiersPayents(tp.getLgPREENREGISTREMENTID(), emg).forEach(action -> {
                action.setStrSTATUT(commonparameter.statut_delete);
                action.setDtUPDATED(new Date());
                emg.merge(action);
                TTiersPayant p = action.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID();
                if (p.getToBeExclude() || p.getIsDepot()) {
                    payantExclusService.updateTiersPayantAccount(p, (-1) * action.getIntPRICE());
                }

            });
            TEmplacement emplacement = ooTUser.getLgEMPLACEMENTID();
            final Typemvtproduit typemvtproduit = checked ? findById(DateConverter.ANNULATION_DE_VENTE) : findById(DateConverter.TMVTP_ANNUL_VENTE_DEPOT_EXTENSION);
            preenregistrementDetails.forEach((e) -> {
                TPreenregistrementDetail newCopieItem = createItemCopy(ooTUser, e, newItem, emg);
                TFamille OTFamille = e.getLgFAMILLEID();
                updateNbreVenteApresAnnulation(OTFamille, ooTUser, newCopieItem.getIntQUANTITY(), emg);
                TFamilleStock familleStock = findStock(OTFamille.getLgFAMILLEID(), emplacement, emg);
                int initStock = familleStock.getIntNUMBERAVAILABLE();
                familleStock.setIntUG(familleStock.getIntUG() - newCopieItem.getIntUG());
                mouvementProduitService.saveMvtProduit(newCopieItem.getIntPRICEUNITAIR(), newCopieItem,
                        typemvtproduit, OTFamille, ooTUser, emplacement,
                        newCopieItem.getIntQUANTITY(), initStock, initStock - newCopieItem.getIntQUANTITY(), emg, newCopieItem.getValeurTva(), checked, e.getIntUG());

                updateReelStockApresAnnulation(OTFamille, familleStock, ooTUser, newCopieItem.getIntQUANTITY(), emg);
                if (!tp.getPkBrand().isEmpty()) {
                    updateReelStockAnnulationDepot(OTFamille, newCopieItem.getIntQUANTITY(), tp.getPkBrand(), emg);

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
            json.put("ref", newItem.getLgPREENREGISTREMENTID());
            sendMessageClientJmsQueue(newItem.getLgPREENREGISTREMENTID());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            json.put("success", false);
            json.put("msg", "Erreur annulation de la vente ");
            json.put("ref", ref);

        }
        return json;

    }

    private Optional<VenteExclus> findByVenteId(String venteId) {
        try {
            return Optional.ofNullable(this.getEm().createQuery("SELECT o FROM VenteExclus o WHERE o.preenregistrement.lgPREENREGISTREMENTID=?1", VenteExclus.class)
                    .setParameter(1, venteId)
                    .setMaxResults(1).getSingleResult());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
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
        TPreenregistrementDetail newItem = new TPreenregistrementDetail();
        newItem.setLgPREENREGISTREMENTDETAILID(UUID.randomUUID().toString());
        newItem.setLgPREENREGISTREMENTID(p);
        newItem.setIntPRICE((-1) * tp.getIntPRICE());
        newItem.setIntQUANTITY((-1) * tp.getIntQUANTITY());
        newItem.setIntQUANTITYSERVED((-1) * tp.getIntQUANTITYSERVED());
        newItem.setMontantTva((-1) * tp.getMontantTva());
        newItem.setDtCREATED(new Date());
        newItem.setStrSTATUT(commonparameter.statut_is_Closed);
        newItem.setDtUPDATED(new Date());
        newItem.setBoolACCOUNT(tp.getBoolACCOUNT());
        newItem.setLgFAMILLEID(tp.getLgFAMILLEID());
        newItem.setCmuPrice(tp.getCmuPrice());
        newItem.setIntPRICEUNITAIR(tp.getIntPRICEUNITAIR());
        newItem.setValeurTva(tp.getValeurTva());
        newItem.setIntUG((-1) * tp.getIntUG());
        newItem.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        newItem.setIntPRICEDETAILOTHER(tp.getIntPRICEDETAILOTHER());
        newItem.setIntFREEPACKNUMBER(tp.getIntFREEPACKNUMBER());
        newItem.setIntPRICEREMISE((-1) * tp.getIntPRICEREMISE());
        newItem.setIntAVOIR((-1) * tp.getIntAVOIR());
        newItem.setIntAVOIRSERVED((-1) * tp.getIntQUANTITYSERVED());
        newItem.setBISAVOIR(tp.getBISAVOIR());
        newItem.setPrixAchat(tp.getPrixAchat());
        newItem.setMontantTvaUg((-1) * tp.getMontantTvaUg());
        emg.persist(newItem);
        return newItem;
    }

    public TPreenregistrement createPreventeCopy(TUser ooTUser, TPreenregistrement tp, EntityManager emg) {
        TPreenregistrement newTp = new TPreenregistrement();
        newTp.setLgPREENREGISTREMENTID(UUID.randomUUID().toString());
        newTp.setLgUSERID(ooTUser);
        newTp.setIntPRICEREMISE((-1) * tp.getIntPRICEREMISE());
        newTp.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        newTp.setIntACCOUNT((-1) * tp.getIntACCOUNT());
        newTp.setIntREMISEPARA((-1) * tp.getIntREMISEPARA());
        newTp.setIntPRICE((-1) * tp.getIntPRICE());
        newTp.setIntPRICEOTHER((-1) * tp.getIntPRICEOTHER());
        newTp.setIntCUSTPART((-1) * tp.getIntCUSTPART());
        newTp.setMontantTva((-1) * tp.getMontantTva());
        newTp.setDtCREATED(new Date());
        newTp.setDtUPDATED(newTp.getDtCREATED());
        newTp.setLgPARENTID(tp.getLgPREENREGISTREMENTID());
        newTp.setStrSTATUT(commonparameter.statut_is_Closed);
        newTp.setLgUSERVENDEURID(tp.getLgUSERVENDEURID());
        newTp.setLgUSERCAISSIERID(tp.getLgUSERCAISSIERID());
        newTp.setBISAVOIR(tp.getBISAVOIR());
        newTp.setBISCANCEL(tp.getBISCANCEL());
        newTp.setLgNATUREVENTEID(tp.getLgNATUREVENTEID());
        newTp.setLgREMISEID(tp.getLgREMISEID());
        newTp.setStrREFTICKET(DateConverter.getShortId(10));
        newTp.setLgTYPEVENTEID(tp.getLgTYPEVENTEID());
        newTp.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        newTp.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        newTp.setStrFIRSTNAMECUSTOMER(tp.getStrFIRSTNAMECUSTOMER());
        newTp.setStrREFBON(tp.getStrREFBON());
        newTp.setStrPHONECUSTOME(tp.getStrPHONECUSTOME());
        newTp.setStrLASTNAMECUSTOMER(tp.getStrLASTNAMECUSTOMER());
        newTp.setStrNUMEROSECURITESOCIAL(tp.getStrNUMEROSECURITESOCIAL());
        newTp.setStrINFOSCLT(tp.getStrINFOSCLT());
        newTp.setIntSENDTOSUGGESTION(0);
        newTp.setPkBrand(tp.getPkBrand());
        newTp.setClient(tp.getClient());
        newTp.setAyantDroit(tp.getAyantDroit());
        newTp.setLgREGLEMENTID(tp.getLgREGLEMENTID());
        newTp.setLgPREENGISTREMENTANNULEID(tp.getLgPREENREGISTREMENTID());
        newTp.setMedecin(tp.getMedecin());
        newTp.setStrREF(buildRef(LocalDate.now(), ooTUser.getLgEMPLACEMENTID()).getReference());
        tp.setBISCANCEL(true);
        tp.setDtANNULER(tp.getDtCREATED());
        tp.setLgUSERID(ooTUser);
        newTp.setChecked(Boolean.FALSE);
        tp.setChecked(Boolean.FALSE);
        LocalDate dateVente = DateConverter.convertDateToLocalDate(tp.getDtCREATED());
        if (!dateVente.isEqual(LocalDate.now())) {
            newTp.setChecked(Boolean.TRUE);
        }
        newTp.setMargeug((-1) * tp.getMargeug());
        newTp.setMontantnetug((-1) * tp.getMontantnetug());
        newTp.setMontantttcug((-1) * tp.getMontantttcug());
        newTp.setMontantTvaUg((-1) * tp.getMontantTvaUg());
        emg.merge(tp);
        emg.persist(newTp);
        return newTp;
    }

    public void copyPreenregistrementTp(TPreenregistrement preenregistrement, String oldPreenregistrement, TUser o, EntityManager emg) {
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(oldPreenregistrement, emg);
        for (TPreenregistrementCompteClientTiersPayent a : clientTiersPayents) {
            TCompteClientTiersPayant OTCompteClientTiersPayant = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent newCtp = new TPreenregistrementCompteClientTiersPayent();
            newCtp.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            newCtp.setLgPREENREGISTREMENTID(preenregistrement);
            newCtp.setIntPRICE(a.getIntPRICE() * (-1));
            newCtp.setLgUSERID(o);
            newCtp.setStrSTATUT(DateConverter.STATUT_DELETE);
            newCtp.setDtCREATED(new Date());
            newCtp.setDtUPDATED(newCtp.getDtCREATED());
            newCtp.setLgCOMPTECLIENTTIERSPAYANTID(OTCompteClientTiersPayant);
            newCtp.setStrREFBON(a.getStrREFBON());
            newCtp.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            newCtp.setIntPERCENT(a.getIntPERCENT());
            newCtp.setIntPRICERESTE(0);
            newCtp.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            newCtp.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            emg.persist(newCtp);

            TCompteClient OTCompteClient = OTCompteClientTiersPayant.getLgCOMPTECLIENTID();
            if (OTCompteClient != null && OTCompteClientTiersPayant.getDblPLAFOND() != null && OTCompteClientTiersPayant.getDblPLAFOND() != 0) {
                OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE((OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() : 0) + newCtp.getIntPRICE());
                OTCompteClientTiersPayant.setDtUPDATED(new Date());
                emg.merge(OTCompteClientTiersPayant);
            }
            if (OTCompteClient != null && OTCompteClient.getDblPLAFOND() != null && OTCompteClient.getDblPLAFOND() != 0) {
                OTCompteClient.setDblQUOTACONSOMENSUELLE((OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + newCtp.getIntPRICE());
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

            return getEm().
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

    public Optional<TParameters> findParamettre(String KEY_PARAMETER) {
        try {
            return Optional.ofNullable(getEm().find(TParameters.class, KEY_PARAMETER));
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

    public void addTransaction(TUser ooTUser, TCashTransaction cashTransaction, TPreenregistrement newPreenregistrement, TPreenregistrement old, boolean checked) {
        TCashTransaction newTransaction = new TCashTransaction();
        newTransaction.setId(UUID.randomUUID().toString());
        newTransaction.setLgUSERID(ooTUser);
        newTransaction.setDtCREATED(newPreenregistrement.getDtUPDATED());
        newTransaction.setDtUPDATED(newPreenregistrement.getDtUPDATED());
        newTransaction.setIntACCOUNT((-1) * cashTransaction.getIntACCOUNT());
        newTransaction.setIntAMOUNT((-1) * cashTransaction.getIntAMOUNT());
        newTransaction.setIntAMOUNT2((-1) * cashTransaction.getIntAMOUNT2());
        newTransaction.setIntAMOUNTCREDIT(0);
        newTransaction.setIntAMOUNTDEBIT(cashTransaction.getIntAMOUNT());
        newTransaction.setStrREFFACTURE(newPreenregistrement.getLgPREENREGISTREMENTID());
        newTransaction.setStrDESCRIPTION("Annulation de vente ");
        newTransaction.setStrRESSOURCEREF(newTransaction.getStrREFFACTURE());
        newTransaction.setStrTASK("ANNULE_VENTE");
        newTransaction.setStrTYPEVENTE(newPreenregistrement.getStrTYPEVENTE());
        newTransaction.setStrTRANSACTIONREF("D");
        newTransaction.setBoolCHECKED(checked);
        newTransaction.setStrTYPE(cashTransaction.getStrTYPE());
        newTransaction.setCaissier(old.getLgUSERCAISSIERID());
        newTransaction.setLgTYPEREGLEMENTID(cashTransaction.getLgTYPEREGLEMENTID());
        newTransaction.setStrNUMEROCOMPTE(cashTransaction.getStrNUMEROCOMPTE());
        newTransaction.setLgREGLEMENTID(cashTransaction.getLgREGLEMENTID());
        newTransaction.setStrREFCOMPTECLIENT(cashTransaction.getStrREFCOMPTECLIENT());
        newTransaction.setStrTYPEVENTE(cashTransaction.getStrTYPEVENTE());
        newTransaction.setIntAMOUNTRECU((-1) * cashTransaction.getIntAMOUNTRECU());
        newTransaction.setIntAMOUNTREMIS(cashTransaction.getIntAMOUNTREMIS());
        getEm().persist(newTransaction);

    }

    public void addTransactionCredit(TUser ooTUser, TCashTransaction cashTransaction, TPreenregistrement newPreenregistrement, TPreenregistrement old, boolean checked) {

        Integer amount = cashTransaction.getIntAMOUNT();
        TCashTransaction newTransac = new TCashTransaction();
        newTransac.setId(UUID.randomUUID().toString());
        newTransac.setLgUSERID(ooTUser);
        newTransac.setDtCREATED(newPreenregistrement.getDtUPDATED());
        newTransac.setDtUPDATED(newPreenregistrement.getDtUPDATED());
        newTransac.setIntACCOUNT((-1) * cashTransaction.getIntACCOUNT());
        newTransac.setIntAMOUNT((-1) * cashTransaction.getIntAMOUNT());
        newTransac.setIntAMOUNT2((-1) * cashTransaction.getIntAMOUNT2());
        newTransac.setIntAMOUNTCREDIT((-1) * amount);
        newTransac.setIntAMOUNTDEBIT(cashTransaction.getIntAMOUNT());
        newTransac.setStrREFFACTURE(newPreenregistrement.getLgPREENREGISTREMENTID());
        newTransac.setStrDESCRIPTION("Annulation de vente ");
        newTransac.setBoolCHECKED(checked);
        newTransac.setStrRESSOURCEREF(newTransac.getStrREFFACTURE());
        newTransac.setStrTASK("ANNULE_VENTE");
        newTransac.setStrTYPEVENTE(newPreenregistrement.getStrTYPEVENTE());
        newTransac.setStrTRANSACTIONREF("D");
        newTransac.setCaissier(old.getLgUSERCAISSIERID());
        newTransac.setStrTYPE(cashTransaction.getStrTYPE());
        newTransac.setCaissier(old.getLgUSERCAISSIERID());
        newTransac.setLgTYPEREGLEMENTID(cashTransaction.getLgTYPEREGLEMENTID());
        newTransac.setStrNUMEROCOMPTE(cashTransaction.getStrNUMEROCOMPTE());
        newTransac.setLgREGLEMENTID(cashTransaction.getLgREGLEMENTID());
        newTransac.setStrREFCOMPTECLIENT(cashTransaction.getStrREFCOMPTECLIENT());
        newTransac.setStrTYPEVENTE(cashTransaction.getStrTYPEVENTE());
        newTransac.setIntAMOUNTRECU((-1) * cashTransaction.getIntAMOUNTRECU());
        newTransac.setIntAMOUNTREMIS(cashTransaction.getIntAMOUNTREMIS());
        getEm().persist(newTransac);

    }

    private void copyRecette(TPreenregistrement newPreen, TRecettes old, TUser o, EntityManager emg) {
        TRecettes tr = old;
        LOG.log(Level.INFO, "tr {0} ", new Object[]{tr});
        tr.setLgUSERID(o);
        tr.setDtCREATED(newPreen.getDtUPDATED());
        tr.setDtUPDATED(newPreen.getDtUPDATED());
        tr.setStrCREATEDBY(o.getStrLOGIN());
        tr.setStrREFFACTURE(newPreen.getLgPREENREGISTREMENTID());
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
            OTMouvement.setDtUPDATED(OTMouvement.getDtCREATED());
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
            TypedQuery<TFamilleStock> query = emg.createQuery("SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2", TFamilleStock.class);
            query.
                    setParameter(1, OTFamille);
            query.
                    setParameter(2, emplacement.getLgEMPLACEMENTID());
            query.setMaxResults(1);
            TFamilleStock familleStock = query.getSingleResult();

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
            if (!forcerStock(salesParams.getQte(), salesParams.getProduitId(), salesParams.getUserId().getLgEMPLACEMENTID())) {
                return json.put("success", false).put("msg", "Impossible de forcer le stock « voir le gestionnaire »");
            }
            TTypeVente OTTypeVente = typeVenteFromId(salesParams.getTypeVenteId(), emg);
            TNatureVente oTNatureVente = natureVenteFromId(salesParams.getNatureVenteId(), emg);
            TRemise OTRemise = remiseFromId(salesParams.getRemiseId(), emg);
            TUser vendeur = userFromId(salesParams.getUserVendeurId(), emg);
            TFamille tf = emg.find(TFamille.class, salesParams.getProduitId());
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
            OTPreenregistrement.setDtUPDATED(OTPreenregistrement.getDtCREATED());
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
            OTPreenregistrement.setStrSTATUT(salesParams.getStatut());
            TPreenregistrementDetail dp = addPreenregistrementItem(OTPreenregistrement, tf, salesParams.getQte(), salesParams.getQteServie(), salesParams.getQteUg(), salesParams.getItemPu(), emg);
            OTPreenregistrement.setCmuAmount(computeCmuAmount(dp));
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
                    emg.persist(OTPreenregistrement);
                    createPreenregistrementTierspayant(salesParams.getTierspayants(), OTPreenregistrement, emg);

                } else {
                    OTPreenregistrement.setStrREF(buildRefDevis(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID()).getReference());
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

    private boolean forcerStock(int qty, String familleId, TEmplacement em) {
        TFamilleStock familleStock = this.findStock(familleId, em, getEm());
        if (qty > familleStock.getIntNUMBERAVAILABLE()) {
            Optional<TParameters> o = findParamettre("FORCER_STOCK_VENTE");
            if (o.isEmpty()) {
                return true;
            } else {
                return Integer.valueOf(o.get().getStrVALUE()).compareTo(1) == 0;
            }
        } else {
            return true;
        }

    }

    @Override
    public JSONObject createPreVente(SalesParams salesParams) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            if (!forcerStock(salesParams.getQte(), salesParams.getProduitId(), salesParams.getUserId().getLgEMPLACEMENTID())) {
                return json.put("success", false).put("msg", "Impossible de forcer le stock « voir le gestionnaire »");
            }
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
            } else {
                findClientById(salesParams.getClientId()).ifPresent(my -> {
                    OTPreenregistrement.setClient(my);
                });
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
            OTPreenregistrement.setDtUPDATED(OTPreenregistrement.getDtCREATED());
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
            OTPreenregistrement.setCmuAmount(computeCmuAmount(dt));
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
    }

    private TTypeVente typeVenteFromId(String id, EntityManager emg) {
        if (id == null || "".equals(id)) {
            return null;
        }
        return emg.find(TTypeVente.class, id);
    }

    private TNatureVente natureVenteFromId(String id, EntityManager emg) {
        if (id == null || "".equals(id)) {
            return null;
        }
        return emg.find(TNatureVente.class, id);
    }

    public TPreenregistrementDetail addPreenregistrementItem(TPreenregistrement tp, TFamille OTFamille, int qte, int qteServie, int qteUg, Integer pu, EntityManager emg) {
        try {
            TCodeTva tva = OTFamille.getLgCODETVAID();
            Optional<TParameters> KEY_TAKE_INTO_ACCOUNT = findParamettre("KEY_TAKE_INTO_ACCOUNT");
            TPreenregistrementDetail tpd = new TPreenregistrementDetail(UUID.randomUUID().toString());
            tpd.setBoolACCOUNT(true);
            tpd.setCmuPrice(OTFamille.cmuPrice().get());
            tpd.setLgFAMILLEID(OTFamille);
            tpd.setDtCREATED(new Date());
            tpd.setDtUPDATED(tpd.getDtCREATED());
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
                if (Integer.parseInt(KEY_TAKE_INTO_ACCOUNT.get().getStrVALUE().trim()) == 1) {
                    if (!OTFamille.getLgZONEGEOID().getBoolACCOUNT() || !OTFamille.getBoolACCOUNT()) {
                        tpd.setBoolACCOUNT(false);
                    } else {
                        tp.setIntACCOUNT(tp.getIntACCOUNT() + tpd.getIntPRICE());
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
                int oldCmuAmount = computeCmuAmount(tpd);
                int qty = tpd.getIntQUANTITY() + params.getQte();
                if (!forcerStock(qty, params.getProduitId(), tp.getLgUSERID().getLgEMPLACEMENTID())) {
                    return json.put("success", false).put("msg", "Impossible de forcer le stock « voir le gestionnaire »");
                }
                Integer int_PRICE_OLD = tpd.getIntPRICE();
                Integer montantTva = tpd.getMontantTva();
                tpd.setIntFREEPACKNUMBER(0);
                tpd.setIntQUANTITY(qty);
                tpd.setIntPRICE(tpd.getIntPRICEUNITAIR() * tpd.getIntQUANTITY());
                tpd.setMontantTva(calculeTva(tva, tpd.getIntPRICE()));
                tpd.setIntQUANTITYSERVED(tpd.getIntQUANTITYSERVED() + params.getQteServie());
                tpd.setIntAVOIRSERVED(tpd.getIntQUANTITYSERVED());
                tpd.setIntAVOIR(tpd.getIntQUANTITY() - tpd.getIntQUANTITYSERVED());
                tpd.setDtUPDATED(new Date());
                tpd.setBISAVOIR(tpd.getIntAVOIR() > 0);
                tp.setIntPRICE(tp.getIntPRICE() + tpd.getIntPRICE() - int_PRICE_OLD);
                tp.setMontantTva(tp.getMontantTva() + tpd.getMontantTva() - montantTva);
                tp.setCmuAmount((tp.getCmuAmount() - oldCmuAmount) + computeCmuAmount(tpd));
                if (tpd.getBoolACCOUNT()) {
                    tp.setIntACCOUNT(tp.getIntPRICE());
                }
                emg.merge(tpd);
                afficheurProduit(tpd.getLgFAMILLEID().getStrNAME(), tpd.getIntQUANTITY(), tpd.getIntPRICEUNITAIR(), tpd.getIntPRICE());
            } else {
                if (!forcerStock(params.getQte(), params.getProduitId(), tp.getLgUSERID().getLgEMPLACEMENTID())) {
                    return json.put("success", false).put("msg", "Impossible de forcer le stock « voir le gestionnaire »");
                }
                TPreenregistrementDetail dp = addPreenregistrementItem(tp, famille, params.getQte(), params.getQteServie(), params.getQteUg(), params.getItemPu(), emg);
                tp.setCmuAmount(tp.getCmuAmount() + computeCmuAmount(dp));
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
            int oldCmuAmount = computeCmuAmount(detail);
            TFamille famille = detail.getLgFAMILLEID();
            TPreenregistrement tp = detail.getLgPREENREGISTREMENTID();
            if (!forcerStock(params.getQte(), famille.getLgFAMILLEID(), tp.getLgUSERID().getLgEMPLACEMENTID())) {
                return json.put("success", false).put("msg", "Impossible de forcer le stock « voir le gestionnaire »");
            }
            if (detail.getIntPRICEUNITAIR().compareTo(params.getItemPu()) != 0) {
                Optional<TParameters> p = findParamettre("KEY_CHECK_PRICE_UPDATE_AUTH");
                if (p.isPresent()) {
                    TParameters v = p.get();
                    int checkPersmission = Integer.parseInt(v.getStrVALUE());
                    if (checkPersmission == 1) {
                        if (!checkpricevente(famille, params.getItemPu(), emg)) {
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
            tp.setCmuAmount((tp.getCmuAmount() - oldCmuAmount) + computeCmuAmount(detail));
            if (params.isDepot()) {
                tp.setIntPRICEREMISE(calculRemiseDepot(tp.getIntPRICE(), params.getRemiseDepot()));

            }
            if (detail.getBoolACCOUNT()) {
                tp.setIntACCOUNT(tp.getIntACCOUNT() + (detail.getIntPRICE() - oldPrice));
            }
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
        eventLog.setDtUPDATED(eventLog.getDtCREATED());
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
            TPreenregistrementDetail tpd = emg.find(TPreenregistrementDetail.class, itemId);
            TPreenregistrement tp = tpd.getLgPREENREGISTREMENTID();
            tp.setIntPRICE(tp.getIntPRICE() - tpd.getIntPRICE());
            tp.setMontantTva(tp.getMontantTva() - tpd.getMontantTva());
            tp.setCmuAmount(tp.getCmuAmount() - computeCmuAmount(tpd));
            if (tpd.getBoolACCOUNT()) {
                tp.setIntACCOUNT(tp.getIntACCOUNT() - tpd.getIntPRICE());
            }
            emg.merge(tp);
            emg.remove(tpd);
            return tp;
        } catch (Exception e) {
            return null;

        }
    }

    public boolean checkIsVentePossible(TFamilleStock OTFamilleStock, int qte
    ) {
        return OTFamilleStock.getIntNUMBERAVAILABLE() >= qte;
    }

    private boolean boonDejaUtilise(String refBon, String cmpt) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q = getEm().createQuery("SELECT o FROM  TPreenregistrementCompteClientTiersPayent o WHERE o.strREFBON=?1 AND o.lgCOMPTECLIENTTIERSPAYANTID.lgTIERSPAYANTID.lgTIERSPAYANTID =?2 AND o.strSTATUT =?3 AND o.lgPREENREGISTREMENTID.strSTATUT=?4", TPreenregistrementCompteClientTiersPayent.class);
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
            int int_PRICE_COEF = (OTFamille.getIntPRICE() * Integer.valueOf(OTParameters.getStrVALUE())) / 100;
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
                tp.setStrFIRSTNAMECUSTOMER(OTAyantDroit.getStrFIRSTNAME());
                tp.setStrLASTNAMECUSTOMER(OTAyantDroit.getStrLASTNAME());
                tp.setDtUPDATED(new Date());
                tp.setAyantDroit(OTAyantDroit);
                emg.merge(tp);
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

    private int qtyUg(int stockUg, int qtyVendue) {
        int ugVendue = 0;
        try {
            if (qtyVendue <= stockUg) {
                ugVendue = qtyVendue;
            } else {
                ugVendue = stockUg;
            }

        } catch (Exception e) {
        }
        return ugVendue;
    }

    private int margeUg(int qteUg, int qtyVendue, int montantMage) {
        int margeUg = 0;
        try {
            margeUg = (qteUg * montantMage) / qtyVendue;
        } catch (Exception e) {
        }
        return margeUg;
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
        OTReglement.setDtUPDATED(OTReglement.getDtCREATED());
        OTReglement.setLgMODEREGLEMENTID(modeReglement);
        OTReglement.setDtREGLEMENT(OTReglement.getDtCREATED());
        OTReglement.setLgUSERID(user);
        OTReglement.setBoolCHECKED(true);
        OTReglement.setStrSTATUT(statut);
        emg.persist(OTReglement);
        return OTReglement;
    }

    public void addDiffere(TCompteClient OTCompteClient, TClient c, TPreenregistrement OTPreenregistrement, Integer int_PRICE, Integer reste, TUser user, EntityManager emg) {
        TPreenregistrementCompteClient oTPreenregistrementCompteClient = new TPreenregistrementCompteClient(UUID.randomUUID().toString());
        oTPreenregistrementCompteClient.setDtCREATED(new Date());
        oTPreenregistrementCompteClient.setDtUPDATED(oTPreenregistrementCompteClient.getDtCREATED());
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
        OTRecettes.setDtUPDATED(OTRecettes.getDtCREATED());
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
        cashTransaction.setDtUPDATED(cashTransaction.getDtCREATED());
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
            Optional<TParameters> KEY_TAKE_INTO_ACCOUNT = findParamettre("KEY_TAKE_INTO_ACCOUNT");
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
            JSONObject result = createPreenregistrementCompteClientTierspayant(clotureVenteParams.getTierspayants(), tp, clotureVenteParams.isSansBon(), tUser);
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
            }
            tp.setCompletionDate(new Date());
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()), clotureVenteParams.getUserId().getLgEMPLACEMENTID()).getReference());

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
            MvtTransaction mvtTransaction = addTransaction(tUser, tp, montant,
                    amount, clotureVenteParams.getMontantRecu(),
                    true,
                    findById(clotureVenteParams.getTypeRegleId(), emg),
                    typeMvtCaisse.get(), clotureVenteParams.getPartTP(),
                    clotureVenteParams.getMontantPaye(), clotureVenteParams.getMarge(), isDiff, clotureVenteParams.getTypeRegleId());

            emg.merge(tp);
            carnetAsDepotService.create(tp, mvtTransaction, this.getTiersPayant());
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
            return (Integer.parseInt(ordoncier.getStrVALUE()) == 1);
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
            if (isAvoir && tp.getClient() == null) {
                json.put("success", false);
                json.put("msg", "Ajouter un client à la vente");
                json.put("codeError", 0);
                json.put("codeError", 2);
                return json;
            }
            String statut = statutDiff(clotureVenteParams.getTypeRegleId());
            TUser vendeur = userFromId(clotureVenteParams.getUserVendeurId(), emg);
            Integer amount = montant - tp.getIntPRICEREMISE();
            TCompteClient compteClient = findByClientId(clotureVenteParams.getClientId(), emg);
            Optional<TParameters> KEY_TAKE_INTO_ACCOUNT = findParamettre("KEY_TAKE_INTO_ACCOUNT");
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
            updateUgData(clotureVenteParams.getData(), tp);
            if (!tp.getCopy()) {
                tp.setDtUPDATED(new Date());
            }
            tp.setCompletionDate(new Date());
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()), clotureVenteParams.getUserId().getLgEMPLACEMENTID()).getReference());
            java.util.function.Predicate<Optional<TParameters>> test = e -> {
                if (e.isPresent()) {
                    return Integer.parseInt(e.get().getStrVALUE().trim()) == 1;
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
            addTransaction(tUser,
                    tp, montant,
                    tp.getIntACCOUNT(), amount, clotureVenteParams.getMontantRecu(),
                    true, CategoryTransaction.CREDIT, TypeTransaction.VENTE_COMPTANT,
                    findById(clotureVenteParams.getTypeRegleId(), emg), typeMvtCaisse.get(),
                    clotureVenteParams.getMontantPaye(), clotureVenteParams.getMarge(), tp.getIntACCOUNT(), clotureVenteParams.getData());

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
                ex.printStackTrace(System.err);
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
        cashTransaction.setDtUPDATED(cashTransaction.getDtCREATED());
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
        cashTransaction.setDtUPDATED(cashTransaction.getDtCREATED());
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

    public JSONObject createPreenregistrementCompteClientTierspayant(List<TiersPayantParams> tierspayants, TPreenregistrement OTPreenregistrement, final boolean b_WITHOUT_BON, final TUser u) {
        JSONObject json = new JSONObject();
        List<TCompteClientTiersPayant> cmparray = new ArrayList<>();
        List<TiersPayantParams> tierspayantsData = tierspayants;

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
                    TCompteClientTiersPayant OTCompteClientTiersPayant = this.getEm().find(TCompteClientTiersPayant.class, params.getCompteTp());
                    TTiersPayant payant = OTCompteClientTiersPayant.getLgTIERSPAYANTID();
                    if (boonDejaUtilise(params.getNumBon(), payant.getLgTIERSPAYANTID()) && !OTPreenregistrement.getCopy()) {
                        try {
                            json.putOnce("success", false).putOnce("msg", "Le numéro de  <span style='color:red;font-weight:800;'> " + params.getNumBon() + " </span> est déjà utilisé par l'assureur :: " + payant.getStrFULLNAME());
                        } catch (JSONException ex) {
                        }
                    } else {
                        TPreenregistrementCompteClientTiersPayent item = getTPreenregistrementCompteClientTiersPayent(OTPreenregistrement.getLgPREENREGISTREMENTID(), OTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID(), this.getEm());
                        item.setDtUPDATED(OTPreenregistrement.getDtUPDATED());
                        item.setIntPERCENT(params.getTaux());
                        item.setIntPRICE(params.getTpnet());
                        item.setIntPRICERESTE(params.getTpnet());
                        item.setStrREFBON(params.getNumBon());
                        item.setDblQUOTACONSOVENTE(item.getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOVENTE() != null ? item.getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOVENTE() + item.getIntPRICE() : 0);
                        item.setStrSTATUT(commonparameter.statut_is_Closed);
                        item.setLgUSERID(u);
                        if (params.isPrincipal() || (tierspayants.size() == 1) || OTCompteClientTiersPayant.getBISRO() || (OTCompteClientTiersPayant.getIntPRIORITY() == 1)) {
                            OTPreenregistrement.setStrREFBON(params.getNumBon());
                        }
                        this.getEm().merge(item);
                        cmparray.add(OTCompteClientTiersPayant);
                        if (payant.getToBeExclude() || payant.getIsDepot()) {
                            this.setTiersPayant(payant);
                            payantExclusService.updateTiersPayantAccount(payant, item.getIntPRICE());
                        }

                    }

                });
                if (StringUtils.isBlank(OTPreenregistrement.getStrREFBON())) {
                    cmparray.sort(Comparator.comparing(TCompteClientTiersPayant::getIntPRIORITY));
                    tierspayantsData.stream().filter(ob -> ob.getCompteTp().equals(cmparray.get(0).getLgCOMPTECLIENTTIERSPAYANTID())).findFirst().ifPresent(t -> OTPreenregistrement.setStrREFBON(t.getNumBon()));
                }
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
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaUpdate<TPreenregistrementDetail> cq = cb.createCriteriaUpdate(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            cq
                    .set(root.get(TPreenregistrementDetail_.bISAVOIR), false)
                    .set(root.get(TPreenregistrementDetail_.intAVOIR), 0)
                    .set(root.get(TPreenregistrementDetail_.intAVOIRSERVED), root.get(TPreenregistrementDetail_.intQUANTITY))
                    .set(root.get(TPreenregistrementDetail_.intQUANTITYSERVED), root.get(TPreenregistrementDetail_.intQUANTITY))
                    .where(cb.and(cb.equal(root.get(TPreenregistrementDetail_.bISAVOIR), true), cb.equal(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID).get(TPreenregistrement_.lgPREENREGISTREMENTID), lg_PREENREGISTREMENT_ID)));
            emg.createQuery(cq).executeUpdate();
            preenregistrement.setCompletionDate(new Date());
            preenregistrement.setBISAVOIR(false);
            emg.merge(preenregistrement);
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
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
            OPreenregistrement.setDtUPDATED(new Date());
            OPreenregistrement.setStrREFBON(str_REF_BON);
            TPreenregistrement preenregistrement = OPreenregistrement.getLgPREENREGISTREMENTID();
            preenregistrement.setStrREFBON(str_REF_BON);
            emg.merge(preenregistrement);
            emg.merge(OPreenregistrement);

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
    public JSONObject closeventeBon(String lg_PREENREGISTREMENT_ID) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
            preenregistrement.setDtUPDATED(new Date());
            preenregistrement.setCompletionDate(preenregistrement.getDtUPDATED());
            preenregistrement.setBWITHOUTBON(false);
            emg.merge(preenregistrement);
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
    public JSONObject addtierspayant(SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, params.getVenteId());
            TCompteClientTiersPayant clientTiersPayant = emg.find(TCompteClientTiersPayant.class, params.getTierspayants().get(0).getCompteTp());
            TPreenregistrementCompteClientTiersPayent clientTiersPayent
                    = new TPreenregistrementCompteClientTiersPayent(UUID.randomUUID().toString());
            clientTiersPayent.setLgUSERID(params.getUserId());
            clientTiersPayent.setDblQUOTACONSOVENTE(0.0);
            clientTiersPayent.setDtCREATED(new Date());
            clientTiersPayent.setDtUPDATED(clientTiersPayent.getDtCREATED());
            clientTiersPayent.setLgPREENREGISTREMENTID(preenregistrement);
            clientTiersPayent.setLgCOMPTECLIENTTIERSPAYANTID(clientTiersPayant);
            clientTiersPayent.setIntPRICE(0);
            clientTiersPayent.setIntPERCENT(params.getTierspayants().get(0).getTaux());
            clientTiersPayent.setStrSTATUT(commonparameter.statut_is_Process);
            clientTiersPayent.setStrSTATUTFACTURE("unpaid");
            emg.persist(clientTiersPayent);
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
    public JSONObject addtierspayant(String venteId, SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, venteId);
            TCompteClientTiersPayant clientTiersPayant = emg.find(TCompteClientTiersPayant.class, params.getTypeVenteId());
            TPreenregistrementCompteClientTiersPayent clientTiersPayent
                    = new TPreenregistrementCompteClientTiersPayent(UUID.randomUUID().toString());
            clientTiersPayent.setLgUSERID(preenregistrement.getLgUSERID());
            clientTiersPayent.setDblQUOTACONSOVENTE(0.0);
            clientTiersPayent.setDtCREATED(new Date());
            clientTiersPayent.setDtUPDATED(clientTiersPayent.getDtCREATED());
            clientTiersPayent.setLgPREENREGISTREMENTID(preenregistrement);
            clientTiersPayent.setLgCOMPTECLIENTTIERSPAYANTID(clientTiersPayant);
            clientTiersPayent.setIntPRICE(0);
            clientTiersPayent.setIntPRICERESTE(0);
            clientTiersPayent.setIntPERCENT(params.getQte());
            clientTiersPayent.setStrSTATUT(commonparameter.statut_is_Process);
            clientTiersPayent.setStrSTATUTFACTURE("unpaid");
            emg.persist(clientTiersPayent);
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
    public JSONObject removetierspayant(String comptClientTpId, String venteId) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrementCompteClientTiersPayent op = (TPreenregistrementCompteClientTiersPayent) emg.createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?1 AND t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?2")
                    .setParameter(1, comptClientTpId).setParameter(2, venteId).getSingleResult();
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
            if (!params.isCheckUg()) {
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
                return json;
            } else {
                return shownetpayVnoCheckUg(params);
            }

        } catch (Exception e) {

            return json.put("success", false).put("msg", "Erreur::: L'Opération n'a pas aboutie");
        }

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
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, params.getVenteId());
            preenregistrement.setLgREMISEID(params.getRemiseId());
            preenregistrement.setRemise(findTRemise(params.getRemiseId(), emg));
            emg.merge(preenregistrement);
            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
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
            return null;
        }

    }

    public MontantAPaye calculVoNet(TPreenregistrement OTPreenregistrement, List<TiersPayantParams> tierspayants, EntityManager emg) {
        try {
            ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(OTPreenregistrement, emg);
            int remiseCarnet = 0;
            int montantvente;
            int totalTp = 0;
            int totalTaux = 0;
            int montantVariable;
            int diffMontantTotalAndCmuAmount = 0;
            int cmuAmount = 0;
            boolean isCmu=tierspayants.stream().allMatch(TiersPayantParams::isCmu);
          
            MontantAPaye montantAPaye;
            List<TiersPayantParams> resultat = new ArrayList<>();
            if (OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
                totalTaux = 100;
                TRemise remise = OTPreenregistrement.getRemise();
                remise = remise != null ? remise : OTPreenregistrement.getClient().getRemise();
                if (remise != null) {
                    montantAPaye = getRemiseVno(OTPreenregistrement, remise, OTPreenregistrement.getIntPRICE());
                    montantvente = montantAPaye.getMontant();
                    remiseCarnet = montantAPaye.getRemise();
                } else {
                    montantAPaye = sumVenteSansRemise(lstTPreenregistrementDetail);
                    montantvente = montantAPaye.getMontant();

                }

                TiersPayantParams tp = new TiersPayantParams();
                Integer tpnet = montantvente - remiseCarnet;
                totalTp += tpnet;
                tp.setCompteTp(tierspayants.get(0).getCompteTp());
                tp.setNumBon(tierspayants.get(0).getNumBon());
                tp.setTpnet(tpnet);
                tp.setDiscount(remiseCarnet);
                tp.setTaux(100);
                resultat.add(tp);
            } else {
                TRemise remise = OTPreenregistrement.getRemise();

                if (remise != null) {
                    montantAPaye = getRemiseVno(OTPreenregistrement, remise, OTPreenregistrement.getIntPRICE());
                  
                    cmuAmount= isCmu? montantAPaye.getCmuAmount():0;
                    //  montantvente = montantAPaye.getMontant();
                    montantvente = cmuAmount > 0 ? cmuAmount : montantAPaye.getMontant();
                    montantVariable = montantvente;
                    remiseCarnet = montantAPaye.getRemise();

                } else {
                    montantAPaye = sumVenteSansRemise(lstTPreenregistrementDetail);
                      cmuAmount=isCmu?montantAPaye.getCmuAmount():0;
                    montantvente = cmuAmount > 0 ? cmuAmount : montantAPaye.getMontant();
                    montantVariable = montantvente;
                }
                diffMontantTotalAndCmuAmount = montantAPaye.getMontant() - cmuAmount;
                for (TiersPayantParams tierspayant : tierspayants) {
                    TiersPayantParams tp = new TiersPayantParams();
                    Integer taux = tierspayant.getTaux();
                    Double montantTp = montantvente * (Double.valueOf(taux) / 100);
                    Integer tpnet = (int) Math.ceil(montantTp);
                    int thatTaux = 0;
                    if (montantVariable > tpnet) {
                        montantVariable -= tpnet;
                        thatTaux = taux;
                        totalTaux += thatTaux;
                    } else if (montantVariable <= tpnet) {
                        tpnet = montantVariable;
                        thatTaux = 100 - totalTaux;
                        totalTaux += thatTaux;

                    }
                    totalTp += tpnet;
                    tp.setTaux(thatTaux);
                    tp.setCompteTp(tierspayant.getCompteTp());
                    tp.setNumBon(tierspayant.getNumBon());
                    tp.setTpnet(tpnet);
                    tp.setDiscount(0);
                    resultat.add(tp);
                }

            }
            Integer netCustomer = (montantvente - totalTp) - remiseCarnet + (diffMontantTotalAndCmuAmount != montantvente ? diffMontantTotalAndCmuAmount : 0);
            if (totalTaux >= 100) {
                netCustomer = 0;
            }
            int finalSaleAmount=diffMontantTotalAndCmuAmount!=montantvente?montantvente + diffMontantTotalAndCmuAmount:montantvente;
            MontantAPaye map = new MontantAPaye(netCustomer, finalSaleAmount, totalTp,
                    remiseCarnet, montantAPaye.getMarge(), montantAPaye.getMontantTva());
            map.setTierspayants(resultat);
            map.setCmuAmount(cmuAmount);
            return map;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new MontantAPaye();
        }
    }

    private MontantAPaye getRemiseVno(TPreenregistrement OTPreenregistrement, TRemise OTRemise, Integer para) {
        Integer int_TOTAL_REMISE, int_REMISE_PARA = 0, montantNet;
        LongAdder totalRemise = new LongAdder();
        LongAdder totalRemisePara = new LongAdder();
        LongAdder totalAmount = new LongAdder();
        LongAdder marge = new LongAdder();
        LongAdder montantTva = new LongAdder();
        LongAdder montantAccount = new LongAdder();
         LongAdder montantCMU = new LongAdder();
        ArrayList<TPreenregistrementDetail> lstTPreenregistrementDetail = items(OTPreenregistrement, getEm());
        lstTPreenregistrementDetail.forEach(x -> {
            totalAmount.add(x.getIntPRICE());
            montantCMU.add(x.getCmuPrice());
            montantTva.add(x.getMontantTva());
            TFamille famille = x.getLgFAMILLEID();
            Integer remise = 0;
            if (!StringUtils.isEmpty(famille.getStrCODEREMISE()) && !famille.getStrCODEREMISE().equals("2") && !famille.getStrCODEREMISE().equals("3")) {
                TGrilleRemise grilleRemise = grilleRemiseRemiseFromWorkflow(x.getLgPREENREGISTREMENTID(), famille, OTRemise.getLgREMISEID());
                if (grilleRemise != null) {
                    remise = (int) ((x.getIntPRICE() * grilleRemise.getDblTAUX()) / 100);
                    if (!x.getBoolACCOUNT()) {
                        totalRemisePara.add(remise);
                    }
                    totalRemise.add(remise);
                    x.setLgGRILLEREMISEID(grilleRemise.getLgGRILLEREMISEID());
                }

            }

            x.setIntPRICEREMISE(remise);
            getEm().merge(x);
            if (x.getLgFAMILLEID().getBoolACCOUNT()) {
                int thatMarge = (x.getIntPRICE() - remise - x.getMontantTva()) - (x.getIntQUANTITY() * famille.getIntPAF());
                marge.add(thatMarge);
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
                montantTotal, 0, DateConverter.arrondiModuloOfNumber(int_TOTAL_REMISE, 5), marge.intValue(), tva).cmuAmount(montantAccount.intValue());
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
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()), tp.getLgUSERVENDEURID().getLgEMPLACEMENTID()).getReference());
            tp.setIntACCOUNT(tp.getIntPRICE());
            tp.setIntPRICEOTHER(tp.getIntACCOUNT());
            tp.setCompletionDate(new Date());
            cloturerItemsVente(tp.getLgPREENREGISTREMENTID(), emg);
            MvtTransaction mt = addTransactionDepot(tUser, tp,
                    findById(DateConverter.MODE_ESP, emg),
                    typeMvtCaisse.get(),
                    emg, clotureVenteParams.getMarge(), client.orElse(null));
            emg.persist(mt);
            emg.merge(tp);
            addtransactionAssurance(typeMvtCaisse, tp, false, (-1) * tp.getIntPRICE(), compteClient, tReglement, clotureVenteParams.getTypeRegleId(), tUser, clotureVenteParams, emg);
            mvtProduitService.updateVenteStockDepot(tp, lstTPreenregistrementDetail, emg, emplacement);
            json.put("success", true).put("msg", "Opération effectuée avec success").put("ref", tp.getLgPREENREGISTREMENTID());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Erreur: Echec de validation de la vente");
        }
        return json;
    }

    private void updateUgData(MontantAPaye data, TPreenregistrement p) {
        if (data != null) {
            p.setMargeug(data.getMargeUg());
            p.setMontantnetug(data.getMontantNetUg());
            p.setMontantttcug(data.getMontantTtcUg());
            p.setMontantTvaUg(data.getMontantTvaUg());
        }
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
            tp.setCompletionDate(tp.getDtUPDATED());
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(commonparameter.statut_is_Closed);
            tp.setStrSTATUTVENTE(statut);
            tp.setIntPRICE(montant);
            tp.setIntPRICEOTHER(montant);
            updateUgData(clotureVenteParams.getData(), tp);
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()), clotureVenteParams.getUserId().getLgEMPLACEMENTID()).getReference());
            cloturerItemsVente(tp.getLgPREENREGISTREMENTID(), emg);
            addtransactionComptant(typeMvtCaisse, tp, false, clotureVenteParams.getMontantPaye(), compteClient, clotureVenteParams.getMontantRemis(), clotureVenteParams.getMontantRecu(), tReglement, clotureVenteParams.getTypeRegleId(), clotureVenteParams.getUserId(), emg);
            addRecette(clotureVenteParams.getMontantPaye(), "Vente VNO", tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getUserId(), emg);
            MvtTransaction mvtTransaction = addTransaction(tUser,
                    tp, montant,
                    tp.getIntACCOUNT(), amount, clotureVenteParams.getMontantRecu(),
                    true, CategoryTransaction.CREDIT, TypeTransaction.VENTE_COMPTANT,
                    findById(clotureVenteParams.getTypeRegleId(), emg), typeMvtCaisse.get(),
                    clotureVenteParams.getMontantPaye(), clotureVenteParams.getMarge(), tp.getIntACCOUNT(), clotureVenteParams.getData());
            mvtProduitService.updateVenteStockDepot(tp, lstTPreenregistrementDetail, emg, emplacement);
            mvtTransaction.setPreenregistrement(tp);
            emg.persist(mvtTransaction);
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
        } catch (JSONException e) {
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
                    = getEm().createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 ORDER BY t.lgCOMPTECLIENTTIERSPAYANTID.intPRIORITY ASC ", TPreenregistrementCompteClientTiersPayent.class)
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
        TPreenregistrement newTP = new TPreenregistrement();
        newTP.setLgPREENREGISTREMENTID(UUID.randomUUID().toString());
        newTP.setLgUSERID(ooTUser);
        newTP.setIntPRICEREMISE(tp.getIntPRICEREMISE());
        newTP.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        newTP.setIntACCOUNT(tp.getIntACCOUNT());
        newTP.setIntREMISEPARA(tp.getIntREMISEPARA());
        newTP.setIntPRICE(tp.getIntPRICE());
        newTP.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        newTP.setIntCUSTPART(tp.getIntCUSTPART());
        newTP.setMontantTva(tp.getMontantTva());
        newTP.setDtCREATED(tp.getDtCREATED());
        newTP.setDtUPDATED(tp.getDtUPDATED());
        newTP.setCompletionDate(tp.getDtUPDATED());
        newTP.setLgPARENTID(tp.getLgPREENREGISTREMENTID());
        newTP.setLgUSERVENDEURID(tp.getLgUSERVENDEURID());
        newTP.setLgUSERCAISSIERID(tp.getLgUSERCAISSIERID());
        newTP.setBISAVOIR(tp.getBISAVOIR());
        newTP.setBISCANCEL(false);
        newTP.setLgNATUREVENTEID(tp.getLgNATUREVENTEID());
        newTP.setLgREMISEID(tp.getLgREMISEID());
        newTP.setStrREFTICKET(DateConverter.getShortId(10));
        newTP.setLgTYPEVENTEID(tp.getLgTYPEVENTEID());
        newTP.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        newTP.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        newTP.setStrFIRSTNAMECUSTOMER(tp.getStrFIRSTNAMECUSTOMER());
        newTP.setStrREFBON(tp.getStrREFBON());
        newTP.setStrPHONECUSTOME(tp.getStrPHONECUSTOME());
        newTP.setStrLASTNAMECUSTOMER(tp.getStrLASTNAMECUSTOMER());
        newTP.setStrNUMEROSECURITESOCIAL(tp.getStrNUMEROSECURITESOCIAL());
        newTP.setStrINFOSCLT(tp.getStrINFOSCLT());
        newTP.setIntSENDTOSUGGESTION(0);
        newTP.setPkBrand(tp.getPkBrand());
        newTP.setClient(tp.getClient());
        newTP.setAyantDroit(tp.getAyantDroit());
        newTP.setMedecin(tp.getMedecin());
        newTP.setStrSTATUT(DateConverter.STATUT_PROCESS);
        newTP.setLgPREENGISTREMENTANNULEID(tp.getLgPREENREGISTREMENTID());
        newTP.setStrREF(buildRefTmp(LocalDate.now(), ooTUser.getLgEMPLACEMENTID()).getReferenceTemp());
        newTP.setStrREF(tp.getStrREF());
        newTP.setChecked(Boolean.TRUE);
        newTP.setCopy(Boolean.TRUE);
        newTP.setMargeug(tp.getMargeug());
        newTP.setMontantnetug(tp.getMontantnetug());
        newTP.setMontantttcug(tp.getMontantttcug());
        newTP.setMontantTvaUg(tp.getMontantTvaUg());
        newTP.setCmuAmount(tp.getCmuAmount());
        getEm().persist(newTP);
        return newTP;
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
            TPreenregistrement newTP = createPreventeCopy(u, tp);
            getTPreenregistrementDetail(tp, getEm()).forEach(z -> {
                createItemCopy(z, newTP);
            });
            copyPreenregistrementTp(newTP, venteId, u);
            findOptionalCmt(tp, getEm()).ifPresent(cp -> {
                addDiffere(newTP, cp);
            });

            data.put("lgPREENREGISTREMENTID", newTP.getLgPREENREGISTREMENTID());
            data.put("strREF", newTP.getStrREF());
            data.put("intPRICE", newTP.getIntPRICE());
            data.put("intPRICEREMISE", newTP.getIntPRICEREMISE());
            try {
                tp.setCompletionDate(new Date());
                getEm().merge(tp);
            } catch (Exception e) {
            }
            return json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
        } catch (Exception e) {
           
            return json.put("success", false).put("msg", "Erreur :: l'opération a échouée");
        }
    }

    private TPreenregistrementDetail createItemCopy(TPreenregistrementDetail tp, TPreenregistrement p) {
        TPreenregistrementDetail newTd = new TPreenregistrementDetail();
        newTd.setLgPREENREGISTREMENTDETAILID(UUID.randomUUID().toString());
        newTd.setLgPREENREGISTREMENTID(p);
        newTd.setIntPRICE(tp.getIntPRICE());
        newTd.setIntQUANTITY(tp.getIntQUANTITY());
        newTd.setIntQUANTITYSERVED(tp.getIntQUANTITYSERVED());
        newTd.setMontantTva(tp.getMontantTva());
        newTd.setDtCREATED(new Date());
        newTd.setStrSTATUT(DateConverter.STATUT_PROCESS);
        newTd.setDtUPDATED(new Date());
        newTd.setBoolACCOUNT(tp.getBoolACCOUNT());
        newTd.setLgFAMILLEID(tp.getLgFAMILLEID());
        newTd.setIntPRICEUNITAIR(tp.getIntPRICEUNITAIR());
        newTd.setValeurTva(tp.getValeurTva());
        newTd.setIntUG(tp.getIntUG());
        newTd.setMontantTvaUg(tp.getMontantTvaUg());
        newTd.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        newTd.setIntPRICEDETAILOTHER(tp.getIntPRICEDETAILOTHER());
        newTd.setIntFREEPACKNUMBER(tp.getIntFREEPACKNUMBER());
        newTd.setIntPRICEREMISE(tp.getIntPRICEREMISE());
        newTd.setIntAVOIR(tp.getIntAVOIR());
        newTd.setIntAVOIRSERVED(tp.getIntQUANTITYSERVED());
        newTd.setBISAVOIR(tp.getBISAVOIR());
        newTd.setCmuPrice(tp.getCmuPrice());
        getEm().persist(newTd);
        return newTd;
    }

    private void copyPreenregistrementTp(TPreenregistrement preenregistrement, String oldPreenregistrement, TUser o) {
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(oldPreenregistrement, getEm());
        clientTiersPayents.forEach((a) -> {
            TCompteClientTiersPayant OTCompteClientTiersPayant = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent newCmp = new TPreenregistrementCompteClientTiersPayent();
            newCmp.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            newCmp.setLgPREENREGISTREMENTID(preenregistrement);
            newCmp.setIntPRICE(a.getIntPRICE());
            newCmp.setLgUSERID(o);
            newCmp.setStrSTATUT(DateConverter.STATUT_PROCESS);
            newCmp.setDtCREATED(a.getDtCREATED());
            newCmp.setDtUPDATED(a.getDtUPDATED());
            newCmp.setLgCOMPTECLIENTTIERSPAYANTID(OTCompteClientTiersPayant);
            newCmp.setStrREFBON(a.getStrREFBON());
            newCmp.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            newCmp.setIntPERCENT(a.getIntPERCENT());
            newCmp.setIntPRICERESTE(a.getIntPRICERESTE());
            newCmp.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            newCmp.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            getEm().persist(newCmp);
            TCompteClient OTCompteClient = OTCompteClientTiersPayant.getLgCOMPTECLIENTID();
            if (OTCompteClient != null && OTCompteClientTiersPayant.getDblPLAFOND() != null && OTCompteClientTiersPayant.getDblPLAFOND() != 0) {
                OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE((OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() : 0) + newCmp.getIntPRICE());
                OTCompteClientTiersPayant.setDtUPDATED(new Date());
                getEm().merge(OTCompteClientTiersPayant);
            }
            if (OTCompteClient != null && OTCompteClient.getDblPLAFOND() != null && OTCompteClient.getDblPLAFOND() != 0) {
                OTCompteClient.setDblQUOTACONSOMENSUELLE((OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + newCmp.getIntPRICE());
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
                    montantvente = montantAPaye.getCmuAmount() > 0 ? montantAPaye.getCmuAmount() : montantAPaye.getMontant();
                    montantVariable = montantvente;
                    RemiseCarnet = montantAPaye.getRemise();
                } else {
                    montantAPaye = sumVenteSansRemise(lstTPreenregistrementDetail);
                    montantvente = montantAPaye.getCmuAmount() > 0 ? montantAPaye.getCmuAmount() : montantAPaye.getMontant();
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
                netCustomer = (montantvente - totalTp) - RemiseCarnet + (montantAPaye.getMontant()-montantAPaye.getCmuAmount());
            }

            MontantAPaye map = new MontantAPaye(netCustomer, montantvente + (montantAPaye.getMontant()-montantAPaye.getCmuAmount()), totalTp,
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
        if (tp.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(DateConverter.VENTE_CARNET_ID)) {
            updateCompteClientVenteCarner(tp, salesParams);
            updateVenteCarnet(salesParams, tp);
        } else {
            clonePreenregistrementTp(tp, salesParams, DateConverter.STATUT_IS_CLOSED);
            updateVente(salesParams, tp);
        }

        return tp;
    }

    private void clonePreenregistrementTp(TPreenregistrement old, SalesParams salesParams, String statut) throws Exception {
        List<TPreenregistrementCompteClientTiersPayent> newList = getTPreenregistrementCompteClientTiersPayent(old.getLgPREENREGISTREMENTID());
        List<TPreenregistrementCompteClientTiersPayent> array = new ArrayList<>();
        TClient client = old.getClient();
        ArrayList<TPreenregistrementDetail> list = items(old, getEm());
        int montant = old.getIntPRICE();
        int montantVariable = montant;
        List<TiersPayantParams> payantParamses = salesParams.getTierspayants();
        for (TiersPayantParams b : payantParamses) {
            TCompteClientTiersPayant payant;
            Optional<TCompteClientTiersPayant> op = findOneCompteClientTiersPayantById(b.getCompteTp());
            if (!op.isPresent()) {
                op = findCompteClientTiersPayantByClientIdAndTiersPayantId(client.getLgCLIENTID(), b.getCompteTp());
            }
            if (op.isPresent()) {
                payant = op.get();
                if (payant.getBISRO() || (payant.getIntPRIORITY() == 1)) {
                    old.setStrREFBON(b.getNumBon());
                }
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
                if (payantParamses.size() > 1) {
                    TPreenregistrementCompteClientTiersPayent opc = getEm().find(TPreenregistrementCompteClientTiersPayent.class, b.getItemId());
                    payant = clientService.updateOrCreateClientAssurance(client, p, b.getTaux(), opc.getLgCOMPTECLIENTTIERSPAYANTID());
                } else {
                    payant = clientService.updateOrCreateClientAssurance(client, p, b.getTaux());
                }

                if (payant.getBISRO() || (payant.getIntPRIORITY() == 1)) {
                    old.setStrREFBON(b.getNumBon());
                }
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
        _new.setPreenregistrement(p);
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

            Integer RemiseCarnet = 0, montantvente;
            Integer totalTp;

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
//                    remiseCarnet = montantAPaye.getRemise();
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
//        transactionNew.setStrREFBON(salesParams.getTierspayants().get(0).getNumBon());
        _new.setCompletionDate(new Date());
        getEm().merge(_new);
        return _new;
    }

    @Override
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
                mouvementProduitService.saveMvtProduit(_newItem.getIntPRICEUNITAIR(), _newItem,
                        typemvtproduit, OTFamille, ooTUser, emplacement,
                        _newItem.getIntQUANTITY(), initStock, initStock - _newItem.getIntQUANTITY(), emg, _newItem.getValeurTva(), checked, e.getIntUG());

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
        TPreenregistrement newVente = new TPreenregistrement();
        newVente.setLgPREENREGISTREMENTID(UUID.randomUUID().toString());
        newVente.setLgUSERID(ooTUser);
        newVente.setIntPRICEREMISE((-1) * tp.getIntPRICEREMISE());
        newVente.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        newVente.setIntACCOUNT((-1) * tp.getIntACCOUNT());
        newVente.setIntREMISEPARA((-1) * tp.getIntREMISEPARA());
        newVente.setIntPRICE((-1) * tp.getIntPRICE());
        newVente.setIntPRICEOTHER((-1) * tp.getIntPRICEOTHER());
        newVente.setIntCUSTPART((-1) * tp.getIntCUSTPART());
        newVente.setMontantTva((-1) * tp.getMontantTva());
        newVente.setDtCREATED(tp.getDtUPDATED());
        newVente.setDtUPDATED(tp.getDtUPDATED());
        newVente.setCompletionDate(newVente.getDtCREATED());
        newVente.setLgPARENTID(tp.getLgPREENREGISTREMENTID());
        newVente.setStrSTATUT(commonparameter.statut_is_Closed);
        newVente.setLgUSERVENDEURID(tp.getLgUSERVENDEURID());
        newVente.setLgUSERCAISSIERID(tp.getLgUSERCAISSIERID());
        newVente.setBISAVOIR(tp.getBISAVOIR());
        newVente.setBISCANCEL(tp.getBISCANCEL());
        newVente.setLgNATUREVENTEID(tp.getLgNATUREVENTEID());
        newVente.setLgREMISEID(tp.getLgREMISEID());
        newVente.setStrREFTICKET(DateConverter.getShortId(10));
        newVente.setLgTYPEVENTEID(tp.getLgTYPEVENTEID());
        newVente.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        newVente.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        newVente.setStrFIRSTNAMECUSTOMER(tp.getStrFIRSTNAMECUSTOMER());
        newVente.setStrREFBON(tp.getStrREFBON());
        newVente.setStrPHONECUSTOME(tp.getStrPHONECUSTOME());
        newVente.setStrLASTNAMECUSTOMER(tp.getStrLASTNAMECUSTOMER());
        newVente.setStrNUMEROSECURITESOCIAL(tp.getStrNUMEROSECURITESOCIAL());
        newVente.setStrINFOSCLT(tp.getStrINFOSCLT());
        newVente.setIntSENDTOSUGGESTION(0);
        newVente.setPkBrand(tp.getPkBrand());
        newVente.setClient(tp.getClient());
        newVente.setAyantDroit(tp.getAyantDroit());
        newVente.setLgREGLEMENTID(tp.getLgREGLEMENTID());
        newVente.setLgPREENGISTREMENTANNULEID(tp.getLgPREENREGISTREMENTID());
        newVente.setMedecin(tp.getMedecin());
        newVente.setStrREF(tp.getStrREF());
        tp.setBISCANCEL(true);
        tp.setDtANNULER(tp.getDtUPDATED());
        tp.setLgUSERID(ooTUser);
        newVente.setChecked(Boolean.FALSE);
        tp.setChecked(Boolean.FALSE);
        getEm().merge(tp);
        getEm().persist(newVente);
        return newVente;
    }

    public void clonePreenregistrementTp(TPreenregistrement preenregistrement, String oldPreenregistrement, TUser o) {
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(oldPreenregistrement, getEm());
        for (TPreenregistrementCompteClientTiersPayent a : clientTiersPayents) {
            TCompteClientTiersPayant OTCompteClientTiersPayant = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent newItem = new TPreenregistrementCompteClientTiersPayent();
            newItem.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            newItem.setLgPREENREGISTREMENTID(preenregistrement);
            newItem.setIntPRICE(a.getIntPRICE() * (-1));
            newItem.setLgUSERID(o);
            newItem.setStrSTATUT(DateConverter.STATUT_DELETE);
            newItem.setDtCREATED(a.getDtUPDATED());
            newItem.setDtUPDATED(a.getDtUPDATED());
            newItem.setLgCOMPTECLIENTTIERSPAYANTID(OTCompteClientTiersPayant);
            newItem.setStrREFBON(a.getStrREFBON());
            newItem.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            newItem.setIntPERCENT(a.getIntPERCENT());
            newItem.setIntPRICERESTE(0);
            newItem.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            newItem.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            getEm().persist(newItem);

            TCompteClient OTCompteClient = OTCompteClientTiersPayant.getLgCOMPTECLIENTID();
            if (OTCompteClient != null && OTCompteClientTiersPayant.getDblPLAFOND() != null && OTCompteClientTiersPayant.getDblPLAFOND() != 0) {
                OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE((OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() : 0) + newItem.getIntPRICE());
                OTCompteClientTiersPayant.setDtUPDATED(new Date());
                getEm().merge(OTCompteClientTiersPayant);
            }
            if (OTCompteClient != null && OTCompteClient.getDblPLAFOND() != null && OTCompteClient.getDblPLAFOND() != 0) {
                OTCompteClient.setDblQUOTACONSOMENSUELLE((OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + newItem.getIntPRICE());
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
            if (q != null) {
                return Optional.of(q);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<TCompteClientTiersPayant> findCompteClientTiersPayantByClientIdAndTiersPayantId(String clientId, String tierspayantId) {
        try {
            TypedQuery<TCompteClientTiersPayant> q = getEm().createQuery("SELECT t FROM TCompteClientTiersPayant t WHERE t.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID = ?1 AND t.lgTIERSPAYANTID.lgTIERSPAYANTID = ?2", TCompteClientTiersPayant.class)
                    .setParameter(1, clientId).setParameter(2, tierspayantId);
            q.setMaxResults(1);
            TCompteClientTiersPayant p = q.getSingleResult();
            if (p != null) {
                return Optional.of(p);
            }
            return Optional.empty();
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
        TPreenregistrementCompteClientTiersPayent newItem = new TPreenregistrementCompteClientTiersPayent();
        newItem.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
        newItem.setLgPREENREGISTREMENTID(old);
        newItem.setIntPRICE(json.getInt("montanttp"));
        newItem.setLgUSERID(user);
        newItem.setStrSTATUT(statut);
        newItem.setDtCREATED(old.getDtCREATED());
        newItem.setDtUPDATED(old.getDtUPDATED());
        newItem.setLgCOMPTECLIENTTIERSPAYANTID(payant);
        newItem.setStrREFBON(numBon);
        newItem.setDblQUOTACONSOVENTE(0.0);
        newItem.setIntPERCENT(json.getInt("taux"));
        newItem.setIntPRICERESTE(newItem.getIntPERCENT());
        newItem.setStrSTATUTFACTURE("unpaid");
        getEm().persist(newItem);
        TCompteClient OTCompteClient = payant.getLgCOMPTECLIENTID();
        if (OTCompteClient != null && payant.getDblPLAFOND() != null && payant.getDblPLAFOND() != 0) {
            payant.setDblQUOTACONSOMENSUELLE((payant.getDblQUOTACONSOMENSUELLE() != null ? payant.getDblQUOTACONSOMENSUELLE() : 0) + newItem.getIntPRICE());
            payant.setDtUPDATED(old.getDtUPDATED());
            getEm().merge(payant);
        }
        if (OTCompteClient != null && OTCompteClient.getDblPLAFOND() != null && OTCompteClient.getDblPLAFOND() != 0) {
            OTCompteClient.setDblQUOTACONSOMENSUELLE((OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + newItem.getIntPRICE());
            OTCompteClient.setDtUPDATED(new Date());
            getEm().merge(OTCompteClient);
        }
        return newItem;
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

    private MontantAPaye sumVenteSansRemise(ArrayList<TPreenregistrementDetail> list) {
        int montant = 0;
        int montantMarge = 0;
        int montantTva = 0;
        int montantAccount = 0;
        int montantNetUg = 0;
        int montantTtcUg = 0;
        int margeUg = 0;
        int montantCMU=0;
        for (TPreenregistrementDetail x : list) {
            montant += x.getIntPRICE();
            montantCMU+=x.getCmuPrice();
            TFamille famille = x.getLgFAMILLEID();
            if (famille.getBoolACCOUNT()) {
                int marge = ((x.getIntPRICE() - x.getMontantTva()) - (x.getIntQUANTITY() * famille.getIntPAF()));
                montantAccount += x.getIntPRICE();
                montantTva += x.getMontantTva();
                montantMarge += marge;

            }
        }
        return new MontantAPaye(montant,
                montant,
                0, 0,
                montantMarge, montantTva).montantAccount(montantAccount)
                .margeUg(margeUg)
                .montantNetUg(montantNetUg)
                .montantTtcUg(montantTtcUg)
                .cmuAmount(montantCMU);
                

    }

    private JSONObject shownetpayVnoCheckUg(SalesParams params) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            MontantAPaye montantAPaye;
            TPreenregistrement p = emg.find(TPreenregistrement.class, params.getVenteId());
            if (p.getRemise() == null) {
                montantAPaye = sumVenteSansRemise(items(p, emg), p);
                p.setIntPRICE(montantAPaye.getMontant());
                p.setIntACCOUNT(montantAPaye.getMontantAccount());
                p.setIntPRICEOTHER(montantAPaye.getMontant());
                json.put("success", true).put("msg", "Opération effectuée avec success");
                json.put("data", new JSONObject(montantAPaye));

            } else {
                TRemise remise = p.getRemise();
                montantAPaye = getRemiseVnoCheckUg(p, remise);
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

    private MontantAPaye getRemiseVnoCheckUg(TPreenregistrement OTPreenregistrement, TRemise OTRemise) {
        Integer int_TOTAL_REMISE, int_REMISE_PARA = 0, montantNet = 0;
        LongAdder totalRemise = new LongAdder();
        LongAdder totalRemisePara = new LongAdder();
        LongAdder totalAmount = new LongAdder();
        LongAdder marge = new LongAdder();
        LongAdder montantTva = new LongAdder();
        LongAdder montantAccount = new LongAdder();
        LongAdder montantTtcUg = new LongAdder();
        LongAdder margeUg = new LongAdder();
        LongAdder tvaUg = new LongAdder();
        TEmplacement emplacement = OTPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
        boolean isVno = OTPreenregistrement.getStrTYPEVENTE().equals(DateConverter.VENTE_COMPTANT);
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
                if (isVno) {
                    int _margeUg = ((x.getIntPRICE() - x.getMontantTva()) - (x.getIntQUANTITY() * famille.getIntPAF()));

                    TFamilleStock stock = this.findStock(famille.getLgFAMILLEID(), emplacement, getEm());
                    if (stock.getIntUG() > 0) {
                        int qtyUg = qtyUg(stock.getIntUG(), x.getIntQUANTITYSERVED());
                        int _montant = qtyUg * x.getIntPRICEUNITAIR();
                        montantTtcUg.add(qtyUg * x.getIntPRICEUNITAIR());
                        margeUg.add(margeUg(qtyUg, x.getIntQUANTITY(), _margeUg));
                        if (x.getValeurTva() > 0) {
                            Double HT = _montant / (1 + (Double.valueOf(x.getValeurTva()) / 100));
                            int _montantTvaUg = _montant - HT.intValue();
                            x.setMontantTvaUg(_montantTvaUg);
                            tvaUg.add(_montantTvaUg);
                        }
                    }
                }
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
                montantTotal, 0, DateConverter.arrondiModuloOfNumber(int_TOTAL_REMISE, 5), marge.intValue(), tva)
                .margeUg(margeUg.intValue())
                .montantTtcUg(montantTtcUg.intValue())
                .montantTvaUg(tvaUg.intValue()).
                montantNetUg(montantTtcUg.intValue());
    }

    private MontantAPaye sumVenteSansRemise(ArrayList<TPreenregistrementDetail> list, TPreenregistrement p) {
        int montant = 0;
        int montantMarge = 0;
        int montantTva = 0;
        int montantAccount = 0;
        int montantNetUg = 0;
        int montantTtcUg = 0;
        int margeUg = 0;
        int tvaug = 0;
        TEmplacement emplacement = p.getLgUSERID().getLgEMPLACEMENTID();
        boolean isVno = p.getStrTYPEVENTE().equals(DateConverter.VENTE_COMPTANT);
        for (TPreenregistrementDetail x : list) {
            montant += x.getIntPRICE();
            TFamille famille = x.getLgFAMILLEID();
            if (famille.getBoolACCOUNT()) {
                int marge = ((x.getIntPRICE() - x.getMontantTva()) - (x.getIntQUANTITY() * famille.getIntPAF()));
                montantAccount += x.getIntPRICE();
                montantTva += x.getMontantTva();
                montantMarge += marge;
                if (isVno) {
                    TFamilleStock stock = this.findStock(famille.getLgFAMILLEID(), emplacement, getEm());
                    if (stock.getIntUG() > 0) {
                        int qtyUg = qtyUg(stock.getIntUG(), x.getIntQUANTITYSERVED());
                        int _montant = qtyUg * x.getIntPRICEUNITAIR();
                        montantTtcUg += _montant;
                        montantNetUg = montantTtcUg;
                        margeUg += margeUg(qtyUg, x.getIntQUANTITY(), marge);
                        if (x.getValeurTva() > 0) {
                            Double HT = _montant / (1 + (Double.valueOf(x.getValeurTva()) / 100));
                            int _montantTvaUg = _montant - HT.intValue();
                            x.setMontantTvaUg(_montantTvaUg);
                            tvaug += _montantTvaUg;
                        }

                    }
                }
            }
        }
        return new MontantAPaye(montant,
                montant,
                0, 0,
                montantMarge, montantTva)
                .montantTvaUg(tvaug)
                .montantAccount(montantAccount)
                .margeUg(margeUg)
                .montantNetUg(montantNetUg)
                .montantTtcUg(montantTtcUg);
    }

    @Override
    public JSONObject closePreventeVente(TUser ooTUser, String lg_PREENREGISTREMENT_ID) {
        TPreenregistrement p = getEm().find(TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
        p.setStrSTATUT(DateConverter.STATUT_PROCESS);
        p.setLgUSERID(ooTUser);
        p.setCompletionDate(new Date());
        getEm().merge(p);
        return new JSONObject().put("success", true);
    }

    private TPreenregistrement copyDevis(TPreenregistrement tp) {
        TPreenregistrement newTp = new TPreenregistrement();
        newTp.setLgPREENREGISTREMENTID(UUID.randomUUID().toString());
        newTp.setLgUSERID(tp.getLgUSERID());
        newTp.setIntPRICEREMISE(tp.getIntPRICEREMISE());
        newTp.setStrSTATUTVENTE(tp.getStrSTATUTVENTE());
        newTp.setIntACCOUNT(tp.getIntACCOUNT());
        newTp.setIntREMISEPARA(tp.getIntREMISEPARA());
        newTp.setIntPRICE(tp.getIntPRICE());
        newTp.setIntPRICEOTHER(tp.getIntPRICEOTHER());
        newTp.setIntCUSTPART(tp.getIntCUSTPART());
        newTp.setMontantTva(tp.getMontantTva());
        newTp.setDtCREATED(tp.getDtCREATED());
        newTp.setDtUPDATED(tp.getDtUPDATED());
        newTp.setLgPARENTID(tp.getLgPREENREGISTREMENTID());
        newTp.setLgUSERVENDEURID(tp.getLgUSERVENDEURID());
        newTp.setLgUSERCAISSIERID(tp.getLgUSERCAISSIERID());
        newTp.setBISAVOIR(tp.getBISAVOIR());
        newTp.setBISCANCEL(false);
        newTp.setLgNATUREVENTEID(tp.getLgNATUREVENTEID());
        newTp.setLgREMISEID(tp.getLgREMISEID());
        newTp.setStrREFTICKET(DateConverter.getShortId(10));
        newTp.setLgTYPEVENTEID(tp.getLgTYPEVENTEID());
        newTp.setStrTYPEVENTE(tp.getStrTYPEVENTE());
        newTp.setStrFIRSTNAMECUSTOMER(tp.getStrFIRSTNAMECUSTOMER());
        newTp.setStrREFBON(tp.getStrREFBON());
        newTp.setStrPHONECUSTOME(tp.getStrPHONECUSTOME());
        newTp.setStrLASTNAMECUSTOMER(tp.getStrLASTNAMECUSTOMER());
        newTp.setStrNUMEROSECURITESOCIAL(tp.getStrNUMEROSECURITESOCIAL());
        newTp.setStrINFOSCLT(tp.getStrINFOSCLT());
        newTp.setIntSENDTOSUGGESTION(0);
        newTp.setPkBrand(tp.getPkBrand());
        newTp.setClient(tp.getClient());
        newTp.setAyantDroit(tp.getAyantDroit());
        newTp.setMedecin(tp.getMedecin());
        newTp.setStrSTATUT(tp.getStrSTATUT());
        newTp.setStrREF(buildRefTmp(LocalDate.now(), newTp.getLgUSERID().getLgEMPLACEMENTID()).getReferenceTemp());
        newTp.setChecked(Boolean.TRUE);
        newTp.setCopy(Boolean.FALSE);
        newTp.setMargeug(tp.getMargeug());
        newTp.setMontantnetug(tp.getMontantnetug());
        newTp.setMontantttcug(tp.getMontantttcug());
        newTp.setMontantTvaUg(tp.getMontantTvaUg());
        newTp.setCmuAmount(tp.getCmuAmount());
        getEm().persist(newTp);
        return newTp;
    }

    @Override
    public JSONObject clonerDevis(TUser ooTUser, String devisId) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            TPreenregistrement p = getEm().find(TPreenregistrement.class, devisId);
            TPreenregistrement newTp = copyDevis(p);
            newTp.setDtCREATED(new Date());
            newTp.setDtUPDATED(newTp.getDtCREATED());
            newTp.setCompletionDate(newTp.getDtCREATED());
            newTp.setLgUSERID(ooTUser);
            List<TPreenregistrementDetail> details = getTPreenregistrementDetail(p, getEm());
            details.stream().forEach(e -> {
                TPreenregistrementDetail detail = new TPreenregistrementDetail(e);
                detail.setLgPREENREGISTREMENTDETAILID(UUID.randomUUID().toString());
                detail.setLgPREENREGISTREMENTID(newTp);
                detail.setDtCREATED(newTp.getDtUPDATED());
                detail.setDtUPDATED(newTp.getDtUPDATED());
                getEm().persist(detail);
            });

            List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = getTPreenregistrementCompteClientTiersPayent(p.getLgPREENREGISTREMENTID());
            clientTiersPayents.stream().map(clientTiersPayent -> new TPreenregistrementCompteClientTiersPayent(clientTiersPayent)).map(pt -> {
                pt.setLgPREENREGISTREMENTID(newTp);
                return pt;
            }).map(pt -> {
                pt.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
                pt.setDtCREATED(newTp.getDtUPDATED());
                pt.setDtUPDATED(newTp.getDtUPDATED());
                return pt;
            }).forEachOrdered(pt -> {
                getEm().persist(pt);
            });
            JSONObject data = new JSONObject();
            data.put("lgPREENREGISTREMENTID", newTp.getLgPREENREGISTREMENTID());
            data.put("strREF", newTp.getStrREF());
            data.put("intPRICE", newTp.getIntPRICE());
            return json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
        } catch (JSONException e) {
            LOG.log(Level.SEVERE, "clonerDevis=====>> ", e);
            return null;
        }

    }

    @Override
    public void updateVenteTva() {
        Map<TPreenregistrement, List<TPreenregistrementDetail>> preenregistrements = findAllItem().stream().collect(Collectors.groupingBy(TPreenregistrementDetail::getLgPREENREGISTREMENTID));
        preenregistrements.entrySet().stream().map(entry -> {
            TPreenregistrement p = entry.getKey();
            List<TPreenregistrementDetail> details = entry.getValue();
            MvtTransaction mt = findByPkey(p.getLgPREENREGISTREMENTID());
            int pmontantTva = 0;
            for (TPreenregistrementDetail d : details) {
                int montantTva = calculeTva(18, d.getIntPRICE());
                pmontantTva += montantTva;
                d.setMontantTva(montantTva);
                getEm().merge(d);
            }
            p.setMontantTva(p.getMontantTva() + pmontantTva);
            getEm().merge(p);
            mt.setMontantTva(mt.getMontantTva() + pmontantTva);
            return mt;
        }).forEachOrdered(mt -> {
            getEm().merge(mt);
        });
    }

    private Integer calculeTva(int codeTva, Integer amount) {
        Double HT = amount / (1 + (Double.valueOf(codeTva) / 100));
        return amount - HT.intValue();
    }

    private List<TPreenregistrement> findAllWithItem() {
        TypedQuery<TPreenregistrement> tq = getEm().createQuery("SELECT o FROM TPreenregistrement o WHERE FUNCTION('DATE',o.dtCREATED)=?1", TPreenregistrement.class);
        tq.setParameter(1, java.sql.Date.valueOf("2019-12-07"), TemporalType.DATE);
        return tq.getResultList();
    }

    private List<TPreenregistrementDetail> findAllItem() {
        TypedQuery<TPreenregistrementDetail> tq = getEm().createQuery("SELECT o FROM TPreenregistrementDetail o   WHERE o.montantTva=0 AND o.strSTATUT='is_Closed'", TPreenregistrementDetail.class);
        return tq.getResultList();
    }

    private MvtTransaction findByPkey(String pkey) {
        try {
            TypedQuery<MvtTransaction> tq = getEm().createQuery("SELECT o FROM MvtTransaction o WHERE o.pkey=?1", MvtTransaction.class);
            tq.setParameter(1, pkey);
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private void updateCompteClientVenteCarner(TPreenregistrement old, SalesParams salesParams) throws Exception {
        TPreenregistrementCompteClientTiersPayent opc = getTPreenregistrementCompteClientTiersPayent(old.getLgPREENREGISTREMENTID()).get(0);
        TClient client = old.getClient();
        TiersPayantParams b = salesParams.getTierspayants().get(0);
        TCompteClientTiersPayant payant;
        Optional<TCompteClientTiersPayant> op = findOneCompteClientTiersPayantById(b.getCompteTp());
        if (!op.isPresent()) {
            op = findCompteClientTiersPayantByClientIdAndTiersPayantId(client.getLgCLIENTID(), b.getCompteTp());
        }
        if (op.isPresent()) {
            payant = op.get();
        } else {
            TTiersPayant p = getEm().find(TTiersPayant.class, b.getCompteTp());
            payant = clientService.updateOrCreateClientAssurance(client, p, b.getTaux());
        }

        opc.setStrREFBON(b.getNumBon());
        opc.setLgCOMPTECLIENTTIERSPAYANTID(payant);
        getEm().merge(opc);

    }

    private TPreenregistrement updateVenteCarnet(SalesParams salesParams, TPreenregistrement _new) throws Exception {
        _new.setLgUSERID(salesParams.getUserId());
        TClient client = findClient(salesParams.getClientId(), getEm());
        _new.setClient(client);
        _new.setStrFIRSTNAMECUSTOMER(client.getStrFIRSTNAME());
        _new.setStrLASTNAMECUSTOMER(client.getStrLASTNAME());
        _new.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        _new.setStrPHONECUSTOME(client.getStrADRESSE());
        _new.setStrINFOSCLT("");
        _new.setStrREFBON(salesParams.getTierspayants().get(0).getNumBon());
        getEm().merge(_new);
        return _new;
    }

    @Override
    public void upadteVente() {
        List<TPreenregistrement> list = findAllWithItem();

        for (TPreenregistrement p : list) {
            LocalDateTime venteDate = DateConverter.convertDateToLocalDateTime(p.getDtUPDATED());
            LocalDate toDay = LocalDate.now();
            LocalDateTime venteDateNew = LocalDateTime.of(toDay, LocalTime.of(venteDate.getHour(), venteDate.getMinute(), venteDate.getSecond(), venteDate.getNano()));

            p.setDtCREATED(DateConverter.convertLocalDateTimeToDate(venteDateNew));
            p.setDtUPDATED(p.getDtCREATED());
            p.setCompletionDate(new Date());
            getEm().merge(p);
            MvtTransaction mt = findByPkey(p.getLgPREENREGISTREMENTID());
            if (mt != null) {
                mt.setMvtDate(toDay);
                mt.setCreatedAt(venteDateNew);
                getEm().merge(mt);
            }
            List<TPreenregistrementDetail> details = getTPreenregistrementDetail(p, getEm());
            details.stream().map(detail -> {
                detail.setDtCREATED(p.getDtCREATED());
                return detail;
            }).map(detail -> {
                detail.setDtUPDATED(p.getDtCREATED());
                return detail;
            }).map(detail -> {
                getEm().merge(detail);
                return detail;
            }).map(detail -> findHMvtProduitByPkey(detail.getLgPREENREGISTREMENTDETAILID())).filter(hMvtProduit -> (hMvtProduit != null)).map(hMvtProduit -> {
                hMvtProduit.setMvtDate(toDay);
                return hMvtProduit;
            }).map(hMvtProduit -> {
                hMvtProduit.setCreatedAt(venteDateNew);
                return hMvtProduit;
            }).forEachOrdered(hMvtProduit -> {
                getEm().merge(hMvtProduit);
            });
        }

    }

    private HMvtProduit findHMvtProduitByPkey(String pkey) {
        try {
            TypedQuery<HMvtProduit> q = getEm().createQuery("SELECT o FROM HMvtProduit o WHERE o.pkey=?1", HMvtProduit.class);
            q.setParameter(1, pkey);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private int computeCmuAmount(TPreenregistrementDetail pd) {
       
        if ( Objects.nonNull(pd.getCmuPrice()) && pd.getCmuPrice() > 0) {
            return pd.getIntQUANTITY() * pd.getCmuPrice();
        }
        return pd.getIntQUANTITY() * pd.getIntPRICEUNITAIR();
    }

}
