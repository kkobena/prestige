

<%@page import="bll.entity.EntityData"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="bll.facture.reglementManager"%>
<%@page import="dal.TFactureDetail"%>
<%@page import="dal.TFacture"%>
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


<%
    // int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws_details_reglement");
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

<%    //new logger().OCategory.info("lg_FACTURE_ID " + request.getParameter("lg_FACTURE_ID"));
    dataManager OdataManager = new dataManager();
    String lg_DOSSIER_REGLEMENT_ID = "", search_value = "";
    Integer int_PRIORITY;
    double Amount_total = 0.0;

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");

    }
    if (request.getParameter("lg_DOSSIER_REGLEMENT_ID") != null) {
        lg_DOSSIER_REGLEMENT_ID = request.getParameter("lg_DOSSIER_REGLEMENT_ID");

    }
    bllBase ObllBase = new bllBase();
    reglementManager OreglementManger = new reglementManager(OdataManager, null);
    List<EntityData> listEntityData = new ArrayList<EntityData>();

    listEntityData = OreglementManger.getAllDossierReglementDetails(lg_DOSSIER_REGLEMENT_ID, search_value);

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > listEntityData.size()) {
            DATA_PER_PAGE = listEntityData.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (listEntityData.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    // Amount_total = 0.0;

    for (int i = pgInt; i < pgInt_Last; i++) {
        JSONObject json = new JSONObject();
        json.put("lg_DOSSIER_REGLEMENT_DETAIL_ID", listEntityData.get(i).getStr_value1());
        json.put("Amount", listEntityData.get(i).getStr_value3());
        json.put("str_REF", (listEntityData.get(i).getStr_value2()!=null?listEntityData.get(i).getStr_value2():""));
        json.put("CLIENT_FULL_NAME", listEntityData.get(i).getStr_value4());
        json.put("CLIENT_MATRICULE", listEntityData.get(i).getStr_value5());
        json.put("int_NB_DOSSIER_RESTANT", listEntityData.size());
        json.put("dt_DATE", listEntityData.get(i).getStr_value6());
        json.put("dt_HEURE", listEntityData.get(i).getStr_value7());
        arrayObj.put(json);

    }
    String result = "({\"total\":\"" + listEntityData.size() + "\",Amount_total:\"" + Amount_total + "\",\"results\":" + arrayObj.toString() + "})";

    //new logger().OCategory.info("JSON " + result);
%>

<%= result%>