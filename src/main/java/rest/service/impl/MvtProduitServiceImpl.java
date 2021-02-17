/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.AjustementDTO;
import commonTasks.dto.AjustementDetailDTO;
import commonTasks.dto.Params;
import commonTasks.dto.RetourDetailsDTO;
import commonTasks.dto.RetourFournisseurDTO;
import commonTasks.dto.SalesStatsParams;
import dal.Notification;
import dal.TAjustement;
import dal.TAjustementDetail;
import dal.TAjustementDetail_;
import dal.TAjustement_;
import dal.TDeconditionnement;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TFamille_;
import dal.TGrossiste_;
import dal.TMouvement;
import dal.TMouvementSnapshot;
import dal.TMouvementprice;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TRetourFournisseur;
import dal.TRetourFournisseurDetail;
import dal.TRetourFournisseurDetail_;
import dal.TRetourFournisseur_;
import dal.TRetourdepot;
import dal.TRetourdepotdetail;
import dal.TUser;
import dal.TUser_;
import dal.Typemvtproduit;
import dal.enumeration.Canal;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.LogService;
import rest.service.MouvementProduitService;
import rest.service.MvtProduitService;
import rest.service.NotificationService;
import rest.service.SuggestionService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author Kobena
 */
@Stateless
public class MvtProduitServiceImpl implements MvtProduitService {

    private static final Logger LOG = Logger.getLogger(MvtProduitServiceImpl.class.getName());
    Comparator<AjustementDetailDTO> comparator = Comparator.comparing(AjustementDetailDTO::getDateOperation);
    @EJB
    SuggestionService suggestionService;
    @EJB
    LogService logService;
    @EJB
    MouvementProduitService mouvementProduitService;
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    NotificationService notificationService;

    public MvtProduitServiceImpl() {
    }

    public EntityManager getEmg() {
        return em;

    }

    @Override
    public void updatefamillenbvente(TFamille famille, int qty, boolean updatable, EntityManager emg) {
        if (updatable) {
            famille.setDtLASTMOUVEMENT(new Date());
            famille.setIntQTERESERVEE(famille.getIntNBRESORTIE() + qty);
            emg.merge(famille);
        }

    }

    public void saveMouvementPrice(TUser user, TFamille OTFamille,
            Integer old, Integer newPu,
            int taux, String action,
            String ref, EntityManager emg) {
        TMouvementprice mouvementprice = new TMouvementprice(UUID.randomUUID().toString());
        mouvementprice.setDtCREATED(new Date());
        mouvementprice.setDtDAY(new Date());
        mouvementprice.setIntPRICENEW(newPu);
        mouvementprice.setIntECART(old - newPu);
        mouvementprice.setIntPRICEOLD(old);
        mouvementprice.setDtUPDATED(new Date());
        mouvementprice.setLgUSERID(user);
        mouvementprice.setStrACTION(action);
        mouvementprice.setStrREF(ref);
        mouvementprice.setLgFAMILLEID(OTFamille);
        mouvementprice.setStrSTATUT(commonparameter.statut_enable);
        emg.persist(mouvementprice);
    }

