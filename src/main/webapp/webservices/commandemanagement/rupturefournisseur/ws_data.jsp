<%@page import="dal.TRuptureHistory"%>
<%@page import="bll.commandeManagement.retourFournisseurManagement"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRetourFournisseur"  %>
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
    List<TRuptureHistory> lstTRuptureHistory = new ArrayList<TRuptureHistory>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination;
    new logger().OCategory.info("dans webservices.commandemanagement.TRuptureFournisseur.ws_data");
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

<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    String search_value = "";
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    
    retourFournisseurManagement OretourFournisseurManagement = new retourFournisseurManagement(OdataManager, OTUser);
    lstTRuptureHistory = OretourFournisseurManagement.getAllTRuptureHistory(search_value);
    
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTRuptureHistory.size()) {
            DATA_PER_PAGE = lstTRuptureHistory.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTRuptureHistory.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTRuptureHistory.get(i));

        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_FAMILLEARTICLE_ID", lstTRuptureHistory.get(i).getLgRUPTUREHISTORYID());
        json.put("int_CIP", lstTRuptureHistory.get(i).getLgFAMILLEID().getIntCIP());
        json.put("str_DESCRIPTION", lstTRuptureHistory.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("int_NUMBER_AVAILABLE", lstTRuptureHistory.get(i).getIntNUMBER());


        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTRuptureHistory.size() + " \" ,\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>