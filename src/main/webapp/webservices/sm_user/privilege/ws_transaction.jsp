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

<%  String str_NAME = "", lg_PRIVELEGE_ID = "", str_DESCRIPTION = "", str_TYPE = "";
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TPrivilege OTprivilege = null;

%>




<%
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
    }
    if (request.getParameter("lg_PRIVELEGE_ID") != null) {
        lg_PRIVELEGE_ID = request.getParameter("lg_PRIVELEGE_ID");
    }
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
     
    if (request.getParameter("mode") != null) {
        
        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");
            
        } else if (request.getParameter("mode").toString().equals("update")) {
            
            if (request.getParameter("lg_PRIVELEGE_ID").toString().equals("init")) {
                
                OTprivilege = new dal.TPrivilege();
                OTprivilege.setLgPRIVELEGEID(key.getComplexId());
                OTprivilege.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                OTprivilege.setStrNAME(request.getParameter("str_NAME").toString());
                OTprivilege.setDtCREATED(new Date());
                OTprivilege.setStrSTATUT(commonparameter.statut_enable);
                
                ObllBase.persiste(OTprivilege);
                new logger().oCategory.info("Creation  TPrivilege " + OTprivilege.getLgPRIVELEGEID() + " StrLabel " + OTprivilege.getStrNAME());
                
            } else {
                new logger().oCategory.info("Ref " + request.getParameter("lg_PRIVELEGE_ID").toString());
                OTprivilege = OdataManager.getEm().find(dal.TPrivilege.class, request.getParameter("lg_PRIVELEGE_ID").toString());
                OTprivilege.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                OTprivilege.setStrNAME(request.getParameter("str_NAME").toString());
                
                ObllBase.persiste(OTprivilege);
                new logger().oCategory.info("Mise a jour TPrivilege " + OTprivilege.getLgPRIVELEGEID() + " StrLabel " + OTprivilege.getStrNAME());
                
            }
        } else if (request.getParameter("mode").toString().equals("delete")) {
            
            OTprivilege = ObllBase.getOdataManager().getEm().find(dal.TPrivilege.class, request.getParameter("lg_PRIVELEGE_ID"));
            
            OTprivilege.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTprivilege);

            /* if( !ObllBase.delete(OTprivilege)){
             ObllBase.setDetailmessage("Impossible de supprimer");
             }*/
            new logger().oCategory.info("Suppression du privilege " + request.getParameter("lg_PRIVELEGE_ID").toString());
            
        } else {
        }
        
    }
    
    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:1,lg_PRIVELEGE_ID: \"" + OTprivilege.getLgPRIVELEGEID() + "\" }";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>