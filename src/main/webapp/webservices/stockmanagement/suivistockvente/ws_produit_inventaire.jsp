
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.Date"%>
<%@page import="dal.dataManager"%>
<%@page import="bll.teller.SnapshotManager"%>
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
   
    TUser OTUser;

    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
   
    Date today = new Date();
    String lg_FAMILLE_ID = "";
    String str_Date_Debut = LocalDate.now().toString(), str_Date_Fin = str_Date_Debut; 
if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        str_Date_Debut = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        str_Date_Fin = request.getParameter("datefin");
    }
 if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }
    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
     TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, user);

   
    JSONArray arrayObj = OSnapshotManager.suiviMvtArticleInventaire(str_Date_Debut, str_Date_Fin, lg_FAMILLE_ID);
      JSONObject data = new JSONObject();

    data.put("results", arrayObj);
    data.put("total", arrayObj.length());
%> 


<%= data%>