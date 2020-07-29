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
    String search_value = "", dt_start = date.formatterMysqlShort.format(new Date()), dt_end = dt_start;
    
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    
    if (request.getParameter("dt_end") != null && !"".equals(request.getParameter("dt_end"))) {
        dt_end = request.getParameter("dt_end");
    }
    if (request.getParameter("dt_start") != null && !"".equals(request.getParameter("dt_start"))) {
        dt_start = request.getParameter("dt_start");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    
    long count = warehouseManager.getCountPerimes(search_value, dt_start, dt_end);
    
    JSONArray array = warehouseManager.getPerimes(search_value, dt_start, dt_end, start, limit);
    
    JSONObject data = new JSONObject();
    data.put("data", array);
    data.put("total", count);
%>

<%= data%>