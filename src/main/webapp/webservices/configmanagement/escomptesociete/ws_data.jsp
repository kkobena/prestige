<%@page import="dal.dataManager"  %>
<%@page import="dal.TEscompteSociete"  %>
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
    String lg_ESCOMPTE_SOCIETE_ID = "%%", str_LIBELLE_ESCOMPTE_SOCIETE = "%%";
    int int_CODE_ESCOMPTE_SOCIETE = 0;
    date key = new date();
    json Ojson = new json();
    Date dt_CREATED, dt_UPDATED;
    List<dal.TEscompteSociete> lstTEscompteSociete = new ArrayList<dal.TEscompteSociete>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data optiminisation quantite ");
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
    if (request.getParameter("lg_ESCOMPTE_SOCIETE_ID") != null) {
        if (request.getParameter("lg_ESCOMPTE_SOCIETE_ID").toString().equals("ALL")) {
            lg_ESCOMPTE_SOCIETE_ID = "%%";
        } else {
            lg_ESCOMPTE_SOCIETE_ID = request.getParameter("lg_ESCOMPTE_SOCIETE_ID").toString();
        }

    }

    OdataManager.initEntityManager();
    lstTEscompteSociete = OdataManager.getEm().createQuery("SELECT t FROM TEscompteSociete t WHERE t.lgESCOMPTESOCIETEID LIKE ?1 AND t.strLIBELLEESCOMPTESOCIETE LIKE ?2 AND t.strSTATUT LIKE ?3 ").
            setParameter(1, lg_ESCOMPTE_SOCIETE_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTEscompteSociete.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTEscompteSociete.size()) {
            DATA_PER_PAGE = lstTEscompteSociete.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTEscompteSociete.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTEscompteSociete.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_ESCOMPTE_SOCIETE_ID", lstTEscompteSociete.get(i).getLgESCOMPTESOCIETEID());
        json.put("int_CODE_ESCOMPTE_SOCIETE", lstTEscompteSociete.get(i).getIntCODEESCOMPTESOCIETE());
        json.put("str_LIBELLE_ESCOMPTE_SOCIETE", lstTEscompteSociete.get(i).getStrLIBELLEESCOMPTESOCIETE());
        json.put("str_STATUT", lstTEscompteSociete.get(i).getStrSTATUT());
        
        dt_CREATED = lstTEscompteSociete.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTEscompteSociete.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTEscompteSociete.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>