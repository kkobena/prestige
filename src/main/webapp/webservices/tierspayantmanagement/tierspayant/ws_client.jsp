<%-- 
    Document   : list
    Created on : 25 oct. 2015, 08:07:30
    Author     : KKOFFI
--%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TClient"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="bll.facture.reglementManager"%>
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
<%    int DATA_PER_PAGE = 10, count = 0, pages_curr = 0;

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
  
    List<TClient> lstdetails = new ArrayList<TClient>();
    
    String query = "%%",lg_TIERS_PAYANT_ID="";
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, null);
   if (request.getParameter("lg_TIERS_PAYANT_ID") != null && !"".equals(request.getParameter("lg_TIERS_PAYANT_ID"))) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
    }

 if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        query = request.getParameter("query");
    }
 
    lstdetails = OPreenregistrement.getAllClients(query,lg_TIERS_PAYANT_ID);

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
        json.put("lg_CLIENT_ID", lstdetails.get(i).getLgCLIENTID());
        json.put("str_FIRST_LAST_NAME", lstdetails.get(i).getStrFIRSTNAME().trim()+" "+lstdetails.get(i).getStrLASTNAME().trim());
         json.put("str_NUMERO_SECURITE_SOCIAL", lstdetails.get(i).getStrNUMEROSECURITESOCIAL().trim());
       json.put("str_CODE_INTERNE", lstdetails.get(i).getStrCODEINTERNE());
          json.put("dt_NAISSANCE", lstdetails.get(i).getDtCREATED()!=null?date.DateToString(lstdetails.get(i).getDtNAISSANCE(), date.formatterShort):"");
         json.put("str_SEXE", lstdetails.get(i).getStrSEXE()!=null?lstdetails.get(i).getStrSEXE():"");
        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total", lstdetails.size());
%>

<%= data%>