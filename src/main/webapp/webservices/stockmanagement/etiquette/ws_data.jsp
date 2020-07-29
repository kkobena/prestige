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


<%
    dataManager OdataManager = new dataManager();
    List<TEtiquette> lstTEtiquette = new ArrayList<TEtiquette>();
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
    String lg_ETIQUETTE_ID = "%%", search_value = "", lg_TYPEETIQUETTE_ID = "%%";
    date key = new date();
    String dt_DEBUT = "", dt_FIN = "";
    Date dtFin;
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

    if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dt_DEBUT = "2015-04-20";
    }
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
        String Odate = key.DateToString(dtFin, key.formatterMysqlShort2);

        dtFin = key.getDate(Odate, "23:59");
    }
    Date dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  " + dt_FIN + "  dt_DEBUT  " + dt_DEBUT + " dtFin:" + dtFin);

    if (request.getParameter("lg_ETIQUETTE_ID") != null && request.getParameter("lg_ETIQUETTE_ID") != "") {
        lg_ETIQUETTE_ID = request.getParameter("lg_ETIQUETTE_ID");
        new logger().OCategory.info("lg_ETIQUETTE_ID " + lg_ETIQUETTE_ID);
    }
    if (request.getParameter("lg_TYPEETIQUETTE_ID") != null && request.getParameter("lg_TYPEETIQUETTE_ID") != "") {
        lg_TYPEETIQUETTE_ID = request.getParameter("lg_TYPEETIQUETTE_ID");
        new logger().OCategory.info("lg_TYPEETIQUETTE_ID " + lg_TYPEETIQUETTE_ID);
    }

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    StockManager OStockManager = new StockManager(OdataManager,OTUser);
    
    lstTEtiquette = OStockManager.listeEtiquette(search_value, lg_TYPEETIQUETTE_ID, dtDEBUT, dtFin);
    new logger().OCategory.info("Size lstTEtiquette  " + lstTEtiquette.size());


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
        json.put("lg_TYPEETIQUETTE_ID", lstTEtiquette.get(i).getLgTYPEETIQUETTEID().getStrDESCRIPTION());
        json.put("str_NAME", lstTEtiquette.get(i).getStrNAME());
        json.put("str_CODE", lstTEtiquette.get(i).getStrCODE());
        String str_STATUT = "";
        if(lstTEtiquette.get(i).getStrSTATUT().equalsIgnoreCase(commonparameter.statut_enable)) {
            str_STATUT = "Non éditée";
        } else if(lstTEtiquette.get(i).getStrSTATUT().equalsIgnoreCase(commonparameter.statut_Read)) {
            str_STATUT = "Editée";
        }
        json.put("str_STATUT", str_STATUT);
        json.put("dt_CREATED", date.DateToString(lstTEtiquette.get(i).getDtCREATED(), date.formatterShort));
        json.put("int_CIP", lstTEtiquette.get(i).getLgFAMILLEID().getIntCIP());
        json.put("lg_FAMILLE_ID", lstTEtiquette.get(i).getLgFAMILLEID().getStrDESCRIPTION());
        json.put("int_NUMBER", lstTEtiquette.get(i).getIntNUMBER());
       /* try {
            int int_QUANTITY = 0;
            String tabQuantite[] = lstTEtiquette.get(i).getIntNUMBER().split("/");
            int_QUANTITY = Integer.parseInt(tabQuantite[1]);
            new logger().OCategory.info("int_QUANTITY "+int_QUANTITY);
            json.put("int_QUANTITY", int_QUANTITY);
        }catch(Exception e) {
            e.printStackTrace();
        }*/
        arrayObj.put(json);

    }
    String result = "({\"total\":\"" + lstTEtiquette.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>