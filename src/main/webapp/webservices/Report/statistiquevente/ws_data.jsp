<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>
<%@page import="java.time.LocalDate"%>
<%@page import="toolkits.utils.date"%>
<%@page import="dal.dataManager"%>
<%@page import="bll.report.StatisticSales"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%
    dataManager OManager = new dataManager();
    JSONArray data = new JSONArray();
    LocalDate now = LocalDate.now();
    String dt_start = now.minusMonths(1).toString();
    String dt_end = LocalDate.now().toString();

    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");

    }
    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    StatisticSales statisticSales = new StatisticSales(OManager);
    data = statisticSales.getSalesStatistics(dt_start, dt_end, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

    JSONObject jSONObject = new JSONObject();
    jSONObject.put("data", data);
    jSONObject.put("total", data.length());

%>

<%= jSONObject%>