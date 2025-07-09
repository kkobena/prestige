package rest;

import dal.TUser;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import rest.service.BalanceService;
import rest.service.dto.BalanceParamsDTO;
import toolkits.parameters.commonparameter;
import static toolkits.parameters.enumExtentionFiles.LOG;
import util.Constant;

/**
 *
 * @author koben
 */
@Path("v1/balance")
@Produces("application/json")
@Consumes("application/json")
public class BalanceVenteRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private BalanceService balanceService;

    @GET
    @Path("/balancesalecash")
    public Response balanceCaisse(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(Constant.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        JSONObject json = balanceService.getBalanceVenteCaisseDataView(BalanceParamsDTO.builder().dtStart(dtStart)
                .dtEnd(dtEnd).emplacementId(tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID()).build());
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("/balancesalecash/carnet")
    public Response balanceCaisseCarnet(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        JSONObject json = balanceService.getBalanceVenteCaisseDataView(BalanceParamsDTO.builder().dtStart(dtStart)
                .dtEnd(dtEnd).showAllAmount(true).emplacementId(tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID()).build());
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("/balancesalecash/carnet-depot")
    public Response balanceCaisseCarnetDepot(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd) {

        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        JSONObject json = balanceService.getBalanceVenteCaisseDataView(BalanceParamsDTO.builder().dtStart(dtStart)
                .dtEnd(dtEnd).emplacementId(tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID()).showAllAmount(true).build());

        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("etat-annuel")
    public Response etatLastThreeYears() {

        JSONObject json = balanceService.etatLastThreeYears();
        return Response.ok().entity(json.toString()).build();

    }

    @GET
    @Path("/balancesalecashdepot")
    public Response balanceCaisse(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd, @QueryParam(value = "emplacementId") String emplacementId) {

        if (emplacementId == null || emplacementId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Le paramètre emplacementId est obligatoire")
                    .build();
        }

        BalanceParamsDTO params = BalanceParamsDTO.builder().dtStart(dtStart).dtEnd(dtEnd).emplacementId(emplacementId)
                .build();
        JSONObject json;

        if ("ALL".equalsIgnoreCase(emplacementId)) {
            // Appelle la nouvelle méthode pour le cumul
            json = balanceService.getBalanceForAllDepots(params);
        } else {
            // Comportement existant
            json = balanceService.getBalanceVenteCaisseDataView(params);
        }

        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("/print-balancesalecashdepot")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response printBalanceCaisse(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd, @QueryParam(value = "emplacementId") String emplacementId) {

        try {
            BalanceParamsDTO params = BalanceParamsDTO.builder().dtStart(dtStart).dtEnd(dtEnd)
                    .emplacementId(emplacementId).build();
            byte[] data = balanceService.generateBalanceReport(params);

            return Response.ok(data, MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"balance_depots.pdf\"").build();

        } catch (Exception e) {
            // LOG.log(java.util.logging.Level.SEVERE, "Erreur lors de la génération du PDF", e);
            return Response.serverError().entity("Erreur interne du serveur lors de la génération du rapport.").build();
        }
    }
}
