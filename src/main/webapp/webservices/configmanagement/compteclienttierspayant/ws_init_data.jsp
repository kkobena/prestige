<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="dal.TCompteClientTiersPayant"%>
<%@page import="dal.TCompteClient"%>
<%@page import="dal.TTypeReglement"%>
<%@page import="dal.TCashTransaction"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
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

<%!     String lg_COMPTE_CLIENT_ID = "%%", str_CODE_COMPTE_CLIENT = "%%", lg_CLIENT_ID = "%%";

    double dbl_QUOTA_CONSO_MENSUELLE = 0.00, dbl_SOLDE = 0.00;

    Date dt_CREATED, dt_UPDATED;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    //privilege Oprivilege = new privilege();
    TUser OTUser = null;

    TCompteClient OTCompteClient = null;

    List<dal.TCompteClientTiersPayant> lstTCompteClientTiersPayant = new ArrayList<dal.TCompteClientTiersPayant>();
    TCompteClientTiersPayant OTCompteClientTiersPayant = null;

%>




<%

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    //new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("lg_CLIENT_ID @" + request.getParameter("lg_CLIENT_ID") + "@");
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);

  //  if (request.getParameter("mode") != null) {
    // if (request.getParameter("mode").toString().equals("init")) {
    new logger().OCategory.info("ds ws data init client");

    OTCompteClient = (TCompteClient) OdataManager.getEm().createQuery("SELECT t FROM TCompteClient t WHERE t.lgCLIENTID.lgCLIENTID LIKE ?1  AND t.strSTATUT LIKE ?3 ").
            setParameter(1, lg_CLIENT_ID).setParameter(3, commonparameter.statut_enable).getSingleResult();

    lstTCompteClientTiersPayant = OdataManager.getEm().createQuery("SELECT t FROM TCompteClientTiersPayant t WHERE t.lgCOMPTECLIENTID.lgCOMPTECLIENTID LIKE ?1  AND t.lgCOMPTECLIENTID.strSTATUT LIKE ?3 ").
            setParameter(1, OTCompteClient.getLgCOMPTECLIENTID())
            .setParameter(3, commonparameter.statut_enable).getResultList();

    ObllBase.setMessage(commonparameter.PROCESS_SUCCESS);
    JSONArray arrayObj = new JSONArray();

    for (int i = 0; i < lstTCompteClientTiersPayant.size(); i++) {
        OTCompteClientTiersPayant = lstTCompteClientTiersPayant.get(i);
        ObllBase.refresh(OTCompteClientTiersPayant);

        JSONObject json = new JSONObject();

        json.put("str_NAME", OTCompteClientTiersPayant.getLgTIERSPAYANTID().getStrNAME());
         json.put("lg_TIERS_PAYANT_ID", OTCompteClientTiersPayant.getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + lstTCompteClientTiersPayant.size() + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

      //  }
  //  } else {
    // }

%>
<%=result%>