<%@page import="dal.TAlertEvent"%>
<%@page import="dal.TUserFone"%>
<%@page import="dal.TAlertEventUserFone"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<%!  String lg_USER_FONE_ID = "%%", str_PHONE = "%%", str_STATUT = "%%", lg_USER_ID = "%%", str_PIC_SMALL;
    Integer int_PRICE;
    Integer int_STOCK_MINIMAL;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    dal.TUserFone OTUserFone = null;


%>




<%
    if (request.getParameter("lg_USER_FONE_ID") != null) {
        lg_USER_FONE_ID = request.getParameter("lg_USER_FONE_ID");
    }
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
    }
    if (request.getParameter("str_PHONE") != null) {
        str_PHONE = request.getParameter("str_PHONE");
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    //new logger().oCategory.info("ID " + request.getParameter("lg_INSTITUTION_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");
            TAlertEvent OTAlertEvent = (TAlertEvent) ObllBase.find(request.getParameter("str_Event"), new TAlertEvent());
            TUserFone OTUserFone = (TUserFone) ObllBase.find(request.getParameter("lg_USER_FONE_ID"), new TUserFone());

            TAlertEventUserFone OTAlertEventUserFone = new TAlertEventUserFone();
            OTAlertEventUserFone.setLgID(key.getComplexId());
            OTAlertEventUserFone.setLgUSERFONEID(OTUserFone);
            OTAlertEventUserFone.setStrEvent(OTAlertEvent);
            OTAlertEventUserFone.setStrSTATUT(commonparameter.statut_enable);
            ObllBase.persiste(OTAlertEventUserFone);

            ObllBase.buildSuccesTraceMessage("Creation Ok");

            /*  OTUser = ObllBase.getOdataManager().getEm().find(dal.TUser.class, request.getParameter("lg_USER_ID"));
             OTUserFone = new dal.TUserFone();
             OTUserFone.setLgUSERFONEID(key.getComplexId());
             OTUserFone.setStrPHONE(request.getParameter("str_PHONE").toString());
             OTUserFone.setStrSTATUT(commonparameter.statut_enable);
             OTUserFone.setLgUSERID(OTUser);
             OTUserFone.setDtCREATED(new Date());
             ObllBase.persiste(OTUserFone);
            
             */
        } else if (request.getParameter("mode").toString().equals("update")) {

            OTUserFone = OdataManager.getEm().find(dal.TUserFone.class, request.getParameter("lg_USER_FONE_ID").toString());
            OTUser = ObllBase.getOdataManager().getEm().find(dal.TUser.class, request.getParameter("lg_USER_ID"));
            OTUserFone.setStrPHONE(request.getParameter("str_PHONE").toString());
            OTUserFone.setStrSTATUT(commonparameter.statut_enable);
            OTUserFone.setLgUSERID(OTUser);
            OTUserFone.setDtUPDATED(new Date());
            ObllBase.persiste(OTUserFone);
        } else if (request.getParameter("mode").toString().equals("delete")) {

            TAlertEventUserFone OTAlertEventUserFone = (TAlertEventUserFone) ObllBase.find(request.getParameter("lg_ID"), new TAlertEventUserFone());
            ObllBase.delete(OTAlertEventUserFone);
            ObllBase.buildSuccesTraceMessage("Suppresion Ok");
            /*
             OTUserFone = OdataManager.getEm().find(dal.TUserFone.class, request.getParameter("lg_USER_FONE_ID").toString());
             OTUserFone.setStrSTATUT(commonparameter.statut_delete);
             ObllBase.persiste(OTUserFone);
             */
            //new logger().oCategory.info("Suppression de productitem " + request.getParameter("lg_PRODUCT_ITEM_ID").toString());
        } else {
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