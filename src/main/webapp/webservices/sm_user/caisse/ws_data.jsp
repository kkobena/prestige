<%@page import="dal.TResumeCaisse"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.dataManager" %>
<%@page import="bll.teller.caisseManagement" %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TCaisse"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>            
<%@page import="org.json.JSONArray"  %> 
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% 
    dataManager OdataManager = new dataManager();
    


%>

<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    String Date_debut = "", Date_Fin = "";
    date key = new date();
    Date dt_Date_debut, dt_Date_Fin;
    Date_debut = key.GetDateNowForSearch(0);
    Date_Fin = key.GetDateNowForSearch(1);
    new logger().OCategory.info(Date_debut + "  ouverture caisse   " + Date_Fin);

    dt_Date_Fin = key.stringToDate(Date_Fin, key.formatterShort);
    dt_Date_debut = key.stringToDate(Date_debut, key.formatterShort);

    OdataManager.initEntityManager();
    caisseManagement OcaisseManagement = new caisseManagement(OdataManager, OTUser);
    TCaisse OTCaisse = OcaisseManagement.GetTCaisse(OTUser.getLgUSERID());//  GetSoldeCaisse("31017105229982132919");
    String str_satut = commonparameter.statut_is_Closed;

    if (OcaisseManagement.CheckResumeCaisse()) {
        str_satut = commonparameter.statut_enable;
    }
    String lg_RESUME_CAISSE_ID = "";

    Date_debut = key.GetDateNowForSearch(0);
    Date_Fin = key.GetDateNowForSearch(1);
    int int_SOLDE_MATIN = 0;
    new logger().OCategory.info(Date_debut + " " + Date_Fin);

    dt_Date_Fin = key.stringToDate(Date_Fin, key.formatterShort);
    dt_Date_debut = key.stringToDate(Date_debut, key.formatterShort);

    try {

        /* OTResumeCaisse = (TResumeCaisse) OdataManager.getEm().createQuery("SELECT t FROM TResumeCaisse t WHERE t.lgUSERID.lgUSERID LIKE ?1  AND  t.strSTATUT LIKE ?2 AND t.dtCREATED >= ?3  AND t.dtCREATED < ?4 ").setParameter(1, OTUser.getLgUSERID()).setParameter(2, commonparameter.statut_is_Using).setParameter(3, dt_Date_debut).setParameter(4, dt_Date_Fin).getSingleResult();
         OdataManager.getEm().refresh(OTResumeCaisse);*/
        TResumeCaisse OTResumeCaisse = OcaisseManagement.getTResumeCaisse(dt_Date_debut, dt_Date_Fin);
        lg_RESUME_CAISSE_ID = OTResumeCaisse.getLdCAISSEID();
        int_SOLDE_MATIN = OTResumeCaisse.getIntSOLDEMATIN();
        str_satut = OTResumeCaisse.getStrSTATUT();
        new logger().OCategory.info("lg_RESUME_CAISSE_ID ++++" + lg_RESUME_CAISSE_ID);
    } catch (Exception e) {
      //  e.printStackTrace();
        lg_RESUME_CAISSE_ID = "";
    }

    JSONArray arrayObj = new JSONArray();
    // OdataManager.getEm().refresh(OTCoffreCaisse);

    JSONObject json = new JSONObject();

    json.put("lg_CAISSE_ID", (OTCaisse != null ? OTCaisse.getLgCAISSEID() : ""));
    json.put("str_NAME_USER", (OTCaisse != null ? OTCaisse.getLgUSERID().getStrFIRSTNAME() + "  " + OTCaisse.getLgUSERID().getStrLASTNAME() : ""));
    json.put("lg_USER_ID", (OTCaisse != null ? OTCaisse.getLgUSERID().getLgUSERID() : ""));
    json.put("str_STATUT", str_satut);
    //lg_RESUME_CAISSE_ID
    json.put("lg_RESUME_CAISSE_ID", lg_RESUME_CAISSE_ID);
    json.put("int_AMOUNT", (OTCaisse != null ? OTCaisse.getIntSOLDE() : 0));
    json.put("int_AMOUNT_FOND_CAISSE", int_SOLDE_MATIN);
    json.put("ld_CREATED_BY", (OTCaisse != null ? OTCaisse.getLgCREATEDBY() : ""));
    json.put("dt_CREATED", date.DateToString(new Date(), date.formatterShort));
    arrayObj.put(json);

 

%>

<%= arrayObj.toString()%>