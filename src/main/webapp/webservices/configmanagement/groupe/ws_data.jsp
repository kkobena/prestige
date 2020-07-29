<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="dal.TGroupeTierspayant"%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>

<%@page import="java.util.Date"%>
<%@page import="toolkits.utils.date"%>

<%@page import="dal.dataManager"%>


<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%@page import="java.util.ArrayList"%>

<%@page import="java.util.List"%>

<%
    dataManager OdataManager = new dataManager();
    String search_value = "%%";
OdataManager.initEntityManager();
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    JSONArray arrayObj = new JSONArray();

    List<TGroupeTierspayant> list = groupeCtl.findTGroupeTierspayantEntities(limit, start, search_value);
    int count = groupeCtl.getTGroupeTierspayantCount(search_value);
    for (TGroupeTierspayant obj : list) {
        JSONObject json = new JSONObject();
        json.put("lg_GROUPE_ID", obj.getLgGROUPEID()).put("str_LIBELLE", obj.getStrLIBELLE()).put("str_ADRESSE", obj.getStrADRESSE()).put("str_TELEPHONE", obj.getStrTELEPHONE());
        arrayObj.put(json);

    }

    JSONObject data = new JSONObject();

    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>