<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRolePrivelege"  %>
<%@page import="dal.TPrivilege"  %>
<%@page import="java.util.*"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="dal.jconnexion"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>
<%@page import="java.sql.ResultSetMetaData"  %>



<%!    Translate oTranslate = new Translate();
    String lg_COMPTE_CLIENT_ID = "%%", str_Status = "%%", by_Position = "%%", lg_ROLE_ID = "%%";
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();

    List<TTiersPayant> LstTTiersPayant = new ArrayList<TTiersPayant>();
%>

<%

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        if (request.getParameter("lg_COMPTE_CLIENT_ID").toString().equals("ALL")) {
            lg_COMPTE_CLIENT_ID = "%%";
        } else {
            lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID").toString();
        }

    }

    //  lg_ROLE_ID = session.getAttribute("lg_ROLE_ID").toString();
    new logger().OCategory.info("option " + request.getParameter("option") + " lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);

    LstTTiersPayant.clear();
    dataManager OdataManager = new dataManager();
    OdataManager.initEntityManager();
    clientManagement OclientManagement = new clientManagement(OdataManager);
    try {
        if (request.getParameter("option") != null) {
            if (request.getParameter("option").toString().equals("NOT_IN")) {
                LstTTiersPayant = OclientManagement.GetAllTierspayantgeUnAuthorize_To_Client(lg_COMPTE_CLIENT_ID);
            } else if (request.getParameter("option").toString().equals("IN")) {
                LstTTiersPayant = OclientManagement.GetAllTierspayantgeAuthorize_To_Client(lg_COMPTE_CLIENT_ID);
            } else {
            }

        }

    } catch (Exception e) {
        LstTTiersPayant.clear();
    }

%>
<%    JSONArray arrayObj = new JSONArray();
    int size = 0;
    try {
        for (int i = 0; i < LstTTiersPayant.size(); i++) {
            JSONObject json = new JSONObject();

            json.put("lg_TIERS_PAYANT_ID", LstTTiersPayant.get(i).getLgTIERSPAYANTID());
            json.put("str_NAME", LstTTiersPayant.get(i).getStrNAME());
            json.put("str_CODE_ORGANISME", LstTTiersPayant.get(i).getStrCODEORGANISME());

            arrayObj.put(json);
            size++;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>


<%
            //String result = "({\"total\":\"" + size + " \",\"results\":" + arrayObj.toString() + "})";
    System.out.println(arrayObj.toString());
%>

<%= arrayObj.toString()%>
