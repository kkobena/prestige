<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>

<%
            JSONArray arrayObj = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("text", "Menu 1");
            json.put("cls", "folder");//leaf:true
           


            //----------
            JSONArray arrayObj_sub = new JSONArray();
            JSONObject json_sub = new JSONObject();

            json_sub.put("id", "basic-panels");
            json_sub.put("text", "04140404");//leaf:true
            json_sub.put("leaf", "true");
            arrayObj_sub.put(json_sub);

            //----------
            json.put("expanded", "true");
            json.put("children", arrayObj_sub);

            arrayObj.put(json);
%>


<%= arrayObj.toString()%>


