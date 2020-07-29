<%@page import="dal.TParameters"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="java.util.ArrayList"%>
<%@page import="dal.TMotifReglement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();
    TPreenregistrementDetail OTPreenregistrementDetail = null;
    date key = new date();
    TParameters OTParameters = null;
%>



<%
    new logger().OCategory.info("dans ws data vente ");
    List<EntityData> listPreenregistrement = new ArrayList<EntityData>(), listPreenregistrementOther = new ArrayList<EntityData>();
Date now = new Date();
    String str_Date_Debut = date.DateToString(now, key.formatterMysqlShort), str_Date_Fin = str_Date_Debut, search_value = "",
            h_debut = "00:00", h_fin = "23:59", lg_PREENGISTREMENT_ID = "%%", str_TYPE_VENTE = "%%", lg_EMPLACEMENT_ID = "";
    
    boolean BTN_ANNULATION = false, isAmountOther = false;
    double dbl_AMOUNT = 0.0;
    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    lg_EMPLACEMENT_ID = (OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS) ? "1" : OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value :" + search_value);
    }

    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equalsIgnoreCase("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        new logger().OCategory.info("str_TYPE_VENTE :" + str_TYPE_VENTE);
    }

    if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        new logger().OCategory.info("h_debut :" + h_debut);
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");
        new logger().OCategory.info("h_fin :" + h_fin);
    }
    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    privilege Oprivilege = new privilege(OdataManager, OTUser);
    TparameterManager OTparameterManager = new TparameterManager(OdataManager);
    OTParameters = OTparameterManager.getParameter(Parameter.KEY_MOVEMENT_FALSE);
    if (OTParameters != null && Integer.parseInt(OTParameters.getStrVALUE()) == 1) {
        isAmountOther = true;
    }
    OTParameters = OTparameterManager.getParameter(Parameter.KEY_ACTIVATE_CONTROLE_VENTE_USER);
    BTN_ANNULATION = Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_BT_ANNULER_VENTE);
    listPreenregistrement = OPreenregistrement.listTPreenregistrement(search_value, lg_PREENGISTREMENT_ID, str_Date_Debut, str_Date_Fin, h_debut, h_fin, str_TYPE_VENTE, start, limit);
    listPreenregistrementOther = OPreenregistrement.listTPreenregistrement(search_value, lg_PREENGISTREMENT_ID, str_Date_Debut, str_Date_Fin, h_debut, h_fin, str_TYPE_VENTE);
    total = listPreenregistrementOther.size();
    dbl_AMOUNT = OPreenregistrement.getAmountTotalTPreenregistrement(listPreenregistrementOther);
%>


<%    JSONArray arrayObj = new JSONArray();

    for (int i = 0; i < (listPreenregistrement.size() < limit ? listPreenregistrement.size() : limit); i++) {
        List<TPreenregistrementDetail> listPreenregistrementDetail = OPreenregistrement.getTPreenregistrementDetail(listPreenregistrement.get(i).getStr_value1(), "%%", commonparameter.statut_is_Closed);
        String str_Product = "";
        for (int k = 0; k < listPreenregistrementDetail.size(); k++) {
            OTPreenregistrementDetail = listPreenregistrementDetail.get(k);
            if (OTPreenregistrementDetail != null) {
                str_Product = "<b><span style='display:inline-block;width: 7%;'>" + OTPreenregistrementDetail.getLgFAMILLEID().getIntCIP() + "</span><span style='display:inline-block;width: 25%;'>" + OTPreenregistrementDetail.getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(isAmountOther == true ? OTPreenregistrementDetail.getIntPRICEDETAILOTHER() : OTPreenregistrementDetail.getIntPRICEUNITAIR(), '.') + " F CFA " + "</span><span style='display:inline-block;width: 15%;'>(" + ((Integer.parseInt(listPreenregistrement.get(i).getStr_value5()) >= 0) ? OTPreenregistrementDetail.getIntQUANTITY() : (-1 * OTPreenregistrementDetail.getIntQUANTITY())) + ")</span></b><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(isAmountOther == true ? OTPreenregistrementDetail.getIntPRICEOTHER() : OTPreenregistrementDetail.getIntPRICE(), '.') + " F CFA " + "</span><br> " + str_Product;
            }
        }

        JSONObject json = new JSONObject();
        new logger().OCategory.info("id vente----" + listPreenregistrement.get(i).getStr_value1());
        json.put("lg_PREENREGISTREMENT_ID", listPreenregistrement.get(i).getStr_value1());
        json.put("str_REF", listPreenregistrement.get(i).getStr_value2());
        json.put("lg_USER_CAISSIER_ID", listPreenregistrement.get(i).getStr_value3());
        json.put("lg_USER_VENDEUR_ID", listPreenregistrement.get(i).getStr_value4());
        json.put("int_PRICE", listPreenregistrement.get(i).getStr_value5());
        /*json.put("dt_CREATED", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value15(), date.formatterMysql), date.formatterShort)); //a decommenter en cas de probleme
         json.put("str_hour", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value15(), date.formatterMysql),date.NomadicUiFormat_Time));*/
        json.put("dt_CREATED", listPreenregistrement.get(i).getStr_value16());
        json.put("str_hour", listPreenregistrement.get(i).getStr_value17());
        json.put("str_STATUT", listPreenregistrement.get(i).getStr_value7());
        json.put("str_FAMILLE_ITEM", str_Product);
        json.put("b_IS_CANCEL", listPreenregistrement.get(i).getStr_value8());
        json.put("str_TYPE_VENTE", listPreenregistrement.get(i).getStr_value10());
        json.put("str_FIRST_LAST_NAME_CLIENT", listPreenregistrement.get(i).getStr_value11());

        json.put("int_PRICE_FORMAT", conversion.AmountFormat(Integer.parseInt(listPreenregistrement.get(i).getStr_value5()), '.'));

        if (OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS) && listPreenregistrement.get(i).getStr_value9() == "0") {
            json.put("etat", commonparameter.PROCESS_SUCCESS);
        }
        json.put("int_SENDTOSUGGESTION", listPreenregistrement.get(i).getStr_value9());
        json.put("BTN_ANNULATION", BTN_ANNULATION);
        json.put("lg_EMPLACEMENT_ID", lg_EMPLACEMENT_ID);
        json.put("lg_TYPE_VENTE_ID", listPreenregistrement.get(i).getStr_value18());
        json.put("b_IS_AVOIR", Boolean.parseBoolean(listPreenregistrement.get(i).getStr_value19()));
        json.put("dbl_AMOUNT", dbl_AMOUNT);
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";

    
%>

<%= result%>
