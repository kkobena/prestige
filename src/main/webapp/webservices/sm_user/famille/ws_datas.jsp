<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="dal.TEmplacement"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>
<%@page import="java.util.Date"%>
<%@page import="toolkits.utils.date"%>
<%@page import="dal.dataManager"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>

<%
    dataManager OdataManager = new dataManager();
   
    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    familleManagement OfamilleManagement = new familleManagement(OdataManager, OTUser);
    String search_value = "";
          
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
   

   
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    TEmplacement empl = OTUser.getLgEMPLACEMENTID();
    JSONArray arrayObj = OfamilleManagement.getAllArticle(search_value,  empl, false, limit, start);
     int count = OfamilleManagement.allCount(search_value, empl); 

    JSONObject data = new JSONObject();

    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>
