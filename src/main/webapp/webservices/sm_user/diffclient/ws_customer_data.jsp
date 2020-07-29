<%@page import="bll.configManagement.compteClientManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="bll.configManagement.ayantDroitManagement"%>
<%@page import="dal.TAyantDroit"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="bll.differe.DiffereManagement"%>
<%@page import="dal.dataManager"  %>

<%@page import="dal.TCompteClient"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>          
<%@page import="org.json.JSONArray"  %> 
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>



<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    Date dt_CREATED, dt_UPDATED;
    date key = new date();
    TUser OTUser = null;
    List<TCompteClientTiersPayant> lstTCompteClientTiersPayant = new ArrayList<TCompteClientTiersPayant>();
    List<dal.TClient> lstTClient = new ArrayList<dal.TClient>();
    TCompteClient OTCompteClient = null;


%>


<!-- logic de gestion des page -->
<%    int dbl_total_differe = 0;
    boolean isCustSolvable = false;
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    JSONArray arrayObj = new JSONArray();
    JSONObject json = null;
    TAyantDroit OTAyantDroit = null;


%>
<!-- fin logic de gestion des pages -->

<%    String lg_COMPTE_CLIENT_ID = "%%", search_value = "", str_CODE_INTERNE = "%%", str_FIRST_NAME = "", lg_TYPE_CLIENT_ID = "%%", str_LAST_NAME = "", lg_CLIENT_ID = "%%";

    double dbl_QUOTA_CONSO_MENSUELLE = 0.00, dbl_SOLDE = 0.00, dbl_CAUTION = 0.0;
    int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start").toString());
        new logger().OCategory.info("start  ==== " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit").toString());
        new logger().OCategory.info("limit  ==== " + limit);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        if (request.getParameter("lg_COMPTE_CLIENT_ID").toString().equals("ALL")) {
            lg_COMPTE_CLIENT_ID = "%%";
        } else {
            lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID").toString();
        }

    }

    new logger().OCategory.info("lg_COMPTE_CLIENT_ID   " + lg_COMPTE_CLIENT_ID);

    if (request.getParameter("lg_CLIENT_ID") != null) {
        if (request.getParameter("lg_CLIENT_ID").toString().equals("ALL")) {
            lg_CLIENT_ID = "%%";
        } else {
            lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID").toString();
        }
        new logger().OCategory.info("lg_CLIENT_ID  ==== " + lg_CLIENT_ID);
    }

    if (request.getParameter("str_LAST_NAME") != null) {
        if (request.getParameter("str_LAST_NAME").toString().equals("ALL")) {
            str_LAST_NAME = "%%";
        } else {
            str_LAST_NAME = request.getParameter("str_LAST_NAME").toString();
        }
        new logger().OCategory.info("str_LAST_NAME  ==== " + str_LAST_NAME);
    }

    if (request.getParameter("str_FIRST_NAME") != null) {
        if (request.getParameter("str_FIRST_NAME").toString().equals("ALL")) {
            str_FIRST_NAME = "%%";
        } else {
            str_FIRST_NAME = request.getParameter("str_FIRST_NAME").toString();
        }
        new logger().OCategory.info("str_FIRST_NAME  ==== " + str_FIRST_NAME);
    }

    if (request.getParameter("str_CODE_INTERNE") != null) {
        if (request.getParameter("str_CODE_INTERNE").toString().equals("ALL")) {
            str_CODE_INTERNE = "%%";
        } else {
            str_CODE_INTERNE = request.getParameter("str_CODE_INTERNE").toString();
        }
        new logger().OCategory.info("str_CODE_INTERNE  ==== " + str_CODE_INTERNE);
    }

    if (request.getParameter("lg_TYPE_CLIENT_ID") != null && !request.getParameter("lg_TYPE_CLIENT_ID").equalsIgnoreCase("") && !request.getParameter("lg_TYPE_CLIENT_ID").equalsIgnoreCase("undefined")) {
        lg_TYPE_CLIENT_ID = request.getParameter("lg_TYPE_CLIENT_ID").toString();
        new logger().OCategory.info("lg_TYPE_CLIENT_ID  ==== " + lg_TYPE_CLIENT_ID);
    }
    new logger().OCategory.info("lg_TYPE_CLIENT_ID   " + lg_TYPE_CLIENT_ID);
    OdataManager.initEntityManager();
    DiffereManagement ODiffereManagement = new DiffereManagement(OdataManager, OTUser);
    clientManagement OclientManagement = new clientManagement(OdataManager);
    ayantDroitManagement OayantDroitManagement = new ayantDroitManagement(OdataManager);
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
    compteClientManagement OcompteClientManagement = new compteClientManagement(OdataManager);

    if (lg_TYPE_CLIENT_ID != null && lg_TYPE_CLIENT_ID != "") {
        lstTClient = OclientManagement.showOnorAllClientByType(search_value, lg_TYPE_CLIENT_ID, commonparameter.statut_enable, start, limit);
        total = OclientManagement.showOnorAllClientByTypeCount(search_value, lg_TYPE_CLIENT_ID, commonparameter.statut_enable);
    } else {
        lstTClient = OclientManagement.showOnorAllClient(search_value, start, limit);
        total = OclientManagement.showOnorAllClientCount(search_value);
    }

