<%@page import="dal.TPreenregistrement"%>
<%@page import="bll.facture.reglementManager"%>
<%@page import="dal.TFactureDetail"%>
<%@page import="dal.TFacture"%>
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



<%
   // int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    int DATA_PER_PAGE = 5, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws_data_reglement_by_bordereau");
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

<%    //new logger().OCategory.info("lg_FACTURE_ID " + request.getParameter("lg_FACTURE_ID"));
    dataManager OdataManager = new dataManager();
    String  lg_dossier_reglement_id = "%%", P_KEY = "%%", lg_dossier_id = "%%", lg_MODULE_ID = "%%", str_VALUE = "%%", str_Status = "%%", lg_FACTURE_ID = "";
    Integer int_PRIORITY;
    double Amount_total = 0.0;
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<TFactureDetail> lstTFactureDetail = new ArrayList<TFactureDetail>();

    bllBase ObllBase = new bllBase();
    reglementManager OreglementManger = new reglementManager(OdataManager, null);
    
    TPreenregistrementCompteClientTiersPayent preregistrement=null;
   
    if (request.getParameter("lg_dossier_reglement_id") != null) {
        lg_dossier_reglement_id = request.getParameter("lg_dossier_reglement_id").toString();
        new logger().OCategory.info("lg_dossier_reglement_id " + request.getParameter("lg_dossier_reglement_id"));
    }

    if (request.getParameter("lg_FACTURE_ID") != null) {
        lg_FACTURE_ID = request.getParameter("lg_FACTURE_ID").toString();

    }

    lstTFactureDetail = OreglementManger.getDetailsFactureByTiersPayant(lg_FACTURE_ID);

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


<%    JSONArray arrayObj = new JSONArray();
    // Amount_total = 0.0;

    for (int i = pgInt; i < pgInt_Last; i++) {

        JSONObject json = new JSONObject();
      
        
        double Amount = 0d, dbl_MONTANT_PAYE = 0d, dbl_MONTANT_RESTANT = 0d;
        preregistrement=OreglementManger.getOPreenregistrementCompteClientTiersPayentByRef(lstTFactureDetail.get(i).getStrREF());
        json.put("lg_FACTURE_ID", lstTFactureDetail.get(i).getLgFACTUREID().getStrCODEFACTURE());
        json.put("lg_FACTURE_DETAIL_ID", lstTFactureDetail.get(i).getLgFACTUREDETAILID());
        if (lstTFactureDetail.get(i).getDblMONTANT() != null) {
            Amount = lstTFactureDetail.get(i).getDblMONTANT();
        }
         json.put("Amount", Amount);
        if (lstTFactureDetail.get(i).getDblMONTANTPAYE() != null) {
            dbl_MONTANT_PAYE = lstTFactureDetail.get(i).getDblMONTANTPAYE();
        }
        json.put("dbl_MONTANT_PAYE", dbl_MONTANT_PAYE);

        if (lstTFactureDetail.get(i).getDblMONTANTRESTANT() != null) {
            dbl_MONTANT_RESTANT = lstTFactureDetail.get(i).getDblMONTANTRESTANT();
        }
        json.put("dbl_MONTANT_RESTANT", dbl_MONTANT_RESTANT);
         json.put("isChecked", false);
          
         json.put("str_REF", (preregistrement.getStrREFBON()!=null?preregistrement.getStrREFBON():""));
        // json.put("CLIENT_FULL_NAME", preregistrement.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrFIRSTNAME()+" "+preregistrement.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrLASTNAME()); 
         // json.put("CLIENT_MATRICULE",preregistrement.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID().getLgCLIENTID().getStrNUMEROSECURITESOCIAL());
      
            json.put("CLIENT_FULL_NAME", preregistrement.getLgPREENREGISTREMENTID().getStrFIRSTNAMECUSTOMER() +" "+preregistrement.getLgPREENREGISTREMENTID().getStrLASTNAMECUSTOMER()); 
          json.put("CLIENT_MATRICULE", preregistrement.getLgPREENREGISTREMENTID().getStrNUMEROSECURITESOCIAL());
          json.put("dt_DATE", date.backabaseUiFormat2.format( preregistrement.getLgPREENREGISTREMENTID().getDtUPDATED()));
         json.put("dt_HEURE", date.NomadicUiFormatTime.format( preregistrement.getLgPREENREGISTREMENTID().getDtUPDATED()));
          json.put("int_NB_DOSSIER_RESTANT", lstTFactureDetail.size());
          arrayObj.put(json);

    }
    String result = "({\"total\":\"" + lstTFactureDetail.size() + "\",Amount_total:\"" + Amount_total + "\",\"results\":" + arrayObj.toString() + "})";

    //new logger().OCategory.info("JSON " + result);
%>

<%= result%>