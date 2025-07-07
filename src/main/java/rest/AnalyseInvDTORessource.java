/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import rest.service.AnalyseInvDTOService;
import rest.service.dto.AnalyseInvDTO;

/**
 *
 * @author airman
 */
@Path("v1/analyse-inventaire") // Chemin de base de l'API
@Produces(MediaType.APPLICATION_JSON) // Produit du JSON par défaut
@Consumes(MediaType.APPLICATION_JSON) // Consomme du JSON par défaut
public class AnalyseInvDTORessource {

    @EJB
    private AnalyseInvDTOService analyseInvDTOService;

    /**
     * Endpoint pour récupérer la liste des données d'analyse pour un inventaire. Accessible via une requête GET à
     * /api/v1/analyse-inventaire?inventaireId=xxx
     *
     * @param inventaireId
     *            L'ID de l'inventaire passé en paramètre d'URL.
     *
     * @return Une réponse HTTP contenant la liste des DTOs en JSON.
     */
    @GET
    public Response getAnalyseInventaire(@QueryParam("inventaireId") String inventaireId) {
        if (inventaireId == null || inventaireId.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Le paramètre 'inventaireId' est manquant.")
                    .build();
        }

        List<AnalyseInvDTO> data = analyseInvDTOService.listAnalyseInv(inventaireId);

        // La conversion de la liste en JSON est gérée automatiquement par JAX-RS
        return Response.ok(data).build();
    }
}
