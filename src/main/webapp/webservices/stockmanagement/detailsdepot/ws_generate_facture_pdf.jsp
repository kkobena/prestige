<%@page import="bll.common.Parameter"%>
<%@page import="bll.printer.DriverPrinter"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.printer.PrintManangement"%>

<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.preenregistrement.DevisManagement"%>
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
    dataManager OdataManager = new dataManager();
    TParameters OTParameters;
    date key = new date();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%
    String lg_PREENREGISTREMENT_ID = "", lg_IMPRIMANTE_ID = "%%";
    int int_NUMBER_COPY = 1;
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + lg_PREENREGISTREMENT_ID);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();

    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_facture_vente";

    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "facture_vente_" + report_generate_file);

    OdataManager.initEntityManager();
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    /*TCompteClientTiersPayant OTCompteClientTiersPayant = new TCompteClientTiersPayant();
     List<TPreenregistrementCompteClientTiersPayent> lstT = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
     List<TPreenregistrementCompteClient> lstTPC = new ArrayList<TPreenregistrementCompteClient>();
     TPreenregistrementCompteClient OTPreenregistrementCompteClient = new TPreenregistrementCompteClient();*/

    TPreenregistrement oTPreenregistrement = OdataManager.getEm().find(TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
    TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");

    new logger().OCategory.info(" ****   lg_preenregistrement_id depuis generate pdf   ****  HHHHH" + oTPreenregistrement.getLgPREENREGISTREMENTID() + " " + oTOfficine.getStrNOMCOMPLET());

    String P_H_CLT_INFOS = "FACTURATION";
    new logger().OCategory.info(" **** P_H_CLT_INFOS   ****" + P_H_CLT_INFOS);

    barecodeManager obarecodeManager = new barecodeManager();
    String fileBarecode = obarecodeManager.buildLineBarecode(oTPreenregistrement.getLgPREENREGISTREMENTID());
    Map parameters = new HashMap();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", " Caissier(e) :: " + " " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC", "  ");
    parameters.put("P_REFERENCE", oTPreenregistrement.getLgPREENREGISTREMENTID());
    parameters.put("P_BARE_CODE", jdom.barecode_file + "" + fileBarecode + ".jpg");
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);

    parameters.put("int_TOTAL_TVA", OPreenregistrement.getTotalMontantTvaByVente(oTPreenregistrement.getLgPREENREGISTREMENTID()));
    parameters.put("P_PATH_SUBREPORT", Ojdom.scr_report_file);

    OreportManager.BuildReport(parameters, Ojconnexion);
    Ojconnexion.CloseConnexion();

    PrinterManager OPrinterManager = new PrinterManager(OdataManager, OTUser);
    PrintManangement OPrintManangement = new PrintManangement();
    DriverPrinter ODriverPrinter = new DriverPrinter(OdataManager, OTUser);
    String str_final_file = Ojdom.scr_report_pdf + "facture_vente_" + report_generate_file;
    new logger().OCategory.info("str_final_file -----------" + str_final_file);
   

    /*String str_final_file = Ojdom.scr_report_pdf+"facture_vente_" + report_generate_file;
     new logger().OCategory.info("str_final_file -----------" + str_final_file);
     new PrintManangement().doPrintDataByPrinterName("Canon MF4700 Series UFRII LT", str_final_file, 1);*/
  //  response.sendRedirect("../../../data/reports/pdf/" + "facture_vente_" + report_generate_file);
    String result;
    if (OPrintManangement.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + OPrintManangement.getMessage() + "\", errors: \"" + OPrintManangement.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + OPrintManangement.getMessage() + "\", errors: \"" + OPrintManangement.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>


