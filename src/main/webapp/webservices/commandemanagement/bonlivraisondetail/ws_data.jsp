<%@page import="dal.TParameters"%>
<%@page import="bll.configManagement.familleGrossisteManagement"%>
<%@page import="bll.commandeManagement.bonLivraisonManagement"%>
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
    List<dal.TBonLivraisonDetail> lstTBonLivraisonDetail = new ArrayList<dal.TBonLivraisonDetail>();
%>

<%
    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data etiquette -----");
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
    String lg_BON_LIVRAISON_ID = "%%", search_value = "", str_TYPE_TRANSACTION = "ALL";
    boolean bool_CHECKEXPIRATIONDATE = false;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    TFamilleGrossiste OTFamilleGrossiste = null;
    OdataManager.initEntityManager();

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_TYPE_TRANSACTION") != null && !request.getParameter("str_TYPE_TRANSACTION").equalsIgnoreCase("")) {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        
    }

    if (request.getParameter("lg_BON_LIVRAISON_ID") != null) {
        if (request.getParameter("lg_BON_LIVRAISON_ID").toString().equals("ALL")) {
            lg_BON_LIVRAISON_ID = "%%";
        } else {
            lg_BON_LIVRAISON_ID = request.getParameter("lg_BON_LIVRAISON_ID").toString();
        }

    }
    if (request.getParameter("bool_CHECKEXPIRATIONDATE") != null) {
        bool_CHECKEXPIRATIONDATE = Boolean.parseBoolean(request.getParameter("bool_CHECKEXPIRATIONDATE"));
        new logger().OCategory.info("bool_CHECKEXPIRATIONDATE  " + bool_CHECKEXPIRATIONDATE);
    }
    new logger().OCategory.info("lg_BON_LIVRAISON_ID " + lg_BON_LIVRAISON_ID);

    familleManagement OfamilleManagement = new familleManagement(OdataManager);

    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);

  
    bonLivraisonManagement ObonLivraisonManagement = new bonLivraisonManagement(OdataManager, OTUser);

    // lstTBonLivraisonDetail = ObonLivraisonManagement.getTBonLivraisonDetailBis(search_value, lg_BON_LIVRAISON_ID); // ancienne version
    lstTBonLivraisonDetail = ObonLivraisonManagement.getTBonLivraisonDetailBis(search_value, lg_BON_LIVRAISON_ID, str_TYPE_TRANSACTION, bool_CHECKEXPIRATIONDATE);
    familleGrossisteManagement OfamilleGrossisteManagement = new familleGrossisteManagement(OdataManager);
    TParameters OParameters = OdataManager.getEm().find(TParameters.class, "KEY_ACTIVATE_PEREMPTION_DATE");
    OdataManager.getEm().refresh(OParameters);

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
        OTFamilleGrossiste = OfamilleGrossisteManagement.findFamilleGrossiste(lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getLgFAMILLEID(), lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getLgORDERID().getLgGROSSISTEID().getLgGROSSISTEID());

        json.put("lg_BON_LIVRAISON_DETAIL", lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONDETAIL());

        json.put("int_QTE_CMDE", lstTBonLivraisonDetail.get(i).getIntQTECMDE());
        json.put("int_QTE_RECUE_REEL", (lstTBonLivraisonDetail.get(i).getIntQTERECUE() > 0 ? lstTBonLivraisonDetail.get(i).getIntQTERECUE() - lstTBonLivraisonDetail.get(i).getIntQTEUG() : "-"));
        json.put("int_QTE_RECUE_BIS", (lstTBonLivraisonDetail.get(i).getIntQTERECUE() > 0 ? lstTBonLivraisonDetail.get(i).getIntQTERECUE() - lstTBonLivraisonDetail.get(i).getIntQTEUG() : -1));
        json.put("str_LIVRAISON_ADP", lstTBonLivraisonDetail.get(i).getStrLIVRAISONADP());
        json.put("str_MANQUE_FORCES", lstTBonLivraisonDetail.get(i).getStrMANQUEFORCES());
        json.put("str_ETAT_ARTICLE", lstTBonLivraisonDetail.get(i).getStrETATARTICLE());
        json.put("int_PRIX_REFERENCE", lstTBonLivraisonDetail.get(i).getIntPRIXREFERENCE());
        json.put("int_PRIX_VENTE", lstTBonLivraisonDetail.get(i).getIntPRIXVENTE());

        json.put("int_PAF", lstTBonLivraisonDetail.get(i).getIntPAF());
        json.put("int_PA_REEL", lstTBonLivraisonDetail.get(i).getIntPAREEL());
        json.put("lg_FAMILLE_PRIX_ACHAT", lstTBonLivraisonDetail.get(i).getIntQTEUG());

        json.put("lg_FAMILLE_ID", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("lg_FAMILLE_NAME", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getStrNAME());
        // json.put("lg_FAMILLE_CIP", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntCIP());

        json.put("lg_FAMILLE_CIP", (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntCIP()));

        json.put("str_REF_LIVRAISON", lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getStrREFLIVRAISON());
        // json.put("int_QTE_MANQUANT", lstTBonLivraisonDetail.get(i).getIntQTECMDE() - lstTBonLivraisonDetail.get(i).getIntQTERECUE());
        // json.put("int_QTE_MANQUANT", (lstTBonLivraisonDetail.get(i).getIntQTEMANQUANT() >= 0 ? lstTBonLivraisonDetail.get(i).getIntQTEMANQUANT() : 0));
        json.put("int_SEUIL", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntSEUILMIN());

        // dbl_PRIX_MOYEN_PONDERE
        json.put("dbl_PRIX_MOYEN_PONDERE", (lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getDblPRIXMOYENPONDERE() != null ? lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getDblPRIXMOYENPONDERE() : 0));

        try {

            TFamille OTFamille = lstTBonLivraisonDetail.get(i).getLgFAMILLEID();

            TFamilleStock OTFamilleStock = OtellerManagement.getTProductItemStock(OTFamille.getLgFAMILLEID());

            int QTE_STOCK = OTFamilleStock.getIntNUMBERAVAILABLE();
            json.put("lg_FAMILLE_QTE_STOCK", QTE_STOCK);
             json.put("prixDiff", lstTBonLivraisonDetail.get(i).getIntPRIXVENTE().compareTo(OTFamille.getIntPRICE())!=0);

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
            TFamille OTFamille = OfamilleManagement.getFamilleDecondition(lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntCIP());
            json.put("lg_FAMILLE_DECONDITION_ID", OTFamille.getLgFAMILLEID());
            json.put("str_DESCRIPTION_DECONDITION", OTFamille.getStrDESCRIPTION());
            int int_NUMBER_AVAILABLE_DECONDITION = 0;
            try {
                int_NUMBER_AVAILABLE_DECONDITION = OtellerManagement.getTProductItemStock(OTFamille.getLgFAMILLEID()).getIntNUMBERAVAILABLE();
            } catch (Exception e) {
            }
            json.put("int_NUMBER_AVAILABLE_DECONDITION", int_NUMBER_AVAILABLE_DECONDITION);
        } catch (Exception e) {

        }

        json.put("lg_FAMILLE_CIP", (OTFamilleGrossiste != null ? OTFamilleGrossiste.getStrCODEARTICLE() : lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntCIP()));
        json.put("int_NUMBERDETAIL", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntNUMBERDETAIL());
        json.put("bool_DECONDITIONNE", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getBoolDECONDITIONNE());
        json.put("bool_DECONDITIONNE_EXIST", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getBoolDECONDITIONNEEXIST());

        if ("1".equals(OParameters.getStrVALUE())) {
            json.put("int_QTE_MANQUANT", 0);
            json.put("checkExpirationdate", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getBoolCHECKEXPIRATIONDATE() == true ? false : true);
            json.put("DISPLAYFILTER", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getBoolCHECKEXPIRATIONDATE() == true ? false : true);
            json.put("int_QTE_RECUE", lstTBonLivraisonDetail.get(i).getIntQTECMDE());
            if (lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getBoolCHECKEXPIRATIONDATE()) {
                json.put("int_QTE_RECUE", (lstTBonLivraisonDetail.get(i).getIntQTERECUE() > 0 ? lstTBonLivraisonDetail.get(i).getIntQTERECUE() - lstTBonLivraisonDetail.get(i).getIntQTEUG() : "-"));
                json.put("intQTERECUE", (lstTBonLivraisonDetail.get(i).getIntQTERECUE() > 0 ? lstTBonLivraisonDetail.get(i).getIntQTERECUE() - lstTBonLivraisonDetail.get(i).getIntQTEUG() :0));
                if (lstTBonLivraisonDetail.get(i).getIntQTEMANQUANT() > lstTBonLivraisonDetail.get(i).getIntQTECMDE()) {
                    json.put("int_QTE_MANQUANT", lstTBonLivraisonDetail.get(i).getIntQTECMDE());
                } else {
                    json.put("int_QTE_MANQUANT", (lstTBonLivraisonDetail.get(i).getIntQTEMANQUANT() >= 0 ? lstTBonLivraisonDetail.get(i).getIntQTEMANQUANT() : 0));
                }

            }
        } else {
            json.put("checkExpirationdate", true);
            json.put("DISPLAYFILTER", true);
            json.put("int_QTE_RECUE", lstTBonLivraisonDetail.get(i).getIntQTECMDE());
            json.put("int_QTE_MANQUANT", 0);
        }
        arrayObj.put(json);
    }
    JSONObject result=new JSONObject();
    result.put("total", lstTBonLivraisonDetail.size()).put("results", arrayObj);
    //String result = "({\"total\":\"" + lstTBonLivraisonDetail.size() + " \",\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>