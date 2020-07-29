<%@page import="dal.TFamilleGrossiste"%>
<%@page import="dal.TSuggestionOrderDetails"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.commandeManagement.suggestionManagement"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TSuggestionOrder"  %>
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
    List<TSuggestionOrderDetails> lstTSuggestionOrderDetails = new ArrayList<TSuggestionOrderDetails>();
    TFamilleGrossiste OTFamilleGrossiste = null;
    JSONObject json = null;
%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data suggestion order ");
%>



<!-- fin logic de gestion des page -->

<%    List<dal.TSuggestionOrder> lstTSuggestionOrder = new ArrayList<dal.TSuggestionOrder>();
    String lg_SUGGESTION_ORDER_ID = "%%", str_STATUT = "%%", search_value = "", str_Product = "";
    int int_TOTAL_VENTE = 0, int_TOTAL_ACHAT = 0, int_NOMBRE_ARTICLES = 0, nb = 0;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
 int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
       
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
       
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_SUGGESTION_ORDER_ID") != null) {
        if (request.getParameter("lg_SUGGESTION_ORDER_ID").toString().equals("ALL")) {
            lg_SUGGESTION_ORDER_ID = "%%";
        } else {
            lg_SUGGESTION_ORDER_ID = request.getParameter("lg_SUGGESTION_ORDER_ID").toString();
        }

    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().oCategory.info("str_STATUT : " + str_STATUT);
    }

    new logger().OCategory.info("lg_SUGGESTION_ORDER_ID   " + lg_SUGGESTION_ORDER_ID);
    suggestionManagement OsuggestionManagement = new suggestionManagement(OdataManager, OTUser);
    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager, OTUser);
    lstTSuggestionOrder = OsuggestionManagement.ListeSuggestionOrder(search_value, lg_SUGGESTION_ORDER_ID,start,limit);
   total=OsuggestionManagement.ListeSuggestionOrder(search_value, lg_SUGGESTION_ORDER_ID);
%>

<%    JSONArray arrayObj = new JSONArray();
    for (TSuggestionOrder suggestionOrder:lstTSuggestionOrder) {
      
        lg_SUGGESTION_ORDER_ID = suggestionOrder.getLgSUGGESTIONORDERID();
        str_STATUT = suggestionOrder.getStrSTATUT();
        
        lstTSuggestionOrderDetails = OWarehouseManager.getTSuggestionOrderDetails(lg_SUGGESTION_ORDER_ID);

        int_NOMBRE_ARTICLES = lstTSuggestionOrderDetails.size();
        nb = 0;
        int_TOTAL_ACHAT = 0;
        int_TOTAL_VENTE = 0;
        str_Product = "";
       
        for (int k = 0; k < lstTSuggestionOrderDetails.size(); k++) {
            OTFamilleGrossiste = OsuggestionManagement.getOfamilleGrossisteManagement().findFamilleGrossiste(lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getLgFAMILLEID(), suggestionOrder.getLgGROSSISTEID().getLgGROSSISTEID());
              
            int status=OsuggestionManagement.isOnAnotherSuggestion(lstTSuggestionOrderDetails.get(k).getLgFAMILLEID());
           
            if(status==1){
              str_Product = "<span style='background-color:#73C774;'> <b><span style='display:inline-block;width: 7%;'>" + (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getIntCIP()) +  "</span><span style='display:inline-block;width: 25%;'>" + lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + lstTSuggestionOrderDetails.get(k).getIntNUMBER() + ")</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTSuggestionOrderDetails.get(k).getIntPAFDETAIL(), '.') + " F CFA </span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTSuggestionOrderDetails.get(k).getIntPRICEDETAIL(), '.') + " F CFA " + "</span></b></span><br> " + str_Product; 
        
            }
            
            else if(status==2){ 
               str_Product = "<span style='background-color:#5fa2dd;'> <b><span style='display:inline-block;width: 7%;'>" + (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getIntCIP()) +  "</span><span style='display:inline-block;width: 25%;'>" + lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + lstTSuggestionOrderDetails.get(k).getIntNUMBER() + ")</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTSuggestionOrderDetails.get(k).getIntPAFDETAIL(), '.') + " F CFA </span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTSuggestionOrderDetails.get(k).getIntPRICEDETAIL(), '.') + " F CFA " + "</span></b></span><br> " + str_Product;   
            }
            
            else {
                str_Product = "<b><span style='display:inline-block;width: 7%;'>" + (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getIntCIP()) + "</span><span style='display:inline-block;width: 25%;'>" + lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + lstTSuggestionOrderDetails.get(k).getIntNUMBER() + ")</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTSuggestionOrderDetails.get(k).getIntPAFDETAIL(), '.') + " F CFA </span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTSuggestionOrderDetails.get(k).getIntPRICEDETAIL(), '.') + " F CFA " + "</span></b><br> " + str_Product; 
            }
           
            nb = nb + lstTSuggestionOrderDetails.get(k).getIntNUMBER();
        }
        int_TOTAL_ACHAT = OWarehouseManager.getPriceTotalAchat(lstTSuggestionOrderDetails);
        int_TOTAL_VENTE = OWarehouseManager.getPriceTotalVente(lstTSuggestionOrderDetails);
        json = new JSONObject();

        json.put("lg_SUGGESTION_ORDER_ID", suggestionOrder.getLgSUGGESTIONORDERID());
        json.put("int_NUMBER", nb);
        /*String lg_GROSSISTE_ID = lstTSuggestionOrder.get(i).getLgGROSSISTEID().getStrLIBELLE();
         json.put("lg_GROSSISTE_ID", lg_GROSSISTE_ID);*/
        json.put("lg_GROSSISTE_ID", suggestionOrder.getLgGROSSISTEID().getStrLIBELLE());
        json.put("int_DATE_BUTOIR_ARTICLE", suggestionOrder.getLgGROSSISTEID().getIntDATEBUTOIRARTICLE());
        
        // int_QTE_ARTICLES
        json.put("int_NOMBRE_ARTICLES", int_NOMBRE_ARTICLES);

        json.put("int_TOTAL_VENTE", int_TOTAL_VENTE);
        json.put("int_TOTAL_ACHAT", int_TOTAL_ACHAT);

        json.put("str_REF", suggestionOrder.getStrREF());

        json.put("str_FAMILLE_ITEM", str_Product);

        json.put("str_STATUT", suggestionOrder.getStrSTATUT());
        json.put("dt_CREATED", date.DateToString(suggestionOrder.getDtUPDATED(), date.formatterShort));
        json.put("dt_UPDATED", date.DateToString(suggestionOrder.getDtUPDATED(), date.NomadicUiFormat_Time));
        
        

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + total + " \" ,\"results\":" + arrayObj.toString() + "})";
  
%>

<%= result%>