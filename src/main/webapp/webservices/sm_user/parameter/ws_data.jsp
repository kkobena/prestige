<%@page import="bll.utils.TparameterManager"%>
<%@page import="dal.TParameters"%>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% 
    dataManager OdataManager = new dataManager();
    TParameters OTParameters = new TParameters();
%>

<%
    int count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data parameter");
%>


<%    
    String str_KEY = "", str_VALUE = "", str_DESCRIPTION = "", search_value = "";
    if (request.getParameter("str_KEY") != null) {
        str_KEY = request.getParameter("str_KEY").toString();
        new logger().OCategory.info("str_KEY  = " + str_KEY);
    }
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value  = " + search_value);
    }
    

    OdataManager.initEntityManager();
    TparameterManager OTparameterManager = new TparameterManager(OdataManager);
    
  
    
%>



<%
    try {
         OTParameters = OTparameterManager.getParameter(str_KEY);
        str_KEY = OTParameters.getStrKEY();
        str_VALUE = OTParameters.getStrVALUE();
        str_DESCRIPTION = OTParameters.getStrDESCRIPTION();

    } catch (Exception e) {
        e.printStackTrace();
    }

    String result = "({\"success\":\"" + OTparameterManager.getMessage() + " \",\"str_KEY\":\"" + str_KEY + "\",\"str_VALUE\":\"" + str_VALUE + "\",\"str_DESCRIPTION\":\"" + str_DESCRIPTION + "\"})";
    new logger().OCategory.info("JSON " + result);
%>

<%=result%>