<%@page import="dal.TTypeStockFamille"%>
<%@page import="bll.stockManagement.StockManager"%>
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


<%
    dataManager OdataManager = new dataManager();
    List<TFamille> lstTFamille = new ArrayList<TFamille>();


%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
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

<%    String lg_FAMILLE_ID = "%%", search_value = "", lg_TYPE_STOCK_ID = "1";
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (request.getParameter("lg_TYPE_STOCK_ID") != null) {
        lg_TYPE_STOCK_ID = request.getParameter("lg_TYPE_STOCK_ID");
        new logger().OCategory.info("lg_TYPE_STOCK_ID " + lg_TYPE_STOCK_ID);
    }

    OdataManager.initEntityManager();

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    familleManagement OfamilleManagement = new familleManagement(OdataManager);
    StockManager OStockManager = new StockManager(OdataManager, OTUser);

    lstTFamille = OfamilleManagement.showAllInitial(search_value, lg_FAMILLE_ID);

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

        String Role = "";

        JSONObject json = new JSONObject();
        // System.out.println("178");
        json.put("lg_FAMILLE_ID", lstTFamille.get(i).getLgFAMILLEID());
        try {
            json.put("lg_FAMILLEARTICLE_ID", lstTFamille.get(i).getLgFAMILLEARTICLEID().getStrLIBELLE());
        } catch (Exception e) {

        }

        try {
            json.put("lg_TYPEETIQUETTE_ID", lstTFamille.get(i).getLgTYPEETIQUETTEID().getStrDESCRIPTION());
        } catch (Exception e) {
        }

        json.put("str_NAME", lstTFamille.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTFamille.get(i).getStrDESCRIPTION());
        json.put("int_PRICE", lstTFamille.get(i).getIntPRICE());
        try {
            json.put("lg_GROSSISTE_ID", lstTFamille.get(i).getLgGROSSISTEID().getStrLIBELLE());
        } catch (Exception e) {

        }
        json.put("int_CIP", lstTFamille.get(i).getIntCIP());
        json.put("int_PAF", lstTFamille.get(i).getIntPAF());
        json.put("int_PAT", lstTFamille.get(i).getIntPAT());
        int int_STOCK_REAPROVISONEMENT = 0, int_QTE_REAPPROVISIONNEMENT = 0;
        if (lstTFamille.get(i).getIntSTOCKREAPROVISONEMENT() != null) {
            int_STOCK_REAPROVISONEMENT = lstTFamille.get(i).getIntSTOCKREAPROVISONEMENT();
        }
        if (lstTFamille.get(i).getIntQTEREAPPROVISIONNEMENT() != null) {
            int_QTE_REAPPROVISIONNEMENT = lstTFamille.get(i).getIntQTEREAPPROVISIONNEMENT();
        }
        json.put("int_QTE_REAPPROVISIONNEMENT", int_QTE_REAPPROVISIONNEMENT);
        json.put("int_STOCK_REAPROVISONEMENT", int_STOCK_REAPROVISONEMENT);
        json.put("int_EAN13", lstTFamille.get(i).getIntEAN13());
        json.put("int_S", lstTFamille.get(i).getIntS());
        json.put("int_T", lstTFamille.get(i).getIntT());
        //
        json.put("int_SEUIL_MIN", lstTFamille.get(i).getIntSEUILMIN());
        try {
            json.put("lg_ZONE_GEO_ID", lstTFamille.get(i).getLgZONEGEOID().getStrLIBELLEE());
        } catch (Exception e) {

        }

        try {
            if (lstTFamille.get(i).getBoolDECONDITIONNE() == 0 && lstTFamille.get(i).getBoolDECONDITIONNEEXIST() == 1) {
                TFamille OTFamille = OfamilleManagement.getFamilleDecondition(lstTFamille.get(i).getIntCIP());
                json.put("lg_FAMILLE_DECONDITION_ID", OTFamille.getLgFAMILLEID());
                json.put("str_DESCRIPTION_DECONDITION", OTFamille.getStrDESCRIPTION());
                int int_NUMBER_AVAILABLE_DECONDITION = 0;
                try {
                    int_NUMBER_AVAILABLE_DECONDITION = new tellerManagement(OdataManager).getTProductItemStock(OTFamille.getLgFAMILLEID()).getIntNUMBERAVAILABLE();
                } catch (Exception e) {
                }
                json.put("int_NUMBER_AVAILABLE_DECONDITION", int_NUMBER_AVAILABLE_DECONDITION);
            }

        } catch (Exception e) {

        }
        //   new logger().OCategory.info("int_NUMBERDETAIL " + lstTFamille.get(i).getIntNUMBERDETAIL() + " cip " + lstTFamille.get(i).getStrDESCRIPTION());
        json.put("int_NUMBERDETAIL", lstTFamille.get(i).getIntNUMBERDETAIL());

        json.put("int_PRICE_TIPS", lstTFamille.get(i).getIntPRICETIPS());
        json.put("int_TAUX_MARQUE", lstTFamille.get(i).getIntTAUXMARQUE());
        try {
            json.put("lg_CODE_ACTE_ID", lstTFamille.get(i).getLgCODEACTEID().getStrLIBELLEE());
        } catch (Exception e) {

        }
        try {
            json.put("lg_CODE_GESTION_ID", lstTFamille.get(i).getLgCODEGESTIONID().getStrCODEBAREME());
        } catch (Exception e) {

        }
        try {
            json.put("lg_FORME_ID", lstTFamille.get(i).getLgFORMEID().getStrLIBELLE());
        } catch (Exception e) {

        }
        try {
            json.put("lg_FABRIQUANT_ID", lstTFamille.get(i).getLgFABRIQUANTID().getStrNAME());
        } catch (Exception e) {

        }
        try {
            json.put("lg_INDICATEUR_REAPPROVISIONNEMENT_ID", lstTFamille.get(i).getLgINDICATEURREAPPROVISIONNEMENTID().getStrLIBELLEINDICATEUR());
        } catch (Exception e) {

        }
        json.put("str_CODE_TAUX_REMBOURSEMENT", lstTFamille.get(i).getStrCODETAUXREMBOURSEMENT());

        try {
            json.put("str_CODE_REMISE", lstTFamille.get(i).getStrCODEREMISE());
            json.put("lg_REMISE_ID", lstTFamille.get(i).getLgREMISEID().getStrNAME());
        } catch (Exception e) {

        }

        json.put("bool_DECONDITIONNE", lstTFamille.get(i).getBoolDECONDITIONNE());
        json.put("bool_DECONDITIONNE_EXIST", lstTFamille.get(i).getBoolDECONDITIONNEEXIST());

        try {
            json.put("lg_CODE_TVA_ID", lstTFamille.get(i).getLgCODETVAID().getStrNAME());
        } catch (Exception e) {

        }
        int int_NUMBER = 0, int_NUMBER_AVAILABLE = 0;
        try {

            TFamilleStock OTFamilleStock = new tellerManagement(OdataManager).getTProductItemStock(lstTFamille.get(i).getLgFAMILLEID());

            int_NUMBER_AVAILABLE = OTFamilleStock.getIntNUMBERAVAILABLE();
            int_NUMBER = int_NUMBER_AVAILABLE;
            /*TTypeStockFamille OTTypeStockFamille = OStockManager.getTTypeStockFamilleByTypestock(lg_TYPE_STOCK_ID, lstTFamille.get(i).getLgFAMILLEID());
             int_NUMBER_AVAILABLE = OTTypeStockFamille.getIntNUMBER();
             int_NUMBER = int_NUMBER_AVAILABLE;*/
        } catch (Exception e) {

        }
        json.put("int_NUMBER", int_NUMBER);
        json.put("int_NUMBER_AVAILABLE", int_NUMBER_AVAILABLE);
        json.put("int_T", lstTFamille.get(i).getIntT());
        json.put("str_DESCRIPTION_PLUS", lstTFamille.get(i).getIntCIP() + " " + lstTFamille.get(i).getStrDESCRIPTION() + " (" + lstTFamille.get(i).getIntPRICE() + ")");

        json.put("str_STATUT", lstTFamille.get(i).getStrSTATUT());

        json.put("bool_RESERVE", lstTFamille.get(i).getBoolRESERVE());
        if (lstTFamille.get(i).getBoolRESERVE()) {
            json.put("int_SEUIL_RESERVE", lstTFamille.get(i).getIntSEUILRESERVE());
            TTypeStockFamille OTTypeStockFamille = OStockManager.getTTypeStockFamilleByTypestock(lg_TYPE_STOCK_ID, lstTFamille.get(i).getLgFAMILLEID());
            json.put("int_STOCK_RESERVE", OTTypeStockFamille.getIntNUMBER());
        }

        json.put("dt_CREATED", date.DateToString(lstTFamille.get(i).getDtCREATED(), date.formatterMysql));
        json.put("dt_UPDATED", date.DateToString(lstTFamille.get(i).getDtUPDATED(), date.formatterMysql));
        if (lstTFamille.get(i).getDtPEREMPTION() != null) {
            json.put("dt_LAST_INVENTAIRE", date.DateToString(lstTFamille.get(i).getDtPEREMPTION(), date.formatterShort));
        }

        arrayObj.put(json);
    }

    if (lstTFamille.size() == 0) {
        JSONObject json = new JSONObject();
        json.put("lg_FAMILLE_ID", "0");
        json.put("str_DESCRIPTION", "Ajouter un nouvel article");
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTFamille.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
    lstTFamille = null;
    OdataManager = null;
    Os_Search_poste = null;
    Os_Search_poste_data = null;
    OStockManager = null;
%>

<%= result%>