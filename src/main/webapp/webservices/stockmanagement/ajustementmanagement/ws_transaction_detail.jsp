<%@page import="bll.teller.SnapshotManager"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TAjustementDetail"%>
<%@page import="dal.TAjustement"%>
<%@page import="bll.stockManagement.AjustementManagement"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="dal.TFamille"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
    TAjustement OTAjustement = null;
    TAjustementDetail OTAjustementDetail = null;
    AjustementManagement OAjustementManagement;
%>




<%
    String lg_AJUSTEMENT_ID = "%%", lg_USER_ID = "%%", dt_CREATED = "%%", lg_FAMILLE_ID = "%%",
            str_COMMENTAIRE = "", lg_AJUSTEMENTDETAIL_ID = "%%", str_ref = "";
    int int_QUANTITY = 0;

    if (request.getParameter("lg_AJUSTEMENT_ID") != null) {
        lg_AJUSTEMENT_ID = request.getParameter("lg_AJUSTEMENT_ID");
        new logger().OCategory.info("lg_AJUSTEMENT_ID " + lg_AJUSTEMENT_ID);
    }

    if (request.getParameter("lg_AJUSTEMENTDETAIL_ID") != null) {
        lg_AJUSTEMENTDETAIL_ID = request.getParameter("lg_AJUSTEMENTDETAIL_ID");
        new logger().OCategory.info("lg_AJUSTEMENTDETAIL_ID " + lg_AJUSTEMENTDETAIL_ID);
    }
    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (request.getParameter("str_COMMENTAIRE") != null) {
        str_COMMENTAIRE = request.getParameter("str_COMMENTAIRE");
        new logger().OCategory.info("str_COMMENTAIRE " + str_COMMENTAIRE);
    }

    if (request.getParameter("int_QUANTITY") != null) {
        int_QUANTITY = new Integer(request.getParameter("int_QUANTITY"));
        new logger().OCategory.info("int_QUANTITY " + int_QUANTITY);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    new logger().oCategory.info("Utilisateur conecté : " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID @" + request.getParameter("lg_AJUSTEMENT_ID") + "@");
    

    str_ref = lg_AJUSTEMENT_ID;
      OAjustementManagement = new AjustementManagement(OdataManager, OTUser);
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("create")) {
          
            if (request.getParameter("lg_AJUSTEMENT_ID").equals("0")) {
                OTAjustement = OAjustementManagement.createAjustement(str_COMMENTAIRE,lg_FAMILLE_ID, int_QUANTITY); 
            } else {
               OTAjustement = OAjustementManagement.updateAjustement(lg_AJUSTEMENT_ID, str_COMMENTAIRE);//23122017
                 OTAjustementDetail = OAjustementManagement.createOrUpdateAjustementDetail(lg_AJUSTEMENT_ID, lg_FAMILLE_ID, int_QUANTITY);
            }

            if (OAjustementManagement.getMessage() == commonparameter.PROCESS_SUCCESS) {
                str_ref = OTAjustement.getLgAJUSTEMENTID();
                new logger().OCategory.info("lg_AJUSTEMENT_ID nouveau " + str_ref);
            }

           
            ObllBase.setDetailmessage(OAjustementManagement.getDetailmessage());
            ObllBase.setMessage(OAjustementManagement.getMessage());

        } else if (request.getParameter("mode").equals("update")) {
          
            OTAjustementDetail = OAjustementManagement.getAjustementDetail(lg_AJUSTEMENTDETAIL_ID);
            OAjustementManagement.UpdateAjustementDetail(OTAjustementDetail, int_QUANTITY);
            ObllBase.setDetailmessage(OAjustementManagement.getDetailmessage());
            ObllBase.setMessage(OAjustementManagement.getMessage());
        } else if (request.getParameter("mode").equals("delete")) {
           
            OTAjustementDetail = OAjustementManagement.getAjustementDetail(lg_AJUSTEMENTDETAIL_ID);
            OAjustementManagement.RemoveAjustementDetail(OTAjustementDetail);
            ObllBase.setDetailmessage(OAjustementManagement.getDetailmessage());
            ObllBase.setMessage(OAjustementManagement.getMessage());
        } else if (request.getParameter("mode").equals("cloturer")) {
            OAjustementManagement = new AjustementManagement(OdataManager, OTUser, new StockManager(OdataManager, OTUser), new SnapshotManager(OdataManager, OTUser));
            OAjustementManagement.closureAjustement(lg_AJUSTEMENT_ID, str_COMMENTAIRE);
            ObllBase.setMessage(OAjustementManagement.getMessage());
            ObllBase.setDetailmessage(OAjustementManagement.getDetailmessage());
        } 
    } else {
    }

    String result;
    new logger().OCategory.info("ObllBase.getMessage() ---- " + ObllBase.getMessage());

    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{ref:\"" + str_ref  + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {
        result = "{ref:\"" + str_ref + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>