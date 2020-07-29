

<%@page import="dal.TUser"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="org.json.JSONArray"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="bll.commandeManagement.bonLivraisonManagement"%>

<%@page import="org.json.JSONException"%>

<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>

<%@page import="java.util.*"  %>

<%@page import="toolkits.utils.date"  %>

<%@page import="org.json.JSONObject"  %>          

<%@page import="toolkits.utils.jdom"  %>



<%
    dataManager OdataManager = new dataManager();

    OdataManager.initEntityManager();
    String search_value = "%%", zoneIDO = "%%";
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    StockManager OStockManager = new StockManager(OdataManager, OTUser);
    if (request.getParameter("zoneIDO") != null && !"".equals(request.getParameter("zoneIDO"))) {
        zoneIDO = request.getParameter("zoneIDO");
    }
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    String MODE_SELECTION = request.getParameter("MODE_SELECTION");
    String zoneID = request.getParameter("zoneID");
    JSONArray uncheckedList = new JSONArray(request.getParameter("uncheckedList"));
    JSONArray recordsToSend = new JSONArray(request.getParameter("recordsToSend"));
    
    JSONObject data = OStockManager.updateSelectionZone(zoneID, MODE_SELECTION, recordsToSend, uncheckedList, zoneIDO, search_value+"%");


%>

<%= data%>