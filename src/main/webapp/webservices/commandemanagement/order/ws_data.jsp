<%@page import="dal.TFamilleGrossiste"%>
<%@page import="bll.configManagement.familleGrossisteManagement"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TOrderDetail"%>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TFamilleGrossiste OTFamilleGrossiste = null;
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans webservices.commandemanagement.order.ws_data ----");
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

<%    List<TOrder> lstTOrder = new ArrayList<TOrder>();
    String lg_ORDER_ID = "%%", str_REF_ORDER = "%%", lg_GROSSISTE_ID = "%%", str_STATUT = "is_Process", search_value = "";
    int int_LINE = 0;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_ORDER_ID") != null) {
        if (request.getParameter("lg_ORDER_ID").toString().equals("ALL")) {
            lg_ORDER_ID = "%%";
        } else {
            lg_ORDER_ID = request.getParameter("lg_ORDER_ID").toString();
        }

    }

    if (request.getParameter("str_STATUT") != null) {
        if (request.getParameter("str_STATUT").toString().equals("ALL")) {
            str_STATUT = "%%";
        } else {
            str_STATUT = request.getParameter("str_STATUT").toString();
        }

    }
    new logger().OCategory.info("str_STATUT   " + str_STATUT);

    orderManagement OorderManagement = new orderManagement(OdataManager, OTUser);
    familleGrossisteManagement OfamilleGrossisteManagement = new familleGrossisteManagement(OdataManager);
    /*lstTOrder = OdataManager.getEm().createQuery("SELECT t FROM TOrder t WHERE (t.strREFORDER LIKE ?1 OR t.lgGROSSISTEID.strLIBELLE LIKE ?2) AND t.strSTATUT LIKE ?3 ORDER BY t.dtCREATED DESC ")
     // .setParameter(1, lg_ORDER_ID)
     .setParameter(1, Os_Search_poste.getOvalue())
     .setParameter(2, Os_Search_poste.getOvalue())
     .setParameter(3, commonparameter.statut_is_Process)
     .getResultList();*/
    lstTOrder = OorderManagement.listeOrder(search_value, commonparameter.statut_is_Process);

%>

<%//Filtre de pagination
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

        JSONObject json = new JSONObject();

        //  OorderManagement.DetailCommande(lstTOrder.get(i).getLgORDERID(), json);
        json.put("lg_ORDER_ID", lstTOrder.get(i).getLgORDERID());
        json.put("str_REF_ORDER", lstTOrder.get(i).getStrREFORDER());
         String lg_USER_ID = "";
        if (lstTOrder.get(i).getLgUSERID() != null) {
            lg_USER_ID = lstTOrder.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTOrder.get(i).getLgUSERID().getStrLASTNAME();
        }
        json.put("lg_USER_ID", lg_USER_ID);
        json.put("lg_GROSSISTE_ID", lstTOrder.get(i).getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("str_GROSSISTE_LIBELLE", lstTOrder.get(i).getLgGROSSISTEID().getStrLIBELLE());
        // TELEPHONE
        json.put("str_GROSSISTE_TELEPHONE", lstTOrder.get(i).getLgGROSSISTEID().getStrTELEPHONE());
        json.put("str_GROSSISTE_MOBILE", lstTOrder.get(i).getLgGROSSISTEID().getStrMOBILE());
        // PHARMA_ML
        json.put("str_GROSSISTE_URLPHARMAML", lstTOrder.get(i).getLgGROSSISTEID().getStrURLPHARMAML());
        // PHARMA_ML
        json.put("str_GROSSISTE_URLEXTRANET", lstTOrder.get(i).getLgGROSSISTEID().getStrURLEXTRANET());
        
        json.put("str_STATUT", lstTOrder.get(i).getStrSTATUT());
        json.put("dt_CREATED", date.DateToString(lstTOrder.get(i).getDtUPDATED(), date.formatterShort));
        json.put("dt_UPDATED", date.DateToString(lstTOrder.get(i).getDtUPDATED(), date.NomadicUiFormat_Time));

        String str_Product = "";

        int nb = 0, int_TOTAL_ACHAT = 0, int_TOTAL_VENTE = 0;

        List<TOrderDetail> lstTOrderDetail = new ArrayList<TOrderDetail>();
        lstTOrderDetail = OorderManagement.getTOrderDetail("", lstTOrder.get(i).getLgORDERID(), lstTOrder.get(i).getStrSTATUT());

        /*  for (int k = 0; k < lstTOrderDetail.size(); k++) {
            //new logger().OCategory.info(" lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getStrNAME() @@@@  "+lstTSuggestionOrderDetails.get(k).getLgFAMILLEID().getLgFAMILLEID());
            str_Product = "<b>" + lstTOrderDetail.get(k).getLgFAMILLEID().getIntCIP() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + lstTOrderDetail.get(k).getLgFAMILLEID().getStrNAME() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + new conversion().AmountFormat(lstTOrderDetail.get(k).getLgFAMILLEID().getIntPRICE(), commonparameter.CHAR_SEPARATEUR_POINT) + " F CFA&nbsp;&nbsp;" + " :  (" + lstTOrderDetail.get(k).getIntQTEREPGROSSISTE() + ")</b><br> " + str_Product;

            nb = nb + lstTOrderDetail.get(k).getIntQTEREPGROSSISTE();

          
        }*/
        for (int k = 0; k < lstTOrderDetail.size(); k++) {
            OTFamilleGrossiste = OfamilleGrossisteManagement.findFamilleGrossiste(lstTOrderDetail.get(k).getLgFAMILLEID().getLgFAMILLEID(), lstTOrderDetail.get(k).getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
            str_Product = "<b><span style='display:inline-block;width: 7%;'>" + (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTOrderDetail.get(k).getLgFAMILLEID().getIntCIP()) + "</span><span style='display:inline-block;width: 25%;'>" + lstTOrderDetail.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + lstTOrderDetail.get(k).getIntQTEREPGROSSISTE() + ")</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTOrderDetail.get(k).getIntPAFDETAIL(), '.') + " F CFA " + "</span></b><br> " + str_Product;

            //str_Product = "<b><span style='display:inline-block;width: 7%;'>" + lstTOrderDetail.get(k).getLgFAMILLEID().getIntCIP() + "</span><span style='display:inline-block;width: 25%;'>" + lstTOrderDetail.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + lstTOrderDetail.get(k).getIntQTEREPGROSSISTE()+ ")</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTOrderDetail.get(k).getIntPAFDETAIL(), '.') + " F CFA " + "</span></b><br> " + str_Product;
            nb = nb + lstTOrderDetail.get(k).getIntQTEREPGROSSISTE();
        }
        lstTOrderDetail = OorderManagement.getTOrderDetail("", lstTOrder.get(i).getLgORDERID(), lstTOrder.get(i).getStrSTATUT());
        int_TOTAL_ACHAT = OorderManagement.getPriceTotalAchat(lstTOrderDetail);
        int_TOTAL_VENTE = OorderManagement.getPriceTotalVente(lstTOrderDetail);
        json.put("int_LINE", lstTOrderDetail.size());
        json.put("str_FAMILLE_ITEM", str_Product);
        json.put("int_NBRE_PRODUIT", nb);
        json.put("PRIX_ACHAT_TOTAL", int_TOTAL_ACHAT);
        json.put("PRIX_VENTE_TOTAL", int_TOTAL_VENTE);

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTOrder.size() + "\" ,\"results\":" + arrayObj.toString() + "})";
    
%> 

<%= result%>