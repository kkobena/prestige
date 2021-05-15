<%@page import="dal.TDossierTiersPayant"%>
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
<%@page import="bll.configManagement.familleManagement"  %>

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TDossierTiersPayant OTDossierTiersPayant = new TDossierTiersPayant();
    String lg_DOSSIER_TIERS_PAYANT_ID = "%%", str_NUMERO_TRI = "%%", str_LIBELLE_DOSSIER = "%%";
%>


<%
    if (request.getParameter("lg_DOSSIER_TIERS_PAYANT_ID") != null) {
        lg_DOSSIER_TIERS_PAYANT_ID = request.getParameter("lg_DOSSIER_TIERS_PAYANT_ID");
    }
    if (request.getParameter("str_NUMERO_TRI") != null) {
        str_NUMERO_TRI = request.getParameter("str_NUMERO_TRI");
    }
    if (request.getParameter("str_LIBELLE_DOSSIER") != null) {
        str_LIBELLE_DOSSIER = request.getParameter("str_LIBELLE_DOSSIER");
    }

    new logger().oCategory.info("Verif de l'authentification : " + request.getParameter("mode"));

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
  TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_DOSSIER_TIERS_PAYANT_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TDossierTiersPayant OTDossierTiersPayant = new TDossierTiersPayant();
            OTDossierTiersPayant.setLgDOSSIERTIERSPAYANTID(key.getComplexId());
            OTDossierTiersPayant.setStrNUMEROTRI(str_NUMERO_TRI);
            OTDossierTiersPayant.setStrLIBELLEDOSSIER(str_LIBELLE_DOSSIER);
            OTDossierTiersPayant.setStrSTATUT(commonparameter.statut_enable);
            OTDossierTiersPayant.setDtCREATED(new Date());
            ObllBase.persiste(OTDossierTiersPayant);

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_DOSSIER_TIERS_PAYANT_ID").toString().equals("init")) {

                dal.TDossierTiersPayant OTDossierTiersPayant = new TDossierTiersPayant();
                OTDossierTiersPayant.setLgDOSSIERTIERSPAYANTID(key.getComplexId());
                OTDossierTiersPayant.setStrNUMEROTRI(str_NUMERO_TRI);
                OTDossierTiersPayant.setStrLIBELLEDOSSIER(str_LIBELLE_DOSSIER);
                OTDossierTiersPayant.setStrSTATUT(commonparameter.statut_enable);
                OTDossierTiersPayant.setDtCREATED(new Date());
                ObllBase.persiste(OTDossierTiersPayant);
                new logger().oCategory.info("Mise a jour OTDossierTiersPayant " + OTDossierTiersPayant.getLgDOSSIERTIERSPAYANTID() + " StrLabel " + OTDossierTiersPayant.getStrLIBELLEDOSSIER());

            } else {

                dal.TDossierTiersPayant OTDossierTiersPayant = null;
                OTDossierTiersPayant = ObllBase.getOdataManager().getEm().find(dal.TDossierTiersPayant.class, request.getParameter("lg_DOSSIER_TIERS_PAYANT_ID").toString());
                OTDossierTiersPayant.setStrNUMEROTRI(str_NUMERO_TRI);
                OTDossierTiersPayant.setStrLIBELLEDOSSIER(str_LIBELLE_DOSSIER);
                OTDossierTiersPayant.setStrSTATUT(commonparameter.statut_enable);
                OTDossierTiersPayant.setDtUPDATED(new Date());

                ObllBase.persiste(OTDossierTiersPayant);
                new logger().oCategory.info("Mise a jour OTDossierTiersPayant " + OTDossierTiersPayant.getLgDOSSIERTIERSPAYANTID() + " StrLabel " + OTDossierTiersPayant.getStrLIBELLEDOSSIER());

            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            OTDossierTiersPayant = ObllBase.getOdataManager().getEm().find(dal.TDossierTiersPayant.class, request.getParameter("lg_DOSSIER_TIERS_PAYANT_ID"));

            OTDossierTiersPayant.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTDossierTiersPayant);

            new logger().oCategory.info("Suppression de famille " + request.getParameter("lg_DOSSIER_TIERS_PAYANT_ID").toString());

        } else {
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>