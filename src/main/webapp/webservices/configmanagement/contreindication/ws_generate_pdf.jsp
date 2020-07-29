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

<%!     //String lg_PREENREGISTREMENT_ID = "%%";
    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TParameters OTParameters;
    bllBase ObllBase = new bllBase();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%

    //if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        //lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
   // }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    
    
    
    date key = new date();
        jdom Ojdom = new jdom();
        Ojdom.InitRessource();
        Ojdom.LoadRessource();
        Translate oTranslate = new Translate();
        dataManager OdataManager = new dataManager();
        reportManager OreportManager = new reportManager();
        Map parameters = new HashMap();
        //OdataManager.initEntityManager();
        String scr_report_file = "", report_tittle = "";
        report_tittle = key.getDateTime() + "_" + key.GetNumberRandom();

        jconnexion ojconnexion = new jconnexion();
        ojconnexion.initConnexion();
        ojconnexion.OpenConnexion();
        
        bllBase obllBase = new bllBase();
        obllBase.checkDatamanager();
        //TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");

        dal.TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");
        
        
        parameters.put("P_H_TITLE", "CONTRES INDICATIONS");
        parameters.put("P_AUTRE_DESC", "Teste autre desc");
        parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
        parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
        
        
    //reportManager OreportManager = new reportManager();
    //String scr_report_file = "rp_type_societes";
    //String report_generate_file = key.GetNumberRandom();

    //report_generate_file = report_generate_file + ".pdf";
    //OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    //OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "rp_type_societes_" + report_generate_file);

    //bllBase obllBase = new bllBase();
    //obllBase.checkDatamanager();
    //TTypeSociete oTypeSociete = obllBase.getOdataManager().getEm().find(dal.TTypeSociete.class,"");
    
    
    //barecodeManager obarecodeManager = new barecodeManager();
    //String fileBarecode = obarecodeManager.buildLineBarecode(oTPreenregistrement.getLgPREENREGISTREMENTID());
    //Map parameters = new HashMap();

    //parameters.put("str_REF", oTPreenregistrement.getStrREF());
    //parameters.put("P_H_TITLE", "TYPE SOCIETE");
    //parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    //parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    //parameters.put("P_INSTITUTION_ADRESSE",oTOfficine.getStrADRESSSEPOSTALE());
    //parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    //parameters.put("P_AUTRE_DESC", "Autre description");
    //parameters.put("P_REFERENCE", oTPreenregistrement.getLgPREENREGISTREMENTID());
    //parameters.put("P_dt_CREATED", obllBase.getKey().DateToString(new Date(), obllBase.getKey().formatterMysql));
    //parameters.put("P_BARE_CODE", jdom.barecode_file + "" + fileBarecode + ".jpg");

    //new logger().OCategory.info("P_REFERENCE" + lg_PREENREGISTREMENT_ID);
    //new logger().OCategory.info("getStrADRESSSEPOSTALE" + oTOfficine.getStrADRESSSEPOSTALE());
    //OreportManager.BuildReport(parameters, Ojconnexion);

        
        scr_report_file = "rp_contre_indications";

        report_tittle = report_tittle + ".pdf";
        OreportManager.setPath_report_src(Ojdom.scr_report_file  + scr_report_file + ".jrxml");
        //OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + report_tittle);
        OreportManager.setPath_report_pdf("D://PROJECTS//JAVA//LABOREX//laborex//web//data//reports//pdf//configmanagement//contreindication//" + report_tittle);
        OreportManager.BuildReportPDF(parameters, ojconnexion);
        
        
    //ObllBase.setKey(new date());
    //ObllBase.setOTranslate(OTranslate);
    //ObllBase.setOTUser(OTUser);

    //Ojconnexion.CloseConnexion();

    //response.sendRedirect("../../../data/reports/pdf/configmanagement/typesociete/" + "rp_type_societes_" + report_generate_file);

%>


<p>
    Exportation effectuée avec succès!
<p>
<p>
    Repertoire de stockage: D:\PROJECTS\JAVA\LABOREX\laborex\web\data\reports\pdf\configmanagement\contreindication
<p>