<%@page import="dal.TPreenregistrement"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
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
    TUser OTUser = null;
    TPreenregistrement OTPreenregistrement = null;


%>




<%
     String lg_PREENREGISTREMENT_ID = "", str_STATUT = commonparameter.statut_is_Process, str_TYPE_VENTE = "%%";
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID: "+lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equals("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        new logger().OCategory.info("str_TYPE_VENTE "+str_TYPE_VENTE);
    }
    
     if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT "+str_STATUT);
    }
    

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
 TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_PREENREGISTREMENT_ID"));
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, user);

    if (request.getParameter("mode") != null) {

      if (request.getParameter("mode").toString().equals("delete")) {

            try {
                OTPreenregistrement = ObllBase.getOdataManager().getEm().find(dal.TPreenregistrement.class, request.getParameter("lg_PREENREGISTREMENT_ID"));
                OTPreenregistrement.setStrSTATUT(commonparameter.statut_delete);
                ObllBase.persiste(OTPreenregistrement);
                new logger().oCategory.info("Suppression Prevente " + OTPreenregistrement.getStrREF());

            } catch (Exception e) {

                new logger().oCategory.info("Desole prevente inexistante");

                e.printStackTrace();
            }

            new logger().oCategory.info("Suppression  OTPreenregistrement " + request.getParameter("lg_PREENREGISTREMENT_ID").toString());

        } else if (request.getParameter("mode").toString().equals("deleteprevente")) {
            new logger().oCategory.info("Dans deleteprevente");
            OPreenregistrement.DeleteTPreenregistrement(lg_PREENREGISTREMENT_ID);
            ObllBase.setMessage(OPreenregistrement.getMessage());
            ObllBase.setDetailmessage(OPreenregistrement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("trash") || request.getParameter("mode").toString().equals("restaure")) {
            OPreenregistrement.DeleteTPreenregistrement(lg_PREENREGISTREMENT_ID, str_STATUT);
            ObllBase.setMessage(OPreenregistrement.getMessage());
            ObllBase.setDetailmessage(OPreenregistrement.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("restaureall")) {
            OPreenregistrement.RestaureAllPrevente(lg_PREENREGISTREMENT_ID, str_STATUT, str_TYPE_VENTE);
            ObllBase.setMessage(OPreenregistrement.getMessage());
            ObllBase.setDetailmessage(OPreenregistrement.getDetailmessage());
        } 
    } else {
    }

    String result;
    new logger().OCategory.info("ObllBase.getMessage() ---- " + ObllBase.getMessage());
    new logger().OCategory.info("commonparameter.PROCESS_SUCCESS ---- " + commonparameter.PROCESS_SUCCESS);//int_total_vente
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>