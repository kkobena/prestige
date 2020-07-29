<%@page import="bll.configManagement.familleGrossisteManagement"%>
<%@page import="dal.TFamilleGrossiste"%>
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


<%
    dataManager OdataManager = new dataManager();
    List<TFamilleGrossiste> lstTFamilleGrossiste = new ArrayList<TFamilleGrossiste>();

%>


<%    int DATA_PER_PAGE = jdom.int_size_pagination;
    new logger().OCategory.info("dans ws data TFamilleGrossiste ");
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

<%    String search_value = "", lg_GROSSISTE_ID = "%%", lg_FAMILLE_ID = "%%";
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_GROSSISTE_ID") != null) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    OdataManager.initEntityManager();
    familleGrossisteManagement OfamilleGrossisteManagement = new familleGrossisteManagement(OdataManager);
    lstTFamilleGrossiste = OfamilleGrossisteManagement.getListeFamilleGrossiste(search_value, lg_FAMILLE_ID, lg_GROSSISTE_ID);


%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTFamilleGrossiste.size()) {
            DATA_PER_PAGE = lstTFamilleGrossiste.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTFamilleGrossiste.size() - (DATA_PER_PAGE * (pgInt)));
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

        json.put("lg_FAMILLE_GROSSISTE_ID", lstTFamilleGrossiste.get(i).getLgFAMILLEGROSSISTEID());
        json.put("lg_GROSSISTE_ID", lstTFamilleGrossiste.get(i).getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("lg_GROSSISTE_LIBELLE", lstTFamilleGrossiste.get(i).getLgGROSSISTEID().getStrLIBELLE());

        json.put("lg_FAMILLE_ID", lstTFamilleGrossiste.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("lg_FAMILLE_LIBELLE", lstTFamilleGrossiste.get(i).getLgFAMILLEID().getStrNAME());

        json.put("str_CODE_ARTICLE", lstTFamilleGrossiste.get(i).getStrCODEARTICLE());
        json.put("int_PRICE", lstTFamilleGrossiste.get(i).getIntPRICE());
        json.put("int_PAF", lstTFamilleGrossiste.get(i).getIntPAF());

        json.put("str_STATUT", lstTFamilleGrossiste.get(i).getStrSTATUT());
        json.put("dt_CREATED", date.DateToString(lstTFamilleGrossiste.get(i).getDtCREATED(), date.formatterShort));
        json.put("dt_UPDATED", date.DateToString(lstTFamilleGrossiste.get(i).getDtUPDATED(), date.formatterShort));

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTFamilleGrossiste.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>