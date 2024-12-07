/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import rest.service.CaisseService;
import rest.service.LotService;

/**
 *
 * @author Hermann N'ZI
 */

@Path("v1/lot")
@Produces("application/json")
@Consumes("application/json")
public class LotRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private LotService lotService;

    @GET
    @Path("listlot")
    public Response getAllLots(@QueryParam(value = "start") int start, @QueryParam(value = "limit") int limit,
            @QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd) {
        JSONObject json = lotService.getAllLots(dtStart, dtEnd, limit, start);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("listlots")
    public Response getLots() {
        JSONObject json = lotService.getLots();
        return Response.ok().entity(json.toString()).build();
    }
}
