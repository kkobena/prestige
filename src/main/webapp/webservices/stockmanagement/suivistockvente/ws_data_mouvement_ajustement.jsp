<%@page import="toolkits.utils.jdom"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TAjustementDetail"%>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();
    date key = new date();
    TUser OTUser;
    List<TAjustementDetail> lstTAjustementDetail = new ArrayList<TAjustementDetail>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination;
    new logger().OCategory.info("dans ws data ws_data_mouvement_ajustement");
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

<%    String search_value = "", lg_FAMILLE_ID = "%%", lg_USER_ID = "%%", lg_AJUSTEMENT_ID = "%%";
    String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin = "", str_TYPE_ACTION = "%%";
    Date dtFin, dtDEBUT;

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_TYPE_ACTION") != null) {
        str_TYPE_ACTION = request.getParameter("str_TYPE_ACTION");
        new logger().OCategory.info("str_TYPE_ACTION " + str_TYPE_ACTION);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
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
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  " + dt_FIN + "  dt_DEBUT  " + dt_DEBUT + " lg_FAMILLE_ID " + lg_FAMILLE_ID + " dtFin:" + dtFin);

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, OTUser);

    lstTAjustementDetail = OSnapshotManager.listTAjustementDetail(search_value, dtDEBUT, dtFin, lg_FAMILLE_ID, lg_AJUSTEMENT_ID, lg_USER_ID, str_TYPE_ACTION);
%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTAjustementDetail.size()) {
            DATA_PER_PAGE = lstTAjustementDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTAjustementDetail.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTAjustementDetail.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("int_TAUX_MARQUE", lstTAjustementDetail.get(i).getIntNUMBER());

        json.put("dt_DAY", date.DateToString(lstTAjustementDetail.get(i).getLgAJUSTEMENTID().getDtUPDATED(), date.formatterShort));
        json.put("lg_FAMILLE_ID", lstTAjustementDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("str_NAME", lstTAjustementDetail.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("int_CIP", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntCIP());
        json.put("dt_UPDATED", date.DateToString(lstTAjustementDetail.get(i).getLgAJUSTEMENTID().getDtUPDATED(), date.NomadicUiFormat_Time));
        json.put("lg_USER_ID", lstTAjustementDetail.get(i).getLgAJUSTEMENTID().getLgUSERID().getStrFIRSTNAME() + " " + lstTAjustementDetail.get(i).getLgAJUSTEMENTID().getLgUSERID().getStrLASTNAME());

        arrayObj.put(json);

    }

    String result = "({\"total\":\"" + lstTAjustementDetail.size() + " \",\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>