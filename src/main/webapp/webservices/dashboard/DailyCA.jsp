<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>

<%@page import="toolkits.utils.Util"%>
<%@page import="bll.report.Dashboard"%>
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
    Dashboard dashboard = new Dashboard(OdataManager);
    JSONObject data = dashboard.getDailyCA_AND_SalesCount();
    JSONObject json = dashboard.getAchatAmount();
    JSONObject paniermoy = dashboard.getPanierMoyen();
    JSONObject magenet=dashboard.getCANetAndMargeNet(); 
     data.put("MARGENET",  Util.getFormattedLongValue(magenet.getLong("MARGENET")) );
    data.put("panierMYVNO", Util.getFormattedDoubleValue(paniermoy.getDouble("panierMYVNO")));  
    data.put("PanierMYVO",Util.getFormattedDoubleValue(paniermoy.getDouble("PanierMYVO")));
    data.put("PanierMY",Util.getFormattedDoubleValue(paniermoy.getDouble("PanierMY")) );
     data.put("MONTANTVO", paniermoy.getDouble("MONTANTVO") );
    data.put("MONTANTVNO",  paniermoy.getDouble("MONTANTVNO"));
    data.put("DailyAchatAmount", Util.getFormattedDoubleValue(json.getDouble("DailyAchatAmount")));
    data.put("DailyAchatCount", Util.getFormattedDoubleValue(json.getDouble("DailyAchatCount")) );
    data.put("MONTANTDEPO", paniermoy.getDouble("MONTANTDEPO")  );
%>

<%= data%>