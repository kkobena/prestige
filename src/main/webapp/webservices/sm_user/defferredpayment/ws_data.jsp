<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
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
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    List<EntityData> lstdetails = new ArrayList<EntityData>();
    String dt_start = date.formatterMysqlShort.format(new Date());
    String dt_end = date.formatterMysqlShort.format(new Date());
    String lg_CLIENT_ID = "%%";
    reglementManager OreglementManager = new reglementManager(OdataManager, OTUser);
    if (request.getParameter("dt_debut") != null && !"".equals(request.getParameter("dt_debut"))) {
        dt_start = request.getParameter("dt_debut");

    }
    if (request.getParameter("dt_fin") != null && !"".equals(request.getParameter("dt_fin"))) {
        dt_end = request.getParameter("dt_fin");

    }

    if (request.getParameter("lg_CLIENT_ID") != null && !"".equals(request.getParameter("lg_CLIENT_ID"))) {
        lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
    }

    lstdetails = OreglementManager.getAllDossierReglementsDifferes(lg_CLIENT_ID, dt_start, dt_end);

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
        json.put("lg_DEFFERED_ID", lstdetails.get(i).getStr_value1());
        json.put("ORGANISME", lstdetails.get(i).getStr_value4());
        json.put("MODEREGLEMENT", lstdetails.get(i).getStr_value3());
        json.put("MONTANTREGL", lstdetails.get(i).getStr_value2());
        json.put("MONTANTATT", lstdetails.get(i).getStr_value8());
        json.put("OPPERATEUR", lstdetails.get(i).getStr_value5());
        json.put("HEUREREGL", lstdetails.get(i).getStr_value7());
        json.put("DATEREGL", lstdetails.get(i).getStr_value6());
        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", lstdetails.size());
%>

<%= data%>