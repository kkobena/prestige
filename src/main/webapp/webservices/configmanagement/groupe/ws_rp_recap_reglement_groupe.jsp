<%--
    Recapitulatif de reglement d'une facture de groupe : liste des sous-factures reglees
    (montant verse / restant par ligne), totaux, en-tete officine et reference de la facture
    de groupe. Modele Jasper : rp_recap_reglement_groupe.jrxml (dossier reports du serveur).
--%>

<%@page import="util.Constant"%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>
<%@page import="dal.TGroupeTierspayant"%>
<%@page import="dal.TFacture"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TOfficine"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.jconnexion"%>
<%@page import="report.reportManager"%>
<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>
<%@page import="bll.bllBase"%>
<%@page import="java.util.*"%>
<%@page import="toolkits.utils.date"%>

<%
    dataManager OdataManager = new dataManager();
    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(Constant.AIRTIME_USER);
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date key = new date();
    reportManager OreportManager = new reportManager();
    bllBase obllBase = new bllBase();
    obllBase.checkDatamanager();
    GroupeTierspayantController controller = new GroupeTierspayantController(OdataManager.getEmf());
    TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");

    Integer lg_GROUPE_ID = -1;
    String CODEFACTURE = null;
    if (request.getParameter("lg_GROUPE_ID") != null && !"".equals(request.getParameter("lg_GROUPE_ID"))) {
        lg_GROUPE_ID = Integer.valueOf(request.getParameter("lg_GROUPE_ID"));
    }
    if (request.getParameter("CODEFACTURE") != null && !"".equals(request.getParameter("CODEFACTURE"))) {
        CODEFACTURE = request.getParameter("CODEFACTURE");
    }

    String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
    String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();
    String P_H_LOGO = jdom.scr_report_file_logo;
    String P_FOOTER_RC = "";
    if (oTOfficine.getStrREGISTRECOMMERCE() != null) {
        P_FOOTER_RC += "RC N\u00b0 " + oTOfficine.getStrREGISTRECOMMERCE();
    }
    if (oTOfficine.getStrCOMPTECONTRIBUABLE() != null) {
        P_FOOTER_RC += " - CC N\u00b0 " + oTOfficine.getStrCOMPTECONTRIBUABLE();
    }
    if (oTOfficine.getStrPHONE() != null) {
        P_INSTITUTION_ADRESSE += " - T\u00e9l : " + util.DateConverter.phoneNumberFormat("+225", oTOfficine.getStrPHONE());
    }

    TGroupeTierspayant g = controller.findById(lg_GROUPE_ID);

    // total verse sur les sous-factures de cette facture de groupe
    List<TFacture> factures = controller.getGroupeInvoiceDetails(lg_GROUPE_ID, CODEFACTURE);
    long totalVerse = 0;
    for (TFacture f : factures) {
        if (f.getDblMONTANTPAYE() != null && f.getDblMONTANTPAYE() > 0) {
            totalVerse += f.getDblMONTANTPAYE().longValue();
        }
    }

    Map parameters = new HashMap();
    parameters.put("P_H_LOGO", P_H_LOGO);
    parameters.put("P_H_INSTITUTION", P_H_INSTITUTION);
    parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
    parameters.put("P_FOOTER_RC", P_FOOTER_RC);
    parameters.put("P_PRINTED_BY", " LE PHARMACIEN ");
    parameters.put("P_AUTRE_DESC", oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
    parameters.put("P_H_CLT_INFOS", "R\u00c9CAPITULATIF DE R\u00c8GLEMENT - FACTURE N\u00b0 " + CODEFACTURE);
    parameters.put("P_CODEREGROUPEMENT", g.getStrLIBELLE());
    parameters.put("P_ADRESSE", g.getStrADRESSE());
    parameters.put("P_GROUPETELEPHONE", g.getStrTELEPHONE());
    parameters.put("lg_GROUPE", g.getLgGROUPEID());
    parameters.put("P_CODEGRPUPEFACTURE", CODEFACTURE);
    parameters.put("P_TOTAL_INGROUPE_LETTERS", conversion.GetNumberTowords(Double.parseDouble(totalVerse + "")).toUpperCase()
            + " (" + conversion.AmountFormat((int) totalVerse) + " FCFA)");

    // Nom de fichier lisible : Recap_reglement_<groupe>_<facture>-<jour>-<heure>.pdf. Un dossier
    // de recapitulatifs edites dans la journee ne se relit pas avec un simple numero aleatoire.
    String nomGroupeFichier = java.text.Normalizer
            .normalize(org.apache.commons.lang3.StringUtils.defaultString(g.getStrLIBELLE()), java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "").replaceAll("[^A-Za-z0-9]+", "_");
    nomGroupeFichier = org.apache.commons.lang3.StringUtils.strip(nomGroupeFichier, "_");
    String str_file = "Recap_reglement_" + nomGroupeFichier + "_"
            + org.apache.commons.lang3.StringUtils.strip(
                    org.apache.commons.lang3.StringUtils.defaultString(CODEFACTURE)
                            .replaceAll("[^A-Za-z0-9]+", "_"), "_")
            + "-" + new java.text.SimpleDateFormat("ddMMyyyy").format(new java.util.Date())
            + "-" + new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date()) + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + "rp_recap_reglement_groupe.jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + str_file);
    OreportManager.BuildReport(parameters, Ojconnexion);
    Ojconnexion.CloseConnexion();
    OdataManager.closeEntityManager();

    response.sendRedirect("../../../data/reports/pdf/" + str_file);
%>
