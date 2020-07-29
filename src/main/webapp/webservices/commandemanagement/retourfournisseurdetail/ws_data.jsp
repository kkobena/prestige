<%@page import="dal.TTypeStockFamille"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="bll.commandeManagement.retourFournisseurManagement"%>
<%@page import="bll.common.Parameter"%>
<%@page import="com.asc.prestige2.business.promotions.concrete.PrestigePromotion"%>
<%@page import="com.asc.prestige2.business.promotions.PromotionService"%>
<%@page import="dal.TPromotionProduct"%>
<%@page import="dal.TFamille"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="dal.TRetourFournisseurDetail"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<%
    dataManager OdataManager = new dataManager();
    List<TRetourFournisseurDetail> lstTRetourFournisseurDetail = new ArrayList<TRetourFournisseurDetail>();
    TUser OTUser = null;
    TTypeStockFamille OTTypeStockFamille = null;
    JSONArray arrayObj = new JSONArray();
    JSONObject json = null;
%>

<%    
    new logger().OCategory.info("dans ws data details retour fournisseur ");
    String lg_RETOUR_FRS_ID = "%%", lg_TYPE_STOCK_ID = commonparameter.PROCESS_SUCCESS;

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

     int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }

    if (request.getParameter("lg_RETOUR_FRS_ID") != null) {
        lg_RETOUR_FRS_ID = request.getParameter("lg_RETOUR_FRS_ID").toString();
        new logger().OCategory.info("lg_RETOUR_FRS_ID " + lg_RETOUR_FRS_ID);
    }
    
    retourFournisseurManagement OretourFournisseurManagement = new retourFournisseurManagement(OdataManager, OTUser);
    StockManager OStockManager = new StockManager(OdataManager, OTUser);
    lstTRetourFournisseurDetail = OretourFournisseurManagement.getTRetourFournisseurDetail(lg_RETOUR_FRS_ID, start, limit);
    total = OretourFournisseurManagement.getTRetourFournisseurDetail(lg_RETOUR_FRS_ID).size();
    
%>


<%    
    
    for(int i = 0; i < (lstTRetourFournisseurDetail.size() < limit ? lstTRetourFournisseurDetail.size() : limit); i++) {
        
        json = new JSONObject();
        
        json.put("lg_RETOUR_FRS_DETAIL", lstTRetourFournisseurDetail.get(i).getLgRETOURFRSDETAIL());
        json.put("lg_RETOUR_FRS_ID", lstTRetourFournisseurDetail.get(i).getLgRETOURFRSID().getStrREFRETOURFRS());
        json.put("int_NUMBER_RETURN", lstTRetourFournisseurDetail.get(i).getIntNUMBERRETURN());
        json.put("int_NUMBER_ANSWER", lstTRetourFournisseurDetail.get(i).getIntNUMBERANSWER());
        json.put("lg_MOTIF_RETOUR", lstTRetourFournisseurDetail.get(i).getLgMOTIFRETOUR().getStrLIBELLE());
        json.put("lg_FAMILLE_ID", lstTRetourFournisseurDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        json.put("lg_FAMILLE_NAME", lstTRetourFournisseurDetail.get(i).getLgFAMILLEID().getStrNAME());
        json.put("lg_FAMILLE_CIP", lstTRetourFournisseurDetail.get(i).getLgFAMILLEID().getIntCIP());
        json.put("lg_FAMILLE_PRIX_VENTE", lstTRetourFournisseurDetail.get(i).getLgFAMILLEID().getIntPRICE());
        json.put("lg_FAMILLE_PRIX_ACHAT", lstTRetourFournisseurDetail.get(i).getLgFAMILLEID().getIntPAT());
        json.put("int_STOCK", lstTRetourFournisseurDetail.get(i).getIntSTOCK());
       /* OTTypeStockFamille = OStockManager.getTTypeStockFamilleByTypestock(lg_TYPE_STOCK_ID, lstTRetourFournisseurDetail.get(i).getLgFAMILLEID().getLgFAMILLEID());
        if(OTTypeStockFamille != null) {
            json.put("int_STOCK", OTTypeStockFamille.getIntNUMBER());
            json.put("int_DIFF", (OTTypeStockFamille.getIntNUMBER() - lstTRetourFournisseurDetail.get(i).getIntNUMBERRETURN()));
        }*/
       
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + total + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result " + result);

   
%>


<%= result%>
