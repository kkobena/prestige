<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TMouvementSnapshot"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();
    TUser OTUser;

    List<TMouvementSnapshot> lstTMouvementSnapshot = new ArrayList<TMouvementSnapshot>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data ws_data_mouvement_perime");
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

<%    String lg_USER_ID = "%%", search_value = "", lg_FAMILLE_ID = "%%", lg_GROSSISTE_ID = "%%", lg_FABRIQUANT_ID = "%%", lg_FAMILLEARTICLE_ID = "%%", lg_ZONE_GEO_ID = "%%";
    String dt_DEBUT = "", dt_FIN = "";
    Date dtFin;
    List<Date> lstDate = new ArrayList<Date>();

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("lg_GROSSISTE_ID") != null) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
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

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dt_DEBUT = "2015-04-20";
    }
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
        String Odate = key.DateToString(dtFin, key.formatterMysqlShort2);

        dtFin = key.getDate(Odate, "23:59");
    }
    Date dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  " + dt_FIN + "  dt_DEBUT  " + dt_DEBUT + " lg_FAMILLE_ID " + lg_FAMILLE_ID + " dtFin:" + dtFin);

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connect�   " + OTUser.getStrFIRSTNAME());
    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager);

    lstTMouvementSnapshot = OWarehouseManager.listTMouvementSnapshot(search_value, lg_FAMILLE_ID, dtDEBUT, dtFin, lg_GROSSISTE_ID, lg_FABRIQUANT_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID);
%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTMouvementSnapshot.size()) {
            DATA_PER_PAGE = lstTMouvementSnapshot.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTMouvementSnapshot.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTMouvementSnapshot.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();
      
        json.put("dt_DAY", date.DateToString(lstTMouvementSnapshot.get(i).getDtCREATED(), date.formatterShort));
 
        json.put("lg_FAMILLE_ID", lstTMouvementSnapshot.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("int_CIP", lstTMouvementSnapshot.get(i).getLgFAMILLEID().getIntCIP());
        json.put("int_NUMBER", lstTMouvementSnapshot.get(i).getIntSTOCKJOUR());
        json.put("str_NAME", lstTMouvementSnapshot.get(i).getLgFAMILLEID().getStrNAME());
        json.put("dt_UPDATED", date.DateToString(lstTMouvementSnapshot.get(i).getDtCREATED(), date.formatterShort));
    

        arrayObj.put(json);

    }

    String result = "({\"total\":\"" + lstTMouvementSnapshot.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>