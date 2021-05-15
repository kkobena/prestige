<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="bll.teller.clientManager"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TCompteClient"%>
<%@page import="dal.TClient"%>
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

    TUser OTUser = null;

    TCompteClientTiersPayant OTCompteClientTiersPayant = null;


%>




<%    String lg_COMPTE_CLIENT_ID = "%%", lg_COMPTE_CLIENT_TIERS_PAYANT_ID = "%%", lg_TIERS_PAYANT_ID = "%%", lg_CLIENT_ID = "%%",
            RFIND = "", RFIND_TAUX = "", RFIND_PRIORITY = "", RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID = "", str_NUMERO_SECURITE_SOCIAL = "";
    int int_POURCENTAGE = 0;
    double dbl_QUOTA_CONSO_MENSUELLE = 0.00, dbl_QUOTA_CONSO_VENTE = 0.0;

    new logger().OCategory.info(" *** get tiers payant   *** ");

    if (request.getParameter("str_NUMERO_SECURITE_SOCIAL") != null) {
        str_NUMERO_SECURITE_SOCIAL = request.getParameter("str_NUMERO_SECURITE_SOCIAL");
        new logger().OCategory.info("str_NUMERO_SECURITE_SOCIAL " + str_NUMERO_SECURITE_SOCIAL);
    }

    if (request.getParameter("dbl_QUOTA_CONSO_VENTE") != null && !request.getParameter("dbl_QUOTA_CONSO_VENTE").equals("")) {
        dbl_QUOTA_CONSO_VENTE = Double.parseDouble(request.getParameter("dbl_QUOTA_CONSO_VENTE"));
        new logger().OCategory.info("dbl_QUOTA_CONSO_VENTE " + dbl_QUOTA_CONSO_VENTE);
    }

    if (request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID") != null) {
        lg_COMPTE_CLIENT_TIERS_PAYANT_ID = request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_TIERS_PAYANT_ID " + lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
    }

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);
    }
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID);
    }

    if (request.getParameter("int_POURCENTAGE") != null) {
        int_POURCENTAGE = new Integer((request.getParameter("int_POURCENTAGE")));
        new logger().OCategory.info("int_POURCENTAGE " + int_POURCENTAGE);
    }

    if (request.getParameter("dbl_QUOTA_CONSO_MENSUELLE") != null && !request.getParameter("dbl_QUOTA_CONSO_MENSUELLE").equals("")) {
        dbl_QUOTA_CONSO_MENSUELLE = Double.parseDouble(request.getParameter("dbl_QUOTA_CONSO_MENSUELLE"));
        new logger().OCategory.info("dbl_QUOTA_CONSO_MENSUELLE " + dbl_QUOTA_CONSO_MENSUELLE);
    }
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
   
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().OCategory.info("create");
            OtierspayantManagement.create_compteclt_tierspayant(lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE, 0, dbl_QUOTA_CONSO_MENSUELLE, dbl_QUOTA_CONSO_VENTE, str_NUMERO_SECURITE_SOCIAL,0,false);

            if (OTCompteClientTiersPayant == null) {
                RFIND = "";
                RFIND_TAUX = "";
                RFIND_PRIORITY = "";
                RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID = "";
                new logger().OCategory.info("desole OTCompteClientTiersPayant is null ");
            } else {
                RFIND = OTCompteClientTiersPayant.getLgTIERSPAYANTID().getStrNAME();
                RFIND_TAUX = (OTCompteClientTiersPayant.getIntPOURCENTAGE() + "");
                RFIND_PRIORITY = (OTCompteClientTiersPayant.getIntPRIORITY() + "");
                RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID = OTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID();

            }

            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());

        } else if (request.getParameter("mode").toString().equals("update")) {
            new logger().OCategory.info("update");
            OTCompteClientTiersPayant = OtierspayantManagement.updateComptecltTierspayantLigth(lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE);

            if (OTCompteClientTiersPayant == null) {
                RFIND = "";
                RFIND_TAUX = "";
                RFIND_PRIORITY = "";
                RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID = "";
                new logger().OCategory.info("desole OTCompteClientTiersPayant is null ");
            } else {
                RFIND = OTCompteClientTiersPayant.getLgTIERSPAYANTID().getStrNAME();
                RFIND_TAUX = (OTCompteClientTiersPayant.getIntPOURCENTAGE() + "");
                RFIND_PRIORITY = (OTCompteClientTiersPayant.getIntPRIORITY() + "");
                RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID = OTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID();

            }

            /*     TPreenregistrement  OTPreenregistrement = ObllBase.getOdataManager().getEm().find(dal.TPreenregistrement.class, request.getParameter("lg_PREENREGISTREMENT_ID"));
           
             int  int_total_vente = OPreenregistrement.GetVenteTotalwithRemise(OTPreenregistrement.getLgPREENREGISTREMENTID());

             int_cust_part = OclientManager.GetCustPart(int_total_vente, OTCompteClientTiersPayant.getIntPOURCENTAGE());
             */
            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());

        } else if (request.getParameter("mode").toString().equals("delete")) {

            OtierspayantManagement.delete_compteclt_tierspayant(lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());

        } else {
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", RFIND: \"" + RFIND + "\", RFIND_TAUX: \"" + RFIND_TAUX + "\", RFIND_PRIORITY: \"" + RFIND_PRIORITY + "\", RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID: \"" + RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", RFIND: \"" + RFIND + "\", RFIND_TAUX: \"" + RFIND_TAUX + "\", RFIND_PRIORITY: \"" + RFIND_PRIORITY + "\", RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID: \"" + RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>