/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import bll.common.Parameter;
import commonTasks.dto.ClotureVenteParams;
import commonTasks.dto.Params;
import commonTasks.dto.SalesStatsParams;
import commonTasks.dto.TiersPayantParams;
import dal.TPrivilege;
import dal.TUser;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.CommonService;
import rest.service.GenerateTicketService;
import rest.service.SalesStatsService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author Kobena
 */
@Path("v1/ventestats")
@Produces("application/json")
@Consumes("application/json")
public class SalesStatsRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    SalesStatsService salesService;
    @EJB
    CommonService commonService;
    @EJB
    GenerateTicketService generateTicketService;

    @GET
    @Path("preventes")
    public Response getDetails(@QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, @QueryParam(value = "query") String query,
            @QueryParam(value = "typeVenteId") String typeVenteId, @QueryParam(value = "statut") String statut
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        List<TPrivilege> LstTPrivilege = (List<TPrivilege>) hs.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
        boolean asAuthority = DateConverter.hasAuthorityByName(LstTPrivilege, commonparameter.str_SHOW_VENTE);
        boolean allActivitis = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_SHOW_ALL_ACTIVITY);
        SalesStatsParams body = new SalesStatsParams();
        body.setLimit(limit);
        body.setStart(start);
        body.setQuery(query);
        body.setStatut(statut);
        body.setAll(false);
        body.setTypeVenteId(typeVenteId);
        body.setShowAll(asAuthority);
        body.setShowAllActivities(allActivitis);
        body.setUserId(tu);
        JSONObject jsono = salesService.getListeTPreenregistrement(body);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("annulations")
    public Response annulations(
            @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, 
            @QueryParam(value = "query") String query,
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd, 
            @QueryParam(value = "statut") String statut
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        List<TPrivilege> LstTPrivilege = (List<TPrivilege>) hs.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
        boolean asAuthority = DateConverter.hasAuthorityByName(LstTPrivilege, commonparameter.str_SHOW_VENTE);
        boolean allActivitis = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_SHOW_ALL_ACTIVITY);
        SalesStatsParams body = new SalesStatsParams();
        body.setLimit(limit);
        body.setStart(start);
        body.setQuery(query);
        body.setStatut(commonparameter.statut_is_Closed);
        body.setAll(false);
        body.setShowAll(asAuthority);
        body.setShowAllActivities(allActivitis);
        body.setUserId(tu);
        try {
            body.setDtEnd(LocalDate.parse(dtEnd));
            body.setDtStart(LocalDate.parse(dtStart));
        } catch (Exception e) {
        }

        JSONObject jsono = salesService.annulations(body);
        return Response.ok().entity(jsono.toString()).build();
    }

    @POST
    @Path("remove/{id}")
    public Response delete(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.delete(id);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("update/{venteId}")
    public Response update(@PathParam("venteId") String venteId, String statut) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.trash(venteId, statut);
        return Response.ok().entity(json).build();
    }

    @GET
    @Path("devis")
    public Response allDevis(@QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, @QueryParam(value = "query") String query,
            @QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd, @QueryParam(value = "statut") String statut
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        List<TPrivilege> LstTPrivilege = (List<TPrivilege>) hs.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
        boolean asAuthority = DateConverter.hasAuthorityByName(LstTPrivilege, commonparameter.str_SHOW_VENTE);
        boolean allActivitis = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_SHOW_ALL_ACTIVITY);
        SalesStatsParams body = new SalesStatsParams();
        body.setLimit(limit);
        body.setStart(start);
        body.setQuery(query);
        body.setStatut(statut);
        try {
            body.setDtEnd(LocalDate.parse(dtEnd));
            body.setDtStart(LocalDate.parse(dtStart));
        } catch (Exception e) {
        }

        body.setAll(false);
        body.setShowAll(asAuthority);
        body.setShowAllActivities(allActivitis);
        body.setUserId(tu);

        JSONObject jsono = salesService.getListeTPreenregistrement(body);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("preventes-depot")
    public Response preventeDepotOnly(@QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, @QueryParam(value = "query") String query,
            @QueryParam(value = "typeVenteId") String typeVenteId, @QueryParam(value = "statut") String statut
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        List<TPrivilege> LstTPrivilege = (List<TPrivilege>) hs.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
        boolean asAuthority = DateConverter.hasAuthorityByName(LstTPrivilege, commonparameter.str_SHOW_VENTE);
        boolean allActivitis = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_SHOW_ALL_ACTIVITY);
        SalesStatsParams body = new SalesStatsParams();
        body.setLimit(limit);
        body.setStart(start);
        body.setQuery(query);
        body.setStatut(statut);
        body.setAll(false);
        body.setTypeVenteId(typeVenteId);
        body.setShowAll(asAuthority);
        body.setShowAllActivities(allActivitis);
        body.setUserId(tu);
        body.setDepotOnly(true);
        JSONObject jsono = salesService.getListeTPreenregistrement(body);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("depot/{id}")
    public Response getOne(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.findVenteById(id);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("{id}")
    public Response findOne(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.reloadVenteById(id);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    public Response getAlls(
            @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit,
            @QueryParam(value = "query") String query,
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd,
            @QueryParam(value = "hStart") String hStart,
            @QueryParam(value = "hEnd") String hEnd,
            @QueryParam(value = "sansBon") boolean sansBon,
            @QueryParam(value = "onlyAvoir") boolean onlyAvoir,
            @QueryParam(value = "typeVenteId") String typeVenteId
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        List<TPrivilege> LstTPrivilege = (List<TPrivilege>) hs.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
        boolean asAuthority = DateConverter.hasAuthorityByName(LstTPrivilege, commonparameter.str_SHOW_VENTE);
        boolean allActivitis = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_SHOW_ALL_ACTIVITY);
        boolean canCancel = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_BT_ANNULER_VENTE);
        boolean modification = DateConverter.hasAuthorityByName(LstTPrivilege, DateConverter.P_BT_MODIFICATION_DE_VENTE);
        SalesStatsParams body = new SalesStatsParams();
        body.setCanCancel(canCancel);
        body.setLimit(limit);
        body.setStart(start);
        body.setQuery(query);
        body.setTypeVenteId(typeVenteId);
        body.setStatut(commonparameter.statut_is_Closed);
        body.setAll(false);
        body.setSansBon(sansBon);
        body.setOnlyAvoir(onlyAvoir);
        body.setShowAll(asAuthority);
        body.setShowAllActivities(allActivitis);
        body.setUserId(tu);
        body.setModification(modification);
        try {
            body.sethEnd(LocalTime.parse(hEnd));
        } catch (Exception e) {
        }
        try {
            body.sethStart(LocalTime.parse(hStart));
        } catch (Exception e) {
        }
        try {
            body.setDtEnd(LocalDate.parse(dtEnd));
            body.setDtStart(LocalDate.parse(dtStart));
        } catch (Exception e) {
        }
        JSONObject jsono = salesService.listeVentes(body);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("ticket/vno/{id}")
    public Response getTicket(@PathParam("id") String id) throws JSONException {
        JSONObject json = generateTicketService.lunchPrinterForTicket(id);
        return Response.ok().entity(json).build();
    }

    @GET
    @Path("ticket/vo/{id}")
    public Response getTicketVo(@PathParam("id") String id) throws JSONException {
        JSONObject json = generateTicketService.lunchPrinterForTicketVo(id);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("avoirs")
    public Response getAllsAvoir(@QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit, @QueryParam(value = "query") String query,
            @QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd,
            @QueryParam(value = "hStart") String hStart,
            @QueryParam(value = "hEnd") String hEnd,
            @QueryParam(value = "typeVenteId") String typeVenteId
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        List<TPrivilege> LstTPrivilege = (List<TPrivilege>) hs.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
        boolean asAuthority = DateConverter.hasAuthorityByName(LstTPrivilege, commonparameter.str_SHOW_VENTE);
        boolean allActivitis = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_SHOW_ALL_ACTIVITY);
        boolean canCancel = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_BT_ANNULER_VENTE);

        SalesStatsParams body = new SalesStatsParams();
        body.setCanCancel(canCancel);
        body.setLimit(limit);
        body.setStart(start);
        body.setQuery(query);
        body.setTypeVenteId(null);
        body.setStatut(commonparameter.statut_is_Closed);
        body.setAll(false);
        body.setSansBon(false);
        body.setOnlyAvoir(true);
        body.setShowAll(asAuthority);
        body.setShowAllActivities(allActivitis);
        body.setUserId(tu);
        try {
            body.sethEnd(LocalTime.parse(hEnd));
        } catch (Exception e) {
        }
        try {
            body.sethStart(LocalTime.parse(hStart));
        } catch (Exception e) {
        }
        try {
            body.setDtEnd(LocalDate.parse(dtEnd));
            body.setDtStart(LocalDate.parse(dtStart));
        } catch (Exception e) {
        }
        JSONObject jsono = salesService.listeVentes(body);
        return Response.ok().entity(jsono.toString()).build();
    }

    @GET
    @Path("tvastat")
    public Response tvastat(@QueryParam(value = "dtStart") String dtStart, @QueryParam(value = "dtEnd") String dtEnd) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        Params params = new Params();
        params.setDtEnd(dtEnd);
        params.setDtStart(dtStart);
        params.setOperateur(tu);
        JSONObject json = salesService.tvasViewData(params);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("modifiertierspayant/{id}")
    public Response modifiertpayantvente(@PathParam("id") String venteId, ClotureVenteParams params) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = salesService.modifiertypevente(venteId, params);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("venteTierspayantData/{id}")
    public Response venteTierspayantData(@PathParam("id") String venteId) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        List<TiersPayantParams> data = salesService.venteTierspayantData(venteId);
        JSONObject json = new JSONObject();
        json.put("total", data.size());
        json.put("data", data);
        return Response.ok().entity(json.toString()).build();
    }
}
