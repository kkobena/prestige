<%@page import="dal.TRegimeCaisse"%>
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

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TRegimeCaisse OTRegimeCaisse = null;
    String lg_REGIMECAISSE_ID = "%%", str_CODEREGIMECAISSE = "%%",str_LIBELLEREGIMECAISSE = "%%";
    boolean bool_CONTROLEMATRICULE;

%>



<%
    if (request.getParameter("lg_REGIMECAISSE_ID") != null) {
        lg_REGIMECAISSE_ID = request.getParameter("lg_REGIMECAISSE_ID");
    }
    if (request.getParameter("str_CODEREGIMECAISSE") != null) {
        str_CODEREGIMECAISSE = request.getParameter("str_CODEREGIMECAISSE");
    }
    if (request.getParameter("str_LIBELLEREGIMECAISSE") != null) {
        str_LIBELLEREGIMECAISSE = request.getParameter("str_LIBELLEREGIMECAISSE");
    }
    if (request.getParameter("bool_CONTROLEMATRICULE") != null) {
        bool_CONTROLEMATRICULE = Boolean.parseBoolean(request.getParameter("bool_CONTROLEMATRICULE"));
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
    new logger().oCategory.info("ID " + request.getParameter("lg_REGIMECAISSE_ID"));

    new logger().oCategory.info("bool_CONTROLEMATRICULE   @@@@@@@@@@@@@@@@     " + request.getParameter("bool_CONTROLEMATRICULE"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TRegimeCaisse OTRegimeCaisse = new TRegimeCaisse();

            OTRegimeCaisse.setLgREGIMECAISSEID(key.getComplexId());
            OTRegimeCaisse.setStrCODEREGIMECAISSE(str_CODEREGIMECAISSE);
            OTRegimeCaisse.setStrLIBELLEREGIMECAISSE(str_LIBELLEREGIMECAISSE);
            OTRegimeCaisse.setBoolCONTROLEMATRICULE(bool_CONTROLEMATRICULE);
            OTRegimeCaisse.setStrSTATUT(commonparameter.statut_enable);
            OTRegimeCaisse.setDtCREATED(new Date());

            ObllBase.persiste(OTRegimeCaisse);
            new logger().oCategory.info("Mise a jour OTRegimeCaisse " + OTRegimeCaisse.getLgREGIMECAISSEID()+ " CODEBAREME " + OTRegimeCaisse.getStrCODEREGIMECAISSE());

        } else if (request.getParameter("mode").toString().equals("update")) {

                dal.TRegimeCaisse OTRegimeCaisse = null;
            OTRegimeCaisse = ObllBase.getOdataManager().getEm().find(dal.TRegimeCaisse.class, request.getParameter("lg_REGIMECAISSE_ID").toString());

            OTRegimeCaisse.setStrCODEREGIMECAISSE(str_CODEREGIMECAISSE);
            OTRegimeCaisse.setStrLIBELLEREGIMECAISSE(str_LIBELLEREGIMECAISSE);
            OTRegimeCaisse.setBoolCONTROLEMATRICULE(bool_CONTROLEMATRICULE);
            OTRegimeCaisse.setStrSTATUT(commonparameter.statut_enable);
            OTRegimeCaisse.setDtUPDATED(new Date());
            
            ObllBase.persiste(OTRegimeCaisse);
            new logger().oCategory.info("Mise a jour OTRegimeCaisse " + OTRegimeCaisse.getLgREGIMECAISSEID()+ " StrLabel " + OTRegimeCaisse.getStrCODEREGIMECAISSE());

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TRegimeCaisse OTRegimeCaisse = null;
            OTRegimeCaisse = ObllBase.getOdataManager().getEm().find(dal.TRegimeCaisse.class, request.getParameter("lg_REGIMECAISSE_ID"));

            OTRegimeCaisse.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTRegimeCaisse);

            new logger().oCategory.info("Suppression de code gestion " + request.getParameter("lg_REGIMECAISSE_ID").toString());

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