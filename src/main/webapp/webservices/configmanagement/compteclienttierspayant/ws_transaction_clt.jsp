<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TCompteClient"%>
<%@page import="dal.TClient"%>
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

%>




<%    String lg_COMPTE_CLIENT_ID = "%%", lg_COMPTE_CLIENT_TIERS_PAYANT_ID = "%%", lg_TIERS_PAYANT_ID = "%%", str_NUMERO_SECURITE_SOCIAL = "", lg_TYPE_CLIENT_ID = "";
    int int_POURCENTAGE = 0, int_PRIORITY = 1;
    double dbl_QUOTA_CONSO_MENSUELLE = 0.0, dbl_QUOTA_CONSO_VENTE = 0.0,dbl_PLAFOND=0;
    Integer db_PLAFOND_ENCOURS=0;
    boolean b_IsAbsolute=false; 

         if (request.getParameter("dbl_PLAFOND") != null) {
        dbl_PLAFOND = Integer.valueOf(request.getParameter("dbl_PLAFOND"))   ; 
        
    }
     if (request.getParameter("db_PLAFOND_ENCOURS") != null) {
        db_PLAFOND_ENCOURS = Integer.valueOf(request.getParameter("db_PLAFOND_ENCOURS"))   ; 
        
    }
    if (request.getParameter("b_IsAbsolute") != null) {
        b_IsAbsolute = Boolean.valueOf(request.getParameter("b_IsAbsolute"))  ; 
        
    }
    
    if (request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID") != null) {
        lg_COMPTE_CLIENT_TIERS_PAYANT_ID = request.getParameter("lg_COMPTE_CLIENT_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_TIERS_PAYANT_ID " + lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
    }

    if (request.getParameter("str_NUMERO_SECURITE_SOCIAL") != null) {
        str_NUMERO_SECURITE_SOCIAL = request.getParameter("str_NUMERO_SECURITE_SOCIAL");
        new logger().OCategory.info("str_NUMERO_SECURITE_SOCIAL " + str_NUMERO_SECURITE_SOCIAL);
    }

    if (request.getParameter("dbl_QUOTA_CONSO_MENSUELLE") != null) {
        dbl_QUOTA_CONSO_MENSUELLE = Double.parseDouble(request.getParameter("dbl_QUOTA_CONSO_MENSUELLE"));
        new logger().OCategory.info("dbl_QUOTA_CONSO_MENSUELLE " + dbl_QUOTA_CONSO_MENSUELLE);
    }
    if (request.getParameter("dbl_QUOTA_CONSO_VENTE") != null) {
        dbl_QUOTA_CONSO_VENTE = Double.parseDouble(request.getParameter("dbl_QUOTA_CONSO_VENTE"));
        new logger().OCategory.info("dbl_QUOTA_CONSO_VENTE " + dbl_QUOTA_CONSO_VENTE);
    }

    if (request.getParameter("int_PRIORITY") != null) {
        int_PRIORITY = Integer.parseInt(request.getParameter("int_PRIORITY"));
        new logger().OCategory.info("int_PRIORITY " + int_PRIORITY);
    }

    if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
        lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
        new logger().OCategory.info("lg_COMPTE_CLIENT_ID " + lg_COMPTE_CLIENT_ID);
    }
    if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
        lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
        new logger().OCategory.info("lg_TIERS_PAYANT_ID " + lg_TIERS_PAYANT_ID);
    }

    if (request.getParameter("int_POURCENTAGE") != null) {
        int_POURCENTAGE = new Integer((request.getParameter("int_POURCENTAGE")));
        new logger().OCategory.info("int_POURCENTAGE " + int_POURCENTAGE);
    }
    if (request.getParameter("lg_TYPE_CLIENT_ID") != null) {
        lg_TYPE_CLIENT_ID = request.getParameter("lg_TYPE_CLIENT_ID");
        new logger().OCategory.info("lg_TYPE_CLIENT_ID " + lg_TYPE_CLIENT_ID);
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
  TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION RETOUR");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_COMPTE_CLIENT_ID"));

    new logger().oCategory.info("lg_COMPTE_CLIENT_ID   @@@@@@@@@@@@@@@@     " + request.getParameter("lg_COMPTE_CLIENT_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            OtierspayantManagement.create_compteclt_tierspayant(lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE, int_PRIORITY, dbl_PLAFOND, dbl_QUOTA_CONSO_VENTE, str_NUMERO_SECURITE_SOCIAL,db_PLAFOND_ENCOURS,b_IsAbsolute);
            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());
            /* try {
             OTCompteClientTiersPayant = (TCompteClientTiersPayant) OdataManager.getEm().createQuery("SELECT t FROM TCompteClientTiersPayant t WHERE t.lgCOMPTECLIENTID.lgCOMPTECLIENTID LIKE ?1  AND t.lgTIERSPAYANTID.lgTIERSPAYANTID LIKE ?2  AND t.strSTATUT LIKE ?3").
             setParameter(1, lg_COMPTE_CLIENT_ID)
             .setParameter(2, lg_TIERS_PAYANT_ID)
             .setParameter(3, commonparameter.statut_enable)
             .getSingleResult();
                

             OTCompteClientTiersPayant.setIntPOURCENTAGE(int_POURCENTAGE);
             OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
             OTCompteClientTiersPayant.setDtCREATED(new Date());

             OTCompteClient = ObllBase.getOdataManager().getEm().find(TCompteClient.class, lg_COMPTE_CLIENT_ID);
             if (OTCompteClient != null) {
             OTCompteClientTiersPayant.setLgCOMPTECLIENTID(OTCompteClient);
             }

             OTTiersPayant = ObllBase.getOdataManager().getEm().find(TTiersPayant.class, lg_TIERS_PAYANT_ID);
             if (OTTiersPayant != null) {
             OTCompteClientTiersPayant.setLgTIERSPAYANTID(OTTiersPayant);
             }

             ObllBase.persiste(OTCompteClientTiersPayant);

             new logger().OCategory.info("*** Desole ce tiers payant existe deja *** ");
             ObllBase.setMessage("Error");
             ObllBase.setDetailmessage("Desole ce tiers payant existe deja");

             } catch (Exception e) {

             OTCompteClientTiersPayant = new TCompteClientTiersPayant();
             new logger().OCategory.info("lg_COMPTE_CLIENT_ID  " + lg_COMPTE_CLIENT_ID);
             OTCompteClientTiersPayant.setLgCOMPTECLIENTTIERSPAYANTID(key.getComplexId());
             OTCompteClientTiersPayant.setIntPOURCENTAGE(int_POURCENTAGE);
             OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
             OTCompteClientTiersPayant.setDtCREATED(new Date());

             OTCompteClient = ObllBase.getOdataManager().getEm().find(TCompteClient.class, lg_COMPTE_CLIENT_ID);
             if (OTCompteClient != null) {
             OTCompteClientTiersPayant.setLgCOMPTECLIENTID(OTCompteClient);
             }

             OTTiersPayant = ObllBase.getOdataManager().getEm().find(TTiersPayant.class, lg_TIERS_PAYANT_ID);
             if (OTTiersPayant != null) {
             OTCompteClientTiersPayant.setLgTIERSPAYANTID(OTTiersPayant);
             }

             ObllBase.persiste(OTCompteClientTiersPayant);
             new logger().oCategory.info("Creation OTCompteClient " + OTCompteClientTiersPayant.getLgCOMPTECLIENTID().getLgCOMPTECLIENTID());

             }*/

        } else if (request.getParameter("mode").equals("createstandartdclient")) {
            OtierspayantManagement.createCompteClientTiersPayant(lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE, int_PRIORITY, lg_TYPE_CLIENT_ID);
            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());

        } else if (request.getParameter("mode").equals("update")) {
           boolean modeupdate=false;
           if(request.getParameter("modeupdate")!=null){
               modeupdate=Boolean.valueOf(request.getParameter("modeupdate"));
           }
            OtierspayantManagement.updateComptecltTierspayant(lg_COMPTE_CLIENT_TIERS_PAYANT_ID, lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE, int_PRIORITY, dbl_PLAFOND, dbl_QUOTA_CONSO_VENTE, str_NUMERO_SECURITE_SOCIAL,db_PLAFOND_ENCOURS,modeupdate,b_IsAbsolute);
            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());
            /*OTCompteClientTiersPayant = ObllBase.getOdataManager().getEm().find(TCompteClientTiersPayant.class, lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
             new logger().OCategory.info("lg_COMPTE_CLIENT_TIERS_PAYANT_ID  " + lg_COMPTE_CLIENT_TIERS_PAYANT_ID);

             OTCompteClientTiersPayant.setIntPOURCENTAGE(int_POURCENTAGE);
             OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_enable);
             OTCompteClientTiersPayant.setDtCREATED(new Date());

             OTCompteClient = ObllBase.getOdataManager().getEm().find(TCompteClient.class, lg_COMPTE_CLIENT_ID);
             if (OTCompteClient != null) {
             OTCompteClientTiersPayant.setLgCOMPTECLIENTID(OTCompteClient);
             }

             OTTiersPayant = ObllBase.getOdataManager().getEm().find(TTiersPayant.class, lg_TIERS_PAYANT_ID);
             if (OTTiersPayant != null) {
             OTCompteClientTiersPayant.setLgTIERSPAYANTID(OTTiersPayant);
             }

             ObllBase.persiste(OTCompteClientTiersPayant);
             new logger().oCategory.info("Creation OTCompteClient " + OTCompteClientTiersPayant.getLgCOMPTECLIENTID().getLgCOMPTECLIENTID());
             */
        } else if (request.getParameter("mode").toString().equals("delete")) {

            /*OTCompteClientTiersPayant = ObllBase.getOdataManager().getEm().find(TCompteClientTiersPayant.class, lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
             new logger().OCategory.info("lg_COMPTE_CLIENT_TIERS_PAYANT_ID  " + lg_COMPTE_CLIENT_TIERS_PAYANT_ID);

             OTCompteClientTiersPayant.setStrSTATUT(commonparameter.statut_delete);
             ObllBase.persiste(OTCompteClientTiersPayant);*/
            OtierspayantManagement.deleteComptecltTierspayant(lg_COMPTE_CLIENT_TIERS_PAYANT_ID);
            ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
            ObllBase.setMessage(OtierspayantManagement.getMessage());

            //new logger().oCategory.info("Suppression de compte client " + request.getParameter("lg_COMPTE_CLIENT_ID").toString());
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