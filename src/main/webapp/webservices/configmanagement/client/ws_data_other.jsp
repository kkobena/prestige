<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TCompteClient"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TClient"  %>
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


<%     dataManager OdataManager = new dataManager();
       
    date key = new date();
    List<dal.TClient> lstTClient = new ArrayList<dal.TClient>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data client ");
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
    String  lg_TYPE_CLIENT_ID = "%%", search_value = "";
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    } 
    
    if (request.getParameter("lg_TYPE_CLIENT_ID") != null) {
        lg_TYPE_CLIENT_ID = request.getParameter("lg_TYPE_CLIENT_ID");
        new logger().OCategory.info("lg_TYPE_CLIENT_ID " + lg_TYPE_CLIENT_ID);
    }
    OdataManager.initEntityManager();
    clientManagement OclientManagement = new clientManagement(OdataManager);
    /*lstTClient = OdataManager.getEm().createQuery("SELECT t FROM TClient t WHERE t.lgCLIENTID LIKE ?1 AND t.strFIRSTNAME LIKE ?2 AND t.strSTATUT LIKE ?3 ").
     setParameter(1, lg_CLIENT_ID)
     .setParameter(2, Os_Search_poste.getOvalue())
     .setParameter(3, commonparameter.statut_enable)
     .getResultList();*/
    lstTClient = OclientManagement.showOnorAllClientByType(search_value, lg_TYPE_CLIENT_ID, commonparameter.statut_enable);
    new logger().OCategory.info(lstTClient.size());
    new logger().OCategory.info("Fin formation liste client : " + lstTClient.size() + "éléments");
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTClient.size()) {
            DATA_PER_PAGE = lstTClient.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTClient.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTClient.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        new logger().OCategory.info("info compte " + lstTClient.get(i).getTCompteClientCollection().size());
        Collection<TCompteClient> CollTCompteClient = lstTClient.get(i).getTCompteClientCollection();
        Iterator iterarorTCompteClient = CollTCompteClient.iterator();
        Double dbl_SOLDE = 0.0;
        Double dbl_CAUTION = 0.0;
        Double dbl_QUOTA_CONSO_MENSUELLE = 0.0;
        String lg_COMPTE_CLIENT_ID = "";
        while (iterarorTCompteClient.hasNext()) {
            Object elTCompteClient = iterarorTCompteClient.next();
            TCompteClient OTCompteClient = (TCompteClient) elTCompteClient;

            OdataManager.getEm().refresh(OTCompteClient);
            //dbl_SOLDE += OTCompteClient.getDblSOLDE();
            dbl_CAUTION += OTCompteClient.getDblCAUTION();
            dbl_QUOTA_CONSO_MENSUELLE += OTCompteClient.getDblQUOTACONSOMENSUELLE();
            lg_COMPTE_CLIENT_ID = OTCompteClient.getLgCOMPTECLIENTID();
        }
        new logger().OCategory.info("dbl_SOLDE " + dbl_SOLDE + " dbl_CAUTION " + dbl_CAUTION + " dbl_QUOTA_CONSO_MENSUELLE " + dbl_QUOTA_CONSO_MENSUELLE);
        json.put("dbl_SOLDE", dbl_SOLDE);
        json.put("dbl_CAUTION", dbl_CAUTION);
        json.put("dbl_QUOTA_CONSO_MENSUELLE", dbl_QUOTA_CONSO_MENSUELLE);
        json.put("lg_COMPTE_CLIENT_ID", lg_COMPTE_CLIENT_ID);
        // lg_CLIENT_ID
        json.put("lg_CLIENT_ID", lstTClient.get(i).getLgCLIENTID());
        // str_CODE_INTERNE
        json.put("str_CODE_INTERNE", lstTClient.get(i).getStrCODEINTERNE());
        // str_FIRST_NAME
        json.put("str_FIRST_NAME", lstTClient.get(i).getStrFIRSTNAME());
        // str_LAST_NAME
        json.put("str_LAST_NAME", lstTClient.get(i).getStrLASTNAME());
        json.put("str_FIRST_LAST_NAME", lstTClient.get(i).getStrFIRSTNAME() + " " + lstTClient.get(i).getStrLASTNAME());
        // str_NUMERO_SECURITE_SOCIAL
        json.put("str_NUMERO_SECURITE_SOCIAL", lstTClient.get(i).getStrNUMEROSECURITESOCIAL());
        // dt_NAISSANCE
        json.put("dt_NAISSANCE", key.DateToString(lstTClient.get(i).getDtNAISSANCE(), key.formatterMysql));
        // str_SEXE
        json.put("str_SEXE", lstTClient.get(i).getStrSEXE());
        // str_ADRESSE
        json.put("str_ADRESSE", lstTClient.get(i).getStrADRESSE());
        // str_DOMICILE
        json.put("str_DOMICILE", lstTClient.get(i).getStrDOMICILE());
        // str_AUTRE_ADRESSE
        json.put("str_AUTRE_ADRESSE", lstTClient.get(i).getStrAUTREADRESSE());
        // str_CODE_POSTAL
        json.put("str_CODE_POSTAL", lstTClient.get(i).getStrCODEPOSTAL());
        // str_COMMENTAIRE
        json.put("str_COMMENTAIRE", lstTClient.get(i).getStrCOMMENTAIRE());
        // lg_RISQUE_ID
//        json.put("lg_RISQUE_ID", lstTClient.get(i).getLgRISQUEID().getStrCODERISQUE()); 
        // lg_VILLE_ID
        json.put("lg_VILLE_ID", lstTClient.get(i).getLgVILLEID().getStrName());
        json.put("lg_TYPE_CLIENT_ID", lstTClient.get(i).getLgTYPECLIENTID().getStrNAME());

        json.put("str_STATUT", lstTClient.get(i).getStrSTATUT());

        arrayObj.put(json);
    }
    JSONObject json = new JSONObject();
    json.put("lg_CLIENT_ID", "0");
    json.put("str_FIRST_LAST_NAME", "Ajouter un nouveau client");
    arrayObj.put(json);

    String result = "({\"total\":\"" + lstTClient.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>