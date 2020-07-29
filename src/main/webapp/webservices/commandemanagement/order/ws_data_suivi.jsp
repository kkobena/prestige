<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TOrder"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>

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
    String lg_ORDER_ID = "%%", str_REF_ORDER = "%%", lg_GROSSISTE_ID = "%%", str_STATUT = "is_Process";
    int int_LINE;
    String dt_DEBUT = "", dt_FIN = "";
   
    date key = new date();
    List<dal.TOrder> lstTOrder = null;
    Date dt_CREATED, dt_UPDATED;
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans webservices.commandemanagement.order.ws_data_suivi");
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

<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }
    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    if (request.getParameter("lg_GROSSISTE_ID") != null && request.getParameter("lg_GROSSISTE_ID") != "") {
        new logger().OCategory.info("lg_GROSSISTE_ID " + request.getParameter("lg_GROSSISTE_ID"));
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
    }

    if (request.getParameter("lg_ORDER_ID") != null) {
        if (request.getParameter("lg_ORDER_ID").toString().equals("ALL")) {
            lg_ORDER_ID = "%%";
        } else {
            lg_ORDER_ID = request.getParameter("lg_ORDER_ID").toString();
        }

    }
    new logger().OCategory.info("str_STATUT   " + str_STATUT);

    lstTOrder = new ArrayList<dal.TOrder>();

    OdataManager.initEntityManager();
    orderManagement OorderManagement = new orderManagement(OdataManager, OTUser);
    
    lstTOrder = OorderManagement.listeOrderBySearch(Os_Search_poste.getOvalue(), lg_GROSSISTE_ID, dt_DEBUT, dt_FIN);
    new logger().OCategory.info(lstTOrder.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTOrder.size()) {
            DATA_PER_PAGE = lstTOrder.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTOrder.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTOrder.get(i));

        } catch (Exception er) {
        }

        List<dal.TOrderDetail> lstTOrderDetail;

        lg_ORDER_ID = lstTOrder.get(i).getLgORDERID();

       // orderManagement OorderManagement = new orderManagement(OdataManager, OTUser);

        int nb = 0;
        int PRIX_ACHAT_TOTAL = 0, PRIX_VENTE_TOTAL = 0;
        String NAME = "";

        String str_Product = "";

        if (lstTOrder.get(i).getStrSTATUT().equalsIgnoreCase(commonparameter.statut_is_Process)
                || lstTOrder.get(i).getStrSTATUT().equalsIgnoreCase(commonparameter.orderIsPassed)
                || lstTOrder.get(i).getStrSTATUT().equalsIgnoreCase(commonparameter.statut_is_Closed)) {

            str_STATUT = lstTOrder.get(i).getStrSTATUT();
            new logger().OCategory.info("str_STATUT " + str_STATUT);

            lstTOrderDetail = OorderManagement.getTOrderDetail(lg_ORDER_ID, str_STATUT);
            int_LINE = lstTOrderDetail.size();

            for (int k = 0; k < lstTOrderDetail.size(); k++) {

                NAME = lstTOrderDetail.get(k).getLgFAMILLEID().getStrNAME();
                int num = lstTOrderDetail.get(k).getIntNUMBER();

                if (NAME != null) {
                    str_Product = "<b>" + NAME + " :  (" + num + ")</b><br> " + str_Product;

                    nb = nb + lstTOrderDetail.get(k).getIntNUMBER();

                    PRIX_ACHAT_TOTAL = PRIX_ACHAT_TOTAL + lstTOrderDetail.get(k).getLgFAMILLEID().getIntPAF();

                    PRIX_VENTE_TOTAL = PRIX_VENTE_TOTAL + lstTOrderDetail.get(k).getLgFAMILLEID().getIntPRICE();
                }

            }

        }

        new logger().OCategory.info("Sortie controle str_STATUT " + str_STATUT);

        new logger().OCategory.info("str_Product   ----  " + str_Product);

        JSONObject json = new JSONObject();

        json.put("lg_ORDER_ID", lstTOrder.get(i).getLgORDERID());
        json.put("str_REF_ORDER", lstTOrder.get(i).getStrREFORDER());
        json.put("int_LINE", int_LINE);
        json.put("lg_GROSSISTE_ID", lstTOrder.get(i).getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("str_GROSSISTE_LIBELLE", lstTOrder.get(i).getLgGROSSISTEID().getStrLIBELLE());

        json.put("str_FAMILLE_ITEM", str_Product);

        json.put("int_NBRE_PRODUIT", nb);

        //PRIX_ACHAT_TOTAL
        json.put("PRIX_ACHAT_TOTAL", PRIX_ACHAT_TOTAL);

        // PRIX_VENTE_TOTAL
        json.put("PRIX_VENTE_TOTAL", PRIX_VENTE_TOTAL);

        json.put("str_STATUT", lstTOrder.get(i).getStrSTATUT());

        dt_CREATED = lstTOrder.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTOrder.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTOrder.size() + " \" ,\"results\":" + arrayObj.toString() + "})";
   
%>

<%= result%>