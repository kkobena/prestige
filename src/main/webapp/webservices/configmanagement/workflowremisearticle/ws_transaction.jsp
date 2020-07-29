<%@page import="dal.TWorkflowRemiseArticle"%>
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
<%@page import="bll.configManagement.WorkflowRemiseArticleManagement"  %>


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
   String lg_WORKFLOW_REMISE_ARTICLE_ID ="%%", str_DESCRIPTION ="%%", str_CODE_REMISE_ARTICLE = "";
   int str_CODE_GRILLE_VO = 0, str_CODE_GRILLE_VNO = 0;



%>

<%
    // lg_WORKFLOW_REMISE_ARTICLE_ID
    if (request.getParameter("lg_WORKFLOW_REMISE_ARTICLE_ID") != null) {
        lg_WORKFLOW_REMISE_ARTICLE_ID = request.getParameter("lg_WORKFLOW_REMISE_ARTICLE_ID");
    }   
    // str_DESCRIPTION
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
    }
    // str_CODE_REMISE_ARTICLE
    if (request.getParameter("str_CODE_REMISE_ARTICLE") != null) {
        str_CODE_REMISE_ARTICLE = request.getParameter("str_CODE_REMISE_ARTICLE");
    }
    // str_CODE_GRILLE_VO
    if (request.getParameter("str_CODE_GRILLE_VO") != null) {
        str_CODE_GRILLE_VO = Integer.parseInt(request.getParameter("str_CODE_GRILLE_VO"));
    }
    // str_CODE_GRILLE_VNO
    if (request.getParameter("str_CODE_GRILLE_VNO") != null) {
        str_CODE_GRILLE_VNO = Integer.parseInt(request.getParameter("str_CODE_GRILLE_VNO"));
    }
    
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_WORKFLOW_REMISE_ARTICLE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");

            
            
            WorkflowRemiseArticleManagement OWorkflowRemiseArticleManagement = new WorkflowRemiseArticleManagement(OdataManager);
            OWorkflowRemiseArticleManagement.create(str_DESCRIPTION, str_CODE_REMISE_ARTICLE, str_CODE_GRILLE_VO, str_CODE_GRILLE_VNO);

            new logger().oCategory.info("Creation  OOKKK");

        } else if (request.getParameter("mode").toString().equals("update")) {

            WorkflowRemiseArticleManagement OWorkflowRemiseArticleManagement = new WorkflowRemiseArticleManagement(OdataManager);
            OWorkflowRemiseArticleManagement.update(lg_WORKFLOW_REMISE_ARTICLE_ID, str_DESCRIPTION, str_CODE_REMISE_ARTICLE, str_CODE_GRILLE_VO, str_CODE_GRILLE_VNO);

            new logger().oCategory.info("Modif OK  OOKKK");

        } else if (request.getParameter("mode").toString().equals("delete")) {

            TWorkflowRemiseArticle OTWorkflowRemiseArticle = null;
            
            OTWorkflowRemiseArticle = ObllBase.getOdataManager().getEm().find(TWorkflowRemiseArticle.class, lg_WORKFLOW_REMISE_ARTICLE_ID);
            
            OTWorkflowRemiseArticle.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTWorkflowRemiseArticle);

            new logger().oCategory.info("Suppression du Work flow remise article " + request.getParameter("lg_WORKFLOW_REMISE_ARTICLE_ID").toString());

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