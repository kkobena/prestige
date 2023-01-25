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
import javax.ws.rs.DefaultValue;
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
import rest.service.CommandeService;
import rest.service.OrderService;
import rest.service.dto.CommandeFiltre;
import toolkits.parameters.commonparameter;
import util.Constant;

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
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
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
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
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
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
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
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        params.setOperateur(tu);
        JSONObject json = orderService.creerBonLivraison(params);
        return Response.ok().entity(json.toString()).build();
    }
    
    @POST
    @Path("updateorderitem")
    public Response modifierProduitCommande(ArticleDTO dto) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        try {
            TOrderDetail detail = orderService.modificationProduitCommandeEncours(dto, tu);
            return Response.ok().entity(computeOrderSummary(detail.getLgORDERID().getLgORDERID())
                    .toString()).build();
            
        } catch (Exception e) {
            
            return Response.ok().entity(new JSONObject().put("success", false).toString()).build();
        }
        
    }
    
    @POST
    @Path("update/scheduled")
    public Response updateScheduled(Params params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        JSONObject json = orderService.updateScheduled(params.getRef(), params.isScheduled());
        return Response.ok().entity(json.toString()).build();
    }
    
    @GET
    @Path("commande-en-cours-items")
    public Response reglements(
            @DefaultValue("ALL") @QueryParam(value = "filtre") CommandeFiltre filtre,
            @QueryParam(value = "orderId") String orderId,
            @QueryParam(value = "start") int start,
            @QueryParam(value = "query") String query,
            @QueryParam(value = "limit") int limit
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        
        JSONObject jsono = orderService.fetchOrderItems(filtre, orderId, query, start, limit);
        return Response.ok().entity(jsono.toString()).build();
    }
    
    
      @POST
    @Path("orderitem-prix-vente")
    public Response modifierProduitPrixVenteCommandeEnCours(ArticleDTO dto) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        try {
            String orderId= orderService.modifierProduitPrixVenteCommandeEnCours(dto, tu);
            return Response.ok().entity(computeOrderSummary(orderId)
                    .toString()).build();
            
        } catch (Exception e) {
            
            return Response.ok().entity(new JSONObject().put("success", false).toString()).build();
        }
        
    }
    
    
    private JSONObject computeOrderSummary(String orderId) {
        LongAdder prixAchatTotal = new LongAdder();
        LongAdder prixVenteTotal = new LongAdder();
        orderService.findByOrderId(orderId).forEach(p -> {
            prixAchatTotal.add(p.getIntPRICE());
            prixVenteTotal.add(p.getIntNUMBER() * p.getIntPRICEDETAIL());
        });
        return new JSONObject().put("success", true)
                .put("prixAchat", prixAchatTotal.intValue())
                .put("prixVente", prixVenteTotal.intValue());
    }
}
