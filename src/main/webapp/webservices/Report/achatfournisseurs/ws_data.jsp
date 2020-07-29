<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="dal.TBonLivraisonDetail"%>
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
<%    int DATA_PER_PAGE = 20, count = 0, pages_curr = 0;

%>
<%    String action = request.getParameter("action");
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


<%    dataManager OdataManager = new dataManager();
    List<TBonLivraisonDetail> lstdetails = new ArrayList<TBonLivraisonDetail>();
    Date dtstart=date.formatterMysqlShort.parse(date.formatterMysqlShort.format(new Date()));
    Date dtend=new Date();
    String dt_start = "";
    String dt_end = "";
    String search_value = "", lg_ARTICLE_ID = "";
    StatisticsFamilleArticle familleArticle = new StatisticsFamilleArticle(OdataManager);
    if (request.getParameter("dt_start_Articlevendu") != null && !"".equals(request.getParameter("dt_start_Articlevendu"))) {
        
dtstart =java.sql.Date.valueOf(request.getParameter("dt_start_Articlevendu"));
    }
    if (request.getParameter("dt_end_Articlevendu") != null && !"".equals(request.getParameter("dt_end_Articlevendu"))) {
        dt_end = request.getParameter("dt_end_Articlevendu") + " 23:59:59";
    dtend=   date.formatterMysql.parse(dt_end);
    }

    if (request.getParameter("search_value") != null && !"".equals(request.getParameter("search_value"))) {
        search_value = request.getParameter("search_value");
    }
    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
           
    lstdetails = familleArticle.getABonLivraisonDetails(dtstart, dtend,  search_value); 

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


<%    JSONArray arrayObj = new JSONArray();
    JSONObject data = new JSONObject();
    for (int i = pgInt; i < pgInt_Last; i++) {
        JSONObject json = new JSONObject();

        json.put("id", lstdetails.get(i).getLgBONLIVRAISONDETAIL()); 
       
        json.put("str_Libelle_Produit", lstdetails.get(i).getLgFAMILLEID().getStrNAME());
       json.put("str_LIBELLE", lstdetails.get(i).getLgFAMILLEID().getLgFAMILLEARTICLEID().getStrLIBELLE());
        json.put("QTECMD", lstdetails.get(i).getIntQTECMDE());
        json.put("QTEUG", lstdetails.get(i).getIntQTEUG());
        json.put("QTERECU", lstdetails.get(i).getIntQTERECUE()); 
        json.put("QTEMANQUANT", lstdetails.get(i).getIntQTEMANQUANT());
        json.put("PRIXACHAT", lstdetails.get(i).getIntPAF());
         json.put("MONTANT", lstdetails.get(i).getIntPAF()*lstdetails.get(i).getIntQTERECUE());
        json.put("OPERATEUR", lstdetails.get(i).getLgBONLIVRAISONID().getLgUSERID().getStrFIRSTNAME().substring(0, 1).toUpperCase()+". "+lstdetails.get(i).getLgBONLIVRAISONID().getLgUSERID().getStrLASTNAME());
        json.put("DATEACHAT", date.formatterShort.format(lstdetails.get(i).getDtCREATED())); 
      
        

        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", lstdetails.size());
%>

<%= data%>