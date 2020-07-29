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

<%    String search_value = "%%", lg_TIERS_PAYANT_ID = "%%", lg_TYPE_TIERS_PAYANT_ID = "%%", str_STATUT = commonparameter.statut_enable;
    int int_NUMBER_COPY = 1;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
       
    }

    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !request.getParameter("lg_TIERS_PAYANT_ID").equalsIgnoreCase("")) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID);
    }
    
     if (request.getParameter("str_STATUT") != null && !str_STATUT.equalsIgnoreCase("")) {
        str_STATUT = request.getParameter("str_STATUT").toString();
        new logger().OCategory.info("str_STATUT  = " + str_STATUT);
    }
    
    
    if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null && !request.getParameter("lg_TYPE_TIERS_PAYANT_ID").equalsIgnoreCase("")) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TYPE_TIERS_PAYANT_ID  = " + lg_TYPE_TIERS_PAYANT_ID);
    }

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date key = new date();
    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_liste_tierspayant";
    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "liste_tierspayant_" + report_generate_file);
    new logger().OCategory.info("Dans edition scr_report_file " + scr_report_file);
    OdataManager.initEntityManager();
    TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");

    String P_H_CLT_INFOS = "Liste des tiers payants "+(str_STATUT.equalsIgnoreCase(commonparameter.statut_enable) ? "" : "Désativés");
    Map parameters = new HashMap();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_H_CC_RC", ((oTOfficine.getStrCOMPTECONTRIBUABLE() != null && !oTOfficine.getStrCOMPTECONTRIBUABLE().equalsIgnoreCase("")) ? "CC:" + oTOfficine.getStrCOMPTECONTRIBUABLE() : "") + ((oTOfficine.getStrREGISTRECOMMERCE() != null && !oTOfficine.getStrREGISTRECOMMERCE().equalsIgnoreCase("")) ? " / RC: " + oTOfficine.getStrREGISTRECOMMERCE() : ""));

    parameters.put("P_H_CI_RI", ((oTOfficine.getStrCENTREIMPOSITION() != null && !oTOfficine.getStrCENTREIMPOSITION().equalsIgnoreCase("")) ? "CI:" + oTOfficine.getStrCENTREIMPOSITION() : "") + ((oTOfficine.getStrREGISTREIMPOSITION() != null && !oTOfficine.getStrREGISTREIMPOSITION().equalsIgnoreCase("")) ? " / RI: " + oTOfficine.getStrREGISTREIMPOSITION() : ""));
    parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
   String finalphonestring = oTOfficine.getStrPHONE() != null ? "Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
            if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
                String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
                for (String va : phone) {
                    finalphonestring += " / " + conversion.PhoneNumberFormat(va);
                }
            }
            parameters.put("P_H_PHONE", finalphonestring);
       
    parameters.put("P_REFERENCE", lg_TIERS_PAYANT_ID);
    parameters.put("P_TYPE_TIERS_PAYANT", lg_TYPE_TIERS_PAYANT_ID);
    parameters.put("str_STATUT", str_STATUT);
    parameters.put("P_SEARCH", search_value + "%");

    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS.toUpperCase());

    new logger().OCategory.info("getStrADRESSSEPOSTALE" + oTOfficine.getStrADRESSSEPOSTALE());
    OreportManager.BuildReport(parameters, Ojconnexion);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "liste_tierspayant_" + report_generate_file);
  
%>



