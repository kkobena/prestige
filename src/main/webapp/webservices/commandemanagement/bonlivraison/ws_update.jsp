

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

    String lgGROSSISTEIDEDIT = request.getParameter("lgGROSSISTEIDEDIT");
    String lgBONLIVRAISONID = request.getParameter("lgBONLIVRAISONID");
    String dt_DATELIVRAISON = request.getParameter("dt_DATE_LIVRAISON");
    String str_REF = request.getParameter("str_REF");
    int intMHT =  Integer.valueOf(request.getParameter("int_MHT")), intTVA = Integer.valueOf(request.getParameter("int_TVA"));

    bonLivraisonManagement bl = new bonLivraisonManagement(OdataManager);

    JSONObject data = bl.updateBL(lgBONLIVRAISONID, dt_DATELIVRAISON, intMHT, intTVA, lgGROSSISTEIDEDIT, str_REF);


%>

<%= data%>