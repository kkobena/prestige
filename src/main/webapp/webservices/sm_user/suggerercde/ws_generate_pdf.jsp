<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TSuggestionOrder"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="java.awt.print.PageFormat"%>
<%@page import="java.awt.print.Paper"%>
<%@page import="dal.TPreenregistrementCompteClient"%>
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
    bllBase ObllBase = new bllBase();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%
    String lg_SUGGESTION_ORDER_ID = "";

    if (request.getParameter("lg_SUGGESTION_ORDER_ID") != null) {
        lg_SUGGESTION_ORDER_ID = request.getParameter("lg_SUGGESTION_ORDER_ID");
        new logger().OCategory.info("lg_SUGGESTION_ORDER_ID " + lg_SUGGESTION_ORDER_ID);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date key = new date();
    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_suggestion";
    // String scr_report_file = "rp_liste_retrocession_final";
    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "suggestion_" + report_generate_file);
    new logger().OCategory.info("Dans edition scr_report_file " + scr_report_file);
    bllBase obllBase = new bllBase();
    obllBase.checkDatamanager();
    TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");
    TSuggestionOrder OTSuggestionOrder = obllBase.getOdataManager().getEm().find(TSuggestionOrder.class, lg_SUGGESTION_ORDER_ID);

   
    String P_H_CLT_INFOS = "SUGGESTION DE REAPPROVISIONNEMENT " + OTSuggestionOrder.getStrREF();
    Map parameters = new HashMap();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    
    parameters.put("P_REFERENCE", OTSuggestionOrder.getLgSUGGESTIONORDERID());
    parameters.put("P_H_CC_RC", (oTOfficine.getStrCOMPTECONTRIBUABLE() != null ? "CC:" + oTOfficine.getStrCOMPTECONTRIBUABLE() : "") + (oTOfficine.getStrREGISTRECOMMERCE() != null ? " / RC: " + oTOfficine.getStrREGISTRECOMMERCE() : ""));

    parameters.put("P_H_CI_RI", (oTOfficine.getStrCENTREIMPOSITION() != null ? "CI:" + oTOfficine.getStrCENTREIMPOSITION() : "") + (oTOfficine.getStrREGISTREIMPOSITION() != null ? " / RI: " + oTOfficine.getStrREGISTREIMPOSITION() : ""));
parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
  String finalphonestring = oTOfficine.getStrPHONE() != null ? "Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
            if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
                String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
                for (String va : phone) {
                    finalphonestring += " / " + conversion.PhoneNumberFormat(va);
                }
            }
            parameters.put("P_H_PHONE", finalphonestring);
          
 parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    OreportManager.BuildReport(parameters, Ojconnexion);

    ObllBase.setKey(new date());
    ObllBase.setOTranslate(OTranslate);
    ObllBase.setOTUser(OTUser);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "suggestion_" + report_generate_file);

%>


