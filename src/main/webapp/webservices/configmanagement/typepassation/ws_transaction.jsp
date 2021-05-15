<%@page import="dal.TDci"%>
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
    dal.TDci OTDci = null;
    String lg_DCI_ID = "%%", str_NAME = "%%", str_CODE = "%%"; 


%>



<%
    if (request.getParameter("lg_DCI_ID") != null) {
        lg_DCI_ID = request.getParameter("lg_DCI_ID");
    }
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_DCI_ID"));

    new logger().oCategory.info("str_NAME   @@@@@@@@@@@@@@@@     " + request.getParameter("str_NAME"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            try {
                TDci OTDci = new TDci();

                OTDci.setLgDCIID(key.getComplexId());
                OTDci.setStrNAME(str_NAME);
                OTDci.setStrCODE(str_CODE);                
                OTDci.setStrSTATUT(commonparameter.statut_enable);
                OTDci.setDtCREATED(new Date());              

                ObllBase.persiste(OTDci);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de création de la ville");
            }

            //   new logger().oCategory.info("Mise a jour OTDci " + OTDci.getLgVILLEID() + " VILLE " + OTDci.getStrName());
        } else if (request.getParameter("mode").toString().equals("update")) {

            try {
                TDci OTDci = ObllBase.getOdataManager().getEm().find(TDci.class, request.getParameter("lg_DCI_ID").toString());

                OTDci.setStrNAME(str_NAME);
                OTDci.setStrCODE(str_CODE);                
                OTDci.setStrSTATUT(commonparameter.statut_enable);
                OTDci.setDtCREATED(new Date());
               

                ObllBase.persiste(OTDci);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de mise à jour de la ville");
            }

            // new logger().oCategory.info("Mise a jour OTDci " + OTDci.getLgVILLEID() + " StrLabel " + OTDci.getStrName());
        } else if (request.getParameter("mode").toString().equals("delete")) {

            try {
                TDci OTDci = ObllBase.getOdataManager().getEm().find(dal.TDci.class, request.getParameter("lg_DCI_ID"));

                OTDci.setStrSTATUT(commonparameter.statut_delete);
                ObllBase.persiste(OTDci);
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de suppression de la ville");
            }

           // new logger().oCategory.info("Suppression ville " + request.getParameter("lg_DCI_ID").toString());
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