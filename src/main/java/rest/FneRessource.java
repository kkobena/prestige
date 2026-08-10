/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dal.TPrivilege;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
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
    public Response createAvoirGroupe(@QueryParam(value = "ids") String ids) {
        if (!hasAvoirPrivilege()) {
            return forbidden();
        }
        JSONObject compteRendu = fneService.createGroupeAvoir(ids);
        return Response.ok(compteRendu.put("success", compteRendu.optInt("emis", 0) > 0).toString()).build();
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
