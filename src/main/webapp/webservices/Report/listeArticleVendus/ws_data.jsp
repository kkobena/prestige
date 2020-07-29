<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="java.util.Date"%>
<%@page import="toolkits.utils.date"%>
<%@page import="bll.report.StatisticSales"%>
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
    String dt_start = "";
    String dt_end = "";
    String search_value = "%%";
    StatisticSales OStatisticSales = new StatisticSales(OdataManager);

%>

<%    int DATA_PER_PAGE = 20, count = 0, pages_curr = 0;

%>
<%    String action = request.getParameter("action");
    int pageAsInt = 0;

    try {
        if ((action != null) && action.equals("filltable")) {
        } else {

            String p = request.getParameter("start");

            if (p != null) {
                int int_page = new Integer(p).intValue();
                int_page = (int_page / DATA_PER_PAGE) + 1;
                p = new Integer(int_page).toString();

                // Strip quotation marks
                StringBuffer buffer = new StringBuffer();
                for (int index = 0; index < p.length(); index++) {
                    char c = p.charAt(index);
                    if (c != '\\') {
                        buffer.append(c);
                    }
                }
                p = buffer.toString();
                Integer intTemp = new Integer(p);

                pageAsInt = intTemp.intValue();

            } else {
                pageAsInt = 1;
            }

        }
    } catch (Exception E) {
    }


%>


<%    if (request.getParameter("dt_start_Articlevendu") != null && !"".equals(request.getParameter("dt_start_Articlevendu"))) {
        dt_start = request.getParameter("dt_start_Articlevendu");

    }
    if (request.getParameter("dt_end_Articlevendu") != null && !"".equals(request.getParameter("dt_end_Articlevendu"))) {
        dt_end = request.getParameter("dt_end_Articlevendu") + " 23:59:59";
        // System.out.println("dt_end   " + request.getParameter("dt_end_Articlevendu"));
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    if ("".equals(dt_start)) {
        dt_start = date.formatterMysqlShort.format(new Date());
    }
    if ("".equals(dt_end)) {
       dt_end = date.formatterMysql.format(new Date());
    }
  System.out.println("dt_start "+dt_start+" dt_end "+dt_end);
    lstdetails = OStatisticSales.getListArticleVendu(search_value, dt_start, dt_end);

%>

<%    try {
        if (DATA_PER_PAGE > lstdetails.size()) {
            DATA_PER_PAGE = lstdetails.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstdetails.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    JSONObject data = new JSONObject();
    for (int i = pgInt; i < pgInt_Last; i++) {
        JSONObject json = new JSONObject();

        json.put("id", i);
        //  json.put("str_FAmille", lstdetails.get(i).getStr_value2());
        json.put("str_Libelle_Produit", lstdetails.get(i).getStr_value2());
        json.put("int_QTE_VENDUE", lstdetails.get(i).getStr_value3());
        json.put("str_CODE_CIP", lstdetails.get(i).getStr_value8());
        json.put("int_MONTANT_VENTES", lstdetails.get(i).getStr_value5());
        json.put("int_QTY", lstdetails.get(i).getStr_value6());
        json.put("Emplacement", lstdetails.get(i).getStr_value7());
        json.put("MONTANREMISE", lstdetails.get(i).getStr_value4());
        json.put("int_PU", lstdetails.get(i).getStr_value9());
        json.put("int_MONTANT_BRUT", lstdetails.get(i).getStr_value10());

        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", lstdetails.size());
%>

<%= data%>