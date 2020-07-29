<%@page import="toolkits.utils.jdom"%>
<%@page import="bll.stockManagement.AjustementManagement"%>
<%@page import="dal.TAjustementDetail"%>
<%@page import="dal.TAjustementDetail"%>
<%@page import="dal.TAjustementDetail"%>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();
    List<TAjustementDetail> lstTAjustementDetail = new ArrayList<TAjustementDetail>();
    JSONArray arrayObj = new JSONArray();
    JSONObject json = null;
    TUser OTUser = null;
%>



<%    String lg_AJUSTEMENT_ID = "%%", lg_AJUSTEMENTDETAIL_ID = "%%", lg_FAMILLE_ID = "%%", lg_USER_ID = "%%", search_value = "";

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
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
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_AJUSTEMENTDETAIL_ID") != null) {
        lg_AJUSTEMENTDETAIL_ID = request.getParameter("lg_AJUSTEMENTDETAIL_ID");
        new logger().OCategory.info("lg_AJUSTEMENTDETAIL_ID " + lg_AJUSTEMENTDETAIL_ID);
    }

    if (request.getParameter("lg_AJUSTEMENT_ID") != null) {
        lg_AJUSTEMENT_ID = request.getParameter("lg_AJUSTEMENT_ID");
        new logger().OCategory.info("lg_AJUSTEMENT_ID " + lg_AJUSTEMENT_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    OdataManager.initEntityManager();
    AjustementManagement OAjustementManagement = new AjustementManagement(OdataManager, OTUser);
    lstTAjustementDetail = OAjustementManagement.SearchAllOrOneAjustementDetail(search_value, lg_AJUSTEMENT_ID, lg_USER_ID, lg_FAMILLE_ID, start, limit);
    total = OAjustementManagement.SearchAllOrOneAjustementDetail(search_value, lg_AJUSTEMENT_ID, lg_USER_ID, lg_FAMILLE_ID).size();
%>

<%
    for (int i = 0; i < (lstTAjustementDetail.size() < limit ? lstTAjustementDetail.size() : limit); i++) {

        json = new JSONObject();
        json.put("lg_AJUSTEMENTDETAIL_ID", lstTAjustementDetail.get(i).getLgAJUSTEMENTDETAILID());
        json.put("lg_AJUSTEMENT_ID", lstTAjustementDetail.get(i).getLgAJUSTEMENTID().getLgAJUSTEMENTID());
        json.put("int_CIP", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntCIP());
        json.put("lg_FAMILLE_ID", lstTAjustementDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        //json.put("int_FAMILLE_PRICE", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntPRICE());
        try {
            json.put("int_FAMILLE_PRICE", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntPAF());
            json.put("int_PRICE_DETAIL", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntPAF()* lstTAjustementDetail.get(i).getIntNUMBER());

        } catch (Exception e) {

        }
        json.put("str_FAMILLE_NAME", lstTAjustementDetail.get(i).getLgFAMILLEID().getStrNAME());
        json.put("int_QUANTITY", lstTAjustementDetail.get(i).getIntNUMBER());
        json.put("int_S", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntPRICE());
        //json.put("int_T", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntT());
        json.put("int_T", lstTAjustementDetail.get(i).getIntNUMBERAFTERSTOCK());
        json.put("int_QUANTITY_SERVED", lstTAjustementDetail.get(i).getIntNUMBERCURRENTSTOCK());

        json.put("int_PAF", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntPAF());
        json.put("int_PAT", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntPAT());
        json.put("int_EAN13", lstTAjustementDetail.get(i).getLgFAMILLEID().getIntEAN13());
        //json.put("dt_CREATED", date.DateToString(lstTAjustementDetail.get(i).getDtCREATED(), key.formatterMysql));
        json.put("str_STATUT", lstTAjustementDetail.get(i).getStrSTATUT());

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result:"+result);

%>

<%= result%>
