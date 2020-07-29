<%@page import="toolkits.utils.Maths"%>
<%@page import="org.json.JSONException"%>

<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TModeReglement"%>
<%@page import="dal.TDevise"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="dal.TTypeVente"%>
<%@page import="dal.TOfficine"%>
<%@page import="dal.TPromotionProduct"%>

<%@page import="dal.TRemise"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.common.Parameter"%>
<%@page import="dal.TCompteClient"%>
<%@page import="dal.TAyantDroit"%>
<%@page import="bll.commandeManagement.suggestionManagement"%>
<%@page import="bll.preenregistrement.DevisManagement"%>
<%@page import="dal.TFamilleStock"%>

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

    TDevise OTDevise = null;
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
    TPreenregistrement OTPreenregistrement = null;
    TFamilleStock OTFamilleStock = null;
    JSONArray arrayObj = new JSONArray();
    JSONObject json = new JSONObject();
    TModeReglement OTModeReglement = null;
%>

<%    int int_FREE_PACK_NUMBER = 0, int_PRICE_DETAIL = 0, int_QUANTITY = 0, int_QUANTITY_SERVED = 0,
            int_TOTAL_VENTE_RECAP = 0, int_AMOUNT_REMIS = 0, int_AMOUNT_RECU = 0, int_TAUX_CHANGE = 0, int_TAUX = 0;

%>

