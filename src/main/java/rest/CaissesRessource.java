/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import commonTasks.dto.Params;
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
import rest.service.CaisseService;
import rest.service.SalesStatsService;
import toolkits.parameters.commonparameter;
import rest.service.TvaDataService;

/**
 *
 * @author koben
 */
@Path("v2/caisse")
@Produces("application/json")
@Consumes("application/json")
public class CaissesRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private CaisseService caisseService;
    @EJB
    private SalesStatsService salesStatsService;
    @EJB
    private TvaDataService dataService;

    @GET
    @Path("balancesalecash")
    public Response balanceCaisse(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        JSONObject json = caisseService.balanceVenteCaisseVersion2(dtSt, dtEn, true, tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        return Response.ok().entity(json.toString()).build();
    }

    @Deprecated
    public Response tvastatDeprecated(@QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        Params params = new Params();
        params.setDtEnd(dtEnd);
        params.setDtStart(dtStart);
        params.setOperateur(tu);
        JSONObject json = salesStatsService.tvasData(params);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("balanceparas")
    public Response balancepara(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        JSONObject json = caisseService.balancePara(dtSt, dtEn, tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("tvas")
    public Response tvastat(@QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        Params params = new Params();
        params.setDtEnd(dtEnd);
        params.setDtStart(dtStart);
        params.setOperateur(tu);
        JSONObject json = dataService.statistiqueTvaViewSomeCriteria(params);
      
        return Response.ok().entity(json.toString()).build();
    }
}
