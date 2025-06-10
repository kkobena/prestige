package rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import rest.service.InventaireFamilleService;

/**
 *
 * @author airman
 */

@Path("v1/inventaire")
@Produces("application/json")
@Consumes("application/json")
public class InventaireFamilleRessource {

    @EJB
    private InventaireFamilleService inventaireFamilleService;

    @GET
    @Path("inv")
    // methode avec parametre
    public Response getAllInventaireFamilles(@QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, @QueryParam(value = "invId") String invId) {
        return Response.ok().entity(inventaireFamilleService.getAllInventaireFamilles(invId, start, limit).toString())
                .build();
    }
}
