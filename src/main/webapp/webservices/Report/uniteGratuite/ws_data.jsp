

<%@page import="org.json.JSONException"%>
<%@page import="bll.report.UgManager"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>

<%@page import="java.util.*"  %>

<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>

<%@page import="org.json.JSONObject"  %>          
<%@page import="org.json.JSONArray"  %> 

<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>



<% 
    dataManager OdataManager = new dataManager();
  
    date key = new date();
%>

<%
    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;
   
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
   OdataManager.initEntityManager();
     String dt_start = date.formatterMysqlShort.format(new Date());
    String dt_end = date.formatterMysqlShort.format(new Date());
    String search_value = "%%", lgGrossiste = "%%";
if (request.getParameter("lgGrossiste") != null && !"".equals(request.getParameter("lgGrossiste"))) {
        lgGrossiste = request.getParameter("lgGrossiste");
    }
    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");
    }
    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");

    }

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    int _page =  Integer.valueOf(request.getParameter("page")); 
   

    
 UgManager ugManager = new UgManager(OdataManager);
 
   JSONObject js= ugManager.getUgByArticle(search_value, lgGrossiste, dt_start, dt_end, start,limit, _page); 
   JSONArray array =new  JSONArray();
   try {
         array =js.getJSONArray("data");    
       } catch (JSONException e) {
           e.printStackTrace();
       }
       
 
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > array.length()) {
            DATA_PER_PAGE = array.length();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (array.length()  - (DATA_PER_PAGE * (pgInt)));
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
        
JSONObject json=array.getJSONObject(i);
    
     arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", array.length());
%>

<%= data%>