    @Override
    public void saveMvtArticle(TFamille tf, TUser ooTUser, TFamilleStock familleStock, int qty, String emplacementId, EntityManager emg) {
        Optional<TMouvement> tm = findMouvement(tf, commonparameter.REMOVE, commonparameter.str_ACTION_VENTE, emplacementId, emg);
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
            OTMouvement.setStrTYPEACTION(commonparameter.REMOVE);
            OTMouvement.setDtCREATED(new Date());
            OTMouvement.setDtUPDATED(new Date());
            OTMouvement.setLgEMPLACEMENTID(emplacementFromId(emplacementId, emg));
            emg.persist(OTMouvement);
        }
        createSnapshotMouvementArticle(tf, qty, ooTUser, familleStock, emplacementId, emg);
    }

    private TEmplacement emplacementFromId(String lgEMPLACEMENTID, EntityManager emg) {
        return emg.find(TEmplacement.class, lgEMPLACEMENTID);
    }

    @Override
    public Optional<TMouvement> findMouvement(TFamille OTFamille, String action, String typeAction, String emplacementId, EntityManager emg) {
        try {
            TypedQuery<TMouvement> query = emg.createQuery("SELECT t FROM TMouvement t WHERE    t.dtDAY  = ?1   AND t.lgFAMILLEID.lgFAMILLEID = ?2 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?3 AND t.strACTION = ?4 AND t.strTYPEACTION = ?5 ", TMouvement.class);
            query.setParameter(1, new Date(), TemporalType.DATE).
                    setParameter(2, OTFamille.getLgFAMILLEID()).
                    setParameter(3, emplacementId).
                    setParameter(4, action).
                    setParameter(5, typeAction);

            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<TMouvementSnapshot> findTMouvementSnapshot(String lg_FAMILLE_ID, String emplacementId, EntityManager emg) {
        try {
            TypedQuery<TMouvementSnapshot> query = emg.createQuery("SELECT t FROM TMouvementSnapshot t WHERE    t.dtDAY  = ?1   AND t.lgFAMILLEID.lgFAMILLEID = ?2 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?3  ", TMouvementSnapshot.class);
            query.setParameter(1, new Date(), TemporalType.DATE).
                    setParameter(2, lg_FAMILLE_ID).
                    setParameter(3, emplacementId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void createSnapshotMouvementArticle(TFamille OTFamille, int qty, TUser ooTUser, TFamilleStock familleStock, String emplacementId, EntityManager emg) {

        Optional<TMouvementSnapshot> tm = findTMouvementSnapshot(OTFamille.getLgFAMILLEID(), emplacementId, emg);
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
            OTMouvementSnapshot.setIntNUMBERTRANSACTION(1);
            OTMouvementSnapshot.setIntSTOCKJOUR(familleStock.getIntNUMBERAVAILABLE() - qty);
            OTMouvementSnapshot.setIntSTOCKDEBUT(familleStock.getIntNUMBERAVAILABLE());
            OTMouvementSnapshot.setLgEMPLACEMENTID(emplacementFromId(emplacementId, emg));
            emg.persist(OTMouvementSnapshot);
            familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - qty);
            familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
            familleStock.setDtUPDATED(new Date());
            emg.merge(familleStock);
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

    private TFamilleStock findStock(String OTFamille, TEmplacement emplacement, EntityManager emg) {

        try {
            TypedQuery<TFamilleStock> query = emg.createQuery("SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable' ORDER BY t.dtCREATED DESC", TFamilleStock.class);
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

    @Override
    public void updateStockDepot(TUser user, TPreenregistrement tp, TEmplacement OTEmplacement, EntityManager emg) throws Exception {
        List<TPreenregistrementDetail> list = getTPreenregistrementDetail(tp, emg);
        user = (user == null) ? tp.getLgUSERID() : user;
        final Typemvtproduit typemvtproduit = getTypemvtproduitByID(DateConverter.ENTREE_EN_STOCK);
        for (TPreenregistrementDetail d : list) {
            TFamille tFamille = d.getLgFAMILLEID();
            updateStockDepot(typemvtproduit, user, tFamille, d.getIntQUANTITYSERVED(), OTEmplacement, emg);
        }
    }

    private TFamille findProduitById(String id, EntityManager emg) {

        try {
            return emg.find(TFamille.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateVenteStockDepot(TPreenregistrement tp, List<TPreenregistrementDetail> list, EntityManager emg, TEmplacement depot) throws Exception {
        TUser tu = tp.getLgUSERID();
        final TEmplacement emplacement = tu.getLgEMPLACEMENTID();
        final String emplacementId = emplacement.getLgEMPLACEMENTID();
        final boolean isDepot = !("1".equals(emplacementId));
        //
        final Typemvtproduit typemvtproduit = tp.getChecked() ? getTypemvtproduitByID(DateConverter.VENTE) : getTypemvtproduitByID(DateConverter.TMVTP_VENTE_DEPOT_EXTENSION);
        final Typemvtproduit __typemvtproduit = getTypemvtproduitByID(DateConverter.ENTREE_EN_STOCK);
        list.stream().forEach(it -> {
            it.setStrSTATUT(commonparameter.statut_is_Closed);
            TFamille tFamille = it.getLgFAMILLEID();
            if (it.getIntPRICEUNITAIR().compareTo(tFamille.getIntPRICE()) != 0) {
                saveMouvementPrice(tu, tFamille, tFamille.getIntPRICE(), it.getIntPRICEUNITAIR(), 0, commonparameter.str_ACTION_VENTE, tp.getStrREF(), emg);
                String desc = "Modification du prix du produit [ " + tFamille.getIntCIP() + " ] de " + tFamille.getIntPRICE() + " à " + it.getIntPRICEUNITAIR() + " à la vente par " + tu.getStrFIRSTNAME() + " " + tu.getStrLASTNAME();
                logService.updateItem(tu, tp.getStrREF(), desc, TypeLog.MODIFICATION_PRIX_VENTE_PRODUIT, tp, emg);
                notificationService.save(new Notification()
                        .canal(Canal.EMAIL)
                        .typeNotification(TypeNotification.MODIFICATION_PRIX_VENTE_PRODUIT)
                        .message(desc)
                        .addUser(tu)
                );
            }
            TFamilleStock familleStock = findStock(tFamille.getLgFAMILLEID(), emplacement, emg);
            int initStock = familleStock.getIntNUMBERAVAILABLE();
            if (tFamille.getBoolDECONDITIONNE() == 1) {
                //LOG.log(Level.INFO, "updateVenteStock -------------------- {0}\n quantité produit  {1} \n quantie de la vente {2}", new Object[]{tFamille.getIntCIP(), familleStock.getIntNUMBERAVAILABLE(), it.getIntQUANTITY()});
                if (!checkIsVentePossible(familleStock, it.getIntQUANTITY())) {
                    TFamille OTFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                    TFamilleStock stockParent = findByProduitId(OTFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID(), emg);
                    //  LOG.log(Level.INFO, "updateVenteStock -------------------- {0}\n quantité parent  {1}", new Object[]{OTFamilleParent.getIntCIP(), stockParent.getIntNUMBERAVAILABLE()});
                    deconditionner(tu, emplacement, tFamille, OTFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);

                } else {
                    saveMvtArticle(tFamille, tu, familleStock, it.getIntQUANTITY(), emplacementId, emg);
                }
            } else {
                //  LOG.log(Level.INFO, "updateVenteStock *********************************   {0}\n quantité produit  {1} \n quantie de la vente {2}", new Object[]{tFamille.getIntCIP(), familleStock.getIntNUMBERAVAILABLE(), it.getIntQUANTITY()});
                saveMvtArticle(tFamille, tu, familleStock, it.getIntQUANTITY(), emplacementId, emg);
            }

            updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
            mouvementProduitService.saveMvtProduit(it.getIntPRICEUNITAIR(), it.getLgPREENREGISTREMENTDETAILID(),
                    typemvtproduit, tFamille, tu, emplacement,
                    it.getIntQUANTITY(), initStock, initStock - it.getIntQUANTITY(), emg, it.getValeurTva(), tp.getChecked(), it.getIntUG());

            emg.merge(it);
            updateStockDepot(__typemvtproduit, tu, tFamille, it.getIntQUANTITYSERVED(), depot, emg);

            suggestionService.makeSuggestionAuto(familleStock, tFamille, emg);
        });

    }

    @Override
    public void updateVenteStock(TPreenregistrement tp, List<TPreenregistrementDetail> list, EntityManager emg) {
        TUser tu = tp.getLgUSERID();
        final TEmplacement emplacement = tu.getLgEMPLACEMENTID();
        final String emplacementId = emplacement.getLgEMPLACEMENTID();
        final boolean isDepot = !("1".equals(emplacementId));
        list.stream().forEach(it -> {
            it.setStrSTATUT(commonparameter.statut_is_Closed);
            TFamille tFamille = it.getLgFAMILLEID();
            if (it.getIntPRICEUNITAIR().compareTo(tFamille.getIntPRICE()) != 0) {
                saveMouvementPrice(tu, tFamille, tFamille.getIntPRICE(), it.getIntPRICEUNITAIR(), 0, commonparameter.str_ACTION_VENTE, tp.getStrREF(), emg);
                String desc = "Modification du prix du produit [ " + tFamille.getIntCIP() + " ] de " + tFamille.getIntPRICE() + " à " + it.getIntPRICEUNITAIR() + " à la vente par " + tu.getStrFIRSTNAME() + " " + tu.getStrLASTNAME();
                logService.updateItem(tu, tFamille.getIntCIP(), desc, TypeLog.MODIFICATION_PRIX_VENTE_PRODUIT, tFamille, emg);
                notificationService.save(new Notification()
                        .canal(Canal.EMAIL)
                        .typeNotification(TypeNotification.MODIFICATION_PRIX_VENTE_PRODUIT)
                        .message(desc)
                        .addUser(tu));
            }
            TFamilleStock familleStock = findStock(tFamille.getLgFAMILLEID(), emplacement, emg);
            if (tFamille.getBoolDECONDITIONNE() == 1) {
                //LOG.log(Level.INFO, "updateVenteStock -------------------- {0}\n quantité produit  {1} \n quantie de la vente {2}", new Object[]{tFamille.getIntCIP(), familleStock.getIntNUMBERAVAILABLE(), it.getIntQUANTITY()});
                if (!checkIsVentePossible(familleStock, it.getIntQUANTITY())) {
                    TFamille OTFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                    TFamilleStock stockParent = findByProduitId(OTFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID(), emg);
                    //  LOG.log(Level.INFO, "updateVenteStock -------------------- {0}\n quantité parent  {1}", new Object[]{OTFamilleParent.getIntCIP(), stockParent.getIntNUMBERAVAILABLE()});
                    deconditionner(tu, emplacement, tFamille, OTFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);

                } else {
                    saveMvtArticle(tFamille, tu, familleStock, it.getIntQUANTITY(), emplacementId, emg);
                }
            } else {
                //  LOG.log(Level.INFO, "updateVenteStock *********************************   {0}\n quantité produit  {1} \n quantie de la vente {2}", new Object[]{tFamille.getIntCIP(), familleStock.getIntNUMBERAVAILABLE(), it.getIntQUANTITY()});
                saveMvtArticle(tFamille, tu, familleStock, it.getIntQUANTITY(), emplacementId, emg);
            }

            updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
            emg.merge(it);
            suggestionService.makeSuggestionAuto(familleStock, tFamille, emg);
        });

    }

    private Typemvtproduit getTypemvtproduitByID(String id) {
        return getEmg().find(Typemvtproduit.class, id);
    }

    private void updateQtyUg(TFamilleStock familleStock, TPreenregistrement tp, TPreenregistrementDetail it) {
        try {
            if (tp.getStrTYPEVENTE().equals(DateConverter.VENTE_COMPTANT) && familleStock.getIntUG() > 0) {
                int ugVendue = 0;
                int stockUg = familleStock.getIntUG();
                int qtyVendue = it.getIntQUANTITYSERVED();
                if (qtyVendue <= stockUg) {
                    ugVendue = qtyVendue;
                } else {
                    ugVendue = stockUg;
                }
                familleStock.setIntUG(familleStock.getIntUG() - ugVendue);
                it.setIntUG(ugVendue);
            }
        } catch (Exception e) {
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
            final Typemvtproduit typemvtproduit = getTypemvtproduitByID(DateConverter.VENTE);
            list.stream().forEach(it -> {
                TFamille tFamille = it.getLgFAMILLEID();
                if (it.getIntPRICEUNITAIR().compareTo(tFamille.getIntPRICE()) != 0) {
                    saveMouvementPrice(tu, tFamille, tFamille.getIntPRICE(), it.getIntPRICEUNITAIR(), 0, commonparameter.str_ACTION_VENTE, tp.getStrREF(), emg);
                    String desc = "Modification du prix du produit [ " + tFamille.getIntCIP() + " ] de " + tFamille.getIntPRICE() + " à " + it.getIntPRICEUNITAIR() + " à la vente par " + tu.getStrFIRSTNAME() + " " + tu.getStrLASTNAME();
                    logService.updateItem(tu, tFamille.getIntCIP(), desc, TypeLog.MODIFICATION_PRIX_VENTE_PRODUIT, tFamille, emg);
                    notificationService.save(new Notification()
                            .canal(Canal.EMAIL)
                            .typeNotification(TypeNotification.MODIFICATION_PRIX_VENTE_PRODUIT)
                            .message(desc)
                            .addUser(tu));
                }
                TFamilleStock familleStock = findStock(tFamille.getLgFAMILLEID(), emplacement, emg);
                Integer initStock = familleStock.getIntNUMBERAVAILABLE();
                updateQtyUg(familleStock, tp, it);
                if (tFamille.getBoolDECONDITIONNE() == 1) {
                    if (!checkIsVentePossible(familleStock, it.getIntQUANTITY())) {
                        TFamille OTFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                        TFamilleStock stockParent = findByProduitId(OTFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID(), emg);
                        deconditionner(tu, emplacement, tFamille, OTFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);
                    } else {
                        saveMvtArticle(tFamille, tu, familleStock, it.getIntQUANTITY(), emplacementId, emg);
                    }
                } else {
                    saveMvtArticle(tFamille, tu, familleStock, it.getIntQUANTITY(), emplacementId, emg);
                }
                updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
                mouvementProduitService.saveMvtProduit(it.getIntPRICEUNITAIR(), it.getLgPREENREGISTREMENTDETAILID(),
                        typemvtproduit, tFamille, tu, emplacement,
                        it.getIntQUANTITY(), initStock, initStock - it.getIntQUANTITY(), emg, it.getValeurTva(), true, it.getIntUG());
                emg.merge(it);
            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    private void updateStockDepot(Typemvtproduit typemvtproduit, TUser ooTUser, TFamille OTFamille, Integer qty, TEmplacement OTEmplacement, EntityManager emg) {
        Integer initStock = 0;
        TFamilleStock familleStock;
        boolean isDetail = (OTFamille.getLgFAMILLEPARENTID() != null && !"".equals(OTFamille.getLgFAMILLEPARENTID()));

        familleStock = findByProduitId(OTFamille.getLgFAMILLEID(), OTEmplacement.getLgEMPLACEMENTID(), emg);

        if (familleStock != null) {
            initStock = familleStock.getIntNUMBERAVAILABLE();
            familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() + qty);
            familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
            familleStock.setDtUPDATED(new Date());
            emg.merge(familleStock);

        } else if (familleStock == null) {

            if (isDetail) {
                familleStock = findByParent(OTFamille.getLgFAMILLEPARENTID(), OTEmplacement.getLgEMPLACEMENTID(), emg);
                if (familleStock == null) {
                    familleStock = createStock(OTFamille, qty, OTEmplacement, emg);
                } else {
                    initStock = familleStock.getIntNUMBERAVAILABLE();
                    familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() + qty);
                    familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
                    familleStock.setDtUPDATED(new Date());
                    emg.merge(familleStock);
                }
                TFamilleStock _familleStock = findByProduitId(OTFamille.getLgFAMILLEPARENTID(), OTEmplacement.getLgEMPLACEMENTID(), emg);
                if (_familleStock == null) {
                    TFamille p = findProduitById(OTFamille.getLgFAMILLEPARENTID(), emg);
                    if (p != null) {
                        createStock(p, 0, OTEmplacement, emg);
                    }

                }
            } else {
                familleStock = createStock(OTFamille, qty, OTEmplacement, emg);

            }

        }
        mouvementProduitService.saveMvtProduit(OTFamille.getIntPRICE(), familleStock.getLgFAMILLESTOCKID(),
                typemvtproduit, OTFamille, ooTUser, OTEmplacement,
                qty, initStock, initStock - qty, emg, 0, false, 0);

        saveMvtArticleAddProduct(OTFamille, ooTUser, familleStock, qty, initStock, OTEmplacement, emg);
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

    @Override
    public void saveMvtArticleAddProduct(TFamille tf, TUser ooTUser, TFamilleStock familleStock, Integer qty, Integer initStock, TEmplacement emplacementId, EntityManager emg) {
        Optional<TMouvement> tm = findMouvement(tf, commonparameter.ADD, commonparameter.str_ACTION_ENTREESTOCK, emplacementId.getLgEMPLACEMENTID(), emg);
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
            OTMouvement.setStrACTION(commonparameter.str_ACTION_ENTREESTOCK);
            OTMouvement.setStrTYPEACTION(commonparameter.ADD);
            OTMouvement.setDtCREATED(new Date());
            OTMouvement.setDtUPDATED(new Date());
            OTMouvement.setLgEMPLACEMENTID(emplacementId);
            emg.persist(OTMouvement);
        }
        createSnapshotMvtArticle(tf, familleStock, initStock, emplacementId, emg);
//        createSnapshotMouvementArticle(tf, qty, ooTUser, familleStock, emplacementId, emg);
    }

    public boolean checkIsVentePossible(TFamilleStock OTFamilleStock, int qte
    ) {
        return OTFamilleStock.getIntNUMBERAVAILABLE() >= qte;
    }

    public void updateTMouvement(TMouvement OTMouvement, Integer int_NUMBER, EntityManager emg) {
        OTMouvement.setStrSTATUT(commonparameter.statut_enable);
        OTMouvement.setDtUPDATED(new Date());
        OTMouvement.setIntNUMBERTRANSACTION(OTMouvement.getIntNUMBERTRANSACTION() + 1);
        OTMouvement.setIntNUMBER(OTMouvement.getIntNUMBER() + int_NUMBER);
        emg.merge(OTMouvement);

    }

    private void deconditionner(TUser tu, TEmplacement te, TFamille OTFamilleChild, TFamille OTFamilleParent, TFamilleStock OTFamilleStockParent, TFamilleStock OTFamilleStockChild, Integer qteVendue, EntityManager emg) {
        Integer numberToDecondition = 0;
        Integer qtyDetail = OTFamilleParent.getIntNUMBERDETAIL();
        Integer stockInitDetail = OTFamilleStockChild.getIntNUMBERAVAILABLE();
        Integer stockDetailInit = stockInitDetail;
        Integer stockInit = OTFamilleStockParent.getIntNUMBERAVAILABLE();
        Integer stockVirtuel = stockInitDetail + (stockInit * qtyDetail);
        int compare = stockVirtuel.compareTo(qteVendue);
//        LOG.log(Level.INFO, "------------  deconditionner >>>>>>>> compare {0} \n stockVirtuel {1} \n qtyDetail {2} \n stockInitDetail {3} stock parent {4}",
//                new Object[]{compare, stockVirtuel, qtyDetail, stockInitDetail, stockInit});
        if (compare >= 0) {
            while (stockInitDetail < qteVendue) {
                numberToDecondition++;
                stockInitDetail += qtyDetail;
            }
            OTFamilleStockParent.setIntNUMBERAVAILABLE(OTFamilleStockParent.getIntNUMBERAVAILABLE() - numberToDecondition);
            OTFamilleStockParent.setIntNUMBER(OTFamilleStockParent.getIntNUMBERAVAILABLE());
            OTFamilleStockParent.setDtUPDATED(new Date());

            OTFamilleStockChild.setIntNUMBERAVAILABLE(OTFamilleStockChild.getIntNUMBERAVAILABLE() + (numberToDecondition * qtyDetail) - qteVendue);
            OTFamilleStockChild.setIntNUMBER(OTFamilleStockChild.getIntNUMBERAVAILABLE());
            OTFamilleStockChild.setDtUPDATED(new Date());
            emg.merge(OTFamilleStockParent);
            emg.merge(OTFamilleStockChild);
            TDeconditionnement parent = createDecondtionne(OTFamilleParent, numberToDecondition, tu, emg);
            TDeconditionnement child = createDecondtionne(OTFamilleChild, (numberToDecondition * qtyDetail), tu, emg);
            Optional<TMouvement> opChild = findByDay(OTFamilleChild, te.getLgEMPLACEMENTID(), emg);
            if (opChild.isPresent()) {
                updateTMouvement(opChild.get(), (numberToDecondition * qtyDetail), emg);
            } else {
                createTMouvementDecon(OTFamilleChild, te, commonparameter.ADD, commonparameter.str_ACTION_DECONDITIONNEMENT, (numberToDecondition * qtyDetail), tu, emg);
            }
            saveMvtArticle(OTFamilleChild, tu, OTFamilleStockChild, stockDetailInit, qteVendue, te, emg);
            Optional<TMouvement> opParent = findByDay(OTFamilleParent, te.getLgEMPLACEMENTID(), emg);
            if (opParent.isPresent()) {
                updateTMouvement(opParent.get(), numberToDecondition, emg);
            } else {
                createTMouvementDecon(OTFamilleParent, te, commonparameter.REMOVE, commonparameter.str_ACTION_DECONDITIONNEMENT, numberToDecondition, tu, emg);
            }
            Optional<TMouvementSnapshot> mvtChild = findMouvementSnapshotByDay(OTFamilleChild, te.getLgEMPLACEMENTID(), emg);
            if (mvtChild.isPresent()) {
                updateSnapshotMouvementArticle(mvtChild.get(), OTFamilleStockChild, emg);
            } else {
                createSnapshotMouvementDecon(OTFamilleChild, OTFamilleStockChild.getIntNUMBERAVAILABLE(), stockInitDetail, te, emg);
            }
            Optional<TMouvementSnapshot> mvtparent = findMouvementSnapshotByDay(OTFamilleParent, te.getLgEMPLACEMENTID(), emg);
            if (mvtparent.isPresent()) {
                updateSnapshotMouvementArticle(mvtparent.get(), OTFamilleStockParent, emg);
            } else {
                createSnapshotMouvementDecon(OTFamilleParent, OTFamilleStockParent.getIntNUMBERAVAILABLE(), stockInit, te, emg);
            }
            mouvementProduitService.saveMvtProduit(child.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_POSITIF, OTFamilleChild, tu, OTFamilleStockParent.getLgEMPLACEMENTID(), (numberToDecondition * qtyDetail), stockInitDetail, stockInitDetail + (numberToDecondition * qtyDetail) - qteVendue, emg, 0);
            mouvementProduitService.saveMvtProduit(parent.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_NEGATIF, OTFamilleParent, tu, OTFamilleStockParent.getLgEMPLACEMENTID(), numberToDecondition, stockInit, stockInit - numberToDecondition, emg, 0);
            String desc = "Déconditionnement du produit [ " + OTFamilleParent.getIntCIP() + " ] de " + OTFamilleParent.getIntPRICE() + " stock initial " + stockInit + " quantité déconditionnée " + numberToDecondition + " stock finale " + (stockInit - numberToDecondition) + " stock détail initial  " + stockInitDetail + " stock détail final = " + (stockInitDetail + (numberToDecondition * qtyDetail) - qteVendue) + " . Opérateur : " + tu.getStrFIRSTNAME() + " " + tu.getStrLASTNAME();
            logService.updateItem(tu, OTFamilleParent.getIntCIP(), desc, TypeLog.DECONDITIONNEMENT, OTFamilleParent, emg);

            notificationService.save(new Notification()
                    .canal(Canal.EMAIL)
                    .typeNotification(TypeNotification.DECONDITIONNEMENT)
                    .message(desc)
                    .addUser(tu));

        }
    }

    private Optional<TMouvement> findByDay(TFamille OTFamille, String lgEmpl, EntityManager emg) {
        try {
            TypedQuery<TMouvement> query = emg.createQuery("SELECT o FROM TMouvement o  WHERE o.dtDAY =?1 AND o.lgFAMILLEID.lgFAMILLEID =?2 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?3 AND o.strACTION =?4", TMouvement.class);
            query.setParameter(1, new Date(), TemporalType.DATE);
            query.setParameter(2, OTFamille.getLgFAMILLEID());
            query.setParameter(3, lgEmpl);
            query.setParameter(4, commonparameter.str_ACTION_DECONDITIONNEMENT);
            query.setFirstResult(0).setMaxResults(1);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    private Optional<TMouvementSnapshot> findMouvementSnapshotByDay(TFamille OTFamille, String lgEmpl, EntityManager emg) {
        try {
            TypedQuery<TMouvementSnapshot> query = emg.createQuery("SELECT o FROM TMouvementSnapshot o  WHERE o.dtDAY =?1 AND o.lgFAMILLEID.lgFAMILLEID =?2 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?3 AND o.strSTATUT='enable' ", TMouvementSnapshot.class);
            query.setParameter(1, new Date(), TemporalType.DATE);
            query.setParameter(2, OTFamille.getLgFAMILLEID());
            query.setParameter(3, lgEmpl);
            query.setFirstResult(0).setMaxResults(1);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public void updateSnapshotMouvementArticle(TMouvementSnapshot OTMouvementSnapshot, TFamilleStock stock, EntityManager emg) {
        OTMouvementSnapshot.setDtUPDATED(new Date());
        OTMouvementSnapshot.setIntNUMBERTRANSACTION(OTMouvementSnapshot.getIntNUMBERTRANSACTION() + 1);
        OTMouvementSnapshot.setIntSTOCKJOUR(stock.getIntNUMBERAVAILABLE());
        emg.merge(OTMouvementSnapshot);
    }

    private void createSnapshotMouvementDecon(TFamille OTFamille, int int_NUMBER, int int_STOCK_DEBUT, TEmplacement OTEmplacement, EntityManager emg) {
        TMouvementSnapshot OTMouvementSnapshot = new TMouvementSnapshot();
        OTMouvementSnapshot.setLgMOUVEMENTSNAPSHOTID(UUID.randomUUID().toString());
        OTMouvementSnapshot.setLgFAMILLEID(OTFamille);
        OTMouvementSnapshot.setDtDAY(new Date());
        OTMouvementSnapshot.setDtCREATED(new Date());
        OTMouvementSnapshot.setDtUPDATED(new Date());
        OTMouvementSnapshot.setStrSTATUT(commonparameter.statut_enable);
        OTMouvementSnapshot.setIntNUMBERTRANSACTION(1);
        OTMouvementSnapshot.setIntSTOCKJOUR(int_NUMBER);
        OTMouvementSnapshot.setIntSTOCKDEBUT(int_STOCK_DEBUT);
        OTMouvementSnapshot.setLgEMPLACEMENTID(OTEmplacement);
        emg.persist(OTMouvementSnapshot);
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

    public void createTMouvementDecon(TFamille OTFamille, TEmplacement OTEmplacement, String str_TYPE_ACTION, String str_ACTION, Integer int_NUMBER, TUser user, EntityManager emg) {
        TMouvement OTMouvement = new TMouvement();
        OTMouvement.setLgMOUVEMENTID(UUID.randomUUID().toString());
        OTMouvement.setDtDAY(new Date());
        OTMouvement.setStrSTATUT(commonparameter.statut_enable);
        OTMouvement.setLgFAMILLEID(OTFamille);
        OTMouvement.setLgUSERID(user);
        OTMouvement.setPKey("");
        OTMouvement.setStrACTION(str_ACTION);
        OTMouvement.setStrTYPEACTION(str_TYPE_ACTION);
        OTMouvement.setDtCREATED(new Date());
        OTMouvement.setDtUPDATED(new Date());
        OTMouvement.setLgEMPLACEMENTID(OTEmplacement);
        OTMouvement.setIntNUMBERTRANSACTION(1);
        OTMouvement.setIntNUMBER(int_NUMBER);
        emg.persist(OTMouvement);

    }

    private void createSnapshotMvtArticle(TFamille OTFamille, TFamilleStock familleStock, Integer initStock, TEmplacement emplacementId, EntityManager emg) {

        Optional<TMouvementSnapshot> tm = findTMouvementSnapshot(OTFamille.getLgFAMILLEID(), emplacementId.getLgEMPLACEMENTID(), emg);
        if (tm.isPresent()) {
            TMouvementSnapshot mouvementSnapshot = tm.get();
            mouvementSnapshot.setDtUPDATED(new Date());
            mouvementSnapshot.setIntNUMBERTRANSACTION(mouvementSnapshot.getIntNUMBERTRANSACTION() + 1);
            mouvementSnapshot.setIntSTOCKJOUR(familleStock.getIntNUMBERAVAILABLE());
            emg.merge(mouvementSnapshot);
        } else {
            TMouvementSnapshot OTMouvementSnapshot = new TMouvementSnapshot();
            OTMouvementSnapshot.setLgMOUVEMENTSNAPSHOTID(UUID.randomUUID().toString());
            OTMouvementSnapshot.setLgFAMILLEID(OTFamille);
            OTMouvementSnapshot.setDtDAY(new Date());
            OTMouvementSnapshot.setDtCREATED(new Date());
            OTMouvementSnapshot.setDtUPDATED(new Date());
            OTMouvementSnapshot.setIntNUMBERTRANSACTION(1);
            OTMouvementSnapshot.setStrSTATUT(commonparameter.statut_enable);
            OTMouvementSnapshot.setIntSTOCKJOUR(familleStock.getIntNUMBERAVAILABLE());
            OTMouvementSnapshot.setIntSTOCKDEBUT(initStock);
            OTMouvementSnapshot.setLgEMPLACEMENTID(emplacementId);
            emg.persist(OTMouvementSnapshot);

        }
    }

    private void createSnapshotMvtArticle(TFamille OTFamille, Integer qty, TUser ooTUser, Integer initStock, Integer finalStock, TEmplacement emplacementId, EntityManager emg) {
        Optional<TMouvementSnapshot> tm = findTMouvementSnapshot(OTFamille.getLgFAMILLEID(), emplacementId.getLgEMPLACEMENTID(), emg);
        if (tm.isPresent()) {
            TMouvementSnapshot mouvementSnapshot = tm.get();
            mouvementSnapshot.setDtUPDATED(new Date());
            mouvementSnapshot.setIntNUMBERTRANSACTION(mouvementSnapshot.getIntNUMBERTRANSACTION() + 1);
            mouvementSnapshot.setIntSTOCKJOUR(finalStock);
            emg.merge(mouvementSnapshot);
        } else {
            TMouvementSnapshot OTMouvementSnapshot = new TMouvementSnapshot();
            OTMouvementSnapshot.setLgMOUVEMENTSNAPSHOTID(UUID.randomUUID().toString());
            OTMouvementSnapshot.setLgFAMILLEID(OTFamille);
            OTMouvementSnapshot.setDtDAY(new Date());
            OTMouvementSnapshot.setDtCREATED(new Date());
            OTMouvementSnapshot.setDtUPDATED(new Date());
            OTMouvementSnapshot.setIntNUMBERTRANSACTION(1);
            OTMouvementSnapshot.setStrSTATUT(commonparameter.statut_enable);
            OTMouvementSnapshot.setIntSTOCKJOUR(finalStock);
            OTMouvementSnapshot.setIntSTOCKDEBUT(initStock);
            OTMouvementSnapshot.setLgEMPLACEMENTID(emplacementId);
            emg.persist(OTMouvementSnapshot);

        }
    }

    public void saveMvtArticle(TFamille tf, TUser ooTUser, TFamilleStock familleStock, Integer qtyInit, int qty, TEmplacement emplacementId, EntityManager emg) {
        Optional<TMouvement> tm = findMouvement(tf, commonparameter.REMOVE, commonparameter.str_ACTION_VENTE, emplacementId.getLgEMPLACEMENTID(), emg);
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
            OTMouvement.setStrTYPEACTION(commonparameter.REMOVE);
            OTMouvement.setDtCREATED(new Date());
            OTMouvement.setDtUPDATED(new Date());
            OTMouvement.setLgEMPLACEMENTID(emplacementId);
            emg.persist(OTMouvement);
        }
        createSnapshotMvtArticle(tf, familleStock, qtyInit, emplacementId, emg);
    }

    @Override
    public JSONObject creerAjustement(Params params) throws JSONException {
        EntityManager emg = this.getEmg();
        JSONObject json = new JSONObject();
        try {
            String str_NAME = "Ajustement du " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"));
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
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            json.put("data", new JSONObject().put("lgAJUSTEMENTID", OTAjustement.getLgAJUSTEMENTID()));
        } catch (Exception e) {
            e.printStackTrace(System.err);
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
            ajusterProduitAjustement(params, ajustement, emg);
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            json.put("data", new JSONObject().put("lgAJUSTEMENTID", ajustement.getLgAJUSTEMENTID()));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            json.put("success", false).put("msg", "L'opération a échoué");

        }
        return json;
    }

    private void ajusterProduitAjustement(Params params, TAjustement ajustement, EntityManager emg) {
        TAjustementDetail OTAjustementDetail = updateAjustementDetail(params);
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
    public JSONObject modifierProduitAjustement(Params params) throws JSONException {
        EntityManager emg = getEmg();
        JSONObject json = new JSONObject();
        try {
            TAjustementDetail ajustementDetail = emg.find(TAjustementDetail.class, params.getRef());
            if (ajustementDetail == null) {

                json.put("success", false).put("msg", "L'opération a échoué");
                return json;
            }
            ajustementDetail.setIntNUMBER(params.getValue());
            ajustementDetail.setIntNUMBERAFTERSTOCK(params.getValue() + params.getValueTwo());
            ajustementDetail.setDtUPDATED(new Date());
            emg.merge(ajustementDetail);
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            json.put("data", new JSONObject().put("lgAJUSTEMENTID", ajustementDetail.getLgAJUSTEMENTID().getLgAJUSTEMENTID()));
            return json;

        } catch (Exception e) {
            e.printStackTrace(System.err);
            json.put("success", false).put("msg", "L'opération a échoué");
            return json;
        }
    }

    private TAjustementDetail updateAjustementDetail(Params params) {
        try {
            TAjustementDetail ajustementDetail = findAjustementDetailsByParenId(params.getRefParent(), params.getRefTwo());
            if (ajustementDetail == null) {
                return null;
            }
            ajustementDetail.setIntNUMBER(ajustementDetail.getIntNUMBER() + params.getValue());
            ajustementDetail.setIntNUMBERAFTERSTOCK(ajustementDetail.getIntNUMBERAFTERSTOCK() + params.getValue());
            ajustementDetail.setDtUPDATED(new Date());
            return getEmg().merge(ajustementDetail);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
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
                String action = (compare < 0) ? commonparameter.ADD : commonparameter.REMOVE;
                String _action = (compare < 0) ? DateConverter.AJUSTEMENT_POSITIF : DateConverter.AJUSTEMENT_NEGATIF;
                saveMvtArticle(commonparameter.str_ACTION_AJUSTEMENT, action, famille, tUser, familleStock, it.getIntNUMBER(), initStock, emplacement, emg);
                mouvementProduitService.saveMvtProduit(it.getLgAJUSTEMENTDETAILID(), _action, famille, tUser, emplacement, it.getIntNUMBER(), initStock, initStock + it.getIntNUMBER(), emg, 0);
                suggestionService.makeSuggestionAuto(familleStock, famille, emg);
                String desc = "Ajustement du produit :[  " + famille.getIntCIP() + "  " + famille.getStrNAME() + " ] : Quantité initiale : [ " + initStock + " ] : Quantité ajustée [ " + it.getIntNUMBER() + " ] :Quantité finale [ " + (initStock + it.getIntNUMBER()) + " ]";
                logService.updateItem(tUser, famille.getIntCIP(), desc, TypeLog.AJUSTEMENT_DE_PRODUIT, famille, emg);
                it.setStrSTATUT(commonparameter.statut_enable);
                it.setDtUPDATED(new Date());
                emg.merge(it);

            });
            ajustement.setStrCOMMENTAIRE(params.getDescription());
            ajustement.setDtUPDATED(new Date());
            ajustement.setStrSTATUT(commonparameter.statut_enable);
            emg.merge(ajustement);
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            return json;

        } catch (Exception e) {
            e.printStackTrace(System.err);
            json.put("success", false).put("msg", "L'opération a échoué");
            return json;
        }
    }

    private List<TAjustementDetail> findAjustementDetailsByParenId(String idParent, EntityManager em) {
        return em.createQuery("SELECT o FROM TAjustementDetail o WHERE o.lgAJUSTEMENTID.lgAJUSTEMENTID=?1 ", TAjustementDetail.class).setParameter(1, idParent).getResultList();
    }

    private TAjustementDetail findAjustementDetailsByParenId(String idParent, String produitId) {
        try {
            TypedQuery<TAjustementDetail> q = getEmg().createQuery("SELECT o FROM TAjustementDetail o WHERE o.lgAJUSTEMENTID.lgAJUSTEMENTID=?1 AND o.lgFAMILLEID.lgFAMILLEID=?2 ", TAjustementDetail.class);
            q.setParameter(1, idParent);
            q.setParameter(2, produitId);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void saveMvtArticle(String action, String typeAction, TFamille tf, TUser ooTUser, TFamilleStock familleStock, Integer qty, Integer intiQty, TEmplacement emplacementId, EntityManager emg) {
        Optional<TMouvement> tm = findMouvement(tf, action, typeAction, emplacementId.getLgEMPLACEMENTID(), emg);
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
            OTMouvement.setStrACTION(action);
            OTMouvement.setStrTYPEACTION(typeAction);
            OTMouvement.setDtCREATED(new Date());
            OTMouvement.setDtUPDATED(new Date());
            OTMouvement.setLgEMPLACEMENTID(emplacementId);
            emg.persist(OTMouvement);
        }
        createSnapshotMvtArticle(tf, familleStock, intiQty, emplacementId, emg);
    }

    @Override
    public void saveMvtArticle(String action, String typeAction, TFamille tf, TUser ooTUser, Integer qty, Integer intiQty, Integer finalQty, TEmplacement emplacementId, EntityManager emg) {
        Optional<TMouvement> tm = findMouvement(tf, action, typeAction, emplacementId.getLgEMPLACEMENTID(), emg);
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
            OTMouvement.setStrACTION(action);
            OTMouvement.setStrTYPEACTION(typeAction);
            OTMouvement.setDtCREATED(new Date());
            OTMouvement.setDtUPDATED(new Date());
            OTMouvement.setLgEMPLACEMENTID(emplacementId);
            emg.persist(OTMouvement);
        }
        createSnapshotMvtArticle(tf, qty, ooTUser, intiQty, finalQty, emplacementId, emg);
    }

    @Override
    public JSONObject findOneAjustement(String idAjustement) throws JSONException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public long countAjustement(SalesStatsParams params, EntityManager emg) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<TAjustementDetail> root = cq.from(TAjustementDetail.class);
            Join<TAjustementDetail, TAjustement> st = root.join("lgAJUSTEMENTID", JoinType.INNER);
            cq.select(cb.countDistinct(root.get(TAjustementDetail_.lgAJUSTEMENTID)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TAjustement_.dtUPDATED)), java.sql.Date.valueOf(params.getDtStart()),
                    java.sql.Date.valueOf(params.getDtEnd()));
            predicates.add(btw);
            predicates.add(cb.equal(st.get(TAjustement_.strSTATUT), commonparameter.statut_enable));
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.or(cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%"));
                predicates.add(predicate);
            }
            if (!params.isShowAll()) {
                predicates.add(cb.equal(st.get(TAjustement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID()));
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
            predicates.add(btw);
            predicates.add(cb.equal(st.get(TAjustement_.strSTATUT), commonparameter.statut_enable));

            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.or(cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%"));
                predicates.add(predicate);
            }
            if (!params.isShowAll()) {
                predicates.add(cb.and(cb.equal(st.get(TAjustement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
            }
            cq.where(predicates.toArray(new Predicate[predicates.size()]));
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

    @Override
    public JSONObject removeAjustementDetail(String id) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {

            TAjustementDetail ajustementDetail = emg.find(TAjustementDetail.class, id);
            emg.remove(ajustementDetail);
            return json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            if (emg.getTransaction().isActive()) {
                emg.getTransaction().rollback();
            }
            return json.put("success", false).put("msg", "Opération a échoué");
        }
    }

    public long ajsutementsDetailsCount(SalesStatsParams params, String idAjustement) {
        EntityManager emg = this.getEmg();
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<Number> cq = cb.createQuery(Number.class);
            Root<TAjustementDetail> root = cq.from(TAjustementDetail.class);
            Join<TAjustementDetail, TAjustement> st = root.join("lgAJUSTEMENTID", JoinType.INNER);
            cq.select(cb.count(root));
            predicates.add(cb.and(cb.equal(st.get(TAjustement_.lgAJUSTEMENTID), idAjustement)));
            if (params.getQuery() != null && !"".equals(params.getQuery())) {
                Predicate predicate = cb.and(cb.or(cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%")));
                predicates.add(predicate);
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            return ((Number) q.getSingleResult()).longValue();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }

    }

    @Override
    public JSONObject ajsutementsDetails(SalesStatsParams params, String idAjustement) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {
            long count = ajsutementsDetailsCount(params, idAjustement);
            if (count == 0) {
                json.put("total", 0);
                json.put("data", new JSONArray());
                return json;
            }
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
            List<AjustementDetailDTO> data = list.stream().map(AjustementDetailDTO::new).sorted(comparator).collect(Collectors.toList());
            json.put("total", count);
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
            ajustementDetails.forEach(c -> {
                emg.remove(c);
            });
            emg.remove(ajustement);
            return json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            if (emg.getTransaction().isActive()) {
                emg.getTransaction().rollback();
            }
            return json.put("success", false).put("msg", "Opération a échoué");
        }

    }

    @Override
    public JSONObject validerRetourFournisseur(Params params) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {
            TRetourFournisseur fournisseur = emg.find(TRetourFournisseur.class, params.getRef());
            List<TRetourFournisseurDetail> details = getTRetourFournisseurDetail(params.getRef(), emg);
            DoubleAdder amount = new DoubleAdder();
            final TEmplacement empl = params.getOperateur().getLgEMPLACEMENTID();
            final String emplecementId = empl.getLgEMPLACEMENTID();
            details.forEach(d -> {
                TFamille tf = d.getLgFAMILLEID();
                TFamilleStock stock = findByProduitId(tf.getLgFAMILLEID(), emplecementId, emg);
                int sockInit = stock.getIntNUMBERAVAILABLE();
                int finalQty = sockInit - d.getIntNUMBERRETURN();
                amount.add(d.getIntNUMBERRETURN() * d.getIntPAF());
                d.setStrSTATUT(DateConverter.STATUT_ENABLE);
                d.setDtUPDATED(new Date());
                stock.setIntNUMBERAVAILABLE(finalQty);
                stock.setIntNUMBER(stock.getIntNUMBERAVAILABLE());
                stock.setDtUPDATED(new Date());
                emg.merge(stock);
                emg.merge(d);
                mouvementProduitService.saveMvtProduit(0, d.getIntPAF(), d.getLgRETOURFRSDETAIL(),
                        DateConverter.RETOUR_FOURNISSEUR, tf, params.getOperateur(), empl, d.getIntNUMBERRETURN(), sockInit, finalQty, emg, 0);
                saveMvtArticle(commonparameter.str_ACTION_RETOURFOURNISSEUR, commonparameter.REMOVE, tf,
                        params.getOperateur(), stock, d.getIntNUMBERRETURN(), sockInit, empl, emg);
                suggestionService.makeSuggestionAuto(stock, tf, emg);
                String desc = "Retour fournisseur du  produit " + tf.getIntCIP() + " " + tf.getStrNAME() + "Numéro BL =  " + fournisseur.getLgBONLIVRAISONID().getStrREFLIVRAISON() + " stock initial= " + sockInit + " qté retournée= " + d.getIntNUMBERRETURN() + " qté après retour = " + finalQty + " . Retour effectué par " + params.getOperateur().getStrFIRSTNAME() + " " + params.getOperateur().getStrLASTNAME();
                logService.updateItem(params.getOperateur(), tf.getIntCIP(), desc, TypeLog.RETOUR_FOURNISSEUR, tf, emg);
                notificationService.save(new Notification()
                        .canal(Canal.SMS)
                        .typeNotification(TypeNotification.RETOUR_FOURNISSEUR)
                        .message(desc)
                        .addUser(params.getOperateur())
                );
            });
            fournisseur.setStrSTATUT(DateConverter.STATUT_ENABLE);
            fournisseur.setDtUPDATED(new Date());
            fournisseur.setDlAMOUNT(amount.doubleValue());
            fournisseur.setStrCOMMENTAIRE(params.getDescription());
            fournisseur.setLgUSERID(params.getOperateur());
            fournisseur.setStrREPONSEFRS(params.getRefTwo());
            emg.merge(fournisseur);

            json.put("success", true).put("msg", "Opération effectuée avec success");
        } catch (Exception e) {
            json.put("success", false).put("msg", "Echec de la validation");
            LOG.log(Level.SEVERE, null, e);
        }

        return json;

    }

    public List<TRetourFournisseurDetail> getTRetourFournisseurDetail(String lg_RETOUR_FRS_ID, EntityManager emg) {
        try {
            TypedQuery<TRetourFournisseurDetail> query = emg.
                    createQuery("SELECT t FROM TRetourFournisseurDetail t WHERE t.strSTATUT NOT LIKE ?1 AND t.lgRETOURFRSID.lgRETOURFRSID LIKE ?2 ", TRetourFournisseurDetail.class).
                    setParameter(1, DateConverter.STATUT_ENABLE).
                    setParameter(2, lg_RETOUR_FRS_ID);
            return query.getResultList();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }

    }

    @Override
    public JSONObject deconditionner(Params params) throws JSONException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TFamilleStock updateStock(TFamille tf, TEmplacement emplacementId, int qty, int ug, EntityManager em) {
        TFamilleStock stock = findStock(tf.getLgFAMILLEID(), emplacementId, em);
        stock.setIntNUMBERAVAILABLE(stock.getIntNUMBERAVAILABLE() + qty);
        stock.setIntNUMBER(stock.getIntNUMBERAVAILABLE());
        stock.setIntUG((stock.getIntUG() != null ? (stock.getIntUG() + ug) : ug));
        stock.setDtUPDATED(new Date());
        em.merge(stock);
        return stock;
    }

    @Override
    public int updateStockReturnInitStock(TFamille tf, TEmplacement emplacementId, int qty, int ug, EntityManager em) {
        TFamilleStock stock = findStock(tf.getLgFAMILLEID(), emplacementId, em);
        int stockInit = stock.getIntNUMBERAVAILABLE();
        stock.setIntNUMBERAVAILABLE(stock.getIntNUMBERAVAILABLE() + qty);
        stock.setIntNUMBER(stock.getIntNUMBERAVAILABLE());
        stock.setIntUG((stock.getIntUG() != null ? (stock.getIntUG() + ug) : ug));
        stock.setDtUPDATED(new Date());
        em.merge(stock);
        return stockInit;
    }

    @Override
    public JSONObject loadetourFournisseur(String dtStart, String dtEnd, int start, int limit, String fourId, String query, boolean cunRemove) throws JSONException {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<TRetourFournisseur> cq = cb.createQuery(TRetourFournisseur.class);
            Root<TRetourFournisseur> root = cq.from(TRetourFournisseur.class);
            Fetch<TRetourFournisseur, TRetourFournisseurDetail> d = root.fetch("tRetourFournisseurDetailCollection", JoinType.INNER);
            cq.select(root).distinct(true);
            predicates.add(cb.equal(root.get(TRetourFournisseur_.strSTATUT), DateConverter.STATUT_ENABLE));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TRetourFournisseur_.dtUPDATED)), java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (!StringUtils.isEmpty(fourId)) {
                predicates.add(cb.equal(root.get(TRetourFournisseur_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), fourId));
            }
            if (!StringUtils.isEmpty(query)) {
                List<Predicate> subpr = new ArrayList<>();
                Subquery<TRetourFournisseur> sub = cq.subquery(TRetourFournisseur.class);
                Root<TRetourFournisseurDetail> pr = sub.from(TRetourFournisseurDetail.class);
                subpr.add(cb.or(cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.intCIP), query + "%"), cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.strNAME), query + "%"), cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.intEAN13), query + "%")));
                sub.select(pr.get(TRetourFournisseurDetail_.lgRETOURFRSID)).where(cb.and(subpr.toArray(new Predicate[subpr.size()])));
                predicates.add(cb.in(root).value(sub));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<TRetourFournisseur> q = getEmg().createQuery(cq);
            List<RetourFournisseurDTO> l = q.getResultList().stream().map(x -> new RetourFournisseurDTO(x, x.getTRetourFournisseurDetailCollection().stream().map(RetourDetailsDTO::new).collect(Collectors.toList()), cunRemove)).collect(Collectors.toList());
            return new JSONObject().put("total", l.size()).put("results", new JSONArray(l));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("total", 0).put("results", new JSONArray());
        }
    }

    @Override
    public JSONObject validerRetourDepot(String retourId, TUser user) throws JSONException {
        try {
            TRetourdepot OTRetourdepot = this.getEmg().find(TRetourdepot.class, retourId);
            TRetourdepot ORetourdepotOfficine = createRetourdepot(user, OTRetourdepot.getStrNAME(), user.getLgEMPLACEMENTID(), OTRetourdepot.getStrDESCRIPTION(), OTRetourdepot.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            createTretourDetails(OTRetourdepot, ORetourdepotOfficine, user);
            return new JSONObject().put("success", true);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new JSONObject().put("success", false);
        }
    }

    public TRetourdepot createRetourdepot(TUser user, String libelle, TEmplacement OTEmplacement,
            String description, String strPKEY) throws Exception {
        TRetourdepot OTRetourdepot = new TRetourdepot();
        OTRetourdepot.setStrNAME(StringUtils.isEmpty(libelle) ? DateConverter.getShortId(8) : libelle);
        OTRetourdepot.setLgRETOURDEPOTID(UUID.randomUUID().toString());
        OTRetourdepot.setStrDESCRIPTION(description);
        OTRetourdepot.setLgUSERID(user);
        OTRetourdepot.setLgEMPLACEMENTID(OTEmplacement);
        OTRetourdepot.setPkey(strPKEY);
        OTRetourdepot.setBoolPending(OTEmplacement.getBoolSAMELOCATION());
        OTRetourdepot.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
        OTRetourdepot.setDtCREATED(new Date());
        OTRetourdepot.setDtUPDATED(new Date());
        OTRetourdepot.setBoolFLAG(false);
        OTRetourdepot.setBoolPending(false);
        return OTRetourdepot;
    }

    private List<TRetourdepotdetail> getRetourdepotdetailsByRetourdepot(String lg_retourDepot) {
        return this.getEmg().createQuery("SELECT OBJECT(o) FROM TRetourdepotdetail o WHERE o.lgRETOURDEPOTID.lgRETOURDEPOTID=?1 ")
                .setParameter(1, lg_retourDepot)
                .getResultList();
    }

    private void createTretourDetails(TRetourdepot retourdepot, TRetourdepot officine, TUser user) throws Exception {
        int total = 0;
        List<TRetourdepotdetail> data = getRetourdepotdetailsByRetourdepot(retourdepot.getLgRETOURDEPOTID());
        Collection<TRetourdepotdetail> collection = new ArrayList<>(data.size());
        for (TRetourdepotdetail ODRetourdepot : data) {
            TFamille f = ODRetourdepot.getLgFAMILLEID();
            TFamilleStock of = this.findStock(f.getLgFAMILLEID(), officine.getLgEMPLACEMENTID(), this.getEmg());
            int ofStockInit = of.getIntNUMBERAVAILABLE();
            int ofFinale = ofStockInit + ODRetourdepot.getIntNUMBERRETURN();
            of.setIntNUMBERAVAILABLE(ofFinale);
            of.setIntNUMBER(ofFinale);
            this.getEmg().merge(of);
            TFamilleStock depot = this.findStock(f.getLgFAMILLEID(), retourdepot.getLgEMPLACEMENTID(), this.getEmg());
            int deStockInit = depot.getIntNUMBERAVAILABLE();
            int deFinale = deStockInit - ODRetourdepot.getIntNUMBERRETURN();
            depot.setIntNUMBERAVAILABLE(deFinale);
            depot.setIntNUMBER(deFinale);
            this.getEmg().merge(depot);
            TRetourdepotdetail retourdepotdetail = new TRetourdepotdetail(UUID.randomUUID().toString());
            retourdepotdetail.setDtCREATED(new Date());
            retourdepotdetail.setIntNUMBERRETURN(ODRetourdepot.getIntNUMBERRETURN());
            retourdepotdetail.setIntPRICE(ODRetourdepot.getIntPRICE());
            retourdepotdetail.setIntPRICEDETAIL(ODRetourdepot.getIntPRICEDETAIL());
            retourdepotdetail.setIntSTOCK(ODRetourdepot.getIntSTOCK());
            retourdepotdetail.setLgRETOURDEPOTID(officine);
            retourdepotdetail.setLgFAMILLEID(f);
            retourdepotdetail.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
            retourdepotdetail.setDtUPDATED(new Date());
            total += retourdepotdetail.getIntPRICE();
            collection.add(retourdepotdetail);
            mouvementProduitService.saveMvtProduit(0, f.getIntPAF(), retourdepotdetail.getLgRETOURDEPOTDETAILID(),
                    DateConverter.TMVTP_RETOUR_DEPOT, f, user, officine.getLgEMPLACEMENTID(),
                    ODRetourdepot.getIntNUMBERRETURN(), ofStockInit, ofFinale, this.getEmg(), 0);
            saveMvtArticle(DateConverter.ACTION_ENTREE_RETOUR_DEPOT, commonparameter.ADD, f,
                    user, of, retourdepotdetail.getIntNUMBERRETURN(), ofStockInit, officine.getLgEMPLACEMENTID(), this.getEmg());
            mouvementProduitService.saveMvtProduit(0, f.getIntPAF(), ODRetourdepot.getLgRETOURDEPOTDETAILID(),
                    DateConverter.RETOUR_FOURNISSEUR, f, user, retourdepot.getLgEMPLACEMENTID(),
                    ODRetourdepot.getIntNUMBERRETURN(), deStockInit, deFinale, this.getEmg(), 0);
            saveMvtArticle(DateConverter.ACTION_RETOURFOURNISSEUR, commonparameter.REMOVE, f,
                    user, depot, retourdepotdetail.getIntNUMBERRETURN(), deStockInit, retourdepot.getLgEMPLACEMENTID(), this.getEmg());
        }
        retourdepot.setDblAMOUNT(total);
        officine.setDblAMOUNT(total);
        retourdepot.setBoolPending(false);
        retourdepot.setDtUPDATED(new Date());
        retourdepot.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
        officine.setTRetourdepotdetailCollection(collection);
        getEmg().persist(officine);
        getEmg().merge(retourdepot);
    }

  

}
