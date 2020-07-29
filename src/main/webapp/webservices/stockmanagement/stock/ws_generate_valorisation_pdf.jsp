<%@page import="bll.common.Parameter"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TTypeSociete"%>
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
<%@page import="java.math.BigInteger"  %>



<!-- fin logic de gestion des page -->

<%

    dataManager OdataManager = new dataManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    date key = new date();
    Date now = new Date();
   
    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_valorisation_stock";
    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();

    Map parameters = new HashMap();
    OdataManager.initEntityManager();
    String P_H_TITLE = "VALORISATION DU STOCK DU ";
    
 
    
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "valorisation_stock_" + report_generate_file);

    jconnexion ojconnexion = new jconnexion();
    ojconnexion.initConnexion();
    ojconnexion.OpenConnexion();

    TOfficine oTOfficine = OdataManager.getEm().find(dal.TOfficine.class, "1");

    P_H_TITLE += date.DateToString(now, date.formatterShort) + " à "+date.DateToString(now, date.NomadicUiFormat_Time);
    parameters.put("P_H_CLT_INFOS", P_H_TITLE.toUpperCase());
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME());
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
       
    parameters.put("P_EMPLACEMENT", OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    
    
    OreportManager.BuildReport(parameters, ojconnexion);

    ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "valorisation_stock_" + report_generate_file);

%>


