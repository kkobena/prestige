/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import commonTasks.dto.GenererFactureDTO;
import commonTasks.dto.ReglementCarnetDTO;
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
import rest.service.CarnetAsDepotService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author koben
 */
@Path("v2/carnet-depot")
@Produces("application/json")
@Consumes("application/json")
public class CarnetDepotRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private CarnetAsDepotService carnetAsDepotService;

    @GET
    @Path("list")
    public Response fetchAll(
            @QueryParam(value = "query") String query, @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit) {
        JSONObject json = carnetAsDepotService.all(start, limit, query, null);
        return Response.ok().entity(json.toString()).build();

    }

    @POST
    @Path("exclure")
    public Response exclure(GenererFactureDTO datas) throws JSONException {
        carnetAsDepotService.setAsDepot(datas);
        return Response.ok().build();
    }

    @PUT
    @Path("exclure-inclure/{id}/{isDepot}")
    public Response exclureOrInclureById(@PathParam("id") String id, @PathParam("isDepot") boolean isDepot) {
        carnetAsDepotService.update(id, isDepot);
        return Response.ok().build();
    }

    @GET
    @Path("list-exclus")
    public Response fetchExclud(
            @QueryParam(value = "query") String query, @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit) {
        JSONObject json = carnetAsDepotService.all(start, limit, query, true);
        return Response.ok().entity(json.toString()).build();

    }

    @GET
    @Path("ventes")
    public Response fetchVenteByTiersPayant(
            @QueryParam(value = "tiersPayantId") String tiersPayantId, @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd, @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit) {
        JSONObject json = carnetAsDepotService.fetchVenteByTiersPayant(tiersPayantId, dtStart, dtEnd, start, limit);
        return Response.ok().entity(json.toString()).build();

    }

    @GET
    @Path("reglements")
    public Response reglementsCarnet(
            @QueryParam(value = "tiersPayantId") String tiersPayantId, @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd, @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit) {
        JSONObject json = carnetAsDepotService.reglementsCarnet(tiersPayantId, dtStart, dtEnd, start, limit);
        return Response.ok().entity(json.toString()).build();

    }

    @PUT
    @Path("regler/{tiersPayantId}")
    public Response exclureOrInclureById(@PathParam("tiersPayantId") String id, ReglementCarnetDTO o) {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        JSONObject json = carnetAsDepotService.faireReglement(o.setTiersPayantId(id), tu);
        return Response.ok().entity(json.toString()).build();
    }
}
