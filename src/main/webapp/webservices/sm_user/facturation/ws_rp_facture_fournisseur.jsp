<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TOrderDetail"%>
<%@page import="dal.TOrder"%>
<%@page import="java.awt.print.PageFormat"%>
<%@page import="java.awt.print.Paper"%>
<%@page import="dal.TPreenregistrementCompteClient"%>
<%@page import="dal.TOfficine"%>
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

<%!  String lg_FACTURE_ID = "%%";
    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TParameters OTParameters;
    bllBase ObllBase = new bllBase();
    TUser OTUser = null;
    List<dal.TOrder> lstTOrder = null;
%>


<!-- fin logic de gestion des page -->

<%
    if (request.getParameter("lg_FACTURE_ID") != null) {
        lg_FACTURE_ID = request.getParameter("lg_FACTURE_ID");
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("Utilisateur " + OTUser.getStrFIRSTNAME() + " Id " + OTUser.getLgUSERID());
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date key = new date();
    reportManager OreportManager = new reportManager();

    String scr_report_file = "rp_facture_des_fournisseur";
    String report_generate_file = key.GetNumberRandom();

    new logger().OCategory.info("scr_report_file " + scr_report_file);
    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "rp_facture_des_fournisseur" + report_generate_file);

    bllBase obllBase = new bllBase();
    obllBase.checkDatamanager();

    TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");

    // Récupération des paramètres
    // Entete
    String P_AUTRE_DESC = "";
    String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
    String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();
    String P_H_CLT_INFOS = "ETAT DE CONTROLE DES COMMANDES";
    //String P_H_LOGO = jdom.scr_report_file_logo;

    Paper paper = new Paper();
    PageFormat format = new PageFormat();
       
    Map parameters = new HashMap();
    parameters.put("P_H_LOGO", "");
    parameters.put("P_H_INSTITUTION", "");
    parameters.put("P_INSTITUTION_ADRESSE", "");
    // parameters.put("P_PRINTED_BY", "  " + " " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC", "");
    parameters.put("P_H_CLT_INFOS", "");
    parameters.put("P_LG_FACTURE_ID", lg_FACTURE_ID);

    new logger().OCategory.info("lg_FACTURE_ID " + lg_FACTURE_ID);
    OreportManager.BuildReport(parameters, Ojconnexion);


    /*   ObllBase.setKey(new date());
     ObllBase.setOTranslate(OTranslate);
     ObllBase.setOTUser(OTUser);

     Ojconnexion.CloseConnexion();*/
    response.sendRedirect("../../../data/reports/pdf/" + "rp_facture_des_fournisseur" + report_generate_file);

%>


