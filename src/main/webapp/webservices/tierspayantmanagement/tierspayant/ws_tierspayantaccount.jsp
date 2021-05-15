<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TClient"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="bll.facture.reglementManager"%>
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
    OdataManager.initEntityManager();
    clientManagement m = new clientManagement(OdataManager);
    String query = "%%", lg_TIERS_PAYANT_ID = "",dt_start=date.formatterMysqlShort.format(java.sql.Date.valueOf("2015-12-01")),dt_end=date.formatterMysqlShort.format(new Date());
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !"".equals(request.getParameter("lg_TIERS_PAYANT_ID"))) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
    }
 if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
         dt_start = request.getParameter("dt_start_vente");
     }
     if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
         dt_end = request.getParameter("dt_end_vente");

     }
     JSONArray array = m.getTiersPayantDATA(dt_start, dt_end, lg_TIERS_PAYANT_ID);
     JSONObject data = new JSONObject();

     data.put("data", array);
     data.put("total", 1);
%>

<%= data%>