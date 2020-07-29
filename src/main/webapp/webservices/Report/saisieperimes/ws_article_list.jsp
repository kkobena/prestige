<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="java.util.Date"%>
<%@page import="toolkits.utils.date"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.report.StatisticsFamilleArticle"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>

<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%@page import="java.util.ArrayList"%>

<%@page import="java.util.List"%>


<%
    dataManager OdataManager = new dataManager();
    WarehouseManager warehouseManager = new WarehouseManager(OdataManager);
    String search_value = "", lg_FAMILLE_ID = "%%";

    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    if (request.getParameter("lg_FAMILLE_ID") != null && !"".equals(request.getParameter("lg_FAMILLE_ID"))) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));

    long count = warehouseManager.getListArticleCount(search_value, lg_FAMILLE_ID);

    JSONArray array = warehouseManager.getListArticle(search_value, lg_FAMILLE_ID, start, limit);

    JSONObject data = new JSONObject();
    data.put("data", array);
    data.put("total", count);
%>

<%= data%>