<%@page import="bll.configManagement.TauxRembourssementManagement"%>
<%@page import="dal.TTauxRembourssement"%>
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
    dal.TTauxRembourssement OTTauxRembourssement = null;
   String lg_TAUX_REMBOUR_ID = "%%", str_LIBELLEE = "%%" ;
   int str_CODE_REMB = 0;
%>




<%
    if (request.getParameter("lg_TAUX_REMBOUR_ID") != null) {
        lg_TAUX_REMBOUR_ID = request.getParameter("lg_TAUX_REMBOUR_ID");
        new logger().oCategory.info("lg_TAUX_REMBOUR_ID : " + request.getParameter("lg_TAUX_REMBOUR_ID"));
    }
    if (request.getParameter("str_CODE_REMB") != null) {
        str_CODE_REMB = Integer.parseInt(request.getParameter("str_CODE_REMB"));
    }
    if (request.getParameter("str_LIBELLEE") != null) {
        str_LIBELLEE = request.getParameter("str_LIBELLEE");
    }    
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("TAUX WS TRANSACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("lg_TAUX_REMBOUR_ID " + request.getParameter("lg_TAUX_REMBOUR_ID"));

    if (request.getParameter("mode") != null) {

        //MODE CREATION
        if (request.getParameter("mode").toString().equals("create")) {
            TauxRembourssementManagement OTauxRembourssementManagement = new TauxRembourssementManagement(OdataManager);
            OTauxRembourssementManagement.create(str_CODE_REMB, str_LIBELLEE);

            //MODE MODIFICATION
        } else if (request.getParameter("mode").toString().equals("update")) {

            TauxRembourssementManagement OTauxRembourssementManagement = new TauxRembourssementManagement(OdataManager);
            OTauxRembourssementManagement.update(lg_TAUX_REMBOUR_ID, str_CODE_REMB, str_LIBELLEE);

            //MODE SUPPRESSION
        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TTauxRembourssement OTTauxRembourssement = null;
            OTTauxRembourssement = ObllBase.getOdataManager().getEm().find(dal.TTauxRembourssement.class, request.getParameter("lg_TAUX_REMBOUR_ID"));

            OTTauxRembourssement.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTTauxRembourssement);

            new logger().oCategory.info("Suppression de code gestion " + request.getParameter("lg_TAUX_REMBOUR_ID").toString());

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