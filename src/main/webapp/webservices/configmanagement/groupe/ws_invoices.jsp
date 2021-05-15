<%-- 
    Document   : ws_zone
    Created on : 29 juin 2017, 00:27:26
    Author     : KKOFFI
--%>


<%@page import="dal.TFacture"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TGroupeTierspayant"%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>

<%@page import="java.util.Date"%>
<%@page import="toolkits.utils.date"%>

<%@page import="dal.dataManager"%>


<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>

<%@page import="java.util.ArrayList"%>

<%@page import="java.util.List"%>

<%
    
    dataManager OdataManager = new dataManager();
    String CODEFACTURE = "";
    OdataManager.initEntityManager();
    GroupeTierspayantController groupeCtl = new GroupeTierspayantController(OdataManager.getEmf());
    String search_value = "", lgTP = "";

    if (request.getParameter("lgTP") != null && !"".equals(request.getParameter("lgTP"))) {
        lgTP = request.getParameter("lgTP");
    }
    if (request.getParameter("CODEFACTURE") != null && !"".equals(request.getParameter("CODEFACTURE"))) {
        CODEFACTURE = request.getParameter("CODEFACTURE");
    }

    if (request.getParameter("query") != null && !"".equals(request.getParameter("query"))) {
        search_value = request.getParameter("query");
    }
 
    JSONArray arrayObj = new JSONArray();
    List<TFacture> tps = groupeCtl.getGroupeInvoiceDetails( search_value,  CODEFACTURE);
    int count = groupeCtl.getGroupeInvoiceDetailsCount(search_value,  CODEFACTURE);
    for (TFacture f : tps) {
        TTiersPayant OTTiersPayant = OdataManager.getEm().find(TTiersPayant.class, f.getStrCUSTOMER());
        JSONObject json = new JSONObject();
        json.put("lg_FACTURE_ID", f.getLgFACTUREID());
        json.put("str_CODE_FACTURE", f.getStrCODEFACTURE());
        json.put("int_NB_DOSSIER", f.getIntNBDOSSIER());
        json.put("dt_CREATED", date.formatterShort.format(f.getDtDATEFACTURE()));
        json.put("str_STATUT", f.getStrSTATUT());

        json.put("str_CUSTOMER_NAME", OTTiersPayant.getStrFULLNAME());
        json.put("str_PERIODE", "Du " + date.formatterShort.format(f.getDtDEBUTFACTURE()) + " Au " + date.formatterShort.format(f.getDtFINFACTURE()));
        json.put("dbl_MONTANT_CMDE", f.getDblMONTANTCMDE());
        json.put("dbl_MONTANT_RESTANT", f.getDblMONTANTRESTANT());
        json.put("dbl_MONTANT_PAYE", f.getDblMONTANTPAYE());
        json.put("MONTANTREMISE", f.getDblMONTANTREMISE());
        json.put("MONTANTFORFETAIRE", f.getDblMONTANTFOFETAIRE());
        json.put("MONTANTBRUT", f.getDblMONTANTBrut());
         json.put("MONTANTVERSEE", f.getDblMONTANTRESTANT());
         json.put("MONTANTVIRTUEL", f.getDblMONTANTRESTANT()); 
        
        json.put("isChecked", false); 
        
        arrayObj.put(json);

    }
    JSONObject data = new JSONObject();

    data.put("data", arrayObj);
    data.put("total", count);
%>

<%= data%>