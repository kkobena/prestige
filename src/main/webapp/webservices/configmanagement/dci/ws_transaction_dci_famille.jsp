<%@page import="bll.configManagement.dciManagement"%>
<%@page import="dal.TFamille"%>
<%@page import="dal.TTypeetiquette"%>
<%@page import="dal.TFamilleDci"%>
<%@page import="dal.TDci"%>
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
    dataManager OdataManager = new dataManager();
    date key = new date();

    TDci OTDci = null;


%>



<%    String lg_DCI_ID = "", lg_FAMILLE_ID = "", lg_FAMILLE_DCI_ID = "";

    if (request.getParameter("lg_DCI_ID") != null) {
        lg_DCI_ID = request.getParameter("lg_DCI_ID");
        new logger().oCategory.info("lg_DCI_ID : " + lg_DCI_ID);
    }
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().oCategory.info("lg_FAMILLE_ID : " + lg_FAMILLE_ID);
    }
    if (request.getParameter("lg_FAMILLE_DCI_ID") != null) {
        lg_FAMILLE_DCI_ID = request.getParameter("lg_FAMILLE_DCI_ID");
        new logger().oCategory.info("lg_FAMILLE_DCI_ID : " + lg_FAMILLE_DCI_ID);
    }
    OdataManager.initEntityManager();

    dciManagement OdciManagement = new dciManagement(OdataManager);

    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
 

    //new logger().oCategory.info("str_NAME   @@@@@@@@@@@@@@@@     " + request.getParameter("str_NAME"));
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            OdciManagement.createFamilleDci(lg_DCI_ID, lg_FAMILLE_ID);
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OdciManagement.deleteFamilleDci(lg_FAMILLE_DCI_ID);
        }
    }

    String result;
    if (OdciManagement.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + OdciManagement.getMessage() + "\", errors: \"" + OdciManagement.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + OdciManagement.getMessage() + "\", errors: \"" + OdciManagement.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>