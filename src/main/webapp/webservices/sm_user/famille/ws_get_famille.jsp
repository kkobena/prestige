<%@page import="bll.teller.SnapshotManager"%>
<%@page import="dal.TMouvement"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.common.Parameter"%>
<%@page import="dal.TAyantDroit"%>
<%@page import="bll.configManagement.ayantDroitManagement"%>
<%@page import="dal.TRemise"%>
<%@page import="bll.preenregistrement.DevisManagement"%>
<%@page import="dal.TCompteClient"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="bll.differe.DiffereManagement"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TPreenregistrementCompteClient"%>
<%@page import="bll.teller.clientManager"%>
<%@page import="dal.TTypeReglement"%>
<%@page import="dal.TCashTransaction"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<% 
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
    JSONArray arrayObj = new JSONArray();
    JSONObject json = null;
    TMouvement OTMouvement = null;
%>




<%    String lg_FAMILLE_ID = "";

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().oCategory.info("lg_FAMILLE_ID : " + lg_FAMILLE_ID);
    }


    OdataManager.initEntityManager(); 
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, OTUser);
    TFamilleStock OTFamillestock = OtellerManagement.getTProductItemStock(lg_FAMILLE_ID);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("Produit inexistant");
    
    if(OTFamillestock != null) {
        ObllBase.buildSuccesTraceMessage("Produit trouvé");
        json = new JSONObject();
        //code ajouté
        json.put("int_NUMBER_AVAILABLE", OTFamillestock.getIntNUMBERAVAILABLE());
        json.put("lg_CODE_GESTION_ID", OTFamillestock.getLgFAMILLEID().getLgCODEGESTIONID() != null ? OTFamillestock.getLgFAMILLEID().getLgCODEGESTIONID().getStrCODEBAREME() : "");
        json.put("int_STOCK_REAPROVISONEMENT", OTFamillestock.getLgFAMILLEID().getIntSTOCKREAPROVISONEMENT() != null ? OTFamillestock.getLgFAMILLEID().getIntSEUILMIN() : 0);
        json.put("int_QTE_REAPPROVISIONNEMENT", OTFamillestock.getLgFAMILLEID().getIntQTEREAPPROVISIONNEMENT() != null ? OTFamillestock.getLgFAMILLEID().getIntQTEREAPPROVISIONNEMENT() : 0);
        json.put("str_CODE_REMISE", (OTFamillestock.getLgFAMILLEID().getStrCODEREMISE() != null ? OTFamillestock.getLgFAMILLEID().getStrCODEREMISE() : ""));
        json.put("lg_TYPEETIQUETTE_ID", (OTFamillestock.getLgFAMILLEID().getLgTYPEETIQUETTEID() != null ? OTFamillestock.getLgFAMILLEID().getLgTYPEETIQUETTEID().getStrDESCRIPTION() : ""));
        try {
            OTMouvement = OSnapshotManager.getTMouvement("", OTFamillestock.getLgFAMILLEID().getLgFAMILLEID(), commonparameter.str_ACTION_INVENTAIRE);
            if (OTMouvement.getDtUPDATED() != null) {
                json.put("dt_LAST_INVENTAIRE", date.DateToString(OTMouvement.getDtUPDATED(), date.formatterOrange));
            }

        } catch (Exception e) {
        }
        try {
            OTMouvement = OSnapshotManager.getTMouvement("", OTFamillestock.getLgFAMILLEID().getLgFAMILLEID(), commonparameter.str_ACTION_ENTREESTOCK);
            if (OTMouvement.getDtUPDATED() != null) {
                json.put("dt_LAST_ENTREE", date.DateToString(OTMouvement.getDtUPDATED(), date.formatterOrange));
            }
        } catch (Exception e) {
        }
        try {
            OTMouvement = OSnapshotManager.getTMouvement("", OTFamillestock.getLgFAMILLEID().getLgFAMILLEID(), commonparameter.str_ACTION_VENTE);
            if (OTMouvement.getDtUPDATED() != null) {
                json.put("dt_LAST_VENTE", date.DateToString(OTMouvement.getDtUPDATED(), date.formatterOrange));
            }
        } catch (Exception e) {
        }
        json.put("lg_CODE_TVA_ID", (OTFamillestock.getLgFAMILLEID().getLgCODETVAID() != null ? OTFamillestock.getLgFAMILLEID().getLgCODETVAID().getStrNAME() : ""));
        json.put("int_T", OTFamillestock.getLgFAMILLEID().getIntT());
        json.put("str_CODE_TAUX_REMBOURSEMENT", OTFamillestock.getLgFAMILLEID().getStrCODETAUXREMBOURSEMENT());
        json.put("lg_CODE_ACTE_ID", OTFamillestock.getLgFAMILLEID().getLgCODEACTEID() != null ? OTFamillestock.getLgFAMILLEID().getLgCODEACTEID().getStrLIBELLEE() : "");
        json.put("int_TAUX_MARQUE", OTFamillestock.getLgFAMILLEID().getIntTAUXMARQUE());
        json.put("int_PAF", OTFamillestock.getLgFAMILLEID().getIntPAF());
        json.put("int_PAT", OTFamillestock.getLgFAMILLEID().getIntPAT());
        json.put("int_PRICE_TIPS", OTFamillestock.getLgFAMILLEID().getIntPRICETIPS());
        json.put("int_PRICE", OTFamillestock.getLgFAMILLEID().getIntPRICE());
         json.put("lg_FAMILLEARTICLE_ID", OTFamillestock.getLgFAMILLEID().getLgFAMILLEARTICLEID() != null ? OTFamillestock.getLgFAMILLEID().getLgFAMILLEARTICLEID().getStrLIBELLE() : "");
         json.put("lg_ZONE_GEO_ID", OTFamillestock.getLgFAMILLEID().getLgZONEGEOID() != null ? OTFamillestock.getLgFAMILLEID().getLgZONEGEOID().getStrLIBELLEE() : "");
         json.put("str_DESCRIPTION", OTFamillestock.getLgFAMILLEID().getStrDESCRIPTION());
         json.put("int_CIP", OTFamillestock.getLgFAMILLEID().getIntCIP());
         json.put("int_NUMBERDETAIL", OTFamillestock.getLgFAMILLEID().getIntNUMBERDETAIL());
         json.put("int_EAN13", OTFamillestock.getLgFAMILLEID().getIntEAN13());
         arrayObj.put(json);
        //fin code ajouté
    } else {
        lg_FAMILLE_ID = "";
    }
String result = "";
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "({\"ref_init\":" + "\"" + lg_FAMILLE_ID + "\"" + ",\"results\":" + arrayObj.toString() + "})";
    } else {
        result = "({\"ref_init\":" + "\"" + lg_FAMILLE_ID + "\"" + ",\"results\":" + arrayObj.toString() + "})";
    }
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>