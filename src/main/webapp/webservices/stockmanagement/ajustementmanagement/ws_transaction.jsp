<%@page import="bll.stockManagement.AjustementManagement"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
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

<%  
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
   

%>




<%
     String lg_AJUSTEMENT_ID = "";

    if (request.getParameter("lg_AJUSTEMENT_ID") != null) {
        lg_AJUSTEMENT_ID = request.getParameter("lg_AJUSTEMENT_ID");
        new logger().OCategory.info("lg_AJUSTEMENT_ID ------ "+lg_AJUSTEMENT_ID);
    }
    
   
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    AjustementManagement OAjustementManagement = new AjustementManagement(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("delete")) {

            OAjustementManagement.removeAjustement(lg_AJUSTEMENT_ID);
            ObllBase.setDetailmessage(OAjustementManagement.getDetailmessage());
            ObllBase.setMessage(OAjustementManagement.getMessage());
        }
    } else {
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