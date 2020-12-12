/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.Params;
import dal.HMvtProduit;
import dal.Notification;
import dal.Rupture;
import dal.TBonLivraison;
import dal.TBonLivraisonDetail;
import dal.TClient;
import dal.TEmplacement;
import dal.TEtiquette;
import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TFamilleStock;
import dal.TGrossiste;
import dal.TInventaire;
import dal.TInventaireFamille;
import dal.TLot;
import dal.TMouvement;
import dal.TMouvementSnapshot;
import dal.TOfficine;
import dal.TOrder;
import dal.TOrderDetail;
import dal.TParameters;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TRuptureHistory;
import dal.TTypeetiquette;
import dal.TUser;
import dal.TWarehouse;
import dal.Typemvtproduit;
import dal.enumeration.Canal;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.servlet.http.Part;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.CommandeService;
import rest.service.LogService;
import rest.service.MouvementProduitService;
import rest.service.MvtProduitService;
import rest.service.NotificationService;
import rest.service.OrderService;
import rest.service.TransactionService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author DICI
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CommandeServiceImpl implements CommandeService {
    
    private static final Logger LOG = Logger.getLogger(CommandeServiceImpl.class.getName());
    
    @EJB
    MvtProduitService mvtProduitService;
    @EJB
    MouvementProduitService mouvementProduitService;
    @EJB
    TransactionService transactionService;
    @EJB
    LogService logService;
    @EJB
    OrderService orderService;
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private NotificationService notificationService;
    
    public EntityManager getEm() {
        return em;
    }
    @Inject
    private UserTransaction userTransaction;
    
    public List<TBonLivraisonDetail> bonLivraisonDetail(String lg_BON_LIVRAISON_ID, EntityManager emg) {
        try {
            String query = "SELECT t FROM TBonLivraisonDetail t WHERE  t.lgBONLIVRAISONID.lgBONLIVRAISONID =?1";
            TypedQuery<TBonLivraisonDetail> q = emg.
                    createQuery(query, TBonLivraisonDetail.class).
                    setParameter(1, lg_BON_LIVRAISON_ID);
            return q.getResultList();
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
        
    }
    
    public List<Object[]> listLot(String str_REF_LIVRAISON, EntityManager em, String idArticle) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<TLot> root = cq.from(TLot.class);
            cq.multiselect(cb.sum(root.get("intNUMBER")), cb.sum(root.get("intNUMBERGRATUIT")), root.get("lgFAMILLEID").get("lgFAMILLEID"))
                    .groupBy(root.get("lgFAMILLEID").get("lgFAMILLEID"));
            cq.where(cb.and(cb.equal(root.get("strREFLIVRAISON"), str_REF_LIVRAISON), cb.equal(root.get("lgFAMILLEID").get("lgFAMILLEID"), idArticle)));
            Query q = em.createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
        
    }
    
    private boolean isEntreeStockIsAuthorize(List<TBonLivraisonDetail> lstTBonLivraisonDetail) {
        java.util.function.Predicate<TBonLivraisonDetail> p = e -> (e.getIntQTERECUE() < e.getIntQTECMDE()) && (e.getLgFAMILLEID().getBoolCHECKEXPIRATIONDATE());
        return lstTBonLivraisonDetail.parallelStream().anyMatch(p);
    }
    
    @Override
    public JSONObject cloturerBonLivraison(String id, TUser user) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TParameters tp = findParameter(DateConverter.KEY_ACTIVATE_PEREMPTION_DATE, emg);
            TBonLivraison OTBonLivraison = emg.find(TBonLivraison.class, id);
            List<TPreenregistrementDetail> avoirs = getAvoirs();
            Set<TPreenregistrementDetail> _avoirs = new HashSet<>();
            TOfficine officine = getEm().find(TOfficine.class, DateConverter.OFFICINE);
            userTransaction.begin();
            if (tp == null) {
                return json.put("success", false).put("msg", "Paramètre d'autorisation de saisie de produit sans date de péremption inexistant");
            }
            TOrder order = OTBonLivraison.getLgORDERID();
            TGrossiste grossiste = order.getLgGROSSISTEID();
            if (OTBonLivraison == null || OTBonLivraison.getStrSTATUT().equals(DateConverter.STATUT_IS_CLOSED)) {
                return json.put("success", false).put("msg", "Impossible de trouver ce bon. Verifier s'il ce bon n'est pas deja cloturé");
            }
            List<TBonLivraisonDetail> lstTBonLivraisonDetail = bonLivraisonDetail(id, emg);
            if (Integer.valueOf(tp.getStrVALUE()) == 1) {
                if (isEntreeStockIsAuthorize(lstTBonLivraisonDetail)) {
                    return json.put("success", false).put("msg", "La reception de certains produits n'a pas ete faites. Veuillez verifier vos saisie");
                }
            }
            for (TBonLivraisonDetail bn : lstTBonLivraisonDetail) {
                TFamille OFamille = bn.getLgFAMILLEID();
                List<Object[]> lst = listLot(OTBonLivraison.getStrREFLIVRAISON(), emg, OFamille.getLgFAMILLEID());
                if (lst.isEmpty()) {
                    createTLot(bn, user, OFamille, bn.getIntQTECMDE(), OTBonLivraison.getStrREFLIVRAISON(), grossiste, order.getStrREFORDER(), 0, emg);
                    addToStock(bn.getIntPRIXVENTE(), bn.getIntPAF(), bn.getLgBONLIVRAISONDETAIL(), user, bn.getIntQTECMDE(), 0, emg, OFamille);
                    bn.setIntQTERECUE(bn.getIntQTECMDE());
                    bn.setIntQTEMANQUANT(0);
                    bn.setIntQTEUG(0);
                } else {
                    for (Object[] item : lst) {
                        Integer cmde = Integer.valueOf(item[0] + ""), qu = Integer.valueOf(item[1] + "");
                        if (cmde < bn.getIntQTECMDE()) {
                            LOG.log(Level.INFO, "La reception de certains produits n'a pas ete faite {0} {1} {2}", new Object[]{OFamille.getIntCIP(), cmde, bn.getIntQTECMDE()});
                            if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
                                userTransaction.rollback();
                            }
                            return json.put("success", false).put("msg", "La reception de certains produits n'a pas ete faite. Veuillez verifier vos saisie");
                        }
                        cmde = (cmde > (bn.getIntQTECMDE() + bn.getIntQTEUG()) ? (bn.getIntQTECMDE() + bn.getIntQTEUG()) : cmde);
                        addToStock(bn.getIntPRIXVENTE(), bn.getIntPAF(), bn.getLgBONLIVRAISONDETAIL(), user, cmde, qu, emg, OFamille);
                        
                        if (qu > 0) {
                            String comm = "ENTREE UG Num BL :  " + OTBonLivraison.getStrREFLIVRAISON() + " PRODUIT : " + bn.getLgFAMILLEID().getIntCIP() + " " + bn.getLgFAMILLEID().getStrNAME() + " QUANTITE " + qu + "  PAR " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
                            logService.updateItem(user, OTBonLivraison.getStrREFLIVRAISON(), comm, TypeLog.QUANTITE_UG, bn, emg);
                            notificationService.save(new Notification()
                                    .canal(Canal.EMAIL)
                                    .typeNotification(TypeNotification.QUANTITE_UG)
                                    .message(comm)
                                    .addUser(user)
                            );
                        }
                        
                    }
                    
                }
                bn.setStrSTATUT(commonparameter.statut_is_Closed);
                bn.setDtUPDATED(new Date());
                emg.merge(bn);
                OFamille.setDtDATELASTENTREE(new Date());
                OFamille.setDtUPDATED(new Date());
                OFamille.setIntPAF(bn.getIntPAF());
                OFamille.setIntPAT(OFamille.getIntPAT());
                TFamilleGrossiste familleGrossiste = this.findFamilleGrossiste(OFamille.getLgFAMILLEID(), grossiste.getLgGROSSISTEID(), this.getEm());
                if (familleGrossiste != null) {
                    familleGrossiste.setIntPAF(bn.getIntPAF());
                }
                if (bn.getPrixUni() != null && bn.getPrixUni().compareTo(bn.getIntPRIXVENTE()) != 0) {
                    OFamille.setIntPRICE(bn.getIntPRIXVENTE());
                    if (familleGrossiste != null) {
                        familleGrossiste.setIntPRICE(bn.getIntPRIXVENTE());
                    }
                } else if (StringUtils.isEmpty(OFamille.getIntT()) && (bn.getIntPRIXVENTE().compareTo(OFamille.getIntPRICE()) > 0)) {
                    OFamille.setIntPRICE(bn.getIntPRIXVENTE());
                    if (familleGrossiste != null) {
                        
                        familleGrossiste.setIntPRICE(bn.getIntPRIXVENTE());
                    }
                    
                }
                emg.merge(OFamille);
                
                if (familleGrossiste != null) {
                    this.getEm().merge(familleGrossiste);
                }
                
                avoirs.stream().filter(e -> e.getLgFAMILLEID().equals(OFamille))
                        .forEach(s -> {
                            _avoirs.add(s);
                        });
            }
            
            closureOrder(order, emg);
            OTBonLivraison.setStrSTATUT(commonparameter.statut_is_Closed);
            OTBonLivraison.setDtUPDATED(new Date());
            OTBonLivraison.setLgUSERID(user);
            emg.merge(OTBonLivraison);
            transactionService.addTransactionBL(user, OTBonLivraison, emg);
            String comm = "ENTREE EN STOCK DU BL " + OTBonLivraison.getStrREFLIVRAISON() + " PAR " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
            logService.updateItem(user, OTBonLivraison.getStrREFLIVRAISON(), comm, TypeLog.ENTREE_EN_STOCK, OTBonLivraison, emg);
            notificationService.save(new Notification()
                    .canal(Canal.EMAIL)
                    .typeNotification(TypeNotification.ENTREE_EN_STOCK)
                    .message(comm)
                    .addUser(user)
            );
            
            Map<TClient, List<TPreenregistrementDetail>> map = _avoirs.stream().collect(Collectors.groupingBy(e -> e.getLgPREENREGISTREMENTID().getClient()));
            map.forEach((k, v) -> {
                if (k != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(officine.getStrNOMABREGE()).append(" ").append("M/Mme ");
                    sb.append(k.getStrFIRSTNAME()).append(" ").append(k.getStrLASTNAME()).append(" Vos produits en avoir suivant(s) sont disponibles: ").append("\n");
                    Map<TPreenregistrement, List<TPreenregistrementDetail>> mapp = v.stream().collect(Collectors.groupingBy(e -> e.getLgPREENREGISTREMENTID()));
                    mapp.forEach((p, values) -> {
                        values.forEach(e -> {
                            sb.append("-").append(e.getLgFAMILLEID().getStrNAME())
                                    .append("( ").append(e.getIntAVOIR()).append(" )")
                                    .append("\n");
                        });
                        sb.append("Ref:  ").append(p.getStrREFTICKET()).append(". Montant vente=").append(DateConverter.amountFormat(p.getIntPRICE())).append("\n");
                    });
                    sb.append("Merci de nous faire toujours confiance.");
                    
                    notificationService.save(new Notification()
                            .canal(Canal.SMS)
                            .typeNotification(TypeNotification.AVOIR_PRODUIT)
                            .message(sb.toString())
                            .addUser(user), k);
                    
                }
                
            });
            userTransaction.commit();
            
        } catch (Exception e) {
            try {
                LOG.log(Level.SEVERE, null, e);
                
                if (userTransaction.getStatus() == Status.STATUS_ACTIVE
                        || userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    userTransaction.rollback();
                }
                
                return json.put("success", false).put("msg", "Echec de validation de l'entrée en stock");
            } catch (SystemException ex) {
                Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json.put("success", true).put("msg", "Opération effectuée avec success");
    }
    
    List<TPreenregistrementDetail> getAvoirs() {
        try {
            TypedQuery<TPreenregistrementDetail> q = getEm().createNamedQuery("TPreenregistrementDetail.findAvoir", TPreenregistrementDetail.class);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
            
        }
        
    }
    
    private TEtiquette createEtiquette(TUser u, TWarehouse OTWarehouse, TTypeetiquette OTTypeetiquette, String str_CODE, String str_NAME, TFamille OTFamille, String int_NUMBER, EntityManager em) {
        TEtiquette OTEtiquette = null;
        try {
            OTEtiquette = new TEtiquette();
            OTEtiquette.setLgETIQUETTEID(UUID.randomUUID().toString());
            OTEtiquette.setStrCODE(str_CODE);
            OTEtiquette.setStrNAME(str_NAME);
            OTEtiquette.setDtPEROMPTION(OTWarehouse.getDtPEREMPTION());
            OTEtiquette.setLgFAMILLEID(OTFamille);
            OTEtiquette.setStrSTATUT(commonparameter.statut_enable);
            OTEtiquette.setDtCREATED(new Date());
            OTEtiquette.setIntNUMBER(int_NUMBER);
            OTEtiquette.setLgTYPEETIQUETTEID(OTTypeetiquette);
            OTEtiquette.setLgEMPLACEMENTID(u.getLgEMPLACEMENTID());
            em.persist(OTEtiquette);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return OTEtiquette;
    }
    
    private TParameters findParameter(String key, EntityManager emg) {
        try {
            TParameters parameters = emg.find(TParameters.class, key);
            return parameters;
        } catch (Exception e) {
            return null;
        }
    }
    
    private void addToStock(Integer prixU, Integer prixA, String key, TUser u, int qty, int ug, EntityManager em, TFamille OTFamille) {
        int initStock = mvtProduitService.updateStockReturnInitStock(OTFamille, u.getLgEMPLACEMENTID(), qty, ug, em);
        int finalQty = initStock + qty;
        if (finalQty > 0) {
            OTFamille.setDblPRIXMOYENPONDERE(Double.valueOf(calculPrixMoyenPondereReception(initStock, OTFamille.getIntPAF(), qty, prixA)));
        }
        
        mouvementProduitService.saveMvtProduit(prixU, prixA, key, DateConverter.ENTREE_EN_STOCK, OTFamille,
                u, u.getLgEMPLACEMENTID(), qty, initStock, finalQty, em, 0);
        mvtProduitService.saveMvtArticle(DateConverter.ACTION_ENTREE_STOCK, DateConverter.TYPE_ACTION_ADD,
                OTFamille, u, qty, initStock, finalQty, u.getLgEMPLACEMENTID(), em);
    }
    
    public TEtiquette createEtiquette(TBonLivraisonDetail bn, TUser u, TTypeetiquette OTTypeetiquette, TWarehouse OTWarehouse, TFamille OFamille, String int_NUMBER, EntityManager em) {
        TEtiquette OTEtiquette = null;
        String result;
        try {
            String str_NAME_TYPE_ETIQUETTE = OTTypeetiquette.getStrNAME();
            if (str_NAME_TYPE_ETIQUETTE.equalsIgnoreCase("CIP")) {
                result = OFamille.getIntCIP();
            } else if (str_NAME_TYPE_ETIQUETTE.equalsIgnoreCase("CIP_PRIX")) {
                result = DateConverter.getShortId(4) + "-" + OFamille.getIntCIP() + "-" + bn.getIntPRIXVENTE();
            } else if (str_NAME_TYPE_ETIQUETTE.equalsIgnoreCase("CIP_DESIGNATION")) {
                result = DateConverter.getShortId(4) + "-" + OFamille.getIntCIP() + "-" + OFamille.getStrNAME();
            } else if (str_NAME_TYPE_ETIQUETTE.equalsIgnoreCase("CIP_PRIX_DESIGNATION")) {
                result = DateConverter.getShortId(4) + "-" + OFamille.getIntCIP() + "-" + bn.getIntPRIXVENTE() + "-" + OFamille.getStrNAME();
            } else if (str_NAME_TYPE_ETIQUETTE.equalsIgnoreCase("POSITION")) {
                result = DateConverter.getShortId(4) + "-" + OFamille.getLgZONEGEOID().getStrLIBELLEE();
            } else {
                result = DateConverter.getShortId(4) + "-" + OFamille.getIntCIP() + "-" + bn.getIntPRIXVENTE() + "-" + OFamille.getStrNAME();
            }
            OTEtiquette = createEtiquette(u, OTWarehouse, OTTypeetiquette, result, str_NAME_TYPE_ETIQUETTE, OFamille, int_NUMBER, em);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
        }
        return OTEtiquette;
    }
    
    private void addWarehouse(TBonLivraisonDetail bn, TUser user, TFamille OTFamille, Integer int_NUMBER, TGrossiste OTGrossiste, String str_REF_LIVRAISON, Date dt_SORTIE_USINE, Date dt_PEREMPTION, int int_NUMBER_GRATUIT, TTypeetiquette OTTypeetiquette,
            String str_REF_ORDER, String int_NUM_LOT, EntityManager em) {
        TEtiquette OTEtiquette;
        try {
            Date now = new Date();
            TWarehouse OTWarehouse = new TWarehouse();
            OTWarehouse.setLgWAREHOUSEID(UUID.randomUUID().toString());
            OTWarehouse.setLgUSERID(user);
            OTWarehouse.setLgFAMILLEID(OTFamille);
            OTWarehouse.setIntNUMBER(int_NUMBER);
            OTWarehouse.setDtPEREMPTION(dt_PEREMPTION);
            OTWarehouse.setDtSORTIEUSINE(dt_SORTIE_USINE);
            OTWarehouse.setStrREFLIVRAISON(str_REF_LIVRAISON);
            OTWarehouse.setLgGROSSISTEID(OTGrossiste);
            OTWarehouse.setStrREFORDER(str_REF_ORDER);
            OTWarehouse.setDtCREATED(now);
            OTWarehouse.setDtUPDATED(now);
            OTWarehouse.setIntNUMLOT(int_NUM_LOT);
            OTWarehouse.setIntNUMBERGRATUIT(int_NUMBER_GRATUIT);
            OTWarehouse.setStrSTATUT(commonparameter.statut_enable);
            OTWarehouse.setLgTYPEETIQUETTEID(OTTypeetiquette == null ? em.find(TTypeetiquette.class, DateConverter.DEFAUL_TYPEETIQUETTE) : OTFamille.getLgTYPEETIQUETTEID());
            OTEtiquette = createEtiquette(bn, user, OTWarehouse.getLgTYPEETIQUETTEID(), OTWarehouse, OTFamille, String.valueOf(OTWarehouse.getIntNUMBER()), em);
            OTWarehouse.setStrCODEETIQUETTE(OTEtiquette.getStrCODE());
            em.persist(OTWarehouse);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
    }
    
    private TLot createTLot(TBonLivraisonDetail bn, TUser u, TFamille OTFamille, int int_NUMBER, String str_REF_LIVRAISON, TGrossiste OTGrossiste, String str_REF_ORDER, int int_UG, EntityManager em) {
        TLot OTLot = null;
        try {
            Date now = new Date();
            OTLot = new TLot(UUID.randomUUID().toString());
            OTLot.setLgUSERID(u);
            OTLot.setLgFAMILLEID(OTFamille);
            OTLot.setIntNUMBER(int_NUMBER); //quantite commandé + quantité livré
            OTLot.setDtSORTIEUSINE(now);
            OTLot.setStrREFLIVRAISON(str_REF_LIVRAISON);
            OTLot.setLgGROSSISTEID(OTGrossiste);
            OTLot.setDtCREATED(now);
            OTLot.setDtUPDATED(now);
            OTLot.setStrREFORDER(str_REF_ORDER);
            OTLot.setIntNUMBERGRATUIT(int_UG);
            OTLot.setStrSTATUT(commonparameter.statut_enable);
            OTLot.setIntQTYVENDUE(0);
            em.persist(OTLot);
            addWarehouse(bn, u, OTFamille, int_NUMBER, OTGrossiste, str_REF_LIVRAISON, new Date(), null, 0, null, str_REF_ORDER, null, em);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        return OTLot;
    }
    
    @Override
    public void closureOrder(TOrder OTOrder, EntityManager em) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<TOrderDetail> cq = cb.createCriteriaUpdate(TOrderDetail.class);
            Root<TOrderDetail> root = cq.from(TOrderDetail.class);
            cq.set(root.get("strSTATUT"), DateConverter.STATUT_IS_CLOSED)
                    .set(root.get("dtUPDATED"), new Date());
            cq.where(cb.equal(root.get("lgORDERID").get("lgORDERID"), OTOrder.getLgORDERID()));
            em.createQuery(cq).executeUpdate();
            OTOrder.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
            OTOrder.setRecu(Boolean.TRUE);
            OTOrder.setDtUPDATED(new Date());
            em.merge(OTOrder);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
        }
    }
    
    private List<TInventaireFamille> findByInventaire(String id) {
        try {
            TypedQuery<TInventaireFamille> q = getEm().createQuery("SELECT o FROM TInventaireFamille o WHERE o.lgINVENTAIREID.lgINVENTAIREID=?1", TInventaireFamille.class);
            q.setParameter(1, id);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }
    
    private Typemvtproduit findById(String id) {
        return getEm().find(Typemvtproduit.class, id);
    }
    
    public void saveMvtProduit(String pkey, Typemvtproduit typemvtproduit, TFamille famille, TUser lgUSERID, TEmplacement emplacement, Integer qteMvt, Integer qteDebut, Integer qteFinale) {
        HMvtProduit h = new HMvtProduit();
        h.setUuid(UUID.randomUUID().toString());
        h.setCreatedAt(LocalDateTime.now());
        h.setEmplacement(emplacement);
        h.setLgUSERID(lgUSERID);
        h.setFamille(famille);
        h.setMvtDate(LocalDate.now());
        h.setValeurTva(0);
        h.setTypemvtproduit(typemvtproduit);
        h.setPrixUn(famille.getIntPRICE());
        h.setPrixAchat(famille.getIntPAF());
        h.setQteMvt(qteMvt);
        h.setQteDebut(qteDebut);
        h.setPkey(pkey);
        h.setQteFinale(qteFinale);
        h.setChecked(true);
        getEm().persist(h);
    }
    
    @Override
    public JSONObject cloturerInvetaire(String inventaireId, TUser user) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEm();
        try {
            TInventaire inventaire = emg.find(TInventaire.class, inventaireId);
            List<TInventaireFamille> list = findByInventaire(inventaireId);
            Typemvtproduit typemvtproduit = findById(DateConverter.INVENTAIRE);
            userTransaction.begin();
            LongAdder count = new LongAdder();
            LongAdder count2 = new LongAdder();
            TEmplacement emplacement = user.getLgEMPLACEMENTID();
            list.stream().forEach(s -> {
                if (s.getIntNUMBER().compareTo(s.getIntNUMBERINIT()) != 0) {
                    count.increment();
                    TFamilleStock stock = s.getLgFAMILLESTOCKID();
                    stock.setIntNUMBERAVAILABLE(s.getIntNUMBER());
                    stock.setIntNUMBER(s.getIntNUMBER());
                    stock.setDtUPDATED(new Date());
                    emg.merge(stock);
                    s.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
                    s.setDtUPDATED(new Date());
                    emg.merge(s);
                    
                }
                
                saveMvtProduit(s.getLgINVENTAIREFAMILLEID() + "", typemvtproduit, s.getLgFAMILLEID(),
                        user, emplacement, s.getIntNUMBER(), s.getIntNUMBERINIT(), s.getIntNUMBER());
                mvtProduitService.saveMvtArticle(DateConverter.ACTION_INVENTAIRE, DateConverter.OTHER,
                        s.getLgFAMILLEID(), user, s.getIntNUMBER(), s.getIntNUMBERINIT(),
                        s.getIntNUMBER(), emplacement, emg);
                count2.increment();
                if (count2.intValue() > 0 && count2.intValue() % 10 == 0) {
                    emg.flush();
                    emg.clear();
                    
                }
            });
            inventaire.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
            inventaire.setDtUPDATED(new Date());
            inventaire.setLgUSERID(user);
            emg.merge(inventaire);
            String result = "Cloture effectuée avec succès; " + count.intValue() + " Articles mis à jour";
            json.put("success", true).put("msg", result);
            userTransaction.commit();
            
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException | JSONException e) {
            json.put("success", false).put("msg", "La cloture n'a pas abouti");
            try {
                LOG.log(Level.SEVERE, null, e);
                
                if (userTransaction.getStatus() == Status.STATUS_ACTIVE
                        || userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    userTransaction.rollback();
                }
            } catch (SystemException ex) {
                Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }
    
    @Override
    public String generateCIP(String int_CIP) {
        String result;
        int resultCIP = 0;
        
        char[] charArray = int_CIP.toCharArray();
        
        if (int_CIP.length() == 6) {
            for (int i = 1; i <= charArray.length; i++) {
                resultCIP += Integer.parseInt(charArray[(i - 1)] + "") * (i + 1);
            }
            
            int mod = resultCIP % 11;
            result = int_CIP + "" + mod;
        } else {
            result = int_CIP;
        }
        
        return result;
    }
    
    private TGrossiste getGrossiste(String lg_GROSSISTE_ID) {
        
        try {
            TypedQuery<TGrossiste> OTGrossiste = getEm().createQuery("SELECT t FROM TGrossiste t WHERE (t.lgGROSSISTEID = ?1 OR t.strLIBELLE = ?1 OR t.strCODE = ?1) AND t.strSTATUT = ?2", TGrossiste.class)
                    .setParameter(1, lg_GROSSISTE_ID).setParameter(2, commonparameter.statut_enable);
            return OTGrossiste.getSingleResult();
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
        
    }
    
    private void createSnapshotMvtArticle(TFamille OTFamille, Integer qty, TUser ooTUser, Integer initStock, Integer finalStock, TEmplacement emplacementId, EntityManager emg) {
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
    
    public void saveMvtArticle(TFamille tf, TUser ooTUser, Integer qty, Integer intiQty, Integer finalQty, TEmplacement emplacementId, EntityManager emg) {
        
        TMouvement OTMouvement = new TMouvement();
        OTMouvement.setLgMOUVEMENTID(UUID.randomUUID().toString());
        OTMouvement.setIntNUMBERTRANSACTION(1);
        OTMouvement.setDtDAY(new Date());
        OTMouvement.setStrSTATUT(commonparameter.statut_enable);
        OTMouvement.setIntNUMBER(qty);
        OTMouvement.setLgFAMILLEID(tf);
        OTMouvement.setLgUSERID(ooTUser);
        OTMouvement.setPKey("");
        OTMouvement.setStrACTION(DateConverter.ACTION_INVENTAIRE);
        OTMouvement.setStrTYPEACTION(DateConverter.OTHER);
        OTMouvement.setDtCREATED(new Date());
        OTMouvement.setDtUPDATED(new Date());
        OTMouvement.setLgEMPLACEMENTID(emplacementId);
        emg.persist(OTMouvement);
        createSnapshotMvtArticle(tf, qty, ooTUser, intiQty, finalQty, emplacementId, emg);
    }
    
    private TFamilleGrossiste findFamilleGrossiste(String lg_FAMILLE_ID, String lg_GROSSISTE_ID, EntityManager entityManager) {
        TFamilleGrossiste OTFamilleGrossiste = null;
        try {
            TypedQuery<TFamilleGrossiste> qry = entityManager.createQuery("SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1 AND (t.lgGROSSISTEID.lgGROSSISTEID = ?2 OR t.lgGROSSISTEID.strDESCRIPTION = ?2) AND t.strSTATUT = ?3 ", TFamilleGrossiste.class).
                    setParameter(1, lg_FAMILLE_ID)
                    .setParameter(2, lg_GROSSISTE_ID)
                    .setParameter(3, commonparameter.statut_enable);
            qry.setMaxResults(1);
            OTFamilleGrossiste = qry.getSingleResult();
            
        } catch (Exception e) {
//                 e.printStackTrace();
        }
        
        return OTFamilleGrossiste;
    }
    
    @Override
    public JSONObject createProduct(Params params) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            if (params.getDescription().length() < 6) {
                json.put("success", false);
                json.put("msg", "Le code CIP doit avoir au minimum 6 caractères");
                return json;
                
            }
            userTransaction.begin();
            EntityManager entityManager = this.getEm();
            TFamille OTFamille = entityManager.find(TFamille.class, params.getRef());
            TGrossiste OTGrossiste = getGrossiste(params.getRefParent());
            TFamilleGrossiste OTFamilleGrossiste = findFamilleGrossiste(OTFamille.getLgFAMILLEID(), OTGrossiste.getLgGROSSISTEID(), entityManager);
            String str_CODE_ARTICLE = generateCIP(params.getDescription());
            if (OTFamilleGrossiste != null) {
                OTFamilleGrossiste.setStrCODEARTICLE(str_CODE_ARTICLE);
                OTFamilleGrossiste.setDtUPDATED(new Date());
                entityManager.merge(OTFamilleGrossiste);
                
            } else {
                OTFamilleGrossiste = new TFamilleGrossiste();
                OTFamilleGrossiste.setLgFAMILLEID(OTFamille);
                OTFamilleGrossiste.setIntPAF(OTFamille.getIntPAF());
                OTFamilleGrossiste.setIntPRICE(OTFamille.getIntPRICE());
                OTFamilleGrossiste.setLgGROSSISTEID(OTGrossiste);
                OTFamilleGrossiste.setDtUPDATED(new Date());
                OTFamilleGrossiste.setDtCREATED(new Date());
                OTFamilleGrossiste.setStrCODEARTICLE(str_CODE_ARTICLE);
                entityManager.persist(OTFamilleGrossiste);
                
            }
            userTransaction.commit();
            json.put("success", true);
            json.put("msg", "Opération effectée avec success ");
        } catch (NotSupportedException | SystemException e) {
            json.put("success", false);
            json.put("msg", "Impossible de creer un code article ERROR :: " + e.getMessage());
            LOG.log(Level.SEVERE, null, e);
            try {
                if (userTransaction.getStatus() == Status.STATUS_ACTIVE
                        || userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    userTransaction.rollback();
                }
            } catch (SystemException ex) {
                Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }
    
    @Override
    public String genererReferenceCommande() {
        TParameters OTParameters = this.getEm().find(TParameters.class, "KEY_LAST_ORDER_COMMAND_NUMBER");
        TParameters OTParameters_KEY_SIZE_ORDER_NUMBER = this.getEm().find(TParameters.class, "KEY_SIZE_ORDER_NUMBER");
        JSONArray jsonArray = new JSONArray(OTParameters.getStrVALUE());
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        LocalDate date = LocalDate.parse(jsonObject.getString("str_last_date"), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        int lastCode = 0;
        if (date.equals(LocalDate.now())) {
            lastCode = Integer.valueOf(jsonObject.getString("str_last_date"));
        } else {
            date = LocalDate.now();
        }
        lastCode++;
        
        String left = StringUtils.leftPad("" + lastCode, Integer.valueOf(OTParameters_KEY_SIZE_ORDER_NUMBER.getStrVALUE()), '0');
        jsonObject.put("int_last_code", left);
        jsonObject.put("str_last_date", date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
        OTParameters.setStrVALUE(jsonArray.toString());
        this.getEm().merge(OTParameters);
        return LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")).concat("_") + left;
    }
    
    private TFamille findByCipOrEa0(String searchValue, TGrossiste grossiste) {
        try {
            TypedQuery<TFamille> q = getEm().createQuery("SELECT o FROM TFamille o WHERE o.lgGROSSISTEID =?1 AND (o.intCIP LIKE ?2 OR o.intEAN13 LIKE ?2) AND o.strSTATUT='enable' ", TFamille.class);
            q.setParameter(1, grossiste);
            q.setParameter(2, searchValue);
            q.setMaxResults(1);
            return q.getSingleResult();
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    @Override
    public TFamille findByCipOrEan(String searchValue, TGrossiste grossiste) {
        TFamille famille = findByCipOrEa0(searchValue, grossiste);
        if (famille != null) {
            return famille;
        }
        famille = findByCipOrEan1(searchValue, grossiste);
        return famille;
    }
    
    private TFamille findByCipOrEan1(String searchValue, TGrossiste grossiste) {
        try {
            TypedQuery<TFamilleGrossiste> q = getEm().createQuery("SELECT o FROM TFamilleGrossiste o WHERE o.lgGROSSISTEID =?1 AND o.strCODEARTICLE=?2 AND o.strSTATUT='enable' ", TFamilleGrossiste.class);
            q.setParameter(1, grossiste);
            q.setParameter(2, searchValue);
            q.setMaxResults(1);
            return q.getSingleResult().getLgFAMILLEID();
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    @Override
    public TOrderDetail findByProductAndOrder(TOrder order, TFamille famille) {
        try {
            TypedQuery<TOrderDetail> q = getEm().createNamedQuery("TOrderDetail.findByLgORDERIDAndLgFAMILLEID", TOrderDetail.class);
            q.setParameter("lgORDERID", order);
            q.setParameter("lgFAMILLEID", famille);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    @Override
    public void updateOrderItemQtyFromResponse(TOrderDetail item, int qty, TGrossiste grossiste) {
        
        item.setIntPRICE(qty * item.getIntPAFDETAIL());
        item.setIntQTEREPGROSSISTE(qty);
        item.setDtUPDATED(new Date());
        getEm().merge(item);
        
    }
    
    @Override
    public void addRuptureHistory(TOrderDetail item, TGrossiste grossiste) {
        TRuptureHistory OTRuptureHistory = new TRuptureHistory();
        OTRuptureHistory.setLgRUPTUREHISTORYID(RandomStringUtils.randomAlphanumeric(20));
        OTRuptureHistory.setLgFAMILLEID(item.getLgFAMILLEID());
        OTRuptureHistory.setIntNUMBER(item.getIntNUMBER());
        OTRuptureHistory.setDtCREATED(new Date());
        OTRuptureHistory.setGrossisteId(grossiste);
        getEm().persist(OTRuptureHistory);
        getEm().remove(item);
    }
    
    @Override
    public JSONObject verificationCommande(Part part, String orderId, TUser OTUser) {
        String fileName = part.getSubmittedFileName();
        String extension = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
        try {
            if (extension.equalsIgnoreCase("csv")) {
                
                return verificationCommandeCsv(part, orderId, OTUser);
                
            } else {
                return verificationCommandeXlsx(part, orderId, OTUser);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return new JSONObject().put("success", false);
        }
        
    }
    
    JSONObject verificationCommandeCsv(Part part, String orderId, TUser OTUser) throws IOException {
        try {
            CSVParser parser = new CSVParser(new InputStreamReader(part.getInputStream()), CSVFormat.EXCEL.withDelimiter(';'));
            int nbrePrisEnCompte = 0, nbreNonPrisEnCompte = 0, totalItemsCount = 0;
            TOrder order = getEm().find(TOrder.class, orderId);
            List<TOrderDetail> l = orderService.findByOrderId(order.getLgORDERID());
            TGrossiste grossiste = order.getLgGROSSISTEID();
            Set<TFamille> s = new HashSet<>();
            userTransaction.begin();
            Rupture rupture = orderService.creerRupture(order);
            for (CSVRecord cSVRecord : parser) {
                TFamille famille = findByCipOrEan(cSVRecord.get(0), grossiste);
                if (famille != null) {
                    totalItemsCount++;
                    s.add(famille);
                    
                    TOrderDetail item = findByProductAndOrder(order, famille);
                    if (item == null) {
                        continue;
                    }
                    int qtyCommande = Integer.valueOf(cSVRecord.get(1));
                    int qtyResponse = Integer.valueOf(cSVRecord.get(3));
                    int qty = qtyCommande - qtyResponse;
                    if (qtyResponse > 0) {
                        updateOrderItemQtyFromResponse(item, qtyResponse, grossiste);
                        nbrePrisEnCompte++;
                        if (qtyCommande > qtyResponse) {
                            orderService.creerRuptureItem(rupture, famille, qty);
                            nbreNonPrisEnCompte++;
                        }
                    } else {
                        
                        orderService.creerRuptureItem(rupture, famille, qty);
                        getEm().remove(item);
                        nbreNonPrisEnCompte++;
                    }
                    
                }
            }
            Set<TOrderDetail> orderDetails = productNotInOrderResponse(s, order, l.size(), totalItemsCount);
            for (TOrderDetail orderDetail : orderDetails) {
                orderService.creerRuptureItem(rupture, orderDetail.getLgFAMILLEID(), orderDetail.getIntNUMBER());
                getEm().remove(orderDetail);
                nbreNonPrisEnCompte++;
            }
            if (nbrePrisEnCompte == 0) {
                order.setStrSTATUT(DateConverter.STATUT_DELETE);
                getEm().merge(order);
            }
            userTransaction.commit();
            
            return new JSONObject().put("success", true)
                    .put("nbrePrisEnCompte", "Nombre de produits pris en compte :: " + nbrePrisEnCompte + "<br>" + (nbreNonPrisEnCompte > 0 ? nbreNonPrisEnCompte + " produit(s) en rupture" : ""));
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return new JSONObject().put("success", false);
            
        }
        
    }
    
    public Rupture creerRupture(TOrder order) {
        Rupture rupture = new Rupture();
        TGrossiste grossiste = order.getLgGROSSISTEID();
        rupture.setGrossiste(grossiste);
        rupture.setReference(order.getStrREFORDER());
        rupture.setStatut(DateConverter.STATUT_RUPTURE);
        getEm().persist(rupture);
        return rupture;
        
    }
    
    private Set<TOrderDetail> productNotInOrderResponse(Set<TFamille> familles, TOrder order, int originalSize, int totalItemsCount) {
        
        if (originalSize == totalItemsCount) {
            return Collections.emptySet();
        }
        List<TOrderDetail> l = orderService.findByOrderId(order.getLgORDERID());
        Set<TOrderDetail> s = new HashSet<>();
        
        Set<TFamille> set = l.stream().map(x -> x.getLgFAMILLEID()).collect(Collectors.toSet());
        
        ListUtils.removeAll(set, familles).forEach(e -> {
            
            TOrderDetail detail = findByProductAndOrder(order, e);
            s.add(detail);
        });
        return s;
        
    }
    
    JSONObject verificationCommandeXlsx(Part part, String orderId, TUser OTUse) throws IOException {
        try {
            
            int nbrePrisEnCompte = 0, nbreNonPrisEnCompte = 0, totalItemsCount = 0;
            TOrder order = getEm().find(TOrder.class, orderId);
            TGrossiste grossiste = order.getLgGROSSISTEID();
            List<TOrderDetail> l = orderService.findByOrderId(order.getLgORDERID());
//        new HSSFWorkbook(part.getInputStream());
            HSSFWorkbook workbook = new HSSFWorkbook(part.getInputStream());
            int num = workbook.getNumberOfSheets();
            userTransaction.begin();
            Rupture rupture = orderService.creerRupture(order);
            Set<TFamille> s = new HashSet<>();
            for (int j = 0; j < num; j++) {
                Sheet sheet = workbook.getSheetAt(j);
                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {
                    Row nextrow = (Row) rows.next();
                    
                    Cell cipCell = nextrow.getCell(0);
                    Cell qtyCell = nextrow.getCell(3);
                    Cell qtyCmd = nextrow.getCell(1);
                    TFamille famille = findByCipOrEan(((cipCell.getCellType() == 1) ? cipCell.getStringCellValue() : cipCell.getNumericCellValue() + ""), grossiste);
                    if (famille != null) {
                        totalItemsCount++;
                        s.add(famille);
                        TOrderDetail item = findByProductAndOrder(order, famille);
                        int qtyResponse = (int) qtyCell.getNumericCellValue();
                        int qtyCommande = (int) qtyCmd.getNumericCellValue();
                        int qty = qtyCommande - qtyResponse;
                        if (qtyResponse > 0) {
                            updateOrderItemQtyFromResponse(item, qtyResponse, grossiste);
                            nbrePrisEnCompte++;
                            if (qtyCommande > qtyResponse) {
                                orderService.creerRuptureItem(rupture, famille, qty);
                                nbreNonPrisEnCompte++;
                            }
                        } else {
//                    addRuptureHistory(item, grossiste);
                            orderService.creerRuptureItem(rupture, famille, item.getIntNUMBER());
                            getEm().remove(item);
                            nbreNonPrisEnCompte++;
                        }
                    }
                    
                }
            }
            Set<TOrderDetail> orderDetails = productNotInOrderResponse(s, order, l.size(), totalItemsCount);
            for (TOrderDetail orderDetail : orderDetails) {
                orderService.creerRuptureItem(rupture, orderDetail.getLgFAMILLEID(), orderDetail.getIntNUMBER());
                getEm().remove(orderDetail);
                nbreNonPrisEnCompte++;
            }
            if (nbrePrisEnCompte == 0) {
                order.setStrSTATUT(DateConverter.STATUT_DELETE);
                getEm().merge(order);
            }
            userTransaction.commit();
            return new JSONObject().put("success", true)
                    .put("nbrePrisEnCompte", "Nombre de produits pris en compte :: " + nbrePrisEnCompte + "<br>" + (nbreNonPrisEnCompte > 0 ? nbreNonPrisEnCompte + " produit(s) en rupture" : ""));
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(CommandeServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return new JSONObject().put("success", false);
            
        }
    }
}
