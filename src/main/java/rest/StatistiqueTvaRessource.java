package rest;

import commonTasks.dto.Params;
import dal.TUser;
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
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;
import rest.service.TvaDataService;

/**
 *
 * @author koben
 */
@Path("v3")
@Produces("application/json")
@Consumes("application/json")
public class StatistiqueTvaRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private TvaDataService dataService;

    @GET
    @Path("tvas/criterion")
    public Response tvastatCriterion(@QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd) throws JSONException {
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

    @GET
    @Path("tvas")
    public Response tvastat(@QueryParam(value = "typeVente") String typeVente, @QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        Params params = new Params();
        params.setDtEnd(dtEnd);
        params.setDtStart(dtStart);
        params.setOperateur(tu);
        JSONObject json;
        if (StringUtils.isNotBlank(typeVente) && !typeVente.equalsIgnoreCase("TOUT")) {
            json = dataService.statistiqueTvaVnoOnlyView(params);
        } else {
            json = dataService.statistiqueTvaView(params);
        }

        return Response.ok().entity(json.toString()).build();
    }

}