<%    String lg_PREENREGISTREMENT_ID = "", listeCompteclientTierspayant = "", lg_PREENREGISTREMENT_DETAIL_ID = "", str_NOM = "", str_net_paid = "",
            lg_FAMILLE_ID = "", lg_TYPE_REGLEMENT_ID = "", str_MEDECIN = "", lg_NATURE_VENTE_ID = "", str_BANQUE = "",
            str_LIEU = "", str_CODE_MONNAIE = "", lg_TYPE_VENTE_ID = "",
            str_REF_BON = "", lg_REMISE_ID = "", str_FIRST_NAME_FACTURE = "", str_LAST_NAME_FACTURE = "",
            int_NUMBER_FACTURE = "", lg_AYANTS_DROITS_ID = "", str_NUMERO_SECURITE_SOCIAL = "", lg_COMPTE_CLIENT_ID = "",
            my_view_titre = "", KEY_PARAMETER = Parameter.KEY_LAST_ORDER_NUMBER_PREVENTE, lg_USER_VENDEUR_ID = "",
            str_ref = "", lg_COMPTE_CLIENT_TIERS_PAYANT_ID = "";
    String result = "";
    boolean b_WITHOUT_BON = false, answer_decondition = false;
   
    JSONArray backend = new JSONArray();
    Integer partTP = 0;
    if (request.getParameter("backend") != null) {

        try {
            backend = new JSONArray(request.getParameter("backend"));

        } catch (JSONException ex) {

        }

    }
    if (request.getParameter("LstTCompteClientTiersPayant") != null) {
        listeCompteclientTierspayant = request.getParameter("LstTCompteClientTiersPayant");

    }

    if (request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID") != null) {
        lg_COMPTE_CLIENT_TIERS_PAYANT_ID = request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID");

    }

    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");

    }

    if (request.getParameter("str_net_paid") != null) {
        str_net_paid = request.getParameter("str_net_paid");

    }

    if (request.getParameter("int_FREE_PACK_NUMBER") != null
            && (!request.getParameter("int_FREE_PACK_NUMBER").equalsIgnoreCase(""))) {
        int_FREE_PACK_NUMBER = Integer.parseInt(request.getParameter("int_FREE_PACK_NUMBER"));
    }

    if (request.getParameter("my_view_titre") != null) {
        my_view_titre = request.getParameter("my_view_titre");
        new logger().OCategory.info("my_view_titre " + my_view_titre);
    }

    if (request.getParameter("int_TAUX") != null && !"".equals(request.getParameter("int_TAUX"))) {
        int_TAUX = Integer.parseInt(request.getParameter("int_TAUX"));
        new logger().OCategory.info("int_TAUX " + int_TAUX);
    }

    if (request.getParameter("str_FIRST_NAME_FACTURE") != null) {
        str_FIRST_NAME_FACTURE = request.getParameter("str_FIRST_NAME_FACTURE");
        new logger().OCategory.info("str_FIRST_NAME_FACTURE " + str_FIRST_NAME_FACTURE);
    }

    if (request.getParameter("b_WITHOUT_BON") != null) {
        b_WITHOUT_BON = Boolean.parseBoolean(request.getParameter("b_WITHOUT_BON"));
        new logger().OCategory.info("b_WITHOUT_BON " + b_WITHOUT_BON);
    }

    if (request.getParameter("str_LAST_NAME_FACTURE") != null) {
        str_LAST_NAME_FACTURE = request.getParameter("str_LAST_NAME_FACTURE");
        new logger().OCategory.info("str_LAST_NAME_FACTURE " + str_LAST_NAME_FACTURE);
    }

    if (request.getParameter("lg_USER_VENDEUR_ID") != null && !request.getParameter("lg_USER_VENDEUR_ID").equalsIgnoreCase("")) {
        lg_USER_VENDEUR_ID = request.getParameter("lg_USER_VENDEUR_ID");
        new logger().OCategory.info("lg_USER_VENDEUR_ID " + lg_USER_VENDEUR_ID);
    }

    if (request.getParameter("int_NUMBER_FACTURE") != null) {
        int_NUMBER_FACTURE = request.getParameter("int_NUMBER_FACTURE");

    }

    if (request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID") != null) {
        lg_PREENREGISTREMENT_DETAIL_ID = request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID");

    }

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

    }

    if (request.getParameter("int_PRICE_DETAIL") != null) {

        int_PRICE_DETAIL = new Integer(request.getParameter("int_PRICE_DETAIL"));

    }

    if (request.getParameter("int_TOTAL_VENTE_RECAP") != null) {
        int_TOTAL_VENTE_RECAP = new Integer(request.getParameter("int_TOTAL_VENTE_RECAP"));

    }

    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null) {
        new logger().OCategory.info("lg_TYPE_REGLEMENT_ID   " + request.getParameter("lg_TYPE_REGLEMENT_ID"));
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");

    }
    if (request.getParameter("partTP") != null) {
        new logger().OCategory.info("partTP   " + request.getParameter("partTP"));
        partTP = new Integer(request.getParameter("partTP"));

    }
    if (request.getParameter("int_AMOUNT_RECU") != null && !request.getParameter("int_AMOUNT_RECU").equalsIgnoreCase("")) {
        new logger().OCategory.info("int_AMOUNT_RECU   " + request.getParameter("int_AMOUNT_RECU"));
        int_AMOUNT_RECU = new Integer(request.getParameter("int_AMOUNT_RECU"));

    }

    if (request.getParameter("int_AMOUNT_REMIS") != null && !request.getParameter("int_AMOUNT_REMIS").equalsIgnoreCase("")) {

        int_AMOUNT_REMIS = new Integer(request.getParameter("int_AMOUNT_REMIS"));

    }

    // Recupération du medecin et de la nature de la vente
    if (request.getParameter("str_MEDECIN") != null) {

        str_MEDECIN = request.getParameter("str_MEDECIN");

    }

    if (request.getParameter("lg_NATURE_VENTE_ID") != null) {
        new logger().OCategory.info("lg_NATURE_VENTE_ID   " + request.getParameter("lg_NATURE_VENTE_ID"));
        lg_NATURE_VENTE_ID = request.getParameter("lg_NATURE_VENTE_ID");

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
    if (request.getParameter("lg_AYANTS_DROITS_ID") != null && !request.getParameter("lg_AYANTS_DROITS_ID").equals("")) {
        lg_AYANTS_DROITS_ID = request.getParameter("lg_AYANTS_DROITS_ID");
        new logger().OCategory.info("lg_AYANTS_DROITS_ID " + lg_AYANTS_DROITS_ID);

    }

    if (request.getParameter("str_REF_BON") != null) {
        new logger().OCategory.info("str_REF_BON   " + request.getParameter("str_REF_BON"));
        str_REF_BON = request.getParameter("str_REF_BON");

    }
    if (request.getParameter("lg_TYPE_VENTE_ID") != null) {
        new logger().OCategory.info("lg_TYPE_VENTE_ID " + request.getParameter("lg_TYPE_VENTE_ID"));
        lg_TYPE_VENTE_ID = request.getParameter("lg_TYPE_VENTE_ID");

    }

    if (request.getParameter("lg_REMISE_ID") != null) {

        lg_REMISE_ID = request.getParameter("lg_REMISE_ID");

    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    DiffereManagement ODiffereManagement = new DiffereManagement(OdataManager, OTUser);
    DevisManagement ODevisManagement = new DevisManagement(OdataManager, OTUser);
    tellerManagement OtellerManagement = new tellerManagement(OdataManager, OTUser);

    str_ref = lg_PREENREGISTREMENT_ID;
    JSONObject value = new JSONObject();

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("create")) {

            OTFamilleStock = OtellerManagement.getTProductItemStock(lg_FAMILLE_ID, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            if (OTFamilleStock.getLgFAMILLEID().getBoolDECONDITIONNE() == 1) {
                if (!OPreenregistrement.checkIsVentePossible(OTFamilleStock, int_QUANTITY_SERVED)) {
                    answer_decondition = true;
                    json.put("answer_decondition", answer_decondition);
                    arrayObj.put(json);
                    result = "{results:" + arrayObj.toString() + ", errors_code: \"" + OPreenregistrement.getMessage() + "\", errors: \"" + OPreenregistrement.getDetailmessage() + "\"}";
                    out.println(result);

                    return;
                }
            }

            if (lg_PREENREGISTREMENT_ID.equals("0")) {
                if (my_view_titre.equalsIgnoreCase("by_devis")) {
                    KEY_PARAMETER = "";
                }
                OTPreenregistrement = OPreenregistrement.createPreVente2(str_MEDECIN, lg_TYPE_VENTE_ID, lg_NATURE_VENTE_ID, lg_REMISE_ID, KEY_PARAMETER, lg_USER_VENDEUR_ID, str_FIRST_NAME_FACTURE, str_LAST_NAME_FACTURE, OTFamilleStock, int_QUANTITY, int_QUANTITY_SERVED, int_FREE_PACK_NUMBER, listeCompteclientTierspayant);

                json.put("lg_PREENREGISTREMENT_ID", OTPreenregistrement.getLgPREENREGISTREMENTID());
                json.put("str_REF", OTPreenregistrement.getStrREF());
                json.put("int_PRICE", OTPreenregistrement.getIntPRICE());
                arrayObj.put(json);
            } else {
               
                OTPreenregistrement = OPreenregistrement.addORupdatePreenregistrementDetail(lg_PREENREGISTREMENT_ID, OTFamilleStock, int_QUANTITY, int_QUANTITY_SERVED, int_FREE_PACK_NUMBER);

                json.put("lg_PREENREGISTREMENT_ID", OTPreenregistrement.getLgPREENREGISTREMENTID());
                json.put("str_REF", OTPreenregistrement.getStrREF());
                json.put("int_PRICE", OTPreenregistrement.getIntPRICE());
                arrayObj.put(json);

            }

        } else if (request.getParameter("mode").equals("update")) {
            OTFamilleStock = OtellerManagement.getTProductItemStock(lg_FAMILLE_ID, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            if (OTFamilleStock.getLgFAMILLEID().getBoolDECONDITIONNE() == 1) {
                if (!OPreenregistrement.checkIsVentePossible(OTFamilleStock, int_QUANTITY_SERVED)) {
                    answer_decondition = true;
                    json.put("answer_decondition", answer_decondition);
                    arrayObj.put(json);
                    result = "{results:" + arrayObj.toString() + ", errors_code: \"" + OPreenregistrement.getMessage() + "\", errors: \"" + OPreenregistrement.getDetailmessage() + "\"}";

                    out.println(result);

                    return;
                }
            }
            OTPreenregistrement = OPreenregistrement.updateTPreenregistrementDetail(lg_PREENREGISTREMENT_DETAIL_ID, int_PRICE_DETAIL, int_QUANTITY, int_QUANTITY_SERVED);
            if (OTPreenregistrement != null) {
                json.put("int_PRICE", OTPreenregistrement.getIntPRICE());
                arrayObj.put(json);
            }
        } else if (request.getParameter("mode").equals("delete")) {
            OTPreenregistrement = OPreenregistrement.removePreenregistrementDetail(lg_PREENREGISTREMENT_DETAIL_ID);
            if (OTPreenregistrement != null) {
                json.put("int_PRICE", OTPreenregistrement.getIntPRICE());
                arrayObj.put(json);
            }
        } else if (request.getParameter("mode").equals("cloturer")) {
            OdataManager.setTransactionGroupe(true);
           
            TCompteClient OTCompteClient = null;
            TTypeVente OTTypeVente = OPreenregistrement.getTypeVente(lg_TYPE_VENTE_ID);
            if (OTTypeVente != null) {
                if (OTTypeVente.getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
                    //  TCompteClient OTCompteClient = OdataManager.getEm().find(TCompteClient.class, lg_COMPTE_CLIENT_ID);

                    try {
                        OTCompteClient = OdataManager.getEm().getReference(TCompteClient.class, lg_COMPTE_CLIENT_ID);
                    } catch (Exception e) {
                        OTCompteClient = OdataManager.getEm().find(TCompteClient.class, lg_COMPTE_CLIENT_ID);

                    }

                    if (OTCompteClient != null) {
                        str_FIRST_NAME_FACTURE = OTCompteClient.getLgCLIENTID().getStrFIRSTNAME();
                        str_LAST_NAME_FACTURE = OTCompteClient.getLgCLIENTID().getStrLASTNAME();
                        str_NUMERO_SECURITE_SOCIAL = OTCompteClient.getLgCLIENTID().getStrNUMEROSECURITESOCIAL();
                    }
                } else if (OTTypeVente.getLgTYPEVENTEID().equals(Parameter.VENTE_ASSURANCE)) {
                    TAyantDroit OTAyantDroit = OdataManager.getEm().find(TAyantDroit.class, lg_AYANTS_DROITS_ID);
                    if (OTAyantDroit != null) {
                        str_FIRST_NAME_FACTURE = OTAyantDroit.getStrFIRSTNAME();
                        str_LAST_NAME_FACTURE = OTAyantDroit.getStrLASTNAME();
                        str_NUMERO_SECURITE_SOCIAL = OTAyantDroit.getStrNUMEROSECURITESOCIAL();
                    }
                }
                OTDevise = ODevisManagement.getTDevise(str_CODE_MONNAIE);
                if (OTDevise != null) {
                    str_CODE_MONNAIE = OTDevise.getStrNAME();
                    int_TAUX_CHANGE = OTDevise.getIntTAUX().intValue();
                }
                OTModeReglement = OPreenregistrement.getTModeReglementByTypeReglement("", lg_TYPE_REGLEMENT_ID);
                lg_TYPE_REGLEMENT_ID = ("Especes".equals(lg_TYPE_REGLEMENT_ID) ? "1" : lg_TYPE_REGLEMENT_ID);
               
                if (OTTypeVente.getLgTYPEVENTEID().equals(Parameter.VENTE_COMPTANT)) {
                    if ("4".equals(lg_TYPE_REGLEMENT_ID)) {

                        try {
                            OTCompteClient = OdataManager.getEm().getReference(TCompteClient.class, lg_COMPTE_CLIENT_ID);
                        } catch (Exception e) {
                            OTCompteClient = OdataManager.getEm().find(TCompteClient.class, lg_COMPTE_CLIENT_ID);

                        }

                    }
                     OPreenregistrement.CloturerVente(lg_PREENREGISTREMENT_ID, lg_TYPE_REGLEMENT_ID, OTTypeVente, int_AMOUNT_RECU, int_AMOUNT_REMIS, OTCompteClient, str_FIRST_NAME_FACTURE, str_LAST_NAME_FACTURE, int_NUMBER_FACTURE, lg_USER_VENDEUR_ID, str_BANQUE, str_LIEU, str_CODE_MONNAIE, OTModeReglement, int_TAUX_CHANGE, int_TOTAL_VENTE_RECAP, str_NOM);
                   
                } else {

                    JSONObject object = OPreenregistrement.cloturerVente(lg_PREENREGISTREMENT_ID, lg_TYPE_REGLEMENT_ID, OTTypeVente, int_AMOUNT_RECU, int_AMOUNT_REMIS, lg_COMPTE_CLIENT_ID, str_FIRST_NAME_FACTURE, str_LAST_NAME_FACTURE, int_NUMBER_FACTURE, str_NUMERO_SECURITE_SOCIAL, lg_USER_VENDEUR_ID, b_WITHOUT_BON, backend, int_TAUX, str_BANQUE, str_LIEU, str_CODE_MONNAIE, OTModeReglement, int_TAUX_CHANGE, int_TOTAL_VENTE_RECAP, str_NOM, partTP);
                        OPreenregistrement.setMessage(object.get("statut") + "");
                    OPreenregistrement.setDetailmessage(object.getString("message"));
                }

                json.put("lg_PREENREGISTREMENT_ID", lg_PREENREGISTREMENT_ID);
                arrayObj.put(json);

            }
           
        } else if (request.getParameter("mode").equals("remise")) {
            OPreenregistrement.updateRemiseByVente(lg_PREENREGISTREMENT_ID, lg_REMISE_ID);
        } else if (request.getParameter("mode").equals("annulervente")) {
            try {
                OTPreenregistrement = OPreenregistrement.AnnulerVente(lg_PREENREGISTREMENT_ID);

                if (OTPreenregistrement != null) {
                    ODiffereManagement.updateSnapshotVenteSociete(OTPreenregistrement.getLgPREENREGISTREMENTID(), lg_COMPTE_CLIENT_ID);
                    json.put("lg_PREENREGISTREMENT_ID", OTPreenregistrement.getLgPREENREGISTREMENTID());
                    arrayObj.put(json);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (request.getParameter("mode").equals("updatetypevente")) {
            OPreenregistrement.updateTypeventeInVente(lg_PREENREGISTREMENT_ID, lg_TYPE_VENTE_ID);
        } else if (request.getParameter("mode").equals("updateayantdroit")) {
            OPreenregistrement.updateayantdroit(lg_PREENREGISTREMENT_ID, lg_AYANTS_DROITS_ID);
        } else if (request.getParameter("mode").equals("clotureravoir")) {
            OPreenregistrement.clotureravoir(lg_PREENREGISTREMENT_ID);
        } else if (request.getParameter("mode").equals("updatebon")) {
            OPreenregistrement.updateVente(lg_PREENREGISTREMENT_ID, str_REF_BON);
        } else if (request.getParameter("mode").equals("closurebon")) {
            OPreenregistrement.closeventeBon(lg_PREENREGISTREMENT_ID);
        } else if (request.getParameter("mode").equals("addtierspayant")) {
           OPreenregistrement.addCompteClient(lg_PREENREGISTREMENT_ID, lg_COMPTE_CLIENT_TIERS_PAYANT_ID, int_TAUX);
            OPreenregistrement.setMessage(ODiffereManagement.getMessage());
            OPreenregistrement.setDetailmessage(ODiffereManagement.getDetailmessage());
        } else if (request.getParameter("mode").equals("removetierspayant")) {
            OPreenregistrement.removeTiersPayant(lg_PREENREGISTREMENT_ID, lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
        } else if (request.getParameter("mode").equals("shownetpay")) {

            JSONArray tierspayants = null;
            if (request.getParameter("tierspayants") != null) {
                tierspayants = new JSONArray(request.getParameter("tierspayants"));
            }

            Map<TPreenregistrement, JSONObject> map = OPreenregistrement.getNetPaid(lg_PREENREGISTREMENT_ID, tierspayants);

            String msg = "";
            int success = 0;
            int intPARTTIERSPAYANT = 0;
            JSONObject netData = new JSONObject();
            for (Map.Entry<TPreenregistrement, JSONObject> entry : map.entrySet()) {
                OTPreenregistrement = entry.getKey();
                value = entry.getValue();
                msg = value.optString("message");
                success = value.optInt("success");
                intPARTTIERSPAYANT = value.optInt("partTP");
                netData.putOnce("data", value.getJSONArray("datatierspayant"));

            }

            json.put("b_IS_AVOIR", OTPreenregistrement.getBISAVOIR());
            json.put("int_REMISE", OTPreenregistrement.getIntPRICEREMISE());
            json.put("int_NET", OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_COMPTANT) ? Maths.arrondiModuloOfNumber((OTPreenregistrement.getIntPRICE() - OTPreenregistrement.getIntPRICEREMISE()), 5) : ((OTPreenregistrement.getIntCUSTPART() - OTPreenregistrement.getIntPRICEREMISE() >= 0) ? Maths.arrondiModuloOfNumber((OTPreenregistrement.getIntCUSTPART() - OTPreenregistrement.getIntPRICEREMISE()), 5) : 0));
            // - OTPreenregistrement.getIntPRICEREMISE()
            //  int net = (OTPreenregistrement.getIntPRICE() - OTPreenregistrement.getIntCUSTPART());
            json.put("int_PART_TIERSPAYANT", intPARTTIERSPAYANT);
            //json.put("int_PART_TIERSPAYANT", (OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equalsIgnoreCase(Parameter.VENTE_COMPTANT) ? 0 : (net > 0) ? net : 0));
            JSONObject netJson = new JSONObject();
            arrayObj.put(json);
            netJson.put("viewdata", json);
            netJson.put("backend", netData);
            netJson.put("intPARTTIERSPAYANT", intPARTTIERSPAYANT);

            netJson.put("message", msg);
            netJson.put("success", success);
            out.println(netJson);

            return;

        } else if (request.getParameter("mode").equals("reinitializeDisplay")) {
            TOfficine OTOfficine = OdataManager.getEm().find(TOfficine.class, "1");
            OPreenregistrement.reinitializeDisplay(OTOfficine.getStrNOMABREGE(), "  BIENVENUE A VOUS");
        } else if (request.getParameter("mode").equals("devis")) {
            OPreenregistrement.DoDevis(lg_PREENREGISTREMENT_ID, lg_TYPE_VENTE_ID, lg_COMPTE_CLIENT_ID);
        }

    }
    result = "{results:" + arrayObj.toString() + ", message:" + value.toString() + "  , errors_code: \"" + OPreenregistrement.getMessage() + "\", errors: \"" + OPreenregistrement.getDetailmessage() + "\"}";
%>
<%=result%>