<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TCompteClient"  %>
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
    String lg_COMPTE_CLIENT_ID = "%%", str_CODE_COMPTE_CLIENT = "%%", lg_CLIENT_ID = "%%", lg_TYPE_CLIENT_ID = "%%";

    double dbl_QUOTA_CONSO_MENSUELLE = 0.00, dbl_SOLDE = 0.00;

    Date dt_CREATED, dt_UPDATED;
    json Ojson = new json();
    date key = new date();
    List<dal.TCompteClient> lstTCompteClient = new ArrayList<dal.TCompteClient>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data compte client ");
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
<!-- fin logic de gestion des pages -->

<%    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

    new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        if (request.getParameter("lg_COMPTE_CLIENT_ID").toString().equals("ALL")) {
            lg_COMPTE_CLIENT_ID = "%%";
        } else {
            lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID").toString();
        }

    }

    new logger().OCategory.info("lg_COMPTE_CLIENT_ID   " + lg_COMPTE_CLIENT_ID);

    if (request.getParameter("lg_CLIENT_ID") != null) {
        if (request.getParameter("lg_CLIENT_ID").toString().equals("ALL")) {
            lg_CLIENT_ID = "%%";
        } else {
            lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID").toString();
        }
        new logger().OCategory.info("lg_CLIENT_ID  ==== " + lg_CLIENT_ID);
    }

    if (request.getParameter("lg_TYPE_CLIENT_ID") != null) {
        lg_TYPE_CLIENT_ID = request.getParameter("lg_TYPE_CLIENT_ID").toString();
        new logger().OCategory.info("lg_TYPE_CLIENT_ID  ==== " + lg_TYPE_CLIENT_ID);
    }
    new logger().OCategory.info("lg_TYPE_CLIENT_ID   " + lg_TYPE_CLIENT_ID);
    OdataManager.initEntityManager();
    /*lstTCompteClient = OdataManager.getEm().createQuery("SELECT t FROM TCompteClient t WHERE t.lgCOMPTECLIENTID LIKE ?1 AND t.strCODECOMPTECLIENT LIKE ?2 AND t.strSTATUT LIKE ?3 ").
     setParameter(1, lg_COMPTE_CLIENT_ID)
     .setParameter(2, Os_Search_poste.getOvalue())
     .setParameter(3, commonparameter.statut_enable)
     .getResultList();*/
    new logger().OCategory.info(" avant data loading ... ");
    lstTCompteClient = OdataManager.getEm().createQuery("SELECT t FROM TCompteClient t WHERE t.lgCLIENTID.lgCLIENTID LIKE ?1  AND  t.lgCLIENTID.strFIRSTNAME LIKE ?2  AND t.strSTATUT LIKE ?3  AND t.lgCLIENTID.strSTATUT LIKE ?3  AND t.lgCLIENTID.lgTYPECLIENTID.lgTYPECLIENTID LIKE ?4  ORDER BY t.lgCLIENTID.strFIRSTNAME ASC ").
            setParameter(1, lg_CLIENT_ID)
            .setParameter(2, Os_Search_poste.getOvalue())
            .setParameter(3, commonparameter.statut_enable)
            .setParameter(4, lg_TYPE_CLIENT_ID)
            .getResultList();

    new logger().OCategory.info(" apres data loading ... ");

    new logger().OCategory.info("lstTCompteClient.size()   " + lstTCompteClient.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTCompteClient.size()) {
            DATA_PER_PAGE = lstTCompteClient.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTCompteClient.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTCompteClient.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("str_FIRST_NAME", lstTCompteClient.get(i).getLgCLIENTID().getStrFIRSTNAME());
        json.put("str_LAST_NAME", lstTCompteClient.get(i).getLgCLIENTID().getStrLASTNAME());
        json.put("str_ADRESSE", lstTCompteClient.get(i).getLgCLIENTID().getStrADRESSE());
        json.put("str_NUMERO_SECURITE_SOCIAL", lstTCompteClient.get(i).getLgCLIENTID().getStrNUMEROSECURITESOCIAL());
        json.put("lg_COMPTE_CLIENT_ID", lstTCompteClient.get(i).getLgCOMPTECLIENTID());
        json.put("lg_CLIENT_ID", lstTCompteClient.get(i).getLgCLIENTID().getLgCLIENTID());
        json.put("dbl_CAUTION", lstTCompteClient.get(i).getDblCAUTION());
        json.put("str_CODE_INTERNE", lstTCompteClient.get(i).getLgCLIENTID().getStrCODEINTERNE());
        json.put("dbl_QUOTA_CONSO_MENSUELLE", lstTCompteClient.get(i).getDblQUOTACONSOMENSUELLE());
        json.put("dbl_SOLDE", lstTCompteClient.get(i).getDecBalance());
        json.put("str_CODE_POSTAL", lstTCompteClient.get(i).getLgCLIENTID().getStrCODEPOSTAL());
        json.put("str_SEXE", lstTCompteClient.get(i).getLgCLIENTID().getStrSEXE());
        json.put("lg_VILLE_ID", lstTCompteClient.get(i).getLgCLIENTID().getLgVILLEID().getStrName());

        dt_CREATED = lstTCompteClient.get(i).getDtCREATED();
        if (dt_CREATED != null) {
            json.put("dt_CREATED", key.DateToString(dt_CREATED, key.formatterOrange));
        }

        dt_UPDATED = lstTCompteClient.get(i).getDtUPDATED();
        if (dt_UPDATED != null) {
            json.put("dt_UPDATED", key.DateToString(dt_UPDATED, key.formatterOrange));
        }
        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTCompteClient.size() + " \",\"results\":" + arrayObj.toString() + "})";
    // new logger().OCategory.info("result   ----  " + resut);
%>

<%= result%>