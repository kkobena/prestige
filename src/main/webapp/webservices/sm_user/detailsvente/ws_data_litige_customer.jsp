<%@page import="dal.TCompteClient"%>
<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.TPreenregistrementCompteClient"%>
<%@page import="dal.TPreenregistrementDetail"%>
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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();
    date str_LAST_CONNECTION_DATE;
    privilege Oprivilege = new privilege();
    json Ojson = new json();
    

%>



<%
   List<TPreenregistrementCompteClientTiersPayent> lstTPreenregistrementCompteClientTiersPayent = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
    String lg_PREENREGISTREMENT_ID = "", str_REF = "", str_STATUT = commonparameter.statut_is_Closed;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager, OTUser);

    new logger().OCategory.info("dans ws data vente a envoyer en litige");

    if (request.getParameter("str_STATUT") != null) {
        str_STATUT = request.getParameter("str_STATUT").toString();
        new logger().OCategory.info("str_STATUT  " + str_STATUT);
    }

    if (request.getParameter("str_REF") != null) {
        str_REF = request.getParameter("str_REF").toString();
        new logger().OCategory.info("str_REF  " + str_REF);
    }


    JSONArray arrayObj = new JSONArray();

    String str_MEDECIN = "";
    String lg_TYPE_VENTE_ID = "";
    String code_statut = commonparameter.PROCESS_FAILED;
    String desc_statut = "Référence non valide";
    String str_FIRST_LAST_NAME = "";

    try {
        TPreenregistrement OTPreenregistrement = OPreenregistrement.getTPreenregistrementByRef(str_REF);
        if (OTPreenregistrement != null) {
            code_statut = commonparameter.PROCESS_SUCCESS;
            desc_statut = OTPreenregistrement.getLgPREENREGISTREMENTID();
            lstTPreenregistrementCompteClientTiersPayent = OtierspayantManagement.ShowAllOrOneTierspayantByVente("", "%%", "%%", OTPreenregistrement.getLgPREENREGISTREMENTID(), str_STATUT);
            if(lstTPreenregistrementCompteClientTiersPayent.size() > 0) {
                TCompteClient OTCompteClient = lstTPreenregistrementCompteClientTiersPayent.get(0).getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTID();
                str_FIRST_LAST_NAME = OTCompteClient.getLgCLIENTID().getStrFIRSTNAME() + " " + OTCompteClient.getLgCLIENTID().getStrLASTNAME();
            }
            

        }
    } catch (Exception e) {

    }

    new logger().OCategory.info("code_statut " + code_statut + " desc_statut " + desc_statut);


     String result = "({\"errors_code\": \"" + code_statut + "\", \"errors\": \"" + desc_statut + "\", \"str_MEDECIN\":\"" + str_MEDECIN + "\", \"str_FIRST_LAST_NAME\":\"" + str_FIRST_LAST_NAME + "\"})";
    new logger().OCategory.info("result " + result);
%>

<%= result%>
