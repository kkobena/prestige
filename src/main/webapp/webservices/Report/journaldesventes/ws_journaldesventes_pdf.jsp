<%-- 
    Document   : ws_statistiquefamillearticle_pdf
    Created on : 8 juil. 2015, 19:23:22
    Author     : EACHUA
--%>

<%@page import="dal.TRetrocession"%>
<%@page import="dal.TOfficine"%>
<%@page import="cust_barcode.barecodeManager"%>
<%@page import="dal.TParameters"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.jconnexion"%>
<%@page import="report.reportManager"%>
<%@page import="dal.TTypeReglement"%>
<%@page import="dal.TCashTransaction"%>
<%@page import="dal.TRetrocession"%>
<%@page import="dal.TRetrocessionDetail"%>
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
<%@page import="java.text.SimpleDateFormat"  %>

<%!     String lg_PERIODE = "%%";
    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TParameters OTParameters;
    bllBase ObllBase = new bllBase();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%

    if (request.getParameter("lg_periode") != null) {
        lg_PERIODE = request.getParameter("lg_periode");
        new logger().OCategory.info("lg_periode " + lg_PERIODE);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("User connecté " + OTUser.getStrFIRSTNAME());

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date key = new date();
    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_journal_vente";
    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "rpt_rp_journal_vente_" + report_generate_file);

    bllBase obllBase = new bllBase();
    obllBase.checkDatamanager();

    //TRetrocession oTRetrocession = obllBase.getOdataManager().getEm().find(TRetrocession.class, lg_RETROCESSION_ID);
    //new logger().OCategory.info("Retrocession sélectionnée ----- "+oTRetrocession.getLgRETROCESSIONID());
    TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");

    String P_H_CLT_INFOS = "Statistique Famille Article";
    String P_PERIODE_DEBUT = null;
    String P_PERIODE_FIN = null;
    /*if (oTRetrocession.getStrSTATUT().equals("devis")) {
     P_H_TITTLE = "Devis";
     }*/
    SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy/MM/dd");
    String str_datejr = date.GetDateNow(dateformatter);
    Date dateDuJour = dateformatter.parse(str_datejr);

    if (lg_PERIODE.equals("Jour")) {
        P_PERIODE_DEBUT = str_datejr;
        P_PERIODE_FIN = str_datejr;
        new logger().OCategory.info("P_PERIODE_jr " + P_PERIODE_DEBUT);
        new logger().OCategory.info("P_PERIODE_jr " + P_PERIODE_FIN);
    } else if (lg_PERIODE.equals("Semaine")) {
       //P_PERIODE_DEBUT = date.;
        //P_PERIODE_DEBUT = date.GetDateNow_Date();
    } else if (lg_PERIODE.equals("Mois")) {
        Calendar calendar = Calendar.getInstance();

        P_PERIODE_DEBUT = dateformatter.format(date.GetDebutMois());
        calendar.setTime(dateDuJour);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = Integer.parseInt(date.getAnnee(dateDuJour));
        new logger().OCategory.info("Mois1: " + currentMonth);
        P_PERIODE_FIN = currentYear + "/" + currentMonth + "/" + maxDay;    //dateformatter.format(date.GetFinMois(Integer.parseInt(date.getAnnee(dateDuJour)), 2));

        if (currentMonth < 10) {
            String str_month = "";
            str_month = "" + currentMonth;
            str_month = "0" + str_month;
            new logger().OCategory.info("Mois2_s: " + str_month);
            P_PERIODE_FIN = currentYear + "/" + str_month + "/" + maxDay;
        }

        new logger().OCategory.info("P_PERIODE_MOIS_DEBUT " + P_PERIODE_DEBUT);
        new logger().OCategory.info("P_PERIODE_MOIS_FIN " + P_PERIODE_FIN);
    } else if (lg_PERIODE.equals("Trimestre")) {
        //P_PERIODE_DEBUT = date.GetDateNow_Date();
        //P_PERIODE_FIN = date.GetDateNow_Date();
    } else if (lg_PERIODE.equals("Semestre")) {
       //P_PERIODE_DEBUT = date.GetDateNow_Date();
        // P_PERIODE_FIN = date.GetDateNow_Date();
    } else if (lg_PERIODE.equals("Annee")) {

        String str_dateDebutAnnne = Integer.parseInt(date.getAnnee(dateDuJour))+"/01/01" ;
        String str_dateFinAnnee = Integer.parseInt(date.getAnnee(dateDuJour))+"/12/31";
        new logger().OCategory.info("janvier: " + str_dateDebutAnnne);
        new logger().OCategory.info("decembre: " + str_dateFinAnnee);
        //Date dt_dateDebutAnnne = dateformatter.parse(str_dateDebutAnnne);
        //Date dt_dateFinAnnee = dateformatter.parse(str_dateFinAnnee);

        P_PERIODE_DEBUT = str_dateDebutAnnne;
        P_PERIODE_FIN = str_dateFinAnnee;

        new logger().OCategory.info("P_PERIODE_ANNE_DEBUT " + P_PERIODE_DEBUT);
        new logger().OCategory.info("P_PERIODE_ANNE_FIN " + P_PERIODE_FIN);
    } else {
        new logger().OCategory.info("Aucun test effectue ;periode = " + lg_PERIODE);
    }

    new logger().OCategory.info("Fin test ");

    //barecodeManager obarecodeManager = new barecodeManager();
    //String fileBarecode = obarecodeManager.buildLineBarecode(oTRetrocession.getLgRETROCESSIONID());
    Map parameters = new HashMap();

    //parameters.put("str_REF", oTRetrocession.getStrREFERENCE());
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    parameters.put("P_PERIODE_DEBUT", P_PERIODE_DEBUT);
    parameters.put("P_PERIODE_FIN", P_PERIODE_FIN);
    //parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    //parameters.put("P_AUTRE_DESC", "  ");
    //parameters.put("P_REFERENCE", oTRetrocession.getLgRETROCESSIONID());
    parameters.put("P_dt_CREATED", obllBase.getKey().DateToString(new Date(), obllBase.getKey().formatterMysql));
    //parameters.put("P_BARE_CODE", jdom.barecode_file + "" + fileBarecode + ".jpg");

    //new logger().OCategory.info("getStrADRESSSEPOSTALE" + oTOfficine.getStrADRESSSEPOSTALE() + " nom utilisateur "+OTUser.getStrFIRSTNAME() 
    //       + "  " + OTUser.getStrLASTNAME() + " id retrocession "+oTRetrocession.getLgRETROCESSIONID() + " code barre "+jdom.barecode_file + "" + fileBarecode + ".jpg");
    OreportManager.BuildReport(parameters, Ojconnexion);

    ObllBase.setKey(new date());
    ObllBase.setOTranslate(OTranslate);
    ObllBase.setOTUser(OTUser);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "rpt_rp_journal_vente_" + report_generate_file);

%>
