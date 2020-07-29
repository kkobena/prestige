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


<%    dataManager OdataManager = new dataManager();
    List<EntityData> lstdetails = new ArrayList<EntityData>();
    String dt_start = date.formatterMysqlShort.format(new Date());
    String dt_end = dt_start;
    String search_value = "%%";
    StatisticsFamilleArticle familleArticle = new StatisticsFamilleArticle(OdataManager);
    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");

    }
    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente") ;

    }

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    System.out.println("dt_end "+dt_end);
    lstdetails = familleArticle.getAllShopRuptureStocks(search_value, dt_start, dt_end);

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
        json.put("Quantite", lstdetails.get(i).getStr_value1());
        json.put("QTEPROPOSE", lstdetails.get(i).getStr_value2());
        json.put("SEUILPROPOSE", lstdetails.get(i).getStr_value3());
        json.put("Nombre Fois", lstdetails.get(i).getStr_value4());
        json.put("str_LIBELLE", lstdetails.get(i).getStr_value5());
        json.put("CODEGESTION", lstdetails.get(i).getStr_value6());
        json.put("CODECIP", lstdetails.get(i).getStr_value7());
        json.put("SEUILREAP", lstdetails.get(i).getStr_value8()); 
        json.put("QTEREAP", lstdetails.get(i).getStr_value9()); 
        // json.put("POURCENTAGE TOTAL", lstdetails.get(i).getStr_value13());

        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", lstdetails.size());
%>

<%= data%>