<%@page import="bll.stockManagement.DepotManager"%>
<%@page import="dal.TRetourdepotdetail"%>
<%@page import="bll.stockManagement.DepotManager"%>
<%@page import="dal.TRetourdepotdetail"%>
<%@page import="dal.TRetourdepotdetail"%>
<%@page import="dal.TRetourdepotdetail"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
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
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% 
    dataManager OdataManager = new dataManager();
    
    List<TRetourdepotdetail> lstTRetourdepotdetail = new ArrayList<TRetourdepotdetail>();

%>



<%
String lg_RETOURDEPOT_ID = "%%", lg_RETOURDEPOTDETAIL_ID = "%%", lg_FAMILLE_ID = "%%", lg_USER_ID = "%%", search_value = "", str_STATUT = commonparameter.statut_is_Process;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();
    new logger().OCategory.info("User connecté "+OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME());
    
    DepotManager ODepotManager = new DepotManager(OdataManager, OTUser);

    new logger().OCategory.info("dans ws data detail ajustement");

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    } 
    
    if (request.getParameter("lg_RETOURDEPOTDETAIL_ID") != null) {
        lg_RETOURDEPOTDETAIL_ID = request.getParameter("lg_RETOURDEPOTDETAIL_ID");
        new logger().OCategory.info("lg_RETOURDEPOTDETAIL_ID " + lg_RETOURDEPOTDETAIL_ID);
    } 
    
    if (request.getParameter("lg_RETOURDEPOT_ID") != null) {
        lg_RETOURDEPOT_ID = request.getParameter("lg_RETOURDEPOT_ID");
        new logger().OCategory.info("lg_RETOURDEPOT_ID " + lg_RETOURDEPOT_ID);
    } 
    
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }
    
    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }


    lstTRetourdepotdetail = ODepotManager.getTRetourdepotdetail(search_value, lg_RETOURDEPOT_ID, str_STATUT);

    JSONArray arrayObj = new JSONArray();
    for (int i = 0; i < lstTRetourdepotdetail.size(); i++) {

        try {
            OdataManager.getEm().refresh(lstTRetourdepotdetail.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();
        json.put("lg_AJUSTEMENTDETAIL_ID", lstTRetourdepotdetail.get(i).getLgRETOURDEPOTDETAILID());
        json.put("lg_RETOURDEPOT_ID", lstTRetourdepotdetail.get(i).getLgRETOURDEPOTID().getLgRETOURDEPOTID());
        json.put("int_CIP", lstTRetourdepotdetail.get(i).getLgFAMILLEID().getIntCIP());
        json.put("lg_FAMILLE_ID", lstTRetourdepotdetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("str_FAMILLE_NAME", lstTRetourdepotdetail.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("int_QUANTITY", lstTRetourdepotdetail.get(i).getIntNUMBERRETURN());
        json.put("int_S", lstTRetourdepotdetail.get(i).getLgFAMILLEID().getIntPRICE());
        json.put("int_QUANTITY_SERVED", lstTRetourdepotdetail.get(i).getIntSTOCK());
       
        json.put("int_PAF", lstTRetourdepotdetail.get(i).getLgFAMILLEID().getIntPAF());
        json.put("int_PAT", lstTRetourdepotdetail.get(i).getLgFAMILLEID().getIntPAT());
        json.put("int_EAN13", lstTRetourdepotdetail.get(i).getLgFAMILLEID().getIntEAN13());
        json.put("str_STATUT", lstTRetourdepotdetail.get(i).getStrSTATUT());
        
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTRetourdepotdetail.size() + " \",\"results\":" + arrayObj.toString() + "})";


%>

<%= result%>
