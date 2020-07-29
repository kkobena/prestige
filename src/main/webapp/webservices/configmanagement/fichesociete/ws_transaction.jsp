<%@page import="dal.TFicheSociete"%>
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
<%@page import="bll.configManagement.ficheSocieteManagement"  %>


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_FICHE_SOCIETE_ID = "%%",str_CODE_INTERNE = "%%", str_LIBELLE_ENTREPRISE = "%%",
            str_TYPE_SOCIETE = "%%", str_CODE_REGROUPEMENT = "%%", str_CONTACTS_TELEPHONIQUES = "%%",
            str_COMPTE_COMPTABLE = "%%", str_DOMICIALIATION_BANCAIRE = "%%",
            str_RIB_SOCIETE = "%%", str_CODE_EXONERATION_TVA = "%%", str_CODE_REMISE = "%%",
            str_CODE_FACTURE = "%%", str_CODE_BON_LIVRAISON = "%%", str_RAISON_SOCIALE = "%%",
            str_BUREAU_DISTRIBUTEUR = "%%", lg_ESCOMPTE_SOCIETE_ID = "%%" ,
            lg_VILLE_ID = "%%", str_ADRESSE_PRINCIPALE = "%%", str_AUTRE_ADRESSE = "%%", str_CODE_POSTAL = "%%";
    int int_ECHEANCE_PAIEMENT;
    double dbl_CHIFFRE_AFFAIRE, dbl_REMISE_SUPPLEMENTAIRE, dbl_MONTANT_PORT;
    boolean bool_CLIENT_EN_COMPTE, bool_LIVRE, bool_EDIT_FACTION_FIN_VENTE; 

    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    dal.TFicheSociete OTFicheSociete = new dal.TFicheSociete();

%>

