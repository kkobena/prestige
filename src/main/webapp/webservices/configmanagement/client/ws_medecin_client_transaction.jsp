<%@page import="bll.configManagement.clientManagement"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRolePrivelege"  %>
<%@page import="dal.TPrivilege"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.utils.logger"  %>


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String strLASTNAME = "%%", strFIRSTNAME = "%%", strLOGIN = "%%", lg_CLIENT_ID = "", lg_MEDECIN_ID = "", str_NAME = "";
    date key = new date();
    int int_POURCENTAGE = 5;
%>

<%
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();

            if (request.getParameter("lg_CLIENT_ID") != null) {
                lg_CLIENT_ID = request.getParameter("lg_CLIENT_ID");
                new logger().OCategory.info("lg_CLIENT_ID "+lg_CLIENT_ID);
            }
            
            if (request.getParameter("lg_MEDECIN_ID") != null) {
                lg_MEDECIN_ID = request.getParameter("lg_MEDECIN_ID");
                new logger().OCategory.info("lg_MEDECIN_ID "+lg_MEDECIN_ID);
            }

            bllBase ObllBase = new bllBase();
            ObllBase.setOTUser(OTUser);
            ObllBase.LoadDataManger(OdataManager);
            ObllBase.LoadMultilange(oTranslate);
            //ObllBase.setMessage(commonparameter.PROCESS_SUCCESS);
            clientManagement OclientManagement = new clientManagement(OdataManager);
            if (request.getParameter("mode") != null) {
                if (request.getParameter("mode").toString().equals("create")) {
                    
                    OclientManagement.create_medecin_client(lg_MEDECIN_ID, lg_CLIENT_ID, str_NAME);
                    ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
                    ObllBase.setMessage(OclientManagement.getMessage());
                } else if (request.getParameter("mode").toString().equals("delete")) {
                    OclientManagement.delete_medecin_client(lg_MEDECIN_ID, lg_CLIENT_ID);
                    ObllBase.setDetailmessage(OclientManagement.getDetailmessage());
                    ObllBase.setMessage(OclientManagement.getMessage());
                } else {
                }

            }

            String result;
            if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
                result = "{success:1}";
            } else {
                result = "{success:0, errors: \"" + ObllBase.getDetailmessage() + "\" }";
            }



            new logger().OCategory.info(result);

%>
<%=result%>