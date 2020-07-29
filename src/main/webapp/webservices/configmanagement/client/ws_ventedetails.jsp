<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.configManagement.grossisteManagement"%>
<%@page import="dal.TGrossiste"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
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


<%
    dataManager OdataManager = new dataManager();
    List<TPreenregistrementDetail> lstTGrossiste = new ArrayList<TPreenregistrementDetail>();

%>


<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data grossiste ");
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

<%    String IDVENTE = "", search_value = "%%";

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("IDVENTE") != null && !request.getParameter("IDVENTE").equalsIgnoreCase("")) {
        IDVENTE = request.getParameter("IDVENTE");
       
    }
    if("".equals(search_value)){
        search_value = "%%";
    }

    OdataManager.initEntityManager();
     clientManagement m = new clientManagement(OdataManager);
    
    lstTGrossiste = m.getDetailsByVente(IDVENTE,search_value);
    


%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTGrossiste.size()) {
            DATA_PER_PAGE = lstTGrossiste.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTGrossiste.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
JSONObject data=new JSONObject();
    for (int i = pgInt; i < pgInt_Last; i++) {
      JSONObject json = new JSONObject();

        json.put("ID", lstTGrossiste.get(i).getLgPREENREGISTREMENTDETAILID());

        json.put("CIP", lstTGrossiste.get(i).getLgFAMILLEID().getIntCIP());
        json.put("NAME",lstTGrossiste.get(i).getLgFAMILLEID().getStrNAME());  
        json.put("MONTANTVENTE", lstTGrossiste.get(i).getIntPRICE());
        json.put("PU", lstTGrossiste.get(i).getIntPRICEUNITAIR());
        json.put("QTY", lstTGrossiste.get(i).getIntQUANTITY());
        
        arrayObj.put(json);
    }
    data.put("data", arrayObj);
    data.put("total",arrayObj.length() );
%>

<%= data%> 