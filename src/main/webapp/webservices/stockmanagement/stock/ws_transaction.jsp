

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

    String lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
    String CODEEMPLACEMENT = request.getParameter("CODEEMPLACEMENT");
    
    StockManager OStockManager = new StockManager(OdataManager);
    JSONObject data = OStockManager.updateProductZone(CODEEMPLACEMENT,lg_FAMILLE_ID);


%>

<%= data%>