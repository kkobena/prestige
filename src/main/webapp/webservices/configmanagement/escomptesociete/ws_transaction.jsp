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
    dal.TEscompteSociete OTEscompteSociete = new TEscompteSociete();;
    String lg_ESCOMPTE_SOCIETE_ID = "%%", str_LIBELLE_ESCOMPTE_SOCIETE = "%%";
    int int_CODE_ESCOMPTE_SOCIETE;
%>


<%
    if (request.getParameter("lg_ESCOMPTE_SOCIETE_ID") != null) {
        lg_ESCOMPTE_SOCIETE_ID = request.getParameter("lg_ESCOMPTE_SOCIETE_ID");
    }
    if (request.getParameter("int_CODE_ESCOMPTE_SOCIETE") != null) {
        int_CODE_ESCOMPTE_SOCIETE = Integer.parseInt(request.getParameter("int_CODE_ESCOMPTE_SOCIETE"));
    }
    if (request.getParameter("str_LIBELLE_ESCOMPTE_SOCIETE") != null) {
        str_LIBELLE_ESCOMPTE_SOCIETE = request.getParameter("str_LIBELLE_ESCOMPTE_SOCIETE");
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_ESCOMPTE_SOCIETE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TEscompteSociete OTEscompteSociete = new TEscompteSociete();
            OTEscompteSociete.setLgESCOMPTESOCIETEID(key.getComplexId());
            OTEscompteSociete.setIntCODEESCOMPTESOCIETE(int_CODE_ESCOMPTE_SOCIETE);
            OTEscompteSociete.setStrLIBELLEESCOMPTESOCIETE(str_LIBELLE_ESCOMPTE_SOCIETE);
            OTEscompteSociete.setStrSTATUT(commonparameter.statut_enable);
            OTEscompteSociete.setDtCREATED(new Date());
            ObllBase.persiste(OTEscompteSociete);

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_ESCOMPTE_SOCIETE_ID").toString().equals("init")) {

                dal.TEscompteSociete OTEscompteSociete = new TEscompteSociete();
                OTEscompteSociete.setLgESCOMPTESOCIETEID(key.getComplexId());
                OTEscompteSociete.setIntCODEESCOMPTESOCIETE(int_CODE_ESCOMPTE_SOCIETE);
                OTEscompteSociete.setStrLIBELLEESCOMPTESOCIETE(str_LIBELLE_ESCOMPTE_SOCIETE);
                OTEscompteSociete.setStrSTATUT(commonparameter.statut_enable);
                OTEscompteSociete.setDtCREATED(new Date());
                new logger().oCategory.info("Nous sommes apres la date " );
                ObllBase.persiste(OTEscompteSociete);
                 new logger().oCategory.info("Verif de la persistence" );

            } else {

                dal.TEscompteSociete OTEscompteSociete = null;
                OTEscompteSociete = ObllBase.getOdataManager().getEm().find(dal.TEscompteSociete.class, request.getParameter("lg_ESCOMPTE_SOCIETE_ID").toString());
                OTEscompteSociete.setIntCODEESCOMPTESOCIETE(int_CODE_ESCOMPTE_SOCIETE);
                OTEscompteSociete.setStrLIBELLEESCOMPTESOCIETE(str_LIBELLE_ESCOMPTE_SOCIETE);
                OTEscompteSociete.setStrSTATUT(commonparameter.statut_enable);
                OTEscompteSociete.setDtUPDATED(new Date());

                ObllBase.persiste(OTEscompteSociete);

            }

            new logger().oCategory.info("Mise a jour OTEscompteSociete " + OTEscompteSociete.getLgESCOMPTESOCIETEID() + " StrLabel " + OTEscompteSociete.getStrLIBELLEESCOMPTESOCIETE());

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TEscompteSociete OTEscompteSociete = null;
            OTEscompteSociete = ObllBase.getOdataManager().getEm().find(dal.TEscompteSociete.class, request.getParameter("lg_ESCOMPTE_SOCIETE_ID"));

            OTEscompteSociete.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTEscompteSociete);

            new logger().oCategory.info("Suppression de famille " + request.getParameter("lg_ESCOMPTE_SOCIETE_ID").toString());

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