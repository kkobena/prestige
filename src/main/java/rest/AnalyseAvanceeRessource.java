
package rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import rest.service.AnalyseAvanceeService;
import rest.service.AnalyseAvanceeService.AnalyseAvanceeDTO;

/**
 *
 * @author airman
 */
@Path("v1/analyse-avancee")
public class AnalyseAvanceeRessource {

    @EJB
    private AnalyseAvanceeService analyseAvanceeService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnalyse(@FormParam("inventaireId") String inventaireId,
            @FormParam("inventaireName") String inventaireName) {
        if (inventaireId == null || inventaireId.trim().isEmpty()) {
            AnalyseAvanceeDTO errorResponse = new AnalyseAvanceeDTO();
            errorResponse.setSuccess(false);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        try {
            // Appel corrigé avec les deux paramètres
            AnalyseAvanceeDTO data = analyseAvanceeService.getAnalyseAvancee(inventaireId, inventaireName);
            return Response.ok(data).build();
        } catch (Exception e) {
            e.printStackTrace();
            AnalyseAvanceeDTO errorResponse = new AnalyseAvanceeDTO();
            errorResponse.setSuccess(false);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }
}