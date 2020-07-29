<%@page import="dal.TTypeetiquette"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TTypeetiquette"%>
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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_ETIQUETTE_ID = "%%", search_value = "";
    date key = new date();
    String dt_DEBUT = "", dt_FIN = "";
    json Ojson = new json();
    List<TTypeetiquette> lstTTypeetiquette = new ArrayList<TTypeetiquette>();
    TUser OTUser;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
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
    search_value = request.getParameter("search_value");
    if (search_value != null) {
       // Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        search_value = "";
        //Os_Search_poste.setOvalue("%%");
    }
    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }
    if (request.getParameter("lg_ETIQUETTE_ID") != null && request.getParameter("lg_ETIQUETTE_ID") != "")  {
        lg_ETIQUETTE_ID = request.getParameter("lg_ETIQUETTE_ID");
        new logger().OCategory.info("lg_ETIQUETTE_ID " + lg_ETIQUETTE_ID);
    } 
    
  
    
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  "+dt_FIN+"  dt_DEBUT  "+dt_DEBUT);

    OdataManager.initEntityManager();
    StockManager OStockManager = new StockManager(OdataManager);
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    lstTTypeetiquette = OStockManager.listeTypeetiquette(search_value);
    new logger().OCategory.info("Size lstTTypeetiquette  " + lstTTypeetiquette.size());


%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTTypeetiquette.size()) {
            DATA_PER_PAGE = lstTTypeetiquette.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTypeetiquette.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTTypeetiquette.get(i));
        } catch (Exception er) {
        }
    
            JSONObject json = new JSONObject();
            

            json.put("lg_TYPEETIQUETTE_ID", lstTTypeetiquette.get(i).getLgTYPEETIQUETTEID());
            json.put("str_NAME", lstTTypeetiquette.get(i).getStrNAME());
            json.put("str_DESCRIPTION", lstTTypeetiquette.get(i).getStrDESCRIPTION());
            json.put("str_STATUT", lstTTypeetiquette.get(i).getStrSTATUT());
           
            json.put("dt_CREATED", date.DateToString(lstTTypeetiquette.get(i).getDtCREATED(), date.formatterShort));
           // json.put("dt_UPDATED", date.DateToString(lstTTypeetiquette.get(i).getDtUPDATED(), date.formatterShort));
            
            arrayObj.put(json);
        
    }
     String result = "({\"total\":\"" + lstTTypeetiquette.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>