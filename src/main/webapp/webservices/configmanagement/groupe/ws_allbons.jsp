<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="util.DateConverter"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TGroupeTierspayant"%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>

<%@page import="java.util.Date"%>
<%@page import="toolkits.utils.date"%>

<%@page import="dal.dataManager"%>


<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%@page import="java.util.ArrayList"%>

<%@page import="java.util.List"%>

<%
    dataManager OdataManager = new dataManager();
    String lg_TIERS_PAYANT_ID = "";
    OdataManager.initEntityManager();
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());
   String search_value = "",dt_start=date.formatterMysqlShort.format(new Date()),dt_end=date.formatterMysqlShort.format(new Date());
     if (request.getParameter("dt_start") != null && !"".equals(request.getParameter("dt_start"))) {
        dt_start = request.getParameter("dt_start");
    }
    
    if (request.getParameter("dt_end") != null && !"".equals(request.getParameter("dt_end"))) {
        dt_end = request.getParameter("dt_end");
    }
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !"".equals(request.getParameter("lg_TIERS_PAYANT_ID"))) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID") ;
    }
    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
    int start = Integer.valueOf(request.getParameter("start"));
    int limit = Integer.valueOf(request.getParameter("limit"));
    JSONArray arrayObj = new JSONArray();

    List<TPreenregistrementCompteClientTiersPayent> lis = groupeCtl.findAllBons(false, dt_start, dt_end, start, limit, lg_TIERS_PAYANT_ID, search_value);
    int count = groupeCtl.allBonsCount(dt_start, dt_end, lg_TIERS_PAYANT_ID, search_value);
    for (TPreenregistrementCompteClientTiersPayent obj : lis) {
        JSONObject json = new JSONObject();
       json.put("lg_PCMT_ID", obj.getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());
        json.put("REFBON", obj.getStrREFBON());
        json.put("AMOUNT", obj.getIntPRICE());
           json.put("AMOUNT_VENTE", obj.getLgPREENREGISTREMENTID().getIntPRICE());
           try { 
               json.put("CLIENT_FULLNAME", obj.getLgPREENREGISTREMENTID().getClient().getStrFIRSTNAME()+" "+obj.getLgPREENREGISTREMENTID().getClient().getStrLASTNAME());  
               } catch (Exception e) {
               }
          
           json.put("DATE_VENTE", DateConverter.convertDateToDD_MM_YYYY_HH_mm(obj.getLgPREENREGISTREMENTID().getDtUPDATED()));
       json.put("isChecked", false);
       
        
        arrayObj.put(json);

    }

    JSONObject data = new JSONObject();

    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>