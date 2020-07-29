<%@page import="bll.teller.tellerManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TTypeStockFamille"  %>
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
    List<TTypeStockFamille> lstTTypeStockFamille = new ArrayList<TTypeStockFamille>();
    TFamilleStock OTFamilleStock = null;
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data reserve ");
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

<%    String lg_TYPE_STOCK_ID = "1", search_value = "", lg_TYPE_STOCK_RESERVE_ID = "2", lg_TYPE_STOCK_FAMILLE_ID = "%%", lg_FAMILLE_ID = "%%", str_TYPE_TRANSACTION = "%%";

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_TYPE_TRANSACTION") != null) {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
    }

    if (request.getParameter("lg_TYPE_STOCK_ID") != null && request.getParameter("lg_TYPE_STOCK_ID") != "") {
        lg_TYPE_STOCK_ID = request.getParameter("lg_TYPE_STOCK_ID");
        new logger().OCategory.info("lg_TYPE_STOCK_ID " + lg_TYPE_STOCK_ID);
    }

    if (request.getParameter("lg_TYPE_STOCK_FAMILLE_ID") != null && request.getParameter("lg_TYPE_STOCK_FAMILLE_ID") != "") {
        lg_TYPE_STOCK_FAMILLE_ID = request.getParameter("lg_TYPE_STOCK_ID");
        new logger().OCategory.info("lg_TYPE_STOCK_FAMILLE_ID " + lg_TYPE_STOCK_FAMILLE_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    new logger().OCategory.info("search_value    " + search_value);

    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    StockManager OStockManager = new StockManager(OdataManager, OTUser);
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);

    if (str_TYPE_TRANSACTION.equalsIgnoreCase("%%") || str_TYPE_TRANSACTION.equalsIgnoreCase("ALL")) {
        lstTTypeStockFamille = OStockManager.listeTTypeStockFamille(search_value, lg_TYPE_STOCK_FAMILLE_ID, lg_TYPE_STOCK_RESERVE_ID, lg_FAMILLE_ID);
    } else {
        lstTTypeStockFamille = OStockManager.listeTTypeStockFamilleReassort(search_value, lg_TYPE_STOCK_FAMILLE_ID, lg_TYPE_STOCK_RESERVE_ID, lg_FAMILLE_ID);
    }
    new logger().OCategory.info("Size lstTTypeStockFamille  " + lstTTypeStockFamille.size());

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTTypeStockFamille.size()) {
            DATA_PER_PAGE = lstTTypeStockFamille.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTypeStockFamille.size() - (DATA_PER_PAGE * (pgInt)));
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

        json.put("lg_FAMILLE_ID", lstTTypeStockFamille.get(i).getLgFAMILLEID().getLgFAMILLEID());

        json.put("str_NAME", lstTTypeStockFamille.get(i).getLgFAMILLEID().getStrNAME());
        json.put("str_DESCRIPTION", lstTTypeStockFamille.get(i).getLgFAMILLEID().getStrDESCRIPTION());

        json.put("int_CIP", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntCIP());

        try {
            json.put("int_STOCK_REAPROVISONEMENT", String.valueOf(OStockManager.getQuantiteStockByFamilleTypeStock(lstTTypeStockFamille.get(i).getLgFAMILLEID().getLgFAMILLEID(), lg_TYPE_STOCK_RESERVE_ID)));
        } catch (Exception e) {

        }

        try {
            json.put("lg_ZONE_GEO_ID", lstTTypeStockFamille.get(i).getLgFAMILLEID().getLgZONEGEOID().getStrLIBELLEE());
        } catch (Exception e) {
        }

        try {
            json.put("int_NUMBER_ENTREE", String.valueOf(OStockManager.getQuantiteReassort(lstTTypeStockFamille.get(i).getLgFAMILLEID().getLgFAMILLEID())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int int_NUMBER = 0, int_NUMBER_AVAILABLE = 0;
       try {
            
            OTFamilleStock = OtellerManagement.getTProductItemStock(lstTTypeStockFamille.get(i).getLgFAMILLEID().getLgFAMILLEID());
            int_NUMBER = OTFamilleStock.getIntNUMBER();
            int_NUMBER_AVAILABLE = OTFamilleStock.getIntNUMBERAVAILABLE();
        }catch(Exception e) {
            
        }
        json.put("int_NUMBER", int_NUMBER_AVAILABLE);
       
        json.put("str_STATUT", lstTTypeStockFamille.get(i).getStrSTATUT());
        json.put("dt_CREATED", lstTTypeStockFamille.get(i).getDtCREATED() != null ? date.DateToString(lstTTypeStockFamille.get(i).getDtCREATED(), date.formatterShort) : "");
        json.put("dt_UPDATED", lstTTypeStockFamille.get(i).getDtUPDATED() != null ? date.DateToString(lstTTypeStockFamille.get(i).getDtUPDATED(), date.formatterShort) : "");

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTTypeStockFamille.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>