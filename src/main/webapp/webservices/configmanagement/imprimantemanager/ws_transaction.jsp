<%@page import="bll.configManagement.PrinterManager"%>
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
<%@page import="bll.userManagement.*"  %>



<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();

%>




<%    String str_DESCRIPTION = "", str_NAME = "", lg_IMPRIMANTE_ID = "";

    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
        new logger().oCategory.info("str_DESCRIPTION : " + str_DESCRIPTION);
    }
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
        new logger().oCategory.info("str_NAME : " + str_NAME);
    }

    if (request.getParameter("lg_IMPRIMANTE_ID") != null) {
        lg_IMPRIMANTE_ID = request.getParameter("lg_IMPRIMANTE_ID");
        new logger().oCategory.info("lg_IMPRIMANTE_ID : " + lg_IMPRIMANTE_ID);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
     TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    PrinterManager OPrinterManager = new PrinterManager(OdataManager);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_USER_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            OPrinterManager.createImprimante(str_NAME, str_DESCRIPTION);
            ObllBase.setMessage(OPrinterManager.getMessage());
            ObllBase.setDetailmessage(OPrinterManager.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("update")) {
            OPrinterManager.updateImprimante(lg_IMPRIMANTE_ID, str_NAME, str_DESCRIPTION);
            ObllBase.setMessage(OPrinterManager.getMessage());
            ObllBase.setDetailmessage(OPrinterManager.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OPrinterManager.deleteImprimante(lg_IMPRIMANTE_ID);
            ObllBase.setMessage(OPrinterManager.getMessage());
            ObllBase.setDetailmessage(OPrinterManager.getDetailmessage());

        } else {
        }

    }

    String result;

    if (ObllBase.getMessage()
            .equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }

    new logger().OCategory.info("JSON " + result);


%>
<%=result%>