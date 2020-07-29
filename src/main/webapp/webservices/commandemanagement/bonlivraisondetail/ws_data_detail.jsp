<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TTypeStockFamille"%>
<%@page import="bll.commandeManagement.bonLivraisonManagement"%>
<%@page import="dal.TBonLivraisonDetail"%>
<%@page import="dal.TFamilleGrossiste"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="dal.TFamille"%>
<%@page import="dal.dataManager"  %>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();
    List<TBonLivraisonDetail> lstTBonLivraisonDetail = new ArrayList<TBonLivraisonDetail>();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data etiquette ");
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
    String lg_BON_LIVRAISON_ID = "%%", str_STATUT = commonparameter.statut_is_Closed;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    if (request.getParameter("lg_BON_LIVRAISON_ID") != null && request.getParameter("lg_BON_LIVRAISON_ID") != "") {
        lg_BON_LIVRAISON_ID = request.getParameter("lg_BON_LIVRAISON_ID");
        new logger().OCategory.info("lg_BON_LIVRAISON_ID " + lg_BON_LIVRAISON_ID);
    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }

    familleManagement OfamilleManagement = new familleManagement(OdataManager);
    bonLivraisonManagement ObonLivraisonManagement = new bonLivraisonManagement(OdataManager, OTUser);
    orderManagement OorderManagement = new orderManagement(OdataManager, OTUser);
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
    StockManager OStockManager = new StockManager(OdataManager, OTUser);
    lstTBonLivraisonDetail = ObonLivraisonManagement.getTBonLivraisonDetail(lg_BON_LIVRAISON_ID, str_STATUT);

%>   

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTBonLivraisonDetail.size()) {
            DATA_PER_PAGE = lstTBonLivraisonDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTBonLivraisonDetail.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTBonLivraisonDetail.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_BON_LIVRAISON_DETAIL", lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONDETAIL());

        json.put("int_QTE_CMDE", lstTBonLivraisonDetail.get(i).getIntQTECMDE());
        json.put("int_QTE_RECUE", lstTBonLivraisonDetail.get(i).getIntQTERECUE());

        json.put("str_LIVRAISON_ADP", lstTBonLivraisonDetail.get(i).getStrLIVRAISONADP());
        json.put("str_MANQUE_FORCES", lstTBonLivraisonDetail.get(i).getStrMANQUEFORCES());
        json.put("str_ETAT_ARTICLE", lstTBonLivraisonDetail.get(i).getStrETATARTICLE());
        json.put("int_PRIX_REFERENCE", lstTBonLivraisonDetail.get(i).getIntPRIXREFERENCE());
        json.put("int_PRIX_VENTE", lstTBonLivraisonDetail.get(i).getIntPRIXVENTE());

        json.put("int_PAF", lstTBonLivraisonDetail.get(i).getIntPAF());
        json.put("int_PA_REEL", lstTBonLivraisonDetail.get(i).getIntPAREEL());

        json.put("lg_FAMILLE_ID", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("lg_FAMILLE_NAME", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getStrNAME());
        json.put("lg_FAMILLE_CIP", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntCIP());
        json.put("lg_FAMILLE_PRIX_VENTE", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntPRICE());
        json.put("lg_FAMILLE_PRIX_ACHAT", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntPAF());
        json.put("str_REF_LIVRAISON", lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getStrREFLIVRAISON());
        // json.put("int_QTE_MANQUANT", lstTBonLivraisonDetail.get(i).getIntQTECMDE() - lstTBonLivraisonDetail.get(i).getIntQTERECUE());
        json.put("int_QTE_MANQUANT", lstTBonLivraisonDetail.get(i).getIntQTEMANQUANT());
        json.put("int_SEUIL", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntSEUILMIN());

        // dbl_PRIX_MOYEN_PONDERE
        json.put("dbl_PRIX_MOYEN_PONDERE", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getDblPRIXMOYENPONDERE());

        try {
            TTypeStockFamille OTTypeStockFamille = OStockManager.getTTypeStockFamilleByTypestock("1", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
            int int_NUMBER = 0;
            if (OTTypeStockFamille != null) {
                int_NUMBER = OTTypeStockFamille.getIntNUMBER();
            }
            json.put("lg_FAMILLE_QTE_STOCK", int_NUMBER);

        } catch (Exception E) {

        }

        try {
            json.put("lg_ZONE_GEO_ID", lstTBonLivraisonDetail.get(i).getLgZONEGEOID().getLgZONEGEOID());
            json.put("lg_ZONE_GEO_NAME", lstTBonLivraisonDetail.get(i).getLgZONEGEOID().getStrLIBELLEE());
        } catch (Exception e) {

        }
        try {
            json.put("lg_GROSSISTE_ID", lstTBonLivraisonDetail.get(i).getLgGROSSISTEID().getStrLIBELLE());
        } catch (Exception e) {
        }

        try {
            json.put("lg_BON_LIVRAISON_ID", lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getStrREFLIVRAISON());
            json.put("str_REF_ORDER", lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getLgORDERID().getStrREFORDER());
            json.put("dt_DATE_LIVRAISON", key.DateToString(lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getDtDATELIVRAISON(), key.formatterShort));
            json.put("str_STATUT", lstTBonLivraisonDetail.get(i).getStrSTATUT());
            json.put("dt_CREATED", key.DateToString(lstTBonLivraisonDetail.get(i).getDtCREATED(), key.formatterShort));
            json.put("dt_UPDATED", key.DateToString(lstTBonLivraisonDetail.get(i).getDtUPDATED(), key.formatterShort));
        } catch (Exception e) {
        }

        try {
            TFamilleGrossiste OTFamilleGrossiste = OorderManagement.getTProduct(lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getLgFAMILLEID(), lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());
            json.put("str_CODE_ARTICLE", OTFamilleGrossiste.getStrCODEARTICLE());

        } catch (Exception e) {

        }
        try {
            TFamille OTFamille = OfamilleManagement.getFamilleDecondition(lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntCIP());
            json.put("lg_FAMILLE_DECONDITION_ID", OTFamille.getLgFAMILLEID());
            json.put("str_DESCRIPTION_DECONDITION", OTFamille.getStrDESCRIPTION());
            int int_NUMBER_AVAILABLE_DECONDITION = 0;
            TFamilleStock OTFamilleStock = new tellerManagement(OdataManager).getTProductItemStock(OTFamille.getLgFAMILLEID());
            if (OTFamilleStock != null) {
                int_NUMBER_AVAILABLE_DECONDITION = OTFamilleStock.getIntNUMBERAVAILABLE();
            }

            json.put("int_NUMBER_AVAILABLE_DECONDITION", int_NUMBER_AVAILABLE_DECONDITION);
        } catch (Exception e) {
 
        }

        json.put("int_CIP", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntCIP());
        json.put("int_NUMBERDETAIL", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntNUMBERDETAIL());
        json.put("bool_DECONDITIONNE", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getBoolDECONDITIONNE());
        json.put("bool_DECONDITIONNE_EXIST", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getBoolDECONDITIONNEEXIST());

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTBonLivraisonDetail.size() + " \",\"results\":" + arrayObj.toString() + "})";
   
%>

<%= result%>