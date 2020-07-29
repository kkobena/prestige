<%@page import="toolkits.utils.conversion"%>
<%@page import="bll.common.Parameter"%>
<%@page import="dal.TParameters"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.gateway.outService.ServicesNotifCustomer"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="bll.report.JournalVente"%>
<%@page import="bll.teller.caisseManagement"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="bll.gateway.outService.ServicesUpdatePriceFamille"%>
<%@page import="bll.gateway.outService.ServiceSoldeCaisseVeille"%>
<%@page import="dal.TAlertEventUserFone"%>
<%@page import="bll.gateway.outService.ServiceSoldeCaisse"%>
<%@page import="dal.TAlertEvent"%>
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
<%@page import="bll.userManagement.*"  %>


<%
  
    dataManager OdataManager = new dataManager();
  TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
%>


<%
Date today = new Date();
    String str_Date_Debut = date.DateToString(today, date.formatterMysqlShort), str_Date_Fin = date.DateToString(today, date.formatterMysqlShort), search_value = "",
            h_debut = "00:00", h_fin = "13:00",lg_USER_ID = "%%", lg_TYPE_REGLEMENT_ID = "%%";
   
    OdataManager.initEntityManager();
    caisseManagement OcaisseManagement = new caisseManagement(OdataManager, OTUser);
    JSONArray arrayObj = new JSONArray();

    JSONObject json = new JSONObject();
    OcaisseManagement.sendSmsChiffreAffaireMiDay(str_Date_Debut, str_Date_Fin, h_debut, h_fin, lg_USER_ID, lg_TYPE_REGLEMENT_ID); // a decommenter apres 04/09/2016
  /*  JournalVente OJournalVente = new JournalVente(OdataManager, OTUser);
        TparameterManager OTparameterManager = new TparameterManager(OdataManager);
        ServicesNotifCustomer OServicesNotifCustomer = new ServicesNotifCustomer(OdataManager, OTUser);
        List<EntityData> listTMvtCaissesFalse = new ArrayList<EntityData>(), listPreenregistrement = new ArrayList<EntityData>();
        TParameters OTParameters = null;
        Double P_SORTIECAISSE_ESPECE_FALSE = 0.0;
        int int_PRICE_TOTAL = 0;
        List<TAlertEventUserFone> lstTAlertEventUserFone = new ArrayList<TAlertEventUserFone>();
        String message = "Récapitulatif de caisse de mi-journée du" + date.formatterShort.format(java.sql.Date.valueOf(str_Date_Debut)) + "\n";
        
        try {

            /*OTParameters = OTparameterManager.getParameter(Parameter.KEY_MOVEMENT_FALSE);
            if (OTParameters != null && Integer.parseInt(OTParameters.getStrVALUE()) == 1 && !str_Date_Debut.equalsIgnoreCase(str_Date_Fin)) {
                listTMvtCaissesFalse = OJournalVente.getAllMouvmentsCaisse(str_Date_Debut, str_Date_Fin, false);
            }
            for (EntityData Odata : listTMvtCaissesFalse) {
                P_SORTIECAISSE_ESPECE_FALSE += (-1) * Double.valueOf(Odata.getStr_value1());
            }
            new logger().OCategory.info("P_SORTIECAISSE_ESPECE_FALSE:" + P_SORTIECAISSE_ESPECE_FALSE);
            listPreenregistrement = OJournalVente.getListeCaisse(str_Date_Debut, str_Date_Fin, h_debut, h_fin, lg_USER_ID, lg_TYPE_REGLEMENT_ID);
            int_PRICE_TOTAL = OJournalVente.getTotalAmountCashTransaction(listPreenregistrement);
            message += "Chiffre d'affaire: "+conversion.AmountFormat(int_PRICE_TOTAL, '.');
            if(int_PRICE_TOTAL > 0) {
                lstTAlertEventUserFone = OdataManager.getEm().createQuery("SELECT t FROM TAlertEventUserFone t WHERE t.strEvent.strEvent LIKE ?1")
                        .setParameter(1, "N_GET_SOLDE_CAISSE").getResultList();
                for (TAlertEventUserFone OTAlertEventUserFone : lstTAlertEventUserFone) {
                    OServicesNotifCustomer.doservice(message, OTAlertEventUserFone.getLgUSERFONEID().getStrPHONE(), str_Date_Debut + OTAlertEventUserFone.getLgUSERFONEID().getLgUSERID().getLgUSERID());
                    OcaisseManagement.buildSuccesTraceMessage("Opération effectuée avec succès");
                }
                
            } else {
                OcaisseManagement.buildErrorTraceMessage("Aucun SMS envoyé");
            }

        } catch (Exception e) {
            OcaisseManagement.buildErrorTraceMessage("Echec d'envoi du SMS");
        }
    
    
     */
    json.put("statut", OcaisseManagement.getMessage());
    json.put("message", OcaisseManagement.getDetailmessage());

    arrayObj.put(json);
    String result = "{\"total\":\"" + OcaisseManagement.getMessage() + " \",\"results\":" + arrayObj.toString() + "}";


%>
<%= result%>