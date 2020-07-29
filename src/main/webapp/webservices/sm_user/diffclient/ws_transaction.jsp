
<%@page import="bll.differe.DiffereManagement"%>
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

<%!    String lg_ECART_MVT_ID = "%%", lg_TYPE_ECART_MVT = "%%", str_REF = "%%", str_DESCRIPTION = "%%", str_STATUT = "%%", str_BENEFICIAIRE = "%%",
            str_BANQUE = "%%", str_LIEU = "%%", str_CODE_MONNAIE = "%%",
            lg_MODE_REGLEMENT_ID = "%%";
    Integer int_PRICE_RESUME;
    Integer int_QUANTITY;
    Integer int_TAUX_CHANGE = 0;
    double dbl_AMOUNT = 0.0;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    //privilege Oprivilege = new privilege();
    TUser OTUser = null;
    dal.TPreenregistrement OTPreenregistrement = null;
    

    int int_total_vente = 0;

%>




<%
    if (request.getParameter("str_REF") != null) {
        str_REF = request.getParameter("str_REF");
    }
    if (request.getParameter("str_BENEFICIAIRE") != null) {
        str_BENEFICIAIRE = request.getParameter("str_BENEFICIAIRE");
    }

    if (request.getParameter("lg_ECART_MVT_ID") != null) {
        lg_ECART_MVT_ID = request.getParameter("lg_ECART_MVT_ID");

    }
    if (request.getParameter("str_BANQUE") != null) {
        str_BANQUE = request.getParameter("str_BANQUE");

    }
    if (request.getParameter("str_LIEU") != null) {
        str_LIEU = request.getParameter("str_LIEU");

    }
    if (request.getParameter("str_CODE_MONNAIE") != null) {
        str_CODE_MONNAIE = request.getParameter("str_CODE_MONNAIE");

    }

    if (request.getParameter("int_TAUX_CHANGE") != null) {
        int_TAUX_CHANGE = new Integer(request.getParameter("int_TAUX_CHANGE"));

    }

    if (request.getParameter("dbl_AMOUNT") != null) {
        dbl_AMOUNT = new Double(request.getParameter("dbl_AMOUNT"));
    }

    if (request.getParameter("lg_MODE_REGLEMENT_ID") != null) {
        lg_MODE_REGLEMENT_ID = request.getParameter("lg_MODE_REGLEMENT_ID");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_PREENREGISTREMENT_ID"));

    DiffereManagement ODiffereManagement = new DiffereManagement(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("reglerdif")) {
            new logger().oCategory.info("*** ID str_BENEFICIAIRE *** " + str_BENEFICIAIRE);
            //recuperer la liste des differes a regler la pourcourir et faire le reglement pour chacun
           // ListTEcartMvt =ODiffereManagement.func_beneficiaireListEcartMvt("1", str_BENEFICIAIRE);
            
            
        } else if (request.getParameter("mode").toString().equals("update")) {

        } else if (request.getParameter("mode").toString().equals("delete")) {

        } else if (request.getParameter("mode").toString().equals("delete")) {

        }
    } else {
    }

    String result;
    new logger().OCategory.info("ObllBase.getMessage() ---- " + ObllBase.getMessage());
    new logger().OCategory.info("commonparameter.PROCESS_SUCCESS ---- " + commonparameter.PROCESS_SUCCESS);//int_total_vente
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{lg_PREENREGISTREMENT_ID:\"" + OTPreenregistrement.getLgPREENREGISTREMENTID() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>