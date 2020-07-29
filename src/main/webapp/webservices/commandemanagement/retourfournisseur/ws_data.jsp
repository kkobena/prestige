<%@page import="bll.Util"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TRetourFournisseurDetail"%>
<%@page import="bll.commandeManagement.retourFournisseurManagement"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRetourFournisseur"  %>
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



<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();

    List<TRetourFournisseur> lstTRetourFournisseur = new ArrayList<TRetourFournisseur>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans webservices retour fournisseur");
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

<%    String lg_RETOUR_FRS_ID = "%%", search_value = "", lg_GROSSISTE_ID = "%%";
    String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin;
    Date dtDEBUT, dtFin;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    boolean BTNDELETE = Util.isAllowed(OdataManager.getEm(), Util.ACTIONDELETERETOUR, OTUser.getTRoleUserCollection().stream().findFirst().get().getLgROLEID().getLgROLEID());
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }

    if (request.getParameter("lg_RETOUR_FRS_ID") != null) {
        lg_RETOUR_FRS_ID = request.getParameter("lg_RETOUR_FRS_ID");
        new logger().OCategory.info("lg_RETOUR_FRS_ID " + lg_RETOUR_FRS_ID);
    }

    if (request.getParameter("lg_GROSSISTE_ID") != null && !request.getParameter("lg_GROSSISTE_ID").equals("")) {
        lg_GROSSISTE_ID = request.getParameter("lg_GROSSISTE_ID");
        new logger().OCategory.info("lg_GROSSISTE_ID " + lg_GROSSISTE_ID);
    }

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
        OdateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
        OdateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
    }

    dtFin = key.getDate(OdateFin, "23:59");
    //OdateFin = key.DateToString(dtFin, key.formatterMysql);
    new logger().OCategory.info("dtFin *** " + dtFin + " OdateFin *** " + OdateFin);
    if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dtDEBUT = date.GetDebutMois();
        OdateDebut = key.DateToString(dtDEBUT, key.formatterMysqlShort2);
    } else {
        dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
        OdateDebut = key.DateToString(dtDEBUT, key.formatterMysqlShort2);

    }
    dtDEBUT = key.getDate(OdateDebut, "00:00");
    //OdateDebut = key.DateToString(dtDEBUT, key.formatterMysql);

    new logger().OCategory.info("dtDEBUT ---- " + dtDEBUT + " OdateDebut ---- " + OdateDebut);

    OdataManager.initEntityManager();
    retourFournisseurManagement OretourFournisseurManagement = new retourFournisseurManagement(OdataManager, OTUser);

    lstTRetourFournisseur = OretourFournisseurManagement.getAllTRetourFournisseur(search_value, lg_RETOUR_FRS_ID, dtDEBUT, dtFin, lg_GROSSISTE_ID);

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTRetourFournisseur.size()) {
            DATA_PER_PAGE = lstTRetourFournisseur.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTRetourFournisseur.size() - (DATA_PER_PAGE * (pgInt)));
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
            OdataManager.getEm().refresh(lstTRetourFournisseur.get(i));

        } catch (Exception er) {
        }

        List<TRetourFournisseurDetail> lstTRetourFournisseurDetail = new ArrayList<TRetourFournisseurDetail>();
        lstTRetourFournisseurDetail = OretourFournisseurManagement.getTRetourFournisseurDetail(lstTRetourFournisseur.get(i).getLgRETOURFRSID());

        int nb = 0;
        String str_Product = "";

        for (int k = 0; k < lstTRetourFournisseurDetail.size(); k++) {

            // str_Product = "<b>" + lstTRetourFournisseurDetail.get(k).getLgFAMILLEID().getIntCIP() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + lstTRetourFournisseurDetail.get(k).getLgFAMILLEID().getStrNAME() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + new conversion().AmountFormat(lstTRetourFournisseurDetail.get(k).getLgFAMILLEID().getIntPRICE(), commonparameter.CHAR_SEPARATEUR_POINT) + " F CFA&nbsp;&nbsp;" + " :  (" + lstTRetourFournisseurDetail.get(k).getIntNUMBERRETURN() + ")</b><br> " + str_Product;
            str_Product = "<b><span style='display:inline-block;width: 7%;'>" + lstTRetourFournisseurDetail.get(k).getLgFAMILLEID().getIntCIP() + "</span><span style='display:inline-block;width: 25%;'>" + lstTRetourFournisseurDetail.get(k).getLgFAMILLEID().getStrDESCRIPTION() + "</span><span style='display:inline-block;width: 15%;'> Qté Retournée: " + lstTRetourFournisseurDetail.get(k).getIntNUMBERRETURN() + "</span><span style='display:inline-block;width: 15%;'>" + new conversion().AmountFormat(lstTRetourFournisseurDetail.get(k).getLgFAMILLEID().getIntPAF(), commonparameter.CHAR_SEPARATEUR_POINT) + " F CFA</span></b><br>" + str_Product;
            nb = nb + lstTRetourFournisseurDetail.get(k).getIntNUMBERRETURN();

        }

        JSONObject json = new JSONObject();

        json.put("lg_RETOUR_FRS_ID", lstTRetourFournisseur.get(i).getLgRETOURFRSID());
        json.put("str_REF_RETOUR_FRS", lstTRetourFournisseur.get(i).getStrREFRETOURFRS());
        json.put("str_REPONSE_FRS", lstTRetourFournisseur.get(i).getStrREPONSEFRS());
        json.put("str_COMMENTAIRE", lstTRetourFournisseur.get(i).getStrCOMMENTAIRE());

        json.put("lg_BON_LIVRAISON_ID", lstTRetourFournisseur.get(i).getLgBONLIVRAISONID().getLgBONLIVRAISONID());
        json.put("str_REF_LIVRAISON", lstTRetourFournisseur.get(i).getLgBONLIVRAISONID().getStrREFLIVRAISON());

        json.put("lg_GROSSISTE_ID", lstTRetourFournisseur.get(i).getLgGROSSISTEID().getLgGROSSISTEID());
        json.put("str_GROSSISTE_LIBELLE", lstTRetourFournisseur.get(i).getLgGROSSISTEID().getStrLIBELLE());
        json.put("dt_DATE", key.DateToString(lstTRetourFournisseur.get(i).getDtDATE(), key.formatterShort));
        json.put("int_LINE", lstTRetourFournisseurDetail.size());
        json.put("str_FAMILLE_ITEM", str_Product);
        json.put("DATEBL", date.formatterShort.format(lstTRetourFournisseur.get(i).getLgBONLIVRAISONID().getDtCREATED()));
        json.put("str_STATUT", lstTRetourFournisseur.get(i).getStrSTATUT());
        json.put("lg_USER_ID", lstTRetourFournisseur.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTRetourFournisseur.get(i).getLgUSERID().getStrLASTNAME());
        json.put("dt_CREATED", date.backabaseUiFormat1.format(lstTRetourFournisseur.get(i).getDtUPDATED()));
        json.put("MONTANTRETOUR", lstTRetourFournisseur.get(i).getDlAMOUNT().longValue());
        json.put("BTNDELETE", BTNDELETE);

        arrayObj.put(json);
    }
    String result = "({\"total\":\"" + lstTRetourFournisseur.size() + " \" ,\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

    /*lstTRetourFournisseurDetail = null;
     lstTRetourFournisseur = null;
     OdataManager = null;*/
%>

<%= result%>