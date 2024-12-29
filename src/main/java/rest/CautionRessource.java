/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import rest.service.CautionTiersPayantService;
import rest.service.dto.AddCautionDTO;

/**
 *
 * @author koben
 */
@Path("v1/cautions")
@Produces("application/json")
@Consumes("application/json")
public class CautionRessource {

    @EJB
    private CautionTiersPayantService cautionTiersPayantService;

    @GET
    public Response findAll(@QueryParam(value = "start") int start, @QueryParam(value = "limit") int limit,
            @QueryParam(value = "tiersPayantId") String tiersPayantId) {

        return Response.ok().entity(cautionTiersPayantService.fetch(tiersPayantId, start, limit).toString()).build();
    }

    @POST
    public Response add(AddCautionDTO addCaution) {
        cautionTiersPayantService.addCaution(addCaution);
        return Response.ok().entity(ResultFactory.getSuccessResultMsg()).build();
    }

    @DELETE
    @Path("{id}")
    public Response supprimerCaution(@PathParam("id") String id) {

        return Response.ok().entity(cautionTiersPayantService.supprimerCaution(id).toString()).build();
    }

    @PUT
    public Response update(AddCautionDTO addCaution) {

        return Response.ok().entity(cautionTiersPayantService.update(addCaution).toString()).build();
    }

    @GET
    @Path("/historiques")
    public Response getHistoriques(@QueryParam(value = "idCaution") String idCaution) {
        return Response.ok().entity(cautionTiersPayantService.getHistoriquesView(idCaution).toString()).build();
    }

    @GET
    @Path("/ventes")
    public Response getVentes(@QueryParam(value = "idCaution") String idCaution) {
        return Response.ok().entity(cautionTiersPayantService.getVentesView(idCaution).toString()).build();
    }
}
