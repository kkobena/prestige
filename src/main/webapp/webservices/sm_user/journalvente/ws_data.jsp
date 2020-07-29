<%@page import="bll.configManagement.EmplacementManagement"%>
<%@page import="dal.TEmplacement"%>
<%@page import="bll.report.JournalVente"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="java.util.ArrayList"%>
<%@page import="dal.TMotifReglement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
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
    TPreenregistrementCompteClientTiersPayent OTPreenregistrementCompteClientTiersPayent = null;
    TEmplacement OTEmplacement = null;
    date key = new date();
    JSONObject json = null;
%>


<%    List<EntityData> listPreenregistrement = new ArrayList<EntityData>();
    List<TPreenregistrementCompteClientTiersPayent> listTPreenregistrementCompteClientTiersPayent = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
    Date today = new Date();
    String str_Date_Debut = key.DateToString(today, key.formatterMysqlShort), str_Date_Fin = key.DateToString(today, key.formatterMysqlShort), search_value = "",
            h_debut = "00:00", h_fin = "23:59", lg_PREENGISTREMENT_ID = "%%", str_TYPE_VENTE = "%%";

    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    
     int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value :" + search_value);
    }

    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equalsIgnoreCase("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        new logger().OCategory.info("str_TYPE_VENTE :" + str_TYPE_VENTE);
    }

    if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        new logger().OCategory.info("h_debut :" + h_debut);
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");
        new logger().OCategory.info("h_fin :" + h_fin);
    }
    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    JournalVente OJournalVente = new JournalVente(OdataManager, OTUser);
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    EmplacementManagement OEmplacementManagement = new EmplacementManagement(OdataManager);
    listPreenregistrement = OJournalVente.getJournalVente(search_value, lg_PREENGISTREMENT_ID, str_Date_Debut, str_Date_Fin, h_debut, h_fin, str_TYPE_VENTE, start, limit);
    total = OJournalVente.getJournalVente(search_value, lg_PREENGISTREMENT_ID, str_Date_Debut, str_Date_Fin, h_debut, h_fin, str_TYPE_VENTE).size();
%>

<%    JSONArray arrayObj = new JSONArray();

   // for(int i = 0; i < (total < limit ? total : limit); i++) {
for(int i = 0; i < (listPreenregistrement.size() < limit ? listPreenregistrement.size() : limit); i++) {
        String str_Product = "";

        if (listPreenregistrement.get(i).getStr_value10().equalsIgnoreCase(Parameter.KEY_VENTE_ORDONNANCE)) {
            if (!listPreenregistrement.get(i).getStr_value17().equalsIgnoreCase(Parameter.KEY_NATURE_VENTE_DEPOT)) {
                listTPreenregistrementCompteClientTiersPayent = OPreenregistrement.getListeTPreenregistrementCompteClientTiersPayent(listPreenregistrement.get(i).getStr_value1(), commonparameter.statut_is_Closed);
                for (int k = 0; k < listTPreenregistrementCompteClientTiersPayent.size(); k++) {
                    OTPreenregistrementCompteClientTiersPayent = listTPreenregistrementCompteClientTiersPayent.get(k);
                    if (OTPreenregistrementCompteClientTiersPayent != null) {
                        str_Product = "<span style='display: inline-block; width: 350px;'><b>Tiers payant</b>: " + OTPreenregistrementCompteClientTiersPayent.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrFULLNAME() + "</span><span style='display: inline-block; width: 350px;'><b>Montant</b>: " + conversion.AmountFormat(OTPreenregistrementCompteClientTiersPayent.getIntPRICE(), '.') + "</span><span style='display: inline-block; width: 350px;'><b>Pourcentage</b>: " + OTPreenregistrementCompteClientTiersPayent.getIntPERCENT() + "</span><br>" + str_Product;
                    }
                }
            } else {
                OTEmplacement = OEmplacementManagement.getEmplacementByOwner(listPreenregistrement.get(i).getStr_value11());
                str_Product = (OTEmplacement != null && OTEmplacement.getLgTYPEDEPOTID() != null ? "<span style='display: inline-block; width: 350px;'><b>" + OTEmplacement.getLgTYPEDEPOTID().getStrDESCRIPTION() + ": " +OTEmplacement.getStrDESCRIPTION() + "</b>" : "");
            }

        } else {
            if (!listPreenregistrement.get(i).getStr_value17().equalsIgnoreCase(Parameter.KEY_NATURE_VENTE_DEPOT)) {
                str_Product = "<span style='display: inline-block; width: 350px;'><b> Pas de Tiers payant</b>";
            } else {
                OTEmplacement = OEmplacementManagement.getEmplacementByOwner(listPreenregistrement.get(i).getStr_value11());
                str_Product = (OTEmplacement != null && OTEmplacement.getLgTYPEDEPOTID() != null ? "<span style='display: inline-block; width: 350px;'><b>"+ OTEmplacement.getLgTYPEDEPOTID().getStrDESCRIPTION() + ": " +OTEmplacement.getStrDESCRIPTION() + "</b>" : "");
          
                
            }
            
        }

        json = new JSONObject();
        json.put("lg_PREENREGISTREMENT_ID", listPreenregistrement.get(i).getStr_value1());
        json.put("str_REF", listPreenregistrement.get(i).getStr_value2());
        json.put("lg_USER_CAISSIER_ID", listPreenregistrement.get(i).getStr_value3());
        json.put("lg_USER_VENDEUR_ID", listPreenregistrement.get(i).getStr_value11());
        json.put("int_PRICE", listPreenregistrement.get(i).getStr_value5());
        //json.put("dt_CREATED", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value5(), key.formatterMysql), date.formatterShort));
        //new logger().OCategory.info("Date ----"+date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value6(), date.formatterMysql), date.formatterShort));
        json.put("dt_CREATED", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value6(), date.formatterMysql), date.formatterShort));
        json.put("str_hour", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value6(), date.formatterMysql), date.NomadicUiFormat_Time));
        json.put("str_STATUT", listPreenregistrement.get(i).getStr_value7());
        json.put("str_FAMILLE_ITEM", str_Product);
        json.put("str_TYPE_VENTE", listPreenregistrement.get(i).getStr_value10());
        json.put("str_TIERS_PAYANT_RO", listPreenregistrement.get(i).getStr_value15());

        json.put("int_PRICE_FORMAT", conversion.AmountFormat(Integer.parseInt(listPreenregistrement.get(i).getStr_value5()), '.'));
        json.put("str_REF_BON", listPreenregistrement.get(i).getStr_value14());
        json.put("int_PRICE_REMISE_FORMAT", conversion.AmountFormat(Integer.parseInt(listPreenregistrement.get(i).getStr_value13()), '.'));
        json.put("VENTE_NET_FORMAT", conversion.AmountFormat(Integer.parseInt(listPreenregistrement.get(i).getStr_value12()), '.'));
        json.put("int_PRICE_REMISE", Integer.parseInt(listPreenregistrement.get(i).getStr_value13()));
        json.put("VENTE_NET", Integer.parseInt(listPreenregistrement.get(i).getStr_value12()));
        json.put("VENTE_NAME", listPreenregistrement.get(i).getStr_value16());

        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";

    System.out.println(result);
%>

<%= result%>
