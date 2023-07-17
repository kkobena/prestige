/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.ArticleDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import javax.persistence.criteria.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.SuggestionService;
import rest.service.dto.SuggestionDTO;
import rest.service.dto.SuggestionOrderDetailDTO;

import rest.service.dto.SuggestionsDTO;
import util.Constant;
import util.DateConverter;
import util.FunctionUtils;
import util.NumberUtils;

/**
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

//
                if (OTCodeGestion.getLgOPTIMISATIONQUANTITEID().getStrCODEOPTIMISATION().equals("1")) {
                    int_TOTAL_JOURS_VENTE = nombresJourVente(LocalDate.now().minusMonths(mois_histo), emg).stream().map(TCalendrier::getIntNUMBERJOUR).reduce(0, Integer::sum);
                    qteVenteArticle += quantiteVendue(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille.getLgFAMILLEID(), emg);

                    if (famille.getBoolDECONDITIONNEEXIST() == 1) {

                        try {
                            double finalQty = Math.ceil(quantiteDeconditionnesVentes(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille, emg) / famille.getIntNUMBERDETAIL());
                            qteVenteArticle += (int) finalQty;
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
                            double finalQty = Math.ceil(quantiteDeconditionnesVentes(LocalDate.now().minusMonths(mois_histo), LocalDate.now(), famille, emg) / famille.getIntNUMBERDETAIL());
                            qteVenteArticle += (int) finalQty;
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

                    Double qteReappro2 = Math.ceil(qteReappro + ((famille.getLgGROSSISTEID().getIntCOEFSECURITY() * qteReappro) / 100));
                    qteReappro = qteReappro2.intValue();
                }
            } else {
                if (OTFamilleStock.getLgFAMILLEID().getIntSEUILMIN() > OTFamilleStock.getIntNUMBERAVAILABLE()) {
                    Double qteReapp = Math.ceil((famille.getIntSEUILMIN() - OTFamilleStock.getIntNUMBERAVAILABLE()) + int_SEUIL_MIN_CALCULE);
                    qteReappro = qteReapp.intValue();
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
                    .setParameter(1, lg_CODE_GESTION_ID).setParameter(2, Constant.STATUT_ENABLE).getResultList();
        } catch (Exception e) {

        }
        return lst;
    }

    public TCalendrier getTCalendrier(String lg_MONTH_ID, int int_ANNEE, EntityManager emg) {
        TCalendrier oTCalendrier = null;
        try {
            oTCalendrier = (TCalendrier) emg.createQuery("SELECT t FROM TCalendrier t WHERE t.lgMONTHID.lgMONTHID = ?1 AND t.intANNEE = ?2")
                    .setParameter(1, lg_MONTH_ID).setParameter(2, int_ANNEE).getSingleResult();
        } catch (Exception e) {
//      
        }
        return oTCalendrier;
    }

    public TSuggestionOrder createSuggestionOrder(TGrossiste grossiste, String strSTATUT) {
        try {
            TSuggestionOrder suggestionOrder = new TSuggestionOrder();
            suggestionOrder.setLgSUGGESTIONORDERID(UUID.randomUUID().toString());
            suggestionOrder.setStrREF("REF_" + DateConverter.getShortId(7));
            suggestionOrder.setLgGROSSISTEID(grossiste);
            suggestionOrder.setStrSTATUT(strSTATUT);
            suggestionOrder.setDtCREATED(new Date());
            suggestionOrder.setDtUPDATED(suggestionOrder.getDtCREATED());
            getEmg().persist(suggestionOrder);
            return suggestionOrder;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }

    }

    private TSuggestionOrder checkSuggestionGrossiteExiste(String lgGROSSISTEID) {

        try {
            TypedQuery<TSuggestionOrder> q
                    = this.getEmg()
                            .createQuery("SELECT t FROM TSuggestionOrder t WHERE t.lgGROSSISTEID.lgGROSSISTEID =?1  AND t.strSTATUT = ?2 ORDER BY t.dtUPDATED DESC ", TSuggestionOrder.class);

            q.setMaxResults(1)
                    .setParameter(1, lgGROSSISTEID)
                    .setParameter(2, Constant.STATUT_AUTO);
            return q
                    .getSingleResult();

        } catch (Exception e) {
            return null;

        }

    }

    private TSuggestionOrderDetails isProductExistInSomeSuggestion(String lgFamilleId, String suggestionOrderId) {
        TSuggestionOrderDetails oTSuggestionOrderDetails = null;
        try {
            oTSuggestionOrderDetails = (TSuggestionOrderDetails) getEmg().createQuery("SELECT t FROM TSuggestionOrderDetails t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgSUGGESTIONORDERID.lgSUGGESTIONORDERID = ?2").
                    setParameter(1, lgFamilleId).
                    setParameter(2, suggestionOrderId).setMaxResults(1).
                    getSingleResult();

        } catch (Exception e) {

        }
        return oTSuggestionOrderDetails;
    }

    public TSuggestionOrderDetails addToTSuggestionOrderDetails(TFamille famille, TGrossiste oTGrossiste, TSuggestionOrder oTSuggestionOrder, int qteSuggere, EntityManager emg) {
        TSuggestionOrderDetails oTSuggestionOrderDetails = null;
        try {
            oTSuggestionOrderDetails = isProductExistInSomeSuggestion(famille.getLgFAMILLEID(), oTSuggestionOrder.getLgSUGGESTIONORDERID());
            if (oTSuggestionOrderDetails == null) {
                createTSuggestionOrderDetails(oTSuggestionOrder, famille, oTGrossiste, qteSuggere);
            } else {
                oTSuggestionOrderDetails.setIntNUMBER(qteSuggere);
                oTSuggestionOrderDetails.setIntPRICE(qteSuggere * oTSuggestionOrderDetails.getIntPAFDETAIL());
                oTSuggestionOrderDetails.setDtUPDATED(new Date());

                emg.merge(oTSuggestionOrderDetails);
            }
            oTSuggestionOrder.setDtUPDATED(new Date());
            emg.merge(oTSuggestionOrder);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }

        return oTSuggestionOrderDetails;
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
    public void makeSuggestionAuto(String oTPreenregistrement) {
        EntityManager emg = this.getEmg();
        try {
            TPreenregistrement preenregistrement = emg.find(TPreenregistrement.class, oTPreenregistrement);
            TUser user = preenregistrement.getLgUSERID();
            List<TPreenregistrementDetail> list = getTPreenregistrementDetail(preenregistrement, emg);
            makeSuggestionAuto(list, user.getLgEMPLACEMENTID());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
    }

    public TFamilleStock findStock(String oTFamille, TEmplacement emplacement) {

        try {
            Query query = this.getEmg().createQuery("SELECT t FROM TFamilleStock t WHERE  t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgEMPLACEMENTID.lgEMPLACEMENTID = ?2");
            query.
                    setParameter(1, oTFamille);
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

    public void makeSuggestionAuto0(List<TPreenregistrementDetail> list, TEmplacement emplacementId) {

        try {

            list.forEach(item -> {
                TFamille famille = item.getLgFAMILLEID();
                TFamilleStock familleStock = findStock(famille.getLgFAMILLEID(), emplacementId);
                if (familleStock != null) {
                    makeSuggestionAuto(familleStock, famille);
                }

            });

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void makeSuggestionAuto(List<TPreenregistrementDetail> list, TEmplacement emplacementId) {

        try {

            list.forEach(item -> {
                TFamille famille = item.getLgFAMILLEID();
                if (StringUtils.isNotEmpty(famille.getLgFAMILLEPARENTID()) && famille.getBoolDECONDITIONNE() == 1) {
                    famille = this.getEmg().find(TFamille.class, famille.getLgFAMILLEPARENTID());
                }
                TFamilleStock familleStock = findStock(famille.getLgFAMILLEID(), emplacementId);
                if (familleStock != null) {
                    makeSuggestionAuto(familleStock, famille);
                }

            });

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void proccessSuggetion(TFamille famille, TEmplacement emplacementId) {

        if (StringUtils.isNotEmpty(famille.getLgFAMILLEPARENTID()) && famille.getBoolDECONDITIONNE() == 1) {
            famille = this.getEmg().find(TFamille.class, famille.getLgFAMILLEPARENTID());
        }
        TFamilleStock familleStock = findStock(famille.getLgFAMILLEID(), emplacementId);
        if (familleStock != null) {
            makeSuggestionAuto(familleStock, famille);
        }

    }

    @Override
    public void makeSuggestionAuto(TFamilleStock oFamilleStock, TFamille famille) {

        EntityManager emg = getEmg();
        if (Objects.nonNull(famille.getIntSEUILMIN()) && oFamilleStock.getIntNUMBERAVAILABLE() <= famille.getIntSEUILMIN() && famille.getBoolDECONDITIONNE() == 0 && famille.getStrSTATUT().equals(DateConverter.STATUT_ENABLE)) {

            int statut = verifierProduitDansLeProcessusDeCommande(famille);
            if (statut == 0 || statut == 1) {
                TSuggestionOrder oSuggestionOrder;
                Integer intQTEASUGGERE;
                TGrossiste grossiste = famille.getLgGROSSISTEID();
                if (grossiste != null) {
                    oSuggestionOrder = checkSuggestionGrossiteExiste(grossiste.getLgGROSSISTEID());
                    if (statut == 0) {
                        intQTEASUGGERE = calcQteReappro(oFamilleStock, famille, emg);
                        if (oSuggestionOrder == null) {
                            oSuggestionOrder = createSuggestionOrder(grossiste, Constant.STATUT_AUTO);
                            createTSuggestionOrderDetails(oSuggestionOrder, famille, grossiste, intQTEASUGGERE);
                        } else {
                            addToTSuggestionOrderDetails(famille, grossiste, oSuggestionOrder, intQTEASUGGERE, emg);
                        }
                    } else {
                        if (oSuggestionOrder != null) {
                            intQTEASUGGERE = calcQteReappro(oFamilleStock, famille, emg);
                            addToTSuggestionOrderDetails(famille, grossiste, oSuggestionOrder, intQTEASUGGERE, emg);
                        }

                    }
                }

            }
        }
    }

    private TFamilleGrossiste findOrCreateFamilleGrossiste(TFamille lgFAMILLEID, TGrossiste lgGROSSISTEID) {
        TFamilleGrossiste oTFamilleGrossiste;
        try {
            Query qry = getEmg().createQuery("SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgGROSSISTEID.lgGROSSISTEID = ?2  AND t.strSTATUT = ?3 ").
                    setParameter(1, lgFAMILLEID.getLgFAMILLEID())
                    .setParameter(2, lgGROSSISTEID.getLgGROSSISTEID())
                    .setParameter(3, Constant.STATUT_ENABLE);
            qry.setMaxResults(1);
            oTFamilleGrossiste = (TFamilleGrossiste) qry.getSingleResult();

        } catch (Exception e) {
            oTFamilleGrossiste = new TFamilleGrossiste(UUID.randomUUID().toString());
            oTFamilleGrossiste.setLgFAMILLEID(lgFAMILLEID);
            oTFamilleGrossiste.setLgGROSSISTEID(lgGROSSISTEID);
            oTFamilleGrossiste.setDtCREATED(new Date());
            oTFamilleGrossiste.setDtUPDATED(oTFamilleGrossiste.getDtCREATED());
            oTFamilleGrossiste.setIntNBRERUPTURE(0);
            oTFamilleGrossiste.setBlRUPTURE(Boolean.TRUE);
            oTFamilleGrossiste.setStrCODEARTICLE(lgFAMILLEID.getIntCIP());
            oTFamilleGrossiste.setIntPAF(lgFAMILLEID.getIntPAF());
            oTFamilleGrossiste.setStrSTATUT(Constant.STATUT_ENABLE);
            oTFamilleGrossiste.setIntPRICE(lgFAMILLEID.getIntPRICE());
            getEmg().persist(oTFamilleGrossiste);

        }

        return oTFamilleGrossiste;
    }

    private TSuggestionOrderDetails initTSuggestionOrderDetail(TSuggestionOrder suggestionOrder, TFamille famille, TGrossiste grossiste, int intNumber) {
        TFamilleGrossiste familleGrossiste = findOrCreateFamilleGrossiste(famille, grossiste);
        TSuggestionOrderDetails orderDetails = new TSuggestionOrderDetails();
        orderDetails.setLgSUGGESTIONORDERDETAILSID(UUID.randomUUID().toString());
        orderDetails.setLgSUGGESTIONORDERID(suggestionOrder);
        orderDetails.setLgFAMILLEID(famille);
        orderDetails.setLgGROSSISTEID(grossiste);
        orderDetails.setIntNUMBER(intNumber);
        orderDetails.setIntPRICE((familleGrossiste != null && familleGrossiste.getIntPAF() != null && familleGrossiste.getIntPAF() != 0) ? familleGrossiste.getIntPAF() * intNumber : famille.getIntPAF() * intNumber);
        orderDetails.setIntPAFDETAIL((familleGrossiste != null && familleGrossiste.getIntPAF() != null && familleGrossiste.getIntPAF() != 0) ? familleGrossiste.getIntPAF() : famille.getIntPAF());
        orderDetails.setIntPRICEDETAIL((familleGrossiste != null && familleGrossiste.getIntPRICE() != null && familleGrossiste.getIntPRICE() != 0) ? familleGrossiste.getIntPRICE() : famille.getIntPRICE());
        orderDetails.setStrSTATUT(Constant.STATUT_IS_PROGRESS);
        orderDetails.setDtCREATED(new Date());
        orderDetails.setDtUPDATED(orderDetails.getDtCREATED());
        getEmg().persist(orderDetails);
        return orderDetails;

    }

    public TSuggestionOrderDetails findFamilleInTSuggestionOrderDetails(String lgSUGGESTIONORDERID, String lgfamilleId) {
        TSuggestionOrderDetails oTSuggestionOrderDetails = null;
        try {
            Query qry = this.getEmg().createQuery("SELECT t FROM TSuggestionOrderDetails t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgSUGGESTIONORDERID.lgSUGGESTIONORDERID = ?2 ").
                    setParameter(2, lgSUGGESTIONORDERID).
                    setParameter(1, lgfamilleId).setMaxResults(1);
            oTSuggestionOrderDetails = (TSuggestionOrderDetails) qry.getSingleResult();

        } catch (Exception e) {

        }
        return oTSuggestionOrderDetails;
    }

    public void createTSuggestionOrderDetails(TSuggestionOrder suggestionOrder, TFamille oTFamille, TGrossiste oTGrossiste, int intNUMBER) {
        initTSuggestionOrderDetail(suggestionOrder, oTFamille, oTGrossiste, intNUMBER);

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
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementDetail_.strSTATUT), Constant.STATUT_IS_CLOSED)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementDetail_.dtCREATED)), java.sql.Date.valueOf(dtDEBUT),
                    java.sql.Date.valueOf(dtFin));
            predicates.add(cb.and(btw));
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
            Query q = emg.createQuery(cq);
            return (Integer) q.getSingleResult();
        } catch (Exception e) {

        }
        return qty;
    }

   public Integer calcQteReappro(TFamilleStock oFamilleStock, TFamille tf, EntityManager emg) {
        int qteReappro = 1;
        try {
            TCodeGestion codeGestion = tf.getLgCODEGESTIONID();
            if (codeGestion != null && (!codeGestion.getLgOPTIMISATIONQUANTITEID().getStrCODEOPTIMISATION().equals("0"))) {
                qteReappro = getQuantityReapportByCodeGestionArticle(oFamilleStock, tf, emg);

            } else if (Objects.nonNull(tf.getIntQTEREAPPROVISIONNEMENT())   && Objects.nonNull( tf.getIntSEUILMIN()) && tf.getIntSEUILMIN() >= oFamilleStock.getIntNUMBERAVAILABLE()) {
                
                    qteReappro = (tf.getIntSEUILMIN() - oFamilleStock.getIntNUMBERAVAILABLE()) + tf.getIntQTEREAPPROVISIONNEMENT();

                
            }
        } catch (Exception e) {
        }

        return (qteReappro > 0 ? qteReappro : 1);
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
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementDetail_.strSTATUT), Constant.STATUT_IS_CLOSED)));
            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementDetail_.dtCREATED)), java.sql.Date.valueOf(dtDEBUT),
                    java.sql.Date.valueOf(dtFin));
            predicates.add(cb.and(btw));
            cq.where(cb.and(predicates.toArray(Predicate[]::new)));
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
            q.setParameter(2, Constant.STATUT_AUTO);
            q.setParameter(3, Constant.STATUT_IS_PROGRESS);
            q.setParameter(4, Constant.STATUT_PENDING);
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
            if (detail.getStrSTATUT().equals(Constant.STATUT_IS_PROGRESS)) {
                return 2;
            }
            if (detail.getStrSTATUT().equals(Constant.STATUT_PASSED)) {
                return 3;
            }
            if (detail.getStrSTATUT().equals(Constant.STATUT_IS_CLOSED)) {
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
                TSuggestionOrder suggestionOrder = createSuggestionOrder(OTGrossiste, Constant.STATUT_IS_PROGRESS);
                v.forEach(o -> {
                    TFamille OTFamille = getEmg().find(TFamille.class, o.getLgFAMILLEID());
                    initTSuggestionOrderDetail(suggestionOrder, OTFamille, OTGrossiste, o.getIntQUANTITY());
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
                TSuggestionOrder suggestionOrder = createSuggestionOrder(OTGrossiste, Constant.STATUT_IS_PROGRESS);
                v.forEach(o -> {
                    TFamille OTFamille = getEmg().find(TFamille.class, o.getLgFAMILLEID());
                    initTSuggestionOrderDetail(suggestionOrder, OTFamille, OTGrossiste, o.getIntQUANTITY());
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
                TGrossiste grossiste = getEmg().find(TGrossiste.class, k);
                TSuggestionOrder suggestionOrder = createSuggestionOrder(grossiste, Constant.STATUT_IS_PROGRESS);
                v.forEach(o -> {
                    TFamille otfamille = getEmg().find(TFamille.class, o.getId());
                    TFamilleStock familleStock = findStock(otfamille.getLgFAMILLEID(), u.getLgEMPLACEMENTID());
                    if (otfamille.getBoolDECONDITIONNE().compareTo(Short.valueOf("0")) == 0 && familleStock != null) {
                        initTSuggestionOrderDetail(suggestionOrder, otfamille, grossiste, (otfamille.getIntQTEREAPPROVISIONNEMENT() > 0 ? otfamille.getIntQTEREAPPROVISIONNEMENT() : 0));
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

    @Override
    public JSONObject findCHDetailStock(String idProduit, String emplacement) {
        JSONObject json = new JSONObject();
        try {
            TypedQuery<TFamilleStock> q = getEmg().createQuery("SELECT o FROM TFamilleStock o WHERE o.lgFAMILLEID.lgFAMILLEPARENTID=?1 AND o.lgEMPLACEMENTID.lgEMPLACEMENTID=?2", TFamilleStock.class);
            q.setParameter(1, idProduit);
            q.setParameter(2, emplacement);
            q.setMaxResults(1);
            TFamilleStock familleStock = q.getSingleResult();
            json.put("success", true);
            json.put("stock", familleStock.getIntNUMBERAVAILABLE());
            return json;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false);
            return json;
        }
    }

    @Override
    public void removeItem(String itemId) {
        TSuggestionOrderDetails item = getItem(itemId);
        TSuggestionOrder suggestion = item.getLgSUGGESTIONORDERID();
        if (CollectionUtils.isNotEmpty(suggestion.getTSuggestionOrderDetailsCollection()) && suggestion.getTSuggestionOrderDetailsCollection().size() == 1) {
            getEmg().remove(item);
            getEmg().remove(suggestion);
        } else {
            getEmg().remove(item);
            suggestion.setDtUPDATED(new Date());
            getEmg().persist(suggestion);
        }

    }

    @Override
    public SuggestionDTO getSuggestionAmount(String suggestionId) {
        long montantAchat = 0;
        long montantVente = 0;
        try {
            TSuggestionOrder order = getEmg().find(TSuggestionOrder.class, suggestionId);

            for (TSuggestionOrderDetails item : order.getTSuggestionOrderDetailsCollection()) {
                montantAchat += item.getIntPRICE();
                montantVente += ((long) item.getIntPRICEDETAIL() * item.getIntNUMBER());
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }
        return SuggestionDTO.builder()
                .montantAchat(montantAchat)
                .montantVente(montantVente)
                .build();

    }

    @Override
    public void addItem(SuggestionOrderDetailDTO suggestionOrderDetail) {

        Objects.requireNonNull(suggestionOrderDetail.getQte(), "La quantité ne doit pas être null");
        TSuggestionOrder order = getEmg().find(TSuggestionOrder.class, suggestionOrderDetail.getSuggestionId());
        TGrossiste grossiste = order.getLgGROSSISTEID();
        TFamille famille = getEmg().find(TFamille.class, suggestionOrderDetail.getFamilleId());
        TSuggestionOrderDetails suggestionOrderDetails = isProductExist(famille.getLgFAMILLEID(), order.getLgSUGGESTIONORDERID());
        if (Objects.isNull(suggestionOrderDetails)) {
            initTSuggestionOrderDetail(order, famille, grossiste, suggestionOrderDetail.getQte());
            famille.setIntORERSTATUS((short) 1);
            getEmg().merge(famille);
        } else {
            suggestionOrderDetails.setIntNUMBER(suggestionOrderDetail.getQte() + suggestionOrderDetails.getIntNUMBER());
            suggestionOrderDetails.setIntPRICE(suggestionOrderDetails.getIntNUMBER() * suggestionOrderDetails.getIntPAFDETAIL());
            getEmg().merge(suggestionOrderDetails);
        }

    }

    private TSuggestionOrderDetails isProductExist(String lgFamilleId, String suggId) {
        TSuggestionOrderDetails suggestionOrderDetails = null;
        try {
            suggestionOrderDetails = (TSuggestionOrderDetails) this.getEmg().createQuery("SELECT t FROM TSuggestionOrderDetails t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgSUGGESTIONORDERID.lgSUGGESTIONORDERID = ?2").
                    setParameter(1, lgFamilleId).
                    setParameter(2, suggId).setMaxResults(1).
                    getSingleResult();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());

        }
        return suggestionOrderDetails;
    }

    @Override
    public void updateItemSeuil(SuggestionOrderDetailDTO suggestionOrderDetail) {
        Objects.requireNonNull(suggestionOrderDetail.getSeuil(), "Le seuil ne doit pas être null");
        TSuggestionOrderDetails suggestionOrderDetails = getItem(suggestionOrderDetail.getItemId());
        if (Objects.nonNull(suggestionOrderDetails)) {
            TFamille famille = suggestionOrderDetails.getLgFAMILLEID();
            famille.setIntSEUILMIN(suggestionOrderDetail.getSeuil());
            famille.setDtUPDATED(new Date());
            getEmg().merge(famille);
        }

    }

    @Override
    public void updateItemQteCmde(SuggestionOrderDetailDTO suggestionOrderDetail) {
        Objects.requireNonNull(suggestionOrderDetail.getQte(), "Le quantité ne doit pas être null");
        TSuggestionOrderDetails suggestionOrderDetails = getItem(suggestionOrderDetail.getItemId());
        if (Objects.nonNull(suggestionOrderDetails)) {
            suggestionOrderDetails.setIntNUMBER(suggestionOrderDetail.getQte());
            suggestionOrderDetails.setIntPRICE(suggestionOrderDetail.getQte() * suggestionOrderDetails.getIntPAFDETAIL());
            suggestionOrderDetails.setDtUPDATED(new Date());
            getEmg().merge(suggestionOrderDetails);
        }

    }

    @Override
    public void updateItemQtePrixPaf(SuggestionOrderDetailDTO suggestionOrderDetail) {
        Objects.requireNonNull(suggestionOrderDetail.getPrixPaf(), "Le prix achat ne doit pas être null");
        TSuggestionOrderDetails suggestionOrderDetails = getItem(suggestionOrderDetail.getItemId());
        if (Objects.nonNull(suggestionOrderDetails)) {
            suggestionOrderDetails.setIntPAFDETAIL(suggestionOrderDetail.getPrixPaf());
            suggestionOrderDetails.setIntPRICE(suggestionOrderDetails.getIntNUMBER() * suggestionOrderDetails.getIntPAFDETAIL());
            suggestionOrderDetails.setDtUPDATED(new Date());
            getEmg().merge(suggestionOrderDetails);
        }
    }

    @Override
    public void updateItemQtePrixVente(SuggestionOrderDetailDTO suggestionOrderDetail) {
        Objects.requireNonNull(suggestionOrderDetail.getPrixVente(), "Le prix de vente ne doit pas être null");
        TSuggestionOrderDetails suggestionOrderDetails = getItem(suggestionOrderDetail.getItemId());
        if (Objects.nonNull(suggestionOrderDetails)) {
            suggestionOrderDetails.setIntPRICEDETAIL(suggestionOrderDetail.getPrixVente());
            suggestionOrderDetails.setDtUPDATED(new Date());
            getEmg().merge(suggestionOrderDetails);
        }
    }

    @Override
    public SuggestionDTO create(SuggestionDTO suggestion) {
        TGrossiste grossiste = this.getEmg().find(TGrossiste.class, suggestion.getGrossisteId());
        TSuggestionOrder suggestionOrder = createSuggestionOrder(grossiste, Constant.STATUT_IS_PROGRESS);
        TSuggestionOrderDetails details = addItem(suggestion.getItem(), suggestionOrder);
        return SuggestionDTO.builder()
                .montantAchat(details.getIntPRICE())
                .montantVente((long) details.getIntPRICEDETAIL() * details.getIntNUMBER())
                .build();
    }

    private TSuggestionOrderDetails addItem(SuggestionOrderDetailDTO suggestionOrderDetail, TSuggestionOrder order) {
        Objects.requireNonNull(suggestionOrderDetail.getQte(), "La quantité ne doit pas être null");
        TGrossiste grossiste = order.getLgGROSSISTEID();
        TFamille famille = getEmg().find(TFamille.class, suggestionOrderDetail.getFamilleId());
        famille.setIntORERSTATUS((short) 1);
        getEmg().merge(famille);
        return initTSuggestionOrderDetail(order, famille, grossiste, suggestionOrderDetail.getQte());

    }

    @Override
    public JSONObject fetch(String search, int start, int limit) {
        long count = count(search);
        return FunctionUtils.returnData(getSuggestions(search, start, limit), count);

    }

    private List<SuggestionsDTO> getSuggestions(String search, int start, int limit) {
        CriteriaBuilder cb = getEmg().getCriteriaBuilder();
        CriteriaQuery<TSuggestionOrder> cq = cb.createQuery(TSuggestionOrder.class);
        Root<TSuggestionOrder> root = cq.from(TSuggestionOrder.class);
        Join<TSuggestionOrder, TSuggestionOrderDetails> join = root.join(TSuggestionOrder_.tSuggestionOrderDetailsCollection);
        cq.select(root).distinct(true).orderBy(cb.desc(root.get(TSuggestionOrder_.dtUPDATED)));
        List<Predicate> predicates = listPredicates(cb, root, join, search);
        cq.where(cb.and(predicates.toArray(Predicate[]::new)));
        TypedQuery<TSuggestionOrder> q = getEmg().createQuery(cq);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        return q.getResultList().stream().map(this::buildSuggestionsDTO).collect(Collectors.toList());
    }

    private SuggestionsDTO buildSuggestionsDTO(TSuggestionOrder suggestionOrder) {
        int montantAchat = 0;
        int montantVente = 0;
        int nbreLigne = 0;
        int totalQty = 0;
        String items = " ";

        for (TSuggestionOrderDetails item : suggestionOrder.getTSuggestionOrderDetailsCollection()) {
            montantAchat += item.getIntPRICE();
            montantVente += (item.getIntPRICEDETAIL() * item.getIntNUMBER());
            nbreLigne++;
            totalQty += item.getIntNUMBER();
            int status = isOnAnotherSuggestion(item.getLgFAMILLEID());
            TFamille famille = item.getLgFAMILLEID();
            TFamilleGrossiste familleGrossiste = findFamilleGrossiste(famille.getLgFAMILLEID(), suggestionOrder.getLgGROSSISTEID().getLgGROSSISTEID());
            if (status == 1) {

                items += "<span style='background-color:#73C774;'> <b><span style='display:inline-block;width: 7%;'>" + (familleGrossiste != null ? familleGrossiste.getStrCODEARTICLE() : famille.getIntCIP()) + "</span><span style='display:inline-block;width: 25%;'>" + famille.getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + item.getIntNUMBER() + ")</span><span style='display:inline-block;width: 15%;'>" + NumberUtils.formatLongToString(item.getIntPAFDETAIL()) + " F CFA </span><span style='display:inline-block;width: 15%;'>" + NumberUtils.formatLongToString(item.getIntPRICEDETAIL()) + " F CFA " + "</span></b></span><br> ";
            } else if (status == 2) {
                items += "<span style='background-color:#5fa2dd;'> <b><span style='display:inline-block;width: 7%;'>" + (familleGrossiste != null ? familleGrossiste.getStrCODEARTICLE() : famille.getIntCIP()) + "</span><span style='display:inline-block;width: 25%;'>" + famille.getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + item.getIntNUMBER() + ")</span><span style='display:inline-block;width: 15%;'>" + NumberUtils.formatLongToString(item.getIntPAFDETAIL()) + " F CFA </span><span style='display:inline-block;width: 15%;'>" + NumberUtils.formatLongToString(item.getIntPRICEDETAIL()) + " F CFA " + "</span></b></span><br> ";
            } else {
                items += " <b><span style='display:inline-block;width: 7%;'>" + (familleGrossiste != null ? familleGrossiste.getStrCODEARTICLE() : famille.getIntCIP()) + "</span><span style='display:inline-block;width: 25%;'>" + famille.getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + item.getIntNUMBER() + ")</span><span style='display:inline-block;width: 15%;'>" + NumberUtils.formatLongToString(item.getIntPAFDETAIL()) + " F CFA </span><span style='display:inline-block;width: 15%;'>" + NumberUtils.formatLongToString(item.getIntPRICEDETAIL()) + " F CFA " + "</span></b><br> ";
            }

        }
        return new SuggestionsDTO(suggestionOrder, items, montantAchat, montantVente, nbreLigne, totalQty);
    }

    private TSuggestionOrderDetails getItem(String id) {
        return getEmg().find(TSuggestionOrderDetails.class, id);
    }

    private List<Predicate> listPredicates(CriteriaBuilder cb, Root<TSuggestionOrder> root, Join<TSuggestionOrder, TSuggestionOrderDetails> join, String search) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(root.get(TSuggestionOrder_.strSTATUT).in(Set.of(Constant.STATUT_IS_PROGRESS,
                Constant.STATUT_AUTO,
                Constant.STATUT_PENDING)));

        if (StringUtils.isNotEmpty(search)) {
            search = search + "%";
            predicates.add(cb.or(cb.like(root.get(TSuggestionOrder_.strREF), search),
                    cb.like(join.get(TSuggestionOrderDetails_.lgFAMILLEID).get(TFamille_.intCIP), search),
                    cb.like(join.get(TSuggestionOrderDetails_.lgFAMILLEID).get(TFamille_.strNAME), search)
            ));
        }
        return predicates;
    }

    private long count(String search) {
        CriteriaBuilder cb = getEmg().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TSuggestionOrder> root = cq.from(TSuggestionOrder.class);
        Join<TSuggestionOrder, TSuggestionOrderDetails> join = root.join(TSuggestionOrder_.tSuggestionOrderDetailsCollection);
        cq.select(cb.countDistinct(root));
        List<Predicate> predicates = listPredicates(cb, root,
                join, search);
        cq.where(cb.and(predicates.toArray(Predicate[]::new)));
        TypedQuery<Long> q = getEmg().createQuery(cq);
        return Objects.isNull(q.getSingleResult()) ? 0 : q.getSingleResult();

    }

    private TFamilleGrossiste findFamilleGrossiste(String familleId, String grossisteId) {

        try {
            Query qry = getEmg().createQuery("SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1 AND (t.lgGROSSISTEID.lgGROSSISTEID = ?2 OR t.lgGROSSISTEID.strDESCRIPTION = ?2) AND t.strSTATUT = ?3 ").
                    setParameter(1, familleId)
                    .setParameter(2, grossisteId)
                    .setParameter(3, Constant.STATUT_ENABLE);
            qry.setMaxResults(1);
            return (TFamilleGrossiste) qry.getSingleResult();

        } catch (Exception e) {
            return null;

        }

    }

    private int isOnAnotherSuggestion(TFamille lgFamilleID) {
        int status = (lgFamilleID.getIntORERSTATUS() == 2 ? 2 : 0);
        try {
            long count = (long) getEmg().createQuery("SELECT COUNT(o)  FROM TSuggestionOrderDetails o WHERE  o.lgFAMILLEID.lgFAMILLEID =?1 ").setParameter(1, lgFamilleID.getLgFAMILLEID())
                    .setMaxResults(1)
                    .getSingleResult();
            if (count > 1) {
                return 1;

            }
        } catch (Exception e) {

        }

        return status;
    }

    @Override
    public void setToPending(String id) {
        TSuggestionOrder order = this.getEmg().find(TSuggestionOrder.class, id);
        order.setStrSTATUT(Constant.STATUT_PENDING);
        this.getEmg().merge(order);

    }

}
