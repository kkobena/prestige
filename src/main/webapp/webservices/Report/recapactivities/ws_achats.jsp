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
    String dt_start = date.formatterMysqlShort.format(new Date()), dt_end = dt_start;
    
  

    if (request.getParameter("dt_end") != null && !"".equals(request.getParameter("dt_end"))) {
        dt_end = request.getParameter("dt_end");
    }

    if (request.getParameter("dt_start") != null && !"".equals(request.getParameter("dt_start"))) {
        dt_start = request.getParameter("dt_start");
    }

        
  
   

    JSONObject data =  groupeCtl.getGrossisteAchats(dt_start, dt_end);

   
%>

<%= data%>