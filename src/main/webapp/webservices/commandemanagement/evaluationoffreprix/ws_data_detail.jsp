<%@page import="bll.configManagement.familleGrossisteManagement"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TEvaluationoffreprix"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>

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
    List<TEvaluationoffreprix> lstTEvaluationoffreprix = new ArrayList<TEvaluationoffreprix>();
    JSONObject json =  null;
    TUser OTUser = null;
    TFamilleStock OTFamillestock = null;
%>

<%    
    String lg_EVALUATIONOFFREPRIX_ID = "", search_value = "";
     int start = 0, limit = jdom.int_size_pagination, total = 0;

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

    
    if (request.getParameter("lg_EVALUATIONOFFREPRIX_ID") != null) {
        lg_EVALUATIONOFFREPRIX_ID = request.getParameter("lg_EVALUATIONOFFREPRIX_ID");
        new logger().OCategory.info("lg_EVALUATIONOFFREPRIX_ID " + lg_EVALUATIONOFFREPRIX_ID);
    }

    OdataManager.initEntityManager();
OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    orderManagement OorderManagement = new orderManagement(OdataManager);
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
   
    lstTEvaluationoffreprix = OorderManagement.getAllTEvaluationoffreprix(search_value, commonparameter.statut_enable, start, limit);
    total = OorderManagement.getAllTEvaluationoffreprix(search_value, commonparameter.statut_enable).size();

%>


<%    JSONArray arrayObj = new JSONArray();

    for(int i = 0; i < (total < limit ? total : limit); i++) {
        json = new JSONObject();
        OTFamillestock = OtellerManagement.getTProductItemStock(lstTEvaluationoffreprix.get(i).getLgFAMILLEID().getLgFAMILLEID());
         json.put("lg_ORDERDETAIL_ID", lstTEvaluationoffreprix.get(i).getLgEVALUATIONOFFREPRIXID());
         json.put("int_PRICE", lstTEvaluationoffreprix.get(i).getIntPRICEOFFRE());
         json.put("lg_FAMILLE_CIP", lstTEvaluationoffreprix.get(i).getLgFAMILLEID().getIntCIP());
         json.put("lg_FAMILLE_NAME", lstTEvaluationoffreprix.get(i).getLgFAMILLEID().getStrDESCRIPTION());
         json.put("lg_FAMILLE_QTE_STOCK", OTFamillestock != null ? OTFamillestock.getIntNUMBERAVAILABLE() : 0);
         json.put("lg_FAMILLE_PRIX_VENTE", lstTEvaluationoffreprix.get(i).getLgFAMILLEID().getIntPAF());
         json.put("lg_FAMILLE_PRIX_ACHAT", lstTEvaluationoffreprix.get(i).getIntPRICEOFFRE());
         json.put("int_NUMBER", lstTEvaluationoffreprix.get(i).getIntNUMBER());
         json.put("int_QTE_REP_GROSSISTE", lstTEvaluationoffreprix.get(i).getIntNUMBERGRATUIT());
         json.put("int_PRIX_REFERENCE", lstTEvaluationoffreprix.get(i).getIntMOISLIQUIDATION());
       

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + total + "\" ,\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>