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

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans webservices commande passée*****");
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

<%    String lg_ORDER_ID = "%%", str_REF_ORDER = "%%", lg_GROSSISTE_ID = "%%", str_STATUT = "passed", search_value = "";
    int int_LINE;
    List<TOrder> lstTOrder = new ArrayList<>();
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
    
    if (request.getParameter("lg_ORDER_ID") != null) {
        if (request.getParameter("lg_ORDER_ID").toString().equals("ALL")) {
            lg_ORDER_ID = "%%";
        } else {
            lg_ORDER_ID = request.getParameter("lg_ORDER_ID").toString();
        }
        
    }
    
    new logger().OCategory.info("str_STATUT   " + str_STATUT);
    TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    orderManagement OorderManagement = new orderManagement(OdataManager, user);
    familleGrossisteManagement OfamilleGrossisteManagement = new familleGrossisteManagement(OdataManager);
    OfamilleGrossisteManagement.setOTUser(user);
    // lstTOrder = OorderManagement.listeOrder(search_value, commonparameter.orderIsPassed);
    lstTOrder = OorderManagement.listeOrder(search_value);
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

        /*List<dal.TOrderDetail> lstTOrderDetail;

         lg_ORDER_ID = lstTOrder.get(i).getLgORDERID();

     
         lstTOrderDetail = OorderManagement.getTOrderDetail(lg_ORDER_ID,str_STATUT);
         int nb = 0;
         int_LINE = lstTOrderDetail.size();
         int PRIX_ACHAT_TOTAL = 0, PRIX_VENTE_TOTAL =0;
         String NAME = "";*/
        int nb = 0, int_TOTAL_ACHAT = 0, int_TOTAL_VENTE = 0;
        
        List<TOrderDetail> lstTOrderDetail = new ArrayList<>();
        lstTOrderDetail = OorderManagement.getTOrderDetail(lstTOrder.get(i).getLgORDERID(), str_STATUT);
        
        String str_Product = "";
        
        for (int k = 0; k < lstTOrderDetail.size(); k++) {
            //  new logger().OCategory.info("id++++"+lstTOrderDetail.get(k).getLgFAMILLEID().getLgFAMILLEID());
            OTFamilleGrossiste = OfamilleGrossisteManagement.findFamilleGrossiste(lstTOrderDetail.get(k).getLgFAMILLEID().getLgFAMILLEID(), lstTOrderDetail.get(k).getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
            str_Product = "<b><span style='display:inline-block;width: 7%;'>" + (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTOrderDetail.get(k).getLgFAMILLEID().getIntCIP()) + "</span><span style='display:inline-block;width: 25%;'>" + lstTOrderDetail.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + lstTOrderDetail.get(k).getIntQTEREPGROSSISTE() + ")</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTOrderDetail.get(k).getIntPAFDETAIL(), '.') + " F CFA " + "</span></b><br> " + str_Product;
            //    str_Product = "<b>" + lstTOrderDetail.get(k).getLgFAMILLEID().getIntCIP() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + lstTOrderDetail.get(k).getLgFAMILLEID().getStrNAME() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + conversion.AmountFormat(lstTOrderDetail.get(k).getIntPAFDETAIL(), commonparameter.CHAR_SEPARATEUR_POINT) + " F CFA&nbsp;&nbsp;" + " :  (" + lstTOrderDetail.get(k).getIntQTEREPGROSSISTE() + ")</b><br> " + str_Product;

            nb = nb + lstTOrderDetail.get(k).getIntQTEREPGROSSISTE();
            
        }
        
        JSONObject json = new JSONObject();
        
        json.put("lg_ORDER_ID", lstTOrder.get(i).getLgORDERID());
        json.put("str_REF_ORDER", lstTOrder.get(i).getStrREFORDER());
        json.put("int_LINE", lstTOrderDetail.size());
        json.put("lg_GROSSISTE_ID", lstTOrder.get(i).getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("str_GROSSISTE_LIBELLE", lstTOrder.get(i).getLgGROSSISTEID().getStrLIBELLE());
        json.put("lg_USER_ID", lstTOrder.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTOrder.get(i).getLgUSERID().getStrLASTNAME());
        
        json.put("str_FAMILLE_ITEM", str_Product);
        
        json.put("int_NBRE_PRODUIT", nb);
        int_TOTAL_ACHAT = OorderManagement.getPriceTotalAchat(lstTOrderDetail);
        int_TOTAL_VENTE = OorderManagement.getPriceTotalVente(lstTOrderDetail);
        
        json.put("PRIX_ACHAT_TOTAL", int_TOTAL_ACHAT);
        json.put("PRIX_VENTE_TOTAL", int_TOTAL_VENTE);
        
        json.put("str_STATUT", lstTOrder.get(i).getStrSTATUT());
        json.put("dt_CREATED", date.DateToString(lstTOrder.get(i).getDtUPDATED(), date.formatterShort));
        json.put("dt_UPDATED", date.DateToString(lstTOrder.get(i).getDtUPDATED(), date.NomadicUiFormat_Time));
        
        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTOrder.size() + " \" ,\"results\":" + arrayObj.toString() + "})";
    
%>

<%= result%>