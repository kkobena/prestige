package rest;

import bll.bllBase;
import bll.entity.EntityData;
import bll.report.StatisticsFamilleArticle;
import bll.facture.reglementManager;
import bll.teller.tellerManagement;
import bll.tierspayantManagement.tierspayantManagement;
import dal.TDossierReglement;
import dal.TFactureDetail;
import dal.TModeReglement;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TTiersPayant;
import dal.TTypeReglement;
import dal.TTypeTiersPayant;
import dal.TUser;
import dal.dataManager;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;
import toolkits.utils.date;
import toolkits.utils.jdom;

/**
 * Migration REST des webservices JSP du reglement des factures tiers payant
 * (webservices/sm_user/reglement/ws_data_reglement_by_bordereau.jsp, ws_transaction.jsp mode doReglement,
 * ws_mode_reglemnt.jsp, webservices/sm_user/modereglement/ws_data.jsp,
 * webservices/configmanagement/groupe/ws_reglement.jsp).
 *
 * Toutes les operations deleguent aux MEMES methodes metier bll.facture.reglementManager / bll.teller.tellerManagement
 * que les JSP historiques : memes regles, memes messages, memes cles JSON. Chemin distinct de v1/reglement (deja
 * utilise par ReglementRessource pour les reglements differes).
 */
@Path("v1/reglement-facture")
@Produces("application/json")
@Consumes("application/json")
public class ReglementFactureRessource {

    private static final Logger LOG = Logger.getLogger(ReglementFactureRessource.class.getName());

    @Inject
    private HttpServletRequest servletRequest;

    private TUser utilisateurSession() {
        return (TUser) servletRequest.getSession().getAttribute(commonparameter.AIRTIME_USER);
    }

    private Response reponseDeconnecte() {
        return Response.ok()
                .entity(new JSONObject().put("success", commonparameter.PROCESS_FAILED)
                        .put("errors", util.Constant.DECONNECTED_MESSAGE).put("total", 0)
                        .put("results", new JSONArray()).toString())
                .build();
    }

