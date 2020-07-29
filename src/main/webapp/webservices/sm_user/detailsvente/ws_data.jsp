<%@page import="bll.common.Parameter"%>


<%@page import="dal.TFamille"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.TPreenregistrementDetail"%>
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
    List<TPreenregistrementDetail> lstTPreenregistrementDetail = new ArrayList<>();
    Preenregistrement OPreenregistrement = null;
    
    TUser OTUser = null;
    JSONArray arrayObj = new JSONArray();
    
%>

<%
    new logger().OCategory.info("dans ws data details vente ------------------------------------------------  >");
    String lg_PREENREGISTREMENT_ID = "", lg_PREENREGISTREMENT_DETAIL_ID = "%%", str_STATUT = "", search_value = "";
    boolean bool_UPDATE_PRICE = false;

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
//commonparameter.statut_is_Process
    OdataManager.initEntityManager();
    OPreenregistrement = new Preenregistrement(OdataManager, OTUser);

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

    if (request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID") != null && request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID") != "") {
        lg_PREENREGISTREMENT_DETAIL_ID = request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID").toString();
        new logger().OCategory.info("lg_PREENREGISTREMENT_DETAIL_ID  = " + lg_PREENREGISTREMENT_DETAIL_ID);
    }
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null && request.getParameter("lg_PREENREGISTREMENT_ID") != "") {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID").toString();
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID  " + lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT").toString();
        new logger().OCategory.info("str_STATUT  " + str_STATUT);
    }

    bool_UPDATE_PRICE = Boolean.valueOf(session.getAttribute(commonparameter.UPDATE_PRICE).toString());
    lstTPreenregistrementDetail = OPreenregistrement.getListePreenregistrementDetail(false, search_value, lg_PREENREGISTREMENT_ID, str_STATUT, start, limit);
    total = OPreenregistrement.getPreenregistrementDetailCount(search_value, lg_PREENREGISTREMENT_ID, str_STATUT);

%>


<%    for (TPreenregistrementDetail op : lstTPreenregistrementDetail) {

      JSONObject   json = new JSONObject();

        json.put("lg_PREENREGISTREMENT_DETAIL_ID", op.getLgPREENREGISTREMENTDETAILID());
        json.put("lg_PREENREGISTREMENT_ID", op.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
        json.put("str_REF", op.getLgPREENREGISTREMENTID().getStrREF());
        json.put("lg_FAMILLE_ID", op.getLgFAMILLEID().getLgFAMILLEID());
        json.put("bl_PROMOTED", op.getLgFAMILLEID().getBlPROMOTED());
        json.put("int_FAMILLE_PRICE", op.getIntPRICEUNITAIR());
        json.put("int_FREE_PACK_NUMBER", op.getIntFREEPACKNUMBER());
        json.put("str_FAMILLE_NAME", op.getLgFAMILLEID().getStrDESCRIPTION());
        json.put("lg_USER_ID", op.getLgPREENREGISTREMENTID().getLgUSERID().getStrFIRSTNAME());
        json.put("int_QUANTITY", op.getIntQUANTITY());

        json.put("int_QUANTITY_SERVED", (op.getLgPREENREGISTREMENTID().getStrSTATUT().equals(commonparameter.statut_is_Closed) ? op.getIntQUANTITYSERVED() + op.getIntAVOIR() : op.getIntQUANTITYSERVED()));
        json.put("int_PRICE_DETAIL", op.getIntPRICE());
        json.put("b_IS_AVOIR", op.getBISAVOIR());

        json.put("int_CIP", op.getLgFAMILLEID().getIntCIP());
        json.put("bool_UPDATE_PRICE", bool_UPDATE_PRICE);
        json.put("int_AVOIR", op.getIntAVOIR());
        json.put("int_EAN13", op.getLgFAMILLEID().getIntEAN13());
        json.put("dt_CREATED", date.DateToString(op.getDtCREATED(), date.formatterMysql));
        json.put("str_STATUT", op.getStrSTATUT());

        arrayObj.put(json);
    }
    JSONObject data = new JSONObject();
    data.put("total", total).put("results", arrayObj);


%>


<%= data%>
