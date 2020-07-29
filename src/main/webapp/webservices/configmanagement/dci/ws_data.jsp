<%@page import="bll.configManagement.dciManagement"%>
<%@page import="dal.TDci"%>
<%@page import="dal.dataManager"  %>
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
    List<TDci> lstTDci = new ArrayList<TDci>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data dci ");
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
     String lg_DCI_ID = "%%", search_value = "";    
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    } 
    
    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value " + search_value);
    } 
    
    if (request.getParameter("lg_DCI_ID") != null && request.getParameter("lg_DCI_ID") != "") {
        lg_DCI_ID = request.getParameter("lg_DCI_ID");
        new logger().OCategory.info("lg_DCI_ID " + lg_DCI_ID);
    } 

    
   
    OdataManager.initEntityManager();
    dciManagement OdciManagement = new dciManagement(OdataManager);
    lstTDci = OdciManagement.showAllInitial(search_value);
    
%>   

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTDci.size()) {
            DATA_PER_PAGE = lstTDci.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTDci.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTDci.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_DCI_ID", lstTDci.get(i).getLgDCIID());
        json.put("str_NAME", lstTDci.get(i).getStrNAME());        
        json.put("str_CODE", lstTDci.get(i).getStrCODE());        
        json.put("str_STATUT", lstTDci.get(i).getStrSTATUT());
        json.put("dt_CREATED", key.DateToString(lstTDci.get(i).getDtCREATED(), key.formatterShort));
        json.put("dt_UPDATED", key.DateToString(lstTDci.get(i).getDtUPDATED(), key.formatterShort));

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTDci.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>