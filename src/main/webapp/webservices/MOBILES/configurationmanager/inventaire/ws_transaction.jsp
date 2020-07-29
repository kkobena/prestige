<%@page import="bll.stockManagement.InventaireManager"%>
<%@page import="dal.TInventaire"%>
<%@page import="bll.common.Parameter"%>
<%@page import="dal.TInventaire"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>
<%@page import="bll.userManagement.*"  %>



<%
    dataManager OdataManager = new dataManager();
    TInventaire OTInventaire = null;
    JSONArray arrayObj = new JSONArray();
    JSONObject json = new JSONObject();
    TUser OTUser = null;

%>




<%    String lg_PRODUCT_ID = "", lg_INVENTAIRE_ID = "", str_NAME = "", str_DESCRIPTION = "", lg_USER_ID = "", code_statut = commonparameter.PROCESS_FAILED, lstProduct = "";
    int int_QUANTITY = 0, lg_INVENTAIREDETAIL_ID = 0;

    if (request.getParameter("lg_INVENTAIREDETAIL_ID") != null) {
        lg_INVENTAIREDETAIL_ID = Integer.parseInt(request.getParameter("lg_INVENTAIREDETAIL_ID"));
        new logger().oCategory.info("lg_INVENTAIREDETAIL_ID : " + lg_INVENTAIREDETAIL_ID);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().oCategory.info("lg_USER_ID : " + lg_USER_ID);
    }

    if (request.getParameter("lstProduct") != null) {
        lstProduct = request.getParameter("lstProduct");
        new logger().oCategory.info("lstProduct : " + lstProduct);
    }

    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
        new logger().oCategory.info("str_NAME : " + str_NAME);
    }

    if (request.getParameter("lg_INVENTAIRE_ID") != null) {
        lg_INVENTAIRE_ID = request.getParameter("lg_INVENTAIRE_ID");
        new logger().oCategory.info("lg_INVENTAIRE_ID : " + lg_INVENTAIRE_ID);
    }


    if (request.getParameter("int_QUANTITY") != null) {
        int_QUANTITY = Integer.parseInt(request.getParameter("int_QUANTITY"));
        new logger().oCategory.info("int_QUANTITY : " + int_QUANTITY);
    }

    if (request.getParameter("lg_PRODUCT_ID") != null) {
        lg_PRODUCT_ID = request.getParameter("lg_PRODUCT_ID");
        new logger().oCategory.info("lg_PRODUCT_ID : " + lg_PRODUCT_ID);
    }

    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
        new logger().oCategory.info("str_DESCRIPTION : " + str_DESCRIPTION);
    }

    new logger().OCategory.info("mode:" + request.getParameter("mode"));
    OdataManager.initEntityManager();
    // OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OTUser = OdataManager.getEm().find(TUser.class, lg_USER_ID);
    InventaireManager OInventaireManager = new InventaireManager(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            try {
                OTInventaire = OInventaireManager.createInventaire(str_NAME, str_DESCRIPTION, "Emplacement");
                code_statut = OInventaireManager.getMessage();
                if (OTInventaire != null) {
                    OInventaireManager.refresh(OTInventaire);
                    json.put("lg_INVENTAIRE_ID", OTInventaire.getLgINVENTAIREID());
                    json.put("str_NAME", OTInventaire.getStrNAME());
                    json.put("str_DESCRIPTION", OTInventaire.getStrDESCRIPTION());
                    json.put("lg_INVENTAIRE_ID", OTInventaire.getLgINVENTAIREID());
                    json.put("dbl_AMOUNT_ACHAT_BEFORE", 0);
                    json.put("dbl_AMOUNT_VENTE_BEFORE", 0);
                    json.put("dbl_AMOUNT_ACHAT_AFTER", 0);
                    json.put("dbl_AMOUNT_VENTE_AFTER", 0);
                    json.put("code_statut", code_statut);
                    json.put("desc_statut", OInventaireManager.getDetailmessage());
                    arrayObj.put(json);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (request.getParameter("mode").toString().equals("updatemassive")) {
            OInventaireManager.updateInventairedetail(lstProduct, OTUser.getLgUSERID());
            json.put("code_statut", OInventaireManager.getMessage());
            json.put("desc_statut", OInventaireManager.getDetailmessage());
            arrayObj.put(json);

        } 
    }
    String result = arrayObj.toString();

    new logger().OCategory.info("JSON " + result);


%>
<%=result%>