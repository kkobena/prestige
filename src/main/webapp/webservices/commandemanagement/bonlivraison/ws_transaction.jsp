<%@page import="org.json.JSONObject"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="com.asc.prestige2.business.bonlivraisons.concrete.PrestigeBLService"%>
<%@page import="com.asc.prestige2.business.bonlivraisons.BLService"%>
<%@page import="bll.commandeManagement.bonLivraisonManagement"%>
<%@page import="dal.TBonLivraisonDetail"%>
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
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    bonLivraisonManagement ObonLivraisonManagement = null;
    String lg_BON_LIVRAISON_DETAIL = "%%", lg_BON_LIVRAISON_ID = "%%", lg_ZONE_GEO_ID = "%%", result,
            dt_DATE_REGLEMENT = "", STATUS = "";

    int int_PRIX_REFERENCE = 0, int_PRIX_VENTE = 0, int_PAF = 0,
            int_PA_REEL = 0, int_MONTANT_REGLE = 0, amountMHT = 0, amountMTTC = 0;
    BLService service = new PrestigeBLService();

%>



<%    if (request.getParameter("dt_DATE_REGLEMENT") != null) {
        dt_DATE_REGLEMENT = request.getParameter("dt_DATE_REGLEMENT");
    }
    if (request.getParameter("int_MONTANT_REGLE") != null) {
        int_MONTANT_REGLE = Integer.parseInt(request.getParameter("int_MONTANT_REGLE"));
    }

    if (request.getParameter("lg_BON_LIVRAISON_DETAIL") != null) {
        lg_BON_LIVRAISON_DETAIL = request.getParameter("lg_BON_LIVRAISON_DETAIL");
    }

    // lg_BON_LIVRAISON_ID
    if (request.getParameter("lg_BON_LIVRAISON_ID") != null) {
        lg_BON_LIVRAISON_ID = request.getParameter("lg_BON_LIVRAISON_ID");
    }
    // lg_ZONE_GEO_ID
    if (request.getParameter("lg_ZONE_GEO_ID") != null) {
        lg_ZONE_GEO_ID = request.getParameter("lg_ZONE_GEO_ID");
    }
    if (request.getParameter("int_PRIX_REFERENCE") != null) {
        int_PRIX_REFERENCE = Integer.parseInt(request.getParameter("int_PRIX_REFERENCE"));
    }
    if (request.getParameter("int_PRIX_VENTE") != null) {
        int_PRIX_VENTE = Integer.parseInt(request.getParameter("int_PRIX_VENTE"));
    }
    if (request.getParameter("int_PAF") != null) {
        int_PAF = Integer.parseInt(request.getParameter("int_PAF"));
    }
    if (request.getParameter("int_PA_REEL") != null) {
        int_PA_REEL = Integer.parseInt(request.getParameter("int_PA_REEL"));
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").equals("reglement")) {
          
            boolean regle = false;
            if (STATUS.equalsIgnoreCase("NON REGLE")) {
                regle = service.markNONREGLEBonLivraison(lg_BON_LIVRAISON_ID);
            } else if (STATUS.equalsIgnoreCase("REGLE EN PARTIE")) {
                regle = service.markREGLEENPARTIEBonLivraison(lg_BON_LIVRAISON_ID, date.stringToDate(dt_DATE_REGLEMENT), int_MONTANT_REGLE);
            } else {
                //  regle = service.markREGLEBonLivraison(lg_BON_LIVRAISON_ID, date.stringToDate(dt_DATE_REGLEMENT), int_MONTANT_REGLE);
            }

            if (regle) {
                result = "{success:\"" + 1 + "\"}";
            } else {
                result = "{success:\"" + 0 + "\"}";
            }

        } else if (request.getParameter("mode").equals("modifproductprice")) {

            try {
                ObonLivraisonManagement = new bonLivraisonManagement(OdataManager, OTUser);
                JSONObject json = ObonLivraisonManagement.changePrice(lg_BON_LIVRAISON_DETAIL, int_PRIX_REFERENCE, int_PRIX_VENTE, int_PAF, int_PA_REEL, lg_ZONE_GEO_ID);
                amountMHT = json.getInt("montantMHT");
                amountMTTC = json.getInt("amountMTTC");
                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de création de etiquette");
            }

        } else if (request.getParameter("mode").equals("update")) {

            try {

                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de mise à jour de etiquette");
            }

        } else if (request.getParameter("mode").equals("delete")) {

            try {

                ObllBase.buildSuccesTraceMessage(ObllBase.getOTranslate().getValue("SUCCES"));
            } catch (Exception e) {
                e.printStackTrace();
                ObllBase.buildErrorTraceMessage("Echec de suppression de etiquette");
            }

            // new logger().oCategory.info("Suppression ville " + request.getParameter("lg_BON_LIVRAISON_DETAIL").toString());
        } else if (request.getParameter("mode").equals("closureBonLivraison")) {
            try {
               
                ObonLivraisonManagement = new bonLivraisonManagement(OdataManager, OTUser);
               // ObonLivraisonManagement.closureBonlivraison(lg_BON_LIVRAISON_ID);
              ObonLivraisonManagement.closurerBonlivraison(lg_BON_LIVRAISON_ID); 
                ObllBase.setDetailmessage(ObonLivraisonManagement.getDetailmessage());
                ObllBase.setMessage(ObonLivraisonManagement.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // new logger().oCategory.info("Suppression ville " + request.getParameter("lg_BON_LIVRAISON_DETAIL").toString());
        } else if (request.getParameter("mode").equals("disable")) {
            ObonLivraisonManagement = new bonLivraisonManagement(OdataManager, OTUser);
            ObonLivraisonManagement.deleteBonLivraison(lg_BON_LIVRAISON_ID);
            ObllBase.setDetailmessage(ObonLivraisonManagement.getDetailmessage());
            ObllBase.setMessage(ObonLivraisonManagement.getMessage());

        }
    }

    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getDetailmessage() + "\", amountMHT:\""+amountMHT+"\" , amountMTTC:\""+amountMTTC+"\" , errors: \"" + ObllBase.getMessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getDetailmessage() + "\",   amountMHT:\""+amountMHT+"\" , amountMTTC:\""+amountMTTC+"\" , errors: \"" + ObllBase.getMessage() + "\"}";
    }

%>
<%=result%>