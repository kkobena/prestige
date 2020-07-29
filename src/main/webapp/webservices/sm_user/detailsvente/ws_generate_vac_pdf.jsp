<%@page import="bll.preenregistrement.DevisManagement"%>
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

<%!     String lg_PREENREGISTREMENT_ID = "%%";
    Translate OTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TParameters OTParameters;
    bllBase ObllBase = new bllBase();
    TUser OTUser = null;
%>


<!-- fin logic de gestion des page -->

<%

    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
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
    reportManager OreportManager_subreport = new reportManager();
    String scr_report_file = "rp_ticket_vac";
    String report_generate_file = key.GetNumberRandom();

    report_generate_file = report_generate_file + ".pdf";
    OreportManager.setPath_report_src(Ojdom.scr_report_file + scr_report_file + ".jrxml");
    OreportManager.setPath_report_pdf(Ojdom.scr_report_pdf + "ticket_" + report_generate_file);



    bllBase obllBase = new bllBase();
    obllBase.checkDatamanager();
    TCompteClientTiersPayant OTCompteClientTiersPayant = new TCompteClientTiersPayant();
    List<TPreenregistrementCompteClientTiersPayent> lstT = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
    List<TPreenregistrementCompteClient> lstTPC = new ArrayList<TPreenregistrementCompteClient>();
    TPreenregistrementCompteClient OTPreenregistrementCompteClient = new TPreenregistrementCompteClient();

    TPreenregistrement oTPreenregistrement = obllBase.getOdataManager().getEm().find(dal.TPreenregistrement.class, lg_PREENREGISTREMENT_ID);
    TOfficine oTOfficine = obllBase.getOdataManager().getEm().find(dal.TOfficine.class, "1");

    new logger().OCategory.info(" ****   lg_preenregistrement_id depuis generate pdf   ****  HHHHH" + oTPreenregistrement.getLgPREENREGISTREMENTID() + " " + oTOfficine.getStrNOMCOMPLET());

    String P_H_TITTLE = "";

    String P_H_CLT_INFOS = "";
    String str_clt_firstname = "", str_clt_lastname = "", str_tp_name = "", str_tp_infos = "";
    int int_tp_taux = 0;
    String int_customer_amount = "";
    String P_CUST_PART = "5555";

    if (oTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID().equals("3")) {

        lstT = obllBase.getOdataManager().getEm().createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID LIKE ?1").
                setParameter(1, oTPreenregistrement.getLgPREENREGISTREMENTID())
                .getResultList();

        lstTPC = obllBase.getOdataManager().getEm().createQuery("SELECT t FROM TPreenregistrementCompteClient t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID LIKE ?1").
                setParameter(1, oTPreenregistrement.getLgPREENREGISTREMENTID())
                .getResultList();
       /* if (lstTPC != null && (!lstTPC.isEmpty())) {
            if (OTPreenregistrementCompteClient != null) {
                if (OTPreenregistrementCompteClient.getIntPRICE() != null) {
                    int_customer_amount = "PART CLIENT   " + OTPreenregistrementCompteClient.getIntPRICE() + "  CFA";
                } else {
                    int_customer_amount = "";
                }
            } else {
                int_customer_amount = "";
            }
        } else {
            int_customer_amount = "";
        }*/

        for (int i = lstT.size(); --i >= 0;) {

            OTCompteClientTiersPayant = lstT.get(i).getLgCOMPTECLIENTTIERSPAYANTID();

            str_tp_infos = OTCompteClientTiersPayant.getLgTIERSPAYANTID().getStrNAME() + " " + OTCompteClientTiersPayant.getIntPOURCENTAGE() + "%" + " :: " + lstT.get(i).getIntPRICE() + "  " + str_tp_infos;
            new logger().OCategory.info(" str_tp_infos   " + str_tp_infos);
            new logger().OCategory.info(" ****   OTPreenregistrementCompteClientTiersPayent   **** " + OTCompteClientTiersPayant.getLgCOMPTECLIENTTIERSPAYANTID());
        }

        str_clt_firstname = lstT.get(0).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME();
        str_clt_lastname = lstT.get(0).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME();
        P_H_CLT_INFOS = " Ticket de M/Mme  " + str_clt_firstname + "  " + str_clt_lastname + " ** " + str_tp_infos;

        new logger().OCategory.info("str_clt_firstname  " + str_clt_firstname + " str_clt_lastname " + str_clt_lastname);

    } else {
        P_H_CLT_INFOS = "";
    }

    new logger().OCategory.info(" ****   lg_preenregistrement_id depuis generate pdf   ****  HHHHH" + oTPreenregistrement.getLgPREENREGISTREMENTID());
    new logger().OCategory.info(" **** P_H_CLT_INFOS   ****" + P_H_CLT_INFOS);

    barecodeManager obarecodeManager = new barecodeManager();
    String fileBarecode = obarecodeManager.buildLineBarecode(oTPreenregistrement.getLgPREENREGISTREMENTID());
    Map parameters = new HashMap();

    // new logger().OCategory.info(" *** num devis   *** " + oTPreenregistrement.getLgPREENREGISTREMENTID());
    parameters.put("str_REF", oTPreenregistrement.getStrREF());
    parameters.put("P_H_TITTLE", P_H_TITTLE);
    parameters.put("P_H_LOGO", jdom.scr_report_file_logo);
    parameters.put("P_H_INSTITUTION", oTOfficine.getStrNOMABREGE());
    parameters.put("P_INSTITUTION_ADRESSE", oTOfficine.getStrADRESSSEPOSTALE());
    parameters.put("P_PRINTED_BY", " Caissier(e) :: " + " " + OTUser.getStrFIRSTNAME() + "  " + OTUser.getStrLASTNAME());
    parameters.put("P_AUTRE_DESC", "  ");
    parameters.put("P_REFERENCE", oTPreenregistrement.getLgPREENREGISTREMENTID());
    parameters.put("P_dt_CREATED", obllBase.getKey().DateToString(new Date(), obllBase.getKey().formatterMysql));
    parameters.put("P_BARE_CODE", jdom.barecode_file + "" + fileBarecode + ".jpg");
    parameters.put("P_H_CLT_INFOS", P_H_CLT_INFOS);

    new logger().OCategory.info("P_REFERENCE" + lg_PREENREGISTREMENT_ID);
    new logger().OCategory.info("getStrADRESSSEPOSTALE" + oTOfficine.getStrADRESSSEPOSTALE());
    
    
   // Map subparameters = new HashMap();
   // subparameters.put("P_CUST_PART", P_CUST_PART);
   // OreportManager_subreport.BuildReport(subparameters, Ojconnexion);

    
    
    OreportManager.BuildReport(parameters, Ojconnexion);
    
    
    ObllBase.setKey(new date());
    ObllBase.setOTranslate(OTranslate);
    ObllBase.setOTUser(OTUser);

    Ojconnexion.CloseConnexion();

    response.sendRedirect("../../../data/reports/pdf/" + "ticket_" + report_generate_file);

%>


