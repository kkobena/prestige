/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dal.TUser;
import java.time.LocalDate;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.DashBoardService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author DICI
 */
@Path("v1/recap")
@Produces("application/json")
@Consumes("application/json")
public class DashBoardRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    DashBoardService dashBoardService;

    @GET
    @Path("credits")
    public Response donneesCreditAccordes(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd,
            @QueryParam(value = "start") int start,
            @QueryParam(value = "query") String query,
            @QueryParam(value = "limit") int limit
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject jsono = dashBoardService.donneesCreditAccordesView(LocalDate.parse(dtStart), LocalDate.parse(dtEnd),  tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), tu, query, start, limit, false);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("reglements")
    public Response reglements(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd,
            @QueryParam(value = "start") int start,
            @QueryParam(value = "query") String query,
            @QueryParam(value = "limit") int limit
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        JSONObject jsono = dashBoardService.donneesReglementsTpView(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), tu, query, start, limit, false);
        return Response.ok().entity(jsono.toString()).build();
    }
    
     @GET
    @Path("dashboard")
    public Response donneesRecaps(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd
           
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject jsono = dashBoardService.donneesRecapActiviteView(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), tu, "");
        return Response.ok().entity(jsono.toString()).build();
    }
}
