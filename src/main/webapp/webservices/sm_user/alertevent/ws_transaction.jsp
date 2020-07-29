<%@page import="bll.gateway.outService.Iservice"%>
<%@page import="bll.gateway.outService.ServicesUpdatePriceFamille"%>
<%@page import="bll.gateway.outService.ServiceSoldeCaisseVeille"%>
<%@page import="dal.TAlertEventUserFone"%>
<%@page import="bll.gateway.outService.ServiceSoldeCaisse"%>
<%@page import="dal.TAlertEvent"%>
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
<%@page import="bll.userManagement.*"  %>



<%!     String str_FIRST_NAME = "%%", str_STATUT = "%%", str_LAST_NAME = "%%", lg_ROLE_ID = "", str_Event = "%%", str_PASSWORD = "%%", str_SMS_French_Text = "%%", str_FUNCTION = "%%", lg_SKIN_ID = "%%", lg_Language_ID = "%%";
    date str_LAST_CONNECTION_DATE;
    String str_MAIL_French_Text = "";
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TUser OTUser = null;
    user Ouser = null;

    List<TUser> lstTUserLogin = new ArrayList<TUser>();

%>




<%

    if (request.getParameter("str_FIRST_NAME") != null) {
        str_FIRST_NAME = request.getParameter("str_FIRST_NAME");
    }
    //str_MAIL_French_Text
    if (request.getParameter("str_MAIL_French_Text") != null) {
        str_MAIL_French_Text = request.getParameter("str_MAIL_French_Text");
        new logger().oCategory.info("str_MAIL_French_Text : " + str_MAIL_French_Text);
    }
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
    }
    if (request.getParameter("str_LAST_NAME") != null) {
        str_LAST_NAME = request.getParameter("str_LAST_NAME");
    }
    if (request.getParameter("str_Event") != null) {
        str_Event = request.getParameter("str_Event");
    }
    if (request.getParameter("str_SMS_French_Text") != null) {
        str_SMS_French_Text = request.getParameter("str_SMS_French_Text");
        new logger().oCategory.info("LOGIN   " + str_SMS_French_Text);
    }
    if (request.getParameter("str_FUNCTION") != null) {
        str_FUNCTION = request.getParameter("str_FUNCTION");
    }

    if (request.getParameter("lg_Language_ID") != null) {
        lg_Language_ID = request.getParameter("lg_Language_ID");
    }
    // lg_ROLE_ID
    if (request.getParameter("lg_ROLE_ID") != null) {
        lg_ROLE_ID = request.getParameter("lg_ROLE_ID");
    }

    if (request.getParameter("str_PASSWORD") != null) {
        str_PASSWORD = request.getParameter("str_PASSWORD");
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
    new logger().oCategory.info("ID " + request.getParameter("str_Event"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Dans create user   ");

            try {

                // List<TUser> lstTUserLogin = new ArrayList<TUser>();
                OdataManager.initEntityManager();
                lstTUserLogin = OdataManager.getEm().createQuery("SELECT t FROM TUser t WHERE t.strLOGIN LIKE ?1  AND t.strSTATUT LIKE ?2")
                        .setParameter(1, request.getParameter("str_SMS_French_Text").toString())
                        .setParameter(2, commonparameter.statut_enable).getResultList();

                new logger().oCategory.info("Resultat " + lstTUserLogin.size());

                if (lstTUserLogin.size() == 0) {

                    user Ouser = new user(OdataManager);
                    //  Ouser.createUser(str_SMS_French_Text, str_PASSWORD, str_MAIL_French_Text, str_FIRST_NAME, str_LAST_NAME, lg_ROLE_ID, lg_Language_ID);
                    new logger().oCategory.info("User creer avec succès   " + str_FIRST_NAME + " " + str_LAST_NAME);

                } else {
                    ObllBase.setMessage("0");
                    ObllBase.setDetailmessage("Desolé Login " + request.getParameter("str_SMS_French_Text") + "Deja Utilise");
                }

            } catch (Exception e) {

                new logger().oCategory.info("ERROR   ");
            }

        } else if (request.getParameter("mode").toString().equals("update")) {

            try {

                new logger().oCategory.info(str_Event + " " + str_SMS_French_Text + "  " + str_MAIL_French_Text + " ");
                TAlertEvent oTAlertEvent = (TAlertEvent) ObllBase.find(str_Event, new TAlertEvent());
                oTAlertEvent.setStrSMSFrenchText(str_SMS_French_Text);
                oTAlertEvent.setStrMAILFrenchText(str_MAIL_French_Text);
                ObllBase.persiste(oTAlertEvent);
                // user Ouser = new user(OdataManager);
                //  Ouser.updateMyUser(str_Event, str_SMS_French_Text, str_MAIL_French_Text, str_FIRST_NAME, str_LAST_NAME, lg_Language_ID);

                ObllBase.setMessage("0");
                ObllBase.setDetailmessage("User modifié avec SUCCES " + str_Event);
            } catch (Exception e) {
                ObllBase.setMessage("0");
                ObllBase.setDetailmessage("Echec de modificatuion User " + str_SMS_French_Text);
            }

        } else if (request.getParameter("mode").toString().equals("notify")) {

            try {

                new logger().OCategory.info(str_Event);

                //  user Ouser = new user(OdataManager);
                //  Ouser.updatePassword(str_Event, str_PASSWORD);
                TAlertEvent oTAlertEvent = (TAlertEvent) ObllBase.find(str_Event, new TAlertEvent());

                int int_price = 0;

                List<TAlertEventUserFone> lstTAlertEventUserFone = new ArrayList(oTAlertEvent.getTAlertEventUserFoneCollection());
                Iservice OService = null;
                if(str_Event.equals("N_GET_SOLDE_CAISSE")){
                    OService = new  ServiceSoldeCaisseVeille(OdataManager, OTUser);
                 
                }else if(str_Event.equals("N_UPDATE_FAMILLE_PRICE")){
                    OService = new ServicesUpdatePriceFamille(OdataManager, OTUser);
                 
                }
                
                
                for (int k = 0; k < lstTAlertEventUserFone.size(); k++) {
                    OService.doservice(lstTAlertEventUserFone.get(k));
                    
                }

                ObllBase.setMessage("1");
                ObllBase.setDetailmessage("Encour de traitement");
                
            } catch (Exception e) {
                ObllBase.setMessage("0");
                ObllBase.setDetailmessage("Echec de modificatuion MOT DE PASSE User " + str_SMS_French_Text);
            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            OTUser = ObllBase.getOdataManager().getEm().find(dal.TUser.class, request.getParameter("str_Event"));

            OTUser.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTUser);

            new logger().oCategory.info("Suppression de user " + request.getParameter("str_Event").toString());

        } else {
        }

    }

    String result;

    if (ObllBase.getMessage()
            .equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }

    new logger().OCategory.info("JSON " + result);


%>
<%=result%>