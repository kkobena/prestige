<%@page import="dal.TContreIndication"%>
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

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TContreIndication OTContreIndication = null;
    String lg_CONTRE_INDICATION_ID = "%%", str_CODE_CONTRE_INDICATION = "%%", str_LIBELLE_CONTRE_INDICATION = "%%";
%>


<%
    if (request.getParameter("lg_CONTRE_INDICATION_ID") != null) {
        lg_CONTRE_INDICATION_ID = request.getParameter("lg_CONTRE_INDICATION_ID");
    }
    if (request.getParameter("str_CODE_CONTRE_INDICATION") != null) {
        str_CODE_CONTRE_INDICATION = request.getParameter("str_CODE_CONTRE_INDICATION");
    }
    if (request.getParameter("str_LIBELLE_CONTRE_INDICATION") != null) {
        str_LIBELLE_CONTRE_INDICATION = request.getParameter("str_LIBELLE_CONTRE_INDICATION");
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_CONTRE_INDICATION_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TContreIndication OTContreIndication = new TContreIndication();
            OTContreIndication.setLgCONTREINDICATIONID(key.getComplexId());
            OTContreIndication.setStrCODECONTREINDICATION(str_CODE_CONTRE_INDICATION);
            OTContreIndication.setStrLIBELLECONTREINDICATION(str_LIBELLE_CONTRE_INDICATION);
            OTContreIndication.setStrSTATUT(commonparameter.statut_enable);
            OTContreIndication.setDtCREATED(new Date());
            ObllBase.persiste(OTContreIndication);

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_CONTRE_INDICATION_ID").toString().equals("init")) {
                
                dal.TContreIndication OTContreIndication = new TContreIndication();
                OTContreIndication.setLgCONTREINDICATIONID(key.getComplexId());
                OTContreIndication.setStrCODECONTREINDICATION(str_CODE_CONTRE_INDICATION);
                OTContreIndication.setStrLIBELLECONTREINDICATION(str_LIBELLE_CONTRE_INDICATION);
                OTContreIndication.setStrSTATUT(commonparameter.statut_enable);
                OTContreIndication.setDtCREATED(new Date());
                ObllBase.persiste(OTContreIndication);
                

            } else {

                dal.TContreIndication OTContreIndication = null;
                OTContreIndication = ObllBase.getOdataManager().getEm().find(dal.TContreIndication.class, request.getParameter("lg_CONTRE_INDICATION_ID").toString());
                OTContreIndication.setStrCODECONTREINDICATION(str_CODE_CONTRE_INDICATION);
                OTContreIndication.setStrLIBELLECONTREINDICATION(str_LIBELLE_CONTRE_INDICATION);
                OTContreIndication.setStrSTATUT(commonparameter.statut_enable);
                OTContreIndication.setDtUPDATED(new Date());

                ObllBase.persiste(OTContreIndication);
                new logger().oCategory.info("Mise a jour OTContreIndication " + OTContreIndication.getLgCONTREINDICATIONID() + " StrLabel " + OTContreIndication.getStrCODECONTREINDICATION());

            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            OTContreIndication = ObllBase.getOdataManager().getEm().find(dal.TContreIndication.class, request.getParameter("lg_CONTRE_INDICATION_ID"));

            OTContreIndication.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTContreIndication);

            new logger().oCategory.info("Suppression de famille " + request.getParameter("lg_CONTRE_INDICATION_ID").toString());

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