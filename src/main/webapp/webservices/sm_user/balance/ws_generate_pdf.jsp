<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>
<%@page import="bll.report.StatisticsFamilleArticle"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TMvtCaisse"%>
<%@page import="net.sf.jasperreports.engine.JREmptyDataSource"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.report.JournalVente"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.printer.PrintManangement"%>
<%@page import="dal.TOfficine"%>
<%@page import="cust_barcode.barecodeManager"%>
<%@page import="dal.TParameters"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.jconnexion"%>
<%@page import="report.reportManager"%>
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
    dataManager OdataManager = new dataManager();
    List<EntityData> lstEntityData = new ArrayList<EntityData>();
    TParameters OTParameters = null;
    date key = new date();
%>


<!-- fin logic de gestion des page -->

<%    String lg_TYPE_REGLEMENT_ID = "%%", lg_EMPLACEMENT_ID = "%%";
    int VENTE_BRUT = 0, TOTAL_REMISE = 0, VENTE_NET = 0, TOTAL_GLOBAL = 0, NB = 0;
    String P_VENTEDEPOT_LABEL = "Ventes aux d?p?ts extensions", P_REGLEMENTDEPOT_LABEL = "R?glement des ventes des d?p?ts";
    double P_VO_PERCENT = 0d, P_VNO_PERCENT = 0d, P_VENTEDEPOT_ESPECE = 0d, P_VENTEDEPOT_CHEQUES = 0d, P_VENTEDEPOT_CB = 0d,
            P_TOTAL_VENTEDEPOT_CAISSE = 0d, P_REGLEMENTDEPOT_ESPECE = 0d, P_REGLEMENTDEPOT_CHEQUES = 0d, P_REGLEMENTDEPOT_CB = 0d,
            P_TOTAL_REGLEMENTDEPOT_CAISSE = 0d;
    Double P_SORTIECAISSE_ESPECE_FALSE = 0d;
    int TOTALBRUT = 0, TOTALNET = 0, P_TOTAL_REMISE = 0, P_TOTAL_PANIER = 0, TOTAL_REMISEVNO = 0,
            P_TOTAL_ESPECE = 0, P_TOTAL_CHEQUES = 0, P_TOTAL_CARTEBANCAIRE = 0,
            P_TOTAL_TIERSPAYANT = 0, P_AMOUNT_VO_TIERESPAYANT = 0, P_VO_PANIER_MOYEN = 0,
            P_AMOUNT_VO_ESPECE = 0, P_AMOUNT_VO_CHEQUE = 0, P_AMOUNT_VO_CARTEBANCAIRE = 0, P_AMOUNT_VO_DIFFERE = 0, P_TOTAL_AVOIR = 0;
    Date today = new Date();
    String str_Date_Debut = key.DateToString(today, key.formatterMysqlShort), str_Date_Fin = key.DateToString(today, key.formatterMysqlShort), search_value = "",
            h_debut = "00:00", h_fin = "23:59", lg_PREENGISTREMENT_ID = "%%", str_TYPE_VENTE = "%%";
    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    JournalVente OjournalVente = new JournalVente(OdataManager, OTUser);
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    //StatisticsFamilleArticle familleArticle = new StatisticsFamilleArticle(OdataManager, OTUser);
  //  TparameterManager OTparameterManager = new TparameterManager(OdataManager);
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());
    String empl = OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID();
    //JSONArray array = groupeCtl.getBalance(str_Date_Debut, str_Date_Fin, empl);
    JSONArray array = null;
    try {
        array = groupeCtl.getBalance(str_Date_Debut, str_Date_Fin, empl);
    } catch (Exception e) {
    }
    System.out.println("array  " + array);
    List<EntityData> listTMvtCaissesFalse = new ArrayList<EntityData>();
    // List<EntityData> listTDataEntity = OjournalVente.getBalanceVenteCaisse(str_Date_Debut, str_Date_Fin);
    List<EntityData> listTMvtCaisses = OjournalVente.getAllMouvmentsCaisse(str_Date_Debut, str_Date_Fin);
    List<EntityData> listTDataInDepot = OjournalVente.getListeVenteInDepotForBalanceVenteCaisse(str_Date_Debut, str_Date_Fin, lg_TYPE_REGLEMENT_ID, lg_EMPLACEMENT_ID);

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();

    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_balancevente_caissev2";
    // String scr_report_file = "rp_liste_retrocession_final";
    String report_generate_file = key.GetNumberRandom();
    TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");
    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "balancevente_caisse" + report_generate_file);
    int count = 0;
    Integer VENTE_BRUT_VNO = 0, P_AMOUNT_VNO_ESPECE = 0, NBVNO = 0, VENTE_VNONET = 0, P_AMOUNT_VNO_CARTEBANCAIRE = 0, P_AMOUNT_VNO_DIFFERE = 0, TOTAL_VNOREMISE = 0, P_VNO_PANIER_MOYEN = 0, P_AMOUNT_VNO_CHEQUE = 0;
    if (array.length() > 1) {
        JSONObject totaux = array.getJSONObject((array.length() - 1));
        TOTAL_GLOBAL = totaux.getInt("GLOBAL");
        P_TOTAL_PANIER = ((Long) Math.round(Double.valueOf(totaux.getInt("GLOBAL")) / totaux.getInt("NB"))).intValue();
        for (int idx = 0; idx < (array.length() - 1); idx++) {
            try {
                JSONObject o = array.getJSONObject(idx);
                TOTALBRUT += o.getInt("VENTE_BRUT");
                P_TOTAL_REMISE += o.getInt("TOTAL_REMISE");
                TOTALNET += o.getInt("VENTE_NET");
                if ("VO".equals(o.getString("str_TYPE_VENTE"))) {
                    VENTE_BRUT = o.getInt("VENTE_BRUT");
                    VENTE_NET = o.getInt("VENTE_NET");
                    NB = o.getInt("NB");
                    TOTAL_REMISE = o.getInt("TOTAL_REMISE");
                    double mnt = o.getInt("VENTE_NET");
                    double percent = (mnt * 100) /Math.abs(totaux.getInt("GLOBAL"));
                    P_VO_PERCENT = Math.abs(Math.round(percent));
                    P_AMOUNT_VO_TIERESPAYANT = o.getInt("PART_TIERSPAYANT");
                    P_VO_PANIER_MOYEN = o.getInt("PANIER_MOYEN");
                    P_AMOUNT_VO_ESPECE = o.getInt("TOTAL_ESPECE");
                    P_AMOUNT_VO_CHEQUE = o.getInt("TOTAL_CHEQUE");
                    P_AMOUNT_VO_CARTEBANCAIRE = o.getInt("TOTAL_CARTEBANCAIRE");
                    P_AMOUNT_VO_DIFFERE = o.getInt("TOTAL_DIFFERE");

                } else {
                    TOTAL_REMISEVNO = o.getInt("TOTAL_REMISE");
                   double mnt = o.getInt("VENTE_NET");
                    double percent = (mnt * 100) /Math.abs(totaux.getInt("GLOBAL"));
                    P_VNO_PERCENT = Math.round(percent);
                    P_VNO_PANIER_MOYEN = o.getInt("PANIER_MOYEN");
                    P_AMOUNT_VNO_ESPECE = o.getInt("TOTAL_ESPECE");
                    P_AMOUNT_VNO_CHEQUE = o.getInt("TOTAL_CHEQUE");
                    P_AMOUNT_VNO_CARTEBANCAIRE = o.getInt("TOTAL_CARTEBANCAIRE");
                    P_AMOUNT_VNO_DIFFERE = o.getInt("TOTAL_DIFFERE");
                    VENTE_BRUT_VNO = o.getInt("VENTE_BRUT");
                    VENTE_VNONET = o.getInt("VENTE_NET");
                    NBVNO = o.getInt("NB");

                }
                count += o.getInt("NB");
                P_TOTAL_AVOIR += o.getInt("TOTAL_DIFFERE");

                P_TOTAL_ESPECE += o.getInt("TOTAL_ESPECE");
                P_TOTAL_CHEQUES += o.getInt("TOTAL_CHEQUE");
                P_TOTAL_CARTEBANCAIRE += o.getInt("TOTAL_CARTEBANCAIRE");
                P_TOTAL_TIERSPAYANT += o.getInt("PART_TIERSPAYANT");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    String P_H_CLT_INFOS = "BALANCE VENTE/CAISSE             DU " + date.formatterShort.format(java.sql.Date.valueOf(str_Date_Debut)) + " AU " + date.formatterShort.format(java.sql.Date.valueOf(str_Date_Fin));
    Map parameters = new HashMap();

    parameters.put("P_EMPLACEMENT", OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);

    parameters.put("P_TYPE_VENTE", str_TYPE_VENTE);

    parameters.put("P_VO_PERCENT", Math.round(P_VO_PERCENT));

    parameters.put("P_AMOUNT_REMISE_VO", conversion.AmountFormat(TOTAL_REMISE, ' '));
    parameters.put("P_VENTE_NET_VO", conversion.AmountFormat(VENTE_NET, ' '));
    parameters.put("P_AMOUNT_BRUT_VO", conversion.AmountFormat(VENTE_BRUT, ' '));
    parameters.put("P_NB_VO", conversion.AmountFormat(NB, ' '));
    parameters.put("P_AMOUNT_VO_TIERESPAYANT", conversion.AmountFormat(P_AMOUNT_VO_TIERESPAYANT, ' '));

    parameters.put("P_VO_PANIER_MOYEN", conversion.AmountFormat(P_VO_PANIER_MOYEN, ' '));

    parameters.put("P_AMOUNT_VO_ESPECE", conversion.AmountFormat(P_AMOUNT_VO_ESPECE, ' '));
    parameters.put("P_AMOUNT_VO_CHEQUE", conversion.AmountFormat(P_AMOUNT_VO_CHEQUE, ' '));
    parameters.put("P_AMOUNT_VO_CARTEBANCAIRE", conversion.AmountFormat(P_AMOUNT_VO_CARTEBANCAIRE, ' '));
    parameters.put("P_AMOUNT_VO_DIFFERE", conversion.AmountFormat(P_AMOUNT_VO_DIFFERE, ' '));

    parameters.put("P_VENTE_NET_AVOIR", "0");
    parameters.put("P_NB_AVOIR", "0");
    parameters.put("P_VO_PANIER_AVOIR", "0");
    parameters.put("P_AVOIR_", "0");
    parameters.put("P_AMOUNT_AVOIR_ESPECE", "0");
    parameters.put("P_AMOUNT_AVOIR_CHEQUE", "0");
    parameters.put("P_AMOUNT_AVOIR_CARTEBANCAIRE", "0");
    parameters.put("P_AMOUNT_AVOIR_DIFFERE", "0");
    parameters.put("P_AMOUNT_BRUT_AVOIR", "0");
    parameters.put("P_AMOUNT_AVOIR_TIERESPAYANT", "0");
    parameters.put("P_AMOUNT_AVOIR_VO", "0");

    parameters.put("P_AMOUNT_BRUT_VNO", conversion.AmountFormat(VENTE_BRUT_VNO, ' '));
    parameters.put("P_VENTE_NET_VNO", conversion.AmountFormat(VENTE_VNONET, ' '));
    parameters.put("P_AMOUNT_VNO_ESPECE", conversion.AmountFormat(P_AMOUNT_VNO_ESPECE, ' '));

    parameters.put("P_NB_VNO", conversion.AmountFormat(NBVNO, ' '));
    parameters.put("P_AMOUNT_REMISE_VNO", conversion.AmountFormat(TOTAL_REMISEVNO, ' '));
    parameters.put("P_AMOUNT_VNO_TIERSPAYANT", "0");
    parameters.put("P_VNO_PANIER_MOYEN", conversion.AmountFormat(P_VNO_PANIER_MOYEN, ' '));
    //parameters.put("P_AMOUNT_VNO_DIFFERE", "0");
    parameters.put("P_AMOUNT_AVOIR_TIERSPAYANT", "0");
    parameters.put("P_AMOUNT_REMISE_AVOIR", "0");

    parameters.put("P_AMOUNT_VNO_CHEQUE", conversion.AmountFormat(P_AMOUNT_VNO_CHEQUE, ' '));
    parameters.put("P_AMOUNT_VNO_CARTEBANCAIRE", conversion.AmountFormat(P_AMOUNT_VNO_CARTEBANCAIRE, ' '));
    parameters.put("P_AMOUNT_VNO_DIFFERE", conversion.AmountFormat(P_AMOUNT_VNO_DIFFERE, ' '));

    parameters.put("P_NB", conversion.AmountFormat(count, ' '));
    parameters.put("P_TOTAL_BRUT", conversion.AmountFormat(TOTALBRUT, ' '));
    parameters.put("P_TOTAL_NET", conversion.AmountFormat(TOTALNET, ' '));
    parameters.put("P_TOTAL_REMISE", conversion.AmountFormat(P_TOTAL_REMISE, ' '));

    parameters.put("P_TOTAL_PANIER", conversion.AmountFormat(P_TOTAL_PANIER, ' '));
    parameters.put("P_TOTAL_ESPECE", conversion.AmountFormat(P_TOTAL_ESPECE, ' '));
    parameters.put("P_TOTAL_CHEQUES", conversion.AmountFormat(P_TOTAL_CHEQUES, ' '));
    parameters.put("P_TOTAL_CARTEBANCAIRE", conversion.AmountFormat(P_TOTAL_CARTEBANCAIRE, ' '));
    parameters.put("P_TOTAL_TIERSPAYANT", conversion.AmountFormat(P_TOTAL_TIERSPAYANT, ' '));
    parameters.put("P_TOTAL_AVOIR", P_TOTAL_AVOIR);

    parameters.put("P_VNO_PERCENT", Math.round(P_VNO_PERCENT));

    double P_TOTAL_PERCENT = (P_VO_PERCENT + P_VNO_PERCENT);
    parameters.put("P_TOTAL_PERCENT", Math.round(P_TOTAL_PERCENT));
    double P_TOTAL_VENTE = P_TOTAL_ESPECE + P_TOTAL_CHEQUES + P_TOTAL_CARTEBANCAIRE;

    parameters.put("P_TOTAL_VENTE", conversion.AmountFormat((int) P_TOTAL_VENTE, ' '));
    String P_REGLEMENT_LIBELLE = "", P_FONDCAISSE_LABEL = "",
            P_SORIECAISSE_LABEL = "", P_ENTREECAISSE_LABEL = "", P_REGLEMENT_LABEL = "", P_ACCOMPTE_LABEL = "", P_DIFFERE_LABEL = "", P_TOTAL_CAISSE_LABEL = "";
    double P_SORTIECAISSE_ESPECE = 0d, P_SORTIECAISSE_CHEQUES = 0d,
            P_SORTIECAISSE_CB = 0d, P_TOTAL_FONDCAISSE = 0d, P_SORTIECAISSE_VIREMENT = 0d,
            P_TOTAL_SORTIE_CAISSE = 0d, P_ENTREECAISSE_ESPECE = 0d, P_ENTREECAISSE_VIREMENT = 0d,
            P_ENTREECAISSE_CHEQUES = 0d, P_ENTREECAISSE_CB = 0d, P_TOTAL_GLOBAL_CAISS = 0d,
            P_TOTAL_ENTREE_CAISSE = 0d, P_REGLEMENT_ESPECE = 0d, P_REGLEMENT_CHEQUES = 0d, P_REGLEMENT_VIREMENT = 0d,
            P_REGLEMENT_CB = 0d, P_TOTAL_REGLEMENT_CAISSE = 0d, P_ACCOMPTE_ESPECE = 0d, P_ACCOMPTE_CHEQUES = 0d, P_ACCOMPTE_VIREMENT = 0d,
            P_ACCOMPTE_CB = 0d, P_TOTAL_ACCOMPTE_CAISSE = 0d, P_FONDCAISSE = 0d, P_DIFFERE_CHEQUES = 0d, P_DIFFERE_CB = 0d, P_TOTAL_GLOBAL_CAISSE = 0d,
            P_DIFFERE_ESPECE = 0d, P_DIFFERE_VIREMENT = 0d, P_TOTAL_VIREMENT_GLOBAL = 0d, P_TOTAL_DIFFERE_CAISSE = 0d, P_TOTAL_ESPECES_GLOBAL = 0d, P_TOTAL_CHEQUES_GLOBAL = 0d, P_TOTAL_CB_GLOBAL = 0d;

    for (EntityData Odata : listTMvtCaisses) {
        // System.out.println("P_SORIECAISSE_LABEL +++++++++++++++++++++++++++++++++ " + Odata.getStr_value2() + "  --------------------" + Odata.getStr_value4());
        if (Odata.getStr_value4().equals(Parameter.KEY_PARAM_MVT_FOND_DE_CAISSE)) {
            P_FONDCAISSE_LABEL = Odata.getStr_value2();
            P_FONDCAISSE = Double.valueOf(Odata.getStr_value1());
        } else if (Odata.getStr_value4().equals(Parameter.KEY_PARAM_MVT_SORTIECAISSE)) {

            P_SORIECAISSE_LABEL = Odata.getStr_value2();
            // System.out.println("P_SORIECAISSE_LABEL +++++++++++++++++++++++++++++++++ " + P_SORIECAISSE_LABEL + "  --------------------");
            if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_ESPECE)) {
                P_SORTIECAISSE_ESPECE = (1) * Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CHEQUE)) {
                P_SORTIECAISSE_CHEQUES = (1) * Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CARTEBANQUAIRE)) {
                P_SORTIECAISSE_CB = (1) * Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_VIREMENT)) {
                P_SORTIECAISSE_VIREMENT = (1) * Double.valueOf(Odata.getStr_value1());
            }
        } else if (Odata.getStr_value4().equals(Parameter.KEY_PARAM_MVT_ENTREE_CAISSE)) {
            P_ENTREECAISSE_LABEL = Odata.getStr_value2();
            if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_ESPECE)) {
                P_ENTREECAISSE_ESPECE = Double.valueOf(Odata.getStr_value1());

            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CHEQUE)) {
                P_ENTREECAISSE_CHEQUES = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CARTEBANQUAIRE)) {
                P_ENTREECAISSE_CB = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_VIREMENT)) {
                P_ENTREECAISSE_VIREMENT = Double.valueOf(Odata.getStr_value1());
            }
        } else if (Odata.getStr_value4().equals(Parameter.KEY_PARAM_MVT_REGLEMENTTIERS)) {
            P_REGLEMENT_LABEL = Odata.getStr_value2();
            if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_ESPECE)) {
                P_REGLEMENT_ESPECE = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CHEQUE)) {
                P_REGLEMENT_CHEQUES = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CARTEBANQUAIRE)) {
                P_REGLEMENT_CB = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_VIREMENT)) {
                P_REGLEMENT_VIREMENT = Double.valueOf(Odata.getStr_value1());
            }
        } else if (Odata.getStr_value4().equals(Parameter.KEY_PARAM_MVT_REGLEMENT_DIFFERES)) {
            P_DIFFERE_LABEL = Odata.getStr_value2();
            if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_ESPECE)) {
                P_DIFFERE_ESPECE = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CHEQUE)) {
                P_DIFFERE_CHEQUES = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CARTEBANQUAIRE)) {
                P_DIFFERE_CB = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_VIREMENT)) {
                P_DIFFERE_VIREMENT = Double.valueOf(Odata.getStr_value1());
            }
        } else if (Odata.getStr_value4().equals(Parameter.KEY_PARAM_MVT_ACCOMPTES)) {
            P_ACCOMPTE_LABEL = Odata.getStr_value2();
            if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_ESPECE)) {
                P_ACCOMPTE_ESPECE = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CHEQUE)) {
                P_ACCOMPTE_CHEQUES = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_CARTEBANQUAIRE)) {
                P_ACCOMPTE_CB = Double.valueOf(Odata.getStr_value1());
            } else if (Odata.getStr_value6().equals(Parameter.KEY_TYPEREGLEMENT_VIREMENT)) {
                P_ACCOMPTE_VIREMENT = Double.valueOf(Odata.getStr_value1());
            }
        }
    }

    /* subquery parameters */
    //code ajout? 05/04/2016
    if (OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS)) {
        P_VENTEDEPOT_ESPECE = (-1) * OPreenregistrement.getPriceVenteToDepot("", Parameter.VENTE_DEPOT_EXTENSION, str_Date_Debut, str_Date_Fin);
        P_TOTAL_VENTEDEPOT_CAISSE = P_VENTEDEPOT_ESPECE + P_VENTEDEPOT_CHEQUES + P_VENTEDEPOT_CB;
        P_REGLEMENTDEPOT_ESPECE = OjournalVente.getTotalAmountVenteInDepotForBalanceVenteCaisse(listTDataInDepot, Parameter.KEY_TYPEREGLEMENT_ESPECE) + OjournalVente.getTotalAmountVenteInDepotForBalanceVenteCaisse(listTDataInDepot, Parameter.KEY_TYPEREGLEMENT_DIFERRE);
        P_REGLEMENTDEPOT_CHEQUES = OjournalVente.getTotalAmountVenteInDepotForBalanceVenteCaisse(listTDataInDepot, Parameter.KEY_TYPEREGLEMENT_CHEQUE);
        P_REGLEMENTDEPOT_CB = OjournalVente.getTotalAmountVenteInDepotForBalanceVenteCaisse(listTDataInDepot, Parameter.KEY_TYPEREGLEMENT_CARTEBANQUAIRE);
        P_TOTAL_REGLEMENTDEPOT_CAISSE = P_REGLEMENTDEPOT_ESPECE + P_REGLEMENTDEPOT_CHEQUES + P_REGLEMENTDEPOT_CB;
    }

    P_VENTEDEPOT_LABEL = (P_TOTAL_VENTEDEPOT_CAISSE != 0 ? P_VENTEDEPOT_LABEL : "");
    P_REGLEMENTDEPOT_LABEL = (P_TOTAL_REGLEMENTDEPOT_CAISSE > 0 ? P_REGLEMENTDEPOT_LABEL : "");

    parameters.put("P_VENTEDEPOT_LABEL", P_VENTEDEPOT_LABEL);
    parameters.put("P_VENTEDEPOT_ESPECE", conversion.AmountFormat((int) P_VENTEDEPOT_ESPECE, ' '));
    parameters.put("P_VENTEDEPOT_CHEQUES", conversion.AmountFormat((int) P_VENTEDEPOT_CHEQUES, ' '));
    parameters.put("P_VENTEDEPOT_CB", conversion.AmountFormat((int) P_VENTEDEPOT_CB, ' '));
    parameters.put("P_TOTAL_VENTEDEPOT_CAISSE", conversion.AmountFormat((int) P_TOTAL_VENTEDEPOT_CAISSE, ' '));

    parameters.put("P_REGLEMENTDEPOT_LABEL", P_REGLEMENTDEPOT_LABEL);
    parameters.put("P_REGLEMENTDEPOT_ESPECE", conversion.AmountFormat((int) P_REGLEMENTDEPOT_ESPECE, ' '));
    parameters.put("P_REGLEMENTDEPOT_CHEQUES", conversion.AmountFormat((int) P_REGLEMENTDEPOT_CHEQUES, ' '));
    parameters.put("P_REGLEMENTDEPOT_CB", conversion.AmountFormat((int) P_REGLEMENTDEPOT_CB, ' '));
    parameters.put("P_TOTAL_REGLEMENTDEPOT_CAISSE", conversion.AmountFormat((int) P_TOTAL_REGLEMENTDEPOT_CAISSE, ' '));

    //fin code ajout? 05/04/2016
    P_TOTAL_SORTIE_CAISSE = P_SORTIECAISSE_ESPECE + P_SORTIECAISSE_CHEQUES + P_SORTIECAISSE_CB;
    P_TOTAL_ENTREE_CAISSE = P_ENTREECAISSE_ESPECE + P_ENTREECAISSE_CHEQUES + P_ENTREECAISSE_CB;
    P_TOTAL_REGLEMENT_CAISSE = P_REGLEMENT_ESPECE + P_REGLEMENT_CHEQUES + P_REGLEMENT_CB;
    P_TOTAL_ACCOMPTE_CAISSE = P_ACCOMPTE_ESPECE + P_ACCOMPTE_CHEQUES + P_ACCOMPTE_CB;
    P_TOTAL_DIFFERE_CAISSE = P_DIFFERE_ESPECE + P_DIFFERE_CHEQUES + P_DIFFERE_CB;

    P_TOTAL_ESPECES_GLOBAL = (P_FONDCAISSE + P_TOTAL_ESPECE + P_ENTREECAISSE_ESPECE + P_REGLEMENT_ESPECE + P_ACCOMPTE_ESPECE + P_DIFFERE_ESPECE + P_REGLEMENTDEPOT_ESPECE) + P_SORTIECAISSE_ESPECE;
    P_TOTAL_CHEQUES_GLOBAL = P_TOTAL_CHEQUES + P_SORTIECAISSE_CHEQUES + P_ENTREECAISSE_CHEQUES + P_REGLEMENT_CHEQUES + P_ACCOMPTE_CHEQUES + P_DIFFERE_CHEQUES + P_REGLEMENTDEPOT_CHEQUES;
    P_TOTAL_VIREMENT_GLOBAL = P_ENTREECAISSE_VIREMENT + P_SORTIECAISSE_VIREMENT + P_REGLEMENT_VIREMENT + P_ACCOMPTE_VIREMENT + P_DIFFERE_VIREMENT;
    P_TOTAL_CB_GLOBAL = P_TOTAL_CARTEBANCAIRE + P_SORTIECAISSE_CB + P_ENTREECAISSE_CB + P_REGLEMENT_CB + P_ACCOMPTE_CB + P_DIFFERE_CB + P_REGLEMENTDEPOT_CB;

    /* code ajout? 15/07/2015 */
    P_TOTAL_GLOBAL_CAISSE = +P_TOTAL_ESPECES_GLOBAL + P_TOTAL_CHEQUES_GLOBAL + P_TOTAL_CB_GLOBAL;

    parameters.put("P_TOTAL_GLOBAL_CAISSE", conversion.AmountFormat((int) P_TOTAL_GLOBAL_CAISSE, ' '));
    parameters.put("P_TOTAL_VIREMENT_GLOBAL", conversion.AmountFormat((int) P_TOTAL_VIREMENT_GLOBAL, ' '));

    parameters.put("P_SORIECAISSE_LABEL", P_SORIECAISSE_LABEL);

    parameters.put("P_TOTAL_CB_GLOBAL", conversion.AmountFormat((int) P_TOTAL_CB_GLOBAL, ' '));
    parameters.put("P_TOTAL_CHEQUES_GLOBAL", conversion.AmountFormat((int) P_TOTAL_CHEQUES_GLOBAL, ' '));
    parameters.put("P_FONDCAISSE", conversion.AmountFormat((int) P_FONDCAISSE, ' '));
    parameters.put("P_SORTIECAISSE_ESPECE", conversion.AmountFormat((int) P_SORTIECAISSE_ESPECE, ' '));
    parameters.put("P_SORTIECAISSE_CHEQUES", conversion.AmountFormat((int) P_SORTIECAISSE_CHEQUES, ' '));
    parameters.put("P_SORTIECAISSE_CB", conversion.AmountFormat((int) P_SORTIECAISSE_CB, ' '));
    parameters.put("P_SORTIECAISSE_VIREMENT", conversion.AmountFormat((int) P_SORTIECAISSE_VIREMENT, ' '));
    parameters.put("P_TOTAL_FONDCAISSE", conversion.AmountFormat((int) P_FONDCAISSE, ' '));
    parameters.put("P_TOTAL_SORTIE_CAISSE", conversion.AmountFormat((int) P_TOTAL_SORTIE_CAISSE, ' '));
    parameters.put("P_ENTREECAISSE_ESPECE", conversion.AmountFormat((int) P_ENTREECAISSE_ESPECE, ' '));
    parameters.put("P_ENTREECAISSE_VIREMENT", conversion.AmountFormat((int) P_ENTREECAISSE_VIREMENT, ' '));
    parameters.put("P_ENTREECAISSE_CHEQUES", conversion.AmountFormat((int) P_ENTREECAISSE_CHEQUES, ' '));
    parameters.put("P_ENTREECAISSE_CB", conversion.AmountFormat((int) P_ENTREECAISSE_CB, ' '));
    parameters.put("P_TOTAL_ENTREE_CAISSE", conversion.AmountFormat((int) P_TOTAL_ENTREE_CAISSE, ' '));
    parameters.put("P_REGLEMENT_ESPECE", conversion.AmountFormat((int) P_REGLEMENT_ESPECE, ' '));
    parameters.put("P_REGLEMENT_VIREMENT", conversion.AmountFormat((int) P_REGLEMENT_VIREMENT, ' '));
    parameters.put("P_REGLEMENT_CHEQUES", conversion.AmountFormat((int) P_REGLEMENT_CHEQUES, ' '));
    parameters.put("P_REGLEMENT_CB", conversion.AmountFormat((int) P_REGLEMENT_CB, ' '));
    parameters.put("P_TOTAL_REGLEMENT_CAISSE", conversion.AmountFormat((int) P_TOTAL_REGLEMENT_CAISSE, ' '));
    parameters.put("P_ACCOMPTE_ESPECE", conversion.AmountFormat((int) P_ACCOMPTE_ESPECE, ' '));
    parameters.put("P_ACCOMPTE_CHEQUES", conversion.AmountFormat((int) P_ACCOMPTE_CHEQUES, ' '));
    parameters.put("P_ACCOMPTE_CB", conversion.AmountFormat((int) P_ACCOMPTE_CB, ' '));
    parameters.put("P_ACCOMPTE_VIREMENT", conversion.AmountFormat((int) P_ACCOMPTE_VIREMENT, ' '));

    parameters.put("P_TOTAL_ACCOMPTE_CAISSE", conversion.AmountFormat((int) P_TOTAL_ACCOMPTE_CAISSE, ' '));
    parameters.put("P_DIFFERE_ESPECE", conversion.AmountFormat((int) P_DIFFERE_ESPECE, ' '));
    parameters.put("P_DIFFERE_VIREMENT", conversion.AmountFormat((int) P_DIFFERE_VIREMENT, ' '));
    parameters.put("P_DIFFERE_CHEQUES", conversion.AmountFormat((int) P_DIFFERE_CHEQUES, ' '));
    parameters.put("P_DIFFERE_CB", conversion.AmountFormat((int) P_DIFFERE_CB, ' '));

    parameters.put("P_TOTAL_DIFFERE_CAISSE", conversion.AmountFormat((int) P_TOTAL_DIFFERE_CAISSE, ' '));
    P_TOTAL_CAISSE_LABEL = "Total caisse " + date.formatterShort.format(java.sql.Date.valueOf(str_Date_Debut)) + " AU " + date.formatterShort.format(java.sql.Date.valueOf(str_Date_Fin));
    parameters.put("P_TOTAL_CAISSE_LABEL", P_TOTAL_CAISSE_LABEL);

    parameters.put("P_FONDCAISSE_LABEL", P_FONDCAISSE_LABEL);
    parameters.put("P_ENTREECAISSE_LABEL", P_ENTREECAISSE_LABEL);
    parameters.put("P_DIFFERE_LABEL", P_DIFFERE_LABEL);
    parameters.put("P_ACCOMPTE_LABEL", P_ACCOMPTE_LABEL);
    parameters.put("P_REGLEMENT_LABEL", P_REGLEMENT_LABEL);
    parameters.put("P_TOTAL_ESPECES_GLOBAL", conversion.AmountFormat((int) P_TOTAL_ESPECES_GLOBAL, ' '));

    parameters.put("P_ACCOMPTE_CB", conversion.AmountFormat((int) P_ACCOMPTE_CB, ' '));
    String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
    String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();

    String P_H_LOGO = jdom.scr_report_file_logo;

    parameters.put("P_H_LOGO", P_H_LOGO);
    parameters.put("P_H_INSTITUTION", P_H_INSTITUTION);

    parameters.put("P_PRINTED_BY", " " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC", oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());

    String P_FOOTER_RC = "";

    if (oTOfficine.getStrREGISTRECOMMERCE() != null) {
        P_FOOTER_RC += "RC N? " + oTOfficine.getStrREGISTRECOMMERCE();
    }

    if (oTOfficine.getStrCOMPTECONTRIBUABLE() != null) {
        P_FOOTER_RC += " - CC N? " + oTOfficine.getStrCOMPTECONTRIBUABLE();
    }
    if (oTOfficine.getStrREGISTREIMPOSITION() != null) {
        P_FOOTER_RC += " - R?gime d'Imposition " + oTOfficine.getStrREGISTREIMPOSITION();
    }
    if (oTOfficine.getStrCENTREIMPOSITION() != null) {
        P_FOOTER_RC += " - Centre des Imp?ts: " + oTOfficine.getStrCENTREIMPOSITION();
    }

    if (oTOfficine.getStrPHONE() != null) {
        String finalphonestring = oTOfficine.getStrPHONE() != null ? "- Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
        if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
            String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
            for (String va  : phone) {
                finalphonestring += " / " + conversion.PhoneNumberFormat(va);
            }
        }
        P_INSTITUTION_ADRESSE += " -  " + finalphonestring;
    }
    if (oTOfficine.getStrCOMPTEBANCAIRE() != null) {
        P_INSTITUTION_ADRESSE += " - Compte Bancaire: " + oTOfficine.getStrCOMPTEBANCAIRE();
    }
    if (oTOfficine.getStrNUMCOMPTABLE() != null) {
        P_INSTITUTION_ADRESSE += " - CPT N?: " + oTOfficine.getStrNUMCOMPTABLE();
    }
    parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
    parameters.put("P_FOOTER_RC", P_FOOTER_RC);

    OreportManager.BuildReportEmptyDs(parameters);

    // Ojconnexion.CloseConnexion();
    String str_final_file = Ojdom.scr_report_pdf + "balancevente_caisse" + report_generate_file;

    new logger().OCategory.info("str_final_file -----------" + str_final_file);

    response.sendRedirect(request.getContextPath() + "/data/reports/pdf/" + "balancevente_caisse" + report_generate_file);


%>

