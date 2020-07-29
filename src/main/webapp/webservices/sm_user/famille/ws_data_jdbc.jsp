<%@page import="dal.TParameters"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TTypeStockFamille"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TFamille"  %>
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
    List<TFamille> lstTFamille = new ArrayList<TFamille>();
    TFamilleStock OTFamilleStock = null;
    JSONArray arrayObj = new JSONArray();
    JSONObject json = null;
%>

<%
    new logger().OCategory.info("dans ws data famille jdbc");
    String lg_FAMILLE_ID = "%%", str_TYPE_TRANSACTION = "%%", search_value = "";
    short boolDECONDITIONNE = 0;
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

    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value query " + search_value);
    }

    if (request.getParameter("str_TYPE_TRANSACTION") != null) {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && !request.getParameter("lg_FAMILLE_ID").equalsIgnoreCase("")) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    familleManagement OfamilleManagement = new familleManagement(OdataManager, OTUser);
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);

    if (str_TYPE_TRANSACTION.equalsIgnoreCase("%%") || str_TYPE_TRANSACTION.equalsIgnoreCase("ALL")) {
        lstTFamille = OfamilleManagement.getListArticleByJdbc(search_value, lg_FAMILLE_ID, start, limit);
        total = OfamilleManagement.getListArticleByJdbc(search_value, lg_FAMILLE_ID).size();
    } else if (str_TYPE_TRANSACTION.equalsIgnoreCase("init")) {
        lstTFamille = OfamilleManagement.getListArticleInit(search_value, lg_FAMILLE_ID, start, limit);
        total = OfamilleManagement.getListArticleInit(search_value, lg_FAMILLE_ID).size();
    } else {
        lstTFamille = OfamilleManagement.getListArticleDecondition(search_value, "%%", boolDECONDITIONNE, start, limit);
        total = OfamilleManagement.getListArticleDecondition(search_value, "%%", boolDECONDITIONNE).size();
    }
    //  lstTFamille = OfamilleManagement.getListArticleByJdbc(search_value, lg_FAMILLE_ID);
    // OdataManager.CloseTransaction();
    new logger().OCategory.info("lstTFamille dans le ws --" + lstTFamille.size());

%>


<%
    for (int i = 0; i < (lstTFamille.size() < limit ? lstTFamille.size() : limit); i++) {

        OTFamilleStock = OtellerManagement.getTProductItemStock(lstTFamille.get(i).getLgFAMILLEID(), OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

        json = new JSONObject();
        json.put("lg_FAMILLE_ID", lstTFamille.get(i).getLgFAMILLEID());
        json.put("str_NAME", lstTFamille.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTFamille.get(i).getStrDESCRIPTION());
        // json.put("str_DESCRIPTION_int_NUMBER_AVAILABLE_int_NUMBER", lstTFamille.get(i).getStrDESCRIPTION()+"@@"+ lstTFamille.get(i).getTFamilleStockCollection().iterator().next().getIntNUMBERAVAILABLE()); a decommenter en cas de probleme
        json.put("int_PRICE", lstTFamille.get(i).getIntPRICE());
        json.put("int_PAF", lstTFamille.get(i).getIntPAF());
        json.put("bl_PROMOTED", lstTFamille.get(i).getBlPROMOTED());
        json.put("CIP", lstTFamille.get(i).getIntCIP());
        //   json.put("str_DESCRIPTION_PLUS",lstTFamille.get(i).getIntCIP() +"   "+ lstTFamille.get(i).getStrDESCRIPTION()+"   " +lstTFamille.get(i).getIntPRICE());
        json.put("str_DESCRIPTION_PLUS", lstTFamille.get(i).getIntCIP() + " " + lstTFamille.get(i).getStrDESCRIPTION() + " (" + lstTFamille.get(i).getIntPRICE() + ")");
        json.put("int_NUMBER_AVAILABLE", OTFamilleStock.getIntNUMBERAVAILABLE());
        json.put("int_NUMBER", OTFamilleStock.getIntNUMBER());
        json.put("lg_ZONE_GEO_ID", lstTFamille.get(i).getStrCODETABLEAU()+" vvv");
      //  json.put("bl_PROMOTED", lstTFamille.get(i).getBlPROMOTED());

        arrayObj.put(json);

    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>