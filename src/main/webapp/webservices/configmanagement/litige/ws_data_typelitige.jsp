<%@page import="bll.configManagement.LitigeManager"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TTypelitige"  %>
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
    String lg_TYPELITIGE_ID = "%%", search_value = "";
    date key = new date();
    json Ojson = new json();
    List<TTypelitige> lstTTypelitige = new ArrayList<TTypelitige>();

    Integer int_search_cip;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data type litige");
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
    if (request.getParameter("search_value") != null && request.getParameter("search_value") != "") {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    
    if (request.getParameter("lg_TYPELITIGE_ID") != null && request.getParameter("lg_TYPELITIGE_ID") != "") {
        lg_TYPELITIGE_ID = request.getParameter("lg_TYPELITIGE_ID");
        new logger().OCategory.info("lg_TYPELITIGE_ID " + lg_TYPELITIGE_ID);
    }
    
    OdataManager.initEntityManager();
    LitigeManager OLitigeManager = new LitigeManager(OdataManager);
    lstTTypelitige = OLitigeManager.listTTypelitige(search_value, lg_TYPELITIGE_ID);

%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTTypelitige.size()) {
            DATA_PER_PAGE = lstTTypelitige.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTypelitige.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTTypelitige.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_TYPELITIGE_ID", lstTTypelitige.get(i).getLgTYPELITIGEID());
        json.put("str_NAME", lstTTypelitige.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTTypelitige.get(i).getStrDESCRIPTION());
        json.put("str_STATUT", lstTTypelitige.get(i).getStrSTATUT());
        json.put("dt_CREATED", key.DateToString(lstTTypelitige.get(i).getDtCREATED(), key.formatterMysql));
        json.put("dt_UPDATED", key.DateToString(lstTTypelitige.get(i).getDtUPDATED(), key.formatterMysql));

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTTypelitige.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>