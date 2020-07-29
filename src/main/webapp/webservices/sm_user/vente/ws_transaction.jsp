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
    date key = new date();
    //privilege Oprivilege = new privilege();
    TUser OTUser = null;
    dal.TPreenregistrement OTPreenregistrement = null;
    int int_total_vente = 0;

%>




<%    String lg_PREENREGISTREMENT_ID = "", str_FIRST_NAME = "", str_LAST_NAME = "",str_ADRESSE="";
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_PREENREGISTREMENT_ID"));
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    if (request.getParameter("str_FIRST_NAME") != null) {
        str_FIRST_NAME = request.getParameter("str_FIRST_NAME");

    }
    if (request.getParameter("str_LAST_NAME") != null) {
        str_LAST_NAME = request.getParameter("str_LAST_NAME");

    }
    if (request.getParameter("str_ADRESSE") != null) {
        str_ADRESSE = request.getParameter("str_ADRESSE");

    }
    
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");

    }
    
    if (request.getParameter("str_ADRESSE") != null) {
        str_ADRESSE = request.getParameter("str_ADRESSE");

    }
    

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            //     OTPreenregistrement = OPreenregistrement.CreatePreVente();
            /* try {
    
             OTPreenregistrement = OPreenregistrement.DoPreenregistrement(lg_FAMILLE_ID, int_PRICE_RESUME, int_QUANTITY);
             } catch (Exception e) {
             new logger().OCategory.info("impossible de creer preenregistrement  : " + request.getParameter("lg_PREENREGISTREMENT_ID"));
             }*/
            new logger().oCategory.info("Creation  TPreenregistrement " + OTPreenregistrement.getStrREF() + " prix " + OTPreenregistrement.getIntPRICE());

        } else if (request.getParameter("mode").toString().equals("update")) {

            //    OTPreenregistrement = OPreenregistrement.CreatePreVente();
            /* try {
             OTPreenregistrement = ObllBase.getOdataManager().getEm().find(dal.TPreenregistrement.class, request.getParameter("lg_PREENREGISTREMENT_ID"));
             OTPreenregistrement = OPreenregistrement.UpdatePreenregistrement(lg_PREENREGISTREMENT_ID, lg_FAMILLE_ID, int_PRICE_RESUME, int_QUANTITY);
             } catch (Exception e) {

             new logger().OCategory.info("preenregistrement inexistant : " + request.getParameter("lg_PREENREGISTREMENT_ID"));
             }*/
            new logger().oCategory.info("Mise a jour TPreenregistrement " + OTPreenregistrement.getLgPREENREGISTREMENTID() + " StrLabel " + OTPreenregistrement.getStrREF());

        } else if (request.getParameter("mode").toString().equals("delete")) {

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

        } else if (request.getParameter("mode").equals("updatecustomerinfos")) {
            OTPreenregistrement = OPreenregistrement.updatePreenregistrementCustomerInfo(lg_PREENREGISTREMENT_ID, str_FIRST_NAME, str_LAST_NAME,str_ADRESSE);
            if (OTPreenregistrement != null) {
                ObllBase.setMessage("1");
                ObllBase.setDetailmessage("Modification éffectuée avec succès");
            }
        }
    } else {
    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", lg_PREENREGISTREMENT_ID:\"" + OTPreenregistrement.getLgPREENREGISTREMENTID() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>