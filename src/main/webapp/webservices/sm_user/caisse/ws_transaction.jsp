<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.reportventeManagement.Reportvente"%>
<%@page import="bll.gateway.outService.ServicesNotifCustomer"%>
<%@page import="dal.TAlertEventUserFone"%>
<%@page import="dal.TResumeCaisse"%>
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

<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TCoffreCaisse OTCoffreCaisse = null;


%>




<%    String lg_RESUME_CAISSE_ID_REF = "", lg_USER_ID = "", str_STATUT = "", lg_Participant_ID = "", ld_CREATED_BY = "", ld_UPDATED_BY = "";
    Integer int_AMOUNT;
    int int_NB_DIX = 0, int_NB_CINQ = 0, int_NB_DEUX = 0, int_NB_MIL = 0, int_NB_CINQ_CENT = 0, int_NB_AUTRE = 0;

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
    }
    if (request.getParameter("lg_RESUME_CAISSE_ID") != null) {
        lg_RESUME_CAISSE_ID_REF = request.getParameter("lg_RESUME_CAISSE_ID");
    }

    if ((request.getParameter("int_NB_DIX") != null) && (request.getParameter("int_NB_DIX") != "")) {
        int_NB_DIX = Integer.parseInt(request.getParameter("int_NB_DIX"));
    } else {
        int_NB_DIX = 0;
    }
    if ((request.getParameter("int_NB_CINQ") != null) && (request.getParameter("int_NB_CINQ") != "")) {
        int_NB_CINQ = Integer.parseInt(request.getParameter("int_NB_CINQ"));
    } else {
        int_NB_CINQ = 0;
    }
    if ((request.getParameter("int_NB_DEUX") != null) && (request.getParameter("int_NB_DEUX") != "")) {
        int_NB_DEUX = Integer.parseInt(request.getParameter("int_NB_DEUX"));
    } else {
        int_NB_DEUX = 0;
    }
    if ((request.getParameter("int_NB_MIL") != null) && (request.getParameter("int_NB_MIL") != "")) {
        int_NB_MIL = Integer.parseInt(request.getParameter("int_NB_MIL"));
    } else {
        int_NB_MIL = 0;
    }
    if ((request.getParameter("int_NB_CINQ_CENT") != null) && (request.getParameter("int_NB_CINQ_CENT") != "")) {
        int_NB_CINQ_CENT = Integer.parseInt(request.getParameter("int_NB_CINQ_CENT"));
    } else {
        int_NB_CINQ_CENT = 0;
    }
    if ((request.getParameter("int_NB_AUTRE") != null) && (request.getParameter("int_NB_AUTRE") != "")) {
        int_NB_AUTRE = Integer.parseInt(request.getParameter("int_NB_AUTRE"));
    } else {
        int_NB_AUTRE = 0;
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
   TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_RESUME_CAISSE_ID"));
    bll.teller.caisseManagement OcaisseManagement = new bll.teller.caisseManagement(ObllBase.getOdataManager(), ObllBase.getOTUser());

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("close")) {
            new logger().oCategory.info("close");

            OcaisseManagement.CloseCaisse(request.getParameter("lg_RESUME_CAISSE_ID"));
            ObllBase.setMessage(OcaisseManagement.getMessage());
            ObllBase.setDetailmessage(OcaisseManagement.getDetailmessage());
            //code ajouté 04/09/2016
            if (OcaisseManagement.getMessage().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS)) {
                OcaisseManagement.sendNotification("%%");
          
            }
            //fin code ajouté 04/09/2016

            // ObllBase.persiste(OTCoffreCaisse);
            //  new logger().oCategory.info("Creation  TModule " + OTCoffreCaisse.getLgUSERID().getStrFIRSTNAME() + " StrLabel " + OTCoffreCaisse.getIntAMOUNT());
        } else if (request.getParameter("mode").toString().equals("validate_cloture")) {

            OcaisseManagement.ValideCloseCaisse(request.getParameter("lg_RESUME_CAISSE_ID"));

            ObllBase.setMessage(OcaisseManagement.getMessage());
            ObllBase.setDetailmessage(OcaisseManagement.getDetailmessage());

        } else if (request.getParameter("mode").equals("dobilletage")) {
            TResumeCaisse OTResumeCaisse = null;
            try {
                new logger().OCategory.info(" resume caisse id   " + request.getParameter("lg_RESUME_CAISSE_ID"));
                OTResumeCaisse = ObllBase.getOdataManager().getEm().find(TResumeCaisse.class, request.getParameter("lg_RESUME_CAISSE_ID"));
                new logger().OCategory.info(" OTResumeCaisse   id  " + OTResumeCaisse.getLdCAISSEID());
                OcaisseManagement.DoBilletage(OTResumeCaisse, int_NB_DIX, int_NB_CINQ, int_NB_DEUX, int_NB_MIL, int_NB_CINQ_CENT, int_NB_AUTRE);
                ObllBase.setMessage(OcaisseManagement.getMessage());
                ObllBase.setDetailmessage(OcaisseManagement.getDetailmessage());

                new logger().OCategory.info("  *** OTResumeCaisse  ***  " + OTResumeCaisse.getLdCAISSEID());
            } catch (Exception e) {
                ObllBase.buildErrorTraceMessage("ERROR", "DESOLE PAS DE CAISSE POUR CETTE OPERATION " + e.toString() + "");
            }

        } else if (request.getParameter("mode").toString().equals("delete")) {
        } else if (request.getParameter("mode").toString().equals("rollbackclose")) {
            OcaisseManagement.RollBackCloseCaisse(request.getParameter("lg_RESUME_CAISSE_ID"));
            ObllBase.setMessage(OcaisseManagement.getMessage());
            ObllBase.setDetailmessage(OcaisseManagement.getDetailmessage());
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