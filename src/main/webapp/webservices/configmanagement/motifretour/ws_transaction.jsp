<%@page import="bll.configManagement.motifretourManagement"%>
<%@page import="dal.TMotifRetour"%>
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
    dal.TMotifRetour OTMotifRetour = null;
    String lg_MOTIF_RETOUR = "%%", str_CODE = "%%", str_LIBELLE = "%%";
%>


<%
    if (request.getParameter("lg_MOTIF_RETOUR") != null) {
        lg_MOTIF_RETOUR = request.getParameter("lg_MOTIF_RETOUR");
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
    }
    if (request.getParameter("str_LIBELLE") != null) {
        str_LIBELLE = request.getParameter("str_LIBELLE");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_MOTIF_RETOUR"));

    

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            // CREATION MODE CREATE
            new logger().oCategory.info("CREATION MODE CREATE");
            motifretourManagement OmotifretourManagement = new motifretourManagement(OdataManager);
            OmotifretourManagement.create(str_CODE, str_LIBELLE);

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_MOTIF_RETOUR").toString().equals("init")) {

                // CREATION MODE INIT
                new logger().oCategory.info("CREATION MODE INIT");
                
                new logger().oCategory.info("str_CODE "+str_CODE);
                new logger().oCategory.info("str_LIBELLE "+str_LIBELLE);
                motifretourManagement OmotifretourManagement = new motifretourManagement(OdataManager);
                OmotifretourManagement.create(str_CODE, str_LIBELLE);

            } else {

                // MAJ
                new logger().oCategory.info("CREATION MODE INIT");
                motifretourManagement OmotifretourManagement = new motifretourManagement(OdataManager);
                OmotifretourManagement.update(lg_MOTIF_RETOUR, str_CODE, str_LIBELLE);

                //new logger().oCategory.info("Mise a jour OTMotifRetour " + OTMotifRetour.getLgMOTIFRETOUR() + " StrLabel " + OTMotifRetour.getStrLIBELLE());

            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            motifretourManagement OmotifretourManagement = new motifretourManagement(OdataManager);
            OmotifretourManagement.deleted(lg_MOTIF_RETOUR);
            new logger().oCategory.info("Suppression de lg_MOTIF_RETOUR " + request.getParameter("lg_MOTIF_RETOUR").toString());

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