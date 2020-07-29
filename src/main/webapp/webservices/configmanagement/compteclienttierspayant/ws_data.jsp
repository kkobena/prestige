<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="bll.configManagement.clientManagement"%>
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

<%
    dataManager OdataManager = new dataManager();
    List<TCompteClientTiersPayant> lstTCompteClientTiersPayant = new ArrayList<TCompteClientTiersPayant>();

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data compte client tiers payant");
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

<%    String lg_COMPTE_CLIENT_ID = "%%", search_value = "", lg_CLIENT_ID = "%%", lg_TIERS_PAYANT_ID = "%%";

TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    privilege Oprivilege = new privilege(OdataManager, OTUser);
    boolean BTNDELETE = Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_BT_DELETE);

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    
    if (request.getParameter("query") != null) {
        search_value = request.getParameter("query");
        new logger().OCategory.info("search_value query:" + search_value);
    }
    
    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);
    }
    
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID);
    }
    
    if (request.getParameter("lg_CLIENT_ID") != null) {
        lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
        new logger().OCategory.info("lg_CLIENT_ID " + lg_CLIENT_ID);
    }

    
    OdataManager.initEntityManager();


    /*lstTCompteClientTiersPayant = OdataManager.getEm().createQuery("SELECT t FROM TCompteClientTiersPayant t WHERE t.lgCOMPTECLIENTID.lgCLIENTID.lgCLIENTID LIKE ?1  AND t.strSTATUT LIKE ?3 ").
     setParameter(1, lg_CLIENT_ID)//AND t.strCODECOMPTECLIENT LIKE ?2
     //  .setParameter(2, Os_Search_poste.getOvalue())
     .setParameter(3, commonparameter.statut_enable)
     .getResultList();*/
    lstTCompteClientTiersPayant = new clientManagement(OdataManager).getTiersPayantsByClient(search_value, lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID);

    new logger().OCategory.info(lstTCompteClientTiersPayant.size());
%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTCompteClientTiersPayant.size()) {
            DATA_PER_PAGE = lstTCompteClientTiersPayant.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTCompteClientTiersPayant.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTCompteClientTiersPayant.get(i));
        } catch (Exception er) {
        }

        String Role = "";

        JSONObject json = new JSONObject();

        json.put("str_FIRST_NAME", lstTCompteClientTiersPayant.get(i).getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME());
        json.put("str_LAST_NAME", lstTCompteClientTiersPayant.get(i).getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());
        json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_ID", lstTCompteClientTiersPayant.get(i).getLgCOMPTECLIENTTIERSPAYANTID());
        json.put("lg_TIERS_PAYANT_ID", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrFULLNAME());
        json.put("str_TIERS_PAYANT_NAME", lstTCompteClientTiersPayant.get(i).getLgTIERSPAYANTID().getStrFULLNAME());
        json.put("lg_COMPTE_CLIENT_ID", lstTCompteClientTiersPayant.get(i).getLgCOMPTECLIENTID().getLgCOMPTECLIENTID());
        json.put("lg_CLIENT_ID", lstTCompteClientTiersPayant.get(i).getLgCOMPTECLIENTID().getLgCLIENTID().getLgCLIENTID());
        json.put("int_POURCENTAGE", lstTCompteClientTiersPayant.get(i).getIntPOURCENTAGE());
        json.put("int_PRIORITY", lstTCompteClientTiersPayant.get(i).getIntPRIORITY());
        json.put("dbl_PLAFOND", lstTCompteClientTiersPayant.get(i).getDblPLAFOND());
        json.put("db_PLAFOND_ENCOURS", lstTCompteClientTiersPayant.get(i).getDbPLAFONDENCOURS());
        json.put("db_PLAFOND_ENCOURS", lstTCompteClientTiersPayant.get(i).getDbPLAFONDENCOURS());
        json.put("dbl_QUOTA_CONSO_MENSUELLE", lstTCompteClientTiersPayant.get(i).getDblQUOTACONSOMENSUELLE());
        json.put("dbl_QUOTA_CONSO_VENTE", lstTCompteClientTiersPayant.get(i).getDblQUOTACONSOVENTE());
        json.put("b_IsAbsolute", lstTCompteClientTiersPayant.get(i).getBIsAbsolute());
        json.put("b_CANBEUSE", lstTCompteClientTiersPayant.get(i).getBCANBEUSE());
        json.put("db_CONSOMMATION_MENSUELLE", lstTCompteClientTiersPayant.get(i).getDbCONSOMMATIONMENSUELLE()!=null?lstTCompteClientTiersPayant.get(i).getDbCONSOMMATIONMENSUELLE():0);
        
        json.put("str_NUMERO_SECURITE_SOCIAL",(lstTCompteClientTiersPayant.get(i).getStrNUMEROSECURITESOCIAL()!=null?lstTCompteClientTiersPayant.get(i).getStrNUMEROSECURITESOCIAL():"" ));
        String str_REGIME = "";
        if(lstTCompteClientTiersPayant.get(i).getIntPRIORITY() == 1) {
            str_REGIME = "RO";
        } else {
            str_REGIME = "RC"+(lstTCompteClientTiersPayant.get(i).getIntPRIORITY()-1);
        }
        json.put("BTNDELETE", BTNDELETE);
        json.put("str_REGIME", str_REGIME);
        json.put("dt_CREATED", date.DateToString(lstTCompteClientTiersPayant.get(i).getDtCREATED(), date.formatterShort));

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTCompteClientTiersPayant.size() + " \",\"results\":" + arrayObj.toString() + "})";
   
%>

<%= result%>