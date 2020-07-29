<%@page import="bll.entity.EntityData"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="bll.stockManagement.InventaireManager"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="bll.commandeManagement.retourFournisseurManagement"%>
<%@page import="dal.TFamille"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="dal.TMouvement"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.TMouvement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TTypeRisque"  %>
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
    List<EntityData> lstEntityData = new ArrayList<EntityData>();
    TUser OTUser;

%>


<%    Date today = new Date();
    String lg_FAMILLE_ID = "%%", search_value = "";
    String str_Date_Debut = date.DateToString(today, date.formatterMysqlShort), str_Date_Fin = date.DateToString(today, date.formatterMysqlShort);

    int start = 0, limit = jdom.int_size_pagination, total = 0;
    new logger().OCategory.info("dans ws data recap suivi mouvement article ");

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

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, OTUser);

    // lstEntityData = OSnapshotManager.getRecapMouvementSuiviByArticle(search_value, str_Date_Debut, str_Date_Fin, lg_FAMILLE_ID); // a decommenter en cas de probleme. 08/06/2016
    lstEntityData = OSnapshotManager.getRecapMouvementSuiviByArticle(search_value, str_Date_Debut, str_Date_Fin, lg_FAMILLE_ID, start, limit);
    total = OSnapshotManager.getRecapMouvementSuiviByArticle(search_value, str_Date_Debut, str_Date_Fin, lg_FAMILLE_ID).size();

%>


<%    JSONArray arrayObj = new JSONArray();
    for (EntityData OEntityData : lstEntityData) {
        JSONObject json = new JSONObject();
        json.put("dt_UPDATED", date.DateToString(date.stringToDate(OEntityData.getStr_value1(), date.formatterMysqlShort), date.formatterShort));
        json.put("int_STOCK_REAPROVISONEMENT", OEntityData.getStr_value2());
        json.put("int_NUMBER", OEntityData.getStr_value3());
        json.put("lg_FAMILLE_ID", OEntityData.getStr_value4());
        json.put("str_NAME", OEntityData.getStr_value5());
        json.put("int_CIP", OEntityData.getStr_value6());
        json.put("int_PRICE", OEntityData.getStr_value7());
        json.put("int_PAF", OEntityData.getStr_value8());
        json.put("int_NUMBER_VENTE", OEntityData.getStr_value9());

        json.put("int_NUMBER_DECONDITIONNEMENT_OUT", OEntityData.getStr_value10());
        json.put("int_NUMBER_DECONDITIONNEMENT_IN", OEntityData.getStr_value11());
        json.put("int_NUMBER_RETOUR", OEntityData.getStr_value12());
        json.put("int_NUMBER_PERIME", OEntityData.getStr_value13());
        json.put("int_NUMBER_AJUSTEMENT_OUT", OEntityData.getStr_value14());
        json.put("int_NUMBER_AJUSTEMENT_IN", OEntityData.getStr_value15());
        json.put("int_NUMBER_BON", OEntityData.getStr_value16());
        json.put("int_NUMBER_INVENTAIRE", OEntityData.getStr_value17());
        json.put("int_NUMBER_ANNULEVENTE", OEntityData.getStr_value18());

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>