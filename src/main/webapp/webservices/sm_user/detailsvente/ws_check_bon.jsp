<%@page import="bll.preenregistrement.DevisManagement"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="dal.TFamille"%>
<%@page import="bll.teller.clientManager"%>
<%@page import="toolkits.utils.StringUtils"%>
<%@page import="bll.differe.DiffereManagement"%>
<%@page import="dal.TMotifReglement"%>
<%@page import="dal.TReglement"%>
<%@page import="dal.TCompteClientTiersPayant"%>
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

<%!    String str_REF_BON = "%%";

    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();

    TUser OTUser = null;
    int int_result = 0;


%>




<%

    if (request.getParameter("str_REF_BON") != null) {
        new logger().OCategory.info("str_REF_BON   " + request.getParameter("str_REF_BON"));
        str_REF_BON = request.getParameter("str_REF_BON");

    }

    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));

    DiffereManagement ODiffereManagement = new DiffereManagement(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {
        if (request.getParameter("mode").toString().equals("check")) {
            new logger().OCategory.info("check");
            int_result = ODiffereManagement.GetBon(str_REF_BON);
        }
    } else {
    }

    String result;

     new logger().OCategory.info("int_result ---- " + int_result);
    new logger().OCategory.info("ObllBase.getMessage() ---- " + ObllBase.getMessage() + "  ObllBase.getDetailmessage()   " + ObllBase.getDetailmessage());
    new logger().OCategory.info("commonparameter.PROCESS_SUCCESS ---- " + commonparameter.PROCESS_SUCCESS);
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {//int_total_vente
        result = "{int_result:\"" + int_result + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    } else {
        result = "{int_result:\"" + int_result + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>