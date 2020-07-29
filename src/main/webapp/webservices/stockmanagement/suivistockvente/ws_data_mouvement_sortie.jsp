<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="bll.commandeManagement.retourFournisseurManagement"%>
<%@page import="bll.stockManagement.InventaireManager"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TGrossiste"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="dal.TMouvement"%>
<%@page import="bll.teller.SnapshotManager"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TTypeRisque"  %>
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

    json Ojson = new json();

    List<TMouvement> lstTMouvement = new ArrayList<TMouvement>();
    TUser OTUser;

%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data suivi mouvement article");
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

<%    String lg_FAMILLE_ID = "%%", lg_INVENTAIRE_ID = "%%", search_value = "", lg_USER_ID = "%%", P_KEY = "%%", str_TYPE_ACTION = "%%", str_ACTION = "%%";
    String dt_DEBUT = "", dt_FIN = "", lg_ORDER_ID = "%%", lg_GROSSISTE_ID = "%%", lg_PREENREGISTREMENT_ID = "%%", lg_RETOUR_FRS_ID = "%%";
    Date dtFin;
    List<Date> lstDate = new ArrayList<Date>();

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("str_TYPE_ACTION") != null) {
        str_TYPE_ACTION = request.getParameter("str_TYPE_ACTION");
        new logger().OCategory.info("str_TYPE_ACTION " + str_TYPE_ACTION);
    }

    if (request.getParameter("lg_RETOUR_FRS_ID") != null) {
        lg_RETOUR_FRS_ID = request.getParameter("lg_RETOUR_FRS_ID");
        new logger().OCategory.info("lg_RETOUR_FRS_ID " + lg_RETOUR_FRS_ID);
    }

    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("lg_ORDER_ID") != null) {
        lg_ORDER_ID = request.getParameter("lg_ORDER_ID");
        new logger().OCategory.info("lg_ORDER_ID " + lg_ORDER_ID);
    }

    if (request.getParameter("lg_GROSSISTE_ID") != null) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }

    if (request.getParameter("str_ACTION") != null) {
        str_ACTION = request.getParameter("str_ACTION");
        new logger().OCategory.info("str_ACTION " + str_ACTION);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID " + lg_USER_ID);
    }

    if (request.getParameter("P_KEY") != null) {
        P_KEY = request.getParameter("P_KEY");
        new logger().OCategory.info("P_KEY " + P_KEY);
    }

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }
    if (request.getParameter("lg_FAMILLE_ID") != null && request.getParameter("lg_FAMILLE_ID") != "") {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().OCategory.info("lg_FAMILLE_ID " + lg_FAMILLE_ID);
    }

    if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dt_DEBUT = "2015-04-20";
