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
    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TParameters OTParameters;
    date key = new date();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%
    String str_Date_Debut = "", str_Date_Fin = "", OdateDebut = "", OdateFin = "", search_value = "%%",
            h_debut = "00:00", h_fin = "23:59", str_TYPE_VENTE = "%%";
    Date dt_Date_Debut, dt_Date_Fin;

    if (request.getParameter("search_value") != null && !request.getParameter("search_value").equalsIgnoreCase("")) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value :" + search_value);
    }

    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equalsIgnoreCase("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        new logger().OCategory.info("str_TYPE_VENTE :" + str_TYPE_VENTE);
    }

    if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        new logger().OCategory.info("h_debut :---" + h_debut);
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");
        new logger().OCategory.info("h_fin :" + h_fin);
    }
    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    if (str_Date_Fin.equalsIgnoreCase("") || str_Date_Fin == null) {
        dt_Date_Fin = new Date();
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    } else {
        dt_Date_Fin = key.stringToDate(str_Date_Fin, key.formatterMysqlShort);
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    }

    dt_Date_Fin = key.getDate(OdateFin, "23:59");
    //dt_Date_Fin = key.GetNewDate(1);
    OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort);
    new logger().OCategory.info("dt_Date_Fin *** " + dt_Date_Fin + " OdateFin *** " + OdateFin);
    if (str_Date_Debut.equalsIgnoreCase("") || str_Date_Debut == null) {
        dt_Date_Debut = new Date();
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);
    } else {
        dt_Date_Debut = key.stringToDate(str_Date_Debut, key.formatterMysqlShort);
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);

    }
    dt_Date_Debut = key.getDate(OdateDebut, "00:00");
    OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort);

    new logger().OCategory.info("dt_Date_Debut ---- " + dt_Date_Debut + " OdateDebut ---- " + OdateDebut);

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("Utilisateur " + OTUser.getStrFIRSTNAME() + " Id " + OTUser.getLgUSERID());
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    reportManager OreportManager = new reportManager();

    OdataManager.initEntityManager();

    String scr_report_file = "rp_liste_vente";

    String report_generate_file = key.GetNumberRandom();

    new logger().OCategory.info("scr_report_file " + scr_report_file);
    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "fiche_liste_vente_" + report_generate_file);

    TOfficine oTOfficine = OdataManager.getEm().find(dal.TOfficine.class, "1");

    Paper paper = new Paper();
    PageFormat format = new PageFormat();

    String P_H_CLT_INFOS = "Liste des ventes du " + key.DateToString(dt_Date_Debut, key.formatterShort) + " au " + key.DateToString(dt_Date_Fin, key.formatterShort) + " de " + h_debut + " à " + h_fin;

    new logger().OCategory.info(" **** P_H_CLT_INFOS   ****" + P_H_CLT_INFOS);

    Map parameters = new HashMap();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC", "  ");
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    parameters.put("P_DT_DEBUT", OdateDebut);
    parameters.put("P_DT_FIN", OdateFin);
    parameters.put("P_TYPE_VENTE", str_TYPE_VENTE);
    parameters.put("P_H_DEBUT", h_debut);
    parameters.put("P_H_FIN", h_fin);
    parameters.put("P_SEARCH", search_value);
    parameters.put("P_USER", OTUser.getLgUSERID());
    parameters.put("P_EMPLACEMENT", OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

    OreportManager.BuildReport(parameters, Ojconnexion);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "fiche_liste_vente_" + report_generate_file);

%>


