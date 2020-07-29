<%@page import="dal.TSnapshotPreenregistrementCompteClientTiersPayent"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
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
   
    date key = new date();

    json Ojson = new json();
    List<TSnapshotPreenregistrementCompteClientTiersPayent> lstTSnapshotPreenregistrementCompteClientTiersPayent = new ArrayList<TSnapshotPreenregistrementCompteClientTiersPayent>();
    TUser OTUser;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data type vente ");
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
        String dt_DEBUT = "", dt_FIN = "";
     String lg_COMPTE_CLIENT_ID = "%%", str_STATUT = "is_Waiting", str_NAME = "%%", str_DESCRIPTION = "%%", lg_TIERS_PAYANT_ID = "%%", search_value = "";
    String lg_SERVICE_ID = "%%", P_KEY_IDENTITY = "", str_REF_SERVICECONCERNE = "%%", lg_COMPTE_ID = "%%", str_PHONE = "%%";
    int int_AMOUNTTRANSACTION = 0;
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
    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null && request.getParameter("lg_COMPTE_CLIENT_ID") != "")  {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);
    } 
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && request.getParameter("lg_TIERS_PAYANT_ID") != "") {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID);
    } 
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    } 
    
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  "+dt_FIN+"  dt_DEBUT  "+dt_DEBUT);

    OdataManager.initEntityManager();
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    lstTSnapshotPreenregistrementCompteClientTiersPayent = OtierspayantManagement.listTSnapshotPreenregistrementCompteClientTiersPayent(search_value, dt_DEBUT, dt_FIN, lg_TIERS_PAYANT_ID, lg_COMPTE_CLIENT_ID, str_STATUT);
    new logger().OCategory.info("Size lstTSnapshotPreenregistrementCompteClientTiersPayent  " + lstTSnapshotPreenregistrementCompteClientTiersPayent.size());


%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTSnapshotPreenregistrementCompteClientTiersPayent.size()) {
            DATA_PER_PAGE = lstTSnapshotPreenregistrementCompteClientTiersPayent.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTSnapshotPreenregistrementCompteClientTiersPayent.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i));
        } catch (Exception er) {
        }
    
            JSONObject json = new JSONObject();
            

            json.put("lg_SNAPSHOT_PREENREGISTREMENT_COMPTECLIENT_TIERSPAENT_ID", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getLgSNAPSHOTPREENREGISTREMENTCOMPTECLIENTTIERSPAENTID());
            json.put("int_NUMBER_TRANSACTION", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getIntNUMBERTRANSACTION());
            json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_ID", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTTIERSPAYANTID());
            json.put("str_STATUT", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getStrSTATUT());
            json.put("int_PRICE", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getIntPRICE());
            json.put("str_REF", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getStrREF());
            json.put("str_FIRST_NAME", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME());
            json.put("str_LAST_NAME", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());
            json.put("lg_CLIENT_ID", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getLgCLIENTID());
            json.put("lg_TIERS_PAYANT_ID", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTIERSPAYANTID());
            json.put("str_FULLNAME", lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrFULLNAME());
            json.put("dt_CREATED", date.DateToString(lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getDtCREATED(), date.formatterShort));
            json.put("dt_UPDATED", date.DateToString(lstTSnapshotPreenregistrementCompteClientTiersPayent.get(i).getDtUPDATED(), date.formatterShort));
            
            arrayObj.put(json);
        
    }
    new logger().OCategory.info(arrayObj.toString());

%>

<%= arrayObj.toString()%>