<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TPreenregistrementCompteClient"  %>
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
    String lg_PREENREGISTREMENT_COMPTE_CLIENT_ID = "%%", lg_COMPTE_CLIENT_ID = "%%", lg_PREENREGISTREMENT_ID = "%%",
            lg_USER_ID = "%%", str_STATUT = "%%";
    Date dt_CREATED, dt_UPDATED;
    int int_PRICE;
    json Ojson = new json();
    date key = new date();
    List<dal.TPreenregistrementCompteClient> lstTPreenregistrementCompteClient = new ArrayList<dal.TPreenregistrementCompteClient>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data preenregistrement compte client ");
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

<%  if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_PREENREGISTREMENT_COMPTE_CLIENT_ID") != null) {
        if (request.getParameter("lg_PREENREGISTREMENT_COMPTE_CLIENT_ID").toString().equals("ALL")) {
            lg_PREENREGISTREMENT_COMPTE_CLIENT_ID = "%%";
        } else {
            lg_PREENREGISTREMENT_COMPTE_CLIENT_ID = request.getParameter("lg_PREENREGISTREMENT_COMPTE_CLIENT_ID").toString();
        }

    }

    OdataManager.initEntityManager();
    lstTPreenregistrementCompteClient = OdataManager.getEm().createQuery("SELECT t FROM TPreenregistrementCompteClient t WHERE t.lgPREENREGISTREMENTCOMPTECLIENTID LIKE ?1 ").
            setParameter(1, lg_PREENREGISTREMENT_COMPTE_CLIENT_ID)
            .getResultList();
    new logger().OCategory.info(lstTPreenregistrementCompteClient.size());
%>

<%
//Filtre de pagination
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


<%   JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTPreenregistrementCompteClient.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_PREENREGISTREMENT_COMPTE_CLIENT_ID", lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTCOMPTECLIENTID());
        json.put("lg_PREENREGISTREMENT_ID", lstTPreenregistrementCompteClient.get(i).getLgPREENREGISTREMENTID().getStrREF());
        
        json.put("lg_COMPTE_CLIENT_ID", lstTPreenregistrementCompteClient.get(i).getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME() + " " +
                lstTPreenregistrementCompteClient.get(i).getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());
        
        json.put("lg_USER_ID", lstTPreenregistrementCompteClient.get(i).getLgUSERID().getStrLASTCONNECTIONDATE() + " "+
                               lstTPreenregistrementCompteClient.get(i).getLgUSERID().getStrLASTCONNECTIONDATE() );

        json.put("int_PRICE", lstTPreenregistrementCompteClient.get(i).getIntPRICE());
        json.put("str_STATUT", lstTPreenregistrementCompteClient.get(i).getStrSTATUT());
        
        dt_CREATED = lstTPreenregistrementCompteClient.get(i).getDtCREATED();      
        if (dt_CREATED != null){
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterMysql));
        }

        dt_UPDATED = lstTPreenregistrementCompteClient.get(i).getDtUPDATED();      
        if (dt_UPDATED != null){
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterMysql));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTPreenregistrementCompteClient.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>