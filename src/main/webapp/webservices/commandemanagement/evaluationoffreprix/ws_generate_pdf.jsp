<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TOrderDetail"%>
<%@page import="dal.TOrder"%>
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
    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TParameters OTParameters;
    bllBase ObllBase = new bllBase();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%
    String lg_ORDER_ID = "%%", str_NAME_FILE = "", str_STATUT = commonparameter.orderIsPassed;

    if (request.getParameter("lg_ORDER_ID") != null) {
        lg_ORDER_ID = request.getParameter("lg_ORDER_ID");
        new logger().OCategory.info("lg_ORDER_ID " + lg_ORDER_ID);
    }
    
    if (request.getParameter("str_STATUT") != null && !request.getParameter("str_STATUT").equalsIgnoreCase("")) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("Utilisateur " + OTUser.getStrFIRSTNAME() + " Id " + OTUser.getLgUSERID());
    jdom Ojdom = new jdom();
    Ojdom.InitRessource();
    Ojdom.LoadRessource();
    jconnexion Ojconnexion = new jconnexion();
    Ojconnexion.initConnexion();
    Ojconnexion.OpenConnexion();
    date key = new date();
    reportManager OreportManager = new reportManager();

    String scr_report_file = "rp_bon_commande_passee";
    String report_generate_file = key.GetNumberRandom();

    new logger().OCategory.info("scr_report_file " + scr_report_file);
    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "bon_de_commande_" + report_generate_file);

    bllBase obllBase = new bllBase();
    obllBase.checkDatamanager();
    TOrder oTOrder = obllBase.getOdataManager().getEm().find(dal.TOrder.class, lg_ORDER_ID);
    TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");

    new logger().OCategory.info(" ****   inventaire id depuis generate pdf   ****  HHHHH" + oTOrder.getLgORDERID() + " " + oTOfficine.getStrNOMCOMPLET());

    // Récupération des paramètres
    // Entete
    String P_H_INSTITUTION = oTOfficine.getStrNOMABREGE();
    String P_INSTITUTION_ADRESSE = oTOfficine.getStrADRESSSEPOSTALE();
    String P_H_CLT_INFOS = "COMMANDE DE REAPPROVISIONNEMENT";
    String P_H_LOGO = jdom.scr_report_file_logo;
    String P_H_lg_ORDER_ID = oTOrder.getStrREFORDER();
    String P_H_lg_GROSSISTE_ID = oTOrder.getLgGROSSISTEID().getStrLIBELLE();

    int int_LINE = 0;
    int int_PRODUCT = 0;
    String int_COUNT = "", PRIX_VENTE_TOTAL = "", PRIX_ACHAT_TOTAL = "";
    int int_PRIX_ACHAT_TOTAL = 0;
    int int_PRIX_VENTE_TOTAL = 0;

    List<dal.TOrderDetail> lstTOrderDetail = null;

    orderManagement OorderManagement = new orderManagement(OdataManager, OTUser);

    lstTOrderDetail = OorderManagement.getTOrderDetail(lg_ORDER_ID, str_STATUT);
    int_LINE = lstTOrderDetail.size();

    new logger().OCategory.info(" Avant la boucle for  **** nbre detail  " + int_LINE);

    int_COUNT = "(" + int_LINE + " lignes) de " + OorderManagement.getQuantityByCommande(lstTOrderDetail) + " produit(s)";
    int_PRIX_ACHAT_TOTAL = OorderManagement.getPriceTotalAchat(lstTOrderDetail);
    int_PRIX_VENTE_TOTAL = OorderManagement.getPriceTotalVente(lstTOrderDetail);
    Map parameters = new HashMap();
    parameters.put("P_H_LOGO", P_H_LOGO);
    parameters.put("P_H_INSTITUTION", P_H_INSTITUTION);
    parameters.put("P_INSTITUTION_ADRESSE", P_INSTITUTION_ADRESSE);
    parameters.put("P_PRINTED_BY", OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);

    parameters.put("P_REFERENCE", lg_ORDER_ID);
    parameters.put("P_H_lg_ORDER_ID", P_H_lg_ORDER_ID);
    parameters.put("P_H_lg_GROSSISTE_ID", P_H_lg_GROSSISTE_ID);

    parameters.put("PRIX_VENTE_TOTAL", int_PRIX_VENTE_TOTAL);
    parameters.put("PRIX_ACHAT_TOTAL", int_PRIX_ACHAT_TOTAL);
    parameters.put("int_COUNT", int_COUNT);
    parameters.put("P_EMPLACEMENT",  OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    
     parameters.put("P_H_CC_RC", (oTOfficine.getStrCOMPTECONTRIBUABLE() != null ? "CC:" + oTOfficine.getStrCOMPTECONTRIBUABLE() : "") + (oTOfficine.getStrREGISTRECOMMERCE() != null ? " / RC: " + oTOfficine.getStrREGISTRECOMMERCE() : ""));

     parameters.put("P_H_CI_RI", (oTOfficine.getStrCENTREIMPOSITION() != null ? "CI:" + oTOfficine.getStrCENTREIMPOSITION() : "") + (oTOfficine.getStrREGISTREIMPOSITION() != null ? " / RI: " + oTOfficine.getStrREGISTREIMPOSITION() : ""));
parameters.put("P_AUTRE_DESC", oTOfficine.getStrFIRSTNAME() + " " + oTOfficine.getStrLASTNAME());
    String finalphonestring = oTOfficine.getStrPHONE() != null ? "Tel: " + conversion.PhoneNumberFormat("+225", oTOfficine.getStrPHONE()) : "";
            if (!"".equals(oTOfficine.getStrAUTRESPHONES())) {
                String[] phone = oTOfficine.getStrAUTRESPHONES().split(";");
                for (String va : phone) {
                    finalphonestring += " / " + conversion.PhoneNumberFormat(va);
                }
            }
            parameters.put("P_H_PHONE", finalphonestring);
           
OreportManager.BuildReport(parameters, Ojconnexion);

    ObllBase.setKey(new date());
    ObllBase.setOTranslate(OTranslate);
    ObllBase.setOTUser(OTUser);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "bon_de_commande_" + report_generate_file);

%>


