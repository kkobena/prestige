<%@page import="java.io.File"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="java.io.File"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="org.apache.commons.fileupload.DiskFileUpload"%>
<%@page import="org.apache.commons.fileupload.FileUpload"%>
<%@page import="java.io.File"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.TTiersPayant"%>
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
<%@page import="bll.configManagement.familleManagement"  %>

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TTiersPayant OTTiersPayant = null;

    String lg_TIERS_PAYANT_ID = "%%", str_CODE_ORGANISME = "%%", str_NAME = "%%", str_FULLNAME = "%%",
            str_ADRESSE = "%%", str_MOBILE = "%%", str_TELEPHONE = "%%", str_MAIL = "%%",
            str_NUMERO_CAISSE_OFFICIEL = "%%", str_CENTRE_PAYEUR = "%%", str_CODE_REGROUPEMENT = "%%",
            str_CODE_COMPTABLE = "%%", str_CODE_PAIEMENT = "%%", str_CODE_EDIT_BORDEREAU = "%%",
            str_NUMERO_IDF_ORGANISME = "%%", str_CODE_DOC_COMPTOIRE = "%%",
            lg_VILLE_ID = "%%", lg_TYPE_TIERS_PAYANT_ID = "%%", lg_TYPE_CONTRAT_ID = "%%",
            lg_REGIMECAISSE_ID = "%%", lg_RISQUE_ID = "%%";

    double dbl_PLAFOND_CREDIT = 0.00, dbl_TAUX_REMBOURSEMENT = 0.00, dbl_SEUIL_MINIMUM = 0.00,
            dbl_POURCENTAGE_REMISE = 0.00, dbl_REMISE_FORFETAIRE = 0.00, dbl_MONTANT_F_CLIENT = 0.00,
            dbl_BASE_REMISE = 0.00;

    boolean bool_INTERDICTION = false, bool_PRENUM_FACT_SUBROGATOIRE = false, bool_ENABLED = false;

    int int_NUMERO_DECOMPTE = 0, int_NBRE_EXEMPLAIRE_BORD = 0, int_PERIODICITE_EDIT_BORD = 0;
    int int_DATE_DERNIERE_EDITION;

    Date dt_DELAI_PAIEMENT, dt_CREATED, dt_UPDATED;

%>



