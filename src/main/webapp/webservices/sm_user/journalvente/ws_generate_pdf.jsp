<%@page import="bll.entity.EntityData"%>
<%@page import="bll.report.JournalVente"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="bll.common.Parameter"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.printer.PrintManangement"%>
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
TParameters OTParameters = null;
List<EntityData> listTMvtCaissesFalse = new ArrayList<EntityData>();
%>


<!-- fin logic de gestion des page -->

<%    
    Date today = new Date();
    String str_Date_Debut = date.DateToString(today, date.formatterMysqlShort), str_Date_Fin = date.DateToString(today, date.formatterMysqlShort), OdateDebut = "", lg_FAMILLE_ID = "%%", str_ACTION = "%%", OdateFin = "", 
        h_debut = "00:00", h_fin = "23:59", search_value = "%%", str_TYPE_VENTE = "%%", title = "JOURNAL DE VENTE",
        lg_PREENGISTREMENT_ID = "%%";
    Double P_SORTIECAISSE_ESPECE_FALSE = 0d;
    
    OdataManager.initEntityManager();
    
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    
    String lg_USER_ID = OTUser.getLgUSERID(), lg_EMPLACEMENT_ID = "";
    
   


    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    
    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equalsIgnoreCase("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        new logger().OCategory.info("str_TYPE_VENTE :" + str_TYPE_VENTE);
    }
    
     if (request.getParameter("title") != null && !request.getParameter("title").equalsIgnoreCase("")) {
        title = request.getParameter("title");
        new logger().OCategory.info("title :" + title);
    }

   
    if (request.getParameter("str_ACTION") != null && !request.getParameter("str_ACTION").equalsIgnoreCase("")) {
        str_ACTION = request.getParameter("str_ACTION");
        new logger().OCategory.info("str_ACTION " + str_ACTION);
    }

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }
    
    
    if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        new logger().OCategory.info("h_debut :" + h_debut);
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");
        new logger().OCategory.info("h_fin :" + h_fin);
    }

  
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date date = new date();
    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_journal_vente";
    String report_generate_file = date.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "journal_vente_" + report_generate_file);
    new logger().OCategory.info("Dans edition scr_report_file " + scr_report_file);
    OdataManager.initEntityManager();
    privilege Oprivilege = new privilege(OdataManager, OTUser);
     TparameterManager OTparameterManager = new TparameterManager(OdataManager);
     JournalVente OjournalVente = new JournalVente(OdataManager, OTUser);
        
    if (Oprivilege.isColonneStockMachineIsAuthorize(commonparameter.str_SHOW_VENTE)) {
        lg_USER_ID = "%%";
    }
     lg_EMPLACEMENT_ID = (Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_SHOW_ALL_ACTIVITY) ? "%%" : OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");
    OTParameters = OTparameterManager.getParameter(Parameter.KEY_MOVEMENT_FALSE);
     //code ajouté 24/08/2016
    
     if (OTParameters != null && Integer.parseInt(OTParameters.getStrVALUE()) == 1 && !str_Date_Debut.equalsIgnoreCase(str_Date_Fin)) {
        listTMvtCaissesFalse = OjournalVente.getAllMouvmentsCaisse(str_Date_Debut, str_Date_Fin, false);
    }
    for (EntityData Odata : listTMvtCaissesFalse) {
        P_SORTIECAISSE_ESPECE_FALSE += (-1) * Double.valueOf(Odata.getStr_value1());
    }
    //fin code ajouté 24/08/2016

    String P_H_CLT_INFOS = title + " "+ date.DateToString(date.stringToDate(str_Date_Debut, date.formatterMysqlShort), date.formatterShort)+ " AU "+ date.DateToString(date.stringToDate(str_Date_Fin, date.formatterMysqlShort), date.formatterShort);
    Map parameters = new HashMap();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_H_CC_RC", ((oTOfficine.getStrCOMPTECONTRIBUABLE() != null && !oTOfficine.getStrCOMPTECONTRIBUABLE().equalsIgnoreCase("")) ? "CC:" + oTOfficine.getStrCOMPTECONTRIBUABLE() : "") + ((oTOfficine.getStrREGISTRECOMMERCE() != null && !oTOfficine.getStrREGISTRECOMMERCE().equalsIgnoreCase("")) ? " / RC: " + oTOfficine.getStrREGISTRECOMMERCE() : ""));

    parameters.put("P_H_CI_RI", ((oTOfficine.getStrCENTREIMPOSITION() != null && !oTOfficine.getStrCENTREIMPOSITION().equalsIgnoreCase("")) ? "CI:" + oTOfficine.getStrCENTREIMPOSITION() : "") + ((oTOfficine.getStrREGISTREIMPOSITION() != null && !oTOfficine.getStrREGISTREIMPOSITION().equalsIgnoreCase("")) ? " / RI: " + oTOfficine.getStrREGISTREIMPOSITION() : ""));
    parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
   String finalphonestring = oTOfficine.getStrPHONE() != null ? "Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
            if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
                String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
                for (String va : phone) {
                    finalphonestring += " / " + conversion.PhoneNumberFormat(va);
                }
            }
            parameters.put("P_H_PHONE", finalphonestring);
          
    parameters.put("P_REFERENCE", lg_PREENGISTREMENT_ID);
    parameters.put("P_LG_USER", lg_USER_ID);
    parameters.put("P_SEARCH", search_value + "%");
    /*  parameters.put("P_DT_DEBUT", str_Date_Debut); // a decommenter en cas de probleme 20/11/2016
    parameters.put("P_DT_FIN", str_Date_Fin);*/
    
     parameters.put("P_DT_DEBUT", str_Date_Debut + " " + h_debut);
    parameters.put("P_DT_FIN", str_Date_Fin + " " + h_fin);
    
    parameters.put("P_TYPE_VENTE", str_TYPE_VENTE);
    parameters.put("P_H_DEBUT", h_debut);
    parameters.put("P_H_FIN", h_fin);
    parameters.put("P_KEY_MOVEMENT_FALSE", OTParameters != null ? Integer.parseInt(OTParameters.getStrVALUE()) : 0);
    parameters.put("P_SORTIECAISSE_ESPECE_FALSE", P_SORTIECAISSE_ESPECE_FALSE.intValue());
    parameters.put("P_EMPLACEMENT", lg_EMPLACEMENT_ID);

    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS.toUpperCase());

    new logger().OCategory.info("P_REFERENCE:"+lg_PREENGISTREMENT_ID+"|P_LG_USER:"+lg_USER_ID+"|P_SEARCH:"+search_value 
            + "%|P_DT_DEBUT:"+str_Date_Debut+"|P_DT_FIN:"+str_Date_Fin+"|P_TYPE_VENTE:"+str_TYPE_VENTE+"|P_H_DEBUT:"+h_debut
            + "|P_H_FIN:"+h_fin+"|P_EMPLACEMENT:"+OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    
    
    new logger().OCategory.info("getStrADRESSSEPOSTALE" + oTOfficine.getStrADRESSSEPOSTALE());
    OreportManager.BuildReport(parameters, Ojconnexion);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "journal_vente_" + report_generate_file);
    

%>



