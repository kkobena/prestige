<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="org.json.JSONObject"%>
<%@page import="toolkits.utils.Util"%>


<%
  
   
    
    JSONObject data = Util.getYearsInterval();

   
%>

<%= data%>