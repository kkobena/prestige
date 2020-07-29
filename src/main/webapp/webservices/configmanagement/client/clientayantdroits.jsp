<%@page import="bll.configManagement.ayantDroitManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TAyantDroit"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>          
<%@page import="org.json.JSONArray"  %> 
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();
    List<TAyantDroit> lstTAyantDroit = new ArrayList<dal.TAyantDroit>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data ayant droit ---");
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

<%    String lg_CLIENT_ID = "%%";

    if (request.getParameter("lg_CLIENT_ID") != null) {
        lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
        new logger().OCategory.info("lg_CLIENT_ID " + lg_CLIENT_ID);
    }

    OdataManager.initEntityManager();
    ayantDroitManagement OayantDroitManagement = new ayantDroitManagement(OdataManager);
    lstTAyantDroit = OayantDroitManagement.getListeAyantDroitByNameClient("", "%%", lg_CLIENT_ID);

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTAyantDroit.size()) {
            DATA_PER_PAGE = lstTAyantDroit.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTAyantDroit.size() - (DATA_PER_PAGE * (pgInt)));
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
        try {
            OdataManager.getEm().refresh(lstTAyantDroit.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        json.put("str_CODE_INTERNE", lstTAyantDroit.get(i).getStrCODEINTERNE());

        json.put("str_NUMERO_SECURITE_SOCIAL", lstTAyantDroit.get(i).getStrNUMEROSECURITESOCIAL());
        json.put("str_FIRST_LAST_NAME", lstTAyantDroit.get(i).getStrFIRSTNAME() + " " + lstTAyantDroit.get(i).getStrLASTNAME());
        json.put("dt_NAISSANCE", key.DateToString(lstTAyantDroit.get(i).getDtNAISSANCE(), key.formatterShort));

        json.put("str_SEXE", lstTAyantDroit.get(i).getStrSEXE());
        arrayObj.put(json);

        data.put("data", arrayObj);
        data.put("total", lstTAyantDroit.size());
       
    }

%>

<%= data%>