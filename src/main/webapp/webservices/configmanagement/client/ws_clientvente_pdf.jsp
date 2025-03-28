<%-- 
    Document   : ws_operateur_pdf
    Created on : 20 janv. 2016, 10:08:02
    Author     : KKOFFI
--%>

<%@page import="org.json.JSONObject"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <%@page import="java.math.RoundingMode"%>
    <%@page import="java.math.BigDecimal"%>
    <%@page import="bll.common.Parameter"%>


    <%@page import="toolkits.utils.conversion"%>


    <%@page import="dal.TOfficine"%>
    <%@page import="cust_barcode.barecodeManager"%>
    <%@page import="dal.TParameters"%>
    <%@page import="toolkits.utils.jdom"%>
    <%@page import="dal.jconnexion"%>
    <%@page import="report.reportManager"%>


    <%@page import="toolkits.utils.logger"  %>
    <%@page import="dal.dataManager"  %>
    <%@page import="dal.TUser"  %>

    <%@page import="bll.bllBase"  %>
    <%@page import="java.util.*"  %>

    <%@page import="toolkits.utils.date"  %>

    <%@page import="toolkits.parameters.commonparameter"  %>
    <%
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        dataManager OdataManager = new dataManager();
        String dt_start = date.formatterMysqlShort.format(new Date());
        String dt_end = date.formatterMysqlShort.format(new Date());
        String search_value = "%%", tierpayantClient = "%%", lg_COMPTE_CLIENT_ID = "", client = "";
        if (request.getParameter("lg_COMPTE_CLIENT_ID") != null && !"".equals(request.getParameter("lg_COMPTE_CLIENT_ID"))) {
            lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");

        }
        if (request.getParameter("tierpayantClient") != null && !"".equals(request.getParameter("tierpayantClient"))) {
            tierpayantClient = request.getParameter("tierpayantClient");
        }
        if (request.getParameter("client") != null && !"".equals(request.getParameter("client"))) {
            client = request.getParameter("client");
        }
        if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
            dt_start = request.getParameter("dt_start_vente");
        }
        if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
            dt_end = request.getParameter("dt_end_vente");

        }

        if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
            search_value = request.getParameter("search_value");
        }

        String pdf_file_name = "rp_clientvente";

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
        Map parameters = new HashMap();
        String scr_report_file = pdf_file_name;
        String report_generate_file = key.GetNumberRandom();
        clientManagement m = new clientManagement(OdataManager);
        JSONObject clientData = m.getClientAchats(lg_COMPTE_CLIENT_ID, dt_start, dt_end, tierpayantClient, search_value);
        new logger().OCategory.info("scr_report_file " + scr_report_file);
        report_generate_file = report_generate_file + ".pdf";
        OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
        OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + pdf_file_name + report_generate_file);

        String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
        String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();

        String periode = date.formatterShort.format(java.sql.Date.valueOf(dt_start));
        String P_PERIODE = "AU : " + periode;
        if (!"".equals(dt_end) && !dt_end.equals(dt_start)) {
            P_PERIODE = "DU " + periode + " AU " + date.formatterShort.format(java.sql.Date.valueOf(dt_end));
            parameters.put("P_DATE_END", dt_end);
        } else {
            parameters.put("P_DATE_END", date.formatterMysql.format(new Date()));
        }
       
        parameters.put("P_DATE_START", dt_start);
        client=client.substring(client.indexOf("[")+1, client.length()-1);
        parameters.put("P_H_CLT_INFOS", "LES VENTES DU CLIENT \n  " + client.toUpperCase() + "\n " + P_PERIODE);

        String P_H_LOGO = jdom.scr_report_file_logo;

        parameters.put("P_H_LOGO", P_H_LOGO);
        parameters.put("P_H_INSTITUTION", P_H_INSTITUTION);

        parameters.put("P_PRINTED_BY", " " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
        parameters.put("P_AUTRE_DESC", oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());

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
            String finalphonestring = oTOfficine.getStrPHONE() != null ? " - Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
            if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
                String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
                for (String va : phone) {
                    finalphonestring += " / " + conversion.PhoneNumberFormat(va);
                }
            }
            P_INSTITUTION_ADRESSE += finalphonestring;
        }
        if (oTOfficine.getStrCOMPTEBANCAIRE() != null) {
            P_INSTITUTION_ADRESSE += " - Compte Bancaire: " + oTOfficine.getStrCOMPTEBANCAIRE();
        }
        if (oTOfficine.getStrNUMCOMPTABLE() != null) {
            P_INSTITUTION_ADRESSE += " - CPT N°: " + oTOfficine.getStrNUMCOMPTABLE();
        }
        parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
        parameters.put("P_FOOTER_RC", P_FOOTER_RC);
        parameters.put("P_SEARCH", "%" + search_value + "%");
        parameters.put("P_COMPTECLIENTID", lg_COMPTE_CLIENT_ID);
        parameters.put("P_TIERSPAYANT", tierpayantClient);
        parameters.put("P_TOTALVENTE", conversion.AmountFormat(new Integer(clientData.get("TOTALVENTE").toString())) ); 
        parameters.put("P_TOTALTP", conversion.AmountFormat(new Integer(clientData.get("TOTALTP").toString())) ); 

        OreportManager.BuildReport(parameters, Ojconnexion);
// fin
        Ojconnexion.CloseConnexion();

        response.sendRedirect("../../../data/reports/pdf/" + pdf_file_name + report_generate_file);

    %>



</html>
