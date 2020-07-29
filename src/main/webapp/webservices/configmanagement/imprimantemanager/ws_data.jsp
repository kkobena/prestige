<%@page import="dal.TImprimante"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="toolkits.utils.jdom"%>
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


<% 
    
    dataManager OdataManager = new dataManager();
    date key = new date();
    List<TImprimante> lstTImprimante = new ArrayList<TImprimante>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data imprimante");
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


<%    String lg_IMPRIMANTE_ID = "%%", search_value = "";
   
    if (request.getParameter("lg_IMPRIMANTE_ID") != null) {
        lg_IMPRIMANTE_ID = request.getParameter("lg_IMPRIMANTE_ID");
        new logger().OCategory.info("lg_IMPRIMANTE_ID " + lg_IMPRIMANTE_ID);
    }
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    PrinterManager OPrinterManager = new PrinterManager(OdataManager, OTUser);
    lstTImprimante = OPrinterManager.getListeImprimante(search_value, lg_IMPRIMANTE_ID);


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


<%        JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {


        JSONObject json = new JSONObject();
        json.put("lg_IMPRIMANTE_ID", lstTImprimante.get(i).getLgIMPRIMANTEID());
        json.put("str_NAME", lstTImprimante.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTImprimante.get(i).getStrDESCRIPTION());
        json.put("str_STATUT", lstTImprimante.get(i).getStrSTATUT());
        
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTImprimante.size() + " \",\"results\":" + arrayObj.toString() + "})";


%>

<%= result%> 
