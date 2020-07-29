<%@page import="bll.preenregistrement.DevisManagement"%>
<%@page import="dal.TCompteClient"%>
<%@page import="toolkits.utils.conversion"%>
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

<%     String lg_PREENREGISTREMENT_ID = "";
    Translate OTranslate = new Translate();
    /*dataManager OdataManager = new dataManager();
    TParameters OTParameters;*/
    bllBase ObllBase = new bllBase();
    TUser OTUser = null;
    TCompteClient OTCompteClient = null;
%>


<!-- fin logic de gestion des page -->

<%
    String dt_start = date.formatterMysqlShort.format(new Date()), dt_end = dt_start;
    if (request.getParameter("dt_start") != null && !"".equals(request.getParameter("dt_start"))) {
        dt_start = request.getParameter("dt_start");

    }

    if (request.getParameter("dt_end") != null && !"".equals(request.getParameter("dt_end"))) {
        dt_end = request.getParameter("dt_end");

    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date key = new date();
    reportManager OreportManager = new reportManager();
    String scr_report_file = "rp_mvt_caisse";
    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "rp_mvt_caisse_" + report_generate_file);

    bllBase obllBase = new bllBase();
    obllBase.checkDatamanager();

    TPreenregistrement oTPreenregistrement = obllBase.getOdataManager().getEm().find(dal.TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
    TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");

    String P_H_TITTLE = "";

    String P_H_CLT_INFOS = "";
    String periode = "au " + date.formatterShort.format(java.sql.Date.valueOf(dt_start)) ;
    if (!dt_start.equals(dt_end)) {
        periode = "du " + date.formatterShort.format(java.sql.Date.valueOf(dt_start)) + " au " + date.formatterShort.format(java.sql.Date.valueOf(dt_end));
    }
    P_H_CLT_INFOS = " Liste des mouvements de caisse \n" + periode;

   
    Map parameters = new HashMap();

    parameters.put("P_H_TITTLE", P_H_TITTLE);
    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
    String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();

    String P_H_LOGO = jdom.scr_report_file_logo;

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
          

    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    parameters.put("P_H_LOGO", P_H_LOGO);
    parameters.put("P_H_INSTITUTION", P_H_INSTITUTION);

    parameters.put("P_PRINTED_BY", " " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());

    String P_FOOTER_RC = "";

    if (oTOfficine.getStrREGISTRECOMMERCE() != null) {
        P_FOOTER_RC += "RC N° " + oTOfficine.getStrREGISTRECOMMERCE();
    }

    if (oTOfficine.getStrCOMPTECONTRIBUABLE() != null) {
        P_FOOTER_RC += " - CC N° " + oTOfficine.getStrCOMPTECONTRIBUABLE();
    }
    if (oTOfficine.getStrREGISTREIMPOSITION() != null) {
        P_FOOTER_RC += " - Régime d'Imposition " + oTOfficine.getStrREGISTREIMPOSITION();
    }
    if (oTOfficine.getStrCENTREIMPOSITION() != null) {
        P_FOOTER_RC += " - Centre des Impôts: " + oTOfficine.getStrCENTREIMPOSITION();
    }

    if (oTOfficine.getStrPHONE() != null) {
        P_INSTITUTION_ADRESSE += " - Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE());
    }
    if (oTOfficine.getStrCOMPTEBANCAIRE() != null) {
        P_INSTITUTION_ADRESSE += " - Compte Bancaire: " + oTOfficine.getStrCOMPTEBANCAIRE();
    }
    if (oTOfficine.getStrNUMCOMPTABLE() != null) {
        P_INSTITUTION_ADRESSE += " - CPT N°: " + oTOfficine.getStrNUMCOMPTABLE();
    }

    parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
    parameters.put("P_FOOTER_RC", P_FOOTER_RC);

    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    parameters.put("P_EMPLACEMENT_ID", OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    parameters.put("P_USER_ID", "%%");
    parameters.put("dt_DEBUT", dt_start);
    parameters.put("dt_FIN", dt_end);
    OreportManager.BuildReport(parameters, Ojconnexion);

    ObllBase.setKey(new date());
    ObllBase.setOTranslate(OTranslate);
    ObllBase.setOTUser(OTUser);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "rp_mvt_caisse_" + report_generate_file);

%>


