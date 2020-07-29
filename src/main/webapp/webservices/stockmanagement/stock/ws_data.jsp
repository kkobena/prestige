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
   
    String str_TYPE_TRANSACTION = "%%", lg_ZONE_GEO_ID = "", lg_FAMILLEARTICLE_ID = "", search_value = "", lg_GROSSISTE_ID = "";
    int int_NUMBER = 0;
    boolean undefined = true;
    if (request.getParameter("int_NUMBER") != null && !"".equals(request.getParameter("int_NUMBER"))) {
        int_NUMBER = new Integer(request.getParameter("int_NUMBER"));
        undefined = false;
    }
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("str_TYPE_TRANSACTION") != null && request.getParameter("str_TYPE_TRANSACTION") != "") {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
    }

    if (request.getParameter("lg_FAMILLEARTICLE_ID") != null && request.getParameter("lg_FAMILLEARTICLE_ID") != "") {
        lg_FAMILLEARTICLE_ID = request.getParameter("lg_FAMILLEARTICLE_ID");
    }
    if (request.getParameter("lg_ZONE_GEO_ID") != null && request.getParameter("lg_ZONE_GEO_ID") != "") {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
    }
    if (request.getParameter("lg_GROSSISTE_ID") != null && request.getParameter("lg_GROSSISTE_ID") != "") {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
  StockManager  OStockManager=new StockManager(OdataManager, OTUser);
    JSONArray arrayObj = OStockManager.etatStock(false, str_TYPE_TRANSACTION, search_value, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, lg_GROSSISTE_ID, int_NUMBER, start, limit);
    JSONObject data = new JSONObject();
    int count = OStockManager.etatStock(str_TYPE_TRANSACTION, search_value, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, lg_GROSSISTE_ID, int_NUMBER);

    data.put("results", arrayObj);
    data.put("total", count);
%>

<%= data%>