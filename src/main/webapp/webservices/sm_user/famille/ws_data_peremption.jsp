<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>
<%@page import="dal.TTiersPayant"%>
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

    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());
    String search_value = "",
            dt_obsolete="",
            perimes="",
            cmbobsolete="";
    
    if (request.getParameter("dt_obsolete") != null && !"".equals(request.getParameter("dt_obsolete"))) {
        dt_obsolete = request.getParameter("dt_obsolete");
    }if (request.getParameter("perimes") != null && !"".equals(request.getParameter("perimes"))) {
        perimes = request.getParameter("perimes");
    }if (request.getParameter("cmbobsolete") != null && !"".equals(request.getParameter("cmbobsolete"))) {
        cmbobsolete = request.getParameter("cmbobsolete");
    }
    
    
    
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    String empl = OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID();
    JSONArray arrayObj = groupeCtl.getObseleteProducts(false, search_value, dt_obsolete, perimes, cmbobsolete, empl, start, limit);
    int count = groupeCtl.getObseleteProductsCount(search_value, dt_obsolete, perimes, cmbobsolete, empl);

    JSONObject data = new JSONObject();

    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>