<%@page import="bll.configManagement.LitigeManager"%>
<%@page import="dal.TFamille"%>
<%@page import="bll.configManagement.LitigeManager"%>
<%@page import="dal.TGroupeFamille"%>
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

<%!   
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();

%>




<%
     String str_NAME = "", lg_TYPELITIGE_ID = "", str_DESCRIPTION = "", lg_LITIGE_ID = "", str_REF = "",
        lg_PREENREGISTREMENT_ID = "", lg_TIERS_PAYANT_ID = "";
        int int_AMOUNT = 0;
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
        new logger().OCategory.info("str_NAME " + str_NAME);
    }
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
        new logger().OCategory.info("str_DESCRIPTION " + str_DESCRIPTION);
    }
    if (request.getParameter("str_REF") != null) {
        str_REF = request.getParameter("str_REF");
        new logger().OCategory.info("str_REF " + str_REF);
    }

    if (request.getParameter("lg_LITIGE_ID") != null) {
        lg_LITIGE_ID = request.getParameter("lg_LITIGE_ID");
        new logger().OCategory.info("lg_LITIGE_ID   " + lg_LITIGE_ID);
    }
    
    if (request.getParameter("int_AMOUNT") != null) {
        int_AMOUNT = Integer.parseInt(request.getParameter("int_AMOUNT"));
        new logger().OCategory.info("int_AMOUNT   " + int_AMOUNT);
    }
    
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID   " + lg_TIERS_PAYANT_ID);
    }
    
    

    if (request.getParameter("lg_TYPELITIGE_ID") != null) {
        lg_TYPELITIGE_ID = request.getParameter("lg_TYPELITIGE_ID");
        new logger().OCategory.info("lg_TYPELITIGE_ID   " + lg_TYPELITIGE_ID);
    }
    
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID   " + lg_PREENREGISTREMENT_ID);
    }
    

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
 TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    ObllBase.setDetailmessage("PAS D'ACTION");
    LitigeManager OLitigeManager = new LitigeManager(OdataManager, OTUser);
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            OLitigeManager.createLitige(str_NAME, str_DESCRIPTION, lg_PREENREGISTREMENT_ID, lg_TIERS_PAYANT_ID, lg_TYPELITIGE_ID);
            ObllBase.setMessage(OLitigeManager.getMessage());
            ObllBase.setDetailmessage(OLitigeManager.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OLitigeManager.deleteLitige(lg_LITIGE_ID);
            ObllBase.setDetailmessage(OLitigeManager.getDetailmessage());
            ObllBase.setMessage(OLitigeManager.getMessage());
        } else if (request.getParameter("mode").toString().equals("closurewithremboursement")) {
            OLitigeManager.closureLitigeWithRemboursement(str_REF, int_AMOUNT);
            ObllBase.setDetailmessage(OLitigeManager.getDetailmessage());
            ObllBase.setMessage(OLitigeManager.getMessage());
        } else if (request.getParameter("mode").toString().equals("closurewithoutremboursement")) {
           // OLitigeManager.closureLitige(str_REF);
            OLitigeManager.closureLitigeWithoutRemboursement(str_REF, int_AMOUNT);
            ObllBase.setDetailmessage(OLitigeManager.getDetailmessage());
            ObllBase.setMessage(OLitigeManager.getMessage());
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