<%

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
 TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");

    // lg_TIERS_PAYANT_ID
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
    }
    // str_CODE_ORGANISME
    if (request.getParameter("str_CODE_ORGANISME") != null) {
        str_CODE_ORGANISME = request.getParameter("str_CODE_ORGANISME");
    }
    // str_NAME
    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
    }
    // str_FULLNAME
    if (request.getParameter("str_FULLNAME") != null) {
        str_FULLNAME = request.getParameter("str_FULLNAME");
    }
    // str_ADRESSE
    if (request.getParameter("str_ADRESSE") != null) {
        str_ADRESSE = request.getParameter("str_ADRESSE");
    }
    // str_MOBILE
    if (request.getParameter("str_MOBILE") != null) {
        str_MOBILE = request.getParameter("str_MOBILE");
    }
    // str_TELEPHONE
    if (request.getParameter("str_TELEPHONE") != null) {
        str_TELEPHONE = request.getParameter("str_TELEPHONE");
    }
    // str_MAIL
    if (request.getParameter("str_MAIL") != null) {
        str_MAIL = request.getParameter("str_MAIL");
    }
    // dbl_PLAFOND_CREDIT   
    if (request.getParameter("dbl_PLAFOND_CREDIT") != null) {
        dbl_PLAFOND_CREDIT = Double.parseDouble(request.getParameter("dbl_PLAFOND_CREDIT"));
    }
    // dbl_TAUX_REMBOURSEMENT
    if (request.getParameter("dbl_TAUX_REMBOURSEMENT") != null) {
        dbl_TAUX_REMBOURSEMENT = Double.parseDouble(request.getParameter("dbl_TAUX_REMBOURSEMENT"));
    }
    // str_NUMERO_CAISSE_OFFICIEL
    if (request.getParameter("str_NUMERO_CAISSE_OFFICIEL") != null) {
        str_NUMERO_CAISSE_OFFICIEL = request.getParameter("str_NUMERO_CAISSE_OFFICIEL");
    }
    // str_CENTRE_PAYEUR
    if (request.getParameter("str_CENTRE_PAYEUR") != null) {
        str_CENTRE_PAYEUR = request.getParameter("str_CENTRE_PAYEUR");
    }
    // str_CODE_REGROUPEMENT
    if (request.getParameter("str_CODE_REGROUPEMENT") != null) {
        str_CODE_REGROUPEMENT = request.getParameter("str_CODE_REGROUPEMENT");
    }
    // dbl_SEUIL_MINIMUM
    if (request.getParameter("dbl_SEUIL_MINIMUM") != null) {
        dbl_SEUIL_MINIMUM = Double.parseDouble(request.getParameter("dbl_SEUIL_MINIMUM"));
    }
    // bool_INTERDICTION
    if (request.getParameter("bool_INTERDICTION") != null) {
        bool_INTERDICTION = Boolean.parseBoolean(request.getParameter("bool_INTERDICTION"));
    }
    // str_CODE_COMPTABLE
    if (request.getParameter("str_CODE_COMPTABLE") != null) {
        str_CODE_COMPTABLE = request.getParameter("str_CODE_COMPTABLE");
    }
    // bool_PRENUM_FACT_SUBROGATOIRE
    if (request.getParameter("bool_PRENUM_FACT_SUBROGATOIRE") != null) {
        bool_PRENUM_FACT_SUBROGATOIRE = Boolean.parseBoolean(request.getParameter("bool_PRENUM_FACT_SUBROGATOIRE"));
    }
    // int_NUMERO_DECOMPTE
    if (request.getParameter("int_NUMERO_DECOMPTE") != null) {
        int_NUMERO_DECOMPTE = Integer.parseInt(request.getParameter("int_NUMERO_DECOMPTE"));
    }
    // str_CODE_PAIEMENT
    if (request.getParameter("str_CODE_PAIEMENT") != null) {
        str_CODE_PAIEMENT = request.getParameter("str_CODE_PAIEMENT");
    }
    // dt_DELAI_PAIEMENT  
    if (request.getParameter("dt_DELAI_PAIEMENT") != null) {
        dt_DELAI_PAIEMENT = ObllBase.getKey().stringToDate(request.getParameter("dt_DELAI_PAIEMENT"), key.formatterOrange);
    }
    // dbl_POURCENTAGE_REMISE
    if (request.getParameter("dbl_POURCENTAGE_REMISE") != null) {
        dbl_POURCENTAGE_REMISE = Double.parseDouble(request.getParameter("dbl_POURCENTAGE_REMISE"));
    }
    // dbl_REMISE_FORFETAIRE
    if (request.getParameter("dbl_REMISE_FORFETAIRE") != null) {
        dbl_REMISE_FORFETAIRE = Double.parseDouble(request.getParameter("dbl_REMISE_FORFETAIRE"));
    }
    // str_CODE_EDIT_BORDEREAU
    if (request.getParameter("str_CODE_EDIT_BORDEREAU") != null) {
        str_CODE_EDIT_BORDEREAU = request.getParameter("str_CODE_EDIT_BORDEREAU");
    }
    // int_NBRE_EXEMPLAIRE_BORD
    if (request.getParameter("int_NBRE_EXEMPLAIRE_BORD") != null) {
        int_NBRE_EXEMPLAIRE_BORD = Integer.parseInt(request.getParameter("int_NBRE_EXEMPLAIRE_BORD"));
    }
    // int_PERIODICITE_EDIT_BORD
    if (request.getParameter("int_PERIODICITE_EDIT_BORD") != null) {
        int_PERIODICITE_EDIT_BORD = Integer.parseInt(request.getParameter("int_PERIODICITE_EDIT_BORD"));
    }
    // int_DATE_DERNIERE_EDITION
    if (request.getParameter("int_DATE_DERNIERE_EDITION") != null) {
        int_DATE_DERNIERE_EDITION = Integer.parseInt(request.getParameter("int_DATE_DERNIERE_EDITION"));
    }

    // str_NUMERO_IDF_ORGANISME
    if (request.getParameter("str_NUMERO_IDF_ORGANISME") != null) {
        str_NUMERO_IDF_ORGANISME = request.getParameter("str_NUMERO_IDF_ORGANISME");
    }
    // dbl_MONTANT_F_CLIENT
    if (request.getParameter("dbl_MONTANT_F_CLIENT") != null) {
        dbl_MONTANT_F_CLIENT = Double.parseDouble(request.getParameter("dbl_MONTANT_F_CLIENT"));
    }
    // dbl_BASE_REMISE
    if (request.getParameter("dbl_BASE_REMISE") != null) {
        dbl_BASE_REMISE = Double.parseDouble(request.getParameter("dbl_BASE_REMISE"));
    }
    // str_CODE_DOC_COMPTOIRE
    if (request.getParameter("str_CODE_DOC_COMPTOIRE") != null) {
        str_CODE_DOC_COMPTOIRE = request.getParameter("str_CODE_DOC_COMPTOIRE");
    }
    // bool_ENABLED
    if (request.getParameter("bool_ENABLED") != null) {
        bool_ENABLED = Boolean.parseBoolean(request.getParameter("bool_ENABLED"));
    }
    // lg_VILLE_ID
    if (request.getParameter("lg_VILLE_ID") != null) {
        lg_VILLE_ID = request.getParameter("lg_VILLE_ID");
    }
    // lg_TYPE_TIERS_PAYANT_ID
    if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID");
    }
    // lg_TYPE_CONTRAT_ID
    if (request.getParameter("lg_TYPE_CONTRAT_ID") != null) {
        lg_TYPE_CONTRAT_ID = request.getParameter("lg_TYPE_CONTRAT_ID");
    }
    // lg_REGIMECAISSE_ID
    if (request.getParameter("lg_REGIMECAISSE_ID") != null) {
        lg_REGIMECAISSE_ID = request.getParameter("lg_REGIMECAISSE_ID");
    }
    // lg_RISQUE_ID
    if (request.getParameter("lg_RISQUE_ID") != null) {
        lg_RISQUE_ID = request.getParameter("lg_RISQUE_ID");
    }

    // ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_TIERS_PAYANT_ID"));

    // new logger().oCategory.info("lg_OPTIMISATION_QUANTITE_ID   @@@@@@@@@@@@@@@@     " + request.getParameter("lg_OPTIMISATION_QUANTITE_ID"));
    if (request.getParameter("mode") != null) {
        tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("ID VILLE " + request.getParameter("lg_VILLE_ID"));
            new logger().oCategory.info("ID lg_REGIMECAISSE_ID " + request.getParameter("lg_REGIMECAISSE_ID"));
            new logger().OCategory.info(" str_CODE_ORGANISME " + str_CODE_ORGANISME + " str_NAME " + str_NAME + " str_FULLNAME " + str_FULLNAME + " str_ADRESSE " + str_ADRESSE + " str_MOBILE " + str_MOBILE);
            new logger().OCategory.info("str_TELEPHONE " + str_TELEPHONE + " str_MAIL " + str_MAIL + " dbl_PLAFOND_CREDIT " + dbl_PLAFOND_CREDIT + " dbl_TAUX_REMBOURSEMENT " + dbl_TAUX_REMBOURSEMENT + " str_NUMERO_CAISSE_OFFICIEL " + str_NUMERO_CAISSE_OFFICIEL + " str_CENTRE_PAYEUR " + str_CENTRE_PAYEUR);
            new logger().OCategory.info("str_CODE_REGROUPEMENT " + str_CODE_REGROUPEMENT + " dbl_SEUIL_MINIMUM " + dbl_SEUIL_MINIMUM + " bool_INTERDICTION " + bool_INTERDICTION + " str_CODE_COMPTABLE " + str_CODE_COMPTABLE + " bool_PRENUM_FACT_SUBROGATOIRE " + bool_PRENUM_FACT_SUBROGATOIRE + " int_NUMERO_DECOMPTE " + int_NUMERO_DECOMPTE + " lg_TYPE_CONTRAT_ID " + lg_TYPE_CONTRAT_ID + " lg_REGIMECAISSE_ID " + lg_REGIMECAISSE_ID);

            //tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
          //  OtierspayantManagement.create(str_CODE_ORGANISME, str_NAME, str_FULLNAME, str_ADRESSE, str_MOBILE, str_TELEPHONE, str_MAIL, dbl_PLAFOND_CREDIT, dbl_TAUX_REMBOURSEMENT, str_NUMERO_CAISSE_OFFICIEL, str_CENTRE_PAYEUR, str_CODE_REGROUPEMENT, dbl_SEUIL_MINIMUM, bool_INTERDICTION, str_CODE_COMPTABLE, bool_PRENUM_FACT_SUBROGATOIRE, int_NUMERO_DECOMPTE, str_CODE_PAIEMENT, dt_DELAI_PAIEMENT, dbl_POURCENTAGE_REMISE, dbl_REMISE_FORFETAIRE, str_CODE_EDIT_BORDEREAU, int_NBRE_EXEMPLAIRE_BORD, int_PERIODICITE_EDIT_BORD, int_DATE_DERNIERE_EDITION, str_NUMERO_IDF_ORGANISME, dbl_MONTANT_F_CLIENT, dbl_BASE_REMISE, str_CODE_DOC_COMPTOIRE, bool_ENABLED, lg_VILLE_ID, lg_TYPE_TIERS_PAYANT_ID, lg_TYPE_CONTRAT_ID, lg_REGIMECAISSE_ID, lg_RISQUE_ID);
            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());
            //  new logger().oCategory.info("creation OTTiersPayant " + OTTiersPayant.getStrNAME());

        } else if (request.getParameter("mode").toString().equals("update")) {

         //   OtierspayantManagement.update(lg_TIERS_PAYANT_ID, str_CODE_ORGANISME, str_NAME, str_FULLNAME, str_ADRESSE, str_MOBILE, str_TELEPHONE, str_MAIL, dbl_PLAFOND_CREDIT, dbl_TAUX_REMBOURSEMENT, str_NUMERO_CAISSE_OFFICIEL, str_CENTRE_PAYEUR, str_CODE_REGROUPEMENT, dbl_SEUIL_MINIMUM, bool_INTERDICTION, str_CODE_COMPTABLE, bool_PRENUM_FACT_SUBROGATOIRE, int_NUMERO_DECOMPTE, str_CODE_PAIEMENT, dt_DELAI_PAIEMENT, dbl_POURCENTAGE_REMISE, dbl_REMISE_FORFETAIRE, str_CODE_EDIT_BORDEREAU, int_NBRE_EXEMPLAIRE_BORD, int_PERIODICITE_EDIT_BORD, int_DATE_DERNIERE_EDITION, str_NUMERO_IDF_ORGANISME, dbl_MONTANT_F_CLIENT, dbl_BASE_REMISE, str_CODE_DOC_COMPTOIRE, bool_ENABLED, lg_VILLE_ID, lg_TYPE_TIERS_PAYANT_ID, lg_TYPE_CONTRAT_ID, lg_REGIMECAISSE_ID, lg_RISQUE_ID);
            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());

        } else if (request.getParameter("mode").toString().equals("updatephoto")) {

            try {

                //tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
                //OtierspayantManagement.update(lg_TIERS_PAYANT_ID, str_CODE_ORGANISME, str_NAME, str_FULLNAME, str_ADRESSE, str_MOBILE, str_TELEPHONE, str_MAIL, dbl_PLAFOND_CREDIT, dbl_TAUX_REMBOURSEMENT, str_NUMERO_CAISSE_OFFICIEL, str_CENTRE_PAYEUR, str_CODE_REGROUPEMENT, dbl_SEUIL_MINIMUM, bool_INTERDICTION, str_CODE_COMPTABLE, bool_PRENUM_FACT_SUBROGATOIRE, int_NUMERO_DECOMPTE, str_CODE_PAIEMENT, dt_DELAI_PAIEMENT, dbl_POURCENTAGE_REMISE, dbl_REMISE_FORFETAIRE, str_CODE_EDIT_BORDEREAU, int_NBRE_EXEMPLAIRE_BORD, int_PERIODICITE_EDIT_BORD, int_DATE_DERNIERE_EDITION, str_NUMERO_IDF_ORGANISME, dbl_MONTANT_F_CLIENT, dbl_BASE_REMISE, str_CODE_DOC_COMPTOIRE, bool_ENABLED, lg_VILLE_ID, lg_TYPE_TIERS_PAYANT_ID, lg_TYPE_CONTRAT_ID, lg_REGIMECAISSE_ID, lg_RISQUE_ID);
                //code gestion uoload
                boolean isMultipart = FileUpload.isMultipartContent(request);
                if (!isMultipart) {
                //request.setAttribute("msg", "Request was not multipart!");
                    //request.getRequestDispatcher("msg.jsp").forward(request, response);
                    new logger().OCategory.info("Erreur d'imporation");
                    return;
                }
                DiskFileUpload upload = new DiskFileUpload();
                List items = upload.parseRequest(request);
                Iterator itr = items.iterator();
                while (itr.hasNext()) {
                    FileItem item = (FileItem) itr.next();
                    if (item.isFormField()) {
                        String fieldName = item.getFieldName();
                        new logger().OCategory.info("fieldName "+fieldName);
                    } else {
                        String fileName = "";   // The file name the user entered.
                        String extension = "";  // The extension.

                        fileName = item.getName();
                        int dotPos = fileName.lastIndexOf(".");
                        extension = fileName.substring(dotPos);
                        if (extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png")) {
                            try {
                                File fullFile = new File(item.getName());
                                String New_Name_Of_File = fullFile.getName();
                                new logger().OCategory.info("nom fichier " + jdom.path_photo_absolute + "pic_customer\\" + New_Name_Of_File);
                                File savedFile = new File(jdom.path_photo_absolute + "pic_customer\\", New_Name_Of_File);
                                item.write(savedFile);
                                String file_final_name = jdom.path_photo_absolute + "pic_customer\\" + New_Name_Of_File;

                                new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID );
                                OtierspayantManagement.updatePhotoTiersPayant(lg_TIERS_PAYANT_ID, New_Name_Of_File);
                                ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
                                ObllBase.setMessage(OtierspayantManagement.getMessage());
                            } catch (Exception e) {
                                ObllBase.setDetailmessage("Erreur lors du deplacement du fichier");
                                new logger().OCategory.info("Erreur lors du deplacement du fichier");
                                e.printStackTrace();
                                ObllBase.setMessage(commonparameter.PROCESS_FAILED);
                            }
                        } else {
                            new logger().OCategory.info("Extension du fichier  " + extension);
                        }
                    }
                }
               
            } catch (Exception e) {

                new logger().OCategory.info("Error  : " + e.toString());

            }

        } else if (request.getParameter("mode").toString().equals("delete")) {

            //tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
            OtierspayantManagement.delete(lg_TIERS_PAYANT_ID);
            new logger().oCategory.info("Suppression de TP " + request.getParameter("lg_TIERS_PAYANT_ID").toString());

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