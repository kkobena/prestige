<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="dal.TPrivilege"%>
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


<% boolean P_BT_EDIT = false;
    dataManager OdataManager = new dataManager();

    List<TPrivilege> lstTPrivilege = new ArrayList<TPrivilege>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data");
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

<%    String lg_PRIVELEGE_ID = "%%", search_value = "";
    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_PRIVELEGE_ID") != null && request.getParameter("lg_PRIVELEGE_ID") != "") {
        lg_PRIVELEGE_ID = request.getParameter("lg_PRIVELEGE_ID");
        new logger().OCategory.info("lg_PRIVELEGE_ID " + lg_PRIVELEGE_ID);
    }

    OdataManager.initEntityManager();
    privilege Oprivilege = new privilege(OdataManager, OTUser);
    P_BT_EDIT = Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_BT_EDIT);
    /*    lstTPrivilege = OdataManager.getEm().createQuery("SELECT t FROM TPrivilege t WHERE t.lgPRIVELEGEID LIKE ?1 AND t.strNAME LIKE ?2 AND t.strSTATUT LIKE ?3 "). // a decommenter en cas de probleme
     setParameter(1, lg_PRIVELEGE_ID).
     setParameter(2, Os_Search_poste.getOvalue()).
     setParameter(3, commonparameter.statut_enable).getResultList();
     new logger().OCategory.info(lstTPrivilege.size());*/
    lstTPrivilege = Oprivilege.getListePrivilege(search_value, lg_PRIVELEGE_ID);
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTPrivilege.size()) {
            DATA_PER_PAGE = lstTPrivilege.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTPrivilege.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTPrivilege.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_PRIVELEGE_ID", lstTPrivilege.get(i).getLgPRIVELEGEID());
        json.put("str_NAME", lstTPrivilege.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTPrivilege.get(i).getStrDESCRIPTION());
        json.put("is_select", P_BT_EDIT);

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTPrivilege.size() + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>