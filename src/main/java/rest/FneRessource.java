/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dal.TPrivilege;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import rest.service.exception.FneExeception;
import rest.service.fne.FneService;
import rest.service.fne.TypeInvoice;
import util.CommonUtils;
import util.Constant;

/**
 *
 * @author koben
 */
@Path("v1/fne")
@Produces("application/json")
@Consumes("application/json")
public class FneRessource {

    private static final Logger LOG = Logger.getLogger(FneRessource.class.getName());

    @EJB
    private FneService fneService;
    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("invoices/sign/{id}/{typeInvoice}")
    public Response getSign(@PathParam("id") String id, @PathParam("typeInvoice") TypeInvoice typeInvoice) {
        try {
            fneService.createInvoice(id, typeInvoice);
            return Response.ok(new JSONObject().put("success", true).toString()).build();
        } catch (FneExeception e) {
            // motif du refus remonte a l'ecran (facture deja certifiee, avoir deja emis...)
            // au lieu d'une erreur technique sans explication
            return badRequest(e);
        }
    }

    @GET
    @Path("invoices/sign-group")
    public Response getSignGroupInvoice(@QueryParam(value = "ids") String ids,
            @QueryParam(value = "typeInvoice") TypeInvoice typeInvoice) {
        JSONObject compteRendu = fneService.createGroupeInvoice(ids, typeInvoice);
        // aucune facture certifiee et aucun echec technique : toutes etaient deja certifiees
        boolean succes = compteRendu.optInt("certifiees", 0) > 0;
        return Response.ok(compteRendu.put("success", succes).toString()).build();
    }

    /**
     * Emission d'un avoir FNE total sur une facture certifiee (API FNE #2 /refund).
     */
    @POST
    @Path("invoices/avoir/{id}")
    public Response createAvoirTotal(@PathParam("id") String id) {
        if (!hasAvoirPrivilege()) {
            return forbidden();
        }
        try {
            return Response.ok(fneService.createAvoirTotal(id).toString()).build();
        } catch (FneExeception e) {
            return badRequest(e);
        }
    }

    /**
     * Avoirs totaux sur toutes les factures certifiees d'une facture de GROUPE, emis une par une.
     *
     * Meme principe que la certification de groupe : une facture qui porte deja un avoir n'est jamais renvoyee, une
     * facture non certifiee est ignoree, et le compte rendu detaille ce qui a ete fait.
     */
    @POST
    @Path("invoices/avoir-group")
    // Un POST d'ExtJS transmet ses parametres dans le CORPS de la requete, pas dans l'URL : avec le
    // seul @QueryParam, ids arrivait toujours vide et l'appel echouait en erreur technique. On lit
    // donc le corps (@FormParam) en priorite, et l'URL en secours pour un appel construit a la main.
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createAvoirGroupe(@FormParam(value = "ids") String idsCorps,
            @QueryParam(value = "ids") String idsUrl) {
        if (!hasAvoirPrivilege()) {
            return forbidden();
        }
        String ids = StringUtils.defaultIfEmpty(idsCorps, idsUrl);
        if (StringUtils.isEmpty(ids)) {
            LOG.log(Level.WARNING, "avoir-group appele sans identifiants de factures");
            return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("success", false).put(
                    "message",
                    "Aucune facture transmise : rouvrez la liste des factures de groupe " + "et relancez l'opération.")
                    .toString()).build();
        }
        try {
            JSONObject compteRendu = fneService.createGroupeAvoir(ids);
            return Response.ok(compteRendu.put("success", compteRendu.optInt("emis", 0) > 0).toString()).build();
        } catch (Exception e) {
            // sans ce filet, une erreur technique remontait en HTTP 500 sans message : l'ecran
            // affichait alors son texte par defaut, qui accusait le privilege a tort
            LOG.log(Level.SEVERE, "avoir-group : echec pour les factures " + ids, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new JSONObject().put("success", false)
                            .put("message",
                                    "L'émission des avoirs a échoué pour une raison technique : "
                                            + StringUtils.defaultIfEmpty(e.getMessage(), e.getClass().getSimpleName())
                                            + ". Le détail figure dans le journal du serveur.")
                            .toString())
                    .build();
        }
    }

    /**
     * Rattachement manuel d'une facture certifiee avant la gestion des avoirs : le corps de la requete est le JSON de
     * la reponse de certification (ou de la facture FNE avec id + items).
     */
    @POST
    @Path("invoices/rattacher/{id}")
    public Response rattacherFacture(@PathParam("id") String id, String body) {
        if (!hasAvoirPrivilege()) {
            return forbidden();
        }
        try {
            return Response.ok(fneService.rattacherFacture(id, body).toString()).build();
        } catch (FneExeception e) {
            return badRequest(e);
        }
    }

    /**
     * Tentative de recuperation automatique des identifiants FNE d'une ancienne facture a partir de son URL de
     * verification.
     */
    @POST
    @Path("invoices/recuperer/{id}")
    public Response recupererFacture(@PathParam("id") String id) {
        if (!hasAvoirPrivilege()) {
            return forbidden();
        }
        try {
            return Response.ok(fneService.recupererDepuisToken(id).toString()).build();
        } catch (FneExeception e) {
            return badRequest(e);
        }
    }

    /**
     * Releve FNE d'un tiers payant (factures certifiees / avoirs / solde net) sur une periode. Consultation seule.
     */
    @GET
    @Path("releve")
    public Response releveFne(@QueryParam("tiersPayantId") String tiersPayantId, @QueryParam("dtStart") String dtStart,
            @QueryParam("dtEnd") String dtEnd) {
        try {
            return Response.ok(fneService.releveFne(tiersPayantId, dtStart, dtEnd).toString()).build();
        } catch (FneExeception e) {
            return badRequest(e);
        }
    }

    private boolean hasAvoirPrivilege() {
        HttpSession hs = servletRequest.getSession();
        List<TPrivilege> privileges = (List<TPrivilege>) hs.getAttribute(Constant.USER_LIST_PRIVILEGE);
        return privileges != null && CommonUtils.hasAuthorityByName(privileges, Constant.AUTORISATION_AVOIR_FNE);
    }

    private Response forbidden() {
        return Response.status(Response.Status.FORBIDDEN).entity(new JSONObject().put("success", false)
                .put("message", "Vous n'avez pas le privilege : autorisation d'avoir FNE").toString()).build();
    }

    private Response badRequest(FneExeception e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new JSONObject().put("success", false).put("message", e.getMessage()).toString()).build();
    }

}
