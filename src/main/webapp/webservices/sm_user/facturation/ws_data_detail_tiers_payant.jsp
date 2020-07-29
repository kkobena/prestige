<%-- 
    Document   : ws_data_detail_tiers_payant
    Created on : 23 nov. 2015, 17:38:16
    Author     : KKOFFI
--%>
<%@page import="bll.entity.EntityData"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
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

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
   
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

<%  String str_CODE_REGROUPEMENT = "%%", lg_TYPE_TIERS_PAYANT_ID = "%%",lg_TIERS_PAYANT="%%";
    String dt_debut=date.formatterMysqlShort.format(new Date())+" 23:59" , dt_fin=date.formatterMysql.format(new Date());
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    dataManager OdataManager = new dataManager();
    List<EntityData> ListEntityData = new ArrayList<EntityData>();
    factureManagement OfactureManagement = new factureManagement(OdataManager, OTUser);
    if (request.getParameter("dt_debut") != null && !"".equals(request.getParameter("dt_debut"))) {
        dt_debut = request.getParameter("dt_debut");
        
    }
     if (request.getParameter("dt_fin") != null && !"".equals(request.getParameter("dt_fin"))) {
        dt_fin = request.getParameter("dt_fin")+ " 23:59";
       
    }
    
    if (request.getParameter("lg_TYPE_TIERS_PAYANT_ID") != null && !"".equals(request.getParameter("lg_TYPE_TIERS_PAYANT_ID"))) {
        lg_TYPE_TIERS_PAYANT_ID = request.getParameter("lg_TYPE_TIERS_PAYANT_ID");
        
    }
    
 if (request.getParameter("str_CODE_REGROUPEMENT") != null && !"".equals(request.getParameter("str_CODE_REGROUPEMENT"))) {
        str_CODE_REGROUPEMENT = request.getParameter("str_CODE_REGROUPEMENT");
       
    }
 if (request.getParameter("lg_TIERS_PAYANT") != null && !"".equals(request.getParameter("lg_TIERS_PAYANT"))) {
        lg_TIERS_PAYANT = request.getParameter("lg_TIERS_PAYANT");
       
    }
    ListEntityData = OfactureManagement.getVenteTiersPayant(dt_debut, dt_fin, str_CODE_REGROUPEMENT, lg_TYPE_TIERS_PAYANT_ID,lg_TIERS_PAYANT);
double Montant_total = 0;

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > ListEntityData.size()) {
            DATA_PER_PAGE = ListEntityData.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (ListEntityData.size() - (DATA_PER_PAGE * (pgInt)));
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

        json.put("lg_TIERS_PAYANT_ID", ListEntityData.get(i).getStr_value2());
        json.put("str_FULLNAME", ListEntityData.get(i).getStr_value1());
        json.put("str_ACOUNT_DOSSIER", ListEntityData.get(i).getStr_value3());
        json.put("dbl_MONTANT", ListEntityData.get(i).getStr_value4());
        
        json.put("isChecked", "false");
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + ListEntityData.size() + "\",\"results\":" + arrayObj.toString() + ",\"Montant_total\":" + Montant_total + "})";
%>

<%= result%>
