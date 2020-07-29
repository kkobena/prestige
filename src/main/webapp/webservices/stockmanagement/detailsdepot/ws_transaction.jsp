<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TModeReglement"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.stockManagement.DepotManager"%>
<%@page import="bll.configManagement.EmplacementManagement"%>
<%@page import="dal.TEmplacement"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.common.Parameter"%>
<%@page import="dal.TCompteClient"%>
<%@page import="bll.commandeManagement.suggestionManagement"%>
<%@page import="bll.preenregistrement.DevisManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="dal.TFamille"%>
<%@page import="bll.teller.clientManager"%>
<%@page import="toolkits.utils.StringUtils"%>
<%@page import="bll.differe.DiffereManagement"%>
<%@page import="dal.TMotifReglement"%>
<%@page import="dal.TReglement"%>
<%@page import="dal.TCompteClientTiersPayant"%>
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

<%
    Integer int_PRICE_DETAIL = 0;
    Integer int_QUANTITY = 0;
    Integer int_QUANTITY_SERVED = 0;
    Integer int_TOTAL_VENTE_RECAP = 0;
    Integer int_AMOUNT_REMIS = 0;
    Integer int_AMOUNT_RECU = 0;

    Integer int_TAUX_CHANGE = 0;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    //privilege Oprivilege = new privilege();

    TUser OTUser = null;
    dal.TPreenregistrement OTPreenregistrement = null;
    TPreenregistrementDetail OTPreenregistrementDetail = null;
    TFamille OTFamille = null;

    int int_total_vente = 0;
    int int_total_product = 0;

    Double dbl_total_remise = 0.0;
    Double dbl_net_apayer = 0.0;
    int int_cust_part = 0;
    int int_total_percent = 0;
    int int_famille_stock_available = 0;
    int int_famille_stock_reel = 0;
    int amount_vente_first_total = 0;
    int int_nb_limite = 0;
    TFamilleStock OTFamilleStock = new TFamilleStock();
    TEmplacement OTEmplacement = null;

%>




