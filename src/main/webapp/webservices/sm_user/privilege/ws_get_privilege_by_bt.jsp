<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>
<%@page import="dal.TPrivilege"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="dal.TSousMenu"  %>




<%  Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    privilege Oprivilege = new privilege();
    

%>

<%

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info(" ws tree_menu TUser  " + OTUser.getStrLOGIN());

    OdataManager.initEntityManager();
    Oprivilege.LoadDataManger(OdataManager);
    Oprivilege.LoadMultilange(OTranslate);
    Oprivilege.setOTUser(OTUser);
    List<TPrivilege> lstTPrivilege = Oprivilege.GetAllPrivilege(OTUser, "P_BT");

    JSONArray arrayObj = new JSONArray();

    for (int i = 0; i < lstTPrivilege.size(); i++) {
        try {
            OdataManager.getEm().refresh(lstTPrivilege.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_PRIVELEGE_ID", lstTPrivilege.get(i).getLgPRIVELEGEID());
        json.put("str_NAME", lstTPrivilege.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTPrivilege.get(i).getStrDESCRIPTION());

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTPrivilege.size() + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>