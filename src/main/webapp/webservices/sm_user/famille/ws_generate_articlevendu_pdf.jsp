<%-- 
    Document   : ws_operateur_pdf
    Created on : 20 janv. 2016, 10:08:02
    Author     : KKOFFI
--%>

<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="bll.report.JsonDataSourceApp"%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>

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
        String str_Date_Debut = LocalDate.now().toString(), str_Date_Fin = str_Date_Debut,
                h_debut = "", h_fin = "", search_value = "", str_TYPE_TRANSACTION = "", prixachatFiltre = "TOUT",  stockFiltre = "TOUT";

        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

        int int_NUMBER = 0, stock = Integer.MIN_VALUE;

        dataManager OdataManager = new dataManager();
        OdataManager.initEntityManager();

        if (request.getParameter("search_value") != null) {
            search_value = request.getParameter("search_value");
            new logger().OCategory.info("search_value :" + search_value);
        }

        if (request.getParameter("str_TYPE_TRANSACTION") != null) {
            str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
            new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
        }
        if (request.getParameter("int_NUMBER") != null && !request.getParameter("int_NUMBER").equalsIgnoreCase("")) {
            int_NUMBER = Integer.parseInt(request.getParameter("int_NUMBER"));
            new logger().OCategory.info("int_NUMBER " + int_NUMBER);
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
        if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
            search_value = request.getParameter("query");
        }
        if (request.getParameter("prixachatFiltre") != null) {
            prixachatFiltre = request.getParameter("prixachatFiltre");
        }
          if (request.getParameter("stockFiltre") != null) {
        stockFiltre = request.getParameter("stockFiltre");
    }
    if (request.getParameter("stock") != null) {
        try {
            stock = Integer.parseInt(request.getParameter("stock"));
        } catch (Exception e) {
        }

    }
        TOfficine oTOfficine = OdataManager.getEm().find(dal.TOfficine.class, "1");
        Map<String, Object> parameters = new HashMap();

        String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
        String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();
        String au = " au " + LocalDate.parse(str_Date_Fin).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String du = LocalDate.parse(str_Date_Debut).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!str_Date_Debut.equals(str_Date_Fin)) {
            parameters.put("P_H_CLT_INFOS", "Liste des articles vendus du " + du + au);
        } else {
            parameters.put("P_H_CLT_INFOS", "Liste des articles vendus au " + du);
        }

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
            String finalphonestring = oTOfficine.getStrPHONE() != null ? "- Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
            if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
                String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
                for (String va  : phone) {
                    finalphonestring += " / " + conversion.PhoneNumberFormat(va);
                }
            }
            P_INSTITUTION_ADRESSE += " -  " + finalphonestring;
        }
        if (oTOfficine.getStrCOMPTEBANCAIRE() != null) {
            P_INSTITUTION_ADRESSE += " - Compte Bancaire: " + oTOfficine.getStrCOMPTEBANCAIRE();
        }
        if (oTOfficine.getStrNUMCOMPTABLE() != null) {
            P_INSTITUTION_ADRESSE += " - CPT N°: " + oTOfficine.getStrNUMCOMPTABLE();
        }
        parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
        parameters.put("P_FOOTER_RC", P_FOOTER_RC);
        Preenregistrement p = new Preenregistrement(OdataManager, OTUser);
        JSONObject arrayObj = p.getArticlesVendus(search_value, true, str_Date_Debut,
                str_Date_Fin, h_debut, h_fin,
                str_TYPE_TRANSACTION,
                int_NUMBER, 0, 0, "", prixachatFiltre, stock, stockFiltre);
        JsonDataSourceApp app = new JsonDataSourceApp();
        DateFormat DATEFORMAT = new SimpleDateFormat("HH_mm_ss");
        String fileName = "rp_articlesvendus_" + DATEFORMAT.format(new Date()) + ".pdf";
        app.fill(parameters, arrayObj, jdom.scr_report_file + "rp_articlesvendus.jrxml", fileName);

        response.sendRedirect(request.getContextPath() + "/data/reports/pdf/" + fileName);

    %>
</html>
