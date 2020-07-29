<%@page import="toolkits.utils.date"%>
<%@page import="dal.dataManager"%>
<%@page import="bll.report.StatisticSales"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%   
     int DATA_PER_PAGE = 20, count = 0, pages_curr = 0;
    dataManager OManager = new dataManager();
    JSONArray data = new JSONArray();
    
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


<%    String dt_start = "",search_value="";
    String dt_end = "";
      if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
   
    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");
        
    }

    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente") ;
       
    }

   if("".equals(dt_start)){
        dt_start=date.formatterMysqlShort.format(new Date());
    }
    StatisticSales statisticSales = new StatisticSales(OManager);
    data = statisticSales.getSalesByOperateur(dt_start, dt_end,search_value); 
   


%>

<%    try {
        if (DATA_PER_PAGE > data.length()) {
            DATA_PER_PAGE = data.length();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (data.length() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONObject jSONObject = new JSONObject();
    jSONObject.put("data", data);
    jSONObject.put("total", data.length()); 
 
%>

<%= jSONObject%>