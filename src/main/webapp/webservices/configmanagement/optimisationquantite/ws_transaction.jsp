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

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TOptimisationQuantite OTOptimisationQuantite = new TOptimisationQuantite();
    String lg_OPTIMISATION_QUANTITE_ID = "%%", str_CODE_OPTIMISATION = "%%", str_LIBELLE_OPTIMISATION = "%%";
%>


<%
    if (request.getParameter("lg_OPTIMISATION_QUANTITE_ID") != null) {
        lg_OPTIMISATION_QUANTITE_ID = request.getParameter("lg_OPTIMISATION_QUANTITE_ID");
    }
    if (request.getParameter("str_CODE_OPTIMISATION") != null) {
        str_CODE_OPTIMISATION = request.getParameter("str_CODE_OPTIMISATION");
    }
    if (request.getParameter("str_LIBELLE_OPTIMISATION") != null) {
        str_LIBELLE_OPTIMISATION = request.getParameter("str_LIBELLE_OPTIMISATION");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_OPTIMISATION_QUANTITE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TOptimisationQuantite OTOptimisationQuantite = new TOptimisationQuantite();
            OTOptimisationQuantite.setLgOPTIMISATIONQUANTITEID(key.getComplexId());
            OTOptimisationQuantite.setStrCODEOPTIMISATION(str_CODE_OPTIMISATION);
            OTOptimisationQuantite.setStrLIBELLEOPTIMISATION(str_LIBELLE_OPTIMISATION);
            OTOptimisationQuantite.setStrSTATUT(commonparameter.statut_enable);
            OTOptimisationQuantite.setDtCREATED(new Date());
            ObllBase.persiste(OTOptimisationQuantite);
            new logger().oCategory.info("CREATION OTOptimisationQuantite " + OTOptimisationQuantite.getLgOPTIMISATIONQUANTITEID() + " StrLabel " + OTOptimisationQuantite.getStrCODEOPTIMISATION());

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_OPTIMISATION_QUANTITE_ID").toString().equals("init")) {

                dal.TOptimisationQuantite OTOptimisationQuantite = new TOptimisationQuantite();
                OTOptimisationQuantite.setLgOPTIMISATIONQUANTITEID(key.getComplexId());
                OTOptimisationQuantite.setStrCODEOPTIMISATION(str_CODE_OPTIMISATION);
                OTOptimisationQuantite.setStrLIBELLEOPTIMISATION(str_LIBELLE_OPTIMISATION);
                OTOptimisationQuantite.setStrSTATUT(commonparameter.statut_enable);
                OTOptimisationQuantite.setDtCREATED(new Date());
                ObllBase.persiste(OTOptimisationQuantite);
                new logger().oCategory.info("CREATION OTOptimisationQuantite " + OTOptimisationQuantite.getLgOPTIMISATIONQUANTITEID() + " StrLabel " + OTOptimisationQuantite.getStrCODEOPTIMISATION());

            } else {

                dal.TOptimisationQuantite OTOptimisationQuantite = null;
                OTOptimisationQuantite = ObllBase.getOdataManager().getEm().find(dal.TOptimisationQuantite.class, request.getParameter("lg_OPTIMISATION_QUANTITE_ID").toString());
                OTOptimisationQuantite.setStrCODEOPTIMISATION(str_CODE_OPTIMISATION);
                OTOptimisationQuantite.setStrLIBELLEOPTIMISATION(str_LIBELLE_OPTIMISATION);
                OTOptimisationQuantite.setStrSTATUT(commonparameter.statut_enable);
                OTOptimisationQuantite.setDtUPDATED(new Date());

                ObllBase.persiste(OTOptimisationQuantite);
                new logger().oCategory.info("Mise a jour OTOptimisationQuantite " + OTOptimisationQuantite.getLgOPTIMISATIONQUANTITEID() + " StrLabel " + OTOptimisationQuantite.getStrLIBELLEOPTIMISATION());

            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            OTOptimisationQuantite = ObllBase.getOdataManager().getEm().find(dal.TOptimisationQuantite.class, request.getParameter("lg_OPTIMISATION_QUANTITE_ID"));

            OTOptimisationQuantite.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTOptimisationQuantite);

            new logger().oCategory.info("Suppression de famille " + request.getParameter("lg_OPTIMISATION_QUANTITE_ID").toString());

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