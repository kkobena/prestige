<%@page import="toolkits.utils.jdom"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>



<%
    dataManager OdataManager = new dataManager();
    date key = new date();
    TUser OTUser;
    List<TPreenregistrementDetail> lstTPreenregistrementDetail = new ArrayList<TPreenregistrementDetail>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination;
    new logger().OCategory.info("dans ws data ws_data_mouvement_vente");
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

<%    String lg_PREENREGISTREMENT_ID = "%%", lg_USER_ID = "%%", search_value = "", str_ACTION = "Vente",
            lg_FAMILLE_ID = "%%", P_KEY = "", lg_FABRIQUANT_ID = "%%", lg_FAMILLEARTICLE_ID = "%%", lg_ZONE_GEO_ID = "%%",
            str_STAK = commonparameter.str_ACTION_VENTE;
    String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin = "";
    Date dtFin, dtDEBUT;

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_STAK") != null) {
        str_STAK = request.getParameter("str_STAK");
        new logger().OCategory.info("str_STAK " + str_STAK);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("P_KEY") != null) {
        P_KEY = request.getParameter("P_KEY");
        new logger().OCategory.info("P_KEY " + P_KEY);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (request.getParameter("lg_FABRIQUANT_ID") != null && request.getParameter("lg_FABRIQUANT_ID") != "") {
        lg_FABRIQUANT_ID = request.getParameter("lg_FABRIQUANT_ID");
        new logger().OCategory.info("lg_FABRIQUANT_ID " + lg_FABRIQUANT_ID);
    }

    if (request.getParameter("lg_FAMILLEARTICLE_ID") != null && request.getParameter("lg_FAMILLEARTICLE_ID") != "") {
        lg_FAMILLEARTICLE_ID = request.getParameter("lg_FAMILLEARTICLE_ID");
        new logger().OCategory.info("lg_FAMILLEARTICLE_ID " + lg_FAMILLEARTICLE_ID);
    }

    if (request.getParameter("lg_ZONE_GEO_ID") != null && request.getParameter("lg_ZONE_GEO_ID") != "") {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
        new logger().OCategory.info("lg_ZONE_GEO_ID " + lg_ZONE_GEO_ID);
    }

    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dtDEBUT = new Date();
    } else {
        dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
    }
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
    }
    OdateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
    dtFin = key.getDate(OdateFin, "23:59");
    OdateDebut = key.DateToString(dtDEBUT, key.formatterMysqlShort2);
    dtDEBUT = key.getDate(OdateDebut, "00:00");

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, OTUser);

    new logger().OCategory.info(" dtFIN********  " + dtFin + "  dtDEBUT***********  " + dtDEBUT + " lg_FAMILLE_ID " + lg_FAMILLE_ID + " dtFin:" + dtFin);

    lstTPreenregistrementDetail = OSnapshotManager.listTPreenregistrementDetail(search_value, dtDEBUT, dtFin, lg_FAMILLE_ID, lg_USER_ID, lg_PREENREGISTREMENT_ID, lg_FABRIQUANT_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID);

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTPreenregistrementDetail.size()) {
            DATA_PER_PAGE = lstTPreenregistrementDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTPreenregistrementDetail.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTPreenregistrementDetail.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("dt_DAY", date.DateToString(lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getDtUPDATED(), date.formatterShort));
        json.put("int_NUMBER_VENTE", lstTPreenregistrementDetail.get(i).getIntQUANTITY() + (lstTPreenregistrementDetail.get(i).getIntFREEPACKNUMBER() != null ? lstTPreenregistrementDetail.get(i).getIntFREEPACKNUMBER() : 0));
        json.put("int_NUMBER_RETOUR", lstTPreenregistrementDetail.get(i).getIntPRICE());
        json.put("lg_USER_ID", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getLgUSERID().getStrFIRSTNAME() + " " + lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getLgUSERID().getStrLASTNAME());
        json.put("str_CODE_TAUX_REMBOURSEMENT", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getLgTYPEVENTEID().getStrNAME());

        json.put("lg_FAMILLE_ID", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("int_CIP", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getIntCIP());
        json.put("int_NUMBER", lstTPreenregistrementDetail.get(i).getIntQUANTITY());
        json.put("str_NAME", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        //  json.put("int_STOCK_REAPROVISONEMENT", lstTPreenregistrementDetail.get(i).getIntNUMBER());
        json.put("dt_UPDATED", date.DateToString(lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getDtUPDATED(), date.formatterShort));
        json.put("dt_LAST_VENTE", date.DateToString(lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getDtUPDATED(), date.NomadicUiFormat_Time));

        json.put("str_ACTION", str_ACTION);

        arrayObj.put(json);

    }

    String result = "({\"total\":\"" + lstTPreenregistrementDetail.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>