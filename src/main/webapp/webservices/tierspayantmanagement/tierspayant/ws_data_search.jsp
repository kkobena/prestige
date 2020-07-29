<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>

<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%@page import="java.util.ArrayList"%>

<%@page import="java.util.List"%>

<%
  dataManager OdataManager = new dataManager();

    List<TTiersPayant> lstTTiersPayant = new ArrayList<TTiersPayant>();  

%>
<%    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;

%>
<%  
   
    
    String action = request.getParameter("action");
    int pageAsInt = 0;

    try {
        if ((action != null) && action.equals("filltable")) {
        } else {

            String p = request.getParameter("start");

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


<%    String search_value = "";
    
    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
    }

     tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
    lstTTiersPayant = OtierspayantManagement.ShowAllOrOneTierspayant(search_value, "%%", "%%", commonparameter.statut_enable);
  

%>

<%    try {
        if (DATA_PER_PAGE > lstTTiersPayant.size()) {
            DATA_PER_PAGE = lstTTiersPayant.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTTiersPayant.size() - (DATA_PER_PAGE * (pgInt)));
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

        JSONObject json = new JSONObject();
       json.put("lg_TIERS_PAYANT_ID", lstTTiersPayant.get(i).getLgTIERSPAYANTID());
      json.put("str_FULLNAME", lstTTiersPayant.get(i).getStrFULLNAME());
        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", lstTTiersPayant.size());
   
%>

<%= data%>