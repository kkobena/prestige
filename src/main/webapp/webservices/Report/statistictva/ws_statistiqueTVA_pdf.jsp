<%-- 
    Document   : ws_operateur_pdf
    Created on : 20 janv. 2016, 10:08:02
    Author     : KKOFFI
--%>

<%@page import="bll.report.StatisticsFamilleArticle"%>
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
        TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        dataManager OdataManager = new dataManager();
        OdataManager.initEntityManager();

       StatisticsFamilleArticle familleArticle=new StatisticsFamilleArticle(OdataManager, OTUser);
        String search_value = "",
                dt_start = LocalDate.now().toString(),
                dt_end = dt_start;

       if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
            dt_start = request.getParameter("dt_start_vente");
          
        }

        if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
            dt_end = request.getParameter("dt_end_vente");

        }
      
        TOfficine oTOfficine = OdataManager.getEm().find(dal.TOfficine.class, "1");
        Map<String, Object> parameters = new HashMap();

        String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
        String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();
        String au = " au " + LocalDate.parse(dt_end).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String du = LocalDate.parse(dt_start).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); 
        if (!dt_start.equals(dt_end)) {
             parameters.put("P_H_CLT_INFOS", "Statistiques des\n Résultats par Taux de TVA du " + du + au);

        } else {
            parameters.put("P_H_CLT_INFOS", "Statistiques des\n Résultats par Taux de TVA du " + du );
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
                for (String va : phone) {
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

         JSONArray array = familleArticle.getTvaDatasReport(dt_start, dt_end,OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        JSONObject data = new JSONObject();
        data.put("root", array);
        JsonDataSourceApp app = new JsonDataSourceApp();
        DateFormat DATEFORMAT = new SimpleDateFormat("HH_mm_ss");
        String fileName = "rp_resultat_tva_" + DATEFORMAT.format(new Date()) + ".pdf";
        app.fill(parameters, data, jdom.scr_report_file + "rp_tva.jrxml", fileName);

        response.sendRedirect(request.getContextPath() + "/data/reports/pdf/" + fileName);

    %>
</html>
