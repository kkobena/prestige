<%@page import="dal.TCoefficientPonderation"%>
<%@page import="bll.configManagement.CodeGestionManager"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TCodeGestion"  %>
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
    
    List<TCodeGestion> lstTCodeGestion = new ArrayList<TCodeGestion>();
    List<TCoefficientPonderation> lstTCoefficientPonderation = new ArrayList<TCoefficientPonderation>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data code gestion ");
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
    String lg_CODE_GESTION_ID = "%%", search_value = "", lg_OPTIMISATION_QUANTITE_ID = "%%";
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    } 
    
    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value " + search_value);
    } 
    
    if (request.getParameter("lg_CODE_GESTION_ID") != null && !request.getParameter("lg_CODE_GESTION_ID").equalsIgnoreCase("")) {
        lg_CODE_GESTION_ID = request.getParameter("lg_CODE_GESTION_ID");
        new logger().OCategory.info("lg_CODE_GESTION_ID " + lg_CODE_GESTION_ID);
    } 
    
    if (request.getParameter("lg_OPTIMISATION_QUANTITE_ID") != null && !request.getParameter("lg_OPTIMISATION_QUANTITE_ID").equalsIgnoreCase("")) {
        lg_OPTIMISATION_QUANTITE_ID = request.getParameter("lg_OPTIMISATION_QUANTITE_ID");
        new logger().OCategory.info("lg_OPTIMISATION_QUANTITE_ID " + lg_OPTIMISATION_QUANTITE_ID);
    } 
    
    

    OdataManager.initEntityManager();
    CodeGestionManager OCodeGestionManager = new CodeGestionManager(OdataManager);
    lstTCodeGestion = OCodeGestionManager.getlistTCodeGestion(search_value, lg_CODE_GESTION_ID, lg_OPTIMISATION_QUANTITE_ID);
    
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTCodeGestion.size()) {
            DATA_PER_PAGE = lstTCodeGestion.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTCodeGestion.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%   JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTCodeGestion.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_CODE_GESTION_ID", lstTCodeGestion.get(i).getLgCODEGESTIONID());
        json.put("str_CODE_BAREME", lstTCodeGestion.get(i).getStrCODEBAREME());
        json.put("int_JOURS_COUVERTURE_STOCK", lstTCodeGestion.get(i).getIntJOURSCOUVERTURESTOCK());
        json.put("int_MOIS_HISTORIQUE_VENTE", lstTCodeGestion.get(i).getIntMOISHISTORIQUEVENTE());
        json.put("int_DATE_BUTOIR_ARTICLE", lstTCodeGestion.get(i).getIntDATEBUTOIRARTICLE());
        json.put("int_DATE_LIMITE_EXTRAPOLATION", lstTCodeGestion.get(i).getIntDATELIMITEEXTRAPOLATION());

        json.put("bool_OPTIMISATION_SEUIL_CMDE", lstTCodeGestion.get(i).getBoolOPTIMISATIONSEUILCMDE());
        
        json.put("int_COEFFICIENT_PONDERATION", lstTCodeGestion.get(i).getIntCOEFFICIENTPONDERATION());
        json.put("lg_OPTIMISATION_QUANTITE_ID", lstTCodeGestion.get(i).getLgOPTIMISATIONQUANTITEID().getStrLIBELLEOPTIMISATION());
        json.put("str_STATUT", lstTCodeGestion.get(i).getStrSTATUT());

        if (lstTCodeGestion.get(i).getDtCREATED() != null) {
            json.put("dt_CREATED", date.DateToString(lstTCodeGestion.get(i).getDtCREATED(), date.formatterShort));
        }

        if (lstTCodeGestion.get(i).getDtUPDATED() != null) {
            json.put("dt_UPDATED", date.DateToString(lstTCodeGestion.get(i).getDtUPDATED(), date.formatterShort));
        }
        
        lstTCoefficientPonderation = OCodeGestionManager.getListTCoefficientPonderation(lstTCodeGestion.get(i).getLgCODEGESTIONID());
        for(TCoefficientPonderation OTCoefficientPonderation: lstTCoefficientPonderation) {
            json.put("int_COEFFICIENT_PONDERATION"+OTCoefficientPonderation.getIntINDICEMONTH(), OTCoefficientPonderation.getIntCOEFFICIENTPONDERATION());
        }
        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTCodeGestion.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>