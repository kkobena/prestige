<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TFamille"  %>
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
    date key = new date();
    List<EntityData> lstTImprimante = new ArrayList<EntityData>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data user imprimante");
%>


<!-- logic de gestion des page -->
<%
    String action = request.getParameter("action"); //get parameter ?action=
    int pageAsInt = 0;

    try {
        if ((action != null) && action.equals("filltable")) {
        } else {

            String p = request.getParameter("start"); // get paramerer ?page=
            new logger().OCategory.info("p " + p);
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

<%    String lg_USER_ID = "%%", search_value = "", lg_IMPRIMANTE_ID = "%%";

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    if (request.getParameter("lg_USER_ID") != null && request.getParameter("lg_USER_ID") != "") {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("lg_IMPRIMANTE_ID") != null && request.getParameter("lg_IMPRIMANTE_ID") != "") {
        lg_IMPRIMANTE_ID = request.getParameter("lg_IMPRIMANTE_ID");
        new logger().OCategory.info("lg_IMPRIMANTE_ID " + lg_IMPRIMANTE_ID);
    }

    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("Utilisateur connecté " + OTUser.getStrFIRSTNAME());
    PrinterManager OPrinterManager = new PrinterManager(OdataManager);

    lstTImprimante = OPrinterManager.showAllOrOneUserByImprimante(search_value, lg_USER_ID, lg_IMPRIMANTE_ID);

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTImprimante.size()) {
            DATA_PER_PAGE = lstTImprimante.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTImprimante.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {

        JSONObject json = new JSONObject();
        json.put("lg_IMPRIMANTE_ID", lstTImprimante.get(i).getStr_value1());
        json.put("str_DESCRIPTION", lstTImprimante.get(i).getStr_value2());
        json.put("is_select", lstTImprimante.get(i).getStr_value3());
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTImprimante.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>