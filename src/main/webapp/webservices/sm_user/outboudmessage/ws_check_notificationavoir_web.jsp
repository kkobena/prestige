<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="bll.gateway.outService.ServicesNotifCustomer"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
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
<%@page import="bll.userManagement.*"  %>


<%
   // Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
   

%>


<%

   TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    JSONArray arrayObj = new JSONArray();

    JSONObject json = new JSONObject();
    
    
     OPreenregistrement.generationDataForNotif();
     
    json.put("statut", OPreenregistrement.getMessage());
    json.put("message", OPreenregistrement.getDetailmessage());

    arrayObj.put(json);
    String result = "{\"total\":\"" + OPreenregistrement.getMessage() + " \",\"results\":" + arrayObj.toString() + "}";


%>
<%= result%>