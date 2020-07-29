<%@page import="bll.userManagement.user"%>
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
    date key = new date();
    dal.TUserFone OTUserFone = null;


%>




<%    String lg_USER_FONE_ID = "%%", str_PHONE = "%%", lg_USER_ID = "%%";
    Integer int_PRICE;
    Integer int_STOCK_MINIMAL;
    if (request.getParameter("lg_USER_FONE_ID") != null) {
        lg_USER_FONE_ID = request.getParameter("lg_USER_FONE_ID");
        
    new logger().oCategory.info("lg_USER_FONE_ID " + lg_USER_FONE_ID);
    }
    
    if (request.getParameter("str_PHONE") != null) {
        str_PHONE = request.getParameter("str_PHONE");
        new logger().oCategory.info("str_PHONE " + str_PHONE);
    }
    
     if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().oCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    //new logger().oCategory.info("ID " + request.getParameter("lg_INSTITUTION_ID"));
    user Ouser = new user(OdataManager);
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            Ouser.createUserPhone(lg_USER_ID, str_PHONE);
            ObllBase.setMessage(Ouser.getMessage());
            ObllBase.setDetailmessage(Ouser.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("update")) {
            Ouser.updateUserPhone(lg_USER_FONE_ID, str_PHONE);
            ObllBase.setMessage(Ouser.getMessage());
            ObllBase.setDetailmessage(Ouser.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            Ouser.removeUserPhone(lg_USER_FONE_ID);
            ObllBase.setMessage(Ouser.getMessage());
            ObllBase.setDetailmessage(Ouser.getDetailmessage());
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