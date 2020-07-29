<%@page import="bll.Util"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.stockManagement.AjustementManagement"%>
<%@page import="dal.TAjustement"%>
<%@page import="bll.retrocessionManagement.RetrocessionDetailManagement"%>
<%@page import="dal.TAjustementDetail"%>
<%@page import="bll.retrocessionManagement.RetrocessionManagement"%>
<%@page import="dal.TAjustement"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TAjustement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TUser"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();

    TUser OTUser;

    date key = new date();

    List<TAjustement> lstTAjustement = new ArrayList<TAjustement>();
    JSONArray arrayObj = new JSONArray();
%>



<%
    String dt_DEBUT = "", dt_FIN = "", OdateDebut = "", OdateFin = "";
    Date dtFin, dtDEBUT;
    new logger().OCategory.info("dans ws data ajustement");
    String search_value = "", lg_AJUSTEMENT_ID = "%%", lg_USER_ID = "%%";

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
 
  //  boolean BTNDELETE = Oprivilege.isColonneStockMachineIsAuthorize(Parameter.P_BT_DELETE);
  boolean isALLOWED=Util.isAllowed(OdataManager.getEm(), Util.ACTIONDELETEAJUSTEMENT, OTUser.getTRoleUserCollection().stream().findFirst().get().getLgROLEID().getLgROLEID());
    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value").toString();
        new logger().OCategory.info("search_value  " + search_value);
    }

    if (request.getParameter("lg_AJUSTEMENT_ID") != null) {
        lg_AJUSTEMENT_ID = request.getParameter("lg_AJUSTEMENT_ID").toString();
        new logger().OCategory.info("lg_AJUSTEMENT_ID  " + lg_AJUSTEMENT_ID);
    }

    if (request.getParameter("lg_USER_ID") != null) {
        lg_USER_ID = request.getParameter("lg_USER_ID").toString();
        new logger().OCategory.info("lg_USER_ID  " + lg_USER_ID);
    }

    if (request.getParameter("datedebut") != null) {
        new logger().OCategory.info("datedebut " + request.getParameter("datedebut"));
        dt_DEBUT = request.getParameter("datedebut");
    }
    if (request.getParameter("datefin") != null) {
        new logger().OCategory.info("datefin " + request.getParameter("datefin"));
        dt_FIN = request.getParameter("datefin");
    }

    if (dt_DEBUT.equalsIgnoreCase("") || dt_DEBUT == null) {
        dtDEBUT = new Date();
    } else {
        dtDEBUT = key.stringToDate(dt_DEBUT, key.formatterMysqlShort);
    }
    if (dt_FIN.equalsIgnoreCase("") || dt_FIN == null) {
        dtFin = new Date();
    } else {
        dtFin = key.stringToDate(dt_FIN, key.formatterMysqlShort);
    }
    OdateFin = key.DateToString(dtFin, key.formatterMysqlShort2);
    dtFin = key.getDate(OdateFin, "23:59");
    OdateDebut = key.DateToString(dtDEBUT, key.formatterMysqlShort2);
    dtDEBUT = key.getDate(OdateDebut, "00:00");

    AjustementManagement OAjustementManagement = new AjustementManagement(OdataManager, OTUser);

    lstTAjustement = OAjustementManagement.SearchAllOrOneAjustement(lg_AJUSTEMENT_ID, lg_USER_ID, dtDEBUT, dtFin);

    try {

        for (int i = 0; i < lstTAjustement.size(); i++) {

            OdataManager.getEm().refresh(lstTAjustement.get(i));
            List<TAjustementDetail> lstTAjustementDetail = new ArrayList<TAjustementDetail>();
            lstTAjustementDetail = OAjustementManagement.SearchAllOrOneAjustementDetail("", lstTAjustement.get(i).getLgAJUSTEMENTID(), lg_USER_ID, "%%");

            String str_Product = "";
            for (int k = 0; k < lstTAjustementDetail.size(); k++) {
                //  str_Product = "<b>" + lstTAjustementDetail.get(k).getLgFAMILLEID().getStrNAME() + " :  (" + lstTAjustementDetail.get(k).getIntNUMBER() + ")</b><br> " + str_Product;
                str_Product = "<b>" + lstTAjustementDetail.get(k).getLgFAMILLEID().getIntCIP() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + lstTAjustementDetail.get(k).getLgFAMILLEID().getStrNAME() + "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;" + conversion.AmountFormat(lstTAjustementDetail.get(k).getLgFAMILLEID().getIntPRICE(), commonparameter.CHAR_SEPARATEUR_POINT) + " F CFA&nbsp;&nbsp;" + " :  (" + lstTAjustementDetail.get(k).getIntNUMBER() + ")</b><br> " + str_Product;
            }

            JSONObject json = new JSONObject();
            json.put("lg_AJUSTEMENT_ID", lstTAjustement.get(i).getLgAJUSTEMENTID());
            json.put("lg_USER_ID", lstTAjustement.get(i).getLgUSERID().getStrFIRSTNAME() + " " + lstTAjustement.get(i).getLgUSERID().getStrLASTNAME());
            json.put("str_COMMENTAIRE", lstTAjustement.get(i).getStrCOMMENTAIRE());
            json.put("str_NAME", lstTAjustement.get(i).getStrNAME());
            json.put("str_STATUT", lstTAjustement.get(i).getStrSTATUT());

            json.put("dt_CREATED", date.DateToString(lstTAjustement.get(i).getDtCREATED(), key.formatterShort));
            json.put("dt_UPDATED", date.DateToString(lstTAjustement.get(i).getDtCREATED(), key.NomadicUiFormat_Time));
            json.put("str_FAMILLE_ITEM", str_Product);
           json.put("BTNDELETE", isALLOWED);
            arrayObj.put(json);

        }

    } catch (Exception er) {
        new logger().OCategory.info("Failed to display list !");
    }
    String result = "({\"total\":\"" + lstTAjustement.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result " + result);

%>

<%= result%>
