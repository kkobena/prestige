<%@page import="bll.gateway.outService.ServicesUpdatePriceFamille"%>
<%@page import="bll.gateway.outService.ServiceSoldeCaisseVeille"%>
<%@page import="dal.TAlertEventUserFone"%>
<%@page import="bll.gateway.outService.ServiceSoldeCaisse"%>
<%@page import="dal.TAlertEvent"%>
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




<%    String lg_OUTBOUND_MESSAGE_ID = "%%", search_value = "";
    if (request.getParameter("lg_OUTBOUND_MESSAGE_ID") != null) {
        lg_OUTBOUND_MESSAGE_ID = request.getParameter("lg_OUTBOUND_MESSAGE_ID");
        new logger().OCategory.info("lg_OUTBOUND_MESSAGE_ID " + lg_OUTBOUND_MESSAGE_ID);
    }
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().oCategory.info("search_value : " + search_value);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    ServicesUpdatePriceFamille OServicesUpdatePriceFamille = new ServicesUpdatePriceFamille(OdataManager, OTUser);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("reload")) {
            OServicesUpdatePriceFamille.reloadSms(search_value, lg_OUTBOUND_MESSAGE_ID, 0);
            ObllBase.setMessage(OServicesUpdatePriceFamille.getMessage());
            ObllBase.setDetailmessage(OServicesUpdatePriceFamille.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("reloadall")) {
            OServicesUpdatePriceFamille.reloadSms(search_value, lg_OUTBOUND_MESSAGE_ID, 0);
            ObllBase.setMessage(OServicesUpdatePriceFamille.getMessage());
            ObllBase.setDetailmessage(OServicesUpdatePriceFamille.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {

            OTUser = ObllBase.getOdataManager().getEm().find(dal.TUser.class, request.getParameter("str_Event"));

            OTUser.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTUser);

            new logger().oCategory.info("Suppression de user " + request.getParameter("str_Event").toString());

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