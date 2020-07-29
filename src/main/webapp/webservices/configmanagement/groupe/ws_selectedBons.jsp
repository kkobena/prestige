

<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TFacture"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TGroupeTierspayant"%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>
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
 TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    int  MODE_SELECTION = 0;
    String lg_TIERS_PAYANT_ID = "", search_value = "", dt_start = date.formatterMysqlShort.format(new Date()), dt_end = dt_start;
   
    
    JSONArray listProductSelected = new JSONArray();
    JSONArray unselectedrecords = new JSONArray();
    if (request.getParameter("listProductSelected") != null && !"".equals(request.getParameter("listProductSelected"))) {
        listProductSelected = new JSONArray(request.getParameter("listProductSelected"));
    }
    if (request.getParameter("dt_start") != null && !"".equals(request.getParameter("dt_start"))) {
        dt_start = request.getParameter("dt_start");
    }
    if (request.getParameter("dt_end") != null && !"".equals(request.getParameter("dt_end"))) {
        dt_end = request.getParameter("dt_end");
    }

    if (request.getParameter("MODE_SELECTION") != null && !"".equals(request.getParameter("MODE_SELECTION"))) {
        MODE_SELECTION = new Integer(request.getParameter("MODE_SELECTION"));
    }

    
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !"".equals(request.getParameter("lg_TIERS_PAYANT_ID"))) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
    }
    if (request.getParameter("unselectedrecords") != null && !"".equals(request.getParameter("unselectedrecords"))) {
        unselectedrecords = new JSONArray(request.getParameter("unselectedrecords"));
    }

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());
    JSONObject data = new JSONObject();
    int success = 0;
    Set<TFacture> grfact= groupeCtl.generateFacture(dt_start, dt_end,unselectedrecords,listProductSelected ,lg_TIERS_PAYANT_ID,MODE_SELECTION,OTUser);   

    success = grfact.size();
    if (success > 0) {
        session.setAttribute("invoicesToPrint", grfact);
    }

    data.put("status", success);

%>

<%= data%>
