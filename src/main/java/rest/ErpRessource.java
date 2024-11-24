/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import rest.service.ErpService;
import rest.service.RetourFournisseurService;
import rest.service.impl.DataExportService;

/**
 *
 * @author koben
 */
@Path("v1")
@Produces("application/json")
@Consumes("application/json")
public class ErpRessource {

    @EJB
    private ErpService erpService;
    @EJB
    private RetourFournisseurService retourFournisseurService;
    @EJB
    private DataExportService dataExportService;

    @GET
    @Path("valorisation")
    public Response valorisation(@QueryParam(value = "dtJour") String dtJour) {
        return Response.ok().entity(erpService.valorisation(dtJour)).build();
    }

    @GET
    @Path("ca-comptant")
    public Response caComptant(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd) {
        return Response.ok().entity(erpService.caComptant(dtStart, dtEnd)).build();
    }

    @GET
    @Path("ca-credit")
    public Response caCredis(@QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd) {
        return Response.ok().entity(erpService.rrpTiersPayant(dtStart, dtEnd)).build();
    }

    @GET
    @Path("reglements")
    public Response reglements(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd) {
        return Response.ok().entity(erpService.erpReglements(dtStart, dtEnd)).build();
    }

    @GET
    @Path("factures")
    public Response erpFactures(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd) {
        return Response.ok().entity(erpService.erpFactures(dtStart, dtEnd)).build();
    }

    @GET
    @Path("fournisseurs")
    public Response fournisseurs() {
        return Response.ok().entity(erpService.fournisseurs()).build();
    }

    @GET
    @Path("achats-fournisseurs")
    public Response achatsFournisseurs(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd) {
        return Response.ok().entity(erpService.achatsFournisseurs(dtStart, dtEnd)).build();
    }

    @GET
    @Path("stock")
    public Response export() {
        return Response.ok().entity(dataExportService.listProduits()).build();
    }

    @GET
    @Path("tierspayants")
    public Response allTiersPayants() {
        return Response.ok().entity(erpService.allTiersPayants()).build();
    }

    @GET
    @Path("avoirs-fournisseurs")
    public Response avoirFournisseurs(@QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd) {
        return Response.ok().entity(retourFournisseurService.erpAvoirsFournisseurs(dtStart, dtEnd)).build();
    }

    @GET
    @Path("whareouse-vno")
    public Response venteVNO(@QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd) {
        return Response.ok().entity(dataExportService.listVentes(dtStart, dtEnd, "VNO")).build();
    }

    @GET
    @Path("whareouse-maxmin")
    public Response maxmin() {
        return Response.ok().entity(dataExportService.getMaxAndMinDate()).build();
    }

    @GET
    @Path("ws/groupe-tierspayants")
    public Response allGroupeTiersPayants() {
        return Response.ok().entity(erpService.allGroupeTiersPayants()).build();
    }

    @GET
    @Path("ws/tierspayants")
    public Response allWsTiersPayants() {
        return Response.ok().entity(erpService.allWsTiersPayants()).build();
    }

    @GET
    @Path("ws/clients")
    public Response allWsClients() {
        return Response.ok().entity(erpService.allWsClients()).build();
    }
}
