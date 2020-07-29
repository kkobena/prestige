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
    String lg_RETOUR_FRS_ID = "%%", str_REPONSE_FRS = "%%", str_COMMENTAIRE = "%%", lg_BON_LIVRAISON_ID = "%%", lg_GROSSISTE_ID = "%%";    
    json Ojson = new json();
    date key = new date();
    Date dt_DATE, dt_CREATED, dt_UPDATED;
    List<dal.TRetourFournisseur> lstTRetourFournisseur = new ArrayList<dal.TRetourFournisseur>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data retour fournisseur ");
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
    if (request.getParameter("lg_RETOUR_FRS_ID") != null) {
        if (request.getParameter("lg_RETOUR_FRS_ID").toString().equals("ALL")) {
            lg_RETOUR_FRS_ID = "%%";
        } else {
            lg_RETOUR_FRS_ID = request.getParameter("lg_RETOUR_FRS_ID").toString();
        }

    }

   
    OdataManager.initEntityManager();
    lstTRetourFournisseur = OdataManager.getEm().createQuery("SELECT t FROM TRetourFournisseur t WHERE t.lgRETOURFRSID LIKE ?1 AND t.strSTATUT LIKE ?2 ").
            setParameter(1, lg_RETOUR_FRS_ID)            
            .setParameter(2, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTRetourFournisseur.size());
%>   

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTRetourFournisseur.size()) {
            DATA_PER_PAGE = lstTRetourFournisseur.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTRetourFournisseur.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTRetourFournisseur.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_RETOUR_FRS_ID", lstTRetourFournisseur.get(i).getLgRETOURFRSID());
        json.put("str_REPONSE_FRS", lstTRetourFournisseur.get(i).getStrREPONSEFRS());        
        json.put("str_COMMENTAIRE", lstTRetourFournisseur.get(i).getStrCOMMENTAIRE());
        json.put("dt_DATE", lstTRetourFournisseur.get(i).getDtDATE());
        
        try {

                 lg_BON_LIVRAISON_ID = lstTRetourFournisseur.get(i).getLgBONLIVRAISONID().getStrREFLIVRAISON();
                 if (lg_BON_LIVRAISON_ID != null) {
                     json.put("lg_BON_LIVRAISON_ID", lg_BON_LIVRAISON_ID);
                 }

             } catch (Exception e) {

             }
         try {

                 lg_GROSSISTE_ID = lstTRetourFournisseur.get(i).getLgGROSSISTEID().getStrDESCRIPTION();
                 if (lg_GROSSISTE_ID != null) {
                     json.put("lg_GROSSISTE_ID", lg_GROSSISTE_ID);
                 }

             } catch (Exception e) {

             }
        
        

        json.put("str_STATUT", lstTRetourFournisseur.get(i).getStrSTATUT());

        dt_CREATED = lstTRetourFournisseur.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }
        

        dt_UPDATED = lstTRetourFournisseur.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTRetourFournisseur.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>