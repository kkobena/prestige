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
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
    List<TPreenregistrementCompteClientTiersPayent> lstTPreenregistrementCompteClientTiersPayent = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
    TPreenregistrement OTPreenregistrement = null;
    TCompteClient OTCompteClient = null;
    TRemise OTRemise = null;
    TAyantDroit OTAyantDroit = null;
    JSONArray arrayObj = new JSONArray(), arrayObjTiersPayant = new JSONArray();
    JSONObject json = new JSONObject(), jsonTiersPayant = null;
%>

<%
    String lg_PREENREGISTREMENT_ID = "";
    int int_TAUX = 0;
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    ayantDroitManagement OayantDroitManagement = new ayantDroitManagement(OdataManager);
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);

    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID:" + lg_PREENREGISTREMENT_ID);
    }

    OTPreenregistrement = OPreenregistrement.getTPreenregistrementByRef(lg_PREENREGISTREMENT_ID);
    json.put("int_PRICE", OTPreenregistrement.getIntPRICE());
    if (OTPreenregistrement.getLgREMISEID() != null && !OTPreenregistrement.getLgREMISEID().equals("")) {
        OTRemise = OdataManager.getEm().find(TRemise.class, OTPreenregistrement.getLgREMISEID());
        json.put("lg_REMISE_ID", OTRemise.getStrNAME());
        json.put("lg_TYPE_REMISE_ID", OTRemise.getLgTYPEREMISEID().getStrDESCRIPTION());
    } else {
        json.put("lg_REMISE_ID", "Aucun");
        json.put("lg_TYPE_REMISE_ID", "Aucun");
    }

    // if (!OTPreenregistrement.getStrSTATUT().equalsIgnoreCase(commonparameter.statut_is_Devis)) {
    if (OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equalsIgnoreCase(Parameter.VENTE_COMPTANT)) { // vente au comptant
        if (!OTPreenregistrement.getStrSTATUT().equals(commonparameter.statut_is_Devis)) {
                 System.out.println("---------------------------------------------------  ");
            try {
                 OTCompteClient = OdataManager.getEm().find(TCompteClient.class, OTPreenregistrement.getStrINFOSCLT());    
                } catch (Exception e) {
                }
            if (OTCompteClient != null) {
                json.put("lg_COMPTE_CLIENT_ID", OTCompteClient.getLgCOMPTECLIENTID());
                json.put("lg_TYPE_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgTYPECLIENTID().getLgTYPECLIENTID());
                json.put("lg_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgCLIENTID());
                json.put("str_FIRST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME());
                json.put("str_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrLASTNAME());
                json.put("str_FIRST_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME() + " " + OTCompteClient.getLgCLIENTID().getStrLASTNAME());
                json.put("str_NUMERO_SECURITE_SOCIAL", OTCompteClient.getLgCLIENTID().getStrNUMEROSECURITESOCIAL());
            }else{
                json.put("str_FIRST_NAME", OTPreenregistrement.getStrFIRSTNAMECUSTOMER());
            json.put("str_LAST_NAME", OTPreenregistrement.getStrLASTNAMECUSTOMER());
          
            }
        } else {
             try {
                 OTCompteClient = OdataManager.getEm().find(TCompteClient.class, OTPreenregistrement.getStrINFOSCLT()); 
                 json.put("lg_COMPTE_CLIENT_ID", OTCompteClient.getLgCOMPTECLIENTID());
                json.put("lg_TYPE_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgTYPECLIENTID().getLgTYPECLIENTID());
                json.put("lg_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgCLIENTID());
                json.put("str_FIRST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME());
                json.put("str_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrLASTNAME());
                json.put("str_FIRST_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME() + " " + OTCompteClient.getLgCLIENTID().getStrLASTNAME());
                } catch (Exception e) {
                }
            //json.put("str_FIRST_NAME", OTPreenregistrement.getStrFIRSTNAMECUSTOMER());
           // json.put("str_LAST_NAME", OTPreenregistrement.getStrLASTNAMECUSTOMER());
            System.out.println("***********************  "+OTPreenregistrement.getStrFIRSTNAMECUSTOMER());
        }
    } else { // vente a creadit
        lstTPreenregistrementCompteClientTiersPayent = OPreenregistrement.getListePreenregistrementCompteClientTiersPayent(OTPreenregistrement.getLgPREENREGISTREMENTID());
      
        for (int i = 0; i < lstTPreenregistrementCompteClientTiersPayent.size(); i++) {
            jsonTiersPayant = new JSONObject();

            jsonTiersPayant.put("IDTIERSPAYANT", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTIERSPAYANTID());
            jsonTiersPayant.put("str_TIERS_PAYANT_NAME", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME());
            jsonTiersPayant.put("int_PRIORITY", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getIntPRIORITY());
            jsonTiersPayant.put("int_POURCENTAGE", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getIntPOURCENTAGE());
            jsonTiersPayant.put("lg_COMPTE_TIERS_PAYANT_ID", lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTTIERSPAYANTID());
            int_TAUX += lstTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getIntPOURCENTAGE();

            arrayObjTiersPayant.put(jsonTiersPayant);
        }

        if (lstTPreenregistrementCompteClientTiersPayent.size() > 0) {
            OTCompteClient = lstTPreenregistrementCompteClientTiersPayent.get(0).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID();
            json.put("lg_COMPTE_CLIENT_ID", OTCompteClient.getLgCOMPTECLIENTID());
            json.put("lg_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgCLIENTID());
            json.put("str_FIRST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME());
            json.put("str_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrLASTNAME());

            json.put("str_FIRST_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME() + " " + OTCompteClient.getLgCLIENTID().getStrLASTNAME());
            json.put("str_NUMERO_SECURITE_SOCIAL", OTCompteClient.getLgCLIENTID().getStrNUMEROSECURITESOCIAL());

            OTAyantDroit = OayantDroitManagement.getAyantDroitByNameClient(OTPreenregistrement.getStrFIRSTNAMECUSTOMER(), OTPreenregistrement.getStrLASTNAMECUSTOMER());
            if (OTAyantDroit != null) {
                json.put("str_FIRST_NAME_AD", OTAyantDroit.getStrFIRSTNAME());
                json.put("str_LAST_NAME_AD", OTAyantDroit.getStrLASTNAME());
                json.put("lg_AYANTS_DROITS_ID", OTAyantDroit.getLgAYANTSDROITSID());
                json.put("str_FIRST_LAST_NAME_AD", OTAyantDroit.getStrFIRSTNAME() + " " + OTAyantDroit.getStrLASTNAME());
                json.put("str_NUMERO_SECURITE_SOCIAL_AD", OTAyantDroit.getStrNUMEROSECURITESOCIAL());
            }
            json.put("Tierspayant", arrayObjTiersPayant);
        }
    }
    json.put("str_PHONE", OTPreenregistrement.getStrPHONECUSTOME());
 
    // } 

    /* else { //devis
        OTCompteClient = OdataManager.getEm().find(TCompteClient.class, OTPreenregistrement.getStrINFOSCLT());
        if (OTCompteClient != null) {
            json.put("lg_COMPTE_CLIENT_ID", OTCompteClient.getLgCOMPTECLIENTID());
            json.put("lg_TYPE_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgTYPECLIENTID().getLgTYPECLIENTID());
            json.put("lg_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgCLIENTID());
            json.put("str_FIRST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME());
            json.put("str_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrLASTNAME());
            json.put("str_FIRST_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME() + " " + OTCompteClient.getLgCLIENTID().getStrLASTNAME());
            json.put("str_NUMERO_SECURITE_SOCIAL", OTCompteClient.getLgCLIENTID().getStrNUMEROSECURITESOCIAL());
        }
    }*/
    json.put("int_TAUX", int_TAUX);
    arrayObj.put(json);

    String result = "({results:" + arrayObj.toString() + "})";


%>
<%=result%>