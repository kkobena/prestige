<%@page import="java.math.RoundingMode"%>
<%@page import="dal.TAyantDroit"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TDossierFacture"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="bll.facture.factureManagement"%>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();

    List<dal.TFactureDetail> lstTFactureDetail = new ArrayList<dal.TFactureDetail>();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
   
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

<%    String lg_FACTURE_ID = "%%", search_value = "";
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        
    }
    if (request.getParameter("lg_FACTURE_ID") != null) {
        lg_FACTURE_ID = request.getParameter("lg_FACTURE_ID").toString();
       
    }
    OdataManager.initEntityManager();
    
    lstTFactureDetail = OdataManager.getEm().createQuery("SELECT t FROM TFactureDetail t ,TPreenregistrementCompteClientTiersPayent p  WHERE t.lgFACTUREID.lgFACTUREID = ?1   AND ( p.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.strFIRSTNAME LIKE ?2 OR p.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.strLASTNAME LIKE ?3 OR p.lgPREENREGISTREMENTID.strREFBON LIKE ?4 OR p.lgCOMPTECLIENTTIERSPAYANTID.lgCOMPTECLIENTID.lgCLIENTID.strNUMEROSECURITESOCIAL LIKE ?5) AND t.strREF=p.lgPREENREGISTREMENTCOMPTECLIENTPAYENTID  ")
            .setParameter(1, lg_FACTURE_ID)
            .setParameter(2, search_value + "%")
            .setParameter(3, search_value + "%")
            .setParameter(4, search_value + "%")
            .setParameter(5, search_value + "%")
            .getResultList();

    new logger().OCategory.info(lstTFactureDetail.size());

%>

<%//Filtre de pagination
    try {
        if (DATA_PER_PAGE > lstTFactureDetail.size()) {
            DATA_PER_PAGE = lstTFactureDetail.size();
        }
    } catch (Exception E) {
    }

    int pgInt = pageAsInt - 1;
    int pgInt_Last = pageAsInt - 1;

    if (pgInt == 0) {
        pgInt_Last = DATA_PER_PAGE;
    } else {

        pgInt_Last = (lstTFactureDetail.size() - (DATA_PER_PAGE * (pgInt)));
        pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
        if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
            pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
        }
        pgInt = ((DATA_PER_PAGE) * (pgInt));
    }

%>


<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    factureManagement OfactureManagement = new factureManagement(OdataManager, OTUser);

    JSONArray arrayObj = new JSONArray();
    for (int i = pgInt; i < pgInt_Last; i++) {
        try {
            OdataManager.getEm().refresh(lstTFactureDetail.get(i));
        } catch (Exception er) {
        }
      
        TPreenregistrementCompteClientTiersPayent OTPreenregistrementCompteClientTiersPayent = OfactureManagement.GetInfoTierspayant(lstTFactureDetail.get(i).getStrREF());
        String str_NOM = OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getStrFIRSTNAMECUSTOMER();
        String str_PRENOM = OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getStrLASTNAMECUSTOMER();
        String str_SECURITE_SOCIAL = OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getStrNUMEROSECURITESOCIAL();
        JSONObject json = new JSONObject();

        json.put("lg_DOSSIER_FACTURE_ID", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());
        json.put("str_NOM", str_NOM != null ? str_NOM : "");
        json.put("str_PRENOM", str_PRENOM != null ? str_PRENOM : "");
        json.put("str_SECURITE_SOCIAL", str_SECURITE_SOCIAL != null ? str_SECURITE_SOCIAL : "");
        json.put("str_NUM_DOSSIER", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getStrREFBON());
        json.put("lg_PREENREGISTREMENT_ID", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getLgPREENREGISTREMENTID());
        json.put("dt_CREATED", key.DateToString(OTPreenregistrementCompteClientTiersPayent.getDtCREATED(), key.formatterShort));
        //json.put("MONTANTBRUT", lstTFactureDetail.get(i).getDblMONTANTBrut().setScale(2,RoundingMode.HALF_UP));
       json.put("MONTANTBRUT",OTPreenregistrementCompteClientTiersPayent.getIntPRICE());
        long montantRemise=lstTFactureDetail.get(i).getDblMONTANTREMISE().longValue(); 
       if(montantRemise==0){
           montantRemise=OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getIntPRICEREMISE();
       }
        json.put("MONTANTREMISE", montantRemise); 
        json.put("dbl_MONTANT", lstTFactureDetail.get(i).getDblMONTANT().longValue());
        json.put("str_REF", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getStrREF());
       // json.put("str_REF_BON", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getStrREFBON());
          json.put("str_REF_BON", OTPreenregistrementCompteClientTiersPayent.getStrREFBON());
       json.put("int_CUST_PART", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getIntCUSTPART());
        json.put("int_PERCENT", OTPreenregistrementCompteClientTiersPayent.getIntPERCENT());
       json.put("int_PRICE", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getIntPRICE());
        json.put("dt_DATE", date.backabaseUiFormat2.format(OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getDtUPDATED()));
        json.put("dt_HEURE", date.NomadicUiFormatTime.format(OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTID().getDtUPDATED()));
        json.put("lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID", OTPreenregistrementCompteClientTiersPayent.getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());

        arrayObj.put(json);
        //   new logger().OCategory.info("getStrREF " + OTDossierFacture.getStrNUMDOSSIER());

    }
    String result = "({\"total\":\"" + lstTFactureDetail.size() + "\",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>