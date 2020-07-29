<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.commandeManagement.etatControle"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>

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
    
   
    date key = new date();

    // List<bll.commandeManagement.etatControle> lstetatControle = null;
    
    Date dt_CREATED, dt_UPDATED;
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data etat controle ");
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
    List<etatControle> lstetatControle = new ArrayList<etatControle>();
    String lg_SUGGESTION_ORDER_ID = "%%", lg_GROSSISTE_ID = "%%", str_STATUT = "%%",dateDeb,dateFin;
    int int_NUMBER, int_TOTAL_VENTE = 0, int_TOTAL_ACHAT = 0;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }    
    

    WarehouseManager oWarehouseManager = new WarehouseManager(OdataManager, OTUser);

    new logger().OCategory.info("lg_SUGGESTION_ORDER_ID   " + lg_SUGGESTION_ORDER_ID);
    
    if(request.getParameter("lg_GROSSISTE_ID") != null)
    {        
       // Date dt_dateDeb = key.stringToDate(date.getAnnee(date.GetDateNow_Date())+"01/01", key.formatterMysqlShort);
       // Date dt_dateFin = key.stringToDate(date.GetDateNow(), key.formatterMysqlShort);
        Date dt_dateDeb = key.stringToDate("2015/01/01", key.formatterMysqlShort);//DATE A MODIFIER
        Date dt_dateFin = key.stringToDate("2015/08/10", key.formatterMysqlShort);//DATE A MODIFIER
        new logger().OCategory.info("dt_dateDeb :" + dt_dateDeb+ "    dt_dateFin: "+dt_dateFin);
        
        new logger().OCategory.info("lg_GROSSISTE_ID :" + lg_GROSSISTE_ID);
        
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        lstetatControle = oWarehouseManager.EtatControleAchat(lg_GROSSISTE_ID,dt_dateDeb,dt_dateFin);
    }
    else
    {
        lstetatControle = oWarehouseManager.EtatControleAchat();
    }

    //lstetatControle = oWarehouseManager.EtatControleAchat();
    new logger().OCategory.info(lstetatControle.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstetatControle.size()) {
            DATA_PER_PAGE = lstetatControle.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstetatControle.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        

        JSONObject json = new JSONObject();

        json.put("str_LIBELLE", lstetatControle.get(i).str_LIBELLE);
        
        new logger().OCategory.info("str_LIBELLE   ----  " + lstetatControle.get(i).str_LIBELLE);

        json.put("int_CIP", lstetatControle.get(i).int_CIP);
        json.put("str_NAME", lstetatControle.get(i).str_NAME);

        json.put("str_ORDER_REF", lstetatControle.get(i).str_ORDER_REF);
        json.put("int_ORDER_PRICE", lstetatControle.get(i).int_ORDER_PRICE);
        
        json.put("str_BL_REF", lstetatControle.get(i).str_BL_REF);

        Date dt_DATE_LIVRAISON = lstetatControle.get(i).dt_DATE_LIVRAISON;
        if (dt_DATE_LIVRAISON != null) {
            json.put("dt_DATE_LIVRAISON", key.DateToString(dt_DATE_LIVRAISON, key.formatterShort));
        }
        
        json.put("int_BL_PRICE", lstetatControle.get(i).int_BL_PRICE);
        json.put("int_BL_NUMBER", lstetatControle.get(i).int_BL_NUMBER);
        
        dt_CREATED = lstetatControle.get(i).dt_CREATED;
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterShort));
        }
        
        json.put("int_NUMBER", lstetatControle.get(i).int_NUMBER);        
        json.put("int_QTE_CMD",lstetatControle.get(i).int_QTE_CMD);
        
        Date dt_date_entre_stock = lstetatControle.get(i).dt_ENTREE_STCK;
        if(dt_date_entre_stock != null)
        {
            json.put("dt_ENTREE_STCK", key.DateToString(dt_date_entre_stock,key.formatterShort));
        }
        
        

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstetatControle.size() + " \" ,\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>