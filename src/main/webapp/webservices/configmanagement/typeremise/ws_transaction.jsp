<%@page import="dal.TTypeRemise"%>
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
    dal.TTypeRemise OTTypeRemise = null;
    String lg_TYPE_REMISE_ID = "%%", str_NAME = "%%", str_DESCRIPTION = "%%";
%>


<%
    if (request.getParameter("lg_TYPE_REMISE_ID") != null) {
        lg_TYPE_REMISE_ID = request.getParameter("lg_TYPE_REMISE_ID");
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

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_TYPE_REMISE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TTypeRemise OTTypeRemise = new TTypeRemise();
            OTTypeRemise.setLgTYPEREMISEID(key.getComplexId());
            OTTypeRemise.setStrNAME(str_NAME);
            OTTypeRemise.setStrDESCRIPTION(str_DESCRIPTION);
            OTTypeRemise.setStrSTATUT(commonparameter.statut_enable);
            OTTypeRemise.setDtCREATED(new Date());
            ObllBase.persiste(OTTypeRemise);

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_TYPE_REMISE_ID").toString().equals("init")) {

                dal.TTypeRemise OTTypeRemise = new TTypeRemise();
                OTTypeRemise.setLgTYPEREMISEID(key.getComplexId());
                OTTypeRemise.setStrNAME(str_NAME);
                OTTypeRemise.setStrDESCRIPTION(str_DESCRIPTION);
                OTTypeRemise.setStrSTATUT(commonparameter.statut_enable);
                OTTypeRemise.setDtCREATED(new Date());
                ObllBase.persiste(OTTypeRemise);

            } else {

                dal.TTypeRemise OTTypeRemise = null;
                OTTypeRemise = ObllBase.getOdataManager().getEm().find(dal.TTypeRemise.class, request.getParameter("lg_TYPE_REMISE_ID").toString());
                OTTypeRemise.setStrNAME(str_NAME);
                OTTypeRemise.setStrDESCRIPTION(str_DESCRIPTION);
                OTTypeRemise.setStrSTATUT(commonparameter.statut_enable);
                OTTypeRemise.setDtUPDATED(new Date());

                ObllBase.persiste(OTTypeRemise);
                new logger().oCategory.info("Mise a jour OTTypeRemise " + OTTypeRemise.getLgTYPEREMISEID()+ " StrLabel " + OTTypeRemise.getStrNAME());

            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TTypeRemise OTTypeRemise = null;
            OTTypeRemise = ObllBase.getOdataManager().getEm().find(dal.TTypeRemise.class, request.getParameter("lg_TYPE_REMISE_ID"));

            OTTypeRemise.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTTypeRemise);

            new logger().oCategory.info("Suppression de famille " + request.getParameter("lg_TYPE_REMISE_ID").toString());

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