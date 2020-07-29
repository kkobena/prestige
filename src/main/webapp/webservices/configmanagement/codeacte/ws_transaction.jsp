<%@page import="dal.TCodeActe"%>
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
<%@page import="bll.configManagement.codeActeManagement"  %>

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TCodeActe OTCodeActe = null;
    dal.TCodeGestion OTCodeGestion = null;
    String lg_CODE_ACTE_ID = "%%", str_LIBELLEE = "%%" ;  
%>




<%
    if (request.getParameter("lg_CODE_ACTE_ID") != null) {
        lg_CODE_ACTE_ID = request.getParameter("lg_CODE_ACTE_ID");
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

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_CODE_ACTE_ID"));

    new logger().oCategory.info("lg_OPTIMISATION_QUANTITE_ID   @@@@@@@@@@@@@@@@     " + request.getParameter("lg_OPTIMISATION_QUANTITE_ID"));

   if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_CODE_ACTE_ID").toString().equals("init")) {

                OTCodeActe = new dal.TCodeActe();
                OTCodeActe.setLgCODEACTEID(key.getComplexId());
                OTCodeActe.setStrLIBELLEE(request.getParameter("str_LIBELLEE").toString());
                //OTCodeActe.setStrName(request.getParameter("STR_NAME").toString());
                //OTCodeActe.setStrStatut(commonparameter.statut_enable);

                ObllBase.persiste(OTCodeActe);
                new logger().oCategory.info("Creation Code Acte " + OTCodeActe.getLgCODEACTEID()+ " StrLabel " + OTCodeActe.getStrLIBELLEE());

            } else {
                new logger().oCategory.info("Ref " + request.getParameter("lg_CODE_ACTE_ID").toString());
                OTCodeActe = OdataManager.getEm().find(dal.TCodeActe.class, request.getParameter("lg_CODE_ACTE_ID").toString());
                OTCodeActe.setStrLIBELLEE(request.getParameter("str_LIBELLEE").toString());
               // OTCodeActe.setStrName(request.getParameter("STR_NAME").toString());

                ObllBase.persiste(OTCodeActe);
                new logger().oCategory.info("Creation Code Acte " + OTCodeActe.getLgCODEACTEID()+ " StrLabel " + OTCodeActe.getStrLIBELLEE());

            }
        } else if (request.getParameter("mode").toString().equals("delete")) {

            OTCodeActe = OdataManager.getEm().find(dal.TCodeActe.class, request.getParameter("lg_CODE_ACTE_ID").toString());

            //OTCodeActe.setStrStatut(commonparameter.statut_delete);

            /*if (!ObllBase.delete(OTCodeActe)) {
             ObllBase.setDetailmessage("Impossible de supprimer");
             }*/
                 //   new logger().oCategory.info("Suppression de institution " + request.getParameter("lg_INSTITUTION_ID").toString());
        } else {
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:1,lg_CODE_ACTE_ID: \"" + OTCodeActe.getLgCODEACTEID()+ "\" }";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>