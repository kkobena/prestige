<%@page import="dal.TTypeStockFamille"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TTypeStockFamille"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"%>
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
    String lg_FAMILLE_ID = "%%", lg_TYPE_STOCK_FAMILLE_ID = "%%", str_NAME = "%%", str_DESCRIPTION = "%%", lg_FAMILLEARTICLE_ID = "%%", search_value = "";
    String lg_SERVICE_ID = "%%", lg_TYPE_STOCK_ID = "3", P_KEY_IDENTITY = "", str_REF_SERVICECONCERNE = "%%", lg_COMPTE_ID = "%%", str_PHONE = "%%";
    int str_CODE_TVA = 0;
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<TTypeStockFamille> lstTTypeStockFamille = new ArrayList<TTypeStockFamille>();

    Integer int_search_cip;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data type stock famille ");
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

<%    search_value = request.getParameter("search_value");
    if (search_value != null) {
        // Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
        //  search_value = "%"+search_value+"%";
    } else {
        search_value = "";
        //Os_Search_poste.setOvalue("%%");
    }

    if (request.getParameter("lg_TYPE_STOCK_FAMILLE_ID") != null && request.getParameter("lg_TYPE_STOCK_FAMILLE_ID") != "") {
        lg_TYPE_STOCK_FAMILLE_ID = request.getParameter("lg_TYPE_STOCK_FAMILLE_ID");
        new logger().OCategory.info("lg_TYPE_STOCK_FAMILLE_ID " + lg_TYPE_STOCK_FAMILLE_ID);
    }
    if (request.getParameter("lg_TYPE_STOCK_ID") != null && request.getParameter("lg_TYPE_STOCK_ID") != "") {
        lg_TYPE_STOCK_ID = request.getParameter("lg_TYPE_STOCK_ID");
        new logger().OCategory.info("lg_TYPE_STOCK_ID " + lg_TYPE_STOCK_ID);
    }
    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }
    new logger().OCategory.info("search_value    " + search_value);

    OdataManager.initEntityManager();
    StockManager OStockManager = new StockManager(OdataManager);
    lstTTypeStockFamille = OStockManager.listeTTypeStockFamille(search_value, lg_TYPE_STOCK_FAMILLE_ID, lg_TYPE_STOCK_ID, lg_FAMILLE_ID);
    str_CODE_TVA = OStockManager.getCurrentTVA();
    new logger().OCategory.info("Size lstTTypeStockFamille  " + lstTTypeStockFamille.size());

%>

<%//Filtrede pagination
    try {
        if (DATA_PER_PAGE > lstTTypeStockFamille.size()) {
            DATA_PER_PAGE = lstTTypeStockFamille.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTypeStockFamille.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTTypeStockFamille.get(i).getLgFAMILLEID());
            OdataManager.getEm().refresh(lstTTypeStockFamille.get(i).getLgFAMILLEID().getLgFAMILLEID());
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_FAMILLE_ID", lstTTypeStockFamille.get(i).getLgFAMILLEID().getLgFAMILLEID());
        try {
            json.put("lg_FAMILLEARTICLE_ID", lstTTypeStockFamille.get(i).getLgFAMILLEID().getLgFAMILLEARTICLEID().getStrLIBELLE());
        } catch (Exception e) {

        }
        json.put("str_NAME", lstTTypeStockFamille.get(i).getLgFAMILLEID().getStrNAME());
        json.put("str_DESCRIPTION", lstTTypeStockFamille.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("int_PRICE", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntPRICE());
        json.put("int_NUMBER_ENTREE", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntPAF());
        
        
        json.put("int_CIP", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntCIP());
        json.put("int_PAF", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntPAF());
        json.put("int_PAT", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntPAT());
        json.put("int_STOCK_REAPROVISONEMENT", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntSEUILMIN());
        json.put("int_EAN13", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntEAN13());
        json.put("int_S", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntS());
        json.put("int_T", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntT());
       
        json.put("int_PRICE_TIPS", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntPRICETIPS());
        json.put("int_TAUX_MARQUE", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntTAUXMARQUE());
        
       
        json.put("str_CODE_TAUX_REMBOURSEMENT", lstTTypeStockFamille.get(i).getLgFAMILLEID().getStrCODETAUXREMBOURSEMENT());
        json.put("str_CODE_TVA", str_CODE_TVA);
        //json.put("str_CODE_ETIQUETTE", lstTTypeStockFamille.get(i).getLgFAMILLEID().getStrCODEETIQUETTE());
        json.put("str_CODE_REMISE", lstTTypeStockFamille.get(i).getLgFAMILLEID().getStrCODEREMISE());

       
        json.put("int_NUMBER", lstTTypeStockFamille.get(i).getIntNUMBER());
        json.put("int_NUMBER_AVAILABLE", lstTTypeStockFamille.get(i).getIntNUMBER());
        json.put("int_T", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntT());

      //json.put("int_QTEDETAIL", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntQTEDETAIL());
        //j        t("int_PRICE_DETAIL", lstTTypeStockFamille.get(i).getLgFAMILLEID().getIntPRICEDETAIL());
        json.put("str_STATUT", lstTTypeStockFamille.get(i).getLgFAMILLEID().getStrSTATUT());
        json.put("dt_CREATED", key.DateToString(lstTTypeStockFamille.get(i).getLgFAMILLEID().getDtCREATED(), key.formatterMysql));
        json.put("dt_UPDATED", key.DateToString(lstTTypeStockFamille.get(i).getLgFAMILLEID().getDtUPDATED(), key.formatterMysql));

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTTypeStockFamille.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>