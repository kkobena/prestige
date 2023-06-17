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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.SuggestionService;
import rest.service.dto.SuggestionDTO;
import rest.service.dto.SuggestionOrderDetailDTO;

import util.Constant;
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
//                    oTCalendrier = getTCalendrier(String.valueOf(LocalDate.now().minusMonths(i)), LocalDate.now().getYear());
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
                    .setParameter(2, Constant.STATUT_AUTO);
            return q
                    .getSingleResult();

        } catch (Exception e) {
            return null;

        }

    }

    private TSuggestionOrderDetails isProductExistInSomeSuggestion(String lgFamilleId, String suggestionOrderId) {
        TSuggestionOrderDetails OTSuggestionOrderDetails = null;
        try {
            OTSuggestionOrderDetails = (TSuggestionOrderDetails) getEmg().createQuery("SELECT t FROM TSuggestionOrderDetails t WHERE t.lgFAMILLEID.lgFAMILLEID = ?1 AND t.lgSUGGESTIONORDERID.lgSUGGESTIONORDERID = ?2").
                    setParameter(1, lgFamilleId).
                    setParameter(2, suggestionOrderId).setMaxResults(1).
                    getSingleResult();

        } catch (Exception e) {

        }
        return OTSuggestionOrderDetails;
    }

    public TSuggestionOrderDetails addToTSuggestionOrderDetails(TFamille famille, TGrossiste oTGrossiste, TSuggestionOrder oTSuggestionOrder, int qteSuggere, EntityManager emg) {
        TSuggestionOrderDetails OTSuggestionOrderDetails = null;
        try {
            OTSuggestionOrderDetails = isProductExistInSomeSuggestion(famille.getLgFAMILLEID(), oTSuggestionOrder.getLgSUGGESTIONORDERID());
            if (OTSuggestionOrderDetails == null) {
                createTSuggestionOrderDetails(oTSuggestionOrder, famille, oTGrossiste, qteSuggere, emg);
            } else {
                OTSuggestionOrderDetails.setIntNUMBER(qteSuggere);
                OTSuggestionOrderDetails.setIntPRICE(qteSuggere * OTSuggestionOrderDetails.getIntPAFDETAIL());
                OTSuggestionOrderDetails.setDtUPDATED(new Date());

                emg.merge(OTSuggestionOrderDetails);
            }
            oTSuggestionOrder.setDtUPDATED(new Date());
            emg.merge(oTSuggestionOrder);

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
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void makeSuggestionAuto(List<TPreenregistrementDetail> list, TEmplacement emplacementId) {

        try {

            list.forEach(item -> {
                TFamille famille = item.getLgFAMILLEID();
                if (StringUtils.isNotEmpty(famille.getLgFAMILLEPARENTID()) && famille.getBoolDECONDITIONNE() == 1) {
                    famille = em.find(TFamille.class, famille.getLgFAMILLEPARENTID());
                }
                TFamilleStock familleStock = findStock(famille.getLgFAMILLEID(), emplacementId, getEmg());
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
            famille = em.find(TFamille.class, famille.getLgFAMILLEPARENTID());
        }
        TFamilleStock familleStock = findStock(famille.getLgFAMILLEID(), emplacementId, getEmg());
        if (familleStock != null) {
            makeSuggestionAuto(familleStock, famille);
        }

    }

    @Override
    public void makeSuggestionAuto(TFamilleStock OTFamilleStock, TFamille famille) {

        EntityManager emg = getEmg();
        if (Objects.nonNull(famille.getIntSEUILMIN()) && OTFamilleStock.getIntNUMBERAVAILABLE() <= famille.getIntSEUILMIN() && famille.getBoolDECONDITIONNE() == 0 && famille.getStrSTATUT().equals(DateConverter.STATUT_ENABLE)) {

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
                            OTSuggestionOrder = createSuggestionOrder(grossiste, Constant.STATUT_AUTO, emg);
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

    public TFamilleGrossiste findOrFamilleGrossiste(TFamille lg_FAMILLE_ID, TGrossiste lg_GROSSISTE_ID, EntityManager emg) {
        TFamilleGrossiste OTFamilleGrossiste;
        try {
            Query qry = emg.createQuery("SELECT DISTINCT t FROM TFamilleGrossiste t WHERE t.lgFAMILLEID.lgFAMILLEID LIKE ?1 AND (t.lgGROSSISTEID.lgGROSSISTEID = ?2 OR t.lgGROSSISTEID.strDESCRIPTION = ?2) AND t.strSTATUT = ?3 ").
                    setParameter(1, lg_FAMILLE_ID.getLgFAMILLEID())
                    .setParameter(2, lg_GROSSISTE_ID.getLgGROSSISTEID())
                    .setParameter(3, Constant.STATUT_ENABLE);
            qry.setMaxResults(1);
            OTFamilleGrossiste = (TFamilleGrossiste) qry.getSingleResult();

        } catch (Exception e) {
            OTFamilleGrossiste = new TFamilleGrossiste(UUID.randomUUID().toString());
            OTFamilleGrossiste.setLgFAMILLEID(lg_FAMILLE_ID);
            OTFamilleGrossiste.setLgGROSSISTEID(lg_GROSSISTE_ID);
            OTFamilleGrossiste.setDtCREATED(new Date());
            OTFamilleGrossiste.setDtUPDATED(OTFamilleGrossiste.getDtCREATED());
            OTFamilleGrossiste.setIntNBRERUPTURE(0);
            OTFamilleGrossiste.setBlRUPTURE(Boolean.TRUE);
            OTFamilleGrossiste.setStrCODEARTICLE(lg_FAMILLE_ID.getIntCIP());
            OTFamilleGrossiste.setIntPAF(lg_FAMILLE_ID.getIntPAF());
            OTFamilleGrossiste.setStrSTATUT(Constant.STATUT_ENABLE);
            OTFamilleGrossiste.setIntPRICE(lg_FAMILLE_ID.getIntPRICE());
            emg.persist(OTFamilleGrossiste);

        }

        return OTFamilleGrossiste;
    }

    private TSuggestionOrderDetails initTSuggestionOrderDetail(TSuggestionOrder OTSuggestionOrder, TFamille OTFamille, TGrossiste OTGrossiste, int int_NUMBER) {

        try {
            TFamilleGrossiste OTFamilleGrossiste = findOrFamilleGrossiste(OTFamille, OTGrossiste, this.getEmg());
            TSuggestionOrderDetails OTSuggestionOrderDetails = new TSuggestionOrderDetails();
            OTSuggestionOrderDetails.setLgSUGGESTIONORDERDETAILSID(UUID.randomUUID().toString());
            OTSuggestionOrderDetails.setLgSUGGESTIONORDERID(OTSuggestionOrder);
            OTSuggestionOrderDetails.setLgFAMILLEID(OTFamille);
            OTSuggestionOrderDetails.setLgGROSSISTEID(OTGrossiste);
            OTSuggestionOrderDetails.setIntNUMBER(int_NUMBER);
            OTSuggestionOrderDetails.setIntPRICE((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPAF() != null && OTFamilleGrossiste.getIntPAF() != 0) ? OTFamilleGrossiste.getIntPAF() * int_NUMBER : OTFamille.getIntPAF() * int_NUMBER);
            OTSuggestionOrderDetails.setIntPAFDETAIL((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPAF() != null && OTFamilleGrossiste.getIntPAF() != 0) ? OTFamilleGrossiste.getIntPAF() : OTFamille.getIntPAF());
            OTSuggestionOrderDetails.setIntPRICEDETAIL((OTFamilleGrossiste != null && OTFamilleGrossiste.getIntPRICE() != null && OTFamilleGrossiste.getIntPRICE() != 0) ? OTFamilleGrossiste.getIntPRICE() : OTFamille.getIntPRICE());
            OTSuggestionOrderDetails.setStrSTATUT(Constant.STATUT_IS_PROGRESS);
            OTSuggestionOrderDetails.setDtCREATED(new Date());
            OTSuggestionOrderDetails.setDtUPDATED(OTSuggestionOrderDetails.getDtCREATED());
            getEmg().persist(OTSuggestionOrderDetails);
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
        initTSuggestionOrderDetail(OTSuggestionOrder, OTFamille, OTGrossiste, int_NUMBER);

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
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            Query q = emg.createQuery(cq);
            return (Integer) q.getSingleResult();
        } catch (Exception e) {

        }
        return qty;
    }

    public Integer calcQteReappro(TFamilleStock OTFamilleStock, TFamille tf, EntityManager emg) {
        int qtyReappro = 1;
        try {
            TCodeGestion codeGestion = tf.getLgCODEGESTIONID();
            if (codeGestion != null && (!codeGestion.getLgOPTIMISATIONQUANTITEID().getStrCODEOPTIMISATION().equals("0"))) {
                qtyReappro = getQuantityReapportByCodeGestionArticle(OTFamilleStock, tf, emg);

            } else if (Objects.nonNull(tf.getIntQTEREAPPROVISIONNEMENT()) && Objects.nonNull(tf.getIntSEUILMIN()) && tf.getIntSEUILMIN() >= OTFamilleStock.getIntNUMBERAVAILABLE()) {

                qtyReappro = (tf.getIntSEUILMIN() - OTFamilleStock.getIntNUMBERAVAILABLE()) + tf.getIntQTEREAPPROVISIONNEMENT();

            }
        } catch (Exception e) {
        }

        return (qtyReappro > 0 ? qtyReappro : 1);
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
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
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
                TSuggestionOrder suggestionOrder = createSuggestionOrder(OTGrossiste, DateConverter.STATUT_PROCESS, getEmg());
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
                TSuggestionOrder suggestionOrder = createSuggestionOrder(OTGrossiste, DateConverter.STATUT_PROCESS, getEmg());
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
                TGrossiste OTGrossiste = getEmg().find(TGrossiste.class, k);
                TSuggestionOrder suggestionOrder = createSuggestionOrder(OTGrossiste, DateConverter.STATUT_PROCESS, getEmg());
                v.forEach(o -> {
                    TFamille OTFamille = getEmg().find(TFamille.class, o.getId());
                    TFamilleStock familleStock = findStock(OTFamille.getLgFAMILLEID(), u.getLgEMPLACEMENTID(), this.getEmg());
                    if (OTFamille.getBoolDECONDITIONNE().compareTo(Short.valueOf("0")) == 0 && familleStock != null) {
                        initTSuggestionOrderDetail(suggestionOrder, OTFamille, OTGrossiste, (OTFamille.getIntQTEREAPPROVISIONNEMENT() > 0 ? OTFamille.getIntQTEREAPPROVISIONNEMENT() : 0));
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
                montantVente += (item.getIntPRICEDETAIL() * item.getIntNUMBER());
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
            LOG.log(Level.SEVERE, " *** Desoleeeeeee OTSuggestionOrderDetails   5555 *** ", e);

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

    private TSuggestionOrderDetails getItem(String id) {
        return getEmg().find(TSuggestionOrderDetails.class, id);
    }
}
