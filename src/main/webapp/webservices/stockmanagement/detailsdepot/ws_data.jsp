<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();
    List<TPreenregistrementDetail> lstTPreenregistrementDetail = new ArrayList<TPreenregistrementDetail>();
    Preenregistrement OPreenregistrement = null;
    TPreenregistrement OTPreenregistrement = null;

%>



<%    String lg_PREENREGISTREMENT_ID = "%%", lg_PREENREGISTREMENT_DETAIL_ID = "%%";
    String str_STATUT = commonparameter.statut_is_Process;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    OTUser = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());

    OPreenregistrement = new Preenregistrement(OdataManager, OTUser);

    try {
        OdataManager.getEm().refresh(OTUser);
    } catch (Exception er) {
    }

    new logger().OCategory.info("dans ws data details vente ");

    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    if (request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID") != null && request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID") != "") {
        lg_PREENREGISTREMENT_DETAIL_ID = request.getParameter("lg_PREENREGISTREMENT_DETAIL_ID").toString();
        new logger().OCategory.info("lg_PREENREGISTREMENT_DETAIL_ID  = " + lg_PREENREGISTREMENT_DETAIL_ID);
    }
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null && request.getParameter("lg_PREENREGISTREMENT_ID") != "") {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID").toString();
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID  " + lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT").toString();
        new logger().OCategory.info("str_STATUT  " + str_STATUT);
    }

    lstTPreenregistrementDetail = OdataManager.getEm().createQuery("SELECT t FROM TPreenregistrementDetail t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID LIKE ?1 AND t.lgFAMILLEID.strNAME LIKE ?2 AND t.strSTATUT LIKE ?3 ORDER BY t.dtCREATED DESC")
            .setParameter(1, lg_PREENREGISTREMENT_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, str_STATUT)
            .getResultList();
    OTPreenregistrement = OdataManager.getEm().find(TPreenregistrement.class, lg_PREENREGISTREMENT_ID);

    JSONArray arrayObj = new JSONArray();

    String str_MEDECIN = "";
    String lg_TYPE_VENTE_ID = "";
    int int_total_vente = 0, int_total_product = 0;
    Double dbl_total_remise = 0.0;
    for (int i = 0; i < lstTPreenregistrementDetail.size(); i++) {

        try {
            OdataManager.getEm().refresh(lstTPreenregistrementDetail.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();
        json.put("lg_PREENREGISTREMENT_DETAIL_ID", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTDETAILID());
        json.put("lg_PREENREGISTREMENT_ID", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
        json.put("str_REF", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getStrREF());
        json.put("lg_FAMILLE_ID", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        // json.put("int_FAMILLE_PRICE", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getIntPRICE()); ancien bon code
        json.put("int_FAMILLE_PRICE", lstTPreenregistrementDetail.get(i).getIntPRICEUNITAIR());
        json.put("str_FAMILLE_NAME", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getStrNAME());
        json.put("lg_USER_ID", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getLgUSERID().getStrFIRSTNAME());
        json.put("int_QUANTITY", lstTPreenregistrementDetail.get(i).getIntQUANTITY());
        json.put("int_S", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getIntS());
        json.put("int_T", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getIntT());
        json.put("int_QUANTITY_SERVED", lstTPreenregistrementDetail.get(i).getIntQUANTITYSERVED());
        json.put("int_PRICE_DETAIL", lstTPreenregistrementDetail.get(i).getIntPRICE());
        json.put("int_CIP", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getIntCIP());
        json.put("int_EAN13", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getIntEAN13());
        json.put("dt_CREATED", date.DateToString(lstTPreenregistrementDetail.get(i).getDtCREATED(), date.formatterMysql));
        json.put("str_STATUT", lstTPreenregistrementDetail.get(i).getStrSTATUT());
        try {
            json.put("str_MEDECIN", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getStrMEDECIN());
            json.put("lg_TYPE_VENTE_ID", OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID());

            str_MEDECIN = lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getStrMEDECIN();
            lg_TYPE_VENTE_ID = OTPreenregistrement.getLgTYPEVENTEID().getStrDESCRIPTION();

        } catch (Exception e) {

        }

        arrayObj.put(json);
    }

   // new logger().OCategory.info(" ****  calcul total vente **** ");
    //int_total_vente = OPreenregistrement.GetVenteTotal(lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
    int_total_vente = OPreenregistrement.GetVenteTotalwithRemise(lg_PREENREGISTREMENT_ID, str_STATUT);
    //new logger().OCategory.info("int_total_vente =  " + int_total_vente);

    dbl_total_remise = OPreenregistrement.GetAmountRemise(lg_PREENREGISTREMENT_ID, str_STATUT);
   // new logger().OCategory.info("dbl_total_remise =  " + dbl_total_remise);

    // new logger().OCategory.info(" ****  calcul total product **** ");
    //int_total_product = OPreenregistrement.GetProductTotal(lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
    int_total_product = OPreenregistrement.GetProductTotal(lg_PREENREGISTREMENT_ID, str_STATUT);
//    new logger().OCategory.info("int_total_product =  " + int_total_product);

    // String result = "({\"total\":\"" + lstTPreenregistrementDetail.size() + " \",\"results\":" + arrayObj.toString() + "})";
    String result = "({\"total\":\"" + lstTPreenregistrementDetail.size() + "\",\"results\":" + arrayObj.toString() + ",\"str_MEDECIN\":\"" + str_MEDECIN + "\",\"lg_TYPE_VENTE_ID\":\"" + lg_TYPE_VENTE_ID + "\",\"dbl_total_remise\":\"" + dbl_total_remise + " \",\"total_vente\":\"" + int_total_vente + "\",\"int_total_product\": \"" + int_total_product + "\"})";
    new logger().OCategory.info("result " + result);

    lstTPreenregistrementDetail = null;
    OdataManager = null;
    Os_Search_poste = null;
    Os_Search_poste_data = null;
    OTPreenregistrement = null;

%>

<%= result%>
