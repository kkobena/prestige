<%@page import="toolkits.utils.jdom"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();

    List<TPreenregistrementCompteClientTiersPayent> lstTPreenregistrementCompteClientTiersPayent = new ArrayList<TPreenregistrementCompteClientTiersPayent>();


%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data tiers payant ");
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


<%    String lg_PREENREGISTREMENT_ID = "%%", search_value = "", lg_TIERS_PAYANT_ID = "%%", lg_TYPE_TIERS_PAYANT_ID = "%%";
    String str_STATUT = commonparameter.statut_is_Closed;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager, OTUser);

    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID").toString();
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT").toString();
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }

    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TIERS_PAYANT_ID  " + lg_TIERS_PAYANT_ID);
    }

    if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TYPE_TIERS_PAYANT_ID  " + lg_TYPE_TIERS_PAYANT_ID);
    }

    OdataManager.initEntityManager();
    lstTPreenregistrementCompteClientTiersPayent = OtierspayantManagement.ShowAllOrOneTierspayantByVente(search_value, lg_TIERS_PAYANT_ID, lg_TYPE_TIERS_PAYANT_ID, lg_PREENREGISTREMENT_ID, str_STATUT);

%>
<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTPreenregistrementCompteClientTiersPayent.size()) {
            DATA_PER_PAGE = lstTPreenregistrementCompteClientTiersPayent.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTPreenregistrementCompteClientTiersPayent.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID());
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        // lg_TIERS_PAYANT_ID
        json.put("lg_TIERS_PAYANT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        // str_CODE_ORGANISME
        json.put("str_CODE_ORGANISME", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrCODEORGANISME());
        // str_NAME
        json.put("str_NAME", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME());
        // str_FULLNAME
        json.put("str_FULLNAME", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrFULLNAME());
        // str_ADRESSE
        json.put("str_ADRESSE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrADRESSE());
        // str_MOBILE
        json.put("str_MOBILE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrMOBILE());
        // str_TELEPHONE
        json.put("str_TELEPHONE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrTELEPHONE());
        // str_MAIL
        json.put("str_MAIL", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrMAIL());
        // dbl_PLAFOND_CREDIT
        json.put("dbl_PLAFOND_CREDIT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getDblPLAFONDCREDIT());
        // dbl_TAUX_REMBOURSEMENT (à associer à la table TRembourcement
        json.put("dbl_TAUX_REMBOURSEMENT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getDblTAUXREMBOURSEMENT());
        // str_NUMERO_CAISSE_OFFICIEL
        json.put("str_NUMERO_CAISSE_OFFICIEL", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNUMEROCAISSEOFFICIEL());
        // str_CENTRE_PAYEUR
        json.put("str_CENTRE_PAYEUR", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrCENTREPAYEUR());
        // str_CODE_REGROUPEMENT
        json.put("str_CODE_REGROUPEMENT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrCODEREGROUPEMENT());
        // dbl_SEUIL_MINIMUM
        json.put("dbl_SEUIL_MINIMUM", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getDblSEUILMINIMUM());
        // bool_INTERDICTION
        json.put("bool_INTERDICTION", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getBoolINTERDICTION());
        // str_CODE_COMPTABLE
        json.put("str_CODE_COMPTABLE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrCODECOMPTABLE());
        // bool_PRENUM_FACT_SUBROGATOIRE
        json.put("bool_PRENUM_FACT_SUBROGATOIRE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getBoolPRENUMFACTSUBROGATOIRE());
        // int_NUMERO_DECOMPTE 
        json.put("int_NUMERO_DECOMPTE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getIntNUMERODECOMPTE());
        // str_CODE_PAIEMENT
        json.put("str_CODE_PAIEMENT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrCODEPAIEMENT());
        // dt_DELAI_PAIEMENT
        json.put("dt_DELAI_PAIEMENT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getDtDELAIPAIEMENT());
        // dbl_POURCENTAGE_REMISE
        json.put("dbl_POURCENTAGE_REMISE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getDblPOURCENTAGEREMISE());
        // dbl_REMISE_FORFETAIRE 
        json.put("dbl_REMISE_FORFETAIRE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getDblREMISEFORFETAIRE());
        // str_CODE_EDIT_BORDEREAU
        json.put("str_CODE_EDIT_BORDEREAU", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrCODEEDITBORDEREAU());
        // int_NBRE_EXEMPLAIRE_BORD
        json.put("int_NBRE_EXEMPLAIRE_BORD", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getIntNBREEXEMPLAIREBORD());
        // int_PERIODICITE_EDIT_BORD
        json.put("int_PERIODICITE_EDIT_BORD", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getIntPERIODICITEEDITBORD());
        // int_DATE_DERNIERE_EDITION
        json.put("int_DATE_DERNIERE_EDITION", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getIntDATEDERNIEREEDITION());
        // str_NUMERO_IDF_ORGANISME
        json.put("str_NUMERO_IDF_ORGANISME", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNUMEROIDFORGANISME());
        // dbl_MONTANT_F_CLIENT
        json.put("dbl_MONTANT_F_CLIENT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getDblMONTANTFCLIENT());
        // dbl_BASE_REMISE
        json.put("dbl_BASE_REMISE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getDblBASEREMISE());
        // str_CODE_DOC_COMPTOIRE
        json.put("str_CODE_DOC_COMPTOIRE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrCODEDOCCOMPTOIRE());
        // bool_ENABLED
        json.put("bool_ENABLED", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getBoolENABLED());

        json.put("lg_CUSTOMER_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        json.put("str_LIBELLE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME());

        // lg_VILLE_ID
        try {
            json.put("lg_VILLE_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgVILLEID().getStrName());
        } catch (Exception e) {

        }
        // lg_TYPE_TIERS_PAYANT_ID
        try {
            json.put("lg_TYPE_TIERS_PAYANT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTYPETIERSPAYANTID().getStrLIBELLETYPETIERSPAYANT());
        } catch (Exception e) {

        }
        // lg_TYPE_CONTRAT_ID
        try {
            json.put("lg_TYPE_CONTRAT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTYPECONTRATID().getStrLIBELLETYPECONTRAT());
        } catch (Exception e) {

        }
        // lg_RISQUE_ID
        try {
            json.put("lg_RISQUE_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgRISQUEID().getStrLIBELLERISQUE());
        } catch (Exception e) {

        }

        //lg_REGIMECAISSE_ID
        try {
            json.put("lg_REGIMECAISSE_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgREGIMECAISSEID().getStrCODEREGIMECAISSE());
        } catch (Exception e) {

        }

        json.put("str_STATUT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrSTATUT());
        json.put("str_PHOTO", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrPHOTO());
        json.put("int_PRICE_VENTE", lstTPreenregistrementCompteClientTiersPayent.get(i).getIntPRICE());
        json.put("str_CLIENT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME() + " " 
                + lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());
        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTPreenregistrementCompteClientTiersPayent.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>