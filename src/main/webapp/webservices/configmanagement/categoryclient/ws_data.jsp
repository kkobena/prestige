<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>

<%@page import="bll.configManagement.CategoryClientManager"%>
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
    String search_value = "";
    
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    
    
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    CategoryClientManager manager=new CategoryClientManager(OdataManager, null);
   JSONArray array = manager.findCategoryClientEntities(limit, start, "%"+search_value+"%");
    
    JSONObject data = new JSONObject();
    data.put("data", array);
    data.put("total", array.length());
%>

<%= data%>