//                new logger().OCategory.info("dt_DEBUT:" + dt_DEBUT);
    }
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
        String Odate = key.DateToString(dtFin, key.formatterMysqlShort2);

        dtFin = key.getDate(Odate, "23:59");
        //String dateJour = key.DateToString(key.getDate(dt_FIN, "00:00"), key.formatterMysqlShort); 
    }
    Date dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
    new logger().OCategory.info("search_value  = " + search_value + "   dt_FIN  " + dt_FIN + "  dt_DEBUT  " + dt_DEBUT + " lg_FAMILLE_ID " + lg_FAMILLE_ID + " dtFin:" + dtFin);

    OdataManager.initEntityManager();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    new logger().OCategory.info("user connecté   " + OTUser.getStrFIRSTNAME());
    SnapshotManager OSnapshotManager = new SnapshotManager(OdataManager, OTUser);
    retourFournisseurManagement OretourFournisseurManagement = new retourFournisseurManagement(OdataManager);
    InventaireManager OInventaireManager = new InventaireManager(OdataManager);
    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager);

    lstTMouvement = OSnapshotManager.listTMouvement(search_value, dtDEBUT, dtFin, lg_FAMILLE_ID, lg_USER_ID, P_KEY, str_TYPE_ACTION, str_ACTION);

   

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTMouvement.size()) {
            DATA_PER_PAGE = lstTMouvement.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTMouvement.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTMouvement.get(i));
        } catch (Exception er) {
        }

        JSONObject json = new JSONObject();

        if (lstTMouvement.size() > 0) {

            if (lstDate.size() == 0) {
                lstDate.add(key.getDate(key.DateToString(lstTMouvement.get(i).getDtCREATED(), key.formatterMysqlShort2), "00:00"));
                json.put("lg_MOUVEMENT_ID", lstTMouvement.get(i).getLgMOUVEMENTID());
                json.put("lg_USER_ID", lstTMouvement.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTMouvement.get(i).getLgUSERID().getStrLASTNAME());
                json.put("lg_FAMILLE_ID", lstTMouvement.get(i).getLgFAMILLEID().getStrNAME());
                try {
                    TGrossiste OTGrossiste = OdataManager.getEm().find(TGrossiste.class, lstTMouvement.get(i).getPKey());
                    json.put("P_KEY", OTGrossiste.getStrDESCRIPTION());
                } catch (Exception e) {
                }

                String OdateDeb = key.DateToString(lstTMouvement.get(i).getDtCREATED(), key.formatterMysqlShort2);
                String OdateFin = key.DateToString(lstTMouvement.get(i).getDtCREATED(), key.formatterMysqlShort2);

                Date DTDEBUT = key.getDate(OdateDeb, "00:00");
                Date DTFIN = key.getDate(OdateFin, "23:59");

                new logger().OCategory.info("OdateDeb " + OdateDeb + " OdateFin " + OdateFin + " DTDEBUT " + DTDEBUT + " DTFIN " + DTFIN);
                json.put("str_TYPE_ACTION", lstTMouvement.get(i).getStrTYPEACTION());
                json.put("str_ACTION", lstTMouvement.get(i).getStrACTION());
                json.put("dt_DAY", date.DateToString(lstTMouvement.get(i).getDtDAY(), date.formatterShort));
               // json.put("int_NUMBER_DEBUT", OSnapshotManager.getQauntityStockJourArticle(lstTMouvement.get(i).getLgFAMILLEID().getStrDESCRIPTION(), DTDEBUT, DTFIN, lstTMouvement.get(i).getLgFAMILLEID().getLgFAMILLEID(), lg_USER_ID, P_KEY, "%%", "%%"));
                json.put("int_NUMBER", lstTMouvement.get(i).getIntNUMBER());
                json.put("int_NUMBERTRANSACTION", OWarehouseManager.getQauntityPerimeByArticle(lstTMouvement.get(i).getLgFAMILLEID().getStrDESCRIPTION(), DTDEBUT, DTFIN, lstTMouvement.get(i).getLgFAMILLEID().getLgFAMILLEID(), lg_GROSSISTE_ID));
                json.put("int_NUMBER_CMDE", OretourFournisseurManagement.getQauntityRetourByArticle(lstTMouvement.get(i).getLgFAMILLEID().getStrDESCRIPTION(), dtDEBUT, dtFin, lstTMouvement.get(i).getLgFAMILLEID().getLgFAMILLEID(), lg_RETOUR_FRS_ID, lg_GROSSISTE_ID));
                json.put("int_NUMBER_BON", OSnapshotManager.getQauntityVenteByArticle(lstTMouvement.get(i).getLgFAMILLEID().getStrDESCRIPTION(), DTDEBUT, DTFIN, lstTMouvement.get(i).getLgFAMILLEID().getLgFAMILLEID(), lg_USER_ID, lg_PREENREGISTREMENT_ID));
                json.put("int_NUMBER_INVENTAIRE", OInventaireManager.getQauntityInventaireByArticle(lstTMouvement.get(i).getLgFAMILLEID().getIntCIP(), DTDEBUT, DTFIN, lg_FAMILLE_ID, lg_USER_ID, lg_INVENTAIRE_ID));

                arrayObj.put(json);
            } else {
                if (!lstDate.get(0).equals(key.getDate(key.DateToString(lstTMouvement.get(i).getDtCREATED(), key.formatterMysqlShort2), "00:00"))) {
                    json.put("lg_MOUVEMENT_ID", lstTMouvement.get(i).getLgMOUVEMENTID());
                    json.put("lg_USER_ID", lstTMouvement.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTMouvement.get(i).getLgUSERID().getStrLASTNAME());
                    json.put("lg_FAMILLE_ID", lstTMouvement.get(i).getLgFAMILLEID().getStrNAME());
                    try {
                        TGrossiste OTGrossiste = OdataManager.getEm().find(TGrossiste.class, lstTMouvement.get(i).getPKey());
                        json.put("P_KEY", OTGrossiste.getStrDESCRIPTION());
                    } catch (Exception e) {
                    }

                    String OdateDeb = key.DateToString(lstTMouvement.get(i).getDtCREATED(), key.formatterMysqlShort2);
                    String OdateFin = key.DateToString(lstTMouvement.get(i).getDtCREATED(), key.formatterMysqlShort2);

                    Date DTDEBUT = key.getDate(OdateDeb, "00:00");
                    Date DTFIN = key.getDate(OdateFin, "23:59");

                    new logger().OCategory.info("OdateDeb " + OdateDeb + " OdateFin " + OdateFin + " DTDEBUT " + DTDEBUT + " DTFIN " + DTFIN);
                    json.put("str_TYPE_ACTION", lstTMouvement.get(i).getStrTYPEACTION());
                    json.put("str_ACTION", lstTMouvement.get(i).getStrACTION());
                    json.put("dt_DAY", date.DateToString(lstTMouvement.get(i).getDtDAY(), date.formatterShort));
                   // json.put("int_NUMBER_DEBUT", OSnapshotManager.getQauntityStockJourArticle(lstTMouvement.get(i).getLgFAMILLEID().getIntCIP(), DTDEBUT, DTFIN, lg_FAMILLE_ID, lg_USER_ID, P_KEY, "%%", "%%"));
                    json.put("int_NUMBER", lstTMouvement.get(i).getIntNUMBER());
                    json.put("int_NUMBERTRANSACTION", OWarehouseManager.getQauntityPerimeByArticle(lstTMouvement.get(i).getLgFAMILLEID().getStrDESCRIPTION(), DTDEBUT, DTFIN, lstTMouvement.get(i).getLgFAMILLEID().getLgFAMILLEID(), lg_GROSSISTE_ID));
                    json.put("int_NUMBER_CMDE", OretourFournisseurManagement.getQauntityRetourByArticle(lstTMouvement.get(i).getLgFAMILLEID().getStrDESCRIPTION(), dtDEBUT, dtFin, lstTMouvement.get(i).getLgFAMILLEID().getLgFAMILLEID(), lg_RETOUR_FRS_ID, lg_GROSSISTE_ID));
                    json.put("int_NUMBER_BON", OSnapshotManager.getQauntityVenteByArticle(lstTMouvement.get(i).getLgFAMILLEID().getStrDESCRIPTION(), DTDEBUT, DTFIN, lstTMouvement.get(i).getLgFAMILLEID().getLgFAMILLEID(), lg_USER_ID, lg_PREENREGISTREMENT_ID));
                    json.put("int_NUMBER_INVENTAIRE", OInventaireManager.getQauntityInventaireByArticle(lstTMouvement.get(i).getLgFAMILLEID().getIntCIP(), DTDEBUT, DTFIN, lg_FAMILLE_ID, lg_USER_ID, lg_INVENTAIRE_ID));

                    arrayObj.put(json);

                    lstDate.clear();
                    lstDate.add(key.getDate(key.DateToString(lstTMouvement.get(i).getDtCREATED(), key.formatterMysqlShort2), "00:00"));
                }
            }

        }

    }

    String result = "({\"total\":\"" + lstTMouvement.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result%>