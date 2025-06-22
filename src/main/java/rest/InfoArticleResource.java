package rest;

import java.util.HashMap;
import rest.service.InfoArticleService;
import rest.service.dto.InfoArticleDTO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 *
 * @author airman
 */

@Path("v1/info")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InfoArticleResource {

    /*
     * @Inject private InfoArticleService infoArticleService;
     *
     * @GET public Response getInfoArticles(@QueryParam("start") @DefaultValue("0") int start,
     *
     * @QueryParam("limit") @DefaultValue("25") int limit, @QueryParam("search") String searchTerm) {
     *
     * try { List<InfoArticleDTO> articles = infoArticleService.getInfoArticles(start, limit, searchTerm); return
     * Response.ok(articles).build(); } catch (Exception e) { return
     * Response.status(Response.Status.INTERNAL_SERVER_ERROR) .entity("Erreur lors de la récupération des articles: " +
     * e.getMessage()).build(); } }
     */

    @Inject
    private InfoArticleService infoArticleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfoArticles(@QueryParam("start") @DefaultValue("0") int start,
            @QueryParam("limit") @DefaultValue("25") int limit, @QueryParam("search") String searchTerm) {

        List<InfoArticleDTO> articles = infoArticleService.getInfoArticles(start, limit, searchTerm);
        Long total = infoArticleService.countInfoArticles(searchTerm);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", articles);
        response.put("total", total);

        return Response.ok(response).build();
    }
}