/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.ArticleDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.TCalendrier;
import dal.TCodeGestion;
import dal.TCoefficientPonderation;
import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleGrossiste;
import dal.TFamilleStock;
import dal.TFamille_;
import dal.TGrossiste;
import dal.TOrderDetail;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TPreenregistrementDetail_;
import dal.TSuggestionOrder;
import dal.TSuggestionOrderDetails;
import dal.TUser;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.SuggestionService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author Kobena
 */
@Stateless
public class SuggestionImpl implements SuggestionService {
    
    private static final Logger LOG = Logger.getLogger(SuggestionImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    
    public EntityManager getEmg() {
        return em;
    }
    
    public SuggestionImpl() {
    }
    
    @Override
    public Integer getQuantityReapportByCodeGestionArticle(TFamilleStock OTFamilleStock, TFamille famille, EntityManager emg) {
        Integer result, int_TOTAL_JOURS_VENTE = 0, int_BUTOIR_CHOISI,
                mois_histo;
        Integer qteReappro = 0, int_SEUIL_MIN_CALCULE, qteVenteArticle = 0, qteVenteJour;
        try {
            List<TCoefficientPonderation> lstTCoefficientPonderations;
            
            int JourDuMois = LocalDate.now().getDayOfMonth();
            
            TCodeGestion OTCodeGestion = famille.getLgCODEGESTIONID();
            mois_histo = OTCodeGestion.getIntMOISHISTORIQUEVENTE();
            // choix du butoir
            if (JourDuMois > OTCodeGestion.getIntDATEBUTOIRARTICLE()) {
                int_BUTOIR_CHOISI = OTFamilleStock.getLgFAMILLEID().getLgGROSSISTEID().getIntDATEBUTOIRARTICLE();
                result = int_BUTOIR_CHOISI - JourDuMois;
                
                if (!(result < OTCodeGestion.getIntJOURSCOUVERTURESTOCK())) {
                    result = OTCodeGestion.getIntJOURSCOUVERTURESTOCK();
                }

//            int_TOTAL_JOURS_VENTE = nombresJourVente(LocalDate.now().minusMonths(mois_histo)).parallelStream().map(TCalendrier::getIntNUMBERJOUR).reduce(0, Integer::sum);
                if (OTCodeGestion.getLgOPTIMISATIONQUANTITEID().getStrCODEOPTIMISATION().equals("1")) {
                    int_TOTAL_JOURS_VENTE = nombresJourVente(LocalDate.now().minusMonths(mois_histo), emg).stream().map(TCalendrier::getIntNUMBERJOUR).reduce(0, Integer::sum);
//                    OTCalendrier = getTCalendrier(String.valueOf(LocalDate.now().minusMonths(i)), LocalDate.now().getYear());
                    qteVenteArticle += quantiteVendue(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille.getLgFAMILLEID(), emg);
                    
                    if (famille.getBoolDECONDITIONNEEXIST() == 1) {
                        
                        try {
                            Double finalQty = Math.ceil(quantiteDeconditionnesVentes(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille, emg) / famille.getIntNUMBERDETAIL());
                            qteVenteArticle += finalQty.intValue();
                        } catch (Exception e) {
                        }
                        
                    }
                    
                } else if (OTCodeGestion.getLgOPTIMISATIONQUANTITEID().getStrCODEOPTIMISATION().equalsIgnoreCase("2")) {
                    lstTCoefficientPonderations = getListTCoefficientPonderation(OTCodeGestion.getLgCODEGESTIONID(), emg);
                    mois_histo = OTCodeGestion.getIntMOISHISTORIQUEVENTE();
                    if (lstTCoefficientPonderations.size() < OTCodeGestion.getIntMOISHISTORIQUEVENTE()) {
                        mois_histo = lstTCoefficientPonderations.size();
                    }
                    qteVenteArticle += quantiteVendue(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille.getLgFAMILLEID(), emg);
                    
                    if (famille.getBoolDECONDITIONNEEXIST() == 1) {
                        
                        try {
                            Double finalQty = Math.ceil(quantiteDeconditionnesVentes(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille, emg) / famille.getIntNUMBERDETAIL());
                            qteVenteArticle += finalQty.intValue();
                        } catch (Exception e) {
                        }
                        
                    }
                    qteVenteArticle = qteVenteArticle * 2;//a revoir

                    int_TOTAL_JOURS_VENTE = nombresJourVente(LocalDate.now().minusMonths(mois_histo), emg).stream().map(TCalendrier::getIntNUMBERJOUR).reduce(0, Integer::sum);
                }
                
            } else {
                int_BUTOIR_CHOISI = OTCodeGestion.getIntDATEBUTOIRARTICLE();
                result = int_BUTOIR_CHOISI - JourDuMois;
                if (OTCodeGestion.getIntDATELIMITEEXTRAPOLATION() <= JourDuMois) {
                    int_TOTAL_JOURS_VENTE = nombresJourVente(LocalDate.now(), emg).stream().map(TCalendrier::getIntNUMBERJOUR).reduce(0, Integer::sum);
                    qteVenteArticle += quantiteVendue(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille.getLgFAMILLEID(), emg);
                    
                    if (famille.getBoolDECONDITIONNEEXIST() == 1) {
                        
                        try {
                            Double finalQty = Math.ceil(quantiteDeconditionnesVentes(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille, emg) / famille.getIntNUMBERDETAIL());
                            qteVenteArticle += finalQty.intValue();
                        } catch (Exception e) {
                        }
                        
                    }
                    qteVenteArticle = qteVenteArticle * OTCodeGestion.getIntCOEFFICIENTPONDERATION();//a revoir
                    mois_histo++;
                }
            }
            qteVenteJour = qteVenteArticle / int_TOTAL_JOURS_VENTE;
            int_SEUIL_MIN_CALCULE = qteVenteJour * result;
            if (OTCodeGestion.getBoolOPTIMISATIONSEUILCMDE()) {
                int_SEUIL_MIN_CALCULE = qteVenteJour * famille.getLgGROSSISTEID().getIntDELAIREAPPROVISIONNEMENT();
                if (int_SEUIL_MIN_CALCULE > OTFamilleStock.getIntNUMBERAVAILABLE()) {
                    qteReappro = qteVenteJour * result;
                    qteReappro = (int_SEUIL_MIN_CALCULE - OTFamilleStock.getIntNUMBERAVAILABLE()) + qteReappro;
                    
                    Double _qteReappro = Math.ceil(qteReappro + ((famille.getLgGROSSISTEID().getIntCOEFSECURITY() * qteReappro) / 100));
                    qteReappro = _qteReappro.intValue();
                }
            } else {
                if (OTFamilleStock.getLgFAMILLEID().getIntSEUILMIN() > OTFamilleStock.getIntNUMBERAVAILABLE()) {
                    Double _qteReappro = Math.ceil((famille.getIntSEUILMIN() - OTFamilleStock.getIntNUMBERAVAILABLE()) + int_SEUIL_MIN_CALCULE);
                    qteReappro = _qteReappro.intValue();
                }
            }
        } catch (Exception e) {
        }
        return qteReappro;
    }
    
    public List<TCoefficientPonderation> getListTCoefficientPonderation(String lg_CODE_GESTION_ID, EntityManager emg) {
        List<TCoefficientPonderation> lst = new ArrayList<>();
        try {
            lst = emg.createQuery("SELECT t FROM TCoefficientPonderation t WHERE t.lgCODEGESTIONID.lgCODEGESTIONID = ?1 AND t.strSTATUT = ?2 ORDER BY t.intINDICEMONTH ASC")
                    .setParameter(1, lg_CODE_GESTION_ID).setParameter(2, commonparameter.statut_enable).getResultList();
        } catch (Exception e) {
            
        }
        return lst;
    }
    
    public TCalendrier getTCalendrier(String lg_MONTH_ID, int int_ANNEE, EntityManager emg) {
        TCalendrier OTCalendrier = null;
        try {
            OTCalendrier = (TCalendrier) emg.createQuery("SELECT t FROM TCalendrier t WHERE t.lgMONTHID.lgMONTHID = ?1 AND t.intANNEE = ?2")
                    .setParameter(1, lg_MONTH_ID).setParameter(2, int_ANNEE).getSingleResult();
        } catch (Exception e) {
//      
        }
        return OTCalendrier;
    }
    
    public TSuggestionOrder createSuggestionOrder(TGrossiste OTGrossiste, String str_STATUT, EntityManager emg) {
        try {
            TSuggestionOrder OTSuggestionOrder = new TSuggestionOrder();
            OTSuggestionOrder.setLgSUGGESTIONORDERID(UUID.randomUUID().toString());
            OTSuggestionOrder.setStrREF("REF_" + DateConverter.getShortId(7));
            OTSuggestionOrder.setLgGROSSISTEID(OTGrossiste);
            OTSuggestionOrder.setStrSTATUT(str_STATUT);
            OTSuggestionOrder.setDtCREATED(new Date());
            OTSuggestionOrder.setDtUPDATED(new Date());
            emg.persist(OTSuggestionOrder);
            return OTSuggestionOrder;
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
        
    }
    
    public TSuggestionOrder checkSuggestionGrossiteExiste(String lg_GROSSISTE_ID, EntityManager emg) {
        
        try {
            TypedQuery<TSuggestionOrder> q
                    = emg
                            .createQuery("SELECT t FROM TSuggestionOrder t WHERE t.lgGROSSISTEID.lgGROSSISTEID =?1  AND t.strSTATUT = ?2 ORDER BY t.dtUPDATED DESC ", TSuggestionOrder.class);
            
            q.setMaxResults(1)
                    .setParameter(1, lg_GROSSISTE_ID)
                    .setParameter(2, commonparameter.statut_is_Auto);
            return q
                    .getSingleResult();
            
        } catch (Exception e) {
            return null;
            
        }
        
    }
    
    private TSuggestionOrderDetails isProductExistInSomeSuggestion(String lg_famille_id, String OTSuggestionOrder, EntityManager emg) {
        TSuggestionOrderDetails OTSuggestionOrderDetails = null;
        try {
            OTSuggestionOrderDetails = (TSuggestionOrderDetails) emg.createQuery("SELECT t FROM TSuggestionOrderDetails t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgSUGGESTIONORDERID.lgSUGGESTIONORDERID = ?2").
                    setParameter(1, lg_famille_id).
                    setParameter(2, OTSuggestionOrder).setMaxResults(1).
                    getSingleResult();
            
        } catch (Exception e) {
            
        }
        return OTSuggestionOrderDetails;
    }
    
    public TSuggestionOrderDetails addToTSuggestionOrderDetails(TFamille OTFamille, TGrossiste OTGrossiste, TSuggestionOrder OTSuggestionOrder, int int_QTE_A_SUGGERE, EntityManager emg) {
        TSuggestionOrderDetails OTSuggestionOrderDetails = null;
        try {
            OTSuggestionOrderDetails = isProductExistInSomeSuggestion(OTFamille.getLgFAMILLEID(), OTSuggestionOrder.getLgSUGGESTIONORDERID(), emg);
            if (OTSuggestionOrderDetails == null) {
                createTSuggestionOrderDetails(OTSuggestionOrder, OTFamille, OTGrossiste, int_QTE_A_SUGGERE, emg);
            } else {
                OTSuggestionOrderDetails.setIntNUMBER(int_QTE_A_SUGGERE);
                OTSuggestionOrderDetails.setIntPRICE(int_QTE_A_SUGGERE * OTSuggestionOrderDetails.getIntPAFDETAIL());
                OTSuggestionOrderDetails.setDtUPDATED(new Date());
                
                emg.merge(OTSuggestionOrderDetails);
            }
            OTSuggestionOrder.setDtUPDATED(new Date());
            emg.merge(OTSuggestionOrder);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            
        }
        
        return OTSuggestionOrderDetails;
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
    
    @Override
    public void makeSuggestionAuto(String OTPreenregistrement) {
        EntityManager emg = this.getEmg();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, OTPreenregistrement);
            TUser user = preenregistrement.getLgUSERID();
            List<TPreenregistrementDetail> list = getTPreenregistrementDetail(preenregistrement, emg);
            makeSuggestionAuto(list, user.getLgEMPLACEMENTID());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            
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
    
    public void makeSuggestionAuto(List<TPreenregistrementDetail> list, TEmplacement emplacementId, EntityManager emg) {
        
        try {
            
            list.forEach(item -> {
                TFamille famille = item.getLgFAMILLEID();
                TFamilleStock familleStock = findStock(famille.getLgFAMILLEID(), emplacementId, emg);
                if (familleStock != null) {
                    makeSuggestionAuto(familleStock, famille);
                }
                
            });
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void makeSuggestionAuto(List<TPreenregistrementDetail> list, TEmplacement emplacementId) {
        
        try {
            
            list.forEach(item -> {
                TFamille famille = item.getLgFAMILLEID();
                TFamilleStock familleStock = findStock(famille.getLgFAMILLEID(), emplacementId, getEmg());
                if (familleStock != null) {
                    makeSuggestionAuto(familleStock, famille);
                }
                
            });
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    @Override
    public void makeSuggestionAuto(TFamilleStock OTFamilleStock, TFamille famille) {
        EntityManager emg=getEmg();
        if (famille.getIntSEUILMIN() != null && famille.getBoolDECONDITIONNE() == 0) {
            if (OTFamilleStock.getIntNUMBERAVAILABLE() <= famille.getIntSEUILMIN()) {
                int statut = verifierProduitDansLeProcessusDeCommande(famille);
                if (statut == 0 || statut == 1) {
                    TSuggestionOrder OTSuggestionOrder;
                    Integer int_QTE_A_SUGGERE;
                    TGrossiste grossiste = famille.getLgGROSSISTEID();
                    if (grossiste != null) {
                        OTSuggestionOrder = checkSuggestionGrossiteExiste(grossiste.getLgGROSSISTEID(), emg);
                        if (statut == 0) {
                            int_QTE_A_SUGGERE = calcQteReappro(OTFamilleStock, famille, emg);
                            if (OTSuggestionOrder == null) {
                                OTSuggestionOrder = createSuggestionOrder(grossiste, commonparameter.statut_is_Auto, emg);
                                createTSuggestionOrderDetails(OTSuggestionOrder, famille, grossiste, int_QTE_A_SUGGERE, emg);
                            } else {
                                addToTSuggestionOrderDetails(famille, grossiste, OTSuggestionOrder, int_QTE_A_SUGGERE, emg);
                            }
                        } else {
                            if (OTSuggestionOrder != null) {
                                int_QTE_A_SUGGERE = calcQteReappro(OTFamilleStock, famille, emg);
                                addToTSuggestionOrderDetails(famille, grossiste, OTSuggestionOrder, int_QTE_A_SUGGERE, emg);
                            }
                            
                        }
                    }
                    
                }
            }
        }
    }
    
    public TFamilleGrossiste findOrFamilleGrossiste(TFamille lg_FAMILLE_ID, TGrossiste lg_GROSSISTE_ID, EntityManager emg) {
        TFamilleGrossiste OTFamilleGrossiste;
        try {
            Query qry = emg.createQuery("SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1 AND (t.lgGROSSISTEID.lgGROSSISTEID = ?2 OR t.lgGROSSISTEID.strDESCRIPTION = ?2) AND t.strSTATUT = ?3 ").
                    setParameter(1, lg_FAMILLE_ID.getLgFAMILLEID())
                    .setParameter(2, lg_GROSSISTE_ID.getLgGROSSISTEID())
                    .setParameter(3, commonparameter.statut_enable);
            qry.setMaxResults(1);
            OTFamilleGrossiste = (TFamilleGrossiste) qry.getSingleResult();
            
        } catch (Exception e) {
            OTFamilleGrossiste = new TFamilleGrossiste(UUID.randomUUID().toString());
            OTFamilleGrossiste.setLgFAMILLEID(lg_FAMILLE_ID);
            OTFamilleGrossiste.setLgGROSSISTEID(lg_GROSSISTE_ID);
            OTFamilleGrossiste.setDtUPDATED(new Date());
            OTFamilleGrossiste.setDtCREATED(new Date());
            OTFamilleGrossiste.setIntNBRERUPTURE(0);
            OTFamilleGrossiste.setBlRUPTURE(Boolean.TRUE);
            OTFamilleGrossiste.setStrCODEARTICLE(lg_FAMILLE_ID.getIntCIP());
            OTFamilleGrossiste.setIntPAF(lg_FAMILLE_ID.getIntPAF());
            OTFamilleGrossiste.setStrSTATUT(commonparameter.statut_enable);
            OTFamilleGrossiste.setIntPRICE(lg_FAMILLE_ID.getIntPRICE());
            emg.persist(OTFamilleGrossiste);
            
        }
        
        return OTFamilleGrossiste;
    }
    
    private TSuggestionOrderDetails initTSuggestionOrderDetail(TSuggestionOrder OTSuggestionOrder, TFamille OTFamille, TGrossiste OTGrossiste, int int_NUMBER, EntityManager emg) {
        
        try {
            TFamilleGrossiste OTFamilleGrossiste = findOrFamilleGrossiste(OTFamille, OTGrossiste, emg);
            TSuggestionOrderDetails OTSuggestionOrderDetails = new TSuggestionOrderDetails();
            OTSuggestionOrderDetails.setLgSUGGESTIONORDERDETAILSID(UUID.randomUUID().toString());
            OTSuggestionOrderDetails.setLgSUGGESTIONORDERID(OTSuggestionOrder);
            OTSuggestionOrderDetails.setLgFAMILLEID(OTFamille);
            OTSuggestionOrderDetails.setLgGROSSISTEID(OTGrossiste);
            OTSuggestionOrderDetails.setIntNUMBER(int_NUMBER);
            OTSuggestionOrderDetails.setIntPRICE((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPAF() != null && OTFamilleGrossiste.getIntPAF() != 0) ? OTFamilleGrossiste.getIntPAF() * int_NUMBER : OTFamille.getIntPAF() * int_NUMBER);
            OTSuggestionOrderDetails.setIntPAFDETAIL((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPAF() != null && OTFamilleGrossiste.getIntPAF() != 0) ? OTFamilleGrossiste.getIntPAF() : OTFamille.getIntPAF());
            OTSuggestionOrderDetails.setIntPRICEDETAIL((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPRICE() != null && OTFamilleGrossiste.getIntPRICE() != 0) ? OTFamilleGrossiste.getIntPRICE() : OTFamille.getIntPRICE());
            OTSuggestionOrderDetails.setStrSTATUT(commonparameter.statut_is_Process);
            OTSuggestionOrderDetails.setDtCREATED(new Date());
            OTSuggestionOrderDetails.setDtUPDATED(new Date());
            emg.persist(OTSuggestionOrderDetails);
//            OTFamille.setIntORERSTATUS((short) 1);
//            emg.merge(OTFamille);

            return OTSuggestionOrderDetails;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            
            return null;
        }
    }
    
    public TSuggestionOrderDetails findFamilleInTSuggestionOrderDetails(String lg_SUGGESTION_ORDER_ID, String lg_famille_id, EntityManager emg) {
        TSuggestionOrderDetails OTSuggestionOrderDetails = null;
        try {
            Query qry = emg.createQuery("SELECT t FROM TSuggestionOrderDetails t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgSUGGESTIONORDERID.lgSUGGESTIONORDERID = ?2 ").
                    setParameter(2, lg_SUGGESTION_ORDER_ID).
                    setParameter(1, lg_famille_id).setMaxResults(1);
            OTSuggestionOrderDetails = (TSuggestionOrderDetails) qry.getSingleResult();
            
        } catch (Exception e) {
            
        }
        return OTSuggestionOrderDetails;
    }
    
    public void createTSuggestionOrderDetails(TSuggestionOrder OTSuggestionOrder, TFamille OTFamille, TGrossiste OTGrossiste, int int_NUMBER, EntityManager emg) {
        initTSuggestionOrderDetail(OTSuggestionOrder, OTFamille, OTGrossiste, int_NUMBER, emg);
        
    }
    
    @Override
    public Integer quantiteVendue(LocalDate dtDEBUT, LocalDate dtFin, String produitId, EntityManager emg) {
        Integer qty = 0;
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            cq.select(
                    cb.sum(root.get(TPreenregistrementDetail_.intQUANTITY))
            );
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get("lgFAMILLEID"), produitId)));
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementDetail_.strSTATUT), commonparameter.statut_is_Closed)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementDetail_.dtCREATED)), java.sql.Date.valueOf(dtDEBUT),
                    java.sql.Date.valueOf(dtFin));
            predicates.add(cb.and(btw));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            return (Integer) q.getSingleResult();
        } catch (Exception e) {
            
        }
        return qty;
    }
    
    public Integer calcQteReappro(TFamilleStock OTFamilleStock, TFamille tf, EntityManager emg) {
        int QTE_REAPPRO = 1;
        try {
            TCodeGestion codeGestion = tf.getLgCODEGESTIONID();
            if (codeGestion != null && (!codeGestion.getLgOPTIMISATIONQUANTITEID().getStrCODEOPTIMISATION().equals("0"))) {
                QTE_REAPPRO = getQuantityReapportByCodeGestionArticle(OTFamilleStock, tf, emg);
                
            } else if (tf.getIntQTEREAPPROVISIONNEMENT() != null && tf.getIntSEUILMIN() != null) {
                if (tf.getIntSEUILMIN() >= OTFamilleStock.getIntNUMBERAVAILABLE()) {
                    QTE_REAPPRO = (tf.getIntSEUILMIN() - OTFamilleStock.getIntNUMBERAVAILABLE()) + tf.getIntQTEREAPPROVISIONNEMENT();
                    
                }
            }
        } catch (Exception e) {
        }
        
        return (QTE_REAPPRO > 0 ? QTE_REAPPRO : 1);
    }
    
    public Integer quantiteDeconditionnesVentes(LocalDate dtDEBUT, LocalDate dtFin, TFamille produitId, EntityManager emg) {
        Integer qty = 0;
        try {
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TPreenregistrementDetail> root = cq.from(TPreenregistrementDetail.class);
            cq.select(
                    cb.sum(root.get(TPreenregistrementDetail_.intQUANTITY))
            );
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementDetail_.lgFAMILLEID).get(TFamille_.lgFAMILLEPARENTID).get("lgFAMILLEID"), produitId.getLgFAMILLEID())));
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementDetail_.strSTATUT), commonparameter.statut_is_Closed)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementDetail_.dtCREATED)), java.sql.Date.valueOf(dtDEBUT),
                    java.sql.Date.valueOf(dtFin));
            predicates.add(cb.and(btw));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            return (Integer) q.getSingleResult();
        } catch (Exception e) {
            
        }
        return qty;
    }
    
    @Override
    public List<TCalendrier> nombresJourVente(LocalDate begin, EntityManager emg) {
        try {
            TypedQuery<TCalendrier> tq = emg.createQuery("SELECT o FROM TCalendrier o WHERE o.lgMONTHID.lgMONTHID   =?1", TCalendrier.class);
            tq.setParameter(1, begin.getMonthValue());
            return tq.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<TSuggestionOrderDetails> findFamillesBySuggestion(String suggestionId) {
        try {
            TypedQuery<TSuggestionOrderDetails> q = getEmg().createQuery("SELECT o FROM TSuggestionOrderDetails o WHERE o.lgSUGGESTIONORDERID.lgSUGGESTIONORDERID=?1 ", TSuggestionOrderDetails.class);
            q.setParameter(1, suggestionId);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public int verifierProduitDansLeProcessusDeCommande(TFamille famille) {
        int statut = verifierProduitCommande(famille);
        if (statut == 0) {
            statut = verifierProduitDansSuggestion(famille);
        }
        return statut;
    }
    
    private int verifierProduitDansSuggestion(TFamille famille) {
        try {
            TypedQuery<TSuggestionOrderDetails> q = getEmg().createQuery("SELECT o FROM TSuggestionOrderDetails o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND ( o.strSTATUT=?2 OR o.strSTATUT=?3 OR o.strSTATUT=?4  ) ", TSuggestionOrderDetails.class);
            q.setParameter(1, famille.getLgFAMILLEID());
            q.setParameter(2, DateConverter.STATUT_AUTO);
            q.setParameter(3, DateConverter.STATUT_PROCESS);
            q.setParameter(4, DateConverter.STATUT_PENDING);
            q.setMaxResults(1);
            return q.getSingleResult() != null ? 1 : 0;
            
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int verifierProduitCommande(TFamille famille) {
        try {
            TypedQuery<TOrderDetail> q = getEmg().createQuery("SELECT o FROM TOrderDetail o WHERE o.lgFAMILLEID.lgFAMILLEID=?1 AND o.lgORDERID.recu=?2 ", TOrderDetail.class);
            q.setParameter(1, famille.getLgFAMILLEID());
            q.setParameter(2, Boolean.FALSE);
            q.setMaxResults(1);
            TOrderDetail detail = q.getSingleResult();
            if (detail == null) {
                return 0;
            }
            if (detail.getStrSTATUT().equals(DateConverter.STATUT_PROCESS)) {
                return 2;
            }
            if (detail.getStrSTATUT().equals(DateConverter.STATUT_PASSED)) {
                return 3;
            }
            if (detail.getStrSTATUT().equals(DateConverter.STATUT_IS_CLOSED)) {
                return 4;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public JSONObject makeSuggestion(List<VenteDetailsDTO> datas) throws JSONException {
        try {
            LongAdder count = new LongAdder();
            Map<String, List<VenteDetailsDTO>> groupingByGrossisteId = datas.stream().collect(Collectors.groupingBy(VenteDetailsDTO::getTypeVente));
            groupingByGrossisteId.forEach((k, v) -> {
                TGrossiste OTGrossiste = getEmg().find(TGrossiste.class, k);
                TSuggestionOrder suggestionOrder = createSuggestionOrder(OTGrossiste, DateConverter.STATUT_PROCESS, getEmg());
                v.forEach(o -> {
                    TFamille OTFamille = getEmg().find(TFamille.class, o.getLgFAMILLEID());
                    initTSuggestionOrderDetail(suggestionOrder, OTFamille, OTGrossiste, o.getIntQUANTITY(), getEmg());
                    count.increment();
                });
                
            });
            return new JSONObject().put("success", true).put("count", count.intValue());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }
        
    }
     @Override
    public JSONObject makeSuggestion(Set<VenteDetailsDTO> datas) throws JSONException {
        try {
            LongAdder count = new LongAdder();
            Map<String, List<VenteDetailsDTO>> groupingByGrossisteId = datas.stream().collect(Collectors.groupingBy(VenteDetailsDTO::getTypeVente));
            groupingByGrossisteId.forEach((k, v) -> {
                TGrossiste OTGrossiste = getEmg().find(TGrossiste.class, k);
                TSuggestionOrder suggestionOrder = createSuggestionOrder(OTGrossiste, DateConverter.STATUT_PROCESS, getEmg());
                v.forEach(o -> {
                    TFamille OTFamille = getEmg().find(TFamille.class, o.getLgFAMILLEID());
                    initTSuggestionOrderDetail(suggestionOrder, OTFamille, OTGrossiste, o.getIntQUANTITY(), getEmg());
                    count.increment();
                });
                
            });
            return new JSONObject().put("success", true).put("count", count.intValue());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }
        
    }
    @Override
    public JSONObject makeSuggestionFromArticleInvendus(List<ArticleDTO> datas, TUser u) throws JSONException {
        try {
            LongAdder count = new LongAdder();
            Map<String, List<ArticleDTO>> groupingByGrossisteId = datas.stream().collect(Collectors.groupingBy(ArticleDTO::getGrossisteId));
            groupingByGrossisteId.forEach((k, v) -> {
                TGrossiste OTGrossiste = getEmg().find(TGrossiste.class, k);
                TSuggestionOrder suggestionOrder = createSuggestionOrder(OTGrossiste, DateConverter.STATUT_PROCESS, getEmg());
                v.forEach(o -> {
                    TFamille OTFamille = getEmg().find(TFamille.class, o.getId());
                    TFamilleStock familleStock = findStock(OTFamille.getLgFAMILLEID(), u.getLgEMPLACEMENTID(), this.getEmg());
                    if (OTFamille.getBoolDECONDITIONNE().compareTo(Short.valueOf("0")) == 0 && familleStock != null) {
                        initTSuggestionOrderDetail(suggestionOrder, OTFamille, OTGrossiste, (OTFamille.getIntQTEREAPPROVISIONNEMENT() > 0 ? OTFamille.getIntQTEREAPPROVISIONNEMENT() : 0), getEmg());
                        count.increment();
                    }
                    
                });
                
            });
            return new JSONObject().put("success", true).put("count", count.intValue());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return new JSONObject().put("success", false);
        }
        
    }
}
