<%@page import="bll.stockManagement.StockManager"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="java.io.File"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="java.io.File"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="org.apache.commons.fileupload.DiskFileUpload"%>
<%@page import="org.apache.commons.fileupload.FileUpload"%>
<%@page import="java.io.File"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TOptimisationQuantite"%>
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
    date key = new date();


%>



<%    String lg_FAMILLE_ID = "%%", lg_TYPEETIQUETTE_ID = "", lg_ETIQUETTE_ID = "%%";
    int int_NUMBER = 0;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (request.getParameter("lg_ETIQUETTE_ID") != null) {
        lg_ETIQUETTE_ID = request.getParameter("lg_ETIQUETTE_ID");
        new logger().OCategory.info("lg_ETIQUETTE_ID " + lg_ETIQUETTE_ID);
    }

    if (request.getParameter("lg_TYPEETIQUETTE_ID") != null) {
        lg_TYPEETIQUETTE_ID = request.getParameter("lg_TYPEETIQUETTE_ID");
        new logger().OCategory.info("lg_TYPEETIQUETTE_ID " + lg_TYPEETIQUETTE_ID);
    }

    if (request.getParameter("int_NUMBER") != null) {
        int_NUMBER = Integer.parseInt(request.getParameter("int_NUMBER"));
        new logger().OCategory.info("int_NUMBER " + int_NUMBER);
    }

    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    StockManager OStockManager = new StockManager(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            /*for (int i = 0; i < int_NUMBER; i++) {
             OStockManager.createEtiquetteBis(lg_TYPEETIQUETTE_ID, lg_FAMILLE_ID, (i + 1) + "/" + int_NUMBER);
             }*/
            OStockManager.createEtiquetteBis(lg_TYPEETIQUETTE_ID, lg_FAMILLE_ID, String.valueOf(int_NUMBER));
            ObllBase.setDetailmessage(OStockManager.getDetailmessage());
            ObllBase.setMessage(OStockManager.getMessage());

        } else if (request.getParameter("mode").toString().equals("delete")) {
            OStockManager.deleteEtiquette(lg_ETIQUETTE_ID);
            ObllBase.setDetailmessage(OStockManager.getDetailmessage());
            ObllBase.setMessage(OStockManager.getMessage());
        } else if (request.getParameter("mode").toString().equals("createetiquette")) {
            OStockManager.createEtiquette(lg_FAMILLE_ID, String.valueOf(int_NUMBER), commonparameter.statut_is_Process);
            ObllBase.setDetailmessage(OStockManager.getDetailmessage());
            ObllBase.setMessage(OStockManager.getMessage());
        } else if (request.getParameter("mode").toString().equals("updateetiquette")) {
            OStockManager.updateQuantiteEtiquette(lg_ETIQUETTE_ID, String.valueOf(int_NUMBER));
            ObllBase.setDetailmessage(OStockManager.getDetailmessage());
            ObllBase.setMessage(OStockManager.getMessage());
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