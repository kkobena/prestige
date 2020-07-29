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
    
%>


<!-- fin logic de gestion des page -->

<%
    String search_value = "";
  
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value "+search_value);
    }
    
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date key = new date();
    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_liste_utilisateur";
     // String scr_report_file = "rp_liste_retrocession_final";
    String report_generate_file = key.GetNumberRandom();
    
    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "liste_utilisateur_" + report_generate_file);
    new logger().OCategory.info("Dans edition scr_report_file "+scr_report_file);
    OdataManager.initEntityManager();
   TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");
    

    String P_H_CLT_INFOS = "Liste des utilisateurs";
    Map parameters = new HashMap();
    
     
    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC", "  ");
    parameters.put("P_EMPLACEMENT", OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    parameters.put("P_SEARCH", search_value + "%");
 
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    
    
    new logger().OCategory.info("getStrADRESSSEPOSTALE" + oTOfficine.getStrADRESSSEPOSTALE());
    OreportManager.BuildReport(parameters, Ojconnexion);
    
    
    Ojconnexion.CloseConnexion();
    
    response.sendRedirect("../../../data/reports/pdf/" + "liste_utilisateur_" + report_generate_file);
    
%>


