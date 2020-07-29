<%@page import="bll.mouvementprice.MouvementPrice"%>
<%@page import="dal.TMouvementprice"%>
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
    List<TMouvementprice> lstTMouvementprice = new ArrayList<TMouvementprice>();
    TMouvementprice OTMouvementprice = null;

%>

<!-- logic de gestion des page -->
<%    int DATA_PER_PAGE = toolkits.utils.jdom.int_size_pagination, count = 0, pages_curr = 0;
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

    String str_Date_Debut = "", str_Date_Fin = "", OdateDebut = "", lg_FAMILLE_ID = "%%", str_ACTION = "%%", OdateFin = "", search_value = "", lg_USER_ID = "%%";
    Date dt_Date_Debut, dt_Date_Fin;

    OdataManager.initEntityManager();
  
    new logger().OCategory.info("dans ws data preenregistemnt----");

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }
    if (request.getParameter("str_ACTION") != null && !request.getParameter("str_ACTION").equalsIgnoreCase("")) {
        str_ACTION = request.getParameter("str_ACTION");
        new logger().OCategory.info("str_ACTION " + str_ACTION);
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

    MouvementPrice OMouvementPrice = new MouvementPrice(OdataManager, OTUser);
    lstTMouvementprice = OMouvementPrice.listPrixModifies(search_value, dt_Date_Debut, dt_Date_Fin, lg_USER_ID, lg_FAMILLE_ID, str_ACTION);
    JSONArray arrayObj = new JSONArray();
   

%>



<!-- fin logic de gestion des page -->
<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTMouvementprice.size()) {
            DATA_PER_PAGE = lstTMouvementprice.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTMouvementprice.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>
<%    //lstTMouvementprice.size()
    for (int i = pgInt; i < pgInt_Last; i++) {

        JSONObject json = new JSONObject();
        json.put("lg_FAMILLEARTICLE_ID", lstTMouvementprice.get(i).getLgMOUVEMENTPRICEID());
        json.put("str_DESCRIPTION", lstTMouvementprice.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("lg_AJUSTEMENTDETAIL_ID", lstTMouvementprice.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTMouvementprice.get(i).getLgUSERID().getStrLASTNAME());
        json.put("int_CIP", lstTMouvementprice.get(i).getLgFAMILLEID().getIntCIP());
        json.put("MOUVEMENT", lstTMouvementprice.get(i).getStrACTION());
        json.put("dt_CREATED", date.DateToString(lstTMouvementprice.get(i).getDtCREATED(), key.formatterShort));
        json.put("str_STATUT", lstTMouvementprice.get(i).getStrSTATUT());
        json.put("str_DESCRIPTION_PLUS", lstTMouvementprice.get(i).getStrREF());

        /*json.put("int_MOY_VENTE", conversion.AmountFormat(lstTMouvementprice.get(i).getIntPRICEOLD(), '.'));
        json.put("int_NUMBER", conversion.AmountFormat(lstTMouvementprice.get(i).getIntPRICENEW(), '.'));*/
         json.put("int_PRICE_DETAIL", lstTMouvementprice.get(i).getIntPRICEOLD());
        json.put("int_QTEDETAIL", lstTMouvementprice.get(i).getIntPRICENEW());
        json.put("lg_ETAT_ARTICLE_ID", date.getTime(lstTMouvementprice.get(i).getDtCREATED()));

        json.put("dt_CREATED", date.formatterShort.format(lstTMouvementprice.get(i).getDtCREATED()));
        //json.put("int_QTE_SORTIE", conversion.AmountFormat(lstTMouvementprice.get(i).getIntECART(), '.'));
        json.put("int_NUMBERDETAIL", lstTMouvementprice.get(i).getIntECART());
        

        // new logger().OCategory.info(" Total_vente  ====== "+Total_vente);
        //json.put("int_PRICE_FORMAT", conversion.AmountFormat(lstTMouvementprice.get(i).getIntPRICE(), '.'));
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTMouvementprice.size() + " \",\"results\":" + arrayObj.toString() + "})";

    System.out.println(result);
%>

<%= result%>
