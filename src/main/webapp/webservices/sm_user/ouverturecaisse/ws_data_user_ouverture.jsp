<%@page import="bll.teller.caisseManagement"%>
<%@page import="dal.TCoffreCaisse"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    TUser OTUser = null, OTUser1 = null;
    date key = new date();
    TCoffreCaisse OTCoffreCaisse = null;
%>

<%    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    Date dt_Date_Fin = key.stringToDate(key.GetDateNowForSearch(1), key.formatterShort),
            dt_Date_debut = key.stringToDate(key.GetDateNowForSearch(0), key.formatterShort);
    double dbl_AMOUNT = 0;
    boolean display = false;

    OdataManager.initEntityManager();
    caisseManagement OcaisseManagement = new caisseManagement(OdataManager, OTUser);
    OTCoffreCaisse = OcaisseManagement.getTCoffreCaisseOfSomeDay(OTUser.getLgUSERID(), commonparameter.statut_is_Waiting_validation, dt_Date_debut, dt_Date_Fin);

    JSONArray arrayObj = new JSONArray();
    JSONObject json = new JSONObject();

    if (OTCoffreCaisse != null) {
        dbl_AMOUNT = OTCoffreCaisse.getIntAMOUNT();
        json.put("ID_COFFRE_CAISSE", OTCoffreCaisse.getIdCoffreCaisse());
        json.put("str_STATUT", oTranslate.getValue(OTCoffreCaisse.getStrSTATUT()));
        OTUser1 = OdataManager.getEm().find(TUser.class, OTCoffreCaisse.getLdCREATEDBY());
        json.put("ld_CREATED_BY", OTUser1 != null ? OTUser1.getStrFIRSTNAME() + " " + OTUser1.getStrLASTNAME() : "");
        json.put("dt_CREATED", date.DateToString(OTCoffreCaisse.getDtCREATED(), date.formatterShort));
        display = true;
    } 
    json.put("str_NAME_USER", OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME());
    json.put("lg_USER_ID", OTUser.getLgUSERID());
    json.put("int_AMOUNT", dbl_AMOUNT);
    json.put("display", display);
    
    arrayObj.put(json);

 String result = "{results:" + arrayObj.toString() + ", errors_code: \"" + OcaisseManagement.getMessage() + "\", errors: \"" + OcaisseManagement.getDetailmessage() + "\"}";
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>