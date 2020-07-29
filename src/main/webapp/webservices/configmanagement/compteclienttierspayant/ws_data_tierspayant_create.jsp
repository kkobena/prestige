<%@page import="dal.TTiersPayant"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TCompteClient"  %>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    Date dt_CREATED, dt_UPDATED;

    date key = new date();
    List<TTiersPayant> lstTiersPayant = new ArrayList<TTiersPayant>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data client tiers payant");
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
<!-- fin logic de gestion des pages -->

<%    String lg_TYPE_TIERS_PAYANT_ID = "%%", lg_TYPE_CLIENT_ID = "%%", search_value = "", lg_TIERS_PAYANT_ID = "%%";
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value" + search_value);
    }
    
    if (request.getParameter("query") != null && !request.getParameter("query").equalsIgnoreCase("")) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value" + search_value);
    }

    if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TYPE_TIERS_PAYANT_ID  ==== " + lg_TYPE_TIERS_PAYANT_ID);
    }

    if (request.getParameter("lg_TYPE_CLIENT_ID") != null) {
        lg_TYPE_CLIENT_ID = request.getParameter("lg_TYPE_CLIENT_ID").toString();
        new logger().OCategory.info("lg_TYPE_CLIENT_ID  ==== " + lg_TYPE_CLIENT_ID);
    }
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TIERS_PAYANT_ID  ==== " + lg_TIERS_PAYANT_ID);
    }

    OdataManager.initEntityManager();
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);

   // lstTiersPayant = OtierspayantManagement.getTiersPayantByTypeBis(lg_TYPE_CLIENT_ID);
    lstTiersPayant = OtierspayantManagement.ShowAllOrOneTierspayant(search_value, lg_TIERS_PAYANT_ID, lg_TYPE_TIERS_PAYANT_ID);
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTiersPayant.size()) {
            DATA_PER_PAGE = lstTiersPayant.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTiersPayant.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTiersPayant.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_TIERS_PAYANT_ID", lstTiersPayant.get(i).getLgTIERSPAYANTID());
        json.put("str_CODE_ORGANISME", lstTiersPayant.get(i).getStrCODEORGANISME());
        json.put("str_NAME", lstTiersPayant.get(i).getStrNAME());
        json.put("str_FULLNAME", lstTiersPayant.get(i).getStrFULLNAME());
        json.put("dt_CREATED", date.DateToString(lstTiersPayant.get(i).getDtCREATED(), date.formatterMysql));

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTiersPayant.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>