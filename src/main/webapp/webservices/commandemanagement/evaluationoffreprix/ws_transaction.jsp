<%@page import="dal.TEvaluationoffreprix"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="toolkits.utils.jdom"%>
<%@page import="java.io.File"%>
<%@page import="dal.TFamille"%>
<%@page import="dal.TGrossiste"%>
<%@page import="bll.commandeManagement.orderManagement"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="dal.TEvaluationoffreprix"%>
<%@page import="dal.TTypeRemise"%>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>
<%@page import="bll.configManagement.familleManagement"  %>

<%
    dataManager OdataManager = new dataManager();
    TEvaluationoffreprix OTEvaluationoffreprix = null;
    TUser OTUser = null;

%>


<%    int int_UG = 0, int_NUMBER = 0, int_PRICE = 0;
    String lg_EVALUATIONOFFREPRIX_ID = "", lg_FAMILLE_ID = "";
    Double PRIX_ACHAT_TOTAL = 0.0;
    
    if (request.getParameter("lg_EVALUATIONOFFREPRIX_ID") != null) {
        lg_EVALUATIONOFFREPRIX_ID = request.getParameter("lg_EVALUATIONOFFREPRIX_ID");
        new logger().oCategory.info("lg_EVALUATIONOFFREPRIX_ID : " + lg_EVALUATIONOFFREPRIX_ID);
    }
    
  /*  if (request.getParameter("str_PRESTATAIRE") != null) {
        str_PRESTATAIRE = request.getParameter("str_PRESTATAIRE");
        new logger().oCategory.info("str_PRESTATAIRE : " + str_PRESTATAIRE);
    }

    if (request.getParameter("lg_EVALUATIONOFFREPRIX_DETAIL_ID") != null) {
        lg_EVALUATIONOFFREPRIX_DETAIL_ID = request.getParameter("lg_EVALUATIONOFFREPRIX_DETAIL_ID");
        new logger().oCategory.info("lg_EVALUATIONOFFREPRIX_DETAIL_ID : " + lg_EVALUATIONOFFREPRIX_DETAIL_ID);
    }*/

    if (request.getParameter("lg_FAMILLE_ID") != null) {
        lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        new logger().oCategory.info("lg_FAMILLE_ID : " + lg_FAMILLE_ID);
    }

    if (request.getParameter("int_UG") != null) {
        int_UG = Integer.parseInt(request.getParameter("int_UG"));
        new logger().OCategory.info("int_UG " + int_UG);
    }

    if (request.getParameter("int_NUMBER") != null) {
        int_NUMBER = Integer.parseInt(request.getParameter("int_NUMBER"));
        new logger().OCategory.info("int_NUMBER " + int_NUMBER);
    }

    if (request.getParameter("int_PRICE") != null) {
        int_PRICE = Integer.parseInt(request.getParameter("int_PRICE"));
        new logger().OCategory.info("int_PRICE " + int_PRICE);
    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    orderManagement OorderManagement = new orderManagement(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
           /* if (request.getParameter("lg_EVALUATIONOFFREPRIX_ID").toString().equals("0")) {
                OTEvaluationoffreprix = OorderManagement.addProductToEvaluateOffer(str_PRESTATAIRE);
            } else {
                OTEvaluationoffreprix = OorderManagement.updateProductToEvaluateOffer(lg_EVALUATIONOFFREPRIX_ID, str_PRESTATAIRE);
            }

            if (OTEvaluationoffreprix != null) {
                str_REF = OTEvaluationoffreprix.getLgEVALUATIONOFFREPRIXID();
                PRIX_ACHAT_TOTAL = OTEvaluationoffreprix.getIntPRICEOFFRE();
                new logger().OCategory.info("str_REF nouveau " + str_REF);
            }*/

            OorderManagement.addProductToEvaluateOffer(lg_FAMILLE_ID, int_NUMBER, int_UG, int_PRICE);
            PRIX_ACHAT_TOTAL = OorderManagement.getTotalAchatOffre(OorderManagement.getAllTEvaluationoffreprix("", commonparameter.statut_enable));
        } else if (request.getParameter("mode").toString().equals("update")) {

            /*OTEvaluationoffreprix = OorderManagement.updateProductToEvaluateOfferDetail(lg_EVALUATIONOFFREPRIX_DETAIL_ID, int_NUMBER, int_UG, int_PRICE);
            if (OTEvaluationoffreprix != null) {
                PRIX_ACHAT_TOTAL = OTEvaluationoffreprix.getIntPRICEOFFRE();
            }*/
            OorderManagement.updateProductToEvaluateOffer(lg_EVALUATIONOFFREPRIX_ID, int_NUMBER, int_UG, int_PRICE);
             PRIX_ACHAT_TOTAL = OorderManagement.getTotalAchatOffre(OorderManagement.getAllTEvaluationoffreprix("", commonparameter.statut_enable));
        } else if (request.getParameter("mode").toString().equals("delete")) {
            OorderManagement.deleteProductToEvaluateOffer(lg_EVALUATIONOFFREPRIX_ID);
             PRIX_ACHAT_TOTAL = OorderManagement.getTotalAchatOffre(OorderManagement.getAllTEvaluationoffreprix("", commonparameter.statut_enable));
        }/* else if (request.getParameter("mode").toString().equals("deletedetail")) {
            OTEvaluationoffreprix = OorderManagement.deleteProductToEvaluateOfferDetail(lg_EVALUATIONOFFREPRIX_DETAIL_ID);
            if (OTEvaluationoffreprix != null) {
                PRIX_ACHAT_TOTAL = OTEvaluationoffreprix.getIntPRICEOFFRE();
            }
        } */else if (request.getParameter("mode").toString().equals("closure")) {
            OorderManagement.closureEvaluationOffer();
        } else if (request.getParameter("mode").toString().equals("init")) {
             PRIX_ACHAT_TOTAL = OorderManagement.getTotalAchatOffre(OorderManagement.getAllTEvaluationoffreprix("", commonparameter.statut_enable));
             if(PRIX_ACHAT_TOTAL > 0) {
                 OorderManagement.buildSuccesTraceMessage("Valeur Achat de l'évaluation: "+PRIX_ACHAT_TOTAL);
             } else {
                 OorderManagement.buildErrorTraceMessage("Liste vide");
             }
        }
    }

    String result;
    if (OorderManagement.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + OorderManagement.getMessage() + "\", errors: \"" + OorderManagement.getDetailmessage() + "\", PRIX_ACHAT_TOTAL: \"" + PRIX_ACHAT_TOTAL + "\"}";

    } else {
        result = "{success:\"" + OorderManagement.getMessage() + "\", errors: \"" + OorderManagement.getDetailmessage() + "\", PRIX_ACHAT_TOTAL: \"" + PRIX_ACHAT_TOTAL + "\"}";
    }
    new logger().OCategory.info("JSON " + result);

    /* OdataManager = null;
     OorderManagement = null;*/

%>
<%=result%>