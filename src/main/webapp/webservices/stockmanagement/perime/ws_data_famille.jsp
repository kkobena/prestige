<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TBonLivraisonDetail"%>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();

    date key = new date();
   
    List<TBonLivraisonDetail> lstTBonLivraisonDetail = new ArrayList<TBonLivraisonDetail>();


%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data etat stock ");
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

<%
    String lg_FAMILLE_ID = "%%", search_value = "", lg_GROSSISTE_ID = "%%", Odate = "", dt_DEBUT = "", dt_FIN = "";
    
    int nbreJour = 0;
    Date dtFin, dtDEBUT;
    
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }
    if (request.getParameter("lg_GROSSISTE_ID") != null && request.getParameter("lg_GROSSISTE_ID") != "") {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }

    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    
     if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dtDEBUT = new Date();
        Odate = key.DateToString(dtDEBUT, key.formatterMysqlShort2);
    } else {
        dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
        Odate = key.DateToString(dtDEBUT, key.formatterMysqlShort2); 
    }
      dtDEBUT = key.getDate(Odate, "00:00");
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
        Odate = key.DateToString(dtFin, key.formatterMysqlShort2);
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
        Odate = key.DateToString(dtFin, key.formatterMysqlShort2);
    }
    dtFin = key.getDate(Odate, "23:59");
    
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  " + dt_FIN + "  dt_DEBUT  " + dt_DEBUT + " lg_FAMILLE_ID " + lg_FAMILLE_ID + "dtDEBUT:" + dtDEBUT+ " dtFin:" + dtFin);
    

    OdataManager.initEntityManager();
    StockManager OStockManager = new StockManager(OdataManager);
    
    
   lstTBonLivraisonDetail = OStockManager.getListeLivraisonByProductAndGrossiste(search_value, dtDEBUT, dtFin, lg_FAMILLE_ID, lg_GROSSISTE_ID);
    


%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTBonLivraisonDetail.size()) {
            DATA_PER_PAGE = lstTBonLivraisonDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTBonLivraisonDetail.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTBonLivraisonDetail.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("dt_PEREMPTION", date.DateToString(lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getDtDATELIVRAISON(), date.formatterShort));
        json.put("int_NUM_LOT", lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getStrREFLIVRAISON());
        json.put("lg_GROSSISTE_ID", lstTBonLivraisonDetail.get(i).getLgGROSSISTEID().getStrLIBELLE());
        json.put("int_NUMBER", lstTBonLivraisonDetail.get(i).getIntQTECMDE());
        json.put("lg_FAMILLE_ID", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("str_NAME", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("lg_ZONE_GEO_ID", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getLgZONEGEOID().getStrLIBELLEE());
        json.put("int_CIP", lstTBonLivraisonDetail.get(i).getLgFAMILLEID().getIntCIP());       
        json.put("int_STOCK_REAPROVISONEMENT", lstTBonLivraisonDetail.get(i).getIntQTERECUE());
        json.put("int_VALUE1", lstTBonLivraisonDetail.get(i).getIntQTEUG());
        json.put("int_VALUE2", conversion.AmountFormat(lstTBonLivraisonDetail.get(i).getIntPAF(), '.'));
          json.put("dt_ENTREE", date.DateToString(lstTBonLivraisonDetail.get(i).getLgBONLIVRAISONID().getDtUPDATED(), date.formatterShort));
        arrayObj.put(json);

    }
    new logger().OCategory.info(arrayObj.toString());

%>

<%= arrayObj.toString()%>