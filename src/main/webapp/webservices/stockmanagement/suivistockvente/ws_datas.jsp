<%@page import="bll.entity.EntityData"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="bll.stockManagement.InventaireManager"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="bll.commandeManagement.retourFournisseurManagement"%>
<%@page import="bll.configManagement.familleManagement"%>
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

    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data suivi detaillee des ventes ");
%>
<!-- logic de gestion des page -->
<%
    String action = request.getParameter("action"); //get parameter ?action=
    int pageAsInt = 0;

    try {
        if ((action != null) && action.equals("filltable")) {
        } else {

            String p = request.getParameter("start"); // get paramerer ?page=

            if (p != null) {
                int int_page = new Integer(p).intValue();
                int_page = (int_page / DATA_PER_PAGE) + 1;
                p = new Integer(int_page).toString();

                // Strip quotation marks
                StringBuffer buffer = new StringBuffer();
                for (int index = 0; index < p.length(); index++) {
                    char c = p.charAt(index);
                    if (c != '\\') {
                        buffer.append(c);
                    }
                }
                p = buffer.toString();
                Integer intTemp = new Integer(p);

                pageAsInt = intTemp.intValue();

            } else {
                pageAsInt = 1;
            }

        }
    } catch (Exception E) {
    }


%>
<!-- fin logic de gestion des page -->






<%    Date today = new Date();
    String lg_FAMILLE_ID = "%%", lg_FAMILLEARTICLE_ID = "%%", lg_ZONE_GEO_ID = "%%", lg_FABRIQUANT_ID = "%%", search_value = "", lg_USER_ID = "%%", P_KEY = "%%", str_TYPE_ACTION = "%%", str_ACTION = "%%";
    String str_Date_Debut = date.DateToString(today, date.formatterMysqlShort), str_Date_Fin = date.DateToString(today, date.formatterMysqlShort);

    int start = 0, limit = jdom.int_size_pagination, total = 0;
    new logger().OCategory.info("dans ws data suivi mouvement article ");

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

    if (request.getParameter("str_TYPE_ACTION") != null) {
        str_TYPE_ACTION = request.getParameter("str_TYPE_ACTION");
        new logger().OCategory.info("str_TYPE_ACTION " + str_TYPE_ACTION);
    }

    if (request.getParameter("lg_FABRIQUANT_ID") != null && request.getParameter("lg_FABRIQUANT_ID") != "") {
        lg_FABRIQUANT_ID = request.getParameter("lg_FABRIQUANT_ID");
        new logger().OCategory.info("lg_FABRIQUANT_ID " + lg_FABRIQUANT_ID);
    }

    if (request.getParameter("lg_FAMILLEARTICLE_ID") != null && request.getParameter("lg_FAMILLEARTICLE_ID") != "") {
        lg_FAMILLEARTICLE_ID = request.getParameter("lg_FAMILLEARTICLE_ID");
        new logger().OCategory.info("lg_FAMILLEARTICLE_ID " + lg_FAMILLEARTICLE_ID);
    }

    if (request.getParameter("lg_ZONE_GEO_ID") != null && request.getParameter("lg_ZONE_GEO_ID") != "") {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
        new logger().OCategory.info("lg_ZONE_GEO_ID " + lg_ZONE_GEO_ID);
    }

    if (request.getParameter("str_ACTION") != null) {
        str_ACTION = request.getParameter("str_ACTION");
        new logger().OCategory.info("str_ACTION " + str_ACTION);
    }

    if (request.getParameter("lg_USER_ID") != null && request.getParameter("lg_USER_ID") != "") {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("P_KEY") != null) {
        P_KEY = request.getParameter("P_KEY");
        new logger().OCategory.info("P_KEY " + P_KEY);
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

    // lstEntityData = OSnapshotManager.getMouvementSuiviArticle(search_value, str_Date_Debut, str_Date_Fin, lg_FAMILLE_ID, lg_USER_ID, P_KEY, str_TYPE_ACTION, str_ACTION, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, lg_FABRIQUANT_ID, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID()); // a decommenter en cas de probleme. 08/06/2016
    lstEntityData = OSnapshotManager.getMouvementSuiviArticle(search_value, str_Date_Debut, str_Date_Fin, lg_FAMILLE_ID, lg_USER_ID, P_KEY, str_TYPE_ACTION, str_ACTION, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, lg_FABRIQUANT_ID, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), start, limit);
///total = OSnapshotManager.getMouvementSuiviArticle(search_value, str_Date_Debut, str_Date_Fin, lg_FAMILLE_ID, lg_USER_ID, P_KEY, str_TYPE_ACTION, str_ACTION, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, lg_FABRIQUANT_ID, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID()).size();

%> 
<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstEntityData.size()) {
            DATA_PER_PAGE = lstEntityData.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstEntityData.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>
<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
//for(EntityData OEntityData : lstEntityData) {
        JSONObject json = new JSONObject();

        json.put("lg_FAMILLE_ID", lstEntityData.get(i).getStr_value1());
        json.put("int_CIP", lstEntityData.get(i).getStr_value2());
        json.put("str_NAME", lstEntityData.get(i).getStr_value3());
        json.put("int_TAUX_MARQUE", lstEntityData.get(i).getStr_value5());

        //json.put("int_NUMBER_VENTE", lstEntityData.get(i).getStr_value6()); // a decommenter en cas de probleme
        json.put("int_NUMBER_VENTE", Integer.parseInt(lstEntityData.get(i).getStr_value6()));
        json.put("int_NUMBER_RETOUR", lstEntityData.get(i).getStr_value14());
        json.put("int_NUMBER_AJUSTEMENT_IN", lstEntityData.get(i).getStr_value12());
        json.put("int_NUMBER_AJUSTEMENT_OUT", lstEntityData.get(i).getStr_value8());
        json.put("int_NUMBER_DECONDITIONNEMENT_IN", lstEntityData.get(i).getStr_value11());
        json.put("int_NUMBER_DECONDITIONNEMENT_OUT", lstEntityData.get(i).getStr_value9());
        json.put("int_NUMBER_PERIME", lstEntityData.get(i).getStr_value7());
        json.put("int_NUMBER_BON", lstEntityData.get(i).getStr_value10());
        json.put("int_NUMBER_INVENTAIRE", lstEntityData.get(i).getStr_value13());
        json.put("int_NUMBER_ANNULEVENTE", lstEntityData.get(i).getStr_value15());
        arrayObj.put(json);

    }

    String result = "({\"total\":\"" + lstEntityData.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>