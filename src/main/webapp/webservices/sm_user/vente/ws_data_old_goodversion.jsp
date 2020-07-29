<%@page import="bll.common.Parameter"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="toolkits.utils.conversion"%>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
   
    date key = new date();
    List<TPreenregistrement> lstTPreenregistrement = new ArrayList<TPreenregistrement>();
    TPreenregistrementDetail OTPreenregistrementDetail = null;
    boolean BTN_ANNULATION = false;

%>

<!-- logic de gestion des page -->
<%
    int DATA_PER_PAGE = toolkits.utils.jdom.int_size_pagination, count = 0, pages_curr = 0;
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


<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
 
    String str_Date_Debut = "", str_Date_Fin = "", OdateDebut = "", OdateFin = "", search_value = "", lg_USER_ID = "%%", lg_PREENREGISTREMENT_ID = "%%", str_STATUT = commonparameter.statut_is_Closed;
    Date dt_Date_Debut, dt_Date_Fin;

    OdataManager.initEntityManager();
    OTUser = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());

    new logger().OCategory.info("dans ws data preenregistemnt");
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    } 
    
    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    } 
    
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + lg_PREENREGISTREMENT_ID);
    } 

    
    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }
    
    if (str_Date_Fin.equalsIgnoreCase("") || str_Date_Fin == null) {
        dt_Date_Fin = new Date();
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    } else {
        dt_Date_Fin = key.stringToDate(str_Date_Fin, key.formatterMysqlShort);
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    }

    dt_Date_Fin = key.getDate(OdateFin, "23:59");
    //dt_Date_Fin = key.GetNewDate(1);
    // OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysql);
    new logger().OCategory.info("dt_Date_Fin *** " + dt_Date_Fin + " OdateFin *** " + OdateFin);
    if (str_Date_Debut.equalsIgnoreCase("") || str_Date_Debut == null) {
        dt_Date_Debut = new Date();
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);
    } else {
        dt_Date_Debut = key.stringToDate(str_Date_Debut, key.formatterMysqlShort);
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);

    }
    dt_Date_Debut = key.getDate(OdateDebut, "00:00");
     new logger().OCategory.info("dt_Date_Debut *** " + dt_Date_Debut + " OdateDebut *** " + OdateDebut);
   
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    privilege Oprivilege = new privilege(OdataManager, OTUser);
    BTN_ANNULATION = Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_BT_ANNULER_VENTE);
    lstTPreenregistrement = OPreenregistrement.listTPreenregistrement(search_value, dt_Date_Debut, dt_Date_Fin, lg_USER_ID, lg_PREENREGISTREMENT_ID, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID(), str_STATUT);
    /* lstTPreenregistrement = OdataManager.getEm().createQuery("SELECT t FROM TPreenregistrement t WHERE t.lgPREENREGISTREMENTID LIKE ?1 AND t.strREF LIKE ?2 AND t.strSTATUT LIKE ?3 AND t.lgUSERID.lgEMPLACEMENTID.lgEMPLACEMENTID LIKE ?4 ORDER BY t.dtCREATED DESC")
     .setParameter(1, lg_PREENREGISTREMENT_ID)
     .setParameter(2, Os_Search_poste.getOvalue())
     .setParameter(3, commonparameter.statut_is_Closed)
     .setParameter(4, OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID())
     .getResultList();*/

    JSONArray arrayObj = new JSONArray();
    int Total_vente = 0;

%>



