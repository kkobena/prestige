<%@page import="org.json.JSONObject"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="java.util.List"%>
<%@page import="toolkits.utils.date"%>
<%@page import="dal.dataManager"%>
<%@page import="bll.report.StatisticSales"%>
<%@page import="java.util.Date"%>

<%@page import="org.json.JSONArray"%>

<%
    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;
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


<%    String dt_start = date.formatterMysqlShort.format(new Date());
    String dt_end = date.formatterMysqlShort.format(new Date());

    if (request.getParameter("dt_start_vente") != null && !"".equals(request.getParameter("dt_start_vente"))) {
        dt_start = request.getParameter("dt_start_vente");

    }

    if (request.getParameter("dt_end_vente") != null && !"".equals(request.getParameter("dt_end_vente"))) {
        dt_end = request.getParameter("dt_end_vente");

    }

    StatisticSales statisticSales = new StatisticSales(OManager);
    List<EntityData> lstdetails = statisticSales.getRapportGestionData(dt_start, dt_end);


%>

<%    try {
        if (DATA_PER_PAGE > lstdetails.size()) {
            DATA_PER_PAGE = lstdetails.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstdetails.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONObject jSONObject = new JSONObject();
    JSONArray arrayObj = new JSONArray();

    long depense = 0;
    int j=0;
    for (int i = pgInt; i < pgInt_Last; i++) {
        JSONObject json = new JSONObject();

        json.put("id", i);
        
        json.put("AMOUNT", lstdetails.get(i).getStr_value2());
        json.put("STATUS", lstdetails.get(i).getStr_value3());
        if (Integer.valueOf(lstdetails.get(i).getStr_value3()) == 0   ) {
            json.put("TYPEMVT", "Chiffre d'Affaires"); 
            json.put("LIBELLE", lstdetails.get(i).getStr_value1());
            json.put("DISPLAY", 0);  
            
        } 
        if (Integer.valueOf(lstdetails.get(i).getStr_value3()) == 3) {
            depense += Long.valueOf(lstdetails.get(i).getStr_value2());
            json.put("LIBELLE", lstdetails.get(i).getStr_value1());
             json.put("DISPLAY", 3); 
            json.put("TYPEMVT", "Dépenses"); 
        }
        if (Integer.valueOf(lstdetails.get(i).getStr_value3()) == 1) {
            json.put("LIBELLE", "Achats");
            json.put("TYPEMVT", "Achats"); 
             json.put("DISPLAY", 0); 
        }
        if (Integer.valueOf(lstdetails.get(i).getStr_value3()) == 4) {
            json.put("LIBELLE", lstdetails.get(i).getStr_value1());
            json.put("TYPEMVT", "Règlements"); 
             json.put("DISPLAY", 3); 
        } 
        if (Integer.valueOf(lstdetails.get(i).getStr_value3()) == 2) {
        json.put("LIBELLE", lstdetails.get(i).getStr_value1()); 
            json.put("TYPEMVT", "Marges"); 
             json.put("DISPLAY", 0); 
        }
      
        arrayObj.put(json);
        j++;
    }
 
    jSONObject.put("data", arrayObj);
    
    jSONObject.put("total", lstdetails.size());

%>

<%= jSONObject%>