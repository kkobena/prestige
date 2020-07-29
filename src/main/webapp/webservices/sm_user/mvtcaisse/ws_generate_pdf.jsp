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
    //List<String> datas = new ArrayList<String>();
%>


<!-- fin logic de gestion des page -->

<%
    String str_REF = "", lg_IMPRIMANTE_ID = "%%", title = "Mouvement de caisse";

    if (request.getParameter("str_REF") != null) {
        str_REF = request.getParameter("str_REF");
        new logger().OCategory.info("str_REF " + str_REF);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    date key = new date();
    OdataManager.initEntityManager();
    DriverPrinter ODriverPrinter = new DriverPrinter(OdataManager, OTUser);
    TellerMovement OTellerMovement = new TellerMovement(OdataManager, OTUser);
    // PrinterManager OPrinterManager = new PrinterManager(OdataManager, OTUser);
    barecodeManager obarecodeManager = new barecodeManager();

    EntityData OEntityData = OTellerMovement.getMvtCaisse(str_REF);

    //String fileBarecode = obarecodeManager.buildLineBarecode("3416210");
    String fileBarecode = obarecodeManager.buildLineBarecode(OEntityData.getStr_value6());
    //   String fileBarecode = obarecodeManager.buildbarcodeOther(str_REF, jdom.barecode_file + "" + str_REF + ".jpg");
    ODriverPrinter.setType_ticket(commonparameter.str_ACTION_OTHER);
    ODriverPrinter.setDatas(OTellerMovement.generateData(OEntityData));
    ODriverPrinter.setDatasSubTotal(new ArrayList<String>());
    ODriverPrinter.setTitle(title);
    ODriverPrinter.setDatasInfoTiersPayant(new ArrayList<String>());
    ODriverPrinter.setDatasInfoSeller(new ArrayList<String>());
    //  ODriverPrinter.setDataCommentaires(new ArrayList<String>());
    List<String> comment = new ArrayList<String>();
    if (OEntityData.getStr_value7().length() > 0) {
        comment.add(" " + ";0");
        comment.add(OEntityData.getStr_value7() + ";0");
    }

    ODriverPrinter.setDataCommentaires(comment);
    ODriverPrinter.setCodeShow(true);
    ODriverPrinter.setName_code_bare(jdom.barecode_file + "" + fileBarecode + ".png");
    TParameters KEY_TICKET_COUNT = OdataManager.getEm().getReference(TParameters.class, "KEY_TICKET_COUNTMVT");
    int int_NUMBER_EXEMPLAIRE = 1;
    if (KEY_TICKET_COUNT != null) {
        int_NUMBER_EXEMPLAIRE = Integer.valueOf(KEY_TICKET_COUNT.getStrVALUE().trim());
    }
    for (int i = 0; i < int_NUMBER_EXEMPLAIRE; i++) {
        ODriverPrinter.PrintTicketVente(1);

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


