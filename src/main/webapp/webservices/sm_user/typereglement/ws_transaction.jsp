<%@page import="dal.TTypeReglement"%>
<%@page import="dal.TEscompteSociete"%>
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
    date key = new date();
    
    
%>



<%
    String lg_TYPE_REGLEMENT_ID = "", str_NAME = "", str_DESCRIPTION = "", str_FLAG = "0";
    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null) {
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");
    }
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
    }
    if (request.getParameter("str_DESCRIPTION") != null) {
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_TYPE_REGLEMENT_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TTypeReglement OTTypeReglement = new TTypeReglement();

            OTTypeReglement.setLgTYPEREGLEMENTID(key.getComplexId());
            OTTypeReglement.setStrNAME(str_NAME);
            OTTypeReglement.setStrFLAG(str_FLAG);
            OTTypeReglement.setStrDESCRIPTION(str_DESCRIPTION);
            OTTypeReglement.setStrSTATUT(commonparameter.statut_enable);
            OTTypeReglement.setDtCREATED(new Date());
            ObllBase.persiste(OTTypeReglement);
            new logger().oCategory.info("Mise a jour OTTypeReglement " + OTTypeReglement.getLgTYPEREGLEMENTID() + " CODEBAREME " + OTTypeReglement.getStrNAME());

        } else if (request.getParameter("mode").toString().equals("update")) {

            dal.TTypeReglement OTTypeReglement = null;
            OTTypeReglement = ObllBase.getOdataManager().getEm().find(dal.TTypeReglement.class, request.getParameter("lg_TYPE_REGLEMENT_ID").toString());

            OTTypeReglement.setStrNAME(str_NAME);
            OTTypeReglement.setStrDESCRIPTION(str_DESCRIPTION);
            OTTypeReglement.setStrSTATUT(commonparameter.statut_enable);
            OTTypeReglement.setDtUPDATED(new Date());

            ObllBase.persiste(OTTypeReglement);
            new logger().oCategory.info("Mise a jour OTTypeReglement " + OTTypeReglement.getLgTYPEREGLEMENTID() + " StrLabel " + OTTypeReglement.getStrNAME());

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TTypeReglement OTTypeReglement = null;
            OTTypeReglement = ObllBase.getOdataManager().getEm().find(dal.TTypeReglement.class, request.getParameter("lg_TYPE_REGLEMENT_ID"));

            OTTypeReglement.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTTypeReglement);

            new logger().oCategory.info("Suppression de code gestion " + request.getParameter("lg_TYPE_REGLEMENT_ID").toString());

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