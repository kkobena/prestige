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
    String lg_FACTURE_ID = "%%", str_CODE_FACTURE = "%%", lg_bordereau_id = "%%", lg_customer_id = "%%", lg_MODULE_ID = "%%", lg_dossier_id = "%%", str_Status = "%%";
    Integer int_PRIORITY;
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TFacture> lstTFacture = new ArrayList<dal.TFacture>();

    
    
    

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data liste facture");
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

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_customer_id") != null) {
        lg_customer_id = "%" +request.getParameter("lg_customer_id").toString()+ "%";;
    }

    if (request.getParameter("lg_bordereau_id") != null) {
        lg_bordereau_id = "%" + request.getParameter("lg_bordereau_id").toString() + "%";
        new logger().OCategory.info("lg_bordereau_id " + request.getParameter("lg_dossier_id"));
    } else {
        lg_bordereau_id = "%%";
    }

    OdataManager.initEntityManager();
    lstTFacture = OdataManager.getEm().createQuery("SELECT t FROM TFacture t WHERE t.lgFACTUREID LIKE ?1 AND t.strCODEFACTURE LIKE ?2  AND (t.strSTATUT LIKE ?3 OR t.strSTATUT LIKE ?5)  AND t.strCUSTOMER LIKE ?4").
            setParameter(1, lg_FACTURE_ID)
            .setParameter(2, lg_bordereau_id)
            .setParameter(3, commonparameter.statut_enable)
            .setParameter(5, commonparameter.statut_is_Process) 
            .setParameter(4, lg_customer_id)
            .getResultList();
    new logger().OCategory.info(lstTFacture.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTFacture.size()) {
            DATA_PER_PAGE = lstTFacture.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTFacture.size() - (DATA_PER_PAGE * (pgInt)));
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

    factureManagement OfactureManagement = new factureManagement(OdataManager, OTUser);

    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTFacture.get(i));
        } catch (Exception er) {
        }

        String str_NAMECUSTOMER = OfactureManagement.getNameCustomer(lstTFacture.get(i).getLgTYPEFACTUREID().getLgTYPEFACTUREID(), lstTFacture.get(i).getStrCUSTOMER());

        JSONObject json = new JSONObject();

        json.put("lg_FACTURE_ID", lstTFacture.get(i).getLgFACTUREID());
        json.put("str_CODE_FACTURE", lstTFacture.get(i).getStrCODEFACTURE());
        json.put("int_NB_DOSSIER", lstTFacture.get(i).getIntNBDOSSIER());
        json.put("dt_CREATED", key.DateToString(lstTFacture.get(i).getDtDATEFACTURE(), key.formatterOrange));
        json.put("str_STATUT", lstTFacture.get(i).getStrSTATUT());
        json.put("lg_TYPE_FACTURE_ID", lstTFacture.get(i).getLgTYPEFACTUREID().getStrLIBELLE());
        json.put("str_CUSTOMER_NAME", str_NAMECUSTOMER);
        json.put("str_PERIODE", "Du " + key.DateToString(lstTFacture.get(i).getDtDEBUTFACTURE(), key.formatterShort) + " Au " + key.DateToString(lstTFacture.get(i).getDtFINFACTURE(), key.formatterShort));
        json.put("dbl_MONTANT_CMDE", lstTFacture.get(i).getDblMONTANTRESTANT());
        json.put("str_CUSTOMER", lstTFacture.get(i).getStrCUSTOMER());
        
        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTFacture.size() + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>