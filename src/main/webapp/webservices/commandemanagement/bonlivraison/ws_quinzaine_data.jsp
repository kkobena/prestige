<%@page import="dal.TQuinzaine"%>
<%@page import="dal.TBonLivraisonDetail"%>
<%@page import="com.asc.prestige2.business.bonlivraisons.concrete.PrestigeBLService"%>
<%@page import="com.asc.prestige2.business.bonlivraisons.BLService"%>
<%@page import="dal.TParameters"%>
<%@page import="dal.TFamilleGrossiste"%>
<%@page import="bll.configManagement.familleGrossisteManagement"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.commandeManagement.bonLivraisonManagement"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TBonLivraison"  %>
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
   BLService _service = new PrestigeBLService();
%>

<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TFamilleGrossiste OTFamilleGrossiste = null;
    date key = new date();
%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans webservices.commandemanagement.bonlivraison.ws_quinzaine_data.jsp");
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

<%  List<TQuinzaine> quinzaines = _service.getQuinzaines();
    String lg_QUINZAINE_ID = "%%", str_GROSSISTE_LIBELLE = "%%", search_value = "", dt_START_DATE = "%%", dt_END_DATE = "";

  

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    if (request.getParameter("lg_QUINZAINE_ID") != null) {
        lg_QUINZAINE_ID = request.getParameter("lg_QUINZAINE_ID").toString();
        new logger().OCategory.info("lg_QUINZAINE_ID " + lg_QUINZAINE_ID);
    }
    if (request.getParameter("str_GROSSISTE_LIBELLE") != null) {
        str_GROSSISTE_LIBELLE = request.getParameter("str_GROSSISTE_LIBELLE").toString();
        new logger().OCategory.info("str_GROSSISTE_LIBELLE " + str_GROSSISTE_LIBELLE);
        
    }
    
 if (request.getParameter("dt_START_DATE") != null) {
        dt_START_DATE = request.getParameter("dt_START_DATE").toString();
        new logger().OCategory.info("dt_START_DATE " + dt_START_DATE);
        
    }
 
 if (request.getParameter("dt_END_DATE") != null) {
        dt_END_DATE = request.getParameter("dt_END_DATE").toString();
        new logger().OCategory.info("dt_END_DATE " + dt_END_DATE);
        
    }
    

%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > quinzaines.size()) {
            DATA_PER_PAGE = quinzaines.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (quinzaines.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>



<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        TQuinzaine quinzaine = quinzaines.get(i);
        lg_QUINZAINE_ID = quinzaine.getLgQUINZAINEID();
        str_GROSSISTE_LIBELLE = quinzaine.getLgGROSSISTEID().getStrLIBELLE();
        JSONObject json = new JSONObject();
        json.put("lg_QUINZAINE_ID",quinzaine.getLgQUINZAINEID());
        json.put("str_GROSSISTE_LIBELLE", quinzaine.getLgGROSSISTEID().getStrLIBELLE());
        
        json.put("dt_START_DATE", date.formatterOrange.format(quinzaine.getDtDATESTART()));
        json.put("dt_END_DATE", date.formatterOrange.format(quinzaine.getDtDATEEND()));
       

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + quinzaines.size() + " \" ,\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
    
%>

<%= result%>