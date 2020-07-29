<%@page import="bll.teller.SnapshotManager"%>
<%@page import="dal.TMouvement"%>
<%@page import="dal.TFamilleGrossiste"%>
<%@page import="bll.configManagement.familleGrossisteManagement"%>
<%@page import="bll.commandeManagement.suggestionManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="dal.TSuggestionOrderDetails"%>

<%@page import="bll.configManagement.familleManagement"%>
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



<%
    dataManager OdataManager = new dataManager();
    TUser OTUser;
    TFamilleStock OTFamillestock = null;
    TFamilleGrossiste OTFamilleGrossiste = null;
    List<TSuggestionOrderDetails> lstTSuggestionOrderDetails = new ArrayList<TSuggestionOrderDetails>(),
            lstTSuggestionOrderDetailsBase = new ArrayList<TSuggestionOrderDetails>();
    JSONObject json = null;

%>


<%    String lg_SUGGESTION_ORDER_DETAILS_ID = "%%", lg_SUGGESTION_ORDER_ID = "%%", search_value = "";
    Date now = new Date();
    int int_ACHAT = 0, int_VENTE = 0, start = 0, limit = 10, total = 0;
 
    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
    suggestionManagement OsuggestionManagement = new suggestionManagement(OdataManager, OTUser);
    //  familleGrossisteManagement OfamilleGrossisteManagement = new familleGrossisteManagement(OdataManager);
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, OTUser);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_SUGGESTION_ORDER_DETAILS_ID") != null) {
        lg_SUGGESTION_ORDER_DETAILS_ID = request.getParameter("lg_SUGGESTION_ORDER_DETAILS_ID").toString();
        new logger().OCategory.info("lg_SUGGESTION_ORDER_DETAILS_ID " + lg_SUGGESTION_ORDER_DETAILS_ID);
    }

    if (request.getParameter("lg_SUGGESTION_ORDER_ID") != null) {
        lg_SUGGESTION_ORDER_ID = request.getParameter("lg_SUGGESTION_ORDER_ID").toString();
        new logger().OCategory.info("lg_SUGGESTION_ORDER_ID " + lg_SUGGESTION_ORDER_ID);
    }

    lstTSuggestionOrderDetailsBase = OsuggestionManagement.ListeSuggestionOrderDetails(search_value, lg_SUGGESTION_ORDER_ID, start, limit);
    lstTSuggestionOrderDetails = OsuggestionManagement.ListeSuggestionOrderDetails(search_value, lg_SUGGESTION_ORDER_ID);
    total = lstTSuggestionOrderDetails.size();
%>


