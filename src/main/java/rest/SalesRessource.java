/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import commonTasks.dto.ClotureVenteParams;
import commonTasks.dto.MedecinDTO;
import commonTasks.dto.QueryDTO;
import commonTasks.dto.SalesParams;
import dal.TPreenregistrement;
import dal.TUser;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import rest.qualifier.SalesPrimary;
import rest.service.GenerateTicketService;
import rest.service.SalesService;
import rest.service.SmsService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author DICI
 */
@Path("v1/vente")
@Produces("application/json")
@Consumes("application/json")
public class SalesRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @Inject
    @SalesPrimary
    SalesService salesService;
    @EJB
    GenerateTicketService generateTicketService;
    @EJB
    SmsService smsService;

    @POST
    @Path("ticket/vno")
    public Response getTicket(ClotureVenteParams clotureVenteParams) throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        clotureVenteParams.setUserId(tu);
        JSONObject json = generateTicketService.lunchPrinterForTicket(clotureVenteParams);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("ticket/vno/{id}")
    public Response getTicketById(@PathParam("id") String id) throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        JSONObject json = generateTicketService.lunchPrinterForTicket(id);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("copy/{id}")
    public Response getTicketCoy(@PathParam("id") String id) throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        JSONObject json = generateTicketService.generateticket10(id);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("ticket/vo")
    public Response getTicketVo(ClotureVenteParams clotureVenteParams) throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        clotureVenteParams.setUserId(tu);
        JSONObject json = generateTicketService.lunchPrinterForTicketVo(clotureVenteParams);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("ticket/vo/{id}")
    public Response getTicketVo(@PathParam("id") String id) throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        JSONObject json = generateTicketService.lunchPrinterForTicketVo(id);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("add/vno")
    public Response addVente(SalesParams params) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        params.setUserId(tu);
        params.setStatut(commonparameter.statut_is_Process);
        JSONObject json = salesService.createPreVente(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("add/assurance")
    public Response addVenteAssurance(SalesParams params) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        params.setUserId(tu);
        params.setStatut(commonparameter.statut_is_Process);
        JSONObject json = salesService.createPreVenteVo(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("devis")
    public Response addDevis(SalesParams params) {
        JSONObject json = new JSONObject();
        try {
            HttpSession hs = servletRequest.getSession();
            TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
            if (tu == null) {
                return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
            }
            params.setUserId(tu);
            params.setDevis(true);
            params.setStatut(commonparameter.statut_is_Devis);
            json = salesService.faireDevis(params);

        } catch (JSONException ex) {

        }
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("remise")
    public Response addRemise(SalesParams params) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = new JSONObject();
        try {
            json = salesService.addRemisse(params);

        } catch (JSONException ex) {

        }
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("cloturer/vno")
    public Response cloturerVno(ClotureVenteParams clotureVenteParams) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        clotureVenteParams.setUserId(tu);
        JSONObject json = salesService.updateVenteClotureComptant(clotureVenteParams);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("net/vno")
    public Response netPayer(SalesParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.shownetpayVno(params);

        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("net/assurance")
    public Response netPayerAssurance(SalesParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.shownetpayVo(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("cloturer/assurance")
    public Response cloturerAssurance(ClotureVenteParams clotureVenteParams) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        clotureVenteParams.setUserId(tu);
        JSONObject json = salesService.updateVenteClotureAssurance(clotureVenteParams);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("clotureravoir/{id}")
    public Response clotureravoir(@PathParam("id") String id) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.clotureravoir(id, tu);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("updatetypevente")
    public Response updatetypevente(SalesParams params) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.transformerVente(params);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("removetp/{compteClientId}/{venteId}")
    public Response removetp(@PathParam("compteClientId") String compteClientId, @PathParam("venteId") String venteId) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject jsono = salesService.removetierspayant(compteClientId, venteId);
        return Response.ok().entity(jsono.toString()).build();
    }

    @POST
    @Path("addtp/{venteId}")
    public Response addtp(@PathParam("venteId") String venteId, SalesParams params) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject jsono = salesService.addtierspayant(venteId, params);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("annulation/{id}")
    public Response deleteVente(@PathParam("id") String id) {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        JSONObject jsono = salesService.annulerVente(tu, id);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("search/{id}")
    public Response searchProductById(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject jsono = salesService.produits(id, tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("search")
    public Response searchProduct(@QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, @QueryParam(value = "query") String query) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        QueryDTO body = new QueryDTO();
        body.setLimit(limit);
        body.setStart(start);
        body.setQuery(query);
        body.setEmplacementId(tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        JSONObject jsono = salesService.produits(body, false);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("deatails")
    public Response getDetails(@QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, @QueryParam(value = "query") String query,
            @QueryParam(value = "venteId") String venteId, @QueryParam(value = "statut") String statut
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        QueryDTO body = new QueryDTO();
        body.setLimit(limit);
        body.setStart(start);
        body.setQuery(query);
        body.setStatut(statut);
        body.setVenteId(venteId);
        body.setEmplacementId(tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        JSONObject jsono = salesService.detailsVente(body, false);
        return Response.ok().entity(jsono.toString()).build();
    }

    @POST
    @Path("add/item")
    public Response addItemVente(SalesParams params) {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        params.setUserId(tu);
        JSONObject json = salesService.addPreenregistrementItem(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("remove/vno/item/{id}")
    public Response removeItemVente(@PathParam("id") String itemId) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        TPreenregistrement tp = salesService.removePreenregistrementDetail(itemId);
        JSONObject json = salesService.shownetpayVno(tp);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("remove/depot/item/{id}")
    public Response removeItemVentedepot(@PathParam("id") String itemId) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        TPreenregistrement tp = salesService.removePreenregistrementDetail(itemId);
        JSONObject json = salesService.shownetpaydepotAgree(tp);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("update/item/vno")
    public Response updateItemVente(SalesParams params) {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        params.setUserId(tu);
        JSONObject json = salesService.updateTPreenregistrementDetail(params);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("quantite-vente/{id}")
    public Response nobreproduits(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        JSONObject jsono = new JSONObject();
        jsono.put("success", true).put("data", salesService.nbreProduitsByVente(id));
        return Response.ok().entity(jsono.toString()).build();
    }

    @POST
    @Path("add/depot")
    public Response addVenteDepot(SalesParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        params.setUserId(tu);
        params.setDepot(true);
        params.setTypeVenteId((params.getTypeDepoId().equals("1") ? "4" : "5"));
        params.setStatut(commonparameter.statut_is_Process);
        JSONObject json = salesService.createPreVenteVo(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("remise-depot")
    public Response addRemiseDepot(SalesParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        JSONObject json = salesService.updatRemiseVenteDepot(params.getVenteId(), params.getRemiseDepot());
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("clotureVenteDepot")
    public Response clotureVenteDepot(ClotureVenteParams clotureVenteParams) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);

        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        clotureVenteParams.setUserId(tu);
        JSONObject json = salesService.clotureVenteDepot(clotureVenteParams);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("clotureVenteDepotAgree")
    public Response clotureVenteDepotAgree(ClotureVenteParams clotureVenteParams) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        clotureVenteParams.setUserId(tu);
        JSONObject json = salesService.clotureVenteDepotAgree(clotureVenteParams);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("cheick-caisse")
    public Response checkCaisse() throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = new JSONObject();
        boolean b = salesService.checkCaisse(tu);
        json.put("success", true).put("data", b);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("shownetpaydepotAgree")
    public Response shownetpaydepotAgree(SalesParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.shownetpaydepotAgree(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("update/client")
    public Response updateVenteClient(SalesParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.updateclient(params);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("findone/{id}")
    public Response findByQuery(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject jsono = salesService.findOneproduit(id, tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        return Response.ok().entity(jsono.toString()).build();
    }

    @PUT
    @Path("retmoveClient/{id}")
    public Response retmoveClient(@PathParam("id") String venteId) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.removeClientToVente(venteId);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("modifiertypevente/{id}")
    public Response modifiertypevente(@PathParam("id") String venteId, ClotureVenteParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.modifiertypevente(venteId, params);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("client/{id}")
    public Response updateCurrentVenteClientData(@PathParam("id") String venteId, SalesParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.mettreAjourDonneesClientVenteExistante(venteId, params);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("modifier-vente-terme/{id}")
    public Response modifierventeterme(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.modificationVenteCloturee(id, tu);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("tp/{id}")
    public Response modifiertierspayantprincipal(@PathParam("id") String id, ClotureVenteParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.modificationVentetierpayantprincipal(id, params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("ticket/depot/{id}")
    public Response getTicketDepot(@PathParam("id") String id) throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = generateTicketService.lunchPrinterForTicketDepot(id);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("ticket/depot")
    public Response getTicketDepot(ClotureVenteParams clotureVenteParams) throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        clotureVenteParams.setUserId(tu);
        JSONObject json = generateTicketService.lunchPrinterForTicketDepot(clotureVenteParams);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("net/outstanding")
    public Response shownetpayVoWithEncour(SalesParams params) throws JSONException {
        JSONObject json = salesService.shownetpayVoWithEncour(params);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("testticket")
    public Response testticket(@QueryParam("id") String id) throws JSONException {
        generateTicketService.generateTicketOnFly(id);
        return Response.ok().entity(new JSONObject().put("success", true)).build();
    }

    @POST
    @Path("update/medecin")
    public Response updateMedecin(SalesParams params) {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        params.setUserId(tu);
        JSONObject json = salesService.updateMedecin(params.getVenteId(), params.getMedecinId());
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("add/medecin/{id}")
    public Response addMedecin(@PathParam("id") String id, MedecinDTO params) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.updateMedecin(id, params);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("update/infosclienttp/{id}")
    public Response updateClientOrTierpayant(@PathParam("id") String id, SalesParams salesParams) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        salesParams.setVenteId(id);
        JSONObject json = salesService.updateClientOrTierpayant(salesParams);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("find/infosclienttpforupdating")
    public Response findVenteForUpdationg(@QueryParam("id") String id) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.findVenteForUpdationg(id);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("updateclientortierpayant")
    public Response updateClientOrTierpayant(SalesParams salesParams) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        salesParams.setUserId(tu);
        JSONObject json = salesService.updateClientOrTierpayant(salesParams);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("gettoken")
    public Response testtoken() throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject jsono = smsService.findAccessToken();
        return Response.ok().entity(jsono.toString()).build();
    }
   
  /*  @GET
    @Path("sendsms")
    public Response sendSms() throws JSONException {

        Sms sms = new Sms();
        sms.setMessage("kobena testt");
//        sms.setReceiverAddres("57591746");
        mes.submit(sms);
        Mail mail = new Mail();
        mail.setMessage("Test laborex ");
//        mail.setReceiverAddres("badoukobena@gmail.com");
        mail.setSubject("cloture de caisse ");
        mes.submit(mail);
        return Response.ok().build();
    }
    @Resource(name = "concurrent/__defaultManagedExecutorService")
    ManagedExecutorService mes;*/
    
    
    
}
