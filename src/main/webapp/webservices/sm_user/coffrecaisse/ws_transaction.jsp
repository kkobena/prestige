<%@page import="bll.teller.caisseManagement"%>
<%@page import="dal.TCoffreCaisse"%>
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

%>




<%    String ID_COFFRE_CAISSE = "", lg_USER_ID = "", str_STATUT = "", lg_Participant_ID = "", ld_CREATED_BY = "", ld_UPDATED_BY = "";
    int int_AMOUNT = 0;
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
    }
    if (request.getParameter("ID_COFFRE_CAISSE") != null) {
        ID_COFFRE_CAISSE = request.getParameter("ID_COFFRE_CAISSE");
    }
    
    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID:"+lg_USER_ID);
    }

    
    if (request.getParameter("int_AMOUNT") != null) {
        int_AMOUNT = Integer.parseInt(request.getParameter("int_AMOUNT"));
        new logger().OCategory.info("int_AMOUNT:"+int_AMOUNT);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("ID_COFFRE_CAISSE"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("utilisateur:"+lg_USER_ID);

            caisseManagement OcaisseManagement = new caisseManagement(OdataManager, OTUser);
            OcaisseManagement.sendCashToCaisseEmp(lg_USER_ID, int_AMOUNT);

            ObllBase.setMessage(OcaisseManagement.getMessage());
            ObllBase.setDetailmessage(OcaisseManagement.getDetailmessage());

                    // ObllBase.persiste(OTCoffreCaisse);
            //  new logger().oCategory.info("Creation  TModule " + OTCoffreCaisse.getLgUSERID().getStrFIRSTNAME() + " StrLabel " + OTCoffreCaisse.getIntAMOUNT());
        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("ID_COFFRE_CAISSE").toString().equals("init")) {

                bll.teller.caisseManagement OcaisseManagement = new bll.teller.caisseManagement(ObllBase.getOdataManager(), ObllBase.getOTUser());
                OcaisseManagement.sendCashToCaisseEmp(request.getParameter("lg_USER_ID"), Integer.parseInt(request.getParameter("int_AMOUNT")));

                ObllBase.setMessage(OcaisseManagement.getMessage());
                ObllBase.setDetailmessage(OcaisseManagement.getDetailmessage());

            } else {
                new logger().oCategory.info("Ref " + request.getParameter("ID_COFFRE_CAISSE").toString());

                TCoffreCaisse OTCoffreCaisse = ObllBase.getOdataManager().getEm().find(dal.TCoffreCaisse.class, request.getParameter("ID_COFFRE_CAISSE"));

                try {

                    if (OTUser != null) {
                        OTCoffreCaisse.setLgUSERID(OTUser);
                    }

                } catch (Exception e) {

                    new logger().OCategory.info("mauvais schema  : " + request.getParameter("ID_COFFRE_CAISSE"));

                    OTCoffreCaisse.setIntAMOUNT(new Double(Integer.parseInt(request.getParameter("int_AMOUNT"))));

                    ObllBase.persiste(OTCoffreCaisse);
                    new logger().oCategory.info("Creation  TModule " + OTCoffreCaisse.getIdCoffreCaisse() + " StrLabel " + OTCoffreCaisse.getStrSTATUT());

                }
            }
        } else if (request.getParameter("mode").toString().equals("delete")) {

            TCoffreCaisse OTCoffreCaisse = ObllBase.getOdataManager().getEm().find(dal.TCoffreCaisse.class, request.getParameter("ID_COFFRE_CAISSE"));

            if (!ObllBase.delete(OTCoffreCaisse)) {
                ObllBase.setDetailmessage("Impossible de supprimer");
            }

            new logger().oCategory.info("Suppression du menu " + request.getParameter("ID_COFFRE_CAISSE").toString());

        } else {
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        //result = "{success:1,ID_COFFRE_CAISSE: \"" + OTCoffreCaisse.getIdCoffreCaisse() + "\" }";
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }

    /*  if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
     result = "{success:1,ID_COFFRE_CAISSE: \"" + OTCoffreCaisse.getIdCoffreCaisse() + "\" }";  
     } else {
     result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
     }*/
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>