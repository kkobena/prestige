<%@page import="dal.TTypeStockFamille"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="dal.TWarehouse"%>
<%@page import="dal.TFamille"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.warehouse.WarehouseManager"%>
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
%>



<%
    String lg_TYPE_STOCK_ID = "1";
    String lg_FAMILLE_ID = "%%", lg_WAREHOUSE_ID = "%%", int_NUM_LOT = "";

    int int_NUMBER = 0;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager, OTUser);
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, OTUser);
    StockManager OStockManager = new StockManager(OdataManager, OTUser);

    ObllBase.setDetailmessage("PAS D'ACTION");

    // lg_FAMILLE_ID
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }
    if (request.getParameter("lg_WAREHOUSE_ID") != null) {
        lg_WAREHOUSE_ID = request.getParameter("lg_WAREHOUSE_ID");
        new logger().OCategory.info("lg_WAREHOUSE_ID " + lg_WAREHOUSE_ID);
    }

    if (!OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS)) {
        lg_TYPE_STOCK_ID = "3";
    }

    if (request.getParameter("lg_TYPE_STOCK_ID") != null) {
        lg_TYPE_STOCK_ID = request.getParameter("lg_TYPE_STOCK_ID");
        new logger().OCategory.info("lg_TYPE_STOCK_ID " + lg_TYPE_STOCK_ID);
    }

    if (request.getParameter("int_NUMBER") != null) {
        int_NUMBER = Integer.parseInt(request.getParameter("int_NUMBER"));
        new logger().OCategory.info("int_NUMBER " + int_NUMBER);
    }

    if (request.getParameter("int_NUM_LOT") != null) {
        int_NUM_LOT = request.getParameter("int_NUM_LOT");
        new logger().OCategory.info("int_NUM_LOT " + int_NUM_LOT);
    }

    // ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("addToPerime")) {
            //StockManager OStockManager = new StockManager(OdataManager);
            OStockManager.addProductToPerime(lg_WAREHOUSE_ID);
            ObllBase.setDetailmessage(OStockManager.getDetailmessage());
            ObllBase.setMessage(OStockManager.getMessage());
            //  new logger().oCategory.info("creation OTTiersPayant " + OTTiersPayant.getStrNAME());

        } else if (request.getParameter("mode").toString().equals("deleteToPerime")) {
            //StockManager OStockManager = new StockManager(OdataManager);

            OWarehouseManager.deleteProductToPerime(lg_WAREHOUSE_ID, lg_TYPE_STOCK_ID);
            ObllBase.setDetailmessage(OWarehouseManager.getDetailmessage());
            ObllBase.setMessage(OWarehouseManager.getMessage());
            //  new logger().oCategory.info("creation OTTiersPayant " + OTTiersPayant.getStrNAME());

        } else if (request.getParameter("mode").equals("doPerime")) {
            try {
                 TFamilleStock OTFamilleStock = new tellerManagement(OdataManager, OTUser).getTProductItemStock(lg_FAMILLE_ID);
                new logger().oCategory.info("Stock avant avec perime " + OTFamilleStock.getIntNUMBERAVAILABLE());
                if (OTFamilleStock.getIntNUMBERAVAILABLE() < int_NUMBER) {
                    ObllBase.buildErrorTraceMessage("La quantité à envoyer en périmé doit être inférieure ou égale à la quantité en stock");
                } else {
                    TWarehouse OTWarehouse = OWarehouseManager.AddStock(OTFamilleStock.getLgFAMILLEID(), int_NUMBER, int_NUM_LOT);
               
                    for (int i = 0; i < OTWarehouse.getIntNUMBER(); i++) {
                        OWarehouseManager.addProductItemInWarehouseDetail("", OTWarehouse.getLgFAMILLEID(), OTWarehouse.getDtPEREMPTION(), OTWarehouse);
                    }
                    
                   // OSnapshotManager.SaveMouvementFamille(OTWarehouse.getLgFAMILLEID(), "", commonparameter.REMOVE, commonparameter.str_ACTION_PERIME, int_NUMBER); a decommenter en cas de probleme
                    ObllBase.setDetailmessage(OWarehouseManager.getDetailmessage());
                    ObllBase.setMessage(OWarehouseManager.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de l'opération");
            }

            new logger().oCategory.info(ObllBase.getDetailmessage());
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