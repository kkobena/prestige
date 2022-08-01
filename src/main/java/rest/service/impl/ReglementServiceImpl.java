/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import bll.common.Parameter;
import commonTasks.dto.ClotureVenteParams;
import commonTasks.dto.DelayedDTO;
import commonTasks.dto.Params;
import commonTasks.dto.ReglementCarnetDTO;
import dal.MvtTransaction;
import dal.Notification;
import dal.ReglementCarnet;
import dal.TCashTransaction;
import dal.TClient;
import dal.TClient_;
import dal.TCompteClient;
import dal.TCompteClient_;
import dal.TDossierReglement;
import dal.TDossierReglementDetail;
import dal.TEmplacement_;
import dal.TModeReglement;
import dal.TMotifReglement;
import dal.TMvtCaisse;
import dal.TPreenregistrementCompteClient;
import dal.TPreenregistrementCompteClient_;
import dal.TReglement;
import dal.TResumeCaisse;
import dal.TTiersPayant;
import dal.TTypeMvtCaisse;
import dal.TTypeReglement;
import dal.TUser;
import dal.TUser_;
import dal.enumeration.Canal;
import dal.enumeration.CategoryTransaction;
import dal.enumeration.TypeLog;
import dal.enumeration.TypeNotification;
import dal.enumeration.TypeTransaction;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.LogService;
import rest.service.NotificationService;
import rest.service.ReglementService;
import rest.service.TransactionService;
import rest.service.dto.DossierReglementDTO;
import util.DateConverter;

/**
 *
 * @author DICI
 */
