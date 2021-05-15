<%@page import="dal.TCompteClient"%>
<%@page import="dal.TClient"%>
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
    String  lg_CLIENT_ID = "%%";
    
    double dbl_QUOTA_CONSO_MENSUELLE = 0.00, dbl_CAUTION = 0.00;

%>




<%
   
    if (request.getParameter("dbl_QUOTA_CONSO_MENSUELLE") != null) {
        dbl_QUOTA_CONSO_MENSUELLE = Double.parseDouble(request.getParameter("dbl_QUOTA_CONSO_MENSUELLE"));
    }
   
    if (request.getParameter("lg_CLIENT_ID") != null) {
        lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_COMPTE_CLIENT_ID"));
    
    new logger().oCategory.info("lg_CLIENT_ID   @@@@@@@@@@@@@@@@     " + request.getParameter("lg_CLIENT_ID"));
    
    if (request.getParameter("mode") != null) {
        
        if (request.getParameter("mode").toString().equals("create")) {
            
            dal.TCompteClient OTCompteClient = new TCompteClient();
            OTCompteClient.setLgCOMPTECLIENTID(key.getComplexId());
            OTCompteClient.setDblCAUTION(dbl_CAUTION);
            OTCompteClient.setDblQUOTACONSOMENSUELLE(dbl_QUOTA_CONSO_MENSUELLE);
            OTCompteClient.setDecBalance(dbl_CAUTION);
            
            TClient OTClient = ObllBase.getOdataManager().getEm().find(TClient.class, lg_CLIENT_ID);
            if (OTClient == null) {
                ObllBase.buildErrorTraceMessage("Impossible de creer un " + OTClient, "Ref OTClient : " + lg_CLIENT_ID + "  Invalide ");
                return;
            }
            OTCompteClient.setLgCLIENTID(OTClient);
            OTCompteClient.setStrSTATUT(commonparameter.statut_enable);
            OTCompteClient.setDtCREATED(new Date());
            
            ObllBase.persiste(OTCompteClient);
            new logger().oCategory.info("Mise a jour OTCompteClient " + OTCompteClient.getLgCOMPTECLIENTID() + " CODE_COMPTE_CLIENT " + OTCompteClient.getStrCODECOMPTECLIENT());
            
        } else if (request.getParameter("mode").toString().equals("update")) {
            
            dal.TCompteClient OTCompteClient = null;
            OTCompteClient = ObllBase.getOdataManager().getEm().find(dal.TCompteClient.class, request.getParameter("lg_COMPTE_CLIENT_ID").toString());
            
            try {
                
                dal.TClient OTClient = ObllBase.getOdataManager().getEm().find(dal.TClient.class, request.getParameter("lg_CLIENT_ID").toString());
                new logger().oCategory.info("lg_CLIENT_ID     Create   " + lg_CLIENT_ID + "  lg_CLIENT_ID du request       " + request.getParameter("lg_CLIENT_ID").toString());
                
                if (OTClient != null) {
                    OTCompteClient.setLgCLIENTID(OTClient);
                }
            } catch (Exception e) {
                
            }
            
            OTCompteClient.setDblCAUTION(dbl_CAUTION);
            OTCompteClient.setDblQUOTACONSOMENSUELLE(dbl_QUOTA_CONSO_MENSUELLE);
           // OTCompteClient.setDblSOLDE(dbl_CAUTION);
            OTCompteClient.setStrSTATUT(commonparameter.statut_enable);
            OTCompteClient.setDtUPDATED(new Date());
            
            ObllBase.persiste(OTCompteClient);
            new logger().oCategory.info("Mise a jour OTCompteClient " + OTCompteClient.getLgCOMPTECLIENTID() + " Code Compte " + OTCompteClient.getStrCODECOMPTECLIENT());
            
        } else if (request.getParameter("mode").toString().equals("delete")) {
            
            dal.TCompteClient OTCompteClient = null;
            OTCompteClient = ObllBase.getOdataManager().getEm().find(dal.TCompteClient.class, request.getParameter("lg_COMPTE_CLIENT_ID"));
            
            OTCompteClient.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTCompteClient);
            
            new logger().oCategory.info("Suppression de compte client " + request.getParameter("lg_COMPTE_CLIENT_ID").toString());
            
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