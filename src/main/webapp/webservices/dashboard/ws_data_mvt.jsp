<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>

<%@page import="bll.report.Dashboard"%>
<%@page import="java.util.Date"%>
<%@page import="dal.dataManager"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%
    dataManager OdataManager = new dataManager();
    Dashboard dashboard = new Dashboard(OdataManager);
    JSONArray data = dashboard.getListMVT();
    JSONObject json = new JSONObject();
    json.put("data", data);
%>

<%= json%>