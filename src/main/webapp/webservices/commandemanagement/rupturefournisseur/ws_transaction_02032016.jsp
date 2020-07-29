<%@page import="bll.commandeManagement.retourFournisseurManagement"%>
<%@page import="bll.commandeManagement.bonLivraisonManagement"%>
<%@page import="dal.TFamille"%>
<%@page import="dal.TGrossiste"%>
<%@page import="dal.TFamilleGrossiste"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="dal.TRetourFournisseur"%>
<%@page import="dal.TTypeRemise"%>
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

    dal.TRetourFournisseur OTRetourFournisseur = null;
    dal.TRetourFournisseurDetail OTRetourFournisseurDetail = null;
    dal.TFamille OTFamille = null;
    dal.TGrossiste OTGrossiste = null;
    dal.TFamilleStock OTFamillestock = null;

    String mode = "%%";

    // t_retour_fournisseur
    String lg_RETOUR_FRS_ID = "%%", str_REF_RETOUR_FRS = "%%", lg_GROSSISTE_ID = "%%",
            lg_BON_LIVRAISON_ID = "%%", str_STATUT = "%%", str_REPONSE_FRS = "%%",
            str_COMMENTAIRE = "%%", str_REF = "%%";

    Date dt_DATE;
    // t_retour_fournisseur_detail
    String lg_RETOUR_FRS_DETAIL = "%%", lg_FAMILLE_ID = "%%", lg_MOTIF_RETOUR = "%%", str_RPSE_FRS = "%%";
    int int_NUMBER_RETURN = 0, int_STOCK = 0;

%>


