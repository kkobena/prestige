/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.AyantDroitDTO;
import commonTasks.dto.ClientDTO;
import commonTasks.dto.ClientLambdaDTO;
import commonTasks.dto.TiersPayantDTO;
import commonTasks.dto.TiersPayantParams;
import commonTasks.dto.VenteTiersPayantsDTO;
import dal.TAyantDroit;
import dal.TAyantDroit_;
import dal.TCategorieAyantdroit;
import dal.TClient;
import dal.TClient_;
import dal.TCompteClient;
import dal.TCompteClientTiersPayant;
import dal.TCompteClientTiersPayant_;
import dal.TCompteClient_;
import dal.TEmplacement_;
import dal.TGroupeTierspayant;
import dal.TGroupeTierspayant_;
import dal.TModelFacture;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClient;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementCompteClientTiersPayent_;
import dal.TPreenregistrementCompteClient_;
import dal.TPreenregistrement_;
import dal.TRemise;
import dal.TRisque;
import dal.TTiersPayant;
import dal.TTiersPayant_;
import dal.TTypeClient;
import dal.TTypeTiersPayant;
import dal.TTypeTiersPayant_;
import dal.TUser_;
import dal.TVille;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.ClientService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author Kobena
 */
@Stateless
public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = Logger.getLogger(ClientServiceImpl.class.getName());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEmg() {
        return em;

    }

    @Override
    public JSONObject createClient(ClientLambdaDTO clientLambda, String venteId) throws JSONException {
        try {
            TPreenregistrement tp = getEmg().find(TPreenregistrement.class, venteId);
            TClient tc = new TClient(UUID.randomUUID().toString());
            tc.setStrADRESSE(clientLambda.getStrADRESSE());
            tc.setLgTYPECLIENTID(new TTypeClient(clientLambda.getLgTYPECLIENTID()));
            tc.setStrFIRSTNAME(clientLambda.getStrFIRSTNAME());
            tc.setStrLASTNAME(clientLambda.getStrLASTNAME());
            tc.setStrSEXE(clientLambda.getStrSEXE());
            tc.setStrSTATUT("enable");
            tc.setDtUPDATED(new Date());
            tc.setDtCREATED(new Date());
            tc.setEmail(clientLambda.getEmail());
            tc.setStrCODEINTERNE(DateConverter.getShortId(6));
            getEmg().persist(tc);
            createCompteClient(tc, getEmg());
            tp.setClient(tc);
            tp.setStrFIRSTNAMECUSTOMER(tc.getStrFIRSTNAME());
            tp.setStrLASTNAMECUSTOMER(tc.getStrLASTNAME());
            tp.setStrPHONECUSTOME(clientLambda.getStrADRESSE());
            getEmg().merge(tp);
            return new JSONObject().put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            // emg.getTransaction().rollback();
            return new JSONObject().put("success", false).put("msg",
                    "La mise à jour des infos du client n'a pas abouti");
        }
    }

    @Override
    public TClient createClient(ClientLambdaDTO clientLambda) {
        EntityManager emg = this.getEmg();
        try {
            TClient tc = new TClient(UUID.randomUUID().toString());
            tc.setStrADRESSE(clientLambda.getStrADRESSE());
            tc.setLgTYPECLIENTID(new TTypeClient(clientLambda.getLgTYPECLIENTID()));
            tc.setStrFIRSTNAME(clientLambda.getStrFIRSTNAME());
            tc.setStrLASTNAME(clientLambda.getStrLASTNAME());
            tc.setStrSEXE(clientLambda.getStrSEXE());
            tc.setStrSTATUT("enable");
            tc.setDtUPDATED(new Date());
            tc.setDtCREATED(new Date());
            tc.setStrCODEINTERNE(DateConverter.getShortId(6));
            tc.setEmail(clientLambda.getEmail());
            emg.persist(tc);
            createCompteClient(tc, emg);
            return tc;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    public TCompteClient createCompteClient(TClient tc, EntityManager emg) {
        TCompteClient OTCompteClient = new TCompteClient();

        OTCompteClient.setLgCOMPTECLIENTID(UUID.randomUUID().toString());
        OTCompteClient.setStrCODECOMPTECLIENT("");
        // OTCompteClient.setDblQUOTACONSOMENSUELLE(dbl_QUOTA_CONSO_MENSUELLE); // a decommenter en cas de probleme.
        // 17/08/2016
        OTCompteClient.setDblQUOTACONSOMENSUELLE(0.0); // forcer l'initialisation de la consommation a 0. La
                                                       // consommation du quota evolue au fur et a mesure de vente
        OTCompteClient.setDblPLAFOND(-1.0); // code ajouté
        OTCompteClient.setPKey("");
        OTCompteClient.setDblCAUTION(-1.0);
        OTCompteClient.setDecBalanceInDisponible(0);
        OTCompteClient.setDecbalanceDisponible(0);
        OTCompteClient.setStrTYPE("");
        OTCompteClient.setStrSTATUT(commonparameter.statut_enable);
        OTCompteClient.setDtCREATED(new Date());
        OTCompteClient.setLgCLIENTID(tc);
        OTCompteClient.setDtUPDATED(new Date());
        emg.persist(OTCompteClient);
        return OTCompteClient;
    }

    @Override
    public List<ClientLambdaDTO> findClientLambda(String query) {
        try {
            EntityManager emg = this.getEmg();
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<ClientLambdaDTO> cq = cb.createQuery(ClientLambdaDTO.class);
            Root<TClient> root = cq.from(TClient.class);
            cq.select(cb.construct(ClientLambdaDTO.class, root.get(TClient_.lgCLIENTID),
                    root.get(TClient_.strFIRSTNAME), root.get(TClient_.strLASTNAME), root.get(TClient_.strADRESSE),
                    root.get(TClient_.lgTYPECLIENTID).get("lgTYPECLIENTID"), root.get(TClient_.strSEXE),
                    root.get(TClient_.email))).orderBy(cb.asc(root.get(TClient_.strFIRSTNAME)));
            predicates.add(cb.and(cb.equal(root.get(TClient_.strSTATUT), "enable")));
            predicates.add(cb.and(cb.equal(root.get(TClient_.lgTYPECLIENTID).get("lgTYPECLIENTID"),
                    commonparameter.STANDART_CLIENT_ID)));

            if (query != null && !query.equals("")) {
                predicates.add(cb.or(cb.like(root.get(TClient_.strFIRSTNAME), query + "%"),
                        cb.like(root.get(TClient_.strLASTNAME), query + "%"),
                        cb.like(cb.concat(cb.concat(root.get(TClient_.strFIRSTNAME), " "),
                                root.get(TClient_.strLASTNAME)), query + "%")));
            }

            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ClientDTO> findClientAssurance(String query, String typeClientId) {
        try {
            EntityManager emg = this.getEmg();
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<TClient> cq = cb.createQuery(TClient.class);
            Root<TClient> root = cq.from(TClient.class);
            cq.select(root).orderBy(cb.asc(root.get(TClient_.strFIRSTNAME)));
            predicates.add(cb.and(cb.equal(root.get(TClient_.strSTATUT), "enable")));
            if (!StringUtils.isEmpty(typeClientId)) {
                predicates.add(cb.and(cb.equal(root.get(TClient_.lgTYPECLIENTID).get("lgTYPECLIENTID"), typeClientId)));
            }
            if (!StringUtils.isEmpty(query)) {
                predicates.add(cb.or(cb.like(root.get(TClient_.strNUMEROSECURITESOCIAL), query + "%"),
                        cb.like(root.get(TClient_.strFIRSTNAME), query + "%"),
                        cb.like(cb.concat(cb.concat(root.get(TClient_.strFIRSTNAME), " "),
                                root.get(TClient_.strLASTNAME)), query + "%")));// ,
                                                                                // cb.like(root.get(TClient_.strLASTNAME),
                                                                                // query + "%"),
                                                                                // cb.like(cb.concat(cb.concat(root.get(TClient_.strFIRSTNAME),
                                                                                // " "),
                                                                                // root.get(TClient_.strLASTNAME)),
                                                                                // query + "%")));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            q.setMaxResults(100);
            List<TClient> resultat = q.getResultList();

            return resultat.stream().map(cl -> new ClientDTO(cl, findTiersPayantByClientId(cl.getLgCLIENTID(), emg),
                    findAyantDroitByClientId(cl.getLgCLIENTID(), emg))).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    Comparator<TiersPayantParams> comparator = Comparator.comparingInt(TiersPayantParams::getOrder);

    private List<TiersPayantParams> findTiersPayantByClientId(String clientId, EntityManager emg) {
        try {
            TypedQuery<TCompteClientTiersPayant> query = emg.createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1 AND o.strSTATUT=?2",
                    TCompteClientTiersPayant.class);
            query.setParameter(1, clientId);
            query.setParameter(2, DateConverter.STATUT_ENABLE);
            return query.getResultList().stream().map(TiersPayantParams::new).sorted(comparator)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<TiersPayantParams> findTiersPayantByClientId(String clientId) {
        try {
            TypedQuery<TCompteClientTiersPayant> query = getEmg().createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1",
                    TCompteClientTiersPayant.class);
            query.setParameter(1, clientId);
            return query.getResultList().stream().map(TiersPayantParams::new).sorted(comparator)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<TiersPayantParams> findTiersPayantByClientIdExcludeRo(String clientId) {
        try {
            TypedQuery<TCompteClientTiersPayant> query = this.getEmg().createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1 AND o.intPRIORITY >1",
                    TCompteClientTiersPayant.class);
            query.setParameter(1, clientId);
            return query.getResultList().stream().map(TiersPayantParams::new).sorted(comparator)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private List<AyantDroitDTO> findAyantDroitByClientId(String clientId, EntityManager emg) {
        try {
            TypedQuery<TAyantDroit> query = emg
                    .createQuery("SELECT o FROM TAyantDroit o WHERE o.lgCLIENTID.lgCLIENTID=?1", TAyantDroit.class);
            query.setParameter(1, clientId);
            return query.getResultList().stream().map(AyantDroitDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<TiersPayantDTO> findTiersPayants(String query, String type) {
        try {
            EntityManager emg = this.getEmg();
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<TiersPayantDTO> cq = cb.createQuery(TiersPayantDTO.class);
            Root<TTiersPayant> root = cq.from(TTiersPayant.class);
            cq.select(cb.construct(TiersPayantDTO.class, root.get(TTiersPayant_.lgTIERSPAYANTID),
                    root.get(TTiersPayant_.strNAME), root.get(TTiersPayant_.strFULLNAME)))
                    .orderBy(cb.asc(root.get(TTiersPayant_.strNAME)));
            predicates.add(cb.and(cb.equal(root.get(TTiersPayant_.strSTATUT), "enable")));
            if (type != null && !"".equals(type)) {
                predicates.add(cb.equal(
                        root.get(TTiersPayant_.lgTYPETIERSPAYANTID).get(TTypeTiersPayant_.lgTYPETIERSPAYANTID), type));
            }

            if (query != null && !query.equals("")) {
                predicates.add(cb.or(cb.like(root.get(TTiersPayant_.strCODEORGANISME), query + "%"),
                        cb.like(root.get(TTiersPayant_.strNAME), query + "%"),
                        cb.like(root.get(TTiersPayant_.strFULLNAME), query + "%")));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            return q.getResultList();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    private TClient findById(String id, EntityManager emg) {
        try {
            return emg.find(TClient.class, id);

        } catch (Exception e) {
            return null;
        }
    }

    private TClient updateClientCarnet(ClientDTO client, TClient tc, TCompteClientTiersPayant oltp, EntityManager emg) {
        try {
            tc = updateClient(client, tc);
            updateCompteClient(client, tc, emg);
            findAndUpdate(oltp, client, emg);
            return tc;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }

    private TRemise findRemiseById(String idRemise) {
        try {
            if (idRemise == null) {
                return null;
            }
            return getEmg().find(TRemise.class, idRemise);
        } catch (Exception e) {
            return null;
        }
    }

    private TClient createClientCarnet(ClientDTO client, TClient tc, TTiersPayant p, EntityManager emg) {

        try {
            tc = createClient(client, emg);
            TCompteClient compteClient = createCompteClient(client, tc, emg);
            createComptClientTierspayant(client, compteClient, emg, p);
            return tc;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private boolean doesNumeroSecuriteSocialExist(String str_NUMERO_SECURITE_SOCIAL, TTiersPayant payant) {
        try {
            TypedQuery<TCompteClientTiersPayant> query = this.getEmg().createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.strNUMEROSECURITESOCIAL =?1 AND o.lgTIERSPAYANTID.lgTIERSPAYANTID=?2 ",
                    TCompteClientTiersPayant.class);
            query.setParameter(1, str_NUMERO_SECURITE_SOCIAL);
            query.setParameter(2, payant.getLgTIERSPAYANTID());
            return !(query.getResultList().isEmpty());
        } catch (Exception e) {
            return false;
        }
    }

    private TClient updateClient(ClientDTO clientDTO, TClient client) {

        if (client == null) {
            client = getEmg().find(TClient.class, clientDTO.getLgCLIENTID());
        }
        client.setStrSEXE(clientDTO.getStrSEXE());
        client.setStrSTATUT(commonparameter.statut_enable);
        client.setStrNUMEROSECURITESOCIAL(clientDTO.getStrNUMEROSECURITESOCIAL());
        client.setStrLASTNAME(clientDTO.getStrLASTNAME().toUpperCase());
        client.setStrFIRSTNAME(clientDTO.getStrFIRSTNAME().toUpperCase());
        client.setDtUPDATED(new Date());
        client.setStrCODEPOSTAL(clientDTO.getStrCODEPOSTAL());
        client.setStrADRESSE(clientDTO.getStrADRESSE());
        client.setLgVILLEID(findVilleById(clientDTO.getLgVILLEID(), getEmg()));
        client.setRemise(findRemiseById(clientDTO.getRemiseId()));
        try {
            client.setDtNAISSANCE(dateFormat.parse(clientDTO.getDtNAISSANCE()));
        } catch (ParseException e) {
        }
        getEmg().merge(client);
        return client;
    }

    private TClient createClient(ClientDTO clientDTO, EntityManager emg) {

        TClient client = new TClient(UUID.randomUUID().toString());
        client.setDtCREATED(new Date());
        client.setStrSEXE(clientDTO.getStrSEXE());
        client.setStrSTATUT(commonparameter.statut_enable);
        client.setStrNUMEROSECURITESOCIAL(clientDTO.getStrNUMEROSECURITESOCIAL());
        client.setStrLASTNAME(clientDTO.getStrLASTNAME().toUpperCase());
        client.setStrFIRSTNAME(clientDTO.getStrFIRSTNAME().toUpperCase());
        client.setDtUPDATED(new Date());
        client.setStrCODEPOSTAL(clientDTO.getStrCODEPOSTAL());
        client.setStrADRESSE(clientDTO.getStrADRESSE());
        client.setLgTYPECLIENTID(findTypeClientById(clientDTO.getLgTYPECLIENTID(), emg));
        client.setLgVILLEID(findVilleById(clientDTO.getLgVILLEID(), emg));
        client.setStrCODEINTERNE(DateConverter.getShortId(6));
        client.setRemise(findRemiseById(clientDTO.getRemiseId()));

        try {
            client.setDtNAISSANCE(dateFormat.parse(clientDTO.getDtNAISSANCE()));
        } catch (ParseException e) {
        }
        emg.persist(client);
        return client;
    }

    private TVille findVilleById(String id, EntityManager emg) {
        try {
            return emg.find(TVille.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    private TTypeClient findTypeClientById(String id, EntityManager emg) {
        try {
            return emg.find(TTypeClient.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    private TTiersPayant findTiersPayantById(String id, EntityManager emg) {
        try {
            return emg.find(TTiersPayant.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    private TCategorieAyantdroit findCateAyantById(String id, EntityManager emg) {
        try {
            return emg.find(TCategorieAyantdroit.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    private TRisque findRisqueById(String id, EntityManager emg) {
        try {
            return emg.find(TRisque.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    private TAyantDroit findAyantDroitByNum(String num, EntityManager emg) {
        try {
            return emg.createNamedQuery("TAyantDroit.findByStrNUMEROSECURITESOCIAL", TAyantDroit.class).setMaxResults(1)
                    .setParameter("strNUMEROSECURITESOCIAL", num).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private void updateAyantDroit(ClientDTO clientDTO, TClient client, String oldNum, EntityManager emg) {
        TAyantDroit ayantDroit = findAyantDroitByNum(oldNum, emg);
        ayantDroit.setDtUPDATED(new Date());
        ayantDroit.setStrSTATUT(commonparameter.statut_enable);
        ayantDroit.setLgRISQUEID(findRisqueById(clientDTO.getLgRISQUEID(), emg));
        ayantDroit.setLgCATEGORIEAYANTDROITID(findCateAyantById(clientDTO.getLgCATEGORIEAYANTDROITID(), emg));
        ayantDroit.setStrSEXE(clientDTO.getStrSEXE());
        ayantDroit.setStrFIRSTNAME(clientDTO.getStrFIRSTNAME().toUpperCase());
        ayantDroit.setStrLASTNAME(clientDTO.getStrLASTNAME().toUpperCase());
        ayantDroit.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        try {
            ayantDroit.setDtNAISSANCE(dateFormat.parse(clientDTO.getDtNAISSANCE()));
        } catch (Exception e) {
        }
        emg.persist(ayantDroit);
    }

    private TCompteClient createCompteClient(ClientDTO clientDTO, TClient tc, EntityManager emg) {
        TCompteClient OTCompteClient = new TCompteClient();
        OTCompteClient.setLgCOMPTECLIENTID(UUID.randomUUID().toString());
        OTCompteClient.setStrCODECOMPTECLIENT("");

        OTCompteClient.setDblQUOTACONSOMENSUELLE(clientDTO.getDblQUOTACONSOMENSUELLE().doubleValue());
        OTCompteClient.setDblPLAFOND(-1.0);
        if (clientDTO.getDbPLAFONDENCOURS() > 0) {
            OTCompteClient.setDblPLAFOND(clientDTO.getDbPLAFONDENCOURS().doubleValue());
        }
        OTCompteClient.setPKey(tc.getLgCLIENTID());
        OTCompteClient.setDblCAUTION(-1.0);
        OTCompteClient.setDecBalanceInDisponible(0);
        OTCompteClient.setDecbalanceDisponible(0);
        OTCompteClient.setDecBalance(0.0);
        OTCompteClient.setStrTYPE("CLIENT");
        OTCompteClient.setStrSTATUT(commonparameter.statut_enable);
        OTCompteClient.setDtCREATED(new Date());
        OTCompteClient.setLgCLIENTID(tc);
        OTCompteClient.setDtUPDATED(new Date());
        OTCompteClient.setDtEffective(new Date());
        emg.persist(OTCompteClient);
        return OTCompteClient;
    }

    private TCompteClient findByClientId(String clientId, EntityManager emg) {
        try {
            return (TCompteClient) emg.createQuery("SELECT o FROM TCompteClient o WHERE o.lgCLIENTID.lgCLIENTID=?1 ")
                    .setParameter(1, clientId).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private TCompteClient updateCompteClient(ClientDTO clientDTO, TClient tc, EntityManager emg) {
        TCompteClient OTCompteClient = findByClientId(tc.getLgCLIENTID(), emg);
        OTCompteClient.setStrCODECOMPTECLIENT("");
        OTCompteClient.setDblQUOTACONSOMENSUELLE(clientDTO.getDblQUOTACONSOMENSUELLE().doubleValue());
        OTCompteClient.setDblPLAFOND(-1.0);
        if (clientDTO.getDbPLAFONDENCOURS() > 0) {

            OTCompteClient.setDblPLAFOND(clientDTO.getDbPLAFONDENCOURS().doubleValue());
        }
        OTCompteClient.setPKey(tc.getLgCLIENTID());
        OTCompteClient.setDblCAUTION(-1.0);
        OTCompteClient.setDecBalanceInDisponible(0);
        OTCompteClient.setDecbalanceDisponible(0);
        OTCompteClient.setStrTYPE("");
        OTCompteClient.setStrSTATUT(commonparameter.statut_enable);
        OTCompteClient.setDtUPDATED(new Date());

        emg.merge(OTCompteClient);
        return OTCompteClient;
    }

    private void createComptClientTierspayant(ClientDTO cdto, TCompteClient OTCompteClient, EntityManager emg,
            TTiersPayant p) {
        TCompteClientTiersPayant OTCompteClientTiersPayant = new TCompteClientTiersPayant(UUID.randomUUID().toString());
        OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
        OTCompteClientTiersPayant.setStrNUMEROSECURITESOCIAL(cdto.getStrNUMEROSECURITESOCIAL());
        OTCompteClientTiersPayant.setBISRO(Boolean.TRUE);
        OTCompteClientTiersPayant.setBCANBEUSE(Boolean.TRUE);
        OTCompteClientTiersPayant.setBIsAbsolute(cdto.isbIsAbsolute());
        OTCompteClientTiersPayant.setDtCREATED(new Date());
        OTCompteClientTiersPayant.setDtUPDATED(new Date());
        OTCompteClientTiersPayant.setLgCOMPTECLIENTID(OTCompteClient);
        OTCompteClientTiersPayant.setLgTIERSPAYANTID(p);
        OTCompteClientTiersPayant.setIntPOURCENTAGE(cdto.getIntPOURCENTAGE());
        OTCompteClientTiersPayant.setIntPRIORITY(cdto.getIntPRIORITY());
        OTCompteClientTiersPayant.setDbPLAFONDENCOURS(0);
        OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE(0);
        OTCompteClientTiersPayant.setDblPLAFOND(0.0);
        OTCompteClientTiersPayant.setDblQUOTACONSOVENTE(0.0);
        OTCompteClientTiersPayant.setIsCapped(Boolean.FALSE);
        if (cdto.getDbPLAFONDENCOURS() > 0) {
            OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
            OTCompteClientTiersPayant.setDbPLAFONDENCOURS(cdto.getDbPLAFONDENCOURS());
        }
        if (cdto.getDblQUOTACONSOMENSUELLE() > 0) {
            OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
            OTCompteClientTiersPayant.setDblPLAFOND(Double.valueOf(cdto.getDblQUOTACONSOMENSUELLE()));
        }
        emg.persist(OTCompteClientTiersPayant);
    }

    private void createComptClientTierspayant(List<TiersPayantParams> tiersPayants, TCompteClient OTCompteClient,
            EntityManager emg) {
        tiersPayants.forEach(p -> {
            TCompteClientTiersPayant OTCompteClientTiersPayant = new TCompteClientTiersPayant(
                    UUID.randomUUID().toString());
            OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
            OTCompteClientTiersPayant.setStrNUMEROSECURITESOCIAL(p.getNumSecurity());
            OTCompteClientTiersPayant.setBCANBEUSE(Boolean.TRUE);
            OTCompteClientTiersPayant.setBIsAbsolute(p.isbIsAbsolute());
            OTCompteClientTiersPayant.setDtCREATED(new Date());
            OTCompteClientTiersPayant.setDtUPDATED(new Date());
            OTCompteClientTiersPayant.setLgCOMPTECLIENTID(OTCompteClient);
            OTCompteClientTiersPayant.setLgTIERSPAYANTID(findTiersPayantById(p.getLgTIERSPAYANTID(), emg));
            OTCompteClientTiersPayant.setIntPOURCENTAGE(p.getTaux());
            OTCompteClientTiersPayant.setIntPRIORITY(p.getOrder());
            OTCompteClientTiersPayant.setDbPLAFONDENCOURS(0);
            OTCompteClientTiersPayant.setDblPLAFOND(0.0);
            OTCompteClientTiersPayant.setDblQUOTACONSOVENTE(0.0);
            OTCompteClientTiersPayant.setIsCapped(Boolean.FALSE);
            OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE(0);
            if (p.getDbPLAFONDENCOURS() > 0) {
                OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
                OTCompteClientTiersPayant.setDbPLAFONDENCOURS(p.getDbPLAFONDENCOURS());
            }
            if (p.getDblQUOTACONSOMENSUELLE() > 0) {
                OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
                OTCompteClientTiersPayant.setDblPLAFOND(Double.valueOf(p.getDblQUOTACONSOMENSUELLE()));
            }
            emg.persist(OTCompteClientTiersPayant);

        });
    }

    private void findAndUpdate(TCompteClientTiersPayant OTCompteClientTiersPayant, ClientDTO cdto, EntityManager emg) {
        OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
        OTCompteClientTiersPayant.setStrNUMEROSECURITESOCIAL(cdto.getStrNUMEROSECURITESOCIAL());
        OTCompteClientTiersPayant.setBISRO(Boolean.TRUE);
        OTCompteClientTiersPayant.setBIsAbsolute(cdto.isbIsAbsolute());
        OTCompteClientTiersPayant.setDtUPDATED(new Date());
        OTCompteClientTiersPayant.setLgTIERSPAYANTID(findTiersPayantById(cdto.getLgTIERSPAYANTID(), emg));
        OTCompteClientTiersPayant.setIntPOURCENTAGE(cdto.getIntPOURCENTAGE());
        OTCompteClientTiersPayant.setIntPRIORITY(cdto.getIntPRIORITY());
        OTCompteClientTiersPayant.setDbPLAFONDENCOURS(0);
        OTCompteClientTiersPayant.setIsCapped(Boolean.FALSE);
        OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE(0);
        if (cdto.getDbPLAFONDENCOURS() > 0) {
            OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
            OTCompteClientTiersPayant.setDbPLAFONDENCOURS(cdto.getDbPLAFONDENCOURS());
        }
        if (cdto.getDblQUOTACONSOMENSUELLE() > 0) {
            OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
            OTCompteClientTiersPayant.setDblPLAFOND(Double.valueOf(cdto.getDblQUOTACONSOMENSUELLE()));
        }
        emg.merge(OTCompteClientTiersPayant);
    }

    private void updateComptClientTierspayant(ClientDTO cdto, TTiersPayant p) {
        TCompteClientTiersPayant OTCompteClientTiersPayant = getEmg().find(TCompteClientTiersPayant.class,
                cdto.getCompteTp());
        OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
        OTCompteClientTiersPayant.setStrNUMEROSECURITESOCIAL(cdto.getStrNUMEROSECURITESOCIAL());
        OTCompteClientTiersPayant.setBISRO(Boolean.TRUE);
        OTCompteClientTiersPayant.setBIsAbsolute(cdto.isbIsAbsolute());
        OTCompteClientTiersPayant.setDtUPDATED(new Date());
        OTCompteClientTiersPayant.setLgTIERSPAYANTID(p);
        OTCompteClientTiersPayant.setIntPOURCENTAGE(cdto.getIntPOURCENTAGE());
        OTCompteClientTiersPayant.setIntPRIORITY(cdto.getIntPRIORITY());
        OTCompteClientTiersPayant.setDbPLAFONDENCOURS(0);
        OTCompteClientTiersPayant.setIsCapped(Boolean.FALSE);
        OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE(0);
        if (cdto.getDbPLAFONDENCOURS() > 0) {
            OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
            OTCompteClientTiersPayant.setDbPLAFONDENCOURS(cdto.getDbPLAFONDENCOURS());
        }
        if (cdto.getDblQUOTACONSOMENSUELLE() > 0) {
            OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
            OTCompteClientTiersPayant.setDblPLAFOND(Double.valueOf(cdto.getDblQUOTACONSOMENSUELLE()));
        }
        getEmg().merge(OTCompteClientTiersPayant);
    }

    private TCompteClientTiersPayant findTCompteClientTiersPayantById(String id, EntityManager emg) {
        try {
            return emg.find(TCompteClientTiersPayant.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateComptClientTierspayant(List<TiersPayantParams> tiersPayants, TCompteClient OTCompteClient,
            EntityManager emg) {
        tiersPayants.forEach(p -> {
            TCompteClientTiersPayant OTCompteClientTiersPayant = findTCompteClientTiersPayantById(p.getCompteTp(), emg);
            if (OTCompteClientTiersPayant == null) {
                OTCompteClientTiersPayant = new TCompteClientTiersPayant(UUID.randomUUID().toString());
                OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
                OTCompteClientTiersPayant.setStrNUMEROSECURITESOCIAL(p.getNumSecurity());
                OTCompteClientTiersPayant.setBCANBEUSE(Boolean.TRUE);
                OTCompteClientTiersPayant.setBIsAbsolute(p.isbIsAbsolute());
                OTCompteClientTiersPayant.setDtCREATED(new Date());
                OTCompteClientTiersPayant.setDtUPDATED(new Date());
                OTCompteClientTiersPayant.setLgCOMPTECLIENTID(OTCompteClient);
                OTCompteClientTiersPayant.setLgTIERSPAYANTID(findTiersPayantById(p.getLgTIERSPAYANTID(), emg));
                OTCompteClientTiersPayant.setIntPOURCENTAGE(p.getTaux());
                OTCompteClientTiersPayant.setIntPRIORITY(p.getOrder());
                OTCompteClientTiersPayant.setDbPLAFONDENCOURS(0);
                OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE(0);
                OTCompteClientTiersPayant.setIsCapped(Boolean.FALSE);
                if (p.getDbPLAFONDENCOURS() > 0) {
                    OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
                    OTCompteClientTiersPayant.setDbPLAFONDENCOURS(p.getDbPLAFONDENCOURS());
                }
                if (p.getDblQUOTACONSOMENSUELLE() > 0) {
                    OTCompteClientTiersPayant.setIsCapped(Boolean.TRUE);
                    OTCompteClientTiersPayant.setDblPLAFOND(Double.valueOf(p.getDblQUOTACONSOMENSUELLE()));
                }
                emg.persist(OTCompteClientTiersPayant);
            }

        });
    }

    private boolean compteClientTpHasSales(String id, EntityManager emg) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> tq = emg.createQuery(
                    "SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTTIERSPAYANTID=?1 AND o.strSTATUT='enable'",
                    TPreenregistrementCompteClientTiersPayent.class);
            tq.setParameter(1, id);
            return !tq.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void createAyantDroit(ClientDTO clientDTO, TClient client, EntityManager emg) {
        TAyantDroit ayantDroit = new TAyantDroit(client.getLgCLIENTID());
        ayantDroit.setDtCREATED(new Date());
        ayantDroit.setDtUPDATED(new Date());
        ayantDroit.setStrSTATUT(commonparameter.statut_enable);
        ayantDroit.setLgCLIENTID(client);
        ayantDroit.setLgRISQUEID(findRisqueById(clientDTO.getLgRISQUEID(), emg));
        ayantDroit.setLgCATEGORIEAYANTDROITID(findCateAyantById(clientDTO.getLgCATEGORIEAYANTDROITID(), emg));
        ayantDroit.setStrSEXE(clientDTO.getStrSEXE());
        ayantDroit.setStrFIRSTNAME(clientDTO.getStrFIRSTNAME());
        ayantDroit.setStrLASTNAME(clientDTO.getStrLASTNAME());
        ayantDroit.setStrCODEINTERNE(client.getStrCODEINTERNE());
        ayantDroit.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        try {
            ayantDroit.setDtNAISSANCE(dateFormat.parse(clientDTO.getDtNAISSANCE()));
        } catch (Exception e) {
        }
        emg.persist(ayantDroit);
    }

    private TCompteClientTiersPayant findByClientTiersPayantId(String clientId, String tiersPayantId,
            EntityManager emg) {
        try {
            return emg.createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1 AND o.strNUMEROSECURITESOCIAL =?2 ",
                    TCompteClientTiersPayant.class).setParameter(1, clientId).setParameter(2, tiersPayantId)
                    .setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private TCompteClientTiersPayant findByClientTiersPayantId(String clientId, String statut, int priority) {
        try {
            return getEmg().createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1 AND o.strSTATUT =?2 AND o.intPRIORITY=?3 ",
                    TCompteClientTiersPayant.class).setParameter(1, clientId).setParameter(2, statut)
                    .setParameter(3, priority).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param clientId
     * @param query
     *
     * @return
     */
    @Override
    public List<AyantDroitDTO> findAyantDroitByClientId(String clientId, String query) {
        try {
            EntityManager emg = this.getEmg();
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<TAyantDroit> cq = cb.createQuery(TAyantDroit.class);
            Root<TAyantDroit> root = cq.from(TAyantDroit.class);
            cq.select(root).orderBy(cb.asc(root.get(TAyantDroit_.strFIRSTNAME)));
            predicates.add(cb.and(cb.equal(root.get(TAyantDroit_.strSTATUT), "enable")));
            predicates.add(cb.and(cb.equal(root.get(TAyantDroit_.lgCLIENTID).get(TClient_.lgCLIENTID), clientId)));
            if (query != null && !query.equals("")) {
                predicates.add(cb.or(cb.like(root.get(TAyantDroit_.strFIRSTNAME), query + "%"),
                        cb.like(root.get(TAyantDroit_.strLASTNAME), query + "%"),
                        cb.like(root.get(TAyantDroit_.strNUMEROSECURITESOCIAL), query + "%")));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            Query q = emg.createQuery(cq);
            List<TAyantDroit> ayantDroits = q.getResultList();
            return ayantDroits.stream().map(AyantDroitDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject addAyantDroitToClient(AyantDroitDTO dto) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {
            TAyantDroit ayantDroit = new TAyantDroit(UUID.randomUUID().toString());
            ayantDroit.setDtCREATED(new Date());
            ayantDroit.setDtUPDATED(new Date());
            ayantDroit.setStrSTATUT(commonparameter.statut_enable);
            ayantDroit.setLgCLIENTID(findById(dto.getLgCLIENTID(), emg));
            ayantDroit.setLgRISQUEID(findRisqueById(dto.getLgRISQUEID(), emg));
            ayantDroit.setLgCATEGORIEAYANTDROITID(findCateAyantById(dto.getLgCATEGORIEAYANTDROITID(), emg));
            ayantDroit.setStrSEXE(dto.getStrSEXE());
            ayantDroit.setStrFIRSTNAME(dto.getStrFIRSTNAME());
            ayantDroit.setStrLASTNAME(dto.getStrLASTNAME());
            ayantDroit.setStrCODEINTERNE(DateConverter.getShortId(6));
            ayantDroit.setStrNUMEROSECURITESOCIAL(dto.getStrNUMEROSECURITESOCIAL());
            try {
                ayantDroit.setDtNAISSANCE(dateFormat.parse(dto.getDtNAISSANCE()));
            } catch (Exception e) {
            }
            emg.persist(ayantDroit);
            AyantDroitDTO data = new AyantDroitDTO(ayantDroit);
            LOG.log(Level.INFO, "{0}", data);
            json.put("success", true).put("data", new JSONObject(data));
            return json;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "L'ajout de l'ayant droit a échoué");
            return json;
        }
    }

    @Override
    public JSONObject findClientAssuranceById(String clientId, String venteId) throws JSONException {
        try {
            EntityManager emg = this.getEmg();
            TClient cl = emg.find(TClient.class, clientId);
            return new JSONObject().put("success", true).put("data",
                    new JSONObject(new ClientDTO(cl, findTiersPayantByClientId(clientId, emg),
                            ventesAssuranceByClientId(clientId, venteId, emg),
                            findAyantDroitByClientId(clientId, emg))));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "-- findClientAssuranceById ---- ", e);
            return new JSONObject().put("success", false).put("msg", "Client avec cet idendifient n'existe pas ");
        }
    }

    private List<TiersPayantParams> ventesAssuranceByClientId(String clientId, String venteId, EntityManager emg) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> tq = emg.createQuery(
                    "SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1 AND o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?2  ",
                    TPreenregistrementCompteClientTiersPayent.class).setParameter(1, clientId).setParameter(2, venteId);
            return tq.getResultList().stream().map(TiersPayantParams::new).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ClientDTO> clientDifferes(String query, String empl) {
        try {
            EntityManager emg = this.getEmg();
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<TClient> cq = cb.createQuery(TClient.class);
            Root<TPreenregistrementCompteClient> root = cq.from(TPreenregistrementCompteClient.class);
            cq.select(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID))
                    .distinct(true);
            predicates.add(cb.and(
                    cb.equal(root.get(TPreenregistrementCompteClient_.strSTATUT), DateConverter.STATUT_IS_CLOSED)));
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementCompteClient_.lgUSERID)
                    .get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID), empl)));
            predicates.add(cb.and(cb.greaterThan(root.get(TPreenregistrementCompteClient_.intPRICERESTE), 0)));
            if (query != null && !query.equals("")) {
                predicates
                        .add(cb.or(
                                cb.like(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                                        .get(TCompteClient_.lgCLIENTID).get(TClient_.strFIRSTNAME), query + "%"),
                                cb.like(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                                        .get(TCompteClient_.lgCLIENTID).get(TClient_.strLASTNAME), query + "%"),
                                cb.like(cb.concat(
                                        cb.concat(
                                                root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                                                        .get(TCompteClient_.lgCLIENTID).get(TClient_.strFIRSTNAME),
                                                " "),
                                        root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                                                .get(TCompteClient_.lgCLIENTID).get(TClient_.strLASTNAME)),
                                        query + "%")));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<TClient> q = emg.createQuery(cq);
            return q.getResultList().stream().map(ClientDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ClientDTO> clientDiffere(String query, String empl) {
        try {
            EntityManager emg = this.getEmg();
            List<Predicate> predicates = new ArrayList<>();
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<TClient> cq = cb.createQuery(TClient.class);
            Root<TPreenregistrementCompteClient> root = cq.from(TPreenregistrementCompteClient.class);
            cq.select(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID).get(TCompteClient_.lgCLIENTID))
                    .distinct(true);
            predicates.add(cb.and(
                    cb.equal(root.get(TPreenregistrementCompteClient_.strSTATUT), DateConverter.STATUT_IS_CLOSED)));
            predicates.add(cb.and(cb.equal(root.get(TPreenregistrementCompteClient_.lgUSERID)
                    .get(TUser_.lgEMPLACEMENTID).get(TEmplacement_.lgEMPLACEMENTID), empl)));

            if (query != null && !query.equals("")) {
                predicates
                        .add(cb.or(
                                cb.like(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                                        .get(TCompteClient_.lgCLIENTID).get(TClient_.strFIRSTNAME), query + "%"),
                                cb.like(root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                                        .get(TCompteClient_.lgCLIENTID).get(TClient_.strLASTNAME), query + "%"),
                                cb.like(cb.concat(
                                        cb.concat(
                                                root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                                                        .get(TCompteClient_.lgCLIENTID).get(TClient_.strFIRSTNAME),
                                                " "),
                                        root.get(TPreenregistrementCompteClient_.lgCOMPTECLIENTID)
                                                .get(TCompteClient_.lgCLIENTID).get(TClient_.strLASTNAME)),
                                        query + "%")));
            }
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<TClient> q = emg.createQuery(cq);
            return q.getResultList().stream().map(ClientDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    /*
     * modification du client à la vente avec un tiers payant different
     */
    private TClient updateClientCarnet(ClientDTO client, TClient tc, TTiersPayant p, TCompteClientTiersPayant old) {
        try {
            tc = updateClient(client, tc);
            TCompteClient OTCompteClient = updateCompteClient(client, tc, getEmg());
            createComptClientTierspayant(client, OTCompteClient, getEmg(), p);
            desabledCompteClientTiersPayant(old);
            return tc;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }

    private void desabledCompteClientTiersPayant(TCompteClientTiersPayant OTCompteClientTiersPayant) {
        OTCompteClientTiersPayant.setStrSTATUT(DateConverter.STATUT_DELETE);
        OTCompteClientTiersPayant.setIntPRIORITY(-1);
        getEmg().merge(OTCompteClientTiersPayant);
    }

    @Override
    public JSONObject updateOrCreateClientAssurance(ClientDTO client) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        try {
            TClient tc = findById(client.getLgCLIENTID(), emg);
            TTiersPayant p = findTiersPayantById(client.getLgTIERSPAYANTID(), emg);
            if (p == null) {
                json.put("success", false).put("msg", "Veuillez sélectionner un tiers-payant valide");
                return json;
            }
            if (tc == null) {
                if (!StringUtils.isEmpty(client.getStrNUMEROSECURITESOCIAL())) {
                    if (doesNumeroSecuriteSocialExist(client.getStrNUMEROSECURITESOCIAL(), p)) {
                        json.put("success", false).put("msg",
                                "Le numéro de sécurité :: [<span style=\"color: blue; \"> "
                                        + client.getStrNUMEROSECURITESOCIAL()
                                        + " </span>] est déjà utilisé dans le système");
                        return json;
                    }
                }

                tc = createClient(client, emg);
                TCompteClient compteClient = createCompteClient(client, tc, emg);
                createAyantDroit(client, tc, emg);
                createComptClientTierspayant(client, compteClient, emg, p);
                createComptClientTierspayant(client.getTiersPayants(), compteClient, emg);
            } else {
                if (!StringUtils.isEmpty(client.getStrNUMEROSECURITESOCIAL())) {
                    if (!client.getStrNUMEROSECURITESOCIAL().trim().equals(tc.getStrNUMEROSECURITESOCIAL().trim())) {
                        if (doesNumeroSecuriteSocialExist(client.getStrNUMEROSECURITESOCIAL(), p)) {
                            json.put("success", false).put("msg",
                                    "Le numéro de sécurité :: [<span style=\"color: blue; \"> "
                                            + client.getStrNUMEROSECURITESOCIAL()
                                            + " </span>] est déjà utilisé dans le système");
                            return json;
                        }
                    }
                }
                String oldNum = tc.getStrNUMEROSECURITESOCIAL();
                TCompteClientTiersPayant oltp = findByClientTiersPayantId(client.getLgCLIENTID(),
                        DateConverter.STATUT_ENABLE, DateConverter.TIERS_PAYANT_PRINCIPAL);
                TTiersPayant oldltp = oltp.getLgTIERSPAYANTID();
                tc = updateClient(client, tc);
                TCompteClient OTCompteClient = updateCompteClient(client, tc, emg);
                if (!oldltp.equals(p)) {
                    createComptClientTierspayant(client, OTCompteClient, emg, p);
                    desabledCompteClientTiersPayant(oltp);
                } else {
                    updateComptClientTierspayant(client, p);
                }

                updateAyantDroit(client, tc, oldNum, emg);

                updateComptClientTierspayant(client.getTiersPayants(), OTCompteClient, emg);
            }
            ClientDTO data = new ClientDTO(findById(tc.getLgCLIENTID(), emg),
                    findTiersPayantByClientId(tc.getLgCLIENTID(), emg),
                    findAyantDroitByClientId(tc.getLgCLIENTID(), emg));
            json.put("success", true).put("data", new JSONObject(data));
            return json;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

            json.put("success", false).put("msg", "Erreur de création du client");
            return json;

        }
    }

    @Override
    public JSONObject updateCreateClientCarnet(ClientDTO client) throws JSONException {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        TClient tc;
        try {
            tc = findById(client.getLgCLIENTID(), emg);
            TTiersPayant p = findTiersPayantById(client.getLgTIERSPAYANTID(), emg);
            if (p == null) {
                json.put("success", false).put("msg", "Veuillez sélectionner un tiers-payant valide");
                return json;
            }

            if (tc == null) {
                if (!StringUtils.isEmpty(client.getStrNUMEROSECURITESOCIAL())) {
                    if (doesNumeroSecuriteSocialExist(client.getStrNUMEROSECURITESOCIAL(), p)) {
                        json.put("success", false).put("msg",
                                "Le numéro de sécurité :: [<span style=\"color: blue; \"> "
                                        + client.getStrNUMEROSECURITESOCIAL()
                                        + " </span>] est déjà utilisé dans le système");
                        return json;
                    }
                }
                tc = createClientCarnet(client, tc, p, emg);

            } else {
                if (!StringUtils.isEmpty(client.getStrNUMEROSECURITESOCIAL())) {
                    if (!client.getStrNUMEROSECURITESOCIAL().trim().equals(tc.getStrNUMEROSECURITESOCIAL().trim())) {
                        if (doesNumeroSecuriteSocialExist(client.getStrNUMEROSECURITESOCIAL(), p)) {
                            json.put("success", false).put("msg",
                                    "Le numéro de sécurité :: [<span style=\"color: blue; \"> "
                                            + client.getStrNUMEROSECURITESOCIAL()
                                            + " </span>] est déjà utilisé dans le système");
                            return json;
                        }
                    }
                }
                TCompteClientTiersPayant oldTpcm = findByClientTiersPayantId(client.getLgCLIENTID(),
                        DateConverter.STATUT_ENABLE, DateConverter.TIERS_PAYANT_PRINCIPAL);
                TTiersPayant oltp = oldTpcm.getLgTIERSPAYANTID();

                if (!oltp.equals(p)) {
                    updateClientCarnet(client, tc, p, oldTpcm);
                } else {
                    tc = updateClientCarnet(client, tc, oldTpcm, emg);
                }

            }
            ClientDTO data = new ClientDTO(findById(tc.getLgCLIENTID(), emg),
                    findTiersPayantByClientId(tc.getLgCLIENTID()));
            json.put("success", true).put("data", new JSONObject(data));
            return json;
        } catch (Exception e) {

            json.put("success", false).put("msg", "Erreur de création du client");
            return json;

        }
    }

    @Override
    public void updateCompteClientTiersPayantEncourAndPlafond(String venteId) {
        try {
            ventesAssuranceByVenteId(venteId).forEach(x -> {
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
                getEmg().merge(tc);
                getEmg().merge(tp);
            });
        } catch (Exception e) {
        }
    }

    private List<TPreenregistrementCompteClientTiersPayent> ventesAssuranceByVenteId(String venteId) {
        try {
            TypedQuery<TPreenregistrementCompteClientTiersPayent> tq = this.getEmg().createQuery(
                    "SELECT o FROM TPreenregistrementCompteClientTiersPayent o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1  ",
                    TPreenregistrementCompteClientTiersPayent.class).setParameter(1, venteId);
            return tq.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<TCompteClientTiersPayant> findTCompteClientTiersPayanCompteClient(String OTCompteClient) {
        try {
            TypedQuery<TCompteClientTiersPayant> tq = this.getEmg()
                    .createQuery(
                            "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCOMPTECLIENTID =?1  ",
                            TCompteClientTiersPayant.class)
                    .setParameter(1, OTCompteClient);
            return tq.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void updateComptClientTierspayantPriority(String OTCompteClient) {
        findTCompteClientTiersPayanCompteClient(OTCompteClient).forEach(c -> {
            c.setIntPRIORITY(c.getIntPRIORITY() + 1);
            c.setDtUPDATED(new Date());
            getEmg().merge(c);
        });
    }

    private TCompteClientTiersPayant createComptClientTierspayant(TClient cdto, TCompteClient OTCompteClient, int taux,
            TTiersPayant p, boolean isRO, int order) {
        TCompteClientTiersPayant OTCompteClientTiersPayant = new TCompteClientTiersPayant(UUID.randomUUID().toString());
        OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
        OTCompteClientTiersPayant.setStrNUMEROSECURITESOCIAL(cdto.getStrNUMEROSECURITESOCIAL());
        OTCompteClientTiersPayant.setBISRO(isRO);
        OTCompteClientTiersPayant.setBCANBEUSE(Boolean.TRUE);
        OTCompteClientTiersPayant.setBIsAbsolute(false);
        OTCompteClientTiersPayant.setDtCREATED(new Date());
        OTCompteClientTiersPayant.setDtUPDATED(new Date());
        OTCompteClientTiersPayant.setLgCOMPTECLIENTID(OTCompteClient);
        OTCompteClientTiersPayant.setLgTIERSPAYANTID(p);
        OTCompteClientTiersPayant.setIntPOURCENTAGE(taux);
        OTCompteClientTiersPayant.setIntPRIORITY(order);
        OTCompteClientTiersPayant.setDbPLAFONDENCOURS(0);
        OTCompteClientTiersPayant.setDblQUOTACONSOMENSUELLE(0);
        OTCompteClientTiersPayant.setDblPLAFOND(0.0);
        OTCompteClientTiersPayant.setDblQUOTACONSOVENTE(0.0);
        OTCompteClientTiersPayant.setIsCapped(Boolean.FALSE);
        getEmg().persist(OTCompteClientTiersPayant);
        return OTCompteClientTiersPayant;
    }

    @Override
    public TCompteClientTiersPayant updateOrCreateClientAssurance(TClient client, TTiersPayant p, int taux)
            throws Exception {
        TCompteClient compteClient = findByClientId(client.getLgCLIENTID(), this.getEmg());
        updateComptClientTierspayantPriority(compteClient.getLgCOMPTECLIENTID());
        return createComptClientTierspayant(client, compteClient, taux, p, true, 1);

    }

    @Override
    public TCompteClientTiersPayant updateOrCreateClientAssurance(TClient client, TTiersPayant p, int taux,
            TCompteClientTiersPayant old) throws Exception {
        TCompteClient compteClient = old.getLgCOMPTECLIENTID();
        return createComptClientTierspayant(client, compteClient, taux, p, old.getBISRO(), old.getIntPRIORITY());
    }

    @Override
    public JSONObject addNewTiersPayantToClient(TiersPayantDTO tiersPayantDTO, String clientId,
            String typeTiersPayantId, int taux) throws JSONException {
        try {
            TTiersPayant payant = createTiersPayant(tiersPayantDTO, typeTiersPayantId);
            updateOrCreateClientAssurance(getEmg().find(TClient.class, clientId), payant, taux);
            TiersPayantDTO o = new TiersPayantDTO();
            o.setStrFULLNAME(payant.getStrFULLNAME());
            o.setStrNAME(payant.getStrNAME());
            o.setLgTIERSPAYANTID(payant.getLgTIERSPAYANTID());
            return new JSONObject().put("data", new JSONObject(o)).put("success", true);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new JSONObject().put("success", false).put("msg", "Error ");
        }
    }

    private TTiersPayant createTiersPayant(TiersPayantDTO tiersPayantDTO, String typeTiersPayantId) {
        try {
            TTiersPayant payant = new TTiersPayant(UUID.randomUUID().toString());
            payant.setBCANBEUSE(Boolean.TRUE);
            payant.setStrFULLNAME(tiersPayantDTO.getStrFULLNAME());
            payant.setStrNAME(tiersPayantDTO.getStrNAME());
            payant.setStrTELEPHONE(tiersPayantDTO.getStrTELEPHONE());
            payant.setStrMAIL("");
            payant.setStrADRESSE(tiersPayantDTO.getStrADRESSE());
            payant.setStrMOBILE("");
            payant.setStrCODEORGANISME(tiersPayantDTO.getStrCODEORGANISME());
            payant.setBIsAbsolute(Boolean.FALSE);
            payant.setStrCODECOMPTABLE("46700000000");
            payant.setIntMONTANTFAC(-1);
            payant.setIntNBREBONS(-1);
            payant.setStrCOMPTECONTRIBUABLE("");
            payant.setDblPLAFONDCREDIT(0.0);
            payant.setBoolIsACCOUNT(false);
            payant.setDblTAUXREMBOURSEMENT(0.0);
            payant.setStrNUMEROCAISSEOFFICIEL("");
            payant.setStrCENTREPAYEUR("");
            payant.setStrCODEREGROUPEMENT("");
            payant.setDblSEUILMINIMUM(0.0);
            payant.setBoolINTERDICTION(false);
            payant.setBoolPRENUMFACTSUBROGATOIRE(false);
            payant.setIntNUMERODECOMPTE(0);
            payant.setStrCODEPAIEMENT("");
            payant.setDblPOURCENTAGEREMISE(0.0);
            payant.setDblREMISEFORFETAIRE(0.0);
            payant.setStrCODEEDITBORDEREAU("");
            payant.setIntNBREEXEMPLAIREBORD(1);
            payant.setIntPERIODICITEEDITBORD(0);
            payant.setIntDATEDERNIEREEDITION(0);
            payant.setStrNUMEROIDFORGANISME("reterte");
            payant.setDblMONTANTFCLIENT(0.0);
            payant.setDblBASEREMISE(0.0);
            payant.setStrCODEDOCCOMPTOIRE("");
            payant.setBoolENABLED(false);
            payant.setStrPHOTO("");
            payant.setStrCODEOFFICINE("");
            payant.setStrREGISTRECOMMERCE("");
            payant.setStrSTATUT(commonparameter.statut_enable);
            payant.setDtCREATED(new Date());
            payant.setDtUPDATED(new Date());
            payant.setLgMODELFACTUREID(getEmg().find(TModelFacture.class, "1"));
            payant.setLgTYPETIERSPAYANTID(getEmg().find(TTypeTiersPayant.class, typeTiersPayantId));
            payant.setLgRISQUEID(getEmg().find(TRisque.class, "55181642844215217016"));
            try {
                payant.setLgGROUPEID(
                        getEmg().find(TGroupeTierspayant.class, Integer.valueOf(tiersPayantDTO.getGroupeId())));
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            getEmg().persist(payant);
            return payant;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }

    public TCompteClientTiersPayant findCompteClientTiersPayantByClientId(String clientId) {

        try {
            TypedQuery<TCompteClientTiersPayant> query = getEmg().createQuery(
                    "SELECT o FROM TCompteClientTiersPayant o WHERE o.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID=?1 AND o.strSTATUT=?2",
                    TCompteClientTiersPayant.class);
            query.setParameter(1, clientId);
            query.setParameter(2, DateConverter.STATUT_ENABLE);
            return query.getResultList().stream().filter(e -> e.getIntPRIORITY().compareTo(1) == 0).findFirst().get();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public JSONObject updateClientInfos(ClientDTO client, String id) {
        JSONObject json = new JSONObject();
        try {
            client.setLgCLIENTID(id);
            TClient tc = findById(id, getEmg());

            return updateInfosClient(client, tc);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", false).put("msg", "Erreur de modification du client");
        }

    }

    private JSONObject updateInfosClient(ClientDTO client, TClient tc) throws Exception {
        JSONObject json = new JSONObject();
        EntityManager emg = this.getEmg();
        ClientDTO data = null;
        String oldNum = tc.getStrNUMEROSECURITESOCIAL();
        TCompteClientTiersPayant payant = findCompteClientTiersPayantByClientId(client.getLgCLIENTID());
        TTiersPayant p = payant.getLgTIERSPAYANTID();
        if (!StringUtils.isEmpty(client.getStrNUMEROSECURITESOCIAL())) {
            if (!client.getStrNUMEROSECURITESOCIAL().equals(tc.getStrNUMEROSECURITESOCIAL())) {
                if (doesNumeroSecuriteSocialExist(client.getStrNUMEROSECURITESOCIAL(), p)) {
                    json.put("success", false).put("msg", "Le numéro de sécurité :: [<span style=\"color: blue; \"> "
                            + client.getStrNUMEROSECURITESOCIAL() + " </span>] est déjà utilisé dans le système");
                    return json;
                }
            }
        }
        tc.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        tc.setStrLASTNAME(client.getStrLASTNAME().toUpperCase());
        tc.setStrFIRSTNAME(client.getStrFIRSTNAME().toUpperCase());
        tc.setDtUPDATED(new Date());
        tc.setStrADRESSE(client.getStrADRESSE());
        getEmg().merge(tc);
        payant.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
        getEmg().merge(payant);

        if (tc.getLgTYPECLIENTID().getLgTYPECLIENTID().equals(DateConverter.CLIENT_ASSURANCE)) {
            TAyantDroit ayantDroit = findAyantDroitByNum(oldNum, emg);
            ayantDroit.setDtUPDATED(new Date());
            ayantDroit.setStrFIRSTNAME(client.getStrFIRSTNAME().toUpperCase());
            ayantDroit.setStrLASTNAME(client.getStrLASTNAME().toUpperCase());
            ayantDroit.setStrNUMEROSECURITESOCIAL(client.getStrNUMEROSECURITESOCIAL());
            getEmg().merge(ayantDroit);
            data = new ClientDTO(findById(tc.getLgCLIENTID(), emg), Collections.emptyList(),
                    findAyantDroitByClientId(tc.getLgCLIENTID(), emg));
        } else {
            data = new ClientDTO(findById(tc.getLgCLIENTID(), emg), Collections.emptyList(), Collections.emptyList());
        }

        data = new ClientDTO(findById(tc.getLgCLIENTID(), emg), Collections.emptyList(),
                findAyantDroitByClientId(tc.getLgCLIENTID(), emg));
        json.put("success", true).put("data", new JSONObject(data));
        return json;

    }

    @Override
    public JSONObject updateAyantDroitInfos(AyantDroitDTO ayantDroitDTO) {
        JSONObject json = new JSONObject();
        try {
            TAyantDroit ayantDroit = getEmg().find(TAyantDroit.class, ayantDroitDTO.getLgAYANTSDROITSID());
            ayantDroit.setStrFIRSTNAME(ayantDroitDTO.getStrFIRSTNAME().toUpperCase());
            ayantDroit.setStrLASTNAME(ayantDroitDTO.getStrLASTNAME().toUpperCase());
            ayantDroit.setStrNUMEROSECURITESOCIAL(ayantDroitDTO.getStrNUMEROSECURITESOCIAL());
            getEmg().merge(ayantDroit);
            return json.put("success", true).put("data", new JSONObject(new AyantDroitDTO(ayantDroit)));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", false).put("msg", "Erreur de modification des infos de  l'ayant droit");
        }
    }

    @Override
    public JSONObject ventesTiersPayants(String query, String dtStart, String dtEnd, String tiersPayantId,
            String groupeId, String typeTp, int start, int limit) {
        List<VenteTiersPayantsDTO> data = ventesTiersPayants(query, dtStart, dtEnd, tiersPayantId, groupeId, typeTp,
                start, limit, true);
        JSONObject json = new JSONObject();
        int nbre = 0;
        long montant = 0;
        for (VenteTiersPayantsDTO venteTiersPayantsDTO : data) {
            nbre += venteTiersPayantsDTO.getNbreDossier();
            montant += venteTiersPayantsDTO.getMontant();
        }
        return json.put("total", data.size()).put("data", new JSONArray(data)).put("metaData",
                new JSONObject().put("nbre", nbre).put("montant", montant));

    }

    @Override
    public List<VenteTiersPayantsDTO> ventesTiersPayants(String query, String dtStart, String dtEnd,
            String tiersPayantId, String groupeId, String typeTp, int start, int limit, boolean all) {
        List<VenteTiersPayantsDTO> data = new ArrayList<>();
        try {
            CriteriaBuilder cb = this.getEmg().getCriteriaBuilder();
            CriteriaQuery<VenteTiersPayantsDTO> cq = cb.createQuery(VenteTiersPayantsDTO.class);
            Root<TPreenregistrementCompteClientTiersPayent> root = cq
                    .from(TPreenregistrementCompteClientTiersPayent.class);
            cq.select(cb.construct(VenteTiersPayantsDTO.class,
                    root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgTIERSPAYANTID),
                    cb.count(root), cb.sum(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE)),
                    cb.sum(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICERESTE))))
                    .orderBy(cb.asc(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.strFULLNAME)))
                    .groupBy(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgTIERSPAYANTID));
            List<Predicate> predicates = predicateventesTiersPayants(cb, root, query, dtStart, dtEnd, tiersPayantId,
                    groupeId, typeTp);
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<VenteTiersPayantsDTO> q = this.getEmg().createQuery(cq);
            if (!all) {
                q.setFirstResult(start);
                q.setMaxResults(limit);
            }
            return q.getResultList();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "{0}", e);

        }
        return data;
    }

    List<Predicate> predicateventesTiersPayants(CriteriaBuilder cb,
            Root<TPreenregistrementCompteClientTiersPayent> root, String query, String dtStart, String dtEnd,
            String tiersPayantId, String groupeId, String typeTp) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate btw = cb.between(
                cb.function("DATE", Date.class,
                        root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID)
                                .get(TPreenregistrement_.dtCREATED)),
                java.sql.Date.valueOf(dtStart), java.sql.Date.valueOf(dtEnd));
        predicates.add(btw);
        predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID)
                .get(TPreenregistrement_.strSTATUT), DateConverter.STATUT_IS_CLOSED));
        predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.strSTATUT),
                DateConverter.STATUT_IS_CLOSED));
        predicates.add(cb.isFalse(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID)
                .get(TPreenregistrement_.bISCANCEL)));
        predicates.add(cb.greaterThan(root.get(TPreenregistrementCompteClientTiersPayent_.lgPREENREGISTREMENTID)
                .get(TPreenregistrement_.intPRICE), 0));
        predicates.add(cb.greaterThan(root.get(TPreenregistrementCompteClientTiersPayent_.intPRICE), 0));
        if (!StringUtils.isEmpty(query)) {
            query = query.toUpperCase();
            predicates.add(cb.or(
                    cb.like(cb.upper(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.strFULLNAME)),
                            query + "%"),
                    cb.like(cb.upper(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                            .get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.strNAME)), query + "%")));
        }

        if (!StringUtils.isEmpty(tiersPayantId) || !StringUtils.isEmpty(query) || !StringUtils.isEmpty(groupeId)) {
            Join<TPreenregistrementCompteClientTiersPayent, TCompteClientTiersPayant> join = root
                    .join(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID, JoinType.INNER);
            if (!StringUtils.isEmpty(tiersPayantId)) {
                predicates.add(
                        cb.equal(join.get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTIERSPAYANTID),
                                tiersPayantId));
            }

            if (!StringUtils.isEmpty(groupeId)) {
                predicates.add(cb.equal(join.get(TCompteClientTiersPayant_.lgTIERSPAYANTID)
                        .get(TTiersPayant_.lgGROUPEID).get(TGroupeTierspayant_.lgGROUPEID), Integer.valueOf(groupeId)));

            }

        }
        if (!StringUtils.isEmpty(typeTp) && !"ALL".equals(typeTp)) {
            predicates.add(cb.equal(root.get(TPreenregistrementCompteClientTiersPayent_.lgCOMPTECLIENTTIERSPAYANTID)
                    .get(TCompteClientTiersPayant_.lgTIERSPAYANTID).get(TTiersPayant_.lgTYPETIERSPAYANTID)
                    .get(TTypeTiersPayant_.lgTYPETIERSPAYANTID), typeTp));
        }
        return predicates;

    }

    public TCompteClientTiersPayant updateOrCreateClientCarnet(TClient client, TTiersPayant p, int taux)
            throws Exception {
        TCompteClient compteClient = findByClientId(client.getLgCLIENTID(), this.getEmg());
        updateComptClientTierspayantPriority(compteClient.getLgCOMPTECLIENTID());
        return createComptClientTierspayant(client, compteClient, taux, p, true, 1);

    }
}
