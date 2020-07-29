<%@page import="dal.TPreenregistrement"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrementCompteClient"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TTypeRisque"  %>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();
    List<TPreenregistrementCompteClientTiersPayent> lstTPreenregistrementCompteClientTiersPayent = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
    TUser OTUser;

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data detail pres de la balance agee detail ");
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

<%
    String lg_COMPTE_CLIENT_TIERS_PAYANT_ID = "%%", lg_PREENREGISTREMENT_ID = "%%", str_REF = "", lg_COMPTE_CLIENT_ID = "%%", lg_TIERS_PAYANT_ID = "%%", search_value = "", lg_USER_ID = "%%", lg_EMPLACEMENT_ID = "%%";
    String dt_DEBUT = "", dt_FIN = "";
    Date dtFin;

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_REF") != null) {
        str_REF = request.getParameter("str_REF");
        new logger().OCategory.info("str_REF " + str_REF);
    }

    if (request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID") != null && request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID") != "") {
        lg_COMPTE_CLIENT_TIERS_PAYANT_ID = request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_TIERS_PAYANT_ID " + lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
    }

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null && request.getParameter("lg_COMPTE_CLIENT_ID") != "") {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);
    }
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && request.getParameter("lg_TIERS_PAYANT_ID") != "") {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID ******************" + lg_TIERS_PAYANT_ID);
    }
    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }
    if (request.getParameter("lg_EMPLACEMENT_ID") != null) {
        lg_EMPLACEMENT_ID = request.getParameter("lg_EMPLACEMENT_ID");
        new logger().OCategory.info("lg_EMPLACEMENT_ID " + lg_EMPLACEMENT_ID);
    }
    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    /*  if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dt_DEBUT = "2015-04-20";
    }
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
        String Odate = key.DateToString(dtFin, key.formatterMysqlShort2);

        dtFin = key.getDate(Odate, "23:59");
    }
    Date dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);*/
    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);

    try {

        lstTPreenregistrementCompteClientTiersPayent = OPreenregistrement.listTPreenregistrementTiersPayent(search_value, dt_DEBUT, dt_FIN, lg_USER_ID, lg_PREENREGISTREMENT_ID, lg_EMPLACEMENT_ID, lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID,"ALL");
    } catch (Exception e) {

    }

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
        try {
            OdataManager.getEm().refresh(lstTPreenregistrementCompteClientTiersPayent.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());
        json.put("lg_PREENREGISTREMENT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
        json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTTIERSPAYANTID());
        // json.put("str_STATUT", lstTPreenregistrementCompteClientTiersPayent.get(i).getStrSTATUT());
        json.put("int_PRICE", lstTPreenregistrementCompteClientTiersPayent.get(i).getIntPRICE());
        json.put("lg_CLIENT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getLgCLIENTID());
        json.put("lg_TIERS_PAYANT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        json.put("str_FULLNAME", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrFULLNAME());
        json.put("str_REF", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getStrREF());
        json.put("int_PRICE_TOTAL", lstTPreenregistrementCompteClientTiersPayent.get(i).getIntPRICERESTE());
        int int_PRICE_CLIENT = 0;
        try {

            int_PRICE_CLIENT = lstTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTID().getIntPRICE() - lstTPreenregistrementCompteClientTiersPayent.get(i).getIntPRICE();

        } catch (Exception Ex) {

        }

        json.put("int_PRICE_CLIENT", int_PRICE_CLIENT);

       
        json.put("dt_CREATED", date.DateToString(lstTPreenregistrementCompteClientTiersPayent.get(i).getDtCREATED(), date.formatterShort));
        json.put("str_FIRST_NAME", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME());
        json.put("str_LAST_NAME", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());


        arrayObj.put(json);

    }
    String result = "({\"total\":\"" + lstTPreenregistrementCompteClientTiersPayent.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>