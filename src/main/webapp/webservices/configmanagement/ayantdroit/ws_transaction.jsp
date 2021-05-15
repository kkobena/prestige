<%@page import="bll.configManagement.ayantDroitManagement"%>
<%@page import="dal.TAyantDroit"%>
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

<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();

%>

<%    String lg_AYANTS_DROITS_ID = "", str_CODE_INTERNE = "", str_FIRST_NAME = "", str_LAST_NAME = "", str_SEXE = "",
            lg_VILLE_ID = "", lg_CLIENT_ID = "", str_NUMERO_SECURITE_SOCIAL = "", lg_CATEGORIE_AYANTDROIT_ID = "555146116095894790", lg_RISQUE_ID = "55181642844215217016";
    Date dt_NAISSANCE = null;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
     TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    ayantDroitManagement OayantDroitManagement = new ayantDroitManagement(OdataManager);
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");

    if (request.getParameter("lg_AYANTS_DROITS_ID") != null) {
        lg_AYANTS_DROITS_ID = request.getParameter("lg_AYANTS_DROITS_ID");
        new logger().OCategory.info("lg_AYANTS_DROITS_ID " + lg_AYANTS_DROITS_ID);
    }
    if (request.getParameter("str_NUMERO_SECURITE_SOCIAL") != null) {
        str_NUMERO_SECURITE_SOCIAL = request.getParameter("str_NUMERO_SECURITE_SOCIAL");
        new logger().OCategory.info("str_NUMERO_SECURITE_SOCIAL " + str_NUMERO_SECURITE_SOCIAL);
    }
    if (request.getParameter("str_CODE_INTERNE") != null) {
        str_CODE_INTERNE = request.getParameter("str_CODE_INTERNE");
        new logger().OCategory.info("str_CODE_INTERNE " + str_CODE_INTERNE);
    }
    if (request.getParameter("str_FIRST_NAME") != null) {
        str_FIRST_NAME = request.getParameter("str_FIRST_NAME");
        new logger().OCategory.info("str_FIRST_NAME " + str_FIRST_NAME);
    }
    if (request.getParameter("str_LAST_NAME") != null) {
        str_LAST_NAME = request.getParameter("str_LAST_NAME");
        new logger().OCategory.info("str_LAST_NAME " + str_LAST_NAME);
    }
    if (request.getParameter("str_SEXE") != null) {
        str_SEXE = request.getParameter("str_SEXE");
        new logger().OCategory.info("str_SEXE " + str_SEXE);
    }
    // lg_VILLE_ID = "", lg_RISQUE_ID = "", lg_CLIENT_ID = "", lg_CATEGORIE_AYANTDROIT_ID = "";
    // dt_NAISSANCE
    if (request.getParameter("dt_NAISSANCE") != null) {
        dt_NAISSANCE = key.stringToDate(request.getParameter("dt_NAISSANCE"), key.formatterMysqlShort);
        new logger().OCategory.info("dt_NAISSANCE " + dt_NAISSANCE + " ----- " + request.getParameter("dt_NAISSANCE"));
    }
    if (request.getParameter("lg_VILLE_ID") != null) {
        lg_VILLE_ID = request.getParameter("lg_VILLE_ID");
        new logger().OCategory.info("lg_VILLE_ID " + lg_VILLE_ID);
    }
    if (request.getParameter("lg_RISQUE_ID") != null && !request.getParameter("lg_RISQUE_ID").equalsIgnoreCase("")) {
        lg_RISQUE_ID = request.getParameter("lg_RISQUE_ID");
        new logger().OCategory.info("lg_RISQUE_ID " + lg_RISQUE_ID);
    }
    if (request.getParameter("lg_CLIENT_ID") != null) {
        lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
        new logger().OCategory.info("lg_CLIENT_ID " + lg_CLIENT_ID);
    }
    if (request.getParameter("lg_CATEGORIE_AYANTDROIT_ID") != null && !request.getParameter("lg_CATEGORIE_AYANTDROIT_ID").equalsIgnoreCase("")) {
        lg_CATEGORIE_AYANTDROIT_ID = request.getParameter("lg_CATEGORIE_AYANTDROIT_ID");
        new logger().OCategory.info("lg_CATEGORIE_AYANTDROIT_ID " + lg_CATEGORIE_AYANTDROIT_ID);
    }

    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("create")) {

            OayantDroitManagement.createAyantdroit(lg_CLIENT_ID, lg_CATEGORIE_AYANTDROIT_ID, str_FIRST_NAME, str_LAST_NAME, str_SEXE, dt_NAISSANCE, lg_VILLE_ID, lg_RISQUE_ID, str_NUMERO_SECURITE_SOCIAL, str_CODE_INTERNE);

            ObllBase.setDetailmessage(OayantDroitManagement.getDetailmessage());
            ObllBase.setMessage(OayantDroitManagement.getMessage());
        } else if (request.getParameter("mode").equals("update")) {
            //OayantDroitManagement.updateAyantDroit(lg_AYANTS_DROITS_ID, lg_CLIENT_ID, lg_CATEGORIE_AYANTDROIT_ID, str_FIRST_NAME, str_LAST_NAME, str_SEXE, dt_NAISSANCE, lg_VILLE_ID, lg_RISQUE_ID, str_NUMERO_SECURITE_SOCIAL);
            OayantDroitManagement.updateAyantdroit(lg_AYANTS_DROITS_ID, lg_CLIENT_ID, lg_CATEGORIE_AYANTDROIT_ID, str_FIRST_NAME, str_LAST_NAME, str_SEXE, dt_NAISSANCE, lg_VILLE_ID, lg_RISQUE_ID, str_NUMERO_SECURITE_SOCIAL);
            ObllBase.setDetailmessage(OayantDroitManagement.getDetailmessage());
            ObllBase.setMessage(OayantDroitManagement.getMessage());

        } else if (request.getParameter("mode").equals("delete")) {
            OayantDroitManagement.removeAyantdroit(lg_AYANTS_DROITS_ID, lg_CLIENT_ID);
            ObllBase.setDetailmessage(OayantDroitManagement.getDetailmessage());
            ObllBase.setMessage(OayantDroitManagement.getMessage());
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