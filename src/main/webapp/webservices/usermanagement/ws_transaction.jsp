<%@page import="bll.userManagement.authentification"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<% 
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    
    

%>




<%
   
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
  TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    bll.userManagement.authentification Oauthentification = new authentification(OdataManager);
    
    if (request.getParameter("mode") != null) {
        
        if (request.getParameter("mode").equals("deconnexion")) {
            new logger().oCategory.info("deconnexion");
            try {
                user = Oauthentification.SetUserConnexionStateAtDeconnexion(OTUser.getLgUSERID());
                ObllBase.setMessage(Oauthentification.getMessage());
                ObllBase.setDetailmessage(Oauthentification.getDetailmessage());
            } catch (Exception e) {
                new logger().OCategory.info(" Error  " + e.toString());
            }
            
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