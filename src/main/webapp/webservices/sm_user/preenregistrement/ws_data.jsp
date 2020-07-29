<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>


<%
    dataManager OdataManager = new dataManager();
    TUser OTUser;
    date key = new date();
    List<TPreenregistrement> lstTPreenregistrement = new ArrayList<TPreenregistrement>();
    List<TPreenregistrementDetail> lstTPreenregistrementDetail = new ArrayList<TPreenregistrementDetail>();
    JSONObject json = null;
%>



<%
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    String lg_PREENREGISTREMENT_ID = "%%", str_STATUT = "%%", search_value = "", str_TYPE_VENTE = "%%",
            str_Date_Debut = "", str_Date_Fin = "", OdateDebut = "", OdateFin = "";
    Date dt_Date_Debut, dt_Date_Fin;

    int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equalsIgnoreCase("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        new logger().OCategory.info("str_TYPE_VENTE :" + str_TYPE_VENTE);
    }
    if (str_Date_Fin.equalsIgnoreCase("") || str_Date_Fin == null) {
        dt_Date_Fin = new Date();
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    } else {
        dt_Date_Fin = key.stringToDate(str_Date_Fin, key.formatterMysqlShort);
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    }

    dt_Date_Fin = key.getDate(OdateFin, "23:59");

    if (str_Date_Debut.equalsIgnoreCase("") || str_Date_Debut == null) {
        dt_Date_Debut = new Date();
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);
    } else {
        dt_Date_Debut = key.stringToDate(str_Date_Debut, key.formatterMysqlShort);
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);

    }
    dt_Date_Debut = key.getDate(OdateDebut, "00:00");

    new logger().OCategory.info("dt_Date_Debut   " + dt_Date_Debut + " dt_Date_Fin " + dt_Date_Fin);
    OdataManager.initEntityManager();
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    lstTPreenregistrement = OPreenregistrement.getListeTPreenregistrement(search_value, lg_PREENREGISTREMENT_ID,
            str_STATUT, OTUser.getLgUSERID(),
            dt_Date_Debut, dt_Date_Fin,
            str_TYPE_VENTE, start, limit);
    total = OPreenregistrement.getListeTPreenregistrement(search_value, lg_PREENREGISTREMENT_ID, str_STATUT, OTUser.getLgUSERID(), dt_Date_Debut, dt_Date_Fin, str_TYPE_VENTE).size();


%>

<%        JSONArray arrayObj = new JSONArray();

    for (int i = 0; i < (total < limit ? total : limit); i++) {

        lstTPreenregistrementDetail = new Preenregistrement(OdataManager, OTUser).getTPreenregistrementDetail(lstTPreenregistrement.get(i).getLgPREENREGISTREMENTID());
        String str_Product = "";

        for (int k = 0; k < lstTPreenregistrementDetail.size(); k++) {
            str_Product = "<b><span style='display:inline-block;width: 7%;'>" + lstTPreenregistrementDetail.get(k).getLgFAMILLEID().getIntCIP() + "</span><span style='display:inline-block;width: 25%;'>" + lstTPreenregistrementDetail.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 10%;'>(" + lstTPreenregistrementDetail.get(k).getIntQUANTITY() + ")</span><span style='display:inline-block;width: 15%;'>" + conversion.AmountFormat(lstTPreenregistrementDetail.get(k).getIntPRICEUNITAIR(), '.') + " F CFA " + "</span></b><br> " + str_Product;
        }

        json = new JSONObject();
        new logger().OCategory.info("id vente----" + lstTPreenregistrement.get(i).getLgPREENREGISTREMENTID());
        json.put("lg_PREENREGISTREMENT_ID", lstTPreenregistrement.get(i).getLgPREENREGISTREMENTID());
        json.put("str_REF", lstTPreenregistrement.get(i).getStrREF());
        json.put("lg_USER_ID", lstTPreenregistrement.get(i).getLgUSERVENDEURID().getStrFIRSTNAME() + " " + lstTPreenregistrement.get(i).getLgUSERVENDEURID().getStrLASTNAME());
        json.put("int_PRICE", lstTPreenregistrement.get(i).getIntPRICE());
        json.put("dt_CREATED", date.DateToString(lstTPreenregistrement.get(i).getDtUPDATED(), key.formatterShort));
        json.put("str_hour", date.DateToString(lstTPreenregistrement.get(i).getDtUPDATED(), key.NomadicUiFormat_Time));
        json.put("str_STATUT", lstTPreenregistrement.get(i).getStrSTATUT());
        json.put("lg_USER_VENDEUR_ID", lstTPreenregistrement.get(i).getLgUSERCAISSIERID().getStrFIRSTNAME() + " " + lstTPreenregistrement.get(i).getLgUSERCAISSIERID().getStrLASTNAME());
        json.put("str_FAMILLE_ITEM", str_Product);
        json.put("str_TYPE_VENTE", lstTPreenregistrement.get(i).getStrTYPEVENTE());
        json.put("lg_TYPE_VENTE_ID", lstTPreenregistrement.get(i).getLgTYPEVENTEID().getLgTYPEVENTEID());
        json.put("b_IS_AVOIR", lstTPreenregistrement.get(i).getBISAVOIR());
        json.put("b_IS_CANCEL", lstTPreenregistrement.get(i).getBISCANCEL().toString());
        json.put("int_REMISE", lstTPreenregistrement.get(i).getIntPRICEREMISE());
           json.put("int_PRICE_REMISE", lstTPreenregistrement.get(i).getIntPRICEREMISE());
        
        arrayObj.put(json);

    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";


%>

<%= result%>