<%
    // lg_FICHE_SOCIETE_ID
    if (request.getParameter("lg_FICHE_SOCIETE_ID") != null) {
        lg_FICHE_SOCIETE_ID = request.getParameter("lg_FICHE_SOCIETE_ID");
    }
    // str_CODE_INTERNE
    if (request.getParameter("str_CODE_INTERNE") != null) {
        str_CODE_INTERNE = request.getParameter("str_CODE_INTERNE");
    }
    // str_LIBELLE_ENTREPRISE
    if (request.getParameter("str_LIBELLE_ENTREPRISE") != null) {
        str_LIBELLE_ENTREPRISE = request.getParameter("str_LIBELLE_ENTREPRISE");
    }
    // str_TYPE_SOCIETE
    if (request.getParameter("str_TYPE_SOCIETE") != null) {
        str_TYPE_SOCIETE = request.getParameter("str_TYPE_SOCIETE");
    }
    // str_CODE_REGROUPEMENT
    if (request.getParameter("str_CODE_REGROUPEMENT") != null) {
        str_CODE_REGROUPEMENT = request.getParameter("str_CODE_REGROUPEMENT");
    }
    // str_CONTACTS_TELEPHONIQUES
    if (request.getParameter("str_CONTACTS_TELEPHONIQUES") != null) {
        str_CONTACTS_TELEPHONIQUES = request.getParameter("str_CONTACTS_TELEPHONIQUES");
    }
    // str_COMPTE_COMPTABLE
    if (request.getParameter("str_COMPTE_COMPTABLE") != null) {
        str_COMPTE_COMPTABLE = request.getParameter("str_COMPTE_COMPTABLE");
    }
     // dbl_CHIFFRE_AFFAIRE
    if (request.getParameter("dbl_CHIFFRE_AFFAIRE") != null) {
        dbl_CHIFFRE_AFFAIRE = Double.parseDouble(request.getParameter("dbl_CHIFFRE_AFFAIRE"));
    }
     
    // str_DOMICIALIATION_BANCAIRE
    if (request.getParameter("str_DOMICIALIATION_BANCAIRE") != null) {
        str_DOMICIALIATION_BANCAIRE = request.getParameter("str_DOMICIALIATION_BANCAIRE");
    }    
    // str_RIB_SOCIETE
    if (request.getParameter("str_RIB_SOCIETE") != null) {
        str_RIB_SOCIETE = request.getParameter("str_RIB_SOCIETE");
    }
    // str_CODE_EXONERATION_TVA
    if (request.getParameter("str_CODE_EXONERATION_TVA") != null) {
        str_CODE_EXONERATION_TVA = request.getParameter("str_CODE_EXONERATION_TVA");
    }
    // str_CODE_REMISE
    if (request.getParameter("str_CODE_REMISE") != null) {
        str_CODE_REMISE = request.getParameter("str_CODE_REMISE");
    }
    // str_CODE_FACTURE
    if (request.getParameter("str_CODE_FACTURE") != null) {
        str_CODE_FACTURE = request.getParameter("str_CODE_FACTURE");
    }
    // str_CODE_BON_LIVRAISON
    if (request.getParameter("str_CODE_BON_LIVRAISON") != null) {
        str_CODE_BON_LIVRAISON = request.getParameter("str_CODE_BON_LIVRAISON");
    }
    // str_RAISON_SOCIALE
    if (request.getParameter("str_RAISON_SOCIALE") != null) {
        str_RAISON_SOCIALE = request.getParameter("str_RAISON_SOCIALE");
    }
    // str_BUREAU_DISTRIBUTEUR
    if (request.getParameter("str_BUREAU_DISTRIBUTEUR") != null) {
        str_BUREAU_DISTRIBUTEUR = request.getParameter("str_BUREAU_DISTRIBUTEUR");
    }
    
    // str_ADRESSE_PRINCIPALE
    if (request.getParameter("str_ADRESSE_PRINCIPALE") != null) {
        str_ADRESSE_PRINCIPALE = request.getParameter("str_ADRESSE_PRINCIPALE");
    }
    // str_AUTRE_ADRESSE
    if (request.getParameter("str_AUTRE_ADRESSE") != null) {
        str_AUTRE_ADRESSE = request.getParameter("str_AUTRE_ADRESSE");
    }
    
    // str_CODE_POSTAL
    if (request.getParameter("str_CODE_POSTAL") != null) {
        str_CODE_POSTAL = request.getParameter("str_CODE_POSTAL");
    }
    // str_CODE_POSTAL
    if (request.getParameter("int_ECHEANCE_PAIEMENT") != null) {
        int_ECHEANCE_PAIEMENT = Integer.parseInt(request.getParameter("int_ECHEANCE_PAIEMENT"));
    }
   
    // dbl_REMISE_SUPPLEMENTAIRE
    if (request.getParameter("dbl_REMISE_SUPPLEMENTAIRE") != null) {
        dbl_REMISE_SUPPLEMENTAIRE = Double.parseDouble(request.getParameter("dbl_REMISE_SUPPLEMENTAIRE"));
    }
    // dbl_MONTANT_PORT
    if (request.getParameter("dbl_MONTANT_PORT") != null) {
        dbl_MONTANT_PORT = Double.parseDouble(request.getParameter("dbl_MONTANT_PORT"));
    }
    // bool_CLIENT_EN_COMPTE
    if (request.getParameter("bool_CLIENT_EN_COMPTE") != null) {
        bool_CLIENT_EN_COMPTE = Boolean.parseBoolean(request.getParameter("bool_CLIENT_EN_COMPTE"));
    }
    // dbl_REMISE_SUPPLEMENTAIRE
    if (request.getParameter("bool_LIVRE") != null) {
        bool_LIVRE = Boolean.parseBoolean(request.getParameter("bool_LIVRE"));
    }
    // dbl_MONTANT_PORT
    if (request.getParameter("bool_EDIT_FACTION_FIN_VENTE") != null) {
        bool_EDIT_FACTION_FIN_VENTE = Boolean.parseBoolean(request.getParameter("bool_EDIT_FACTION_FIN_VENTE"));
    }
    // lg_ESCOMPTE_SOCIETE_ID
    if (request.getParameter("lg_ESCOMPTE_SOCIETE_ID") != null) {
        lg_ESCOMPTE_SOCIETE_ID = request.getParameter("lg_ESCOMPTE_SOCIETE_ID");
    }
    // lg_VILLE_ID
    if (request.getParameter("lg_VILLE_ID") != null) {
        lg_VILLE_ID = request.getParameter("lg_VILLE_ID");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_FICHE_SOCIETE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");

            ficheSocieteManagement OficheSocieteManagement = new ficheSocieteManagement(OdataManager);
            OficheSocieteManagement.create(str_CODE_INTERNE, str_LIBELLE_ENTREPRISE, str_TYPE_SOCIETE, str_CODE_REGROUPEMENT, str_CONTACTS_TELEPHONIQUES, str_COMPTE_COMPTABLE, dbl_CHIFFRE_AFFAIRE, str_DOMICIALIATION_BANCAIRE, str_RIB_SOCIETE, str_CODE_EXONERATION_TVA, str_CODE_REMISE, bool_CLIENT_EN_COMPTE, bool_LIVRE, dbl_REMISE_SUPPLEMENTAIRE, dbl_MONTANT_PORT, int_ECHEANCE_PAIEMENT, bool_EDIT_FACTION_FIN_VENTE, str_CODE_FACTURE, str_CODE_BON_LIVRAISON, str_RAISON_SOCIALE, str_ADRESSE_PRINCIPALE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_BUREAU_DISTRIBUTEUR, lg_VILLE_ID, lg_ESCOMPTE_SOCIETE_ID);

            new logger().oCategory.info("Creation effectuee avec succes");

        } else if (request.getParameter("mode").toString().equals("update")) {

            ficheSocieteManagement OficheSocieteManagement = new ficheSocieteManagement(OdataManager);
            OficheSocieteManagement.update(lg_FICHE_SOCIETE_ID, str_CODE_INTERNE, str_LIBELLE_ENTREPRISE, str_TYPE_SOCIETE, str_CODE_REGROUPEMENT, str_CONTACTS_TELEPHONIQUES, str_COMPTE_COMPTABLE, dbl_CHIFFRE_AFFAIRE, str_DOMICIALIATION_BANCAIRE, str_RIB_SOCIETE, str_CODE_EXONERATION_TVA, str_CODE_REMISE, bool_CLIENT_EN_COMPTE, bool_LIVRE, dbl_REMISE_SUPPLEMENTAIRE, dbl_MONTANT_PORT, int_ECHEANCE_PAIEMENT, bool_EDIT_FACTION_FIN_VENTE, str_CODE_FACTURE, str_CODE_BON_LIVRAISON, str_RAISON_SOCIALE, str_ADRESSE_PRINCIPALE, str_AUTRE_ADRESSE, str_CODE_POSTAL, str_BUREAU_DISTRIBUTEUR, lg_VILLE_ID, lg_ESCOMPTE_SOCIETE_ID);

            new logger().oCategory.info("Modification effectuee avec succes");

        } else if (request.getParameter("mode").toString().equals("delete")) {

            TFicheSociete OTFicheSociete = null;
            
            OTFicheSociete = ObllBase.getOdataManager().getEm().find(TFicheSociete.class, lg_FICHE_SOCIETE_ID);
            
            OTFicheSociete.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTFicheSociete);

            new logger().oCategory.info("Suppression du FicheSociete " + request.getParameter("lg_FICHE_SOCIETE_ID").toString());

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