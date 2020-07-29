<%@page import="java.time.LocalDate"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TWarehouse"%>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    List<TWarehouse> lstTWarehouse = new ArrayList<TWarehouse>();
    List<EntityData> lsteDatas = new ArrayList<EntityData>();

    int reelSize = 0;
%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data ws_data_mouvement_entree-----------------------------------------------------------");
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

<%    String lg_USER_ID = "%%", lg_GROSSISTE_ID = "%%", search_value = "", lg_FAMILLE_ID = "%%",
            str_ACTION = "Entrée en stock", lg_FABRIQUANT_ID = "%%", lg_FAMILLEARTICLE_ID = "%%", lg_ZONE_GEO_ID = "%%";

    String dt_DEBUT = LocalDate.now().toString(), dt_FIN = dt_DEBUT, OdateDebut = "", OdateFin = "";
    Date dtFin, dtDEBUT;
    int start = 0, limit = 20;
    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("lg_GROSSISTE_ID") != null && request.getParameter("lg_GROSSISTE_ID") != "") {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
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

    if (request.getParameter("datedebut") != null && !"".equals(request.getParameter("datedebut"))) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null && !"".equals(request.getParameter("datefin"))) {
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
    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager, OTUser);
    if (OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equals("1")) {
        lstTWarehouse = OWarehouseManager.listeWarehouses(search_value, lg_FAMILLE_ID, dt_DEBUT, dt_FIN, lg_GROSSISTE_ID, lg_FABRIQUANT_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, false, start, limit);
        reelSize = OWarehouseManager.listeWarehouses(search_value, lg_FAMILLE_ID, dt_DEBUT, dt_FIN, lg_GROSSISTE_ID, lg_FABRIQUANT_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID);
    } else {
        lsteDatas = OWarehouseManager.getEntreeDepot(lg_FAMILLE_ID, dtDEBUT, dtFin);
        reelSize = lsteDatas.size();

    }

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > reelSize) {
            DATA_PER_PAGE = reelSize;
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (reelSize - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    if (OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equals("1")) {
        for (TWarehouse elem : lstTWarehouse) {
            JSONObject json = new JSONObject();
            json.put("dt_DAY", date.DateToString(elem.getDtCREATED(), date.formatterOrange));
            json.put("int_NUMBER_ENTREE", elem.getIntNUMBER());
            json.put("int_PRICE", elem.getIntNUMBER() * elem.getLgFAMILLEID().getDblPRIXMOYENPONDERE());
            json.put("lg_USER_ID", elem.getLgUSERID().getStrFIRSTNAME() + " " + elem.getLgUSERID().getStrLASTNAME());
            json.put("lg_GROSSISTE_ID", elem.getLgGROSSISTEID().getStrLIBELLE());
            json.put("int_NUM_LOT", elem.getIntNUMLOT());
            //  json.put("dt_UPDATED", date.DateToString(elem.getDtPEREMPTION(), date.formatterShort));

            json.put("lg_FAMILLE_ID", elem.getLgFAMILLEID().getLgFAMILLEID());
            json.put("int_CIP", elem.getLgFAMILLEID().getIntCIP());
            json.put("int_NUMBER", elem.getIntNUMBER());
            json.put("str_NAME", elem.getLgFAMILLEID().getStrNAME());
            
            json.put("dt_UPDATED",(elem.getDtPEREMPTION()!=null?date.formatterShort.format(elem.getDtPEREMPTION()):"") );
            json.put("dt_LAST_VENTE", date.DateToString(elem.getDtUPDATED(), date.NomadicUiFormat_Time));
            json.put("str_ACTION", str_ACTION);

            arrayObj.put(json);

        }
    } else {
        for (int i = pgInt; i < pgInt_Last; i++) {
            JSONObject json = new JSONObject();

            try {
                OdataManager.getEm().refresh(lstTWarehouse.get(i));
            } catch (Exception er) {
            }

            json.put("dt_DAY", lsteDatas.get(i).getStr_value1());
            json.put("int_NUMBER_ENTREE", lsteDatas.get(i).getStr_value2());
            json.put("int_PRICE", lsteDatas.get(i).getStr_value3());
            json.put("lg_USER_ID", lsteDatas.get(i).getStr_value4());
            json.put("lg_GROSSISTE_ID", lsteDatas.get(i).getStr_value5());
            json.put("int_NUM_LOT", "");

            json.put("lg_FAMILLE_ID", lsteDatas.get(i).getStr_value6());
            json.put("int_CIP", lsteDatas.get(i).getStr_value7());

            json.put("str_NAME", lsteDatas.get(i).getStr_value8());

            json.put("str_ACTION", str_ACTION);

            arrayObj.put(json);

        }
    }

    String result = "({\"total\":\"" + reelSize + " \",\"results\":" + arrayObj.toString() + "})";


%>

<%= result%>