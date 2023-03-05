package rest;

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
import org.json.JSONObject;
import rest.service.BalanceService;
import rest.service.dto.BalanceParamsDTO;
import toolkits.parameters.commonparameter;
import util.Constant;

/**
 *
 * @author koben
 */
@Path("v1/balance-vente")
@Produces("application/json")
@Consumes("application/json")
public class BalanceVenteRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private BalanceService balanceService;

    @GET
    @Path("/data")
    public Response getBalanceVente(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        
        JSONObject json = balanceService.getBalanceVenteCaisseDataView(BalanceParamsDTO.builder()
                .dtStart(dtStart)
                .dtEnd(dtEnd)
                .emplacementId(tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID())
                .build());
        return Response.ok().entity(json.toString()).build();
    }
}
