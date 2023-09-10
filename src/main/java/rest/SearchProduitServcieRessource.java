package rest;

import dal.TPrivilege;
import dal.TUser;
import java.util.List;
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
import rest.service.SearchProduitServcie;
import toolkits.parameters.commonparameter;
import util.Constant;

/**
 *
 * @author koben
 */
@Path("v1/produit-search")
@Produces("application/json")
@Consumes("application/json")
public class SearchProduitServcieRessource {

    @Inject
    private HttpServletRequest servletRequest;
    private @EJB SearchProduitServcie searchProduitServcie;

    // '../webservices/configmanagement/grossiste/ws_data.jsp'
    @GET
    @Path("fiche")
    public Response getAll(@QueryParam(value = "start") int start, @QueryParam(value = "limit") int limit,
            @QueryParam(value = "search_value") String search, @QueryParam(value = "str_TYPE_TRANSACTION") String type,
            @QueryParam(value = "lg_DCI_ID") String diciId, @QueryParam(value = "produitId") String produitId)
            throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        List<TPrivilege> attribute = (List<TPrivilege>) hs.getAttribute(Constant.USER_LIST_PRIVILEGE);
        JSONObject jsono = this.searchProduitServcie.fetchProduits(attribute, tu, produitId, search, diciId, type,
                limit, start);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("/produits")
    public Response getProduits(@QueryParam(value = "start") int start, @QueryParam(value = "limit") int limit,
            @QueryParam(value = "search_value") String search, @QueryParam(value = "produitId") String produitId)
            throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        JSONObject jsono = this.searchProduitServcie.fetchOrderProduits(tu, produitId, search, limit, start);
        return Response.ok().entity(jsono.toString()).build();
    }
}
