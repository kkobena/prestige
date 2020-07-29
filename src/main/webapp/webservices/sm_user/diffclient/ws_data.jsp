<%@page import="bll.differe.DiffereManagement"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TPreenregistrementCompteClient"%>
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
    List<TPreenregistrementCompteClient> lstTPreenregistrementCompteClient = new ArrayList<TPreenregistrementCompteClient>();
    TUser OTUser;

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data differe ");
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
    String lg_COMPTE_CLIENT_ID = "%%", search_value = "", Odate = "", dt_DEBUT = "", dt_FIN = "";
    Date dtFin, dtDEBUT;
    
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

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null && request.getParameter("lg_COMPTE_CLIENT_ID") != "") {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);
    }

    
     if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dtDEBUT = new Date();
        Odate = date.DateToString(dtDEBUT, date.formatterMysqlShort2);
    } else {
        dtDEBUT = date.stringToDate(dt_DEBUT, date.formatterMysqlShort);
        Odate = date.DateToString(dtDEBUT, date.formatterMysqlShort2); 
    }
      dtDEBUT = date.getDate(Odate, "00:00");
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
        Odate = date.DateToString(dtFin, date.formatterMysqlShort2);
    } else {
        dtFin = date.stringToDate(dt_FIN, date.formatterMysqlShort);
        Odate = date.DateToString(dtFin, date.formatterMysqlShort2);
    }
    dtFin = date.getDate(Odate, "23:59");
    
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  " + dt_FIN + "  dt_DEBUT  " + dt_DEBUT + " lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID + "dtDEBUT:" + dtDEBUT+ " dtFin:" + dtFin);
    

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    DiffereManagement ODiffereManagement = new DiffereManagement(OdataManager, OTUser);
   
   lstTPreenregistrementCompteClient = ODiffereManagement.getListeDifferes(search_value, lg_COMPTE_CLIENT_ID, dtDEBUT, dtFin);
    
%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTPreenregistrementCompteClient.size()) {
            DATA_PER_PAGE = lstTPreenregistrementCompteClient.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTPreenregistrementCompteClient.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTPreenregistrementCompteClient.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        
        json.put("lg_FAMILLE_ID", lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTCOMPTECLIENTID());
        json.put("int_CIP", lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getStrREF());
        json.put("str_NAME", lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getStrFIRSTNAMECUSTOMER());
        json.put("str_DESCRIPTION", lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getStrLASTNAMECUSTOMER());
        json.put("int_PRICE", lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getIntPRICE() - lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getIntPRICEREMISE());
        json.put("int_PAF", lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getIntCUSTPART());
        json.put("int_PAT", lstTPreenregistrementCompteClient.get(i).getIntPRICERESTE());
        json.put("int_T", date.DateToString(lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getDtUPDATED(), date.formatterShort));
        json.put("int_S", date.DateToString(lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getDtUPDATED(), date.NomadicUiFormat_Time));
        
        arrayObj.put(json);

    }
    new logger().OCategory.info(arrayObj.toString());

%>

<%= arrayObj.toString()%>