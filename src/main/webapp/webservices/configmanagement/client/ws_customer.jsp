<%@page import="bll.configManagement.ayantDroitManagement"%>
<%@page import="dal.TAyantDroit"%>
<%@page import="bll.common.Parameter"%>
<%@page import="dal.TCompteClient"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TCompteClientTiersPayant"%>
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
    TAyantDroit OTAyantDroit = null;
    List<TCompteClientTiersPayant> lstTCompteClientTiersPayant = new ArrayList<TCompteClientTiersPayant>();
    TCompteClient OTCompteClient = null;
    JSONArray arrayObj = new JSONArray(), arrayObjTiersPayant = new JSONArray();
    JSONObject json = new JSONObject(), jsonTiersPayant = null;
%>

<%
    String lg_CLIENT_ID = "";

    if (request.getParameter("lg_CLIENT_ID") != null) {
        lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
        new logger().OCategory.info("lg_CLIENT_ID:" + lg_CLIENT_ID);
    }

    OdataManager.initEntityManager();
    clientManagement OclientManagement = new clientManagement(OdataManager);
    ayantDroitManagement OayantDroitManagement = new ayantDroitManagement(OdataManager);
    OTCompteClient = OclientManagement.getTCompteClientByClient(lg_CLIENT_ID);

    if (OTCompteClient != null) {
        json.put("str_FIRST_NAME", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME());
        json.put("str_CODE_INTERNE", OTCompteClient.getLgCLIENTID().getStrCODEINTERNE());
        json.put("str_LAST_NAME", OTCompteClient.getLgCLIENTID().getStrLASTNAME());
        json.put("dt_NAISSANCE", OTCompteClient.getLgCLIENTID().getDtNAISSANCE() != null ? date.DateToString(OTCompteClient.getLgCLIENTID().getDtNAISSANCE(), date.formatterShort) : "");
        json.put("str_NUMERO_SECURITE_SOCIAL", OTCompteClient.getLgCLIENTID().getStrNUMEROSECURITESOCIAL());
        json.put("str_ADRESSE", OTCompteClient.getLgCLIENTID().getStrADRESSE());
        json.put("str_CODE_POSTAL", OTCompteClient.getLgCLIENTID().getStrCODEPOSTAL());
        json.put("str_SEXE", OTCompteClient.getLgCLIENTID().getStrSEXE());
        json.put("lg_VILLE_ID", (OTCompteClient.getLgCLIENTID().getLgVILLEID()!=null?OTCompteClient.getLgCLIENTID().getLgVILLEID().getStrName():""));
        json.put("lg_TYPE_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgTYPECLIENTID().getStrDESCRIPTION());
        if (OTCompteClient.getLgCLIENTID().getLgTYPECLIENTID().getLgTYPECLIENTID().equalsIgnoreCase("1")) {
            OTAyantDroit = OayantDroitManagement.getAyantDroitByNameClient(OTCompteClient.getLgCLIENTID().getStrFIRSTNAME(), OTCompteClient.getLgCLIENTID().getStrLASTNAME());
            if (OTAyantDroit != null) {
                json.put("lg_CATEGORIE_AYANTDROIT_ID", OTAyantDroit.getLgCATEGORIEAYANTDROITID().getStrLIBELLECATEGORIEAYANTDROIT());
                json.put("lg_RISQUE_ID", OTAyantDroit.getLgRISQUEID().getStrLIBELLERISQUE());
                json.put("lg_AYANTS_DROITS_ID", OTAyantDroit.getLgAYANTSDROITSID());
            }
        }
        lstTCompteClientTiersPayant = OclientManagement.getTiersPayantsByClient("", OTCompteClient.getLgCOMPTECLIENTID(), "%%");
        for (int i = 0; i < lstTCompteClientTiersPayant.size(); i++) {
            jsonTiersPayant = new JSONObject();
            jsonTiersPayant.put("str_TIERS_PAYANT_NAME", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrNAME());
            jsonTiersPayant.put("int_PRIORITY", lstTCompteClientTiersPayant.get(i).getIntPRIORITY());
            jsonTiersPayant.put("int_POURCENTAGE", lstTCompteClientTiersPayant.get(i).getIntPOURCENTAGE());
            jsonTiersPayant.put("lg_COMPTE_TIERS_PAYANT_ID", lstTCompteClientTiersPayant.get(i).getLgCOMPTECLIENTTIERSPAYANTID());
            jsonTiersPayant.put("dbl_QUOTA_CONSO_MENSUELLE", lstTCompteClientTiersPayant.get(i).getDblQUOTACONSOMENSUELLE());
            jsonTiersPayant.put("dbl_QUOTA_CONSO_VENTE", lstTCompteClientTiersPayant.get(i).getDblQUOTACONSOVENTE());
            jsonTiersPayant.put("dbl_PLAFOND", lstTCompteClientTiersPayant.get(i).getDblPLAFOND());
            jsonTiersPayant.put("str_NUMERO_SECURITE_SOCIAL", lstTCompteClientTiersPayant.get(i).getStrNUMEROSECURITESOCIAL());
            arrayObjTiersPayant.put(jsonTiersPayant);
        }
        json.put("Tierspayant", arrayObjTiersPayant);
    }

    arrayObj.put(json);

    String result = "({results:" + arrayObj.toString() + "})";
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>