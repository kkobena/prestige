<%@page import="bll.configManagement.compteClientManagement"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TTiersPayant"  %>
<%@page import="dal.TCompteClient"  %>
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
    List<TCompteClientTiersPayant> lstTCompteClientTiersPayant = new ArrayList<TCompteClientTiersPayant>();
    JSONObject json = null;
    TCompteClient OTComptClient = null;

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data tiers payant dun client ");
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

<%    String lg_COMPTE_CLIENT_ID = "%%", search_value = "", lg_TIERS_PAYANT_ID = "%%";

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value:" + search_value);
    }
    if (request.getParameter("query") != null && !request.getParameter("query").equalsIgnoreCase("")) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value" + search_value);
    }
    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null && !request.getParameter("lg_COMPTE_CLIENT_ID").equalsIgnoreCase("")) {
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID avant :" + lg_COMPTE_CLIENT_ID + request.getParameter("lg_COMPTE_CLIENT_ID"));
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID:" + lg_COMPTE_CLIENT_ID);

    }

    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !request.getParameter("lg_TIERS_PAYANT_ID").equalsIgnoreCase("")) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID").toString();
        new logger().OCategory.info("lg_TIERS_PAYANT_ID" + lg_TIERS_PAYANT_ID);

    }

    OdataManager.initEntityManager();
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
    compteClientManagement OcompteClientManagement = new compteClientManagement(OdataManager);
    lstTCompteClientTiersPayant = OtierspayantManagement.getListCompteClientTiersPayants(search_value, lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID);

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTCompteClientTiersPayant.size()) {
            DATA_PER_PAGE = lstTCompteClientTiersPayant.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTCompteClientTiersPayant.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {

        json = new JSONObject();
        OTComptClient = OcompteClientManagement.getTCompteClient(lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        if (lstTCompteClientTiersPayant == null) {
            json.put("RO", "");
            json.put("RO_TAUX", "");
            json.put("RC1", "");
            json.put("RC1_TAUX", "");
            json.put("RC2", "");
            json.put("RC2_TAUX", "");
        } else if (lstTCompteClientTiersPayant.isEmpty()) {
            json.put("RO", "");
            json.put("RO_TAUX", "");
            json.put("RC1", "");
            json.put("RC1_TAUX", "");
            json.put("RC2", "");
            json.put("RC2_TAUX", "");
        } else if (lstTCompteClientTiersPayant.get(i).getIntPRIORITY().equals(1)) {
            json.put("RO", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrCODEORGANISME());
            json.put("RO_TAUX", lstTCompteClientTiersPayant.get(i).getIntPOURCENTAGE());

        } else if (lstTCompteClientTiersPayant.get(i).getIntPRIORITY().equals(2)) {
            json.put("RC1", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrCODEORGANISME());
            json.put("RC1_TAUX", lstTCompteClientTiersPayant.get(i).getIntPOURCENTAGE());
        } else if (lstTCompteClientTiersPayant.get(i).getIntPRIORITY().equals(3)) {
            json.put("RC2", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrCODEORGANISME());
            json.put("RC2_TAUX", lstTCompteClientTiersPayant.get(i).getIntPOURCENTAGE());

        } else if (lstTCompteClientTiersPayant.get(i).getIntPRIORITY().equals(4)) {
            json.put("RC3", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrCODEORGANISME());
            json.put("RC3_TAUX", lstTCompteClientTiersPayant.get(i).getIntPOURCENTAGE());

        } else {
            json.put("RC1", "");
            json.put("RC1_TAUX", "");
            json.put("RC2", "");
            json.put("RC2_TAUX", "");
        }

        json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_ID", lstTCompteClientTiersPayant.get(i).getLgCOMPTECLIENTTIERSPAYANTID());
        json.put("lg_TIERS_PAYANT_ID", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        json.put("str_CODE_ORGANISME", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrCODEORGANISME());
        json.put("str_NAME", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrNAME());
        json.put("int_POURCENTAGE", lstTCompteClientTiersPayant.get(i).getIntPOURCENTAGE());
        json.put("dbl_PLAFOND_CLIENT", lstTCompteClientTiersPayant.get(i).getDblPLAFOND());
        json.put("dbl_QUOTA_CONSO_MENSUELLE_CLIENT", lstTCompteClientTiersPayant.get(i).getDblQUOTACONSOMENSUELLE());
        json.put("dbl_PLAFOND_CONSO_DIFFRERENCE_CLIENT", lstTCompteClientTiersPayant.get(i).getDblPLAFOND() - lstTCompteClientTiersPayant.get(i).getDblQUOTACONSOMENSUELLE());
        json.put("str_NUMERO_SECURITE_SOCIAL", lstTCompteClientTiersPayant.get(i).getStrNUMEROSECURITESOCIAL());
        json.put("dbl_QUOTA_CONSO_VENTE", lstTCompteClientTiersPayant.get(i).getDblQUOTACONSOVENTE());
        if (OTComptClient != null) {
            json.put("dbl_PLAFOND", OTComptClient.getDblPLAFOND());
            json.put("dbl_QUOTA_CONSO_MENSUELLE", OTComptClient.getDblQUOTACONSOMENSUELLE());
            json.put("dbl_PLAFOND_CONSO_DIFFRERENCE", OTComptClient.getDblPLAFOND() - OTComptClient.getDblQUOTACONSOMENSUELLE());
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTCompteClientTiersPayant.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>