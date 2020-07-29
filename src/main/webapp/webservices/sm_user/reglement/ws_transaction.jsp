<%@page import="org.json.JSONArray"%>
<%@page import="dal.TDossierReglementDetail"%>
<%@page import="dal.TDossierReglement"%>
<%@page import="bll.facture.reglementManager"%>
<%@page import="dal.TDossierFacture"%>
<%@page import="dal.TFactureDetail"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TBonLivraison"%>
<%@page import="dal.TTypeFacture"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TFacture"%>
<%@page import="bll.facture.factureManagement"%>
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

<% String lg_FACTURE_ID = "", str_NOM = "", str_CODE_MONNAIE = "", str_BANQUE = "", lg_NATURE_PAIEMENT = "", str_CUSTOMER = "",
            str_LIEU = "", NATURE_REGLEMENT = "", TYPE_REGLEMENT = "", REF_REGLEMENT = "",
            MODE_REGLEMENT = "", str_refBon = "", lg_DOSSIER_REGLEMENT_ID = "";
    Date dt_reglement = new Date();
    Integer int_TAUX_CHANGE = 0;
    JSONArray listFactureDeatils = new JSONArray();
    JSONArray uncheckedlist = new JSONArray();
    dataManager OdataManager = new dataManager();
    OdataManager.initEntityManager();
    double NET_A_PAYER = 0.0, AMOUNT_PAYE, int_AMOUNT_REMIS = 0.0, int_AMOUNT_RECU = 0.0;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    if (request.getParameter("dt_reglement") != null && !"".equals(request.getParameter("dt_reglement"))) {
        //  new logger().OCategory.info("dt_reglement " + request.getParameter(now+ " ********************************************** "));
        String now = request.getParameter("dt_reglement") + " " + date.NomadicUiFormatTime.format(new Date());

        dt_reglement = date.formatterMysql2.parse(now);

    }
    if (request.getParameter("lg_NATURE_PAIEMENT") != null && !"".equals(request.getParameter("lg_NATURE_PAIEMENT"))) {
        lg_NATURE_PAIEMENT = request.getParameter("lg_NATURE_PAIEMENT").toString();
        new logger().OCategory.info("lg_NATURE_PAIEMENT " + request.getParameter("lg_NATURE_PAIEMENT"));
    }

    if (request.getParameter("checkedList") != null && !"".equals(request.getParameter("checkedList"))) {
        uncheckedlist = new JSONArray(request.getParameter("checkedList").toString());

    }
    if (request.getParameter("lg_FACTURE_ID") != null && !"".equals(request.getParameter("lg_FACTURE_ID"))) {
        lg_FACTURE_ID = request.getParameter("lg_FACTURE_ID").toString();

    }
    if (request.getParameter("str_CUSTOMER") != null && !"".equals(request.getParameter("str_CUSTOMER"))) {
        str_CUSTOMER = request.getParameter("str_CUSTOMER").toString();

    }

    if (request.getParameter("MODE_REGLEMENT") != null && !"".equals(request.getParameter("MODE_REGLEMENT"))) {
        MODE_REGLEMENT = request.getParameter("MODE_REGLEMENT").toString();

    }

    if (request.getParameter("int_AMOUNT_RECU") != null && !"".equals(request.getParameter("int_AMOUNT_RECU"))) {
        int_AMOUNT_RECU = Double.parseDouble(request.getParameter("int_AMOUNT_RECU").toString());

    }
    if (request.getParameter("int_AMOUNT_REMIS") != null && !"".equals(request.getParameter("int_AMOUNT_REMIS"))) {
        int_AMOUNT_REMIS = Double.parseDouble(request.getParameter("int_AMOUNT_REMIS").toString());

    }
    if (request.getParameter("NET_A_PAYER") != null && !"".equals(request.getParameter("NET_A_PAYER"))) {
        NET_A_PAYER = Double.parseDouble(request.getParameter("NET_A_PAYER").toString());

    }

    if (request.getParameter("NATURE_REGLEMENT") != null && !"".equals(request.getParameter("NATURE_REGLEMENT"))) {
        NATURE_REGLEMENT = request.getParameter("NATURE_REGLEMENT").toString();

    }

    if (request.getParameter("TYPE_REGLEMENT") != null && !"".equals(request.getParameter("TYPE_REGLEMENT"))) {
        TYPE_REGLEMENT = request.getParameter("TYPE_REGLEMENT").toString();

    }
    if (request.getParameter("str_LIEU") != null && !"".equals(request.getParameter("str_LIEU"))) {
        str_LIEU = request.getParameter("str_LIEU").toString();

    }
    if (request.getParameter("str_CODE_MONNAIE") != null && !"".equals(request.getParameter("str_CODE_MONNAIE"))) {
        str_CODE_MONNAIE = request.getParameter("str_CODE_MONNAIE").toString();

    }

    if (request.getParameter("str_BANQUE") != null && !"".equals(request.getParameter("str_BANQUE"))) {
        str_BANQUE = request.getParameter("str_BANQUE").toString();

    }
    if (request.getParameter("str_NOM") != null && !"".equals(request.getParameter("str_NOM"))) {
        str_NOM = request.getParameter("str_NOM").toString();

    }
    if (request.getParameter("int_TAUX_CHANGE") != null && !"".equals(request.getParameter("int_TAUX_CHANGE"))) {
        int_TAUX_CHANGE = Integer.parseInt(request.getParameter("int_TAUX_CHANGE").toString());

    }
    if (request.getParameter("str_refBon") != null && !"".equals(request.getParameter("str_refBon"))) {
        int_TAUX_CHANGE = Integer.parseInt(request.getParameter("str_refBon").toString());

    }
    if (request.getParameter("LISTDOSSIERS") != null && !"".equals(request.getParameter("LISTDOSSIERS"))) {
        listFactureDeatils = new JSONArray(request.getParameter("LISTDOSSIERS"));

    }

    bllBase ObllBase = new bllBase();

    reglementManager OreglementManager = new reglementManager(OdataManager, OTUser);
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

        } else if (request.getParameter("mode").equals("doReglement")) {
            // TFacture OTFacture = OdataManager.getEm().find(TFacture.class, lg_FACTURE_ID);
            double finalamout = NET_A_PAYER <= int_AMOUNT_RECU ? NET_A_PAYER : int_AMOUNT_RECU;

            TDossierReglement ODossierReglement = OreglementManager.makeInvoicePayment(lg_FACTURE_ID, lg_NATURE_PAIEMENT, str_BANQUE, str_LIEU, str_CODE_MONNAIE, str_LIEU, MODE_REGLEMENT, int_TAUX_CHANGE, finalamout, int_AMOUNT_REMIS, int_AMOUNT_RECU, listFactureDeatils, str_NOM, str_CUSTOMER, uncheckedlist, dt_reglement);

            if (ODossierReglement != null) {
                lg_DOSSIER_REGLEMENT_ID = ODossierReglement.getLgDOSSIERREGLEMENTID();
                OreglementManager.updateSnapshotVenteSociete(lg_DOSSIER_REGLEMENT_ID, lg_FACTURE_ID);
            }

            ObllBase.setMessage(OreglementManager.getMessage());
            ObllBase.setDetailmessage(OreglementManager.getDetailmessage());

        }

    }

    String result = "";
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getDetailmessage() + "\",str_ref:\"" + lg_FACTURE_ID + "\",lg_DOSSIER_REGLEMENT_ID:\"" + lg_DOSSIER_REGLEMENT_ID + "\", errors: \"" + ObllBase.getMessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getDetailmessage() + "\",str_ref:\"" + lg_FACTURE_ID + "\",lg_DOSSIER_REGLEMENT_ID:\"" + lg_DOSSIER_REGLEMENT_ID + "\", errors: \"" + ObllBase.getMessage() + "\"}";
    }
    new logger().OCategory.info("result " + result);
%>
<%=result%>