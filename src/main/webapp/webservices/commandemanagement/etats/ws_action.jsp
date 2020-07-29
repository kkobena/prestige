

<%@page import="bll.commandeManagement.bonLivraisonManagement"%>
<%@page import="org.json.JSONObject"%>
<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    dataManager OdataManager = new dataManager();
    bonLivraisonManagement b = new bonLivraisonManagement(OdataManager, OTUser);
    JSONObject data = new JSONObject();    
    
    data.put("BTNUPDATE", b.isAllowed("P_BTN_UPDATEBL"));
    
%>

<%= data%>