<%    JSONArray arrayObj = new JSONArray();

    for(int i = 0; i < (lstTSuggestionOrderDetailsBase.size() < limit ? lstTSuggestionOrderDetailsBase.size() : limit); i++) {

        json = new JSONObject();

        OTFamillestock = OtellerManagement.getTProductItemStock(lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgFAMILLEID(), OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        OTFamilleGrossiste = OsuggestionManagement.getOfamilleGrossisteManagement().findFamilleGrossiste(lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgFAMILLEID(), lstTSuggestionOrderDetailsBase.get(i).getLgSUGGESTIONORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("lg_SUGGESTION_ORDER_DETAILS_ID", lstTSuggestionOrderDetailsBase.get(i).getLgSUGGESTIONORDERDETAILSID());
        json.put("lg_FAMILLE_ID", lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("lg_GROSSISTE_ID", lstTSuggestionOrderDetailsBase.get(i).getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("str_FAMILLE_CIP", (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntCIP()));
        json.put("str_FAMILLE_NAME", lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("int_DATE_BUTOIR_ARTICLE", (lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgCODEGESTIONID() != null ? lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgCODEGESTIONID().getIntDATEBUTOIRARTICLE() : 0));
        json.put("int_STOCK", OTFamillestock.getIntNUMBERAVAILABLE());

        json.put("int_NUMBER", lstTSuggestionOrderDetailsBase.get(i).getIntNUMBER());
        // json.put("FLAG", (lstTSuggestionOrderDetailsBase.get(i).getBFalg()!=null?lstTSuggestionOrderDetailsBase.get(i).getBFalg():false));
      // json.put("FLAG", OsuggestionManagement.isOnAnotherSuggestion(lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgFAMILLEID()));
          int status=OsuggestionManagement.isOnAnotherSuggestion(lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID());
      json.put("STATUS",status);
      
//  
         json.put("int_SEUIL", lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntSEUILMIN());
        json.put("str_STATUT", lstTSuggestionOrderDetailsBase.get(i).getStrSTATUT());
        // lg_FAMILLE_PRIX_VENTE
        json.put("lg_FAMILLE_PRIX_VENTE", lstTSuggestionOrderDetailsBase.get(i).getIntPRICEDETAIL());
        // lg_FAMILLE_PRIX_ACHAT
        json.put("lg_FAMILLE_PRIX_ACHAT", lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntPAT());
        json.put("int_PAF_SUGG", lstTSuggestionOrderDetailsBase.get(i).getIntPAFDETAIL());
        json.put("int_PRIX_REFERENCE", lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntPRICETIPS());

        int int_QTE_REASSORT = 0;
        try {
            int_QTE_REASSORT = OTFamillestock.getIntNUMBERAVAILABLE() - lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntSEUILMIN();
            new logger().OCategory.info("int_QTE_REASSORT " + int_QTE_REASSORT);
            if (int_QTE_REASSORT < 0) {
                int_QTE_REASSORT = -1 * int_QTE_REASSORT;
            } else {
                int_QTE_REASSORT = 0;
            }
        } catch (Exception e) {
        }
        json.put("int_QTE_REASSORT", int_QTE_REASSORT);

        int_ACHAT = int_ACHAT + lstTSuggestionOrderDetailsBase.get(i).getIntPAFDETAIL();

        int_VENTE = int_VENTE + lstTSuggestionOrderDetailsBase.get(i).getIntPRICEDETAIL();

        json.put("int_ACHAT", int_ACHAT);
        json.put("int_VENTE", int_VENTE);

        json.put("int_VALUE0", OSnapshotManager.getQauntityVenteByArticle(lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntCIP(), date.DateToString(date.getFirstDayofSomeMonth(Integer.parseInt(date.getoMois(now)) - (0 + Integer.parseInt(date.getoMois(now)))), date.formatterMysqlShort), date.DateToString(date.getLastDayofSomeMonth(Integer.parseInt(date.getoMois(now)) - (0 + Integer.parseInt(date.getoMois(now)))), date.formatterMysqlShort), lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgFAMILLEID(), "%%", "%%"));
        json.put("int_VALUE1", OSnapshotManager.getQauntityVenteByArticle(lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntCIP(), date.DateToString(date.getFirstDayofSomeMonth(Integer.parseInt(date.getoMois(now)) - (1 + Integer.parseInt(date.getoMois(now)))), date.formatterMysqlShort), date.DateToString(date.getLastDayofSomeMonth(Integer.parseInt(date.getoMois(now)) - (1 + Integer.parseInt(date.getoMois(now)))), date.formatterMysqlShort), lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgFAMILLEID(), "%%", "%%"));
        json.put("int_VALUE2", OSnapshotManager.getQauntityVenteByArticle(lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntCIP(), date.DateToString(date.getFirstDayofSomeMonth(Integer.parseInt(date.getoMois(now)) - (2 + Integer.parseInt(date.getoMois(now)))), date.formatterMysqlShort), date.DateToString(date.getLastDayofSomeMonth(Integer.parseInt(date.getoMois(now)) - (2 + Integer.parseInt(date.getoMois(now)))), date.formatterMysqlShort), lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgFAMILLEID(), "%%", "%%"));
        json.put("int_VALUE3", OSnapshotManager.getQauntityVenteByArticle(lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getIntCIP(), date.DateToString(date.getFirstDayofSomeMonth(Integer.parseInt(date.getoMois(now)) - (3 + Integer.parseInt(date.getoMois(now)))), date.formatterMysqlShort), date.DateToString(date.getLastDayofSomeMonth(Integer.parseInt(date.getoMois(now)) - (3 + Integer.parseInt(date.getoMois(now)))), date.formatterMysqlShort), lstTSuggestionOrderDetailsBase.get(i).getLgFAMILLEID().getLgFAMILLEID(), "%%", "%%"));

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";
 
%>

<%= result%>