/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import commonTasks.dto.ArticleDTO;
import commonTasks.dto.Params;
import dal.TUser;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.json.JSONException;
import org.json.JSONObject;
import rest.service.CommandeService;
import rest.service.OrderService;
import rest.service.dto.CommandeCsvDTO;
import rest.service.dto.CommandeFiltre;
import rest.service.dto.OrderDetailDTO;
import toolkits.parameters.commonparameter;
import util.Constant;

/**
 * @author DICI
 */
@Path("v1/commande")
@Produces("application/json")
@Consumes("application/json")
public class CommandeRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    CommandeService commandeService;
    @EJB
    OrderService orderService;

    @PUT
    @Path("validerbl/{id}")
    public Response cloturerBonLivration(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        JSONObject json = commandeService.cloturerBonLivraison(id, tu);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("clotureinventaire/{id}")
    public Response cloturerInventaire(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        JSONObject json = commandeService.cloturerInvetaire(id, tu);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("cip")
    public Response updateCip(Params params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        JSONObject json = commandeService.createProduct(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("creerbl")
    public Response creerBl(Params params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        params.setOperateur(tu);
        JSONObject json = orderService.creerBonLivraison(params);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("updateorderitem")
    public Response modifierProduitCommande(ArticleDTO dto) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        try {
            orderService.modificationProduitCommandeEncours(dto, tu);
            return Response.ok().entity(new JSONObject().put("success", true).toString()).build();
        } catch (Exception e) {

            return Response.ok().entity(new JSONObject().put("success", false).toString()).build();
        }

    }

    @POST
    @Path("update/scheduled")
    public Response updateScheduled(Params params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        JSONObject json = orderService.updateScheduled(params.getRef(), params.isScheduled());
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("commande-en-cours-items")
    public Response reglements(@DefaultValue("ALL") @QueryParam(value = "filtre") CommandeFiltre filtre,
            @QueryParam(value = "orderId") String orderId, @QueryParam(value = "start") int start,
            @QueryParam(value = "query") String query, @QueryParam(value = "limit") int limit) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        JSONObject jsono = orderService.fetchOrderItems(filtre, orderId, query, start, limit);
        return Response.ok().entity(jsono.toString()).build();
    }

    @POST
    @Path("orderitem-prix-vente")
    public Response modifierProduitPrixVenteCommandeEnCours(ArticleDTO dto) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        try {
            orderService.modifierProduitPrixVenteCommandeEnCours(dto, tu);
            return Response.ok().entity(new JSONObject().put("success", true).toString()).build();

        } catch (Exception e) {

            return Response.ok().entity(new JSONObject().put("success", false).toString()).build();
        }

    }

    @GET
    @Path("list")
    public Response findAll(@QueryParam(value = "start") int start, @QueryParam(value = "limit") int limit,
            @QueryParam(value = "query") String query) {

        return Response.ok().entity(this.orderService
                .fetch(query, Set.of(Constant.STATUT_IS_PROGRESS, Constant.STATUT_PHARMA), start, limit).toString())
                .build();
    }

    @DELETE
    @Path("item/{id}")
    public Response delete(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        this.orderService.removeItem(id);
        return Response.ok().build();
    }

    @GET
    @Path("amount/{id}")
    public Response getCommandeAmount(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        return Response.ok().entity(this.orderService.getCommandeAmount(id).toString()).build();
    }

    @POST
    @Path("item/add")
    public Response addItem(OrderDetailDTO orderDetail) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }

        return Response.ok().entity(this.orderService.addItem(orderDetail, tu).toString()).build();
    }

    @GET
    @Path("list/passees")
    public Response findCommandePassees(@QueryParam(value = "start") int start, @QueryParam(value = "limit") int limit,
            @QueryParam(value = "query") String query) {
        return Response.ok()
                .entity(this.orderService.fetch(query, Set.of(Constant.STATUT_PASSED), start, limit).toString())
                .build();
    }

    @GET
    @Path("export-csv")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportToCsv(@QueryParam("id") String commandId) {
        Map<String, List<CommandeCsvDTO>> map = orderService.commandeEncoursCsv(commandId);
        StreamingOutput output = (OutputStream out) -> {
            try {

                Writer writer = new OutputStreamWriter(out, "UTF-8");

                try (CSVPrinter printer = CSVFormat.EXCEL.withDelimiter(';').print(writer)) {

                    map.forEach((k, v) -> {
                        v.forEach(o -> {
                            try {
                                printer.printRecord(o.getCode(), o.getQte());
                            } catch (IOException ex) {
                                Logger.getLogger(CommandeRessource.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });

                    });

                }
            } catch (IOException ex) {
                throw new WebApplicationException("File Not Found !!");
            }
        };

        String filename = "commande_" + map.keySet().stream().findFirst().get() + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("H_mm_ss")) + ".csv";
        return Response.ok(output, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename).build();

    }

    @GET
    @Path("statut/{id}/passe")
    public Response passerCommande(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        this.orderService.passerLaCommande(id);
        return Response.ok().build();
    }

    @GET
    @Path("statut/{id}/rollback")
    public Response passerCommandeEnCours(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult(Constant.DECONNECTED_MESSAGE)).build();
        }
        this.orderService.changerEnCommandeEnCours(id);
        return Response.ok().build();
    }
}
