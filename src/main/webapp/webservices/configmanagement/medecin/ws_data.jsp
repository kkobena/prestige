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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_MEDECIN_ID = "%%",str_CODE_INTERNE = "%%" , str_FIRST_NAME = "%%", str_LAST_NAME = "%%", str_PHONE = "%%", 
            str_MAIL = "%%", str_SEXE = "%%", str_ADRESSE = "%%", str_STATUT = "%%",
            str_Commentaire = "%%",lg_VILLE_ID = "%%", lg_SPECIALITE_ID= "%%";
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    json Ojson = new json();
    List<TMedecin> lstTMedecin = new ArrayList<TMedecin>();
%>

<%
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

<%    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_MEDECIN_ID") != null) {
        if (request.getParameter("lg_MEDECIN_ID").toString().equals("ALL")) {
            lg_MEDECIN_ID = "%%";
        } else {
            lg_MEDECIN_ID = request.getParameter("lg_MEDECIN_ID").toString();
        }

    }

    OdataManager.initEntityManager();
    lstTMedecin = OdataManager.getEm().createQuery("SELECT t FROM TMedecin t WHERE t.lgMEDECINID LIKE ?1 AND t.strFIRSTNAME LIKE ?2 AND t.strSTATUT LIKE ?3 ").
            setParameter(1, lg_MEDECIN_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, commonparameter.statut_enable)
            .getResultList();
    new logger().OCategory.info(lstTMedecin.size());
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
        try {
            OdataManager.getEm().refresh(lstTMedecin.get(i));
        } catch (Exception er) {
        }

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
       
        lg_VILLE_ID = lstTMedecin.get(i).getLgVILLEID().getStrName();
        if (lg_VILLE_ID != null) {
            json.put("lg_VILLE_ID", lg_VILLE_ID);
        }
        
        lg_SPECIALITE_ID = lstTMedecin.get(i).getLgSPECIALITEID().getStrLIBELLESPECIALITE();
        if (lg_SPECIALITE_ID != null) {
            json.put("lg_SPECIALITE_ID", lg_SPECIALITE_ID);
        }

        json.put("str_STATUT", lstTMedecin.get(i).getStrSTATUT());

        dt_CREATED = lstTMedecin.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTMedecin.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTMedecin.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);
%>

<%= result%>