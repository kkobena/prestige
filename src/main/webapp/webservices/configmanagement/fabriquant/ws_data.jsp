<%@page import="bll.configManagement.FabricantManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TFabriquant"  %>
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
    List<TFabriquant> lstTFabriquant = new ArrayList<TFabriquant>();
    
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data remise ");
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
     String lg_FABRIQUANT_ID = "%%", search_value = "";
   
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    } 
    
    if (request.getParameter("lg_FABRIQUANT_ID") != null && !request.getParameter("lg_FABRIQUANT_ID").equalsIgnoreCase("")) {
        lg_FABRIQUANT_ID = request.getParameter("lg_FABRIQUANT_ID");
        new logger().OCategory.info("lg_FABRIQUANT_ID " + lg_FABRIQUANT_ID);
    } 

    OdataManager.initEntityManager();
    FabricantManagement OFabricantManagement = new FabricantManagement(OdataManager);
    lstTFabriquant = OFabricantManagement.getListeTFabriquant(search_value, lg_FABRIQUANT_ID);
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTFabriquant.size()) {
            DATA_PER_PAGE = lstTFabriquant.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTFabriquant.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTFabriquant.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject(); //str_CODE

        json.put("lg_FABRIQUANT_ID", lstTFabriquant.get(i).getLgFABRIQUANTID());        
        json.put("str_CODE", lstTFabriquant.get(i).getStrCODE()); 
        json.put("str_NAME", lstTFabriquant.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTFabriquant.get(i).getStrDESCRIPTION());        
        json.put("str_ADRESSE", lstTFabriquant.get(i).getStrADRESSE());
        json.put("str_TELEPHONE", lstTFabriquant.get(i).getStrTELEPHONE());
       
        json.put("str_STATUT", lstTFabriquant.get(i).getStrSTATUT());
        if (lstTFabriquant.get(i).getDtCREATED() != null) {
            json.put("dt_CREATED", date.DateToString(lstTFabriquant.get(i).getDtCREATED(), date.formatterShort));
        }
        if (lstTFabriquant.get(i).getDtUPDATED() != null) {
            json.put("dt_UPDATED", date.DateToString(lstTFabriquant.get(i).getDtUPDATED(), date.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTFabriquant.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>