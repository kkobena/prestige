<%@page import="dal.TVille"%>
<%@page import="dal.TEscompteSociete"%>
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

<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
   


%>



<%
     String lg_VILLE_ID = "%%", STR_NAME = "%%", STR_CODE_POSTAL = "%%", STR_BUREAU_DISTRIBUTEUR = "%%", str_CODE = "";
    if (request.getParameter("lg_VILLE_ID") != null) {
        lg_VILLE_ID = request.getParameter("lg_VILLE_ID");
    }
    if (request.getParameter("STR_NAME") != null) {
        STR_NAME = request.getParameter("STR_NAME");
    }
    if (request.getParameter("STR_CODE_POSTAL") != null) {
        STR_CODE_POSTAL = request.getParameter("STR_CODE_POSTAL");
    }
    if (request.getParameter("STR_BUREAU_DISTRIBUTEUR") != null) {
        STR_BUREAU_DISTRIBUTEUR = request.getParameter("STR_BUREAU_DISTRIBUTEUR");
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
        new logger().OCategory.info("str_CODE " + str_CODE);

    }

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
    new logger().oCategory.info("ID " + request.getParameter("lg_VILLE_ID"));

    new logger().oCategory.info("STR_BUREAU_DISTRIBUTEUR   @@@@@@@@@@@@@@@@     " + request.getParameter("STR_BUREAU_DISTRIBUTEUR"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            try {
                TVille OTVille = new TVille();

                OTVille.setLgVILLEID(key.getComplexId());
                OTVille.setStrName(STR_NAME);
                OTVille.setStrCodePostal(STR_CODE_POSTAL);
                OTVille.setStrBureauDistributeur(STR_BUREAU_DISTRIBUTEUR);
                OTVille.setStrStatut(commonparameter.statut_enable);
                OTVille.setDtCreated(new Date());
                OTVille.setStrCODE(str_CODE);

                ObllBase.persiste(OTVille);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de création de la ville");
            }

            //   new logger().oCategory.info("Mise a jour OTVille " + OTVille.getLgVILLEID() + " VILLE " + OTVille.getStrName());
        } else if (request.getParameter("mode").toString().equals("update")) {

            try {
                TVille OTVille = ObllBase.getOdataManager().getEm().find(TVille.class, request.getParameter("lg_VILLE_ID").toString());

                OTVille.setStrName(STR_NAME);
                OTVille.setStrCodePostal(STR_CODE_POSTAL);
                OTVille.setStrBureauDistributeur(STR_BUREAU_DISTRIBUTEUR);
                OTVille.setStrStatut(commonparameter.statut_enable);
                OTVille.setDtUpdated(new Date());
                OTVille.setStrCODE(str_CODE);

                ObllBase.persiste(OTVille);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de mise à jour de la ville");
            }

            // new logger().oCategory.info("Mise a jour OTVille " + OTVille.getLgVILLEID() + " StrLabel " + OTVille.getStrName());
        } else if (request.getParameter("mode").toString().equals("delete")) {

            try {
                TVille OTVille = ObllBase.getOdataManager().getEm().find(dal.TVille.class, request.getParameter("lg_VILLE_ID"));

                OTVille.setStrStatut(commonparameter.statut_delete);
                ObllBase.persiste(OTVille);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de suppression de la ville");
            }

           // new logger().oCategory.info("Suppression ville " + request.getParameter("lg_VILLE_ID").toString());
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