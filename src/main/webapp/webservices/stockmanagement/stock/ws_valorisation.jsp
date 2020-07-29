<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
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
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());

   // int year = new Integer(date.FORMATTERYEAR.format(new Date()));
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    String str_BEGIN = "", str_END = "";
    String lg_ZONE_GEO_ID = "", lg_FAMILLEARTICLE_ID = "", lg_GROSSISTE_ID = "", datep = "";
    int mode = 0;
    if (request.getParameter("date") != null) {
        datep = request.getParameter("date");
    }
    if (request.getParameter("str_END") != null) {
        str_END = request.getParameter("str_END");
    }
    if (request.getParameter("str_BEGIN") != null) {
        str_BEGIN = request.getParameter("str_BEGIN");
    }

    if (request.getParameter("lg_ZONE_GEO_ID") != null && !"".equals(request.getParameter("lg_ZONE_GEO_ID")) && !request.getParameter("lg_ZONE_GEO_ID").equalsIgnoreCase("Sectionner un emplacement...") && !"0".equals(request.getParameter("lg_ZONE_GEO_ID")) && !"%%".equals(request.getParameter("lg_ZONE_GEO_ID"))) {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");

    }
    if (request.getParameter("lg_FAMILLEARTICLE_ID") != null && !request.getParameter("lg_FAMILLEARTICLE_ID").equalsIgnoreCase("") && !request.getParameter("lg_FAMILLEARTICLE_ID").equalsIgnoreCase("Sectionner une famille article...") && !"0".equals(request.getParameter("lg_FAMILLEARTICLE_ID")) && !"%%".equals(request.getParameter("lg_FAMILLEARTICLE_ID"))) {
        lg_FAMILLEARTICLE_ID = request.getParameter("lg_FAMILLEARTICLE_ID");

    }
    if (request.getParameter("lg_GROSSISTE_ID") != null && !request.getParameter("lg_GROSSISTE_ID").equalsIgnoreCase("") && !request.getParameter("lg_GROSSISTE_ID").equalsIgnoreCase("Sectionner un grossiste...") && !"0".equals(request.getParameter("lg_GROSSISTE_ID")) && !"%%".equals(request.getParameter("lg_GROSSISTE_ID"))) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");

    }
    if (request.getParameter("mode") != null) {
        mode = new Integer(request.getParameter("mode"));

    }
    if(mode==1 && !"".equals(str_END) ){
       str_END="P"+str_END;//a ne pas modifier 
    }
    
    JSONObject data = groupeCtl.get(mode, datep, lg_GROSSISTE_ID, lg_FAMILLEARTICLE_ID, lg_ZONE_GEO_ID, str_BEGIN, str_END,OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

    data.put("str_NAME_USER", OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME());
    data.put("CREATED_BY", OTUser.getLgUSERID());
    data.put("dt_CREATED", date.formatterMysql2.format(new Date()));


%>

<%= data%>