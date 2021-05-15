<%@page import="dal.TTypeRisque"%>
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
    dal.TTypeRisque OTTypeRisque = null;
    String lg_TYPERISQUE_ID = "%%", str_NAME = "%%", str_DESCRIPTION = "%%";
%>


<%
    if (request.getParameter("lg_TYPERISQUE_ID") != null) {
        lg_TYPERISQUE_ID = request.getParameter("lg_TYPERISQUE_ID");
    }
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
    }
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("TYPE REMISE - WS_TRANSACTION ");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_TYPERISQUE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TTypeRisque OTTypeRisque = new TTypeRisque();
            OTTypeRisque.setLgTYPERISQUEID(key.getComplexId());
            OTTypeRisque.setStrNAME(str_NAME);
            OTTypeRisque.setStrDESCRIPTION(str_DESCRIPTION);
            OTTypeRisque.setStrSTATUT(commonparameter.statut_enable);
            OTTypeRisque.setDtCREATED(new Date());
            ObllBase.persiste(OTTypeRisque);

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_TYPERISQUE_ID").toString().equals("init")) {

                dal.TTypeRisque OTTypeRisque = new TTypeRisque();
                OTTypeRisque.setLgTYPERISQUEID(key.getComplexId());
                OTTypeRisque.setStrNAME(str_NAME);
                OTTypeRisque.setStrDESCRIPTION(str_DESCRIPTION);
                OTTypeRisque.setStrSTATUT(commonparameter.statut_enable);
                OTTypeRisque.setDtCREATED(new Date());
                ObllBase.persiste(OTTypeRisque);

            } else {
                
                dal.TTypeRisque OTTypeRisque = null;
                OTTypeRisque = ObllBase.getOdataManager().getEm().find(dal.TTypeRisque.class, request.getParameter("lg_TYPERISQUE_ID").toString());
                OTTypeRisque.setStrNAME(str_NAME);
                OTTypeRisque.setStrDESCRIPTION(str_DESCRIPTION);
                OTTypeRisque.setStrSTATUT(commonparameter.statut_enable);
                OTTypeRisque.setDtUPDATED(new Date());

                ObllBase.persiste(OTTypeRisque);
                new logger().oCategory.info("Mise a jour OTTypeRisque " + OTTypeRisque.getLgTYPERISQUEID()+ " StrLabel " + OTTypeRisque.getStrNAME());

            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TTypeRisque OTTypeRisque = null;
            OTTypeRisque = ObllBase.getOdataManager().getEm().find(dal.TTypeRisque.class, request.getParameter("lg_TYPERISQUE_ID"));

            OTTypeRisque.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTTypeRisque);

            new logger().oCategory.info("Suppression de famille " + request.getParameter("lg_TYPERISQUE_ID").toString());

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