<%    String lg_PREENREGISTREMENT_ID = "", lg_PREENREGISTREMENT_DETAIL_ID = "", str_NOM = "",
            str_INFOS_CLT = "", str_REF = "", lg_USER_ID = "", dt_CREATED = "",
            lg_FAMILLE_ID = "", lg_TYPE_REGLEMENT_ID = "",
            str_MEDECIN = "", lg_NATURE_VENTE_ID = "3", lg_TIERS_PAYANT_ID = "",
            lg_REGLEMENT_ID = "", str_BANQUE = "", str_LIEU = "",
            str_CODE_MONNAIE = "", lg_MODE_REGLEMENT_ID = "", lg_TYPE_VENTE_ID = "",
            str_REF_BON = "", lg_MOTIF_REGLEMENT_ID = "", str_ORDONNANCE = "", str_STATUT = "",
            str_FIRST_NAME_FACTURE = "", str_LAST_NAME_FACTURE = "", int_NUMBER_FACTURE = "", lg_EMPLACEMENT_ID = "", str_NUMERO_SECURITE_SOCIAL = "";
    String lg_COMPTE_CLIENT_ID = "", mode_change = "", lg_COMPTE_CLIENT_TIERS_PAYANT_ID = "", KEY_PARAMETER = Parameter.KEY_LAST_ORDER_NUMBER_PREVENTE,
            lg_USER_VENDEUR_ID = "";
    int lg_REMISE_ID = 0;
    new logger().OCategory.info("La mille  :" + request.getParameter("lg_FAMILLE_ID"));

    String str_ref = "";
    String str_ref_id = "";
    if (request.getParameter("str_REF") != null) {
        str_REF = request.getParameter("str_REF");
    }
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
    }

    /* if (request.getParameter("lg_FAMILLE_ID") != null) {
     lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");

     }*/
    if (request.getParameter("str_INFOS_CLT") != null) {
        str_INFOS_CLT = request.getParameter("str_INFOS_CLT");
        new logger().OCategory.info("str_INFOS_CLT   " + request.getParameter("str_INFOS_CLT"));
    }

    if (request.getParameter("lg_USER_VENDEUR_ID") != null && !request.getParameter("lg_USER_VENDEUR_ID").equalsIgnoreCase("")) {
        lg_USER_VENDEUR_ID = request.getParameter("lg_USER_VENDEUR_ID");
        new logger().OCategory.info("lg_USER_VENDEUR_ID " + lg_USER_VENDEUR_ID);
    }

    if (request.getParameter("str_FIRST_NAME_FACTURE") != null) {
        str_FIRST_NAME_FACTURE = request.getParameter("str_FIRST_NAME_FACTURE");
        new logger().OCategory.info("str_FIRST_NAME_FACTURE " + str_FIRST_NAME_FACTURE);
    }

    if (request.getParameter("str_LAST_NAME_FACTURE") != null) {
        str_LAST_NAME_FACTURE = request.getParameter("str_LAST_NAME_FACTURE");
        new logger().OCategory.info("str_LAST_NAME_FACTURE " + str_LAST_NAME_FACTURE);
    }

    if (request.getParameter("int_NUMBER_FACTURE") != null) {
        int_NUMBER_FACTURE = request.getParameter("int_NUMBER_FACTURE");
        new logger().OCategory.info("int_NUMBER_FACTURE " + int_NUMBER_FACTURE);
    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT   " + request.getParameter("str_STATUT"));

    }

    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");

    }

    if (request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID") != null) {
        lg_PREENREGISTREMENT_DETAIL_ID = request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID");
        new logger().OCategory.info(" *** lg_PREENREGISTREMENT_DETAIL_ID recuperation   *** " + lg_PREENREGISTREMENT_DETAIL_ID);
    }
    //lg_PREENREGISTREMENT_DETAIL_ID
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID   " + lg_FAMILLE_ID);
    }

    if (request.getParameter("int_QUANTITY") != null) {
        int_QUANTITY = new Integer(request.getParameter("int_QUANTITY"));
        new logger().OCategory.info("int_QUANTITY   " + int_QUANTITY);
    }

    if (request.getParameter("int_QUANTITY_SERVED") != null) {
        int_QUANTITY_SERVED = new Integer(request.getParameter("int_QUANTITY_SERVED"));
        new logger().OCategory.info("int_QUANTITY_SERVED   " + int_QUANTITY_SERVED);
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

    // Recupération du medecin et de la nature de la vente
    if (request.getParameter("str_MEDECIN") != null) {
        new logger().OCategory.info("str_MEDECIN   " + request.getParameter("str_MEDECIN"));
        str_MEDECIN = request.getParameter("str_MEDECIN");

    }

    if (request.getParameter("str_ORDONNANCE") != null) {
        new logger().OCategory.info("str_ORDONNANCE   " + request.getParameter("str_ORDONNANCE"));
        str_ORDONNANCE = request.getParameter("str_ORDONNANCE");

    }

    if (request.getParameter("lg_NATURE_VENTE_ID") != null) {
        new logger().OCategory.info("lg_NATURE_VENTE_ID   " + request.getParameter("lg_NATURE_VENTE_ID"));
        lg_NATURE_VENTE_ID = request.getParameter("lg_NATURE_VENTE_ID");

    }

    if (request.getParameter("lg_REGLEMENT_ID") != null) {
        new logger().OCategory.info("lg_REGLEMENT_ID   " + request.getParameter("lg_REGLEMENT_ID"));
        lg_REGLEMENT_ID = request.getParameter("lg_REGLEMENT_ID");

    }

    if (request.getParameter("str_BANQUE") != null) {
        str_BANQUE = request.getParameter("str_BANQUE");
        new logger().OCategory.info("str_BANQUE   " + str_BANQUE);
    }

    if (request.getParameter("str_LIEU") != null) {
        str_LIEU = request.getParameter("str_LIEU");
        new logger().OCategory.info("str_LIEU   " + str_LIEU);
    }

    if (request.getParameter("str_CODE_MONNAIE") != null) {
        new logger().OCategory.info("str_CODE_MONNAIE   " + request.getParameter("str_CODE_MONNAIE"));
        str_CODE_MONNAIE = request.getParameter("str_CODE_MONNAIE");

    }

    if (request.getParameter("lg_MODE_REGLEMENT_ID") != null) {
        new logger().OCategory.info("lg_MODE_REGLEMENT_ID   " + request.getParameter("lg_MODE_REGLEMENT_ID"));
        lg_MODE_REGLEMENT_ID = request.getParameter("lg_MODE_REGLEMENT_ID");

    }
    if (request.getParameter("str_NOM") != null && !"".equalsIgnoreCase(request.getParameter("str_NOM"))) {
     
        str_NOM = request.getParameter("str_NOM");

    }
    if (request.getParameter("int_TAUX_CHANGE") != null) {
        new logger().OCategory.info("int_TAUX_CHANGE   " + request.getParameter("int_TAUX_CHANGE"));
        int_TAUX_CHANGE = new Integer(request.getParameter("int_TAUX_CHANGE"));

    }

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID   " + request.getParameter("lg_COMPTE_CLIENT_ID"));
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");

    }

    if (request.getParameter("lg_EMPLACEMENT_ID") != null && !request.getParameter("lg_EMPLACEMENT_ID").equals("")) {
        lg_EMPLACEMENT_ID = request.getParameter("lg_EMPLACEMENT_ID");
        new logger().OCategory.info("lg_EMPLACEMENT_ID " + lg_EMPLACEMENT_ID);

    }

    if (request.getParameter("str_REF_BON") != null) {
        new logger().OCategory.info("str_REF_BON   " + request.getParameter("str_REF_BON"));
        str_REF_BON = request.getParameter("str_REF_BON");

    }
    if (request.getParameter("lg_TYPE_VENTE_ID") != null) {
        new logger().OCategory.info("lg_TYPE_VENTE_ID   " + request.getParameter("lg_TYPE_VENTE_ID"));
        lg_TYPE_VENTE_ID = request.getParameter("lg_TYPE_VENTE_ID");

    }

    if (request.getParameter("lg_REMISE_ID") != null && !request.getParameter("lg_REMISE_ID").equalsIgnoreCase("")) {
        lg_REMISE_ID = Integer.parseInt(request.getParameter("lg_REMISE_ID"));
        new logger().OCategory.info("lg_REMISE_ID " + lg_REMISE_ID);
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
    DepotManager ODepotManager = new DepotManager(OdataManager, OTUser);
    DiffereManagement ODiffereManagement = new DiffereManagement(OdataManager, OTUser);
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);
    familleManagement OfamilleManagement = new familleManagement(OdataManager, OTUser);
    EmplacementManagement OEmplacementManagement = new EmplacementManagement(OdataManager, OTUser);
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    str_ref = lg_PREENREGISTREMENT_ID;
    boolean answer_decondition = false;
    TMotifReglement OTMotifReglement = ObllBase.getOdataManager().getEm().find(dal.TMotifReglement.class, "1");

    if (request.getParameter("mode") != null) {
        OTEmplacement = OEmplacementManagement.getEmplacement(lg_EMPLACEMENT_ID);
        if (OTEmplacement != null) {
            if (OTEmplacement.getLgTYPEDEPOTID().getLgTYPEDEPOTID().equals("1")) {
                lg_TYPE_VENTE_ID = Parameter.VENTE_DEPOT_AGREE;
            } else if (OTEmplacement.getLgTYPEDEPOTID().getLgTYPEDEPOTID().equals("2")) {
                lg_TYPE_VENTE_ID = Parameter.VENTE_DEPOT_EXTENSION;
            }
            str_FIRST_NAME_FACTURE = OTEmplacement.getStrFIRSTNAME();
            str_LAST_NAME_FACTURE = OTEmplacement.getStrLASTNAME();
        }

        if (request.getParameter("mode").equals("create")) {

            OTFamille = OfamilleManagement.getTFamille(lg_FAMILLE_ID);
            if (OTFamille == null) {
                String result;
                ObllBase.setMessage(commonparameter.PROCESS_FAILED);
                ObllBase.setDetailmessage("Le produit est introuvable");
                if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {//int_total_vente
                    result = "{ref:\"" + str_ref + "\",str_ref_id:\"" + str_ref_id + "\",dbl_net_apayer:\"" + dbl_net_apayer + "\",int_nb_limite:\"" + int_nb_limite + "\",int_famille_stock_available:\"" + int_famille_stock_available + "\",int_total_vente:\"" + 0 + "\",int_famille_stock_reel:\"" + int_famille_stock_reel + "\",dbl_total_remise:\"" + dbl_total_remise + "\",amount_vente_first_total:\"" + amount_vente_first_total + "\", int_total_product: \"" + int_total_product + "\", int_cust_part: \"" + int_cust_part + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
                } else {
                    result = "{ref:\"" + str_ref + "\",str_ref_id:\"" + str_ref_id + "\",dbl_net_apayer:\"" + dbl_net_apayer + "\",answer_decondition:\"" + answer_decondition + "\",int_nb_limite:\"" + int_nb_limite + "\",int_famille_stock_available:\"" + int_famille_stock_available + "\",int_total_vente:\"" + 0 + "\",int_famille_stock_reel:\"" + int_famille_stock_reel + "\",dbl_total_remise:\"" + dbl_total_remise + "\",amount_vente_first_total:\"" + amount_vente_first_total + "\", int_total_product: \"" + int_total_product + "\", int_cust_part: \"" + int_cust_part + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
                }
                new logger().OCategory.info("JSON " + result);

                out.println(result);
                return;
            }
            OTFamilleStock = OtellerManagement.getTProductItemStock(OTFamille);
            if (OTFamilleStock.getLgFAMILLEID().getBoolDECONDITIONNE() == 1) {
                JSONArray arrayObj = new JSONArray();
                JSONObject json = new JSONObject();
                if (!OPreenregistrement.checkIsVentePossible(OTFamilleStock, int_QUANTITY_SERVED)) {
                    answer_decondition = true;
                    json.put("answer_decondition", answer_decondition);
                    arrayObj.put(json);
                    String result = "{results:" + arrayObj.toString() + ", errors_code: \"" + OPreenregistrement.getMessage() + "\", errors: \"" + OPreenregistrement.getDetailmessage() + "\"}";
                    out.println(result);

                    return;
                }
            }
            new logger().OCategory.info(" **************************************************** =  " + OTFamilleStock);
            OTPreenregistrement = ODepotManager.createVente(OTEmplacement, request.getParameter("lg_PREENREGISTREMENT_ID"), OTEmplacement.getLgCOMPTECLIENTID().getLgCOMPTECLIENTID(), lg_NATURE_VENTE_ID, new Date(), KEY_PARAMETER, lg_USER_VENDEUR_ID, OTFamille, int_PRICE_DETAIL, int_QUANTITY, int_QUANTITY_SERVED, lg_TYPE_VENTE_ID, lg_REMISE_ID, OTFamille.getIntPRICE());
            if (OTPreenregistrement != null) {
                dbl_total_remise = new Double(OTPreenregistrement.getIntPRICEREMISE() + "");
                new logger().OCategory.info("dbl_total_remise create =  " + dbl_total_remise);
                int_total_vente = OTPreenregistrement.getIntPRICE(); //brut
                amount_vente_first_total = int_total_vente;//OPreenregistrement.GetVenteTotal(OTPreenregistrement.getLgPREENREGISTREMENTID());

                int_total_product = OPreenregistrement.GetProductTotal(OTPreenregistrement.getLgPREENREGISTREMENTID());

                str_ref = OTPreenregistrement.getLgPREENREGISTREMENTID();
                str_ref_id = OTPreenregistrement.getStrREF();

                if (OTFamilleStock != null) {
                    int_famille_stock_available = OTFamilleStock.getIntNUMBERAVAILABLE();
                    int_famille_stock_reel = int_famille_stock_available;// OTFamilleStock.getIntNUMBER();

                }
                new logger().OCategory.info("int_famille_stock =  " + int_famille_stock_available);

                dbl_net_apayer = conversion.convertIntToDouble(OTPreenregistrement.getIntPRICE() - OTPreenregistrement.getIntPRICEREMISE());

                // ObllBase.persiste(OTPreenregistrement);
                ObllBase.setMessage(ODepotManager.getMessage());
                ObllBase.setDetailmessage(ODepotManager.getDetailmessage());
            }
            int_nb_limite = ODiffereManagement.getNbrVenteLimite();

        } else if (request.getParameter("mode").equals("update")) {
            OTFamille = OfamilleManagement.getTFamille(lg_FAMILLE_ID);

            TPreenregistrementDetail OPreenregistrementDetail = ODepotManager.UpdateDetailsPreenregistrement(lg_PREENREGISTREMENT_DETAIL_ID, lg_PREENREGISTREMENT_ID, OTFamille.getLgFAMILLEID(), int_PRICE_DETAIL, int_QUANTITY, int_QUANTITY_SERVED, lg_TYPE_VENTE_ID, amount_vente_first_total, lg_REMISE_ID);
            ODiffereManagement.UpdateArticlePrice(OPreenregistrementDetail, int_PRICE_DETAIL);

            OTFamilleStock = OtellerManagement.getTProductItemStock(OTFamille.getLgFAMILLEID());
            if (OTFamilleStock != null) {
                int_famille_stock_available = OTFamilleStock.getIntNUMBERAVAILABLE();
                int_famille_stock_reel = int_famille_stock_available;// OTFamilleStock.getIntNUMBER();
            }
            int_nb_limite = ODiffereManagement.getNbrVenteLimite();
            int_total_product = OPreenregistrement.GetProductTotal(OPreenregistrementDetail.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());

            dbl_total_remise = new Double(OPreenregistrementDetail.getLgPREENREGISTREMENTID().getIntPRICEREMISE() + "");
            int_total_vente = OPreenregistrementDetail.getLgPREENREGISTREMENTID().getIntPRICE();
            dbl_net_apayer = conversion.convertIntToDouble(OPreenregistrementDetail.getLgPREENREGISTREMENTID().getIntPRICE() - OPreenregistrementDetail.getLgPREENREGISTREMENTID().getIntPRICEREMISE());
            ObllBase.setMessage(ODepotManager.getMessage());
            ObllBase.setDetailmessage(ODepotManager.getDetailmessage());

        } else if (request.getParameter("mode").equals("delete")) {
            new logger().OCategory.info("dans delete");
            try {
                OTPreenregistrement = ODepotManager.DeletePreenregistrementDetail(lg_PREENREGISTREMENT_DETAIL_ID, lg_REMISE_ID);
                int_total_vente = OTPreenregistrement.getIntPRICE();
                int_total_product = OPreenregistrement.GetProductTotal(OTPreenregistrement.getLgPREENREGISTREMENTID());
                dbl_total_remise = new Double(OTPreenregistrement.getIntPRICEREMISE() + "");
                new logger().OCategory.info("dbl_total_remise create =  " + dbl_total_remise);
                dbl_net_apayer = conversion.convertIntToDouble(OTPreenregistrement.getIntPRICE() - OTPreenregistrement.getIntPRICEREMISE());

            } catch (Exception e) {
                ObllBase.buildErrorTraceMessage("Echec de suppression du produit");
                e.printStackTrace();
            }

        } else if (request.getParameter("mode").equals("cloturer")) {
            ODepotManager.cloturerVente(lg_COMPTE_CLIENT_ID, request.getParameter("lg_PREENREGISTREMENT_ID"), lg_TYPE_REGLEMENT_ID, int_TOTAL_VENTE_RECAP, int_AMOUNT_RECU, int_AMOUNT_REMIS, lg_MODE_REGLEMENT_ID, OTEmplacement.getLgCOMPTECLIENTID().getLgCOMPTECLIENTID(), OTMotifReglement.getLgMOTIFREGLEMENTID(), str_ORDONNANCE, str_FIRST_NAME_FACTURE, str_LAST_NAME_FACTURE, int_NUMBER_FACTURE, str_NUMERO_SECURITE_SOCIAL, lg_REMISE_ID, lg_USER_VENDEUR_ID, str_BANQUE, str_LIEU, str_CODE_MONNAIE, int_TAUX_CHANGE, str_NOM);

            ObllBase.setMessage(ODepotManager.getMessage());
            ObllBase.setDetailmessage(ODepotManager.getDetailmessage());

        } else if (request.getParameter("mode").equals("updateremise")) {
            OTPreenregistrement = ODepotManager.updatRemiseForVenteDepot(lg_PREENREGISTREMENT_ID, lg_REMISE_ID);

            int_total_vente = OTPreenregistrement.getIntPRICE();

            dbl_total_remise = new Double(OTPreenregistrement.getIntPRICEREMISE() + "");
            new logger().OCategory.info("dbl_total_remise create =  " + dbl_total_remise);

            dbl_net_apayer = conversion.convertIntToDouble(OTPreenregistrement.getIntPRICE() - OTPreenregistrement.getIntPRICEREMISE());

        } else if (request.getParameter("mode").equals("annulervente")) {

            new logger().OCategory.info("dans annulervente");
            try {
                new logger().OCategory.info("lg_PREENREGISTREMENT_ID   annuler vente  " + lg_PREENREGISTREMENT_ID);
                OTPreenregistrement = ODepotManager.AnnulerVente(lg_PREENREGISTREMENT_ID);
                if (OTPreenregistrement != null) {
                    str_ref = OTPreenregistrement.getLgPREENREGISTREMENTID();
                }
                ObllBase.setMessage(ODepotManager.getMessage());
                ObllBase.setDetailmessage(ODepotManager.getDetailmessage());

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
        }

    }

    int_total_percent = 0;
    String result;

    if (ObllBase.getMessage()
            .equals(commonparameter.PROCESS_SUCCESS)) {//int_total_vente
        result = "{ref:\"" + str_ref + "\",str_ref_id:\"" + str_ref_id + "\",dbl_net_apayer:\"" + dbl_net_apayer + "\",int_nb_limite:\"" + int_nb_limite + "\",int_famille_stock_available:\"" + int_famille_stock_available + "\",int_total_vente:\"" + int_total_vente + "\",int_famille_stock_reel:\"" + int_famille_stock_reel + "\",dbl_total_remise:\"" + dbl_total_remise + "\",amount_vente_first_total:\"" + amount_vente_first_total + "\", int_total_product: \"" + int_total_product + "\", int_cust_part: \"" + int_cust_part + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {

        result = "{ref:\"" + str_ref + "\",str_ref_id:\"" + str_ref_id + "\",dbl_net_apayer:\"" + dbl_net_apayer + "\",answer_decondition:\"" + answer_decondition + "\",int_nb_limite:\"" + int_nb_limite + "\",int_famille_stock_available:\"" + int_famille_stock_available + "\",int_total_vente:\"" + int_total_vente + "\",int_famille_stock_reel:\"" + int_famille_stock_reel + "\",dbl_total_remise:\"" + dbl_total_remise + "\",amount_vente_first_total:\"" + amount_vente_first_total + "\", int_total_product: \"" + int_total_product + "\", int_cust_part: \"" + int_cust_part + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }

    OdataManager = null;
    ObllBase = null;
    OTFamilleStock = null;
    OTPreenregistrement = null;
    ODiffereManagement = null;

    OTPreenregistrement = null;
    OTPreenregistrementDetail = null;
    OfamilleManagement = null;

%>
<%=result%>