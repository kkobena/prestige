<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TDossierFacture"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="bll.facture.factureManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>          
<%@page import="org.json.JSONArray"  %> 
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_FACTURE_ID = "%%", lg_customer_id = "%%", P_KEY = "%%", lg_dossier_id = "%%", lg_MODULE_ID = "%%", str_VALUE = "%%", str_Status = "%%";
    Integer int_PRIORITY;
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TFactureDetail> lstTFactureDetail = new ArrayList<dal.TFactureDetail>();

    bllBase ObllBase = new bllBase();
    TCompteClientTiersPayant OTCompteClientTiersPayant = null;

    

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data by dossiers");
%>


<!-- logic de gestion des page -->
<%
    String action = request.getParameter("action"); //get parameter ?action=
    int pageAsInt = 0;

    try {
        if ((action != null) && action.equals("filltable")) {
        } else {

            String p = request.getParameter("start"); // get paramerer ?page=

            if (p != null) {
                int int_page = new Integer(p).intValue();
                int_page = (int_page / DATA_PER_PAGE) + 1;
                p = new Integer(int_page).toString();

                // Strip quotation marks
                StringBuffer buffer = new StringBuffer();
                for (int index = 0; index < p.length(); index++) {
                    char c = p.charAt(index);
                    if (c != '\\') {
                        buffer.append(c);
                    }
                }
                p = buffer.toString();
                Integer intTemp = new Integer(p);

                pageAsInt = intTemp.intValue();

            } else {
                pageAsInt = 1;
            }

        }
    } catch (Exception E) {
    }


%>
<!-- fin logic de gestion des page -->

<%    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    //new logger().OCategory.info("lg_FACTURE_ID " + request.getParameter("lg_FACTURE_ID"));
    if (request.getParameter("lg_customer_id") != null) {
        lg_customer_id = "%" + request.getParameter("lg_customer_id").toString() + "%";
        new logger().OCategory.info("lg_customer_id " + request.getParameter("lg_customer_id"));
    } else {
        lg_customer_id = "%%";
    }

    if (request.getParameter("lg_dossier_id") != null) {
        lg_dossier_id = "%" + request.getParameter("lg_dossier_id").toString() + "%";
        new logger().OCategory.info("lg_dossier_id " + request.getParameter("lg_dossier_id"));
    } else {
        lg_dossier_id = "%%";
    }

    //lg_FACTURE_ID = "57132064627421608451";
    OdataManager.initEntityManager();
    lstTFactureDetail = OdataManager.getEm().createQuery("SELECT t FROM TFactureDetail t WHERE t.lgFACTUREID.strCUSTOMER LIKE ?1   AND t.strREFDESCRIPTION LIKE ?2  AND (t.strSTATUT LIKE ?3 OR t.strSTATUT LIKE ?4)")
            .setParameter(1, lg_customer_id)
            .setParameter(2, lg_dossier_id)
            .setParameter(3, commonparameter.statut_enable)
            .setParameter(4, commonparameter.statut_is_Process)
            .getResultList();
    new logger().OCategory.info(lstTFactureDetail.size());

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTFactureDetail.size()) {
            DATA_PER_PAGE = lstTFactureDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTFactureDetail.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    OTUser = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());

    factureManagement OfactureManagement = new factureManagement(OdataManager, OTUser);

    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTFactureDetail.get(i));
        } catch (Exception er) {
        }
        new logger().OCategory.info("getStrREF " + lstTFactureDetail.get(i).getStrREF());

        //  TPreenregistrementCompteClientTiersPayent OTPreenregistrementCompteClientTiersPayent = OfactureManagement.getCustomer(lstTFactureDetail.get(i).getStrREF());
        // TPreenregistrementCompteClientTiersPayent OTPreenregistrementCompteClientTiersPayent = new TPreenregistrementCompteClientTiersPayent();
        TPreenregistrementCompteClientTiersPayent OTPreenregistrementCompteClientTiersPayent = OfactureManagement.GetInfoTierspayant(lstTFactureDetail.get(i).getStrREF());

        //  String str_NAMECUSTOMER = OfactureManagement.getNameCustomer(lstTFactureDetail.get(i).getLgTYPEFACTUREID().getLgTYPEFACTUREID(), lstTFactureDetail.get(i).());
        JSONObject json = new JSONObject();

        json.put("lg_DOSSIER_FACTURE_ID", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());
        json.put("str_NOM", OTPreenregistrementCompteClientTiersPayent.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME() + " " + OTPreenregistrementCompteClientTiersPayent.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());
        json.put("str_PRENOM", OTPreenregistrementCompteClientTiersPayent.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());
        json.put("str_SECURITE_SOCIAL", OTPreenregistrementCompteClientTiersPayent.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrNUMEROSECURITESOCIAL());
        json.put("str_NUM_DOSSIER", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getStrREFBON());
        //json.put("str_CUSTOMER", OTPreenregistrementCompteClientTiersPayent.);
        json.put("dt_CREATED", key.DateToString(OTPreenregistrementCompteClientTiersPayent.getDtCREATED(), key.formatterShort));
        json.put("dbl_MONTANT", lstTFactureDetail.get(i).getDblMONTANTRESTANT());

        arrayObj.put(json);
        //   new logger().OCategory.info("getStrREF " + OTDossierFacture.getStrNUMDOSSIER());

    }
    String result = "({\"total\":\"" + lstTFactureDetail.size() + "\",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>