<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="bll.gateway.outService.ServicesUpdatePriceFamille"%>
<%@page import="bll.gateway.outService.ServiceSoldeCaisseVeille"%>
<%@page import="dal.TAlertEventUserFone"%>
<%@page import="bll.gateway.outService.ServiceSoldeCaisse"%>
<%@page import="dal.TAlertEvent"%>
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
    dataManager OdataManager = new dataManager();
    date key = new date();

%>


<%
String lg_OUTBOUND_MESSAGE_ID = "%%", search_value = "";
int int_MAX_ROW = 20;
   TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    ServicesUpdatePriceFamille OServicesUpdatePriceFamille = new ServicesUpdatePriceFamille(OdataManager, OTUser);
    JSONArray arrayObj = new JSONArray();

    JSONObject json = new JSONObject();
    
    
     OServicesUpdatePriceFamille.reloadSms(search_value, lg_OUTBOUND_MESSAGE_ID, int_MAX_ROW);
     
    json.put("statut", OServicesUpdatePriceFamille.getMessage());
    json.put("message", OServicesUpdatePriceFamille.getDetailmessage());

    arrayObj.put(json);
    String result = "{\"total\":\"" + OServicesUpdatePriceFamille.getMessage() + " \",\"results\":" + arrayObj.toString() + "}";


%>
<%= result%>