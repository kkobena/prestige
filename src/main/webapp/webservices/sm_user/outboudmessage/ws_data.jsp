<%@page import="bll.gateway.outService.ServicesUpdatePriceFamille"%>
<%@page import="dal.TUser"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TOutboudMessage"  %>
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
<%@page import="dal.TOutboudMessage"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />

<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
List<TOutboudMessage> lstTOutboudMessage = new ArrayList<TOutboudMessage>();
%>
<%

     String lg_OUTBOUND_MESSAGE_ID = "%%", search_value = "";
    if (request.getParameter("lg_OUTBOUND_MESSAGE_ID") != null) {
        lg_OUTBOUND_MESSAGE_ID = request.getParameter("lg_OUTBOUND_MESSAGE_ID");
        new logger().OCategory.info("lg_OUTBOUND_MESSAGE_ID " + lg_OUTBOUND_MESSAGE_ID);
    }
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().oCategory.info("search_value : " + search_value);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    ServicesUpdatePriceFamille OServicesUpdatePriceFamille = new ServicesUpdatePriceFamille(OdataManager, OTUser);

    lstTOutboudMessage = OServicesUpdatePriceFamille.getListeMessageWaiting(search_value, lg_OUTBOUND_MESSAGE_ID, 0);

    JSONArray arrayObj = new JSONArray();
    for (int i = 0; i < lstTOutboudMessage.size(); i++) {
        JSONObject json = new JSONObject();
        json.put("lg_OUTBOUND_MESSAGE_ID", lstTOutboudMessage.get(i).getLgOUTBOUNDMESSAGEID());
        json.put("str_MESSAGE", lstTOutboudMessage.get(i).getStrMESSAGE());
        json.put("str_STATUT",oTranslate.getValue(lstTOutboudMessage.get(i).getStrSTATUT()));
        json.put("etat",lstTOutboudMessage.get(i).getStrSTATUT());
        json.put("str_PHONE", lstTOutboudMessage.get(i).getStrPHONE());
        json.put("dt_CREATED", key.DateToString(lstTOutboudMessage.get(i).getDtCREATED(), key.formatterShort)   );
        json.put("dt_UPDATED", key.DateToString(lstTOutboudMessage.get(i).getDtUPDATED(), key.formatterShort));

       
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTOutboudMessage.size() + " \",\"results\":" + arrayObj.toString() + "})";


%>

<%= result%> 
