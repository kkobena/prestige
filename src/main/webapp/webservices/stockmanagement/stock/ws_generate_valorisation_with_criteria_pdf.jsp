<%-- 
    Document   : ws_operateur_pdf
    Created on : 20 janv. 2016, 10:08:02
    Author     : KKOFFI
--%>

<%@page import="bll.configManagement.familleManagement"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <%@page import="java.math.RoundingMode"%>
    <%@page import="java.math.BigDecimal"%>
    <%@page import="bll.common.Parameter"%>
    <%@page import="bll.report.StatisticSales"%>
    <%@page import="bll.facture.factureManagement"%>
    <%@page import="bll.entity.EntityData"%>
    <%@page import="dal.TTypeMvtCaisse"%>
    <%@page import="dal.TTiersPayant"%>
    <%@page import="dal.TFacture"%>
    <%@page import="toolkits.utils.conversion"%>
    <%@page import="bll.commandeManagement.orderManagement"%>
    <%@page import="dal.TOrderDetail"%>

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
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

        dataManager OdataManager = new dataManager();
        String P_SUBTITLE = "", P_EMPLACEMENT_ID = "1", P_START = "", P_END = "", str_TYPE_TRANSACTION = "", P_SEARCH = "%%";
        if (request.getParameter("P_SEARCH") != null && !"".equals(request.getParameter("P_SEARCH"))) {
            P_SEARCH = request.getParameter("P_SEARCH");

        }
        if (request.getParameter("str_END") != null && !"".equals(request.getParameter("str_END"))) {
            P_END = request.getParameter("str_END");

        }

        if (request.getParameter("str_BEGIN") != null && !"".equals(request.getParameter("str_BEGIN"))) {
            P_START = request.getParameter("str_BEGIN");

        }

        if (request.getParameter("str_TYPE_TRANSACTION") != null && !"".equals(request.getParameter("str_TYPE_TRANSACTION"))) {
            str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        }

        TParameters OTParameters;
        jdom Ojdom = new jdom();
        Ojdom.InitRessource();
        Ojdom.LoadRessource();
        jconnexion Ojconnexion = new jconnexion();
        Ojconnexion.initConnexion();
        Ojconnexion.OpenConnexion();
        date key = new date();
        reportManager OreportManager = new reportManager();
        OdataManager.initEntityManager();

        TOfficine oTOfficine = OdataManager.getEm().find(dal.TOfficine.class, "1");
        familleManagement OfamilleManager = new familleManagement(OdataManager, OTUser);
        List<EntityData> listtva = new ArrayList<EntityData>();
        Map parameters = new HashMap();
        String file_name = "rp_valoristion";
        if (str_TYPE_TRANSACTION.equals("Famille")) {
            if ("".equals(P_START)) {
                file_name = "rp_valoristion_by_famille";
                P_SUBTITLE = "Famille article";
            } else {
                file_name = "rp_valoristion_by_famille_interval";
                P_SUBTITLE = "Famille article Code " + P_START + " au " + P_END;
            }
        } else if (str_TYPE_TRANSACTION.equals("Emplacement")) {
            if ("".equals(P_START)) {
                file_name = "rp_valoristion";
                P_SUBTITLE = "Emplacement";
            } else {
                file_name = "rp_valoristion_interval";
                P_SUBTITLE = "Emplacement Code " + P_START + " au " + P_END;
            }
        } else if (str_TYPE_TRANSACTION.equals("Grossiste")) {
            if ("".equals(P_START)) {
                file_name = "rp_valoristion_by_grossiste";
                P_SUBTITLE = "Fournisseurs";
            } else {
                file_name = "rp_valoristion_by_grossiste_interval";
                P_SUBTITLE = "Fournisseur Code " + P_START + " au " + P_END;
            }
        }
        String P_TVA_0 = "", P_TVA_18 = "";
       String P_TVA_0_PV = "", P_TVA_18_PV = "", P_TVA_0_PAT = "", P_TVA_18_PAT = "", P_TVA_0_PAF = "", P_TVA_18_PAF = "", P_TVA_0_POND = "", P_TVA_18_POND = "", P_TVA_0_PVG = "", P_TVA_0_PATG = "", P_TVA_0_PAFG = "", P_TVA_0_PONDG = "";
