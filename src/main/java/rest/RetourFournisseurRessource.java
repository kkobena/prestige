/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import commonTasks.dto.RetourDetailsDTO;
import commonTasks.dto.RetourFournisseurDTO;
import dal.TUser;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import rest.service.RetourFournisseurService;
import toolkits.parameters.commonparameter;
import util.Constant;

/**
 *
 * @author koben
 */
@Path("v1/retourfournisseur")
@Produces("application/json")
@Consumes("application/json")
public class RetourFournisseurRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private RetourFournisseurService retourFournisseurService;

    @GET
    @Path("retours-items")
    public Response loadDetailsRetouFournisseurs(
            @QueryParam(value = "retourId") String retourId
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        List<RetourDetailsDTO> data = retourFournisseurService.loadDetailRetourFournisseur(retourId);
        return Response.ok().entity(ResultFactory.getSuccessResult(data, data.size())).build();
    }

    @POST
    @Path("new")
    public Response create(
            RetourFournisseurDTO params
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        params.setUser(tu);
        
        RetourFournisseurDTO data = retourFournisseurService.createRetour(params);
        if (data != null) {
            return Response.ok().entity(ResultFactory.getSuccessResult(data, 1)).build();
        }
        return Response.ok().entity(ResultFactory.getFailResult("L'opération a échoué . Veuillez vérifier la quantité à retourner")).build();
    }

    @POST
    @Path("add-item")
    public Response addItem(
            RetourDetailsDTO params
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        RetourDetailsDTO data = retourFournisseurService.addItem(params);
        if (data != null) {
            return Response.ok().entity(ResultFactory.getSuccessResult(data, 1)).build();
        }
        return Response.ok().entity(ResultFactory.getFailResult("L'opération a échoué . Veuillez vérifier la quantité à retourner")).build();
    }

    @POST
    @Path("update-item")
    public Response updateItem(
            RetourDetailsDTO params
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        RetourDetailsDTO data = retourFournisseurService.updateItem(params);
        if (data != null) {
            return Response.ok().entity(ResultFactory.getSuccessResult(data, 1)).build();
        }
        return Response.ok().entity(ResultFactory.getFailResult("L'opération a échoué . Veuillez vérifier la quantité à retourner")).build();
    }

    @DELETE
    @Path("remove-item/{id}")
    public Response removeItem(
          @PathParam("id") String id
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        retourFournisseurService.removeItem(id);
        return Response.ok().entity(ResultFactory.getSuccessResultMsg()).build();

    }
    
     @PUT
    @Path("full-bl/{id}")
    public Response returnFullBonLivraison( @PathParam("id") String id,
            RetourDetailsDTO params
    ) throws CloneNotSupportedException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        retourFournisseurService.returnFullBonLivraison(id,params.getLgMOTIFRETOUR(),tu);
       
        return Response.ok().entity(ResultFactory.getSuccessResultMsg()).build();
    }
}
