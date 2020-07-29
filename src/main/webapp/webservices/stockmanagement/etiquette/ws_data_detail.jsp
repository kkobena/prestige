<%@page import="dal.TEtiquette"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TEtiquette"%>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    
    json Ojson = new json();
    List<TEtiquette> lstTEtiquette = new ArrayList<TEtiquette>();
    TUser OTUser;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data etiquette");
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
    String lg_ETIQUETTE_ID = "%%", search_value = "", lg_TYPEETIQUETTE_ID = "%%";
    date key = new date();
    String dt_DEBUT = "", dt_FIN = "";
    Date dtFin;
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    
   

    OdataManager.initEntityManager();
    StockManager OStockManager = new StockManager(OdataManager);
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    lstTEtiquette = OStockManager.listeEtiquette(search_value, commonparameter.statut_is_Process);


%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTEtiquette.size()) {
            DATA_PER_PAGE = lstTEtiquette.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTEtiquette.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTEtiquette.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("lg_ETIQUETTE_ID", lstTEtiquette.get(i).getLgETIQUETTEID());
        json.put("str_NAME", lstTEtiquette.get(i).getStrNAME());
        json.put("str_CODE", lstTEtiquette.get(i).getStrCODE());
        json.put("dt_CREATED", date.DateToString(lstTEtiquette.get(i).getDtCREATED(), date.formatterShort));
        json.put("int_CIP", lstTEtiquette.get(i).getLgFAMILLEID().getIntCIP());
        json.put("lg_FAMILLE_ID", lstTEtiquette.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("int_PRICE", lstTEtiquette.get(i).getLgFAMILLEID().getIntPRICE());
        json.put("int_PAF", lstTEtiquette.get(i).getLgFAMILLEID().getIntPAF());
        json.put("int_NUMBER", lstTEtiquette.get(i).getIntNUMBER());
       
        arrayObj.put(json);

    }
    String result = "({\"total\":\"" + lstTEtiquette.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>