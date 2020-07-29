<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TDossierFacture"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="bll.facture.factureManagement"%>
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
    String lg_FACTURE_ID = "%%", lg_customer_id = "%%", P_KEY = "%%", lg_PREENREGISTREMENT_ID = "%%", lg_MODULE_ID = "%%", str_VALUE = "%%", str_Status = "%%";
    Integer int_PRIORITY;
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TPreenregistrementDetail> lstTPreenregistrementDetail = new ArrayList<dal.TPreenregistrementDetail>();

    bllBase ObllBase = new bllBase();
    TCompteClientTiersPayant OTCompteClientTiersPayant = null;

    

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data detail bon");
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

<%    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    // new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + request.getParameter("lg_PREENREGISTREMENT_ID"));
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID").toString();
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + request.getParameter("lg_PREENREGISTREMENT_ID"));
    }

    //lg_FACTURE_ID = "57132064627421608451";
    OdataManager.initEntityManager();
    lstTPreenregistrementDetail = OdataManager.getEm().createQuery("SELECT t FROM TPreenregistrementDetail t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1 AND t.strSTATUT = ?2").
            setParameter(1, lg_PREENREGISTREMENT_ID)
            .setParameter(2, commonparameter.statut_is_Closed)
            .getResultList();
    new logger().OCategory.info(lstTPreenregistrementDetail.size());

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTPreenregistrementDetail.size()) {
            DATA_PER_PAGE = lstTPreenregistrementDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTPreenregistrementDetail.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    OTUser = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTPreenregistrementDetail.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        double percent = (double) lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getTPreenregistrementCompteClientTiersPayentCollection().iterator().next().getIntPERCENT();
        double Quote_part = (double)(lstTPreenregistrementDetail.get(i).getIntPRICE()*(percent/100));
        
        json.put("lg_PREENREGISTREMENT_DETAIL_ID", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTDETAILID());
        json.put("str_NAME", lstTPreenregistrementDetail.get(i).getLgFAMILLEID().getStrNAME());
        json.put("int_PRICE", lstTPreenregistrementDetail.get(i).getIntPRICE());
        json.put("Quote_part",(int) Quote_part);
        json.put("int_PERCENT", lstTPreenregistrementDetail.get(i).getLgPREENREGISTREMENTID().getTPreenregistrementCompteClientTiersPayentCollection().iterator().next().getIntPERCENT());
        json.put("int_QUANTITY_SERVED", lstTPreenregistrementDetail.get(i).getIntQUANTITYSERVED());

        arrayObj.put(json);

    }
    String result = "({\"total\":\"" + lstTPreenregistrementDetail.size() + "\",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>