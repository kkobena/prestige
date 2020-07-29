<%@page import="bll.report.JournalVente"%>
<%@page import="dal.TBilletageDetails"%>
<%@page import="bll.teller.TellerMovement"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.printer.DriverPrinter"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.preenregistrement.DevisManagement"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="java.awt.print.PageFormat"%>
<%@page import="java.awt.print.Paper"%>
<%@page import="dal.TOfficine"%>
<%@page import="cust_barcode.barecodeManager"%>
<%@page import="dal.TParameters"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.jconnexion"%>
<%@page import="report.reportManager"%>
<%@page import="dal.TTypeReglement"%>
<%@page import="dal.TCashTransaction"%>
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
    TBilletageDetails OTBilletageDetails = null;
    //List<String> datas = new ArrayList<String>();
%>


<!-- fin logic de gestion des page -->

<%
    String lg_CAISSE_ID = "", title = "TICKET DE BILLETAGE";
    
    if (request.getParameter("lg_CAISSE_ID") != null) {
        lg_CAISSE_ID = request.getParameter("lg_CAISSE_ID");
        new logger().OCategory.info("lg_CAISSE_ID " + lg_CAISSE_ID);
    }
   
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    

    date key = new date();
    OdataManager.initEntityManager();
    DriverPrinter ODriverPrinter = new DriverPrinter(OdataManager, OTUser);
    JournalVente OJournalVente = new JournalVente(OdataManager, OTUser);
    barecodeManager obarecodeManager = new barecodeManager();
    OTBilletageDetails = OJournalVente.getTBilletageDetails(lg_CAISSE_ID);
    
   String fileBarecode = obarecodeManager.buildLineBarecode(OTBilletageDetails.getLgBILLETAGEDETAILSID());
  //   String fileBarecode = obarecodeManager.buildbarcodeOther(lg_CAISSE_ID, jdom.barecode_file + "" + lg_CAISSE_ID + ".jpg");
    ODriverPrinter.setType_ticket(commonparameter.str_ACTION_OTHER);
    ODriverPrinter.setDatas(OJournalVente.generateData(OTBilletageDetails));
    ODriverPrinter.setDatasSubTotal(new ArrayList<String>());
    ODriverPrinter.setTitle(title); 
    ODriverPrinter.setDatasInfoTiersPayant(new ArrayList<String>());
    ODriverPrinter.setDatasInfoSeller(new ArrayList<String>());
    ODriverPrinter.setDataCommentaires(new ArrayList<String>());
    ODriverPrinter.setDateOperation(OTBilletageDetails.getLgBILLETAGEID().getDtCREATED());
    ODriverPrinter.setCodeShow(true);
    ODriverPrinter.setName_code_bare(jdom.barecode_file + "" + fileBarecode + ".png");
 
     ODriverPrinter.PrintTicketVente(1);     
    
    
     String result;
    if (ODriverPrinter.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ODriverPrinter.getMessage() + "\", errors: \"" + ODriverPrinter.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ODriverPrinter.getMessage() + "\", errors: \"" + ODriverPrinter.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
    
%>
<%=result%>


