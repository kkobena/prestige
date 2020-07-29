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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_RETOUR_FRS_DETAIL = "%%", lg_MOTIF_RETOUR = "%%", lg_FAMILLE_ID = "%%";    
    json Ojson = new json();
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    List<dal.TRetourFournisseurDetail> lstTRetourFournisseurDetail = new ArrayList<dal.TRetourFournisseurDetail>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data etiquette ");
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
    if (request.getParameter("lg_RETOUR_FRS_DETAIL") != null) {
        if (request.getParameter("lg_RETOUR_FRS_DETAIL").toString().equals("ALL")) {
            lg_RETOUR_FRS_DETAIL = "%%";
        } else {
            lg_RETOUR_FRS_DETAIL = request.getParameter("lg_RETOUR_FRS_DETAIL").toString();
        }

    }

   
    OdataManager.initEntityManager();
    lstTRetourFournisseurDetail = OdataManager.getEm().createQuery("SELECT t FROM TRetourFournisseurDetail t WHERE t.lgRETOURFRSDETAIL LIKE ?1 AND t.strSTATUT LIKE ?2 ").
            setParameter(1, lg_RETOUR_FRS_DETAIL)            
            .setParameter(2, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTRetourFournisseurDetail.size());
%>   

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTRetourFournisseurDetail.size()) {
            DATA_PER_PAGE = lstTRetourFournisseurDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTRetourFournisseurDetail.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTRetourFournisseurDetail.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_RETOUR_FRS_DETAIL", lstTRetourFournisseurDetail.get(i).getLgRETOURFRSDETAIL());            
        
        try {

                 lg_FAMILLE_ID = lstTRetourFournisseurDetail.get(i).getLgFAMILLEID().getStrDESCRIPTION();
                 if (lg_FAMILLE_ID != null) {
                     json.put("lg_FAMILLE_ID", lg_FAMILLE_ID);
                 }

             } catch (Exception e) {

             }
         try {

                 lg_MOTIF_RETOUR = lstTRetourFournisseurDetail.get(i).getLgMOTIFRETOUR().getStrLIBELLE();
                 if (lg_MOTIF_RETOUR != null) {
                     json.put("lg_MOTIF_RETOUR", lg_MOTIF_RETOUR);
                 }

             } catch (Exception e) {

             }

        json.put("str_STATUT", lstTRetourFournisseurDetail.get(i).getStrSTATUT());

        dt_CREATED = lstTRetourFournisseurDetail.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTRetourFournisseurDetail.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTRetourFournisseurDetail.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>