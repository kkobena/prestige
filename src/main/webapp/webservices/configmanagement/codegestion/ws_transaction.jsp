<%@page import="bll.configManagement.CodeGestionManager"%>
<%@page import="dal.TCodeGestion"%>
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


%>




<%    String lg_CODE_GESTION_ID = "", str_CODE_BAREME = "", lg_OPTIMISATION_QUANTITE_ID = "1";

    int int_JOURS_COUVERTURE_STOCK = 0, int_MOIS_HISTORIQUE_VENTE = 1, int_COEFFICIENT_PONDERATION = 1,
            int_DATE_BUTOIR_ARTICLE = 1, int_DATE_LIMITE_EXTRAPOLATION = 1,
            int_COEFFICIENT_PONDERATION1 = 1, int_COEFFICIENT_PONDERATION2 = 1, int_COEFFICIENT_PONDERATION3 = 1,
            int_COEFFICIENT_PONDERATION4 = 1, int_COEFFICIENT_PONDERATION5 = 1, int_COEFFICIENT_PONDERATION6 = 1;

    boolean bool_OPTIMISATION_SEUIL_CMDE = false;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");

    if (request.getParameter("lg_CODE_GESTION_ID") != null) {
        lg_CODE_GESTION_ID = request.getParameter("lg_CODE_GESTION_ID");
        new logger().OCategory.info("lg_CODE_GESTION_ID : " + lg_CODE_GESTION_ID);
    }
    if (request.getParameter("str_CODE_BAREME") != null) {
        str_CODE_BAREME = request.getParameter("str_CODE_BAREME");
        new logger().OCategory.info("str_CODE_BAREME : " + str_CODE_BAREME);
    }
    if (request.getParameter("int_JOURS_COUVERTURE_STOCK") != null && !request.getParameter("int_JOURS_COUVERTURE_STOCK").equalsIgnoreCase("")) {
        int_JOURS_COUVERTURE_STOCK = Integer.parseInt(request.getParameter("int_JOURS_COUVERTURE_STOCK"));
        new logger().OCategory.info("int_JOURS_COUVERTURE_STOCK : " + int_JOURS_COUVERTURE_STOCK);
    }
    if (request.getParameter("int_MOIS_HISTORIQUE_VENTE") != null && !request.getParameter("int_MOIS_HISTORIQUE_VENTE").equalsIgnoreCase("")) {
        int_MOIS_HISTORIQUE_VENTE = Integer.parseInt(request.getParameter("int_MOIS_HISTORIQUE_VENTE"));
        new logger().OCategory.info("int_MOIS_HISTORIQUE_VENTE : " + int_MOIS_HISTORIQUE_VENTE);
    }

    // int_DATE_BUTOIR_ARTICLE  
    if (request.getParameter("int_DATE_BUTOIR_ARTICLE") != null && !request.getParameter("int_DATE_BUTOIR_ARTICLE").equalsIgnoreCase("")) {
        int_DATE_BUTOIR_ARTICLE = Integer.parseInt(request.getParameter("int_DATE_BUTOIR_ARTICLE"));
        new logger().OCategory.info("int_DATE_BUTOIR_ARTICLE : " + int_DATE_BUTOIR_ARTICLE);
    }

    // int_DATE_LIMITE_EXTRAPOLATION  
    if (request.getParameter("int_DATE_LIMITE_EXTRAPOLATION") != null && !request.getParameter("int_DATE_LIMITE_EXTRAPOLATION").equalsIgnoreCase("")) {
        int_DATE_LIMITE_EXTRAPOLATION = Integer.parseInt(request.getParameter("int_DATE_LIMITE_EXTRAPOLATION"));
        new logger().OCategory.info("int_DATE_LIMITE_EXTRAPOLATION : " + int_DATE_LIMITE_EXTRAPOLATION);
    }

    if (request.getParameter("bool_OPTIMISATION_SEUIL_CMDE") != null) {
        bool_OPTIMISATION_SEUIL_CMDE = Boolean.parseBoolean(request.getParameter("bool_OPTIMISATION_SEUIL_CMDE"));
        new logger().OCategory.info("bool_OPTIMISATION_SEUIL_CMDE : " + bool_OPTIMISATION_SEUIL_CMDE);
    }
    if (request.getParameter("int_COEFFICIENT_PONDERATION") != null && !request.getParameter("int_COEFFICIENT_PONDERATION").equalsIgnoreCase("")) {
        int_COEFFICIENT_PONDERATION = Integer.parseInt(request.getParameter("int_COEFFICIENT_PONDERATION"));
        new logger().OCategory.info("int_COEFFICIENT_PONDERATION : " + int_COEFFICIENT_PONDERATION);
    }
    if (request.getParameter("lg_OPTIMISATION_QUANTITE_ID") != null && !request.getParameter("lg_OPTIMISATION_QUANTITE_ID").equalsIgnoreCase("")) {
        lg_OPTIMISATION_QUANTITE_ID = request.getParameter("lg_OPTIMISATION_QUANTITE_ID");
        new logger().OCategory.info("lg_OPTIMISATION_QUANTITE_ID : " + lg_OPTIMISATION_QUANTITE_ID);
    }

    if (request.getParameter("int_COEFFICIENT_PONDERATION1") != null && !request.getParameter("int_COEFFICIENT_PONDERATION1").equalsIgnoreCase("")) {
        int_COEFFICIENT_PONDERATION1 = Integer.parseInt(request.getParameter("int_COEFFICIENT_PONDERATION1"));
        new logger().OCategory.info("int_COEFFICIENT_PONDERATION1 : " + int_COEFFICIENT_PONDERATION1);
    }
    
    if (request.getParameter("int_COEFFICIENT_PONDERATION2") != null && !request.getParameter("int_COEFFICIENT_PONDERATION2").equalsIgnoreCase("")) {
        int_COEFFICIENT_PONDERATION2 = Integer.parseInt(request.getParameter("int_COEFFICIENT_PONDERATION2"));
        new logger().OCategory.info("int_COEFFICIENT_PONDERATION2 : " + int_COEFFICIENT_PONDERATION2);
    }
    
    if (request.getParameter("int_COEFFICIENT_PONDERATION3") != null && !request.getParameter("int_COEFFICIENT_PONDERATION3").equalsIgnoreCase("")) {
        int_COEFFICIENT_PONDERATION3 = Integer.parseInt(request.getParameter("int_COEFFICIENT_PONDERATION3"));
        new logger().OCategory.info("int_COEFFICIENT_PONDERATION3 : " + int_COEFFICIENT_PONDERATION3);
    }

    if (request.getParameter("int_COEFFICIENT_PONDERATION4") != null && !request.getParameter("int_COEFFICIENT_PONDERATION4").equalsIgnoreCase("")) {
        int_COEFFICIENT_PONDERATION4 = Integer.parseInt(request.getParameter("int_COEFFICIENT_PONDERATION4"));
        new logger().OCategory.info("int_COEFFICIENT_PONDERATION4 : " + int_COEFFICIENT_PONDERATION4);
    }
    
    if (request.getParameter("int_COEFFICIENT_PONDERATION5") != null && !request.getParameter("int_COEFFICIENT_PONDERATION5").equalsIgnoreCase("")) {
        int_COEFFICIENT_PONDERATION5 = Integer.parseInt(request.getParameter("int_COEFFICIENT_PONDERATION5"));
        new logger().OCategory.info("int_COEFFICIENT_PONDERATION5 : " + int_COEFFICIENT_PONDERATION5);
    }
    
    if (request.getParameter("int_COEFFICIENT_PONDERATION6") != null && !request.getParameter("int_COEFFICIENT_PONDERATION6").equalsIgnoreCase("")) {
        int_COEFFICIENT_PONDERATION6 = Integer.parseInt(request.getParameter("int_COEFFICIENT_PONDERATION6"));
        new logger().OCategory.info("int_COEFFICIENT_PONDERATION6 : " + int_COEFFICIENT_PONDERATION6);
    }
    
    new logger().OCategory.info("le mode : " + request.getParameter("mode"));
    
    CodeGestionManager OCodeGestionManager = new CodeGestionManager(OdataManager, OTUser);
    
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            OCodeGestionManager.createCodeGestion(str_CODE_BAREME, int_JOURS_COUVERTURE_STOCK, int_MOIS_HISTORIQUE_VENTE, int_DATE_BUTOIR_ARTICLE, int_DATE_LIMITE_EXTRAPOLATION, bool_OPTIMISATION_SEUIL_CMDE, int_COEFFICIENT_PONDERATION, lg_OPTIMISATION_QUANTITE_ID, int_COEFFICIENT_PONDERATION1, int_COEFFICIENT_PONDERATION2, int_COEFFICIENT_PONDERATION3, int_COEFFICIENT_PONDERATION4, int_COEFFICIENT_PONDERATION5, int_COEFFICIENT_PONDERATION6);
            ObllBase.setMessage(OCodeGestionManager.getMessage());
            ObllBase.setDetailmessage(OCodeGestionManager.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("update")) {
            OCodeGestionManager.updateCodeGestion(lg_CODE_GESTION_ID, str_CODE_BAREME, int_JOURS_COUVERTURE_STOCK, int_MOIS_HISTORIQUE_VENTE, int_DATE_BUTOIR_ARTICLE, int_DATE_LIMITE_EXTRAPOLATION, bool_OPTIMISATION_SEUIL_CMDE, int_COEFFICIENT_PONDERATION, lg_OPTIMISATION_QUANTITE_ID, int_COEFFICIENT_PONDERATION1, int_COEFFICIENT_PONDERATION2, int_COEFFICIENT_PONDERATION3, int_COEFFICIENT_PONDERATION4, int_COEFFICIENT_PONDERATION5, int_COEFFICIENT_PONDERATION6);
            ObllBase.setMessage(OCodeGestionManager.getMessage());
            ObllBase.setDetailmessage(OCodeGestionManager.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OCodeGestionManager.deleteCodeGestion(lg_CODE_GESTION_ID);
            ObllBase.setMessage(OCodeGestionManager.getMessage());
            ObllBase.setDetailmessage(OCodeGestionManager.getDetailmessage());
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