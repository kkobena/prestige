
<%@page import="bll.configManagement.GroupeTierspayantController"%>
<%@page import="dal.dataManager"  %>
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

    dataManager OdataManager = new dataManager();
    OdataManager.initEntityManager();

    GroupeTierspayantController controller = new GroupeTierspayantController(OdataManager.getEmf());
   JSONArray arra= controller.getListeTSnapshotFamillesell(request.getParameter("lg_FAMILLE_ID"));
   JSONObject data=new JSONObject();
   data.put("results", arra).put("total", arra.length());
%>
<%= data%>