<%
    if (request.getParameter("lg_RETOUR_FRS_ID") != null) {
        lg_RETOUR_FRS_ID = request.getParameter("lg_RETOUR_FRS_ID");
    }
    if (request.getParameter("str_REF_RETOUR_FRS") != null) {
        str_REF_RETOUR_FRS = request.getParameter("str_REF_RETOUR_FRS");
    }
    if (request.getParameter("lg_GROSSISTE_ID") != null) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
    }
    if (request.getParameter("lg_BON_LIVRAISON_ID") != null) {
        lg_BON_LIVRAISON_ID = request.getParameter("lg_BON_LIVRAISON_ID");
    }
    if (request.getParameter("str_REPONSE_FRS") != null) {
        str_REPONSE_FRS = request.getParameter("str_REPONSE_FRS");
    }
    if (request.getParameter("str_COMMENTAIRE") != null) {
        str_COMMENTAIRE = request.getParameter("str_COMMENTAIRE");
    }
    if (request.getParameter("dt_DATE") != null) {
        dt_DATE = date.stringToDate(request.getParameter("dt_DATE"));
        new logger().oCategory.info("dt_DATE : " + dt_DATE);
    }
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
    }

    /*str_RPSE_FRS = "%%";
     int int_NUMBER_RETURN = 0, int_STOCK = 0*/
    if (request.getParameter("lg_RETOUR_FRS_DETAIL") != null) {
        lg_RETOUR_FRS_DETAIL = request.getParameter("lg_RETOUR_FRS_DETAIL");
    }
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
    }
    if (request.getParameter("lg_MOTIF_RETOUR") != null) {
        lg_MOTIF_RETOUR = request.getParameter("lg_MOTIF_RETOUR");
    }
    if (request.getParameter("str_RPSE_FRS") != null) {
        str_RPSE_FRS = request.getParameter("str_RPSE_FRS");
    }
    if (request.getParameter("int_NUMBER_RETURN") != null) {
        int_NUMBER_RETURN = Integer.parseInt(request.getParameter("int_NUMBER_RETURN"));
    }
    if (request.getParameter("int_STOCK") != null) {
        int_STOCK = Integer.parseInt(request.getParameter("int_STOCK"));
        new logger().oCategory.info("int_STOCK : " + int_STOCK);
    }
    if (request.getParameter("mode") != null) {
        mode = request.getParameter("mode");
        new logger().oCategory.info("mode : " + mode);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("RETOUR FRS WS TRANSACTION");
    new logger().oCategory.info("RETOUR FRS WS TRANSACTION");

    bll.commandeManagement.retourFournisseurManagement OretourFournisseurManagement = new retourFournisseurManagement(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {

        OTRetourFournisseur = OretourFournisseurManagement.FindTRetourFournisseur(lg_RETOUR_FRS_ID);
        //MODE CREATION
        if (request.getParameter("mode").toString().equals("create")) {

            new logger().oCategory.info("EN MODE CREATE");
            try {
                
                new logger().oCategory.info("1-TRY lg_MOTIF_RETOUR " + lg_MOTIF_RETOUR);
                
                new logger().oCategory.info("1-TRY lg_BON_LIVRAISON_ID " + lg_BON_LIVRAISON_ID);
                new logger().oCategory.info("1-TRY str_REPONSE_FRS " + str_REPONSE_FRS);
                new logger().oCategory.info("1-TRY str_COMMENTAIRE " + str_COMMENTAIRE);
                
                //String lg_BON_LIVRAISON_ID, String str_REPONSE_FRS, String str_COMMENTAIRE
                
                OTRetourFournisseurDetail = OretourFournisseurManagement.createTRetourFournisseurDetail(lg_RETOUR_FRS_ID, lg_FAMILLE_ID, lg_MOTIF_RETOUR, int_NUMBER_RETURN, str_RPSE_FRS, lg_BON_LIVRAISON_ID, str_REPONSE_FRS, str_COMMENTAIRE);
                new logger().oCategory.info("2-TRY OTRetourFournisseurDetail " + OTRetourFournisseurDetail);
            
            } catch (Exception E) {

                OTFamille = (TFamille) ObllBase.find(lg_FAMILLE_ID, new TFamille());
                new logger().oCategory.info("1-catch OTFamille " + OTFamille);

                OTRetourFournisseurDetail = OretourFournisseurManagement.AddToTRetourFournisseurDetail(lg_BON_LIVRAISON_ID, str_REPONSE_FRS, str_COMMENTAIRE, lg_MOTIF_RETOUR, OTFamille, OTRetourFournisseur, int_NUMBER_RETURN);

                new logger().OCategory.info("*** Error OTRetourFournisseur inexistant  ***" + E.toString());

            }
            
            str_REF = OTRetourFournisseurDetail.getLgRETOURFRSID().getLgRETOURFRSID();
            new logger().oCategory.info(" str_REF " + str_REF);

            //MODE MODIFICATION
        } else if (request.getParameter("mode").toString().equals("update")) {

            new logger().OCategory.info("NOUS SOMMES DANS MODE " + "update");

            new logger().OCategory.info("update details sugg");
            new logger().OCategory.info(" *** lg_ORDER_DETAIL_ID  *** " + lg_RETOUR_FRS_DETAIL);
            new logger().OCategory.info(" *** lg_RETOUR_FRS_ID  *** " + lg_RETOUR_FRS_ID);
            new logger().OCategory.info("*** lg_FAMILLE_ID *** " + lg_FAMILLE_ID);
            new logger().OCategory.info("*** int_NUMBER_RETURN *** " + int_NUMBER_RETURN);

            OTRetourFournisseurDetail = OretourFournisseurManagement.UpdateTRetourFournisseurDetail(lg_RETOUR_FRS_DETAIL, lg_RETOUR_FRS_ID, lg_FAMILLE_ID, lg_MOTIF_RETOUR, int_NUMBER_RETURN, str_RPSE_FRS, lg_BON_LIVRAISON_ID, str_REPONSE_FRS, str_COMMENTAIRE);

            //MODE SUPPRESSION
        } else if (request.getParameter("mode").toString().equals("up")) {
            lg_GROSSISTE_ID = OTRetourFournisseur.getLgGROSSISTEID().getLgGROSSISTEID();

            OTRetourFournisseurDetail = OretourFournisseurManagement.UpdateTRetourFournisseurDetail(lg_RETOUR_FRS_DETAIL, lg_RETOUR_FRS_ID, lg_FAMILLE_ID, lg_MOTIF_RETOUR, int_NUMBER_RETURN, str_RPSE_FRS, lg_BON_LIVRAISON_ID, str_REPONSE_FRS, str_COMMENTAIRE);
        } else if (request.getParameter("mode").toString().equals("delete")) {

            new logger().OCategory.info(" *** OTRetourFournisseur  *** " + lg_RETOUR_FRS_ID);

            OretourFournisseurManagement.DeleteTRetourFournisseur(lg_RETOUR_FRS_ID);

            new logger().OCategory.info(" *** Suppression  *** " + lg_RETOUR_FRS_ID);

        } else if (request.getParameter("mode").toString().equals("deleteDetail")) {

            OTRetourFournisseurDetail = ObllBase.getOdataManager().getEm().find(dal.TRetourFournisseurDetail.class, lg_RETOUR_FRS_DETAIL);
            OTRetourFournisseurDetail.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTRetourFournisseurDetail);

        }
    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\", ref: \"" + str_REF+ "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\", ref: \"" + str_REF+ "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>