<!-- fin logic de gestion des page -->
<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTPreenregistrement.size()) {
            DATA_PER_PAGE = lstTPreenregistrement.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTPreenregistrement.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>
<%    //lstTPreenregistrement.size()
    for (int i = pgInt; i < pgInt_Last; i++) {

        try {
            OdataManager.getEm().refresh(lstTPreenregistrement.get(i));
        } catch (Exception er) {
        }

        List<dal.TPreenregistrementDetail> lstTPreenregistrementDetail = new Preenregistrement(OdataManager, OTUser).getTPreenregistrementDetail(lstTPreenregistrement.get(i).getLgPREENREGISTREMENTID(),"%%", commonparameter.statut_is_Closed);
        String str_Product = "";
        for (int k = 0; k < lstTPreenregistrementDetail.size(); k++) {
            OTPreenregistrementDetail = lstTPreenregistrementDetail.get(k);
            if (OTPreenregistrementDetail == null) {
                new logger().OCategory.info(" *** OTPreenregistrementDetail is null depuis ws data preenregistrement *** ");
            }
         //   int qte =  ((lstTPreenregistrement.get(i).getIntPRICE() >= 0) ? OTPreenregistrementDetail.getIntQUANTITY() : (-1 * OTPreenregistrementDetail.getIntQUANTITY()));
            str_Product = "<b>" + OTPreenregistrementDetail.getLgFAMILLEID().getIntCIP() + "  " + OTPreenregistrementDetail.getLgFAMILLEID().getStrNAME() + "   " + conversion.AmountFormat(OTPreenregistrementDetail.getLgFAMILLEID().getIntPRICE(), '.') + " F CFA " + " :  (" + ((lstTPreenregistrement.get(i).getIntPRICE() >= 0) ? OTPreenregistrementDetail.getIntQUANTITY() : (-1 * OTPreenregistrementDetail.getIntQUANTITY())) + ")</b><br> " + str_Product;
            //str_Product = "<b>" + lstTPreenregistrementDetail.get(k).getLgFAMILLEID().getStrNAME() + " :  (" + lstTPreenregistrementDetail.get(k).getIntQUANTITY() + ")</b><br> " + str_Product;

            //Total_vente = Total_vente + lstTPreenregistrementDetail.get(k).getIntPRICE();
        }

        JSONObject json = new JSONObject();
        json.put("lg_PREENREGISTREMENT_ID", lstTPreenregistrement.get(i).getLgPREENREGISTREMENTID());
        json.put("str_REF", lstTPreenregistrement.get(i).getStrREF());
        json.put("lg_USER_ID", lstTPreenregistrement.get(i).getLgUSERID().getStrFIRSTNAME());
        // json.put("int_PRICE",conversion.AmountFormat(lstTPreenregistrement.get(i).getIntPRICE(),'.') );
        json.put("int_PRICE", lstTPreenregistrement.get(i).getIntPRICE());
        json.put("dt_CREATED", date.DateToString(lstTPreenregistrement.get(i).getDtCREATED(), key.formatterShort));
        json.put("str_hour", date.DateToString(lstTPreenregistrement.get(i).getDtCREATED(), date.NomadicUiFormat_Time));
        json.put("str_STATUT", lstTPreenregistrement.get(i).getStrSTATUT());
        json.put("str_FAMILLE_ITEM", str_Product);
        json.put("b_IS_CANCEL", lstTPreenregistrement.get(i).getBISCANCEL().toString());
        
        json.put("lg_USER_VENDEUR_ID", lstTPreenregistrement.get(i).getLgUSERVENDEURID().getStrFIRSTNAME());
        json.put("lg_USER_CAISSIER_ID", lstTPreenregistrement.get(i).getLgUSERCAISSIERID().getStrFIRSTNAME());
        // new logger().OCategory.info(" Total_vente  ====== "+Total_vente);

        json.put("int_PRICE_FORMAT", conversion.AmountFormat(lstTPreenregistrement.get(i).getIntPRICE(), '.'));

        if (lstTPreenregistrement.get(i).getIntSENDTOSUGGESTION() == null) {
            lstTPreenregistrement.get(i).setIntSENDTOSUGGESTION(0);
        }

        if (OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS) && lstTPreenregistrement.get(i).getIntSENDTOSUGGESTION() == 0) {
            json.put("etat", commonparameter.PROCESS_SUCCESS);
        }
        json.put("int_SENDTOSUGGESTION", lstTPreenregistrement.get(i).getIntSENDTOSUGGESTION());
         json.put("BTN_ANNULATION", BTN_ANNULATION);
       // new logger().OCategory.info(" Total_vente  ====== "+Total_vente);

        // new logger().OCategory.info(" Total_vente  ====== "+Total_vente);
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTPreenregistrement.size() + " \",\"results\":" + arrayObj.toString() + "})";

    System.out.println(result);
%>

<%= result%>
