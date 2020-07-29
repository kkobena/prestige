<%@page import="dal.TEmplacement"%>
<%@page import="bll.configManagement.EmplacementManagement"%>
<%@page import="dal.TRemise"%>
<%@page import="bll.preenregistrement.DevisManagement"%>
<%@page import="dal.TCompteClient"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="bll.differe.DiffereManagement"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TPreenregistrementCompteClient"%>
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

<% String lg_PREENREGISTREMENT_ID = "%%", lg_COMPTE_CLIENT_ID = "%%", lg_TIERS_PAYANT_ID = "%%",
            lg_TYPE_VENTE_ID = "%%", lg_PREENREGISTREMENT_DETAIL_ID = "%%", str_REF = "%%", lg_USER_ID = "%%",
            dt_CREATED = "%%", lg_FAMILLE_ID = "%%", lg_TYPE_REGLEMENT_ID = "%%", str_STATUT = "%%";
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
    TPreenregistrement OTPreenregistrement = null;
    /* TPreenregistrementDetail OTPreenregistrementDetail = null;
     TCashTransaction OTCashTransaction = null;
     TTypeReglement OTTypeReglement = null;
     TPreenregistrementCompteClient OTPreenregistrementCompteClient = null;*/
    TCompteClientTiersPayant OTCompteClientTiersPayant = null;
    JSONArray arrayObj = new JSONArray();

    int int_total_vente = 0;
    int int_total_product = 0;
    int int_cust_part = 0;
    int int_total_percent = 0;
    Double dbl_total_remise = 0.0;
    Double dbl_net_apayer = 0.0;
    String lg_remise_id = "0";
    String lg_remise_id_temp = "";

    double dbl_total_differe = 0.0;
    boolean isCustSolvable = false;
    int issolvable = 0;
    OdataManager.initEntityManager();

    //DiffereManagement ODiffereManagement = new DiffereManagement(OdataManager, OTUser);
    //DevisManagement ODevisManagement = new DevisManagement(OdataManager, OTUser);
    TCompteClient OTCompteClient = null;
    TEmplacement OTEmplacement = null;
    //TRemise OTRemise = null;

    int total_list_ocust = 0;


%>




