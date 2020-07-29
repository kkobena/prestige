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
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));

    long count = warehouseManager.getProductCount();

    JSONArray array = warehouseManager.getProducts(start, limit);

    JSONObject data = new JSONObject();
    data.put("data", array);
    data.put("total", count);
%>

<%= data%>