    /**
     * Traduit une exception technique en message comprehensible pour l'utilisateur, et transmet l'incident au Centre de
     * Support avec la cause exacte (meme mecanisme que dal.dataManager). Best-effort : ne perturbe jamais la reponse.
     */
    private static String signalerAuSupport(String opration, Exception e) {
        String causeTechnique = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            causeTechnique = e.getCause().getMessage();
        }
        String causeSimple;
        String causeMin = causeTechnique.toLowerCase();
        if (causeMin.contains("unknown column")) {
            causeSimple = "la base de données n'est pas à jour (colonne manquante). Les mises à jour de la base "
                    + "(migrations) doivent être exécutées.";
        } else if (causeMin.contains("doesn't exist") || causeMin.contains("table")) {
            causeSimple = "la base de données n'est pas à jour (table manquante). Les mises à jour de la base "
                    + "(migrations) doivent être exécutées.";
        } else if (causeMin.contains("timeout") || causeMin.contains("connection") || causeMin.contains("connexion")) {
            causeSimple = "la base de données ne répond pas. Réessayez dans un instant ; si le problème persiste, "
                    + "le serveur doit être vérifié.";
        } else if (causeMin.contains("unparseable date") || causeMin.contains("parse")) {
            causeSimple = "une date reçue est invalide. Vérifiez les dates saisies à l'écran.";
        } else if (e instanceof NullPointerException) {
            causeSimple = "une donnée attendue est absente (dossier ou facture introuvable).";
        } else {
            causeSimple = causeTechnique;
        }
        try {
            StringBuilder pile = new StringBuilder();
            for (StackTraceElement frame : e.getStackTrace()) {
                pile.append(frame).append('\n');
            }
            rest.service.SupportEventService support = javax.enterprise.inject.spi.CDI.current()
                    .select(rest.service.SupportEventService.class).get();
            rest.service.dto.SupportEventDTO dto = new rest.service.dto.SupportEventDTO();
            dto.setType("JAVA");
            dto.setNiveau("ERROR");
            dto.setModule("REGLEMENT FACTURE");
            dto.setMessageCourt("Echec " + opration + " : " + causeTechnique);
            dto.setUrlOuEcran("api/v1/reglement-facture");
            dto.setPayloadJson("Explication utilisateur : " + causeSimple);
            dto.setStack(pile.length() > 20000 ? pile.substring(0, 20000) : pile.toString());
            support.record(dto, "");
        } catch (RuntimeException ex) {
            // centre de support indisponible : la trace serveur reste
        }
        return causeSimple;
    }

    /** Reproduit la "logic de gestion des page" des JSP historiques : start -> numero de page. */
    private static int pageDepuisStart(String action, String startParam, int dataPerPage) {
        int pageAsInt = 0;
        try {
            if (!"filltable".equals(action)) {
                if (startParam != null) {
                    pageAsInt = (Integer.parseInt(startParam) / dataPerPage) + 1;
                } else {
                    pageAsInt = 1;
                }
            }
        } catch (Exception e) {
        }
        return pageAsInt;
    }

    /** Detail des dossiers d'une facture pour le reglement : MEME logique que ws_data_reglement_by_bordereau.jsp. */
    @GET
    @Path("details-bordereau")
    public Response detailsBordereau(
            @DefaultValue("") @QueryParam("lg_dossier_reglement_id") String lgDossierReglementId,
            @DefaultValue("") @QueryParam("lg_FACTURE_ID") String lgFactureId,
            @DefaultValue("") @QueryParam("action") String action, @QueryParam("start") String startParam) {
        if (utilisateurSession() == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            reglementManager orm = new reglementManager(odm, null);
            List<TFactureDetail> lstTFactureDetail = orm.getDetailsFactureByTiersPayant(lgFactureId);

            int dataPerPage = 5;
            int pageAsInt = pageDepuisStart(action, startParam, dataPerPage);
            if (dataPerPage > lstTFactureDetail.size()) {
                dataPerPage = lstTFactureDetail.size();
            }
            int pgInt = pageAsInt - 1;
            int pgIntLast;
            if (pgInt == 0) {
                pgIntLast = dataPerPage;
            } else {
                pgIntLast = (lstTFactureDetail.size() - (dataPerPage * (pgInt)));
                pgIntLast = (dataPerPage * (pgInt) + pgIntLast);
                if (pgIntLast > (dataPerPage * (pgInt + 1))) {
                    pgIntLast = dataPerPage * (pgInt + 1);
                }
                pgInt = ((dataPerPage) * (pgInt));
            }

            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgIntLast; i++) {
                JSONObject json = new JSONObject();
                double amount = 0d, dblMONTANTPAYE = 0d, dblMONTANTRESTANT = 0d;
                TPreenregistrementCompteClientTiersPayent preregistrement = orm
                        .getOPreenregistrementCompteClientTiersPayentByRef(lstTFactureDetail.get(i).getStrREF());
                json.put("lg_FACTURE_ID", lstTFactureDetail.get(i).getLgFACTUREID().getStrCODEFACTURE());
                json.put("lg_FACTURE_DETAIL_ID", lstTFactureDetail.get(i).getLgFACTUREDETAILID());
                if (lstTFactureDetail.get(i).getDblMONTANT() != null) {
                    amount = lstTFactureDetail.get(i).getDblMONTANT();
                }
                json.put("Amount", amount);
                if (lstTFactureDetail.get(i).getDblMONTANTPAYE() != null) {
                    dblMONTANTPAYE = lstTFactureDetail.get(i).getDblMONTANTPAYE();
                }
                json.put("dbl_MONTANT_PAYE", dblMONTANTPAYE);
                if (lstTFactureDetail.get(i).getDblMONTANTRESTANT() != null) {
                    dblMONTANTRESTANT = lstTFactureDetail.get(i).getDblMONTANTRESTANT();
                }
                json.put("dbl_MONTANT_RESTANT", dblMONTANTRESTANT);
                json.put("isChecked", false);
                json.put("str_REF", (preregistrement.getStrREFBON() != null ? preregistrement.getStrREFBON() : ""));
                json.put("CLIENT_FULL_NAME", preregistrement.getLgPREENREGISTREMENTID().getStrFIRSTNAMECUSTOMER() + " "
                        + preregistrement.getLgPREENREGISTREMENTID().getStrLASTNAMECUSTOMER());
                json.put("CLIENT_MATRICULE", preregistrement.getLgPREENREGISTREMENTID().getStrNUMEROSECURITESOCIAL());
                json.put("dt_DATE",
                        date.backabaseUiFormat2.format(preregistrement.getLgPREENREGISTREMENTID().getDtUPDATED()));
                json.put("dt_HEURE",
                        date.NomadicUiFormatTime.format(preregistrement.getLgPREENREGISTREMENTID().getDtUPDATED()));
                json.put("int_NB_DOSSIER_RESTANT", lstTFactureDetail.size());
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + lstTFactureDetail.size() + "\",\"Amount_total\":\"0.0\",\"results\":"
                    + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "details bordereau reglement", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Reglement d'une facture tiers payant : MEME logique que ws_transaction.jsp mode doReglement. */
    @POST
    @Path("do-reglement")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doReglement(@DefaultValue("") @FormParam("lg_FACTURE_ID") String lgFactureId,
            @DefaultValue("") @FormParam("str_CUSTOMER") String strCustomer,
            @DefaultValue("") @FormParam("MODE_REGLEMENT") String modeReglement,
            @DefaultValue("") @FormParam("TYPE_REGLEMENT") String typeReglement,
            @DefaultValue("") @FormParam("lg_NATURE_PAIEMENT") String lgNaturePaiement,
            @DefaultValue("") @FormParam("str_LIEU") String strLieu,
            @DefaultValue("") @FormParam("str_CODE_MONNAIE") String strCodeMonnaie,
            @DefaultValue("") @FormParam("str_BANQUE") String strBanque,
            @DefaultValue("") @FormParam("str_NOM") String strNom,
            @DefaultValue("0") @FormParam("NET_A_PAYER") double netAPayer,
            @DefaultValue("0") @FormParam("int_TAUX_CHANGE") String tauxChangeParam,
            @DefaultValue("0") @FormParam("int_AMOUNT_REMIS") double amountRemis,
            @DefaultValue("0") @FormParam("int_AMOUNT_RECU") double amountRecu,
            @DefaultValue("") @FormParam("LISTDOSSIERS") String listDossiersParam,
            @DefaultValue("") @FormParam("checkedList") String checkedListParam,
            @DefaultValue("") @FormParam("dt_reglement") String dtReglementParam) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        bllBase obllBase = new bllBase();
        String lgDossierReglementId = "";
        try {
            odm.initEntityManager();
            Date dtReglement = new Date();
            if (StringUtils.isNotEmpty(dtReglementParam)) {
                String now = dtReglementParam + " " + date.NomadicUiFormatTime.format(new Date());
                dtReglement = date.formatterMysql2.parse(now);
            }
            Integer tauxChange = 0;
            if (StringUtils.isNotEmpty(tauxChangeParam)) {
                tauxChange = Integer.parseInt(tauxChangeParam);
            }
            JSONArray listFactureDetails = new JSONArray();
            if (StringUtils.isNotEmpty(listDossiersParam)) {
                listFactureDetails = new JSONArray(listDossiersParam);
            }
            JSONArray uncheckedlist = new JSONArray();
            if (StringUtils.isNotEmpty(checkedListParam)) {
                uncheckedlist = new JSONArray(checkedListParam);
            }
            reglementManager orm = new reglementManager(odm, sessionUser);
            double finalamout = netAPayer <= amountRecu ? netAPayer : amountRecu;
            TDossierReglement oDossierReglement = orm.makeInvoicePayment(lgFactureId, lgNaturePaiement, strBanque,
                    strLieu, strCodeMonnaie, strLieu, modeReglement, tauxChange, finalamout, amountRemis, amountRecu,
                    listFactureDetails, strNom, strCustomer, uncheckedlist, dtReglement);
            if (oDossierReglement != null) {
                lgDossierReglementId = oDossierReglement.getLgDOSSIERREGLEMENTID();
                orm.updateSnapshotVenteSociete(lgDossierReglementId, lgFactureId);
            }
            obllBase.setMessage(orm.getMessage());
            obllBase.setDetailmessage(orm.getDetailmessage());

            // errors vide quand le reglement a abouti : la JSP historique y mettait le code retour
            // interne ("1" = succes), ce qui pretait a confusion cote utilisateur
            String erreurs = "1".equals(obllBase.getDetailmessage()) ? "" : obllBase.getMessage();
            String result = "{\"success\":\"" + obllBase.getDetailmessage() + "\",\"str_ref\":\"" + lgFactureId
                    + "\",\"lg_DOSSIER_REGLEMENT_ID\":\"" + lgDossierReglementId + "\", \"errors\": \"" + erreurs
                    + "\"}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "do reglement", e);
            // incident transmis au Centre de Support, cause expliquee en langage simple a l'utilisateur
            String causeSimple = signalerAuSupport("du règlement de la facture " + lgFactureId, e);
            return Response
                    .ok().entity(
                            new JSONObject().put("success", commonparameter.PROCESS_FAILED).put("str_ref", lgFactureId)
                                    .put("lg_DOSSIER_REGLEMENT_ID", "")
                                    .put("errors",
                                            "Le règlement n'a pas pu être enregistré : " + causeSimple
                                                    + " L'incident a été transmis au Centre de Support.")
                                    .toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Reglement d'une facture de groupe : MEME logique que configmanagement/groupe/ws_reglement.jsp. */
    @POST
    @Path("groupe")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response reglementGroupe(@DefaultValue("") @FormParam("CODEFACTURE") String codeFacture,
            @DefaultValue("") @FormParam("MODE_REGLEMENT") String modeReglement,
            @DefaultValue("") @FormParam("str_LIEU") String strLieu,
            @DefaultValue("") @FormParam("str_CODE_MONNAIE") String strCodeMonnaie,
            @DefaultValue("") @FormParam("str_BANQUE") String strBanque,
            @DefaultValue("") @FormParam("str_NOM") String strNom,
            @DefaultValue("0") @FormParam("NET_A_PAYER") double netAPayer,
            @DefaultValue("0") @FormParam("int_TAUX_CHANGE") String tauxChangeParam,
            @DefaultValue("0") @FormParam("int_AMOUNT_RECU") double amountRecu,
            @DefaultValue("") @FormParam("LISTDOSSIERS") String listDossiersParam,
            @DefaultValue("") @FormParam("checkedList") String checkedListParam,
            @DefaultValue("0") @FormParam("mode") int mode,
            @DefaultValue("") @FormParam("dt_reglement") String dtReglementParam) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            TUser user = odm.getEm().find(TUser.class, sessionUser.getLgUSERID());
            Date dtReglement = new Date();
            if (StringUtils.isNotEmpty(dtReglementParam)) {
                String now = dtReglementParam + " " + date.NomadicUiFormatTime.format(new Date());
                dtReglement = date.formatterMysql2.parse(now);
            }
            Integer tauxChange = 0;
            if (StringUtils.isNotEmpty(tauxChangeParam)) {
                tauxChange = Integer.parseInt(tauxChangeParam);
            }
            JSONArray listFactureDetails = new JSONArray();
            if (StringUtils.isNotEmpty(listDossiersParam)) {
                listFactureDetails = new JSONArray(listDossiersParam);
            }
            JSONArray uncheckedlist = new JSONArray();
            if (StringUtils.isNotEmpty(checkedListParam)) {
                uncheckedlist = new JSONArray(checkedListParam);
            }
            reglementManager orm = new reglementManager(odm, user);
            JSONObject data = orm.makeGroupInvoicePayment(codeFacture, -1, strBanque, strLieu, strCodeMonnaie, strLieu,
                    modeReglement, tauxChange, netAPayer, 0, amountRecu, listFactureDetails, strNom, uncheckedlist,
                    dtReglement, mode);
            return Response.ok().entity(data.toString()).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "reglement groupe", e);
            // incident transmis au Centre de Support, cause expliquee en langage simple a l'utilisateur
            String causeSimple = signalerAuSupport("du règlement de la facture de groupe " + codeFacture, e);
            return Response
                    .ok().entity(
                            new JSONObject().put("success", "0")
                                    .put("errors",
                                            "Le règlement n'a pas pu être enregistré : " + causeSimple
                                                    + " L'incident a été transmis au Centre de Support.")
                                    .toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Liste des modes de reglement : MEME logique que modereglement/ws_data.jsp. */
    @GET
    @Path("modes-reglement")
    public Response modesReglement(@DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("query") String query,
            @DefaultValue("") @QueryParam("lg_MODE_REGLEMENT_ID") String lgModeReglementId,
            @DefaultValue("") @QueryParam("lg_TYPE_REGLEMENT_ID") String lgTypeReglementId,
            @DefaultValue("") @QueryParam("action") String action, @QueryParam("start") String startParam) {
        dataManager odm = new dataManager();
        try {
            String search = StringUtils.isNotEmpty(query) ? query : searchValue;
            String modeId = StringUtils.isNotEmpty(lgModeReglementId) ? lgModeReglementId : "%%";
            String typeId = StringUtils.isNotEmpty(lgTypeReglementId) ? lgTypeReglementId : "%%";
            odm.initEntityManager();
            tellerManagement otm = new tellerManagement(odm);
            List<TModeReglement> lst = otm.getListeTModeReglement(search, modeId, typeId);

            int dataPerPage = jdom.int_size_pagination;
            int pageAsInt = pageDepuisStart(action, startParam, dataPerPage);
            if (dataPerPage > lst.size()) {
                dataPerPage = lst.size();
            }
            int pgInt = pageAsInt - 1;
            int pgIntLast;
            if (pgInt == 0) {
                pgIntLast = dataPerPage;
            } else {
                pgIntLast = (lst.size() - (dataPerPage * (pgInt)));
                pgIntLast = (dataPerPage * (pgInt) + pgIntLast);
                if (pgIntLast > (dataPerPage * (pgInt + 1))) {
                    pgIntLast = dataPerPage * (pgInt + 1);
                }
                pgInt = ((dataPerPage) * (pgInt));
            }
            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgIntLast; i++) {
                JSONObject json = new JSONObject();
                json.put("lg_MODE_REGLEMENT_ID", lst.get(i).getLgMODEREGLEMENTID());
                json.put("str_NAME", lst.get(i).getStrNAME());
                json.put("str_DESCRIPTION", lst.get(i).getStrDESCRIPTION());
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + lst.size() + "\",\"results\":" + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "modes reglement", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Liste des types de reglement : MEME logique que reglement/ws_mode_reglemnt.jsp. */
    @GET
    @Path("types-reglement")
    public Response typesReglement(@DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("query") String query,
            @DefaultValue("") @QueryParam("str_FLAG") String strFlag,
            @DefaultValue("") @QueryParam("lg_TYPE_REGLEMENT_ID") String lgTypeReglementId,
            @DefaultValue("") @QueryParam("action") String action, @QueryParam("start") String startParam) {
        TUser sessionUser = utilisateurSession();
        dataManager odm = new dataManager();
        try {
            String search = StringUtils.isNotEmpty(query) ? query : searchValue;
            String flag = StringUtils.isNotEmpty(strFlag) ? strFlag : "%%";
            String typeId = StringUtils.isNotEmpty(lgTypeReglementId) ? lgTypeReglementId : "%%";
            odm.initEntityManager();
            reglementManager orm = new reglementManager(odm, sessionUser);
            List<TTypeReglement> lst = orm.getListeTTypeReglement(search, typeId, flag);

            int dataPerPage = jdom.int_size_pagination;
            int pageAsInt = pageDepuisStart(action, startParam, dataPerPage);
            if (dataPerPage > lst.size()) {
                dataPerPage = lst.size();
            }
            int pgInt = pageAsInt - 1;
            int pgIntLast;
            if (pgInt == 0) {
                pgIntLast = dataPerPage;
            } else {
                pgIntLast = (lst.size() - (dataPerPage * (pgInt)));
                pgIntLast = (dataPerPage * (pgInt) + pgIntLast);
                if (pgIntLast > (dataPerPage * (pgInt + 1))) {
                    pgIntLast = dataPerPage * (pgInt + 1);
                }
                pgInt = ((dataPerPage) * (pgInt));
            }
            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgIntLast; i++) {
                try {
                    odm.getEm().refresh(lst.get(i));
                } catch (Exception er) {
                }
                JSONObject json = new JSONObject();
                json.put("lg_TYPE_REGLEMENT_ID", lst.get(i).getLgTYPEREGLEMENTID());
                json.put("str_NAME", lst.get(i).getStrNAME());
                json.put("str_DESCRIPTION", lst.get(i).getStrDESCRIPTION());
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + lst.size() + "\",\"results\":" + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "types reglement", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Liste des reglements : MEME logique que reglement/ws_data.jsp. */
    @GET
    @Path("list")
    public Response list(@DefaultValue("") @QueryParam("dt_debut") String dtDebutParam,
            @DefaultValue("") @QueryParam("dt_fin") String dtFinParam,
            @DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("query") String query,
            @DefaultValue("") @QueryParam("lg_TIERS_PAYANT_ID") String lgTiersPayantId,
            @DefaultValue("") @QueryParam("action") String action, @QueryParam("start") String startParam) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            String dtDebut = StringUtils.isNotEmpty(dtDebutParam) ? dtDebutParam
                    : date.formatterMysqlShort.format(new Date());
            String dtFin = StringUtils.isNotEmpty(dtFinParam) ? dtFinParam + " 23:59:59"
                    : date.formatterMysql.format(new Date());
            String search = "%%";
            if (StringUtils.isNotEmpty(searchValue)) {
                search = searchValue;
            }
            if (StringUtils.isNotEmpty(query)) {
                search = query;
            }
            String tiersPayantId = StringUtils.isNotEmpty(lgTiersPayantId) ? lgTiersPayantId : "%%";
            odm.initEntityManager();
            reglementManager orm = new reglementManager(odm, sessionUser);
            List<EntityData> entityDatas = orm.getAllDossierReglements(tiersPayantId, search, dtDebut, dtFin);

            int dataPerPage = jdom.int_size_pagination;
            int pageAsInt = pageDepuisStart(action, startParam, dataPerPage);
            if (dataPerPage > entityDatas.size()) {
                dataPerPage = entityDatas.size();
            }
            int pgInt = pageAsInt - 1;
            int pgIntLast;
            if (pgInt == 0) {
                pgIntLast = dataPerPage;
            } else {
                pgIntLast = (entityDatas.size() - (dataPerPage * (pgInt)));
                pgIntLast = (dataPerPage * (pgInt) + pgIntLast);
                if (pgIntLast > (dataPerPage * (pgInt + 1))) {
                    pgIntLast = dataPerPage * (pgInt + 1);
                }
                pgInt = ((dataPerPage) * (pgInt));
            }
            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgIntLast; i++) {
                JSONObject json = new JSONObject();
                json.put("lg_DOSSIER_REGLEMENT_ID", entityDatas.get(i).getStr_value1());
                json.put("str_MODE_REGLEMENT", entityDatas.get(i).getStr_value3());
                json.put("str_MONTANT", entityDatas.get(i).getStr_value2());
                json.put("dt_DATE_REGLEMENT", entityDatas.get(i).getStr_value6());
                json.put("str_ORGANISME", entityDatas.get(i).getStr_value4());
                json.put("LIBELLE_TYPE_TIERS_PAYANT", entityDatas.get(i).getStr_value8());
                json.put("HEURE_REGLEMENT", entityDatas.get(i).getStr_value7());
                json.put("OPERATEUR", entityDatas.get(i).getStr_value5());
                json.put("MONTANT_ATT", entityDatas.get(i).getStr_value10());
                json.put("CODE_FACTURE", entityDatas.get(i).getStr_value9());
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + entityDatas.size() + "\",\"results\":" + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "liste reglements", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Details d'un reglement : MEME logique que reglement/ws_details_reglement.jsp. */
    @GET
    @Path("details")
    public Response details(@DefaultValue("") @QueryParam("lg_DOSSIER_REGLEMENT_ID") String lgDossierReglementId,
            @DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("action") String action, @QueryParam("start") String startParam) {
        if (utilisateurSession() == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            reglementManager orm = new reglementManager(odm, null);
            List<EntityData> listEntityData = orm.getAllDossierReglementDetails(lgDossierReglementId, searchValue);

            int dataPerPage = 10;
            int pageAsInt = pageDepuisStart(action, startParam, dataPerPage);
            if (dataPerPage > listEntityData.size()) {
                dataPerPage = listEntityData.size();
            }
            int pgInt = pageAsInt - 1;
            int pgIntLast;
            if (pgInt == 0) {
                pgIntLast = dataPerPage;
            } else {
                pgIntLast = (listEntityData.size() - (dataPerPage * (pgInt)));
                pgIntLast = (dataPerPage * (pgInt) + pgIntLast);
                if (pgIntLast > (dataPerPage * (pgInt + 1))) {
                    pgIntLast = dataPerPage * (pgInt + 1);
                }
                pgInt = ((dataPerPage) * (pgInt));
            }
            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgIntLast; i++) {
                JSONObject json = new JSONObject();
                json.put("lg_DOSSIER_REGLEMENT_DETAIL_ID", listEntityData.get(i).getStr_value1());
                json.put("Amount", listEntityData.get(i).getStr_value3());
                json.put("str_REF",
                        (listEntityData.get(i).getStr_value2() != null ? listEntityData.get(i).getStr_value2() : ""));
                json.put("CLIENT_FULL_NAME", listEntityData.get(i).getStr_value4());
                json.put("CLIENT_MATRICULE", listEntityData.get(i).getStr_value5());
                json.put("int_NB_DOSSIER_RESTANT", listEntityData.size());
                json.put("dt_DATE", listEntityData.get(i).getStr_value6());
                json.put("dt_HEURE", listEntityData.get(i).getStr_value7());
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + listEntityData.size() + "\",\"Amount_total\":\"0.0\",\"results\":"
                    + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "details reglement", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /**
     * Recapitulatif par compte d'organisme : port de webservices/sm_user/RecapOrganisme/ws_data.jsp, MEME methode
     * metier bll.report.StatisticsFamilleArticle.getRecapReglementByOrganismeData, memes cles JSON. Relogee dans cette
     * classe (au lieu d'une ressource autonome) : sur certains environnements la ressource dediee n'etait pas
     * enregistree par le serveur (404).
     */
    @GET
    @Path("recap-organisme/list")
    public Response recapOrganisme(@DefaultValue("") @QueryParam("dt_start_vente") String dtStartParam,
            @DefaultValue("") @QueryParam("dt_end_vente") String dtEndParam,
            @DefaultValue("") @QueryParam("lg_TIERS_PAYANT_ID") String lgTiersPayantId,
            @DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("query") String query, @DefaultValue("") @QueryParam("action") String action,
            @QueryParam("start") String startParam) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return Response.ok().entity(new JSONObject().put("data", new JSONArray()).put("total", 0).toString())
                    .build();
        }
        dataManager odm = new dataManager();
        try {
            String dtStart = StringUtils.isNotEmpty(dtStartParam) ? dtStartParam
                    : date.formatterMysqlShort.format(new Date());
            String dtEnd = StringUtils.isNotEmpty(dtEndParam) ? dtEndParam
                    : date.formatterMysqlShort.format(new Date());
            String tiersPayantId = StringUtils.isNotEmpty(lgTiersPayantId) ? lgTiersPayantId : "%%";
            String search = "%%";
            if (StringUtils.isNotEmpty(searchValue)) {
                search = searchValue;
            }
            if (StringUtils.isNotEmpty(query)) {
                search = query;
            }
            odm.initEntityManager();
            StatisticsFamilleArticle familleArticle = new StatisticsFamilleArticle(odm);
            List<EntityData> lstdetails = familleArticle.getRecapReglementByOrganismeData(dtStart, dtEnd, tiersPayantId,
                    search);

            int dataPerPage = 20;
            int pageAsInt = 0;
            try {
                if (!"filltable".equals(action)) {
                    if (startParam != null) {
                        pageAsInt = (Integer.parseInt(startParam) / dataPerPage) + 1;
                    } else {
                        pageAsInt = 1;
                    }
                }
            } catch (Exception e) {
            }
            if (dataPerPage > lstdetails.size()) {
                dataPerPage = lstdetails.size();
            }
            int pgInt = pageAsInt - 1;
            int pgIntLast;
            if (pgInt == 0) {
                pgIntLast = dataPerPage;
            } else {
                pgIntLast = (lstdetails.size() - (dataPerPage * (pgInt)));
                pgIntLast = (dataPerPage * (pgInt) + pgIntLast);
                if (pgIntLast > (dataPerPage * (pgInt + 1))) {
                    pgIntLast = dataPerPage * (pgInt + 1);
                }
                pgInt = ((dataPerPage) * (pgInt));
            }

            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgIntLast; i++) {
                JSONObject json = new JSONObject();
                json.put("id", i);
                json.put("TYPEORGANISME", lstdetails.get(i).getStr_value2());
                json.put("CODEORGANISME", lstdetails.get(i).getStr_value3());
                json.put("NUMORGANISME", lstdetails.get(i).getStr_value5());
                json.put("COMPTECOMPTABLE", lstdetails.get(i).getStr_value4());
                json.put("MONTANTOP", lstdetails.get(i).getStr_value6());
                json.put("MONTANTSOLDE", lstdetails.get(i).getStr_value8());
                json.put("FULNAME", lstdetails.get(i).getStr_value1());
                json.put("CREDIT", lstdetails.get(i).getStr_value7());
                arrayObj.put(json);
            }
            return Response.ok()
                    .entity(new JSONObject().put("data", arrayObj).put("total", lstdetails.size()).toString()).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "recap organisme", e);
            return Response.ok().entity(new JSONObject().put("data", new JSONArray()).put("total", 0).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /**
     * Liste des tiers payants pour les ecrans de reglement : port de
     * tierspayantmanagement/tierspayant/ws_search_data.jsp (MEME methode metier ShowAllOrOneTierspayant, memes cles
     * JSON). Le decoupage respecte start/limit quand l'ecran les envoie (la JSP decoupait toujours par 10, d'ou des
     * pages identiques quand la taille de page differait).
     */
    @GET
    @Path("tierspayants")
    public Response tierspayants(@DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("query") String query,
            @DefaultValue("") @QueryParam("lg_TIERS_PAYANT_ID") String lgTiersPayantId,
            @DefaultValue("") @QueryParam("lg_TYPE_TIERS_PAYANT_ID") String lgTypeTiersPayantId,
            @DefaultValue("") @QueryParam("cmb_TYPE_TIERS_PAYANT") String cmbTypeTiersPayant,
            @DefaultValue("0") @QueryParam("start") int start, @DefaultValue("10") @QueryParam("limit") int limit) {
        if (utilisateurSession() == null) {
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        }
        dataManager odm = new dataManager();
        try {
            String search = StringUtils.isNotEmpty(query) ? query : searchValue;
            String tpId = StringUtils.isNotEmpty(lgTiersPayantId) ? lgTiersPayantId : "%%";
            String typeTp = "%%";
            if (StringUtils.isNotEmpty(lgTypeTiersPayantId)) {
                typeTp = lgTypeTiersPayantId;
            }
            if (StringUtils.isNotEmpty(cmbTypeTiersPayant)) {
                typeTp = cmbTypeTiersPayant;
            }
            odm.initEntityManager();
            tierspayantManagement otm = new tierspayantManagement(odm);
            List<TTiersPayant> lst = otm.ShowAllOrOneTierspayant(StringUtils.defaultString(search), tpId, typeTp,
                    commonparameter.statut_enable);
            int fin = limit > 0 ? Math.min(lst.size(), Math.max(0, start) + limit) : lst.size();
            JSONArray arrayObj = new JSONArray();
            for (int i = Math.max(0, start); i < fin; i++) {
                TTiersPayant t = lst.get(i);
                JSONObject json = new JSONObject();
                json.put("lg_TIERS_PAYANT_ID", t.getLgTIERSPAYANTID());
                json.put("str_CODE_ORGANISME", t.getStrCODEORGANISME());
                json.put("str_NAME", t.getStrNAME());
                json.put("str_FULLNAME", t.getStrFULLNAME());
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + lst.size() + " \",\"results\":" + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "liste tiers payants reglement", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /**
     * Types de tiers payant : port de tierspayantmanagement/typetierspayant/ws_data.jsp (MEME requete, memes cles
     * JSON), avec decoupage start/limit.
     */
    @GET
    @Path("types-tierspayant")
    public Response typesTierspayant(@DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("lg_TYPE_TIERS_PAYANT_ID") String lgTypeTiersPayantId,
            @DefaultValue("0") @QueryParam("start") int start, @DefaultValue("20") @QueryParam("limit") int limit) {
        if (utilisateurSession() == null) {
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        }
        dataManager odm = new dataManager();
        try {
            String typeTp = "%%";
            if (StringUtils.isNotEmpty(lgTypeTiersPayantId) && !"ALL".equals(lgTypeTiersPayantId)) {
                typeTp = lgTypeTiersPayantId;
            }
            String search = "%" + StringUtils.defaultString(searchValue) + "%";
            odm.initEntityManager();
            List<TTypeTiersPayant> lst = odm.getEm().createQuery(
                    "SELECT t FROM TTypeTiersPayant t WHERE t.lgTYPETIERSPAYANTID LIKE ?1 AND t.strCODETYPETIERSPAYANT LIKE ?2 AND t.strSTATUT LIKE ?3 ")
                    .setParameter(1, typeTp).setParameter(2, search).setParameter(3, commonparameter.statut_enable)
                    .getResultList();
            int fin = limit > 0 ? Math.min(lst.size(), Math.max(0, start) + limit) : lst.size();
            JSONArray arrayObj = new JSONArray();
            date key = new date();
            for (int i = Math.max(0, start); i < fin; i++) {
                TTypeTiersPayant t = lst.get(i);
                JSONObject json = new JSONObject();
                json.put("lg_TYPE_TIERS_PAYANT_ID", t.getLgTYPETIERSPAYANTID());
                json.put("str_CODE_TYPE_TIERS_PAYANT", t.getStrCODETYPETIERSPAYANT());
                json.put("str_LIBELLE_TYPE_TIERS_PAYANT", t.getStrLIBELLETYPETIERSPAYANT());
                json.put("str_STATUT", t.getStrSTATUT());
                if (t.getDtCREATED() != null) {
                    json.put("dt_CREATED", key.DateToString(t.getDtCREATED(), key.formatterOrange));
                }
                if (t.getDtUPDATED() != null) {
                    json.put("dt_UPDATED", key.DateToString(t.getDtUPDATED(), key.formatterOrange));
                }
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + lst.size() + " \",\"results\":" + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "types tiers payant", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }
}
