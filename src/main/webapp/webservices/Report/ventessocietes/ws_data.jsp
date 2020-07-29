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
    long count = familleArticle.getVenteSocietesClientCount(dt_start, dt_end, search_value, "1");
  
    lstdetails = familleArticle.getVenteSocietesClientData(dt_start, dt_end, search_value, start, limit);

    JSONArray arrayObj = new JSONArray();
    JSONObject data = new JSONObject();
    int k = 1;
    for (EntityData OData : lstdetails) {
        JSONObject json = new JSONObject();

        json.put("id", k);
        json.put("ANNEE", OData.getStr_value1());
        json.put("LIBELLE", OData.getStr_value2());
        json.put("JANVIER", OData.getStr_value3().split("_")[0]);
        json.put("FEVRIER", OData.getStr_value4().split("_")[0]);
        json.put("MARS", OData.getStr_value5().split("_")[0]);
        json.put("AVRIL", OData.getStr_value6().split("_")[0]);
        json.put("MAI", OData.getStr_value7().split("_")[0]);
        json.put("JUIN", OData.getStr_value8().split("_")[0]);
        json.put("JUIELLET", OData.getStr_value9().split("_")[0]);
        json.put("AOUT", OData.getStr_value10().split("_")[0]);
        json.put("SET", OData.getStr_value11().split("_")[0]);
        json.put("OCT", OData.getStr_value12().split("_")[0]);
        json.put("NOV", OData.getStr_value13().split("_")[0]);
        json.put("DEC", OData.getStr_value14().split("_")[0]);
        json.put("JANVIER_AVOIR", OData.getStr_value3().split("_")[1]);
        json.put("FEVRIER_AVOIR", OData.getStr_value4().split("_")[1]);
        json.put("MARS_AVOIR", OData.getStr_value5().split("_")[1]);
        json.put("AVRIL_AVOIR", OData.getStr_value6().split("_")[1]);
        json.put("MAI_AVOIR", OData.getStr_value7().split("_")[1]);
        json.put("JUIN_AVOIR", OData.getStr_value8().split("_")[1]);
        json.put("JUIELLET_AVOIR", OData.getStr_value9().split("_")[1]);
        json.put("AOUT_AVOIR", OData.getStr_value10().split("_")[1]);
        json.put("SET_AVOIR", OData.getStr_value11().split("_")[1]);
        json.put("OCT_AVOIR", OData.getStr_value12().split("_")[1]);
        json.put("NOV_AVOIR", OData.getStr_value13().split("_")[1]);
        json.put("DEC_AVOIR", OData.getStr_value14().split("_")[1]);
        arrayObj.put(json);
        k++;
    }
    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>