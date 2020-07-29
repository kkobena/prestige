<%@page import="dal.TSpecialite"%>
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
    TRole OTRole = null;
    dal.TSpecialite OTSpecialite = null;
    String lg_SPECIALITE_ID = "%%", str_LIBELLESPECIALITE = "%%", str_CODESPECIALITE = "%%";
%>


<%
    if (request.getParameter("lg_SPECIALITE_ID") != null) {
        lg_SPECIALITE_ID = request.getParameter("lg_SPECIALITE_ID");
    }
    if (request.getParameter("str_LIBELLESPECIALITE") != null) {
        str_LIBELLESPECIALITE = request.getParameter("str_LIBELLESPECIALITE");
    }
    if (request.getParameter("str_CODESPECIALITE") != null) {
        str_CODESPECIALITE = request.getParameter("str_CODESPECIALITE");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_SPECIALITE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TSpecialite OTSpecialite = new TSpecialite();
            OTSpecialite.setLgSPECIALITEID(key.getComplexId());
            OTSpecialite.setStrLIBELLESPECIALITE(str_LIBELLESPECIALITE);
            OTSpecialite.setStrCODESPECIALITE(str_CODESPECIALITE);
            OTSpecialite.setStrSTATUT(commonparameter.statut_enable);
            OTSpecialite.setDtCREATED(new Date());
            ObllBase.persiste(OTSpecialite);

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_SPECIALITE_ID").toString().equals("init")) {

                dal.TSpecialite OTSpecialite = new TSpecialite();
                OTSpecialite.setLgSPECIALITEID(key.getComplexId());
                OTSpecialite.setStrLIBELLESPECIALITE(str_LIBELLESPECIALITE);
                OTSpecialite.setStrCODESPECIALITE(str_CODESPECIALITE);
                OTSpecialite.setStrSTATUT(commonparameter.statut_enable);
                OTSpecialite.setDtCREATED(new Date());
                ObllBase.persiste(OTSpecialite);

            } else {

                dal.TSpecialite OTSpecialite = null;
                OTSpecialite = ObllBase.getOdataManager().getEm().find(dal.TSpecialite.class, lg_SPECIALITE_ID);
                OTSpecialite.setStrLIBELLESPECIALITE(str_LIBELLESPECIALITE);
                OTSpecialite.setStrCODESPECIALITE(str_CODESPECIALITE);
                OTSpecialite.setStrSTATUT(commonparameter.statut_enable);
                OTSpecialite.setDtUPDATED(new Date());

                ObllBase.persiste(OTSpecialite);
                new logger().oCategory.info("Mise a jour OTSpecialite " + OTSpecialite.getLgSPECIALITEID()+ " StrLabel " + OTSpecialite.getStrLIBELLESPECIALITE());

            } 

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TSpecialite OTSpecialite = null;
            OTSpecialite = ObllBase.getOdataManager().getEm().find(dal.TSpecialite.class, request.getParameter("lg_SPECIALITE_ID"));

            OTSpecialite.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTSpecialite);

            new logger().oCategory.info("Suppression de Specialite " + request.getParameter("lg_SPECIALITE_ID").toString());

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