<%@page import="dal.TMedecin"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TMedecin"%>
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
    String lg_CLIENT_ID = "%%", str_Status = "%%", by_Position = "%%", lg_ROLE_ID = "%%";
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();

    List<TMedecin> LstTMedecin = new ArrayList<TMedecin>();
%>

<%

    if (request.getParameter("lg_CLIENT_ID") != null) {
        if (request.getParameter("lg_CLIENT_ID").toString().equals("ALL")) {
            lg_CLIENT_ID = "%%";
        } else {
            lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID").toString();
        }

    }

    //  lg_ROLE_ID = session.getAttribute("lg_ROLE_ID").toString();
    new logger().OCategory.info("option " + request.getParameter("option") + " lg_CLIENT_ID " + lg_CLIENT_ID);

    LstTMedecin.clear();
    dataManager OdataManager = new dataManager();
    OdataManager.initEntityManager();
    clientManagement OclientManagement = new clientManagement(OdataManager);
    try {
        if (request.getParameter("option") != null) {
            if (request.getParameter("option").toString().equals("NOT_IN")) {
                LstTMedecin = OclientManagement.GetAllMedecingeUnAuthorize_To_Client(lg_CLIENT_ID);
            } else if (request.getParameter("option").toString().equals("IN")) {
                LstTMedecin = OclientManagement.GetAllMedecingeAuthorize_To_Client(lg_CLIENT_ID);
            } else {
            }

        }

    } catch (Exception e) {
        LstTMedecin.clear();
    }

%>
<%    JSONArray arrayObj = new JSONArray();
    int size = 0;
    try {
        for (int i = 0; i < LstTMedecin.size(); i++) {
            JSONObject json = new JSONObject();

            json.put("lg_MEDECIN_ID", LstTMedecin.get(i).getLgMEDECINID());
            json.put("str_FIRST_NAME", LstTMedecin.get(i).getStrFIRSTNAME());
            json.put("str_LAST_NAME", LstTMedecin.get(i).getStrLASTNAME());
            json.put("str_FIRST_LAST_NAME", LstTMedecin.get(i).getStrFIRSTNAME() + " " +LstTMedecin.get(i).getStrLASTNAME());
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
