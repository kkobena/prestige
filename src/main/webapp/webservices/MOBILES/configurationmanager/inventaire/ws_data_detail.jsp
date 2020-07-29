<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.stockManagement.InventaireManager"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.TInventaireFamille"%>
<%@page import="dal.TInventaire"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
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
    List<TInventaireFamille> lstTInventaireFamille = new ArrayList<TInventaireFamille>();
    TFamilleStock OTFamilleStock = null;
    JSONObject json = null;
    TInventaire OTInventaire = null;
    TUser OTUser = null;
%>

<%    String lg_INVENTAIRE_ID = "", lg_EMPLACEMENT_ID = "%%", lg_USER_ID ="%%", search_value = "", str_STATUT = commonparameter.statut_enable;
new logger().OCategory.info("ws product of inventory");

int start = 0, limit = 40, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }
    
    if (request.getParameter("lg_EMPLACEMENT_ID") != null && !request.getParameter("lg_EMPLACEMENT_ID").equalsIgnoreCase("")) {
        lg_EMPLACEMENT_ID = request.getParameter("lg_EMPLACEMENT_ID");
        new logger().OCategory.info("lg_EMPLACEMENT_ID " + lg_EMPLACEMENT_ID);
    }
    
    if (request.getParameter("lg_USER_ID") != null && !request.getParameter("lg_USER_ID").equalsIgnoreCase("")) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value query " + search_value);
    }
    
    if (request.getParameter("lg_INVENTAIRE_ID") != null) {
        lg_INVENTAIRE_ID = request.getParameter("lg_INVENTAIRE_ID");
        new logger().OCategory.info("lg_INVENTAIRE_ID " + lg_INVENTAIRE_ID);
    }
    
    OdataManager.initEntityManager();
    OTUser = OdataManager.getEm().find(TUser.class, lg_USER_ID);
    InventaireManager OInventaireManager = new InventaireManager(OdataManager);
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
    if(lg_INVENTAIRE_ID.equals("")) {
        OTInventaire = OInventaireManager.getLastInventaire(str_STATUT);
        if(OTInventaire != null) {
            lg_INVENTAIRE_ID = OTInventaire.getLgINVENTAIREID();
        }
    }
    
    lstTInventaireFamille = OInventaireManager.listTFamilleByInventaire(search_value, lg_INVENTAIRE_ID, "%%", lg_EMPLACEMENT_ID, "%%", true, "%%");
%>


<%    JSONArray arrayObj = new JSONArray();

for(TInventaireFamille OTInventaireFamille : lstTInventaireFamille) {
     json = new JSONObject();
        OTFamilleStock = OtellerManagement.getTProductItemStock(OTInventaireFamille.getLgFAMILLEID().getLgFAMILLEID());
        json.put("lg_PRODUCT_ID", OTFamilleStock.getLgFAMILLEID().getLgFAMILLEID());
        json.put("str_DESCRIPTION", OTFamilleStock.getLgFAMILLEID().getStrDESCRIPTION());
        json.put("int_CIP", OTFamilleStock.getLgFAMILLEID().getIntCIP());
        json.put("lg_EMPLACEMENT_ID", OTInventaireFamille.getLgINVENTAIREFAMILLEID());
        json.put("str_EMPLACEMENT", OTInventaireFamille.getLgFAMILLEID().getLgZONEGEOID().getStrLIBELLEE());
        json.put("int_PRICE_VENTE", OTFamilleStock.getLgFAMILLEID().getIntPRICE());
        json.put("int_PRICE_ACHAT", OTFamilleStock.getLgFAMILLEID().getIntPAF());
        json.put("int_PRICE", OTFamilleStock.getLgFAMILLEID().getIntPAF() * OTInventaireFamille.getIntNUMBER());
        json.put("int_QUANTITY", OTInventaireFamille.getIntNUMBER());
        json.put("int_STOCK", OTInventaireFamille.getIntNUMBERINIT());
        json.put("int_ECART",  OTInventaireFamille.getIntNUMBER() - OTInventaireFamille.getIntNUMBERINIT());
        json.put("str_PIC", "default_product");
        arrayObj.put(json);
}

    String result = arrayObj.toString();

%>
<%= result%>