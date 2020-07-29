<%@page import="dal.TBonLivraisonDetail"%>
<%@page import="dal.TBonLivraisonDetail"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TFamille"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<% String lg_BON_LIVRAISON_DETAIL = "", lg_TYPEETIQUETTE_ID = "", str_REF_ORDER = "", str_REF_LIVRAISON = "", str_PEREMPTION = "", str_SORTIE_USINE = "", lg_FAMILLE_ID = "", lg_GROSSISTE_ID = "", int_NUM_LOT = "";
    Integer int_NUMBER = 0, int_QUANTITE_FREE = 0;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    dal.TWarehouse OTWarehouse = null;
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID: " + lg_FAMILLE_ID);
    }
    if (request.getParameter("lg_GROSSISTE_ID") != null) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }
    if (request.getParameter("int_QUANTITE_FREE") != null) {
        int_QUANTITE_FREE = Integer.parseInt(request.getParameter("int_QUANTITE_FREE"));
        new logger().OCategory.info("int_QUANTITE_FREE " + int_QUANTITE_FREE);
    }

    if (request.getParameter("int_NUMBER") != null) {
        int_NUMBER = Integer.parseInt(request.getParameter("int_NUMBER"));
        new logger().OCategory.info("int_NUMBER " + int_NUMBER);
    }
    if (request.getParameter("str_REF_LIVRAISON") != null) {
        str_REF_LIVRAISON = request.getParameter("str_REF_LIVRAISON");
        new logger().OCategory.info("str_REF_LIVRAISON " + str_REF_LIVRAISON);
    }
    if (request.getParameter("lg_BON_LIVRAISON_DETAIL") != null) {
        lg_BON_LIVRAISON_DETAIL = request.getParameter("lg_BON_LIVRAISON_DETAIL");
        new logger().OCategory.info("lg_BON_LIVRAISON_DETAIL " + lg_BON_LIVRAISON_DETAIL);
    }

    if (request.getParameter("str_SORTIE_USINE") != null) {
        str_SORTIE_USINE = request.getParameter("str_SORTIE_USINE");
        new logger().OCategory.info("str_SORTIE_USINE " + str_SORTIE_USINE);
    }
    if (request.getParameter("str_PEREMPTION") != null && !"".equals(request.getParameter("str_PEREMPTION"))) {
        str_PEREMPTION = request.getParameter("str_PEREMPTION");
        new logger().OCategory.info("str_PEREMPTION " + str_PEREMPTION);
    }
    if (request.getParameter("lg_TYPEETIQUETTE_ID") != null) {
        lg_TYPEETIQUETTE_ID = request.getParameter("lg_TYPEETIQUETTE_ID");
        new logger().OCategory.info("lg_TYPEETIQUETTE_ID " + lg_TYPEETIQUETTE_ID);
    }
    if (request.getParameter("str_REF_ORDER") != null) {
        str_REF_ORDER = request.getParameter("str_REF_ORDER");
        new logger().OCategory.info("str_REF_ORDER " + str_REF_ORDER);
    }

    if (request.getParameter("int_NUM_LOT") != null) {
        int_NUM_LOT = request.getParameter("int_NUM_LOT");
        new logger().OCategory.info("int_NUM_LOT " + int_NUM_LOT);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager, OTUser);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    //new logger().oCategory.info("ID " + request.getParameter("lg_INSTITUTION_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("create")) {
            new logger().oCategory.info("Creation");
            TBonLivraisonDetail OTBonLivraisonDetail = OdataManager.getEm().find(TBonLivraisonDetail.class, lg_BON_LIVRAISON_DETAIL);

            //OWarehouseManager.AddLot(OTBonLivraisonDetail.getLgFAMILLEID().getLgFAMILLEID(), int_NUMBER, OTBonLivraisonDetail.getLgGROSSISTEID().getLgGROSSISTEID(), str_REF_LIVRAISON, str_SORTIE_USINE, str_PEREMPTION, int_QUANTITE_FREE, lg_TYPEETIQUETTE_ID, OTBonLivraisonDetail.getLgBONLIVRAISONID().getLgORDERID().getStrREFORDER(), OTBonLivraisonDetail.getLgBONLIVRAISONDETAIL(), int_NUM_LOT);
             int rs = OWarehouseManager.AddLot(int_NUMBER, str_SORTIE_USINE, str_PEREMPTION, int_QUANTITE_FREE, lg_TYPEETIQUETTE_ID, OTBonLivraisonDetail, int_NUM_LOT);
            ObllBase.setMessage(rs+"") ; 
            ObllBase.setDetailmessage(OWarehouseManager.getDetailmessage());

        } else if (request.getParameter("mode").equals("remove")) {
            OWarehouseManager.RemoveLot(lg_BON_LIVRAISON_DETAIL, int_NUM_LOT);
            ObllBase.setMessage(OWarehouseManager.getMessage());
            ObllBase.setDetailmessage(OWarehouseManager.getDetailmessage());

        } else if (request.getParameter("mode").equals("deleteItem")) {
            OWarehouseManager.removeLot(str_REF_LIVRAISON,lg_FAMILLE_ID,lg_BON_LIVRAISON_DETAIL); 
            ObllBase.setMessage(OWarehouseManager.getMessage());
            ObllBase.setDetailmessage(OWarehouseManager.getDetailmessage());

        } 
        
        
        
        
        
        
        else if (request.getParameter("mode").equals("update")) {

            if (request.getParameter("lg_FAMILLE_ID").equals("init")) {
            } else {
            }
        } else if (request.getParameter("mode").equals("delete")) {

            OTWarehouse = ObllBase.getOdataManager().getEm().find(dal.TWarehouse.class, request.getParameter("lg_LOT_ID"));

            if (!ObllBase.delete(OTWarehouse)) {
                ObllBase.setDetailmessage("Impossible de supprimer");
            }

            new logger().oCategory.info("Suppression de productitem " + request.getParameter("lg_LOT_ID").toString());

        } else {
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS) || ObllBase.getMessage().equals("2")) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }

%>
<%=result%>