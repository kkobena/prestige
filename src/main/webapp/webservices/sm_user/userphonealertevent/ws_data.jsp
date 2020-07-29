<%@page import="dal.TUserFone"%>
<%@page import="dal.TAlertEventUserFone"%>
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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_USER_FONE_ID = "%%", str_PHONE = "%%", str_STATUT = "%%", str_DESCRIPTION = "%%";
    date key = new date();
    Integer int_STOCK_MINIMAL;
    Integer int_PRICE;
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TAlertEventUserFone> lstTAlertEventUserFone = new ArrayList<dal.TAlertEventUserFone>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data");
    new logger().OCategory.info("str_Event = " + request.getParameter("str_Event"));


%>


<!-- logic de gestion des page -->
<%    String action = request.getParameter("action"); //get parameter ?action=
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

<%    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + request.getParameter("search_value"));
    if (request.getParameter("lg_USER_FONE_ID") != null) {
        if (request.getParameter("lg_USER_FONE_ID").toString().equals("ALL")) {
            lg_USER_FONE_ID = "%%";
        } else {
            lg_USER_FONE_ID = request.getParameter("lg_USER_FONE_ID").toString();
        }

    }

    OdataManager.initEntityManager();
    lstTAlertEventUserFone = OdataManager.getEm().createQuery("SELECT t FROM TAlertEventUserFone t WHERE t.strEvent.strEvent LIKE ?1 AND  t.strSTATUT LIKE ?2 ").
            setParameter(1, request.getParameter("str_Event")).
            setParameter(2, commonparameter.statut_enable).
            getResultList();
    new logger().OCategory.info(lstTAlertEventUserFone.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTAlertEventUserFone.size()) {
            DATA_PER_PAGE = lstTAlertEventUserFone.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTAlertEventUserFone.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTAlertEventUserFone.get(i));
        } catch (Exception er) {
        }

        String str_phone =lstTAlertEventUserFone.get(i).getLgUSERFONEID().getStrPHONE()  ; 
         //   List<TUserFone> lstTTUserFone =  new ArrayList(lstTAlertEventUserFone.get(i). );
      /*   for (int k = 0; k < lstTTUserFone.size(); k++) {
           // int_price = int_price + lstTPreenregistrementDetail.get(k).getIntPRICE();
        }
        */
        JSONObject json = new JSONObject();

        json.put("lg_ID", lstTAlertEventUserFone.get(i).getLgID());
        json.put("lg_USER_FONE_ID", lstTAlertEventUserFone.get(i).getLgUSERFONEID().getLgUSERID().getStrLOGIN());
        json.put("str_STATUT", oTranslate.getValue(lstTAlertEventUserFone.get(i).getStrSTATUT()));
        json.put("str_Event", oTranslate.getValue(lstTAlertEventUserFone.get(i).getStrEvent().getStrEvent()));
        json.put("str_LIST_PHONE", str_phone );

//str_LIST_PHONE
        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTAlertEventUserFone.size() + " \",\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>