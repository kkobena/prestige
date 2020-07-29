<%@page import="bll.common.Parameter"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="dal.TParameters"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="dal.TWarehouse"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TWarehouse"%>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();


    List<EntityData> lstTWarehouse = new ArrayList<EntityData>();
    TParameters OTParameters = new TParameters();
    TUser OTUser;

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data liste perime ");
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
    String str_TYPE_TRANSACTION = "%%", lg_FAMILLE_ID = "%%", search_value = "", lg_GROSSISTE_ID = "%%";

    String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin = "";
    Date dtFin, dtDEBUT;
    
    OdataManager.initEntityManager();
    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager);
    TparameterManager OTparameterManager = new TparameterManager(OdataManager);
    OTParameters = OTparameterManager.getParameter("KEY_MONTH_PERIME");
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
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
        dtDEBUT = date.stringToDate(dt_FIN, date.formatterMysqlShort);
    }

    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = date.GetNewDate((Integer.parseInt(OTParameters.getStrVALUE())*30));
    } else {
        dtFin = date.stringToDate(dt_FIN, date.formatterMysqlShort);
    }
    OdateFin = date.DateToString(dtFin, date.formatterMysqlShort2);
    dtFin = date.getDate(OdateFin, "23:59");
    OdateDebut = date.DateToString(dtDEBUT, date.formatterMysqlShort2);
    dtDEBUT = date.getDate(OdateDebut, "00:00");
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  " + dt_FIN + "  dt_DEBUT  " + dt_DEBUT + " lg_FAMILLE_ID " + lg_FAMILLE_ID + " dtFin:" + dtFin+"dtDebut:"+dtDEBUT);

    if (request.getParameter("lg_GROSSISTE_ID") != null && request.getParameter("lg_GROSSISTE_ID") != "") {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (request.getParameter("str_TYPE_TRANSACTION") != null && request.getParameter("str_TYPE_TRANSACTION") != "") {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
    }

    
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());

    if (str_TYPE_TRANSACTION.equalsIgnoreCase(Parameter.KEY_PARAM_PERIME)) {
        // lstTWarehouse = OWarehouseManager.listTFamillePerimeOnly(search_value, lg_FAMILLE_ID);
        lstTWarehouse = OWarehouseManager.listTFamillePerimeOnly(search_value, lg_FAMILLE_ID, dtDEBUT, dtFin);
    } else if (str_TYPE_TRANSACTION.equalsIgnoreCase(Parameter.KEY_PERIMER_ENCOURS)) {
        lstTWarehouse = OWarehouseManager.listTFamillePerimeEncours(search_value, lg_FAMILLE_ID, dtDEBUT, dtFin);
        // lstTWarehouse = OWarehouseManager.listTFamillePerimeEncours(search_value, lg_FAMILLE_ID);
    } else {
        lstTWarehouse = OWarehouseManager.listTFamillePerime(search_value, lg_FAMILLE_ID, dtDEBUT, dtFin);
    }

    new logger().OCategory.info("Size lstTWarehouse  " + lstTWarehouse.size());


%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTWarehouse.size()) {
            DATA_PER_PAGE = lstTWarehouse.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTWarehouse.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTWarehouse.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_WAREHOUSE_ID", lstTWarehouse.get(i).getStr_value1());
        json.put("lg_FAMILLE_ID", lstTWarehouse.get(i).getStr_value2());
        json.put("str_NAME", lstTWarehouse.get(i).getStr_value3());
        json.put("lg_GROSSISTE_ID", lstTWarehouse.get(i).getStr_value4());
        //json.put("lg_ZONE_GEO_ID", lstTWarehouse.get(i).getStr_value5());
        json.put("int_NUMBER", lstTWarehouse.get(i).getStr_value6());
        json.put("int_NUM_LOT", lstTWarehouse.get(i).getStr_value7());
        json.put("int_STOCK_REAPROVISONEMENT", lstTWarehouse.get(i).getStr_value8());
        json.put("dt_PEREMPTION", lstTWarehouse.get(i).getStr_value9());
        json.put("str_STATUT", lstTWarehouse.get(i).getStr_value10());
        json.put("etat", lstTWarehouse.get(i).getStr_value11());
        json.put("int_CIP", lstTWarehouse.get(i).getStr_value12());

        arrayObj.put(json);

    }

    String result = "({\"total\":\"" + lstTWarehouse.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);


%>

<%= result%>