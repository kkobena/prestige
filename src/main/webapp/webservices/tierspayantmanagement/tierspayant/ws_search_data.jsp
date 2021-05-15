<%-- 
    Document   : ws_search_data
    Created on : 10 déc. 2015, 08:36:00
    Author     : KKOFFI
--%>

<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TTiersPayant"  %>
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
    List<TTiersPayant> lstTTiersPayant = new ArrayList<>();

%>

<%
    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data tiers payant ");
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

<%    String lg_TIERS_PAYANT_ID = "%%", search_value = "", lg_TYPE_TIERS_PAYANT_ID = "%%",lg_customer_id="%%";

    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TIERS_PAYANT_ID  = " + lg_TIERS_PAYANT_ID);
    }
  if (request.getParameter("lg_customer_id") != null) {
        lg_customer_id = request.getParameter("lg_customer_id").toString();
        new logger().OCategory.info("lg_customer_id  = " + lg_customer_id);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value  = " + search_value);
    }
    if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null && !lg_TYPE_TIERS_PAYANT_ID.equalsIgnoreCase("")) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TYPE_TIERS_PAYANT_ID  = " + lg_TYPE_TIERS_PAYANT_ID);
    }
    if (request.getParameter("cmb_TYPE_TIERS_PAYANT") != null) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("cmb_TYPE_TIERS_PAYANT").toString();
        new logger().OCategory.info("lg_TYPE_TIERS_PAYANT_ID  = " + lg_TYPE_TIERS_PAYANT_ID);
    }
     if (request.getParameter("query") != null) {
        search_value = request.getParameter("query").toString();
        new logger().OCategory.info("query +++++++++++++++++++++++++ = " + search_value);
    }

    OdataManager.initEntityManager();
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
    lstTTiersPayant = OtierspayantManagement.ShowAllOrOneTierspayant(search_value, lg_TIERS_PAYANT_ID, lg_TYPE_TIERS_PAYANT_ID, commonparameter.statut_enable);
   
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTTiersPayant.size()) {
            DATA_PER_PAGE = lstTTiersPayant.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTiersPayant.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTTiersPayant.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        // lg_TIERS_PAYANT_ID
        json.put("lg_TIERS_PAYANT_ID", lstTTiersPayant.get(i).getLgTIERSPAYANTID());
        // str_CODE_ORGANISME
        json.put("str_CODE_ORGANISME", lstTTiersPayant.get(i).getStrCODEORGANISME());
        // str_NAME
        json.put("str_NAME", lstTTiersPayant.get(i).getStrNAME());
        // str_FULLNAME
        json.put("str_FULLNAME", lstTTiersPayant.get(i).getStrFULLNAME());
        // str_ADRESSE
       
        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTTiersPayant.size() + " \",\"results\":" + arrayObj.toString() + "})";
   
%>

<%= result%>