<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.configManagement.grossisteManagement"%>
<%@page import="dal.TGrossiste"%>
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
    List<TGrossiste> lstTGrossiste = new ArrayList<TGrossiste>();

%>


<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data grossiste inventaire");
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

<%    String search_value = "", lg_INVENTAIRE_ID = "";

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
    grossisteManagement OgrossisteManagement = new grossisteManagement(OdataManager);
    lstTGrossiste = OgrossisteManagement.getListGrossisteFromInventaire(search_value, lg_INVENTAIRE_ID);


%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTGrossiste.size()) {
            DATA_PER_PAGE = lstTGrossiste.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTGrossiste.size() - (DATA_PER_PAGE * (pgInt)));
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

        json.put("lg_GROSSISTE_ID", lstTGrossiste.get(i).getLgGROSSISTEID());
        json.put("str_LIBELLE", lstTGrossiste.get(i).getStrLIBELLE());

        arrayObj.put(json);
    }
    JSONObject json = new JSONObject();

    json.put("lg_GROSSISTE_ID", "");
    json.put("str_LIBELLE", "Tous");

    arrayObj.put(json);
    String result = "({\"total\":\"" + lstTGrossiste.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>