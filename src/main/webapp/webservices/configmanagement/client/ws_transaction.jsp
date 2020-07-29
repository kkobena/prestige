<%@page import="bll.configManagement.ayantDroitManagement"%>
<%@page import="dal.TAyantDroit"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="bll.differe.DiffereManagement"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="bll.teller.clientManager"%>
<%@page import="dal.jconnexion"%>
<%@page import="dal.TCompteClient"%>
<%@page import="bll.configManagement.compteClientManagement"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TClient"%>
<%@page import="dal.TOptimisationQuantite"%>
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

    TCompteClient OTCompteClient = null;
    JSONArray arrayObj = new JSONArray();
    JSONObject json = new JSONObject();

%>
<%    String lg_CLIENT_ID = "", str_CODE_INTERNE = "", str_FIRST_NAME = "",
            str_LAST_NAME = "", str_NUMERO_SECURITE_SOCIAL = "",
            str_SEXE = "", str_ADRESSE = "", str_DOMICILE = "", str_AUTRE_ADRESSE = "",
            str_CODE_POSTAL = "", str_COMMENTAIRE = "",
            lg_VILLE_ID = "", lg_MEDECIN_ID = "",
            lg_TYPE_CLIENT_ID = "", lg_COMPTE_CLIENT_ID = "", lg_TIERS_PAYANT_ID = "", bool_REGIME_add = "", lg_TYPE_TIERS_PAYANT_ID = "";

    String lg_CATEGORIE_AYANTDROIT_ID = "555146116095894790", lg_RISQUE_ID = "55181642844215217016", lg_AYANTS_DROITS_ID = "", lg_COMPTE_CLIENT_TIERS_PAYANT_ID = "", str_NAME = "", remiseId = null;
    String lg_COMPANY_ID = "";
    double dbl_QUOTA_CONSO_MENSUELLE = 0.0, dbl_CAUTION = 0.0, dbl_QUOTA_CONSO_VENTE = 0.0, dbl_PLAFOND = 0;
    int int_POURCENTAGE = 0, int_PRIORITY = 1, dbl_SOLDE = 0, db_PLAFOND_ENCOURS = 0;
    TAyantDroit OTAyantDroit = null;
    TCompteClientTiersPayant OTCompteClientTiersPayant = null;
    boolean b_IsAbsolute = false;
    Date dt_NAISSANCE = null;
    double dbl_total_differe = 0.0;
    int issolvable = 0;

    if (request.getParameter("int_POURCENTAGE") != null) {
        int_POURCENTAGE = Integer.parseInt(request.getParameter("int_POURCENTAGE"));
        new logger().OCategory.info("int_POURCENTAGE " + int_POURCENTAGE);
    }
    if (request.getParameter("dbl_PLAFOND") != null) {
        dbl_PLAFOND = Integer.parseInt(request.getParameter("dbl_PLAFOND"));

    }

    if (request.getParameter("int_PRIORITY") != null && !request.getParameter("int_PRIORITY").equalsIgnoreCase("")) {
        int_PRIORITY = Integer.parseInt(request.getParameter("int_PRIORITY"));
        new logger().OCategory.info("int_PRIORITY " + int_PRIORITY);
    }

    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID);
    }

    if (request.getParameter("lg_COMPANY_ID") != null) {
        lg_COMPANY_ID = request.getParameter("lg_COMPANY_ID");
        new logger().OCategory.info("lg_COMPANY_ID " + lg_COMPANY_ID);
    }
    if (request.getParameter("remiseId") != null && !"".equals(request.getParameter("remiseId"))) {
        remiseId = request.getParameter("remiseId");
    }

    if (request.getParameter("lg_CLIENT_ID") != null) {
        lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
        new logger().OCategory.info("lg_CLIENT_ID " + lg_CLIENT_ID);
    }
    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
    }
    if (request.getParameter("str_CODE_INTERNE") != null) {
        str_CODE_INTERNE = request.getParameter("str_CODE_INTERNE");
        new logger().OCategory.info("str_CODE_INTERNE " + str_CODE_INTERNE);
    }
    if (request.getParameter("str_FIRST_NAME") != null) {
        str_FIRST_NAME = request.getParameter("str_FIRST_NAME");
        new logger().OCategory.info("str_FIRST_NAME " + str_FIRST_NAME);
    }
    if (request.getParameter("lg_TYPE_CLIENT_ID") != null) {
        lg_TYPE_CLIENT_ID = request.getParameter("lg_TYPE_CLIENT_ID");
        new logger().OCategory.info("lg_TYPE_CLIENT_ID " + lg_TYPE_CLIENT_ID);
    }
    if (request.getParameter("str_LAST_NAME") != null) {
        str_LAST_NAME = request.getParameter("str_LAST_NAME");
        new logger().OCategory.info("str_LAST_NAME " + str_LAST_NAME);
    }
    if (request.getParameter("str_NUMERO_SECURITE_SOCIAL") != null) {
        str_NUMERO_SECURITE_SOCIAL = request.getParameter("str_NUMERO_SECURITE_SOCIAL");
        new logger().OCategory.info("str_NUMERO_SECURITE_SOCIAL " + str_NUMERO_SECURITE_SOCIAL);
    }
    if (request.getParameter("dt_NAISSANCE") != null) {
        dt_NAISSANCE = key.stringToDate(request.getParameter("dt_NAISSANCE"), key.formatterMysqlShort);
        new logger().OCategory.info("dt_NAISSANCE " + dt_NAISSANCE);
    }

    if (request.getParameter("str_SEXE") != null) {
        str_SEXE = request.getParameter("str_SEXE");
        new logger().OCategory.info("str_SEXE " + str_SEXE);
    }
    if (request.getParameter("bool_REGIME_add") != null) {
        bool_REGIME_add = request.getParameter("bool_REGIME_add");
        new logger().OCategory.info("*** bool_REGIME_add *** " + bool_REGIME_add);
    }

    if (request.getParameter("str_ADRESSE") != null) {
        str_ADRESSE = request.getParameter("str_ADRESSE");
        new logger().OCategory.info("str_ADRESSE " + str_ADRESSE);
    }
    if (request.getParameter("str_DOMICILE") != null) {
        str_DOMICILE = request.getParameter("str_DOMICILE");
    }
    if (request.getParameter("str_AUTRE_ADRESSE") != null) {
        str_AUTRE_ADRESSE = request.getParameter("str_AUTRE_ADRESSE");
    }
    if (request.getParameter("str_CODE_POSTAL") != null) {
        str_CODE_POSTAL = request.getParameter("str_CODE_POSTAL");
        new logger().OCategory.info("str_CODE_POSTAL " + str_CODE_POSTAL);
    }
    if (request.getParameter("str_COMMENTAIRE") != null) {
        str_COMMENTAIRE = request.getParameter("str_COMMENTAIRE");
    }
    if (request.getParameter("lg_VILLE_ID") != null) {
        lg_VILLE_ID = request.getParameter("lg_VILLE_ID");
        new logger().OCategory.info("lg_VILLE_ID " + lg_VILLE_ID);
    }
    /*if (request.getParameter("lg_MEDECIN_ID") != null) {
     lg_MEDECIN_ID = request.getParameter("lg_MEDECIN_ID");
     }*/
    if (request.getParameter("lg_CATEGORIE_AYANTDROIT_ID") != null && !"".equals(request.getParameter("lg_CATEGORIE_AYANTDROIT_ID"))) {
        lg_CATEGORIE_AYANTDROIT_ID = request.getParameter("lg_CATEGORIE_AYANTDROIT_ID");
        new logger().OCategory.info("lg_CATEGORIE_AYANTDROIT_ID " + lg_CATEGORIE_AYANTDROIT_ID);
    }

    if (request.getParameter("lg_RISQUE_ID") != null && !"".equals(request.getParameter("lg_RISQUE_ID"))) {
        lg_RISQUE_ID = request.getParameter("lg_RISQUE_ID");
        new logger().OCategory.info("lg_RISQUE_ID " + lg_RISQUE_ID);
    }
    // lg_AYANTS_DROITS_ID
    if (request.getParameter("lg_AYANTS_DROITS_ID") != null) {
        lg_AYANTS_DROITS_ID = request.getParameter("lg_AYANTS_DROITS_ID");
        new logger().OCategory.info("lg_AYANTS_DROITS_ID " + lg_AYANTS_DROITS_ID);
    }
    if (request.getParameter("dbl_QUOTA_CONSO_MENSUELLE") != null) {
        dbl_QUOTA_CONSO_MENSUELLE = new Double(request.getParameter("dbl_QUOTA_CONSO_MENSUELLE"));
        new logger().OCategory.info("dbl_QUOTA_CONSO_MENSUELLE " + dbl_QUOTA_CONSO_MENSUELLE);
    }
    if (request.getParameter("dbl_QUOTA_CONSO_VENTE") != null) {
        dbl_QUOTA_CONSO_VENTE = new Double(request.getParameter("dbl_QUOTA_CONSO_VENTE"));
        new logger().OCategory.info("dbl_QUOTA_CONSO_VENTE " + dbl_QUOTA_CONSO_VENTE);
    }

    if (request.getParameter("dbl_CAUTION") != null) {
        new logger().OCategory.info("dbl_CAUTION avant " + request.getParameter("dbl_CAUTION"));
        dbl_CAUTION = new Double(request.getParameter("dbl_CAUTION"));

    }
    if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null) {
        new logger().OCategory.info("lg_TYPE_TIERS_PAYANT_ID avant " + request.getParameter("lg_TYPE_TIERS_PAYANT_ID"));
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID");

    }
    if (request.getParameter("db_PLAFOND_ENCOURS") != null) {

        db_PLAFOND_ENCOURS = Integer.valueOf(request.getParameter("db_PLAFOND_ENCOURS"));

    }

    /* if (request.getParameter("dbl_SOLDE") != null) {
     dbl_SOLDE = new Double(request.getParameter("dbl_SOLDE"));
     new logger().OCategory.info("dbl_SOLDE " + dbl_SOLDE);
     }*/
    if (request.getParameter("dbl_SOLDE") != null) {
        dbl_SOLDE = Integer.parseInt(request.getParameter("dbl_SOLDE"));
        new logger().OCategory.info("dbl_SOLDE " + dbl_SOLDE);
    }
    if (request.getParameter("b_IsAbsolute") != null) {
        b_IsAbsolute = Boolean.valueOf(request.getParameter("b_IsAbsolute"));

    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();

    clientManagement OclientManagement = new clientManagement(OdataManager, OTUser);

    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager, OTUser);
    ayantDroitManagement OayantDroitManagement = new ayantDroitManagement(OdataManager);
    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_CLIENT_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("create")) {
            if (lg_TYPE_TIERS_PAYANT_ID.equals("1")) {
                lg_TYPE_CLIENT_ID = lg_TYPE_TIERS_PAYANT_ID;
            } else if (lg_TYPE_TIERS_PAYANT_ID.equals("2")) {
                lg_TYPE_CLIENT_ID = lg_TYPE_TIERS_PAYANT_ID;
            }

            OTCompteClient = OclientManagement.createClient(str_FIRST_NAME, str_LAST_NAME, str_NUMERO_SECURITE_SOCIAL, dt_NAISSANCE, str_SEXE, str_ADRESSE, str_DOMICILE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_COMMENTAIRE, lg_VILLE_ID, dbl_QUOTA_CONSO_MENSUELLE, dbl_CAUTION, dbl_SOLDE, lg_TYPE_CLIENT_ID, lg_CATEGORIE_AYANTDROIT_ID, lg_RISQUE_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE, int_PRIORITY, str_CODE_INTERNE, dbl_PLAFOND, lg_COMPANY_ID, db_PLAFOND_ENCOURS, b_IsAbsolute,remiseId); 

            if (OTCompteClient != null) {
                OclientManagement.setDetailmessage("Opération effectuée avec succèes");
                OclientManagement.setMessage(commonparameter.PROCESS_SUCCESS);
            }
            ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
            ObllBase.setMessage(OclientManagement.getMessage());
        } else if (request.getParameter("mode").equals("update")) {
            //   clientManagement OclientManagement = new clientManagement(OdataManager);
            //OclientManagement.update(lg_CLIENT_ID, str_CODE_INTERNE, str_FIRST_NAME, str_LAST_NAME, str_NUMERO_SECURITE_SOCIAL, dt_NAISSANCE, str_SEXE, str_ADRESSE, str_DOMICILE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_COMMENTAIRE, lg_VILLE_ID, lg_MEDECIN_ID, dbl_QUOTA_CONSO_MENSUELLE, dbl_CAUTION, dbl_SOLDE, lg_TYPE_CLIENT_ID, lg_AYANTS_DROITS_ID, lg_CATEGORIE_AYANTDROIT_ID, lg_RISQUE_ID);
            OTCompteClient = OclientManagement.update2(lg_CLIENT_ID, str_CODE_INTERNE, str_FIRST_NAME, str_LAST_NAME, str_NUMERO_SECURITE_SOCIAL, dt_NAISSANCE, str_SEXE, str_ADRESSE, str_DOMICILE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_COMMENTAIRE, lg_VILLE_ID, lg_MEDECIN_ID, dbl_QUOTA_CONSO_MENSUELLE, dbl_CAUTION, lg_TYPE_CLIENT_ID, lg_AYANTS_DROITS_ID, lg_CATEGORIE_AYANTDROIT_ID, lg_RISQUE_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE, int_PRIORITY, dbl_QUOTA_CONSO_VENTE, lg_COMPANY_ID, (int) dbl_PLAFOND, db_PLAFOND_ENCOURS, b_IsAbsolute,remiseId);
            ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
            ObllBase.setMessage(OclientManagement.getMessage());

        } else if (request.getParameter("mode").equals("createcarnet")) {
            //  OclientManagement.create(str_CODE_INTERNE, str_FIRST_NAME, str_LAST_NAME, str_NUMERO_SECURITE_SOCIAL, dt_NAISSANCE, str_SEXE, str_ADRESSE, str_DOMICILE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_COMMENTAIRE, lg_VILLE_ID, dbl_QUOTA_CONSO_MENSUELLE, dbl_CAUTION, dbl_SOLDE);

        } else if (request.getParameter("mode").equals("updatecarnet")) {
            // OclientManagement.update(lg_CLIENT_ID, str_CODE_INTERNE, str_FIRST_NAME, str_LAST_NAME, str_NUMERO_SECURITE_SOCIAL, dt_NAISSANCE, str_SEXE, str_ADRESSE, str_DOMICILE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_COMMENTAIRE, lg_VILLE_ID, dbl_QUOTA_CONSO_MENSUELLE, dbl_CAUTION, dbl_SOLDE);
        } else if (request.getParameter("mode").equals("delete")) {
            //     clientManagement OclientManagement = new clientManagement(OdataManager);
            OTCompteClient = OclientManagement.delete(lg_CLIENT_ID);

            new logger().oCategory.info("Suppression du client " + request.getParameter("lg_CLIENT_ID").toString());
            ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
            ObllBase.setMessage(OclientManagement.getMessage());
        } else if (request.getParameter("mode").toString().equals("updateInfoCompte")) {
            new logger().OCategory.info("lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);
            clientManager OclientManager = new clientManager(OdataManager, OTUser);

            try {
                OTCompteClient = ObllBase.getOdataManager().getEm().find(TCompteClient.class, lg_COMPTE_CLIENT_ID);
                Double d = new Double(dbl_SOLDE);
                int int_amount = d.intValue();
                new logger().OCategory.info("int_amount " + int_amount);
                //OCompteClientManagement.update(lg_COMPTE_CLIENT_ID, "", dbl_QUOTA_CONSO_MENSUELLE, dbl_CAUTION, dbl_SOLDE);
                OclientManager.crediterCompteClient(Ojconnexion, OTCompteClient, int_amount); //mise a jour du compte d'un client
                ObllBase.setDetailmessage("Opération effectuée avec succèes");
                ObllBase.setMessage(commonparameter.PROCESS_SUCCESS);

            } catch (Exception e) {
                ObllBase.buildErrorTraceMessage("Echec de mise a jour du compte");
            }

        } else if (request.getParameter("mode").toString().equals("createother")) {
            OclientManagement.createClient(str_FIRST_NAME, str_LAST_NAME, str_SEXE, str_ADRESSE, str_DOMICILE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_COMMENTAIRE, lg_VILLE_ID, dbl_QUOTA_CONSO_MENSUELLE, dbl_CAUTION, dbl_SOLDE, lg_TYPE_CLIENT_ID);
            ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
            ObllBase.setMessage(OclientManagement.getMessage());
        } else if (request.getParameter("mode").toString().equals("updateother")) {
            OclientManagement.updateClient(lg_CLIENT_ID, str_FIRST_NAME, str_LAST_NAME, str_SEXE, str_ADRESSE, str_DOMICILE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_COMMENTAIRE, lg_VILLE_ID, lg_TYPE_CLIENT_ID, str_CODE_INTERNE);
            ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
            ObllBase.setMessage(OclientManagement.getMessage());
        } else if (request.getParameter("mode").toString().equals("disable")) {
            OclientManagement.enableOrDisableClient(lg_COMPTE_CLIENT_ID, commonparameter.statut_disable);
            ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
            ObllBase.setMessage(OclientManagement.getMessage());
        } else if (request.getParameter("mode").toString().equals("enable")) {
            OclientManagement.enableOrDisableClient(lg_COMPTE_CLIENT_ID, commonparameter.statut_enable);
            ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
            ObllBase.setMessage(OclientManagement.getMessage());
        } else {
        }

    }

    if (OTCompteClient != null) {
        OTAyantDroit = OayantDroitManagement.getAyantDroitByNameClient(OTCompteClient.getLgCLIENTID().getStrFIRSTNAME(), OTCompteClient.getLgCLIENTID().getStrLASTNAME());
        OTCompteClientTiersPayant = OtierspayantManagement.getClientTiersPayant(OTCompteClient.getLgCOMPTECLIENTID(), lg_TIERS_PAYANT_ID);

        json.put("str_suc_social", OTCompteClient.getLgCLIENTID().getStrNUMEROSECURITESOCIAL());

        //code ajouté
        if (OTCompteClientTiersPayant != null) {
            json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_ID", OTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID());
            json.put("str_NAME", OTCompteClientTiersPayant.getLgTIERSPAYANTID().getStrNAME());
            json.put("int_POURCENTAGE", OTCompteClientTiersPayant.getIntPOURCENTAGE());
            json.put("int_PRIORITY", OTCompteClientTiersPayant.getIntPRIORITY());
            json.put("dbl_QUOTA_CONSO_MENSUELLE", OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOMENSUELLE() : 0);
            json.put("dbl_QUOTA_CONSO_VENTE", OTCompteClientTiersPayant.getDblQUOTACONSOVENTE() != null ? OTCompteClientTiersPayant.getDblQUOTACONSOVENTE() : 0);
            json.put("dbl_PLAFOND", OTCompteClientTiersPayant.getDblPLAFOND() != null ? OTCompteClientTiersPayant.getDblPLAFOND() : 0);
            json.put("dbl_PLAFOND_QUOTA_DIFFERENCE", (OTCompteClient.getDblPLAFOND() != null ? OTCompteClient.getDblPLAFOND() : 0) - (OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0));
            json.put("IDTIERSPAYANT", OTCompteClientTiersPayant.getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        }
        if (OTAyantDroit != null) {
            json.put("str_last_name", OTAyantDroit.getStrLASTNAME());
            json.put("str_first_name", OTAyantDroit.getStrFIRSTNAME());
            json.put("lg_AYANTS_DROITS_ID", OTAyantDroit.getLgAYANTSDROITSID());
        } else {
            json.put("str_last_name", OTCompteClient.getLgCLIENTID().getStrLASTNAME());
            json.put("str_first_name", OTCompteClient.getLgCLIENTID().getStrFIRSTNAME());
        }
        //fin code ajouté

        json.put("str_cust_id", OTCompteClient.getLgCLIENTID().getLgCLIENTID());
        json.put("str_cust_compte_id", OTCompteClient.getLgCOMPTECLIENTID());
        json.put("str_cust_compte_solde", OTCompteClient.getDecBalance());

    }

    arrayObj.put(json);

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "({\"success\":" + ObllBase.getMessage() + ",\"errors\":\"" + ObllBase.getDetailmessage() + "\",\"total_differe\":" + dbl_total_differe + ",\"isCustSolvable\":" + issolvable + ",\"results\":" + arrayObj.toString() + "})";

    } else {
        result = "({\"success\":" + ObllBase.getMessage() + ",\"errors\":\"" + ObllBase.getDetailmessage() + "\",\"total_differe\":" + dbl_total_differe + ",\"isCustSolvable\":" + issolvable + ",\"results\":" + arrayObj.toString() + "})";
    }


%>
<%=result%>