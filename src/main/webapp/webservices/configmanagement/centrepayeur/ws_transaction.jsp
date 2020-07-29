<%@page import="dal.TCentrePayeur"%>
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
    dal.TCentrePayeur OTCentrePayeur = null;
    String lg_CENTRE_PAYEUR = "%%", str_LIBELLE = "%%", str_CODE = "%%"; 


%>



<%
    if (request.getParameter("lg_CENTRE_PAYEUR") != null) {
        lg_CENTRE_PAYEUR = request.getParameter("lg_CENTRE_PAYEUR");
    }
    if (request.getParameter("str_LIBELLE") != null) {
        str_LIBELLE = request.getParameter("str_LIBELLE");
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
    }
    
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_CENTRE_PAYEUR"));

    new logger().oCategory.info("str_LIBELLE   @@@@@@@@@@@@@@@@     " + request.getParameter("str_LIBELLE"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            try {
                TCentrePayeur OTCentrePayeur = new TCentrePayeur();

                OTCentrePayeur.setLgCENTREPAYEUR(key.getComplexId());
                OTCentrePayeur.setStrLIBELLE(str_LIBELLE);
                OTCentrePayeur.setStrCODE(str_CODE);                
                OTCentrePayeur.setStrSTATUT(commonparameter.statut_enable);
                OTCentrePayeur.setDtCREATED(new Date());              

                ObllBase.persiste(OTCentrePayeur);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de création de la ville");
            }

            //   new logger().oCategory.info("Mise a jour OTCentrePayeur " + OTCentrePayeur.getLgVILLEID() + " VILLE " + OTCentrePayeur.getStrName());
        } else if (request.getParameter("mode").toString().equals("update")) {

            try {
                TCentrePayeur OTCentrePayeur = ObllBase.getOdataManager().getEm().find(TCentrePayeur.class, request.getParameter("lg_CENTRE_PAYEUR").toString());

                OTCentrePayeur.setStrLIBELLE(str_LIBELLE);
                OTCentrePayeur.setStrCODE(str_CODE);                
                OTCentrePayeur.setStrSTATUT(commonparameter.statut_enable);
                OTCentrePayeur.setDtCREATED(new Date());
               

                ObllBase.persiste(OTCentrePayeur);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de mise à jour de la ville");
            }

            // new logger().oCategory.info("Mise a jour OTCentrePayeur " + OTCentrePayeur.getLgVILLEID() + " StrLabel " + OTCentrePayeur.getStrName());
        } else if (request.getParameter("mode").toString().equals("delete")) {

            try {
                TCentrePayeur OTCentrePayeur = ObllBase.getOdataManager().getEm().find(dal.TCentrePayeur.class, request.getParameter("lg_CENTRE_PAYEUR"));

                OTCentrePayeur.setStrSTATUT(commonparameter.statut_delete);
                ObllBase.persiste(OTCentrePayeur);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de suppression de la ville");
            }

           // new logger().oCategory.info("Suppression ville " + request.getParameter("lg_CENTRE_PAYEUR").toString());
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