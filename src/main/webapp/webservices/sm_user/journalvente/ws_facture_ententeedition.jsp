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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();
    List<TPreenregistrementCompteClientTiersPayent> lstTPreenregistrementCompteClientTiersPayent = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
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

<%   Date today = new Date();
    String str_Date_Debut = key.DateToString(today, key.formatterMysqlShort), str_Date_Fin = key.DateToString(today, key.formatterMysqlShort), search_value = "",
            h_debut = "00:00", h_fin = "23:59", lg_TIERS_PAYANT_ID = "%%", OdateDebut = "", OdateFin = "";
    Date dt_Date_Debut, dt_Date_Fin;
 double total_bon = 0.0, total_attendu_tp = 0.0;
    int nbre_bon = 0;
    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value :" + search_value);
    }

    /*if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        new logger().OCategory.info("h_debut :" + h_debut);
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");
        new logger().OCategory.info("h_fin :" + h_fin);
    }*/
    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

     if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !request.getParameter("lg_TIERS_PAYANT_ID").equalsIgnoreCase("")) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID :" + lg_TIERS_PAYANT_ID);
    }
     
     
      if (str_Date_Fin.equalsIgnoreCase("") || str_Date_Fin == null) {
        dt_Date_Fin = new Date();
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    } else {
        dt_Date_Fin = key.stringToDate(str_Date_Fin, key.formatterMysqlShort);
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    }

    dt_Date_Fin = key.getDate(OdateFin, "23:59");
    //dt_Date_Fin = key.GetNewDate(1);
    // OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysql);
    new logger().OCategory.info("dt_Date_Fin *** " + dt_Date_Fin + " OdateFin *** " + OdateFin);
    if (str_Date_Debut.equalsIgnoreCase("") || str_Date_Debut == null) {
        dt_Date_Debut = new Date();
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);
    } else {
        dt_Date_Debut = key.stringToDate(str_Date_Debut, key.formatterMysqlShort);
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);

    }
    dt_Date_Debut = key.getDate(OdateDebut, "00:00");
    new logger().OCategory.info("dt_Date_Debut *** " + dt_Date_Debut + " OdateDebut *** " + OdateDebut);
    
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    lstTPreenregistrementCompteClientTiersPayent = OPreenregistrement.getListVenteTiersPayant(search_value, lg_TIERS_PAYANT_ID, dt_Date_Debut, dt_Date_Fin);
    total_bon = OPreenregistrement.getTotalBon(lstTPreenregistrementCompteClientTiersPayent);
    total_attendu_tp = OPreenregistrement.getTotalAttenduParTP(lstTPreenregistrementCompteClientTiersPayent);
    nbre_bon = OPreenregistrement.getTotalNbreBon(lstTPreenregistrementCompteClientTiersPayent);
%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTPreenregistrementCompteClientTiersPayent.size()) {
            DATA_PER_PAGE = lstTPreenregistrementCompteClientTiersPayent.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTPreenregistrementCompteClientTiersPayent.size() - (DATA_PER_PAGE * (pgInt)));
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
        
        json.put("lg_PREENREGISTREMENT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
        json.put("str_DESCRIPTION", lstTPreenregistrementCompteClientTiersPayent.get(i).getStrREFBON());
        json.put("str_REF", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getStrREF());
        json.put("lg_USER_CAISSIER_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getLgUSERCAISSIERID().getStrFIRSTNAME() + " " + lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getLgUSERCAISSIERID().getStrFIRSTNAME());
        json.put("int_PRICE_DETAIL", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getIntPRICE());
        json.put("int_QTEDETAIL", lstTPreenregistrementCompteClientTiersPayent.get(i).getIntPRICE());
        json.put("dt_CREATED", date.DateToString(lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getDtUPDATED(), date.formatterShort));
        json.put("lg_ETAT_ARTICLE_ID", date.DateToString(lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getDtUPDATED(), date.NomadicUiFormat_Time));
        json.put("str_DESCRIPTION_PLUS", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME() + " " + lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());
        json.put("lg_AJUSTEMENTDETAIL_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrNUMEROSECURITESOCIAL());
        json.put("MOUVEMENT", "<span style='display: inline-block;width:15%;'>"+lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME()+"</span><span style='display: inline-block;width:10%;'>"+(lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTYPETIERSPAYANTID().getLgTYPETIERSPAYANTID().equalsIgnoreCase("1") ? "S" : "X")+"</span>");
        json.put("int_NUMBER_AVAILABLE_DECONDITION",total_attendu_tp );
        json.put("int_NUMBERDETAIL",total_bon );
        json.put("int_SEUIL_RESERVE",nbre_bon);
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTPreenregistrementCompteClientTiersPayent.size() + " \",\"results\":" + arrayObj.toString() + "})";

    System.out.println(result);
%>

<%= result%>
