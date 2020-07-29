<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
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
<%
    dataManager OdataManager = new dataManager();
    List<TCompteClientTiersPayant> lstTCompteClientTiersPayant = new ArrayList<TCompteClientTiersPayant>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data compte client tiers payant");
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
<!-- fin logic de gestion des pages -->

<%    String lg_COMPTE_CLIENT_ID = "%%"; 

    

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        
    }

    

    OdataManager.initEntityManager();

    lstTCompteClientTiersPayant = new clientManagement(OdataManager).getTiersPayantsByClient("", lg_COMPTE_CLIENT_ID, "%%"); 

%>

<%
//Filtre de pagination
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
    JSONObject data = new JSONObject();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTCompteClientTiersPayant.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();
        json.put("str_TIERS_PAYANT_NAME", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrFULLNAME());
        json.put("int_POURCENTAGE", lstTCompteClientTiersPayant.get(i).getIntPOURCENTAGE());
         json.put("lg_TIERS_PAYANT_ID", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        String str_REGIME = "";
        if (lstTCompteClientTiersPayant.get(i).getIntPRIORITY() == 1) {
            str_REGIME = "RO";
        } else {
            str_REGIME = "RC" + (lstTCompteClientTiersPayant.get(i).getIntPRIORITY() - 1);
        }

        json.put("str_REGIME", str_REGIME);

        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", lstTCompteClientTiersPayant.size());
    
%>

<%= data%>