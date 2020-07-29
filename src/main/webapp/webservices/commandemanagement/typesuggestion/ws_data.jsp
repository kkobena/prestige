<%@page import="dal.dataManager"  %>
<%@page import="dal.TTypesuggestion"  %>
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
    String lg_TYPESUGGESTION_ID = "%%", str_NAME = "%%", str_DESCRIPTION = "%%";
    date key = new date();
    json Ojson = new json();
    Date dt_CREATED, dt_UPDATED;
    List<dal.TTypesuggestion> lstTTypesuggestion = new ArrayList<dal.TTypesuggestion>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data type suggestion ");
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
    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_TYPESUGGESTION_ID") != null) {
        if (request.getParameter("lg_TYPESUGGESTION_ID").toString().equals("ALL")) {
            lg_TYPESUGGESTION_ID = "%%";
        } else {
            lg_TYPESUGGESTION_ID = request.getParameter("lg_TYPESUGGESTION_ID").toString();
        }

    }

    OdataManager.initEntityManager();
    lstTTypesuggestion = OdataManager.getEm().createQuery("SELECT t FROM TTypesuggestion t WHERE t.lgTYPESUGGESTIONID LIKE ?1 AND t.strNAME LIKE ?2 AND t.strSTATUT LIKE ?3 ").
            setParameter(1, lg_TYPESUGGESTION_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTTypesuggestion.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTTypesuggestion.size()) {
            DATA_PER_PAGE = lstTTypesuggestion.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTypesuggestion.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTTypesuggestion.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_TYPESUGGESTION_ID", lstTTypesuggestion.get(i).getLgTYPESUGGESTIONID());
        json.put("str_NAME", lstTTypesuggestion.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTTypesuggestion.get(i).getStrDESCRIPTION());
        json.put("str_STATUT", lstTTypesuggestion.get(i).getStrSTATUT());
        
        dt_CREATED = lstTTypesuggestion.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTTypesuggestion.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTTypesuggestion.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>