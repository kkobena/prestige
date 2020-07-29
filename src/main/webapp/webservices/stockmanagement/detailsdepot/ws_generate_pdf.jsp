<%@page import="bll.printer.PrintManangement"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.printer.DriverPrinter"%>
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

    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
    //List<String> datas = new ArrayList<String>();
%>


<!-- fin logic de gestion des page -->

<%
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();

    String lg_PREENREGISTREMENT_ID = "", lg_IMPRIMANTE_ID = "%%", title = "", str_FIRST_NAME_FACTURE = "", str_LAST_NAME_FACTURE = "", int_NUMBER_FACTURE = "";
    String fileBarecode = "";
    boolean bool_IsACCOUNT = false;
    int int_NUMBER_COPY = 1;
    List<TPreenregistrementCompteClientTiersPayent> lstT = new ArrayList<TPreenregistrementCompteClientTiersPayent>();

    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("bool_IsACCOUNT") != null) {
        bool_IsACCOUNT = Boolean.parseBoolean(request.getParameter("bool_IsACCOUNT"));
        new logger().OCategory.info("bool_IsACCOUNT " + bool_IsACCOUNT);
    }

    if (request.getParameter("str_FIRST_NAME_FACTURE") != null) {
        str_FIRST_NAME_FACTURE = request.getParameter("str_FIRST_NAME_FACTURE");
        new logger().OCategory.info("str_FIRST_NAME_FACTURE " + str_FIRST_NAME_FACTURE);
    }

    if (request.getParameter("str_LAST_NAME_FACTURE") != null) {
        str_LAST_NAME_FACTURE = request.getParameter("str_LAST_NAME_FACTURE");
        new logger().OCategory.info("str_LAST_NAME_FACTURE " + str_LAST_NAME_FACTURE);
    }

    if (request.getParameter("int_NUMBER_FACTURE") != null) {
        int_NUMBER_FACTURE = request.getParameter("int_NUMBER_FACTURE");
        new logger().OCategory.info("int_NUMBER_FACTURE " + int_NUMBER_FACTURE);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    date key = new date();
    OdataManager.initEntityManager();
    DriverPrinter ODriverPrinter = new DriverPrinter(OdataManager, OTUser);
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    PrinterManager OPrinterManager = new PrinterManager(OdataManager, OTUser);
    barecodeManager obarecodeManager = new barecodeManager();
    TPreenregistrement oTPreenregistrement = OPreenregistrement.getTPreenregistrementByRef(lg_PREENREGISTREMENT_ID);
   // fileBarecode = obarecodeManager.buildbarcodeOther("222222", jdom.barecode_file + "" + oTPreenregistrement.getLgPREENREGISTREMENTID() + ".jpg");
   
    fileBarecode = obarecodeManager.buildLineBarecode(oTPreenregistrement.getStrREFTICKET()); //a decommenter apres les test
    //fileBarecode = obarecodeManager.buildLineBarecode("4785214785", commonparameter.Code11, oTPreenregistrement.getLgPREENREGISTREMENTID()); //a commenter apres les tests
    OPreenregistrement.lunchPrinterForTicket(oTPreenregistrement, jdom.barecode_file + "" + fileBarecode + ".png");
    ODriverPrinter.setMessage(OPreenregistrement.getMessage());
    ODriverPrinter.setDetailmessage(OPreenregistrement.getDetailmessage());
    /*TPreenregistrement oTPreenregistrement = OdataManager.getEm().find(TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
     TUserImprimante OTUserImprimante = OPrinterManager.getTUserImprimante(OTUser.getLgUSERID(), lg_IMPRIMANTE_ID, commonparameter.str_ACTION_VENTE);
     //TUserImprimante OTUserImprimante = OPrinterManager.getTUserImprimante(OTUser.getLgUSERID(), lg_IMPRIMANTE_ID);
     new logger().OCategory.info("id vente " + oTPreenregistrement.getLgPREENREGISTREMENTID() + "  " + OTUserImprimante.getLgUSERIMPRIMQNTEID() + " user id " + OTUserImprimante.getLgUSERID().getLgUSERID() + " imprimante " + OTUserImprimante.getLgIMPRIMANTEID().getStrDESCRIPTION() + " " + OTUserImprimante.getLgIMPRIMANTEID().getStrNAME());
     if (OTUserImprimante != null) {

     //String fileBarecode = obarecodeManager.buildLineBarecode(oTPreenregistrement.getLgPREENREGISTREMENTID());
     fileBarecode = obarecodeManager.buildbarcodeOther(oTPreenregistrement.getLgPREENREGISTREMENTID(), jdom.barecode_file + "" + oTPreenregistrement.getLgPREENREGISTREMENTID() + ".jpg");

     //String fileBarecode = obarecodeManager.testCODABAR(jdom.barecode_file + "barcodetext.jpg", "123456789012");
     ODriverPrinter.setDatas(OPreenregistrement.generateData(oTPreenregistrement));
     ODriverPrinter.setDatasSubTotal(OPreenregistrement.generateDataSummary(oTPreenregistrement));
     ODriverPrinter.setDatasInfoSeller(OPreenregistrement.generateDataSeller(oTPreenregistrement));
     ODriverPrinter.setTitle("Ticket N° " + oTPreenregistrement.getStrREF());
     ODriverPrinter.setType_ticket(commonparameter.str_ACTION_VENTE);
     ODriverPrinter.setDatasInfoTiersPayant(new ArrayList<String>());
     if (oTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals("3") || oTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals("2")) {
     lstT = OPreenregistrement.getListeTPreenregistrementCompteClientTiersPayent(oTPreenregistrement.getLgPREENREGISTREMENTID());
     //title = "Ticket de M/Mme  " + lstT.get(0).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME() + " " + lstT.get(0).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME();

     // ODriverPrinter.setTitle(title);
     ODriverPrinter.setDatasInfoTiersPayant(OPreenregistrement.generateDataTiersPayant(oTPreenregistrement, lstT));
     int_NUMBER_COPY = lstT.size() + 1;
     }
     ODriverPrinter.setCodeShow(true);
     new logger().OCategory.info("fileBarecode " + fileBarecode + ".");

     ODriverPrinter.setName_code_bare(fileBarecode);
     if (OTUserImprimante != null) {

     if (lstT.size() == 0) {
     ODriverPrinter.PrintTicketVente(OTUserImprimante.getLgIMPRIMANTEID().getStrDESCRIPTION(), int_NUMBER_COPY);
     } else {
     // ODriverPrinter.PrintTicketVente(OTUserImprimante.getLgIMPRIMANTEID().getStrNAME(), 1); 
     for (int i = 0; i <= lstT.size(); i++) {
     ODriverPrinter.PrintTicketVente(OTUserImprimante.getLgIMPRIMANTEID().getStrDESCRIPTION(), 1);
     }
     }
            
     } else {
     ODriverPrinter.setMessage(OPrinterManager.getMessage());
     ODriverPrinter.setDetailmessage(OPrinterManager.getDetailmessage());
     new logger().OCategory.info("Echec de l'impression");
     }
     } else {
     ODriverPrinter.setMessage(OPrinterManager.getMessage());
     ODriverPrinter.setDetailmessage(OPrinterManager.getDetailmessage());
     }*/

    if (bool_IsACCOUNT) {
        //impression sur facture A4
        reportManager OreportManager = new reportManager();
        TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");

        String scr_report_file = "rp_facture_vente";

        String report_generate_file = key.GetNumberRandom();

        report_generate_file = report_generate_file + ".pdf";
        OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
        OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "facture_vente_" + report_generate_file);
        String P_H_CLT_INFOS = "FACTURATION";
        new logger().OCategory.info(" **** P_H_CLT_INFOS   ****" + P_H_CLT_INFOS);
        Map parameters = new HashMap();

        parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
        parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
        parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
        parameters.put("P_PRINTED_BY", " Caissier(e) :: " + " " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
        parameters.put("P_AUTRE_DESC", "  ");
        parameters.put("P_REFERENCE", oTPreenregistrement.getLgPREENREGISTREMENTID());
        parameters.put("P_BARE_CODE", jdom.barecode_file + "" + fileBarecode + ".png");
        parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);

        parameters.put("int_TOTAL_TVA", OPreenregistrement.getTotalMontantTvaByVente(oTPreenregistrement.getLgPREENREGISTREMENTID()));
        parameters.put("P_PATH_SUBREPORT", Ojdom.scr_report_file);

        OreportManager.BuildReport(parameters, Ojconnexion);
        Ojconnexion.CloseConnexion();

       
        PrintManangement OPrintManangement = new PrintManangement();

        String str_final_file = Ojdom.scr_report_pdf + "facture_vente_" + report_generate_file;
        new logger().OCategory.info("str_final_file -----------" + str_final_file);
    
        //fin impression sur facture A4
    }

    String result;
    if (ODriverPrinter.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ODriverPrinter.getMessage() + "\", errors: \"" + ODriverPrinter.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ODriverPrinter.getMessage() + "\", errors: \"" + ODriverPrinter.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>


