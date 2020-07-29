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
%>

<%    String lg_FAMILLE_ID = "%%", lg_ZONE_GEO_ID = "%%", search_value = "";
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

    if (request.getParameter("lg_ZONE_GEO_ID") != null && !request.getParameter("lg_ZONE_GEO_ID").equalsIgnoreCase("")) {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
        new logger().OCategory.info("lg_ZONE_GEO_ID " + lg_ZONE_GEO_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && !request.getParameter("lg_FAMILLE_ID").equalsIgnoreCase("")) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    familleManagement OfamilleManagement = new familleManagement(OdataManager, OTUser);
    
   lstTFamille = OfamilleManagement.getListArticleByJdbc(search_value, lg_FAMILLE_ID, "%%", start, limit);
   total = OfamilleManagement.getTotalListArticleByJdbc(search_value, lg_FAMILLE_ID, "%%");
%>


<%    JSONArray arrayObj = new JSONArray();
JSONObject json = null;
    for(TFamille OTFamille: lstTFamille) {
             json = new JSONObject();
        json.put("lg_FAMILLE_ID", OTFamille.getLgFAMILLEID());
        json.put("str_DESCRIPTION", OTFamille.getStrDESCRIPTION());
       json.put("int_PRICE", OTFamille.getIntPRICE());
        json.put("int_PAF", OTFamille.getIntPAF());
        json.put("CIP", OTFamille.getIntCIP());
        json.put("int_NUMBER_AVAILABLE",OTFamille.getIntPAT());
        json.put("bl_PROMOTED", (OTFamille.getStrNAME() != null && OTFamille.getStrNAME().equals(lg_ZONE_GEO_ID)) ? true : false);
        
        arrayObj.put(json);

    }
    
    String result = "({\"total\":\"" + lstTFamille.size() + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>