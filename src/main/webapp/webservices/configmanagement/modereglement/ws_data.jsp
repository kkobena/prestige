<%@page import="dal.TMedecin"%>
<%@page import="dal.TGroupeFamille"%>
<%@page import="dal.dataManager"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>          
<%@page import="org.json.JSONArray"  %> 
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />

<%!

   dataManager OdataManager = new dataManager();
%>


<%
     List<TMedecin> lstTMedecin = new ArrayList<TMedecin>();
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data medecin");
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
  String  lg_TYPE_REGLEMENT_ID="";
    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null && request.getParameter("lg_TYPE_REGLEMENT_ID") != "") {
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");
        new logger().OCategory.info("lg_TYPELITIGE_ID " + lg_TYPE_REGLEMENT_ID);
    }

    
    
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTMedecin.size()) {
            DATA_PER_PAGE = lstTMedecin.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTMedecin.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
       
        JSONObject json = new JSONObject();
           // TGroupeFamille    OTGroupeFamille = lstTMedecin.get(i);

        json.put("lg_MEDECIN_ID", lstTMedecin.get(i).getLgMEDECINID());
        json.put("str_CODE_INTERNE", lstTMedecin.get(i).getStrCODEINTERNE());
        json.put("str_FIRST_NAME", lstTMedecin.get(i).getStrFIRSTNAME());
        json.put("str_LAST_NAME", lstTMedecin.get(i).getStrLASTNAME());
        json.put("str_FIRST_LAST_NAME", lstTMedecin.get(i).getStrFIRSTNAME() + " " +lstTMedecin.get(i).getStrLASTNAME());
        json.put("str_ADRESSE", lstTMedecin.get(i).getStrADRESSE());
        json.put("str_PHONE", lstTMedecin.get(i).getStrPHONE());
        json.put("str_MAIL", lstTMedecin.get(i).getStrMAIL());
        json.put("str_SEXE", lstTMedecin.get(i).getStrSEXE());
        json.put("str_Commentaire", lstTMedecin.get(i).getStrCommentaire());
       
       

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTMedecin.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>