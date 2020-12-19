<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TRetourdepotdetail"%>
<%@page import="dal.TRetourdepot"%>
<%@page import="bll.stockManagement.DepotManager"%>
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
    TRetourdepot OTRetourdepot = null;
    TRetourdepotdetail OTRetourdepotdetail = null;


%>


<%    String lg_RETOURDEPOT_ID = "", str_DESCRIPTION = "", lg_FAMILLE_ID = "", lg_RETOURDEPOTDETAIL_ID = "", str_ref = "", str_NAME = "";
    int int_NUMBER_RETURN = 0, int_STOCK = 0, int_TOTAL_PRODUCT = 0, int_TOTAL_AMOUNT = 0;

    if (request.getParameter("lg_RETOURDEPOT_ID") != null) {
        lg_RETOURDEPOT_ID = request.getParameter("lg_RETOURDEPOT_ID");
        new logger().OCategory.info("lg_RETOURDEPOT_ID " + lg_RETOURDEPOT_ID);
    }
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
        new logger().OCategory.info("str_DESCRIPTION " + str_DESCRIPTION);
    }
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (request.getParameter("lg_RETOURDEPOTDETAIL_ID") != null) {
        lg_RETOURDEPOTDETAIL_ID = request.getParameter("lg_RETOURDEPOTDETAIL_ID");
        new logger().OCategory.info("lg_RETOURDEPOTDETAIL_ID " + lg_RETOURDEPOTDETAIL_ID);
    }

    if (request.getParameter("int_QUANTITY") != null) {
        int_NUMBER_RETURN = Integer.parseInt(request.getParameter("int_QUANTITY"));
        new logger().OCategory.info("int_NUMBER_RETURN " + int_NUMBER_RETURN);
    }
    if (request.getParameter("int_STOCK") != null) {
        int_STOCK = Integer.parseInt(request.getParameter("int_STOCK"));
        new logger().oCategory.info("int_STOCK : " + int_STOCK);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("Erreur serveur");
    DepotManager ODepotManager = new DepotManager(OdataManager, OTUser);
    str_ref = lg_RETOURDEPOT_ID;
    if (request.getParameter("mode") != null) {
        if (request.getParameter("mode").equals("create")) {
            try {
                if (request.getParameter("lg_RETOURDEPOT_ID").equals("0")) { 
                    OTRetourdepot = ODepotManager.createTRetourdepot(str_NAME, OTUser.getLgEMPLACEMENTID(), str_DESCRIPTION,"");
                } else {
                    OTRetourdepot = ODepotManager.updateTRetourdepot(lg_RETOURDEPOT_ID, str_DESCRIPTION);
                }
                ODepotManager.createTRetourdepotdetail(OTRetourdepot, lg_FAMILLE_ID, int_NUMBER_RETURN);
                if (OTRetourdepot != null) {
                    str_ref = OTRetourdepot.getLgRETOURDEPOTID();
                    int_TOTAL_AMOUNT = ODepotManager.getTotalAmountRetour(OTRetourdepot.getLgRETOURDEPOTID(), OTRetourdepot.getStrSTATUT()).intValue();
                    int_TOTAL_PRODUCT = ODepotManager.getTotalQuantityRetour(OTRetourdepot.getLgRETOURDEPOTID(), OTRetourdepot.getStrSTATUT());
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ObllBase.setDetailmessage(ODepotManager.getDetailmessage());
            ObllBase.setMessage(ODepotManager.getMessage());
        } else if (request.getParameter("mode").equals("update")) {
            OTRetourdepotdetail = ODepotManager.UpdateTRetourdepotdetail(lg_RETOURDEPOTDETAIL_ID, lg_RETOURDEPOT_ID, lg_FAMILLE_ID, int_NUMBER_RETURN);
            if (OTRetourdepotdetail != null) {
                int_TOTAL_AMOUNT = ODepotManager.getTotalAmountRetour(OTRetourdepotdetail.getLgRETOURDEPOTID().getLgRETOURDEPOTID(), OTRetourdepotdetail.getLgRETOURDEPOTID().getStrSTATUT()).intValue();
                int_TOTAL_PRODUCT = ODepotManager.getTotalQuantityRetour(OTRetourdepotdetail.getLgRETOURDEPOTID().getLgRETOURDEPOTID(), OTRetourdepotdetail.getLgRETOURDEPOTID().getStrSTATUT());
                new logger().OCategory.info("lg_RETOURDEPOT_ID nouveau " + str_ref);
            }
            ObllBase.setDetailmessage(ODepotManager.getDetailmessage());
            ObllBase.setMessage(ODepotManager.getMessage());

        } else if (request.getParameter("mode").equals("delete")) {
            ODepotManager.deleteTRetourdepot(lg_RETOURDEPOT_ID);
            ObllBase.setDetailmessage(ODepotManager.getDetailmessage());
            ObllBase.setMessage(ODepotManager.getMessage());
        } else if (request.getParameter("mode").equals("deleteDetail")) {
            OTRetourdepot = ODepotManager.deleteTRetourdepotdetail(lg_RETOURDEPOTDETAIL_ID);
            if (OTRetourdepot != null) {
                int_TOTAL_AMOUNT = ODepotManager.getTotalAmountRetour(OTRetourdepot.getLgRETOURDEPOTID(), OTRetourdepot.getStrSTATUT()).intValue();
                int_TOTAL_PRODUCT = ODepotManager.getTotalQuantityRetour(OTRetourdepot.getLgRETOURDEPOTID(), OTRetourdepot.getStrSTATUT());
                new logger().OCategory.info("lg_RETOURDEPOT_ID nouveau " + str_ref);
            }
            ObllBase.setDetailmessage(ODepotManager.getDetailmessage());
            ObllBase.setMessage(ODepotManager.getMessage());
        } else if (request.getParameter("mode").equals("cloturer")) {
            ODepotManager.closeRetourDepot(lg_RETOURDEPOT_ID, str_DESCRIPTION);
            ObllBase.setDetailmessage(ODepotManager.getDetailmessage());
            ObllBase.setMessage(ODepotManager.getMessage());
        }else if (request.getParameter("mode").equals("closeInOfficine")) {
           
            ODepotManager.closeRetourDepotInOfficine(request.getParameter("lg_RETOUR_FRS_ID"));
            ObllBase.setDetailmessage(ODepotManager.getDetailmessage());
            ObllBase.setMessage(ODepotManager.getMessage());
        }


    }
    String result;
    
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{ref:\"" + str_ref + "\", errors_code: \"" + ObllBase.getMessage()+ "\", int_TOTAL_AMOUNT: \"" + conversion.AmountFormat(int_TOTAL_AMOUNT,'.')+ "\", int_TOTAL_PRODUCT: \"" + conversion.AmountFormat(int_TOTAL_PRODUCT,'.') + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {
        result = "{ref:\"" + str_ref + "\", errors_code: \"" + ObllBase.getMessage()+ "\", int_TOTAL_AMOUNT: \"" + conversion.AmountFormat(int_TOTAL_AMOUNT,'.')+ "\", int_TOTAL_PRODUCT: \"" + conversion.AmountFormat(int_TOTAL_PRODUCT,'.') + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>