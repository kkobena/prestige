<%@page import="bll.userManagement.user"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.dataManager" %>
<%@page import="dal.TRole"  %> 
<%@page import="dal.TRoleUser"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>            
<%@page import="org.json.JSONArray"  %> 
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% 
    dataManager OdataManager = new dataManager();
   TRole OTRole = null;
%>

<%
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    user Ouser = new user(OdataManager);


    JSONArray arrayObj = new JSONArray();

    OTRole = Ouser.getTRoleUser(OTUser.getLgUSERID()).getLgROLEID();
    String role = (OTRole != null ? OTRole.getStrDESIGNATION() : ""), xtypeload = "mainmenumanager";
    if(OTRole != null && (OTRole.getStrNAME().equalsIgnoreCase(commonparameter.ROLE_SUPERADMIN) || OTRole.getStrNAME().equalsIgnoreCase(commonparameter.ROLE_PHARMACIEN))) {
        xtypeload = "dashboard";
    }
    JSONObject json = new JSONObject();
    json.put("lg_USER_ID", OTUser.getLgUSERID());
    json.put("str_LOGIN", OTUser.getStrLOGIN());
    json.put("str_PASSWORD", OTUser.getStrPASSWORD());
    json.put("str_FIRST_NAME", OTUser.getStrFIRSTNAME());
    json.put("str_LAST_NAME", OTUser.getStrLASTNAME());
    json.put("str_LAST_CONNECTION_DATE", date.DateToString(OTUser.getStrLASTCONNECTIONDATE(), new SimpleDateFormat("yyyy/MM/dd")));
    json.put("str_STATUT", OTUser.getStrSTATUT()); 
    json.put("lg_Language_ID", OTUser.getLgLanguageID().getStrDescription());
    json.put("lg_ROLE_ID", role);
    json.put("xtypeload", xtypeload);
    
    
    
    arrayObj.put(json);
 
%>

<%= arrayObj.toString()%>