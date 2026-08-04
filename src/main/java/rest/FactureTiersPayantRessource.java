package rest;

import bll.Util;
import bll.bllBase;
import bll.entity.EntityData;
import bll.facture.factureManagement;
import bll.preenregistrement.Preenregistrement;
import dal.TFacture;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementDetail;
import dal.TPrivilege;
import dal.TTiersPayant;
import dal.TUser;
import dal.dataManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
import util.DateConverter;

/**
 * Migration REST des webservices JSP de facturation tiers payant (webservices/sm_user/facturation/ws_data.jsp,
 * ws_data_detail_facture.jsp, ws_data_details_vente.jsp, ws_data_detail_tiers_payant.jsp, ws_transaction.jsp).
 *
 * Toutes les operations deleguent aux MEMES methodes metier bll.facture.factureManagement /
 * bll.preenregistrement.Preenregistrement que les JSP historiques : memes regles, memes messages, memes cles JSON. Le
 * parametre CODEGROUPE de la liste est retire (toujours vide cote metier).
 */
@Path("v1/facture-tiers-payant")
@Produces("application/json")
@Consumes("application/json")
public class FactureTiersPayantRessource {

    private static final Logger LOG = Logger.getLogger(FactureTiersPayantRessource.class.getName());

    @Inject
    private HttpServletRequest servletRequest;

    private TUser utilisateurSession() {
        return (TUser) servletRequest.getSession().getAttribute(commonparameter.AIRTIME_USER);
    }

    private Response reponseDeconnecte() {
        return Response.ok().entity(new JSONObject().put("success", commonparameter.PROCESS_FAILED)
                .put("errors", util.Constant.DECONNECTED_MESSAGE).put("total", 0).toString()).build();
    }

    /**
     * Liste des factures : MEME logique que ws_data.jsp, sans le parametre CODEGROUPE (retire de l'API ; la methode
     * metier recoit une chaine vide, comme quand le champ n'etait pas renseigne).
     */
    @GET
    @Path("list")
    public Response list(@DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("lg_FACTURE_ID") String lgFactureId,
            @DefaultValue("") @QueryParam("lg_TYPE_FACTURE_ID") String lgTypeFactureId,
            @DefaultValue("") @QueryParam("lg_customer_id") String lgCustomerId,
            @DefaultValue("") @QueryParam("dt_debut") String dtDebutParam,
            @DefaultValue("") @QueryParam("dt_fin") String dtFinParam,
            @DefaultValue("") @QueryParam("impayes") String impayesParam,
            @DefaultValue("0") @QueryParam("start") int start, @DefaultValue("20") @QueryParam("limit") int limit) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            date key = new date();
            String impayes = StringUtils.isNotEmpty(impayesParam) ? impayesParam : null;
            String lgFACTUREID = StringUtils.isNotEmpty(lgFactureId) ? lgFactureId : "%%";
            String lgTYPEFACTUREID = StringUtils.isNotEmpty(lgTypeFactureId) ? lgTypeFactureId : "%%";
            String lgCUSTOMERID = StringUtils.isNotEmpty(lgCustomerId) ? lgCustomerId : "%%";
            Date dtFin = StringUtils.isNotEmpty(dtFinParam) ? key.stringToDate(dtFinParam, key.formatterMysqlShort)
                    : key.GetNewDate(0);
            Date dtDebut = StringUtils.isNotEmpty(dtDebutParam)
                    ? key.stringToDate(dtDebutParam, key.formatterMysqlShort) : date.getPreviousMonth(0);
            String odateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
            String odateDebut = key.DateToString(dtDebut, key.formatterMysqlShort2);
            dtDebut = key.getDate(odateDebut, "00:00");
            dtFin = key.getDate(odateFin, "23:59");

