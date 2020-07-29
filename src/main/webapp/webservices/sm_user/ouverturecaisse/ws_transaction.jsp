<%@page import="dal.TMvtCaisse"%>
<%@page import="bll.teller.caisseManagement"%>
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

<%  
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

%>




<%
    String str_ref = "";

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();


    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("ID_COFFRE_CAISSE"));


    if (request.getParameter("mode") != null) {


        //Open caisse
        // new caisseManagement(OdataManager, Oauthentification.getOTUser()).OpenCaisse("2014291634458658420");


        if (request.getParameter("mode").toString().equals("validate")) {
            new logger().oCategory.info("Creation");


            caisseManagement OcaisseManagement = new caisseManagement(OdataManager, OTUser);
            TMvtCaisse OTMvtCaisse = OcaisseManagement.OpenCaisse(request.getParameter("ID_COFFRE_CAISSE"));


            if(OTMvtCaisse != null) {
                str_ref = OTMvtCaisse.getLgMVTCAISSEID();
            }
            ObllBase.setMessage(OcaisseManagement.getMessage());
            ObllBase.setDetailmessage(OcaisseManagement.getDetailmessage());




        }

    }



    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\", ref: \"" + str_ref + "\"}";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\", ref: \"" + str_ref + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>