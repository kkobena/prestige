<%@page import="bll.utils.TparameterManager"%>
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

<%  String str_KEY = "", str_VALUE = "", str_DESCRIPTION = "";
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();


%>




<%
    if (request.getParameter("str_VALUE") != null) {
        str_VALUE = request.getParameter("str_VALUE");
        new logger().oCategory.info("str_VALUE " + str_VALUE);
    }
    if (request.getParameter("str_KEY") != null) {
        str_KEY = request.getParameter("str_KEY");
        new logger().oCategory.info("str_KEY " + str_KEY);
    }
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
        new logger().oCategory.info("str_DESCRIPTION " + str_DESCRIPTION);
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    TparameterManager OTparameterManager = new TparameterManager(OdataManager);

    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("str_KEY"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
        } else if (request.getParameter("mode").toString().equals("update")) {

            OTparameterManager.updateParameter(str_KEY, str_VALUE, str_DESCRIPTION);
               
        } else if (request.getParameter("mode").toString().equals("delete")) {

            } else {
        }

    }

    String result;
    if (OTparameterManager.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + OTparameterManager.getMessage() + "\", errors: \"" + OTparameterManager.getDetailmessage() + "\"}";
    } else {
        result = "{success:\"" + OTparameterManager.getMessage() + "\", errors: \"" + OTparameterManager.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>