            odm.initEntityManager();
            factureManagement ofm = new factureManagement(odm, sessionUser);
            List<TFacture> lstTFacture = ofm.getListFacture(searchValue, lgFACTUREID, lgTYPEFACTUREID, dtDebut, dtFin,
                    lgCUSTOMERID, "", impayes, start, limit);
            int count = ofm.getListFacturesCount(searchValue, lgFACTUREID, lgTYPEFACTUREID, dtDebut, dtFin,
                    lgCUSTOMERID, "", impayes);

            List<TPrivilege> lstTPrivilege = (List<TPrivilege>) servletRequest.getSession()
                    .getAttribute(commonparameter.USER_LIST_PRIVILEGE);
            boolean isALLOWED = DateConverter.hasAuthorityById(lstTPrivilege, Util.ACTIONDELETEINVOICE);
            boolean actionReglerFacture = DateConverter.hasAuthorityById(lstTPrivilege, Util.ACTION_REGLER_FACTURE);
            boolean autorisationAvoirFne = DateConverter.hasAuthorityByName(lstTPrivilege, "AUTORISATION_AVOIR_FNE");

            JSONArray arrayObj = new JSONArray();
            for (TFacture of : lstTFacture) {
                TTiersPayant otp = (TTiersPayant) ofm.getgetOrganisme(of.getLgTYPEFACTUREID().getLgTYPEFACTUREID(),
                        of.getStrCUSTOMER());
                JSONObject json = new JSONObject();
                json.put("fneUrl", of.getFneUrl());
                json.put("fneAvoirReference", of.getFneAvoirReference());
                json.put("fneAvoirUrl", of.getFneAvoirUrl());
                json.put("AUTORISATION_AVOIR_FNE", autorisationAvoirFne);
                json.put("lg_FACTURE_ID", of.getLgFACTUREID());
                json.put("str_CODE_FACTURE", of.getStrCODEFACTURE());
                json.put("int_NB_DOSSIER", of.getIntNBDOSSIER());
                json.put("dt_CREATED", key.DateToString(of.getDtDATEFACTURE(), key.formatterShort));
                String statut = of.getStrSTATUT();
                String codeGroupe = "";
                if ("enable".equals(statut)) {
                    codeGroupe = ofm.getGroupeFacturesCodeByFacture(of.getLgFACTUREID());
                    if (codeGroupe != null) {
                        statut = "group";
                    }
                }
                json.put("str_STATUT", statut);
                json.put("lg_TYPE_FACTURE_ID", of.getLgTYPEFACTUREID().getStrLIBELLE());
                json.put("str_CUSTOMER_NAME", otp.getStrFULLNAME());
                json.put("str_PERIODE", "Du " + key.DateToString(of.getDtDEBUTFACTURE(), key.formatterShort) + " Au "
                        + key.DateToString(of.getDtFINFACTURE(), key.formatterShort));
                json.put("dbl_MONTANT_CMDE", of.getDblMONTANTCMDE());
                json.put("dbl_MONTANT_RESTANT", of.getDblMONTANTRESTANT());
                json.put("dbl_MONTANT_PAYE", of.getDblMONTANTPAYE());
                json.put("MONTANTREMISE", of.getDblMONTANTREMISE());
                json.put("MONTANTFORFETAIRE", of.getDblMONTANTFOFETAIRE());
                json.put("MONTANTBRUT", of.getDblMONTANTBrut());
                json.put("str_CUSTOMER", of.getStrCUSTOMER());
                json.put("CODEGROUPE", codeGroupe);
                json.put("lg_TYPE_TIERS_PAYANT_ID", otp.getLgTYPETIERSPAYANTID().getStrLIBELLETYPETIERSPAYANT());
                json.put("isALLOWED", isALLOWED);
                json.put("ACTION_REGLER_FACTURE", actionReglerFacture);
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + count + " \",\"results\":" + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "list factures", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /**
     * Detail d'une facture (bordereau) : MEME requete et MEME pagination maison (jdom.int_size_pagination) que
     * ws_data_detail_facture.jsp.
     */
    @GET
    @Path("detail-facture")
    public Response detailFacture(@DefaultValue("") @QueryParam("lg_FACTURE_ID") String lgFactureIdParam,
            @DefaultValue("") @QueryParam("search_value") String searchValue,
            @DefaultValue("") @QueryParam("action") String action, @QueryParam("start") String startParam) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            date key = new date();
            String lgFACTUREID = StringUtils.isNotEmpty(lgFactureIdParam) ? lgFactureIdParam : "%%";
            int dataPerPage = jdom.int_size_pagination;
            int pageAsInt = pageDepuisStart(action, startParam, dataPerPage);

            odm.initEntityManager();
            List<dal.TFactureDetail> lstTFactureDetail = odm.getEm().createQuery(
                    "SELECT t FROM TFactureDetail t ,TPreenregistrementCompteClientTiersPayent p  WHERE t.lgFACTUREID.lgFACTUREID = ?1   AND ( p.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.strFIRSTNAME LIKE ?2 OR p.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.strLASTNAME LIKE ?3 OR p.lgPREENREGISTREMENTID.strREFBON LIKE ?4 OR p.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.strNUMEROSECURITESOCIAL LIKE ?5) AND t.strREF=p.lgPREENREGISTREMENTCOMPTECLIENTPAYENTID  ")
                    .setParameter(1, lgFACTUREID).setParameter(2, searchValue + "%").setParameter(3, searchValue + "%")
                    .setParameter(4, searchValue + "%").setParameter(5, searchValue + "%").getResultList();

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

            factureManagement ofm = new factureManagement(odm, sessionUser);
            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgIntLast; i++) {
                try {
                    odm.getEm().refresh(lstTFactureDetail.get(i));
                } catch (Exception er) {
                }
                TPreenregistrementCompteClientTiersPayent op = ofm
                        .GetInfoTierspayant(lstTFactureDetail.get(i).getStrREF());
                String strNOM = op.getLgPREENREGISTREMENTID().getStrFIRSTNAMECUSTOMER();
                String strPRENOM = op.getLgPREENREGISTREMENTID().getStrLASTNAMECUSTOMER();
                String strSECURITESOCIAL = op.getLgPREENREGISTREMENTID().getStrNUMEROSECURITESOCIAL();
                JSONObject json = new JSONObject();
                json.put("lg_DOSSIER_FACTURE_ID", op.getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());
                json.put("str_NOM", strNOM != null ? strNOM : "");
                json.put("str_PRENOM", strPRENOM != null ? strPRENOM : "");
                json.put("str_SECURITE_SOCIAL", strSECURITESOCIAL != null ? strSECURITESOCIAL : "");
                json.put("str_NUM_DOSSIER", op.getLgPREENREGISTREMENTID().getStrREFBON());
                json.put("lg_PREENREGISTREMENT_ID", op.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
                json.put("dt_CREATED", key.DateToString(op.getDtCREATED(), key.formatterShort));
                json.put("MONTANTBRUT", op.getIntPRICE());
                long montantRemise = lstTFactureDetail.get(i).getDblMONTANTREMISE().longValue();
                if (montantRemise == 0) {
                    montantRemise = op.getLgPREENREGISTREMENTID().getIntPRICEREMISE();
                }
                json.put("MONTANTREMISE", montantRemise);
                json.put("dbl_MONTANT", lstTFactureDetail.get(i).getDblMONTANT().longValue());
                json.put("str_REF", op.getLgPREENREGISTREMENTID().getStrREF());
                json.put("str_REF_BON", op.getStrREFBON());
                json.put("int_CUST_PART", op.getLgPREENREGISTREMENTID().getIntCUSTPART());
                json.put("int_PERCENT", op.getIntPERCENT());
                json.put("int_PRICE", op.getLgPREENREGISTREMENTID().getIntPRICE());
                json.put("dt_DATE", date.backabaseUiFormat2.format(op.getLgPREENREGISTREMENTID().getDtUPDATED()));
                json.put("dt_HEURE", date.NomadicUiFormatTime.format(op.getLgPREENREGISTREMENTID().getDtUPDATED()));
                json.put("lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID",
                        op.getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + lstTFactureDetail.size() + "\",\"results\":" + arrayObj.toString() + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "detail facture", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Details d'une vente : MEME logique que ws_data_details_vente.jsp (accessible en GET comme en POST). */
    @GET
    @Path("details-vente")
    public Response detailsVenteGet(@DefaultValue("") @QueryParam("lg_PREENREGISTREMENT_ID") String preenregistrementId,
            @DefaultValue("") @QueryParam("str_STATUT") String statut) {
        return detailsVente(preenregistrementId, statut);
    }

    @POST
    @Path("details-vente")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response detailsVentePost(@DefaultValue("") @FormParam("lg_PREENREGISTREMENT_ID") String preenregistrementId,
            @DefaultValue("") @FormParam("str_STATUT") String statut) {
        return detailsVente(preenregistrementId, statut);
    }

    private Response detailsVente(String preenregistrementIdParam, String statutParam) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            String strSTATUT = StringUtils.isNotEmpty(statutParam) ? statutParam : commonparameter.statut_is_Process;
            String lgPREENREGISTREMENTID = StringUtils.isNotEmpty(preenregistrementIdParam) ? preenregistrementIdParam
                    : "%%";
            odm.initEntityManager();
            Preenregistrement op = new Preenregistrement(odm, sessionUser);
            List<TPreenregistrementDetail> lst = odm.getEm().createQuery(
                    "SELECT t FROM TPreenregistrementDetail t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 ORDER BY t.dtCREATED DESC")
                    .setParameter(1, lgPREENREGISTREMENTID).getResultList();
            odm.getEm().find(TPreenregistrement.class, lgPREENREGISTREMENTID);

            JSONArray arrayObj = new JSONArray();
            for (TPreenregistrementDetail detail : lst) {
                try {
                    odm.getEm().refresh(detail);
                } catch (Exception er) {
                }
                JSONObject json = new JSONObject();
                json.put("lg_PREENREGISTREMENT_DETAIL_ID", detail.getLgPREENREGISTREMENTDETAILID());
                json.put("lg_PREENREGISTREMENT_ID", detail.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
                json.put("str_REF", detail.getLgPREENREGISTREMENTID().getStrREF());
                json.put("lg_FAMILLE_ID", detail.getLgFAMILLEID().getLgFAMILLEID());
                json.put("int_FAMILLE_PRICE", detail.getIntPRICEUNITAIR());
                json.put("str_FAMILLE_NAME", detail.getLgFAMILLEID().getStrNAME());
                json.put("int_QUANTITY", detail.getIntQUANTITY());
                json.put("int_S", detail.getLgFAMILLEID().getIntS());
                json.put("int_T", detail.getLgFAMILLEID().getIntT());
                json.put("int_QUANTITY_SERVED", detail.getIntQUANTITYSERVED());
                json.put("int_PRICE_DETAIL", detail.getIntPRICE());
                json.put("int_CIP", detail.getLgFAMILLEID().getIntCIP());
                json.put("int_EAN13", detail.getLgFAMILLEID().getIntEAN13());
                json.put("dt_CREATED", date.backabaseUiFormat.format(detail.getDtCREATED()));
                arrayObj.put(json);
            }
            int intTotalVente = op.GetVenteTotalwithRemise(lgPREENREGISTREMENTID, strSTATUT);
            Double dblTotalRemise = op.GetAmountRemise(lgPREENREGISTREMENTID, strSTATUT);
            int intTotalProduct = op.GetProductTotal(lgPREENREGISTREMENTID, strSTATUT);

            String result = "{\"total\":\"" + lst.size() + "\",\"results\":" + arrayObj.toString()
                    + ",\"str_MEDECIN\":\"\",\"lg_TYPE_VENTE_ID\":\"\",\"dbl_total_remise\":\"" + dblTotalRemise
                    + "\",\"total_vente\":\"" + intTotalVente + "\",\"int_total_product\": \"" + intTotalProduct
                    + "\"}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "details vente", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /**
     * Assiette de facturation de l'ecran Creer (ventes par tiers payant) : MEME logique metier que
     * ws_data_detail_tiers_payant.jsp. Pagination : si l'ecran envoie limit (ExtJS pageSize), le decoupage respecte
     * start/limit — la JSP decoupait toujours par jdom.int_size_pagination, et quand la taille de page de l'ecran (15)
     * differait, la page suivante renvoyait les memes donnees. Sans limit, l'ancien decoupage est conserve.
     */
    @GET
    @Path("assiette")
    public Response assiette(@DefaultValue("") @QueryParam("dt_debut") String dtDebutParam,
            @DefaultValue("") @QueryParam("dt_fin") String dtFinParam,
            @DefaultValue("") @QueryParam("lg_TYPE_TIERS_PAYANT_ID") String lgTypeTiersPayantId,
            @DefaultValue("") @QueryParam("str_CODE_REGROUPEMENT") String strCodeRegroupement,
            @DefaultValue("") @QueryParam("lg_TIERS_PAYANT") String lgTiersPayant,
            @DefaultValue("") @QueryParam("action") String action, @QueryParam("start") String startParam,
            @DefaultValue("0") @QueryParam("limit") int limit) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            String dtDebut = StringUtils.isNotEmpty(dtDebutParam) ? dtDebutParam
                    : date.formatterMysqlShort.format(new Date()) + " 23:59";
            String dtFin = StringUtils.isNotEmpty(dtFinParam) ? dtFinParam + " 23:59"
                    : date.formatterMysql.format(new Date());
            String typeTp = StringUtils.isNotEmpty(lgTypeTiersPayantId) ? lgTypeTiersPayantId : "%%";
            String codeRegroupement = StringUtils.isNotEmpty(strCodeRegroupement) ? strCodeRegroupement : "%%";
            String tiersPayant = StringUtils.isNotEmpty(lgTiersPayant) ? lgTiersPayant : "%%";
            int dataPerPage = jdom.int_size_pagination;
            int pageAsInt = pageDepuisStart(action, startParam, dataPerPage);

            odm.initEntityManager();
            factureManagement ofm = new factureManagement(odm, sessionUser);
            List<EntityData> listEntityData = ofm.getVenteTiersPayant(dtDebut, dtFin, codeRegroupement, typeTp,
                    tiersPayant);
            double montantTotal = 0;

            int pgInt;
            int pgIntLast;
            if (limit > 0) {
                int startIdx = 0;
                try {
                    if (StringUtils.isNotEmpty(startParam)) {
                        startIdx = Math.max(0, Integer.parseInt(startParam));
                    }
                } catch (NumberFormatException n) {
                }
                pgInt = Math.min(startIdx, listEntityData.size());
                pgIntLast = Math.min(startIdx + limit, listEntityData.size());
            } else {
                if (dataPerPage > listEntityData.size()) {
                    dataPerPage = listEntityData.size();
                }
                pgInt = pageAsInt - 1;
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
            }

            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgIntLast; i++) {
                JSONObject json = new JSONObject();
                json.put("lg_TIERS_PAYANT_ID", listEntityData.get(i).getStr_value2());
                json.put("str_FULLNAME", listEntityData.get(i).getStr_value1());
                json.put("str_ACOUNT_DOSSIER", listEntityData.get(i).getStr_value3());
                json.put("dbl_MONTANT", listEntityData.get(i).getStr_value4());
                json.put("isChecked", "false");
                arrayObj.put(json);
            }
            String result = "{\"total\":\"" + listEntityData.size() + "\",\"results\":" + arrayObj.toString()
                    + ",\"Montant_total\":" + montantTotal + "}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "assiette facturation", e);
            return Response.ok().entity(new JSONObject().put("total", 0).put("results", new JSONArray()).toString())
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /**
     * Creation des factures tiers payant et suppression d'une facture : MEME logique que ws_transaction.jsp (modes
     * "create facture tiers" et "delete"), y compris le stockage en session des factures a imprimer consomme par
     * ws_rp_print_all_invoices.jsp.
     */
    @POST
    @Path("transaction")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response transaction(@DefaultValue("") @FormParam("mode") String mode,
            @DefaultValue("") @FormParam("MODE_SELECTION") String modeSelection,
            @DefaultValue("") @FormParam("dt_debut") String dtDebutParam,
            @DefaultValue("") @FormParam("dt_fin") String dtFinParam,
            @DefaultValue("") @FormParam("str_CODE_REGROUPEMENT") String strCodeRegroupement,
            @DefaultValue("") @FormParam("lg_TYPE_TIERS_PAYANT_ID") String lgTypeTiersPayantId,
            @DefaultValue("") @FormParam("lg_TIERS_PAYANT") String lgTiersPayant,
            @DefaultValue("") @FormParam("uncheckedList") String uncheckedListParam,
            @DefaultValue("") @FormParam("recordsToSend") String recordsToSend,
            @DefaultValue("") @FormParam("lg_FACTURE_ID") String lgFactureId) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        bllBase obllBase = new bllBase();
        try {
            odm.initEntityManager();
            String dtDebut = StringUtils.isNotEmpty(dtDebutParam) ? dtDebutParam
                    : date.formatterMysqlShort.format(new Date());
            String dtFin = StringUtils.isNotEmpty(dtFinParam) ? dtFinParam + " 23:59:59"
                    : date.formatterMysql.format(new Date());
            String codeRegroupement = StringUtils.isNotEmpty(strCodeRegroupement) ? strCodeRegroupement : "%%";
            String typeTp = StringUtils.isNotEmpty(lgTypeTiersPayantId) ? lgTypeTiersPayantId : "%%";
            String tiersPayant = StringUtils.isNotEmpty(lgTiersPayant) ? lgTiersPayant : "%%";
            String uncheckedList = StringUtils.isNotEmpty(uncheckedListParam) ? uncheckedListParam : "";

            factureManagement ofm = new factureManagement(odm,
                    odm.getEm().find(TUser.class, sessionUser.getLgUSERID()));
            LinkedHashSet<TFacture> invoicesToPrint = new LinkedHashSet<>();

            if ("create facture tiers".equals(mode)) {
                List<EntityData> listEntityData;
                if ("ALL".equals(modeSelection)) {
                    JSONArray array = new JSONArray(uncheckedList);
                    listEntityData = ofm.getVenteTiersPayant(dtDebut, dtFin, codeRegroupement, typeTp, tiersPayant);
                    if (array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            for (int j = 0; j < listEntityData.size(); j++) {
                                if (listEntityData.get(j).getStr_value2().equals(array.getString(i))) {
                                    listEntityData.remove(j);
                                }
                            }
                        }
                        for (int j = 0; j < listEntityData.size(); j++) {
                            creerFacturesPourTiersPayant(ofm, obllBase, invoicesToPrint,
                                    listEntityData.get(j).getStr_value2(), dtDebut, dtFin);
                        }
                    } else {
                        for (EntityData oEntityData : listEntityData) {
                            creerFacturesPourTiersPayant(ofm, obllBase, invoicesToPrint, oEntityData.getStr_value2(),
                                    dtDebut, dtFin);
                        }
                    }
                } else if ("SELECTED".equals(modeSelection)) {
                    JSONArray array = new JSONArray(uncheckedList);
                    listEntityData = ofm.getVenteTiersPayant(dtDebut, dtFin, codeRegroupement, typeTp, tiersPayant);
                    if (array.length() > 0) {
                        for (EntityData oEntityData : listEntityData) {
                            for (int i = 0; i < array.length(); i++) {
                                if (!oEntityData.getStr_value2().equals(array.getString(i))) {
                                    creerFacturesPourTiersPayant(ofm, obllBase, invoicesToPrint,
                                            oEntityData.getStr_value2(), dtDebut, dtFin);
                                }
                            }
                        }
                    } else {
                        JSONArray arrayselected = new JSONArray(recordsToSend);
                        for (int i = 0; i < arrayselected.length(); i++) {
                            creerFacturesPourTiersPayant(ofm, obllBase, invoicesToPrint, arrayselected.getString(i),
                                    dtDebut, dtFin);
                        }
                    }
                } else if ("OTHERS".equals(modeSelection)) {
                    listEntityData = ofm.getVenteTiersPayant(dtDebut, dtFin, codeRegroupement, typeTp, tiersPayant);
                    for (EntityData oEntityData : listEntityData) {
                        creerFacturesPourTiersPayant(ofm, obllBase, invoicesToPrint, oEntityData.getStr_value2(),
                                dtDebut, dtFin);
                    }
                }
            } else if ("delete".equals(mode)) {
                boolean result = ofm.deleteInvoice(lgFactureId, sessionUser);
                if (result) {
                    obllBase.setMessage(commonparameter.PROCESS_SUCCESS);
                } else {
                    obllBase.setMessage(commonparameter.PROCESS_FAILED);
                    // remonte le motif reel du refus (ex : facture certifiee FNE) au lieu d'un message generique
                    if (ofm.getDetailmessage() != null) {
                        obllBase.setDetailmessage(ofm.getDetailmessage().replace("\"", "'"));
                    }
                }
            }
            servletRequest.getSession().setAttribute("invoicesToPrint", invoicesToPrint);

            String result = "{\"success\":\"" + obllBase.getMessage() + "\", \"errors\": \""
                    + obllBase.getDetailmessage() + "\"}";
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "transaction facturation", e);
            // remonte la cause reelle a l'ecran (la JSP historique laissait partir une erreur 500)
            String cause = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                cause = e.getCause().getMessage();
            }
            return Response.ok().entity(new JSONObject().put("success", commonparameter.PROCESS_FAILED)
                    .put("errors", "Génération impossible : " + cause).toString()).build();
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Bloc commun de creation des factures d'un tiers payant, identique a celui repete dans ws_transaction.jsp. */
    private void creerFacturesPourTiersPayant(factureManagement ofm, bllBase obllBase,
            LinkedHashSet<TFacture> invoicesToPrint, String tiersPayantId, String dtDebut, String dtFin)
            throws Exception {
        List<TPreenregistrementCompteClientTiersPayent> list = ofm.getListVenteTiersPayantBIS(tiersPayantId,
                date.formatterMysqlShort.parse(dtDebut), date.formatterMysql.parse(dtFin), "%%", "%%");
        LinkedList<TFacture> factures = ofm.createInvoices(list, date.formatterMysqlShort.parse(dtDebut),
                date.formatterMysql.parse(dtFin), tiersPayantId);
        if (!factures.isEmpty()) {
            for (TFacture e : factures) {
                invoicesToPrint.add(e);
            }
            obllBase.setMessage(commonparameter.PROCESS_SUCCESS);
        } else {
            obllBase.setMessage(commonparameter.PROCESS_FAILED);
        }
    }

    /** Reproduit la "logic de gestion des page" des JSP historiques : start -> numero de page. */
    private int pageDepuisStart(String action, String startParam, int dataPerPage) {
        int pageAsInt = 0;
        try {
            if (!"filltable".equals(action)) {
                if (startParam != null) {
                    int intPage = Integer.valueOf(startParam);
                    intPage = (intPage / dataPerPage) + 1;
                    pageAsInt = intPage;
                } else {
                    pageAsInt = 1;
                }
            }
        } catch (Exception e) {
        }
        return pageAsInt;
    }
}
