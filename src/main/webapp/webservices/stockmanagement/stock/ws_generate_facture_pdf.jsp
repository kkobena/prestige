<%@page import="bll.common.Parameter"%>
<%@page import="bll.printer.DriverPrinter"%>
<%@page import="bll.configManagement.PrinterManager"%>
<%@page import="bll.printer.PrintManangement"%>

<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.preenregistrement.DevisManagement"%>

<%@page import="java.awt.print.PageFormat"%>
<%@page import="java.awt.print.Paper"%>

<%@page import="dal.TOfficine"%>
<%@page import="cust_barcode.barecodeManager"%>
<%@page import="dal.TParameters"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.jconnexion"%>
<%@page import="report.reportManager"%>

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
    date key = new date();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%
    String P_SUBTITLE = "", P_EMPLACEMENT_ID = "1", P_START = "", P_END = "", P_SEARCH = "%%";
    if (request.getParameter("P_SEARCH") != null && !"".equals(request.getParameter("P_SEARCH"))) {
        P_SEARCH = request.getParameter("P_SEARCH");

    }
    int mode = 0;
    String today = date.formatterMysqlShort.format(new Date()), paraDate = today;

    if (request.getParameter("date") != null && !"".equals(request.getParameter("date"))) {
        paraDate = request.getParameter("date");

    }
    if (request.getParameter("str_END") != null && !"".equals(request.getParameter("str_END"))) {
        P_END = request.getParameter("str_END");

    }

    if (request.getParameter("str_BEGIN") != null && !"".equals(request.getParameter("str_BEGIN"))) {
        P_START = request.getParameter("str_BEGIN");

    }

    if (request.getParameter("str_TYPE_TRANSACTION") != null && !"".equals(request.getParameter("str_TYPE_TRANSACTION"))) {
        mode = new Integer(request.getParameter("str_TYPE_TRANSACTION"));
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();

    reportManager OreportManager = new reportManager();
    String file_name = "";
    Map parameters = new HashMap();
    if (paraDate.equals(today)) {
        file_name = "rp_valorisation_stock";
        parameters.put("P_H_CLT_INFOS", "Valorisation Simple\n d'Inventaire du \n" + date.backabaseUiFormat1.format(new Date()));
        if (mode == 1) {
            if ("".equals(P_START)) {
                file_name = "rp_valorisation";
                P_SUBTITLE = "Valorisation par famille d'article";
            } else {
                file_name = "rp_valorisation_famille_interval";
                P_SUBTITLE = "Valorisation par famille d'article: Code " + P_START + " au " + P_END;
            }
        } else if (mode == 2) {
            if ("".equals(P_START)) {
                file_name = "rp_valorisation_emplacement";
                P_SUBTITLE = "Valorisation par  Emplacement";
            } else {
                file_name = "rp_valorisation_emplacement_interval";
                P_SUBTITLE = "Valorisation par  Emplacement: Code " + P_START + " au " + P_END;
            }
        } else if (mode == 3) {
            if ("".equals(P_START)) {
                file_name = "rp_valorisation_grossiste";
                P_SUBTITLE = "Valorisation par  Fournisseurs ";
            } else {
                file_name = "rp_valorisation_grossiste_interval";
                P_SUBTITLE = "Valorisation par  Fournisseurs: Code " + P_START + " au " + P_END;
            }
        }
    } else {
        parameters.put("P_H_CLT_INFOS", "Valorisation Simple\n d'Inventaire du \n" + date.formatterShort.format(java.sql.Date.valueOf(paraDate)));
        file_name = "rp_valorisation_stock2";
        if (mode == 1) {
            if ("".equals(P_START)) {
                file_name = "rp_valorisation2";
                P_SUBTITLE = "Valorisation par famille d'article";
            } else {
                file_name = "rp_valorisation_famille_interval2";
                P_SUBTITLE = "Valorisation par famille d'article: Code " + P_START + " au " + P_END;
            }
        } else if (mode == 2) {
            if ("".equals(P_START)) {
                file_name = "rp_valorisation_emplacement2";
                P_SUBTITLE = "Valorisation par  Emplacement";
            } else {
                file_name = "rp_valorisation_emplacement_interval2";
                P_SUBTITLE = "Valorisation par  Emplacement: Code " + P_START + " au " + P_END;
            }
        } else if (mode == 3) {
            if ("".equals(P_START)) {
                file_name = "rp_valorisation_grossiste2";
                P_SUBTITLE = "Valorisation par  Fournisseurs ";
            } else {
                file_name = "rp_valorisation_grossiste_interval2";
                P_SUBTITLE = "Valorisation par  Fournisseurs: Code " + P_START + " au " + P_END;
            }
        }

    }

    String scr_report_file = file_name;
    System.out.println("file_name ***************** " + file_name);
    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "rp_valorisation" + report_generate_file);

    OdataManager.initEntityManager();

    TOfficine oTOfficine = OdataManager.getEm().find(TOfficine.class, "1");

    String P_H_LOGO = jdom.scr_report_file_logo;
    String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
    String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();

    parameters.put("P_PATH_SUBREPORT", Ojdom.scr_report_file);
    System.out.println("Ojdom.scr_report_file " + Ojdom.scr_report_file);

    String P_H_CC_P_H_RC = "", P_H_CI_P_H_RI = "";
    if ((oTOfficine.getStrCOMPTECONTRIBUABLE() != null && !"".equals(oTOfficine.getStrCOMPTECONTRIBUABLE())) && (oTOfficine.getStrREGISTRECOMMERCE() != null && !"".equals(oTOfficine.getStrREGISTRECOMMERCE()))) {
        P_H_CC_P_H_RC = oTOfficine.getStrCOMPTECONTRIBUABLE() + " / " + oTOfficine.getStrREGISTRECOMMERCE();
    } else if ((oTOfficine.getStrCOMPTECONTRIBUABLE() != null && !"".equals(oTOfficine.getStrCOMPTECONTRIBUABLE())) && (oTOfficine.getStrREGISTRECOMMERCE() == null || "".equals(oTOfficine.getStrREGISTRECOMMERCE()))) {
        P_H_CC_P_H_RC = oTOfficine.getStrCOMPTECONTRIBUABLE();
    } else if ((oTOfficine.getStrCOMPTECONTRIBUABLE() == null || "".equals(oTOfficine.getStrCOMPTECONTRIBUABLE())) && (oTOfficine.getStrREGISTREIMPOSITION() != null || !"".equals(oTOfficine.getStrREGISTREIMPOSITION()))) {
        P_H_CC_P_H_RC = oTOfficine.getStrREGISTRECOMMERCE();
    }
    if ((oTOfficine.getStrCENTREIMPOSITION() != null && !"".equals(oTOfficine.getStrCENTREIMPOSITION())) && (oTOfficine.getStrREGISTREIMPOSITION() != null && !"".equals(oTOfficine.getStrREGISTREIMPOSITION()))) {
        P_H_CI_P_H_RI = oTOfficine.getStrCENTREIMPOSITION() + " / " + oTOfficine.getStrREGISTRECOMMERCE();
    } else if ((oTOfficine.getStrCENTREIMPOSITION() != null && !"".equals(oTOfficine.getStrCENTREIMPOSITION())) && (oTOfficine.getStrREGISTREIMPOSITION() == null || "".equals(oTOfficine.getStrREGISTREIMPOSITION()))) {
        P_H_CI_P_H_RI = oTOfficine.getStrCENTREIMPOSITION();
    } else if ((oTOfficine.getStrCENTREIMPOSITION() == null || "".equals(oTOfficine.getStrCENTREIMPOSITION())) && (oTOfficine.getStrREGISTREIMPOSITION() != null || !"".equals(oTOfficine.getStrREGISTREIMPOSITION()))) {
        P_H_CI_P_H_RI = oTOfficine.getStrREGISTREIMPOSITION();
    }

    parameters.put("P_H_LOGO", P_H_LOGO);
    parameters.put("P_H_INSTITUTION", P_H_INSTITUTION);
    parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
    parameters.put("P_PRINTED_BY", " " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC", oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());

    if (!"".equals(P_H_CI_P_H_RI)) {
        parameters.put("P_H_CI", "CI:" + P_H_CI_P_H_RI);
    } else {
        parameters.put("P_H_CI", " ");
    }
    if (!"".equals(P_H_CC_P_H_RC)) {
        parameters.put("P_H_CC", "CC:" + P_H_CC_P_H_RC);
    } else {
        parameters.put("P_H_CC", " ");
    }
    P_EMPLACEMENT_ID = OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID();
    parameters.put("P_SEARCH", P_SEARCH + "%");
    parameters.put("P_EMPLACEMENT_ID", P_EMPLACEMENT_ID);
    parameters.put("P_SUBTITLE", P_SUBTITLE);
    parameters.put("P_START", P_START);
    parameters.put("P_END", P_END);
    parameters.put("P_START_SUB", P_START);
    parameters.put("P_END_SUB", P_END);
    parameters.put("dt_DATE", paraDate);
    parameters.put("P_EMPLACEMENT", P_EMPLACEMENT_ID);

    String finalphonestring = oTOfficine.getStrPHONE() != null ? "Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
    if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
        String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
        for (String va : phone) {
            finalphonestring += " / " + conversion.PhoneNumberFormat(va);
        }
    }
    parameters.put("P_H_PHONE", finalphonestring);

    OreportManager.BuildReport(parameters, Ojconnexion);
    Ojconnexion.CloseConnexion();

    String str_final_file = Ojdom.scr_report_pdf + "rp_valorisation" + report_generate_file;
    new logger().OCategory.info("str_final_file -----------" + str_final_file);

    //String str_final_file = Ojdom.scr_report_pdf+"facture_vente_" + report_generate_file;
    response.sendRedirect("../../../data/reports/pdf/" + "rp_valorisation" + report_generate_file);


%>



