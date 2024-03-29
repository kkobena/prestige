<%@page import="dal.TParameters"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.common.Parameter"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TZoneGeographique"  %>
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
    List<TZoneGeographique> lstTZoneGeographique = new ArrayList<TZoneGeographique>();
    TUser OTUser;
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data optiminisation quantite ");
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

<%
    String lg_ZONE_GEO_ID = "%%", search_value = "";
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_ZONE_GEO_ID") != null && !request.getParameter("lg_ZONE_GEO_ID").equalsIgnoreCase("")) {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
        new logger().OCategory.info("lg_ZONE_GEO_ID " + lg_ZONE_GEO_ID);
    }

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    familleManagement OfamilleManagement = new familleManagement(OdataManager, OTUser);
    lstTZoneGeographique = OfamilleManagement.getListeTZoneGeographiques(search_value, lg_ZONE_GEO_ID);
    boolean KEYINTOACCOUNT = false;
    try {
        TParameters KEY_TAKE_INTO_ACCOUNT = OdataManager.getEm().getReference(TParameters.class, "KEY_TAKE_INTO_ACCOUNT");
        System.out.println(" KEY_TAKE_INTO_ACCOUNT  " + KEY_TAKE_INTO_ACCOUNT.getStrVALUE());
        if (KEY_TAKE_INTO_ACCOUNT != null) {
            if (Integer.valueOf(KEY_TAKE_INTO_ACCOUNT.getStrVALUE().trim()) == 1) {
                KEYINTOACCOUNT = true;
            }

        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTZoneGeographique.size()) {
            DATA_PER_PAGE = lstTZoneGeographique.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTZoneGeographique.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTZoneGeographique.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_ZONE_GEO_ID", lstTZoneGeographique.get(i).getLgZONEGEOID());
        json.put("str_LIBELLEE", lstTZoneGeographique.get(i).getStrLIBELLEE());
        json.put("str_CODE", lstTZoneGeographique.get(i).getStrCODE());
        json.put("str_STATUT", lstTZoneGeographique.get(i).getStrSTATUT());
        json.put("bool_ACCOUNT", lstTZoneGeographique.get(i).getBoolACCOUNT());
        json.put("KEYINTOACCOUNT", KEYINTOACCOUNT);

        if (lstTZoneGeographique.get(i).getDtCREATED() != null) {
            json.put("dt_CREATED", date.DateToString(lstTZoneGeographique.get(i).getDtCREATED(), date.formatterShort));
        }

        if (lstTZoneGeographique.get(i).getDtUPDATED() != null) {
            json.put("dt_UPDATED", date.DateToString(lstTZoneGeographique.get(i).getDtUPDATED(), date.formatterShort));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTZoneGeographique.size() + " \",\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>