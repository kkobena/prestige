<%@page import="bll.stockManagement.InventaireManager"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TInventaireFamille"  %>
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
    json Ojson = new json();
    List<TInventaireFamille> lstTInventaireFamille = new ArrayList<TInventaireFamille>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data ecart inventaire");
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
     String lg_INVENTAIRE_ID = "%%", search_value = "", str_TYPE = "";
    String MANQUANT = "MANQUANT", SURPLUS = "SURPLUS";

    search_value = request.getParameter("search_value");
    if (search_value != null) {
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        search_value = "";
    }

    if (request.getParameter("lg_INVENTAIRE_ID") != null && request.getParameter("lg_INVENTAIRE_ID") != "") {
        lg_INVENTAIRE_ID = request.getParameter("lg_INVENTAIRE_ID");
        new logger().OCategory.info("lg_INVENTAIRE_ID " + lg_INVENTAIRE_ID);
    }
    if (request.getParameter("str_TYPE") != null && request.getParameter("str_TYPE") != "") {
        str_TYPE = request.getParameter("str_TYPE");
        
    }
            
    OdataManager.initEntityManager();
    InventaireManager OInventaireManager = new InventaireManager(OdataManager);
    new logger().OCategory.info("str_TYPE " + str_TYPE + " search_value "+search_value);
    if (str_TYPE.equalsIgnoreCase(MANQUANT)) {
        lstTInventaireFamille = OInventaireManager.listEcartInventaireManquant(search_value);
    } else if (str_TYPE.equalsIgnoreCase(SURPLUS)) {
        lstTInventaireFamille = OInventaireManager.listEcartInventaireSurplus(search_value);
    } else {
        lstTInventaireFamille = OInventaireManager.listEcartInventaire(search_value);
    }
    new logger().OCategory.info("Size lstTInventaireFamille  " + lstTInventaireFamille.size());

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTInventaireFamille.size()) {
            DATA_PER_PAGE = lstTInventaireFamille.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTInventaireFamille.size() - (DATA_PER_PAGE * (pgInt)));
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

        json.put("lg_INVENTAIRE_FAMILLE_ID", lstTInventaireFamille.get(i).getLgINVENTAIREFAMILLEID());
        json.put("lg_INVENTAIRE_ID", lstTInventaireFamille.get(i).getLgINVENTAIREID().getLgINVENTAIREID());
        json.put("lg_FAMILLE_ID", lstTInventaireFamille.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("int_CIP", lstTInventaireFamille.get(i).getLgFAMILLEID().getIntCIP());
        json.put("str_DESCRIPTION", lstTInventaireFamille.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("lg_ZONE_GEO_ID", lstTInventaireFamille.get(i).getLgFAMILLEID().getLgZONEGEOID().getStrLIBELLEE());
        json.put("lg_FAMILLEARTICLE_ID", lstTInventaireFamille.get(i).getLgFAMILLEID().getLgFAMILLEARTICLEID().getStrLIBELLE());
        json.put("dt_UPDATED", date.DateToString(lstTInventaireFamille.get(i).getLgFAMILLEID().getDtUPDATED(), date.formatterShort));
       // json.put("int_PRICE", lstTInventaireFamille.get(i).getIntPRICE());
        
        json.put("lg_INDICATEUR_REAPPROVISIONNEMENT_ID", lstTInventaireFamille.get(i).getLgINVENTAIREID().getLgUSERID().getStrFIRSTNAME() + " " + lstTInventaireFamille.get(i).getLgINVENTAIREID().getLgUSERID().getStrLASTNAME());
        json.put("int_NUMBER_AVAILABLE", lstTInventaireFamille.get(i).getIntNUMBER());
        json.put("int_MOY_VENTE", lstTInventaireFamille.get(i).getIntNUMBERINIT());
        try {
            json.put("int_QTE_SORTIE", lstTInventaireFamille.get(i).getIntNUMBER() - lstTInventaireFamille.get(i).getIntNUMBERINIT());
        }catch(Exception e) {
            e.printStackTrace();
        }

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTInventaireFamille.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>