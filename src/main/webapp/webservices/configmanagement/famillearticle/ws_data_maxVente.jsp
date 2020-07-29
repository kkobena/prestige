<%-- 
    Document   : ws_data_maxVente
    Created on : 30 juil. 2015, 20:48:12
    Author     : EACHUA
--%>

<%@page import="dal.TParameters"%>
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
    String lg_FAMILLEARTICLE_ID = "%%", str_LIBELLE = "%%", str_CODE_FAMILLE = "%%", str_COMMENTAIRE = "%%",
        lg_GROUPE_FAMILLE_ID = "%%";
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    json Ojson = new json();

    List<dal.TParameters> lstTFamillearticle = new ArrayList<dal.TParameters>();

%>


<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data TFamillearticle ");
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
    if (request.getParameter("lg_FAMILLEARTICLE_ID") != null) {
        if (request.getParameter("lg_FAMILLEARTICLE_ID").toString().equals("ALL")) {
            lg_FAMILLEARTICLE_ID = "%%";
        } else {
            lg_FAMILLEARTICLE_ID = request.getParameter("lg_FAMILLEARTICLE_ID").toString();
        }

    }
    
    OdataManager.initEntityManager();
    if (request.getParameter("remise") != null) {
    lstTFamillearticle = OdataManager.getEm().createQuery("SELECT t FROM TParameters t WHERE t.strKEY LIKE ?1")
            .setParameter(1, "KEY_APPLIQ_REMISE")
            .getResultList();
    }
    else if(request.getParameter("ticket") != null)
    {
        lstTFamillearticle = OdataManager.getEm().createQuery("SELECT t FROM TParameters t WHERE t.strKEY LIKE ?1")
            .setParameter(1, "KEY_EDIT_TICKET")
            .getResultList();
    }
    else
    {
        lstTFamillearticle = OdataManager.getEm().createQuery("SELECT t FROM TParameters t WHERE t.strKEY LIKE ?1")
            .setParameter(1, "KEY_MAX_VALUE_VENTE")
            .getResultList();
    }
    new logger().OCategory.info(lstTFamillearticle);
    

    
%>

<%
//Filtre de pagination
    

    

%>


<%    JSONArray arrayObj = new JSONArray();
    
    String result = "({\"total\":\"" + lstTFamillearticle.get(0).getStrVALUE() + "\""+  "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>
