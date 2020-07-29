<%@page import="dal.TCodeGestion"%>
<%@page import="dal.TOptimisationQuantite"%>
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

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TCodeGestion OTCodeGestion = null;
    String lg_CODE_GESTION_ID = "%%", str_CODE_BAREME = "%%", lg_OPTIMISATION_QUANTITE_ID = "%%";

    int int_JOURS_COUVERTURE_STOCK = 0, int_MOIS_HISTORIQUE_VENTE = 0, int_COEFFICIENT_PONDERATION = 0;

    int dt_BUTOIR_ARTICLE, dt_LIMITE_EXTRAPOLATION;

    boolean bool_OPTIMISATION_SEUIL_CMDE;

%>




<%
    if (request.getParameter("lg_CODE_GESTION_ID") != null) {
        lg_CODE_GESTION_ID = request.getParameter("lg_CODE_GESTION_ID");
    }
    if (request.getParameter("str_CODE_BAREME") != null) {
        str_CODE_BAREME = request.getParameter("str_CODE_BAREME");
    }
    if (request.getParameter("int_JOURS_COUVERTURE_STOCK") != null) {
        int_JOURS_COUVERTURE_STOCK = Integer.parseInt(request.getParameter("int_JOURS_COUVERTURE_STOCK"));
    }
    if (request.getParameter("int_MOIS_HISTORIQUE_VENTE") != null) {
        int_MOIS_HISTORIQUE_VENTE = Integer.parseInt(request.getParameter("int_MOIS_HISTORIQUE_VENTE"));
    }
    if (request.getParameter("dt_BUTOIR_ARTICLE") != null) {
        dt_BUTOIR_ARTICLE = Integer.parseInt(request.getParameter("dt_BUTOIR_ARTICLE"));
    }
    if (request.getParameter("dt_LIMITE_EXTRAPOLATION") != null) {
        dt_LIMITE_EXTRAPOLATION = Integer.parseInt(request.getParameter("dt_LIMITE_EXTRAPOLATION"));
    }
    if (request.getParameter("bool_OPTIMISATION_SEUIL_CMDE") != null) {
        bool_OPTIMISATION_SEUIL_CMDE = Boolean.parseBoolean(request.getParameter("bool_OPTIMISATION_SEUIL_CMDE"));
    }
    if (request.getParameter("int_COEFFICIENT_PONDERATION") != null) {
        int_COEFFICIENT_PONDERATION = Integer.parseInt(request.getParameter("int_COEFFICIENT_PONDERATION"));
    }
    if (request.getParameter("lg_OPTIMISATION_QUANTITE_ID") != null) {
        lg_OPTIMISATION_QUANTITE_ID = request.getParameter("lg_OPTIMISATION_QUANTITE_ID");
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_CODE_GESTION_ID"));

    new logger().oCategory.info("lg_OPTIMISATION_QUANTITE_ID   @@@@@@@@@@@@@@@@     " + request.getParameter("lg_OPTIMISATION_QUANTITE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TCodeGestion OTCodeGestion = new TCodeGestion();
            OTCodeGestion.setLgCODEGESTIONID(key.getComplexId());
            OTCodeGestion.setStrCODEBAREME(str_CODE_BAREME);
            OTCodeGestion.setIntJOURSCOUVERTURESTOCK(int_JOURS_COUVERTURE_STOCK);
            OTCodeGestion.setIntMOISHISTORIQUEVENTE(int_MOIS_HISTORIQUE_VENTE);
            OTCodeGestion.setIntDATEBUTOIRARTICLE(dt_BUTOIR_ARTICLE);
            OTCodeGestion.setIntDATELIMITEEXTRAPOLATION(dt_LIMITE_EXTRAPOLATION);
            OTCodeGestion.setBoolOPTIMISATIONSEUILCMDE(bool_OPTIMISATION_SEUIL_CMDE);
            OTCodeGestion.setIntCOEFFICIENTPONDERATION(int_COEFFICIENT_PONDERATION);

            TOptimisationQuantite OTOptimisationQuantite = ObllBase.getOdataManager().getEm().find(TOptimisationQuantite.class, lg_OPTIMISATION_QUANTITE_ID);
            if (OTOptimisationQuantite == null) {
                ObllBase.buildErrorTraceMessage("Impossible de creer un " + OTOptimisationQuantite, "Ref TOptimisationQuantite : " + lg_OPTIMISATION_QUANTITE_ID + "  Invalide ");
                return;
            }
            OTCodeGestion.setLgOPTIMISATIONQUANTITEID(OTOptimisationQuantite);

            OTCodeGestion.setStrSTATUT(commonparameter.statut_enable);
            OTCodeGestion.setDtCREATED(new Date());

            ObllBase.persiste(OTCodeGestion);
            new logger().oCategory.info("Mise a jour OTCodeGestion " + OTCodeGestion.getLgCODEGESTIONID() + " CODEBAREME " + OTCodeGestion.getStrCODEBAREME());

        } else if (request.getParameter("mode").toString().equals("update")) {

            dal.TCodeGestion OTCodeGestion = null;
            OTCodeGestion = ObllBase.getOdataManager().getEm().find(dal.TCodeGestion.class, request.getParameter("lg_CODE_GESTION_ID").toString());

            try {

                dal.TOptimisationQuantite OTOptimisationQuantite = ObllBase.getOdataManager().getEm().find(dal.TOptimisationQuantite.class, request.getParameter("lg_OPTIMISATION_QUANTITE_ID").toString());
                new logger().oCategory.info("lg_OPTIMISATION_QUANTITE_ID     Create   " + lg_OPTIMISATION_QUANTITE_ID + "  lg_OPTIMISATION_QUANTITE_ID du request       " + request.getParameter("lg_OPTIMISATION_QUANTITE_ID").toString());

                if (OTOptimisationQuantite != null) {
                    OTCodeGestion.setLgOPTIMISATIONQUANTITEID(OTOptimisationQuantite);
                }
            } catch (Exception e) {

            }

            OTCodeGestion.setStrCODEBAREME(str_CODE_BAREME);
            OTCodeGestion.setIntJOURSCOUVERTURESTOCK(int_JOURS_COUVERTURE_STOCK);
            OTCodeGestion.setIntMOISHISTORIQUEVENTE(int_MOIS_HISTORIQUE_VENTE);
            OTCodeGestion.setIntDATEBUTOIRARTICLE(dt_BUTOIR_ARTICLE);
            OTCodeGestion.setIntDATELIMITEEXTRAPOLATION(dt_LIMITE_EXTRAPOLATION);
            OTCodeGestion.setBoolOPTIMISATIONSEUILCMDE(bool_OPTIMISATION_SEUIL_CMDE);
            OTCodeGestion.setIntCOEFFICIENTPONDERATION(int_COEFFICIENT_PONDERATION);
            OTCodeGestion.setStrSTATUT(commonparameter.statut_enable);
            OTCodeGestion.setDtUPDATED(new Date());
            
            ObllBase.persiste(OTCodeGestion);
            new logger().oCategory.info("Mise a jour OTCodeGestion " + OTCodeGestion.getLgCODEGESTIONID()+ " StrLabel " + OTCodeGestion.getStrCODEBAREME());

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TCodeGestion OTCodeGestion = null;
            OTCodeGestion = ObllBase.getOdataManager().getEm().find(dal.TCodeGestion.class, request.getParameter("lg_CODE_GESTION_ID"));
   
            OTCodeGestion.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTCodeGestion);

            new logger().oCategory.info("Suppression de code gestion " + request.getParameter("lg_CODE_GESTION_ID").toString());

        } else {
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);


%>
<%=result%>