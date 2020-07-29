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

<%!
    dataManager OdataManager = new dataManager();


%>


<!-- fin logic de gestion des page -->

<%
    String dt_start = "";
    String dt_end = "";
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");
        System.out.println("dt_start_vente   " + request.getParameter("dt_start_vente"));
    }

    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");

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
    StatisticSales statisticSales = new StatisticSales(OdataManager);
    List<TPreenregistrement> list = statisticSales.getPreenregistrementsForSalesStatistics(dt_start, dt_end,OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());

    TOfficine oTOfficine = OdataManager.getEm().find(dal.TOfficine.class, "1");

    String scr_report_file = "rp_statistic_ventes";
    String report_generate_file = key.GetNumberRandom();

    new logger().OCategory.info("scr_report_file " + scr_report_file);
    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "rp_statistic_ventes" + report_generate_file);

// parameters.put("P_TOTAL_DEVIS", conversion.GetNumberTowords(Double.parseDouble(total_devis + "")) + " -- (" + conversion.AmountFormat(total_devis) +")");
    String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
    String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();
    String periode = date.formatterShort.format(java.sql.Date.valueOf(dt_start));
    String P_H_CLT_INFOS = "PERIODE DU " + periode;
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

    String janv = "1", fev = "2", mars = "3", avri = "4", mai = "5", juin = "6", juil = "7", aout = "8", sept = "9", oct = "10", nov = "11", dec = "12";
    /* mois de année*/
    String P_MONTH_1_NCLIENT = "",
            P_MONTH_1_NCLIENT_CUMUL = "",
            P_MONTH_1_BRUTTTC = "",
            P_MONTH_1_BRUTTTC_CUMUL = "",
            P_MONTH_1_NETTTC = "",
            P_MONTH_1_NETTTC_CUMUL = "",
            P_MONTH_1_REMISE = "",
            P_MONTH_1_REMISE_CUMUL = "",
            P_MONTH_1_PANORD = "",
            P_MONTH_1_PANORD_CUMUL = "",
            P_MONTH_1_PANNO = "",
            P_MONTH_1_PANNO_CUMUL = "",
            P_MONTH_1_VENTESVO = "",
            P_MONTH_1_VENTESVO_CUMUL = "",
            P_MONTH_1_VENTESVNO = "",
            P_MONTH_1_VENTESVNO_CUMUL = "",
            P_MONTH_1_VNOPERCENT_MONTH = "",
            P_MONTH_1_VNOPERCENT_CUMUL = "",
            P_MONTH_1_VOPERCENT_MONTH = "",
            P_MONTH_1_VOPERCENT_CUMUL = "",
            P_MONTH_2_NCLIENT = "",
            P_MONTH_2_NCLIENT_CUMUL = "",
            P_MONTH_2_BRUTTTC = "",
            P_MONTH_2_BRUTTTC_CUMUL = "",
            P_MONTH_2_NETTTC = "",
            P_MONTH_2_NETTTC_CUMUL = "",
            P_MONTH_2_REMISE = "",
            P_MONTH_2_REMISE_CUMUL = "",
            P_MONTH_2_PANORD = "",
            P_MONTH_2_PANORD_CUMUL = "",
            P_MONTH_2_PANNO = "",
            P_MONTH_2_PANNO_CUMUL = "",
            P_MONTH_2_VENTESVO = "",
            P_MONTH_2_VENTESVO_CUMUL = "",
            P_MONTH_2_VENTESVNO = "",
            P_MONTH_2_VENTESVNO_CUMUL = "",
            P_MONTH_2_VNOPERCENT_MONTH = "",
            P_MONTH_2_VNOPERCENT_CUMUL = "",
            P_MONTH_2_VOPERCENT_MONTH = "",
            P_MONTH_2_VOPERCENT_CUMUL = "",
            P_MONTH_3_NCLIENT = "",
            P_MONTH_3_NCLIENT_CUMUL = "",
            P_MONTH_3_BRUTTTC = "",
            P_MONTH_3_BRUTTTC_CUMUL = "",
            P_MONTH_3_NETTTC = "",
            P_MONTH_3_NETTTC_CUMUL = "",
            P_MONTH_3_REMISE = "",
            P_MONTH_3_REMISE_CUMUL = "",
            P_MONTH_3_PANORD = "",
            P_MONTH_3_PANORD_CUMUL = "",
            P_MONTH_3_PANNO = "",
            P_MONTH_3_PANNO_CUMUL = "",
            P_MONTH_3_VENTESVO = "",
            P_MONTH_3_VENTESVO_CUMUL = "",
            P_MONTH_3_VENTESVNO = "",
            P_MONTH_3_VENTESVNO_CUMUL = "",
            P_MONTH_3_VNOPERCENT_MONTH = "",
            P_MONTH_3_VNOPERCENT_CUMUL = "",
            P_MONTH_3_VOPERCENT_MONTH = "",
            P_MONTH_3_VOPERCENT_CUMUL = "",
            P_MONTH_4_NCLIENT = "",
            P_MONTH_4_NCLIENT_CUMUL = "",
            P_MONTH_4_BRUTTTC = "",
            P_MONTH_4_BRUTTTC_CUMUL = "",
            P_MONTH_4_NETTTC = "",
            P_MONTH_4_NETTTC_CUMUL = "",
            P_MONTH_4_REMISE = "",
            P_MONTH_4_REMISE_CUMUL = "",
            P_MONTH_4_PANORD = "",
            P_MONTH_4_PANORD_CUMUL = "",
            P_MONTH_4_PANNO = "",
            P_MONTH_4_PANNO_CUMUL = "",
            P_MONTH_4_VENTESVO = "",
            P_MONTH_4_VENTESVO_CUMUL = "",
            P_MONTH_4_VENTESVNO = "",
            P_MONTH_4_VENTESVNO_CUMUL = "",
            P_MONTH_4_VNOPERCENT_MONTH = "",
            P_MONTH_4_VNOPERCENT_CUMUL = "",
            P_MONTH_4_VOPERCENT_MONTH = "",
            P_MONTH_4_VOPERCENT_CUMUL = "",
            P_MONTH_5_NCLIENT = "",
            P_MONTH_5_NCLIENT_CUMUL = "",
            P_MONTH_5_BRUTTTC = "",
            P_MONTH_5_BRUTTTC_CUMUL = "",
            P_MONTH_5_NETTTC = "",
            P_MONTH_5_NETTTC_CUMUL = "",
            P_MONTH_5_REMISE = "",
            P_MONTH_5_REMISE_CUMUL = "",
            P_MONTH_5_PANORD = "",
            P_MONTH_5_PANORD_CUMUL = "",
            P_MONTH_5_PANNO = "",
            P_MONTH_5_PANNO_CUMUL = "",
            P_MONTH_5_VENTESVO = "",
            P_MONTH_5_VENTESVO_CUMUL = "",
            P_MONTH_5_VENTESVNO = "",
            P_MONTH_5_VENTESVNO_CUMUL = "",
            P_MONTH_5_VNOPERCENT_MONTH = "",
            P_MONTH_5_VNOPERCENT_CUMUL = "",
            P_MONTH_5_VOPERCENT_MONTH = "",
            P_MONTH_5_VOPERCENT_CUMUL = "",
            P_MONTH_6_NCLIENT = "",
            P_MONTH_6_NCLIENT_CUMUL = "",
            P_MONTH_6_BRUTTTC = "",
            P_MONTH_6_BRUTTTC_CUMUL = "",
            P_MONTH_6_NETTTC = "",
            P_MONTH_6_NETTTC_CUMUL = "",
            P_MONTH_6_REMISE = "",
            P_MONTH_6_REMISE_CUMUL = "",
            P_MONTH_6_PANORD = "",
            P_MONTH_6_PANORD_CUMUL = "",
            P_MONTH_6_PANNO = "",
            P_MONTH_6_PANNO_CUMUL = "",
            P_MONTH_6_VENTESVO = "",
            P_MONTH_6_VENTESVO_CUMUL = "",
            P_MONTH_6_VENTESVNO = "",
            P_MONTH_6_VENTESVNO_CUMUL = "",
            P_MONTH_6_VNOPERCENT_MONTH = "",
            P_MONTH_6_VNOPERCENT_CUMUL = "",
            P_MONTH_6_VOPERCENT_MONTH = "",
            P_MONTH_6_VOPERCENT_CUMUL = "",
            P_MONTH_7_NCLIENT = "",
            P_MONTH_7_NCLIENT_CUMUL = "",
            P_MONTH_7_BRUTTTC = "",
            P_MONTH_7_BRUTTTC_CUMUL = "",
            P_MONTH_7_NETTTC = "",
            P_MONTH_7_NETTTC_CUMUL = "",
            P_MONTH_7_REMISE = "",
            P_MONTH_7_REMISE_CUMUL = "",
            P_MONTH_7_PANORD = "",
            P_MONTH_7_PANORD_CUMUL = "",
            P_MONTH_7_PANNO = "",
            P_MONTH_7_PANNO_CUMUL = "",
            P_MONTH_7_VENTESVO = "",
            P_MONTH_7_VENTESVO_CUMUL = "",
            P_MONTH_7_VENTESVNO = "",
            P_MONTH_7_VENTESVNO_CUMUL = "",
            P_MONTH_7_VNOPERCENT_MONTH = "",
            P_MONTH_7_VNOPERCENT_CUMUL = "",
            P_MONTH_7_VOPERCENT_MONTH = "",
            P_MONTH_7_VOPERCENT_CUMUL = "",
            P_MONTH_8_NCLIENT = "",
            P_MONTH_8_NCLIENT_CUMUL = "",
            P_MONTH_8_BRUTTTC = "",
            P_MONTH_8_BRUTTTC_CUMUL = "",
            P_MONTH_8_NETTTC = "",
            P_MONTH_8_NETTTC_CUMUL = "",
            P_MONTH_8_REMISE = "",
            P_MONTH_8_REMISE_CUMUL = "",
            P_MONTH_8_PANORD = "",
            P_MONTH_8_PANORD_CUMUL = "",
            P_MONTH_8_PANNO = "",
            P_MONTH_8_PANNO_CUMUL = "",
            P_MONTH_8_VENTESVO = "",
            P_MONTH_8_VENTESVO_CUMUL = "",
            P_MONTH_8_VENTESVNO = "",
            P_MONTH_8_VENTESVNO_CUMUL = "",
            P_MONTH_8_VNOPERCENT_MONTH = "",
            P_MONTH_8_VNOPERCENT_CUMUL = "",
            P_MONTH_8_VOPERCENT_MONTH = "",
            P_MONTH_8_VOPERCENT_CUMUL = "",
            P_MONTH_9_NCLIENT = "",
            P_MONTH_9_NCLIENT_CUMUL = "",
            P_MONTH_9_BRUTTTC = "",
            P_MONTH_9_BRUTTTC_CUMUL = "",
            P_MONTH_9_NETTTC = "",
            P_MONTH_9_NETTTC_CUMUL = "",
            P_MONTH_9_REMISE = "",
            P_MONTH_9_REMISE_CUMUL = "",
            P_MONTH_9_PANORD = "",
            P_MONTH_9_PANORD_CUMUL = "",
            P_MONTH_9_PANNO = "",
            P_MONTH_9_PANNO_CUMUL = "",
            P_MONTH_9_VENTESVO = "",
            P_MONTH_9_VENTESVO_CUMUL = "",
            P_MONTH_9_VENTESVNO = "",
            P_MONTH_9_VENTESVNO_CUMUL = "",
            P_MONTH_9_VNOPERCENT_MONTH = "",
            P_MONTH_9_VNOPERCENT_CUMUL = "",
            P_MONTH_9_VOPERCENT_MONTH = "",
            P_MONTH_9_VOPERCENT_CUMUL = "",
            P_MONTH_10_NCLIENT = "",
            P_MONTH_10_NCLIENT_CUMUL = "",
            P_MONTH_10_BRUTTTC = "",
            P_MONTH_10_BRUTTTC_CUMUL = "",
            P_MONTH_10_NETTTC = "",
            P_MONTH_10_NETTTC_CUMUL = "",
            P_MONTH_10_REMISE = "",
            P_MONTH_10_REMISE_CUMUL = "",
            P_MONTH_10_PANORD = "",
            P_MONTH_10_PANORD_CUMUL = "",
            P_MONTH_10_PANNO = "",
            P_MONTH_10_PANNO_CUMUL = "",
            P_MONTH_10_VENTESVO = "",
            P_MONTH_10_VENTESVO_CUMUL = "",
            P_MONTH_10_VENTESVNO = "",
            P_MONTH_10_VENTESVNO_CUMUL = "",
            P_MONTH_10_VNOPERCENT_MONTH = "",
            P_MONTH_10_VNOPERCENT_CUMUL = "",
            P_MONTH_10_VOPERCENT_MONTH = "",
            P_MONTH_10_VOPERCENT_CUMUL = "",
            P_MONTH_11_NCLIENT = "",
            P_MONTH_11_NCLIENT_CUMUL = "",
            P_MONTH_11_BRUTTTC = "",
            P_MONTH_11_BRUTTTC_CUMUL = "",
            P_MONTH_11_NETTTC = "",
            P_MONTH_11_NETTTC_CUMUL = "",
            P_MONTH_11_REMISE = "",
            P_MONTH_11_REMISE_CUMUL = "",
            P_MONTH_11_PANORD = "",
            P_MONTH_11_PANORD_CUMUL = "",
            P_MONTH_11_PANNO = "",
            P_MONTH_11_PANNO_CUMUL = "",
            P_MONTH_11_VENTESVO = "",
            P_MONTH_11_VENTESVO_CUMUL = "",
            P_MONTH_11_VENTESVNO = "",
            P_MONTH_11_VENTESVNO_CUMUL = "",
            P_MONTH_11_VNOPERCENT_MONTH = "",
            P_MONTH_11_VNOPERCENT_CUMUL = "",
            P_MONTH_11_VOPERCENT_MONTH = "",
            P_MONTH_11_VOPERCENT_CUMUL = "",
            P_MONTH_12_NCLIENT = "",
            P_MONTH_12_NCLIENT_CUMUL = "",
            P_MONTH_12_BRUTTTC = "",
            P_MONTH_12_BRUTTTC_CUMUL = "",
            P_MONTH_12_NETTTC = "",
            P_MONTH_12_NETTTC_CUMUL = "",
            P_MONTH_12_REMISE = "",
            P_MONTH_12_REMISE_CUMUL = "",
            P_MONTH_12_PANORD = "",
            P_MONTH_12_PANORD_CUMUL = "",
            P_MONTH_12_PANNO = "",
            P_MONTH_12_PANNO_CUMUL = "",
            P_MONTH_12_VENTESVO = "",
            P_MONTH_12_VENTESVO_CUMUL = "",
            P_MONTH_12_VENTESVNO = "",
            P_MONTH_12_VENTESVNO_CUMUL = "",
            P_MONTH_12_VNOPERCENT_MONTH = "",
            P_MONTH_12_VNOPERCENT_CUMUL = "",
            P_MONTH_12_VOPERCENT_MONTH = "",
            P_MONTH_12_VOPERCENT_CUMUL = "";

    /* start boocle for */
    int count_1 = 0, count_1_cumul = 0, count_vo_1 = 0, count_vo_cumul_1 = 0, count_vno_1 = 0, count_vno_cumul_1 = 0;
    double brut_1 = 0, brut_1_cumul = 0;
    double remise_1 = 0, remise_1_cumul = 0;
    double net_1 = 0, net_1_cumul = 0;
    double pan_moy_vo_1 = 0, pan_moy_vo_cumul_1 = 0;
    double pan_moy_vno_1 = 0, pan_moy_vno_cumul_1 = 0;
    double ventes_vo = 0, ventes_vo_cumul = 0, ventes_vno = 0, ventes_vno_cumul = 0;
    double vente_percent_vno = 0, vente_percent_vno_cumul = 0;
    double vente_percent_vo = 0, vente_percent_vo_cumul = 0;
    /* variable FEV*/

    int count_2 = 0, count_2_cumul = 0, count_vo_2 = 0, count_vo_cumul_2 = 0, count_vno_2 = 0, count_vno_cumul_2 = 0;
    double brut_2 = 0, brut_2_cumul = 0;
    double remise_2 = 0, remise_2_cumul = 0;
    double net_2 = 0, net_2_cumul = 0;
    double pan_moy_vo_2 = 0, pan_moy_vo_cumul_2 = 0;
    double pan_moy_vno_2 = 0, pan_moy_vno_cumul_2 = 0;
    double ventes_vo_2 = 0, ventes_vo_cumul_2 = 0, ventes_vno_2 = 0, ventes_vno_cumul_2 = 0;
    double vente_percent_vno_2 = 0, vente_percent_vno_cumul_2 = 0;
    double vente_percent_vo_2 = 0, vente_percent_vo_cumul_2 = 0;
    /* variable FEV end*/
    /* Mars */
    int count_3 = 0, count_3_cumul = 0, count_vo_3 = 0, count_vo_cumul_3 = 0, count_vno_3 = 0, count_vno_cumul_3 = 0;
    double brut_3 = 0, brut_3_cumul = 0;
    double remise_3 = 0, remise_3_cumul = 0;
    double net_3 = 0, net_3_cumul = 0;
    double pan_moy_vo_3 = 0, pan_moy_vo_cumul_3 = 0;
    double pan_moy_vno_3 = 0, pan_moy_vno_cumul_3 = 0;
    double ventes_vo_3 = 0, ventes_vo_cumul_3 = 0, ventes_vno_3 = 0, ventes_vno_cumul_3 = 0;
    double vente_percent_vno_3 = 0, vente_percent_vno_cumul_3 = 0;
    double vente_percent_vo_3 = 0, vente_percent_vo_cumul_3 = 0;
    /* Mars */
    /*avril*/
    int count_4 = 0, count_4_cumul = 0, count_vo_4 = 0, count_vo_cumul_4 = 0, count_vno_4 = 0, count_vno_cumul_4 = 0;
    double brut_4 = 0, brut_4_cumul = 0;
    double remise_4 = 0, remise_4_cumul = 0;
    double net_4 = 0, net_4_cumul = 0;
    double pan_moy_vo_4 = 0, pan_moy_vo_cumul_4 = 0;
    double pan_moy_vno_4 = 0, pan_moy_vno_cumul_4 = 0;
    double ventes_vo_4 = 0, ventes_vo_cumul_4 = 0, ventes_vno_4 = 0, ventes_vno_cumul_4 = 0;
    double vente_percent_vno_4 = 0, vente_percent_vno_cumul_4 = 0;
    double vente_percent_vo_4 = 0, vente_percent_vo_cumul_4 = 0;
    /*avril*/
    /* mai */
    int count_5 = 0, count_5_cumul = 0, count_vo_5 = 0, count_vo_cumul_5 = 0, count_vno_5 = 0, count_vno_cumul_5 = 0;
    double brut_5 = 0, brut_5_cumul = 0;
    double remise_5 = 0, remise_5_cumul = 0;
    double net_5 = 0, net_5_cumul = 0;
    double pan_moy_vo_5 = 0, pan_moy_vo_cumul_5 = 0;
    double pan_moy_vno_5 = 0, pan_moy_vno_cumul_5 = 0;
    double ventes_vo_5 = 0, ventes_vo_cumul_5 = 0, ventes_vno_5 = 0, ventes_vno_cumul_5 = 0;
    double vente_percent_vno_5 = 0, vente_percent_vno_cumul_5 = 0;
    double vente_percent_vo_5 = 0, vente_percent_vo_cumul_5 = 0;
    /*mai*/

    /*juin*/
    int count_6 = 0, count_6_cumul = 0, count_vo_6 = 0, count_vo_cumul_6 = 0, count_vno_6 = 0, count_vno_cumul_6 = 0;
    double brut_6 = 0, brut_6_cumul = 0;
    double remise_6 = 0, remise_6_cumul = 0;
    double net_6 = 0, net_6_cumul = 0;
    double pan_moy_vo_6 = 0, pan_moy_vo_cumul_6 = 0;
    double pan_moy_vno_6 = 0, pan_moy_vno_cumul_6 = 0;
    double ventes_vo_6 = 0, ventes_vo_cumul_6 = 0, ventes_vno_6 = 0, ventes_vno_cumul_6 = 0;
    double vente_percent_vno_6 = 0, vente_percent_vno_cumul_6 = 0;
    double vente_percent_vo_6 = 0, vente_percent_vo_cumul_6 = 0;
    /*juin*/

    /*juillet*/
    int count_7 = 0, count_7_cumul = 0, count_vo_7 = 0, count_vo_cumul_7 = 0, count_vno_7 = 0, count_vno_cumul_7 = 0;
    double brut_7 = 0, brut_7_cumul = 0;
    double remise_7 = 0, remise_7_cumul = 0;
    double net_7 = 0, net_7_cumul = 0;
    double pan_moy_vo_7 = 0, pan_moy_vo_cumul_7 = 0;
    double pan_moy_vno_7 = 0, pan_moy_vno_cumul_7 = 0;
    double ventes_vo_7 = 0, ventes_vo_cumul_7 = 0, ventes_vno_7 = 0, ventes_vno_cumul_7 = 0;
    double vente_percent_vno_7 = 0, vente_percent_vno_cumul_7 = 0;
    double vente_percent_vo_7 = 0, vente_percent_vo_cumul_7 = 0;
    /*juillet*/

    /* aout*/
    int count_8 = 0, count_8_cumul = 0, count_vo_8 = 0, count_vo_cumul_8 = 0, count_vno_8 = 0, count_vno_cumul_8 = 0;
    double brut_8 = 0, brut_8_cumul = 0;
    double remise_8 = 0, remise_8_cumul = 0;
    double net_8 = 0, net_8_cumul = 0;
    double pan_moy_vo_8 = 0, pan_moy_vo_cumul_8 = 0;
    double pan_moy_vno_8 = 0, pan_moy_vno_cumul_8 = 0;
    double ventes_vo_8 = 0, ventes_vo_cumul_8 = 0, ventes_vno_8 = 0, ventes_vno_cumul_8 = 0;
    double vente_percent_vno_8 = 0, vente_percent_vno_cumul_8 = 0;
    double vente_percent_vo_8 = 0, vente_percent_vo_cumul_8 = 0;
    /*aout*/

    /*sept*/
    int count_9 = 0, count_9_cumul = 0, count_vo_9 = 0, count_vo_cumul_9 = 0, count_vno_9 = 0, count_vno_cumul_9 = 0;
    double brut_9 = 0, brut_9_cumul = 0;
    double remise_9 = 0, remise_9_cumul = 0;
    double net_9 = 0, net_9_cumul = 0;
    double pan_moy_vo_9 = 0, pan_moy_vo_cumul_9 = 0;
    double pan_moy_vno_9 = 0, pan_moy_vno_cumul_9 = 0;
    double ventes_vo_9 = 0, ventes_vo_cumul_9 = 0, ventes_vno_9 = 0, ventes_vno_cumul_9 = 0;
    double vente_percent_vno_9 = 0, vente_percent_vno_cumul_9 = 0;
    double vente_percent_vo_9 = 0, vente_percent_vo_cumul_9 = 0;
    /*sept*/

    /*oct*/
    int count_10 = 0, count_10_cumul = 0, count_vo_10 = 0, count_vo_cumul_10 = 0, count_vno_10 = 0, count_vno_cumul_10 = 0;
    double brut_10 = 0, brut_10_cumul = 0;
    double remise_10 = 0, remise_10_cumul = 0;
    double net_10 = 0, net_10_cumul = 0;
    double pan_moy_vo_10 = 0, pan_moy_vo_cumul_10 = 0;
    double pan_moy_vno_10 = 0, pan_moy_vno_cumul_10 = 0;
    double ventes_vo_10 = 0, ventes_vo_cumul_10 = 0, ventes_vno_10 = 0, ventes_vno_cumul_10 = 0;
    double vente_percent_vno_10 = 0, vente_percent_vno_cumul_10 = 0;
    double vente_percent_vo_10 = 0, vente_percent_vo_cumul_10 = 0;
    /*oct*/
    /*nov*/
    int count_11 = 0, count_11_cumul = 0, count_vo_11 = 0, count_vo_cumul_11 = 0, count_vno_11 = 0, count_vno_cumul_11 = 0;
    double brut_11 = 0, brut_11_cumul = 0;
    double remise_11 = 0, remise_11_cumul = 0;
    double net_11 = 0, net_11_cumul = 0;
    double pan_moy_vo_11 = 0, pan_moy_vo_cumul_11 = 0;
    double pan_moy_vno_11 = 0, pan_moy_vno_cumul_11 = 0;
    double ventes_vo_11 = 0, ventes_vo_cumul_11 = 0, ventes_vno_11 = 0, ventes_vno_cumul_11 = 0;
    double vente_percent_vno_11 = 0, vente_percent_vno_cumul_11 = 0;
    double vente_percent_vo_11 = 0, vente_percent_vo_cumul_11 = 0;
    /*nov*/

    /*decem*/
    int count_12 = 0, count_12_cumul = 0, count_vo_12 = 0, count_vo_cumul_12 = 0, count_vno_12 = 0, count_vno_cumul_12 = 0;
    double brut_12 = 0, brut_12_cumul = 0;
    double remise_12 = 0, remise_12_cumul = 0;
    double net_12 = 0, net_12_cumul = 0;
    double pan_moy_vo_12 = 0, pan_moy_vo_cumul_12 = 0;
    double pan_moy_vno_12 = 0, pan_moy_vno_cumul_12 = 0;
    double ventes_vo_12 = 0, ventes_vo_cumul_12 = 0, ventes_vno_12 = 0, ventes_vno_cumul_12 = 0;
    double vente_percent_vno_12 = 0, vente_percent_vno_cumul_12 = 0;
    double vente_percent_vo_12 = 0, vente_percent_vo_cumul_12 = 0;
    /*decm*/
    for (TPreenregistrement OPreenregistrement : list) {
        /* data janv begin*/
        if (janv.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_1++;
            brut_1 += OPreenregistrement.getIntPRICE();
            remise_1 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_1++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_1++;
            }
        }
        if (Integer.valueOf(janv) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_1_cumul++;
            brut_1_cumul += OPreenregistrement.getIntPRICE();
            remise_1_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_1++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_1++;
            }

        }

        /*data decembre end*/
        if (fev.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_2++;
            brut_2 += OPreenregistrement.getIntPRICE();
            remise_2 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_2 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_2++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_2 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_2++;
            }
        }
        if (Integer.valueOf(fev) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_2_cumul++;
            brut_2_cumul += OPreenregistrement.getIntPRICE();
            remise_2_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_2 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_2++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_2 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_2++;
            }

        }
        if (mars.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_3++;
            brut_3 += OPreenregistrement.getIntPRICE();
            remise_3 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_3 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_3++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_3 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_3++;
            }
        }
        if (Integer.valueOf(mars) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_3_cumul++;
            brut_3_cumul += OPreenregistrement.getIntPRICE();
            remise_3_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_3 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_3++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_3 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_3++;
            }

        }
        if (avri.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_4++;
            brut_4 += OPreenregistrement.getIntPRICE();
            remise_4 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_4 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_4++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_4 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_4++;
            }
        }
        if (Integer.valueOf(avri) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_4_cumul++;
            brut_4_cumul += OPreenregistrement.getIntPRICE();
            remise_4_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_4 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_4++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_4 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_4++;
            }

        }
        if (mai.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_5++;
            brut_5 += OPreenregistrement.getIntPRICE();
            remise_5 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_5 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_5++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_5 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_5++;
            }
        }
        if (Integer.valueOf(mai) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_5_cumul++;
            brut_5_cumul += OPreenregistrement.getIntPRICE();
            remise_5_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_5 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_5++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_5 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_5++;
            }

        }
        if (dec.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_12++;
            brut_12 += OPreenregistrement.getIntPRICE();
            remise_12 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_12 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_12++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_12 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_12++;
            }
        }
        if (Integer.valueOf(dec) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_12_cumul++;
            brut_12_cumul += OPreenregistrement.getIntPRICE();
            remise_12_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_12 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_12++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_12 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_12++;
            }

        }
        if (nov.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_11++;
            brut_11 += OPreenregistrement.getIntPRICE();
            remise_11 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_11 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_11++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_11 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_11++;
            }
        }
        if (Integer.valueOf(nov) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_11_cumul++;
            brut_11_cumul += OPreenregistrement.getIntPRICE();
            remise_11_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_11 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_11++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_11 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_11++;
            }

        }
        if (oct.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_10++;
            brut_10 += OPreenregistrement.getIntPRICE();
            remise_10 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_10 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_10++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_10 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_10++;
            }
        }
        if (Integer.valueOf(oct) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_10_cumul++;
            brut_10_cumul += OPreenregistrement.getIntPRICE();
            remise_10_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_10 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_10++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_10 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_10++;
            }

        }
        if (juin.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_6++;
            brut_6 += OPreenregistrement.getIntPRICE();
            remise_6 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_6 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_6++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_6 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_6++;
            }
        }
        if (Integer.valueOf(juin) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_6_cumul++;
            brut_6_cumul += OPreenregistrement.getIntPRICE();
            remise_6_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_6 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_6++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_6 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_6++;
            }

        }
        if (juil.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_7++;
            brut_7 += OPreenregistrement.getIntPRICE();
            remise_7 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_7 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_7++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_7 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_7++;
            }
        }
        if (Integer.valueOf(juil) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_7_cumul++;
            brut_7_cumul += OPreenregistrement.getIntPRICE();
            remise_7_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_7 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_7++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_7 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_7++;
            }

        }
        if (aout.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_8++;
            brut_8 += OPreenregistrement.getIntPRICE();
            remise_8 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_8 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_8++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_8 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_8++;
            }
        }
        if (Integer.valueOf(aout) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_8_cumul++;
            brut_8_cumul += OPreenregistrement.getIntPRICE();
            remise_8_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_8 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_8++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_8 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_8++;
            }

        }
        if (sept.equals(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_9++;
            brut_9 += OPreenregistrement.getIntPRICE();
            remise_9 += OPreenregistrement.getIntPRICEREMISE();

            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_9 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_9++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_9 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_9++;
            }
        }
        if (Integer.valueOf(sept) >= Integer.valueOf(date.FORMATTERMOUNTH.format(OPreenregistrement.getDtCREATED()))) {
            count_9_cumul++;
            brut_9_cumul += OPreenregistrement.getIntPRICE();
            remise_9_cumul += OPreenregistrement.getIntPRICEREMISE();
            if (Parameter.KEY_VENTE_NON_ORDONNANCEE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vno_cumul_9 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vno_cumul_9++;
            } else if (Parameter.KEY_VENTE_ORDONNANCE.equals(OPreenregistrement.getStrTYPEVENTE())) {
                ventes_vo_cumul_9 += (OPreenregistrement.getIntPRICE() - OPreenregistrement.getIntPRICEREMISE());
                count_vo_cumul_9++;
            }

        }
    }

    /* janv debut */
    net_1 = brut_1 - remise_1;
    net_1_cumul = brut_1_cumul - remise_1_cumul;
    if (count_vno_1 > 0) {
        pan_moy_vno_1 = ventes_vno / count_vno_1;
    }
    if (count_vo_1 > 0) {
        pan_moy_vo_1 = ventes_vo / count_vo_1;
    }
    if (count_vno_cumul_1 > 0) {
        pan_moy_vno_cumul_1 = ventes_vno_cumul / count_vno_cumul_1;
    }
    if (count_vo_cumul_1 > 0) {
        pan_moy_vo_cumul_1 = ventes_vo_cumul / count_vo_cumul_1;
    }

    if (ventes_vo_cumul > 0 || ventes_vno_cumul > 0) {
        vente_percent_vo_cumul = (ventes_vo_cumul * 100) / (ventes_vo_cumul + ventes_vno_cumul);
        vente_percent_vno_cumul = (ventes_vno_cumul * 100) / (ventes_vo_cumul + ventes_vno_cumul);
    }

    if (ventes_vo > 0 || ventes_vno > 0) {
        vente_percent_vo = (ventes_vo * 100) / (ventes_vo + ventes_vno);
        vente_percent_vno = (ventes_vno * 100) / (ventes_vno + ventes_vo);
    }

    net_2 = brut_2 - remise_2;
    net_2_cumul = brut_2_cumul - remise_2_cumul;
    if (count_vno_2 > 0) {
        pan_moy_vno_2 = ventes_vno_2 / count_vno_2;
    }
    if (count_vo_2 > 0) {
        pan_moy_vo_2 = ventes_vo_2 / count_vo_2;
    }
    if (count_vno_cumul_2 > 0) {
        pan_moy_vno_cumul_2 = ventes_vno_cumul_2 / count_vno_cumul_2;
    }
    if (count_vo_cumul_2 > 0) {
        pan_moy_vo_cumul_2 = ventes_vo_cumul_2 / count_vo_cumul_2;
    }

    if (ventes_vo_cumul_2 > 0 || ventes_vno_cumul_2 > 0) {
        vente_percent_vo_cumul_2 = (ventes_vo_cumul_2 * 100) / (ventes_vo_cumul_2 + ventes_vno_cumul_2);
        vente_percent_vno_cumul_2 = (ventes_vno_cumul_2 * 100) / (ventes_vo_cumul_2 + ventes_vno_cumul_2);
    }

    if (ventes_vo_2 > 0 || ventes_vno_2 > 0) {
        vente_percent_vo_2 = (ventes_vo_2 * 100) / (ventes_vo_2 + ventes_vno_2);
        vente_percent_vno_2 = (ventes_vno_2 * 100) / (ventes_vno_2 + ventes_vo_2);
    }
    net_3 = brut_3 - remise_3;
    net_3_cumul = brut_3_cumul - remise_3_cumul;
    if (count_vno_3 > 0) {
        pan_moy_vno_3 = ventes_vno_3 / count_vno_3;
    }
    if (count_vo_3 > 0) {
        pan_moy_vo_3 = ventes_vo_3 / count_vo_3;
    }
    if (count_vno_cumul_3 > 0) {
        pan_moy_vno_cumul_3 = ventes_vno_cumul_3 / count_vno_cumul_3;
    }
    if (count_vo_cumul_3 > 0) {
        pan_moy_vo_cumul_3 = ventes_vo_cumul_3 / count_vo_cumul_3;
    }

    if (ventes_vo_cumul_3 > 0 || ventes_vno_cumul_3 > 0) {
        vente_percent_vo_cumul_3 = (ventes_vo_cumul_3 * 100) / (ventes_vo_cumul_3 + ventes_vno_cumul_3);
        vente_percent_vno_cumul_3 = (ventes_vno_cumul_3 * 100) / (ventes_vo_cumul_3 + ventes_vno_cumul_3);
    }

    if (ventes_vo_3 > 0 || ventes_vno_3 > 0) {
        vente_percent_vo_3 = (ventes_vo_3 * 100) / (ventes_vo_3 + ventes_vno_3);
        vente_percent_vno_3 = (ventes_vno_3 * 100) / (ventes_vno_3 + ventes_vo_3);
    }
    net_4 = brut_4 - remise_4;
    net_4_cumul = brut_4_cumul - remise_4_cumul;
    if (count_vno_4 > 0) {
        pan_moy_vno_4 = ventes_vno_4 / count_vno_4;
    }
    if (count_vo_4 > 0) {
        pan_moy_vo_4 = ventes_vo_4 / count_vo_4;
    }
    if (count_vno_cumul_4 > 0) {
        pan_moy_vno_cumul_4 = ventes_vno_cumul_4 / count_vno_cumul_4;
    }
    if (count_vo_cumul_4 > 0) {
        pan_moy_vo_cumul_4 = ventes_vo_cumul_4 / count_vo_cumul_4;
    }

    if (ventes_vo_cumul_4 > 0 || ventes_vno_cumul_4 > 0) {
        vente_percent_vo_cumul_4 = (ventes_vo_cumul_4 * 100) / (ventes_vo_cumul_4 + ventes_vno_cumul_4);
        vente_percent_vno_cumul_4 = (ventes_vno_cumul_4 * 100) / (ventes_vo_cumul_4 + ventes_vno_cumul_4);
    }

    if (ventes_vo_4 > 0 || ventes_vno_4 > 0) {
        vente_percent_vo_4 = (ventes_vo_4 * 100) / (ventes_vo_4 + ventes_vno_4);
        vente_percent_vno_4 = (ventes_vno_4 * 100) / (ventes_vno_4 + ventes_vo_4);
    }
    net_5 = brut_5 - remise_5;
    net_5_cumul = brut_5_cumul - remise_5_cumul;
    if (count_vno_5 > 0) {
        pan_moy_vno_5 = ventes_vno_5 / count_vno_5;
    }
    if (count_vo_5 > 0) {
        pan_moy_vo_5 = ventes_vo_5 / count_vo_5;
    }
    if (count_vno_cumul_5 > 0) {
        pan_moy_vno_cumul_5 = ventes_vno_cumul_5 / count_vno_cumul_5;
    }
    if (count_vo_cumul_5 > 0) {
        pan_moy_vo_cumul_5 = ventes_vo_cumul_5 / count_vo_cumul_5;
    }

    if (ventes_vo_cumul_5 > 0 || ventes_vno_cumul_5 > 0) {
        vente_percent_vo_cumul_5 = (ventes_vo_cumul_5 * 100) / (ventes_vo_cumul_5 + ventes_vno_cumul_5);
        vente_percent_vno_cumul_5 = (ventes_vno_cumul_5 * 100) / (ventes_vo_cumul_5 + ventes_vno_cumul_5);
    }

    if (ventes_vo_5 > 0 || ventes_vno_5 > 0) {
        vente_percent_vo_5 = (ventes_vo_5 * 100) / (ventes_vo_5 + ventes_vno_5);
        vente_percent_vno_5 = (ventes_vno_5 * 100) / (ventes_vno_5 + ventes_vo_5);
    }
    net_6 = brut_6 - remise_6;
    net_6_cumul = brut_6_cumul - remise_6_cumul;
    if (count_vno_6 > 0) {
        pan_moy_vno_6 = ventes_vno_6 / count_vno_6;
    }
    if (count_vo_6 > 0) {
        pan_moy_vo_6 = ventes_vo_6 / count_vo_6;
    }
    if (count_vno_cumul_6 > 0) {
        pan_moy_vno_cumul_6 = ventes_vno_cumul_6 / count_vno_cumul_6;
    }
    if (count_vo_cumul_6 > 0) {
        pan_moy_vo_cumul_6 = ventes_vo_cumul_6 / count_vo_cumul_6;
    }

    if (ventes_vo_cumul_6 > 0 || ventes_vno_cumul_6 > 0) {
        vente_percent_vo_cumul_6 = (ventes_vo_cumul_6 * 100) / (ventes_vo_cumul_6 + ventes_vno_cumul_6);
        vente_percent_vno_cumul_6 = (ventes_vno_cumul_6 * 100) / (ventes_vo_cumul_6 + ventes_vno_cumul_6);
    }

    if (ventes_vo_6 > 0 || ventes_vno_6 > 0) {
        vente_percent_vo_6 = (ventes_vo_6 * 100) / (ventes_vo_6 + ventes_vno_6);
        vente_percent_vno_6 = (ventes_vno_6 * 100) / (ventes_vno_6 + ventes_vo_6);
    }
    net_7 = brut_7 - remise_7;
    net_7_cumul = brut_7_cumul - remise_7_cumul;
    if (count_vno_7 > 0) {
        pan_moy_vno_7 = ventes_vno_7 / count_vno_7;
    }
    if (count_vo_7 > 0) {
        pan_moy_vo_7 = ventes_vo_7 / count_vo_7;
    }
    if (count_vno_cumul_7 > 0) {
        pan_moy_vno_cumul_7 = ventes_vno_cumul_7 / count_vno_cumul_7;
    }
    if (count_vo_cumul_7 > 0) {
        pan_moy_vo_cumul_7 = ventes_vo_cumul_7 / count_vo_cumul_7;
    }

    if (ventes_vo_cumul_7 > 0 || ventes_vno_cumul_7 > 0) {
        vente_percent_vo_cumul_7 = (ventes_vo_cumul_7 * 100) / (ventes_vo_cumul_7 + ventes_vno_cumul_7);
        vente_percent_vno_cumul_7 = (ventes_vno_cumul_7 * 100) / (ventes_vo_cumul_7 + ventes_vno_cumul_7);
    }

    if (ventes_vo_7 > 0 || ventes_vno_7 > 0) {
        vente_percent_vo_7 = (ventes_vo_7 * 100) / (ventes_vo_7 + ventes_vno_7);
        vente_percent_vno_7 = (ventes_vno_7 * 100) / (ventes_vno_7 + ventes_vo_7);
    }
    net_8 = brut_8 - remise_8;
    net_8_cumul = brut_8_cumul - remise_8_cumul;
    if (count_vno_8 > 0) {
        pan_moy_vno_8 = ventes_vno_8 / count_vno_8;
    }
    if (count_vo_8 > 0) {
        pan_moy_vo_8 = ventes_vo_8 / count_vo_8;
    }
    if (count_vno_cumul_8 > 0) {
        pan_moy_vno_cumul_8 = ventes_vno_cumul_8 / count_vno_cumul_8;
    }
    if (count_vo_cumul_8 > 0) {
        pan_moy_vo_cumul_8 = ventes_vo_cumul_8 / count_vo_cumul_8;
    }

    if (ventes_vo_cumul_8 > 0 || ventes_vno_cumul_8 > 0) {
        vente_percent_vo_cumul_8 = (ventes_vo_cumul_8 * 100) / (ventes_vo_cumul_8 + ventes_vno_cumul_8);
        vente_percent_vno_cumul_8 = (ventes_vno_cumul_8 * 100) / (ventes_vo_cumul_8 + ventes_vno_cumul_8);
    }

    if (ventes_vo_8 > 0 || ventes_vno_8 > 0) {
        vente_percent_vo_8 = (ventes_vo_8 * 100) / (ventes_vo_8 + ventes_vno_8);
        vente_percent_vno_8 = (ventes_vno_8 * 100) / (ventes_vno_8 + ventes_vo_8);
    }
    net_9 = brut_9 - remise_9;
    net_9_cumul = brut_9_cumul - remise_9_cumul;
    if (count_vno_9 > 0) {
        pan_moy_vno_9 = ventes_vno_9 / count_vno_9;
    }
    if (count_vo_9 > 0) {
        pan_moy_vo_9 = ventes_vo_9 / count_vo_9;
    }
    if (count_vno_cumul_9 > 0) {
        pan_moy_vno_cumul_9 = ventes_vno_cumul_9 / count_vno_cumul_9;
    }
    if (count_vo_cumul_9 > 0) {
        pan_moy_vo_cumul_9 = ventes_vo_cumul_9 / count_vo_cumul_9;
    }

    if (ventes_vo_cumul_9 > 0 || ventes_vno_cumul_9 > 0) {
        vente_percent_vo_cumul_9 = (ventes_vo_cumul_9 * 100) / (ventes_vo_cumul_9 + ventes_vno_cumul_9);
        vente_percent_vno_cumul_9 = (ventes_vno_cumul_9 * 100) / (ventes_vo_cumul_9 + ventes_vno_cumul_9);
    }

    if (ventes_vo_9 > 0 || ventes_vno_9 > 0) {
        vente_percent_vo_9 = (ventes_vo_9 * 100) / (ventes_vo_9 + ventes_vno_9);
        vente_percent_vno_9 = (ventes_vno_9 * 100) / (ventes_vno_9 + ventes_vo_9);
    }
    net_10 = brut_10 - remise_10;
    net_10_cumul = brut_10_cumul - remise_10_cumul;
    if (count_vno_10 > 0) {
        pan_moy_vno_10 = ventes_vno_10 / count_vno_10;
    }
    if (count_vo_10 > 0) {
        pan_moy_vo_10 = ventes_vo_10 / count_vo_10;
    }
    if (count_vno_cumul_10 > 0) {
        pan_moy_vno_cumul_10 = ventes_vno_cumul_10 / count_vno_cumul_10;
    }
    if (count_vo_cumul_10 > 0) {
        pan_moy_vo_cumul_10 = ventes_vo_cumul_10 / count_vo_cumul_10;
    }

    if (ventes_vo_cumul_10 > 0 || ventes_vno_cumul_10 > 0) {
        vente_percent_vo_cumul_10 = (ventes_vo_cumul_10 * 100) / (ventes_vo_cumul_10 + ventes_vno_cumul_10);
        vente_percent_vno_cumul_10 = (ventes_vno_cumul_10 * 100) / (ventes_vo_cumul_10 + ventes_vno_cumul_10);
    }

    if (ventes_vo_10 > 0 || ventes_vno_10 > 0) {
        vente_percent_vo_10 = (ventes_vo_10 * 100) / (ventes_vo_10 + ventes_vno_10);
        vente_percent_vno_10 = (ventes_vno_10 * 100) / (ventes_vno_10 + ventes_vo_10);
    }
    net_11 = brut_11 - remise_11;
    net_11_cumul = brut_11_cumul - remise_11_cumul;
    if (count_vno_11 > 0) {
        pan_moy_vno_11 = ventes_vno_11 / count_vno_11;
    }
    if (count_vo_11 > 0) {
        pan_moy_vo_11 = ventes_vo_11 / count_vo_11;
    }
    if (count_vno_cumul_11 > 0) {
        pan_moy_vno_cumul_11 = ventes_vno_cumul_11 / count_vno_cumul_11;
    }
    if (count_vo_cumul_11 > 0) {
        pan_moy_vo_cumul_11 = ventes_vo_cumul_11 / count_vo_cumul_11;
    }

    if (ventes_vo_cumul_11 > 0 || ventes_vno_cumul_11 > 0) {
        vente_percent_vo_cumul_11 = (ventes_vo_cumul_11 * 100) / (ventes_vo_cumul_11 + ventes_vno_cumul_11);
        vente_percent_vno_cumul_11 = (ventes_vno_cumul_11 * 100) / (ventes_vo_cumul_11 + ventes_vno_cumul_11);
    }

    if (ventes_vo_11 > 0 || ventes_vno_11 > 0) {
        vente_percent_vo_11 = (ventes_vo_11 * 100) / (ventes_vo_11 + ventes_vno_11);
        vente_percent_vno_11 = (ventes_vno_11 * 100) / (ventes_vno_11 + ventes_vo_11);
    }
    net_12 = brut_12 - remise_12;
    net_12_cumul = brut_12_cumul - remise_12_cumul;
    if (count_vno_12 > 0) {
        pan_moy_vno_12 = ventes_vno_12 / count_vno_12;
    }
    if (count_vo_12 > 0) {
        pan_moy_vo_12 = ventes_vo_12 / count_vo_12;
    }
    if (count_vno_cumul_12 > 0) {
        pan_moy_vno_cumul_12 = ventes_vno_cumul_12 / count_vno_cumul_12;
    }
    if (count_vo_cumul_12 > 0) {
        pan_moy_vo_cumul_12 = ventes_vo_cumul_12 / count_vo_cumul_12;
    }

    if (ventes_vo_cumul_12 > 0 || ventes_vno_cumul_12 > 0) {
        vente_percent_vo_cumul_12 = (ventes_vo_cumul_12 * 100) / (ventes_vo_cumul_12 + ventes_vno_cumul_12);
        vente_percent_vno_cumul_12 = (ventes_vno_cumul_12 * 100) / (ventes_vo_cumul_12 + ventes_vno_cumul_12);
    }

    if (ventes_vo_12 > 0 || ventes_vno_12 > 0) {
        vente_percent_vo_12 = (ventes_vo_12 * 100) / (ventes_vo_12 + ventes_vno_12);
        vente_percent_vno_12 = (ventes_vno_12 * 100) / (ventes_vno_12 + ventes_vo_12);
    }
    

   
    
    if (count_1 > 0) {
        P_MONTH_1_NCLIENT = conversion.AmountFormat((int) count_1, ' ');
        P_MONTH_1_NCLIENT_CUMUL = conversion.AmountFormat((int) count_1_cumul, ' ');
        P_MONTH_1_BRUTTTC = conversion.AmountFormat((int) brut_1, ' ');
        P_MONTH_1_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_1_cumul, ' ');
        P_MONTH_1_NETTTC = conversion.AmountFormat((int) net_1, ' ');
        P_MONTH_1_NETTTC_CUMUL = conversion.AmountFormat((int) net_1_cumul, ' ');
        P_MONTH_1_REMISE = conversion.AmountFormat((int) remise_1, ' ');
        P_MONTH_1_REMISE_CUMUL = conversion.AmountFormat((int) remise_1_cumul, ' ');
        P_MONTH_1_VENTESVO = conversion.AmountFormat((int) ventes_vo, ' ');
        P_MONTH_1_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul, ' ');
        P_MONTH_1_VENTESVNO = conversion.AmountFormat((int) ventes_vno, ' ');
        P_MONTH_1_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul, ' ');
        P_MONTH_1_PANORD = Math.round(pan_moy_vo_1) + "";
        P_MONTH_1_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_1) + "";
        P_MONTH_1_VNOPERCENT_MONTH = vente_percent_vno + "";
        P_MONTH_1_VNOPERCENT_CUMUL = vente_percent_vno_cumul + "";
        P_MONTH_1_VOPERCENT_CUMUL = vente_percent_vo_cumul + "";
        P_MONTH_1_VOPERCENT_MONTH = vente_percent_vo_cumul + "";
        P_MONTH_1_PANNO = Math.round(pan_moy_vno_1) + "";
        P_MONTH_1_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_1) + "";
    }
    if (count_2 > 0) {
        P_MONTH_2_NCLIENT = conversion.AmountFormat((int) count_2, ' ');
        P_MONTH_2_NCLIENT_CUMUL = conversion.AmountFormat((int) count_2_cumul, ' ');
        P_MONTH_2_BRUTTTC = conversion.AmountFormat((int) brut_2, ' ');
        P_MONTH_2_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_2_cumul, ' ');
        P_MONTH_2_NETTTC = conversion.AmountFormat((int) net_2, ' ');
        P_MONTH_2_NETTTC_CUMUL = conversion.AmountFormat((int) net_2_cumul, ' ');
        P_MONTH_2_REMISE = conversion.AmountFormat((int) remise_2, ' ');
        P_MONTH_2_REMISE_CUMUL = conversion.AmountFormat((int) remise_2_cumul, ' ');
        P_MONTH_2_VENTESVO = conversion.AmountFormat((int) ventes_vo_2, ' ');
        P_MONTH_2_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_2, ' ');
        P_MONTH_2_VENTESVNO = conversion.AmountFormat((int) ventes_vno_2, ' ');
        P_MONTH_2_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_2, ' ');
        P_MONTH_2_PANORD = Math.round(pan_moy_vo_2) + "";
        P_MONTH_2_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_2) + "";
        P_MONTH_2_VNOPERCENT_MONTH = vente_percent_vno_2 + "";
        P_MONTH_2_VNOPERCENT_CUMUL = vente_percent_vno_cumul_2 + "";
        P_MONTH_2_VOPERCENT_CUMUL = vente_percent_vo_cumul_2 + "";
        P_MONTH_2_VOPERCENT_MONTH = vente_percent_vo_cumul_2 + "";
        P_MONTH_2_PANNO = Math.round(pan_moy_vno_2) + "";
        P_MONTH_2_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_2) + "";
    }
    if (count_3 > 0) {
        P_MONTH_3_NCLIENT = conversion.AmountFormat((int) count_3, ' ');
        P_MONTH_3_NCLIENT_CUMUL = conversion.AmountFormat((int) count_3_cumul, ' ');
        P_MONTH_3_BRUTTTC = conversion.AmountFormat((int) brut_3, ' ');
        P_MONTH_3_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_3_cumul, ' ');
        P_MONTH_3_NETTTC = conversion.AmountFormat((int) net_3, ' ');
        P_MONTH_3_NETTTC_CUMUL = conversion.AmountFormat((int) net_3_cumul, ' ');
        P_MONTH_3_REMISE = conversion.AmountFormat((int) remise_3, ' ');
        P_MONTH_3_REMISE_CUMUL = conversion.AmountFormat((int) remise_3_cumul, ' ');
        P_MONTH_3_VENTESVO = conversion.AmountFormat((int) ventes_vo_3, ' ');
        P_MONTH_3_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_3, ' ');
        P_MONTH_3_VENTESVNO = conversion.AmountFormat((int) ventes_vno_3, ' ');
        P_MONTH_3_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_3, ' ');
        P_MONTH_3_PANORD = Math.round(pan_moy_vo_3) + "";
        P_MONTH_3_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_3) + "";
        P_MONTH_3_VNOPERCENT_MONTH = vente_percent_vno_3 + "";
        P_MONTH_3_VNOPERCENT_CUMUL = vente_percent_vno_cumul_3 + "";
        P_MONTH_3_VOPERCENT_CUMUL = vente_percent_vo_cumul_3 + "";
        P_MONTH_3_VOPERCENT_MONTH = vente_percent_vo_cumul_3 + "";
        P_MONTH_3_PANNO = Math.round(pan_moy_vno_3) + "";
        P_MONTH_3_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_3) + "";
    }
    if (count_4 > 0) {
        P_MONTH_4_NCLIENT = conversion.AmountFormat((int) count_4, ' ');
        P_MONTH_4_NCLIENT_CUMUL = conversion.AmountFormat((int) count_4_cumul, ' ');
        P_MONTH_4_BRUTTTC = conversion.AmountFormat((int) brut_4, ' ');
        P_MONTH_4_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_4_cumul, ' ');
        P_MONTH_4_NETTTC = conversion.AmountFormat((int) net_4, ' ');
        P_MONTH_4_NETTTC_CUMUL = conversion.AmountFormat((int) net_4_cumul, ' ');
        P_MONTH_4_REMISE = conversion.AmountFormat((int) remise_4, ' ');
        P_MONTH_4_REMISE_CUMUL = conversion.AmountFormat((int) remise_4_cumul, ' ');
        P_MONTH_4_VENTESVO = conversion.AmountFormat((int) ventes_vo_4, ' ');
        P_MONTH_4_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_4, ' ');
        P_MONTH_4_VENTESVNO = conversion.AmountFormat((int) ventes_vno_4, ' ');
        P_MONTH_4_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_4, ' ');
        P_MONTH_4_PANORD = Math.round(pan_moy_vo_4) + "";
        P_MONTH_4_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_4) + "";
        P_MONTH_4_VNOPERCENT_MONTH = vente_percent_vno_4 + "";
        P_MONTH_4_VNOPERCENT_CUMUL = vente_percent_vno_cumul_4 + "";
        P_MONTH_4_VOPERCENT_CUMUL = vente_percent_vo_cumul_4 + "";
        P_MONTH_4_VOPERCENT_MONTH = vente_percent_vo_cumul_4 + "";
        P_MONTH_4_PANNO = Math.round(pan_moy_vno_4) + "";
        P_MONTH_4_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_4) + "";
    }
    if (count_5 > 0) {
        P_MONTH_5_NCLIENT = conversion.AmountFormat((int) count_5, ' ');
        P_MONTH_5_NCLIENT_CUMUL = conversion.AmountFormat((int) count_5_cumul, ' ');
        P_MONTH_5_BRUTTTC = conversion.AmountFormat((int) brut_5, ' ');
        P_MONTH_5_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_5_cumul, ' ');
        P_MONTH_5_NETTTC = conversion.AmountFormat((int) net_5, ' ');
        P_MONTH_5_NETTTC_CUMUL = conversion.AmountFormat((int) net_5_cumul, ' ');
        P_MONTH_5_REMISE = conversion.AmountFormat((int) remise_5, ' ');
        P_MONTH_5_REMISE_CUMUL = conversion.AmountFormat((int) remise_5_cumul, ' ');
        P_MONTH_5_VENTESVO = conversion.AmountFormat((int) ventes_vo_5, ' ');
        P_MONTH_5_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_5, ' ');
        P_MONTH_5_VENTESVNO = conversion.AmountFormat((int) ventes_vno_5, ' ');
        P_MONTH_5_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_5, ' ');
        P_MONTH_5_PANORD = Math.round(pan_moy_vo_5) + "";
        P_MONTH_5_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_5) + "";
        P_MONTH_5_VNOPERCENT_MONTH = vente_percent_vno_5 + "";
        P_MONTH_5_VNOPERCENT_CUMUL = vente_percent_vno_cumul_5 + "";
        P_MONTH_5_VOPERCENT_CUMUL = vente_percent_vo_cumul_5 + "";
        P_MONTH_5_VOPERCENT_MONTH = vente_percent_vo_cumul_5 + "";
        P_MONTH_5_PANNO = Math.round(pan_moy_vno_5) + "";
        P_MONTH_5_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_5) + "";
    }
    if (count_6 > 0) {
        P_MONTH_6_NCLIENT = conversion.AmountFormat((int) count_6, ' ');
        P_MONTH_6_NCLIENT_CUMUL = conversion.AmountFormat((int) count_6_cumul, ' ');
        P_MONTH_6_BRUTTTC = conversion.AmountFormat((int) brut_6, ' ');
        P_MONTH_6_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_6_cumul, ' ');
        P_MONTH_6_NETTTC = conversion.AmountFormat((int) net_6, ' ');
        P_MONTH_6_NETTTC_CUMUL = conversion.AmountFormat((int) net_6_cumul, ' ');
        P_MONTH_6_REMISE = conversion.AmountFormat((int) remise_6, ' ');
        P_MONTH_6_REMISE_CUMUL = conversion.AmountFormat((int) remise_6_cumul, ' ');
        P_MONTH_6_VENTESVO = conversion.AmountFormat((int) ventes_vo_6, ' ');
        P_MONTH_6_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_6, ' ');
        P_MONTH_6_VENTESVNO = conversion.AmountFormat((int) ventes_vno_6, ' ');
        P_MONTH_6_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_6, ' ');
        P_MONTH_6_PANORD = Math.round(pan_moy_vo_6) + "";
        P_MONTH_6_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_6) + "";
        P_MONTH_6_VNOPERCENT_MONTH = vente_percent_vno_6 + "";
        P_MONTH_6_VNOPERCENT_CUMUL = vente_percent_vno_cumul_6 + "";
        P_MONTH_6_VOPERCENT_CUMUL = vente_percent_vo_cumul_6 + "";
        P_MONTH_6_VOPERCENT_MONTH = vente_percent_vo_cumul_6 + "";
        P_MONTH_6_PANNO = Math.round(pan_moy_vno_6) + "";
        P_MONTH_6_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_6) + "";
    }
    if (count_7 > 0) {
        P_MONTH_7_NCLIENT = conversion.AmountFormat((int) count_7, ' ');
        P_MONTH_7_NCLIENT_CUMUL = conversion.AmountFormat((int) count_7_cumul, ' ');
        P_MONTH_7_BRUTTTC = conversion.AmountFormat((int) brut_7, ' ');
        P_MONTH_7_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_7_cumul, ' ');
        P_MONTH_7_NETTTC = conversion.AmountFormat((int) net_7, ' ');
        P_MONTH_7_NETTTC_CUMUL = conversion.AmountFormat((int) net_7_cumul, ' ');
        P_MONTH_7_REMISE = conversion.AmountFormat((int) remise_7, ' ');
        P_MONTH_7_REMISE_CUMUL = conversion.AmountFormat((int) remise_7_cumul, ' ');
        P_MONTH_7_VENTESVO = conversion.AmountFormat((int) ventes_vo_7, ' ');
        P_MONTH_7_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_7, ' ');
        P_MONTH_7_VENTESVNO = conversion.AmountFormat((int) ventes_vno_7, ' ');
        P_MONTH_7_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_7, ' ');
        P_MONTH_7_PANORD = Math.round(pan_moy_vo_7) + "";
        P_MONTH_7_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_7) + "";
        P_MONTH_7_VNOPERCENT_MONTH = vente_percent_vno_7 + "";
        P_MONTH_7_VNOPERCENT_CUMUL = vente_percent_vno_cumul_7 + "";
        P_MONTH_7_VOPERCENT_CUMUL = vente_percent_vo_cumul_7 + "";
        P_MONTH_7_VOPERCENT_MONTH = vente_percent_vo_cumul_7 + "";
        P_MONTH_7_PANNO = Math.round(pan_moy_vno_7) + "";
        P_MONTH_7_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_7) + "";
    }
    if (count_8 > 0) {
        P_MONTH_8_NCLIENT = conversion.AmountFormat((int) count_8, ' ');
        P_MONTH_8_NCLIENT_CUMUL = conversion.AmountFormat((int) count_8_cumul, ' ');
        P_MONTH_8_BRUTTTC = conversion.AmountFormat((int) brut_8, ' ');
        P_MONTH_8_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_8_cumul, ' ');
        P_MONTH_8_NETTTC = conversion.AmountFormat((int) net_8, ' ');
        P_MONTH_8_NETTTC_CUMUL = conversion.AmountFormat((int) net_8_cumul, ' ');
        P_MONTH_8_REMISE = conversion.AmountFormat((int) remise_8, ' ');
        P_MONTH_8_REMISE_CUMUL = conversion.AmountFormat((int) remise_8_cumul, ' ');
        P_MONTH_8_VENTESVO = conversion.AmountFormat((int) ventes_vo_8, ' ');
        P_MONTH_8_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_8, ' ');
        P_MONTH_8_VENTESVNO = conversion.AmountFormat((int) ventes_vno_8, ' ');
        P_MONTH_8_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_8, ' ');
        P_MONTH_8_PANORD = Math.round(pan_moy_vo_8) + "";
        P_MONTH_8_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_8) + "";
        P_MONTH_8_VNOPERCENT_MONTH = vente_percent_vno_8 + "";
        P_MONTH_8_VNOPERCENT_CUMUL = vente_percent_vno_cumul_8 + "";
        P_MONTH_8_VOPERCENT_CUMUL = vente_percent_vo_cumul_8 + "";
        P_MONTH_8_VOPERCENT_MONTH = vente_percent_vo_cumul_8 + "";
        P_MONTH_8_PANNO = Math.round(pan_moy_vno_8) + "";
        P_MONTH_8_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_8) + "";
    }
    if (count_9 > 0) {
        P_MONTH_9_NCLIENT = conversion.AmountFormat((int) count_9, ' ');
        P_MONTH_9_NCLIENT_CUMUL = conversion.AmountFormat((int) count_9_cumul, ' ');
        P_MONTH_9_BRUTTTC = conversion.AmountFormat((int) brut_9, ' ');
        P_MONTH_9_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_9_cumul, ' ');
        P_MONTH_9_NETTTC = conversion.AmountFormat((int) net_9, ' ');
        P_MONTH_9_NETTTC_CUMUL = conversion.AmountFormat((int) net_9_cumul, ' ');
        P_MONTH_9_REMISE = conversion.AmountFormat((int) remise_9, ' ');
        P_MONTH_9_REMISE_CUMUL = conversion.AmountFormat((int) remise_9_cumul, ' ');
        P_MONTH_9_VENTESVO = conversion.AmountFormat((int) ventes_vo_9, ' ');
        P_MONTH_9_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_9, ' ');
        P_MONTH_9_VENTESVNO = conversion.AmountFormat((int) ventes_vno_9, ' ');
        P_MONTH_9_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_9, ' ');
        P_MONTH_9_PANORD = Math.round(pan_moy_vo_9) + "";
        P_MONTH_9_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_9) + "";
        P_MONTH_9_VNOPERCENT_MONTH = vente_percent_vno_9 + "";
        P_MONTH_9_VNOPERCENT_CUMUL = vente_percent_vno_cumul_9 + "";
        P_MONTH_9_VOPERCENT_CUMUL = vente_percent_vo_cumul_9 + "";
        P_MONTH_9_VOPERCENT_MONTH = vente_percent_vo_cumul_9 + "";
        P_MONTH_9_PANNO = Math.round(pan_moy_vno_9) + "";
        P_MONTH_9_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_9) + "";
    }
    if (count_10 > 0) {
        P_MONTH_10_NCLIENT = conversion.AmountFormat((int) count_10, ' ');
        P_MONTH_10_NCLIENT_CUMUL = conversion.AmountFormat((int) count_10_cumul, ' ');
        P_MONTH_10_BRUTTTC = conversion.AmountFormat((int) brut_10, ' ');
        P_MONTH_10_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_10_cumul, ' ');
        P_MONTH_10_NETTTC = conversion.AmountFormat((int) net_10, ' ');
        P_MONTH_10_NETTTC_CUMUL = conversion.AmountFormat((int) net_10_cumul, ' ');
        P_MONTH_10_REMISE = conversion.AmountFormat((int) remise_10, ' ');
        P_MONTH_10_REMISE_CUMUL = conversion.AmountFormat((int) remise_10_cumul, ' ');
        P_MONTH_10_VENTESVO = conversion.AmountFormat((int) ventes_vo_10, ' ');
        P_MONTH_10_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_10, ' ');
        P_MONTH_10_VENTESVNO = conversion.AmountFormat((int) ventes_vno_10, ' ');
        P_MONTH_10_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_10, ' ');
        P_MONTH_10_PANORD = Math.round(pan_moy_vo_10) + "";
        P_MONTH_10_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_10) + "";
        P_MONTH_10_VNOPERCENT_MONTH = vente_percent_vno_10 + "";
        P_MONTH_10_VNOPERCENT_CUMUL = vente_percent_vno_cumul_10 + "";
        P_MONTH_10_VOPERCENT_CUMUL = vente_percent_vo_cumul_10 + "";
        P_MONTH_10_VOPERCENT_MONTH = vente_percent_vo_cumul_10 + "";
        P_MONTH_10_PANNO = Math.round(pan_moy_vno_10) + "";
        P_MONTH_10_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_10) + "";
    }
    if (count_11 > 0) {
        P_MONTH_11_NCLIENT = conversion.AmountFormat((int) count_11, ' ');
        P_MONTH_11_NCLIENT_CUMUL = conversion.AmountFormat((int) count_11_cumul, ' ');
        P_MONTH_11_BRUTTTC = conversion.AmountFormat((int) brut_11, ' ');
        P_MONTH_11_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_11_cumul, ' ');
        P_MONTH_11_NETTTC = conversion.AmountFormat((int) net_11, ' ');
        P_MONTH_11_NETTTC_CUMUL = conversion.AmountFormat((int) net_11_cumul, ' ');
        P_MONTH_11_REMISE = conversion.AmountFormat((int) remise_11, ' ');
        P_MONTH_11_REMISE_CUMUL = conversion.AmountFormat((int) remise_11_cumul, ' ');
        P_MONTH_11_VENTESVO = conversion.AmountFormat((int) ventes_vo_11, ' ');
        P_MONTH_11_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_11, ' ');
        P_MONTH_11_VENTESVNO = conversion.AmountFormat((int) ventes_vno_11, ' ');
        P_MONTH_11_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_11, ' ');
        P_MONTH_11_PANORD = Math.round(pan_moy_vo_11) + "";
        P_MONTH_11_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_11) + "";
        P_MONTH_11_VNOPERCENT_MONTH = vente_percent_vno_11 + "";
        P_MONTH_11_VNOPERCENT_CUMUL = vente_percent_vno_cumul_11 + "";
        P_MONTH_11_VOPERCENT_CUMUL = vente_percent_vo_cumul_11 + "";
        P_MONTH_11_VOPERCENT_MONTH = vente_percent_vo_cumul_11 + "";
        P_MONTH_11_PANNO = Math.round(pan_moy_vno_11) + "";
        P_MONTH_11_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_11) + "";
    }
    if (count_12 > 0) {
        P_MONTH_12_NCLIENT = conversion.AmountFormat((int) count_12, ' ');
        P_MONTH_12_NCLIENT_CUMUL = conversion.AmountFormat((int) count_12_cumul, ' ');
        P_MONTH_12_BRUTTTC = conversion.AmountFormat((int) brut_12, ' ');
        P_MONTH_12_BRUTTTC_CUMUL = conversion.AmountFormat((int) brut_12_cumul, ' ');
        P_MONTH_12_NETTTC = conversion.AmountFormat((int) net_12, ' ');
        P_MONTH_12_NETTTC_CUMUL = conversion.AmountFormat((int) net_12_cumul, ' ');
        P_MONTH_12_REMISE = conversion.AmountFormat((int) remise_12, ' ');
        P_MONTH_12_REMISE_CUMUL = conversion.AmountFormat((int) remise_12_cumul, ' ');
        P_MONTH_12_VENTESVO = conversion.AmountFormat((int) ventes_vo_12, ' ');
        P_MONTH_12_VENTESVO_CUMUL = conversion.AmountFormat((int) ventes_vo_cumul_12, ' ');
        P_MONTH_12_VENTESVNO = conversion.AmountFormat((int) ventes_vno_12, ' ');
        P_MONTH_12_VENTESVNO_CUMUL = conversion.AmountFormat((int) ventes_vno_cumul_12, ' ');
        P_MONTH_12_PANORD = Math.round(pan_moy_vo_12) + "";
        P_MONTH_12_PANORD_CUMUL = Math.round(pan_moy_vo_cumul_12) + "";
        P_MONTH_12_VNOPERCENT_MONTH = vente_percent_vno_12 + "";
        P_MONTH_12_VNOPERCENT_CUMUL = vente_percent_vno_cumul_12 + "";
        P_MONTH_12_VOPERCENT_CUMUL = vente_percent_vo_cumul_12 + "";
        P_MONTH_12_VOPERCENT_MONTH = vente_percent_vo_cumul_12 + "";
        P_MONTH_12_PANNO = Math.round(pan_moy_vno_12) + "";
        P_MONTH_12_PANNO_CUMUL = Math.round(pan_moy_vno_cumul_12) + "";
    }
    Map parameters = new HashMap();
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
    parameters.put("P_H_LOGO", P_H_LOGO);
    parameters.put("P_H_INSTITUTION", P_H_INSTITUTION);
    parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);

    parameters.put("P_AUTRE_DESC",  oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);
    parameters.put("P_PRINTED_BY", "kobena");
   
    String finalphonestring = oTOfficine.getStrPHONE() != null ? "Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
        if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
            String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
            for (String va : phone) {
                finalphonestring += " / " + conversion.PhoneNumberFormat(va);
            }
        }
        parameters.put("P_H_PHONE", finalphonestring);
       
    parameters.put("P_MONTH_12", "12/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_11", "11/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_10", "10/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_9", "09/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_8", "08/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_7", "07/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_6", "06/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_5", "05/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_4", "04/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_3", "03/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_2", "02/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_1", "01/" + date.FORMATTERYEAR.format(java.sql.Date.valueOf(dt_start)));
    parameters.put("P_MONTH_1_NCLIENT", P_MONTH_1_NCLIENT);
    parameters.put("P_MONTH_1_NCLIENT_CUMUL", P_MONTH_1_NCLIENT_CUMUL);
    parameters.put("P_MONTH_1_BRUTTTC", P_MONTH_1_BRUTTTC);
    parameters.put("P_MONTH_1_BRUTTTC_CUMUL", P_MONTH_1_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_1_NETTTC", P_MONTH_1_NETTTC);
    parameters.put("P_MONTH_1_NETTTC_CUMUL", P_MONTH_1_NETTTC_CUMUL);
    parameters.put("P_MONTH_1_REMISE", P_MONTH_1_REMISE);
    parameters.put("P_MONTH_1_REMISE_CUMUL", P_MONTH_1_REMISE_CUMUL);
    parameters.put("P_MONTH_1_PANORD", P_MONTH_1_PANORD);
    parameters.put("P_MONTH_1_PANORD_CUMUL", P_MONTH_1_PANORD_CUMUL);
    parameters.put("P_MONTH_1_PANNO", P_MONTH_1_PANNO);
    parameters.put("P_MONTH_1_PANNO_CUMUL", P_MONTH_1_PANNO_CUMUL);
    parameters.put("P_MONTH_1_VENTESVO", P_MONTH_1_VENTESVO);
    parameters.put("P_MONTH_1_VENTESVO_CUMUL", P_MONTH_1_VENTESVO_CUMUL);
    parameters.put("P_MONTH_1_VENTESVNO", P_MONTH_1_VENTESVNO);
    parameters.put("P_MONTH_1_VENTESVNO_CUMUL", P_MONTH_1_VENTESVNO_CUMUL);
     parameters.put("P_MONTH_1_VNOPERCENT_MONTH",!"".equals(P_MONTH_1_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_1_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_1_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_1_VNOPERCENT_CUMUL",!"".equals(P_MONTH_1_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_1_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_1_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_1_VOPERCENT_MONTH", !"".equals(P_MONTH_1_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_1_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_1_VOPERCENT_MONTH);
    parameters.put("P_MONTH_1_VOPERCENT_CUMUL", !"".equals(P_MONTH_1_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_1_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_1_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_2_NCLIENT", P_MONTH_2_NCLIENT);
    parameters.put("P_MONTH_2_NCLIENT_CUMUL", P_MONTH_2_NCLIENT_CUMUL);
    parameters.put("P_MONTH_2_BRUTTTC", P_MONTH_2_BRUTTTC);
    parameters.put("P_MONTH_2_BRUTTTC_CUMUL", P_MONTH_2_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_2_NETTTC", P_MONTH_2_NETTTC);
    parameters.put("P_MONTH_2_NETTTC_CUMUL", P_MONTH_2_NETTTC_CUMUL);
    parameters.put("P_MONTH_2_REMISE", P_MONTH_2_REMISE);
    parameters.put("P_MONTH_2_REMISE_CUMUL", P_MONTH_2_REMISE_CUMUL);
    parameters.put("P_MONTH_2_PANORD", P_MONTH_2_PANORD);
    parameters.put("P_MONTH_2_PANORD_CUMUL", P_MONTH_2_PANORD_CUMUL);
    parameters.put("P_MONTH_2_PANNO", P_MONTH_2_PANNO);
    parameters.put("P_MONTH_2_PANNO_CUMUL", P_MONTH_2_PANNO_CUMUL);
    parameters.put("P_MONTH_2_VENTESVO", P_MONTH_2_VENTESVO);
    parameters.put("P_MONTH_2_VENTESVO_CUMUL", P_MONTH_2_VENTESVO_CUMUL);
    parameters.put("P_MONTH_2_VENTESVNO", P_MONTH_2_VENTESVNO);
    parameters.put("P_MONTH_2_VENTESVNO_CUMUL", P_MONTH_2_VENTESVNO_CUMUL);
    parameters.put("P_MONTH_2_VNOPERCENT_MONTH",!"".equals(P_MONTH_2_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_2_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_2_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_2_VNOPERCENT_CUMUL",!"".equals(P_MONTH_2_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_2_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_2_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_2_VOPERCENT_MONTH", !"".equals(P_MONTH_2_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_2_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_2_VOPERCENT_MONTH);
    parameters.put("P_MONTH_2_VOPERCENT_CUMUL", !"".equals(P_MONTH_2_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_2_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_2_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_3_NCLIENT", P_MONTH_3_NCLIENT);
    parameters.put("P_MONTH_3_NCLIENT_CUMUL", P_MONTH_3_NCLIENT_CUMUL);
    parameters.put("P_MONTH_3_BRUTTTC", P_MONTH_3_BRUTTTC);
    parameters.put("P_MONTH_3_BRUTTTC_CUMUL", P_MONTH_3_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_3_NETTTC", P_MONTH_3_NETTTC);
    parameters.put("P_MONTH_3_NETTTC_CUMUL", P_MONTH_3_NETTTC_CUMUL);
    parameters.put("P_MONTH_3_REMISE", P_MONTH_3_REMISE);
    parameters.put("P_MONTH_3_REMISE_CUMUL", P_MONTH_3_REMISE_CUMUL);
    parameters.put("P_MONTH_3_PANORD", P_MONTH_3_PANORD);
    parameters.put("P_MONTH_3_PANORD_CUMUL", P_MONTH_3_PANORD_CUMUL);
    parameters.put("P_MONTH_3_PANNO", P_MONTH_3_PANNO);
    parameters.put("P_MONTH_3_PANNO_CUMUL", P_MONTH_3_PANNO_CUMUL);
    parameters.put("P_MONTH_3_VENTESVO", P_MONTH_3_VENTESVO);
    parameters.put("P_MONTH_3_VENTESVO_CUMUL", P_MONTH_3_VENTESVO_CUMUL);
    parameters.put("P_MONTH_3_VENTESVNO", P_MONTH_3_VENTESVNO);
    parameters.put("P_MONTH_3_VENTESVNO_CUMUL", P_MONTH_3_VENTESVNO_CUMUL);
    parameters.put("P_MONTH_3_VNOPERCENT_MONTH",!"".equals(P_MONTH_3_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_3_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_3_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_3_VNOPERCENT_CUMUL",!"".equals(P_MONTH_3_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_3_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_3_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_3_VOPERCENT_MONTH", !"".equals(P_MONTH_3_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_3_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_3_VOPERCENT_MONTH);
    parameters.put("P_MONTH_3_VOPERCENT_CUMUL", !"".equals(P_MONTH_3_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_3_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_3_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_4_NCLIENT", P_MONTH_4_NCLIENT);
    parameters.put("P_MONTH_4_NCLIENT_CUMUL", P_MONTH_4_NCLIENT_CUMUL);
    parameters.put("P_MONTH_4_BRUTTTC", P_MONTH_4_BRUTTTC);
    parameters.put("P_MONTH_4_BRUTTTC_CUMUL", P_MONTH_4_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_4_NETTTC", P_MONTH_4_NETTTC);
    parameters.put("P_MONTH_4_NETTTC_CUMUL", P_MONTH_4_NETTTC_CUMUL);
    parameters.put("P_MONTH_4_REMISE", P_MONTH_4_REMISE);
    parameters.put("P_MONTH_4_REMISE_CUMUL", P_MONTH_4_REMISE_CUMUL);
    parameters.put("P_MONTH_4_PANORD", P_MONTH_4_PANORD);
    parameters.put("P_MONTH_4_PANORD_CUMUL", P_MONTH_4_PANORD_CUMUL);
    parameters.put("P_MONTH_4_PANNO", P_MONTH_4_PANNO);
    parameters.put("P_MONTH_4_PANNO_CUMUL", P_MONTH_4_PANNO_CUMUL);
    parameters.put("P_MONTH_4_VENTESVO", P_MONTH_4_VENTESVO);
    parameters.put("P_MONTH_4_VENTESVO_CUMUL", P_MONTH_4_VENTESVO_CUMUL);
    parameters.put("P_MONTH_4_VENTESVNO", P_MONTH_4_VENTESVNO);
    parameters.put("P_MONTH_4_VENTESVNO_CUMUL", P_MONTH_4_VENTESVNO_CUMUL);
    parameters.put("P_MONTH_4_VNOPERCENT_MONTH",!"".equals(P_MONTH_4_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_4_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_4_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_4_VNOPERCENT_CUMUL",!"".equals(P_MONTH_4_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_4_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_4_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_4_VOPERCENT_MONTH", !"".equals(P_MONTH_4_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_4_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_4_VOPERCENT_MONTH);
    parameters.put("P_MONTH_4_VOPERCENT_CUMUL", !"".equals(P_MONTH_4_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_4_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_4_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_5_NCLIENT", P_MONTH_5_NCLIENT);
    parameters.put("P_MONTH_5_NCLIENT_CUMUL", P_MONTH_5_NCLIENT_CUMUL);
    parameters.put("P_MONTH_5_BRUTTTC", P_MONTH_5_BRUTTTC);
    parameters.put("P_MONTH_5_BRUTTTC_CUMUL", P_MONTH_5_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_5_NETTTC", P_MONTH_5_NETTTC);
    parameters.put("P_MONTH_5_NETTTC_CUMUL", P_MONTH_5_NETTTC_CUMUL);
    parameters.put("P_MONTH_5_REMISE", P_MONTH_5_REMISE);
    parameters.put("P_MONTH_5_REMISE_CUMUL", P_MONTH_5_REMISE_CUMUL);
    parameters.put("P_MONTH_5_PANORD", P_MONTH_5_PANORD);
    parameters.put("P_MONTH_5_PANORD_CUMUL", P_MONTH_5_PANORD_CUMUL);
    parameters.put("P_MONTH_5_PANNO", P_MONTH_5_PANNO);
    parameters.put("P_MONTH_5_PANNO_CUMUL", P_MONTH_5_PANNO_CUMUL);
    parameters.put("P_MONTH_5_VENTESVO", P_MONTH_5_VENTESVO);
    parameters.put("P_MONTH_5_VENTESVO_CUMUL", P_MONTH_5_VENTESVO_CUMUL);
    parameters.put("P_MONTH_5_VENTESVNO", P_MONTH_5_VENTESVNO);
    parameters.put("P_MONTH_5_VENTESVNO_CUMUL", P_MONTH_5_VENTESVNO_CUMUL);
    parameters.put("P_MONTH_5_VNOPERCENT_MONTH",!"".equals(P_MONTH_5_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_5_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_5_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_5_VNOPERCENT_CUMUL",!"".equals(P_MONTH_5_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_5_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_5_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_5_VOPERCENT_MONTH", !"".equals(P_MONTH_5_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_5_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_5_VOPERCENT_MONTH);
    parameters.put("P_MONTH_5_VOPERCENT_CUMUL", !"".equals(P_MONTH_5_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_5_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_5_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_6_NCLIENT", P_MONTH_6_NCLIENT);
    parameters.put("P_MONTH_6_NCLIENT_CUMUL", P_MONTH_6_NCLIENT_CUMUL);
    parameters.put("P_MONTH_6_BRUTTTC", P_MONTH_6_BRUTTTC);
    parameters.put("P_MONTH_6_BRUTTTC_CUMUL", P_MONTH_6_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_6_NETTTC", P_MONTH_6_NETTTC);
    parameters.put("P_MONTH_6_NETTTC_CUMUL", P_MONTH_6_NETTTC_CUMUL);
    parameters.put("P_MONTH_6_REMISE", P_MONTH_6_REMISE);
    parameters.put("P_MONTH_6_REMISE_CUMUL", P_MONTH_6_REMISE_CUMUL);
    parameters.put("P_MONTH_6_PANORD", P_MONTH_6_PANORD);
    parameters.put("P_MONTH_6_PANORD_CUMUL", P_MONTH_6_PANORD_CUMUL);
    parameters.put("P_MONTH_6_PANNO", P_MONTH_6_PANNO);
    parameters.put("P_MONTH_6_PANNO_CUMUL", P_MONTH_6_PANNO_CUMUL);
    parameters.put("P_MONTH_6_VENTESVO", P_MONTH_6_VENTESVO);
    parameters.put("P_MONTH_6_VENTESVO_CUMUL", P_MONTH_6_VENTESVO_CUMUL);
    parameters.put("P_MONTH_6_VENTESVNO", P_MONTH_6_VENTESVNO);
    parameters.put("P_MONTH_6_VENTESVNO_CUMUL", P_MONTH_6_VENTESVNO_CUMUL);
     parameters.put("P_MONTH_6_VNOPERCENT_MONTH",!"".equals(P_MONTH_6_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_6_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_6_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_6_VNOPERCENT_CUMUL",!"".equals(P_MONTH_6_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_6_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_6_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_6_VOPERCENT_MONTH", !"".equals(P_MONTH_6_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_6_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_6_VOPERCENT_MONTH);
    parameters.put("P_MONTH_6_VOPERCENT_CUMUL", !"".equals(P_MONTH_6_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_6_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_6_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_7_NCLIENT", P_MONTH_7_NCLIENT);
    parameters.put("P_MONTH_7_NCLIENT_CUMUL", P_MONTH_7_NCLIENT_CUMUL);
    parameters.put("P_MONTH_7_BRUTTTC", P_MONTH_7_BRUTTTC);
    parameters.put("P_MONTH_7_BRUTTTC_CUMUL", P_MONTH_7_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_7_NETTTC", P_MONTH_7_NETTTC);
    parameters.put("P_MONTH_7_NETTTC_CUMUL", P_MONTH_7_NETTTC_CUMUL);
    parameters.put("P_MONTH_7_REMISE", P_MONTH_7_REMISE);
    parameters.put("P_MONTH_7_REMISE_CUMUL", P_MONTH_7_REMISE_CUMUL);
    parameters.put("P_MONTH_7_PANORD", P_MONTH_7_PANORD);
    parameters.put("P_MONTH_7_PANORD_CUMUL", P_MONTH_7_PANORD_CUMUL);
    parameters.put("P_MONTH_7_PANNO", P_MONTH_7_PANNO);
    parameters.put("P_MONTH_7_PANNO_CUMUL", P_MONTH_7_PANNO_CUMUL);
    parameters.put("P_MONTH_7_VENTESVO", P_MONTH_7_VENTESVO);
    parameters.put("P_MONTH_7_VENTESVO_CUMUL", P_MONTH_7_VENTESVO_CUMUL);
    parameters.put("P_MONTH_7_VENTESVNO", P_MONTH_7_VENTESVNO);
    parameters.put("P_MONTH_7_VENTESVNO_CUMUL", P_MONTH_7_VENTESVNO_CUMUL);
    parameters.put("P_MONTH_7_VNOPERCENT_MONTH",!"".equals(P_MONTH_7_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_7_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_7_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_7_VNOPERCENT_CUMUL",!"".equals(P_MONTH_7_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_7_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_7_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_7_VOPERCENT_MONTH", !"".equals(P_MONTH_7_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_7_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_7_VOPERCENT_MONTH);
    parameters.put("P_MONTH_7_VOPERCENT_CUMUL", !"".equals(P_MONTH_7_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_7_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_7_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_8_NCLIENT", P_MONTH_8_NCLIENT);
    parameters.put("P_MONTH_8_NCLIENT_CUMUL", P_MONTH_8_NCLIENT_CUMUL);
    parameters.put("P_MONTH_8_BRUTTTC", P_MONTH_8_BRUTTTC);
    parameters.put("P_MONTH_8_BRUTTTC_CUMUL", P_MONTH_8_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_8_NETTTC", P_MONTH_8_NETTTC);
    parameters.put("P_MONTH_8_NETTTC_CUMUL", P_MONTH_8_NETTTC_CUMUL);
    parameters.put("P_MONTH_8_REMISE", P_MONTH_8_REMISE);
    parameters.put("P_MONTH_8_REMISE_CUMUL", P_MONTH_8_REMISE_CUMUL);
    parameters.put("P_MONTH_8_PANORD", P_MONTH_8_PANORD);
    parameters.put("P_MONTH_8_PANORD_CUMUL", P_MONTH_8_PANORD_CUMUL);
    parameters.put("P_MONTH_8_PANNO", P_MONTH_8_PANNO);
    parameters.put("P_MONTH_8_PANNO_CUMUL", P_MONTH_8_PANNO_CUMUL);
    parameters.put("P_MONTH_8_VENTESVO", P_MONTH_8_VENTESVO);
    parameters.put("P_MONTH_8_VENTESVO_CUMUL", P_MONTH_8_VENTESVO_CUMUL);
    parameters.put("P_MONTH_8_VENTESVNO", P_MONTH_8_VENTESVNO);
    parameters.put("P_MONTH_8_VENTESVNO_CUMUL", P_MONTH_8_VENTESVNO_CUMUL);
     parameters.put("P_MONTH_8_VNOPERCENT_MONTH",!"".equals(P_MONTH_8_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_8_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_8_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_8_VNOPERCENT_CUMUL",!"".equals(P_MONTH_8_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_8_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_8_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_8_VOPERCENT_MONTH", !"".equals(P_MONTH_8_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_8_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_8_VOPERCENT_MONTH);
    parameters.put("P_MONTH_8_VOPERCENT_CUMUL", !"".equals(P_MONTH_8_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_8_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_8_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_9_NCLIENT", P_MONTH_9_NCLIENT);
    parameters.put("P_MONTH_9_NCLIENT_CUMUL", P_MONTH_9_NCLIENT_CUMUL);
    parameters.put("P_MONTH_9_BRUTTTC", P_MONTH_9_BRUTTTC);
    parameters.put("P_MONTH_9_BRUTTTC_CUMUL", P_MONTH_9_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_9_NETTTC", P_MONTH_9_NETTTC);
    parameters.put("P_MONTH_9_NETTTC_CUMUL", P_MONTH_9_NETTTC_CUMUL);
    parameters.put("P_MONTH_9_REMISE", P_MONTH_9_REMISE);
    parameters.put("P_MONTH_9_REMISE_CUMUL", P_MONTH_9_REMISE_CUMUL);
    parameters.put("P_MONTH_9_PANORD", P_MONTH_9_PANORD);
    parameters.put("P_MONTH_9_PANORD_CUMUL", P_MONTH_9_PANORD_CUMUL);
    parameters.put("P_MONTH_9_PANNO", P_MONTH_9_PANNO);
    parameters.put("P_MONTH_9_PANNO_CUMUL", P_MONTH_9_PANNO_CUMUL);
    parameters.put("P_MONTH_9_VENTESVO", P_MONTH_9_VENTESVO);
    parameters.put("P_MONTH_9_VENTESVO_CUMUL", P_MONTH_9_VENTESVO_CUMUL);
    parameters.put("P_MONTH_9_VENTESVNO", P_MONTH_9_VENTESVNO);
    parameters.put("P_MONTH_9_VENTESVNO_CUMUL", P_MONTH_9_VENTESVNO_CUMUL);
     parameters.put("P_MONTH_9_VNOPERCENT_MONTH",!"".equals(P_MONTH_9_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_9_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_9_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_9_VNOPERCENT_CUMUL",!"".equals(P_MONTH_9_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_9_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_9_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_9_VOPERCENT_MONTH", !"".equals(P_MONTH_9_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_9_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_9_VOPERCENT_MONTH);
    parameters.put("P_MONTH_9_VOPERCENT_CUMUL", !"".equals(P_MONTH_9_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_9_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_9_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_10_NCLIENT", P_MONTH_10_NCLIENT);
    parameters.put("P_MONTH_10_NCLIENT_CUMUL", P_MONTH_10_NCLIENT_CUMUL);
    parameters.put("P_MONTH_10_BRUTTTC", P_MONTH_10_BRUTTTC);
    parameters.put("P_MONTH_10_BRUTTTC_CUMUL", P_MONTH_10_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_10_NETTTC", P_MONTH_10_NETTTC);
    parameters.put("P_MONTH_10_NETTTC_CUMUL", P_MONTH_10_NETTTC_CUMUL);
    parameters.put("P_MONTH_10_REMISE", P_MONTH_10_REMISE);
    parameters.put("P_MONTH_10_REMISE_CUMUL", P_MONTH_10_REMISE_CUMUL);
    parameters.put("P_MONTH_10_PANORD", P_MONTH_10_PANORD);
    parameters.put("P_MONTH_10_PANORD_CUMUL", P_MONTH_10_PANORD_CUMUL);
    parameters.put("P_MONTH_10_PANNO", P_MONTH_10_PANNO);
    parameters.put("P_MONTH_10_PANNO_CUMUL", P_MONTH_10_PANNO_CUMUL);
    parameters.put("P_MONTH_10_VENTESVO", P_MONTH_10_VENTESVO);
    parameters.put("P_MONTH_10_VENTESVO_CUMUL", P_MONTH_10_VENTESVO_CUMUL);
    parameters.put("P_MONTH_10_VENTESVNO", P_MONTH_10_VENTESVNO);
    parameters.put("P_MONTH_10_VENTESVNO_CUMUL", P_MONTH_10_VENTESVNO_CUMUL);
    parameters.put("P_MONTH_10_VNOPERCENT_MONTH",!"".equals(P_MONTH_10_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_10_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_10_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_10_VNOPERCENT_CUMUL",!"".equals(P_MONTH_10_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_10_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_10_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_10_VOPERCENT_MONTH", !"".equals(P_MONTH_10_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_10_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_10_VOPERCENT_MONTH);
    parameters.put("P_MONTH_10_VOPERCENT_CUMUL", !"".equals(P_MONTH_10_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_10_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_10_VOPERCENT_CUMUL);
    parameters.put("P_MONTH_11_NCLIENT", P_MONTH_11_NCLIENT);
    parameters.put("P_MONTH_11_NCLIENT_CUMUL", P_MONTH_11_NCLIENT_CUMUL);
    parameters.put("P_MONTH_11_BRUTTTC", P_MONTH_11_BRUTTTC);
    parameters.put("P_MONTH_11_BRUTTTC_CUMUL", P_MONTH_11_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_11_NETTTC", P_MONTH_11_NETTTC);
    parameters.put("P_MONTH_11_NETTTC_CUMUL", P_MONTH_11_NETTTC_CUMUL);
    parameters.put("P_MONTH_11_REMISE", P_MONTH_11_REMISE);
    parameters.put("P_MONTH_11_REMISE_CUMUL", P_MONTH_11_REMISE_CUMUL);
    parameters.put("P_MONTH_11_PANORD", P_MONTH_11_PANORD);
    parameters.put("P_MONTH_11_PANORD_CUMUL", P_MONTH_11_PANORD_CUMUL);
    parameters.put("P_MONTH_11_PANNO", P_MONTH_11_PANNO);
    parameters.put("P_MONTH_11_PANNO_CUMUL", P_MONTH_11_PANNO_CUMUL);
    parameters.put("P_MONTH_11_VENTESVO", P_MONTH_11_VENTESVO);
    parameters.put("P_MONTH_11_VENTESVO_CUMUL", P_MONTH_11_VENTESVO_CUMUL);
    parameters.put("P_MONTH_11_VENTESVNO", P_MONTH_11_VENTESVNO);
    parameters.put("P_MONTH_11_VENTESVNO_CUMUL", P_MONTH_11_VENTESVNO_CUMUL);
     parameters.put("P_MONTH_11_VNOPERCENT_MONTH",!"".equals(P_MONTH_11_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_11_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_11_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_11_VNOPERCENT_CUMUL",!"".equals(P_MONTH_11_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_11_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_11_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_11_VOPERCENT_MONTH", !"".equals(P_MONTH_11_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_11_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_11_VOPERCENT_MONTH);
    parameters.put("P_MONTH_11_VOPERCENT_CUMUL", !"".equals(P_MONTH_11_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_11_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_11_VOPERCENT_CUMUL);
    
    parameters.put("P_MONTH_12_NCLIENT", P_MONTH_12_NCLIENT);
    parameters.put("P_MONTH_12_NCLIENT_CUMUL", P_MONTH_12_NCLIENT_CUMUL);
    parameters.put("P_MONTH_12_BRUTTTC", P_MONTH_12_BRUTTTC);
    parameters.put("P_MONTH_12_BRUTTTC_CUMUL", P_MONTH_12_BRUTTTC_CUMUL);
    parameters.put("P_MONTH_12_NETTTC", P_MONTH_12_NETTTC);
    parameters.put("P_MONTH_12_NETTTC_CUMUL", P_MONTH_12_NETTTC_CUMUL);
    parameters.put("P_MONTH_12_REMISE", P_MONTH_12_REMISE);
    parameters.put("P_MONTH_12_REMISE_CUMUL", P_MONTH_12_REMISE_CUMUL);
    parameters.put("P_MONTH_12_PANORD", P_MONTH_12_PANORD);
    parameters.put("P_MONTH_12_PANORD_CUMUL", P_MONTH_12_PANORD_CUMUL);
    parameters.put("P_MONTH_12_PANNO", P_MONTH_12_PANNO);
    parameters.put("P_MONTH_12_PANNO_CUMUL", P_MONTH_12_PANNO_CUMUL);
    parameters.put("P_MONTH_12_VENTESVO", P_MONTH_12_VENTESVO);
    parameters.put("P_MONTH_12_VENTESVO_CUMUL", P_MONTH_12_VENTESVO_CUMUL);
    parameters.put("P_MONTH_12_VENTESVNO", P_MONTH_12_VENTESVNO);
    parameters.put("P_MONTH_12_VENTESVNO_CUMUL", P_MONTH_12_VENTESVNO_CUMUL);
     parameters.put("P_MONTH_12_VNOPERCENT_MONTH",!"".equals(P_MONTH_12_VNOPERCENT_MONTH)? new BigDecimal(P_MONTH_12_VNOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_12_VNOPERCENT_MONTH);
    parameters.put("P_MONTH_12_VNOPERCENT_CUMUL",!"".equals(P_MONTH_12_VNOPERCENT_CUMUL)? new BigDecimal(P_MONTH_12_VNOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_12_VNOPERCENT_CUMUL);
    parameters.put("P_MONTH_12_VOPERCENT_MONTH", !"".equals(P_MONTH_12_VOPERCENT_MONTH)?new BigDecimal(P_MONTH_12_VOPERCENT_MONTH).setScale(2, RoundingMode.HALF_UP):P_MONTH_12_VOPERCENT_MONTH);
    parameters.put("P_MONTH_12_VOPERCENT_CUMUL", !"".equals(P_MONTH_12_VOPERCENT_CUMUL)? new BigDecimal(P_MONTH_12_VOPERCENT_CUMUL).setScale(2, RoundingMode.HALF_UP):P_MONTH_12_VOPERCENT_CUMUL);
    OreportManager.BuildReport(parameters, Ojconnexion);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "rp_statistic_ventes" + report_generate_file);

%>


