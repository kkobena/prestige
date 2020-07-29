<%-- 
    Document   : ws_datas
    Created on : 18 sept. 2017, 09:54:36
    Author     : KKOFFI
--%>



<%@page import="bll.configManagement.GroupeTierspayantController"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>


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
    String dt_start = date.formatterMysqlShort.format(new Date()), dt_end = dt_start;
   

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        dt_start = request.getParameter("dt_Date_Debut");

    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        dt_end = request.getParameter("dt_Date_Fin");

    }
    String empl = OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID();
   
    JSONArray arrayObj = groupeCtl.getMVTClient(dt_start, dt_end, empl);
  
    JSONObject data = new JSONObject();
   

    data.put("data", arrayObj);
    data.put("total", arrayObj.length());
%>

<%= data%>