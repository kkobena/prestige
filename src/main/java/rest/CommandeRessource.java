/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import commonTasks.dto.ArticleDTO;
import commonTasks.dto.Params;
import dal.TOrderDetail;
import dal.TUser;
import java.util.concurrent.atomic.LongAdder;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.CommandeService;
import rest.service.OrderService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author DICI
 */
@Path("v1/commande")
@Produces("application/json")
@Consumes("application/json")
public class CommandeRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    CommandeService commandeService;
    @EJB
    OrderService orderService;

    @PUT
    @Path("validerbl/{id}")
    public Response cloturerBonLivration(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = commandeService.cloturerBonLivraison(id, tu);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("clotureinventaire/{id}")
    public Response cloturerInventaire(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = commandeService.cloturerInvetaire(id, tu);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("cip")
    public Response updateCip(Params params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = commandeService.createProduct(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("creerbl")
    public Response creerBl(Params params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        params.setOperateur(tu);
        JSONObject json = orderService.creerBonLivraison(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("updateorderitem")
    public Response modifierProduitCommande(ArticleDTO DTO) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        try {
            TOrderDetail detail = orderService.modificationProduitCommandeEncours(DTO, tu);
            LongAdder PRIX_ACHAT_TOTAL = new LongAdder();
            LongAdder PRIX_VENTE_TOTAL = new LongAdder();
            orderService.findByOrderId(detail.getLgORDERID().getLgORDERID()).forEach(p -> {
                PRIX_ACHAT_TOTAL.add(p.getIntPRICE());
                PRIX_VENTE_TOTAL.add(p.getIntNUMBER() * p.getIntPRICEDETAIL());
            });
            return Response.ok().entity(new JSONObject().put("success", true)
                    .put("prixAchat", PRIX_ACHAT_TOTAL.intValue())
                    .put("prixVente", PRIX_VENTE_TOTAL.intValue())
                    //                    .put("ref", detail.getLgORDERID().getStrREFORDER())
                    .toString()).build();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Response.ok().entity(new JSONObject().put("success", false).toString()).build();
        }

    }

    @POST
    @Path("update/scheduled")
    public Response updateScheduled(Params params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = orderService.updateScheduled(params.getRef(), params.isScheduled());
        return Response.ok().entity(json.toString()).build();
    }
}
