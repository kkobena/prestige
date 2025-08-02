
package rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import rest.service.PointDepotService;

/**
 *
 * @author airman
 */
@Path("v1/pointdepot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PointDepotRessource {

    @EJB
    private PointDepotService pointDepotService;

    @GET
    public Response getPointDepot(@QueryParam("dtStart") String dtStart, @QueryParam("dtEnd") String dtEnd,
            @QueryParam("emplacementId") String emplacementId) {

        JSONObject json = pointDepotService.getPointDepot(dtStart, dtEnd, emplacementId);
        return Response.ok(json.toString()).build();
    }
}