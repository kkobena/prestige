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
      String lg_TYPE_VENTE_ID = "",exclude="";
      String search_value = "";
    dataManager OdataManager = new dataManager();

    OdataManager.initEntityManager();
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());
     if (request.getParameter("exclude") != null && !"".equals(request.getParameter("exclude"))) {
        exclude = request.getParameter("exclude");
    }
    
   
    if (request.getParameter("lg_TYPE_VENTE_ID") != null && !"".equals(request.getParameter("lg_TYPE_VENTE_ID"))) {
        lg_TYPE_VENTE_ID = request.getParameter("lg_TYPE_VENTE_ID");
    }
    
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }

    JSONArray arrayObj = groupeCtl.getAllTypeVente(exclude, lg_TYPE_VENTE_ID);

   
    
    JSONObject data = new JSONObject();
    data.put("data", arrayObj);
    data.put("total", arrayObj.length());

%>

<%= data%>