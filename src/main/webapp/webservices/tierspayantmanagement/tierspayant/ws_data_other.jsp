<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TTiersPayant"  %>
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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    Date dt_CREATED, dt_UPDATED;
    json Ojson = new json();
    date key = new date();
    List<TTiersPayant> lstTTiersPayant = new ArrayList<TTiersPayant>();

%>

<%
    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;
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

<%    String lg_TIERS_PAYANT_ID = "%%", search_value = "", lg_TYPE_TIERS_PAYANT_ID = "%%";

    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TIERS_PAYANT_ID  = " + lg_TIERS_PAYANT_ID);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value  = " + search_value);
    }
     if (request.getParameter("query") != null) {
        search_value = request.getParameter("query").toString();
        new logger().OCategory.info("search_value  = " + search_value);
    }
    if (request.getParameter("cmb_TYPE_TIERS_PAYANT") != null) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("cmb_TYPE_TIERS_PAYANT").toString();
        new logger().OCategory.info("cmb_TYPE_TIERS_PAYANT  = " + lg_TYPE_TIERS_PAYANT_ID);
    }

    OdataManager.initEntityManager();
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
    lstTTiersPayant = OtierspayantManagement.ShowAllOrOneTierspayant(search_value, lg_TIERS_PAYANT_ID, lg_TYPE_TIERS_PAYANT_ID);
   /* lstTTiersPayant = OdataManager.getEm().createQuery("SELECT t FROM TTiersPayant t WHERE t.lgTIERSPAYANTID LIKE ?1 AND t.strCODEORGANISME LIKE ?2 AND t.strSTATUT LIKE ?3 AND t.lgTYPETIERSPAYANTID.lgTYPETIERSPAYANTID LIKE ?4 ").
            setParameter(1, lg_TIERS_PAYANT_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, commonparameter.statut_enable)
            .setParameter(4, lg_TYPE_TIERS_PAYANT_ID)
            .getResultList();
    new logger().OCategory.info(lstTTiersPayant.size());*/
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTTiersPayant.size()) {
            DATA_PER_PAGE = lstTTiersPayant.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTiersPayant.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTTiersPayant.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        // lg_TIERS_PAYANT_ID
        json.put("lg_TIERS_PAYANT_ID", lstTTiersPayant.get(i).getLgTIERSPAYANTID());
        // str_CODE_ORGANISME
        json.put("str_CODE_ORGANISME", lstTTiersPayant.get(i).getStrCODEORGANISME());
        // str_NAME
        json.put("str_NAME", lstTTiersPayant.get(i).getStrNAME());
        // str_FULLNAME
        json.put("str_FULLNAME", lstTTiersPayant.get(i).getStrFULLNAME());
        // str_ADRESSE
        json.put("str_ADRESSE", lstTTiersPayant.get(i).getStrADRESSE());
        // str_MOBILE
        json.put("str_MOBILE", lstTTiersPayant.get(i).getStrMOBILE());
        // str_TELEPHONE
        json.put("str_TELEPHONE", lstTTiersPayant.get(i).getStrTELEPHONE());
        // str_MAIL
        json.put("str_MAIL", lstTTiersPayant.get(i).getStrMAIL());
        // dbl_PLAFOND_CREDIT
        json.put("dbl_PLAFOND_CREDIT", lstTTiersPayant.get(i).getDblPLAFONDCREDIT());
        // dbl_TAUX_REMBOURSEMENT (à associer à la table TRembourcement
        json.put("dbl_TAUX_REMBOURSEMENT", lstTTiersPayant.get(i).getDblTAUXREMBOURSEMENT());
        // str_NUMERO_CAISSE_OFFICIEL
        json.put("str_NUMERO_CAISSE_OFFICIEL", lstTTiersPayant.get(i).getStrNUMEROCAISSEOFFICIEL());
        // str_CENTRE_PAYEUR
        json.put("str_CENTRE_PAYEUR", lstTTiersPayant.get(i).getStrCENTREPAYEUR());
        // str_CODE_REGROUPEMENT
        json.put("str_CODE_REGROUPEMENT", lstTTiersPayant.get(i).getStrCODEREGROUPEMENT());
        // dbl_SEUIL_MINIMUM
        json.put("dbl_SEUIL_MINIMUM", lstTTiersPayant.get(i).getDblSEUILMINIMUM());
        // bool_INTERDICTION
        json.put("bool_INTERDICTION", lstTTiersPayant.get(i).getBoolINTERDICTION());
        // str_CODE_COMPTABLE
        json.put("str_CODE_COMPTABLE", lstTTiersPayant.get(i).getStrCODECOMPTABLE());
        // bool_PRENUM_FACT_SUBROGATOIRE
        json.put("bool_PRENUM_FACT_SUBROGATOIRE", lstTTiersPayant.get(i).getBoolPRENUMFACTSUBROGATOIRE());
        // int_NUMERO_DECOMPTE 
        json.put("int_NUMERO_DECOMPTE", lstTTiersPayant.get(i).getIntNUMERODECOMPTE());
        // str_CODE_PAIEMENT
        json.put("str_CODE_PAIEMENT", lstTTiersPayant.get(i).getStrCODEPAIEMENT());
        // dt_DELAI_PAIEMENT
        json.put("dt_DELAI_PAIEMENT", lstTTiersPayant.get(i).getDtDELAIPAIEMENT());
        // dbl_POURCENTAGE_REMISE
        json.put("dbl_POURCENTAGE_REMISE", lstTTiersPayant.get(i).getDblPOURCENTAGEREMISE());
        // dbl_REMISE_FORFETAIRE 
        json.put("dbl_REMISE_FORFETAIRE", lstTTiersPayant.get(i).getDblREMISEFORFETAIRE());
        // str_CODE_EDIT_BORDEREAU
        json.put("str_CODE_EDIT_BORDEREAU", lstTTiersPayant.get(i).getStrCODEEDITBORDEREAU());
        // int_NBRE_EXEMPLAIRE_BORD
        json.put("int_NBRE_EXEMPLAIRE_BORD", lstTTiersPayant.get(i).getIntNBREEXEMPLAIREBORD());
        // int_PERIODICITE_EDIT_BORD
        json.put("int_PERIODICITE_EDIT_BORD", lstTTiersPayant.get(i).getIntPERIODICITEEDITBORD());
        // int_DATE_DERNIERE_EDITION
        json.put("int_DATE_DERNIERE_EDITION", lstTTiersPayant.get(i).getIntDATEDERNIEREEDITION());
        // str_NUMERO_IDF_ORGANISME
        json.put("str_NUMERO_IDF_ORGANISME", lstTTiersPayant.get(i).getStrNUMEROIDFORGANISME());
        // dbl_MONTANT_F_CLIENT
        json.put("dbl_MONTANT_F_CLIENT", lstTTiersPayant.get(i).getDblMONTANTFCLIENT());
        // dbl_BASE_REMISE
        json.put("dbl_BASE_REMISE", lstTTiersPayant.get(i).getDblBASEREMISE());
        // str_CODE_DOC_COMPTOIRE
        json.put("str_CODE_DOC_COMPTOIRE", lstTTiersPayant.get(i).getStrCODEDOCCOMPTOIRE());
        // bool_ENABLED
        json.put("bool_ENABLED", lstTTiersPayant.get(i).getBoolENABLED());

        json.put("lg_CUSTOMER_ID", lstTTiersPayant.get(i).getLgTIERSPAYANTID());
        json.put("str_LIBELLE", lstTTiersPayant.get(i).getStrNAME());

        // lg_VILLE_ID
        try {
            json.put("lg_VILLE_ID", lstTTiersPayant.get(i).getLgVILLEID().getStrName());
        } catch (Exception e) {

        }
        // lg_TYPE_TIERS_PAYANT_ID
        try {
            json.put("lg_TYPE_TIERS_PAYANT_ID", lstTTiersPayant.get(i).getLgTYPETIERSPAYANTID().getStrLIBELLETYPETIERSPAYANT());
        } catch (Exception e) {

        }
        // lg_TYPE_CONTRAT_ID
        try {
            json.put("lg_TYPE_CONTRAT_ID", lstTTiersPayant.get(i).getLgTYPECONTRATID().getStrLIBELLETYPECONTRAT());
        } catch (Exception e) {

        }
        // lg_RISQUE_ID
        try {
            json.put("lg_RISQUE_ID", lstTTiersPayant.get(i).getLgRISQUEID().getStrLIBELLERISQUE());
        } catch (Exception e) {

        }

        //lg_REGIMECAISSE_ID
        try {
            json.put("lg_REGIMECAISSE_ID", lstTTiersPayant.get(i).getLgREGIMECAISSEID().getStrCODEREGIMECAISSE());
        } catch (Exception e) {

        }

        json.put("str_STATUT", lstTTiersPayant.get(i).getStrSTATUT());
        json.put("str_PHOTO", lstTTiersPayant.get(i).getStrPHOTO());

        dt_CREATED = lstTTiersPayant.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTTiersPayant.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    
    JSONObject json = new JSONObject();
    json.put("lg_TIERS_PAYANT_ID", "");
    json.put("str_FULLNAME", "Tous");
    arrayObj.put(json);
    
    String result = "({\"total\":\"" + lstTTiersPayant.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>