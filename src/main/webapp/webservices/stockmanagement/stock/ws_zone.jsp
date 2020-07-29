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
    List<TZoneGeographique> lstTZoneGeographique = new ArrayList<TZoneGeographique>();
    String search_value = "%%";
    StockManager OStockManager = new StockManager(OdataManager);

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    long count = OStockManager.getZone(search_value);
 TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    lstTZoneGeographique = OStockManager.getZone(search_value,OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), start, limit);

    JSONArray arrayObj = new JSONArray();
    JSONObject data = new JSONObject();

    for (TZoneGeographique OData : lstTZoneGeographique) {
        JSONObject json = new JSONObject();

        json.put("lg_ZONE_GEO_ID", OData.getLgZONEGEOID());
        json.put("str_CODE", OData.getStrCODE());
        json.put("str_LIBELLEE", OData.getStrLIBELLEE());
        

        arrayObj.put(json);

    }
    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>