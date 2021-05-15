<%@page import="util.DateConverter"%>
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



<%
    dataManager OdataManager = new dataManager();
    TFamilleStock OTFamillestock = null;
    TFamilleGrossiste OTFamilleGrossiste = null;
    date key = new date();

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

<%    List<TOrderDetail> lstTOrderDetail = new ArrayList<>();
    String lg_ORDER_ID = "%%", search_value = "", str_STATUT = commonparameter.statut_is_Process, filtre = DateConverter.ALL;
    int start = 0, limit = 0;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    if (request.getParameter("start") != null) {
        start = Integer.valueOf(request.getParameter("start"));
    }
    if (request.getParameter("limit") != null) {
        limit = Integer.valueOf(request.getParameter("limit"));

    }

    if (request.getParameter("lg_ORDER_ID") != null) {
        lg_ORDER_ID = request.getParameter("lg_ORDER_ID").toString();
        new logger().OCategory.info("lg_ORDER_ID " + lg_ORDER_ID);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }
    if (request.getParameter("filtre") != null) {
        filtre = request.getParameter("filtre");

    }
    OdataManager.initEntityManager();
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
    orderManagement OorderManagement = new orderManagement(OdataManager, OTUser);
    familleGrossisteManagement OfamilleGrossisteManagement = new familleGrossisteManagement(OdataManager);
    long total = OorderManagement.getOrderDetailCount(search_value, lg_ORDER_ID, filtre);
    lstTOrderDetail = OorderManagement.getOrderDetail(search_value, lg_ORDER_ID, filtre, start, limit);

%>



<%    JSONArray arrayObj = new JSONArray();
    for (TOrderDetail o : lstTOrderDetail) {
        try {
            OdataManager.getEm().refresh(o);

        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        OTFamillestock = OtellerManagement.geProductItemStock(o.getLgFAMILLEID().getLgFAMILLEID());
        OTFamilleGrossiste = OfamilleGrossisteManagement.findGrossiste(o.getLgFAMILLEID(), o.getLgORDERID().getLgGROSSISTEID());
        json.put("lg_ORDERDETAIL_ID", o.getLgORDERDETAILID());
        json.put("lg_ORDER_ID", o.getLgORDERID().getLgORDERID());
        json.put("int_NUMBER", o.getIntNUMBER());
        json.put("int_PRICE", o.getIntPRICE());
        json.put("int_QTE_MANQUANT", o.getIntQTEMANQUANT());
        json.put("lg_GROSSISTE_ID", o.getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("lg_GROSSISTE_LIBELLE", o.getLgORDERID().getLgGROSSISTEID().getStrLIBELLE());
        //json.put("lg_GROSSISTE_ID", o.getLgGROSSISTEID().getLgGROSSISTEID());

        // bool_BL
        json.put("bool_BL", o.getBoolBL());

        json.put("lg_FAMILLE_ID", o.getLgFAMILLEID().getLgFAMILLEID());
        json.put("lg_FAMILLE_NAME", o.getLgFAMILLEID().getStrNAME());
        json.put("lg_FAMILLE_CIP", (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : o.getLgFAMILLEID().getIntCIP()));
        json.put("lg_FAMILLE_PRIX_VENTE", o.getIntPRICEDETAIL());
        json.put("lg_FAMILLE_PRIX_ACHAT", o.getLgFAMILLEID().getIntPAF());
        json.put("int_PAF", o.getIntPAFDETAIL());
        json.put("int_PRICE_MACHINE", o.getLgFAMILLEID().getIntPRICE());
        json.put("int_PRIX_REFERENCE", o.getLgFAMILLEID().getIntPRICETIPS());
        json.put("int_QTE_LIVRE", o.getIntNUMBER() - o.getIntQTEMANQUANT());
        json.put("int_QTE_REP_GROSSISTE", o.getIntQTEREPGROSSISTE());
        json.put("prixDiff", o.getIntPRICEDETAIL().compareTo(o.getLgFAMILLEID().getIntPRICE()) != 0);

        json.put("int_SEUIL", o.getLgFAMILLEID().getIntSEUILMIN());

        int int_QTE_REASSORT = 0;
        try {
            int_QTE_REASSORT = OTFamillestock.getIntNUMBERAVAILABLE() - o.getLgFAMILLEID().getIntSEUILMIN();

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

        json.put("str_STATUT", o.getStrSTATUT());

        dt_CREATED = o.getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = o.getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + total + " \" ,\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>