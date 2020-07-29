<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="java.time.LocalDate"%>
<%@page import="toolkits.utils.logger"%>
<%@page import="toolkits.parameters.commonparameter"%>
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
 
    String dt_start = LocalDate.now().toString();
    String dt_end = dt_start;
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    StatisticsFamilleArticle familleArticle = new StatisticsFamilleArticle(OdataManager, OTUser);
   
    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");

    }
    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");
       
    }

    JSONArray array = familleArticle.getTvaDatas(dt_start, dt_end,OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

%>
<%  
    JSONObject data=new JSONObject();
   
    data.put("data", array);
    
    data.put("total", array.length());
%>

<%= data%>