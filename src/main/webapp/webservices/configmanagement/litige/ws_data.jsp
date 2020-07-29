<%@page import="com.asc.prestige2.business.litiges.concrete.PrestigeLitige"%>
<%@page import="com.asc.prestige2.business.litiges.LitigeService"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TSnapshotPreenregistrementCompteClientTiersPayent"%>
<%@page import="bll.configManagement.LitigeManager"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TLitige"  %>
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
    LitigeService _service = new PrestigeLitige();
    
    List<TLitige> lstTLitige = _service.getAllLitiges();



%>

<%
    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data litige ");
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

<%   
    String lg_TYPELITIGE_ID = "%%", search_value = "", str_TYPE = "", 
           MANQUANT = "MANQUANT", lg_TIERS_PAYANT_ID = "", lg_CLIENT_ID = "", method = "";
  
    
    
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null && request.getParameter("lg_TIERS_PAYANT_ID") != "") {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID);
    }
    
    if (request.getParameter("lg_CLIENT_ID") != null && request.getParameter("lg_CLIENT_ID") != "") {
        lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
        new logger().OCategory.info("lg_CLIENT_ID " + lg_CLIENT_ID);
    }
    
    if (request.getParameter("method") != null && request.getParameter("method") != "") {
        method = request.getParameter("method");
        new logger().OCategory.info("lg_CLIENT_ID " + method);
    }
    
    
    
    
    /*
    if (request.getParameter("search_value") != null && request.getParameter("search_value") != "") {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    
    if (request.getParameter("lg_TYPELITIGE_ID") != null && request.getParameter("lg_TYPELITIGE_ID") != "") {
        lg_TYPELITIGE_ID = request.getParameter("lg_TYPELITIGE_ID");
        new logger().OCategory.info("lg_TYPELITIGE_ID " + lg_TYPELITIGE_ID);
    }
    if (request.getParameter("str_TYPE") != null && request.getParameter("str_TYPE") != "") {
        str_TYPE = request.getParameter("str_TYPE");
        new logger().OCategory.info("str_TYPE " + str_TYPE);
    }
    OdataManager.initEntityManager();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    LitigeManager OLitigeManager = new LitigeManager(OdataManager);
    if (str_TYPE.equalsIgnoreCase(MANQUANT)) {
        lstTLitige = OLitigeManager.listTLitigeByTypeLitigeNonAbouti(search_value, lg_TYPELITIGE_ID);
    } else {
        lstTLitige = OLitigeManager.listTLitigeByTypeLitige(search_value, lg_TYPELITIGE_ID);
    }
    */

%>

