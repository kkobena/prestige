<%@page import="bll.configManagement.formeArticleManagement"%>
<%@page import="dal.TFormeArticle"%>
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




<%    String lg_FORME_ARTICLE_ID = "", str_LIBELLE = "", str_CODE = "";
    if (request.getParameter("lg_FORME_ARTICLE_ID") != null) {
        lg_FORME_ARTICLE_ID = request.getParameter("lg_FORME_ARTICLE_ID");
        new logger().oCategory.info("lg_FORME_ARTICLE_ID : " + request.getParameter("lg_FORME_ARTICLE_ID"));
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
    }
    if (request.getParameter("str_LIBELLE") != null) {
        str_LIBELLE = request.getParameter("str_LIBELLE");
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    formeArticleManagement OformeArticleManagement = new formeArticleManagement(OdataManager);
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("REMISE WS TRANSACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    if (request.getParameter("mode") != null) {

        //MODE CREATION
        if (request.getParameter("mode").toString().equals("create")) {

            OformeArticleManagement.create(str_CODE, str_LIBELLE);
            ObllBase.setMessage(OformeArticleManagement.getMessage());
            ObllBase.setDetailmessage(OformeArticleManagement.getDetailmessage());

        } else if (request.getParameter("mode").toString().equals("update")) {
            OformeArticleManagement.update(lg_FORME_ARTICLE_ID, str_CODE, str_LIBELLE);

            ObllBase.setMessage(OformeArticleManagement.getMessage());
            ObllBase.setDetailmessage(OformeArticleManagement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OformeArticleManagement.deleteFormeArticle(lg_FORME_ARTICLE_ID);

            ObllBase.setMessage(OformeArticleManagement.getMessage());
            ObllBase.setDetailmessage(OformeArticleManagement.getDetailmessage());
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