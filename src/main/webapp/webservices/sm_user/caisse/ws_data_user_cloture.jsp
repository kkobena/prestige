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


<%
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
    TCaisse OTCaisse = null;
    TResumeCaisse OTResumeCaisse = null;
    date key = new date();
%>

<%    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    Date dt_Date_debut = key.stringToDate(key.GetDateNowForSearch(0), key.formatterShort),
            dt_Date_Fin = key.stringToDate(key.GetDateNowForSearch(1), key.formatterShort);
    System.err.println("dt_Date_debut " + dt_Date_debut + " " + dt_Date_Fin);
    double dbl_AMOUNT = 0;
    boolean display = false;

    OdataManager.initEntityManager();
    caisseManagement OcaisseManagement = new caisseManagement(OdataManager, OTUser);

    OTCaisse = OcaisseManagement.GetTCaisse(OTUser.getLgUSERID());
    OTResumeCaisse = OcaisseManagement.getTResumeCaisse(dt_Date_debut, dt_Date_Fin);

    JSONArray arrayObj = new JSONArray();
    JSONObject json = new JSONObject();

    if (OTCaisse != null && OTResumeCaisse != null) {
        json.put("lg_CAISSE_ID", OTCaisse.getLgCAISSEID());
        json.put("lg_RESUME_CAISSE_ID", OTResumeCaisse.getLdCAISSEID());
        json.put("int_AMOUNT", OTCaisse.getIntSOLDE());
        json.put("ld_CREATED_BY", OTCaisse.getLgCREATEDBY());
        json.put("dt_CREATED", date.DateToString(new Date(), date.formatterShort));
        dbl_AMOUNT = OTResumeCaisse.getIntSOLDEMATIN();
        display = true;
    }
    json.put("str_NAME_USER", OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME());
    json.put("lg_USER_ID", OTUser.getLgUSERID());
    json.put("int_AMOUNT_FOND_CAISSE", dbl_AMOUNT);
    json.put("display", display);

    arrayObj.put(json);

    String result = "{results:" + arrayObj.toString() + ", errors_code: \"" + OcaisseManagement.getMessage() + "\", errors: \"" + OcaisseManagement.getDetailmessage() + "\"}";
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>