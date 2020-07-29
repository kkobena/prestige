<%-- 
    Document   : ws_datan
    Created on : 27 nov. 2015, 12:04:50
    Author     : KKOFFI
--%>
<%@page import="bll.entity.EntityData"%>
<%@page import="dal.TReglement"%>
<%@page import="dal.TFacture"%>
<%@page import="dal.TDossierReglement"%>
<%@page import="bll.facture.reglementManager"%>
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
<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data liste Reglement");
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

<%    dataManager OdataManager = new dataManager();
    String dt_debut=date.formatterMysqlShort.format(new Date()) , dt_fin= date.formatterMysql.format(new Date()) ;
    String lg_REGLEMENT_ID = "%%",search_value="%%", str_Name_organisme = "", str_CODE_FACTURE = "%%", P_KEY = "%%", lg_Participant_ID = "%%", lg_CLIENT_ID = "%%", str_VALUE = "%%", str_Status = "%%";
    Integer int_PRIORITY, Amount_PAYE;
    String lg_TIERS_PAYANT_ID="%%";
    date key = new date();
    
    json Ojson = new json();
    double Amount;
   

    if (request.getParameter("dt_fin") != null && request.getParameter("dt_fin") != "") {
      //  dt_fin = key.stringToDate(request.getParameter("dt_fin"), key.formatterMysqlShort);
         dt_fin=request.getParameter("dt_fin")+" 23:59:59";
    } 

    if (request.getParameter("dt_debut") != null && request.getParameter("dt_debut") != "") {
     //    dt_debut = key.stringToDate(request.getParameter("dt_debut"), key.formatterMysqlShort);
       dt_debut=request.getParameter("dt_debut"); 
    }
    

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }

   
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !"".equals(request.getParameter("lg_TIERS_PAYANT_ID"))) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
    }


    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    reglementManager OreglementManagement = new reglementManager(OdataManager, OTUser);
    
    List<EntityData>entityDatas =OreglementManagement.getAllDossierReglements(lg_TIERS_PAYANT_ID, search_value, dt_debut, dt_fin);
%>

<%
//Filtre de  
    try {
        if (DATA_PER_PAGE > entityDatas.size()) {
            DATA_PER_PAGE = entityDatas.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (entityDatas.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    
    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
       

        // String str_NAMECUSTOMER = OfactureManagement.getNameCustomer(entityDatas.get(i).getLgTYPEFACTUREID().getLgTYPEFACTUREID(), lstTFacture.get(i).getStrCUSTOMER());
        JSONObject json = new JSONObject();
        

        
        json.put("lg_DOSSIER_REGLEMENT_ID", entityDatas.get(i).getStr_value1());
        json.put("str_MODE_REGLEMENT", entityDatas.get(i).getStr_value3());
        json.put("str_MONTANT", entityDatas.get(i).getStr_value2());
        json.put("dt_DATE_REGLEMENT", entityDatas.get(i).getStr_value6());
        json.put("str_ORGANISME", entityDatas.get(i).getStr_value4());
         json.put("LIBELLE_TYPE_TIERS_PAYANT", entityDatas.get(i).getStr_value8());
        json.put("HEURE_REGLEMENT", entityDatas.get(i).getStr_value7());
       json.put("OPERATEUR", entityDatas.get(i).getStr_value5());
        json.put("MONTANT_ATT", entityDatas.get(i).getStr_value10());
       // 
          json.put("CODE_FACTURE", entityDatas.get(i).getStr_value9());

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + entityDatas.size() + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>