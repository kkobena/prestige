<%@page import="java.util.Calendar"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="bll.report.StatisticsFamilleArticle"%>
<%@page import="toolkits.utils.date"%>
<%@page import="dal.dataManager"%> 
<%@page import="bll.report.StatisticSales"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%   
     int DATA_PER_PAGE = 20, count = 0, pages_curr = 0;
    dataManager OManager = new dataManager();
   
    
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
   
      if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
   
    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");
         
    }

   

   if("".equals(dt_start)){
        dt_start=date.formatterMysqlShort.format(new Date());
    }
    StatisticsFamilleArticle statisticSales = new StatisticsFamilleArticle(OManager);
    Calendar calendar=Calendar.getInstance();
    calendar.set(Integer.valueOf(dt_start.split("-")[0]), Integer.valueOf(dt_start.split("-")[1]), Integer.valueOf(dt_start.split("-")[2])); 
     calendar.add(Calendar.YEAR, -1);
     String periode=date.formatterMysqlShort.format(calendar.getTime());
    
    
   /* JSONArray list= statisticSales.getFamilleCA_Data(search_value,dt_start ,periode);*/
     System.out.println("periode +"+periode+" dt_start "+dt_start); 
     JSONArray list= statisticSales.getFamilleCA_Data(search_value,dt_start ,periode);
     
  //  System.out.println("JSONArray  ------------ "+list);


%>

<%    try {
        if (DATA_PER_PAGE > list.length()) {
            DATA_PER_PAGE = list.length();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (list.length() - (DATA_PER_PAGE * (pgInt))); 
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONObject jSONObject = new JSONObject();

  
   
   /* for (int i = pgInt; i < pgInt_Last; i++) {
        JSONObject json = list.getJSONObject(i);

arrayObj.put(json);
    }*/
    jSONObject.put("data", list);
    jSONObject.put("total", list.length()); 
 
%>

<%= jSONObject%>