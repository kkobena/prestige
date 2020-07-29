/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dal.TUser;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.MvtProduitService;
import toolkits.parameters.commonparameter;

/**
 *
 * @author koben
 */
@Path("v1/depot")
@Produces("application/json")
@Consumes("application/json")
public class DepotRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private MvtProduitService mvtProduitService;

    @PUT
    @Path("validerretourdepot/{id}")
    public Response validerRetourDepot(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = mvtProduitService.validerRetourDepot(id, tu);
        return Response.ok().entity(json.toString()).build();
    }

}
