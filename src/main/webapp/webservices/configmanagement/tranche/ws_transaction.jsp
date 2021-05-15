<%@page import="bll.configManagement.TrancheManagement"%>
<%@page import="dal.TTranche"%>
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

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TTranche OTTranche = null;
    String lg_TRANCHE_ID = "%%";
    int int_MONTANT_MIN = 0, int_MONTANT_MAX = 0;
    double dbl_POURCENTAGE_TRANCHE = 0.00;

%>




<%
    new logger().oCategory.info("Parametre lg_TRANCHE_ID : " + request.getParameter("lg_TRANCHE_ID"));
    if (request.getParameter("lg_TRANCHE_ID") != null) {
        lg_TRANCHE_ID = request.getParameter("lg_TRANCHE_ID");
    }

    new logger().oCategory.info("Parametre int_MONTANT_MIN : " + request.getParameter("int_MONTANT_MIN"));
    if (request.getParameter("int_MONTANT_MIN") != null) {
        int_MONTANT_MIN = Integer.parseInt(request.getParameter("int_MONTANT_MIN"));
    }

    new logger().oCategory.info("Parametre int_MONTANT_MAX : " + request.getParameter("int_MONTANT_MAX"));
    if (request.getParameter("int_MONTANT_MAX") != null) {
        int_MONTANT_MAX = Integer.parseInt(request.getParameter("int_MONTANT_MAX"));
    }

    new logger().oCategory.info("Parametre dbl_POURCENTAGE_TRANCHE : " + request.getParameter("dbl_POURCENTAGE_TRANCHE"));
    if (request.getParameter("dbl_POURCENTAGE_TRANCHE") != null) {
        dbl_POURCENTAGE_TRANCHE = Double.parseDouble(request.getParameter("dbl_POURCENTAGE_TRANCHE"));
    }

    new logger().oCategory.info("RECEPTION DE TOUS LES PARAMETRE : OK");

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("TRANCHE WS TRANSACTION");

    bll.configManagement.TrancheManagement OTrancheManagement = new TrancheManagement(OdataManager, OTUser);

    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_TRANCHE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            OTrancheManagement.create(dbl_POURCENTAGE_TRANCHE, int_MONTANT_MIN, int_MONTANT_MAX);

            ObllBase.setDetailmessage(OTrancheManagement.getDetailmessage());
            ObllBase.setMessage(OTrancheManagement.getMessage());

        } else if (request.getParameter("mode").toString().equals("update")) {
            new logger().OCategory.info(" dans mode  " + request.getParameter("mode").toString());
            new logger().OCategory.info(" *** lg_TRANCHE_ID *** " + lg_TRANCHE_ID);
            new logger().OCategory.info(" *** dbl_POURCENTAGE_TRANCHE *** " + dbl_POURCENTAGE_TRANCHE);
            new logger().OCategory.info(" *** int_MONTANT_MIN *** " + int_MONTANT_MIN);
            new logger().OCategory.info(" *** int_MONTANT_MAX *** " + int_MONTANT_MAX);

            OTTranche = OTrancheManagement.update(lg_TRANCHE_ID, dbl_POURCENTAGE_TRANCHE, int_MONTANT_MIN, int_MONTANT_MAX);
            new logger().OCategory.info(" *** update tranche ok  *** " + OTTranche.getLgTRANCHEID());
            ObllBase.setDetailmessage(OTrancheManagement.getDetailmessage());
            ObllBase.setMessage(OTrancheManagement.getMessage());

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TTranche OTTranche = null;
            OTTranche = ObllBase.getOdataManager().getEm().find(dal.TTranche.class, request.getParameter("lg_TRANCHE_ID"));

            OTTranche.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTTranche);

            new logger().oCategory.info("Suppression de tranche " + request.getParameter("lg_TRANCHE_ID").toString());

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