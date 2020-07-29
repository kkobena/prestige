

<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="org.json.JSONException"%>
<%@page import="bll.report.UgManager"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>

<%@page import="java.util.*"  %>

<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>

<%@page import="org.json.JSONObject"  %>          
<%@page import="org.json.JSONArray"  %> 

<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>



<%
    dataManager OdataManager = new dataManager();

    date key = new date();
%>

<%
    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;

%>


<!-- logic de gestion des page -->
<%    String action = request.getParameter("action"); //get parameter ?action=
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

<%    OdataManager.initEntityManager();
    List<TTiersPayant> listTp = new ArrayList<TTiersPayant>();
    String search_value = "%%", lg_COMPTE_CLIENT_ID = "";
    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null && !"".equals(request.getParameter("lg_COMPTE_CLIENT_ID"))) {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
    }

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }

    clientManagement m = new clientManagement(OdataManager);

    listTp = m.getClientTiersPayants(lg_COMPTE_CLIENT_ID);


%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > listTp.size()) {
            DATA_PER_PAGE = listTp.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (listTp.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    JSONObject data = new JSONObject();
    for (int i = pgInt; i < pgInt_Last; i++) {

        JSONObject json = new JSONObject();
        json.put("lg_TIERS_PAYANT_ID", listTp.get(i).getLgTIERSPAYANTID());
        json.put("str_TIERS_PAYANT_NAME", listTp.get(i).getStrFULLNAME());
        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", listTp.size());
%>

<%= data%>