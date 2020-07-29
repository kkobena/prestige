<%@page import="dal.TMvtCaisse"%>
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


<%
    dataManager OdataManager = new dataManager();
    TMvtCaisse OTMvtCaisse = null;
    JSONObject json = null;
%>


<%    List<EntityData> listPreenregistrement = new ArrayList<EntityData>(), listPreenregistrementOther = new ArrayList<EntityData>();
Date today = new Date();
    String str_Date_Debut = date.DateToString(today, date.formatterMysqlShort), str_Date_Fin = date.DateToString(today, date.formatterMysqlShort), search_value = "",
            lg_USER_ID = "%%", lg_TYPE_REGLEMENT_ID = "%%";
    
    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    boolean bool_CHECKED = true;
    
    int int_PRICE_TOTAL = 0, start = 0, limit = jdom.int_size_pagination, total = 0;
    
    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }
    
     if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }
        

    if (request.getParameter("bool_CHECKED") != null) {
        bool_CHECKED = Boolean.parseBoolean(request.getParameter("bool_CHECKED"));
        new logger().OCategory.info("bool_CHECKED :" + bool_CHECKED);
    }
    
     if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value :" + search_value);
    }

    if (request.getParameter("lg_USER_ID") != null && !request.getParameter("lg_USER_ID").equalsIgnoreCase("")) {
        lg_USER_ID = request.getParameter("lg_USER_ID");
        new logger().OCategory.info("lg_USER_ID :" + lg_USER_ID);
    }

    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
        new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
    }

    if (request.getParameter("lg_TYPE_REGLEMENT_ID") != null && !request.getParameter("lg_TYPE_REGLEMENT_ID").equalsIgnoreCase("")) {
        lg_TYPE_REGLEMENT_ID = request.getParameter("lg_TYPE_REGLEMENT_ID");
        new logger().OCategory.info("lg_TYPE_REGLEMENT_ID :" + lg_TYPE_REGLEMENT_ID);
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
    }

    JournalVente OJournalVente = new JournalVente(OdataManager, OTUser);

     listPreenregistrementOther = OJournalVente.getMouvementCaisse(str_Date_Debut, str_Date_Fin, lg_USER_ID, lg_TYPE_REGLEMENT_ID, bool_CHECKED);
    total = listPreenregistrementOther.size();
    listPreenregistrement = OJournalVente.getMouvementCaisse(str_Date_Debut, str_Date_Fin, lg_USER_ID, lg_TYPE_REGLEMENT_ID, bool_CHECKED, start, limit);
    int_PRICE_TOTAL = OJournalVente.getTotalAmountCashTransactionOther(listPreenregistrementOther);
%>


<%    JSONArray arrayObj = new JSONArray();

   
    for(int i = 0; i < (total < limit ? total : limit); i++) {
        json = new JSONObject();
        String ref=OJournalVente.getRef(listPreenregistrement.get(i).getStr_value1());
        json.put("str_ref", ("".equals(ref)?listPreenregistrement.get(i).getStr_value1():ref) );
        
        json.put("str_vendeur", listPreenregistrement.get(i).getStr_value2());
        json.put("str_date", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value3(), date.formatterMysql), date.formatterShort));
        json.put("str_hour", date.DateToString(date.stringToDate(listPreenregistrement.get(i).getStr_value3(), date.formatterMysql), date.NomadicUiFormat_Time));
        json.put("str_mt_vente", conversion.AmountFormat(bool_CHECKED == true ? Integer.parseInt(listPreenregistrement.get(i).getStr_value7()) :(-1) * Integer.parseInt(listPreenregistrement.get(i).getStr_value7()), '.') );
        json.put("str_TRANSACTION_REF", listPreenregistrement.get(i).getStr_value6());
        json.put("str_FAMILLE_ITEM", listPreenregistrement.get(i).getStr_value5());
        //json.put("int_PRICE", listPreenregistrement.get(i).getStr_value7()); //
        json.put("int_PRICE", bool_CHECKED == true ? Integer.parseInt(listPreenregistrement.get(i).getStr_value7()) : (-1) * Integer.parseInt(listPreenregistrement.get(i).getStr_value7()));
        json.put("str_client_infos", listPreenregistrement.get(i).getStr_value8());
        
        json.put("str_mt_clt", listPreenregistrement.get(i).getStr_value9());
         json.put("lg_TYPE_REGLEMENT_ID", listPreenregistrement.get(i).getStr_value11());
        new logger().OCategory.info("str_client_infos************** : " + listPreenregistrement.get(i).getStr_value8());

        json.put("int_PRICE_TOTAL", bool_CHECKED == true ? int_PRICE_TOTAL : (-1) * int_PRICE_TOTAL);

        try {
            OTMvtCaisse = OdataManager.getEm().find(TMvtCaisse.class, listPreenregistrement.get(i).getStr_value1());
            if (OTMvtCaisse != null) {
                json.put("lg_MVT_CAISSE_ID", OTMvtCaisse.getLgMVTCAISSEID());
                json.put("str_COMMENTAIRE", OTMvtCaisse.getStrCOMMENTAIRE());
                json.put("lg_TYPE_MVT_CAISSE_ID", OTMvtCaisse.getLgTYPEMVTCAISSEID().getStrNAME());
                json.put("str_NUM_COMPTE", OTMvtCaisse.getStrNUMCOMPTE());
                //json.put("str_NUM_PIECE_COMPTABLE", OTMvtCaisse.getStrNUMPIECECOMPTABLE());
               json.put("str_NUM_PIECE_COMPTABLE", OTMvtCaisse.getLgMVTCAISSEID());
               //ljson.put("str_NUM_PIECE_COMPTABLE", OTMvtCaisse.getStrNUMPIECECOMPTABLE());
                json.put("lg_MODE_REGLEMENT_ID", OTMvtCaisse.getLgMODEREGLEMENTID().getStrNAME());
                json.put("int_AMOUNT", OTMvtCaisse.getIntAMOUNT());
                json.put("dt_DATE_MVT", date.DateToString(OTMvtCaisse.getDtDATEMVT(), date.formatterShort));
            }
        } catch (Exception e) {
        }

        //new logger().OCategory.info("int_PRICE_TOTAL -----" + int_PRICE_TOTAL);
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";

    System.out.println(result);
%>

<%= result%>
