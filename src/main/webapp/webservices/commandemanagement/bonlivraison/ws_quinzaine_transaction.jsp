
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="org.json.JSONObject"%>
<%@page import="com.asc.prestige2.business.bonlivraisons.concrete.PrestigeBLService"%>
<%@page import="com.asc.prestige2.business.bonlivraisons.BLService"%>
<%@page import="java.text.SimpleDateFormat"%>
<% 
  
  BLService _service = new PrestigeBLService();
  String mode = "", lg_QUINZAINE_ID = "",  dt_START_DATE = "", dt_END_DATE = "", str_GROSSISTE_LIBELLE = "", lg_GROSSISTE_ID = "" , result = "";
  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
  
  if(request.getParameter("mode") != null){
       mode = request.getParameter("mode");
  }
  
   if(request.getParameter("lg_QUINZAINE_ID") != null){
       lg_QUINZAINE_ID = request.getParameter("lg_QUINZAINE_ID");
  }
  
   if(request.getParameter("lg_GROSSISTE_ID") != null){
       lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
  }
  
   
  if(request.getParameter("dt_START_DATE") != null){
       dt_START_DATE = request.getParameter("dt_START_DATE");
  }
  
if(request.getParameter("dt_END_DATE") != null){
       dt_END_DATE = request.getParameter("dt_END_DATE");
 }
 

if(request.getParameter("str_GROSSISTE_LIBELLE") != null){
       str_GROSSISTE_LIBELLE = request.getParameter("str_GROSSISTE_LIBELLE");
 }

if(mode.equalsIgnoreCase("create")){
   boolean created = _service.createQuinzaine(lg_GROSSISTE_ID, formatter.parse(dt_START_DATE), formatter.parse(dt_END_DATE));
   JSONObject  json = new JSONObject();
   json.put("success", created);
           
   result = json.toString();
}else if(mode.equalsIgnoreCase("delete")){
   boolean removed = _service.deleteQuinzaine(lg_QUINZAINE_ID);
   JSONObject  json = new JSONObject();
   json.put("success", removed);
   result = json.toString();
}else if(mode.equalsIgnoreCase("update")){
    boolean updated;
    System.out.printf("mode: %s, lg_GROSSISTE_ID: %s, dt_START_DATE: %s, dt_END_DATE: %s\n", mode, lg_GROSSISTE_ID, formatter.parse(dt_START_DATE),formatter.parse(dt_END_DATE) );
    
    Map<String, Object> record = new HashMap<String, Object>();
    record.put("lg_GROSSISTE_ID",lg_GROSSISTE_ID);
    record.put("dt_DATE_START",formatter.parse(dt_START_DATE));
    record.put("dt_DATE_END",formatter.parse(dt_END_DATE));
    updated = _service.updateQuinzaine(lg_QUINZAINE_ID, record);
   JSONObject  json = new JSONObject();
   json.put("success", updated);
           
   result = json.toString();
}

%>


<%= result   %>
