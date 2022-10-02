

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

    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
       TUser user=OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    int lg_GROUPE_ID = 0, MODE_SELECTION = 0;
    String  dt_start = date.formatterMysqlShort.format(new Date()), dt_end = dt_start;
   
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
        MODE_SELECTION =  Integer.valueOf(request.getParameter("MODE_SELECTION"));
    }

    if (request.getParameter("lg_GROUPE_ID") != null && !"".equals(request.getParameter("lg_GROUPE_ID"))) {
        lg_GROUPE_ID =  Integer.valueOf(request.getParameter("lg_GROUPE_ID"));
    }
    if (request.getParameter("unselectedrecords") != null && !"".equals(request.getParameter("unselectedrecords"))) {
        unselectedrecords = new JSONArray(request.getParameter("unselectedrecords"));
    }
    if (request.getParameter("tierspayantarray") != null && !"".equals(request.getParameter("tierspayantarray"))) {
        tierspayantarray = new JSONArray(request.getParameter("tierspayantarray"));
    }

    
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());
    JSONObject data = new JSONObject();
    int success = 0;
    Map<String, LinkedHashSet<TFacture>> grfact = groupeCtl.generateGroupeFacture(dt_start, dt_end, lg_GROUPE_ID, listProductSelected, unselectedrecords,  MODE_SELECTION,user);

    success = grfact.size();
    if (success > 0) {
        session.setAttribute("groupeinvoicesToPrint", grfact);
    }

    data.put("status", success);

%>

<%= data%>
