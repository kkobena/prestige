<%@page import="bll.teller.tellerManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.TFamille"%>
<%@page import="bll.configManagement.familleManagement"%>
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

%>



<%
    String str_NAME = "", int_CIP = "", str_DESCRIPTION = "", lg_FAMILLE_PARENT_ID = "", lg_FAMILLE_ID = "";
    int int_NUMBER = 0, int_NUMBERDETAIL = 0;

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    if (request.getParameter("str_NAME") != null) {
        str_NAME = request.getParameter("str_NAME");
        new logger().OCategory.info("str_NAME " + str_NAME);
    }

    OdataManager.initEntityManager();
    familleManagement OfamilleManagement = new familleManagement(OdataManager, OTUser);
   String code_statut = commonparameter.PROCESS_FAILED;
    String desc_statut = "Référence déconditionné non valide";
    

    try {
        TFamille OFamille = OfamilleManagement.getTFamille(str_NAME);
        new logger().OCategory.info("Deconditionné "+OFamille.getStrDESCRIPTION() + " id parent "+OFamille.getLgFAMILLEPARENTID());
        if (OFamille != null) {
            TFamilleStock OTFamilleStock = new tellerManagement(OdataManager, OTUser).getTProductItemStock(OFamille.getLgFAMILLEID());
            TFamille OFamilleInit = OfamilleManagement.getTFamille(OFamille.getLgFAMILLEPARENTID());
            str_DESCRIPTION = OFamille.getStrNAME();
            int_NUMBER = OTFamilleStock.getIntNUMBERAVAILABLE();
            int_CIP = OFamilleInit.getIntCIP();
            int_NUMBERDETAIL = OFamilleInit.getIntNUMBERDETAIL();
            lg_FAMILLE_ID = OFamille.getLgFAMILLEID();
            lg_FAMILLE_PARENT_ID = OFamilleInit.getLgFAMILLEID();
            new logger().OCategory.info("Initiale "+OFamilleInit.getStrDESCRIPTION() + " Quantité détail "+ OFamilleInit.getIntNUMBERDETAIL());
            code_statut = commonparameter.PROCESS_SUCCESS;
            desc_statut = "Déconditionné trouvé";
        }
        
    } catch (Exception e) {
        
    }

    new logger().OCategory.info("code_statut " + code_statut + " desc_statut " + desc_statut);
    
    String result = "({\"str_DESCRIPTION\": \"" + str_DESCRIPTION + "\",\"errors_code\": \"" + code_statut + "\", \"errors\": \"" + desc_statut + "\", \"int_CIP\": \"" + int_CIP + "\", \"lg_FAMILLE_ID\":\"" + lg_FAMILLE_ID + "\", \"lg_FAMILLE_PARENT_ID\":\"" + lg_FAMILLE_PARENT_ID + "\",\"int_NUMBER\":" + int_NUMBER + ",\"int_NUMBERDETAIL\":" + int_NUMBERDETAIL + "})";
    new logger().OCategory.info("result " + result);
%>

<%= result%>
