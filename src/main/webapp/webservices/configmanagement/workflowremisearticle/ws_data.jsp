<%@page import="dal.TWorkflowRemiseArticle"%>
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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
   String lg_WORKFLOW_REMISE_ARTICLE_ID ="%%", str_DESCRIPTION ="%%",str_CODE_GRILLE_VO ="%%" , str_CODE_GRILLE_VNO="%%" ;
   int str_CODE_REMISE_ARTICLE  ;
            
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    json Ojson = new json();

    List<dal.TWorkflowRemiseArticle> lstTWorkflowRemiseArticle = new ArrayList<dal.TWorkflowRemiseArticle>();

%>


<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data workfloremisearticle ");
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

<%    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_WORKFLOW_REMISE_ARTICLE_ID") != null) {
        if (request.getParameter("lg_WORKFLOW_REMISE_ARTICLE_ID").toString().equals("ALL")) {
            lg_WORKFLOW_REMISE_ARTICLE_ID = "%%";
        } else {
            lg_WORKFLOW_REMISE_ARTICLE_ID = request.getParameter("lg_WORKFLOW_REMISE_ARTICLE_ID").toString();
        }

    }
    new logger().OCategory.info("lg_WORKFLOW_REMISE_ARTICLE_ID  "+lg_WORKFLOW_REMISE_ARTICLE_ID);
//
    
    OdataManager.initEntityManager();
    lstTWorkflowRemiseArticle = OdataManager.getEm().createQuery("SELECT t FROM TWorkflowRemiseArticle t WHERE t.lgWORKFLOWREMISEARTICLEID LIKE ?1 AND t.strDESCRIPTION  LIKE ?2 AND t.strSTATUT LIKE ?3").
            setParameter(1, lg_WORKFLOW_REMISE_ARTICLE_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTWorkflowRemiseArticle.size());

    
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTWorkflowRemiseArticle.size()) {
            DATA_PER_PAGE = lstTWorkflowRemiseArticle.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;
 
    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTWorkflowRemiseArticle.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTWorkflowRemiseArticle.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("lg_WORKFLOW_REMISE_ARTICLE_ID", lstTWorkflowRemiseArticle.get(i).getLgWORKFLOWREMISEARTICLEID());

        json.put("str_DESCRIPTION", lstTWorkflowRemiseArticle.get(i).getStrDESCRIPTION());
        json.put("str_CODE_REMISE_ARTICLE", lstTWorkflowRemiseArticle.get(i).getStrCODEREMISEARTICLE());
         
       
        str_CODE_GRILLE_VO = String.valueOf(lstTWorkflowRemiseArticle.get(i).getStrCODEGRILLEVO());         
        if (str_CODE_GRILLE_VO != null){
            json.put("str_CODE_GRILLE_VO", str_CODE_GRILLE_VO);
        }
        
        str_CODE_GRILLE_VNO = String.valueOf(lstTWorkflowRemiseArticle.get(i).getStrCODEGRILLEVNO());
        if (str_CODE_GRILLE_VNO != null){
            json.put("str_CODE_GRILLE_VNO", str_CODE_GRILLE_VNO);
        }       
        json.put("str_STATUT", lstTWorkflowRemiseArticle.get(i).getStrSTATUT());

        dt_CREATED = lstTWorkflowRemiseArticle.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTWorkflowRemiseArticle.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
   // String result = "({\"total\":\"" + lstTWorkflowRemiseArticle.size() + " \",\"results\":" + arrayObj.toString() + "})";
    String result = "({\"total\":\"" + lstTWorkflowRemiseArticle.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>