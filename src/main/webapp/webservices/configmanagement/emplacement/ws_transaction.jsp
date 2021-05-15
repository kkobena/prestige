<%@page import="bll.common.Parameter"%>
<%@page import="dal.TCompteClient"%>
<%@page import="dal.TCompteClient"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="bll.configManagement.EmplacementManagement"%>
<%@page import="java.io.File"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="java.io.File"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="org.apache.commons.fileupload.DiskFileUpload"%>
<%@page import="org.apache.commons.fileupload.FileUpload"%>
<%@page import="java.io.File"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TOptimisationQuantite"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>
<%@page import="bll.configManagement.familleManagement"  %>

<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
%>



<%
 String lg_EMPLACEMENT_ID = "", str_DESCRIPTION = "", str_NAME = "", str_LOCALITE = "", str_FIRST_NAME = "",
            str_LAST_NAME = "", str_PHONE = "", lg_TYPE_CLIENT_ID = "5", str_SEXE = "M", lg_TYPEDEPOT_ID = Parameter.DEFAUL_EMPLACEMENT;
    boolean bool_SAME_LOCATION=false;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
   TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");

    if (request.getParameter("lg_EMPLACEMENT_ID") != null) {
        lg_EMPLACEMENT_ID = request.getParameter("lg_EMPLACEMENT_ID");
        new logger().OCategory.info("lg_EMPLACEMENT_ID " + lg_EMPLACEMENT_ID);
    }
    
     if (request.getParameter("lg_TYPEDEPOT_ID") != null && !request.getParameter("lg_TYPEDEPOT_ID").equalsIgnoreCase("")) {
        lg_TYPEDEPOT_ID = request.getParameter("lg_TYPEDEPOT_ID");
        new logger().OCategory.info("lg_TYPEDEPOT_ID " + lg_TYPEDEPOT_ID);
    }

    
    
    if (request.getParameter("lg_TYPE_CLIENT_ID") != null) {
        lg_TYPE_CLIENT_ID = request.getParameter("lg_TYPE_CLIENT_ID");
        new logger().OCategory.info("lg_TYPE_CLIENT_ID " + lg_TYPE_CLIENT_ID);
    }

    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
        new logger().OCategory.info("str_NAME " + str_NAME);
    }

    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
        new logger().OCategory.info("str_DESCRIPTION " + str_DESCRIPTION);
    }

    if (request.getParameter("str_PHONE") != null) {
        str_PHONE = request.getParameter("str_PHONE");
        new logger().OCategory.info("str_PHONE " + str_PHONE);
    }

    if (request.getParameter("str_LOCALITE") != null) {
        str_LOCALITE = request.getParameter("str_LOCALITE");
        new logger().OCategory.info("str_LOCALITE " + str_LOCALITE);
    }

    if (request.getParameter("str_FIRST_NAME") != null) {
        str_FIRST_NAME = request.getParameter("str_FIRST_NAME");
        new logger().OCategory.info("str_FIRST_NAME " + str_FIRST_NAME);
    }
     if (request.getParameter("bool_SAME_LOCATION") != null) {
        bool_SAME_LOCATION =  Boolean.valueOf(request.getParameter("bool_SAME_LOCATION")) ;
     
    }

    if (request.getParameter("str_LAST_NAME") != null) {
        str_LAST_NAME = request.getParameter("str_LAST_NAME");
        new logger().OCategory.info("str_LAST_NAME " + str_LAST_NAME);
    }

    if (request.getParameter("str_SEXE") != null) {
        str_SEXE = request.getParameter("str_SEXE");
        new logger().OCategory.info("str_SEXE " + str_SEXE);
    }

    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    EmplacementManagement OEmplacementManagement = new EmplacementManagement(OdataManager, user);
    clientManagement OclientManagement = new clientManagement(OdataManager, user);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            try {
                
                TCompteClient OTCompteClient = OclientManagement.createClient(str_FIRST_NAME, str_LAST_NAME, str_SEXE, str_LOCALITE, str_LOCALITE, "", "", "", "", 0.0, 0.0, 0, lg_TYPE_CLIENT_ID);
                if (OTCompteClient != null) {
                    OEmplacementManagement.createEmplacement(str_NAME, str_DESCRIPTION, str_LOCALITE, str_FIRST_NAME, str_LAST_NAME, str_PHONE, OTCompteClient, lg_TYPEDEPOT_ID,bool_SAME_LOCATION);
                    ObllBase.setDetailmessage(OEmplacementManagement.getDetailmessage());
                    ObllBase.setMessage(OEmplacementManagement.getMessage());
                } 

            } catch (Exception e) {
                ObllBase.buildErrorTraceMessage("Impossible de créer cet emplacement");
            }

        } else if (request.getParameter("mode").toString().equals("update")) {
            OEmplacementManagement.updateEmplacement(lg_EMPLACEMENT_ID, str_NAME, str_DESCRIPTION, str_LOCALITE, str_FIRST_NAME, str_LAST_NAME, str_PHONE, lg_TYPEDEPOT_ID,bool_SAME_LOCATION);
            ObllBase.setDetailmessage(OEmplacementManagement.getDetailmessage());
            ObllBase.setMessage(OEmplacementManagement.getMessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OEmplacementManagement.deleteEmplacement(lg_EMPLACEMENT_ID);
            ObllBase.setDetailmessage(OEmplacementManagement.getDetailmessage());
            ObllBase.setMessage(OEmplacementManagement.getMessage());
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>