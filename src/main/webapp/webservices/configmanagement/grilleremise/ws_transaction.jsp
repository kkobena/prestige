<%@page import="dal.TRemise"%>
<%@page import="bll.configManagement.remiseManagement"%>
<%@page import="dal.TGrilleRemise"%>
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
<%@page import="bll.configManagement.familleManagement"  %>

<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();


%>




<%    int str_CODE_GRILLE = 0;
    String str_DESCRIPTION = "", lg_REMISE_ID = "", lg_GRILLE_REMISE_ID = "";
    double dbl_TAUX = 0.0;
    if (request.getParameter("str_CODE_GRILLE") != null && !request.getParameter("str_CODE_GRILLE").equalsIgnoreCase("")) {
        str_CODE_GRILLE = Integer.parseInt(request.getParameter("str_CODE_GRILLE"));
        new logger().oCategory.info("str_CODE_GRILLE : " + request.getParameter("str_CODE_GRILLE"));
    }
    if (request.getParameter("lg_REMISE_ID") != null && !request.getParameter("lg_REMISE_ID").equalsIgnoreCase("")) {
        lg_REMISE_ID = request.getParameter("lg_REMISE_ID");
        new logger().oCategory.info("lg_REMISE_ID : " + request.getParameter("lg_REMISE_ID"));
    }
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
        new logger().oCategory.info("str_DESCRIPTION : " + request.getParameter("str_DESCRIPTION"));
    }
    if (request.getParameter("lg_GRILLE_REMISE_ID") != null) {
        lg_GRILLE_REMISE_ID = request.getParameter("lg_GRILLE_REMISE_ID");
        new logger().oCategory.info("lg_GRILLE_REMISE_ID : " + request.getParameter("lg_GRILLE_REMISE_ID"));
    }

    if (request.getParameter("dbl_TAUX") != null) {
        dbl_TAUX = Double.parseDouble(request.getParameter("dbl_TAUX"));
        new logger().oCategory.info("dbl_TAUX : " + request.getParameter("dbl_TAUX"));
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
    new logger().oCategory.info("str_CODE_GRILLE " + request.getParameter("str_CODE_GRILLE"));

    remiseManagement OremiseManagement = new remiseManagement(OdataManager);

    if (request.getParameter("mode") != null) {
        new logger().OCategory.info(" lg_REMISE_ID   " + lg_REMISE_ID);
        if (request.getParameter("mode").toString().equals("create")) {
            OremiseManagement.AddGrilleToRemise(str_CODE_GRILLE, str_DESCRIPTION, dbl_TAUX, lg_REMISE_ID);
            ObllBase.setMessage(OremiseManagement.getMessage());
            ObllBase.setDetailmessage(OremiseManagement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("update")) {
            OremiseManagement.UpdateGrilleOfRemise(lg_GRILLE_REMISE_ID, str_CODE_GRILLE, str_DESCRIPTION, dbl_TAUX, lg_REMISE_ID);
            ObllBase.setMessage(OremiseManagement.getMessage());
            ObllBase.setDetailmessage(OremiseManagement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OremiseManagement.DeleteGrilleOfRemise(lg_GRILLE_REMISE_ID);
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