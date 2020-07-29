<%@page import="dal.TBonLivraison"%>
<%@page import="bll.facture.factureManagement"%>
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
  
    List<TBonLivraison> lstTBonLivraison = new ArrayList<TBonLivraison>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data detail fournisseurs");
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
    String lg_FACTURE_ID = "%%", lg_customer_id = "", lg_type_facture = "%%";
    Date dt_debut, dt_fin;
   
   
    if (request.getParameter("lg_customer_id") != null) {
        lg_customer_id = request.getParameter("lg_customer_id");
        new logger().OCategory.info("lg_customer_id " + request.getParameter("lg_customer_id"));
    }

    if (request.getParameter("lg_type_facture") != null) {
        lg_type_facture = request.getParameter("lg_type_facture");
        new logger().OCategory.info("lg_type_facture " + request.getParameter("lg_type_facture"));
    }

    if (request.getParameter("dt_debut") != null) {
        dt_debut = key.stringToDate(request.getParameter("dt_debut"), key.formatterMysqlShort);
        new logger().OCategory.info("dt_debut " + dt_debut);
    }

    if (request.getParameter("dt_fin") != null) {
        dt_fin = key.stringToDate(request.getParameter("dt_fin"), key.formatterMysqlShort);
        new logger().OCategory.info("dt_fin " + dt_fin);
    }

     dt_fin = date.getDate(request.getParameter("dt_fin"), "23:59");
     dt_debut = new date().stringToDate(request.getParameter("dt_debut"), new date().formatterMysqlShort);

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    factureManagement OfactureManagement = new factureManagement(OdataManager, OTUser);

    lstTBonLivraison = OfactureManagement.getListTBonLivraison(lg_customer_id, dt_debut, dt_fin, commonparameter.statut_is_Closed);


    

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTBonLivraison.size()) {
            DATA_PER_PAGE = lstTBonLivraison.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTBonLivraison.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTBonLivraison.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_BON_LIVRAISON_ID", lstTBonLivraison.get(i).getLgBONLIVRAISONID());
        json.put("str_LIBELLE", lstTBonLivraison.get(i).getLgORDERID().getLgGROSSISTEID().getStrLIBELLE());
        json.put("str_REF_ORDER", lstTBonLivraison.get(i).getLgORDERID().getStrREFORDER());
        json.put("dt_DATE_LIVRAISON", key.DateToString(lstTBonLivraison.get(i).getDtDATELIVRAISON(), key.formatterShort));
        json.put("int_MHT", lstTBonLivraison.get(i).getIntMHT());
        json.put("int_TVA", lstTBonLivraison.get(i).getIntTVA());
        json.put("int_HTTC", lstTBonLivraison.get(i).getIntHTTC());

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTBonLivraison.size() + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>