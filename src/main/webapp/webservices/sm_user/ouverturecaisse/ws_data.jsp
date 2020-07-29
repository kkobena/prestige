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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String ID_COFFRE_CAISSE = "%%", lg_USER_ID = "%%", str_STATUT = "%%", lg_Participant_ID = "%%", ld_CREATED_BY = "%%", ld_UPDATED_BY = "%%";
    Integer int_AMOUNT;
    String Date_debut = "", Date_Fin = "";
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date, dt_CREATED;
    json Ojson = new json();
    Date Dt_Date_encaissement = new Date();
    Date dt_Date_debut, dt_Date_Fin;
    List<dal.TCoffreCaisse> lstTCoffreCaisse = new ArrayList<dal.TCoffreCaisse>();

%>

<%
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            Date_debut = key.GetDateNowForSearch(0);
            Date_Fin = key.GetDateNowForSearch(1);
            new logger().OCategory.info(Date_debut + "  ouverture caisse   " + Date_Fin);

            dt_Date_Fin = key.stringToDate(Date_Fin, key.formatterShort);
            dt_Date_debut = key.stringToDate(Date_debut, key.formatterShort);

            OdataManager.initEntityManager();

            dal.TCoffreCaisse OTCoffreCaisse = new dal.TCoffreCaisse();
//
            try {
                OTCoffreCaisse = (dal.TCoffreCaisse) OdataManager.getEm().createQuery("SELECT t FROM TCoffreCaisse t WHERE t.lgUSERID.lgUSERID LIKE ?1 AND  t.strSTATUT LIKE ?2  AND t.dtCREATED >= ?3  AND t.dtCREATED < ?4 ").
                        setParameter(1, OTUser.getLgUSERID()).
                        setParameter(2, commonparameter.statut_is_Waiting_validation).setParameter(3, dt_Date_debut).setParameter(4, dt_Date_Fin).getSingleResult();
                ID_COFFRE_CAISSE = OTCoffreCaisse.getIdCoffreCaisse();
            } catch (Exception e) {
                e.printStackTrace();
                ID_COFFRE_CAISSE = "";
            }



            JSONArray arrayObj = new JSONArray();
            // OdataManager.getEm().refresh(OTCoffreCaisse);


            JSONObject json = new JSONObject();

            json.put("ID_COFFRE_CAISSE", OTCoffreCaisse.getIdCoffreCaisse());
            json.put("str_NAME_USER", OTCoffreCaisse.getLgUSERID().getStrFIRSTNAME() + "  " + OTCoffreCaisse.getLgUSERID().getStrLASTNAME());
            json.put("lg_USER_ID", OTCoffreCaisse.getLgUSERID().getLgUSERID());
            json.put("str_STATUT", oTranslate.getValue(OTCoffreCaisse.getStrSTATUT()));
            json.put("int_AMOUNT", (OTCoffreCaisse.getIntAMOUNT()));
            try {
                TUser OTUser1 = OdataManager.getEm().find(TUser.class, OTCoffreCaisse.getLdCREATEDBY());
                json.put("ld_CREATED_BY", OTUser1.getStrFIRSTNAME() + " " + OTUser1.getStrLASTNAME());
            } catch(Exception e) {
                
            }
            
            json.put("dt_CREATED", date.DateToString(OTCoffreCaisse.getDtCREATED(), date.formatterMysql));
            arrayObj.put(json);

            System.out.println(arrayObj.toString());

%>

<%= arrayObj.toString()%>