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
    TFamillearticle OTFamillearticle = new TFamillearticle();

%>

<%    String lg_FAMILLEARTICLE_ID = "", str_LIBELLE = "", str_CODE_FAMILLE = "",
            str_COMMENTAIRE = "", lg_GROUPE_FAMILLE_ID = "1";

    if (request.getParameter("lg_FAMILLEARTICLE_ID") != null) {
        lg_FAMILLEARTICLE_ID = request.getParameter("lg_FAMILLEARTICLE_ID");
    }
    // str_LIBELLE
    if (request.getParameter("str_LIBELLE") != null) {
        str_LIBELLE = request.getParameter("str_LIBELLE");
    }
    // str_CODE_FAMILLE
    if (request.getParameter("str_CODE_FAMILLE") != null) {
        str_CODE_FAMILLE = request.getParameter("str_CODE_FAMILLE");
    }
    // str_COMMENTAIRE
    if (request.getParameter("str_COMMENTAIRE") != null) {
        str_COMMENTAIRE = request.getParameter("str_COMMENTAIRE");
    }
    // lg_GROUPE_FAMILLE_ID
    if (request.getParameter("lg_GROUPE_FAMILLE_ID") != null && !request.getParameter("lg_GROUPE_FAMILLE_ID").equalsIgnoreCase("")) {
        lg_GROUPE_FAMILLE_ID = request.getParameter("lg_GROUPE_FAMILLE_ID");
        new logger().OCategory.info("lg_GROUPE_FAMILLE_ID " + lg_GROUPE_FAMILLE_ID);
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    familleArticleManagement OfamilleArticleManagement = new familleArticleManagement(OdataManager);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            OfamilleArticleManagement.create(str_CODE_FAMILLE, str_LIBELLE, str_COMMENTAIRE, lg_GROUPE_FAMILLE_ID);
            ObllBase.setMessage(OfamilleArticleManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleArticleManagement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("update")) {
            OfamilleArticleManagement.update(lg_FAMILLEARTICLE_ID, str_CODE_FAMILLE, str_LIBELLE, str_COMMENTAIRE, lg_GROUPE_FAMILLE_ID);
            ObllBase.setMessage(OfamilleArticleManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleArticleManagement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OfamilleArticleManagement.deleteFamilleArticle(lg_FAMILLEARTICLE_ID);
            ObllBase.setMessage(OfamilleArticleManagement.getMessage());
            ObllBase.setDetailmessage(OfamilleArticleManagement.getDetailmessage());

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