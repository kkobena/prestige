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
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    TUser OTUser = null;
    dal.TCompteClient OTCompteClient = null;

    TCompteClientTiersPayant OTCompteClientTiersPayant = null;
    TTiersPayant OTTiersPayant = null;


%>




<%    String lg_COMPTE_CLIENT_ID = "%%", lg_COMPTE_CLIENT_TIERS_PAYANT_ID = "%%", lg_TIERS_PAYANT_ID = "%%", str_NUMERO_SECURITE_SOCIAL = "";
    int int_POURCENTAGE = 0;
    double dbl_QUOTA_CONSO_MENSUELLE = 0.00, dbl_QUOTA_CONSO_VENTE = 0.0;
    
  boolean  b_IsAbsolute=false;
  Integer dbPLAFONDENCOURS=0; 
    int int_PRIORITY = 0;
    String RFIND = "", RFIND_TAUX = "", RFIND_PRIORITY = "", RFIND_COMPTE_CLIENT_TIERS_PAYANT_ID = "";
    if (request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID") != null) {
        lg_COMPTE_CLIENT_TIERS_PAYANT_ID = request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_TIERS_PAYANT_ID " + lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
    }
     if (request.getParameter("str_NUMERO_SECURITE_SOCIAL") != null) {
        str_NUMERO_SECURITE_SOCIAL = request.getParameter("str_NUMERO_SECURITE_SOCIAL");
        new logger().OCategory.info("str_NUMERO_SECURITE_SOCIAL " + str_NUMERO_SECURITE_SOCIAL);
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
    if (request.getParameter("int_PRIORITY") != null) {
        int_PRIORITY = new Integer((request.getParameter("int_PRIORITY")));
        new logger().OCategory.info("int_PRIORITY " + int_PRIORITY);
    }

    if (request.getParameter("dbl_QUOTA_CONSO_MENSUELLE") != null) {
        dbl_QUOTA_CONSO_MENSUELLE = Double.parseDouble(request.getParameter("dbl_QUOTA_CONSO_MENSUELLE"));
        new logger().OCategory.info("dbl_QUOTA_CONSO_MENSUELLE " + dbl_QUOTA_CONSO_MENSUELLE);
    }

    if (request.getParameter("dbl_QUOTA_CONSO_VENTE") != null) {
        dbl_QUOTA_CONSO_VENTE = Double.parseDouble(request.getParameter("dbl_QUOTA_CONSO_VENTE"));
        
        dbPLAFONDENCOURS=Integer.valueOf(request.getParameter("dbl_QUOTA_CONSO_VENTE"));
        new logger().OCategory.info("dbl_QUOTA_CONSO_VENTE " + dbl_QUOTA_CONSO_VENTE);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
  
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().OCategory.info("create");
            OTCompteClientTiersPayant = OtierspayantManagement.create_compteclt_tierspayant(lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE, int_PRIORITY, dbl_QUOTA_CONSO_MENSUELLE, dbl_QUOTA_CONSO_VENTE, str_NUMERO_SECURITE_SOCIAL,dbPLAFONDENCOURS,b_IsAbsolute); 
            //OTCompteClientTiersPayant = OtierspayantManagement.createcompteclttierspayant(lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE,int_PRIORITY);
            // new logger().OCategory.info(" OTCompteClientTiersPayant create  " + OTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID());
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