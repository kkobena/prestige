<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="java.time.LocalDate"%>
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
    String str_TYPE_TRANSACTION = "", search_value = "", str_TRI = "";
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
     if (request.getParameter("str_TRI") != null && request.getParameter("str_TRI") != "") {
        str_TRI = request.getParameter("str_TRI");
    }
      if (request.getParameter("str_TYPE_TRANSACTION") != null && request.getParameter("str_TYPE_TRANSACTION") != "") {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    String empl = OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID();
    JSONObject data =  new JSONObject();
     JSONArray array=groupeCtl.getListePerimes(search_value, str_TYPE_TRANSACTION, str_TRI, start, limit,false); 
    data.put("results",array);
    int count = groupeCtl.getPerimesCount(search_value, str_TYPE_TRANSACTION, str_TRI); 
    data.put("total", count);

%>

<%= data%>