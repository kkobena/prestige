<%@page import="bll.report.JournalVente"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="java.util.ArrayList"%>
<%@page import="dal.TMotifReglement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %> 


<%
    dataManager OdataManager = new dataManager();
     new logger().OCategory.info("dans ws data facture subrogatoire other------------------------------");
    TPreenregistrementCompteClientTiersPayent OTPreenregistrementCompteClientTiersPayent = null;
    date key = new date();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data facture subrogatoire");
%>


<!-- logic de gestion des page -->
<%
    String action = request.getParameter("action"); //get parameter ?action=
    int pageAsInt = 0;

    try {
        if ((action != null) && action.equals("filltable")) {
        } else {

            String p = request.getParameter("start"); // get paramerer ?page=

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
<!-- fin logic de gestion des page -->

<%    List<EntityData> listPreenregistrement = new ArrayList<EntityData>();
    List<TPreenregistrementCompteClientTiersPayent> listTPreenregistrementCompteClientTiersPayent = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
    Date today = new Date();
    String str_Date_Debut = key.DateToString(today, key.formatterMysqlShort), str_Date_Fin = key.DateToString(today, key.formatterMysqlShort), search_value = "",
            h_debut = "00:00", h_fin = "23:59", str_TYPE_VENTE = "%%", lg_TIERS_PAYANT_ID = "%%";
    double total_bon = 0.0, total_attendu_tp = 0.0;
    int nbre_bon = 0;

    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value :" + search_value);
    }

     if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !request.getParameter("lg_TIERS_PAYANT_ID").equalsIgnoreCase("")) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID :" + lg_TIERS_PAYANT_ID);
    }
    
    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equalsIgnoreCase("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        new logger().OCategory.info("str_TYPE_VENTE :" + str_TYPE_VENTE);
    }

    if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        new logger().OCategory.info("h_debut :" + h_debut);
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");
        new logger().OCategory.info("h_fin :" + h_fin);
    }
    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    JournalVente OJournalVente = new JournalVente(OdataManager, OTUser);
    listPreenregistrement = OJournalVente.getFactureSubrogatoireOther(search_value, str_Date_Debut, str_Date_Fin, h_debut, h_fin, lg_TIERS_PAYANT_ID);
    total_bon = OJournalVente.getTotalBon(search_value, str_Date_Debut, str_Date_Fin, h_debut, h_fin, lg_TIERS_PAYANT_ID);
    total_attendu_tp = OJournalVente.getTotalAttenduParTP(listPreenregistrement);
    nbre_bon = OJournalVente.getTotalNbreBon(listPreenregistrement);
    
%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > listPreenregistrement.size()) {
            DATA_PER_PAGE = listPreenregistrement.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (listPreenregistrement.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();

    for (int i = pgInt; i < pgInt_Last; i++) {


        JSONObject json = new JSONObject();
        json.put("lg_PREENREGISTREMENT_ID", listPreenregistrement.get(i).getStr_value1());
        json.put("int_CIP", listPreenregistrement.get(i).getStr_value2());
        json.put("lg_USER_CAISSIER_ID", listPreenregistrement.get(i).getStr_value3());
        json.put("int_PRICE", listPreenregistrement.get(i).getStr_value5());
        json.put("dt_CREATED", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value6(), date.formatterMysql), date.formatterShort));
        json.put("lg_ETAT_ARTICLE_ID", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value6(), date.formatterMysql), date.NomadicUiFormat_Time));
        json.put("str_TYPE_VENTE", listPreenregistrement.get(i).getStr_value10());
        json.put("str_DESCRIPTION_PLUS", listPreenregistrement.get(i).getStr_value11());
        json.put("MOUVEMENT", "<span style='display: inline-block;width:15%;'>"+listPreenregistrement.get(i).getStr_value15()+"</span><span style='display: inline-block;width:10%;'>"+listPreenregistrement.get(i).getStr_value8()+"</span>");
        json.put("lg_AJUSTEMENTDETAIL_ID", listPreenregistrement.get(i).getStr_value7());
        json.put("lg_FAMILLE_ID", listPreenregistrement.get(i).getStr_value2());

        //a revoir apre
        
        json.put("int_PRICE_FORMAT", conversion.AmountFormat(Integer.parseInt(listPreenregistrement.get(i).getStr_value5()), '.'));
        json.put("str_DESCRIPTION", listPreenregistrement.get(i).getStr_value14());
        json.put("int_PRICE_REMISE_FORMAT", conversion.AmountFormat(Integer.parseInt(listPreenregistrement.get(i).getStr_value13()), '.'));
        json.put("VENTE_NET_FORMAT", conversion.AmountFormat(Integer.parseInt(listPreenregistrement.get(i).getStr_value12()), '.'));
        json.put("int_PRICE_REMISE", Integer.parseInt(listPreenregistrement.get(i).getStr_value13()));
        json.put("int_PRICE_DETAIL", Integer.parseInt(listPreenregistrement.get(i).getStr_value12()));
        json.put("int_QTEDETAIL", Integer.parseInt(listPreenregistrement.get(i).getStr_value4()));
        json.put("int_NUMBER_AVAILABLE_DECONDITION",total_attendu_tp );
        json.put("int_NUMBERDETAIL",total_bon );
        json.put("int_SEUIL_RESERVE",nbre_bon);
        
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + listPreenregistrement.size() + " \",\"results\":" + arrayObj.toString() + "})";

    System.out.println(result);
%>

<%= result%>
