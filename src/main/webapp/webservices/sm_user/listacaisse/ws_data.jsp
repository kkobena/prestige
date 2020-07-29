<%@page import="bll.utils.TparameterManager"%>
<%@page import="dal.TParameters"%>
<%@page import="dal.TMvtCaisse"%>
<%@page import="bll.report.JournalVente"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="toolkits.utils.conversion"%>
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

<%
    dataManager OdataManager = new dataManager();
    TMvtCaisse OTMvtCaisse = null;
    List<Object[]> listPreenregistrement = new ArrayList<Object[]>(), listPreenregistrementOther = new ArrayList<Object[]>();
    List<EntityData> listTMvtCaissesFalse = new ArrayList<EntityData>();
    JSONObject json = null;
    TParameters OTParameters = null;
    Object[] OObject = null;
%>


<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    Date today = new Date();
    String str_Date_Debut = date.DateToString(today, date.formatterMysqlShort), str_Date_Fin = date.DateToString(today, date.formatterMysqlShort), search_value = "",
            h_debut = "00:00", h_fin = "23:59", lg_USER_ID = "%%", lg_TYPE_REGLEMENT_ID = "%%";
    int int_PRICE_TOTAL = 0;
    Double P_SORTIECAISSE_ESPECE_FALSE = 0d;

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

    if (request.getParameter("lg_USER_ID") != null && !request.getParameter("lg_USER_ID").equalsIgnoreCase("")) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID :" + lg_USER_ID);
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

    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null && !request.getParameter("lg_TYPE_REGLEMENT_ID").equalsIgnoreCase("")) {
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");
        new logger().OCategory.info("lg_TYPE_REGLEMENT_ID :" + lg_TYPE_REGLEMENT_ID);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    JournalVente OJournalVente = new JournalVente(OdataManager, OTUser);
    TparameterManager OTparameterManager = new TparameterManager(OdataManager);
    OTParameters = OTparameterManager.getParameter(Parameter.KEY_MOVEMENT_FALSE);
    if (OTParameters != null && Integer.parseInt(OTParameters.getStrVALUE()) == 1 && !str_Date_Debut.equalsIgnoreCase(str_Date_Fin)) {
        listTMvtCaissesFalse = OJournalVente.getAllMouvmentsCaisse(str_Date_Debut, str_Date_Fin, false);
    }
    for (EntityData Odata : listTMvtCaissesFalse) {
        P_SORTIECAISSE_ESPECE_FALSE += (-1) * Double.valueOf(Odata.getStr_value1());
    }
    new logger().OCategory.info("P_SORTIECAISSE_ESPECE_FALSE:" + P_SORTIECAISSE_ESPECE_FALSE);

    listPreenregistrement = OJournalVente.getListeCaisse(str_Date_Debut, str_Date_Fin, h_debut, h_fin, lg_USER_ID, lg_TYPE_REGLEMENT_ID, start, limit);
    listPreenregistrementOther = OJournalVente.getListeCaisse(str_Date_Debut, str_Date_Fin, h_debut, h_fin, lg_USER_ID, lg_TYPE_REGLEMENT_ID);
    int_PRICE_TOTAL = OJournalVente.getTotalAmountCashTransaction(listPreenregistrementOther);

    //code ajouté 25/08/2016
    int_PRICE_TOTAL = int_PRICE_TOTAL + P_SORTIECAISSE_ESPECE_FALSE.intValue();
    total = listPreenregistrementOther.size();
%>


<%    JSONArray arrayObj = new JSONArray();
   // for(int i = 0; i < (total < limit ? total : limit); i++) {
    for (int i = 0; i < (listPreenregistrement.size() < limit ? listPreenregistrement.size() : limit); i++) {
        json = new JSONObject();
        OObject = listPreenregistrement.get(i);
        json.put("str_ref", OObject[2]);
        json.put("str_vendeur", OObject[8]);
        json.put("str_date", date.DateToString(date.stringToDate(OObject[11].toString(), date.formatterMysql), date.formatterShort));
        json.put("str_hour", date.DateToString(date.stringToDate(OObject[11].toString(), date.formatterMysql), date.NomadicUiFormat_Time));
        json.put("str_mt_vente", conversion.AmountFormat(Integer.parseInt(OTParameters != null && Integer.parseInt(OTParameters.getStrVALUE()) == 1 ? OObject[6].toString() : OObject[5].toString()), '.'));
        json.put("str_TRANSACTION_REF", OObject[14]);
        json.put("str_FAMILLE_ITEM", OObject[13]);
        json.put("int_PRICE", OTParameters != null && Integer.parseInt(OTParameters.getStrVALUE()) == 1 ? OObject[6].toString() : OObject[5].toString());
        json.put("str_client_infos", OObject[15]);
        json.put("str_mt_clt", OObject[16]);

        json.put("int_PRICE_TOTAL", int_PRICE_TOTAL);

        try {
            OTMvtCaisse = OdataManager.getEm().find(TMvtCaisse.class, OObject[2]);
            if (OTMvtCaisse != null) {
                json.put("lg_MVT_CAISSE_ID", OTMvtCaisse.getLgMVTCAISSEID());
                json.put("lg_TYPE_MVT_CAISSE_ID", OTMvtCaisse.getLgTYPEMVTCAISSEID().getStrNAME());
                json.put("str_NUM_COMPTE", OTMvtCaisse.getStrNUMCOMPTE());
                //json.put("str_NUM_PIECE_COMPTABLE", OTMvtCaisse.getStrNUMPIECECOMPTABLE());
                json.put("str_NUM_PIECE_COMPTABLE", OTMvtCaisse.getLgMVTCAISSEID());
                json.put("lg_MODE_REGLEMENT_ID", OTMvtCaisse.getLgMODEREGLEMENTID().getStrNAME());
                json.put("int_AMOUNT", OTMvtCaisse.getIntAMOUNT());
                json.put("dt_DATE_MVT", date.DateToString(OTMvtCaisse.getDtDATEMVT(), date.formatterShort));
            }
        } catch (Exception e) {
        }

        //new logger().OCategory.info("int_PRICE_TOTAL -----" + int_PRICE_TOTAL);
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";

    System.out.println("result:====>" + result);
%>

<%= result%>
