package rest;

import bll.common.Parameter;
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
import rest.service.EtatControlBonService;

/**
 *
 * @author koben
 */
@Path("v1/etat-control-bon")
@Produces("application/json")
@Consumes("application/json")
public class EtatControlBonResource {
    
    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private EtatControlBonService etatControlBonService;
    
    @GET
    @Path("list")
    public Response list(
            @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, @QueryParam(value = "search") String search,
            @QueryParam(value = "grossisteId") String grossisteId,
            @QueryParam(value = "dtStart") String dtStart, 
            @QueryParam(value = "dtEnd") String dtEnd) {
        boolean returnFullBLLAuthority = Utils.hasAuthorityByName(Utils.getconnectedUserPrivileges(servletRequest), Parameter.ACTION_RETURN_FULL_BL);
        etatControlBonService.hasReturnFullBLLAuthority(returnFullBLLAuthority);
        JSONObject json = etatControlBonService.list(search, dtStart, dtEnd, grossisteId, start, limit);
        return Response.ok().entity(json.toString()).build();
        
    }
}
