<%@page import="bll.stockManagement.DepotManager"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="toolkits.utils.conversion"%>
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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    TUser OTUser;
    //String str_STATUT =commonparameter.statut_is_Process;

    date key = new date();

    List<TPreenregistrement> lstTPreenregistrement = new ArrayList<TPreenregistrement>();
    TPreenregistrementDetail OTPreenregistrementDetail = null;
    List<dal.TPreenregistrementDetail> lstTPreenregistrementDetail = new ArrayList<TPreenregistrementDetail>();
%>



<%
    String lg_PREENREGISTREMENT_ID = "%%", str_STATUT = "%%", search_value = "", str_TYPE_VENTE = "%%";
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
        new logger().OCategory.info("ws_data vente depot ");

    OdataManager.initEntityManager();
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        new logger().OCategory.info("search_value " + search_value);
    }
    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT");
        new logger().OCategory.info("str_STATUT " + str_STATUT);
    }
    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID " + lg_PREENREGISTREMENT_ID);
    }

    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equalsIgnoreCase("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        new logger().OCategory.info("str_TYPE_VENTE :" + str_TYPE_VENTE);
    }


    Date ODate = new Date();
    String OdateFin = key.DateToString(ODate, key.formatterMysqlShort2);
    Date dt_Date_Debut = key.getDate(OdateFin, "00:00");
    Date dt_Date_Fin = key.getDate(OdateFin, "23:59");
    new logger().OCategory.info("dt_Date_Debut   " + dt_Date_Debut);
    new logger().OCategory.info("dt_Date_Fin   " + dt_Date_Fin);
    DepotManager ODepotManager = new DepotManager(OdataManager, OTUser);
    lstTPreenregistrement = ODepotManager.getListeTPreenregistrement(search_value, lg_PREENREGISTREMENT_ID, str_STATUT, OTUser.getLgUSERID(), dt_Date_Debut, dt_Date_Fin, str_TYPE_VENTE);
    
   
    JSONArray arrayObj = new JSONArray();
    try {

       // arrayObj = new JSONArray();
        for (int i = 0; i < lstTPreenregistrement.size(); i++) {
            OdataManager.getEm().refresh(lstTPreenregistrement.get(i));

            lstTPreenregistrementDetail = new Preenregistrement(OdataManager, OTUser).getTPreenregistrementDetail(lstTPreenregistrement.get(i).getLgPREENREGISTREMENTID());

            new logger().OCategory.info(" ***  lstTPreenregistrementDetail ws data *** " + lstTPreenregistrementDetail.size());

            String str_Product = "";
            for (int k = 0; k < lstTPreenregistrementDetail.size(); k++) {
                OTPreenregistrementDetail = lstTPreenregistrementDetail.get(k);
                if (OTPreenregistrementDetail == null) {
                    new logger().OCategory.info(" *** OTPreenregistrementDetail is null depuis ws data preenregistrement *** ");
                }

                str_Product = "<b>" + OTPreenregistrementDetail.getLgFAMILLEID().getIntCIP() + "  " + OTPreenregistrementDetail.getLgFAMILLEID().getStrNAME() + "   " + conversion.AmountFormat(OTPreenregistrementDetail.getIntPRICEUNITAIR(), '.') + " F CFA " + " :  (" + OTPreenregistrementDetail.getIntQUANTITY() + ")</b><br> " + str_Product;

                //Total_vente = Total_vente + lstTPreenregistrementDetail.get(k).getIntPRICE();
            }
            // new logger().OCategory.info(" *** 131 *** ");
            JSONObject json = new JSONObject();
            json.put("lg_PREENREGISTREMENT_ID", lstTPreenregistrement.get(i).getLgPREENREGISTREMENTID());
            json.put("str_REF", lstTPreenregistrement.get(i).getStrREF());
            json.put("lg_USER_ID", lstTPreenregistrement.get(i).getLgUSERVENDEURID().getStrFIRSTNAME() + " " + lstTPreenregistrement.get(i).getLgUSERVENDEURID().getStrLASTNAME());
            json.put("int_PRICE", lstTPreenregistrement.get(i).getIntPRICE());
            json.put("dt_CREATED", date.DateToString(lstTPreenregistrement.get(i).getDtCREATED(), key.formatterShort));
            json.put("str_hour", date.DateToString(lstTPreenregistrement.get(i).getDtCREATED(), key.NomadicUiFormat_Time));
            json.put("str_STATUT", lstTPreenregistrement.get(i).getStrSTATUT());
            json.put("str_FAMILLE_ITEM", str_Product);
            //json.put("str_TYPE_VENTE", lstTPreenregistrement.get(i).getStrTYPEVENTE());
            json.put("str_TYPE_VENTE", lstTPreenregistrement.get(i).getLgTYPEVENTEID().getStrDESCRIPTION());

            json.put("b_IS_CANCEL", lstTPreenregistrement.get(i).getBISCANCEL().toString());
            json.put("lg_EMPLACEMENT_ID", lstTPreenregistrement.get(i).getStrLASTNAMECUSTOMER());
            json.put("str_FIRST_LAST_NAME_CLIENT", lstTPreenregistrement.get(i).getStrFIRSTNAMECUSTOMER());
            // new logger().OCategory.info(" Total_vente  ====== "+Total_vente);

            arrayObj.put(json);

        }

    } catch (Exception er) {
        new logger().OCategory.info("Failed to display list !" + er.toString());
    }
    String result = "({\"total\":\"" + lstTPreenregistrement.size() + " \",\"results\":" + arrayObj.toString() + "})";


%>

<%= result%>
