<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TTranche"  %>
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
    String lg_TRANCHE_ID = "%%";
    int int_MONTANT_MIN, int_MONTANT_MAX;
    double dbl_POURCENTAGE_TRANCHE;
    json Ojson = new json();
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    List<dal.TTranche> lstTTranche = new ArrayList<dal.TTranche>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data famille ");
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

<%    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_TRANCHE_ID") != null) {
        if (request.getParameter("lg_TRANCHE_ID").toString().equals("ALL")) {
            lg_TRANCHE_ID = "%%";
        } else {
            lg_TRANCHE_ID = request.getParameter("lg_TRANCHE_ID").toString();
        }

    }

    OdataManager.initEntityManager();
    lstTTranche = OdataManager.getEm().createQuery("SELECT t FROM TTranche t WHERE t.lgTRANCHEID LIKE ?1  AND t.strSTATUT LIKE ?2 ").
            setParameter(1, lg_TRANCHE_ID)
            .setParameter(2, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTTranche.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTTranche.size()) {
            DATA_PER_PAGE = lstTTranche.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTranche.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTTranche.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_TRANCHE_ID", lstTTranche.get(i).getLgTRANCHEID());
        json.put("int_MONTANT_MIN", lstTTranche.get(i).getIntMONTANTMIN());
        json.put("int_MONTANT_MAX", lstTTranche.get(i).getIntMONTANTMAX());
        json.put("dbl_POURCENTAGE_TRANCHE", lstTTranche.get(i).getDblPOURCENTAGETRANCHE());
        json.put("str_STATUT", lstTTranche.get(i).getStrSTATUT());
        
        dt_CREATED = lstTTranche.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTTranche.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTTranche.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>