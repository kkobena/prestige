<%@page import="dal.TZoneGeographique"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>
<%@page import="bll.configManagement.familleManagement"  %>

<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();


%>


<%    String lg_ZONE_GEO_ID = "", str_LIBELLEE = "", str_CODE = "";
    boolean bool_ACCOUNT = false;
    if (request.getParameter("bool_ACCOUNT") != null) {
        bool_ACCOUNT = Boolean.valueOf(request.getParameter("bool_ACCOUNT")) ;
       
    }
    if (request.getParameter("lg_ZONE_GEO_ID") != null) {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
        new logger().OCategory.info("lg_ZONE_GEO_ID " + lg_ZONE_GEO_ID);
    }
    if (request.getParameter("str_LIBELLEE") != null) {
        str_LIBELLEE = request.getParameter("str_LIBELLEE");
        new logger().OCategory.info("str_LIBELLEE " + str_LIBELLEE);
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
        new logger().OCategory.info("str_CODE " + str_CODE);
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    familleManagement OfamilleManagement = new familleManagement(OdataManager, user);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("create")) {
            OfamilleManagement.createTZoneGeographiqueBis(str_CODE, str_LIBELLEE, OTUser.getLgEMPLACEMENTID());
            ObllBase.setMessage(OfamilleManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleManagement.getDetailmessage());
        } else if (request.getParameter("mode").equals("update")) {
            OfamilleManagement.updateTZoneGeographique(lg_ZONE_GEO_ID, str_CODE, str_LIBELLEE);
            ObllBase.setMessage(OfamilleManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleManagement.getDetailmessage());
        } else if (request.getParameter("mode").equals("delete")) {
            OfamilleManagement.deleteTZoneGeographique(lg_ZONE_GEO_ID);
            ObllBase.setMessage(OfamilleManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleManagement.getDetailmessage());
        } else if (request.getParameter("mode").equals("updateCount")) {
            OfamilleManagement.updateZoneGeographique(lg_ZONE_GEO_ID, bool_ACCOUNT);
            ObllBase.setMessage(OfamilleManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleManagement.getDetailmessage());
        }
    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>