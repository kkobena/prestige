<%@page import="dal.TFacture"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TFamille"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TSnapShopDalySortieFamille"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TTypeRisque"  %>
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


<%
   
    dataManager OdataManager = new dataManager();

    date key = new date();

    json Ojson = new json();

    TUser OTUser;
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data evaluation vente moyenne ");

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

<%
    String lg_FAMILLE_ID = "%%", search_value = "";
    String lg_USER_ID = "%%", lg_PREENREGISTREMENT_ID = "%%", lg_EMPLACEMENT_ID = "%%", lg_COMPTE_CLIENT_ID = "%%", lg_TIERS_PAYANT_ID = "%%";
    String dt_DEBUT = "", dt_FIN = "";
    Date dtFin;
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
    if (request.getParameter("lg_EMPLACEMENT_ID") != null) {
        lg_EMPLACEMENT_ID = request.getParameter("lg_EMPLACEMENT_ID");
        new logger().OCategory.info("lg_EMPLACEMENT_ID " + lg_EMPLACEMENT_ID);
    }

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);
    }

    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID);
    }

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if ("".equals(dt_DEBUT) || dt_DEBUT == null) {
        dt_DEBUT = "2015-04-20";

    }
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
    }
    Date dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);

    

    OdataManager.initEntityManager();

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    long nbdossierhalfyear=0l,montanthalfyear=0l;
    
   List<TFacture> lsthalfyearinvoices = OPreenregistrement.getPreviousHalfYearBalanceInvoice(search_value, lg_USER_ID, lg_TIERS_PAYANT_ID);
    
   for(TFacture oFacture:lsthalfyearinvoices){
       nbdossierhalfyear+=   OPreenregistrement.getNombreDossierImpayeParFacture(oFacture.getLgFACTUREID());
       montanthalfyear+=oFacture.getDblMONTANTRESTANT().longValue();
   }
    JSONArray arrayObj = new JSONArray();
 JSONObject json1 = new JSONObject();
  JSONObject obj = OPreenregistrement.getPreviousHalfYearBalance();

    json1.put("str_PERIOD", "<= " + date.formatterShort.format(date.getPreviousHalfYearIncludeCurrentMonth(new Date())));
    json1.put("int_NUMBER_PRODUCT",lsthalfyearinvoices.size() );
    json1.put("int_NUMBER_TRANSACTION", nbdossierhalfyear);
    json1.put("int_MONTANT", montanthalfyear);
    json1.put("int_MONTANTNONFACTURE7",obj.getLong("MONTANT") );
     json1.put("int_NBDOSSIER7",obj.getLong("NOMBRE") );
    json1.put("dt_DEBUT", "");
    json1.put("dt_FIN", "");
    
    

    arrayObj.put(json1);
    for (int i = 5; i > -1; i--) {
List<TFacture> list=OPreenregistrement.getBalanceInvoice(search_value, date.getPreviousMonth(i), lg_USER_ID, lg_TIERS_PAYANT_ID);
obj=OPreenregistrement.getBalanceInvoice(date.getPreviousMonth(i));

JSONObject json = new JSONObject();

        json.put("str_PERIOD", date.DateToString(key.getFirstDayofSomeMonth(Integer.parseInt(key.getoMois(new Date())) - (i + Integer.parseInt(key.getoMois(new Date())))), date.formatterShort) + " au "
                + date.DateToString(key.getLastDayofSomeMonth(Integer.parseInt(key.getoMois(new Date())) - (i + Integer.parseInt(key.getoMois(new Date())))), date.formatterShort));
        json.put("int_NUMBER_PRODUCT",list.size() );
        long nbdossier=0l,montant=0l;
         for(TFacture oFacture:list){
       nbdossier+=  OPreenregistrement.getNombreDossierImpayeParFacture(oFacture.getLgFACTUREID())    ;
       montant+=oFacture.getDblMONTANTRESTANT().longValue();
   }
    json.put("int_MONTANTNONFACTURE7",obj.getLong("MONTANT") );
     json.put("int_NBDOSSIER7",obj.getLong("NOMBRE") );     
         
         
        json.put("int_NUMBER_TRANSACTION",nbdossier );
        json.put("int_MONTANT", montant);
        json.put("dt_DEBUT", date.DateToString(key.getFirstDayofSomeMonth(Integer.parseInt(key.getoMois(new Date())) - (i + Integer.parseInt(key.getoMois(new Date())))), date.formatterMysqlShort));
        json.put("dt_FIN", date.DateToString(key.getLastDayofSomeMonth(Integer.parseInt(key.getoMois(new Date())) - (i + Integer.parseInt(key.getoMois(new Date())))), date.formatterMysqlShort));
        arrayObj.put(json);

    }
   
    String result = "({\"total\":\"" + 7 + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>