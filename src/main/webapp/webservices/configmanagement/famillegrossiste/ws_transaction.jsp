<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.configManagement.familleGrossisteManagement"%>
<%@page import="dal.TFamillearticle"%>
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
<%@page import="bll.configManagement.familleArticleManagement"  %>
<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

%>

<%    String lg_FAMILLE_GROSSISTE_ID = "", str_CODE_ARTICLE = "", lg_GROSSISTE_ID = "", lg_FAMILLE_ID = "";
    int int_PRICE = 0, int_PAF = 0;

    if (request.getParameter("int_PRICE") != null) {
        int_PRICE = Integer.parseInt(request.getParameter("int_PRICE"));
        new logger().OCategory.info("int_PRICE " + int_PRICE);
    }

    if (request.getParameter("int_PAF") != null) {
        int_PAF = Integer.parseInt(request.getParameter("int_PAF"));
        new logger().OCategory.info("int_PAF " + int_PAF);
    }

    if (request.getParameter("lg_FAMILLE_GROSSISTE_ID") != null) {
        lg_FAMILLE_GROSSISTE_ID = request.getParameter("lg_FAMILLE_GROSSISTE_ID");
        new logger().OCategory.info("lg_FAMILLE_GROSSISTE_ID " + lg_FAMILLE_GROSSISTE_ID);
    }
    // str_CODE_ARTICLE
    if (request.getParameter("str_CODE_ARTICLE") != null) {
        str_CODE_ARTICLE = request.getParameter("str_CODE_ARTICLE");
        new logger().OCategory.info("str_CODE_ARTICLE " + str_CODE_ARTICLE);
    }
    // lg_GROSSISTE_ID
    if (request.getParameter("lg_GROSSISTE_ID") != null) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }
    // lg_FAMILLE_ID
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
 TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    familleGrossisteManagement OfamilleGrossisteManagement = new familleGrossisteManagement(OdataManager);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("create")) {
            OfamilleGrossisteManagement.create(lg_GROSSISTE_ID, lg_FAMILLE_ID, str_CODE_ARTICLE, int_PRICE, int_PAF);
            ObllBase.setMessage(OfamilleGrossisteManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleGrossisteManagement.getDetailmessage());
        } else if (request.getParameter("mode").equals("update")) { 
            OfamilleGrossisteManagement.updateFamilleGrossiste(lg_FAMILLE_GROSSISTE_ID, lg_GROSSISTE_ID, lg_FAMILLE_ID, str_CODE_ARTICLE);
            ObllBase.setMessage(OfamilleGrossisteManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleGrossisteManagement.getDetailmessage());
        } else if (request.getParameter("mode").equals("delete")) {
            OfamilleGrossisteManagement.delete(lg_FAMILLE_GROSSISTE_ID);
            ObllBase.setMessage(OfamilleGrossisteManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleGrossisteManagement.getDetailmessage());
        } else if (request.getParameter("mode").equals("checkdispoproduct")) { 
          //OfamilleGrossisteManagement.isProductDispo(lg_FAMILLE_GROSSISTE_ID);
            ObllBase.setDetailmessage(OfamilleGrossisteManagement.getDetailmessage());
            ObllBase.setMessage(OfamilleGrossisteManagement.getMessage());
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