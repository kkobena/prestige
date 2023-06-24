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
import dal.MotifAjustement;
import dal.MotifAjustement_;
import dal.TBonLivraisonDetail;
import dal.TMotifRetour;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.DoubleAdder;
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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
    @Resource(name = "concurrent/__defaultManagedExecutorService")
    ManagedExecutorService managedExecutorService;

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

    private TEmplacement emplacementFromId(String lgEMPLACEMENTID, EntityManager emg) {
        return emg.find(TEmplacement.class, lgEMPLACEMENTID);
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

        final Typemvtproduit typemvtproduit = tp.getChecked() ? getTypemvtproduitByID(DateConverter.VENTE) : getTypemvtproduitByID(DateConverter.TMVTP_VENTE_DEPOT_EXTENSION);
        final Typemvtproduit typeMvtProduit = getTypemvtproduitByID(DateConverter.ENTREE_EN_STOCK);
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
            if (tFamille.getBoolDECONDITIONNE() == 1 && !checkIsVentePossible(familleStock, it.getIntQUANTITY())) {
                TFamille oTFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                TFamilleStock stockParent = findStockByProduitId(oTFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID());
                deconditionner(tu, emplacement, tFamille, oTFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);

            }

            updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
            mouvementProduitService.saveMvtProduit(it.getIntPRICEUNITAIR(), it,
                    typemvtproduit, tFamille, tu, emplacement,
                    it.getIntQUANTITY(), initStock, initStock - it.getIntQUANTITY(), emg, it.getValeurTva(), tp.getChecked(), it.getIntUG());

            emg.merge(it);
            updateStockDepot(typeMvtProduit, tu, tFamille, it.getIntQUANTITYSERVED(), depot, emg);
            suggestionService.makeSuggestionAuto(familleStock, tFamille);
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
            if (tFamille.getBoolDECONDITIONNE() == 1 && !checkIsVentePossible(familleStock, it.getIntQUANTITY())) {

                TFamille OTFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                TFamilleStock stockParent = findStockByProduitId(OTFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID());
                deconditionner(tu, emplacement, tFamille, OTFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);
            }

            updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
            emg.merge(it);
            suggestionService.makeSuggestionAuto(familleStock, tFamille);
        });

    }

    private Typemvtproduit getTypemvtproduitByID(String id) {
        return getEmg().find(Typemvtproduit.class, id);
    }

    private void updateQtyUg(TFamilleStock familleStock, TPreenregistrement tp, TPreenregistrementDetail it) {
        try {
            if (tp.getStrTYPEVENTE().equals(DateConverter.VENTE_COMPTANT) && familleStock.getIntUG() > 0) {
                int ugVendue;
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

    private void updateStock(TFamilleStock familleStock, TPreenregistrement tp, TPreenregistrementDetail it) {
        updateQtyUg(familleStock, tp, it);
        familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITYSERVED());
        familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
        familleStock.setDtUPDATED(new Date());

    }

    @Override
    public void updateVenteStock(TPreenregistrement tp, List<TPreenregistrementDetail> list) {
        EntityManager emg = this.getEmg();
        try {

            TUser tu = tp.getLgUSERID();
            final TEmplacement emplacement = tu.getLgEMPLACEMENTID();
            final String emplacementId = emplacement.getLgEMPLACEMENTID();
            final boolean isDepot = !("1".equals(emplacementId));
            final Typemvtproduit typemvtproduit = getTypemvtproduitByID(DateConverter.VENTE);
            final String statut = "is_Closed";
            list.stream().forEach(it -> {
                System.err.println("update vente stock " + it.getIntQUANTITY());
                it.setStrSTATUT(statut);
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

                System.err.println("initStock " + initStock);
                if (tFamille.getBoolDECONDITIONNE() == 1 && !checkIsVentePossible(familleStock, it.getIntQUANTITY())) {
                    TFamille otFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                    TFamilleStock stockParent = findStockByProduitId(otFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID());
                    deconditionner(tu,  stockParent, familleStock, it.getIntQUANTITY());

                }
                updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
                mouvementProduitService.saveMvtProduit(it.getIntPRICEUNITAIR(), it,
                        typemvtproduit, tFamille, tu, emplacement,
                        it.getIntQUANTITY(), initStock, familleStock.getIntNUMBERAVAILABLE() - it.getIntQUANTITY(), emg, it.getValeurTva(), true, it.getIntUG());
                updateStock(familleStock, tp, it);
                emg.merge(familleStock);
                emg.merge(it);
                makeSuggestionAutoAsync(familleStock, tFamille);
            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
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
                if (tFamille.getBoolDECONDITIONNE() == 1 && !checkIsVentePossible(familleStock, it.getIntQUANTITY())) {

                    TFamille otFamilleParent = findProduitById(tFamille.getLgFAMILLEPARENTID(), emg);
                    TFamilleStock stockParent = findStockByProduitId(otFamilleParent.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID());
                    deconditionner(tu, emplacement, tFamille, otFamilleParent, stockParent, familleStock, it.getIntQUANTITY(), emg);

                }
                updatefamillenbvente(tFamille, it.getIntQUANTITY(), isDepot, emg);
                mouvementProduitService.saveMvtProduit(it.getIntPRICEUNITAIR(), it,
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

        familleStock = findStockByProduitId(OTFamille.getLgFAMILLEID(), OTEmplacement.getLgEMPLACEMENTID());

        if (familleStock != null) {
            initStock = familleStock.getIntNUMBERAVAILABLE();
            familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() + qty);
            familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
            familleStock.setDtUPDATED(new Date());
            emg.merge(familleStock);

        } else {

            if (isDetail) {
                familleStock = findByParent(OTFamille.getLgFAMILLEPARENTID(), OTEmplacement.getLgEMPLACEMENTID(), emg);
                if (familleStock == null) {
                    familleStock = createStock(OTFamille, qty, OTEmplacement);
                } else {
                    initStock = familleStock.getIntNUMBERAVAILABLE();
                    familleStock.setIntNUMBERAVAILABLE(familleStock.getIntNUMBERAVAILABLE() + qty);
                    familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
                    familleStock.setDtUPDATED(new Date());
                    emg.merge(familleStock);
                }
                TFamilleStock familleStock2 = findStockByProduitId(OTFamille.getLgFAMILLEPARENTID(), OTEmplacement.getLgEMPLACEMENTID());
                if (familleStock2 == null) {
                    TFamille p = findProduitById(OTFamille.getLgFAMILLEPARENTID(), emg);
                    if (p != null) {
                        createStock(p, 0, OTEmplacement);
                    }

                }
            } else {
                familleStock = createStock(OTFamille, qty, OTEmplacement);

            }

        }
        mouvementProduitService.saveMvtProduit2(OTFamille.getIntPRICE(), familleStock.getLgFAMILLESTOCKID(),
                typemvtproduit, OTFamille, ooTUser, OTEmplacement,
                qty, initStock, initStock - qty, emg, 0, false, 0);

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
        }
        return familleStock;
    }

    public TFamilleStock findStockByProduitId(String produitId, String emplecementId) {
        TFamilleStock familleStock = null;
        try {
            TypedQuery<TFamilleStock> query = getEmg().createQuery("SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2 AND t.strSTATUT='enable' ORDER BY t.dtCREATED DESC", TFamilleStock.class);
            query.
                    setParameter(1, produitId);
            query.
                    setParameter(2, emplecementId);
            query.setMaxResults(1);
            familleStock = query.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
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
//             LOG.log(Level.SEVERE, null, e);
        }
        return famille;
    }

    private TFamilleStock createStock(TFamille oTFamille, Integer qte, TEmplacement oTEmplacement) {
        TFamilleStock oTFamilleStock = new TFamilleStock();
        oTFamilleStock.setLgFAMILLESTOCKID(UUID.randomUUID().toString());
        oTFamilleStock.setIntNUMBER(qte);
        oTFamilleStock.setIntNUMBERAVAILABLE(qte);
        oTFamilleStock.setLgEMPLACEMENTID(oTEmplacement);
        oTFamilleStock.setLgFAMILLEID(oTFamille);
        oTFamilleStock.setStrSTATUT(commonparameter.statut_enable);
        oTFamilleStock.setDtCREATED(new Date());
        oTFamilleStock.setDtUPDATED(oTFamilleStock.getDtCREATED());
        oTFamilleStock.setIntUG(0);
        oTFamilleStock.setLgEMPLACEMENTID(oTEmplacement);
        getEmg().persist(oTFamilleStock);
        return oTFamilleStock;

    }

    public boolean checkIsVentePossible(TFamilleStock oTFamilleStock, int qte
    ) {
        return oTFamilleStock.getIntNUMBERAVAILABLE() >= qte;
    }

    private void deconditionner(TUser tu, TEmplacement te, TFamille tFamilleChild, TFamille tFamilleParent, TFamilleStock ofamilleStockParent, TFamilleStock familleStockChild, Integer qteVendue, EntityManager emg) {
        Integer numberToDecondition = 0;
        Integer qtyDetail = tFamilleParent.getIntNUMBERDETAIL();
        Integer stockInitDetail = familleStockChild.getIntNUMBERAVAILABLE();
        //    Integer stockDetailInit0 = stockInitDetail;
        Integer stockInit = ofamilleStockParent.getIntNUMBERAVAILABLE();
        Integer stockVirtuel = stockInitDetail + (stockInit * qtyDetail);
        int compare = stockVirtuel.compareTo(qteVendue);
        if (compare >= 0) {
            while (stockInitDetail < qteVendue) {
                numberToDecondition++;
                stockInitDetail += qtyDetail;
            }
            ofamilleStockParent.setIntNUMBERAVAILABLE(ofamilleStockParent.getIntNUMBERAVAILABLE() - numberToDecondition);
            ofamilleStockParent.setIntNUMBER(ofamilleStockParent.getIntNUMBERAVAILABLE());
            ofamilleStockParent.setDtUPDATED(new Date());

            familleStockChild.setIntNUMBERAVAILABLE(familleStockChild.getIntNUMBERAVAILABLE() + (numberToDecondition * qtyDetail) - qteVendue);
            familleStockChild.setIntNUMBER(familleStockChild.getIntNUMBERAVAILABLE());
            familleStockChild.setDtUPDATED(new Date());
            emg.merge(ofamilleStockParent);
            emg.merge(familleStockChild);
            TDeconditionnement parent = createDecondtionne(tFamilleParent, numberToDecondition, tu);
            TDeconditionnement child = createDecondtionne(tFamilleChild, (numberToDecondition * qtyDetail), tu);

            mouvementProduitService.saveMvtProduit(child.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_POSITIF, tFamilleChild, tu, ofamilleStockParent.getLgEMPLACEMENTID(), (numberToDecondition * qtyDetail), stockInitDetail, stockInitDetail + (numberToDecondition * qtyDetail) - qteVendue, emg, 0);
            mouvementProduitService.saveMvtProduit(parent.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_NEGATIF, tFamilleParent, tu, ofamilleStockParent.getLgEMPLACEMENTID(), numberToDecondition, stockInit, stockInit - numberToDecondition, emg, 0);
            String desc = "Déconditionnement du produit [ " + tFamilleParent.getIntCIP() + " ] de " + tFamilleParent.getIntPRICE() + " stock initial " + stockInit + " quantité déconditionnée " + numberToDecondition + " stock finale " + (stockInit - numberToDecondition) + " stock détail initial  " + stockInitDetail + " stock détail final = " + (stockInitDetail + (numberToDecondition * qtyDetail) - qteVendue) + " . Opérateur : " + tu.getStrFIRSTNAME() + " " + tu.getStrLASTNAME();
            logService.updateItem(tu, tFamilleParent.getIntCIP(), desc, TypeLog.DECONDITIONNEMENT, tFamilleParent, emg);

            notificationService.save(new Notification()
                    .canal(Canal.EMAIL)
                    .typeNotification(TypeNotification.DECONDITIONNEMENT)
                    .message(desc)
                    .addUser(tu));

        }
    }

    private TDeconditionnement createDecondtionne(TFamille oTFamille, int qty, TUser tUser) {
        TDeconditionnement oTDeconditionnement = new TDeconditionnement();
        oTDeconditionnement.setLgDECONDITIONNEMENTID(UUID.randomUUID().toString());
        oTDeconditionnement.setLgFAMILLEID(oTFamille);
        oTDeconditionnement.setLgUSERID(tUser);
        oTDeconditionnement.setIntNUMBER(qty);
        oTDeconditionnement.setDtCREATED(new Date());
        oTDeconditionnement.setStrSTATUT(commonparameter.statut_enable);
        getEmg().persist(oTDeconditionnement);
        return oTDeconditionnement;
    }

    private MotifAjustement getOneTypeAjustement(Integer value) {
        return getEmg().find(MotifAjustement.class, value);
    }

    @Override
    public JSONObject creerAjustement(Params params) throws JSONException {
        EntityManager emg = this.getEmg();
        JSONObject json = new JSONObject();
        try {
            String desc = "Ajustement du " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"));
            TAjustement ajustement = new TAjustement();
            ajustement.setLgAJUSTEMENTID(UUID.randomUUID().toString());
            ajustement.setLgUSERID(params.getOperateur());
            ajustement.setStrNAME(desc);
            ajustement.setStrCOMMENTAIRE(params.getDescription());
            ajustement.setDtCREATED(new Date());
            ajustement.setDtUPDATED(ajustement.getDtCREATED());
            ajustement.setStrSTATUT(commonparameter.statut_is_Process);
            emg.persist(ajustement);
            ajusterProduitAjustement(params, ajustement, emg);
            json.put("success", true).put("msg", "L'opération effectuée avec success");
            json.put("data", new JSONObject().put("lgAJUSTEMENTID", ajustement.getLgAJUSTEMENTID()));
        } catch (Exception e) {

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

            json.put("success", false).put("msg", "L'opération a échoué");

        }
        return json;
    }

    private void ajusterProduitAjustement(Params params, TAjustement ajustement, EntityManager emg) {
        TAjustementDetail ajustementDetail = updateAjustementDetail(params);
        if (ajustementDetail == null) {
            TEmplacement emplacement = ajustement.getLgUSERID().getLgEMPLACEMENTID();
            TFamilleStock familleStock = findStockByProduitId(params.getRefTwo(), emplacement.getLgEMPLACEMENTID());
            Integer currentStock = familleStock.getIntNUMBERAVAILABLE();
            ajustementDetail = new TAjustementDetail();
            ajustementDetail.setLgAJUSTEMENTDETAILID(UUID.randomUUID().toString());
            ajustementDetail.setLgAJUSTEMENTID(ajustement);
            ajustementDetail.setLgFAMILLEID(familleStock.getLgFAMILLEID());
            ajustementDetail.setIntNUMBER(params.getValue());
            ajustementDetail.setIntNUMBERCURRENTSTOCK(currentStock);
            ajustementDetail.setIntNUMBERAFTERSTOCK(params.getValue() + currentStock);
            ajustementDetail.setDtCREATED(new Date());
            ajustementDetail.setDtUPDATED(ajustementDetail.getDtCREATED());
            ajustementDetail.setTypeAjustement(getOneTypeAjustement(params.getValueFour()));
            ajustementDetail.setStrSTATUT(commonparameter.statut_is_Process);
            emg.persist(ajustementDetail);
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
            LOG.log(Level.SEVERE, null, e);
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
            LOG.log(Level.SEVERE, null, e);
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
                TFamilleStock familleStock = findStockByProduitId(famille.getLgFAMILLEID(), emplacement.getLgEMPLACEMENTID());
                Integer initStock = familleStock.getIntNUMBERAVAILABLE();
                familleStock.setIntNUMBERAVAILABLE(it.getIntNUMBERAFTERSTOCK());
                familleStock.setIntNUMBER(familleStock.getIntNUMBERAVAILABLE());
                familleStock.setDtUPDATED(new Date());
                emg.merge(familleStock);
                int compare = initStock.compareTo(it.getIntNUMBERAFTERSTOCK());

                String action2 = (compare < 0) ? DateConverter.AJUSTEMENT_POSITIF : DateConverter.AJUSTEMENT_NEGATIF;

                mouvementProduitService.saveMvtProduit(it.getLgAJUSTEMENTDETAILID(), action2, famille, tUser, emplacement, it.getIntNUMBER(), initStock, initStock + it.getIntNUMBER(), emg, 0);
                suggestionService.makeSuggestionAuto(familleStock, famille);
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
            LOG.log(Level.SEVERE, null, e);
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
            if (StringUtils.isNotEmpty(params.getTypeFiltre())) {
                predicates.add(cb.equal(root.get(TAjustementDetail_.typeAjustement).get(MotifAjustement_.id), Integer.valueOf(params.getTypeFiltre())));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = emg.createQuery(cq);
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return 0;
        }
    }

    @Override
    public List<AjustementDTO> getAllAjustements(SalesStatsParams params) {
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder cb = getEmg().getCriteriaBuilder();
        CriteriaQuery<TAjustement> cq = cb.createQuery(TAjustement.class);
        Root<TAjustementDetail> root = cq.from(TAjustementDetail.class);
        Join<TAjustementDetail, TAjustement> st = root.join("lgAJUSTEMENTID", JoinType.INNER);
        cq.select(root.get(TAjustementDetail_.lgAJUSTEMENTID)).distinct(true).orderBy(cb.asc(st.get(TAjustement_.dtUPDATED)));
        Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TAjustement_.dtUPDATED)), java.sql.Date.valueOf(params.getDtStart()),
                java.sql.Date.valueOf(params.getDtEnd()));
        predicates.add(btw);
        predicates.add(cb.equal(st.get(TAjustement_.strSTATUT), commonparameter.statut_enable));
        if (StringUtils.isNotEmpty(params.getTypeFiltre())) {
            predicates.add(cb.equal(root.get(TAjustementDetail_.typeAjustement).get(MotifAjustement_.id), Integer.valueOf(params.getTypeFiltre())));
        }
        if (params.getQuery() != null && !"".equals(params.getQuery())) {
            Predicate predicate = cb.or(cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%"));
            predicates.add(predicate);
        }
        if (!params.isShowAll()) {
            predicates.add(cb.and(cb.equal(st.get(TAjustement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        Query q = getEmg().createQuery(cq);
        if (!params.isAll()) {
            q.setFirstResult(params.getStart());
            q.setMaxResults(params.getLimit());
        }
        List<TAjustement> list = q.getResultList();
        return list.stream().map(v -> new AjustementDTO(v, findAjustementDetailsByParenId(v.getLgAJUSTEMENTID(), getEmg()), params.isCanCancel())).collect(Collectors.toList());

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

            List<AjustementDTO> data = getAllAjustements(params);
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
            LOG.log(Level.SEVERE, null, e);
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
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
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
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
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
            LOG.log(Level.SEVERE, null, e);
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
                TFamilleStock stock = findStockByProduitId(tf.getLgFAMILLEID(), emplecementId);
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

                suggestionService.makeSuggestionAuto(stock, tf);
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
            LOG.log(Level.SEVERE, null, e);
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
    public JSONObject loadetourFournisseur(String dtStart, String dtEnd, int start, int limit, String fourId, String query, boolean cunRemove, String filtre) throws JSONException {
        List<RetourFournisseurDTO> data = loadretoursFournisseur(dtStart, dtEnd, start, limit, fourId, query, cunRemove, filtre);
        return new JSONObject().put("total", data.size()).put("results", new JSONArray(data));
    }

    @Override
    public List<RetourFournisseurDTO> loadretoursFournisseur(String dtStart, String dtEnd, int start, int limit, String fourId, String query, boolean cunRemove, String filtre) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<TRetourFournisseur> cq = cb.createQuery(TRetourFournisseur.class);
            Root<TRetourFournisseur> root = cq.from(TRetourFournisseur.class);
            cq.select(root).distinct(true);
            predicates.add(cb.equal(root.get(TRetourFournisseur_.strSTATUT), DateConverter.STATUT_ENABLE));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TRetourFournisseur_.dtUPDATED)), java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (!StringUtils.isEmpty(fourId)) {
                predicates.add(cb.equal(root.get(TRetourFournisseur_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), fourId));
            }
            if (StringUtils.isNotEmpty(query) || (StringUtils.isNotEmpty(filtre) && !filtre.equals(DateConverter.ALL))) {
                List<Predicate> subpr = new ArrayList<>();
                Subquery<TRetourFournisseur> sub = cq.subquery(TRetourFournisseur.class);
                Root<TRetourFournisseurDetail> pr = sub.from(TRetourFournisseurDetail.class);
                if (StringUtils.isNotEmpty(query)) {
                    subpr.add(cb.or(cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.intCIP), query + "%"), cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.strNAME), query + "%"), cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.intEAN13), query + "%")));
                }
                if (StringUtils.isNotEmpty(filtre)) {
                    switch (filtre) {
                        case DateConverter.NOT:
                            subpr.add(cb.equal(pr.get(TRetourFournisseurDetail_.intNUMBERANSWER), 0));
                            break;
                        case DateConverter.WITH:
                            subpr.add(cb.greaterThan(pr.get(TRetourFournisseurDetail_.intNUMBERANSWER), 0));
                            break;
                        default:
                            break;
                    }
                }
                sub.select(pr.get(TRetourFournisseurDetail_.lgRETOURFRSID)).where(cb.and(subpr.toArray(new Predicate[0])));
                predicates.add(cb.in(root).value(sub));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<TRetourFournisseur> q = getEmg().createQuery(cq);
            return q.getResultList().stream().map(x -> new RetourFournisseurDTO(x, x.getTRetourFournisseurDetailCollection().stream().map(RetourDetailsDTO::new).collect(Collectors.toList()), cunRemove)).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RetourDetailsDTO> loadretoursFournisseur(String dtStart, String dtEnd, String fourId, String query, String filtre) {
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = getEmg().getCriteriaBuilder();
            CriteriaQuery<TRetourFournisseur> cq = cb.createQuery(TRetourFournisseur.class);
            Root<TRetourFournisseur> root = cq.from(TRetourFournisseur.class);

            cq.select(root).distinct(true);
            predicates.add(cb.equal(root.get(TRetourFournisseur_.strSTATUT), DateConverter.STATUT_ENABLE));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TRetourFournisseur_.dtUPDATED)), java.sql.Date.valueOf(dtStart),
                    java.sql.Date.valueOf(dtEnd));
            predicates.add(btw);
            if (!StringUtils.isEmpty(fourId)) {
                predicates.add(cb.equal(root.get(TRetourFournisseur_.lgGROSSISTEID).get(TGrossiste_.lgGROSSISTEID), fourId));
            }
            if (!StringUtils.isEmpty(query) || (StringUtils.isNotEmpty(filtre) && !filtre.equals(DateConverter.ALL))) {
                List<Predicate> subpr = new ArrayList<>();
                Subquery<TRetourFournisseur> sub = cq.subquery(TRetourFournisseur.class);
                Root<TRetourFournisseurDetail> pr = sub.from(TRetourFournisseurDetail.class);
                if (StringUtils.isNotEmpty(query)) {
                    subpr.add(cb.or(cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.intCIP), query + "%"), cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.strNAME), query + "%"), cb.like(pr.get(TRetourFournisseurDetail_.lgFAMILLEID).get(TFamille_.intEAN13), query + "%")));
                }
                if (StringUtils.isNotEmpty(filtre)) {
                    switch (filtre) {
                        case DateConverter.NOT:
                            subpr.add(cb.equal(pr.get(TRetourFournisseurDetail_.intNUMBERANSWER), 0));
                            break;
                        case DateConverter.WITH:
                            subpr.add(cb.greaterThan(pr.get(TRetourFournisseurDetail_.intNUMBERANSWER), 0));
                            break;
                        default:
                            break;
                    }
                }

                sub.select(pr.get(TRetourFournisseurDetail_.lgRETOURFRSID)).where(cb.and(subpr.toArray(new Predicate[0])));
                predicates.add(cb.in(root).value(sub));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            TypedQuery<TRetourFournisseur> q = getEmg().createQuery(cq);
            if (StringUtils.isNotEmpty(filtre)) {
                switch (filtre) {
                    case DateConverter.NOT:
                        return q.getResultList().stream().flatMap(e -> e.getTRetourFournisseurDetailCollection()
                                .stream())
                                .filter((t) -> {
                                    return t.getIntNUMBERANSWER() == 0;
                                })
                                .map(RetourDetailsDTO::new).collect(Collectors.toList());

                    case DateConverter.WITH:
                        return q.getResultList().stream()
                                .flatMap(e -> e.getTRetourFournisseurDetailCollection().stream())
                                .filter((t) -> {
                                    return t.getIntNUMBERANSWER() > 0;
                                })
                                .map(RetourDetailsDTO::new).collect(Collectors.toList());

                    default:
                        return q.getResultList().stream().flatMap(e -> e.getTRetourFournisseurDetailCollection().stream()).map(RetourDetailsDTO::new).collect(Collectors.toList());

                }
            }
            return q.getResultList().stream().flatMap(e -> e.getTRetourFournisseurDetailCollection().stream()).map(RetourDetailsDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
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
            LOG.log(Level.SEVERE, null, e);
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

            mouvementProduitService.saveMvtProduit(0, f.getIntPAF(), ODRetourdepot.getLgRETOURDEPOTDETAILID(),
                    DateConverter.RETOUR_FOURNISSEUR, f, user, retourdepot.getLgEMPLACEMENTID(),
                    ODRetourdepot.getIntNUMBERRETURN(), deStockInit, deFinale, this.getEmg(), 0);

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

    @Override
    public List<AjustementDetailDTO> getAllAjustementDetailDTOs(SalesStatsParams params) {
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder cb = getEmg().getCriteriaBuilder();
        CriteriaQuery<TAjustementDetail> cq = cb.createQuery(TAjustementDetail.class);
        Root<TAjustementDetail> root = cq.from(TAjustementDetail.class);
        Join<TAjustementDetail, TAjustement> st = root.join("lgAJUSTEMENTID", JoinType.INNER);
        cq.select(root).orderBy(cb.asc(st.get(TAjustement_.dtUPDATED)));
        Predicate btw = cb.between(cb.function("DATE", Date.class, st.get(TAjustement_.dtUPDATED)), java.sql.Date.valueOf(params.getDtStart()),
                java.sql.Date.valueOf(params.getDtEnd()));
        predicates.add(btw);
        predicates.add(cb.equal(st.get(TAjustement_.strSTATUT), commonparameter.statut_enable));
        if (StringUtils.isNotEmpty(params.getTypeFiltre())) {
            predicates.add(cb.equal(root.get(TAjustementDetail_.typeAjustement).get(MotifAjustement_.id), Integer.valueOf(params.getTypeFiltre())));
        }
        if (params.getQuery() != null && !"".equals(params.getQuery())) {
            Predicate predicate = cb.or(cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intCIP), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.strNAME), params.getQuery() + "%"), cb.like(root.get(TAjustementDetail_.lgFAMILLEID).get(TFamille_.intEAN13), params.getQuery() + "%"));
            predicates.add(predicate);
        }
        if (!params.isShowAll()) {
            predicates.add(cb.and(cb.equal(st.get(TAjustement_.lgUSERID).get(TUser_.lgUSERID), params.getUserId().getLgUSERID())));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<TAjustementDetail> q = getEmg().createQuery(cq);

        return q.getResultList().stream().map(AjustementDetailDTO::new).collect(Collectors.toList());

    }

    @Override
    public void validerFullBlRetourFournisseur(TRetourFournisseur retourFournisseur) {

        EntityManager emg = this.getEmg();
        List<TRetourFournisseurDetail> details = new ArrayList<>(retourFournisseur.getTRetourFournisseurDetailCollection());
        DoubleAdder amount = new DoubleAdder();
        TUser user = retourFournisseur.getLgUSERID();
        final TEmplacement empl = user.getLgEMPLACEMENTID();
        final String emplecementId = empl.getLgEMPLACEMENTID();
        details.forEach(d -> {
            TFamille tf = d.getLgFAMILLEID();
            TFamilleStock stock = findStockByProduitId(tf.getLgFAMILLEID(), emplecementId);
            int sockInit = stock.getIntNUMBERAVAILABLE();
            int finalQty = sockInit - d.getIntNUMBERRETURN();
            amount.add(d.getIntNUMBERRETURN() * d.getIntPAF());
            d.setStrSTATUT(DateConverter.STATUT_ENABLE);
            stock.setIntNUMBERAVAILABLE(finalQty);
            stock.setIntNUMBER(stock.getIntNUMBERAVAILABLE());
            stock.setDtUPDATED(new Date());
            emg.merge(stock);
            emg.persist(d);
            mouvementProduitService.saveMvtProduit(0, d.getIntPAF(), d.getLgRETOURFRSDETAIL(),
                    DateConverter.RETOUR_FOURNISSEUR, tf, user, empl, d.getIntNUMBERRETURN(), sockInit, finalQty, emg, 0);

            suggestionService.makeSuggestionAuto(stock, tf);
            String desc = "Retour fournisseur du  produit " + tf.getIntCIP() + " " + tf.getStrNAME() + "Numéro BL =  " + retourFournisseur.getLgBONLIVRAISONID().getStrREFLIVRAISON() + " stock initial= " + sockInit + " qté retournée= " + d.getIntNUMBERRETURN() + " qté après retour = " + finalQty + " . Retour effectué par " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
            logService.updateItem(user, tf.getIntCIP(), desc, TypeLog.RETOUR_FOURNISSEUR, tf, emg);
            notificationService.save(new Notification()
                    .canal(Canal.SMS)
                    .typeNotification(TypeNotification.RETOUR_FOURNISSEUR)
                    .message(desc)
                    .addUser(user)
            );
        });

        retourFournisseur.setDlAMOUNT(amount.doubleValue());

        emg.persist(retourFournisseur);

    }

    @Override
    public void validerFullBlRetourFournisseur(TRetourFournisseur retourFournisseur, TMotifRetour motifRetour, List<TBonLivraisonDetail> bonLivraisonDetails) {

        EntityManager emg = this.getEmg();
        Set<TRetourFournisseurDetail> retourFournisseurDetails = new HashSet<>();
        DoubleAdder amount = new DoubleAdder();
        TUser user = retourFournisseur.getLgUSERID();
        final TEmplacement empl = user.getLgEMPLACEMENTID();
        final String emplecementId = empl.getLgEMPLACEMENTID();
        for (TBonLivraisonDetail bonLivraisonDetail : bonLivraisonDetails) {
            bonLivraisonDetail.setIntQTERETURN(bonLivraisonDetail.getIntQTERECUE());
            TFamille tf = bonLivraisonDetail.getLgFAMILLEID();
            TFamilleStock stock = findStockByProduitId(tf.getLgFAMILLEID(), emplecementId);
            int sockInit = stock.getIntNUMBERAVAILABLE();
            int finalQty = sockInit - bonLivraisonDetail.getIntQTERECUE();
            amount.add(bonLivraisonDetail.getIntQTERECUE() * bonLivraisonDetail.getIntPAF());
            stock.setIntNUMBERAVAILABLE(finalQty);
            stock.setIntNUMBER(stock.getIntNUMBERAVAILABLE());
            stock.setDtUPDATED(new Date());
            TRetourFournisseurDetail retourFournisseurDetail = createRetourDetail(bonLivraisonDetail, sockInit, motifRetour, retourFournisseur);
            retourFournisseurDetails.add(retourFournisseurDetail);
            emg.merge(stock);
//                emg.persist(retourFournisseurDetail);
            mouvementProduitService.saveMvtProduit(0, retourFournisseurDetail.getIntPAF(), retourFournisseurDetail.getLgRETOURFRSDETAIL(),
                    DateConverter.RETOUR_FOURNISSEUR, tf, user, empl, retourFournisseurDetail.getIntNUMBERRETURN(), sockInit, finalQty, emg, 0);

            suggestionService.makeSuggestionAuto(stock, tf);
            String desc = "Retour fournisseur du  produit " + tf.getIntCIP() + " " + tf.getStrNAME() + "Numéro BL =  " + retourFournisseur.getLgBONLIVRAISONID().getStrREFLIVRAISON() + " stock initial= " + sockInit + " qté retournée= " + retourFournisseurDetail.getIntNUMBERRETURN() + " qté après retour = " + finalQty + " . Retour effectué par " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
            logService.updateItem(user, tf.getIntCIP(), desc, TypeLog.RETOUR_FOURNISSEUR, tf, emg);
            notificationService.save(new Notification()
                    .canal(Canal.SMS)
                    .typeNotification(TypeNotification.RETOUR_FOURNISSEUR)
                    .message(desc)
                    .addUser(user)
            );
            this.getEmg().merge(bonLivraisonDetail);
        }

        retourFournisseur.setDlAMOUNT(amount.doubleValue());

        emg.persist(retourFournisseur);
        retourFournisseurDetails.forEach(this.getEmg()::persist);
    }

    private TRetourFournisseurDetail createRetourDetail(TBonLivraisonDetail bonLivraisonDetail, int sockInit, TMotifRetour motifRetour, TRetourFournisseur retourFournisseur) {
        TFamille famille = bonLivraisonDetail.getLgFAMILLEID();
        TRetourFournisseurDetail oTRetourFournisseurDetail = new TRetourFournisseurDetail(UUID.randomUUID().toString());
        oTRetourFournisseurDetail.setLgRETOURFRSID(retourFournisseur);
        oTRetourFournisseurDetail.setIntNUMBERRETURN(bonLivraisonDetail.getIntQTERECUE());
        oTRetourFournisseurDetail.setIntNUMBERANSWER(oTRetourFournisseurDetail.getIntNUMBERRETURN());
        oTRetourFournisseurDetail.setIntPAF(bonLivraisonDetail.getIntPAF());
        oTRetourFournisseurDetail.setBonLivraisonDetail(bonLivraisonDetail);
        oTRetourFournisseurDetail.setDtCREATED(retourFournisseur.getDtCREATED());
        oTRetourFournisseurDetail.setDtUPDATED(oTRetourFournisseurDetail.getDtCREATED());
        oTRetourFournisseurDetail.setStrSTATUT(DateConverter.STATUT_PROCESS);
        oTRetourFournisseurDetail.setLgFAMILLEID(famille);
        oTRetourFournisseurDetail.setIntSTOCK(sockInit);
        oTRetourFournisseurDetail.setLgMOTIFRETOUR(motifRetour);
        return oTRetourFournisseurDetail;

    }

    private void deconditionner(TUser tu, TFamilleStock ofamilleStockParent, TFamilleStock familleStockChild, Integer qteVendue) {
        Integer numberToDecondition = 0;
        TFamille tFamilleParent = ofamilleStockParent.getLgFAMILLEID();
        TFamille tFamilleChild = familleStockChild.getLgFAMILLEID();
        Integer qtyDetail = tFamilleParent.getIntNUMBERDETAIL();
        Integer stockInitDetail = familleStockChild.getIntNUMBERAVAILABLE();
        //    Integer stockDetailInit0 = stockInitDetail;
        Integer stockInit = ofamilleStockParent.getIntNUMBERAVAILABLE();
        Integer stockVirtuel = stockInitDetail + (stockInit * qtyDetail);
        int compare = stockVirtuel.compareTo(qteVendue);
        if (compare >= 0) {
            while (stockInitDetail < qteVendue) {
                numberToDecondition++;
                stockInitDetail += qtyDetail;
            }
            ofamilleStockParent.setIntNUMBERAVAILABLE(ofamilleStockParent.getIntNUMBERAVAILABLE() - numberToDecondition);
            ofamilleStockParent.setIntNUMBER(ofamilleStockParent.getIntNUMBERAVAILABLE());
            ofamilleStockParent.setDtUPDATED(new Date());

            familleStockChild.setIntNUMBERAVAILABLE(familleStockChild.getIntNUMBERAVAILABLE() + (numberToDecondition * qtyDetail));
            familleStockChild.setIntNUMBER(familleStockChild.getIntNUMBERAVAILABLE());
            familleStockChild.setDtUPDATED(new Date());
            getEmg().merge(ofamilleStockParent);
            TDeconditionnement parent = createDecondtionne(tFamilleParent, numberToDecondition, tu);
            TDeconditionnement child = createDecondtionne(tFamilleChild, (numberToDecondition * qtyDetail), tu);

            mouvementProduitService.saveMvtProduit(child.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_POSITIF, tFamilleChild, tu, ofamilleStockParent.getLgEMPLACEMENTID(), (numberToDecondition * qtyDetail), stockInitDetail, stockInitDetail + (numberToDecondition * qtyDetail), this.getEmg(), 0);
            mouvementProduitService.saveMvtProduit(parent.getLgDECONDITIONNEMENTID(), DateConverter.DECONDTIONNEMENT_NEGATIF, tFamilleParent, tu, ofamilleStockParent.getLgEMPLACEMENTID(), numberToDecondition, stockInit, stockInit - numberToDecondition, this.getEmg(), 0);
            String desc = "Déconditionnement du produit [ " + tFamilleParent.getIntCIP() + " ] de " + tFamilleParent.getIntPRICE() + " stock initial " + stockInit + " quantité déconditionnée " + numberToDecondition + " stock finale " + (stockInit - numberToDecondition) + " stock détail initial  " + stockInitDetail + " stock détail final = " + (stockInitDetail + (numberToDecondition * qtyDetail)) + " . Opérateur : " + tu.getStrFIRSTNAME() + " " + tu.getStrLASTNAME();
            logService.updateItem(tu, tFamilleParent.getIntCIP(), desc, TypeLog.DECONDITIONNEMENT, tFamilleParent, this.getEmg());

            notificationService.save(new Notification()
                    .canal(Canal.EMAIL)
                    .typeNotification(TypeNotification.DECONDITIONNEMENT)
                    .message(desc)
                    .addUser(tu));

        }
    }

    private void makeSuggestionAutoAsync(TFamilleStock familleStock, TFamille tFamille) {
        managedExecutorService.submit(() -> this.suggestionService.makeSuggestionAuto(familleStock, tFamille));

    }
}
