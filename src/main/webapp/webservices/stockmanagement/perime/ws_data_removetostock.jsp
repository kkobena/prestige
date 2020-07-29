<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="bll.teller.tellerManagement"%>
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


<%
    dataManager OdataManager = new dataManager();
    List<TWarehouse> lstTWarehouse = new ArrayList<TWarehouse>();
    TUser OTUser;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data perime retire du stock ");
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

<%    String lg_FAMILLE_ID = "%%", search_value = "";
String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin = "";
    Date dtFin, dtDEBUT;

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
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
    //StockManager OStockManager = new StockManager(OdataManager);
    //tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager, OTUser);

    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    lstTWarehouse = OWarehouseManager.listeTWarehouseRemoveToStock(search_value, dtDEBUT, dtFin, lg_FAMILLE_ID);



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
            OdataManager.getEm().refresh(lstTWarehouse.get(i).getLgFAMILLEID());
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_WAREHOUSE_ID", lstTWarehouse.get(i).getLgWAREHOUSEID());
        json.put("lg_FAMILLE_ID", lstTWarehouse.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("str_NAME", lstTWarehouse.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("lg_FAMILLEARTICLE_ID", lstTWarehouse.get(i).getLgFAMILLEID().getLgFAMILLEARTICLEID().getStrLIBELLE());

        json.put("lg_ZONE_GEO_ID", lstTWarehouse.get(i).getLgFAMILLEID().getLgZONEGEOID().getStrLIBELLEE());
        json.put("int_CIP", lstTWarehouse.get(i).getLgFAMILLEID().getIntCIP());
        json.put("int_NUMBER_AJUSTEMENT_IN", lstTWarehouse.get(i).getIntNUMBERDELETE());
        json.put("str_CODE_TAUX_REMBOURSEMENT", lstTWarehouse.get(i).getIntNUMLOT());
        json.put("dt_UPDATED", date.DateToString(lstTWarehouse.get(i).getDtUPDATED(), date.formatterShort));
        json.put("dt_PEREMPTION", date.DateToString(lstTWarehouse.get(i).getDtPEREMPTION(), date.formatterShort));
        json.put("lg_CODE_ACTE_ID", date.DateToString(dtDEBUT, date.formatterShort) + " au " + date.DateToString(dtFin, date.formatterShort));

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTWarehouse.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>