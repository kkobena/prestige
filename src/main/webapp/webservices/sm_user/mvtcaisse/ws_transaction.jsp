<%@page import="dal.TCoffreCaisse"%>
<%@page import="dal.TTypeMvtCaisse"%>
<%@page import="dal.TMvtCaisse"%>
<%@page import="dal.TResumeCaisse"%>
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

<%    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
 
    TCoffreCaisse OTCoffreCaisse = null;
    
%>
<%
    
    String str_REF = "", dt_DATE_MVT = "",str_NOM="", str_COMMENTAIRE_MOUVEMENT = "";
    String lg_MVT_CAISSE_ID = "", lg_TYPE_MVT_CAISSE_ID = "", str_NUM_COMPTE = "",
            str_NUM_PIECE_COMPTABLE = "", lg_MODE_REGLEMENT_ID = "", str_BANQUE = "", str_LIEU = "", str_CODE_MONNAIE = "", str_COMMENTAIRE = "";
    double int_AMOUNT = 0.0;
    int int_TAUX = 0;
    
    boolean bool_CHECKED = true;
    
    
    if (request.getParameter("lg_MVT_CAISSE_ID") != null) {
        lg_MVT_CAISSE_ID = request.getParameter("lg_MVT_CAISSE_ID");
    }
    
    if (request.getParameter("bool_CHECKED") != null) {
        bool_CHECKED = Boolean.parseBoolean(request.getParameter("bool_CHECKED"));
    }
    
    if (request.getParameter("lg_TYPE_MVT_CAISSE_ID") != null) {
        lg_TYPE_MVT_CAISSE_ID = request.getParameter("lg_TYPE_MVT_CAISSE_ID");
    }
    
    if (request.getParameter("str_NUM_COMPTE") != null) {
        str_NUM_COMPTE = (request.getParameter("str_NUM_COMPTE"));
    }
    if (request.getParameter("str_NUM_PIECE_COMPTABLE") != null) {
        str_NUM_PIECE_COMPTABLE = (request.getParameter("str_NUM_PIECE_COMPTABLE"));
    }
    if (request.getParameter("lg_MODE_REGLEMENT_ID") != null) {
        lg_MODE_REGLEMENT_ID = (request.getParameter("lg_MODE_REGLEMENT_ID"));
    }
      if (request.getParameter("str_NOM") != null && !"".equals(request.getParameter("str_NOM")))  {
        str_NOM = request.getParameter("str_NOM");
        new logger().OCategory.info("str_NOM  ++++++++++++++++++++++++++++++++++++= "+str_NOM);
        
    }
    
    if (request.getParameter("str_BANQUE") != null) {
        str_BANQUE = (request.getParameter("str_BANQUE"));
    }
    if (request.getParameter("str_LIEU") != null) {
        str_LIEU = (request.getParameter("str_LIEU"));
    }
    if (request.getParameter("str_CODE_MONNAIE") != null) {
        str_CODE_MONNAIE = (request.getParameter("str_CODE_MONNAIE"));
    }
    if (request.getParameter("str_COMMENTAIRE") != null) {
        str_COMMENTAIRE = (request.getParameter("str_COMMENTAIRE"));
    }
    if (request.getParameter("str_COMMENTAIRE_MOUVEMENT") != null) {
        str_COMMENTAIRE_MOUVEMENT = (request.getParameter("str_COMMENTAIRE_MOUVEMENT"));
    }
    
    if (request.getParameter("int_TAUX") != null) {
        int_TAUX = Integer.parseInt(request.getParameter("int_TAUX"));
    }
    if (request.getParameter("int_AMOUNT") != null) {
        int_AMOUNT = Double.parseDouble(request.getParameter("int_AMOUNT"));
    }
    
    if (request.getParameter("dt_DATE_MVT") != null) {
        dt_DATE_MVT = (request.getParameter("dt_DATE_MVT"));
    }
    
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);
    
    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    
    bll.teller.TellerMovement OTellerMovement = new bll.teller.TellerMovement(ObllBase.getOdataManager(), ObllBase.getOTUser());
    TMvtCaisse OTMvtCaisse = null;
    if (request.getParameter("mode") != null) {
        Date dt_operation = key.stringToDate(dt_DATE_MVT, key.formatterMysqlShort2);
        if (request.getParameter("mode").toString().equals("create")) {
              //new logger().oCategory.info("creation mvmt caisse +++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
            TTypeMvtCaisse OTTypeMvtCaisse = ObllBase.getOdataManager().getEm().find(TTypeMvtCaisse.class, lg_TYPE_MVT_CAISSE_ID);
            //OTMvtCaisse = OTellerMovement.AddTMvtCaisse(OTTypeMvtCaisse, str_NUM_COMPTE, str_NUM_PIECE_COMPTABLE, lg_MODE_REGLEMENT_ID, int_AMOUNT, str_BANQUE, str_LIEU, str_CODE_MONNAIE, str_COMMENTAIRE, int_TAUX, dt_operation);
            OTMvtCaisse = OTellerMovement.AddTMvtCaisse(OTTypeMvtCaisse, OTTypeMvtCaisse.getStrCODECOMPTABLE(), str_NUM_PIECE_COMPTABLE, lg_MODE_REGLEMENT_ID, int_AMOUNT, str_BANQUE, str_LIEU, str_CODE_MONNAIE, str_COMMENTAIRE, int_TAUX, dt_operation, true,str_NOM,OTUser.getLgUSERID(), str_COMMENTAIRE_MOUVEMENT, 0, (int) int_AMOUNT, bool_CHECKED, new Date());
            if(OTMvtCaisse != null) {
                str_REF = OTMvtCaisse.getLgMVTCAISSEID();
                new logger().oCategory.info("str_REF "+str_REF);
            }
            
            ObllBase.setMessage(OTellerMovement.getMessage());
            ObllBase.setDetailmessage(OTellerMovement.getDetailmessage());
            
        } else if (request.getParameter("mode").toString().equals("update")) {
            new logger().OCategory.info("lg_MVT_CAISSE_ID  " + lg_MVT_CAISSE_ID);
            
            OTellerMovement.UpdateTMvtCaisse(lg_MVT_CAISSE_ID, lg_TYPE_MVT_CAISSE_ID, str_NUM_COMPTE, str_NUM_PIECE_COMPTABLE, lg_MODE_REGLEMENT_ID, int_AMOUNT, str_BANQUE, str_LIEU, str_CODE_MONNAIE, str_COMMENTAIRE, int_TAUX, dt_operation);
            
            ObllBase.setMessage(OTellerMovement.getMessage());
            ObllBase.setDetailmessage(OTellerMovement.getDetailmessage());
            
        } else if (request.getParameter("mode").toString().equals("delete")) {
            
            try {
                new logger().OCategory.info(" resume caisse id   " + request.getParameter("lg_RESUME_CAISSE_ID"));
                OTMvtCaisse = ObllBase.getOdataManager().getEm().find(TMvtCaisse.class, request.getParameter("lg_MVT_CAISSE_ID"));
                OTMvtCaisse.setStrSTATUT(commonparameter.statut_delete);
                ObllBase.persiste(OTMvtCaisse);
                ObllBase.setMessage(OTellerMovement.getMessage());
                ObllBase.setDetailmessage(OTellerMovement.getDetailmessage());
                
            } catch (Exception e) {
                ObllBase.buildErrorTraceMessage("ERROR", "DESOLE PAS DE OTMvtCaisse POUR CETTE OPERATION " + e.toString() + "");
            }
            
        }  if (request.getParameter("mode").toString().equals("createfalse")) {
             TTypeMvtCaisse OTTypeMvtCaisse = ObllBase.getOdataManager().getEm().find(TTypeMvtCaisse.class, lg_TYPE_MVT_CAISSE_ID);
            OTMvtCaisse = OTellerMovement.AddTMvtCaisse(OTTypeMvtCaisse, OTTypeMvtCaisse.getStrCODECOMPTABLE(), str_NUM_PIECE_COMPTABLE, lg_MODE_REGLEMENT_ID, int_AMOUNT, str_BANQUE, str_LIEU, str_CODE_MONNAIE, str_COMMENTAIRE, int_TAUX, dt_operation, true,str_NOM,OTUser.getLgUSERID(), str_COMMENTAIRE_MOUVEMENT, 0, (int) int_AMOUNT, bool_CHECKED, dt_operation);
            if(OTMvtCaisse != null) {
                str_REF = OTMvtCaisse.getLgMVTCAISSEID();
                new logger().oCategory.info("str_REF "+str_REF);
            }
            
            ObllBase.setMessage(OTellerMovement.getMessage());
            ObllBase.setDetailmessage(OTellerMovement.getDetailmessage());
            
        } else {
        }
        
    }
    
    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\", str_REF: \"" + str_REF + "\"}";
        
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\", str_REF: \"" + str_REF + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>