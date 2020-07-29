<%@page import="bll.configManagement.remiseManagement"%>
<%@page import="dal.TRemise"%>
<%@page import="dal.TTypeRemise"%>
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

%>




<%    String lg_REMISE_ID = "", str_NAME = "", str_CODE = "", lg_TYPE_REMISE_ID = "54291528198233221411";
    int str_IDS = 0;
    double dbl_TAUX = 1.0;

    if (request.getParameter("lg_REMISE_ID") != null) {
        lg_REMISE_ID = request.getParameter("lg_REMISE_ID");
        new logger().oCategory.info("lg_REMISE_ID " + lg_REMISE_ID);
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
        new logger().oCategory.info("str_CODE " + str_CODE);
    }
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
        new logger().oCategory.info("str_NAME " + str_NAME);
    }
    if (request.getParameter("str_IDS") != null && !request.getParameter("str_IDS").equalsIgnoreCase("")) {
        str_IDS = Integer.parseInt(request.getParameter("str_IDS"));
        new logger().oCategory.info("str_IDS " + str_IDS);
    }
    if (request.getParameter("dbl_TAUX") != null && !request.getParameter("dbl_TAUX").equalsIgnoreCase("")) {
        dbl_TAUX = Integer.parseInt(request.getParameter("dbl_TAUX"));
        new logger().oCategory.info("dbl_TAUX " + dbl_TAUX);
    }
    if (request.getParameter("lg_TYPE_REMISE_ID") != null && !request.getParameter("lg_TYPE_REMISE_ID").equalsIgnoreCase("")) {
        lg_TYPE_REMISE_ID = request.getParameter("lg_TYPE_REMISE_ID");
        new logger().oCategory.info("str_IDS " + str_IDS);
    }
    // str_CODE_GRILLE

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    remiseManagement OremiseManagement = new remiseManagement(OdataManager);

    ObllBase.setDetailmessage("REMISE WS TRANSACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("lg_REMISE_ID " + request.getParameter("lg_REMISE_ID"));

    if (request.getParameter("mode") != null) {

        //MODE CREATION
        if (request.getParameter("mode").toString().equals("create")) {
            OremiseManagement.createTRemise(str_CODE, str_NAME, str_IDS, lg_TYPE_REMISE_ID, dbl_TAUX);
            ObllBase.setMessage(OremiseManagement.getMessage());
            ObllBase.setDetailmessage(OremiseManagement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("update")) {
            OremiseManagement.update(lg_REMISE_ID, str_CODE, str_NAME, str_IDS, lg_TYPE_REMISE_ID, dbl_TAUX);
            ObllBase.setMessage(OremiseManagement.getMessage());
            ObllBase.setDetailmessage(OremiseManagement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OremiseManagement.deleteRemise(lg_REMISE_ID);
            ObllBase.setMessage(OremiseManagement.getMessage());
            ObllBase.setDetailmessage(OremiseManagement.getDetailmessage());
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