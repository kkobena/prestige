<%@page import="bll.userManagement.user"%>
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

    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();


%>




<%    String str_FIRST_NAME = "", str_LAST_NAME = "", str_PASSWORD = "";
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    
    OdataManager.initEntityManager();
     TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    user Ouser = new user(OdataManager);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    if (request.getParameter("str_FIRST_NAME") != null) {
        str_FIRST_NAME = request.getParameter("str_FIRST_NAME");
        new logger().OCategory.info("str_FIRST_NAME " + str_FIRST_NAME);
    }

    if (request.getParameter("str_LAST_NAME") != null) {
        str_LAST_NAME = request.getParameter("str_LAST_NAME");
        new logger().OCategory.info("str_LAST_NAME " + str_LAST_NAME);
    }

    if (request.getParameter("str_PASSWORD") != null) {
        str_PASSWORD = request.getParameter("str_PASSWORD");
        new logger().OCategory.info("str_PASSWORD " + str_PASSWORD);
    }

    ObllBase.setDetailmessage("PAS D'ACTION");

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("update")) {
            new logger().oCategory.info("update");

            if (Ouser.updateProfilUser(OTUser.getLgUSERID(), str_FIRST_NAME, str_LAST_NAME, str_PASSWORD)) {
                session.setAttribute(commonparameter.AIRTIME_USER, OTUser);
            }
            new logger().oCategory.info("user "+OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME());
            ObllBase.setMessage(Ouser.getMessage());
            ObllBase.setDetailmessage(Ouser.getDetailmessage());

        } else if (request.getParameter("mode").toString().equals("validate")) {
        } else if (request.getParameter("mode").toString().equals("delete")) {
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