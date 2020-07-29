<%@page import="dal.TMouvement"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="bll.configManagement.familleGrossisteManagement"%>
<%@page import="dal.TFamilleGrossiste"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TFamille"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TOrderDetail"  %>
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
    TFamilleStock OTFamillestock = null;
    TFamilleGrossiste OTFamilleGrossiste = null;
    date key = new date();
    TMouvement OTMouvement = null;
    Date dt_CREATED, dt_UPDATED;
%>

<%
    // int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans webservices.commandemanagement.orderdetail.ws_data ---");
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

<%    List<TOrderDetail> lstTOrderDetail = new ArrayList<dal.TOrderDetail>();
    String lg_ORDER_ID = "%%", search_value = "", str_STATUT = commonparameter.statut_is_Process;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    if (request.getParameter("lg_ORDER_ID") != null) {
        lg_ORDER_ID = request.getParameter("lg_ORDER_ID").toString();
        new logger().OCategory.info("lg_ORDER_ID " + lg_ORDER_ID);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT").toString();
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }
    OdataManager.initEntityManager();
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
    orderManagement OorderManagement = new orderManagement(OdataManager, OTUser);
    familleGrossisteManagement OfamilleGrossisteManagement = new familleGrossisteManagement(OdataManager);
   
    lstTOrderDetail = OorderManagement.getTOrderDetail(search_value, lg_ORDER_ID, str_STATUT);

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTOrderDetail.size()) {
            DATA_PER_PAGE = lstTOrderDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTOrderDetail.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTOrderDetail.get(i));

        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        OTFamillestock = OtellerManagement.geProductItemStock(lstTOrderDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        OTFamilleGrossiste = OfamilleGrossisteManagement.findGrossiste(lstTOrderDetail.get(i).getLgFAMILLEID(), lstTOrderDetail.get(i).getLgORDERID().getLgGROSSISTEID());
        json.put("lg_ORDERDETAIL_ID", lstTOrderDetail.get(i).getLgORDERDETAILID());
        json.put("lg_ORDER_ID", lstTOrderDetail.get(i).getLgORDERID().getLgORDERID());
        json.put("int_NUMBER", lstTOrderDetail.get(i).getIntNUMBER());
        json.put("int_PRICE", lstTOrderDetail.get(i).getIntPRICE());
        json.put("int_QTE_MANQUANT", lstTOrderDetail.get(i).getIntQTEMANQUANT());
        json.put("lg_GROSSISTE_ID", lstTOrderDetail.get(i).getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("lg_GROSSISTE_LIBELLE", lstTOrderDetail.get(i).getLgORDERID().getLgGROSSISTEID().getStrLIBELLE());
        //json.put("lg_GROSSISTE_ID", lstTOrderDetail.get(i).getLgGROSSISTEID().getLgGROSSISTEID());

        // bool_BL
        json.put("bool_BL", lstTOrderDetail.get(i).getBoolBL());

        json.put("lg_FAMILLE_ID", lstTOrderDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("lg_FAMILLE_NAME", lstTOrderDetail.get(i).getLgFAMILLEID().getStrNAME());
        json.put("lg_FAMILLE_CIP", (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTOrderDetail.get(i).getLgFAMILLEID().getIntCIP()));
        json.put("lg_FAMILLE_PRIX_VENTE", lstTOrderDetail.get(i).getIntPRICEDETAIL());
        json.put("lg_FAMILLE_PRIX_ACHAT", lstTOrderDetail.get(i).getLgFAMILLEID().getIntPAT());
        json.put("int_PAF", lstTOrderDetail.get(i).getIntPAFDETAIL());
        json.put("int_PRIX_REFERENCE", lstTOrderDetail.get(i).getLgFAMILLEID().getIntPRICETIPS());
        json.put("int_QTE_LIVRE", lstTOrderDetail.get(i).getIntNUMBER() - lstTOrderDetail.get(i).getIntQTEMANQUANT());
        json.put("int_QTE_REP_GROSSISTE", lstTOrderDetail.get(i).getIntQTEREPGROSSISTE());

        json.put("int_SEUIL", lstTOrderDetail.get(i).getLgFAMILLEID().getIntSEUILMIN());

        int int_QTE_REASSORT = 0;
        try {
            int_QTE_REASSORT = OTFamillestock.getIntNUMBERAVAILABLE() - lstTOrderDetail.get(i).getLgFAMILLEID().getIntSEUILMIN();
            new logger().OCategory.info("int_QTE_REASSORT " + int_QTE_REASSORT);
            if (int_QTE_REASSORT < 0) {
                int_QTE_REASSORT = -1 * int_QTE_REASSORT;
            } else {
                int_QTE_REASSORT = 0;
            }
        } catch (Exception e) {
        }
        json.put("int_QTE_REASSORT", int_QTE_REASSORT);

        try {
            json.put("lg_FAMILLE_QTE_STOCK", OTFamillestock.getIntNUMBERAVAILABLE());

        } catch (Exception E) {
            json.put("lg_FAMILLE_QTE_STOCK", 0);
        }

        if (OTFamilleGrossiste != null) {
            json.put("str_CODE_ARTICLE", OTFamilleGrossiste.getStrCODEARTICLE());
        } else {
            json.put("str_CODE_ARTICLE", "");
        }

        json.put("str_STATUT", lstTOrderDetail.get(i).getStrSTATUT());

        dt_CREATED = lstTOrderDetail.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTOrderDetail.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTOrderDetail.size() + " \" ,\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>