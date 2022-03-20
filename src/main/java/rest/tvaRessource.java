/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import commonTasks.dto.TvaDTO;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import rest.service.TvaService;

/**
 *
 * @author koben
 */
@Path("v2/tva")
@Produces("application/json")
@Consumes("application/json")
public class tvaRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private TvaService tvaService;

    @GET
    @Path("list")
    public Response tvastat(@QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd, @QueryParam(value = "typeVente") String typeVente) throws JSONException {
      /*  HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }*/

        List<TvaDTO> json = tvaService.tva(LocalDate.parse(dtStart), LocalDate.parse(dtEnd), false, null);
        return Response.ok().entity(json).build();
    }
}
