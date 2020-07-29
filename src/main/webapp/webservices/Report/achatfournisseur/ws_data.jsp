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


<%    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
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

    lstdetails = familleArticle.getAChatFournisseurs(dt_start, dt_end, search_value);

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
        json.put("ANNEE", lstdetails.get(i).getStr_value1());
        json.put("LIBELLE", lstdetails.get(i).getStr_value2());
        json.put("JANVIER", lstdetails.get(i).getStr_value3().split("_")[0]);
        json.put("FEVRIER", lstdetails.get(i).getStr_value4().split("_")[0]);
        json.put("MARS", lstdetails.get(i).getStr_value5().split("_")[0]);
        json.put("AVRIL", lstdetails.get(i).getStr_value6().split("_")[0]);
        json.put("MAI", lstdetails.get(i).getStr_value7().split("_")[0]);
        json.put("JUIN", lstdetails.get(i).getStr_value8().split("_")[0]);
        json.put("JUIELLET", lstdetails.get(i).getStr_value9().split("_")[0]);
        json.put("AOUT", lstdetails.get(i).getStr_value10().split("_")[0]);
        json.put("SET", lstdetails.get(i).getStr_value11().split("_")[0]);
        json.put("OCT", lstdetails.get(i).getStr_value12().split("_")[0]);
        json.put("NOV", lstdetails.get(i).getStr_value13().split("_")[0]);
        json.put("DEC", lstdetails.get(i).getStr_value14().split("_")[0]);
        json.put("JANVIER_AVOIR", lstdetails.get(i).getStr_value3().split("_")[1]);
        json.put("FEVRIER_AVOIR", lstdetails.get(i).getStr_value4().split("_")[1]);
        json.put("MARS_AVOIR", lstdetails.get(i).getStr_value5().split("_")[1]);
        json.put("AVRIL_AVOIR", lstdetails.get(i).getStr_value6().split("_")[1]);
        json.put("MAI_AVOIR", lstdetails.get(i).getStr_value7().split("_")[1]);
        json.put("JUIN_AVOIR", lstdetails.get(i).getStr_value8().split("_")[1]);
        json.put("JUIELLET_AVOIR", lstdetails.get(i).getStr_value9().split("_")[1]);
        json.put("AOUT_AVOIR", lstdetails.get(i).getStr_value10().split("_")[1]);
        json.put("SET_AVOIR", lstdetails.get(i).getStr_value11().split("_")[1]);
        json.put("OCT_AVOIR", lstdetails.get(i).getStr_value12().split("_")[1]);
        json.put("NOV_AVOIR", lstdetails.get(i).getStr_value13().split("_")[1]);
        json.put("DEC_AVOIR", lstdetails.get(i).getStr_value14().split("_")[1]);
        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", lstdetails.size());
%>

<%= data%>