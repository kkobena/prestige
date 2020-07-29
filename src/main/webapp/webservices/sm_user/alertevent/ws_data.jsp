<%@page import="dal.dataManager"  %>
<%@page import="dal.TAlertEvent"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TAlertEvent"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String  str_IDS = "%%", strLASTNAME = "%%", strFIRSTNAME = "%%", strEvent = "%%", lg_ROLE_ID = "", str_Event = "%%", lg_SKIN_ID = "%%", lg_Language_ID = "%%", str_PASSWORD = "%%";
    date key = new date();
    date str_LAST_CONNECTION_DATE;
    privilege Oprivilege = new privilege();
    json Ojson = new json();
    List<TAlertEvent> lstTAlertEvent = new ArrayList<TAlertEvent>();

%>



<%

    new logger().OCategory.info("dans ws data utilisateur");

    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }
    new logger().OCategory.info("search_value  = " + request.getParameter("search_value"));

    if (request.getParameter("str_Event") != null) {
        if (request.getParameter("str_Event").toString().equals("ALL")) {
            str_Event = "%%";
        } else {
            str_Event = request.getParameter("str_Event").toString();
        }

    }

    OdataManager.initEntityManager();
    lstTAlertEvent = OdataManager.getEm().createQuery("SELECT t FROM TAlertEvent t WHERE t.strEvent LIKE ?1 AND t.strTYPE LIKE ?2")
            .setParameter(1, strEvent)
             .setParameter(2, "system")
            .getResultList();

    JSONArray arrayObj = new JSONArray();
    for (int i = 0; i < lstTAlertEvent.size(); i++) {

        String Role = "";

        try {
            OdataManager.getEm().refresh(lstTAlertEvent.get(i));

            
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();
        json.put("str_Event", lstTAlertEvent.get(i).getStrEvent());
        json.put("str_SMS_English_Text", lstTAlertEvent.get(i).getStrSMSEnglishText());
        json.put("str_SMS_French_Text", lstTAlertEvent.get(i).getStrSMSFrenchText());
        json.put("str_MAIL_French_Text", lstTAlertEvent.get(i).getStrMAILFrenchText());
        json.put("str_MAIL_English_Text", lstTAlertEvent.get(i).getStrMAILEnglishText() );
        json.put("str_DESCRIPTION", lstTAlertEvent.get(i).getStrDESCRIPTION());
        json.put("str_FONCTION", lstTAlertEvent.get(i).getStrFONCTION());
        json.put("str_TYPE", lstTAlertEvent.get(i).getStrTYPE());
       
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTAlertEvent.size() + " \",\"results\":" + arrayObj.toString() + "})";


%>

<%= result%> 
