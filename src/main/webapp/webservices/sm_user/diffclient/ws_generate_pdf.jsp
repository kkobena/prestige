<%@page import="bll.common.Parameter"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.printer.PrintManangement"%>
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
    dataManager OdataManager = new dataManager();

%>


<!-- fin logic de gestion des page -->

<%    String str_Date_Debut = "", str_Date_Fin = "", OdateDebut = "", lg_FAMILLE_ID = "%%", str_ACTION = "%%", OdateFin = "", 
        search_value = "%%", lg_USER_ID = "%%", lg_EMPLACEMENT_ID = "";
    Date dt_Date_Debut, dt_Date_Fin;

    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);


    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }
    if (request.getParameter("str_ACTION") != null && !request.getParameter("str_ACTION").equalsIgnoreCase("")) {
        str_ACTION = request.getParameter("str_ACTION");
        new logger().OCategory.info("str_ACTION " + lg_USER_ID);
    }

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    if (str_Date_Fin.equalsIgnoreCase("") || str_Date_Fin == null) {
        dt_Date_Fin = new Date();
        OdateFin = date.DateToString(dt_Date_Fin, date.formatterMysqlShort2);
    } else {
        dt_Date_Fin = date.stringToDate(str_Date_Fin, date.formatterMysqlShort);
        OdateFin = date.DateToString(dt_Date_Fin, date.formatterMysqlShort2);
    }

    dt_Date_Fin = date.getDate(OdateFin, "23:59");
    //dt_Date_Fin = date.GetNewDate(1);
    // OdateFin = date.DateToString(dt_Date_Fin, date.formatterMysql);
    new logger().OCategory.info("dt_Date_Fin *** " + dt_Date_Fin + " OdateFin *** " + OdateFin);
    if (str_Date_Debut.equalsIgnoreCase("") || str_Date_Debut == null) {
        dt_Date_Debut = new Date();
        OdateDebut = date.DateToString(dt_Date_Debut, date.formatterMysqlShort2);
    } else {
        dt_Date_Debut = date.stringToDate(str_Date_Debut, date.formatterMysqlShort);
        OdateDebut = date.DateToString(dt_Date_Debut, date.formatterMysqlShort2);

    }
    dt_Date_Debut = date.getDate(OdateDebut, "00:00");
    new logger().OCategory.info("dt_Date_Debut *** " + dt_Date_Debut + " OdateDebut *** " + OdateDebut);

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date date = new date();
    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_liste_differe";
    String report_generate_file = date.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "liste_differe_" + report_generate_file);
    new logger().OCategory.info("Dans edition scr_report_file " + scr_report_file);
    
    privilege Oprivilege = new privilege(OdataManager, OTUser);
     lg_EMPLACEMENT_ID = (Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_SHOW_ALL_ACTIVITY) ? "%%" : OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");

    String P_H_CLT_INFOS = "Liste des Différés du "+ date.DateToString(date.stringToDate(str_Date_Debut, date.formatterMysqlShort), date.formatterShort)+ " AU "+ date.DateToString(date.stringToDate(str_Date_Fin, date.formatterMysqlShort), date.formatterShort);
    Map parameters = new HashMap();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_H_CC_RC", ((oTOfficine.getStrCOMPTECONTRIBUABLE() != null && !oTOfficine.getStrCOMPTECONTRIBUABLE().equalsIgnoreCase("")) ? "CC:" + oTOfficine.getStrCOMPTECONTRIBUABLE() : "") + ((oTOfficine.getStrREGISTRECOMMERCE() != null && !oTOfficine.getStrREGISTRECOMMERCE().equalsIgnoreCase("")) ? " / RC: " + oTOfficine.getStrREGISTRECOMMERCE() : ""));

    parameters.put("P_H_CI_RI", ((oTOfficine.getStrCENTREIMPOSITION() != null && !oTOfficine.getStrCENTREIMPOSITION().equalsIgnoreCase("")) ? "CI:" + oTOfficine.getStrCENTREIMPOSITION() : "") + ((oTOfficine.getStrREGISTREIMPOSITION() != null && !oTOfficine.getStrREGISTREIMPOSITION().equalsIgnoreCase("")) ? " / RI: " + oTOfficine.getStrREGISTREIMPOSITION() : ""));
    parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
    parameters.put("P_H_PHONE", (oTOfficine.getStrPHONE() != null ? "Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : ""));
    parameters.put("P_REFERENCE", lg_FAMILLE_ID);
    parameters.put("P_SEARCH", search_value + "%");
    parameters.put("dt_END", OdateFin);
    parameters.put("dt_BEGIN", OdateDebut);
    parameters.put("P_EMPLACEMENT", lg_EMPLACEMENT_ID);

    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS.toUpperCase());

    new logger().OCategory.info("getStrADRESSSEPOSTALE" + oTOfficine.getStrADRESSSEPOSTALE());
    OreportManager.BuildReport(parameters, Ojconnexion);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "liste_differe_" + report_generate_file);
   

%>



