<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="dal.TPrivilege"%>
<%@page import="toolkits.parameters.commonparameter"%>
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
    OdataManager.initEntityManager();
    String lg_TYPE_CLIENT_ID = "", search_value = "", str_STATUT = commonparameter.statut_enable;
    

    if (request.getParameter("str_STATUT") != null && !str_STATUT.equalsIgnoreCase("")) {
        str_STATUT = request.getParameter("str_STATUT");

    }

    if (request.getParameter("lg_TYPE_CLIENT_ID") != null && !request.getParameter("lg_TYPE_CLIENT_ID").equalsIgnoreCase("")) {
        lg_TYPE_CLIENT_ID = request.getParameter("lg_TYPE_CLIENT_ID");

    }

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
   
    int start = 0;
    int limit = 20;
     if(request.getParameter("start")!=null){
       start= Integer.valueOf(request.getParameter("start"));
    }
     if(request.getParameter("start")!=null){
       limit= Integer.valueOf(request.getParameter("limit"));
    }
      TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
       TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
       clientManagement OclientManagement = new clientManagement(OdataManager,user);
          OclientManagement.setUsersPrivileges((List<TPrivilege>) session.getAttribute(commonparameter.USER_LIST_PRIVILEGE));
   
    JSONObject data = OclientManagement.getClients(search_value, lg_TYPE_CLIENT_ID, str_STATUT, start, limit);


%>

<%= data%>