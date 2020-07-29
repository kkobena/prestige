<%@page import="dal.TVille"%>
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


<%
    dataManager OdataManager = new dataManager();
    List<TVille> lstTVille = new ArrayList<TVille>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data ville");
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
     String lg_VILLE_ID = "%%", STR_NAME = "%%", STR_CODE_POSTAL = "%%" ,STR_BUREAU_DISTRIBUTEUR="%%";   
    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_VILLE_ID") != null) {
        if (request.getParameter("lg_VILLE_ID").toString().equals("ALL")) {
            lg_VILLE_ID = "%%";
        } else {
            lg_VILLE_ID = request.getParameter("lg_VILLE_ID").toString();
        }

    }

   
    OdataManager.initEntityManager();
    lstTVille = OdataManager.getEm().createQuery("SELECT t FROM TVille t WHERE t.lgVILLEID LIKE ?1 AND t.strName LIKE ?2 AND t.strStatut LIKE ?3 ").
            setParameter(1, lg_VILLE_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTVille.size());
%>   

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTVille.size()) {
            DATA_PER_PAGE = lstTVille.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTVille.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTVille.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_VILLE_ID", lstTVille.get(i).getLgVILLEID());
        json.put("STR_NAME", lstTVille.get(i).getStrName());

        json.put("STR_DESCRIPTION", lstTVille.get(i).getStrName());
        json.put("STR_CODE_POSTAL", lstTVille.get(i).getStrCodePostal());
        json.put("STR_BUREAU_DISTRIBUTEUR", lstTVille.get(i).getStrBureauDistributeur());
        json.put("str_CODE", lstTVille.get(i).getStrCODE());
        

        json.put("str_STATUT", lstTVille.get(i).getStrStatut());
        if (lstTVille.get(i).getDtCreated() != null) {
           json.put("dt_CREATED", date.DateToString(lstTVille.get(i).getDtCreated(), date.formatterShort));
        }

        if (lstTVille.get(i).getDtUpdated() != null) {
            json.put("dt_UPDATED", date.DateToString(lstTVille.get(i).getDtUpdated(), date.formatterShort));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTVille.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>