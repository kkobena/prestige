<%@page import="bll.teller.caisseManagement"%>
<%@page import="dal.TTypeReglement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
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
    List<TTypeReglement> lstTTypeReglement = new ArrayList<TTypeReglement>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data");
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
    String lg_TYPE_REGLEMENT_ID = "%%", str_FLAG = "%%", search_value = "";
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    
     if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value query " + search_value);
    }
     
     if (request.getParameter("str_FLAG") != null && !request.getParameter("str_FLAG").equalsIgnoreCase("")) {
        str_FLAG = request.getParameter("str_FLAG");
        new logger().OCategory.info("str_FLAG " + str_FLAG);
    }
     
     if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null && !request.getParameter("lg_TYPE_REGLEMENT_ID").equalsIgnoreCase("")) {
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");
        new logger().OCategory.info("lg_TYPE_REGLEMENT_ID " + lg_TYPE_REGLEMENT_ID);
    }
    
    

    OdataManager.initEntityManager();
    caisseManagement OcaisseManagement = new caisseManagement(OdataManager, OTUser);
    lstTTypeReglement = OcaisseManagement.getListeTTypeReglement(search_value, lg_TYPE_REGLEMENT_ID, str_FLAG);
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTTypeReglement.size()) {
            DATA_PER_PAGE = lstTTypeReglement.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTypeReglement.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTTypeReglement.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_TYPE_REGLEMENT_ID", lstTTypeReglement.get(i).getLgTYPEREGLEMENTID());
        json.put("str_NAME", lstTTypeReglement.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTTypeReglement.get(i).getStrDESCRIPTION());

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTTypeReglement.size() + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>