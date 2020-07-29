<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
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
    List<EntityData> lstdetails = new ArrayList<EntityData>();
    String dt_start = date.formatterMysqlShort.format(new Date());
    String dt_end = date.formatterMysqlShort.format(new Date());
    String search_value = "%%";
    StatisticsFamilleArticle familleArticle = new StatisticsFamilleArticle(OdataManager);

    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");
    }
    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");

    }

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));

    long count = familleArticle.getAChatProductCount(dt_start, dt_end, search_value);

    lstdetails = familleArticle.getAChatProduct(dt_start, dt_end, search_value, start, limit);

%>




<%    JSONArray arrayObj = new JSONArray();
    JSONObject data = new JSONObject();
    int k = 1;
    for (EntityData OData : lstdetails) {
        JSONObject json = new JSONObject();

        json.put("id", k);
        json.put("ANNEE", OData.getStr_value1());
        json.put("LIBELLE", OData.getStr_value2());
        json.put("JANVIER", OData.getStr_value3());
        json.put("FEVRIER", OData.getStr_value4());
        json.put("MARS", OData.getStr_value5());
        json.put("AVRIL", OData.getStr_value6());
        json.put("MAI", OData.getStr_value7());
        json.put("JUIN", OData.getStr_value8());
        json.put("JUIELLET", OData.getStr_value9());
        json.put("AOUT", OData.getStr_value10());
        json.put("SET", OData.getStr_value11());
        json.put("OCT", OData.getStr_value12());
        json.put("NOV", OData.getStr_value13());
        json.put("DEC", OData.getStr_value14());
        arrayObj.put(json);
        k++;
    }
    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>