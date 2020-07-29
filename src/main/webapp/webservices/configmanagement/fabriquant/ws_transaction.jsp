<%@page import="bll.configManagement.FabricantManagement"%>
<%@page import="dal.TFabriquant"%>
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




<%    String lg_FABRIQUANT_ID = "", str_CODE = "", str_NAME = "", str_DESCRIPTION = "", str_ADRESSE = "", str_TELEPHONE = "";
    if (request.getParameter("lg_FABRIQUANT_ID") != null) {
        lg_FABRIQUANT_ID = request.getParameter("lg_FABRIQUANT_ID");
        new logger().oCategory.info("lg_FABRIQUANT_ID : " + request.getParameter("lg_FABRIQUANT_ID"));
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
    }
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
    }
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
    }
    if (request.getParameter("str_ADRESSE") != null) {
        str_ADRESSE = request.getParameter("str_ADRESSE");
    }
    if (request.getParameter("str_TELEPHONE") != null) {
        str_TELEPHONE = request.getParameter("str_TELEPHONE");
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("REMISE WS TRANSACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("lg_FABRIQUANT_ID " + request.getParameter("lg_FABRIQUANT_ID"));
    FabricantManagement OFabricantManagement = new FabricantManagement(OdataManager);

    if (request.getParameter("mode") != null) {

        //MODE CREATION
        if (request.getParameter("mode").toString().equals("create")) {
            OFabricantManagement.create(str_CODE, str_NAME, str_DESCRIPTION, str_ADRESSE, str_TELEPHONE);
            ObllBase.setMessage(OFabricantManagement.getMessage());
            ObllBase.setDetailmessage(OFabricantManagement.getDetailmessage());
            //MODE MODIFICATION
        } else if (request.getParameter("mode").toString().equals("update")) {

            
            OFabricantManagement.update(lg_FABRIQUANT_ID, str_CODE, str_NAME, str_DESCRIPTION, str_ADRESSE, str_TELEPHONE);
            ObllBase.setMessage(OFabricantManagement.getMessage());
            ObllBase.setDetailmessage(OFabricantManagement.getDetailmessage());
            //MODE SUPPRESSION
        } else if (request.getParameter("mode").toString().equals("delete")) {

           OFabricantManagement.deleteFabriquant(lg_FABRIQUANT_ID);
           ObllBase.setMessage(OFabricantManagement.getMessage());
            ObllBase.setDetailmessage(OFabricantManagement.getDetailmessage());
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