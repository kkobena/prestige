/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import bll.common.Parameter;
import commonTasks.dto.ManagedUserVM;
import dal.TPrivilege;
import dal.TRoleUser;
import dal.TUser;

import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import rest.service.UserService;
import rest.service.dto.AccountInfoDTO;
import toolkits.parameters.commonparameter;
import util.Constant;
import util.DateConverter;

/**
 * @author koben
 */
@Path("v1/user")
@Produces("application/json")
@Consumes("application/json")
public class AccountResource {

    @EJB
    private UserService userService;
    @Inject
    HttpServletRequest request;

    @POST
    @Path("auth")
    public Response auth(ManagedUserVM managedUser) {

        JSONObject json = new JSONObject();
        TUser tu = userService.connexion(managedUser, request);
        if (tu == null) {
            json.put("success", false);
            return Response.ok().entity(json.toString()).build();
        } else {
            String xtypeuser = "mainmenumanager";
            HttpSession hs = request.getSession(true);
            json.put("str_LOGIN", tu.getStrLOGIN());
            json.put("str_USER_ID", tu.getLgUSERID());
            json.put("str_FIRST_NAME", tu.getStrFIRSTNAME());
            json.put("str_LAST_NAME", tu.getStrLASTNAME());
            json.put("str_PHONE", tu.getStrPHONE());
            hs.setAttribute(Constant.AIRTIME_USER, tu);
            TRoleUser OTRoleUser = userService.getTRoleUser(tu.getLgUSERID());
            if (OTRoleUser != null && OTRoleUser.getLgROLEID() != null) {
                xtypeuser = (OTRoleUser.getLgROLEID().getStrNAME().equalsIgnoreCase(commonparameter.ROLE_PHARMACIEN)
                        || OTRoleUser.getLgROLEID().getStrNAME().equalsIgnoreCase(commonparameter.ROLE_SUPERADMIN)
                        || OTRoleUser.getLgROLEID().getStrNAME().equalsIgnoreCase(commonparameter.ROLE_ADMIN)
                                ? "dashboard" : "mainmenumanager");
            }
            json.put("xtypeuser", xtypeuser);

            json.put("str_PIC", "../general/resources/images/photo_personne/" + tu.getStrPIC());
            json.put("lg_EMPLACEMENT_ID", tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            json.put("OFFICINE",
                    tu.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS)
                            ? userService.getOfficine().getStrNOMABREGE()
                            : tu.getLgEMPLACEMENTID().getStrDESCRIPTION());
            List<TPrivilege> LstTPrivilege = userService.getAllPrivilege(tu);
            hs.setAttribute(commonparameter.USER_LIST_PRIVILEGE, LstTPrivilege);
            boolean asAuthority = DateConverter.hasAuthorityByName(LstTPrivilege, Parameter.P_BT_UPDATE_PRICE_EDIT);
            hs.setAttribute(commonparameter.UPDATE_PRICE, asAuthority);
            json.put("success", true);
            return Response.ok().entity(json.toString()).build();
        }

    }

    @POST
    @Path("logout")
    public Response deconnection() {
        JSONObject json = new JSONObject();
        HttpSession hs = request.getSession();
        TUser tu = (TUser) hs.getAttribute(Constant.AIRTIME_USER);
        boolean invalide = userService.deConnexion(request, tu);
        json.put("success", invalide);
        return Response.ok().entity(json.toString()).build();

    }

    @GET
    @Path("account")
    public Response account() {
        HttpSession hs = request.getSession();
        TUser tu = (TUser) hs.getAttribute(Constant.AIRTIME_USER);
        JSONObject json = new JSONObject();
        return Response.ok().entity(json.put("accountInfo", new JSONObject(this.userService.getAccount(tu))).toString())
                .build();
    }

    @POST
    @Path("account")
    public Response updateAccount(AccountInfoDTO accountInfo) {
        HttpSession hs = request.getSession();
        TUser tu = (TUser) hs.getAttribute(Constant.AIRTIME_USER);
        if (tu == null) {
            throw new RuntimeException(Constant.DECONNECTED_MESSAGE);
        }

        tu = this.userService.updateProfilUser(accountInfo);
        hs.setAttribute(Constant.AIRTIME_USER, tu);
        return Response.ok().build();
    }
}
