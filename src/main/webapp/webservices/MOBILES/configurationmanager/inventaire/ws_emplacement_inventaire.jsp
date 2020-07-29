<%@page import="bll.stockManagement.InventaireManager"%>
<%@page import="dal.TInventaire"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TZoneGeographique"  %>
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


<%
    dataManager OdataManager = new dataManager();
    List<TZoneGeographique> lstTZoneGeographique = new ArrayList<TZoneGeographique>();
    TInventaire OTInventaire = null;
    JSONObject json = null;
%>


<%
    String search_value = "", lg_INVENTAIRE_ID = "", str_STATUT = "";

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT").toString();
        new logger().OCategory.info("str_STATUT  = " + str_STATUT);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value  = " + search_value);
    }

    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query").toString();
        new logger().OCategory.info("search_value query " + search_value);
    }

    if (request.getParameter("lg_INVENTAIRE_ID") != null) {
        lg_INVENTAIRE_ID = request.getParameter("lg_INVENTAIRE_ID").toString();
        new logger().OCategory.info("lg_INVENTAIRE_ID  = " + lg_INVENTAIRE_ID);
    }

    OdataManager.initEntityManager();
    InventaireManager OInventaireManager = new InventaireManager(OdataManager);
    familleManagement OfamilleManagement = new familleManagement(OdataManager);
    if (lg_INVENTAIRE_ID.equals("")) {
        OTInventaire = OInventaireManager.getLastInventaire(str_STATUT);
        if (OTInventaire != null) {
            lg_INVENTAIRE_ID = OTInventaire.getLgINVENTAIREID();
        }
    }

    lstTZoneGeographique = OfamilleManagement.getListZoneEmplacementFromInventaire(search_value, lg_INVENTAIRE_ID);
%>

<%    JSONArray arrayObj = new JSONArray();
    for (TZoneGeographique OTZoneGeographique : lstTZoneGeographique) {
        json = new JSONObject();

        json.put("lg_EMPLACEMENT_ID", OTZoneGeographique.getLgZONEGEOID());
        json.put("str_DESCRIPTION", OTZoneGeographique.getStrLIBELLEE());
        json.put("str_NAME", OTZoneGeographique.getStrCODE());
        arrayObj.put(json);
    }

    String result = arrayObj.toString();

%>
<%= result%>