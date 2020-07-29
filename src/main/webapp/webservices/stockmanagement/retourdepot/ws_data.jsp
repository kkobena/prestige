<%@page import="dal.TEmplacement"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="dal.TRetourdepotdetail"%>
<%@page import="bll.stockManagement.DepotManager"%>
<%@page import="bll.common.Parameter"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRetourdepot"  %>
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


<%
    dataManager OdataManager = new dataManager();
    date key = new date();
    List<TRetourdepot> lstTRetourdepot = new ArrayList<TRetourdepot>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans webservices retour depot");
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

<%    String lg_RETOURDEPOT_ID = "%%", search_value = "", lg_USER_ID = "%%";
    String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin;
    Date dtDEBUT, dtFin;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    privilege Oprivilege = new privilege(OdataManager, OTUser);
    boolean BTNDELETE = Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_BT_DELETE);
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_RETOURDEPOT_ID") != null) {
        lg_RETOURDEPOT_ID = request.getParameter("lg_RETOURDEPOT_ID");
        new logger().OCategory.info("lg_RETOURDEPOT_ID " + lg_RETOURDEPOT_ID);
    }

    if (request.getParameter("lg_USER_ID") != null && !request.getParameter("lg_USER_ID").equals("")) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
        OdateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
        OdateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
    }

    dtFin = key.getDate(OdateFin, "23:59");
    //OdateFin = key.DateToString(dtFin, key.formatterMysql);
    new logger().OCategory.info("dtFin *** " + dtFin + " OdateFin *** " + OdateFin);
    if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
       // dtDEBUT = date.GetDebutMois();
        dtDEBUT = new Date();
        OdateDebut = key.DateToString(dtDEBUT, key.formatterMysqlShort2);
    } else {
        dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
        OdateDebut = key.DateToString(dtDEBUT, key.formatterMysqlShort2);

    }
    dtDEBUT = key.getDate(OdateDebut, "00:00");
    //OdateDebut = key.DateToString(dtDEBUT, key.formatterMysql);

    new logger().OCategory.info("dtDEBUT ---- " + dtDEBUT + " OdateDebut ---- " + OdateDebut);

    OdataManager.initEntityManager();
    DepotManager ODepotManager = new DepotManager(OdataManager, OTUser);
    lstTRetourdepot = ODepotManager.getAllTRetourdepot(search_value, lg_RETOURDEPOT_ID, dtDEBUT, dtFin,OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    TEmplacement OEmplacement=null ;
%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTRetourdepot.size()) {
            DATA_PER_PAGE = lstTRetourdepot.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTRetourdepot.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();

    for (int i = pgInt; i < pgInt_Last; i++) {

        List<TRetourdepotdetail> lstTRetourdepotdetail = new ArrayList<TRetourdepotdetail>();
        lstTRetourdepotdetail = ODepotManager.getTRetourdepotdetail("", lstTRetourdepot.get(i).getLgRETOURDEPOTID(), lstTRetourdepot.get(i).getStrSTATUT());

        int nb = 0;
        String str_Product = "";

        for (int k = 0; k < lstTRetourdepotdetail.size(); k++) {

            // str_Product = "<b>" + lstTRetourdepotdetail.get(k).getLgFAMILLEID().getIntCIP() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + lstTRetourdepotdetail.get(k).getLgFAMILLEID().getStrNAME() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + new conversion().AmountFormat(lstTRetourdepotdetail.get(k).getLgFAMILLEID().getIntPRICE(), commonparameter.CHAR_SEPARATEUR_POINT) + " F CFA&nbsp;&nbsp;" + " :  (" + lstTRetourdepotdetail.get(k).getIntNUMBERRETURN() + ")</b><br> " + str_Product;
            str_Product = "<b><span style='display:inline-block;width: 7%;'>" + lstTRetourdepotdetail.get(k).getLgFAMILLEID().getIntCIP() + "</span><span style='display:inline-block;width: 25%;'>" + lstTRetourdepotdetail.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 15%;'>" + lstTRetourdepotdetail.get(k).getIntNUMBERRETURN() + "</span></b><br>" + str_Product;
            nb = nb + lstTRetourdepotdetail.get(k).getIntNUMBERRETURN();

        }
        try {
                OEmplacement=ODepotManager.getEmplacementBYRef(lstTRetourdepot.get(i).getPkey());
            } catch (Exception e) {
            }
        new logger().OCategory.info("str_Product   ----  " + str_Product);
          
        JSONObject json = new JSONObject();

        json.put("lg_RETOUR_FRS_ID", lstTRetourdepot.get(i).getLgRETOURDEPOTID());
        json.put("str_REF_RETOUR_FRS", lstTRetourdepot.get(i).getStrNAME());
        json.put("lg_EMPLACEMENT_ID",(OEmplacement!=null?OEmplacement.getStrNAME():"") );
        json.put("str_REPONSE_FRS", lstTRetourdepot.get(i).getStrDESCRIPTION());
        json.put("str_COMMENTAIRE", lstTRetourdepot.get(i).getStrCOMMENTAIRE());
        json.put("BTNDELETE", lstTRetourdepot.get(i).getBoolFLAG());
        json.put("lg_BON_LIVRAISON_ID", (OEmplacement!=null?OEmplacement.getStrNAME():""));
        json.put("lg_USER_ID", lstTRetourdepot.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTRetourdepot.get(i).getLgUSERID().getStrLASTNAME());
        json.put("int_LINE", lstTRetourdepotdetail.size());
        json.put("str_FAMILLE_ITEM", str_Product);
        json.put("bool_PENDING", lstTRetourdepot.get(i).getBoolPending()!=null?lstTRetourdepot.get(i).getBoolPending():false);
        json.put("int_TOTAL_PRODUCT", conversion.AmountFormat(ODepotManager.getTotalQuantityRetour(lstTRetourdepotdetail), '.'));
        json.put("int_TOTAL_AMOUNT", conversion.AmountFormat(ODepotManager.getTotalAmountRetour(lstTRetourdepotdetail).intValue(), '.'));
        json.put("str_STATUT", lstTRetourdepot.get(i).getStrSTATUT());
        json.put("lg_USER_ID", lstTRetourdepot.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTRetourdepot.get(i).getLgUSERID().getStrLASTNAME());
        json.put("dt_CREATED", key.DateToString(lstTRetourdepot.get(i).getDtUPDATED(), key.formatterShort));
        json.put("dt_UPDATED", key.DateToString(lstTRetourdepot.get(i).getDtUPDATED(), date.NomadicUiFormat_Time));
        json.put("bool_SAME_LOCATION", lstTRetourdepot.get(i).getLgEMPLACEMENTID().getBoolSAMELOCATION()!=null?lstTRetourdepot.get(i).getLgEMPLACEMENTID().getBoolSAMELOCATION():false);
       json.put("USEREMPLACEMENT", OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTRetourdepot.size() + " \" ,\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

    /*lstTRetourdepotdetail = null;
     lstTRetourdepot = null;
     OdataManager = null;*/
%>

<%= result%>