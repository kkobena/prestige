
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="bll.facture.reglementManager"%>
<%@page import="dal.TGroupeFactures"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TGroupeTierspayant"%>
<%@page import="dal.TUser"%>
<%@page import="org.json.JSONException"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="java.util.*"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="org.json.JSONObject"  %>          
<%@page import="toolkits.utils.jdom"  %>
<%
    dataManager OdataManager = new dataManager();

    OdataManager.initEntityManager();
    String lg_FACTURE_ID = "", str_NOM = "", str_CODE_MONNAIE = "", str_BANQUE = "",
            str_LIEU = "", TYPE_REGLEMENT = "", REF_REGLEMENT = "",
            MODE_REGLEMENT = "", str_refBon = "", lg_DOSSIER_REGLEMENT_ID = "", CODEFACTURE = "";
    Date dt_reglement = new Date();
    Integer int_TAUX_CHANGE = 0;
    JSONArray listFactureDeatils = new JSONArray();
    JSONArray uncheckedlist = new JSONArray();

    double NET_A_PAYER = 0.0, AMOUNT_PAYE, int_AMOUNT_REMIS = 0.0, int_AMOUNT_RECU = 0.0;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
     TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    if (request.getParameter("dt_reglement") != null && !"".equals(request.getParameter("dt_reglement"))) {
        //  new logger().OCategory.info("dt_reglement " + request.getParameter(now+ " ********************************************** "));
        String now = request.getParameter("dt_reglement") + " " + date.NomadicUiFormatTime.format(new Date());

        dt_reglement = date.formatterMysql2.parse(now);

    }

    if (request.getParameter("checkedList") != null && !"".equals(request.getParameter("checkedList"))) {
        uncheckedlist = new JSONArray(request.getParameter("checkedList"));

    }
    if (request.getParameter("lg_FACTURE_ID") != null && !"".equals(request.getParameter("lg_FACTURE_ID"))) {
        lg_FACTURE_ID = request.getParameter("lg_FACTURE_ID");

    }

    if (request.getParameter("MODE_REGLEMENT") != null && !"".equals(request.getParameter("MODE_REGLEMENT"))) {
        MODE_REGLEMENT = request.getParameter("MODE_REGLEMENT");

    }

    if (request.getParameter("int_AMOUNT_RECU") != null && !"".equals(request.getParameter("int_AMOUNT_RECU"))) {
        int_AMOUNT_RECU = Double.parseDouble(request.getParameter("int_AMOUNT_RECU"));

    }
    if (request.getParameter("int_AMOUNT_REMIS") != null && !"".equals(request.getParameter("int_AMOUNT_REMIS"))) {
        int_AMOUNT_REMIS = Double.parseDouble(request.getParameter("int_AMOUNT_REMIS"));

    }
    if (request.getParameter("NET_A_PAYER") != null && !"".equals(request.getParameter("NET_A_PAYER"))) {
        NET_A_PAYER = Double.parseDouble(request.getParameter("NET_A_PAYER"));

    }

    if (request.getParameter("TYPE_REGLEMENT") != null && !"".equals(request.getParameter("TYPE_REGLEMENT"))) {
        TYPE_REGLEMENT = request.getParameter("TYPE_REGLEMENT");

    }
    if (request.getParameter("str_LIEU") != null && !"".equals(request.getParameter("str_LIEU"))) {
        str_LIEU = request.getParameter("str_LIEU");

    }
    if (request.getParameter("str_CODE_MONNAIE") != null && !"".equals(request.getParameter("str_CODE_MONNAIE"))) {
        str_CODE_MONNAIE = request.getParameter("str_CODE_MONNAIE");

    }

    if (request.getParameter("str_BANQUE") != null && !"".equals(request.getParameter("str_BANQUE"))) {
        str_BANQUE = request.getParameter("str_BANQUE");

    }
    if (request.getParameter("str_NOM") != null && !"".equals(request.getParameter("str_NOM"))) {
        str_NOM = request.getParameter("str_NOM");

    }
    if (request.getParameter("int_TAUX_CHANGE") != null && !"".equals(request.getParameter("int_TAUX_CHANGE"))) {
        int_TAUX_CHANGE = Integer.parseInt(request.getParameter("int_TAUX_CHANGE"));

    }
    if (request.getParameter("str_refBon") != null && !"".equals(request.getParameter("str_refBon"))) {
        int_TAUX_CHANGE = Integer.parseInt(request.getParameter("str_refBon"));

    }
    if (request.getParameter("LISTDOSSIERS") != null && !"".equals(request.getParameter("LISTDOSSIERS"))) {
        listFactureDeatils = new JSONArray(request.getParameter("LISTDOSSIERS"));

    }
    int mode = 0;
    if (request.getParameter("mode") != null && !"".equals(request.getParameter("mode"))) {
        mode =  Integer.valueOf(request.getParameter("mode"));

    }
    if (request.getParameter("CODEFACTURE") != null && !"".equals(request.getParameter("CODEFACTURE"))) {
        CODEFACTURE = request.getParameter("CODEFACTURE");

    }
    reglementManager OreglementManager = new reglementManager(OdataManager, user);
    JSONObject data = new JSONObject();
    data = OreglementManager.makeGroupInvoicePayment(CODEFACTURE, -1, str_BANQUE, str_LIEU, str_CODE_MONNAIE, str_LIEU, MODE_REGLEMENT, int_TAUX_CHANGE, NET_A_PAYER, 0, int_AMOUNT_RECU, listFactureDeatils, str_NOM, uncheckedlist, dt_reglement, mode);


%>

<%= data%>