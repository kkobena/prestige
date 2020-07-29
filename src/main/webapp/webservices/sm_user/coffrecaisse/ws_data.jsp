<%@page import="bll.userManagement.user"%>
<%@page import="dal.TRoleUser"%>
<%@page import="dal.TCoffreCaisse"%>
<%@page import="bll.teller.caisseManagement"%>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();
    TUser OTUser1 = null;
    List<TCoffreCaisse> lstTCoffreCaisse = new ArrayList<TCoffreCaisse>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data");
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

<%    String ID_COFFRE_CAISSE = "%%", search_value = "";
    String str_Date_Debut = "", str_Date_Fin = "", OdateDebut = "", OdateFin = "";
    Date dt_Date_Debut, dt_Date_Fin;
     TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("ID_COFFRE_CAISSE") != null) {
        ID_COFFRE_CAISSE = request.getParameter("ID_COFFRE_CAISSE");
        new logger().OCategory.info("ID_COFFRE_CAISSE " + ID_COFFRE_CAISSE);
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
    

    OdataManager.initEntityManager();
    TRoleUser OTRoleUser = new user(OdataManager).getTRoleUser(OTUser.getLgUSERID());
    caisseManagement OcaisseManagement = new caisseManagement(OdataManager, OTUser);
    lstTCoffreCaisse = OcaisseManagement.getListeCoffreCaisse(search_value, ID_COFFRE_CAISSE, dt_Date_Debut, dt_Date_Fin);
    /*lstTCoffreCaisse = OdataManager.getEm().createQuery("SELECT t FROM TCoffreCaisse t WHERE t.idCoffreCaisse LIKE ?1 ORDER BY t.dtCREATED DESC").
            setParameter(1, ID_COFFRE_CAISSE).getResultList();
    new logger().OCategory.info(lstTCoffreCaisse.size());*/
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTCoffreCaisse.size()) {
            DATA_PER_PAGE = lstTCoffreCaisse.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTCoffreCaisse.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTCoffreCaisse.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("ID_COFFRE_CAISSE", lstTCoffreCaisse.get(i).getIdCoffreCaisse());
        json.put("lg_USER_ID", lstTCoffreCaisse.get(i).getLgUSERID().getStrFIRSTNAME() + "  " + lstTCoffreCaisse.get(i).getLgUSERID().getStrLASTNAME());
        //json.put("lg_USER_ID", lstTCoffreCaisse.get(i).getStrStatus());
        json.put("str_STATUT", oTranslate.getValue(lstTCoffreCaisse.get(i).getStrSTATUT()));
        json.put("lg_EMPLACEMENT_ID", lstTCoffreCaisse.get(i).getLgUSERID().getLgEMPLACEMENTID().getStrDESCRIPTION());
        json.put("show", (OTRoleUser != null && OTRoleUser.getLgROLEID().getStrNAME().equalsIgnoreCase(commonparameter.ROLE_SUPERADMIN) ? true : false));
        
        json.put("int_AMOUNT", lstTCoffreCaisse.get(i).getIntAMOUNT());
        try {
            OTUser1 = OdataManager.getEm().find(TUser.class, lstTCoffreCaisse.get(i).getLdCREATEDBY());
            json.put("ld_CREATED_BY", OTUser1.getStrFIRSTNAME() + " "+OTUser1.getStrLASTNAME());
        } catch(Exception e) {
            
        }
        
        json.put("dt_CREATED", date.DateToString(lstTCoffreCaisse.get(i).getDtCREATED(), date.formatterMysql));
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTCoffreCaisse.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info(result);

%>

<%= result%>