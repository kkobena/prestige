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
import commonTasks.dto.VenteReglementDTO;
import commonTasks.dto.VenteRequest;
import dal.AnnulationRecette;
import dal.AnnulationSnapshot;
import dal.HMvtProduit;
import dal.Medecin;
import dal.MvtTransaction;
import dal.Notification;
import dal.Reference;
import dal.TAyantDroit;
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
import dal.VenteReglement;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
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
import org.apache.commons.collections4.CollectionUtils;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.wst.common.project.facet.core.util.internal.CollectionsUtil;
import org.hibernate.jpa.QueryHints;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.qualifier.SalesPrimary;
import rest.service.*;
import rest.service.dto.UpdateVenteParamDTO;
import toolkits.parameters.commonparameter;
import toolkits.utils.StringComplexUtils.DataStringManager;
import util.Afficheur;
import util.Constant;
import util.DateConverter;

import static util.Constant.*;
import util.DateCommonUtils;

/**
 * @author Kobena
 */
@Stateless

@SalesPrimary
public class SalesServiceImpl implements SalesService {

    private static final Logger LOG = Logger.getLogger(SalesServiceImpl.class.getName());

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
    @Resource(name = "concurrent/__defaultManagedExecutorService")
    ManagedExecutorService managedExecutorService;
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
    @EJB
    private VenteReglementService venteReglementService;

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

