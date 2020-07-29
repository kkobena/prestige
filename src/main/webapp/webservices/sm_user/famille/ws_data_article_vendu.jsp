<%@page import="bll.common.Parameter"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="bll.entity.EntityData"%>
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
    JSONObject json = null;
    dataManager OdataManager = new dataManager();

%>


<%    new logger().OCategory.info("dans ws data liste des articles vendus");
    String str_Date_Debut = date.DateToString(new Date(), date.formatterMysqlShort), str_Date_Fin = str_Date_Debut,
            h_debut = "", h_fin = "", search_value = "", str_TYPE_TRANSACTION = "", lg_FAMILLE_ID = "", prixachatFiltre = "TOUT", stockFiltre = "TOUT";

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    int int_NUMBER = 0, stock = Integer.MIN_VALUE;

    int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");

    }
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").trim();
        new logger().OCategory.info("search_value :" + search_value);
    }

    if (request.getParameter("str_TYPE_TRANSACTION") != null) {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
    }
    if (request.getParameter("int_NUMBER") != null && !request.getParameter("int_NUMBER").equalsIgnoreCase("")) {
        int_NUMBER = Integer.parseInt(request.getParameter("int_NUMBER"));
        new logger().OCategory.info("int_NUMBER " + int_NUMBER);
    }

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        new logger().OCategory.info("h_debut :" + h_debut);
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");

    }
    if (request.getParameter("prixachatFiltre") != null) {
        prixachatFiltre = request.getParameter("prixachatFiltre");
    }
    if (request.getParameter("stockFiltre") != null) {
        stockFiltre = request.getParameter("stockFiltre");
    }
    if (request.getParameter("stock") != null) {
        try {
            stock = Integer.parseInt(request.getParameter("stock"));
        } catch (Exception e) {
        }

    }

    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    json = OPreenregistrement.getArticlesVendus(search_value, false, str_Date_Debut,
            str_Date_Fin, h_debut, h_fin,
            str_TYPE_TRANSACTION,
            int_NUMBER, start, limit, lg_FAMILLE_ID, prixachatFiltre, stock, stockFiltre);
    total = OPreenregistrement.getArticlesVendusCount(search_value,
            str_Date_Debut, str_Date_Fin,
            h_debut, h_fin,
            str_TYPE_TRANSACTION,
            int_NUMBER, lg_FAMILLE_ID, prixachatFiltre, stock, stockFiltre);
    json.put("total", total);
%>
<%= json%>