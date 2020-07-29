<%@page import="dal.TFamille"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="dal.TAjustement"%>
<%@page import="dal.TAjustement"%>
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
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%
      Date today = new Date();
    String lg_FAMILLE_ID = "%%", search_value = "";
    String str_Date_Debut = date.DateToString(today, date.formatterMysqlShort), str_Date_Fin = date.DateToString(today, date.formatterMysqlShort);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }
    
    
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    
    new logger().OCategory.info("Utilisateur " + OTUser.getStrFIRSTNAME() + " Id " + OTUser.getLgUSERID());
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
   
    OdataManager.initEntityManager();
    reportManager OreportManager = new reportManager();
    
    String P_H_CLT_INFOS = "FICHE DES MOUVEMENTS ARTICLES        DU "+ date.formatterShort.format(java.sql.Date.valueOf(str_Date_Debut))+ " AU "+  date.formatterShort.format(java.sql.Date.valueOf(str_Date_Fin));
    
    String scr_report_file = "rp_recap_mouvement_article";
    String report_generate_file = date.GetNumberRandom();

    new logger().OCategory.info("scr_report_file " + scr_report_file);
    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "recap_mouvement_article_" + report_generate_file);

    TOfficine oTOfficine = OdataManager.getEm().find(dal.TOfficine.class, "1");

    new logger().OCategory.info(" **** P_H_CLT_INFOS   ****" + P_H_CLT_INFOS);

    Map parameters = new HashMap();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC", "  ");
    parameters.put("P_REFERENCE", lg_FAMILLE_ID);
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    parameters.put("P_DT_DEBUT", str_Date_Debut);
    parameters.put("P_DT_FIN", str_Date_Fin);
    parameters.put("P_EMPLACEMENT", OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    parameters.put("P_SEARCH", search_value+"%");
     parameters.put("P_EMPLACEMENT", OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    
    
    

    OreportManager.BuildReport(parameters, Ojconnexion);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "recap_mouvement_article_" + report_generate_file);

%>


