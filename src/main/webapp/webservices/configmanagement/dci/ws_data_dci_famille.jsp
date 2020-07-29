<%@page import="bll.entity.EntityData"%>
<%@page import="bll.configManagement.dciManagement"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TSnapShopDalySortieFamille"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TTypeRisque"  %>
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
    
    date key = new date();
    
    List<EntityData> lstEntityData = new ArrayList<EntityData>();
    TUser OTUser;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("++++++++++++++++++++++   dans ws Famille DCI ");
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
    String lg_FAMILLE_ID = "%%", lg_DCI_ID = "%%", search_value = "";

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    } 
    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }
    if (request.getParameter("lg_DCI_ID") != null && request.getParameter("lg_DCI_ID") != "") {
        lg_DCI_ID = request.getParameter("lg_DCI_ID");
        new logger().OCategory.info("lg_DCI_ID " + lg_DCI_ID);
    }

    OdataManager.initEntityManager();
    dciManagement OdciManagement = new dciManagement(OdataManager);
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    lstEntityData = OdciManagement.ListDciFamille(search_value, lg_FAMILLE_ID, lg_DCI_ID);


%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstEntityData.size()) {
            DATA_PER_PAGE = lstEntityData.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstEntityData.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstEntityData.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_FAMILLE_DCI_ID", lstEntityData.get(i).getStr_value1());
        json.put("lg_FAMILLE_ID", lstEntityData.get(i).getStr_value2());
        json.put("lg_DCI_ID", lstEntityData.get(i).getStr_value3());
        json.put("str_DESCRIPTION", lstEntityData.get(i).getStr_value4());
        json.put("int_PRICE", lstEntityData.get(i).getStr_value5());
        json.put("str_CODE", lstEntityData.get(i).getStr_value6());
        json.put("dci_str_NAME", lstEntityData.get(i).getStr_value7());        
        json.put("int_CIP", lstEntityData.get(i).getStr_value8());  


        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstEntityData.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>