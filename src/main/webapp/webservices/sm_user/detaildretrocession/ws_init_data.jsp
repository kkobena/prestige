<%@page import="dal.TTypeReglement"%>
<%@page import="dal.TCashTransaction"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
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

<%!    String lg_PREENREGISTREMENT_ID = "%%", lg_PREENREGISTREMENT_DETAIL_ID = "%%", str_REF = "%%", lg_USER_ID = "%%", dt_CREATED = "%%", lg_FAMILLE_ID = "%%", lg_TYPE_REGLEMENT_ID = "%%";
    Integer int_PRICE_DETAIL;
    Integer int_QUANTITY;
    Integer int_QUANTITY_SERVED;
    Integer int_TOTAL_VENTE_RECAP;
    Integer int_AMOUNT_REMIS;
    Integer int_AMOUNT_RECU;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    //privilege Oprivilege = new privilege();
    TUser OTUser = null;
    dal.TPreenregistrement OTPreenregistrement = null;
    TPreenregistrementDetail OTPreenregistrementDetail = null;
    TCashTransaction OTCashTransaction = null;
    TTypeReglement OTTypeReglement = null;

    int int_total_vente = 0;
    int int_total_product = 0;

%>




<%
    if (request.getParameter("str_REF") != null) {
        str_REF = request.getParameter("str_REF");
    }
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
    }

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");

    }
    if (request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID") != null) {
        lg_PREENREGISTREMENT_DETAIL_ID = request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID");

    }
    //lg_PREENREGISTREMENT_DETAIL_ID
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");

    }

    if (request.getParameter("int_QUANTITY") != null) {
        int_QUANTITY = new Integer(request.getParameter("int_QUANTITY"));

    }

    if (request.getParameter("int_QUANTITY_SERVED") != null) {
        int_QUANTITY_SERVED = new Integer(request.getParameter("int_QUANTITY_SERVED"));

    }

    if (request.getParameter("int_PRICE_DETAIL") != null) {
        new logger().OCategory.info("int_PRICE_DETAIL   " + request.getParameter("int_PRICE_DETAIL"));
        int_PRICE_DETAIL = new Integer(request.getParameter("int_PRICE_DETAIL"));

    }

    if (request.getParameter("int_TOTAL_VENTE_RECAP") != null) {
        new logger().OCategory.info("int_TOTAL_VENTE_RECAP   " + request.getParameter("int_TOTAL_VENTE_RECAP"));
        int_TOTAL_VENTE_RECAP = new Integer(request.getParameter("int_TOTAL_VENTE_RECAP"));

    }

    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null) {
        new logger().OCategory.info("lg_TYPE_REGLEMENT_ID   " + request.getParameter("lg_TYPE_REGLEMENT_ID"));
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");

    }

    if (request.getParameter("int_AMOUNT_RECU") != null) {
        new logger().OCategory.info("int_AMOUNT_RECU   " + request.getParameter("int_AMOUNT_RECU"));
        int_AMOUNT_RECU = new Integer(request.getParameter("int_AMOUNT_RECU"));

    }

    if (request.getParameter("int_AMOUNT_REMIS") != null) {
        new logger().OCategory.info("int_AMOUNT_REMIS   " + request.getParameter("int_AMOUNT_REMIS"));
        int_AMOUNT_REMIS = new Integer(request.getParameter("int_AMOUNT_REMIS"));

    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID @" + request.getParameter("lg_PREENREGISTREMENT_ID") + "@");
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("init")) {
            new logger().OCategory.info("create");

            OTPreenregistrement = ObllBase.getOdataManager().getEm().find(dal.TPreenregistrement.class, request.getParameter("lg_PREENREGISTREMENT_ID"));
            int_total_vente = OPreenregistrement.GetVenteTotal(OTPreenregistrement.getLgPREENREGISTREMENTID());
            int_total_product = OPreenregistrement.GetProductTotal(OTPreenregistrement.getLgPREENREGISTREMENTID());
            ObllBase.setMessage(commonparameter.PROCESS_SUCCESS);
        }

    } else {
    }

    String result;
    new logger().OCategory.info("ObllBase.getMessage() ---- " + ObllBase.getMessage());
    new logger().OCategory.info("commonparameter.PROCESS_SUCCESS ---- " + commonparameter.PROCESS_SUCCESS);
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{str_MEDECIN:\"" + OTPreenregistrement.getStrMEDECIN() + "\",ref:\"" + OTPreenregistrement.getLgPREENREGISTREMENTID() + "\",lg_NATURE_VENTE_ID:\"" + OTPreenregistrement.getLgNATUREVENTEID().getStrLIBELLE() + "\",total_vente:\"" + int_total_vente + "\", int_total_product: \"" + int_total_product + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {
        result = "{ref:\"" + OTPreenregistrement.getLgPREENREGISTREMENTID() + "\",total_vente:\"" + int_total_vente + "\", int_total_product: \"" + int_total_product + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>