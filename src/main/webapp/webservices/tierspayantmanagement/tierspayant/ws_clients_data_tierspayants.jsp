<%@page import="bll.facture.factureManagement"%>
<%@page import="dal.TClient"%>
<%@page import="dal.TParameters"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TTypeStockFamille"%>
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


<% 
   

    

    Integer int_search_cip;

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data famille jdbc ");
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
     String lg_TIERS_PAYANT_ID="%%";
  if(request.getParameter("lg_TIERS_PAYANT_ID")!=null){
      lg_TIERS_PAYANT_ID=request.getParameter("lg_TIERS_PAYANT_ID");
  }
       
 dataManager OdataManager = new dataManager();
   
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    List<TClient> listTClient =new ArrayList<TClient>();
 factureManagement management=new factureManagement(OdataManager, OTUser);
  listTClient=management.getAllClients(lg_TIERS_PAYANT_ID);
    new logger().OCategory.info("listTClient dans le ws " + listTClient.size());

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > listTClient.size()) {
            DATA_PER_PAGE = listTClient.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (listTClient.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
JSONObject data = new JSONObject();

    for (int i = pgInt; i < pgInt_Last; i++) {
JSONObject json = new JSONObject();
     json.put("lg_CLIENT_ID", listTClient.get(i).getLgCLIENTID());   
  json.put("str_FIRST_NAME_LAST_NAME",  listTClient.get(i).getStrFIRSTNAME()+" "+ listTClient.get(i).getStrLASTNAME());
                       
           arrayObj.put(json);
      
    }
data.put("data", arrayObj);
 
%>

<%= data%>