    public MvtTransaction addTransaction(TUser ooTUser, TPreenregistrement tp, Integer montant, Integer voidAmount,
            Integer montantNet, Integer montantVerse, Boolean checked, CategoryTransaction categoryTransaction,
            TypeTransaction typeTransaction, TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse,
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
        transactionNew.setPreenregistrement(tp);
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

    public MvtTransaction addTransaction(TUser ooTUser, TPreenregistrement tp, Integer montant, Integer montantNet,
            Integer montantVerse, Boolean checked, TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse,
            Integer montantCredit, Integer montantPaye, Integer marge, boolean diff, String typeReglement)
            throws Exception {
        Integer montantClient = tp.getIntCUSTPART() - tp.getIntPRICEREMISE();
        MvtTransaction transactionNew = new MvtTransaction();
        Integer montantPaid = 0;
        int montantRestant = montantClient;
        if (typeReglement.equals(MODE_ESP) || typeReglement.equals(REGL_DIFF)) {
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
        transactionNew.setUuid(UUID.randomUUID().toString());
        transactionNew.setUser(ooTUser);
        transactionNew.setCreatedAt(DateConverter.convertDateToLocalDateTime(tp.getDtUPDATED()));
        transactionNew.setMvtDate(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()));
        transactionNew.setPkey(tp.getLgPREENREGISTREMENTID());
        transactionNew.setAvoidAmount(tp.getIntACCOUNT());
        transactionNew.setMontant(montant);
        transactionNew.setMagasin(ooTUser.getLgEMPLACEMENTID());
        transactionNew.setCaisse(ooTUser);
        transactionNew.setReference(tp.getStrREF());
        transactionNew.setMontantCredit(montantCredit);
        transactionNew.setMontantVerse(montantVerse);
        transactionNew.setMontantRegle(montantPaid);// 09032020
        transactionNew.setMontantPaye(montantPaye);
        transactionNew.setMontantNet(montantNet);
        transactionNew.settTypeMvtCaisse(tTypeMvtCaisse);
        transactionNew.setReglement(reglement);
        transactionNew.setMontantRestant(0);
        transactionNew.setPreenregistrement(tp);

        if (diff) {
            transactionNew.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        }

        transactionNew.setMontantRemise(tp.getIntPRICEREMISE());
        transactionNew.setMontantTva(tp.getMontantTva());
        transactionNew.setMarge(marge);
        transactionNew.setCategoryTransaction(CategoryTransaction.CREDIT);
        transactionNew.setTypeTransaction(TypeTransaction.VENTE_CREDIT);
        transactionNew.setChecked(checked);
        if (tp.getClient() != null) {
            transactionNew.setOrganisme(tp.getClient().getLgCLIENTID());
        }
        transactionNew.setMontantAcc(tp.getIntACCOUNT());
        getEm().persist(transactionNew);
        return transactionNew;
    }

    public void addTransactionDepot(TUser ooTUser, TPreenregistrement tp, TTypeReglement reglement,
            TTypeMvtCaisse tTypeMvtCaisse, Integer marge, TClient client) {
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
        mvtTransac.setMontantRemise(tp.getIntPRICEREMISE());
        mvtTransac.setCategoryTransaction(CategoryTransaction.CREDIT);
        mvtTransac.setTypeTransaction(TypeTransaction.VENTE_CREDIT);
        mvtTransac.setChecked(false);
        mvtTransac.setMontantTva(tp.getMontantTva());
        mvtTransac.setPreenregistrement(tp);
        if (client != null) {
            mvtTransac.setOrganisme(client.getLgCLIENTID());
        }
        this.getEm().persist(mvtTransac);

    }

    public void addTransactionCopy(TUser ooTUser, TUser caisse, MvtTransaction old, TPreenregistrement newP,
            LocalDateTime localDateTime, LocalDate localDate) {
        MvtTransaction newTransaction = new MvtTransaction();
        newTransaction.setUuid(UUID.randomUUID().toString());
        newTransaction.setUser(ooTUser);
        newTransaction.setPreenregistrement(newP);
        newTransaction.setCreatedAt(localDateTime);
        newTransaction.setPkey(newP.getLgPREENREGISTREMENTID());
        newTransaction.setMvtDate(localDate);
        newTransaction.setAvoidAmount((-1) * old.getAvoidAmount());
        newTransaction.setMontant((-1) * old.getMontant());
        newTransaction.setMagasin(ooTUser.getLgEMPLACEMENTID());
        newTransaction.setCaisse(caisse);
        newTransaction.setReference(newP.getStrREF());
        newTransaction.setMontantCredit((-1) * old.getMontantCredit());
        newTransaction.setMontantVerse((-1) * old.getMontantVerse());
        newTransaction.setMontantRegle((-1) * old.getMontantRegle());
        newTransaction.setMontantPaye((-1) * old.getMontantPaye());
        newTransaction.setMontantNet((-1) * old.getMontantNet());
        newTransaction.settTypeMvtCaisse(old.gettTypeMvtCaisse());
        newTransaction.setReglement(old.getReglement());
        newTransaction.setMontantRestant((-1) * old.getMontantRestant());
        newTransaction.setMontantRemise((-1) * old.getMontantRemise());
        newTransaction.setMontantTva((-1) * old.getMontantTva());
        newTransaction.setMarge((-1) * old.getMarge());
        newTransaction.setCategoryTransaction(CategoryTransaction.DEBIT);
        newTransaction.setTypeTransaction(old.getTypeTransaction());
        newTransaction.setOrganisme(old.getOrganisme());
        newTransaction.setMarge((-1) * old.getMarge());
        newTransaction.setMontantttcug((-1) * old.getMontantttcug());
        newTransaction.setMontantnetug((-1) * old.getMontantnetug());
        newTransaction.setMargeug((-1) * old.getMargeug());
        newTransaction.setMontantTvaUg((-1) * old.getMontantTvaUg());
        newTransaction.setChecked(false);
        this.em.persist(newTransaction);
    }

    @Override
    public JSONObject annulerVente(TUser ooTUser, String id) {
        TPreenregistrement preenregistrement = this.getEm().find(TPreenregistrement.class, id);
        JSONObject json;
        if (preenregistrement == null) {
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
            return annulerVente(ooTUser, preenregistrement);
        } catch (JSONException ex) {
            return new JSONObject();
        }

    }

    public Optional<TPreenregistrementCompteClient> findOptionalCmt(TPreenregistrement preenregistrement,
            EntityManager emg) {
        try {
            TypedQuery<TPreenregistrementCompteClient> query = emg.createQuery(
                    "SELECT o FROM TPreenregistrementCompteClient o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID =?1 ",
                    TPreenregistrementCompteClient.class);
            query.setMaxResults(1);
            query.setParameter(1, preenregistrement.getLgPREENREGISTREMENTID());
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional checkResumeCaisse(TUser ooTUser, EntityManager emg) {
        try {
            TypedQuery<TResumeCaisse> q = emg.createQuery(
                    "SELECT t FROM TResumeCaisse t WHERE t.lgUSERID.lgUSERID = ?1  AND t.strSTATUT = ?2 ",
                    TResumeCaisse.class);
            q.setParameter(1, ooTUser.getLgUSERID()).setParameter(2, STATUT_IS_USING).setMaxResults(1);
            return Optional.ofNullable(q.getSingleResult());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean checkCaisse(TUser ooTUser) {
        try {
            TypedQuery<TResumeCaisse> q = this.getEm().createQuery(
                    "SELECT t FROM TResumeCaisse t WHERE t.lgUSERID.lgUSERID = ?1  AND t.strSTATUT = ?2 ",
                    TResumeCaisse.class);
            q.setParameter(1, ooTUser.getLgUSERID()).setParameter(2, STATUT_IS_USING).setMaxResults(1);
            return (q.getSingleResult() != null);
        } catch (Exception e) {
            return false;
        }
    }

    private Optional<TRecettes> findRecette(String id, EntityManager emg) {
        try {
            TypedQuery<TRecettes> query = emg.createQuery("SELECT o FROM TRecettes o WHERE o.strREFFACTURE=?1",
                    TRecettes.class);
            query.setParameter(1, id);
            query.setMaxResults(1);
            TRecettes recettes = query.getSingleResult();
            return recettes != null ? Optional.of(recettes) : Optional.empty();
        } catch (Exception e) {

            return Optional.empty();
        }
    }

    private Optional<MvtTransaction> getTransaction(String idVente) {
        try {
            TypedQuery<MvtTransaction> q = this.getEm()
                    .createQuery("SELECT o FROM MvtTransaction o WHERE o.pkey =?1 ", MvtTransaction.class)
                    .setParameter(1, idVente);
            MvtTransaction mt = q.getSingleResult();
            if (mt != null) {
                return Optional.ofNullable(mt);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public void copyTransaction(TUser ooTUser, MvtTransaction cashTransaction, TPreenregistrement newP,
            TPreenregistrement old) {

        if (cashTransaction.getMvtDate().isEqual(LocalDate.now())) {
            cashTransaction.setChecked(Boolean.FALSE);
            em.merge(cashTransaction);
            addTransactionCopy(ooTUser, old.getLgUSERCAISSIERID(), cashTransaction, newP, LocalDateTime.now(),
                    LocalDate.now());
        } else {
            MvtTransaction newTransaction = new MvtTransaction();
            newTransaction.setUuid(UUID.randomUUID().toString());
            newTransaction.setUser(ooTUser);
            newTransaction.setCreatedAt(LocalDateTime.now());
            newTransaction.setPkey(newP.getLgPREENREGISTREMENTID());
            newTransaction.setPreenregistrement(newP);
            newTransaction.setMvtDate(LocalDate.now());
            newTransaction.setAvoidAmount((-1) * cashTransaction.getAvoidAmount());
            newTransaction.setMontant((-1) * cashTransaction.getMontant());
            newTransaction.setMontantNet((-1) * cashTransaction.getMontantNet());
            newTransaction.setMontantRegle((-1) * cashTransaction.getMontantRegle());
            newTransaction.setMontantRestant((-1) * cashTransaction.getMontantRestant());
            newTransaction.setMontantRemise((-1) * cashTransaction.getMontantRemise());
            newTransaction.setMontantCredit((-1) * cashTransaction.getMontantCredit());
            newTransaction.setMontantPaye((-1) * cashTransaction.getMontantPaye());
            newTransaction.setCategoryTransaction(CategoryTransaction.DEBIT);
            newTransaction.setMontantTva((-1) * cashTransaction.getMontantTva());
            newTransaction.setMarge((-1) * cashTransaction.getMarge());
            newTransaction.setMontantttcug((-1) * cashTransaction.getMontantttcug());
            newTransaction.setMontantnetug((-1) * cashTransaction.getMontantnetug());
            newTransaction.setMargeug((-1) * cashTransaction.getMargeug());
            newTransaction.setMontantTvaUg((-1) * cashTransaction.getMontantTvaUg());
            newTransaction.setChecked(Boolean.TRUE);
            newTransaction.setReference(newP.getStrREF());
            newTransaction.setOrganisme(cashTransaction.getOrganisme());
            newTransaction.settTypeMvtCaisse(cashTransaction.gettTypeMvtCaisse());
            newTransaction.setReglement(cashTransaction.getReglement());
            newTransaction.setTypeTransaction(cashTransaction.getTypeTransaction());
            newTransaction.setCaisse(cashTransaction.getCaisse());
            newTransaction.setMagasin(cashTransaction.getMagasin());
            em.persist(newTransaction);
        }

    }

    private Typemvtproduit findTypeMvtProduitById(String id) {
        return getEm().find(Typemvtproduit.class, id);
    }

    private JSONObject annulerVente(TUser ooTUser, TPreenregistrement tp) throws JSONException {
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

            Optional<TRecettes> oprectte = findRecette(tp.getLgPREENREGISTREMENTID(), emg);
            List<TPreenregistrementDetail> preenregistrementDetails = items(tp);
            String idVente = tp.getLgPREENREGISTREMENTID();
            TPreenregistrement newItem = createPreventeCopy(ooTUser, tp, emg);
            ref = newItem.getLgPREENREGISTREMENTID();

            LongAdder montantRestant = new LongAdder();
            findOptionalCmt(tp, emg).ifPresent(cp -> {
                montantRestant.add(cp.getIntPRICERESTE());
                cp.setIntPRICE(0);
                cp.setIntPRICERESTE(0);
                cp.setStrSTATUT(STATUT_DELETE);
                cp.setDtUPDATED(new Date());
                emg.merge(cp);
            });
            if (tp.getStrTYPEVENTE().equals("VO")) {
                copyPreenregistrementTp(newItem, idVente, ooTUser, emg);
                findByVenteId(tp.getLgPREENREGISTREMENTID()).ifPresent(venteExclus -> {
                    venteExclus.setStatus(Statut.DELETE);
                    this.getEm().merge(venteExclus);
                });
            }

            getTransaction(idVente).ifPresent(tr -> {

                copyTransaction(ooTUser, tr, newItem, tp);
                if (!checkResumeCaisse(tp.getLgUSERCAISSIERID(), emg).isPresent()) {
                    createAnnulationRecette(tp, tr, ooTUser);
                }
                if (!sameDate) {
                    createAnnulleSnapshot(tp, montantRestant.intValue(), tr.getMontantPaye(), ooTUser,
                            tr.getReglement());
                }
            });
            copyVenteReglement(this.venteReglementService.getByVenteId(tp.getLgPREENREGISTREMENTID()), newItem);
            oprectte.ifPresent(re -> copyRecette(newItem, re, ooTUser));

            findClientTiersPayents(tp.getLgPREENREGISTREMENTID(), emg).forEach(cpClient -> {
                cpClient.setStrSTATUT(STATUT_DELETE);
                cpClient.setDtUPDATED(new Date());
                emg.merge(cpClient);
                TTiersPayant p = cpClient.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID();
                if (p.getToBeExclude() || p.getIsDepot()) {
                    payantExclusService.updateTiersPayantAccount(p, (-1) * cpClient.getIntPRICE());
                } else {
                    updateClientAccount(cpClient);
                }

            });
            TEmplacement emplacement = ooTUser.getLgEMPLACEMENTID();
            final Typemvtproduit typemvtproduit = checked ? findTypeMvtProduitById(ANNULATION_DE_VENTE)
                    : findTypeMvtProduitById(TMVTP_ANNUL_VENTE_DEPOT_EXTENSION);
            preenregistrementDetails.forEach(e -> {
                TPreenregistrementDetail newCopieItem = createItemCopy(ooTUser, e, newItem, emg);
                TFamille oFamille = e.getLgFAMILLEID();
                updateNbreVenteApresAnnulation(oFamille, ooTUser, newCopieItem.getIntQUANTITY());
                TFamilleStock familleStock = findStock(oFamille.getLgFAMILLEID(), emplacement, emg);
                int initStock = familleStock.getIntNUMBERAVAILABLE();
                familleStock.setIntUG(familleStock.getIntUG() - newCopieItem.getIntUG());
                mouvementProduitService.saveMvtProduit(newCopieItem.getIntPRICEUNITAIR(), newCopieItem, typemvtproduit,
                        oFamille, ooTUser, emplacement, newCopieItem.getIntQUANTITY(), initStock,
                        initStock - newCopieItem.getIntQUANTITY(), newCopieItem.getValeurTva(), checked, e.getIntUG());

                updateReelStockApresAnnulation(familleStock, newCopieItem.getIntQUANTITY());
                if (!tp.getPkBrand().isEmpty()) {
                    updateReelStockAnnulationDepot(oFamille, newCopieItem.getIntQUANTITY(), tp.getPkBrand(), emg);

                }

            });

            String desc = "Annulation de la [ " + tp.getStrREF() + " montant  " + tp.getIntPRICE() + " ] par "
                    + ooTUser.getStrFIRSTNAME() + " " + ooTUser.getStrLASTNAME();
            logService.updateItem(ooTUser, tp.getStrREF(), desc, TypeLog.ANNULATION_DE_VENTE, tp);
            notificationService.save(new Notification().canal(Canal.SMS_EMAIL)
                    .typeNotification(TypeNotification.ANNULATION_DE_VENTE).message(desc).addUser(ooTUser));
            json.put("success", true);
            json.put("msg", "L'opération effectuée avec success");
            json.put("ref", newItem.getLgPREENREGISTREMENTID());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false);
            json.put("msg", "Erreur annulation de la vente ");
            json.put("ref", ref);

        }
        return json;

    }

    private Optional<VenteExclus> findByVenteId(String venteId) {
        try {
            return Optional.ofNullable(this.getEm()
                    .createQuery("SELECT o FROM VenteExclus o WHERE o.preenregistrement.lgPREENREGISTREMENTID=?1",
                            VenteExclus.class)
                    .setParameter(1, venteId).setMaxResults(1).getSingleResult());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
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

    public TPreenregistrementDetail createItemCopy(TUser ooTUser, TPreenregistrementDetail tp, TPreenregistrement p,
            EntityManager emg) {
        TPreenregistrementDetail newItem = new TPreenregistrementDetail();
        newItem.setLgPREENREGISTREMENTDETAILID(UUID.randomUUID().toString());
        newItem.setLgPREENREGISTREMENTID(p);
        newItem.setIntPRICE((-1) * tp.getIntPRICE());
        newItem.setIntQUANTITY((-1) * tp.getIntQUANTITY());
        newItem.setIntQUANTITYSERVED((-1) * tp.getIntQUANTITYSERVED());
        newItem.setMontantTva((-1) * tp.getMontantTva());
        newItem.setDtCREATED(new Date());
        newItem.setStrSTATUT(STATUT_IS_CLOSED);
        newItem.setDtUPDATED(newItem.getDtCREATED());
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
        newTp.setStrSTATUT(STATUT_IS_CLOSED);
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
        tp.setDtANNULER(newTp.getDtCREATED());
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

    public void copyPreenregistrementTp(TPreenregistrement preenregistrement, String oldPreenregistrement, TUser o,
            EntityManager emg) {
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(
                oldPreenregistrement, emg);
        for (TPreenregistrementCompteClientTiersPayent a : clientTiersPayents) {
            TCompteClientTiersPayant cltP = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent newCtp = new TPreenregistrementCompteClientTiersPayent();
            newCtp.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            newCtp.setLgPREENREGISTREMENTID(preenregistrement);
            newCtp.setIntPRICE(a.getIntPRICE() * (-1));
            newCtp.setLgUSERID(o);
            newCtp.setStrSTATUT(STATUT_DELETE);
            newCtp.setDtCREATED(new Date());
            newCtp.setDtUPDATED(newCtp.getDtCREATED());
            newCtp.setLgCOMPTECLIENTTIERSPAYANTID(cltP);
            newCtp.setStrREFBON(a.getStrREFBON());
            newCtp.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            newCtp.setIntPERCENT(a.getIntPERCENT());
            newCtp.setIntPRICERESTE(0);
            newCtp.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            newCtp.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            emg.persist(newCtp);

            TCompteClient oTCompteClient = cltP.getLgCOMPTECLIENTID();
            if (oTCompteClient != null && cltP.getDblPLAFOND() != null && cltP.getDblPLAFOND() != 0) {
                cltP.setDblQUOTACONSOMENSUELLE(
                        (cltP.getDblQUOTACONSOMENSUELLE() != null ? cltP.getDblQUOTACONSOMENSUELLE() : 0)
                                + newCtp.getIntPRICE());
                cltP.setDtUPDATED(new Date());
                emg.merge(cltP);
            }
            if (oTCompteClient != null && oTCompteClient.getDblPLAFOND() != null
                    && oTCompteClient.getDblPLAFOND() != 0) {
                oTCompteClient.setDblQUOTACONSOMENSUELLE((oTCompteClient.getDblQUOTACONSOMENSUELLE() != null
                        ? oTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + newCtp.getIntPRICE());
                oTCompteClient.setDtUPDATED(new Date());
                emg.merge(oTCompteClient);
            }
        }

    }

    public List<TPreenregistrementCompteClientTiersPayent> findClientTiersPayents(String preenregistrement,
            EntityManager emg) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> tq = emg.createQuery(
                    "SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID =?1",
                    TPreenregistrementCompteClientTiersPayent.class);
            tq.setParameter(1, preenregistrement);
            return tq.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<TPreenregistrementDetail> items(TPreenregistrement tp) {

        try {

            Query q = this.getEm().createQuery(
                    "SELECT t FROM TPreenregistrementDetail t WHERE  t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1")
                    .setParameter(1, tp.getLgPREENREGISTREMENTID());

            return q.getResultList();

        } catch (Exception ex) {
            throw ex;
        }

    }

    public Optional<TFactureDetail> checkChargedPreenregistrement(String str_REF, EntityManager emg) {

        try {
            TFactureDetail list = (TFactureDetail) emg.createQuery(
                    "SELECT o  FROM TFactureDetail o,TPreenregistrementCompteClientTiersPayent p WHERE p.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 AND p.lgPREENREGISTREMENTCOMPTECLIENTPAYENTID=o.strREF AND   (  o.lgFACTUREID.template= FALSE OR  o.lgFACTUREID.template IS NULL) ")
                    .setParameter(1, str_REF).setMaxResults(1).getSingleResult();
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

    private void createAnnulleSnapshot(TPreenregistrement preenregistrement, int montantRestant, Integer montantPaye,
            TUser o, TTypeReglement tTypeReglement) {
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
            as.setMontantTP(preenregistrement.getIntPRICE()
                    - (preenregistrement.getIntCUSTPART() - preenregistrement.getIntPRICEREMISE()));
        }
        getEm().persist(as);
    }

    private void copyRecette(TPreenregistrement newPreen, TRecettes old, TUser o) {
        TRecettes tr = old;
        LOG.log(Level.INFO, "tr {0} ", new Object[] { tr });
        tr.setLgUSERID(o);
        tr.setDtCREATED(newPreen.getDtUPDATED());
        tr.setDtUPDATED(newPreen.getDtUPDATED());
        tr.setStrCREATEDBY(o.getStrLOGIN());
        tr.setStrREFFACTURE(newPreen.getLgPREENREGISTREMENTID());
        tr.setIntAMOUNT((-1) * old.getIntAMOUNT());
        tr.setIdRecette(UUID.randomUUID().toString());
        getEm().detach(old);
        getEm().persist(tr);
    }

    public void updateFamilleStockApresAnnulation(int qty, TFamilleStock familleStock) {
        familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - qty);
        familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
        familleStock.setDtUPDATED(new Date());
        getEm().merge(familleStock);

    }

    public void updateStockApresAnnulation(TFamilleStock familleStock, int qty) {

        updateFamilleStockApresAnnulation(qty, familleStock);
    }

    public void updateNbreVenteApresAnnulation(TFamille famille, TUser ooTUser, int qty) {
        famille.setIntNOMBREVENTES((famille.getIntNOMBREVENTES() != null ? famille.getIntNOMBREVENTES() + qty : 0));
        famille.setDtLASTMOUVEMENT(new Date());
        getEm().merge(famille);
    }

    public boolean updateReelStockApresAnnulation(TFamilleStock familleStock, int qty) {
        try {

            updateStockApresAnnulation(familleStock, qty);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public void updateReelStockAnnulationDepot(TFamille famille, int int_qte, String empl, EntityManager emg) {
        try {
            TEmplacement emplacement = emg.find(TEmplacement.class, empl);
            TFamilleStock familleStock = findStock(famille.getLgFAMILLEID(), emplacement, emg);
            familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() + int_qte);
            familleStock.setIntNUMBER(familleStock.getIntNUMBER());
            familleStock.setDtUPDATED(new Date());
            emg.merge(familleStock);
        } catch (Exception e) {
        }

    }

    public TFamilleStock findStock(String OTFamille, TEmplacement emplacement, EntityManager emg) {

        try {
            TypedQuery<TFamilleStock> query = emg.createQuery(
                    "SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2",
                    TFamilleStock.class);
            query.setParameter(1, OTFamille);
            query.setParameter(2, emplacement.getLgEMPLACEMENTID());
            query.setMaxResults(1);
            TFamilleStock familleStock = query.getSingleResult();

            return familleStock;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }

    }

    private Optional<TAyantDroit> findAyantDroit(String id) {
        try {
            TAyantDroit ayantDroit = this.getEm().find(TAyantDroit.class, id);

            return Optional.ofNullable(ayantDroit);
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
            if (!forcerStock(salesParams.getQte(), salesParams.getProduitId(),
                    salesParams.getUserId().getLgEMPLACEMENTID())) {
                return json.put("success", false).put("msg", "Impossible de forcer le stock « voir le gestionnaire »");
            }
            TTypeVente typeV = typeVenteFromId(salesParams.getTypeVenteId(), emg);
            TNatureVente oTNatureVente = natureVenteFromId(salesParams.getNatureVenteId(), emg);
            TRemise oTRemise = remiseFromId(salesParams.getRemiseId(), emg);
            TUser vendeur = userFromId(salesParams.getUserVendeurId(), emg);
            TFamille tf = emg.find(TFamille.class, salesParams.getProduitId());
            TPreenregistrement preenregistrement = new TPreenregistrement(UUID.randomUUID().toString());
            preenregistrement.setLgUSERVENDEURID(vendeur != null ? vendeur : salesParams.getUserId());
            preenregistrement.setLgUSERCAISSIERID(salesParams.getUserId());
            preenregistrement.setLgUSERID(salesParams.getUserId());
            Medecin medecin = findMedecin(salesParams.getMedecinId());
            preenregistrement.setMedecin(medecin);
            preenregistrement.setIntREMISEPARA(0);
            preenregistrement.setLgREMISEID(oTRemise != null ? oTRemise.getLgREMISEID() : "");
            preenregistrement.setRemise(oTRemise);
            preenregistrement.setStrFIRSTNAMECUSTOMER("");
            preenregistrement.setStrLASTNAMECUSTOMER("");
            preenregistrement.setStrPHONECUSTOME("");
            preenregistrement.setStrINFOSCLT("");
            findClientById(salesParams.getClientId()).ifPresent(c -> {
                preenregistrement.setStrFIRSTNAMECUSTOMER(c.getStrFIRSTNAME());
                preenregistrement.setStrLASTNAMECUSTOMER(c.getStrLASTNAME());
                preenregistrement.setStrPHONECUSTOME(c.getStrADRESSE());
                preenregistrement.setClient(c);
            });
            preenregistrement.setDtCREATED(new Date());
            preenregistrement.setDtUPDATED(preenregistrement.getDtCREATED());
            preenregistrement.setLgNATUREVENTEID(oTNatureVente);
            preenregistrement.setLgTYPEVENTEID(typeV);
            preenregistrement.setIntPRICE(0);
            preenregistrement.setIntACCOUNT(0);
            preenregistrement.setIntPRICEOTHER(0);
            preenregistrement.setBISCANCEL(false);
            preenregistrement.setBWITHOUTBON(false);
            preenregistrement.setIntCUSTPART(0);
            preenregistrement.setMontantTva(0);
            preenregistrement.setIntPRICEREMISE(0);
            preenregistrement.setCopy(Boolean.FALSE);
            preenregistrement.setIntSENDTOSUGGESTION(0);
            preenregistrement.setStrSTATUT(salesParams.getStatut());
            TPreenregistrementDetail dp = addPreenregistrementItem(preenregistrement, tf, salesParams.getQte(),
                    salesParams.getQteServie(), salesParams.getQteUg(), salesParams.getItemPu(), emg);
            preenregistrement.setCmuAmount(computeCmuAmount(dp));
            if (!salesParams.isDepot()) {
                preenregistrement.setStrTYPEVENTE(VENTE_ASSURANCE);
                if (!salesParams.getTypeVenteId().equals(VENTE_AVEC_CARNET)) {
                    findAyantDroit(salesParams.getAyantDroitId()).ifPresent(a -> {
                        preenregistrement.setStrFIRSTNAMECUSTOMER(a.getStrFIRSTNAME());
                        preenregistrement.setStrLASTNAMECUSTOMER(a.getStrLASTNAME());
                        preenregistrement.setAyantDroit(a);
                    });

                }
                preenregistrement.setPkBrand("");
                if (!salesParams.isDevis()) {
                    preenregistrement
                            .setStrREF(buildRefTmp(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID())
                                    .getReferenceTemp());
                    emg.persist(preenregistrement);
                    createPreenregistrementTierspayant(salesParams.getTierspayants(), preenregistrement, emg);

                } else {
                    preenregistrement
                            .setStrREF(buildRefDevis(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID())
                                    .getReference());
                    preenregistrement.setStrREFBON(salesParams.getBonRef());
                    preenregistrement.setStrREFTICKET(DateConverter.getShortId(10));
                    emg.persist(preenregistrement);
                    List<TCompteClientTiersPayant> clientTiersPayants = findCompteClientTierspayantByClientId(
                            salesParams.getClientId(), emg);
                    for (TCompteClientTiersPayant fda : clientTiersPayants) {
                        createTPreenregistrementCompteClientTiersPayent(preenregistrement, fda, 100,
                                salesParams.getBonRef(), emg);
                    }

                }
            } else {
                TEmplacement emplacement = emg.find(TEmplacement.class, salesParams.getEmplacementId());
                preenregistrement.setPkBrand(emplacement.getLgEMPLACEMENTID());
                preenregistrement.setLgREMISEID(salesParams.getRemiseDepot() + "");
                preenregistrement.setIntPRICEREMISE(
                        calculRemiseDepot(preenregistrement.getIntPRICE(), salesParams.getRemiseDepot()));
                preenregistrement
                        .setStrTYPEVENTE((salesParams.getTypeDepoId().equals("1") ? VENTE_COMPTANT : VENTE_ASSURANCE));
                preenregistrement.setStrREF(
                        buildRefTmp(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID()).getReferenceTemp());
                emg.persist(preenregistrement);
            }
            emg.persist(dp);
            JSONObject data = new JSONObject();
            data.put("lgPREENREGISTREMENTID", preenregistrement.getLgPREENREGISTREMENTID());
            data.put("strREF", preenregistrement.getStrREF());
            data.put("intPRICE", preenregistrement.getIntPRICE());
            data.put("intPRICEREMISE", preenregistrement.getIntPRICEREMISE());
            json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
            afficheurProduit(dp.getLgFAMILLEID().getStrNAME(), dp.getIntQUANTITY(), dp.getIntPRICEUNITAIR(),
                    dp.getIntPRICE());
            return json;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

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
            if (!forcerStock(salesParams.getQte(), salesParams.getProduitId(),
                    salesParams.getUserId().getLgEMPLACEMENTID())) {
                return json.put("success", false).put("msg", "Impossible de forcer le stock « voir le gestionnaire »");
            }
            TFamille tf = emg.find(TFamille.class, salesParams.getProduitId());
            TTypeVente oTTypeVente = typeVenteFromId(salesParams.getTypeVenteId(), emg);
            TNatureVente oTNatureVente = natureVenteFromId(salesParams.getNatureVenteId(), emg);
            TRemise oTRemise = remiseFromId(salesParams.getRemiseId(), emg);
            TUser vendeur = userFromId(salesParams.getUserVendeurId(), emg);
            TPreenregistrement op = new TPreenregistrement(UUID.randomUUID().toString());
            op.setLgUSERVENDEURID(vendeur != null ? vendeur : salesParams.getUserId());
            op.setLgUSERCAISSIERID(salesParams.getUserId());
            op.setLgUSERID(salesParams.getUserId());
            op.setIntREMISEPARA(0);
            op.setPkBrand("");
            Medecin medecin = findMedecin(salesParams.getMedecinId());
            op.setMedecin(medecin);
            if (!salesParams.isDevis()) {
                op.setStrREF(
                        buildRefTmp(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID()).getReferenceTemp());
            } else {
                findClientById(salesParams.getClientId()).ifPresent(my -> {
                    op.setClient(my);
                });
                op.setStrREF(buildRefDevis(LocalDate.now(), salesParams.getUserId().getLgEMPLACEMENTID())
                        .getReferenceTemp());
                op.setStrREFTICKET(DateConverter.getShortId(10));
            }
            op.setLgREMISEID(oTRemise != null ? oTRemise.getLgREMISEID() : "");
            op.setRemise(oTRemise);
            op.setStrFIRSTNAMECUSTOMER("");
            op.setStrLASTNAMECUSTOMER("");
            op.setStrPHONECUSTOME("");
            op.setStrINFOSCLT("");
            op.setDtCREATED(new Date());
            op.setDtUPDATED(op.getDtCREATED());
            op.setLgNATUREVENTEID(oTNatureVente);
            op.setLgTYPEVENTEID(oTTypeVente);
            op.setIntPRICE(0);
            op.setIntACCOUNT(0);
            op.setIntPRICEOTHER(0);
            op.setBISCANCEL(false);
            op.setBWITHOUTBON(false);
            op.setIntCUSTPART(0);
            op.setIntPRICEREMISE(0);
            op.setIntSENDTOSUGGESTION(0);
            op.setMontantTva(0);
            op.setCopy(Boolean.FALSE);
            op.setStrSTATUTVENTE(commonparameter.statut_nondiffere);
            op.setStrSTATUT(salesParams.getStatut());
            op.setStrTYPEVENTE(Parameter.KEY_VENTE_NON_ORDONNANCEE);
            TPreenregistrementDetail dt = addPreenregistrementItem(op, tf, salesParams.getQte(),
                    salesParams.getQteServie(), salesParams.getQteUg(), salesParams.getItemPu(), emg);
            op.setCmuAmount(computeCmuAmount(dt));
            emg.persist(op);
            emg.persist(dt);
            JSONObject data = new JSONObject();
            data.put("lgPREENREGISTREMENTID", op.getLgPREENREGISTREMENTID());
            data.put("strREF", op.getStrREF());
            data.put("intPRICE", op.getIntPRICE());
            json.put("success", true).put("msg", "Opération effectuée avec success").put("data", data);
            afficheurProduit(dt.getLgFAMILLEID().getStrNAME(), dt.getIntQUANTITY(), dt.getIntPRICEUNITAIR(),
                    dt.getIntPRICE());
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

    public TPreenregistrementDetail addPreenregistrementItem(TPreenregistrement tp, TFamille OTFamille, int qte,
            int qteServie, int qteUg, Integer pu, EntityManager emg) {
        try {
            TCodeTva tva = OTFamille.getLgCODETVAID();
            Optional<TParameters> param = findParamettre("KEY_TAKE_INTO_ACCOUNT");
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

            if (param.isPresent()) {
                if (Integer.parseInt(param.get().getStrVALUE().trim()) == 1) {
                    if (OTFamille.getLgZONEGEOID().getBoolACCOUNT() && OTFamille.getBoolACCOUNT()) {
                        tp.setIntACCOUNT(tp.getIntACCOUNT() + tpd.getIntPRICE());
                    } else {
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

    public Optional<TPreenregistrementDetail> findItemByProduitAndVente(String idVente, String idProduit) {
        try {
            TypedQuery<TPreenregistrementDetail> detail = this.getEm().createQuery(
                    "SELECT o FROM TPreenregistrementDetail o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?2 ",
                    TPreenregistrementDetail.class);
            detail.setParameter(1, idProduit);
            detail.setParameter(2, idVente);
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
        Double ht = amount / (1 + (Double.valueOf(codeTva.getIntVALUE()) / 100));
        return amount - ht.intValue();
    }

    @Override
    public JSONObject addPreenregistrementItem(SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement tp = emg.find(TPreenregistrement.class, params.getVenteId());
            Optional<TPreenregistrementDetail> detailOp = findItemByProduitAndVente(params.getVenteId(),
                    params.getProduitId());
            TFamille famille = emg.find(TFamille.class, params.getProduitId());
            TCodeTva tva = famille.getLgCODETVAID();
            TPreenregistrementDetail tpd;
            if (detailOp.isPresent()) {
                tpd = detailOp.get();
                int oldCmuAmount = computeCmuAmount(tpd);
                int qty = tpd.getIntQUANTITY() + params.getQte();
                if (!forcerStock(qty, params.getProduitId(), tp.getLgUSERID().getLgEMPLACEMENTID())) {
                    return json.put("success", false).put("msg",
                            "Impossible de forcer le stock « voir le gestionnaire »");
                }
                Integer oldPrice = tpd.getIntPRICE();
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
                tp.setIntPRICE(tp.getIntPRICE() + tpd.getIntPRICE() - oldPrice);
                tp.setMontantTva(tp.getMontantTva() + tpd.getMontantTva() - montantTva);
                tp.setCmuAmount((tp.getCmuAmount() - oldCmuAmount) + computeCmuAmount(tpd));
                if (tpd.getBoolACCOUNT()) {
                    tp.setIntACCOUNT(tp.getIntPRICE());
                }
                emg.merge(tpd);
                afficheurProduit(tpd.getLgFAMILLEID().getStrNAME(), tpd.getIntQUANTITY(), tpd.getIntPRICEUNITAIR(),
                        tpd.getIntPRICE());
            } else {
                if (!forcerStock(params.getQte(), params.getProduitId(), tp.getLgUSERID().getLgEMPLACEMENTID())) {
                    return json.put("success", false).put("msg",
                            "Impossible de forcer le stock « voir le gestionnaire »");
                }
                TPreenregistrementDetail dp = addPreenregistrementItem(tp, famille, params.getQte(),
                        params.getQteServie(), params.getQteUg(), params.getItemPu(), emg);
                tp.setCmuAmount(tp.getCmuAmount() + computeCmuAmount(dp));
                emg.persist(dp);
                afficheurProduit(dp.getLgFAMILLEID().getStrNAME(), dp.getIntQUANTITY(), dp.getIntPRICEUNITAIR(),
                        dp.getIntPRICE());
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
    public JSONObject updateTPreenregistrementDetail(SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrementDetail detail = emg.find(TPreenregistrementDetail.class, params.getItemId());
            Integer int_QUANTITY_SERVED_OLD = detail.getIntQUANTITYSERVED(), oldPrice = detail.getIntPRICE(),
                    montantTva = detail.getMontantTva();
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
                    if (checkPersmission == 1 && !checkpricevente(famille, params.getItemPu(), emg)) {
                        return json.put("success", false).put("decondition", false).put("msg",
                                "Impossible. Vous n'ête pas autorisé à modifier du prix de vente");

                    }

                }

                // send sms
                detail.setIntPRICEUNITAIR(params.getItemPu());
            }
            detail.setIntQUANTITY(params.getQte());
            detail.setIntPRICE(detail.getIntPRICEUNITAIR() * params.getQte());
            detail.setMontantTva(calculeTva(famille.getLgCODETVAID(), detail.getIntPRICE()));
            detail.setIntQUANTITYSERVED(params.getQteServie());
            int int_AVOIR_SERVED = (params.getQteServie() - int_QUANTITY_SERVED_OLD)
                    + (detail.getIntAVOIRSERVED() != null ? detail.getIntAVOIRSERVED() : 0);
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
            afficheurProduit(detail.getLgFAMILLEID().getStrNAME(), detail.getIntQUANTITY(), detail.getIntPRICEUNITAIR(),
                    detail.getIntPRICE());
            return json.put("success", true).put("msg", "Opération effectuée avec success").put("data",
                    data)/* .put("nets", shownetpayVno(tp)) */;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "updateTPreenregistrementDetail ------>>>", e);
            try {
                json.put("success", false).put("decondition", false).put("msg", "Erreur :: l'opération a échouée");
            } catch (JSONException ex) {

            }
        }
        return json;

    }

    public void updateLogFile(TUser user, String ref, String desc, TypeLog typeLog, Object t, EntityManager emg) {
        TEventLog eventLog = new TEventLog(UUID.randomUUID().toString());
        eventLog.setLgUSERID(user);
        eventLog.setDtCREATED(new Date());
        eventLog.setDtUPDATED(eventLog.getDtCREATED());
        eventLog.setStrCREATEDBY(user.getStrLOGIN());
        eventLog.setStrSTATUT(commonparameter.statut_enable);
        eventLog.setStrTABLECONCERN(t.getClass().getName());
        eventLog.setTypeLog(typeLog);
        eventLog.setStrTYPELOG(ref);
        eventLog.setStrDESCRIPTION(desc + " référence [" + ref + " ]");
        emg.persist(eventLog);

    }

    @Override
    public TPreenregistrement removePreenregistrementDetail(String itemId) {
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

    public boolean checkIsVentePossible(TFamilleStock OTFamilleStock, int qte) {
        return OTFamilleStock.getIntNUMBERAVAILABLE() >= qte;
    }

    private boolean boonDejaUtilise(String refBon, String cmpt) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q = getEm().createQuery(
                    "SELECT o FROM  TPreenregistrementCompteClientTiersPayent o WHERE o.strREFBON=?1 AND o.lgCOMPTECLIENTTIERSPAYANTID.lgTIERSPAYANTID.lgTIERSPAYANTID =?2 AND o.strSTATUT =?3 AND o.lgPREENREGISTREMENTID.strSTATUT=?4",
                    TPreenregistrementCompteClientTiersPayent.class);
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

    public void createTPreenregistrementCompteClientTiersPayent(TPreenregistrement OTPreenregistrement,
            TCompteClientTiersPayant OTCompteClientTiersPayant, int taux, String str_REFBON, EntityManager emg)
            throws Exception {
        Date today = new Date();
        TPreenregistrementCompteClientTiersPayent OTPreenregistrementCompteClientTiersPayent = new TPreenregistrementCompteClientTiersPayent(
                UUID.randomUUID().toString());
        OTPreenregistrementCompteClientTiersPayent.setLgPREENREGISTREMENTID(OTPreenregistrement);
        OTPreenregistrementCompteClientTiersPayent.setLgCOMPTECLIENTTIERSPAYANTID(OTCompteClientTiersPayant);
        OTPreenregistrementCompteClientTiersPayent.setDtCREATED(today);
        OTPreenregistrementCompteClientTiersPayent.setDtUPDATED(today);
        OTPreenregistrementCompteClientTiersPayent.setIntPERCENT(taux);
        OTPreenregistrementCompteClientTiersPayent.setIntPRICE(0);
        OTPreenregistrementCompteClientTiersPayent.setIntPRICERESTE(0);
        OTPreenregistrementCompteClientTiersPayent.setStrREFBON(str_REFBON);
        OTPreenregistrementCompteClientTiersPayent.setStrSTATUTFACTURE("unpaid");
        OTPreenregistrementCompteClientTiersPayent
                .setDblQUOTACONSOVENTE(OTCompteClientTiersPayant.getDblQUOTACONSOVENTE() != null
                        ? OTCompteClientTiersPayant.getDblQUOTACONSOVENTE() : 0);
        OTPreenregistrementCompteClientTiersPayent.setStrSTATUT(OTPreenregistrement.getStrSTATUT());
        emg.persist(OTPreenregistrementCompteClientTiersPayent);
        OTPreenregistrement.setStrINFOSCLT(OTCompteClientTiersPayant.getLgCOMPTECLIENTID().getLgCOMPTECLIENTID());
        if (OTCompteClientTiersPayant.getIntPRIORITY() == 1) {
            OTPreenregistrement.setStrREFBON(str_REFBON);

        }

    }

    public void createPreenregistrementTierspayant(List<TiersPayantParams> tierspayants,
            TPreenregistrement OTPreenregistrement, EntityManager emg) throws Exception {
        for (TiersPayantParams v : tierspayants) {
            TCompteClientTiersPayant OTCompteClientTiersPayant = emg.find(TCompteClientTiersPayant.class,
                    v.getCompteTp());
            createTPreenregistrementCompteClientTiersPayent(OTPreenregistrement, OTCompteClientTiersPayant, v.getTaux(),
                    v.getNumBon(), emg);
        }

    }

    public boolean checkpricevente(TFamille OTFamille, int newPu, EntityManager emg) {
        try {
            TParameters OTParameters = emg.find(TParameters.class, commonparameter.KEY_MAX_PRICE_POURCENT_VENTE);
            int int_PRICE_COEF = (OTFamille.getIntPRICE() * Integer.valueOf(OTParameters.getStrVALUE())) / 100;
            if ((!((OTFamille.getIntPRICE() - int_PRICE_COEF) <= newPu))
                    || (!(newPu <= (OTFamille.getIntPRICE() + int_PRICE_COEF)))) {
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
            // emg.getTransaction().begin();
            if (salesParams.getTypeVenteId().equals(Parameter.VENTE_COMPTANT)
                    && !tp.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_COMPTANT)) {
                List<TPreenregistrementCompteClientTiersPayent> list = findClientTiersPayents(
                        tp.getLgPREENREGISTREMENTID(), emg);
                list.forEach((op) -> {
                    emg.remove(op);
                });
            }
            tp.setLgTYPEVENTEID(typeVenteFromId(salesParams.getTypeVenteId(), emg));
            tp.setStrTYPEVENTE(salesParams.getTypeVenteId().equals(Parameter.VENTE_COMPTANT)
                    ? Parameter.KEY_VENTE_NON_ORDONNANCEE : Parameter.KEY_VENTE_ORDONNANCE);
            tp.setDtUPDATED(new Date());
            emg.merge(tp);
            // emg.getTransaction().commit();
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

    private boolean checkAvoir(List<TPreenregistrementDetail> list) {
        java.util.function.Predicate<TPreenregistrementDetail> p = e -> e.getBISAVOIR();
        return list.stream().anyMatch(p);
    }

    private boolean checkOrdonnancier(List<TPreenregistrementDetail> list) {
        java.util.function.Predicate<TPreenregistrementDetail> p = e -> (e.getLgFAMILLEID().isScheduled()
                && !e.getLgFAMILLEID().getIntT().trim().isEmpty());
        return list.stream().anyMatch(p);
    }

    private TTypeReglement findById(String id) {
        if (StringUtils.isNotEmpty(id)) {
            if ("Especes".equals(id) || "4".equals(id)) {
                return this.getEm().find(TTypeReglement.class, "1");
            } else {
                return this.getEm().find(TTypeReglement.class, id);
            }
        }
        return this.getEm().find(TTypeReglement.class, "1");
    }

    private TTypeRecette findTTypeRecetteById(EntityManager emg) {
        return emg.find(TTypeRecette.class, "1");
    }

    private String statutDiff(String v) {
        if (!v.equals("4")) {
            return commonparameter.statut_nondiffere;
        }
        return commonparameter.statut_differe;
    }

    private TModeReglement findByIdMod(String id) {
        return this.getEm().find(TModeReglement.class, id);
    }

    private TModeReglement findModeReglement(String idTypeRegl) {
        TModeReglement modeReglement;
        switch (idTypeRegl) {
        case "1":
        case "4":
            modeReglement = findByIdMod("1");
            break;
        case "2":
            modeReglement = findByIdMod("2");
            break;
        case "3":
            modeReglement = findByIdMod("5");
            break;
        case "6":
            modeReglement = findByIdMod("7");
            break;
        case "5":
            modeReglement = findByIdMod("6");
            break;
        case "7":
            modeReglement = findByIdMod(MODE_ORANGE);
            break;
        case "8":
            modeReglement = findByIdMod("8");
            break;
        case "9":
            modeReglement = findByIdMod("9");
            break;
        case "10":
            modeReglement = findByIdMod("11");
            break;
        default:
            modeReglement = findByIdMod(idTypeRegl);
            break;

        }
        return modeReglement;
    }

    public TReglement createTReglement(TUser user, TModeReglement modeReglement, String compteClient, String ref,
            String banque, String lieu, String comment, String statut, String customer) {
        TReglement reglement = new TReglement();
        reglement.setLgREGLEMENTID(UUID.randomUUID().toString());
        reglement.setStrBANQUE(banque);
        reglement.setStrCODEMONNAIE("Fr");
        reglement.setStrCOMMENTAIRE(comment);
        reglement.setStrLIEU(lieu);
        reglement.setStrFIRSTLASTNAME(customer);
        reglement.setStrREFRESSOURCE(ref);
        reglement.setIntTAUX(0);
        reglement.setDtCREATED(new Date());
        reglement.setDtUPDATED(reglement.getDtCREATED());
        reglement.setLgMODEREGLEMENTID(modeReglement);
        reglement.setDtREGLEMENT(reglement.getDtCREATED());
        reglement.setLgUSERID(user);
        reglement.setBoolCHECKED(true);
        reglement.setStrSTATUT(statut);
        this.getEm().persist(reglement);
        return reglement;
    }

    public void addDiffere(TCompteClient oTCompteClient, TPreenregistrement p, Integer montantPaye, TUser user) {
        TPreenregistrementCompteClient oTPreenregistrementCompteClient = new TPreenregistrementCompteClient(
                UUID.randomUUID().toString());
        oTPreenregistrementCompteClient.setDtCREATED(new Date());
        oTPreenregistrementCompteClient.setDtUPDATED(oTPreenregistrementCompteClient.getDtCREATED());
        oTPreenregistrementCompteClient.setLgCOMPTECLIENTID(oTCompteClient);
        oTPreenregistrementCompteClient.setLgPREENREGISTREMENTID(p);
        oTPreenregistrementCompteClient.setLgUSERID(user);
        oTPreenregistrementCompteClient.setIntPRICE(p.getIntCUSTPART() == 0 ? p.getIntPRICE() - p.getIntPRICEREMISE()
                : p.getIntCUSTPART() - p.getIntPRICEREMISE());
        oTPreenregistrementCompteClient.setIntPRICERESTE(oTPreenregistrementCompteClient.getIntPRICE() - montantPaye);
        oTPreenregistrementCompteClient.setStrSTATUT(STATUT_IS_CLOSED);
        this.getEm().persist(oTPreenregistrementCompteClient);
    }

    public TRecettes addRecette(Integer montant, String desc, String refId, TUser user, EntityManager emg) {
        TRecettes recette = new TRecettes();
        recette.setIdRecette(UUID.randomUUID().toString());
        recette.setLgTYPERECETTEID(findTTypeRecetteById(emg));
        recette.setIntAMOUNT(montant.doubleValue());
        recette.setDtCREATED(new Date());
        recette.setDtUPDATED(recette.getDtCREATED());
        recette.setStrDESCRIPTION(desc);
        recette.setStrREFFACTURE(refId);
        recette.setStrCREATEDBY(user.getStrLOGIN());
        recette.setLgUSERID(user);
        emg.persist(recette);

        return recette;
    }

    private TCompteClient findByClientId(String id, EntityManager emg) {
        if (id == null || "".equals(id)) {
            return null;
        }
        try {
            TypedQuery<TCompteClient> tq = emg.createQuery(
                    "SELECT o FROM TCompteClient o WHERE o.lgCLIENTID.lgCLIENTID=?1 ", TCompteClient.class);
            tq.setParameter(1, id);
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private Optional<TTypeMvtCaisse> getOne(String id) {
        try {
            return Optional.ofNullable(this.getEm().find(TTypeMvtCaisse.class, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public JSONObject updateVenteClotureAssurance(ClotureVenteParams clotureVenteParams) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        TPreenregistrement tp = null;
        try {
            final TUser tUser = clotureVenteParams.getUserId();
            if (!checkResumeCaisse(tUser, emg).isPresent()) {
                json.put("success", false);
                json.put("msg", "Désolé votre caisse est fermée. Veuillez l'ouvrir avant de proceder à validation");
                return json;
            }

            boolean isDiff = false;
            tp = emg.find(TPreenregistrement.class, clotureVenteParams.getVenteId());
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
            TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId());
            Optional<TTypeMvtCaisse> typeMvtCaisse = getOne(KEY_PARAM_MVT_VENTE_ORDONNANCE);
            List<TPreenregistrementDetail> lstTPreenregistrementDetail = items(tp);
            int montant = tp.getIntPRICE();
            if (diffAmount(montant, lstTPreenregistrementDetail)) {
                json.put("success", false);
                json.put("msg", "Désolé impossible de terminer la vente. Veuillez recalculer le montant de la vente ");
                json.put("codeError", 0);
                return json;
            }
            int amount;
            boolean isAvoir = checkAvoir(lstTPreenregistrementDetail);
            String statut = statutDiff(clotureVenteParams.getTypeRegleId());
            TUser vendeur = userFromId(clotureVenteParams.getUserVendeurId(), emg);
            TCompteClient compteClient = findByClientId(clotureVenteParams.getClientId(), emg);
            Optional<TParameters> takeInAcount = findParamettre("KEY_TAKE_INTO_ACCOUNT");
            TClient client = findClientById(clotureVenteParams.getClientId()).orElse(null);

            if (tp.getMedecin() == null && gererOrdoncier()) {
                boolean isOrdonnancier = checkOrdonnancier(lstTPreenregistrementDetail);
                if (isOrdonnancier) {
                    json.put("success", false);
                    json.put("msg", "Ajouter le médecin qui prescrit l'ordonnance");
                    json.put("codeError", 1);
                    return json;
                }

            }
            JSONObject result = createPreenregistrementCompteClientTierspayant(clotureVenteParams.getTierspayants(), tp,
                    clotureVenteParams.isSansBon(), tUser);
            if (result.has("success")) {
                return result;
            }
            if (clotureVenteParams.getTypeVenteId().equals(VENTE_ASSURANCE_ID)) {
                Optional<TAyantDroit> ayantDroitop = findAyantDroit(clotureVenteParams.getAyantDroitId());
                if (ayantDroitop.isPresent()) {
                    TAyantDroit ayantDroit = ayantDroitop.get();
                    tp.setStrFIRSTNAMECUSTOMER(ayantDroit.getStrFIRSTNAME());
                    tp.setStrLASTNAMECUSTOMER(ayantDroit.getStrLASTNAME());
                    tp.setStrNUMEROSECURITESOCIAL(ayantDroit.getStrNUMEROSECURITESOCIAL());
                    tp.setAyantDroit(ayantDroit);
                    tp.setStrPHONECUSTOME(client.getStrADRESSE());
                }

                amount = montant - tp.getIntPRICEREMISE();
            } else {
                amount = montant - tp.getIntPRICEREMISE();
                if (client != null) {
                    tp.setStrFIRSTNAMECUSTOMER(client.getStrFIRSTNAME());
                    tp.setStrLASTNAMECUSTOMER(client.getStrLASTNAME());
                    tp.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
                    tp.setStrPHONECUSTOME(client.getStrADRESSE());
                    tp.setClient(client);
                }

            }

            if (clotureVenteParams.getTypeRegleId().equals(DateConverter.REGL_DIFF)) {
                isDiff = true;
                addDiffere(compteClient, tp, clotureVenteParams.getMontantPaye(), clotureVenteParams.getUserId());

            }
            TTypeVente oTTypeVente = typeVenteFromId(clotureVenteParams.getTypeVenteId(), emg);
            TReglement tReglement = createTReglement(clotureVenteParams.getUserId(), modeReglement, "",
                    tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getBanque(), clotureVenteParams.getLieux(),
                    clotureVenteParams.getCommentaire(), STATUT_IS_CLOSED, "");
            tp.setBWITHOUTBON(clotureVenteParams.isSansBon());
            tp.setLgTYPEVENTEID(oTTypeVente);
            tp.setLgREGLEMENTID(tReglement);
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(STATUT_IS_CLOSED);
            tp.setStrTYPEVENTE(VENTE_ASSURANCE);
            tp.setStrSTATUTVENTE(statut);
            tp.setIntPRICEOTHER(tp.getIntPRICE());
            if (!tp.getCopy()) {
                tp.setDtUPDATED(new Date());
            }

            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()),
                    clotureVenteParams.getUserId().getLgEMPLACEMENTID()).getReference());

            boolean keyAccount = test.test(takeInAcount);
            if (keyAccount) {
                tp.setIntPRICEOTHER(tp.getIntACCOUNT());
            }
            if (amount > 0) {

                addRecette(clotureVenteParams.getMontantPaye(), VENTE_ASSURANCE, tp.getLgPREENREGISTREMENTID(),
                        clotureVenteParams.getUserId(), emg);
            }
            TTypeReglement typeReglement = findById(clotureVenteParams.getTypeRegleId());
            MvtTransaction mvtTransaction = addTransaction(tUser, tp, montant, amount,
                    clotureVenteParams.getMontantRecu(), true, typeReglement, typeMvtCaisse.get(),
                    clotureVenteParams.getPartTP(), clotureVenteParams.getMontantPaye(), clotureVenteParams.getMarge(),
                    isDiff, clotureVenteParams.getTypeRegleId());
            addReglement(tp, mvtTransaction, clotureVenteParams);

            carnetAsDepotService.create(tp, mvtTransaction, this.getTiersPayant());

            mvtProduitService.updateVenteStock(tp, lstTPreenregistrementDetail);
            tp.setCompletionDate(new Date());
            emg.merge(tp);

            if (clotureVenteParams.getMontantRemis() > 0) {
                afficheurMontantAPayer(clotureVenteParams.getMontantRemis(), " MONNAIE:");
            }
            json.put("success", true).put("copy", tp.getCopy()).put("msg", "Opération effectuée avec success")
                    .put("ref", tp.getLgPREENREGISTREMENTID());
        } catch (Exception e) {

            LOG.log(Level.SEVERE, String.format("Erreur a la closture de la vente %s,%s,%s date :: %s",
                    tp.getLgPREENREGISTREMENTID(), tp.getStrREF(), tp.getLgUSERID().getLgUSERID(), LocalDateTime.now()),
                    e);

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

    private int computeSumOfExclusVenteItemFromCa(TPreenregistrement tp) {
        return tp.getTPreenregistrementDetailCollection().stream().filter(
                e -> !e.getBoolACCOUNT() && tp.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Constant.VENTE_COMPTANT_ID))
                .mapToInt(TPreenregistrementDetail::getIntPRICE).sum();
    }

    private void addReglement(TPreenregistrement tp, MvtTransaction mt, ClotureVenteParams clotureVenteParams) {
        int totalAmountNonCa = computeSumOfExclusVenteItemFromCa(tp);
        Set<VenteReglementDTO> reglements = clotureVenteParams.getReglements();
        LocalDateTime mvtDate = DateCommonUtils.convertDateToLocalDateTime(tp.getDtUPDATED());
        if (CollectionUtils.isNotEmpty(reglements)) {
            List<VenteReglementDTO> reglementsList = reglements.stream()
                    .sorted(Comparator.comparing(VenteReglementDTO::getMontant, Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            if (reglements.size() > 1) {
                VenteReglementDTO first = reglementsList.get(0);

                VenteReglementDTO last = reglementsList.get(reglementsList.size() - 1);
                int totalUgNet;
                int amountNonCa;
                if (Objects.nonNull(mt.getMontantttcug()) && mt.getMontantttcug().compareTo(0) != 0) {

                    if (first.getMontant() >= mt.getMontantnetug()) {
                        first.setMontantTttcug(mt.getMontantttcug());
                        first.setMontantnetug(mt.getMontantnetug());

                    } else {
                        totalUgNet = mt.getMontantnetug() - first.getMontant();
                        first.setMontantTttcug(first.getMontant());
                        first.setMontantnetug(first.getMontant());
                        last.setMontantnetug(totalUgNet);
                        last.setMontantTttcug(totalUgNet);
                    }

                }
                if (first.getMontant() >= totalAmountNonCa) {
                    first.setAmountNonCa(totalAmountNonCa);
                } else {
                    amountNonCa = totalAmountNonCa - first.getMontant();
                    first.setAmountNonCa(first.getMontant());
                    last.setAmountNonCa(amountNonCa);
                }

                venteReglementService.createVenteReglement(tp, first, findById(first.getTypeReglement()), mvtDate);
                venteReglementService.createVenteReglement(tp, last, findById(last.getTypeReglement()), mvtDate);

            } else {
                this.venteReglementService.createNew(tp,
                        findById(reglements.stream().findFirst().get().getTypeReglement()), mt);
            }

        }

    }

    private void copyVenteReglement(List<VenteReglement> reglements, TPreenregistrement copy) {
        reglements.forEach(v -> this.venteReglementService.createCopyVenteReglement(copy, v));
    }

    @Override
    public JSONObject updateVenteClotureComptant(ClotureVenteParams clotureVenteParams) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        TPreenregistrement tp = null;
        try {
            final TUser tUser = clotureVenteParams.getUserId();
            if (!checkResumeCaisse(tUser, emg).isPresent()) {
                json.put("success", false);
                json.put("msg", "Désolé votre caisse est fermée. Veuillez l'ouvrir avant de proceder à validation");
                return json;
            }
            tp = emg.find(TPreenregistrement.class, clotureVenteParams.getVenteId());
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
            List<TPreenregistrementDetail> lstTPreenregistrementDetail = items(tp);
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
            TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId());
            Optional<TTypeMvtCaisse> typeMvtCaisse = getOne(KEY_PARAM_MVT_VENTE_NON_ORDONNANCEE);
            int montant = tp.getIntPRICE();
            if (diffAmount(montant, lstTPreenregistrementDetail)) {
                json.put("success", false);
                json.put("msg", "Désolé impossible de terminer la vente. Veuillez recalculer le montant de la vente ");
                json.put("codeError", 0);
                return json;
            }

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
            Optional<TParameters> toInAccount = findParamettre("KEY_TAKE_INTO_ACCOUNT");
            TClient client = findClientById(clotureVenteParams.getClientId()).orElse(null);
            if (client != null) {
                tp.setStrFIRSTNAMECUSTOMER(client.getStrFIRSTNAME());
                tp.setStrLASTNAMECUSTOMER(client.getStrLASTNAME());
                tp.setStrPHONECUSTOME(client.getStrADRESSE());
                tp.setClient(client);
            }

            if (clotureVenteParams.getTypeRegleId().equals(REGL_DIFF)) {
                addDiffere(compteClient, tp, clotureVenteParams.getMontantPaye(), clotureVenteParams.getUserId());
            }
            TReglement tReglement = createTReglement(clotureVenteParams.getUserId(), modeReglement, "",
                    tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getBanque(), clotureVenteParams.getLieux(),
                    clotureVenteParams.getCommentaire(), STATUT_IS_CLOSED, "");
            tp.setBWITHOUTBON(false);
            tp.setLgREGLEMENTID(tReglement);
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(STATUT_IS_CLOSED);
            tp.setStrSTATUTVENTE(statut);
            tp.setIntPRICEOTHER(tp.getIntPRICE());
            updateUgData(clotureVenteParams.getData(), tp);
            if (!tp.getCopy()) {
                tp.setDtUPDATED(new Date());
            }

            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()),
                    clotureVenteParams.getUserId().getLgEMPLACEMENTID()).getReference());
            java.util.function.Predicate<Optional<TParameters>> testP = e -> {
                if (e.isPresent()) {
                    return Integer.parseInt(e.get().getStrVALUE().trim()) == 1;
                }
                return false;
            };
            boolean keyAccount = testP.test(toInAccount);
            if (keyAccount) {
                tp.setIntPRICEOTHER(tp.getIntACCOUNT());
            }
            TTypeReglement tTypeReglement = findById(clotureVenteParams.getTypeRegleId());
            addRecette(clotureVenteParams.getMontantPaye(), VENTE_COMPTANT, tp.getLgPREENREGISTREMENTID(),
                    clotureVenteParams.getUserId(), emg);
            MvtTransaction mt = addTransaction(tUser, tp, montant, tp.getIntACCOUNT(), amount,
                    clotureVenteParams.getMontantRecu(), true, CategoryTransaction.CREDIT,
                    TypeTransaction.VENTE_COMPTANT, tTypeReglement, typeMvtCaisse.get(),
                    clotureVenteParams.getMontantPaye(), clotureVenteParams.getMarge(), tp.getIntACCOUNT(),
                    clotureVenteParams.getData());
            addReglement(tp, mt, clotureVenteParams);

            this.mvtProduitService.updateVenteStock(tp, lstTPreenregistrementDetail);
            tp.setCompletionDate(new Date());
            emg.merge(tp);
            afficheurMontantAPayer(clotureVenteParams.getMontantRemis(), " MONNAIE:");

            json.put("success", true).put("msg", "Opération effectuée avec success").put("copy", tp.getCopy())
                    .put("ref", tp.getLgPREENREGISTREMENTID());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, String.format("Erreur a la closture de la vente %s,%s,%s date :: %s",
                    tp.getLgPREENREGISTREMENTID(), tp.getStrREF(), tp.getLgUSERID().getLgUSERID(), LocalDateTime.now()),
                    e);

            try {
                json.put("success", false).put("msg", "Erreur: Echec de validation de la vente");
                json.put("codeError", 0);
            } catch (JSONException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }

    public boolean checkRefBonIsUse(String refBon, TCompteClientTiersPayant oTCompteClientTiersPayant,
            EntityManager emg) {

        try {

            if (!"".equals(refBon)) {
                TPreenregistrementCompteClientTiersPayent op = emg.createQuery(
                        "SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?1 AND t.strREFBON = ?2 AND t.strSTATUT = ?3",
                        TPreenregistrementCompteClientTiersPayent.class)
                        .setParameter(1, oTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID())
                        .setParameter(2, refBon).setParameter(3, STATUT_IS_CLOSED).getSingleResult();
                return (op != null);

            }
            return false;

        } catch (Exception ex) {
            return false;
        }

    }

    private TPreenregistrementCompteClientTiersPayent getTPreenregistrementCompteClientTiersPayent(
            String lg_PREENREGISTREMENT_ID, String lg_COMPTE_CLIENT_TIERS_PAYANT_ID, EntityManager emg) {

        TypedQuery<TPreenregistrementCompteClientTiersPayent> q = emg.createQuery(
                "SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 AND t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?2",
                TPreenregistrementCompteClientTiersPayent.class).setParameter(1, lg_PREENREGISTREMENT_ID)
                .setParameter(2, lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
        q.setMaxResults(1);
        return q.getSingleResult();
    }

    public JSONObject createPreenregistrementCompteClientTierspayant(List<TiersPayantParams> tierspayants,
            TPreenregistrement op, final boolean sansBon, TUser u) {
        JSONObject json = new JSONObject();
        List<TCompteClientTiersPayant> cmparray = new ArrayList<>();
        List<TiersPayantParams> tierspayantsData = tierspayants;

        try {
            java.util.function.Predicate<TiersPayantParams> p = e -> {
                if (sansBon) {
                    return true;
                }
                return (e.getNumBon() != null && !"".equals(e.getNumBon()));

            };
            boolean canContinue = tierspayants.stream().anyMatch(p);
            if (canContinue) {
                tierspayants.forEach(params -> {
                    TCompteClientTiersPayant cmptClient = this.getEm().find(TCompteClientTiersPayant.class,
                            params.getCompteTp());
                    TTiersPayant payant = cmptClient.getLgTIERSPAYANTID();
                    if (boonDejaUtilise(params.getNumBon(), payant.getLgTIERSPAYANTID()) && !op.getCopy()) {

                        json.putOnce("success", false).putOnce("msg",
                                "Le numéro de  <span style='color:red;font-weight:800;'> " + params.getNumBon()
                                        + " </span> est déjà utilisé par l'assureur :: " + payant.getStrFULLNAME());

                    } else {
                        TPreenregistrementCompteClientTiersPayent item = getTPreenregistrementCompteClientTiersPayent(
                                op.getLgPREENREGISTREMENTID(), cmptClient.getLgCOMPTECLIENTTIERSPAYANTID(),
                                this.getEm());
                        item.setDtUPDATED(op.getDtUPDATED());
                        item.setIntPERCENT(params.getTaux());
                        item.setIntPRICE(params.getTpnet());
                        item.setIntPRICERESTE(params.getTpnet());
                        item.setStrREFBON(params.getNumBon());
                        item.setDblQUOTACONSOVENTE(item.getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOVENTE() != null
                                ? item.getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOVENTE() + item.getIntPRICE()
                                : 0);
                        item.setStrSTATUT(STATUT_IS_CLOSED);
                        item.setLgUSERID(u);
                        if (params.isPrincipal() || (tierspayants.size() == 1) || cmptClient.getBISRO()
                                || (cmptClient.getIntPRIORITY() == 1)) {
                            op.setStrREFBON(params.getNumBon());
                        }
                        this.getEm().merge(item);
                        cmparray.add(cmptClient);
                        if (payant.getToBeExclude() || payant.getIsDepot()) {
                            this.setTiersPayant(payant);
                            payantExclusService.updateTiersPayantAccount(payant, item.getIntPRICE());
                        } else {
                            updateClientAccount(item);
                        }

                    }

                });
                if (StringUtils.isBlank(op.getStrREFBON())) {
                    cmparray.sort(Comparator.comparing(TCompteClientTiersPayant::getIntPRIORITY));
                    tierspayantsData.stream()
                            .filter(ob -> ob.getCompteTp().equals(cmparray.get(0).getLgCOMPTECLIENTTIERSPAYANTID()))
                            .findFirst().ifPresent(t -> op.setStrREFBON(t.getNumBon()));
                }
            } else {
                json.put("success", false).put("msg", "Veuillez saisir les numéros de bon");
            }

        } catch (Exception e) {
            try {

                json.put("success", false).put("msg", "Erreur:: La validation a échouée");
            } catch (JSONException ex) {
            }
        }
        return json;

    }

    public void cloturerItemsVente(String venteId) {

        CriteriaBuilder cb = this.getEm().getCriteriaBuilder();
        CriteriaUpdate<TPreenregistrementDetail> cq = cb.createCriteriaUpdate(TPreenregistrementDetail.class);
        Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
        cq.set(root.get(TPreenregistrementDetail_.strSTATUT), STATUT_IS_CLOSED)
                .where(cb.equal(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)
                        .get(TPreenregistrement_.lgPREENREGISTREMENTID), venteId));
        this.getEm().createQuery(cq).executeUpdate();

    }

    @Override
    public JSONObject clotureravoir(String venteId, TUser tUser) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, venteId);
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaUpdate<TPreenregistrementDetail> cq = cb.createCriteriaUpdate(TPreenregistrementDetail.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            cq.set(root.get(TPreenregistrementDetail_.bISAVOIR), false)
                    .set(root.get(TPreenregistrementDetail_.intAVOIR), 0)
                    .set(root.get(TPreenregistrementDetail_.intAVOIRSERVED),
                            root.get(TPreenregistrementDetail_.intQUANTITY))
                    .set(root.get(TPreenregistrementDetail_.intQUANTITYSERVED),
                            root.get(TPreenregistrementDetail_.intQUANTITY))
                    .where(cb.and(cb.equal(root.get(TPreenregistrementDetail_.bISAVOIR), true),
                            cb.equal(root.get(TPreenregistrementDetail_.lgPREENREGISTREMENTID)
                                    .get(TPreenregistrement_.lgPREENREGISTREMENTID), venteId)));
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
    public JSONObject addtierspayant(String venteId, SalesParams params) {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, venteId);
            TCompteClientTiersPayant clientTiersPayant = emg.find(TCompteClientTiersPayant.class,
                    params.getTypeVenteId());
            TPreenregistrementCompteClientTiersPayent clientTiersPayent = new TPreenregistrementCompteClientTiersPayent(
                    UUID.randomUUID().toString());
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
            TPreenregistrementCompteClientTiersPayent op = (TPreenregistrementCompteClientTiersPayent) emg.createQuery(
                    "SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?1 AND t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?2")
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
            TPreenregistrementCompteClientTiersPayent op = (TPreenregistrementCompteClientTiersPayent) emg.createQuery(
                    "SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?1 AND t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?2")
                    .setParameter(1, params.getTierspayants().get(0).getCompteTp()).setParameter(2, params.getVenteId())
                    .getSingleResult();
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
                montantAPaye = sumVenteSansRemise(items(p));
                p.setIntPRICE(montantAPaye.getMontant());
                p.setIntACCOUNT(montantAPaye.getMontantAccount());
                getEm().merge(p);
                json.put("success", true).put("msg", "Opération effectuée avec success");
                json.put("data", new JSONObject(montantAPaye));
            } else {
                TRemise remise = p.getRemise();
                montantAPaye = getRemiseVno(p, remise, items(p));
                json.put("success", true).put("msg", "Opération effectuée avec success");
                json.put("data", new JSONObject(montantAPaye));

            }
            afficheurMontantAPayer(montantAPaye.getMontantNet(), "NET A PAYER: ");
        } catch (Exception e) {

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
                    montantAPaye = sumVenteSansRemise(items(p));
                    p.setIntPRICE(montantAPaye.getMontant());
                    p.setIntACCOUNT(montantAPaye.getMontantAccount());
                    p.setIntPRICEOTHER(montantAPaye.getMontant());
                    json.put("success", true).put("msg", "Opération effectuée avec success");
                    json.put("data", new JSONObject(montantAPaye));

                } else {
                    TRemise remise = p.getRemise();
                    montantAPaye = getRemiseVno(p, remise, items(p));
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
            TypedQuery<TWorkflowRemiseArticle> q = getEm().createQuery(
                    "SELECT t FROM TWorkflowRemiseArticle t WHERE t.strCODEREMISEARTICLE = ?1  AND t.strSTATUT = ?2 ",
                    TWorkflowRemiseArticle.class);
            q.setParameter(1, strCODEREMISE).setParameter(2, DateConverter.STATUT_ENABLE);
            q.setMaxResults(1);
            return q.getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    private TGrilleRemise grilleRemiseRemiseFromWorkflow(TPreenregistrement OTPreenregistrement, TFamille OTFamille,
            String remiseId) {
        int int_code_grille_remise;
        TGrilleRemise OTGrilleRemise;
        try {
            TWorkflowRemiseArticle OTWorkflowRemiseArticle = findByArticleRemise(OTFamille.getStrCODEREMISE());
            if (OTWorkflowRemiseArticle == null) {
                return null;
            }
            if ((OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(bll.common.Parameter.VENTE_ASSURANCE))
                    || (OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID()
                            .equals(bll.common.Parameter.VENTE_AVEC_CARNET))) {
                int_code_grille_remise = OTWorkflowRemiseArticle.getStrCODEGRILLEVO();
                OTGrilleRemise = (TGrilleRemise) getEm().createQuery(
                        "SELECT t FROM TGrilleRemise t WHERE t.strCODEGRILLE = ?1  AND t.strSTATUT = ?2  AND t.lgREMISEID.lgREMISEID = ?3 ")
                        .setParameter(1, int_code_grille_remise).setParameter(2, DateConverter.STATUT_ENABLE)
                        .setParameter(3, remiseId).getSingleResult();

                return OTGrilleRemise;
            } else {
                int_code_grille_remise = OTWorkflowRemiseArticle.getStrCODEGRILLEVNO();
                OTGrilleRemise = (TGrilleRemise) getEm().createQuery(
                        "SELECT t FROM TGrilleRemise t WHERE t.strCODEGRILLE  = ?1  AND t.strSTATUT = ?2 AND t.lgREMISEID.lgREMISEID = ?3 ")
                        .setParameter(1, int_code_grille_remise).setParameter(2, DateConverter.STATUT_ENABLE)
                        .setParameter(3, remiseId).getSingleResult();
                return OTGrilleRemise;
            }

        } catch (Exception e) {
            return null;
        }

    }

    private MontantAPaye calculCarnetNet(List<TiersPayantParams> tierspayants, MontantAPaye montantAPaye,
            boolean asRestrictions) {

        MontantAPaye map = computeCarnetNet(montantAPaye, tierspayants.get(0).getCompteTp(), asRestrictions);
        TiersPayantParams tp = map.getTierspayants().get(0);
        tp.setCompteTp(tierspayants.get(0).getCompteTp());
        tp.setNumBon(tierspayants.get(0).getNumBon());
        map.setTierspayants(List.of(tp));
        return map;

    }

    private MontantAPaye calculAssuranceNet(List<TiersPayantParams> tierspayants, MontantAPaye montantAPaye,
            boolean asRestrictions) {
        String msg = " ";

        boolean hasRestructuring = false;
        int remiseCarnet = montantAPaye.getRemise();
        int montantvente = montantAPaye.getMontant();
        int cmuAmount = montantAPaye.getCmuAmount();

        int totalTp = 0;
        int totalTaux = 0;
        List<TiersPayantParams> resultat = new ArrayList<>();
        boolean isCmu = tierspayants.stream().allMatch(TiersPayantParams::isCmu) && (cmuAmount != montantvente);
        int tiersPayantAmount = isCmu ? cmuAmount : montantvente;
        int montantVariable = tiersPayantAmount;
        for (TiersPayantParams tierspayant : tierspayants) {
            TiersPayantParams tp = new TiersPayantParams();
            int taux = tierspayant.getTaux();
            double montantTp = tiersPayantAmount * (Double.valueOf(taux) / 100);
            int tpnet = (int) Math.ceil(montantTp);
            int thatTaux;
            if (asRestrictions) {
                JSONObject json = chechCustomerTiersPayantConsumption(tierspayant.getCompteTp(), tpnet);
                if (json.getBoolean("hasRestructuring")) {
                    msg += json.getString("msg") + " ";
                    hasRestructuring = json.getBoolean("hasRestructuring");
                    tpnet = json.getInt("montantToBePaid");

                }
            }

            if (montantVariable > tpnet) {
                montantVariable -= tpnet;
                thatTaux = taux;
                totalTaux += thatTaux;
            } else {
                tpnet = montantVariable;
                thatTaux = hasRestructuring ? (int) Math.ceil((Double.valueOf(tpnet) * 100) / montantvente)
                        : 100 - totalTaux;
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
        int netCustomer = (montantvente - totalTp) - remiseCarnet;
        MontantAPaye map = new MontantAPaye(netCustomer, montantvente, totalTp, remiseCarnet, montantAPaye.getMarge(),
                montantAPaye.getMontantTva());

        map.setTierspayants(resultat);
        map.setCmuAmount(cmuAmount);
        map.setMessage(msg);
        map.setRestructuring(hasRestructuring);
        return map;
    }

    private MontantAPaye calculVoNet(TPreenregistrement op, List<TiersPayantParams> tierspayants) {
        boolean asRestrictions = checkPlafondVente();
        List<TPreenregistrementDetail> lstTPreenregistrementDetail = items(op);
        TRemise remise = op.getRemise();
        remise = remise != null ? remise : op.getClient().getRemise();
        TTypeVente tTypeVente = op.getLgTYPEVENTEID();
        MontantAPaye montantAPaye;
        if (remise != null) {
            montantAPaye = getRemiseVno(op, remise, lstTPreenregistrementDetail);

        } else {
            montantAPaye = sumVenteSansRemise(lstTPreenregistrementDetail);
        }

        if (tTypeVente.getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
            return calculCarnetNet(tierspayants, montantAPaye, asRestrictions);
        } else {
            return calculAssuranceNet(tierspayants, montantAPaye, asRestrictions);

        }
    }

    private MontantAPaye getRemiseVno(TPreenregistrement op, TRemise oTRemise,
            List<TPreenregistrementDetail> lstTPreenregistrementDetail) {
        int intTOTALREMISE;
        int intREMISEPARA = 0;
        int montantNet;
        LongAdder totalRemise = new LongAdder();
        LongAdder totalRemisePara = new LongAdder();
        LongAdder totalAmount = new LongAdder();
        LongAdder marge = new LongAdder();
        LongAdder montantTva = new LongAdder();
        LongAdder montantAccount = new LongAdder();
        LongAdder montantCMU = new LongAdder();

        lstTPreenregistrementDetail.forEach(x -> {
            totalAmount.add(x.getIntPRICE());
            if (Objects.nonNull(x.getCmuPrice()) && x.getCmuPrice() != 0) {
                montantCMU.add(x.getCmuPrice() * x.getIntQUANTITY());
            } else {
                montantCMU.add(x.getIntPRICE());
            }
            montantTva.add(x.getMontantTva());
            TFamille famille = x.getLgFAMILLEID();
            int remise = 0;
            if (!StringUtils.isEmpty(famille.getStrCODEREMISE()) && !famille.getStrCODEREMISE().equals("2")
                    && !famille.getStrCODEREMISE().equals("3")) {
                TGrilleRemise grilleRemise = grilleRemiseRemiseFromWorkflow(x.getLgPREENREGISTREMENTID(), famille,
                        oTRemise.getLgREMISEID());
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
                int thatMarge = (x.getIntPRICE() - remise - x.getMontantTva())
                        - (x.getIntQUANTITY() * famille.getIntPAF());
                marge.add(thatMarge);
                montantAccount.add(x.getIntPRICE());
                montantTva.add(x.getMontantTva());

            }

        });
        int montantTotal = totalAmount.intValue();
        intTOTALREMISE = totalRemise.intValue();
        int tva = montantTva.intValue();
        montantNet = montantTotal - intTOTALREMISE;
        op.setIntPRICE(montantTotal);
        op.setIntACCOUNT(montantAccount.intValue());
        op.setIntPRICEREMISE(intTOTALREMISE);
        op.setIntREMISEPARA(intREMISEPARA);
        op.setMontantTva(tva);
        if (intTOTALREMISE > 0 && oTRemise == null) {
            op.setRemise(oTRemise);
        }
        return new MontantAPaye(DateConverter.arrondiModuloOfNumber(montantNet, 5), montantTotal, 0,
                DateConverter.arrondiModuloOfNumber(intTOTALREMISE, 5), marge.intValue(), tva)
                        .cmuAmount(montantCMU.intValue());
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
            cq.select(cb.construct(SearchDTO.class, root.get(TFamille_.lgFAMILLEID), root.get(TFamille_.intCIP),
                    root.get(TFamille_.strNAME), root.get("lgZONEGEOID").get("strLIBELLEE"),
                    root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBERAVAILABLE), root.get(TFamille_.intPAF),
                    fa.get(TFamilleStock_.intNUMBER), root.get(TFamille_.intNUMBERDETAIL))).distinct(true);
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
            if (StringUtils.isNotEmpty(params.getQuery())) {
                String search = params.getQuery() + "%";
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), search),
                        cb.like(root.get(TFamille_.intCIP), search), cb.like(root.get(TFamille_.intEAN13), search),
                        cb.like(st.get("strCODEARTICLE"), search), cb.like(root.get(TFamille_.lgFAMILLEID), search),
                        cb.like(root.get(TFamille_.strDESCRIPTION), search)));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate,
                    cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), params.getEmplacementId()));
            cq.select(cb.construct(SearchDTO.class, root.get(TFamille_.lgFAMILLEID), root.get(TFamille_.intCIP),
                    root.get(TFamille_.strNAME), root.get("lgZONEGEOID").get("strLIBELLEE"),
                    root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBERAVAILABLE), root.get(TFamille_.intPAF),
                    fa.get(TFamilleStock_.intNUMBER), root.get(TFamille_.boolDECONDITIONNE),
                    root.get(TFamille_.lgFAMILLEPARENTID))).orderBy(cb.asc(root.get(TFamille_.strNAME))).distinct(true);
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
            if (StringUtils.isNotEmpty(params.getQuery())) {
                String search = params.getQuery() + "%";
                predicate = cb.and(predicate, cb.or(cb.like(root.get(TFamille_.strNAME), search),
                        cb.like(root.get(TFamille_.intCIP), search), cb.like(root.get(TFamille_.intEAN13), search),
                        cb.like(st.get("strCODEARTICLE"), search), cb.like(root.get(TFamille_.lgFAMILLEID), search),
                        cb.like(root.get(TFamille_.strDESCRIPTION), search)));
            }
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate,
                    cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), params.getEmplacementId()));

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
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join("lgPREENREGISTREMENTID",
                    JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> pf = root.join("lgFAMILLEID", JoinType.INNER);
            Predicate p = cb.conjunction();
            if (StringUtils.isNotEmpty(params.getQuery())) {
                String search = params.getQuery() + "%";
                p = cb.and(p, cb.or(cb.like(pf.get(TFamille_.strDESCRIPTION), search),
                        cb.like(pf.get(TFamille_.intCIP), search), cb.like(pf.get(TFamille_.intEAN13), search)));
            }
            if (params.getStatut() != null && !"".equals(params.getStatut())) {
                p = cb.and(p, cb.equal(join.get(TPreenregistrement_.strSTATUT), params.getStatut()));
            }
            p = cb.and(p, cb.equal(join.get(TPreenregistrement_.lgPREENREGISTREMENTID), params.getVenteId()));
            cq.select(root).orderBy(cb.desc(root.get(TPreenregistrementDetail_.dtUPDATED)));
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
            Join<TPreenregistrementDetail, TPreenregistrement> join = root.join("lgPREENREGISTREMENTID",
                    JoinType.INNER);
            Join<TPreenregistrementDetail, TFamille> pf = root.join("lgFAMILLEID", JoinType.INNER);
            Predicate p = cb.conjunction();
            if (StringUtils.isNotEmpty(params.getQuery())) {
                String search = params.getQuery() + "%";
                p = cb.and(p, cb.or(cb.like(pf.get(TFamille_.strDESCRIPTION), search),
                        cb.like(pf.get(TFamille_.intCIP), search), cb.like(pf.get(TFamille_.intEAN13), search)));
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
            TypedQuery<TCompteClientTiersPayant> q = emg.createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1  ",
                    TCompteClientTiersPayant.class);
            q.setParameter(1, clientId);
            return q.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Integer nbreProduitsByVente(String venteId) {
        try {
            Query q = getEm().createQuery(
                    "SELECT COUNT(o) FROM TPreenregistrementDetail o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 ");
            q.setParameter(1, venteId);
            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public JSONObject updatRemiseVenteDepot(String venteId, int valueRemise) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, venteId);
            int remise = (int) Math.ceil(((Double.valueOf(preenregistrement.getIntPRICE()) * valueRemise) / 100));
            preenregistrement.setIntPRICEREMISE(remise);
            json.put("success", true).put("montant", preenregistrement.getIntPRICE()).put("remise", remise)
                    .put("montantNet", preenregistrement.getIntPRICE() - remise);

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
            TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId());
            Optional<TTypeMvtCaisse> typeMvtCaisse = getOne(KEY_PARAM_MVT_VENTE_ORDONNANCE);
            List<TPreenregistrementDetail> lstTPreenregistrementDetail = items(tp);
            boolean isAvoir = checkAvoir(lstTPreenregistrementDetail);
            String statut = statutDiff(clotureVenteParams.getTypeRegleId());
            TUser vendeur = userFromId(clotureVenteParams.getUserVendeurId(), emg);
            Optional<TClient> client = findClientById(clotureVenteParams.getClientId());
            TEmplacement emplacement = emg.find(TEmplacement.class, tp.getPkBrand());
            tp.setStrFIRSTNAMECUSTOMER(emplacement.getStrFIRSTNAME());
            tp.setStrLASTNAMECUSTOMER(emplacement.getStrLASTNAME());
            tp.setStrPHONECUSTOME(emplacement.getStrPHONE());
            client.ifPresent(c -> tp.setClient(c));
            TReglement tReglement = createTReglement(clotureVenteParams.getUserId(), modeReglement, "",
                    tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getBanque(), clotureVenteParams.getLieux(),
                    clotureVenteParams.getCommentaire(), STATUT_IS_CLOSED, "");
            tp.setLgREGLEMENTID(tReglement);
            tp.setDtUPDATED(new Date());
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(STATUT_IS_CLOSED);
            tp.setStrSTATUTVENTE(statut);
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()),
                    tp.getLgUSERVENDEURID().getLgEMPLACEMENTID()).getReference());
            tp.setIntACCOUNT(tp.getIntPRICE());
            tp.setIntPRICEOTHER(tp.getIntACCOUNT());
            tp.setCompletionDate(new Date());
            cloturerItemsVente(tp.getLgPREENREGISTREMENTID());
            addTransactionDepot(tUser, tp, findById(MODE_ESP), typeMvtCaisse.get(), clotureVenteParams.getMarge(),
                    client.orElse(null));
            mvtProduitService.updateVenteStockDepot(tp, lstTPreenregistrementDetail, emplacement);
            emg.merge(tp);
            json.put("success", true).put("msg", "Opération effectuée avec success").put("ref",
                    tp.getLgPREENREGISTREMENTID());
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
            TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId());
            Optional<TTypeMvtCaisse> typeMvtCaisse = getOne(KEY_PARAM_MVT_VENTE_NON_ORDONNANCEE);
            List<TPreenregistrementDetail> lstTPreenregistrementDetail = items(tp);

            int montant = tp.getIntPRICE();
            if (diffAmount(montant, lstTPreenregistrementDetail)) {
                json.put("success", false);
                json.put("msg", "Désolé impossible de terminer la vente. Veuillez recalculer le montant de la vente ");
                json.put("codeError", 0);
                return json;
            }
            boolean isAvoir = checkAvoir(lstTPreenregistrementDetail);
            String statut = statutDiff(clotureVenteParams.getTypeRegleId());
            TUser vendeur = userFromId(clotureVenteParams.getUserVendeurId(), emg);
            Integer amount = montant - tp.getIntPRICEREMISE();
            TCompteClient compteClient = findByClientId(clotureVenteParams.getClientId(), emg);
            TEmplacement emplacement = emg.find(TEmplacement.class, tp.getPkBrand());
            tp.setStrFIRSTNAMECUSTOMER(emplacement.getStrFIRSTNAME());
            tp.setStrLASTNAMECUSTOMER(emplacement.getStrLASTNAME());
            tp.setStrPHONECUSTOME(emplacement.getStrPHONE());
            if (clotureVenteParams.getTypeRegleId().equals(REGL_DIFF)) {
                findClientById(clotureVenteParams.getClientId()).ifPresent(c -> {
                    tp.setClient(c);
                    addDiffere(compteClient, tp, clotureVenteParams.getMontantPaye(), clotureVenteParams.getUserId());
                });
            }
            TReglement tReglement = createTReglement(clotureVenteParams.getUserId(), modeReglement, "",
                    tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getBanque(), clotureVenteParams.getLieux(),
                    clotureVenteParams.getCommentaire(), STATUT_IS_CLOSED, "");
            tp.setBWITHOUTBON(false);
            tp.setLgREGLEMENTID(tReglement);
            tp.setDtUPDATED(new Date());
            tp.setCompletionDate(tp.getDtUPDATED());
            tp.setLgUSERID(clotureVenteParams.getUserId());
            tp.setLgUSERVENDEURID(vendeur != null ? vendeur : clotureVenteParams.getUserId());
            tp.setLgUSERCAISSIERID(tp.getLgUSERID());
            tp.setStrREFTICKET(DateConverter.getShortId(10));
            tp.setBISAVOIR(isAvoir);
            tp.setStrSTATUT(STATUT_IS_CLOSED);
            tp.setStrSTATUTVENTE(statut);
            tp.setIntPRICE(montant);
            tp.setIntPRICEOTHER(montant);
            updateUgData(clotureVenteParams.getData(), tp);
            tp.setStrREF(buildRef(DateConverter.convertDateToLocalDate(tp.getDtUPDATED()),
                    clotureVenteParams.getUserId().getLgEMPLACEMENTID()).getReference());
            cloturerItemsVente(tp.getLgPREENREGISTREMENTID());
            addRecette(clotureVenteParams.getMontantPaye(), tp.getStrREFTICKET() + "_" + tp.getStrREF(),
                    tp.getLgPREENREGISTREMENTID(), clotureVenteParams.getUserId(), emg);
            MvtTransaction mvtTransaction = addTransaction(tUser, tp, montant, tp.getIntACCOUNT(), amount,
                    clotureVenteParams.getMontantRecu(), true, CategoryTransaction.CREDIT,
                    TypeTransaction.VENTE_COMPTANT, findById(clotureVenteParams.getTypeRegleId()), typeMvtCaisse.get(),
                    clotureVenteParams.getMontantPaye(), clotureVenteParams.getMarge(), tp.getIntACCOUNT(),
                    clotureVenteParams.getData());
            mvtProduitService.updateVenteStockDepot(tp, lstTPreenregistrementDetail, emplacement);
            mvtTransaction.setPreenregistrement(tp);
            emg.persist(mvtTransaction);
            emg.merge(tp);
            addReglement(tp, mvtTransaction, clotureVenteParams);
            json.put("success", true).put("msg", "Opération effectuée avec success").put("ref",
                    tp.getLgPREENREGISTREMENTID());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

            try {
                json.put("success", false).put("msg", "Erreur: Echec de validation de la vente");
            } catch (JSONException ex) {
                LOG.log(Level.SEVERE, null, ex);
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
            MontantAPaye montantAPaye = sumVenteSansRemise(items(p));
            p.setIntPRICE(montantAPaye.getMontant());
            Integer montantRemise = calculRemiseDepot(montantAPaye.getMontant(), params.getRemiseDepot());
            p.setIntACCOUNT(montantAPaye.getMontantAccount() - montantRemise);
            json.put("success", true).put("msg", "Opération effectuée avec success");
            json.put("data",
                    new JSONObject(new MontantAPaye(
                            DateConverter.arrondiModuloOfNumber(montantAPaye.getMontant() - montantRemise, 5),
                            montantAPaye.getMontant(), 0, DateConverter.arrondiModuloOfNumber(montantRemise, 5),
                            montantAPaye.getMarge(), montantAPaye.getMontantTva())));
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
            MontantAPaye montantAPaye = sumVenteSansRemise(items(p));
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
            json.put("data",
                    new JSONObject(new MontantAPaye(
                            DateConverter.arrondiModuloOfNumber(montantAPaye.getMontant() - montantRemise, 5),
                            montantAPaye.getMontant(), 0, DateConverter.arrondiModuloOfNumber(montantRemise, 5),
                            montantAPaye.getMarge(), montantAPaye.getMontantTva())));
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
            LOG.log(Level.SEVERE, null, e);
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
            predicate = cb.and(predicate,
                    cb.or(cb.like(root.get(TFamille_.intCIP), query + "%"),
                            cb.like(st.get("strCODEARTICLE"), query + "%"),
                            cb.like(root.get(TFamille_.intEAN13), query + "%")));
            predicate = cb.and(predicate, cb.equal(root.get(TFamille_.strSTATUT), "enable"));
            predicate = cb.and(predicate, cb.equal(fa.get("lgEMPLACEMENTID").get("lgEMPLACEMENTID"), emplacementId));
            cq.select(cb.construct(SearchDTO.class, root.get(TFamille_.lgFAMILLEID), root.get(TFamille_.intCIP),
                    root.get(TFamille_.strNAME), root.get("lgZONEGEOID").get("strLIBELLEE"),
                    root.get(TFamille_.intPRICE), fa.get(TFamilleStock_.intNUMBERAVAILABLE), root.get(TFamille_.intPAF),
                    fa.get(TFamilleStock_.intNUMBER), root.get(TFamille_.boolDECONDITIONNE),
                    root.get(TFamille_.lgFAMILLEPARENTID))).orderBy(cb.asc(root.get(TFamille_.strNAME))).distinct(true);
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
    public JSONObject removeClientToVente(String venteId) throws JSONException {
        try {
            TPreenregistrement op = getEm().find(TPreenregistrement.class, venteId);
            if (op == null) {
                return new JSONObject().put("success", true);
            }
            op.setClient(null);
            op.setAyantDroit(null);
            List<TPreenregistrementCompteClientTiersPayent> list = getTPreenregistrementCompteClientTiersPayent(
                    venteId);
            list.forEach(c -> {
                getEm().remove(c);
            });
            getEm().merge(op);
            return new JSONObject().put("success", true);
        } catch (Exception e) {
            return new JSONObject().put("success", false);
        }
    }

    private List<TPreenregistrementCompteClientTiersPayent> getTPreenregistrementCompteClientTiersPayent(
            String idVente) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q = getEm().createQuery(
                    "SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 ORDER BY t.lgCOMPTECLIENTTIERSPAYANTID.intPRIORITY ASC ",
                    TPreenregistrementCompteClientTiersPayent.class).setParameter(1, idVente);
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
            if ((p.getStrTYPEVENTE().equals(DateConverter.VENTE_ASSURANCE))
                    && (params.getTypeVenteId().equals(DateConverter.VENTE_COMPTANT_ID))) {
                return new JSONObject().put("success", false)
                        .put("msg", "Imposible de modifier une vente assurance en vente au comptant")
                        .put("typeVenteId", oldType.getLgTYPEVENTEID());
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
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false).put("msg", "Imposible de modifier la vente ")
                    .put("typeVenteId", DateConverter.VENTE_COMPTANT_ID);
        }
    }

    @Override
    public JSONObject mettreAjourDonneesClientVenteExistante(String venteId, SalesParams params) throws JSONException {
        try {
            TPreenregistrement op = getEm().find(TPreenregistrement.class, venteId);
            findClientById(params.getClientId()).ifPresent(c -> {
                op.setStrFIRSTNAMECUSTOMER(c.getStrFIRSTNAME());
                op.setStrLASTNAMECUSTOMER(c.getStrLASTNAME());
                op.setStrPHONECUSTOME(c.getStrADRESSE());
                op.setClient(c);
            });
            if (!op.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
                findAyantDroit(params.getAyantDroitId()).ifPresent(a -> {
                    op.setStrFIRSTNAMECUSTOMER(a.getStrFIRSTNAME());
                    op.setStrLASTNAMECUSTOMER(a.getStrLASTNAME());
                    op.setAyantDroit(a);
                });
            }
            createPreenregistrementTierspayant(params.getTierspayants(), op, getEm());
            getEm().merge(op);
            return new JSONObject().put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
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
            TypedQuery<TPreenregistrement> tq = getEm()
                    .createQuery("SELECT o FROM TPreenregistrement o WHERE o.lgPARENTID=?1 ", TPreenregistrement.class);
            tq.setParameter(1, idVente);
            return tq.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private TPreenregistrement findOneById(String idVente) {
        try {
            return getEm().find(TPreenregistrement.class, idVente);

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
            items(tp).forEach(z -> {
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
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(
                oldPreenregistrement, getEm());
        clientTiersPayents.forEach((a) -> {
            TCompteClientTiersPayant oTCompteClientTiersPayant = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent newCmp = new TPreenregistrementCompteClientTiersPayent();
            newCmp.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            newCmp.setLgPREENREGISTREMENTID(preenregistrement);
            newCmp.setIntPRICE(a.getIntPRICE());
            newCmp.setLgUSERID(o);
            newCmp.setStrSTATUT(DateConverter.STATUT_PROCESS);
            newCmp.setDtCREATED(a.getDtCREATED());
            newCmp.setDtUPDATED(a.getDtUPDATED());
            newCmp.setLgCOMPTECLIENTTIERSPAYANTID(oTCompteClientTiersPayant);
            newCmp.setStrREFBON(a.getStrREFBON());
            newCmp.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            newCmp.setIntPERCENT(a.getIntPERCENT());
            newCmp.setIntPRICERESTE(a.getIntPRICERESTE());
            newCmp.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            newCmp.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            getEm().persist(newCmp);
            TCompteClient oTCompteClient = oTCompteClientTiersPayant.getLgCOMPTECLIENTID();
            if (oTCompteClient != null && oTCompteClientTiersPayant.getDblPLAFOND() != null
                    && oTCompteClientTiersPayant.getDblPLAFOND() != 0) {
                oTCompteClientTiersPayant
                        .setDblQUOTACONSOMENSUELLE((oTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() != null
                                ? oTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() : 0) + newCmp.getIntPRICE());
                oTCompteClientTiersPayant.setDtUPDATED(new Date());
                getEm().merge(oTCompteClientTiersPayant);
            }
            if (oTCompteClient != null && oTCompteClient.getDblPLAFOND() != null
                    && oTCompteClient.getDblPLAFOND() != 0) {
                oTCompteClient.setDblQUOTACONSOMENSUELLE((oTCompteClient.getDblQUOTACONSOMENSUELLE() != null
                        ? oTCompteClient.getDblQUOTACONSOMENSUELLE() : 0) + newCmp.getIntPRICE());
                oTCompteClient.setDtUPDATED(new Date());
                getEm().merge(oTCompteClient);
            }
        });

    }

    private void addDiffere(TPreenregistrement newP, TPreenregistrementCompteClient old) {
        TPreenregistrementCompteClient oTPreenregistrementCompteClient = new TPreenregistrementCompteClient(
                UUID.randomUUID().toString());
        oTPreenregistrementCompteClient.setDtCREATED(old.getDtCREATED());
        oTPreenregistrementCompteClient.setDtUPDATED(old.getDtUPDATED());
        oTPreenregistrementCompteClient.setLgCOMPTECLIENTID(old.getLgCOMPTECLIENTID());
        oTPreenregistrementCompteClient.setLgPREENREGISTREMENTID(newP);
        oTPreenregistrementCompteClient.setLgUSERID(newP.getLgUSERID());
        oTPreenregistrementCompteClient.setIntPRICE(old.getIntPRICE());
        oTPreenregistrementCompteClient.setIntPRICERESTE(old.getIntPRICERESTE());
        oTPreenregistrementCompteClient.setStrSTATUT(STATUT_IS_CLOSED);
        getEm().persist(oTPreenregistrementCompteClient);
    }

    private TCompteClientTiersPayant findByClientAndTiersPayant(String clientId, String tierspayentId) {

        try {
            TypedQuery<TCompteClientTiersPayant> q = getEm().createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1 AND o.lgTIERSPAYANTID.lgTIERSPAYANTID=?2",
                    TCompteClientTiersPayant.class);
            q.setParameter(1, clientId);
            q.setParameter(2, tierspayentId);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JSONObject modificationVentetierpayantprincipal(String venteId, ClotureVenteParams params)
            throws JSONException {
        try {
            TPreenregistrement tp = getEm().find(TPreenregistrement.class, venteId);
            TCompteClientTiersPayant olClientTiersPayant = findByClientAndTiersPayant(tp.getClient().getLgCLIENTID(),
                    params.getTypeVenteId());
            TPreenregistrementCompteClientTiersPayent clientTiersPayent = getTPreenregistrementCompteClientTiersPayent(
                    venteId, olClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID(), getEm());
            /**
             * s'il y a modification de tu tiers-payant on rentre dans premiere condition
             */
            if (!params.getTypeVenteId().equals(params.getAyantDroitId())) {

                TCompteClientTiersPayant newClientTiersPayant = findByClientAndTiersPayant(
                        tp.getClient().getLgCLIENTID(), params.getAyantDroitId());
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

            return new JSONObject().put("success", false);
        }
    }

    private JSONObject chechCustomerTiersPayantConsumption(String compteTp, Integer montantToBePaid) {

        TCompteClientTiersPayant tc = this.getEm().find(TCompteClientTiersPayant.class, compteTp);
        TTiersPayant tiersPayant2 = tc.getLgTIERSPAYANTID();
        String tierspayantName = tiersPayant2.getStrFULLNAME();
        int plafondClient = (tc.getDblPLAFOND() == null || tc.getDblPLAFOND() <= 0 ? 0 : tc.getDblPLAFOND().intValue());
        int encoursClient = (tc.getDbPLAFONDENCOURS() == null || tc.getDbPLAFONDENCOURS() <= 0 ? 0
                : tc.getDbPLAFONDENCOURS());
        int plafondTierPayant = (tiersPayant2.getDblPLAFONDCREDIT() == null || tiersPayant2.getDblPLAFONDCREDIT() <= 0
                ? 0 : tiersPayant2.getDblPLAFONDCREDIT().intValue());
        int consoMensuelleClient = (tc.getDbCONSOMMATIONMENSUELLE() == null || tc.getDbCONSOMMATIONMENSUELLE() < 0 ? 0
                : tc.getDbCONSOMMATIONMENSUELLE());
        int consoMensuelleTierPayant = (tiersPayant2.getDbCONSOMMATIONMENSUELLE() == null
                || tiersPayant2.getDbCONSOMMATIONMENSUELLE() < 0 ? 0 : tiersPayant2.getDbCONSOMMATIONMENSUELLE());
        JSONObject json = chechTiersPayantConsumption(plafondTierPayant, consoMensuelleTierPayant, montantToBePaid,
                tierspayantName);
        String msg = json.getString("msg");
        boolean hasRestructuring = json.getBoolean("hasRestructuring");
        montantToBePaid = json.getInt("montantToBePaid");
        json = chechCustomerConsumption(plafondClient, encoursClient, consoMensuelleClient, montantToBePaid,
                tierspayantName);

        montantToBePaid = json.getInt("montantToBePaid");
        if (json.getBoolean("hasRestructuring")) {
            msg = json.getString("msg");
            hasRestructuring = json.getBoolean("hasRestructuring");
        }
        return new JSONObject().put("msg", msg).put("hasRestructuring", hasRestructuring).put("montantToBePaid",
                montantToBePaid);
    }

    private JSONObject chechCustomerConsumption(int plafondClient, int encoursClient, int consoMensuelleClient,
            int montantToBePaid, String tierspayantName) {
        boolean hasRestructuring = false;
        String msg = "";
        boolean isPlafondClient = plafondClient != 0;
        boolean isEncoursClient = encoursClient != 0;

        boolean isMontantToBePaidLess = isPlafondClient && (montantToBePaid > plafondClient);
        boolean isMontantToBePaidLessEncour = isEncoursClient
                && (encoursClient < (consoMensuelleClient + montantToBePaid));

        if (isMontantToBePaidLessEncour) {
            hasRestructuring = true;
            montantToBePaid = encoursClient - consoMensuelleClient;
            msg = "Le tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'>"
                    + tierspayantName
                    + "</span> ne peut prendre en compte <span style='font-weight:900;color:blue;text-decoration: underline;'>"
                    + DateConverter.amountFormat(montantToBePaid)
                    + " </span><br/> . Votre plafond est atteint:[ <span style='font-weight:900;color:blue;'> "
                    + DateConverter.amountFormat(encoursClient) + " </span>]<br/> ";

        }
        if (isMontantToBePaidLess) {
            hasRestructuring = true;
            montantToBePaid = plafondClient;
            msg = "Le tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'>"
                    + tierspayantName
                    + "</span> ne peut prendre en compte <span style='font-weight:900;color:blue;text-decoration: underline;'>"
                    + DateConverter.amountFormat(montantToBePaid)
                    + " </span><br/> .Votre plafond vente est atteint: [ <span style='font-weight:900;color:blue;'> "
                    + DateConverter.amountFormat(plafondClient) + " </span>]<br/> ";
        }

        return new JSONObject().put("msg", msg).put("hasRestructuring", hasRestructuring).put("montantToBePaid",
                montantToBePaid);
    }

    private JSONObject chechTiersPayantConsumption(int plafondTierPayant, int consoMensuelleTierPayant,
            int montantToBePaid, String tierspayantName) {
        boolean hasRestructuring = false;
        String msg = "";
        boolean isPlafondTierPayant = plafondTierPayant != 0;
        if (!isPlafondTierPayant) {
            return new JSONObject().put("msg", msg).put("hasRestructuring", hasRestructuring).put("montantToBePaid",
                    montantToBePaid);
        }
        if (plafondTierPayant < (consoMensuelleTierPayant + montantToBePaid)) {
            hasRestructuring = true;
            montantToBePaid = plafondTierPayant - consoMensuelleTierPayant;
            msg = "Le tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'>"
                    + tierspayantName
                    + "</span> ne peut prendre en compte <span style='font-weight:900;color:blue;text-decoration: underline;'>"
                    + DateConverter.amountFormat(montantToBePaid) + " </span><br/> .Son plafond est atteint.<br/> ";
        }

        return new JSONObject().put("msg", msg).put("hasRestructuring", hasRestructuring).put("montantToBePaid",
                montantToBePaid);
    }

    void afficheurProduit(String libelle, int qty, int prixUnitaire, int montantTotal) {
        if (afficheurActif()) {
            try {
                Afficheur afficheur = Afficheur.getInstance();
                afficheur.affichage(DataStringManager.subStringData(libelle.toUpperCase(), 0, 20));
                afficheur.affichage(
                        DataStringManager.subStringData(qty + "*" + DateConverter.amountFormat(prixUnitaire, '.')
                                + " = " + DateConverter.amountFormat(montantTotal, '.'), 0, 20),
                        "begin");
            } catch (Exception e) {
            }
        }

    }

    void afficheurMontantAPayer(int montantTotal, String libelle) {
        if (afficheurActif()) {
            try {
                Afficheur afficheur = Afficheur.getInstance();
                afficheur.affichage(DataStringManager.subStringData(libelle, 0, 20));
                afficheur.affichage(
                        DataStringManager.subStringData(DateConverter.amountFormat(montantTotal, '.'), 0, 20), "begin");
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
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
            return json.put("success", true).put("clientExist", p.getClient() != null).put("medecinId", m.getId());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
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
            return json.put("success", true).put("clientExist", p.getClient() != null).put("medecinId", m.getId());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", false);
        }
    }

    @Override
    public boolean checkParameterByKey(String key) {
        try {
            TParameters parameters = getEm().find(TParameters.class, key);
            return (Integer.parseInt(parameters.getStrVALUE().trim()) == 1);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public JSONObject updateClientOrTierpayant(SalesParams salesParams) throws JSONException {
        try {
            if (!checkResumeCaisse(salesParams.getUserId(), getEm()).isPresent()) {
                return new JSONObject().put("success", false).put("msg",
                        "Désolé votre caisse est fermée. Veuillez l'ouvrir avant de proceder à l'annulation");

            }
            int taux = getTPreenregistrementCompteClientTiersPayent(salesParams.getVenteId()).stream()
                    .map(TPreenregistrementCompteClientTiersPayent::getIntPERCENT).reduce(0, Integer::sum);
            int newTaux = salesParams.getTierspayants().stream().map(TiersPayantParams::getTaux).reduce(0,
                    Integer::sum);
            if (taux != newTaux) {
                return new JSONObject().put("success", false).put("msg",
                        "Les taux sont différents:  Ancien taux : " + taux + " Nouveau taux: " + newTaux);

            }

            TPreenregistrement tp = updateVenteInfosClientOrtierspayant(salesParams);
            return new JSONObject().put("success", true).put("msg", "Opération effectuée avec succèss").put("refId",
                    tp.getLgPREENREGISTREMENTID());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false).put("msg", "l'opération a échoué");
        }
    }

    @Override
    public JSONObject findVenteForUpdationg(String venteId) throws JSONException {
        try {
            TPreenregistrement tp = findOneById(venteId);
            MvtTransaction m = getTransaction(venteId).get();
            TRemise remise = tp.getRemise();
            VenteRequest request = VenteRequest.builder().lgPREENREGISTREMENTID(venteId)
                    .ayantDroit(new AyantDroitDTO(tp.getAyantDroit())).client(new ClientDTO(tp.getClient()))
                    .dtUPDATED(tp.getDtUPDATED()).intCUSTPART(tp.getIntCUSTPART()).intPRICE(tp.getIntPRICE())
                    .intPRICEREMISE(tp.getIntPRICEREMISE()).strREF(tp.getStrREF()).strREFBON(tp.getStrREFBON())
                    .lgREMISEID((remise != null) ? remise.getLgREMISEID() : null)
                    .lgTYPEVENTEID(tp.getLgTYPEVENTEID().getLgTYPEVENTEID())
                    .strTYPEVENTE(tp.getLgTYPEVENTEID().getStrNAME()).montantPaye(m.getMontantPaye())
                    .montantRegle(m.getMontantRegle()).montantRestant(m.getMontantRestant())
                    .montantCredit(m.getMontantCredit())
                    .tierspayants(getTPreenregistrementCompteClientTiersPayent(venteId).stream()
                            .map(TiersPayantParams::new).collect(Collectors.toList()))
                    .build();
            return new JSONObject().put("data", new JSONObject(request)).put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("data", new JSONObject()).put("success", false);
        }
    }

    private TPreenregistrement updateVenteInfosClientOrtierspayant(SalesParams salesParams) throws Exception {
        TPreenregistrement tp = getEm().find(TPreenregistrement.class, salesParams.getVenteId());
        if (tp.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(DateConverter.VENTE_CARNET_ID)) {
            updateCompteClientVenteCarner(tp, salesParams);
            updateVenteCarnet(salesParams, tp);
        } else {
            clonePreenregistrementTp(tp, salesParams, STATUT_IS_CLOSED);
            updateVente(salesParams, tp);
        }

        return tp;
    }

    private void clonePreenregistrementTp(TPreenregistrement old, SalesParams salesParams, String statut)
            throws Exception {
        List<TPreenregistrementCompteClientTiersPayent> newList = getTPreenregistrementCompteClientTiersPayent(
                old.getLgPREENREGISTREMENTID());
        List<TPreenregistrementCompteClientTiersPayent> array = new ArrayList<>();
        TClient client = old.getClient();
        List<TPreenregistrementDetail> list = items(old);
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
                TPreenregistrementCompteClientTiersPayent opc;
                Optional<TPreenregistrementCompteClientTiersPayent> optc = getTPreenregistrementCompteClientTiersPayent(
                        old.getLgPREENREGISTREMENTID(), b.getCompteTp());
                if (optc.isPresent()) {
                    opc = optc.get();
                    opc.setStrREFBON(b.getNumBon());
                    getEm().merge(opc);
                } else {
                    JSONObject json = calculVoNetAvecPlafondVente(old, montantVariable, b.getTaux(), list);
                    montantVariable = json.getInt("reste");
                    opc = createNewPreenregistrementCompteClientTiersPayant(payant, json, old, salesParams.getUserId(),
                            statut, b.getNumBon());
                }

                array.add(opc);
            } else {

                TTiersPayant p = getEm().find(TTiersPayant.class, b.getCompteTp());
                if (payantParamses.size() > 1) {
                    TPreenregistrementCompteClientTiersPayent opc = getEm()
                            .find(TPreenregistrementCompteClientTiersPayent.class, b.getItemId());
                    payant = clientService.updateOrCreateClientAssurance(client, p, b.getTaux(),
                            opc.getLgCOMPTECLIENTTIERSPAYANTID());
                } else {
                    payant = clientService.updateOrCreateClientAssurance(client, p, b.getTaux());
                }

                if (payant.getBISRO() || (payant.getIntPRIORITY() == 1)) {
                    old.setStrREFBON(b.getNumBon());
                }
                JSONObject json = calculVoNetAvecPlafondVente(old, montantVariable, b.getTaux(), list);
                montantVariable = json.getInt("reste");
                createNewPreenregistrementCompteClientTiersPayant(payant, json, old, salesParams.getUserId(), statut,
                        b.getNumBon());
            }

        }
        ListUtils.removeAll(newList, array).forEach(a -> getEm().remove(a));

    }

    public Optional<TPreenregistrementCompteClientTiersPayent> findOptionalCmtByNumBonAndVenteId(String numBon,
            String venteId) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> query = getEm().createQuery(
                    "SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID =?1 AND o.strREFBON=?2",
                    TPreenregistrementCompteClientTiersPayent.class);
            query.setMaxResults(1);
            query.setParameter(1, venteId);
            query.setParameter(2, numBon);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private TPreenregistrement updateVente(SalesParams salesParams, TPreenregistrement preen) {
        preen.setLgUSERID(salesParams.getUserId());
        TClient client = findClient(salesParams.getClientId(), getEm());
        preen.setClient(client);
        preen.setStrFIRSTNAMECUSTOMER(client.getStrFIRSTNAME());
        preen.setStrLASTNAMECUSTOMER(client.getStrLASTNAME());
        preen.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        preen.setStrPHONECUSTOME(client.getStrADRESSE());
        preen.setStrINFOSCLT("");
        findAyantDroit(salesParams.getAyantDroitId()).ifPresent(a -> {
            preen.setAyantDroit(a);
            preen.setStrFIRSTNAMECUSTOMER(a.getStrFIRSTNAME());
            preen.setStrLASTNAMECUSTOMER(a.getStrLASTNAME());
            preen.setStrNUMEROSECURITESOCIAL(a.getStrNUMEROSECURITESOCIAL());
        });

        preen.setCompletionDate(new Date());
        getEm().merge(preen);
        return preen;
    }

    @Override
    public void annulerVenteAnterieur(TUser ooTUser, TPreenregistrement tp) {
        EntityManager emg = this.getEm();
        final boolean checked = tp.getChecked();
        Optional<TRecettes> oprectte = findRecette(tp.getLgPREENREGISTREMENTID(), emg);
        List<TPreenregistrementDetail> preenregistrementDetails = items(tp);
        String idVente = tp.getLgPREENREGISTREMENTID();
        TPreenregistrement clonedPreen = cloneVente(ooTUser, tp);
        LongAdder montantRestant = new LongAdder();
        findOptionalCmt(tp, emg).ifPresent(cp -> {
            montantRestant.add(cp.getIntPRICERESTE());
            cp.setIntPRICE(0);
            cp.setIntPRICERESTE(0);
            cp.setStrSTATUT(STATUT_DELETE);
            cp.setDtUPDATED(clonedPreen.getDtUPDATED());
            emg.merge(cp);
        });
        if (tp.getStrTYPEVENTE().equals(VENTE_ASSURANCE)) {
            clonePreenregistrementTp(clonedPreen, idVente, ooTUser);
        }

        getTransaction(idVente).ifPresent(tr -> cloneMvtTransaction(ooTUser, tr, clonedPreen, tp));

        oprectte.ifPresent(re -> copyRecette(clonedPreen, re, ooTUser));

        findClientTiersPayents(tp.getLgPREENREGISTREMENTID(), emg).forEach(action -> {
            action.setStrSTATUT(STATUT_DELETE);
            emg.merge(action);
        });
        TEmplacement emplacement = ooTUser.getLgEMPLACEMENTID();
        final Typemvtproduit typemvtproduit = checked ? findTypeMvtProduitById(ANNULATION_DE_VENTE)
                : findTypeMvtProduitById(TMVTP_ANNUL_VENTE_DEPOT_EXTENSION);
        preenregistrementDetails.forEach(e -> {
            TPreenregistrementDetail newItem = createItemCopy(ooTUser, e, clonedPreen, emg);
            TFamille oFamille = e.getLgFAMILLEID();
            updateNbreVenteApresAnnulation(oFamille, ooTUser, newItem.getIntQUANTITY());
            TFamilleStock familleStock = findStock(oFamille.getLgFAMILLEID(), emplacement, emg);
            int initStock = familleStock.getIntNUMBERAVAILABLE();
            mouvementProduitService.saveMvtProduit(newItem.getIntPRICEUNITAIR(), newItem, typemvtproduit, oFamille,
                    ooTUser, emplacement, newItem.getIntQUANTITY(), initStock, initStock - newItem.getIntQUANTITY(),
                    newItem.getValeurTva(), checked, e.getIntUG());

            updateReelStockApresAnnulation(familleStock, newItem.getIntQUANTITY());
            if (StringUtils.isNotEmpty(tp.getPkBrand())) {
                updateReelStockAnnulationDepot(oFamille, newItem.getIntQUANTITY(), tp.getPkBrand(), emg);

            }

        });
        String desc = "Modification de la vente " + tp.getStrREF() + " montant : " + tp.getIntPRICE() + " par "
                + ooTUser.getStrFIRSTNAME() + " " + ooTUser.getStrLASTNAME();
        logService.updateItem(ooTUser, tp.getStrREF(), desc, TypeLog.MODIFICATION_INFO_VENTE, tp,
                clonedPreen.getDtUPDATED());

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
        newVente.setStrSTATUT(STATUT_IS_CLOSED);
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
        List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = findClientTiersPayents(
                oldPreenregistrement, getEm());
        for (TPreenregistrementCompteClientTiersPayent a : clientTiersPayents) {
            TCompteClientTiersPayant compte = a.getLgCOMPTECLIENTTIERSPAYANTID();
            TPreenregistrementCompteClientTiersPayent newItem = new TPreenregistrementCompteClientTiersPayent();
            newItem.setLgPREENREGISTREMENTCOMPTECLIENTPAYENTID(UUID.randomUUID().toString());
            newItem.setLgPREENREGISTREMENTID(preenregistrement);
            newItem.setIntPRICE(a.getIntPRICE() * (-1));
            newItem.setLgUSERID(o);
            newItem.setStrSTATUT(STATUT_DELETE);
            newItem.setDtCREATED(a.getDtUPDATED());
            newItem.setDtUPDATED(a.getDtUPDATED());
            newItem.setLgCOMPTECLIENTTIERSPAYANTID(compte);
            newItem.setStrREFBON(a.getStrREFBON());
            newItem.setDblQUOTACONSOVENTE(a.getDblQUOTACONSOVENTE());
            newItem.setIntPERCENT(a.getIntPERCENT());
            newItem.setIntPRICERESTE(0);
            newItem.setStrSTATUTFACTURE(a.getStrSTATUTFACTURE());
            newItem.setStrLASTTRANSACTION(a.getStrLASTTRANSACTION());
            getEm().persist(newItem);
            updateCompteClientTiersPayantEncourAndPlafond(a);

        }

    }

    private void cloneMvtTransaction(TUser ooTUser, MvtTransaction cashTransaction, TPreenregistrement newP,
            TPreenregistrement old) {
        cashTransaction.setChecked(Boolean.FALSE);
        getEm().merge(cashTransaction);
        addTransactionCopy(ooTUser, old.getLgUSERCAISSIERID(), cashTransaction, newP, cashTransaction.getCreatedAt(),
                cashTransaction.getMvtDate());

    }

    public Optional<TPreenregistrementCompteClientTiersPayent> checkChargedPreenregistrement(String idVente) {

        try {
            TPreenregistrementCompteClientTiersPayent list = (TPreenregistrementCompteClientTiersPayent) getEm()
                    .createQuery(
                            "SELECT p FROM TPreenregistrementCompteClientTiersPayent p WHERE p.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 AND p.strSTATUTFACTURE <> 'unpaid'")
                    .setParameter(1, idVente).setMaxResults(1).getSingleResult();
            return Optional.ofNullable(list);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
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

    private Optional<TCompteClientTiersPayant> findCompteClientTiersPayantByClientIdAndTiersPayantId(String clientId,
            String tierspayantId) {
        try {
            TypedQuery<TCompteClientTiersPayant> q = getEm().createQuery(
                    "SELECT t FROM TCompteClientTiersPayant t WHERE t.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID = ?1 AND t.lgTIERSPAYANTID.lgTIERSPAYANTID = ?2",
                    TCompteClientTiersPayant.class).setParameter(1, clientId).setParameter(2, tierspayantId);
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

    private Optional<TPreenregistrementCompteClientTiersPayent> getTPreenregistrementCompteClientTiersPayent(String id,
            String cpId) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> q = getEm().createQuery(
                    "SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 AND t.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID = ?2",
                    TPreenregistrementCompteClientTiersPayent.class).setParameter(1, id).setParameter(2, cpId);
            q.setMaxResults(1);
            return Optional.ofNullable(q.getSingleResult());

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private TPreenregistrementCompteClientTiersPayent createNewPreenregistrementCompteClientTiersPayant(
            TCompteClientTiersPayant payant, JSONObject json, TPreenregistrement old, TUser user, String statut,
            String numBon) throws Exception {
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
        TCompteClient oCompteClient = payant.getLgCOMPTECLIENTID();
        if (oCompteClient != null && payant.getDblPLAFOND() != null && payant.getDblPLAFOND() != 0) {
            payant.setDblQUOTACONSOMENSUELLE(
                    (payant.getDblQUOTACONSOMENSUELLE() != null ? payant.getDblQUOTACONSOMENSUELLE() : 0)
                            + newItem.getIntPRICE());
            payant.setDtUPDATED(old.getDtUPDATED());
            getEm().merge(payant);
        }
        if (oCompteClient != null && oCompteClient.getDblPLAFOND() != null && oCompteClient.getDblPLAFOND() != 0) {
            oCompteClient.setDblQUOTACONSOMENSUELLE(
                    (oCompteClient.getDblQUOTACONSOMENSUELLE() != null ? oCompteClient.getDblQUOTACONSOMENSUELLE() : 0)
                            + newItem.getIntPRICE());
            oCompteClient.setDtUPDATED(new Date());
            getEm().merge(oCompteClient);
        }
        return newItem;
    }

    private Optional<Reference> getReferenceByDateAndEmplacementId(LocalDate ODate, String emplacementId,
            boolean isDevis) {
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
                r = new Reference().addEmplacement(emplacement)
                        .id(ODate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).lastIntValue(0)
                        .reference(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_"
                                + StringUtils.leftPad(String.valueOf(0), 5, '0'));
            }
            r.setLastIntTmpValue(r.getLastIntTmpValue() + 1);
            r.setReferenceTemp(ODate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_"
                    + StringUtils.leftPad(String.valueOf(r.getLastIntTmpValue()), 5, '0'));
            getEm().merge(r);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return r;
    }

    public Reference buildRef(LocalDate oDate, TEmplacement emplacement) {
        Reference r;

        Optional<Reference> o = getReferenceByDateAndEmplacementId(oDate, emplacement.getLgEMPLACEMENTID(), false);
        if (o.isPresent()) {
            r = o.get();
            r.setLastIntValue(r.getLastIntValue() + 1);
            r.setReference(oDate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_"
                    + StringUtils.leftPad(String.valueOf(r.getLastIntValue()), 5, '0'));
        } else {
            r = new Reference().addEmplacement(emplacement).id(oDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .lastIntValue(1).lastIntTmpValue(1)
                    .referenceTemp(oDate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_"
                            + StringUtils.leftPad(String.valueOf(1), 5, '0'))
                    .reference(oDate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_"
                            + StringUtils.leftPad(String.valueOf(1), 5, '0'));
        }
        getEm().merge(r);

        return r;
    }

    public Reference buildRefDevis(LocalDate oDate, TEmplacement emplacement) {
        Reference r = null;
        try {
            Optional<Reference> o = getReferenceByDateAndEmplacementId(oDate, emplacement.getLgEMPLACEMENTID(), true);
            if (o.isPresent()) {
                r = o.get();
                r.setLastIntValue(r.getLastIntValue() + 1);
                r.setReference(oDate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_"
                        + StringUtils.leftPad(String.valueOf(r.getLastIntValue()), 5, '0'));
            } else {
                r = new Reference().addEmplacement(emplacement)
                        .id(oDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).lastIntValue(1)
                        .reference(oDate.format(DateTimeFormatter.ofPattern("yyMMdd")) + "_"
                                + StringUtils.leftPad(String.valueOf(1), 5, '0'));
            }
            r.setLastIntTmpValue(r.getLastIntValue());
            r.setReferenceTemp(r.getReference());
            getEm().merge(r);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return r;
    }

    private MontantAPaye sumVenteSansRemise(List<TPreenregistrementDetail> list) {
        int montant = 0;
        int montantMarge = 0;
        int montantTva = 0;
        int montantAccount = 0;
        int montantNetUg = 0;
        int montantTtcUg = 0;
        int margeUg = 0;
        int montantCMU = 0;
        for (TPreenregistrementDetail x : list) {
            montant += x.getIntPRICE();
            if (Objects.nonNull(x.getCmuPrice()) && x.getCmuPrice() != 0) {
                montantCMU += (x.getCmuPrice() * x.getIntQUANTITY());
            } else {
                montantCMU += x.getIntPRICE();
            }

            TFamille famille = x.getLgFAMILLEID();
            if (famille.getBoolACCOUNT()) {
                int marge = ((x.getIntPRICE() - x.getMontantTva()) - (x.getIntQUANTITY() * famille.getIntPAF()));
                montantAccount += x.getIntPRICE();
                montantTva += x.getMontantTva();
                montantMarge += marge;

            }
        }
        return new MontantAPaye(montant, montant, 0, 0, montantMarge, montantTva).montantAccount(montantAccount)
                .margeUg(margeUg).montantNetUg(montantNetUg).montantTtcUg(montantTtcUg).cmuAmount(montantCMU);

    }

    private JSONObject shownetpayVnoCheckUg(SalesParams params) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            MontantAPaye montantAPaye;
            TPreenregistrement p = emg.find(TPreenregistrement.class, params.getVenteId());
            if (p.getRemise() == null) {
                montantAPaye = sumVenteSansRemise(items(p), p);
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

    private MontantAPaye getRemiseVnoCheckUg(TPreenregistrement oPreenregistrement, TRemise oTRemise) {
        int intTOTALREMISE;
        Integer intREMISEPARA = 0;
        Integer montantNet = 0;
        LongAdder totalRemise = new LongAdder();
        LongAdder totalRemisePara = new LongAdder();
        LongAdder totalAmount = new LongAdder();
        LongAdder marge = new LongAdder();
        LongAdder montantTva = new LongAdder();
        LongAdder montantAccount = new LongAdder();
        LongAdder montantTtcUg = new LongAdder();
        LongAdder margeUg = new LongAdder();
        LongAdder tvaUg = new LongAdder();
        TEmplacement emplacement = oPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
        boolean isVno = oPreenregistrement.getStrTYPEVENTE().equals(DateConverter.VENTE_COMPTANT);
        List<TPreenregistrementDetail> lstTPreenregistrementDetail = items(oPreenregistrement);
        lstTPreenregistrementDetail.forEach(x -> {
            totalAmount.add(x.getIntPRICE());
            montantTva.add(x.getMontantTva());
            TFamille famille = x.getLgFAMILLEID();
            Integer remise = 0;
            if (!StringUtils.isEmpty(famille.getStrCODEREMISE()) && !famille.getStrCODEREMISE().equals("2")
                    && !famille.getStrCODEREMISE().equals("3")) {
                TGrilleRemise oGrilleRemise = grilleRemiseRemiseFromWorkflow(x.getLgPREENREGISTREMENTID(), famille,
                        oTRemise.getLgREMISEID());
                if (oGrilleRemise != null) {
                    remise = (int) ((x.getIntPRICE() * oGrilleRemise.getDblTAUX()) / 100);
                    if (!x.getBoolACCOUNT()) {
                        totalRemisePara.add(remise);
                    }
                    totalRemise.add(remise);
                    x.setLgGRILLEREMISEID(oGrilleRemise.getLgGRILLEREMISEID());
                }

            }
            x.setIntPRICEREMISE(remise);
            getEm().merge(x);
            if (x.getLgFAMILLEID().getBoolACCOUNT()) {
                int magre0 = (x.getIntPRICE() - remise - x.getMontantTva())
                        - (x.getIntQUANTITY() * famille.getIntPAF());
                marge.add(magre0);
                montantAccount.add(x.getIntPRICE());
                montantTva.add(x.getMontantTva());
                if (isVno) {
                    int margeUg0 = ((x.getIntPRICE() - x.getMontantTva()) - (x.getIntQUANTITY() * famille.getIntPAF()));

                    TFamilleStock stock = this.findStock(famille.getLgFAMILLEID(), emplacement, getEm());
                    if (stock.getIntUG() > 0) {
                        int qtyUg = qtyUg(stock.getIntUG(), x.getIntQUANTITYSERVED());
                        int montant0 = qtyUg * x.getIntPRICEUNITAIR();
                        montantTtcUg.add(qtyUg * x.getIntPRICEUNITAIR());
                        margeUg.add(margeUg(qtyUg, x.getIntQUANTITY(), margeUg0));
                        if (x.getValeurTva() > 0) {
                            Double hortTaxe = montant0 / (1 + (Double.valueOf(x.getValeurTva()) / 100));
                            int montantTvaUg = montant0 - hortTaxe.intValue();
                            x.setMontantTvaUg(montantTvaUg);
                            tvaUg.add(montantTvaUg);
                        }
                    }
                }
            }
        });
        int montantTotal = totalAmount.intValue();
        intTOTALREMISE = totalRemise.intValue();
        int tva = montantTva.intValue();
        montantNet = montantTotal - intTOTALREMISE;
        oPreenregistrement.setIntPRICE(montantTotal);
        oPreenregistrement.setIntACCOUNT(montantAccount.intValue());
        oPreenregistrement.setIntPRICEREMISE(intTOTALREMISE);
        oPreenregistrement.setIntREMISEPARA(intREMISEPARA);
        oPreenregistrement.setMontantTva(tva);
        if (intTOTALREMISE > 0 && oTRemise == null) {
            oPreenregistrement.setRemise(oTRemise);
        }
        return new MontantAPaye(DateConverter.arrondiModuloOfNumber(montantNet, 5), montantTotal, 0,
                DateConverter.arrondiModuloOfNumber(intTOTALREMISE, 5), marge.intValue(), tva)
                        .margeUg(margeUg.intValue()).montantTtcUg(montantTtcUg.intValue())
                        .montantTvaUg(tvaUg.intValue()).montantNetUg(montantTtcUg.intValue());
    }

    private MontantAPaye sumVenteSansRemise(List<TPreenregistrementDetail> list, TPreenregistrement p) {
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
        return new MontantAPaye(montant, montant, 0, 0, montantMarge, montantTva).montantTvaUg(tvaug)
                .montantAccount(montantAccount).margeUg(margeUg).montantNetUg(montantNetUg).montantTtcUg(montantTtcUg);
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
            List<TPreenregistrementDetail> details = items(p);
            details.stream().forEach(e -> {
                TPreenregistrementDetail detail = new TPreenregistrementDetail(e);
                detail.setLgPREENREGISTREMENTDETAILID(UUID.randomUUID().toString());
                detail.setLgPREENREGISTREMENTID(newTp);
                detail.setDtCREATED(newTp.getDtUPDATED());
                detail.setDtUPDATED(newTp.getDtUPDATED());
                getEm().persist(detail);
            });

            List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents = getTPreenregistrementCompteClientTiersPayent(
                    p.getLgPREENREGISTREMENTID());
            clientTiersPayents.stream()
                    .map(clientTiersPayent -> new TPreenregistrementCompteClientTiersPayent(clientTiersPayent))
                    .map(pt -> {
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

    private MvtTransaction findByPkey(String pkey) {
        try {
            TypedQuery<MvtTransaction> tq = getEm().createQuery("SELECT o FROM MvtTransaction o WHERE o.pkey=?1",
                    MvtTransaction.class);
            tq.setParameter(1, pkey);
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    private void updateCompteClientVenteCarner(TPreenregistrement old, SalesParams salesParams) throws Exception {
        TPreenregistrementCompteClientTiersPayent opc = getTPreenregistrementCompteClientTiersPayent(
                old.getLgPREENREGISTREMENTID()).get(0);
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

    private TPreenregistrement updateVenteCarnet(SalesParams salesParams, TPreenregistrement prenn) throws Exception {
        prenn.setLgUSERID(salesParams.getUserId());
        TClient client = findClient(salesParams.getClientId(), getEm());
        prenn.setClient(client);
        prenn.setStrFIRSTNAMECUSTOMER(client.getStrFIRSTNAME());
        prenn.setStrLASTNAMECUSTOMER(client.getStrLASTNAME());
        prenn.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        prenn.setStrPHONECUSTOME(client.getStrADRESSE());
        prenn.setStrINFOSCLT("");
        prenn.setStrREFBON(salesParams.getTierspayants().get(0).getNumBon());
        getEm().merge(prenn);
        return prenn;
    }

    @Override
    public void updateVenteDate(TUser ooTUser, UpdateVenteParamDTO param) {
        TPreenregistrement p = this.getEm().find(TPreenregistrement.class, param.getVenteId());
        if (!VENTE_ASSURANCE_ID.equals(p.getLgTYPEVENTEID().getLgTYPEVENTEID())) {
            return;
        }
        Date initiale = p.getDtUPDATED();
        LocalDate toDay = LocalDate.parse(param.getDate());
        LocalDateTime venteDateNew = LocalDateTime.of(toDay, LocalTime.parse(param.getHeure()));
        Date venteDate = DateCommonUtils.convertLocalDateTimeToDate(venteDateNew);
        p.setDtCREATED(venteDate);
        p.setDtUPDATED(venteDate);
        p.setLgUSERID(ooTUser);
        getEm().merge(p);
        MvtTransaction mt = findMvtTransactionByVenteId(p.getLgPREENREGISTREMENTID());
        if (mt != null) {
            mt.setMvtDate(toDay);
            mt.setCreatedAt(venteDateNew);
            mt.setUser(ooTUser);
            getEm().merge(mt);

        }
        Collection<TPreenregistrementDetail> details = p.getTPreenregistrementDetailCollection();
        details.forEach(detail -> {
            updatePreenregistrementDetailDate(detail, venteDate);
            updateHMvtProduitDate(findHMvtProduitByPkey(detail.getLgPREENREGISTREMENTDETAILID()), venteDateNew,
                    ooTUser);

        });
        Collection<TPreenregistrementCompteClientTiersPayent> preenregistrementCompteClientTiersPayents = p
                .getTPreenregistrementCompteClientTiersPayentCollection();
        if (CollectionUtils.isNotEmpty(preenregistrementCompteClientTiersPayents)) {
            preenregistrementCompteClientTiersPayents.forEach(t -> {
                t.setDtCREATED(venteDate);
                t.setDtUPDATED(venteDate);
                t.setLgUSERID(ooTUser);
                this.getEm().merge(t);
            });
        }
        Collection<TPreenregistrementCompteClient> preenregistrementCompteClientCollection = p
                .getTPreenregistrementCompteClientCollection();
        if (CollectionUtils.isNotEmpty(preenregistrementCompteClientCollection)) {
            preenregistrementCompteClientCollection.forEach(t -> {
                t.setDtCREATED(venteDate);
                t.setDtUPDATED(venteDate);
                t.setLgUSERID(ooTUser);
                this.getEm().merge(t);
            });
        }
        p.getVenteReglements().forEach(t -> {
            t.setMvtDate(venteDateNew);
            this.getEm().merge(t);
        });
        String desc = "Modification de la vente " + p.getStrREF() + " date initiale : "
                + DateCommonUtils.formatDate(initiale) + " nouvelle date :" + DateCommonUtils.formatDate(venteDate)
                + " par " + ooTUser.getStrFIRSTNAME() + " " + ooTUser.getStrLASTNAME();
        this.logService.updateItem(ooTUser, p.getLgPREENREGISTREMENTID(), desc, TypeLog.MODIFICATION_DATE_VENTE_CREDIT,
                p);
    }

    private void updatePreenregistrementDetailDate(TPreenregistrementDetail item, Date venteDate) {
        item.setDtCREATED(venteDate);
        item.setDtUPDATED(venteDate);
        this.getEm().merge(item);

    }

    private void updateHMvtProduitDate(HMvtProduit hMvtProduit, LocalDateTime venteDate, TUser tUser) {
        hMvtProduit.setMvtDate(venteDate.toLocalDate());
        hMvtProduit.setCreatedAt(venteDate);
        hMvtProduit.setLgUSERID(tUser);
        this.getEm().merge(hMvtProduit);

    }

    private HMvtProduit findHMvtProduitByPkey(String pkey) {
        try {
            TypedQuery<HMvtProduit> q = getEm().createQuery(
                    "SELECT o FROM HMvtProduit o WHERE (o.preenregistrementDetail.lgPREENREGISTREMENTDETAILID =?1 OR o.pkey=?1) ",
                    HMvtProduit.class);
            q.setParameter(1, pkey);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private int computeCmuAmount(TPreenregistrementDetail pd) {

        if (Objects.nonNull(pd.getCmuPrice()) && pd.getCmuPrice() > 0) {
            return pd.getIntQUANTITY() * pd.getCmuPrice();
        }
        return pd.getIntQUANTITY() * pd.getIntPRICEUNITAIR();
    }

    private int computeVenteAmount(List<TPreenregistrementDetail> details) {
        return details.stream().map(TPreenregistrementDetail::getIntPRICE).reduce(0, Integer::sum);
    }

    private boolean diffAmount(int venteAmount, List<TPreenregistrementDetail> details) {
        return computeVenteAmount(details) != venteAmount;
    }

    private void updateCompteClientTiersPayantEncourAndPlafond(TPreenregistrementCompteClientTiersPayent x) {
        try {

            TCompteClientTiersPayant tc = x.getLgCOMPTECLIENTTIERSPAYANTID();
            TTiersPayant tp = tc.getLgTIERSPAYANTID();
            tc.setDbCONSOMMATIONMENSUELLE(tc.getDbCONSOMMATIONMENSUELLE() != null
                    ? tc.getDbCONSOMMATIONMENSUELLE() + x.getIntPRICE() : x.getIntPRICE());
            tp.setDbCONSOMMATIONMENSUELLE(tp.getDbCONSOMMATIONMENSUELLE() != null
                    ? tp.getDbCONSOMMATIONMENSUELLE() + x.getIntPRICE() : x.getIntPRICE());
            if (tc.getDbPLAFONDENCOURS() != null && tc.getDbPLAFONDENCOURS() > 0) {
                tc.setBCANBEUSE(tc.getDbPLAFONDENCOURS().compareTo(tc.getDbCONSOMMATIONMENSUELLE()) > 0);
            }
            if (tp.getDblPLAFONDCREDIT() != null && tp.getDblPLAFONDCREDIT().intValue() > 0) {
                tp.setBCANBEUSE(tp.getDblPLAFONDCREDIT().intValue() > tp.getDbCONSOMMATIONMENSUELLE());
            }
            getEm().merge(tc);
            getEm().merge(tp);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "updateCompteClientTiersPayantEncourAndPlafond", e);
        }
    }

    private void updateClientAccount(TPreenregistrementCompteClientTiersPayent payent) {
        updateCompteClientTiersPayantEncourAndPlafond(payent);
    }

    private boolean checkPlafondVente() {
        if (Objects.isNull(Utils.plafondVenteIsActive)) {
            try {
                TParameters tp = getEm().find(TParameters.class, "KEY_ACTIVATION_PLAFOND_VENTE");
                Utils.plafondVenteIsActive = (tp != null && tp.getStrVALUE().trim().equals("1"));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }
        return Utils.plafondVenteIsActive;
    }

    @Override
    public JSONObject computeVONet(SalesParams params) {
        TPreenregistrement pr = getEm().find(TPreenregistrement.class, params.getVenteId());
        MontantAPaye montant = calculVoNet(pr, params.getTierspayants());
        JSONObject json = new JSONObject();
        int montantNet = montant.getMontantNet();
        pr.setIntPRICEREMISE(montant.getRemise());
        pr.setIntCUSTPART(montantNet);
        pr.setIntPRICE(montant.getMontant());
        pr.setCmuAmount(montant.getCmuAmount());
        // pr.setIntACCOUNT(montant.getMontantAccount());
        getEm().merge(pr);
        montant.setRemise(DateConverter.arrondiModuloOfNumber(montant.getRemise(), 5));
        montant.setMontantNet(DateConverter.arrondiModuloOfNumber(montant.getMontantNet(), 5));
        json.put("hasRestructuring", montant.isRestructuring());
        json.put("success", true).put("msg", montant.getMessage());
        json.put("data", new JSONObject(montant));
        afficheurMontantAPayer(montant.getMontantNet(), "NET A PAYER: ");

        return json;
    }

    private MontantAPaye computeCarnetNet(MontantAPaye montantAPaye, String compteTp, boolean asRestrictions) {
        String msg = " ";
        boolean hasRestructuring = false;
        int remiseCarnet = montantAPaye.getRemise();
        int montantvente = montantAPaye.getMontant();
        TiersPayantParams tp = new TiersPayantParams();
        int totalTp = montantvente - remiseCarnet;
        int tierspayantAmount = totalTp;

        tp.setDiscount(remiseCarnet);
        tp.setTaux(100);
        if (asRestrictions) {
            JSONObject json = chechCustomerTiersPayantConsumption(compteTp, tierspayantAmount);
            if (json.getBoolean("hasRestructuring")) {
                msg = json.getString("msg");
                hasRestructuring = json.getBoolean("hasRestructuring");
                tierspayantAmount = json.getInt("montantToBePaid");
                if (totalTp != tierspayantAmount) {

                    tp.setTaux((int) Math.ceil((Double.valueOf(totalTp) * 100) / totalTp));

                }
            }
        }
        tp.setTpnet(tierspayantAmount);
        MontantAPaye map = new MontantAPaye(totalTp - tierspayantAmount, montantvente, tierspayantAmount, remiseCarnet,
                montantAPaye.getMarge(), montantAPaye.getMontantTva());
        map.setTierspayants(List.of(tp));
        map.setMessage(msg);
        map.setRestructuring(hasRestructuring);
        return map;

    }

    private JSONObject calculVoNetAvecPlafondVente(TPreenregistrement pr, int montantVariable, int taux,
            List<TPreenregistrementDetail> list) {
        JSONObject tp = new JSONObject();
        try {
            TClient client = pr.getClient();
            int remiseCarnet = 0;
            int totalTp;

            MontantAPaye montantAPaye;
            TRemise remise = pr.getRemise();
            remise = remise != null ? remise : client.getRemise();
            if (remise != null) {
                montantAPaye = getRemiseVno(pr, remise, list);

                remiseCarnet = montantAPaye.getRemise();
            } else {
                montantAPaye = sumVenteSansRemise(list);

            }
            int montantvente = montantAPaye.getMontant();

            if (pr.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(VENTE_AVEC_CARNET)) {

                Integer tpnet = montantvente - remiseCarnet;
                totalTp = tpnet;
                tp.put("montanttp", totalTp);
                tp.put("taux", taux);
                tp.put("reste", 0);

                return tp;

            } else {

                montantVariable = montantvente;
                Double montantTp = montantvente * (Double.valueOf(taux) / 100);
                int tpnet = (int) Math.ceil(montantTp);
                int taux3;

                if (montantVariable > tpnet) {
                    montantVariable -= tpnet;
                    taux3 = taux;
                } else {
                    tpnet = montantVariable;
                    taux3 = (int) Math.ceil((montantTp * 100) / montantvente);
                }

                tp.put("montanttp", tpnet);
                tp.put("taux", taux3);
                tp.put("reste", montantVariable);

            }

            return tp;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject();
        }
    }

    private MvtTransaction findMvtTransactionByVenteId(String venteId) {
        try {
            TypedQuery<MvtTransaction> tq = getEm().createQuery(
                    "SELECT o FROM MvtTransaction o WHERE (o.preenregistrement.lgPREENREGISTREMENTID =?1 OR o.pkey=?1) ",
                    MvtTransaction.class);
            tq.setParameter(1, venteId);
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }
}
