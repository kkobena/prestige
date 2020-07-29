<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Calendar"%>
<%@page import="bll.report.Dashboard"%>
<%@page import="java.util.Date"%>
<%@page import="toolkits.utils.date"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.report.StatisticsFamilleArticle"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>

<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%@page import="java.util.ArrayList"%>

<%@page import="java.util.List"%>




<%
    dataManager OdataManager = new dataManager();
    Dashboard dashboard = new Dashboard(OdataManager);
    JSONArray data = dashboard.getCaGrapheData();
    int _thisyear = Integer.valueOf(date.FORMATTERYEAR.format(new Date()));

    JSONObject json = new JSONObject();
    long janv = 0, fev = 0, mars = 0, mai = 0, avril = 0, juin = 0, juillet = 0, aout = 0, sept = 0, oct = 0, nov = 0, dec = 0;

    for (int i = 0; i < data.length(); i++) {
        JSONObject object = data.getJSONObject(i);

        if (object.get("MONTH").equals("01/" + _thisyear)) {

            janv = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("02/" + _thisyear)) {

            fev = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("03/" + _thisyear)) {

            mars = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("04/" + _thisyear)) {

            avril = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("05/" + _thisyear)) {

            mai = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("06/" + _thisyear)) {

            juin = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("07/" + _thisyear)) {

            juillet = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("08/" + _thisyear)) {

            aout = object.getLong("MONTHCA");
        }
        if (object.get("MONTH").equals("09/" + _thisyear)) {

            sept = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("10/" + _thisyear)) {

            oct = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("11/" + _thisyear)) {

            nov = object.getLong("MONTHCA");
        } 
        if (object.get("MONTH").equals("12/" + _thisyear)) {

            dec = object.getLong("MONTHCA");
        } 
    }
    json.put("jan", janv);
    json.put("fev", fev);
    json.put("mars", mars);
    json.put("avril", avril);
    json.put("mai", mai);
    json.put("juin", juin);
    json.put("juillet", juillet);
    json.put("aout", aout);
    json.put("sept", sept);
    json.put("oct", oct);
    json.put("nov", nov);
    json.put("dec", dec);

%>

<%= json%>