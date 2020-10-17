/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import bll.common.Parameter;
import commonTasks.dto.CaisseParamsDTO;
import commonTasks.dto.MvtCaisseDTO;
import commonTasks.dto.Params;
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
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.CaisseService;
import rest.service.CommonService;
import rest.service.GenerateTicketService;
import toolkits.parameters.commonparameter;
import util.DateConverter;

/**
 *
 * @author Kobena
 */
@Path("v1/caisse")
@Produces("application/json")
@Consumes("application/json")
public class CaisseRessource {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private CaisseService caisseService;
    @EJB
    CommonService commonService;
    @EJB
    GenerateTicketService generateTicketService;

    public CaisseRessource() {

    }

    @GET
    @Path("listecaisse")
    public Response geListeCaisse(
            @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit,
            @QueryParam(value = "page") int p,
            @QueryParam(value = "user") String lg_USER_ID,
            @QueryParam(value = "reglement") String lg_TYPE_REGLEMENT_ID,
            @QueryParam(value = "startDate") String dt_Date_Debut,
            @QueryParam(value = "endDate") String dt_Date_Fin,
            @QueryParam(value = "startH") String h_debut,
            @QueryParam(value = "endH") String h_fin,
            @QueryParam(value = "findClient") boolean findClient
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        CaisseParamsDTO caisseParams = new CaisseParamsDTO();
        caisseParams.setLimit(limit);
        caisseParams.setStart(start);

        if (!StringUtils.isEmpty(dt_Date_Fin)) {
            caisseParams.setEnd(LocalDate.parse(dt_Date_Fin));
        }
        if (!StringUtils.isEmpty(dt_Date_Debut)) {
            caisseParams.setStartDate(LocalDate.parse(dt_Date_Debut));
        }
        if (!StringUtils.isEmpty(h_debut)) {
            caisseParams.setStartHour(LocalTime.parse(h_debut));
        }
        if (!StringUtils.isEmpty(h_fin)) {
            caisseParams.setStartEnd(LocalTime.parse(h_fin));
        }
        if (!StringUtils.isEmpty(lg_TYPE_REGLEMENT_ID)) {
            caisseParams.setTypeReglementId(lg_TYPE_REGLEMENT_ID);
        }
        if (!StringUtils.isEmpty(lg_USER_ID)) {
            caisseParams.setUtilisateurId(lg_USER_ID);
        }
        caisseParams.setFindClient(findClient);
        caisseParams.setEmplacementId(tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

        JSONObject json = caisseService.donneeCaisses(caisseParams, false);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("balancesalecash")
    public Response balanceCaisse(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd
    ) {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        JSONObject json = caisseService.balanceVenteCaisse(dtSt, dtEn, true, tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("validatecloture/{id}")
    public Response closeResumeCaisse(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = caisseService.closeCaisse(tu, id);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("rollbackclose/{id}")
    public Response rolBack(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = caisseService.rollbackcloseCaisse(tu, id);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("resumecaisse")
    public Response resumecaisse(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd,
            @QueryParam(value = "userId") String userId,
            @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        List<TPrivilege> LstTPrivilege = (List<TPrivilege>) hs.getAttribute(commonparameter.USER_LIST_PRIVILEGE);
        boolean cancel = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_BT_ANNULER_CLOTURE_CAISSE);
        boolean allActivitis = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_SHOW_ALL_ACTIVITY);
        JSONObject json = caisseService.resumeCaisse(dtSt, dtEn, tu, cancel, allActivitis, start, limit, false, userId);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("tableauboard")
    public Response tableauBoard(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        JSONObject json = caisseService.tableauBoardDatas(dtSt, dtEn, true, tu, 0, 0, true);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("addmvtCaisse")
    public Response addMvtCaisse(MvtCaisseDTO caisseDTO) throws JSONException {
        HttpSession hs = servletRequest.getSession();

        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }

        JSONObject json = caisseService.createMvt(caisseDTO, tu);
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("attribuerfondcaisse/{id}")
    public Response attribuerFondCaisse(@PathParam("id") String id, Params p) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = caisseService.attribuerFondDeCaisse(id, tu, p.getValue());
        return Response.ok().entity(json.toString()).build();
    }

    @PUT
    @Path("validationfondcaisse/{id}")
    public Response validationFondCaisse(@PathParam("id") String id) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        JSONObject json = caisseService.validerFondDeCaisse(id, tu);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("rapport-gestion")
    public Response rapportGestion(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        Params p = new Params();
        p.setDtStart(dtStart);
        p.setDtEnd(dtEnd);
        p.setOperateur(tu);
        JSONObject json = caisseService.rapportGestionViewData(p);
        return Response.ok().entity(json.toString()).build();
    }

    @POST
    @Path("tickez")
    public Response ticketZ(
            Params params
    ) throws JSONException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        params.setOperateur(tu);
        JSONObject json = generateTicketService.ticketZ(params);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("tableauboard-old")
    public Response tableauBoardOld(
            @QueryParam(value = "dtStart") String dtStart,
            @QueryParam(value = "dtEnd") String dtEnd
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        LocalDate dtSt = LocalDate.now(), dtEn = dtSt;
        try {
            dtSt = LocalDate.parse(dtStart);
            dtEn = LocalDate.parse(dtEnd);
        } catch (Exception e) {
        }
        JSONObject json = caisseService.tableauBoardDatasOld(dtSt, dtEn, true, tu, 0, 0, true);
        return Response.ok().entity(json.toString()).build();
    }

    @GET
    @Path("mvtcaisses")
    public Response mvtcaisses(
            @QueryParam(value = "start") int start,
            @QueryParam(value = "limit") int limit,
            @QueryParam(value = "user") String lg_USER_ID,
            @QueryParam(value = "dtStart") String dt_Date_Debut,
            @QueryParam(value = "dtEnd") String dt_Date_Fin
    ) {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(commonparameter.AIRTIME_USER);
        if (tu == null) {
            return Response.ok().entity(ResultFactory.getFailResult("Vous êtes déconnecté. Veuillez vous reconnecter")).build();
        }
        CaisseParamsDTO caisseParams = new CaisseParamsDTO();
        caisseParams.setLimit(limit);
        caisseParams.setStart(start);

        if (!StringUtils.isEmpty(dt_Date_Fin)) {
            caisseParams.setEnd(LocalDate.parse(dt_Date_Fin));
        }
        if (!StringUtils.isEmpty(dt_Date_Debut)) {
            caisseParams.setStartDate(LocalDate.parse(dt_Date_Debut));
        }
        if (!StringUtils.isEmpty(lg_USER_ID)) {
            caisseParams.setUtilisateurId(lg_USER_ID);
        }

        caisseParams.setEmplacementId(tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

        JSONObject json = caisseService.mouvementCaisses(caisseParams);
        return Response.ok().entity(json.toString()).build();
    }
}
