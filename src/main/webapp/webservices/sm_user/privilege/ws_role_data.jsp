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
    String lg_ROLE_PRIVILEGE = "%%", str_Status = "%%", by_Position = "%%", lg_ROLE_ID = "%%";
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    privilege Oprivilege = new privilege();
    List<TPrivilege> LstTPrivilege = new ArrayList<TPrivilege>();
%>

<%

            if (request.getParameter("lg_ROLE_PRIVILEGE") != null) {
                if (request.getParameter("lg_ROLE_PRIVILEGE").toString().equals("ALL")) {
                    lg_ROLE_PRIVILEGE = "%%";
                } else {
                    lg_ROLE_PRIVILEGE = request.getParameter("lg_ROLE_PRIVILEGE").toString();
                }

            }

            if (request.getParameter("lg_ROLE_ID") != null) {
                if (request.getParameter("lg_ROLE_ID").toString().equals("ALL")) {
                    lg_ROLE_ID = "%%";
                } else {
                    lg_ROLE_ID = request.getParameter("lg_ROLE_ID").toString();
                }

            }
   
          //  lg_ROLE_ID = session.getAttribute("lg_ROLE_ID").toString();
            new logger().OCategory.info(request.getParameter("option"));

            LstTPrivilege.clear();


            try {
                if (request.getParameter("option") != null) {
                    if (request.getParameter("option").toString().equals("NOT_IN")) {
                        LstTPrivilege = Oprivilege.GetAllPrivilegeUnAuthorize_To_Role(lg_ROLE_ID);
                    } else if (request.getParameter("option").toString().equals("IN")) {
                        LstTPrivilege = Oprivilege.GetAllPrivilegeAuthorize_To_Role(lg_ROLE_ID);
                    } else {
                    }
                
                }

            } catch (Exception e) {
                LstTPrivilege.clear();
            }

%>
<%
            JSONArray arrayObj = new JSONArray();
            int size = 0;
            try {
                for (int i = 0; i < LstTPrivilege.size(); i++) {
                    JSONObject json = new JSONObject();

                json.put("lg_PRIVELEGE_ID", LstTPrivilege.get(i).getLgPRIVELEGEID());
                json.put("str_NAME", LstTPrivilege.get(i).getStrNAME());
                json.put("str_DESCRIPTION", LstTPrivilege.get(i).getStrDESCRIPTION());

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
