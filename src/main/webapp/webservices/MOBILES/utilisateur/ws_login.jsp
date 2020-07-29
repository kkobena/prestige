<%@page import="bll.userManagement.authentification"%>
<%@page import="bll.userManagement.user"%>
<%@page import="dal.dataManager"   %>

<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.conversion"  %>

<%  dataManager OdataManager = new dataManager();
    JSONObject json = new JSONObject();
    JSONArray arrayObj = new JSONArray();
%>



<%
    String result = "";
    String str_PASSWORD = "", str_LOGIN = "", str_USER_ID = "";
    OdataManager.initEntityManager();

    authentification Oauthentification = new authentification(OdataManager);
    if ((request.getParameter("str_USER_ID") != null)) {
        str_USER_ID = request.getParameter("str_USER_ID").toString();
        new logger().OCategory.info("str_USER_ID: " + str_USER_ID);
    }
    if ((request.getParameter("str_PASSWORD") != null)) {
        str_PASSWORD = request.getParameter("str_PASSWORD").toString();
        new logger().OCategory.info("str_PASSWORD: " + str_PASSWORD);
    }
    if ((request.getParameter("str_LOGIN") != null)) {
        str_LOGIN = request.getParameter("str_LOGIN").toString();
        new logger().OCategory.info("str_LOGIN " + str_LOGIN);
    }

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("doLogin")) {
            if (Oauthentification.loginUser(str_PASSWORD, str_LOGIN)) {
                json.put("str_LOGIN", Oauthentification.getOTUser().getStrLOGIN());
                json.put("str_USER_ID", Oauthentification.getOTUser().getLgUSERID());
                json.put("str_FIRST_NAME", Oauthentification.getOTUser().getStrFIRSTNAME());
                json.put("str_LAST_NAME", Oauthentification.getOTUser().getStrLASTNAME());
                json.put("str_PHONE", Oauthentification.getOTUser().getStrPHONE() != null ? Oauthentification.getOTUser().getStrPHONE() : "");
                json.put("str_MAIL", Oauthentification.getOTUser().getStrMAIL() != null ? Oauthentification.getOTUser().getStrMAIL() : "");
            }
        } 
    }

    json.put("desc_statut", Oauthentification.getDetailmessage());
    json.put("code_statut", Oauthentification.getMessage());

    arrayObj.put(json);
    result = arrayObj.toString();

%>
<%= result%>