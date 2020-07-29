<%@page import="toolkits.web.json"%>
<%@page import="bll.retrocessionManagement.RetrocessionDetailManagement"%>
<%@page import="dal.TRetrocessionDetail"%>
<%@page import="bll.retrocessionManagement.RetrocessionManagement"%>
<%@page import="dal.TRetrocession"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TRetrocession"%>
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
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String search_value = "", str_REF = "%%", dt_CREATED = "%%";
    JSONArray arrayObj;
    ;
    TUser OTUser;
    //String str_STATUT =commonparameter.statut_is_Process;

    Integer int_PRICE;
    date key = new date();
    date str_LAST_CONNECTION_DATE;
    privilege Oprivilege = new privilege();

    int taille = 0;

    json Ojson = new json();
    List<TRetrocession> lstTRetrocession = new ArrayList<TRetrocession>();
%>



<%

    new logger().OCategory.info("dans ws data retrocession");
    try {
        OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

        OdataManager.initEntityManager();

        OTUser = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
        OdataManager.getEm().refresh(OTUser);
    } catch (Exception er) {
        new logger().OCategory.info("Failed to refresh OTUser !");
    }

    try {

        if (request.getParameter("search_value") != null) {
            Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
            new logger().OCategory.info("Search book " + request.getParameter("search_value"));
        } else {
            Os_Search_poste.setOvalue("%%");
        }
        new logger().OCategory.info("search_value  = " + request.getParameter("search_value"));
    } catch (Exception er) {
        new logger().OCategory.info("Failed to search value !");
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
    }
    new logger().OCategory.info("search_value  " + search_value);

    RetrocessionManagement ORetrocessionManagement = new RetrocessionManagement(OdataManager);
    RetrocessionDetailManagement ORetrocessionDetailManagement = new RetrocessionDetailManagement(OdataManager);
    lstTRetrocession = ORetrocessionManagement.SearchAllOrOneRetrocession(search_value);
    new logger().OCategory.info("lstTRetrocession sze " + lstTRetrocession.size());
    try {
        
        arrayObj = new JSONArray();
        for (int i = 0; i < lstTRetrocession.size(); i++) {

            OdataManager.getEm().refresh(lstTRetrocession.get(i));
            List<TRetrocessionDetail> lstTRetrocessionDetail = new ArrayList<TRetrocessionDetail>();
            lstTRetrocessionDetail = ORetrocessionDetailManagement.SearchAllOrOneDetailRetrocession(lstTRetrocession.get(i).getLgRETROCESSIONID());
            new logger().OCategory.info("lstTRetrocessionDetail size "+lstTRetrocessionDetail.size());
            String str_Product = "";
            for (int k = 0; k < lstTRetrocessionDetail.size(); k++) {
                str_Product = "<b>" + lstTRetrocessionDetail.get(k).getLgFAMILLEID().getStrNAME() + " :  (" + lstTRetrocessionDetail.get(k).getIntQtefacture() + ")</b><br> " + str_Product;
            }

            JSONObject json = new JSONObject();
            json.put("lg_RETROCESSION_ID", lstTRetrocession.get(i).getLgRETROCESSIONID());
            json.put("str_REFERENCE", lstTRetrocession.get(i).getStrREFERENCE());
            json.put("str_COMMENTAIRE", lstTRetrocession.get(i).getStrCOMMENTAIRE());
            json.put("int_MONTANT_HT", lstTRetrocession.get(i).getIntMONTANTHT());
            json.put("int_MONTANT_TTC", lstTRetrocession.get(i).getIntMONTANTTTC());
            json.put("lg_CLIENT_ID", lstTRetrocession.get(i).getLgCLIENTID().getStrFIRSTNAME() + " " + lstTRetrocession.get(i).getLgCLIENTID().getStrLASTNAME());
            json.put("int_REMISE", lstTRetrocession.get(i).getIntREMISE());
            json.put("int_ESCOMPTE_SOCIETE", lstTRetrocession.get(i).getIntESCOMPTESOCIETE());
           // json.put("lg_TVA_ID", lstTRetrocession.get(i).getLgTVAID().getStrLIBELLEE());
            json.put("dt_CREATED", date.DateToString(lstTRetrocession.get(i).getDtCREATED(), key.formatterMysql));
            json.put("str_STATUT", lstTRetrocession.get(i).getStrSTATUT());
            json.put("str_FAMILLE_ITEM", str_Product);
            new logger().OCategory.info("total prix retrocession ttc "+lstTRetrocession.get(i).getIntMONTANTTTC());
            json.put("int_MONTANT_TTC", lstTRetrocession.get(i).getIntMONTANTTTC());
            new logger().OCategory.info(" ****  calcul total product **** ");
            int int_total_product = ORetrocessionDetailManagement.GetProductTotal(lstTRetrocession.get(i).getLgRETROCESSIONID());
            new logger().OCategory.info("int_total_product =  " + int_total_product);

            json.put("int_TOTAL_PRODUIT", int_total_product);
            // new logger().OCategory.info(" Total_vente  ====== "+Total_vente);

            arrayObj.put(json);

        }

    } catch (Exception er) {
        new logger().OCategory.info("Failed to display list !");
    }
    String result = "({\"total\":\"" + lstTRetrocession.size() + " \",\"results\":" + arrayObj.toString() + "})";
new logger().OCategory.info("result "+result);

%>

<%= result%>
