<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TZoneGeographique"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="java.util.Date"%>
<%@page import="toolkits.utils.date"%>

<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>

<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%@page import="java.util.ArrayList"%>

<%@page import="java.util.List"%>

<%
    dataManager OdataManager = new dataManager();
    String search_value = "%%", zoneID = "%%";
   
      TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
       StockManager OStockManager = new StockManager(OdataManager,OTUser);
    if (request.getParameter("zoneID") != null && !"".equals(request.getParameter("zoneID"))) {
        zoneID = request.getParameter("zoneID");
    }
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    long count = OStockManager.countArticleByZone(zoneID, search_value+"%");

    JSONArray arrayObj = OStockManager.getArticleByZone(zoneID, search_value+"%", start, limit);
    JSONObject data = new JSONObject();

    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>