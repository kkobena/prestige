<%@page import="dal.TRetourFournisseurDetail"%>
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

<%
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();


%>


<%    String lg_RETOUR_FRS_ID = "", str_REF_RETOUR_FRS = "", lg_GROSSISTE_ID = "",
            lg_BON_LIVRAISON_ID = "", str_STATUT = "", str_REPONSE_FRS = "",
            str_COMMENTAIRE = "", str_REF = "";

    // t_retour_fournisseur_detail
    String lg_RETOUR_FRS_DETAIL = "", lg_FAMILLE_ID = "", lg_MOTIF_RETOUR = "", str_RPSE_FRS = "";
    int int_NUMBER_RETURN = 0, int_STOCK = 0;

    Date dt_DATE;

    TRetourFournisseur OTRetourFournisseur = null;
    TRetourFournisseurDetail OTRetourFournisseurDetail = null;

    if (request.getParameter("lg_RETOUR_FRS_ID") != null) {
        lg_RETOUR_FRS_ID = request.getParameter("lg_RETOUR_FRS_ID");
        new logger().OCategory.info("lg_RETOUR_FRS_ID " + lg_RETOUR_FRS_ID);
    }
    if (request.getParameter("str_REF_RETOUR_FRS") != null) {
        str_REF_RETOUR_FRS = request.getParameter("str_REF_RETOUR_FRS");
        new logger().OCategory.info("str_REF_RETOUR_FRS " + str_REF_RETOUR_FRS);
    }
    if (request.getParameter("lg_GROSSISTE_ID") != null) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }
    if (request.getParameter("lg_BON_LIVRAISON_ID") != null) {
        lg_BON_LIVRAISON_ID = request.getParameter("lg_BON_LIVRAISON_ID");
        new logger().OCategory.info("lg_BON_LIVRAISON_ID " + lg_BON_LIVRAISON_ID);

    }
    if (request.getParameter("str_REPONSE_FRS") != null) {
        str_REPONSE_FRS = request.getParameter("str_REPONSE_FRS");
        new logger().OCategory.info("str_REPONSE_FRS " + str_REPONSE_FRS);
    }
    if (request.getParameter("str_COMMENTAIRE") != null) {
        str_COMMENTAIRE = request.getParameter("str_COMMENTAIRE");
        new logger().OCategory.info("str_COMMENTAIRE " + str_COMMENTAIRE);
    }
    if (request.getParameter("dt_DATE") != null) {
        dt_DATE = date.stringToDate(request.getParameter("dt_DATE"));
        new logger().oCategory.info("dt_DATE : " + dt_DATE);
    }
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }

    /*str_RPSE_FRS = "";
     int int_NUMBER_RETURN = 0, int_STOCK = 0*/
    if (request.getParameter("lg_RETOUR_FRS_DETAIL") != null) {
        lg_RETOUR_FRS_DETAIL = request.getParameter("lg_RETOUR_FRS_DETAIL");
        new logger().OCategory.info("lg_RETOUR_FRS_DETAIL " + lg_RETOUR_FRS_DETAIL);
    }
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (request.getParameter("lg_MOTIF_RETOUR") != null) {
        lg_MOTIF_RETOUR = request.getParameter("lg_MOTIF_RETOUR");
        new logger().OCategory.info("lg_MOTIF_RETOUR " + lg_MOTIF_RETOUR);
    }
    if (request.getParameter("str_RPSE_FRS") != null) {
        str_RPSE_FRS = request.getParameter("str_RPSE_FRS");
        new logger().OCategory.info("str_RPSE_FRS " + str_RPSE_FRS);
    }
    if (request.getParameter("int_NUMBER_RETURN") != null) {
        int_NUMBER_RETURN = Integer.parseInt(request.getParameter("int_NUMBER_RETURN"));
        new logger().OCategory.info("int_NUMBER_RETURN " + int_NUMBER_RETURN);
    }
    if (request.getParameter("int_STOCK") != null) {
        int_STOCK = Integer.parseInt(request.getParameter("int_STOCK"));
        new logger().oCategory.info("int_STOCK : " + int_STOCK);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("Erreur serveur");
    retourFournisseurManagement OretourFournisseurManagement = new retourFournisseurManagement(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {

        // OTRetourFournisseur = OretourFournisseurManagement.FindTRetourFournisseur(lg_RETOUR_FRS_ID);
        //MODE CREATION
        if (request.getParameter("mode").equals("create")) {
            if (OretourFournisseurManagement.checkQuantityRetourIsAuthorize(lg_FAMILLE_ID, int_NUMBER_RETURN, lg_BON_LIVRAISON_ID)) {
                if (request.getParameter("lg_RETOUR_FRS_ID").equals("0")) {
                    new logger().OCategory.info("lg_RETOUR_FRS_ID initial  " + lg_RETOUR_FRS_ID);

                    OTRetourFournisseur = OretourFournisseurManagement.createTRetourFournisseur(lg_BON_LIVRAISON_ID, str_REPONSE_FRS, str_COMMENTAIRE);
                } else {
                    OTRetourFournisseur = OretourFournisseurManagement.UpdateTRetourFournisseur(lg_RETOUR_FRS_ID, lg_BON_LIVRAISON_ID, str_REPONSE_FRS, str_COMMENTAIRE);

                }

                if (OretourFournisseurManagement.getMessage() == commonparameter.PROCESS_SUCCESS) {
                    str_REF = OTRetourFournisseur.getLgRETOURFRSID();
                    new logger().OCategory.info("lg_RETOUR_FRS_ID nouveau " + str_REF);
                }
                OretourFournisseurManagement.createTRetourFournisseurDetail(OTRetourFournisseur.getLgRETOURFRSID(), lg_FAMILLE_ID, lg_MOTIF_RETOUR, int_NUMBER_RETURN, str_RPSE_FRS, OTRetourFournisseur.getLgBONLIVRAISONID().getLgBONLIVRAISONID(), str_REPONSE_FRS, str_COMMENTAIRE);

                ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
                ObllBase.setMessage(OretourFournisseurManagement.getMessage());
            } else {
                ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
                ObllBase.setMessage(OretourFournisseurManagement.getMessage());
            }

            //MODE MODIFICATION
        } else if (request.getParameter("mode").equals("update")) {
            if (OretourFournisseurManagement.checkQuantityRetourIsAuthorize(lg_FAMILLE_ID, int_NUMBER_RETURN, lg_BON_LIVRAISON_ID)) {
                OTRetourFournisseurDetail = OretourFournisseurManagement.UpdateTRetourFournisseurDetail(lg_RETOUR_FRS_DETAIL, lg_RETOUR_FRS_ID, lg_FAMILLE_ID, lg_MOTIF_RETOUR, int_NUMBER_RETURN, str_RPSE_FRS, lg_BON_LIVRAISON_ID, str_REPONSE_FRS, str_COMMENTAIRE);
            }
            ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
            ObllBase.setMessage(OretourFournisseurManagement.getMessage());

            //MODE SUPPRESSION
        } /*else if (request.getParameter("mode").toString().equals("up")) {
         lg_GROSSISTE_ID = OTRetourFournisseur.getLgGROSSISTEID().getLgGROSSISTEID();

         OTRetourFournisseurDetail = OretourFournisseurManagement.UpdateTRetourFournisseurDetail(lg_RETOUR_FRS_DETAIL, lg_RETOUR_FRS_ID, lg_FAMILLE_ID, lg_MOTIF_RETOUR, int_NUMBER_RETURN, str_RPSE_FRS, lg_BON_LIVRAISON_ID, str_REPONSE_FRS, str_COMMENTAIRE);
         } */ //MODE SUPPRESSION
        else if (request.getParameter("mode").equals("delete")) {

            OretourFournisseurManagement.DeleteTRetourFournisseur(lg_RETOUR_FRS_ID);
            ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
            ObllBase.setMessage(OretourFournisseurManagement.getMessage());

        } else if (request.getParameter("mode").equals("deleteDetail")) {
            OretourFournisseurManagement.DeleteTRetourFournisseurDetail(lg_RETOUR_FRS_DETAIL);
            ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
            ObllBase.setMessage(OretourFournisseurManagement.getMessage());

        } else if (request.getParameter("mode").equals("updateanswer")) {
            OretourFournisseurManagement.updateQuantiteReponse(lg_RETOUR_FRS_DETAIL, int_NUMBER_RETURN);
            ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
            ObllBase.setMessage(OretourFournisseurManagement.getMessage());
        } else if (request.getParameter("mode").equals("validerRupture")) {
            OretourFournisseurManagement.validerRupture(lg_RETOUR_FRS_ID, str_COMMENTAIRE, str_REPONSE_FRS);
            ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
            ObllBase.setMessage(OretourFournisseurManagement.getMessage());

        } else if (request.getParameter("mode").equals("passeretourfournisseur")) {
            OretourFournisseurManagement.sendRetourFourToCommandePasse(lg_RETOUR_FRS_ID);
            ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
            ObllBase.setMessage(OretourFournisseurManagement.getMessage());

        } else if (request.getParameter("mode").equals("Response")) {
            OretourFournisseurManagement.retourfournisseurResponse(lg_RETOUR_FRS_ID, str_REPONSE_FRS);
            ObllBase.setDetailmessage(OretourFournisseurManagement.getDetailmessage());
            ObllBase.setMessage(OretourFournisseurManagement.getMessage());

        }
    }
    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\", ref: \"" + str_REF + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\", ref: \"" + str_REF + "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>