<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="toolkits.utils.StringComplexUtils.DataStringManager"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.printer.DriverPrinter"%>
<%@page import="bll.printer.PrintManangement"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="toolkits.filesmanagers.FilesType.PdfFiles"%>
<%@page import="dal.TOfficine"%>
<%@page import="cust_barcode.barecodeManager"%>
<%@page import="dal.TParameters"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.jconnexion"%>
<%@page import="report.reportManager"%>
<%@page import="dal.TTypeReglement"%>
<%@page import="dal.TCashTransaction"%>
<%@page import="dal.TEtiquette"%>
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

<%
    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    List<InputStream> inputPdfList = new ArrayList<InputStream>();
     Map parameters = null;
    TUser OTUser = null;
    date key = new date();
%>


<!-- fin logic de gestion des page -->

<%
    String lg_ETIQUETTE_ID = "", lg_IMPRIMANTE_ID = "2", report_generate_file = key.GetNumberRandom(),
            str_final_file = "", str_file = "";
    int int_NUMBER_COPY = 1, k = 1;
    String scr_report_file = "rp_etiquette";
    if (request.getParameter("lg_ETIQUETTE_ID") != null) {
        lg_ETIQUETTE_ID = request.getParameter("lg_ETIQUETTE_ID");
        new logger().OCategory.info("lg_ETIQUETTE_ID " + lg_ETIQUETTE_ID);
    }

    
    if (request.getParameter("begin") != null && !request.getParameter("begin").equalsIgnoreCase("")) {
        k = Integer.parseInt(request.getParameter("begin"));
        new logger().OCategory.info("begin " + k);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    //OdataManager.initEntityManager();
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    
     bllBase obllBase = new bllBase();
    obllBase.checkDatamanager();
    TEtiquette oTEtiquette = obllBase.getOdataManager().getEm().find(dal.TEtiquette.class, lg_ETIQUETTE_ID);
    TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");
    reportManager OreportManager = new reportManager();
     
    parameters = new HashMap();
    barecodeManager obarecodeManager = new barecodeManager();
    String fileBarecode = obarecodeManager.buildbarcodeOther(oTEtiquette.getLgFAMILLEID().getIntCIP(), jdom.barecode_file + key.getComplexId() + ".gif");
    new logger().OCategory.info("fileBarecode "+fileBarecode);
    Date today = new Date();
    for (int i = 0; i < Integer.parseInt(oTEtiquette.getIntNUMBER()); i++) {
        parameters.put("P_H_INSTITUTION_"+k, oTOfficine.getStrNOMABREGE());
        parameters.put("P_INSTITUTION_ADRESSE_" + k, oTEtiquette.getLgFAMILLEID().getStrDESCRIPTION());
        parameters.put("P_BARE_CODE_" + k, fileBarecode);
        parameters.put("P_RICE_" + k, conversion.AmountFormat(oTEtiquette.getLgFAMILLEID().getIntPRICE(), ' ') + " CFA");
        parameters.put("P_OTHER_" + k, date.DateToString(today, date.formatterShortBis));
        parameters.put("P_CIP_" + k, oTEtiquette.getLgFAMILLEID().getIntCIP());
        if (k == 65) {
            new logger().OCategory.info("avant ********" + k);
            report_generate_file = report_generate_file + ".pdf";
            OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
            OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "etiquette_" + report_generate_file);
            OreportManager.BuildReportEmptyDs(parameters);
            str_final_file = Ojdom.scr_report_pdf + "etiquette_" + report_generate_file;
             new logger().OCategory.info("str_file_name ********" + str_final_file);
            inputPdfList.add(new FileInputStream(str_final_file));
            parameters = new HashMap();
            k = 1;
           /* new logger().OCategory.info("apres ********" + k);
            oTEtiquette.setIntNUMBER(String.valueOf(Integer.parseInt(oTEtiquette.getIntNUMBER()) - 65));*/
        } else {
            k++;
        }
       // new logger().OCategory.info("k -------" + k);
       
    }
    
    if (Integer.parseInt(oTEtiquette.getIntNUMBER()) < 65) {
        report_generate_file = report_generate_file + ".pdf";
        OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
        OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "etiquette_" + report_generate_file);
        OreportManager.BuildReportEmptyDs(parameters);
        str_final_file = Ojdom.scr_report_pdf + "etiquette_" + report_generate_file;
        new logger().OCategory.info("str_file_name ********######" + str_final_file);
        inputPdfList.add(new FileInputStream(str_final_file));
    }
    
    str_file = "etiquette_" + key.GetNumberRandom() + ".pdf";
    String outputStreamFile = Ojdom.scr_report_pdf + str_file;
    OutputStream outputStream = new FileOutputStream(outputStreamFile);
    PdfFiles.mergePdfFiles(inputPdfList, outputStream);
    response.sendRedirect("../../../data/reports/pdf/" + str_file);
    oTEtiquette.setStrSTATUT(commonparameter.statut_Read);
    obllBase.persiste(oTEtiquette);
    obllBase.buildSuccesTraceMessage("Opération effectuée avec succes");

%>



