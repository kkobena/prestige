<%@page import="bll.facture.reglementManager"%>
<%@page import="bll.printer.PrintManangement"%>
<%@page import="bll.common.Parameter"%>
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

    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();

    String lg_DOSSIER_REGLEMENT_ID = "";
    String fileBarecode = "";

    if (request.getParameter("lg_DOSSIER_REGLEMENT_ID") != null) {
        lg_DOSSIER_REGLEMENT_ID = request.getParameter("lg_DOSSIER_REGLEMENT_ID");
        new logger().OCategory.info("lg_DOSSIER_REGLEMENT_ID " + lg_DOSSIER_REGLEMENT_ID);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    reglementManager OreglementManager = new reglementManager(OdataManager, OTUser);
    barecodeManager obarecodeManager = new barecodeManager();
    fileBarecode = obarecodeManager.buildLineBarecode(OreglementManager.getKey().getShortId(10));
    //fileBarecode = obarecodeManager.buildLineBarecode("8560900105");
    OreglementManager.lunchPrinterForTicketCaisse(lg_DOSSIER_REGLEMENT_ID, jdom.barecode_file + "" + fileBarecode + ".png");

    String result;
    if (OreglementManager.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + OreglementManager.getMessage() + "\", errors: \"" + OreglementManager.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + OreglementManager.getMessage() + "\", errors: \"" + OreglementManager.getDetailmessage() + "\"}";
    }


%>
<%=result%>