<%
//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTLitige.size()) {
            DATA_PER_PAGE = lstTLitige.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTLitige.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%  JSONArray arrayObj = new JSONArray();
    
    /*
       str_LITIGE_ID: str_LITIGE_ID,
                   
                    str_TIERS_PAYANT_ID: str_TIERS_PAYANT_ID,
                   
                    
                    str_COMMENTAIRE_LITIGE: str_COMMENTAIRE_LITIGE,
                    str_LITIGE_CONSEQUENCE: str_LITIGE_CONSEQUENCE

     */

    
    for (int i = pgInt; i < pgInt_Last; i++) {
      
        JSONObject json = new JSONObject();
        TLitige litige = lstTLitige.get(i);
        
        json.put("lg_LITIGE_ID", litige.getLgLITIGEID());
        json.put("str_TYPE_LITIGE_ID", litige.getLgTYPELITIGEID().getLgTYPELITIGEID());
        json.put("str_TYPE_LITIGE", litige.getLgTYPELITIGEID().getStrDESCRIPTION());
        json.put("str_REFERENCE", litige.getStrREFERENCEVENTELITIGE()); 
        json.put("str_LIBELLE", litige.getStrLIBELLELITIGE());
        json.put("str_DESCRIPTION", litige.getStrDESCRIPTIONLITIGE());
        json.put("str_REF_CREATED", litige.getDtCREATEDLITIGE());
        json.put("str_CLIENT_NAME", litige.getStrCLIENTNAME());
        json.put("str_ETAT_LITIGE", litige.getStrETATLITIGE());
        json.put("str_COMMENTAIRE_LITIGE", litige.getStrCOMMENTAIRELITIGE());
        json.put("str_LITIGE_CONSEQUENCE", litige.getStrCONSEQUENCELITIGE());
        
        /*
        try {
            TPreenregistrementCompteClientTiersPayent O = OdataManager.getEm().find(TPreenregistrementCompteClientTiersPayent.class, lstTLitige.get(i).getStrREFCREATED());
            json.put("str_ORGANISME", O.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrFULLNAME());
            json.put("str_FIRST_LAST_NAME", O.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME() +
                    " "+ O.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME());
           // json.put("int_AMOUNT", O.getIntPRICE());
            json.put("lg_PREENREGISTREMENT_ID", O.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
             json.put("lg_TYPE_VENTE_ID", O.getLgPREENREGISTREMENTID().getLgTYPEVENTEID().getStrDESCRIPTION());
              json.put("str_MEDECIN", O.getLgPREENREGISTREMENTID().getStrMEDECIN());
          
            
            List<TPreenregistrementDetail> lstTPreenregistrementDetail = new ArrayList<TPreenregistrementDetail>();
            lstTPreenregistrementDetail = new Preenregistrement(OdataManager, OTUser).getTPreenregistrementDetailBis(O.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());

            new logger().OCategory.info(" ***  lstTPreenregistrementDetail ws data *** " + lstTPreenregistrementDetail.size());

            String str_Product = "";
            for (int k = 0; k < lstTPreenregistrementDetail.size(); k++) {
                TPreenregistrementDetail OTPreenregistrementDetail = lstTPreenregistrementDetail.get(k);
                str_Product = "<b>" + OTPreenregistrementDetail.getLgFAMILLEID().getStrNAME() + " :  (" + OTPreenregistrementDetail.getIntQUANTITY() + ")</b><br> " + str_Product;
            }
            str_Product +="<u>Total de la Vente</u>: "+O.getLgPREENREGISTREMENTID().getIntPRICE() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<u>Côut endocé par le Tiers payant</u>: "+O.getIntPRICE();
            json.put("str_FAMILLE_ITEM", str_Product);
            int int_total_product = new Preenregistrement(OdataManager, OTUser).GetProductTotal(O.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID(), O.getLgPREENREGISTREMENTID().getStrSTATUT());
            int int_total_vente = new Preenregistrement(OdataManager, OTUser).GetVenteTotalwithRemise(O.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID(), O.getLgPREENREGISTREMENTID().getStrSTATUT());
            json.put("str_FAMILLE_ITEM", str_Product);
            json.put("int_total_product", int_total_product);
            json.put("int_total_vente", int_total_vente);
        }catch(Exception e) {   
            e.printStackTrace();
        }
        
        
        json.put("str_STATUT", lstTLitige.get(i).getStrSTATUT());
        String etat = "";
        if(lstTLitige.get(i).getStrSTATUT().equalsIgnoreCase(commonparameter.statut_enable)) {
            etat = "En cours";
        } else if(lstTLitige.get(i).getStrSTATUT().equalsIgnoreCase(commonparameter.statut_is_Closed)) {
            etat = "Clôturé";
        }
        json.put("etat", etat);
        json.put("str_STATUT", lstTLitige.get(i).getStrSTATUT());
        json.put("str_STATUT_TRAITEMENT", lstTLitige.get(i).getStrSTATUTTRAITEMENT());
        json.put("int_AMOUNT", lstTLitige.get(i).getIntAMOUNT());
        json.put("int_AMOUNT_DUS", lstTLitige.get(i).getIntAMOUNTDUS());
        json.put("int_ECART", lstTLitige.get(i).getIntECART());
        json.put("dt_CREATED", key.DateToString(lstTLitige.get(i).getDtCREATED(), key.formatterShort));
        json.put("dt_UPDATED", key.DateToString(lstTLitige.get(i).getDtUPDATED(), key.formatterShort));
*/
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTLitige.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

%>

<%= result %>
