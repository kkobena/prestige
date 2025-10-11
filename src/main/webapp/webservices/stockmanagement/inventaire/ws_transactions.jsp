<%@page import="org.json.JSONObject"%>
<%@page import="dal.TFamille"%>
<%@page import="dal.TInventaire"%>
<%@page import="bll.stockManagement.InventaireManager"%>
<%@page import="java.io.File"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="java.io.File"%>
<%@page import="java.io.File"%>

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



<%    TInventaire OTInventaire = null;
    String stockFilter = "ALL";
    String search_value = "", liste_article = "", str_BEGIN = "", str_END = "";
    String str_NAME = "%%", str_DESCRIPTION = "%%", lg_ZONE_GEO_ID = "%%", lg_FAMILLEARTICLE_ID = "%%", int_CIP = "%%", lg_FAMILLE_ID = "%%", lg_INVENTAIRE_ID = "%%", lg_GROSSISTE_ID = "%%", lg_INVENTAIRE_FAMILLE_ID = "%%", str_TYPE_TRANSACTION = "", lg_TYPE_STOCK_ID = "1";
    int int_PRICE = 0, int_NUMBER = 0, int_QTE_REAPPROVISIONNEMENT = 0;
    int bool_INVENTAIRE = 1;
    boolean iSChecked = true;
    Integer stockProduit = null;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");

    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
        new logger().OCategory.info("str_NAME " + str_NAME);
    }
    if (request.getParameter("stockFilter") != null) {
        stockFilter = request.getParameter("stockFilter");
        new logger().OCategory.info("stockFilter " + stockFilter);
    }
    if (request.getParameter("stockProduit") != null && !request.getParameter("stockProduit").isEmpty()) {
        stockProduit = Integer.parseInt(request.getParameter("stockProduit"));
        new logger().OCategory.info("stockProduit " + stockProduit);
    }
    if (request.getParameter("int_PRICE") != null) {
        int_PRICE = Integer.parseInt(request.getParameter("int_PRICE"));
        new logger().OCategory.info("int_PRICE " + int_PRICE);
    }
    if (request.getParameter("int_QTE_REAPPROVISIONNEMENT") != null) {
        int_QTE_REAPPROVISIONNEMENT = Integer.parseInt(request.getParameter("int_QTE_REAPPROVISIONNEMENT"));
        new logger().OCategory.info("int_QTE_REAPPROVISIONNEMENT " + int_QTE_REAPPROVISIONNEMENT);
    }

    if (request.getParameter("bool_INVENTAIRE") != null) {
        bool_INVENTAIRE = Integer.valueOf(request.getParameter("bool_INVENTAIRE"));
        new logger().OCategory.info("bool_INVENTAIRE " + bool_INVENTAIRE);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    if (request.getParameter("str_END") != null) {
        str_END = request.getParameter("str_END");
        new logger().OCategory.info("str_END " + str_END);
    }
    if (request.getParameter("str_BEGIN") != null) {
        str_BEGIN = request.getParameter("str_BEGIN");
        new logger().OCategory.info("str_BEGIN " + str_BEGIN);
    }

    if (request.getParameter("liste_article") != null && !request.getParameter("liste_article").equalsIgnoreCase("")) {
        liste_article = request.getParameter("liste_article");
        new logger().OCategory.info("liste_article " + liste_article);
    }

    if (request.getParameter("int_NUMBER") != null) {
        int_NUMBER = Integer.parseInt(request.getParameter("int_NUMBER"));
        new logger().OCategory.info("int_NUMBER " + int_NUMBER);
    }
    if (request.getParameter("iSChecked") != null) {
        iSChecked = Boolean.valueOf(request.getParameter("iSChecked"));

    }

    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
        new logger().OCategory.info("str_DESCRIPTION " + str_DESCRIPTION);
    }

    if (request.getParameter("lg_FAMILLEARTICLE_ID") != null && !request.getParameter("lg_FAMILLEARTICLE_ID").equalsIgnoreCase("") && !request.getParameter("lg_FAMILLEARTICLE_ID").equalsIgnoreCase("Sectionner une famille article...")) {
        lg_FAMILLEARTICLE_ID = request.getParameter("lg_FAMILLEARTICLE_ID");
        new logger().OCategory.info("lg_FAMILLEARTICLE_ID " + lg_FAMILLEARTICLE_ID);
    }
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }
    if (request.getParameter("int_CIP") != null) {
        int_CIP = request.getParameter("int_CIP");
        new logger().OCategory.info("int_CIP " + int_CIP);
    }
    if (request.getParameter("lg_ZONE_GEO_ID") != null && !request.getParameter("lg_ZONE_GEO_ID").equalsIgnoreCase("") && !request.getParameter("lg_ZONE_GEO_ID").equalsIgnoreCase("Sectionner un emplacement...")) {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
        new logger().OCategory.info("lg_ZONE_GEO_ID " + lg_ZONE_GEO_ID);
    }

    if (request.getParameter("lg_INVENTAIRE_ID") != null) {
        lg_INVENTAIRE_ID = request.getParameter("lg_INVENTAIRE_ID");
        new logger().OCategory.info("lg_INVENTAIRE_ID " + lg_INVENTAIRE_ID);
    }

    if (request.getParameter("lg_INVENTAIRE_FAMILLE_ID") != null) {
        lg_INVENTAIRE_FAMILLE_ID = request.getParameter("lg_INVENTAIRE_FAMILLE_ID");
        new logger().OCategory.info("lg_INVENTAIRE_FAMILLE_ID " + lg_INVENTAIRE_FAMILLE_ID);
    }

    if (request.getParameter("lg_GROSSISTE_ID") != null && !request.getParameter("lg_GROSSISTE_ID").equalsIgnoreCase("") && !request.getParameter("lg_GROSSISTE_ID").equalsIgnoreCase("Sectionner un grossiste...")) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");

    }
    if (request.getParameter("str_TYPE_TRANSACTION") != null) {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
    }

    if (!user.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS)) {
        lg_TYPE_STOCK_ID = "3";
    }
    JSONObject json = new JSONObject();
    int success = 0;
    long nligne = 0;
    String result = "", code_statut = commonparameter.PROCESS_FAILED, desc_satut = "";
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    InventaireManager OInventaireManager = new InventaireManager(OdataManager, user);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("create")) {
            nligne = OInventaireManager.createInventaire(str_NAME, lg_FAMILLE_ID, str_NAME, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, lg_GROSSISTE_ID, str_BEGIN, str_END, str_TYPE_TRANSACTION, bool_INVENTAIRE, stockFilter, stockProduit);
            ObllBase.setMessage(OInventaireManager.getMessage());

        } else if (request.getParameter("mode").equals("createInventaireArticle")) {
            // OInventaireManager.createInventaireFamille(lg_INVENTAIRE_ID, lg_FAMILLE_ID, bool_INVENTAIRE);
            ObllBase.setDetailmessage(OInventaireManager.getDetailmessage());
            ObllBase.setMessage(OInventaireManager.getMessage());
        } else if (request.getParameter("mode").equals("createInventaireArticle")) {
            //  OInventaireManager.createInventaireFamille(lg_INVENTAIRE_ID, lg_FAMILLE_ID, bool_INVENTAIRE);
            ObllBase.setDetailmessage(OInventaireManager.getDetailmessage());
            ObllBase.setMessage(OInventaireManager.getMessage());
        } else if (request.getParameter("mode").equals("updateInventaireUnitaireFamille")) {
            OInventaireManager.updateInventaireUnitaireFamille(lg_INVENTAIRE_ID, lg_FAMILLE_ID, iSChecked);
            ObllBase.setDetailmessage(OInventaireManager.getDetailmessage());
            ObllBase.setMessage(OInventaireManager.getMessage());
        } else if (request.getParameter("mode").equals("deleteInventaireFamilleBis")) {
            OInventaireManager.deleteInventaireFamille(lg_INVENTAIRE_ID, lg_FAMILLE_ID);
            ObllBase.setDetailmessage(OInventaireManager.getDetailmessage());
            ObllBase.setMessage(OInventaireManager.getMessage());
        } else if (request.getParameter("mode").equals("delete")) {
            OInventaireManager.deleteInventaire(lg_INVENTAIRE_ID);
            ObllBase.setDetailmessage(OInventaireManager.getDetailmessage());
            ObllBase.setMessage(OInventaireManager.getMessage());
        } else if (request.getParameter("mode").equals("deleteInventaireFamille")) {
            OInventaireManager.deleteInventaireFamille(lg_INVENTAIRE_FAMILLE_ID);
            ObllBase.setDetailmessage(OInventaireManager.getDetailmessage());
            ObllBase.setMessage(OInventaireManager.getMessage());
        } else if (request.getParameter("mode").equals("updateinventairefamille")) {

            OInventaireManager.updateInventaireFamille(lg_INVENTAIRE_FAMILLE_ID, lg_INVENTAIRE_ID, lg_FAMILLE_ID, int_CIP, str_DESCRIPTION, lg_ZONE_GEO_ID, lg_FAMILLEARTICLE_ID, lg_GROSSISTE_ID, int_PRICE, int_NUMBER);
            ObllBase.setDetailmessage(OInventaireManager.getDetailmessage());
            success = Integer.valueOf(OInventaireManager.getMessage());
            ObllBase.setMessage(OInventaireManager.getMessage());
        } else if (request.getParameter("mode").equals("cloturer")) {
            result = "Echec de cloture de l'inventaitre";
            nligne = OInventaireManager.clotureInventaire(lg_INVENTAIRE_ID);
            success = 1;
            result = "Cloture effectu�e avec succ�s; " + nligne + " Articles mis � jour";
            /*if (nligne > 0) {
                success = 1;
                result =  "Cloture effectu�e avec succ�s; " + nligne + " Articles mis � jour";
            }*/
        } else if (request.getParameter("mode").equals("createbis")) {

            try {

                // OTInventaire = OInventaireManager.createInventaire(str_NAME, str_DESCRIPTION, str_TYPE_TRANSACTION);
                if (lg_GROSSISTE_ID.equals("0") || lg_FAMILLEARTICLE_ID.equals("0") || lg_ZONE_GEO_ID.equals("0")) {
                    if (lg_GROSSISTE_ID.equals("0")) {
                        lg_GROSSISTE_ID = "%%";
                    }
                    if (lg_FAMILLEARTICLE_ID.equals("0")) {
                        lg_FAMILLEARTICLE_ID = "%%";
                    }
                    if (lg_ZONE_GEO_ID.equals("0")) {
                        lg_ZONE_GEO_ID = "%%";
                    }

                }
                //result = "Echec de cr�ation de l'inventaitre";
                nligne = OInventaireManager.createInventaire(str_NAME, lg_FAMILLE_ID, str_NAME, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, lg_GROSSISTE_ID, str_BEGIN, str_END, str_TYPE_TRANSACTION, bool_INVENTAIRE, stockFilter, stockProduit);
                if (nligne > 0) {
                    success = 1;
                    result = nligne + " Articles Inventori�s";
                }
                // ObllBase.setMessage(OInventaireManager.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de cr�ation de l'inventaitre");
            }

        }

    }

    json.put("success", success);
    json.put("nombre", result);
    json.put("code_statut", ObllBase.getMessage());
    json.put("desc_satut", ObllBase.getDetailmessage());


%>
<%= json%>