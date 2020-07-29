<%@page import="com.asc.prestige2.business.cash.concrete.PrestigeCashService"%>
<%@page import="com.asc.prestige2.business.cash.CashService"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.reportventeManagement.Reportvente"%>
<%@page import="dal.TResumeCaisse"%>
<%@page import="dal.TBilletage"%>
<%@page import="bll.bllBase"%>
<%@page import="bll.teller.caisseManagement"%>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TBilletage OTBilletage = null;
    date key = new date();
    TUser OTUser = null;

%>

<%    int DATA_PER_PAGE = 25, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data gestion caisse");
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

<%    List<TResumeCaisse> lstTResumeCaisse = new ArrayList<TResumeCaisse>();
   // List<TBilletage> lstTBilletage = new ArrayList<TBilletage>();
    boolean btn_annulation_cloture_caisse = false;
    double int_amount_billetage = 0.0, int_amount_ecart = 0.0, int_amount_annule = 0.0;

    String lg_USER_ID = "%%", str_Date_Debut = "", str_Date_Fin = "", OdateDebut = "", OdateFin = "";
    Date dt_Date_Debut, dt_Date_Fin;

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }
    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }
    if (request.getParameter("lg_USER_ID") != null && !request.getParameter("lg_USER_ID").equalsIgnoreCase("")) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID :" + lg_USER_ID);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    /*Date_debut = key.GetDateNowForSearch(-3);
     Date_Fin = key.GetDateNowForSearch(1);

     dt_Date_Fin = key.stringToDate(Date_Fin, key.formatterShort);
     dt_Date_debut = key.stringToDate(Date_debut, key.formatterShort);*/
    if (str_Date_Fin.equalsIgnoreCase("") || str_Date_Fin == null) {
        dt_Date_Fin = new Date();
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    } else {
        dt_Date_Fin = key.stringToDate(str_Date_Fin, key.formatterMysqlShort);
        OdateFin = key.DateToString(dt_Date_Fin, key.formatterMysqlShort2);
    }
    dt_Date_Fin = key.getDate(OdateFin, "23:59");
    new logger().OCategory.info("dt_Date_Fin *** " + dt_Date_Fin + " OdateFin *** " + OdateFin);
    if (str_Date_Debut.equalsIgnoreCase("") || str_Date_Debut == null) {
        dt_Date_Debut = key.GetNewDate(dt_Date_Fin, -7);
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);
    } else {
        dt_Date_Debut = key.stringToDate(str_Date_Debut, key.formatterMysqlShort);
        OdateDebut = key.DateToString(dt_Date_Debut, key.formatterMysqlShort2);
    }
    dt_Date_Debut = key.getDate(OdateDebut, "00:00");
    new logger().OCategory.info("dt_Date_Debut ---- " + dt_Date_Debut + " OdateDebut ---- " + OdateDebut);

    /*lstTResumeCaisse.clear();
     lstTBilletage.clear();*/
    Reportvente OReportvente = new Reportvente(OdataManager, OTUser);
    privilege Oprivilege = new privilege(OdataManager, OTUser);
    btn_annulation_cloture_caisse = Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_BT_ANNULER_CLOTURE_CAISSE);

    lstTResumeCaisse = OReportvente.listeTResumeCaisseByUser(dt_Date_Debut, dt_Date_Fin, lg_USER_ID);
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTResumeCaisse.size()) {
            DATA_PER_PAGE = lstTResumeCaisse.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTResumeCaisse.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    //
    CashService service = new PrestigeCashService(OdataManager, OTUser);

    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        
         int_amount_billetage = 0.0;
         int_amount_ecart = 0.0;
         int_amount_annule = service.getMontantAnnule(lstTResumeCaisse.get(i).getLgUSERID().getLgUSERID(), date.DateToString(lstTResumeCaisse.get(i).getDtCREATED(), date.formatterMysqlShort), date.DateToString(lstTResumeCaisse.get(i).getDtUPDATED(), date.formatterMysqlShort));
        try {
            OTBilletage = OReportvente.getBilletageByCaisse(lstTResumeCaisse.get(i).getLdCAISSEID(), lg_USER_ID);
            int_amount_billetage = Math.abs(OTBilletage.getIntAMOUNT());
           // int_amount_ecart = int_amount_billetage - (Math.abs(lstTResumeCaisse.get(i).getIntSOLDESOIR())); // a decommenter en cas de probleme 21/02/2017
            int_amount_ecart = int_amount_billetage - (Math.abs(lstTResumeCaisse.get(i).getIntSOLDESOIR()) - int_amount_annule); // code ajouté 21/02/2017
        } catch (Exception e) {
            int_amount_billetage = 0;
            int_amount_ecart = 0;
        }

        // String Role = "";
        String dt_fermeture = "";
        if (lstTResumeCaisse.get(i).getDtUPDATED() == null) {
            dt_fermeture = "PAS ENCORE FERMEE";
        } else {
            dt_fermeture = date.DateToString(lstTResumeCaisse.get(i).getDtUPDATED(), date.formatterOrange);
        }
        JSONObject json = new JSONObject();

        json.put("lg_CAISSE_ID", lstTResumeCaisse.get(i).getLdCAISSEID());
        json.put("lg_USER_ID", lstTResumeCaisse.get(i).getLgUSERID().getLgUSERID());
        // json.put("str_REF", lstTResumeCaisse.get(i).getLdCAISSEID());
        json.put("str_NAME_USER", lstTResumeCaisse.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTResumeCaisse.get(i).getLgUSERID().getStrLASTNAME());
        json.put("str_STATUT", oTranslate.getValue(lstTResumeCaisse.get(i).getStrSTATUT()));
        json.put("etat", lstTResumeCaisse.get(i).getStrSTATUT());
        json.put("int_SOLDE_MATIN", lstTResumeCaisse.get(i).getIntSOLDEMATIN());
        json.put("int_SOLDE_SOIR", lstTResumeCaisse.get(i).getIntSOLDESOIR());
        json.put("int_SOLDE_SOIR_STRING", conversion.AmountFormat(lstTResumeCaisse.get(i).getIntSOLDESOIR(), '.'));
        if (lstTResumeCaisse.get(i).getStrSTATUT().equalsIgnoreCase(commonparameter.statut_is_Using)) {
            json.put("int_SOLDE", lstTResumeCaisse.get(i).getIntSOLDESOIR());
            json.put("int_SOLDE_STRING", conversion.AmountFormat(lstTResumeCaisse.get(i).getIntSOLDESOIR(), '.'));

        } else {
            json.put("int_SOLDE", lstTResumeCaisse.get(i).getIntSOLDESOIR() - lstTResumeCaisse.get(i).getIntSOLDEMATIN());
            json.put("int_SOLDE_STRING", conversion.AmountFormat(lstTResumeCaisse.get(i).getIntSOLDESOIR() - lstTResumeCaisse.get(i).getIntSOLDEMATIN(), '.'));
        }

        json.put("dt_CREATED", date.DateToString(lstTResumeCaisse.get(i).getDtCREATED(), date.formatterOrange));
        json.put("dt_UPDATED", dt_fermeture);
        json.put("int_AMOUNT_BILLETAGE", Math.abs(int_amount_billetage));
        json.put("int_AMOUNT_ECART", int_amount_ecart);
        json.put("int_AMOUNT_ECART_BIS", conversion.AmountFormat((int) int_amount_ecart, '.'));

        json.put("int_AMOUNT_ANNULE", int_amount_annule);
        json.put("btn_annulation", btn_annulation_cloture_caisse);

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTResumeCaisse.size() + " \",\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>