<%@page import="bll.bllBase"%>
<%@page import="bll.differe.DiffereManagement"%>
<%@page import="dal.TFamilleDci"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="dal.TPreenregistrement"%>
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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_FAMILLE_DCI_ID = "%%", lg_FAMILLE_ID = "%%", lg_DCI_ID = "%%";

    date key = new date();

    json Ojson = new json();
    List<TFamilleDci> lstTFamilleDci = new ArrayList<TFamilleDci>();
    DiffereManagement ODiffereManagement = null;
    int int_size = 0;

%>



<%

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    OTUser = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());

    ODiffereManagement = new DiffereManagement(OdataManager, OTUser);

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    try {
        OdataManager.getEm().refresh(OTUser);
    } catch (Exception er) {
    }

   // new logger().OCategory.info("dans ws data famille dci ");

    if (request.getParameter("search_value") != null) {
        Os_Search_poste.setOvalue(request.getParameter("search_value"));
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("");
    }
   // new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());

   // new logger().OCategory.info("search_value  = " + request.getParameter("search_value"));

    OdataManager.initEntityManager();

    String search_value = Os_Search_poste.getOvalue();
   // new logger().OCategory.info("search_value    " + search_value);

    lstTFamilleDci = ODiffereManagement.Func_GetAllFamille_By_DciJdbc("%"+search_value+"%");
   
    JSONArray arrayObj = new JSONArray();
    if ((lstTFamilleDci == null) || lstTFamilleDci.isEmpty()) {

        int_size = 0;
    } else {
       //  new logger().OCategory.info("lstTFamilleDci size " + lstTFamilleDci.size());

        for (int i = 0; i < lstTFamilleDci.size(); i++) {

            try {
                OdataManager.getEm().refresh(lstTFamilleDci.get(i));
            } catch (Exception er) {
            }

            JSONObject json = new JSONObject();
            json.put("lg_FAMILLE_DCI_ID", lstTFamilleDci.get(i).getLgFAMILLEDCIID());
            json.put("lg_FAMILLE_ID", lstTFamilleDci.get(i).getLgFAMILLEID().getLgFAMILLEID());
            json.put("lg_DCI_ID", lstTFamilleDci.get(i).getLgDCIID().getLgDCIID());
            json.put("str_NAME", lstTFamilleDci.get(i).getLgFAMILLEID().getStrNAME());
            json.put("str_DESCRIPTION", lstTFamilleDci.get(i).getLgFAMILLEID().getStrDESCRIPTION());
            json.put("int_PRICE", lstTFamilleDci.get(i).getLgFAMILLEID().getIntPRICE());
            json.put("int_CIP", lstTFamilleDci.get(i).getLgFAMILLEID().getIntCIP());
            arrayObj.put(json);
        }
        int_size = lstTFamilleDci.size();
    }
    String result = "({\"total\":\"" + int_size + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);


%>

<%= result%>
