<%@page import="dal.TRetourFournisseurDetail"%>
<%@page import="bll.commandeManagement.bonLivraisonManagement"%>
<%@page import="bll.commandeManagement.retourFournisseurManagement"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TFamille"  %>
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
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data famille retour fournisseur");
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

<%    String lg_BON_LIVRAISON_ID = "", search_value = "";
    List<TFamille> lstTFamille = new ArrayList<TFamille>();

    if (request.getParameter("lg_BON_LIVRAISON_ID") != null) {
        lg_BON_LIVRAISON_ID = request.getParameter("lg_BON_LIVRAISON_ID").toString();
        new logger().OCategory.info("lg_BON_LIVRAISON_ID " + lg_BON_LIVRAISON_ID);
    }
    
    if (request.getParameter("search_value") != null && !request.getParameter("search_value").equalsIgnoreCase("")) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value " + search_value);
    }
    
    new logger().OCategory.info("search_value query " + request.getParameter("filter") );
     if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value query " + search_value);
    }

    OdataManager.initEntityManager();
    bonLivraisonManagement ObonLivraisonManagement = new bonLivraisonManagement(OdataManager);

    lstTFamille = ObonLivraisonManagement.findFamilleBLDetail(search_value, lg_BON_LIVRAISON_ID);

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTFamille.size()) {
            DATA_PER_PAGE = lstTFamille.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTFamille.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();

    for (int i = pgInt; i < pgInt_Last; i++) {

        JSONObject json = new JSONObject();

        json.put("lg_FAMILLE_ID", lstTFamille.get(i).getLgFAMILLEID());
        json.put("str_NAME", lstTFamille.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTFamille.get(i).getStrDESCRIPTION());
        json.put("int_CIP", lstTFamille.get(i).getIntCIP());
         json.put("str_DESCRIPTION_PLUS", lstTFamille.get(i).getStrNAME());
         json.put("int_PAF", lstTFamille.get(i).getIntPAF());
        json.put("lg_GROSSISTE_ID", lstTFamille.get(i).getStrCODETABLEAU());
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTFamille.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

   lstTFamille = null;
    OdataManager = null;
    search_value = null;
    lg_BON_LIVRAISON_ID = null;
%>

<%= result%>