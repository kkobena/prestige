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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();

    List<dal.TUserFone> lstTUserFone = new ArrayList<dal.TUserFone>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data");
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

<%    String lg_USER_FONE_ID = "%%", str_PHONE = "",lg_USER_ID = "%%", search_value = "";
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value" + search_value);
    } 
    
    if (request.getParameter("lg_USER_FONE_ID") != null) {
        lg_USER_FONE_ID = request.getParameter("lg_USER_FONE_ID");
        new logger().OCategory.info("lg_USER_FONE_ID" + lg_USER_FONE_ID);
    }
    
    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID" + lg_USER_ID);
    }

  
    OdataManager.initEntityManager();
    lstTUserFone = OdataManager.getEm().createQuery("SELECT t FROM TUserFone t WHERE t.lgUSERID.lgUSERID LIKE ?1 AND  t.strSTATUT LIKE ?2 ").
            setParameter(1, lg_USER_ID).
            setParameter(2, commonparameter.statut_enable).
            getResultList();
    new logger().OCategory.info(lstTUserFone.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTUserFone.size()) {
            DATA_PER_PAGE = lstTUserFone.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTUserFone.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTUserFone.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_USER_FONE_ID", lstTUserFone.get(i).getLgUSERFONEID());
        json.put("str_PHONE", lstTUserFone.get(i).getStrPHONE());
        json.put("lg_USER_ID", lstTUserFone.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTUserFone.get(i).getLgUSERID().getStrLASTNAME() + "  (" + lstTUserFone.get(i).getStrPHONE() + ")");
        json.put("str_STATUT", oTranslate.getValue(lstTUserFone.get(i).getStrSTATUT()));

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTUserFone.size() + " \",\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>