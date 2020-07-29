<%@page import="bll.entity.EntityData"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="dal.TResumeCaisse"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.dataManager" %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TCaisse"  %>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();
    EntityData OEntityData = null;
%>

<%
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("ws valorisation du stock");
    String search_value = "", liste_article = "", str_BEGIN = "", str_END = "";
    String lg_ZONE_GEO_ID = "%%", lg_FAMILLEARTICLE_ID = "%%", int_CIP = "%%", lg_FAMILLE_ID = "%%",  lg_GROSSISTE_ID = "%%",  str_TYPE_TRANSACTION = "";
     String lg_TYPE_STOCK_ID = "1";
    if (request.getParameter("str_END") != null) {
        str_END = request.getParameter("str_END");
    }
    if (request.getParameter("str_BEGIN") != null) {
        str_BEGIN = request.getParameter("str_BEGIN");
    }
    
     if (request.getParameter("lg_ZONE_GEO_ID") != null && !request.getParameter("lg_ZONE_GEO_ID").equalsIgnoreCase("") && !request.getParameter("lg_ZONE_GEO_ID").equalsIgnoreCase("Sectionner un emplacement...") && !"0".equals(request.getParameter("lg_ZONE_GEO_ID"))) {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
        new logger().OCategory.info("lg_ZONE_GEO_ID " + lg_ZONE_GEO_ID);
    }
    if (request.getParameter("lg_FAMILLEARTICLE_ID") != null && !request.getParameter("lg_FAMILLEARTICLE_ID").equalsIgnoreCase("") && !request.getParameter("lg_FAMILLEARTICLE_ID").equalsIgnoreCase("Sectionner une famille article...") && !"0".equals(request.getParameter("lg_FAMILLEARTICLE_ID"))) {
        lg_FAMILLEARTICLE_ID = request.getParameter("lg_FAMILLEARTICLE_ID");
        new logger().OCategory.info("lg_FAMILLEARTICLE_ID " + lg_FAMILLEARTICLE_ID);
    }
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }  if (request.getParameter("lg_GROSSISTE_ID") != null && !request.getParameter("lg_GROSSISTE_ID").equalsIgnoreCase("") && !request.getParameter("lg_GROSSISTE_ID").equalsIgnoreCase("Sectionner un grossiste...") && !"0".equals(request.getParameter("lg_GROSSISTE_ID"))) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }
    if (request.getParameter("str_TYPE_TRANSACTION") != null) {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
    }

    OdataManager.initEntityManager();
    familleManagement OfamilleManagement = new familleManagement(OdataManager, OTUser);
    OEntityData = OfamilleManagement.valeurStockAchatVente(lg_FAMILLE_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, lg_GROSSISTE_ID, str_BEGIN, str_END, str_TYPE_TRANSACTION,lg_TYPE_STOCK_ID);

    JSONArray arrayObj = new JSONArray();
    JSONObject json = new JSONObject();
    try {
        if (OEntityData != null) {
            json.put("str_STATUT", conversion.AmountFormat(Integer.parseInt(OEntityData.getStr_value1()), '.'));
            json.put("int_AMOUNT_FOND_CAISSE", conversion.AmountFormat(Integer.parseInt(OEntityData.getStr_value2()), '.'));
            json.put("str_NAME_USER", OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME());
            json.put("ld_CREATED_BY", OTUser.getLgUSERID());
            json.put("dt_CREATED", date.DateToString(new Date(), date.formatterOrange));
            arrayObj.put(json);
        }

    } catch (Exception e) {
    }

   

%>

<%= arrayObj.toString()%>