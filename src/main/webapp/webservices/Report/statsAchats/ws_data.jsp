<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TGroupeTierspayant"%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>

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
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());
    String search_value = "";
    int year = new Integer(date.FORMATTERYEAR.format(new Date()));
   
    if (request.getParameter("year") != null && !"".equals(request.getParameter("year"))) {
        year = new Integer(request.getParameter("year"));
    }

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
   
    JSONArray arrayObj = groupeCtl.statAchatQty(false, year, search_value, start, limit);

    int count = groupeCtl.statAchatQty(year, search_value);

    JSONObject data = new JSONObject();

    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>