@Stateless
public class ReglementServiceImpl implements ReglementService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEmg() {
        return em;
    }
    @EJB
    TransactionService transactionService;
    @EJB
    LogService logService;
    @EJB
    NotificationService notificationService;

    @Override
    public boolean checkCaisse(TUser ooTUser) {
        try {
            TypedQuery<TResumeCaisse> q = getEmg().createQuery("SELECT t FROM TResumeCaisse t WHERE t.lgUSERID.lgUSERID = ?1  AND t.strSTATUT = ?2 ", TResumeCaisse.class);
            q.setParameter(1, ooTUser.getLgUSERID())
                    .setParameter(2, DateConverter.STATUT_IS_IN_USE)
                    .setMaxResults(1);
            return (q.getSingleResult() != null);
        } catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
            return false;
        }
    }

    @Override
    public JSONObject listeDifferesData(Params params, boolean pairclient) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            List<DelayedDTO> list = listeDifferes(params, pairclient);
            json.put("total", list.size()).put("data", new JSONArray(list));
        } catch (Exception e) {
        }
        return json;

    }
    Comparator<DelayedDTO> comparator = Comparator.comparing(DelayedDTO::getDate);

    private TPreenregistrementCompteClient findById(String id) {
        return getEmg().find(TPreenregistrementCompteClient.class, id);
    }

    private TClient findClientById(String id) {
        try {
            return getEmg().find(TClient.class, id);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public List<DelayedDTO> listeDifferes(Params params, boolean pairclient) {
        try {
            EntityManager emg = this.getEmg();
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<TPreenregistrementCompteClient> cq = cb.createQuery(TPreenregistrementCompteClient.class);
            Root<TPreenregistrementCompteClient> root = cq.from(TPreenregistrementCompteClient.class);
            cq.select(root).orderBy(cb.desc(root.get(TPreenregistrementCompteClient_.dtUPDATED)));
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementCompteClient_.strSTATUT), DateConverter.STATUT_IS_CLOSED)));
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementCompteClient_.lgUSERID).get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID), params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID())));
            if (params.getDescription() != null) {
                predicates.add(cb.or(cb.like(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                        .get(TCompteClient_.lgCLIENTID).get(TClient_.strFIRSTNAME), params.getDescription() + "%"),
                        cb.like(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID).get(TClient_.strLASTNAME), params.getDescription() + "%"), cb.like(cb.concat(cb.concat(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID).get(TClient_.strFIRSTNAME), " "), root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID).get(TClient_.strLASTNAME)), params.getDescription() + "%")));
            }
            if (!pairclient) {
                if (params.getRef() != null) {
                    predicates.add(cb.and(cb.equal(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID).get(TClient_.lgCLIENTID), params.getRef())));
                }
            } else {
                if (params.getRef() != null) {
                    predicates.add(cb.and(cb.equal(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID).get(TClient_.lgCLIENTID), params.getRef())));
                }

            }

            Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementCompteClient_.dtUPDATED)), java.sql.Date.valueOf(params.getDtStart()),
                    java.sql.Date.valueOf(params.getDtEnd()));
            predicates.add(cb.and(btw));
            predicates.add(cb.and(cb.greaterThan(root.get(TPreenregistrementCompteClient_.intPRICERESTE), 0)));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<TPreenregistrementCompteClient> q = emg.createQuery(cq);
            return q.getResultList().stream().map(DelayedDTO::new).sorted(comparator).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    private TCompteClient getByClientId(String id) {
        TypedQuery<TCompteClient> q = getEmg().createQuery("SELECT t FROM TCompteClient t WHERE t.lgCLIENTID.lgCLIENTID = ?1", TCompteClient.class).setMaxResults(1)
                .setParameter(1, id);
        return q.getSingleResult();
    }

    private TModeReglement findByIdMod(String id) {
        return getEmg().find(TModeReglement.class, id);
    }

    private TModeReglement findModeReglement(String idTypeRegl) {
        TModeReglement modeReglement;
        switch (idTypeRegl) {
            case "1":
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
            default:
                modeReglement = findByIdMod("1");
                break;
        }
        return modeReglement;
    }

    private TMotifReglement findMotifReglement(String id) {
        return getEmg().find(TMotifReglement.class, id);
    }

    public void addtransactionComptant(TTypeMvtCaisse optionalCaisse, TMvtCaisse caisse, Integer int_AMOUNT, TCompteClient compteClient, Integer int_AMOUNT_REMIS, Integer int_AMOUNT_RECU, TReglement OTReglement, String lg_TYPE_REGLEMENT_ID, TUser user) {
        TCashTransaction cashTransaction = new TCashTransaction(UUID.randomUUID().toString());
        cashTransaction.setBoolCHECKED(Boolean.TRUE);
        cashTransaction.setDtCREATED(new Date());
        cashTransaction.setDtUPDATED(new Date());
        cashTransaction.setIntACCOUNT(int_AMOUNT);
        cashTransaction.setIntAMOUNT(int_AMOUNT);
        cashTransaction.setStrTYPE(Boolean.TRUE);
        cashTransaction.setStrDESCRIPTION("");
        cashTransaction.setLgTYPEREGLEMENTID(lg_TYPE_REGLEMENT_ID);
        cashTransaction.setLgUSERID(user);
        cashTransaction.setIntAMOUNT2(int_AMOUNT);
        cashTransaction.setStrTRANSACTIONREF(DateConverter.TRANSACTION_CREDIT);
        cashTransaction.setStrTASK(DateConverter.OTHER);
        cashTransaction.setStrNUMEROCOMPTE(optionalCaisse.getStrCODECOMPTABLE());
        cashTransaction.setLgREGLEMENTID(OTReglement);
        cashTransaction.setLgMOTIFREGLEMENTID(findMotifReglement(DateConverter.MOTIF_ENTREE_CAISSE));
        cashTransaction.setStrREFFACTURE(caisse.getLgMVTCAISSEID());
        cashTransaction.setStrRESSOURCEREF(caisse.getLgMVTCAISSEID());
        cashTransaction.setStrTYPEVENTE(DateConverter.OTHER);
        cashTransaction.setIntAMOUNTRECU(int_AMOUNT_RECU);
        cashTransaction.setIntAMOUNTCREDIT(int_AMOUNT);
        cashTransaction.setIntAMOUNTDEBIT(0);
        cashTransaction.setIntAMOUNTREMIS(int_AMOUNT_RECU - int_AMOUNT);
        cashTransaction.setCaissier(user);
        cashTransaction.setStrREFCOMPTECLIENT((compteClient != null ? compteClient.getLgCOMPTECLIENTID() : ""));
        getEmg().persist(cashTransaction);

    }

    public TMvtCaisse mvtCaisse(TTypeMvtCaisse OTTypeMvtCaisse, TUser u, TModeReglement modeReglement,
            String str_NUM_COMPTE, String str_NUM_PIECE_COMPTABLE, TReglement reglement, int int_AMOUNT, Date dt_DATE_MVT, String P_KEY) {
        TMvtCaisse OTMvtCaisse = new TMvtCaisse();
        OTMvtCaisse.setLgMVTCAISSEID(UUID.randomUUID().toString());
        OTMvtCaisse.setLgTYPEMVTCAISSEID(OTTypeMvtCaisse);
        OTMvtCaisse.setLgMODEREGLEMENTID(modeReglement);
        OTMvtCaisse.setStrNUMCOMPTE(str_NUM_COMPTE);
        OTMvtCaisse.setStrNUMPIECECOMPTABLE(str_NUM_PIECE_COMPTABLE);
        OTMvtCaisse.setIntAMOUNT(Double.valueOf(int_AMOUNT));
        OTMvtCaisse.setStrCOMMENTAIRE("");
        OTMvtCaisse.setStrSTATUT(DateConverter.STATUT_ENABLE);
        OTMvtCaisse.setDtDATEMVT(dt_DATE_MVT);
        OTMvtCaisse.setStrCREATEDBY(u);
        OTMvtCaisse.setDtCREATED(new Date());
        OTMvtCaisse.setPKey(P_KEY);
        OTMvtCaisse.setDtUPDATED(new Date());
        OTMvtCaisse.setStrREFTICKET(DateConverter.getShortId(10));
        OTMvtCaisse.setLgUSERID(u.getLgUSERID());
        OTMvtCaisse.setBoolCHECKED(true);
        getEmg().persist(OTMvtCaisse);
        return OTMvtCaisse;
    }

    private TDossierReglementDetail createDossierReglementDetail(String ref, TDossierReglement dossierReglement, int montant) {
        TDossierReglementDetail dossierReglementDetail = new TDossierReglementDetail(UUID.randomUUID().toString());
        dossierReglementDetail.setDblAMOUNT(Double.valueOf(montant));
        dossierReglementDetail.setLgDOSSIERREGLEMENTID(dossierReglement);
        dossierReglementDetail.setDtCREATED(dossierReglement.getDtCREATED());
        dossierReglementDetail.setStrREF(ref);
        dossierReglementDetail.setDtUPDATED(dossierReglement.getDtCREATED());
        dossierReglementDetail.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
        getEmg().persist(dossierReglementDetail);
        return dossierReglementDetail;
    }

    @Override
    public JSONObject reglerDiffereAll(ClotureVenteParams clotureVenteParams) throws JSONException {
        JSONObject json = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {

            if (checkCaisse(clotureVenteParams.getUserId())) {
                TTypeMvtCaisse OTTypeMvtCaisse = getEmg().find(TTypeMvtCaisse.class, Parameter.KEY_PARAM_MVT_REGLEMENT_DIFFERES);
                TCompteClient compteClient = getByClientId(clotureVenteParams.getClientId());
                List<TPreenregistrementCompteClient> listpreenregistrementCompteClient = getPreenregistrementCompteClients(clotureVenteParams.getUserVendeurId(), clotureVenteParams.getCompteClientId(), compteClient.getLgCOMPTECLIENTID());
                if (listpreenregistrementCompteClient.isEmpty()) {
                    return json.put("success", false).put("msg", "La lisse des vente est vide");
                }
                TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId());
                TDossierReglement dossierReglement = createDossierReglements(clotureVenteParams.getClientId(), clotureVenteParams.getUserId(),
                        clotureVenteParams.getMontantPaye(), "DIFFERE", dateFormat.parse(clotureVenteParams.getNatureVenteId()), clotureVenteParams.getTotalRecap());
                TReglement reglement = createTReglement(compteClient.getLgCOMPTECLIENTID(), clotureVenteParams.getUserId(),
                        dossierReglement.getLgDOSSIERREGLEMENTID(), clotureVenteParams.getBanque(),
                        clotureVenteParams.getLieux(), "", modeReglement, clotureVenteParams.getMontantPaye(), clotureVenteParams.getNom(), dateFormat.parse(clotureVenteParams.getNatureVenteId()));
                TMvtCaisse caisse = mvtCaisse(OTTypeMvtCaisse, clotureVenteParams.getUserId(),
                        modeReglement, OTTypeMvtCaisse.getStrCODECOMPTABLE(),
                        dossierReglement.getLgDOSSIERREGLEMENTID(), reglement,
                        clotureVenteParams.getMontantPaye(), dossierReglement.getDtREGLEMENT(), clotureVenteParams.getNom());
                addtransactionComptant(OTTypeMvtCaisse, caisse, clotureVenteParams.getMontantPaye(), compteClient, clotureVenteParams.getMontantRemis(), clotureVenteParams.getMontantRecu(), reglement, clotureVenteParams.getTypeRegleId(), clotureVenteParams.getUserId());
                String Description = "Reglement de différé  " + dossierReglement.getDblAMOUNT() + " Type de mouvement " + OTTypeMvtCaisse.getStrDESCRIPTION() + " PAR " + clotureVenteParams.getUserId().getStrFIRSTNAME() + " " + clotureVenteParams.getUserId().getStrLASTNAME();

                transactionService.addTransaction(clotureVenteParams.getUserId(),
                        clotureVenteParams.getUserId(), dossierReglement.getLgDOSSIERREGLEMENTID(),
                        clotureVenteParams.getMontantPaye(),
                        clotureVenteParams.getTotalRecap(), clotureVenteParams.getMontantPaye(), clotureVenteParams.getMontantRecu(), Boolean.TRUE, CategoryTransaction.CREDIT, TypeTransaction.ENTREE,
                        modeReglement.getLgTYPEREGLEMENTID(), OTTypeMvtCaisse, getEmg(),
                        clotureVenteParams.getMontantPaye(), 0, 0, caisse.getStrREFTICKET(), compteClient.getLgCLIENTID().getLgCLIENTID(), clotureVenteParams.getTotalRecap() - clotureVenteParams.getMontantPaye());
             
                listpreenregistrementCompteClient.forEach(a -> {
                    createDossierReglementDetail(a.getLgPREENREGISTREMENTCOMPTECLIENTID(), dossierReglement, a.getIntPRICERESTE());
                    a.setIntPRICERESTE(0);
                    a.setDtUPDATED(new Date());
                    getEmg().merge(a);
                });
                logService.updateItem(clotureVenteParams.getUserId(), caisse.getLgMVTCAISSEID(), Description,
                        TypeLog.MVT_DE_CAISSE, caisse, getEmg());
                notificationService.save(new Notification()
                        .canal(Canal.SMS_EMAIL)
                        .typeNotification(TypeNotification.MVT_DE_CAISSE)
                        .message(Description)
                        .addUser(clotureVenteParams.getUserId()));
                return json.put("success", true).put("msg", "Opération effectuée").put("ref", dossierReglement.getLgDOSSIERREGLEMENTID());
            }

            return json.put("success", false).put("msg", "Votre caisse est fermée");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return json;
    }
  @Override
    public JSONObject faireReglementCarnetDepot(ReglementCarnetDTO reglementCarnetDTO, TUser user) {
        JSONObject json = new JSONObject();
        if (!this.checkCaisse(user)) {
            return json.put("success", false).put("msg", "Votre caisse est fermée");
      }
      TTypeMvtCaisse OTTypeMvtCaisse = getEmg().find(TTypeMvtCaisse.class, DateConverter.MVT_REGLE_TP);
      TTypeReglement typeReglement = getEmg().find(TTypeReglement.class, reglementCarnetDTO.getTypeReglement());
       TModeReglement modeReglement= findModeReglement(typeReglement.getLgTYPEREGLEMENTID());
      TTiersPayant payant = getEmg().find(TTiersPayant.class, reglementCarnetDTO.getTiersPayantId());
        if (payant.getAccount().intValue() < reglementCarnetDTO.getMontantPaye()) {
            return json.put("success", false).put("msg", "VEUILLEZ SAISIR UN MONTANT EGAL OU INFERIEUR AU SOLDE");
        }
        ReglementCarnet carnet = new ReglementCarnet();
        carnet.setTypeReglement(typeReglement);
        carnet.setCreatedAt(LocalDateTime.now());
        carnet.setUser(user);
        carnet.setTiersPayant(payant);
        carnet.setDescription(reglementCarnetDTO.getDescription());
        carnet.setMontantPaye(reglementCarnetDTO.getMontantPaye());
        carnet.setMontantPayer(payant.getAccount().intValue());
        carnet.setMontantRestant(carnet.getMontantPayer() - carnet.getMontantPaye());
        getEmg().persist(carnet);
        payant.setAccount(payant.getAccount() - carnet.getMontantPaye());
        carnet.setReference(findLastReference() + 1);
          TDossierReglement dossierReglement = createDossierReglements(payant.getLgTIERSPAYANTID(),user,
                        carnet.getMontantPaye(), OTTypeMvtCaisse.getStrNAME(), new Date(),  carnet.getMontantPayer());
          carnet.setIdDossier(dossierReglement.getLgDOSSIERREGLEMENTID());
                TReglement reglement = createTReglement(payant.getLgTIERSPAYANTID(), user,
                        dossierReglement.getLgDOSSIERREGLEMENTID(),"",
                        "", "", modeReglement, carnet.getMontantPaye(), "", new Date());
                TMvtCaisse caisse = mvtCaisse(OTTypeMvtCaisse, user,
                        modeReglement, OTTypeMvtCaisse.getStrCODECOMPTABLE(),
                        dossierReglement.getLgDOSSIERREGLEMENTID(), reglement,
                        carnet.getMontantPaye(), dossierReglement.getDtREGLEMENT(), "");
                addtransactionComptant(OTTypeMvtCaisse, caisse,carnet.getMontantPaye(), null, 0, carnet.getMontantPaye(), reglement, typeReglement.getLgTYPEREGLEMENTID(), user);
                String Description = "Reglement de   " + dossierReglement.getDblAMOUNT() + " Type de mouvement " + OTTypeMvtCaisse.getStrDESCRIPTION() + " PAR " + user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
      transactionService.addTransaction(user,
              user, dossierReglement.getLgDOSSIERREGLEMENTID(),
              carnet.getMontantPaye(),
              carnet.getMontantPaye(), carnet.getMontantPaye(), 0, Boolean.TRUE, CategoryTransaction.CREDIT, TypeTransaction.ENTREE,
              modeReglement.getLgTYPEREGLEMENTID(), OTTypeMvtCaisse, getEmg(),
              carnet.getMontantPaye(), 0, 0, caisse.getStrREFTICKET(), payant.getLgTIERSPAYANTID(), 0);
      logService.updateItem(user, caisse.getLgMVTCAISSEID(), Description,
              TypeLog.MVT_DE_CAISSE, caisse, getEmg());
      notificationService.save(new Notification()
              .canal(Canal.SMS_EMAIL)
              .typeNotification(TypeNotification.MVT_DE_CAISSE)
              .message(Description)
              .addUser(user));
      getEmg().persist(carnet);
        getEmg().merge(payant);
      return json.put("success", true).put("msg", "Opération effectuée").put("ref", dossierReglement.getLgDOSSIERREGLEMENTID());
      
    }
        private int findLastReference() {
        try {
            TypedQuery<Integer> query = getEmg().createQuery("SELECT MAX(o.reference) FROM ReglementCarnet o ", Integer.class);
            return query.getSingleResult();
        } catch (Exception e) {
         
            return 0;
        }
    }
    @Override
    public JSONObject reglerDiffere(ClotureVenteParams clotureVenteParams) throws JSONException {
        System.out.println("clotureVenteParams  " + clotureVenteParams);
        JSONObject json = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (checkCaisse(clotureVenteParams.getUserId())) {
                TTypeMvtCaisse OTTypeMvtCaisse = getEmg().find(TTypeMvtCaisse.class, Parameter.KEY_PARAM_MVT_REGLEMENT_DIFFERES);
                TCompteClient compteClient = getByClientId(clotureVenteParams.getClientId());
                JSONArray array = new JSONArray(clotureVenteParams.getCommentaire());
                if (array.isEmpty()) {
                    return json.put("success", false).put("msg", "Veuillez séléction au moins un dossier");
                }
                TModeReglement modeReglement = findModeReglement(clotureVenteParams.getTypeRegleId());
                TDossierReglement dossierReglement = createDossierReglements(clotureVenteParams.getClientId(), clotureVenteParams.getUserId(),
                        clotureVenteParams.getMontantPaye(), "DIFFERE", dateFormat.parse(clotureVenteParams.getNatureVenteId()), clotureVenteParams.getTotalRecap());
                TReglement reglement = createTReglement(compteClient.getLgCOMPTECLIENTID(), clotureVenteParams.getUserId(),
                        dossierReglement.getLgDOSSIERREGLEMENTID(), clotureVenteParams.getBanque(),
                        clotureVenteParams.getLieux(), "", modeReglement, clotureVenteParams.getMontantPaye(), clotureVenteParams.getNom(), dateFormat.parse(clotureVenteParams.getNatureVenteId()));
                TMvtCaisse caisse = mvtCaisse(OTTypeMvtCaisse, clotureVenteParams.getUserId(),
                        modeReglement, OTTypeMvtCaisse.getStrCODECOMPTABLE(),
                        dossierReglement.getLgDOSSIERREGLEMENTID(), reglement,
                        clotureVenteParams.getMontantPaye(), dossierReglement.getDtREGLEMENT(), clotureVenteParams.getNom());
                addtransactionComptant(OTTypeMvtCaisse, caisse, clotureVenteParams.getMontantPaye(), compteClient, clotureVenteParams.getMontantRemis(), clotureVenteParams.getMontantRecu(), reglement, clotureVenteParams.getTypeRegleId(), clotureVenteParams.getUserId());
                String Description = "Reglement de différé  " + dossierReglement.getDblAMOUNT() + " Type de mouvement " + OTTypeMvtCaisse.getStrDESCRIPTION() + " PAR " + clotureVenteParams.getUserId().getStrFIRSTNAME() + " " + clotureVenteParams.getUserId().getStrLASTNAME();
                transactionService.addTransaction(clotureVenteParams.getUserId(),
                        clotureVenteParams.getUserId(), dossierReglement.getLgDOSSIERREGLEMENTID(),
                        clotureVenteParams.getMontantPaye(),
                        clotureVenteParams.getTotalRecap(), clotureVenteParams.getMontantPaye(), clotureVenteParams.getMontantRecu(), Boolean.TRUE, CategoryTransaction.CREDIT, TypeTransaction.ENTREE,
                        modeReglement.getLgTYPEREGLEMENTID(), OTTypeMvtCaisse, getEmg(),
                        clotureVenteParams.getMontantPaye(), 0, 0, caisse.getStrREFTICKET(), compteClient.getLgCLIENTID().getLgCLIENTID(), clotureVenteParams.getTotalRecap() - clotureVenteParams.getMontantPaye());
                LongAdder montant = new LongAdder();
                montant.add(clotureVenteParams.getMontantPaye());
                array.forEach(a -> {
                    TPreenregistrementCompteClient tp = getEmg().find(TPreenregistrementCompteClient.class, a);
                    int _m = tp.getIntPRICERESTE();
                    int total = (int) montant.sumThenReset();
                    if (total > 0 && total >= _m) {
                        createDossierReglementDetail(tp.getLgPREENREGISTREMENTCOMPTECLIENTID(), dossierReglement, tp.getIntPRICERESTE());
                        tp.setIntPRICERESTE(0);
                        tp.setDtUPDATED(new Date());
                        getEmg().merge(tp);
                        total -= _m;
                        montant.add(total);
                    } else if (total > 0 && total < _m) {
                        createDossierReglementDetail(tp.getLgPREENREGISTREMENTCOMPTECLIENTID(), dossierReglement, total);
                        tp.setIntPRICERESTE(_m - total);
                        tp.setDtUPDATED(new Date());
                        getEmg().merge(tp);
                        montant.reset();
                    }
                });
                logService.updateItem(clotureVenteParams.getUserId(), caisse.getLgMVTCAISSEID(), Description,
                        TypeLog.MVT_DE_CAISSE, caisse, getEmg());
                  notificationService.save(new Notification()
                        .canal(Canal.SMS_EMAIL)
                        .typeNotification(TypeNotification.MVT_DE_CAISSE)
                        .message(Description)
                        .addUser(clotureVenteParams.getUserId()));
                return json.put("success", true).put("msg", "Opération effectuée").put("ref", dossierReglement.getLgDOSSIERREGLEMENTID());
            }

            return json.put("success", false).put("msg", "Votre caisse est fermée");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return json;
    }

    public TReglement createTReglement(String str_REF_COMPTE_CLIENT, TUser u, String str_REF_RESSOURCE, String str_BANQUE, String str_LIEU, String str_COMMENTAIRE, TModeReglement OTModeReglement, int int_AMOUNT, String nom, Date dt_reglement) {
        TReglement OTReglement = new TReglement();
        OTReglement.setLgREGLEMENTID(UUID.randomUUID().toString());
        OTReglement.setStrBANQUE(str_BANQUE);
        OTReglement.setStrCODEMONNAIE("FR");
        OTReglement.setStrCOMMENTAIRE(str_COMMENTAIRE);
        OTReglement.setStrLIEU(str_LIEU);
        OTReglement.setStrFIRSTLASTNAME(nom);
        OTReglement.setStrREFRESSOURCE(str_REF_RESSOURCE);
        OTReglement.setIntTAUX(0);
        OTReglement.setDtCREATED(new Date());
        OTReglement.setDtUPDATED(new Date());
        OTReglement.setLgMODEREGLEMENTID(OTModeReglement);
        OTReglement.setDtREGLEMENT(dt_reglement);
        OTReglement.setLgUSERID(u);
        OTReglement.setBoolCHECKED(true);
        OTReglement.setBISFACTURE(Boolean.FALSE);
        OTReglement.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
        getEmg().persist(OTReglement);
        return OTReglement;
    }

    private TDossierReglement createDossierReglements(String lg_CLIENT_ID, TUser u, Integer amount, String nature_dossier, Date dt_reglement, Integer montantattendu) {

        TDossierReglement OTDossierReglement = new TDossierReglement();
        OTDossierReglement.setLgDOSSIERREGLEMENTID(UUID.randomUUID().toString());
        OTDossierReglement.setDblAMOUNT(Double.valueOf(amount));
        OTDossierReglement.setLgUSERID(u);
        OTDossierReglement.setStrNATUREDOSSIER(nature_dossier);
        OTDossierReglement.setStrORGANISMEID(lg_CLIENT_ID);
        OTDossierReglement.setDtREGLEMENT(dt_reglement);
        OTDossierReglement.setDtCREATED(new Date());
        OTDossierReglement.setDtUPDATED(new Date());
        OTDossierReglement.setDblMONTANTATTENDU(Double.valueOf(montantattendu));
        OTDossierReglement.setStrSTATUT(DateConverter.STATUT_IS_CLOSED);
        getEmg().persist(OTDossierReglement);

        return OTDossierReglement;

    }

    public List<TPreenregistrementCompteClient> getPreenregistrementCompteClients(String dtDebut, String dtFin, String cmpt) {

        EntityManager emg = this.getEmg();
        List<Predicate> predicates = new ArrayList<>();
        CriteriaBuilder cb = emg.getCriteriaBuilder();
        CriteriaQuery<TPreenregistrementCompteClient> cq = cb.createQuery(TPreenregistrementCompteClient.class);
        Root<TPreenregistrementCompteClient> root = cq.from(TPreenregistrementCompteClient.class);
        cq.select(root);
        predicates.add(cb.and(cb.equal(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID).get(TCompteClient_.lgCOMPTECLIENTID), cmpt)));
        Predicate btw = cb.between(cb.function("DATE", Date.class, root.get(TPreenregistrementCompteClient_.dtUPDATED)), java.sql.Date.valueOf(dtDebut),
                java.sql.Date.valueOf(dtFin));
        predicates.add(cb.and(btw));
        predicates.add(cb.and(cb.equal(root.get(TPreenregistrementCompteClient_.strSTATUT), DateConverter.STATUT_IS_CLOSED)));
        predicates.add(cb.and(cb.greaterThan(root.get(TPreenregistrementCompteClient_.intPRICERESTE), 0)));
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<TPreenregistrementCompteClient> q = emg.createQuery(cq);
        return q.getResultList();

    }

    private List<MvtTransaction> listeReglement(LocalDate dtStart, LocalDate dtEnd, boolean checked,
            String emplacementId, String typeMvt) {
        try {
            TypedQuery<MvtTransaction> query = getEmg().createQuery(
                    "SELECT o FROM MvtTransaction o WHERE o.mvtDate BETWEEN ?1 AND ?2 AND o.magasin.lgEMPLACEMENTID=?3 AND o.checked=?4 AND o.tTypeMvtCaisse.lgTYPEMVTCAISSEID=?5 ORDER BY o.createdAt ASC ",
                    MvtTransaction.class);
            query.setParameter(1, dtStart);
            query.setParameter(2, dtEnd);
            query.setParameter(3, emplacementId);
            query.setParameter(4, checked);
            query.setParameter(5, typeMvt);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject reglementsDifferes(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId, String clientId) throws JSONException {
        List<DelayedDTO> list = reglementsDifferesDto(dtStart, dtEnd, checked, emplacementId, clientId);
        return new JSONObject().put("total", list.size()).put("data", new JSONArray(list));
    }

    @Override
    public List<DelayedDTO> reglementsDifferesDto(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId, String clientId) {
        try {
            if (clientId != null && !"".equals(clientId)) {
                TClient client = findClientById(clientId);
                List<MvtTransaction> query = listeReglement(dtStart, dtEnd, checked, emplacementId, DateConverter.MVT_REGLE_DIFF);
                return query.stream().map(x -> new DelayedDTO(x, client)).filter(x -> x.getClientId().equals(clientId)).collect(Collectors.toList());
            }
            List<MvtTransaction> query = listeReglement(dtStart, dtEnd, checked, emplacementId, DateConverter.MVT_REGLE_DIFF);
            return query.stream().map(x -> new DelayedDTO(x, findClientById(x.getOrganisme()))).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    private List<DelayedDTO> detailsReglmentDifferes(String refReglement) {
        try {
            TypedQuery<TDossierReglementDetail> q = getEmg().createQuery("SELECT o FROM TDossierReglementDetail o WHERE o.lgDOSSIERREGLEMENTID.lgDOSSIERREGLEMENTID=?1", TDossierReglementDetail.class);
            q.setParameter(1, refReglement);
            return q.getResultList().stream().map(x -> new DelayedDTO(x, findById(x.getStrREF()))).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject detailsReglmentDiffere(String refReglement) throws JSONException {
        List<DelayedDTO> list = detailsReglmentDifferes(refReglement);
        return new JSONObject().put("total", list.size()).put("data", new JSONArray(list));
    }

    
   
    @Override
    public List<DossierReglementDTO> listeReglementFactures(String dtStart, String dtEnd, String tiersPayantId) {
      
        if(StringUtils.isNotEmpty(tiersPayantId)){
      TTiersPayant payant=      getEmg().find(TTiersPayant.class, tiersPayantId);
             TypedQuery<TDossierReglement> q=getEmg().createQuery("SELECT o FROM TDossierReglement o WHERE FUNCTION('DATE',o.dtREGLEMENT) BETWEEN ?1 AND ?2 AND o.strORGANISMEID=?3", TDossierReglement.class);
       q.setParameter(1, java.sql.Date.valueOf(dtStart), TemporalType.DATE);
        q.setParameter(2, java.sql.Date.valueOf(dtEnd), TemporalType.DATE);
           q.setParameter(3, tiersPayantId);
        return  q.getResultList().stream().map(e->new DossierReglementDTO(e, payant)).collect(Collectors.toList());
        }
       TypedQuery<TDossierReglement> q=getEmg().createQuery("SELECT o FROM TDossierReglement o WHERE FUNCTION('DATE',o.dtREGLEMENT) BETWEEN ?1 AND ?2 AND o.lgFACTUREID IS NOT NULL", TDossierReglement.class);
       q.setParameter(1, java.sql.Date.valueOf(dtStart), TemporalType.DATE);
        q.setParameter(2, java.sql.Date.valueOf(dtEnd), TemporalType.DATE);
        return  q.getResultList().stream().map(e->new DossierReglementDTO(e, getEmg().find(TTiersPayant.class, e.getStrORGANISMEID()))).collect(Collectors.toList());
    }

}
