<%@page import="dal.TTypeStockFamille"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TFamille"  %>
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

    Integer int_PRICE;
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TFamille> lstTFamille = new ArrayList<dal.TFamille>();

    Integer int_search_cip;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data famille ");
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

<%    String lg_FAMILLE_ID = "%%", lg_GROUPE_FAMILLE_ID = "%%",lg_TYPE_STOCK_ID = "1", str_NAME = "%%", str_DESCRIPTION = "%%", str_TYPE_TRANSACTION = "%%";
    if (request.getParameter("search_value") != null) {
        //Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        Os_Search_poste.setOvalue(request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    if (request.getParameter("search_cip") != null) {
        Os_Search_poste.setOdata("%" + request.getParameter("search_cip") + "%");
        new logger().OCategory.info("search_cip " + request.getParameter("search_cip"));
    } else {
        Os_Search_poste.setOdata("%%");

    }

    if (request.getParameter("str_TYPE_TRANSACTION") != null) {
        str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
        new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
    }
if (request.getParameter("lg_TYPE_STOCK_ID") != null) {
        lg_TYPE_STOCK_ID = request.getParameter("lg_TYPE_STOCK_ID");
        new logger().OCategory.info("lg_TYPE_STOCK_ID " + lg_TYPE_STOCK_ID);
    }
    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    new logger().OCategory.info("search_cip  = " + Os_Search_poste.getOdata());
    if (request.getParameter("lg_FAMILLE_ID") != null) {
        if (request.getParameter("lg_FAMILLE_ID").toString().equals("ALL")) {
            lg_FAMILLE_ID = "%%";
        } else {
            lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID").toString();
        }

    }

    String search_value = Os_Search_poste.getOvalue();
    String search_cip_value = Os_Search_poste.getOdata();
    new logger().OCategory.info("search_value    " + search_value);

    new logger().OCategory.info("search_cip_value    " + search_cip_value);

    OdataManager.initEntityManager();
 TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
   StockManager OStockManager = new StockManager(OdataManager, OTUser);
    familleManagement OfamilleManagement = new familleManagement(OdataManager);
    //OdataManager.BeginTransaction();

    if (str_TYPE_TRANSACTION.equalsIgnoreCase("%%") || str_TYPE_TRANSACTION.equalsIgnoreCase("ALL")) {
        lstTFamille = OdataManager.getEm().createQuery("SELECT t FROM TFamille t WHERE t.lgFAMILLEID LIKE ?1 AND (t.strDESCRIPTION LIKE ?2 OR t.intCIP LIKE ?3) AND t.strSTATUT LIKE ?4 ORDER BY t.strDESCRIPTION ASC ").
                setParameter(1, lg_FAMILLE_ID)
                .setParameter(2, search_value)
                .setParameter(3, search_value) 
                .setParameter(4, commonparameter.statut_disable)
                .getResultList();

    } 
    // OdataManager.CloseTransaction();
    new logger().OCategory.info("lstTFamille dans le ws " + lstTFamille.size());

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTFamille.size()) {
            DATA_PER_PAGE = lstTFamille.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTFamille.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();

    for (int i = pgInt; i < pgInt_Last; i++) {

        String Role = "";

        JSONObject json = new JSONObject();
        System.out.println("178");
        json.put("lg_FAMILLE_ID", lstTFamille.get(i).getLgFAMILLEID());
        try {
            json.put("lg_FAMILLEARTICLE_ID", lstTFamille.get(i).getLgFAMILLEARTICLEID().getStrLIBELLE());
        } catch (Exception e) {

        }
        
        try {
            json.put("lg_TYPEETIQUETTE_ID", lstTFamille.get(i).getLgTYPEETIQUETTEID().getStrDESCRIPTION());
        } catch (Exception e) {
        }
        json.put("str_NAME", lstTFamille.get(i).getStrNAME());
        json.put("str_DESCRIPTION", lstTFamille.get(i).getStrDESCRIPTION());
        json.put("int_PRICE", lstTFamille.get(i).getIntPRICE());
        try {
            json.put("lg_GROSSISTE_ID", lstTFamille.get(i).getLgGROSSISTEID().getStrLIBELLE());
        } catch (Exception e) {

        }
        json.put("int_CIP", lstTFamille.get(i).getIntCIP());
        json.put("int_PAF", lstTFamille.get(i).getIntPAF());
        json.put("int_PAT", lstTFamille.get(i).getIntPAT());
     
        try {
            json.put("lg_ZONE_GEO_ID", lstTFamille.get(i).getLgZONEGEOID().getStrLIBELLEE());
        } catch (Exception e) {

        }
        
         int int_NUMBER = 0, int_NUMBER_AVAILABLE = 0;
       try {
            
            /*TFamilleStock OTFamilleStock = new tellerManagement(OdataManager).getTProductItemStock(lstTFamille.get(i).getLgFAMILLEID());
            int_NUMBER = OTFamilleStock.getIntNUMBER();
            int_NUMBER_AVAILABLE = OTFamilleStock.getIntNUMBERAVAILABLE();*/
            TTypeStockFamille OTTypeStockFamille = OStockManager.getTTypeStockFamilleByTypestock(lg_TYPE_STOCK_ID, lstTFamille.get(i).getLgFAMILLEID());
            int_NUMBER_AVAILABLE = OTTypeStockFamille.getIntNUMBER();
            int_NUMBER = int_NUMBER_AVAILABLE;
        }catch(Exception e) {
            
        }
        json.put("int_NUMBER", int_NUMBER);
        json.put("int_NUMBER_AVAILABLE", int_NUMBER_AVAILABLE);
     

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTFamille.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>