<%@page import="bll.bllBase"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TNatureVente"  %>
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
    List<dal.TNatureVente> lstTNatureVente = new ArrayList<dal.TNatureVente>();
    Date dt_CREATED, dt_UPDATED;
    Integer int_search_cip;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data nature vente ");
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
     String lg_NATURE_VENTE_ID = "%%", str_LIBELLE = "%%";
    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    if (request.getParameter("search_nature") != null) {
        Os_Search_poste.setOdata("%" + request.getParameter("search_nature") + "%");
        new logger().OCategory.info("search_nature " + request.getParameter("search_nature"));
    } else {
        Os_Search_poste.setOdata("%%");

    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    new logger().OCategory.info("search_nature  = " + Os_Search_poste.getOdata());
    if (request.getParameter("lg_NATURE_VENTE_ID") != null) {
        if (request.getParameter("lg_NATURE_VENTE_ID").toString().equals("ALL")) {
            lg_NATURE_VENTE_ID = "%%";
        } else {
            lg_NATURE_VENTE_ID = request.getParameter("lg_NATURE_VENTE_ID").toString();
        }

    }

    String search_value = Os_Search_poste.getOvalue();
    String search_cip_value = Os_Search_poste.getOdata();
    new logger().OCategory.info("search_nature    " + search_value);

    //new logger().OCategory.info("search_cip_value    " + search_cip_value);
    OdataManager.initEntityManager();
    lstTNatureVente = OdataManager.getEm().createQuery("SELECT t FROM TNatureVente t WHERE t.lgNATUREVENTEID LIKE ?1 AND t.strLIBELLE LIKE ?2 AND t.strLIBELLE NOT LIKE ?3").
            setParameter(1, lg_NATURE_VENTE_ID)
            .setParameter(2, search_value)
            .setParameter(3, commonparameter.DEPOT)
            .getResultList();
    new logger().OCategory.info(lstTNatureVente.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTNatureVente.size()) {
            DATA_PER_PAGE = lstTNatureVente.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTNatureVente.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTNatureVente.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_NATURE_VENTE_ID", lstTNatureVente.get(i).getLgNATUREVENTEID());
        json.put("str_LIBELLE", lstTNatureVente.get(i).getStrLIBELLE());

        dt_CREATED = lstTNatureVente.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTNatureVente.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTNatureVente.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= arrayObj.toString()%>