new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION + " P_START " + P_START + " P_SEARCH " + P_SEARCH);
// code décommenté       
if (!"".equals(str_TYPE_TRANSACTION) && "".equals(P_START)) {
            listtva = OfamilleManager.getValorisationTVAData(P_SEARCH + "%");
            if (!listtva.isEmpty()) {
                EntityData tvao = listtva.get(0);
                EntityData tva18 = listtva.get(1);
                P_TVA_0 = tvao.getStr_value5();
                P_TVA_18 = tva18.getStr_value5();
                P_TVA_0_PV = tvao.getStr_value1();
                P_TVA_18_PV = tva18.getStr_value1();
                P_TVA_0_PAT = tvao.getStr_value4();
                P_TVA_18_PAT = tva18.getStr_value4();
                P_TVA_0_PAF = tvao.getStr_value3();
                P_TVA_18_PAF = tva18.getStr_value3();
                P_TVA_0_POND = tvao.getStr_value4();
                P_TVA_18_POND = tva18.getStr_value4();
                P_TVA_0_PVG = P_TVA_0_PV + P_TVA_18_PV;
                P_TVA_0_PATG = P_TVA_0_PAT + P_TVA_18_PAT;
                P_TVA_0_PAFG = P_TVA_0_PAF + P_TVA_18_PAF;
                P_TVA_0_PONDG = P_TVA_0_POND + P_TVA_18_POND;
            }
        }
//fin code décommenté
        
        String scr_report_file = file_name;
        String report_generate_file = key.GetNumberRandom();
        report_generate_file = report_generate_file + ".pdf";
        OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
        OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + file_name + report_generate_file);

        String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
        String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();

        parameters.put("P_H_CLT_INFOS", "Valorisation Simple\n d'Inventaire");

        String P_H_LOGO = jdom.scr_report_file_logo;

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
        parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());

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
        parameters.put("P_SEARCH", P_SEARCH + "%");
        parameters.put("P_EMPLACEMENT_ID", P_EMPLACEMENT_ID + "%");

        parameters.put("P_SUBTITLE", "");

        parameters.put("P_START", P_START);
        parameters.put("P_TVA_0", P_TVA_0);
        parameters.put("P_TVA_18", P_TVA_18 );
        parameters.put("P_TVA_0_PV", Double.valueOf(P_TVA_0_PV));
        parameters.put("P_TVA_18_PV", Double.valueOf(P_TVA_18_PV));
        parameters.put("P_TVA_0_PAT", Double.valueOf(P_TVA_0_PAT));
        parameters.put("P_TVA_18_PAT", Double.valueOf(P_TVA_18_PAT));
        parameters.put("P_TVA_0_PAF",Double.valueOf( P_TVA_0_PAF));
        parameters.put("P_TVA_18_PAF", Double.valueOf(P_TVA_18_PAF));
        parameters.put("P_TVA_0_POND", Double.valueOf(P_TVA_0_POND));
        parameters.put("P_TVA_18_POND", Double.valueOf(P_TVA_18_POND));
        parameters.put("P_TVA_0_POND", Double.valueOf(P_TVA_0_POND));
        parameters.put("P_TVA_0_PVG", Double.valueOf(P_TVA_0_PV) +Double.valueOf(P_TVA_18_PV) );
        parameters.put("P_TVA_0_PATG", Double.valueOf(P_TVA_0_PAT)+Double.valueOf(P_TVA_18_PAT));
        parameters.put("P_TVA_0_PAFG", Double.valueOf(P_TVA_0_PAF)+Double.valueOf(P_TVA_18_PAF)); 
        parameters.put("P_TVA_0_PONDG", P_TVA_0_PONDG+"");

        parameters.put("P_END", P_END);
        parameters.put("P_PATH_SUBREPORT", "D:\\CONF\\LABOREX\\REPORTS\\");
        parameters.put("SUBREPORT_DIR", "D:\\CONF\\LABOREX\\REPORTS\\");

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

        response.sendRedirect("../../../data/reports/pdf/" + file_name + report_generate_file);

    %>



</html>
