package rest;

import dal.TUser;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.GroupeInvoiceService;
import toolkits.parameters.commonparameter;
import util.Constant;

/**
 *
 * @author airman
 */

@Path("v1/groupe-invoices")
@Produces("application/json")
@Consumes("application/json")
public class GroupeInvoiceRessource {

    @Inject
    private HttpServletRequest servletRequest;

    @EJB
    private GroupeInvoiceService groupeInvoiceService;

    @GET
    public Response list(@QueryParam("dtStart") String dtStart, @QueryParam("dtEnd") String dtEnd,
            @QueryParam("search_value") String searchValue, // compat JSP
            @QueryParam("query") String query, // compat ExtJS
            @QueryParam("lg_GROUPE_ID") Integer lgGroupeId, // compat JSP
            @QueryParam("lgGroupeId") Integer lgGroupeId2, // nouveau
            @QueryParam("codeGroupe") String codeGroupe, @DefaultValue("0") @QueryParam("start") int start,
            @DefaultValue("20") @QueryParam("limit") int limit) throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        // on unifie les paramètres (ancien/new)
        String q = (query != null && !query.isEmpty()) ? query : searchValue;
        Integer gid = (lgGroupeId2 != null) ? lgGroupeId2 : lgGroupeId;

        // ACTION_REGLER_FACTURE: si tu as déjà une fonction ailleurs, on peut l’injecter.
        // Pour ne pas casser, on met false par défaut ici.
        boolean actionReglerFacture = false;

        JSONObject json = groupeInvoiceService.getGroupeInvoices(dtStart, dtEnd, q, gid, codeGroupe,
                actionReglerFacture, start, limit);

        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("details")
    public Response details(@QueryParam("CODEFACTURE") String codeFacture, // compat jsp (CODEFACTURE = code groupe)
            @QueryParam("codeGroupe") String codeGroupe, @QueryParam("lgTP") String lgTP,
            @QueryParam("search_value") String searchValue, @QueryParam("query") String query,
            @DefaultValue("0") @QueryParam("start") int start, @DefaultValue("20") @QueryParam("limit") int limit)
            throws JSONException {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        String q = (query != null && !query.isEmpty()) ? query : searchValue;

        // priorité : codeGroupe, sinon CODEFACTURE
        String cg = (codeGroupe != null && !codeGroupe.isEmpty()) ? codeGroupe : codeFacture;

        JSONObject json = groupeInvoiceService.getGroupeInvoiceDetails(cg, lgTP, q, start, limit);

        return Response.ok().entity(json.toString()).build();
    }
}
