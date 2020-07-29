<%@page import="bll.configManagement.familleManagement"%>
<%@page import="dal.TCodeTva"%>
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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    
    json Ojson = new json();
    List<TCodeTva> lstTCodeTva = new ArrayList<TCodeTva>();
    TUser OTUser;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws datacode tva ");
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
    String lg_CODE_TVA_ID = "%%", search_value = "";
    
    if (request.getParameter("lg_CODE_TVA_ID") != null && request.getParameter("lg_CODE_TVA_ID") != "") {
        lg_CODE_TVA_ID = request.getParameter("lg_CODE_TVA_ID");
        new logger().OCategory.info("lg_CODE_TVA_ID " + lg_CODE_TVA_ID);
    }
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }


    OdataManager.initEntityManager();
    familleManagement OfamilleManagement = new familleManagement(OdataManager);
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    lstTCodeTva = OfamilleManagement.listeTCodeTva(search_value, lg_CODE_TVA_ID);
 

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTCodeTva.size()) {
            DATA_PER_PAGE = lstTCodeTva.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTCodeTva.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTCodeTva.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_CODE_TVA_ID", lstTCodeTva.get(i).getLgCODETVAID());
        json.put("str_NAME", lstTCodeTva.get(i).getStrNAME());
        json.put("int_VALUE", lstTCodeTva.get(i).getIntVALUE());  
        json.put("str_STATUT", lstTCodeTva.get(i).getStrSTATUT());
        json.put("dt_CREATED", date.DateToString(lstTCodeTva.get(i).getDtCREATED(), date.formatterShort));
        arrayObj.put(json);

    }
    String result = "({\"total\":\"" + lstTCodeTva.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>