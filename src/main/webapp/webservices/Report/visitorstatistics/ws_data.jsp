<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="bll.report.StatisticSales"%>
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

<%    long count = 0l;
    String dt_start = date.formatterMysqlShort.format(new Date());
    String dt_end = dt_start;

    dataManager OdataManager = new dataManager();

    StatisticSales s = new StatisticSales(OdataManager);

    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");

    }
    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");

    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    count = s.getFrequentationCount(dt_start, dt_end);
    List<EntityData> datas = s.analyseFrequentation(dt_start, dt_end, start, limit);

    JSONArray arrayObj = new JSONArray();
    JSONObject data = new JSONObject();
    if(!datas.isEmpty()){
    for (int i = 0; i < datas.size(); i++) {
        JSONObject json = new JSONObject();

        json.put("id", i);

        json.put("JOUR",date.formatterShort.format(java.sql.Date.valueOf(datas.get(i).getStr_value1())) );
        json.put("OP", datas.get(i).getStr_value2());
        json.put("VALUES", datas.get(i).getStr_value3());
        json.put("UN", datas.get(i).getStr_value4());
        json.put("DEUX", datas.get(i).getStr_value5());
        json.put("TROIS", datas.get(i).getStr_value6());
        json.put("QUATRE", datas.get(i).getStr_value7());
        json.put("CINQ", datas.get(i).getStr_value8());
        json.put("SIX", datas.get(i).getStr_value9());
        json.put("SEPT", datas.get(i).getStr_value10());
        json.put("HUIT", datas.get(i).getStr_value11());
        json.put("NEUF", datas.get(i).getStr_value12());
        json.put("DIX", datas.get(i).getStr_value13());
        json.put("TOTAL", datas.get(i).getStr_value14());
        arrayObj.put(json);
       
    }
}
  /*  if (datas.size() > 0) {
        JSONObject json = new JSONObject();
        json.put("id", k + 1);

        json.put("JOUR", "TOTAL");
        json.put("OP", "");
        json.put("VALUES", "val_nbre_pan_lig");
        json.put("UN", UN_AMONT + "_" + UN_COUNT + "_" + UN_PAN + "_" + UN_REFERNCEVALUE);
        json.put("DEUX", DEUX_AMONT + "_" + DEUX_COUNT + "_" + DEUX_PAN + "_" + DEUX_REFERNCEVALUE);
        json.put("TROIS", TROIS_AMONT + "_" + TROIS_COUNT + "_" + TROIS_PAN + "_" + TROIS_REFERNCEVALUE);
        json.put("QUATRE", QUATRE_AMONT + "_" + QUATRE_COUNT + "_" + QUATRE_PAN + "_" + QUATRE_REFERNCEVALUE);
        json.put("CINQ", CINQ_AMONT + "_" + CINQ_COUNT + "_" + CINQ_PAN + "_" + CINQ_REFERNCEVALUE);
        json.put("SIX", SIX_AMONT + "_" + SIX_COUNT + "_" + SIX_PAN + "_" + SIX_REFERNCEVALUE);
        json.put("SEPT", SEPT_AMONT + "_" + SEPT_COUNT + "_" + SEPT_PAN + "_" + SEPT_REFERNCEVALUE);
        json.put("HUIT", HUIT_AMONT + "_" + HUIT_COUNT + "_" + HUIT_PAN + "_" + HUIT_REFERNCEVALUE);
        json.put("NEUF", NEUF_AMONT + "_" + NEUF_COUNT + "_" + NEUF_PAN + "_" + NEUF_REFERNCEVALUE);
        json.put("DIX", DIX_AMONT + "_" + DIX_COUNT + "_" + DIX_PAN + "_" + DIX_REFERNCEVALUE);
        json.put("TOTAL", TOTAL_AMOUNT + "_" + TOTAL_COUNT + "_" + TOTAL_PAN + "_" + TOTAL_REFERNCEVALUE);
        arrayObj.put(json);
    }*/
    data.put("data", arrayObj);

    data.put("total", count);
%>

<%= data%>