<%@page import="dal.TTableau"%>
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
    dal.TTableau OTTableau = null;
    String lg_TABLEAU_ID = "%%", str_NAME = "%%", str_CODE = "%%"; 


%>



<%
    if (request.getParameter("lg_TABLEAU_ID") != null) {
        lg_TABLEAU_ID = request.getParameter("lg_TABLEAU_ID");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_TABLEAU_ID"));

    new logger().oCategory.info("str_NAME   @@@@@@@@@@@@@@@@     " + request.getParameter("str_NAME"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            try {
                TTableau OTTableau = new TTableau();

                OTTableau.setLgTABLEAUID(key.getComplexId());
                OTTableau.setStrNAME(str_NAME);
                OTTableau.setStrCODE(str_CODE);                
                OTTableau.setStrSTATUT(commonparameter.statut_enable);
                OTTableau.setDtCREATED(new Date());              

                ObllBase.persiste(OTTableau);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de création de la ville");
            }

            //   new logger().oCategory.info("Mise a jour OTTableau " + OTTableau.getLgVILLEID() + " VILLE " + OTTableau.getStrName());
        } else if (request.getParameter("mode").toString().equals("update")) {

            try {
                TTableau OTTableau = ObllBase.getOdataManager().getEm().find(TTableau.class, request.getParameter("lg_TABLEAU_ID").toString());

                OTTableau.setStrNAME(str_NAME);
                OTTableau.setStrCODE(str_CODE);                
                OTTableau.setStrSTATUT(commonparameter.statut_enable);
                OTTableau.setDtCREATED(new Date());
               

                ObllBase.persiste(OTTableau);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de mise à jour du tableau");
            }

            // new logger().oCategory.info("Mise a jour OTTableau " + OTTableau.getLgVILLEID() + " StrLabel " + OTTableau.getStrName());
        } else if (request.getParameter("mode").toString().equals("delete")) {

            try {
                TTableau OTTableau = ObllBase.getOdataManager().getEm().find(dal.TTableau.class, request.getParameter("lg_TABLEAU_ID"));

                OTTableau.setStrSTATUT(commonparameter.statut_delete);
                ObllBase.persiste(OTTableau);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de suppression du tableau");
            }

           // new logger().oCategory.info("Suppression ville " + request.getParameter("lg_TABLEAU_ID").toString());
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