<%@page import="bll.tierspayantManagement.typetierspayantManagement"%>
<%@page import="bll.configManagement.groupeFamilleManagement"%>
<%@page import="dal.TTypeTiersPayant"%>
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

<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_TYPE_TIERS_PAYANT_ID = "%%", str_CODE_TYPE_TIERS_PAYANT = "%%", str_LIBELLE_TYPE_TIERS_PAYANT = "%%";
    Integer int_PRIORITY;
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    dal.TTypeTiersPayant OTGroupefamille = new dal.TTypeTiersPayant();
%>


<%
    if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID");
    }
    if (request.getParameter("str_CODE_TYPE_TIERS_PAYANT") != null) {
        str_CODE_TYPE_TIERS_PAYANT = request.getParameter("str_CODE_TYPE_TIERS_PAYANT");
    }
    if (request.getParameter("str_LIBELLE_TYPE_TIERS_PAYANT") != null) {
        str_LIBELLE_TYPE_TIERS_PAYANT = request.getParameter("str_LIBELLE_TYPE_TIERS_PAYANT");
    }

    // str_CODE_TYPE_TIERS_PAYANT
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_TYPE_TIERS_PAYANT_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");

            typetierspayantManagement OtypetierspayantManagement = new typetierspayantManagement(OdataManager);
            OtypetierspayantManagement.create(str_CODE_TYPE_TIERS_PAYANT, str_LIBELLE_TYPE_TIERS_PAYANT);

            new logger().oCategory.info("Creation  OOKKK");

        } else if (request.getParameter("mode").toString().equals("update")) {

            typetierspayantManagement OtypetierspayantManagement = new typetierspayantManagement(OdataManager);
            OtypetierspayantManagement.update(lg_TYPE_TIERS_PAYANT_ID, str_CODE_TYPE_TIERS_PAYANT, str_LIBELLE_TYPE_TIERS_PAYANT);

            new logger().oCategory.info("Modif OK  OOKKK");

        } else if (request.getParameter("mode").toString().equals("delete")) {

            typetierspayantManagement OtypetierspayantManagement = new typetierspayantManagement(OdataManager);
            OtypetierspayantManagement.delete(lg_TYPE_TIERS_PAYANT_ID);

            new logger().oCategory.info("Suppression du GroupeFamille " + request.getParameter("lg_TYPE_TIERS_PAYANT_ID").toString());

        } else {
        }

    }

    String result;
    TTypeTiersPayant OTTypeTiersPayant = new dal.TTypeTiersPayant();
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:1,lg_TYPE_TIERS_PAYANT_ID: \"" + OTTypeTiersPayant.getLgTYPETIERSPAYANTID()+ "\" }";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result); 
%>
<%=result%>