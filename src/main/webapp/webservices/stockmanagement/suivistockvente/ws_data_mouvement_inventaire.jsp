<%@page import="toolkits.utils.jdom"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TInventaireFamille"%>
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
    TUser OTUser;
    List<TInventaireFamille> lstTInventaireFamille = new ArrayList<TInventaireFamille>();

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

<%    String lg_INVENTAIRE_ID = "%%", lg_USER_ID = "%%", search_value = "", str_ACTION = "Vente",
            lg_FAMILLE_ID = "%%", P_KEY = "", lg_FABRIQUANT_ID = "%%", lg_FAMILLEARTICLE_ID = "%%", lg_ZONE_GEO_ID = "%%";
    String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin = "";
    Date dtFin, dtDEBUT;

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
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

    if (request.getParameter("lg_INVENTAIRE_ID") != null) {
        lg_INVENTAIRE_ID = request.getParameter("lg_INVENTAIRE_ID");
        new logger().OCategory.info("lg_INVENTAIRE_ID " + lg_INVENTAIRE_ID);
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
        dtDEBUT = date.stringToDate(dt_DEBUT, date.formatterMysqlShort);
    }
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
    } else {
        dtFin = date.stringToDate(dt_FIN, date.formatterMysqlShort);
    }
    OdateFin = date.DateToString(dtFin, date.formatterMysqlShort2);
    dtFin = date.getDate(OdateFin, "23:59");
    OdateDebut = date.DateToString(dtDEBUT, date.formatterMysqlShort2);
    dtDEBUT = date.getDate(OdateDebut, "00:00");
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  " + dt_FIN + "  dt_DEBUT  " + dt_DEBUT + " lg_FAMILLE_ID " + lg_FAMILLE_ID + " dtFin:" + dtFin);

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, OTUser);

    lstTInventaireFamille = OSnapshotManager.listTInventaireFamille(search_value, dtDEBUT, dtFin, lg_FAMILLE_ID, lg_USER_ID, lg_INVENTAIRE_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID);
%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTInventaireFamille.size()) {
            DATA_PER_PAGE = lstTInventaireFamille.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTInventaireFamille.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTInventaireFamille.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

       json.put("dt_DAY", date.DateToString(lstTInventaireFamille.get(i).getLgINVENTAIREID().getDtUPDATED(), date.formatterShort));
        json.put("int_NUMBER_VENTE", lstTInventaireFamille.get(i).getIntNUMBERINIT());
        json.put("int_NUMBER_RETOUR", lstTInventaireFamille.get(i).getIntNUMBER());
        json.put("lg_USER_ID", lstTInventaireFamille.get(i).getLgINVENTAIREID().getLgUSERID().getStrFIRSTNAME() + " " + lstTInventaireFamille.get(i).getLgINVENTAIREID().getLgUSERID().getStrLASTNAME());
       

        json.put("lg_FAMILLE_ID", lstTInventaireFamille.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("int_CIP", lstTInventaireFamille.get(i).getLgFAMILLEID().getIntCIP());
        json.put("str_NAME", lstTInventaireFamille.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("dt_UPDATED", date.DateToString(lstTInventaireFamille.get(i).getLgINVENTAIREID().getDtUPDATED(), date.NomadicUiFormat_Time));
        

        arrayObj.put(json);

    }

    String result = "({\"total\":\"" + lstTInventaireFamille.size() + " \",\"results\":" + arrayObj.toString() + "})";
   

%>

<%= result%>