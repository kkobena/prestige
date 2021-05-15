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
<%@page import="bll.configManagement.medecinManagement"  %>

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TMedecin OTMedecin = null;
    String lg_MEDECIN_ID = "%%",str_CODE_INTERNE = "%%" , str_FIRST_NAME = "%%", str_LAST_NAME = "%%", str_PHONE = "%%",
            str_MAIL = "%%", str_SEXE = "%%", str_ADRESSE = "%%", str_STATUT = "%%",
            str_Commentaire = "%%", lg_VILLE_ID = "%%", lg_SPECIALITE_ID = "%%";
%>

<%
    if (request.getParameter("lg_MEDECIN_ID") != null) {
        lg_MEDECIN_ID = request.getParameter("lg_MEDECIN_ID");
    }
    if (request.getParameter("str_CODE_INTERNE") != null) {
        str_CODE_INTERNE = request.getParameter("str_CODE_INTERNE");
    }    
    if (request.getParameter("str_FIRST_NAME") != null) {
        str_FIRST_NAME = request.getParameter("str_FIRST_NAME");
    }
    if (request.getParameter("str_LAST_NAME") != null) {
        str_LAST_NAME = request.getParameter("str_LAST_NAME");
    }
    if (request.getParameter("str_PHONE") != null) {
        str_PHONE = request.getParameter("str_PHONE");
    }
    if (request.getParameter("str_MAIL") != null) {
        str_MAIL = request.getParameter("str_MAIL");
    }
    if (request.getParameter("str_SEXE") != null) {
        str_SEXE = request.getParameter("str_SEXE");
    }
    if (request.getParameter("str_ADRESSE") != null) {
        str_ADRESSE = request.getParameter("str_ADRESSE");
    }
    if (request.getParameter("str_Commentaire") != null) {
        str_Commentaire = request.getParameter("str_Commentaire");
    }
    if (request.getParameter("lg_VILLE_ID") != null) {
        lg_VILLE_ID = request.getParameter("lg_VILLE_ID");
    }
    if (request.getParameter("lg_SPECIALITE_ID") != null) {
        lg_SPECIALITE_ID = request.getParameter("lg_SPECIALITE_ID");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_MEDECIN_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            try {

                medecinManagement OmedecinManagement = new medecinManagement(OdataManager);
                OmedecinManagement.create(str_CODE_INTERNE, str_FIRST_NAME, str_LAST_NAME, str_ADRESSE, str_PHONE, str_MAIL, str_SEXE, str_Commentaire, lg_VILLE_ID, lg_SPECIALITE_ID);
                ObllBase.setDetailmessage(" MEDECIN cree avec SUCCES");

            } catch (Exception Exc) {
                ObllBase.setDetailmessage("Impossible de creer MEDECIN");
            }

        } else if (request.getParameter("mode").toString().equals("update")) {

            try {
                medecinManagement OmedecinManagement = new medecinManagement(OdataManager);
                OmedecinManagement.update(lg_MEDECIN_ID, str_CODE_INTERNE, str_FIRST_NAME, str_LAST_NAME, str_ADRESSE, str_PHONE, str_MAIL, str_SEXE, str_Commentaire, lg_VILLE_ID, lg_SPECIALITE_ID);
                ObllBase.setDetailmessage("MEDECIN modifie avec SUCCES");

            } catch (Exception EX) {

                ObllBase.setDetailmessage("Echec de modification MEDECIN");
            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            try {
                OTMedecin = ObllBase.getOdataManager().getEm().find(dal.TMedecin.class, request.getParameter("lg_MEDECIN_ID"));
                OTMedecin.setStrSTATUT(commonparameter.statut_delete);
                ObllBase.persiste(OTMedecin);

                ObllBase.setDetailmessage("MEDECIN supprime avec SUCCES");
            } catch (Exception Ex) {

                ObllBase.setDetailmessage("Echec de suppression MEDECIN");
            }
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