<%    if (request.getParameter("str_REF") != null) {
        str_REF = request.getParameter("str_REF");
    }
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID:" + lg_PREENREGISTREMENT_ID);
    }
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
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
        // new logger().OCategory.info("int_PRICE_DETAIL   " + request.getParameter("int_PRICE_DETAIL"));
        int_PRICE_DETAIL = new Integer(request.getParameter("int_PRICE_DETAIL"));

    }

    if (request.getParameter("int_TOTAL_VENTE_RECAP") != null) {
        // new logger().OCategory.info("int_TOTAL_VENTE_RECAP   " + request.getParameter("int_TOTAL_VENTE_RECAP"));
        int_TOTAL_VENTE_RECAP = new Integer(request.getParameter("int_TOTAL_VENTE_RECAP"));

    }

    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null) {
        //new logger().OCategory.info("lg_TYPE_REGLEMENT_ID   " + request.getParameter("lg_TYPE_REGLEMENT_ID"));
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");

    }

    if (request.getParameter("lg_TYPE_VENTE_ID") != null) {
        //new logger().OCategory.info("**** lg_TYPE_VENTE_ID 1  **** " + request.getParameter("lg_TYPE_VENTE_ID"));
        lg_TYPE_VENTE_ID = request.getParameter("lg_TYPE_VENTE_ID");

    }
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        // new logger().OCategory.info("**** lg_TIERS_PAYANT_ID  **** " + request.getParameter("lg_TIERS_PAYANT_ID"));
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");

    }
    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        // new logger().OCategory.info("**** lg_COMPTE_CLIENT_ID   **** " + request.getParameter("lg_COMPTE_CLIENT_ID"));
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");

    }

    if (request.getParameter("int_AMOUNT_RECU") != null) {
        // new logger().OCategory.info("int_AMOUNT_RECU   " + request.getParameter("int_AMOUNT_RECU"));
        int_AMOUNT_RECU = new Integer(request.getParameter("int_AMOUNT_RECU"));

    }

    if (request.getParameter("int_AMOUNT_REMIS") != null) {
        //   new logger().OCategory.info("int_AMOUNT_REMIS   " + request.getParameter("int_AMOUNT_REMIS"));
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
    clientManagement OclientManagement = new clientManagement(OdataManager, OTUser);
    EmplacementManagement OEmplacementManagement = new EmplacementManagement(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {
        new logger().OCategory.info("**** lg_TYPE_VENTE_ID   **** " + lg_TYPE_VENTE_ID);
        if (request.getParameter("mode").toString().equals("init")) {
            new logger().OCategory.info("init");
            OTPreenregistrement = OPreenregistrement.getTPreenregistrementByRef(lg_PREENREGISTREMENT_ID);

            int_total_vente = OPreenregistrement.GetVenteTotalAmountTTc(OTPreenregistrement.getLgPREENREGISTREMENTID());
            int_total_product = OPreenregistrement.GetProductTotal(OTPreenregistrement.getLgPREENREGISTREMENTID());
            // dbl_total_remise = OPreenregistrement.GetTotalAmountRemise(OTPreenregistrement.getLgPREENREGISTREMENTID()); // a decommenter en cas de probleme
            dbl_total_remise = OTPreenregistrement.getIntPRICEREMISE().doubleValue();
            /*lg_remise_id_temp = OTPreenregistrement.getLgREMISEID();
             if (lg_remise_id_temp == null) {
             lg_remise_id = "Aucun";
             } else {
             OTRemise = ObllBase.getOdataManager().getEm().find(dal.TRemise.class, lg_remise_id_temp);
             if (OTRemise == null) {
             lg_remise_id = "Aucun";
             } else {
             lg_remise_id = OTRemise.getStrNAME();
             }
             }*/
            lg_remise_id = OTPreenregistrement.getLgREMISEID();
            JSONObject json = new JSONObject();

            //OTCompteClient = OclientManagement.getTCompteClient(OTPreenregistrement.getStrFIRSTNAMECUSTOMER() + " " + OTPreenregistrement.getStrLASTNAMECUSTOMER()); // a decommenter en cas de probleme 20/12/2016
            OTCompteClient = OclientManagement.getTCompteClient(OTPreenregistrement.getStrFIRSTNAMECUSTOMER());

            int_total_percent = 0;

            if (OTCompteClient != null) {
                OTEmplacement = OEmplacementManagement.getEmplacement(OTCompteClient.getLgCOMPTECLIENTID());
                json.put("str_STATUT", OTPreenregistrement.getStrSTATUT());
                json.put("lg_COMPTE_CLIENT_ID", OTCompteClient.getLgCOMPTECLIENTID());

                json.put("lg_EMPLACEMENT_ID", OTEmplacement != null ? OTEmplacement.getLgEMPLACEMENTID() : "");
                json.put("lg_TYPEDEPOT_ID", ((OTEmplacement != null && OTEmplacement.getLgTYPEDEPOTID() != null) ? OTEmplacement.getLgTYPEDEPOTID().getLgTYPEDEPOTID() : ""));
                json.put("str_EMPLACEMENT", OTEmplacement.getStrDESCRIPTION());
                json.put("lg_CLIENT_ID", OTCompteClient.getLgCLIENTID().getLgCLIENTID());
                json.put("lg_TYPE_VENTE_ID", OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID());
                json.put("lg_USER_VENDEUR_ID", OTPreenregistrement.getLgUSERVENDEURID().getStrFIRSTNAME() + " " + OTPreenregistrement.getLgUSERVENDEURID().getStrLASTNAME());
                json.put("str_CODE_INTERNE", OTCompteClient.getLgCLIENTID().getStrCODEINTERNE());
                try {
                    json.put("str_FIRST_NAME", OTPreenregistrement.getStrFIRSTNAMECUSTOMER().split(" ")[0]);
                    json.put("str_LAST_NAME", OTPreenregistrement.getStrFIRSTNAMECUSTOMER().split(" ")[1]);
                } catch (Exception e) {
                }
                json.put("str_FIRST_LAST_NAME", OTPreenregistrement.getStrFIRSTNAMECUSTOMER());
                json.put("str_NUMERO_SECURITE_SOCIAL", OTPreenregistrement.getStrNUMEROSECURITESOCIAL());
                arrayObj.put(json);

                /*dbl_total_differe = ODiffereManagement.func_beneficiaireTotalDiffere(OTCompteClient.getLgCOMPTECLIENTID());

                 if (!ODiffereManagement.func_CheckIfCustIsSolvable(OTCompteClient.getLgCOMPTECLIENTID())) {
                 issolvable = 0;
                 } else {
                 issolvable = 1;
                 }*/
                total_list_ocust = 1;

                /*lg_remise_id_temp = OTPreenregistrement.getLgREMISEID();
                 if (lg_remise_id_temp == null) {
                 lg_remise_id = "Aucun";
                 } else {
                 OTRemise = ObllBase.getOdataManager().getEm().find(dal.TRemise.class, lg_remise_id_temp);
                 if (OTRemise == null) {
                 lg_remise_id = "Aucun";
                 } else {
                 lg_remise_id = OTRemise.getStrNAME();
                 }
                 }*/
                lg_remise_id = OTPreenregistrement.getLgREMISEID();
//                dbl_net_apayer = ODevisManagement.GetVente_NetAPayer(OTPreenregistrement, 0);
            }

            ObllBase.setMessage(commonparameter.PROCESS_SUCCESS);

        }

    }
    String result;
    String str_ref_init = OTPreenregistrement.getLgPREENREGISTREMENTID();
    String str_ref_init_id = OTPreenregistrement.getStrREF();

    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "({\"ref_init\":" + "\"" + str_ref_init + "\"" + ",\"str_ref_init_id\":" + "\"" + str_ref_init_id + "\"" + ",\"lg_remise_id\":" + "\"" + lg_remise_id + "\"" + ",\"total_list_ocust\":" + total_list_ocust + ",\"dbl_net_apayer\":" + dbl_net_apayer + ",\"dbl_total_remise\":" + dbl_total_remise + ",\"int_total_vente\":" + int_total_vente + ",\"int_total_product\":" + int_total_product + ",\"int_cust_part\":" + int_cust_part + ",\"dbl_total_differe\":" + dbl_total_differe + ",\"isCustSolvable\":" + issolvable + ",\"results\":" + arrayObj.toString() + "})";
    } else {
        result = "({\"ref_init\":" + "\"" + str_ref_init + "\"" + ",\"str_ref_init_id\":" + "\"" + str_ref_init_id + "\"" + ",\"lg_remise_id\":" + "\"" + lg_remise_id + "\"" + ",\"total_list_ocust\":" + total_list_ocust + ",\"dbl_net_apayer\":" + dbl_net_apayer + ",\"dbl_total_remise\":" + dbl_total_remise + ",\"int_total_vente\":" + int_total_vente + ",\"int_total_product\":" + int_total_product + ",\"int_cust_part\":" + int_cust_part + ",\"dbl_total_differe\":" + dbl_total_differe + ",\"isCustSolvable\":" + issolvable + ",\"results\":" + arrayObj.toString() + "})";
    }
    new logger().OCategory.info("JSON " + result);

    OdataManager = null;
    OPreenregistrement = null;
    OTPreenregistrement = null;
%>
<%=result%>