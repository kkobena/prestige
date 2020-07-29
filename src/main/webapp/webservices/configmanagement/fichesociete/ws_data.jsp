<%@page import="dal.TFicheSociete"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
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
    String lg_FICHE_SOCIETE_ID = "%%", str_CODE_INTERNE = "%%", str_LIBELLE_ENTREPRISE = "%%",
            str_TYPE_SOCIETE = "%%", str_CODE_REGROUPEMENT = "%%", str_CONTACTS_TELEPHONIQUES = "%%",
            str_COMPTE_COMPTABLE = "%%", str_DOMICIALIATION_BANCAIRE = "%%",
            str_RIB_SOCIETE = "%%", str_CODE_EXONERATION_TVA = "%%", str_CODE_REMISE = "%%",
            str_CODE_FACTURE = "%%", str_CODE_BON_LIVRAISON = "%%", str_RAISON_SOCIALE = "%%",
            str_BUREAU_DISTRIBUTEUR = "%%", lg_ESCOMPTE_SOCIETE_ID = "%%" ,
            lg_VILLE_ID = "%%", str_ADRESSE_PRINCIPALE = "%%", str_AUTRE_ADRESSE = "%%", str_CODE_POSTAL = "%%";
    int int_ECHEANCE_PAIEMENT;
    double dbl_CHIFFRE_AFFAIRE, dbl_REMISE_SUPPLEMENTAIRE, dbl_MONTANT_PORT;
    boolean bool_CLIENT_EN_COMPTE, bool_LIVRE, bool_EDIT_FACTION_FIN_VENTE; 
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    json Ojson = new json();

    List<dal.TFicheSociete> lstTFicheSociete = new ArrayList<dal.TFicheSociete>();

%>


<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data fiche societe ");
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

<%    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_FICHE_SOCIETE_ID") != null) {
        if (request.getParameter("lg_FICHE_SOCIETE_ID").toString().equals("ALL")) {
            lg_FICHE_SOCIETE_ID = "%%";
        } else {
            lg_FICHE_SOCIETE_ID = request.getParameter("lg_FICHE_SOCIETE_ID").toString();
        }

    }
    
    OdataManager.initEntityManager();
    lstTFicheSociete = OdataManager.getEm().createQuery("SELECT t FROM TFicheSociete t WHERE t.strCODEINTERNE LIKE ?1 AND t.strLIBELLEENTREPRISE LIKE ?2 AND t.strSTATUT LIKE ?3 ").
            setParameter(1, lg_FICHE_SOCIETE_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTFicheSociete.size());

    
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTFicheSociete.size()) {
            DATA_PER_PAGE = lstTFicheSociete.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;
 
    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTFicheSociete.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTFicheSociete.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_FICHE_SOCIETE_ID", lstTFicheSociete.get(i).getLgFICHESOCIETEID());

        json.put("str_CODE_INTERNE", lstTFicheSociete.get(i).getStrCODEINTERNE());
        json.put("str_LIBELLE_ENTREPRISE", lstTFicheSociete.get(i).getStrLIBELLEENTREPRISE());
        json.put("str_TYPE_SOCIETE", lstTFicheSociete.get(i).getStrTYPESOCIETE());
        json.put("str_CODE_REGROUPEMENT", lstTFicheSociete.get(i).getStrCODEREGROUPEMENT());
        json.put("str_CONTACTS_TELEPHONIQUES", lstTFicheSociete.get(i).getStrCONTACTSTELEPHONIQUES());
        json.put("str_COMPTE_COMPTABLE", lstTFicheSociete.get(i).getStrCOMPTECOMPTABLE());
        json.put("dbl_CHIFFRE_AFFAIRE", lstTFicheSociete.get(i).getDblCHIFFREAFFAIRE());
        json.put("str_DOMICIALIATION_BANCAIRE", lstTFicheSociete.get(i).getStrDOMICIALIATIONBANCAIRE());
        json.put("str_RIB_SOCIETE", lstTFicheSociete.get(i).getStrRIBSOCIETE());        
        json.put("str_CODE_EXONERATION_TVA", lstTFicheSociete.get(i).getStrCODEEXONERATIONTVA());
        json.put("str_CODE_REMISE", lstTFicheSociete.get(i).getStrCODEREMISE());
        json.put("bool_CLIENT_EN_COMPTE", lstTFicheSociete.get(i).getBoolCLIENTENCOMPTE());
        json.put("bool_LIVRE", lstTFicheSociete.get(i).getBoolLIVRE());
        json.put("dbl_REMISE_SUPPLEMENTAIRE", lstTFicheSociete.get(i).getDblREMISESUPPLEMENTAIRE());
        json.put("dbl_MONTANT_PORT", lstTFicheSociete.get(i).getDblMONTANTPORT());
        json.put("int_ECHEANCE_PAIEMENT", lstTFicheSociete.get(i).getIntECHEANCEPAIEMENT());
        json.put("bool_EDIT_FACTION_FIN_VENTE", lstTFicheSociete.get(i).getBoolEDITFACTIONFINVENTE());
        json.put("str_CODE_FACTURE", lstTFicheSociete.get(i).getStrCODEFACTURE());
        json.put("str_CODE_BON_LIVRAISON", lstTFicheSociete.get(i).getStrCODEBONLIVRAISON());
        json.put("str_RAISON_SOCIALE", lstTFicheSociete.get(i).getStrRAISONSOCIALE());        
        json.put("str_ADRESSE_PRINCIPALE", lstTFicheSociete.get(i).getStrADRESSEPRINCIPALE());
        json.put("str_AUTRE_ADRESSE", lstTFicheSociete.get(i).getStrAUTREADRESSE());        
        json.put("str_CODE_POSTAL", lstTFicheSociete.get(i).getStrCODEPOSTAL());
        json.put("str_BUREAU_DISTRIBUTEUR", lstTFicheSociete.get(i).getStrBUREAUDISTRIBUTEUR());        
        
        
        
        lg_ESCOMPTE_SOCIETE_ID = lstTFicheSociete.get(i).getLgESCOMPTESOCIETEID().getStrLIBELLEESCOMPTESOCIETE();         
        if (lg_ESCOMPTE_SOCIETE_ID != null){
            json.put("lg_ESCOMPTE_SOCIETE_ID", lg_ESCOMPTE_SOCIETE_ID);
        }
        
        lg_VILLE_ID = lstTFicheSociete.get(i).getLgVILLEID().getStrName();
        if (lg_VILLE_ID != null){
            json.put("lg_VILLE_ID", lg_VILLE_ID);
        }       
        json.put("str_STATUT", lstTFicheSociete.get(i).getStrSTATUT());

        dt_CREATED = lstTFicheSociete.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTFicheSociete.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
   // String result = "({\"total\":\"" + lstTFicheSociete.size() + " \",\"results\":" + arrayObj.toString() + "})";
    String result = "({\"total\":\"" + lstTFicheSociete.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>