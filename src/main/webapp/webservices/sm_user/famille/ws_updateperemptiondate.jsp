<%-- 
    Document   : create
    Created on : 25 oct. 2015, 08:07:54
    Author     : KKOFFI
--%>

<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.configManagement.familleArticleManagement"%>
<%@page import="dal.dataManager"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TUser"%>
<%@page import="org.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    String lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
    boolean enabled = Boolean.parseBoolean(request.getParameter("checked"));
    JSONObject json = new JSONObject();
    dataManager OdataManager = new dataManager();
    familleManagement management = new familleManagement(OdataManager);
    int success = 0;
    
    if (management.enableORdisablePeremptionDate(lg_FAMILLE_ID, enabled)) {
       success=1; 
    }
    json.put("success", success);
%>
<%=json%>