%>


<%    int virtual_liste_size = 0;
    String lg_CATEGORIE_AYANTDROIT_ID = "", lg_AYANTS_DROITS_ID = "", lg_RISQUE_ID = "", str_FIRST_LAST_NAME = "";

    if (lstTClient == null) {
        virtual_liste_size = 0;
    } else if (lstTClient.isEmpty()) {
        virtual_liste_size = 0;
    } else {
        for (int i = 0; i < (total < limit ? total : limit); i++) {
            JSONObject cmpt = new JSONObject();
            JSONArray cmpta = new JSONArray();
            json = new JSONObject();
            json.put("TYPECLIENT", lstTClient.get(i).getLgTYPECLIENTID().getLgTYPECLIENTID());
            if (lstTClient.get(i).getLgTYPECLIENTID().getLgTYPECLIENTID().equalsIgnoreCase("1")) {

               // OTAyantDroit = OayantDroitManagement.getAyantDroitByNameClient(lstTClient.get(i).getStrFIRSTNAME(), lstTClient.get(i).getStrLASTNAME());
                             OTAyantDroit=     OayantDroitManagement. getPremierAyantDroit(lstTClient.get(i).getLgCLIENTID());
               if (OTAyantDroit != null) {
                    lg_AYANTS_DROITS_ID = OTAyantDroit.getLgAYANTSDROITSID();
                    lg_CATEGORIE_AYANTDROIT_ID = OTAyantDroit.getLgCATEGORIEAYANTDROITID().getLgCATEGORIEAYANTDROITID();
                    lg_RISQUE_ID = (OTAyantDroit.getLgRISQUEID()!=null?OTAyantDroit.getLgRISQUEID().getLgRISQUEID():"");
                    str_FIRST_LAST_NAME = OTAyantDroit.getStrFIRSTNAME() + " " + OTAyantDroit.getStrLASTNAME();
                    str_FIRST_NAME = OTAyantDroit.getStrFIRSTNAME();
                    str_LAST_NAME = OTAyantDroit.getStrLASTNAME();
                }

            } else {
                str_FIRST_LAST_NAME = lstTClient.get(i).getStrFIRSTNAME() + " " + lstTClient.get(i).getStrLASTNAME();
                str_FIRST_NAME = lstTClient.get(i).getStrFIRSTNAME();
                str_LAST_NAME = lstTClient.get(i).getStrLASTNAME();
            }

            OTCompteClient = OclientManagement.getTCompteClientByClient(lstTClient.get(i).getLgCLIENTID());
            if (OTCompteClient != null) {
                if (OTCompteClient.getStrSTATUT().equals(commonparameter.statut_enable)) {
                    dbl_SOLDE = (OTCompteClient.getDecBalance() != null ? OTCompteClient.getDecBalance() : 0);
                    dbl_CAUTION = (OTCompteClient.getDblCAUTION() != null ? OTCompteClient.getDblCAUTION() : 0);
                    dbl_QUOTA_CONSO_MENSUELLE = (OTCompteClient.getDblQUOTACONSOMENSUELLE() != null ? OTCompteClient.getDblQUOTACONSOMENSUELLE() : 0);
                    //   dbl_total_differe = ODiffereManagement.func_beneficiaireTotalDiffere(OTCompteClient.getLgCOMPTECLIENTID()); ancien bon code. a decommenter en cas de probleme
                    dbl_total_differe = OTCompteClient.getDecBalanceInDisponible();

                    /*if (!ODiffereManagement.func_CheckIfCustIsSolvable(OTCompteClient.getLgCOMPTECLIENTID())) {
                     issolvable = 0;
                     } else {
                     issolvable = 1;
                     }*/
                    json.put("lg_COMPTE_CLIENT_ID", OTCompteClient.getLgCOMPTECLIENTID());
                    json.put("dbl_total_differe", dbl_total_differe);
                    lg_COMPTE_CLIENT_ID = OTCompteClient.getLgCOMPTECLIENTID();
                }
            }

            new logger().OCategory.info("dbl_SOLDE " + dbl_SOLDE + " dbl_CAUTION " + dbl_CAUTION + " dbl_QUOTA_CONSO_MENSUELLE " + dbl_QUOTA_CONSO_MENSUELLE);
            json.put("dbl_SOLDE", dbl_SOLDE);
            json.put("dbl_CAUTION", dbl_CAUTION);
            json.put("dbl_QUOTA_CONSO_MENSUELLE", dbl_QUOTA_CONSO_MENSUELLE);
            json.put("lg_COMPTE_CLIENT_ID", lg_COMPTE_CLIENT_ID);
            json.put("lg_CLIENT_ID", lstTClient.get(i).getLgCLIENTID());
            json.put("str_CODE_INTERNE", lstTClient.get(i).getStrCODEINTERNE());
            /*json.put("str_FIRST_NAME", lstTClient.get(i).getStrFIRSTNAME());
             json.put("str_LAST_NAME", lstTClient.get(i).getStrLASTNAME());
             json.put("str_FIRST_LAST_NAME", lstTClient.get(i).getStrFIRSTNAME() + " " + lstTClient.get(i).getStrLASTNAME());*/
            json.put("str_FIRST_NAME", str_FIRST_NAME);
            json.put("str_LAST_NAME", str_LAST_NAME);
            json.put("str_FIRST_LAST_NAME", str_FIRST_LAST_NAME);
            json.put("str_NUMERO_SECURITE_SOCIAL", lstTClient.get(i).getStrNUMEROSECURITESOCIAL());
            json.put("dt_NAISSANCE", key.DateToString(lstTClient.get(i).getDtNAISSANCE(), key.formatterMysql));
            json.put("str_SEXE", lstTClient.get(i).getStrSEXE());
            json.put("str_ADRESSE", lstTClient.get(i).getStrADRESSE());
            json.put("str_DOMICILE", lstTClient.get(i).getStrDOMICILE());
            json.put("str_AUTRE_ADRESSE", lstTClient.get(i).getStrAUTREADRESSE());
            json.put("str_CODE_POSTAL", lstTClient.get(i).getStrCODEPOSTAL());
            json.put("str_COMMENTAIRE", lstTClient.get(i).getStrCOMMENTAIRE());
            if (lstTClient.get(i).getLgVILLEID() != null) {
                json.put("lg_VILLE_ID", lstTClient.get(i).getLgVILLEID().getStrName());
            }

            json.put("lg_RISQUE_ID", lg_RISQUE_ID);
            json.put("lg_AYANTS_DROITS_ID", lg_AYANTS_DROITS_ID);
            // json.put("lg_MEDECIN_ID", lstTClient.get(i).getLgMEDECINID().getStrFIRSTNAME() + lstTClient.get(i).getLgMEDECINID().getStrLASTNAME());
            json.put("lg_CATEGORIE_AYANTDROIT_ID", lg_CATEGORIE_AYANTDROIT_ID);

            json.put("lg_TYPE_CLIENT_ID", lstTClient.get(i).getLgTYPECLIENTID().getStrNAME());

            json.put("str_STATUT", lstTClient.get(i).getStrSTATUT());

            dt_CREATED = lstTClient.get(i).getDtCREATED();
            if (dt_CREATED != null) {
                json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
            }

            dt_UPDATED = lstTClient.get(i).getDtUPDATED();
            if (dt_UPDATED != null) {
                json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
            }

            //  lstTCompteClientTiersPayant = ODiffereManagement.func_GetCustomerTiersPayants(lstTClient.get(i).getLgCLIENTID());
            lstTCompteClientTiersPayant = OtierspayantManagement.getListCompteClientTiersPayants(lstTClient.get(i).getLgCLIENTID());
            // new logger().OCategory.info("lstTCompteClientTiersPayant taille ++++++"+lstTCompteClientTiersPayant.size());
            if (lstTCompteClientTiersPayant.size() == 0) {
                json.put("RO", "");
                json.put("RO_TAUX", "");
                json.put("RC1", "");
                json.put("RC1_TAUX", "");
                json.put("RC2", "");
                json.put("RC2_TAUX", "");
                arrayObj.put(json);
            } else {
                for (int k = 0; k < lstTCompteClientTiersPayant.size(); k++) {
                    JSONObject ro = new JSONObject();
                    if (lstTCompteClientTiersPayant.get(k).getIntPRIORITY() == 1) {
                       json.put("RO", lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getStrNAME());
                        System.out.println("  lstTCompteClientTiersPayant.get(k)  "+lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID()+"  lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID() "+ lstTCompteClientTiersPayant.get(k)+ " ------------------------------------- ");
                        json.put("RO_TAUX", lstTCompteClientTiersPayant.get(k).getIntPOURCENTAGE());
                        json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_RO_ID", lstTCompteClientTiersPayant.get(k).getLgCOMPTECLIENTTIERSPAYANTID());

                        json.put("dbl_PLAFOND_RO_ID", lstTCompteClientTiersPayant.get(k).getDblPLAFOND());
                        json.put("dbl_QUOTA_CONSO_MENSUELLE_RO_ID", lstTCompteClientTiersPayant.get(k).getDblQUOTACONSOMENSUELLE());
                        json.put("dbl_PLAFOND_QUOTA_DIFFERENCE_RO_ID", lstTCompteClientTiersPayant.get(k).getDblPLAFOND() - lstTCompteClientTiersPayant.get(k).getDblQUOTACONSOMENSUELLE());

                        ro.put("NAME", lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getStrNAME());
                        ro.put("TAUX", lstTCompteClientTiersPayant.get(k).getIntPOURCENTAGE());
                        ro.put("bCANBEUSE", lstTCompteClientTiersPayant.get(k).getBCANBEUSE());
                        ro.put("dbCONSOMMATION", lstTCompteClientTiersPayant.get(k).getDbCONSOMMATIONMENSUELLE());
                        ro.put("dbPLAFONDENCOURS", lstTCompteClientTiersPayant.get(k).getDbPLAFONDENCOURS());
                         ro.put("dbPLAFONDVENTE", lstTCompteClientTiersPayant.get(k).getDblPLAFOND());
                         ro.put("ID", lstTCompteClientTiersPayant.get(k).getLgCOMPTECLIENTTIERSPAYANTID()); 
                         ro.put("IDTIERSPAYANT", lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getLgTIERSPAYANTID());
                        ro.put("message", (lstTCompteClientTiersPayant.get(k).getDbPLAFONDENCOURS() > 0 && lstTCompteClientTiersPayant.get(k).getDbCONSOMMATIONMENSUELLE() >= lstTCompteClientTiersPayant.get(k).getDbPLAFONDENCOURS()) ? "Impossible de faire des ventes avec <strong style='color:blue;'>" + lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getStrNAME() + "</strong><br/> votre plafond est atteint: <span style='font-weight:900;color:red;'>" + lstTCompteClientTiersPayant.get(k).getDbPLAFONDENCOURS() + " FCFA </span><br/> Votre consommation est: <span style='font-weight:900;color:red;'>" + lstTCompteClientTiersPayant.get(k).getDbCONSOMMATIONMENSUELLE() + " FCFA</span><br/>" : "");
                        ro.put("bCANBEUSETP", lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getBCANBEUSE());
                        ro.put("messageTP", (lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDblPLAFONDCREDIT() > 0 && lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDbCONSOMMATIONMENSUELLE() >= lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDblPLAFONDCREDIT()) ? "Impossible de faire des ventes avec  <strong style='color:blue;'>" + lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getStrNAME() + "</strong><br/> son plafond crédit est atteint: <span style='font-weight:900;color:red;'>" + lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDblPLAFONDCREDIT().longValue() + " FCFA</span><br/> Sa consommation est: <span style='font-weight:900;color:red;'>" + lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDbCONSOMMATIONMENSUELLE() + " FCFA</span></br/>" : "");
                           cmpt.put("RO", ro);
    
                        OTCompteClient = OcompteClientManagement.getTCompteClient(lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getLgTIERSPAYANTID());
                        if (OTCompteClient != null) {

                            json.put("dbl_PLAFOND_RO", OTCompteClient.getDblPLAFOND());
                            json.put("dbl_QUOTA_CONSO_MENSUELLE_RO", OTCompteClient.getDblQUOTACONSOMENSUELLE());
                            json.put("dbl_PLAFOND_QUOTA_DIFFERENCE_RO", OTCompteClient.getDblPLAFOND() - OTCompteClient.getDblQUOTACONSOMENSUELLE());

                        }

                    } else {
                        json.put("RC" + (lstTCompteClientTiersPayant.get(k).getIntPRIORITY() - 1), lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getStrNAME());
                        json.put("RC" + (lstTCompteClientTiersPayant.get(k).getIntPRIORITY() - 1) + "_TAUX", lstTCompteClientTiersPayant.get(k).getIntPOURCENTAGE());
                        json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_RC" + (lstTCompteClientTiersPayant.get(k).getIntPRIORITY() - 1) + "_ID", lstTCompteClientTiersPayant.get(k).getLgCOMPTECLIENTTIERSPAYANTID());

                        json.put("dbl_PLAFOND_RC" + (lstTCompteClientTiersPayant.get(k).getIntPRIORITY() - 1) + "_ID", lstTCompteClientTiersPayant.get(k).getDblPLAFOND());
                        json.put("dbl_QUOTA_CONSO_MENSUELLE_RC" + (lstTCompteClientTiersPayant.get(k).getIntPRIORITY() - 1) + "_ID", lstTCompteClientTiersPayant.get(k).getDblQUOTACONSOMENSUELLE());
                        json.put("dbl_PLAFOND_QUOTA_DIFFERENCE_RC" + (lstTCompteClientTiersPayant.get(k).getIntPRIORITY() - 1) + "_ID", lstTCompteClientTiersPayant.get(k).getDblPLAFOND() - lstTCompteClientTiersPayant.get(k).getDblQUOTACONSOMENSUELLE());

                        ro.put("NAME", lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getStrNAME());
                         ro.put("ID", lstTCompteClientTiersPayant.get(k).getLgCOMPTECLIENTTIERSPAYANTID()); 
                         ro.put("IDTIERSPAYANT", lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getLgTIERSPAYANTID());
                        
                        ro.put("TAUX", lstTCompteClientTiersPayant.get(k).getIntPOURCENTAGE());
                        ro.put("bCANBEUSE", lstTCompteClientTiersPayant.get(k).getBCANBEUSE());
                        ro.put("dbPLAFONDVENTE", lstTCompteClientTiersPayant.get(k).getDblPLAFOND());
                        ro.put("dbCONSOMMATION", lstTCompteClientTiersPayant.get(k).getDbCONSOMMATIONMENSUELLE());
                        ro.put("dbPLAFONDENCOURS", lstTCompteClientTiersPayant.get(k).getDbPLAFONDENCOURS());
                       ro.put("message", (lstTCompteClientTiersPayant.get(k).getDbPLAFONDENCOURS() > 0 && lstTCompteClientTiersPayant.get(k).getDbCONSOMMATIONMENSUELLE() >= lstTCompteClientTiersPayant.get(k).getDbPLAFONDENCOURS()) ? "Impossible de faire des ventes avec <strong style='color:blue;'>" + lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getStrNAME() + "</strong><br/> votre plafond est atteint: <span style='font-weight:900;color:red;'>" + lstTCompteClientTiersPayant.get(k).getDbPLAFONDENCOURS() + " FCFA </span><br/> Votre consommation est: <span style='font-weight:900;color:red;'>" + lstTCompteClientTiersPayant.get(k).getDbCONSOMMATIONMENSUELLE() + " FCFA</span><br/>" : "");
                        ro.put("bCANBEUSETP", lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getBCANBEUSE());
                        ro.put("messageTP", (lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDblPLAFONDCREDIT() > 0 && lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDbCONSOMMATIONMENSUELLE() >= lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDblPLAFONDCREDIT()) ? "Impossible de faire des ventes avec <strong style='color:blue;>" + lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getStrNAME() + "</strong> <br/> son plafond crédit est atteint: <span style='font-weight:900;color:red;'>" + lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDblPLAFONDCREDIT().longValue() + " FCFA </span><br/> Sa consommation est: <span style='font-weight:900;color:red;'>" + lstTCompteClientTiersPayant.get(k).getLgTIERSPAYANTID().getDbCONSOMMATIONMENSUELLE() + " FCFA</span><br/>" : "");
                        cmpt.put("RC_" + (lstTCompteClientTiersPayant.get(k).getIntPRIORITY() - 1), ro);

                    }
                   
                  
                    ObllBase.setDetailmessage(ODiffereManagement.getDetailmessage());
                    ObllBase.setMessage(ODiffereManagement.getMessage());
                }
                 cmpta.put(cmpt);
                 json.put("COMPTCLTTIERSPAYANT", cmpta);
                  
                arrayObj.put(json);
               
            }

        }
        virtual_liste_size = total;
        /*ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
         ObllBase.setMessage(OclientManagement.getMessage());*/
    }

    String result = "({\"total\":" + total + ",\"total_differe\":" + dbl_total_differe + ",\"isCustSolvable\":" + isCustSolvable + ",\"results\":" + arrayObj.toString() + "})";
    
%>

<%= result%>