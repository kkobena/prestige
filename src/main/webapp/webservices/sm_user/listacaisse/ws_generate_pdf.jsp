<%@page import="bll.report.JournalVente"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.common.Parameter"%>
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
    TParameters OTParameters;
    List<EntityData> listTMvtCaissesFalse = new ArrayList<EntityData>();
%>


<!-- fin logic de gestion des page -->

<%     String lg_EMPLACEMENT_ID = "";
    
    String str_Date_Debut = date.DateToString(new Date(), date.formatterMysqlShort), str_Date_Fin = date.DateToString(new Date(), date.formatterMysqlShort), search_value = "",
            h_debut = "00:00", h_fin = "23:59", lg_USER_ID = "%%", lg_TYPE_REGLEMENT_ID = "%%";
Double P_SORTIECAISSE_ESPECE_FALSE = 0d;
    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value :" + search_value);
    }

    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null && !request.getParameter("lg_TYPE_REGLEMENT_ID").equalsIgnoreCase("")) {
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");
        new logger().OCategory.info("lg_TYPE_REGLEMENT_ID :" + lg_TYPE_REGLEMENT_ID);
    }

    if (request.getParameter("lg_USER_ID") != null && !request.getParameter("lg_USER_ID").equalsIgnoreCase("")) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
       
    }
    if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        new logger().OCategory.info("h_debut :" + h_debut);
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");
        new logger().OCategory.info("h_fin :" + h_fin);
    }
    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null && !request.getParameter("lg_TYPE_REGLEMENT_ID").equalsIgnoreCase("")) {
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");
        new logger().OCategory.info("lg_TYPE_REGLEMENT_ID :" + lg_TYPE_REGLEMENT_ID);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    jdom.InitRessource();
    jdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    OdataManager.initEntityManager();
    reportManager OreportManager = new reportManager();
    privilege Oprivilege = new privilege(OdataManager, OTUser);
     TparameterManager OTparameterManager = new TparameterManager(OdataManager);
     JournalVente OjournalVente = new JournalVente(OdataManager, OTUser);
    
    if (Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_SHOW_ALL_ACTIVITY)) {
        lg_EMPLACEMENT_ID = "%%";
    } else {
        lg_EMPLACEMENT_ID = OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID();
    }
    OTParameters = OTparameterManager.getParameter(Parameter.KEY_MOVEMENT_FALSE);


    String scr_report_file = "rp_liste_caisse";
    String report_generate_file = date.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(jdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(jdom.scr_report_pdf + "liste_caisse_" + report_generate_file);
    
    new logger().OCategory.info("Dans edition scr_report_file " + scr_report_file);
    
    
    //code ajouté 24/08/2016
    
     if (OTParameters != null && Integer.parseInt(OTParameters.getStrVALUE()) == 1 && !str_Date_Debut.equalsIgnoreCase(str_Date_Fin)) {
        listTMvtCaissesFalse = OjournalVente.getAllMouvmentsCaisse(str_Date_Debut, str_Date_Fin, false);
    }
    for (EntityData Odata : listTMvtCaissesFalse) {
        P_SORTIECAISSE_ESPECE_FALSE += (-1) * Double.valueOf(Odata.getStr_value1());
    }
    //fin code ajouté 24/08/2016
    TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");
    String P_H_CLT_INFOS = "LISTE DES CAISSES DU " + date.formatterShort.format(java.sql.Date.valueOf(str_Date_Debut)) + " AU " + date.formatterShort.format(java.sql.Date.valueOf(str_Date_Fin));
    Map parameters = new HashMap();

    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);

    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME());
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
          
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS.toUpperCase());

    parameters.put("P_SEARCH", search_value + "%");
    parameters.put("P_LG_USER", lg_USER_ID);
    parameters.put("P_LG_TYPE_REGLEMENT", lg_TYPE_REGLEMENT_ID);
    parameters.put("P_SORTIECAISSE_ESPECE_FALSE", P_SORTIECAISSE_ESPECE_FALSE.intValue());

    parameters.put("P_DT_DEBUT", str_Date_Debut);
    parameters.put("P_DT_FIN", str_Date_Fin);
    /*parameters.put("P_H_DEBUT", date.DateToString(date.stringToDate(str_Date_Debut, date.formatterMysql), date.NomadicUiFormat_Time));
     parameters.put("P_H_FIN", date.DateToString(date.stringToDate(str_Date_Fin, date.formatterMysql), date.NomadicUiFormat_Time));*/
    parameters.put("P_H_DEBUT", h_debut);
    parameters.put("P_H_FIN", h_fin);
    parameters.put("P_H_EMPLACEMENT", lg_EMPLACEMENT_ID);
    parameters.put("P_KEY_MOVEMENT_FALSE", OTParameters != null ? Integer.parseInt(OTParameters.getStrVALUE()) : 0);

    OreportManager.BuildReport(parameters, Ojconnexion);

    Ojconnexion.CloseConnexion();


  //  String str_final_file = jdom.scr_report_pdf + "liste_caisse_" + report_generate_file;
 
    response.sendRedirect("../../../data/reports/pdf/" + "liste_caisse_" + report_generate_file);


%>



