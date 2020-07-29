<%@page import="dal.TTauxMarque"%>
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
    dal.TTauxMarque OTTauxMarque = null;
    String lg_TAUX_MARQUE_ID = "%%", str_NAME = "%%", str_CODE = "%%"; 


%>



<%
    if (request.getParameter("lg_TAUX_MARQUE_ID") != null) {
        lg_TAUX_MARQUE_ID = request.getParameter("lg_TAUX_MARQUE_ID");
    }
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_TAUX_MARQUE_ID"));

    new logger().oCategory.info("str_NAME   @@@@@@@@@@@@@@@@     " + request.getParameter("str_NAME"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            try {
                TTauxMarque OTTauxMarque = new TTauxMarque();

                OTTauxMarque.setLgTAUXMARQUEID(key.getComplexId());
                OTTauxMarque.setStrNAME(str_NAME);
                OTTauxMarque.setStrCODE(str_CODE);                
                OTTauxMarque.setStrSTATUT(commonparameter.statut_enable);
                OTTauxMarque.setDtCREATED(new Date());              

                ObllBase.persiste(OTTauxMarque);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de création de taux de marque");
            }

            //   new logger().oCategory.info("Mise a jour OTTauxMarque " + OTTauxMarque.getLgVILLEID() + " VILLE " + OTTauxMarque.getStrName());
        } else if (request.getParameter("mode").toString().equals("update")) {

            try {
                TTauxMarque OTTauxMarque = ObllBase.getOdataManager().getEm().find(TTauxMarque.class, request.getParameter("lg_TAUX_MARQUE_ID").toString());

                OTTauxMarque.setStrNAME(str_NAME);
                OTTauxMarque.setStrCODE(str_CODE);                
                OTTauxMarque.setStrSTATUT(commonparameter.statut_enable);
                OTTauxMarque.setDtCREATED(new Date());
               

                ObllBase.persiste(OTTauxMarque);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de mise à jour taux de marque");
            }

            // new logger().oCategory.info("Mise a jour OTTauxMarque " + OTTauxMarque.getLgVILLEID() + " StrLabel " + OTTauxMarque.getStrName());
        } else if (request.getParameter("mode").toString().equals("delete")) {

            try {
                TTauxMarque OTTauxMarque = ObllBase.getOdataManager().getEm().find(dal.TTauxMarque.class, request.getParameter("lg_TAUX_MARQUE_ID"));

                OTTauxMarque.setStrSTATUT(commonparameter.statut_delete);
                ObllBase.persiste(OTTauxMarque);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de suppression de taux de marque");
            }

           // new logger().oCategory.info("Suppression ville " + request.getParameter("lg_TAUX_MARQUE_ID").toString());
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