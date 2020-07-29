
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.printer.PrintManangement"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="dal.TOfficine"%>
<%@page import="cust_barcode.barecodeManager"%>
<%@page import="dal.TParameters"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.jconnexion"%>
<%@page import="report.reportManager"%>
<%@page import="dal.TTypeReglement"%>
<%@page import="dal.TCashTransaction"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.TPreenregistrementDetail"%>
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

<%
    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TParameters OTParameters;
    date key = new date();

%>


<!-- fin logic de gestion des page -->

<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    String lg_GROSSISTE_ID = "%%", search_value = "", P_KEY = "";
    String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin, lg_IMPRIMANTE_ID = "2";
    int int_NUMBER_COPY = 1;
    Date dtDEBUT, dtFin;

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_GROSSISTE_ID") != null && !request.getParameter("lg_GROSSISTE_ID").equals("")) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
        OdateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
        OdateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
    }

    dtFin = key.getDate(OdateFin, "23:59");
    //dt_Date_Fin = key.GetNewDate(1);
    OdateFin = key.DateToString(dtFin, key.formatterMysql);
    new logger().OCategory.info("dtFin *** " + dtFin + " OdateFin *** " + OdateFin);
    if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dtDEBUT = date.GetDebutMois();
        OdateDebut = key.DateToString(dtDEBUT, key.formatterMysqlShort2);
    } else {
        dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
        OdateDebut = key.DateToString(dtDEBUT, key.formatterMysqlShort2);

    }
    dtDEBUT = key.getDate(OdateDebut, "00:00");
    OdateDebut = key.DateToString(dtDEBUT, key.formatterMysql);

    new logger().OCategory.info("dtDEBUT ---- " + dtDEBUT + " OdateDebut ---- " + OdateDebut);

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();

    reportManager OreportManager = new reportManager();
    //  String scr_report_file = "rp_liste_etat_controle_achat";
    String scr_report_file = "rp_etat_controle";

    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "etat_controle_" + report_generate_file);
    new logger().OCategory.info("Dans edition scr_report_file " + scr_report_file);
    OdataManager.initEntityManager();
    TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");
      SimpleDateFormat formatterShort = new SimpleDateFormat("dd/MM/yyyy");
    String P_H_CLT_INFOS = "LISTE DES ETATS DE CONTRÔLE D'ACHATS\n DU "+formatterShort.format(dtDEBUT) +" AU "+formatterShort.format(dtFin);
    Map parameters = new HashMap();
 String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
        String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
    parameters.put("P_REFERENCE", lg_GROSSISTE_ID);
    parameters.put("P_SEARCH", "%"+search_value + "%");
    parameters.put("P_BEGIN", OdateDebut);
    parameters.put("P_END", OdateFin);
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    parameters.put("P_H_CC", (oTOfficine.getStrCOMPTECONTRIBUABLE() != null ? oTOfficine.getStrCOMPTECONTRIBUABLE() + "/" : ""));
    parameters.put("P_H_RC", oTOfficine.getStrREGISTRECOMMERCE());
    parameters.put("P_H_CI", (oTOfficine.getStrCENTREIMPOSITION() != null ? oTOfficine.getStrCENTREIMPOSITION() + "/" : ""));
    parameters.put("P_H_RI", oTOfficine.getStrREGISTREIMPOSITION());
   parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());

        String P_FOOTER_RC = "";

        if (oTOfficine.getStrREGISTRECOMMERCE() != null && !"".equals(oTOfficine.getStrREGISTRECOMMERCE())) {
            P_FOOTER_RC += "RC N°: " + oTOfficine.getStrREGISTRECOMMERCE();
        }

        if (oTOfficine.getStrCOMPTECONTRIBUABLE() != null && !"".equals(oTOfficine.getStrCOMPTECONTRIBUABLE())) {
            P_FOOTER_RC += " - CC N°: " + oTOfficine.getStrCOMPTECONTRIBUABLE();
        }
        if (oTOfficine.getStrREGISTREIMPOSITION() != null && !"".equals(oTOfficine.getStrREGISTREIMPOSITION())) {
            P_FOOTER_RC += " - Régime d'Imposition: " + oTOfficine.getStrREGISTREIMPOSITION();
        }
        if (oTOfficine.getStrCENTREIMPOSITION() != null && !"".equals(oTOfficine.getStrCENTREIMPOSITION())) {
            P_FOOTER_RC += " - Centre des Impôts: " + oTOfficine.getStrCENTREIMPOSITION();
        }

        if (oTOfficine.getStrPHONE() != null) {
                  String finalphonestring = oTOfficine.getStrPHONE() != null ? "Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
        if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
            String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
            for (String va : phone) {
                finalphonestring += " / " + conversion.PhoneNumberFormat( va);
            }
        }
       
        
        P_INSTITUTION_ADRESSE += " -  " +finalphonestring;
        }
        if (oTOfficine.getStrCOMPTEBANCAIRE() != null && !"".equals(oTOfficine.getStrCOMPTEBANCAIRE())) {
            P_INSTITUTION_ADRESSE += " - Compte Bancaire: " + oTOfficine.getStrCOMPTEBANCAIRE();
        }
        if (oTOfficine.getStrNUMCOMPTABLE() != null && !"".equals(oTOfficine.getStrNUMCOMPTABLE())) {
            P_INSTITUTION_ADRESSE += " - CPT N°: " + oTOfficine.getStrNUMCOMPTABLE();
        }
        parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
       
        parameters.put("P_FOOTER_RC", P_FOOTER_RC);
    OreportManager.BuildReport(parameters, Ojconnexion);

    Ojconnexion.CloseConnexion();

   response.sendRedirect("../../../data/reports/pdf/" + "etat_controle_" + report_generate_file);
   
%>



