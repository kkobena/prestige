<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TModule"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<%!    String str_VALUE = "", lg_MODULE_ID = "", str_DESCRIPTION = "", str_TYPE = "";
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    //TModule OTModule = new TModule();
    TRole OTRole = null;
   dal.TModule OTModule=null;

%>




<%
            if (request.getParameter("str_VALUE") != null) {
                str_VALUE = request.getParameter("str_VALUE");
            }
            if (request.getParameter("lg_MODULE_ID") != null) {
                lg_MODULE_ID = request.getParameter("lg_MODULE_ID");
            }
            if (request.getParameter("str_DESCRIPTION") != null) {
                str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
            }
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();


            bllBase ObllBase = new bllBase();
            ObllBase.setOTUser(OTUser);
            ObllBase.LoadDataManger(OdataManager);
            ObllBase.LoadMultilange(oTranslate);
            ObllBase.setMessage(commonparameter.PROCESS_FAILED);
            new logger().oCategory.info("le mode : " + request.getParameter("mode"));
            new logger().oCategory.info("ID " + request.getParameter("lg_MODULE_ID"));


            if (request.getParameter("mode") != null) {




                if (request.getParameter("mode").toString().equals("create")) {
                    new logger().oCategory.info("Creation");



                } else if (request.getParameter("mode").toString().equals("update")) {


                    if (request.getParameter("lg_MODULE_ID").toString().equals("init")) {

                        OTModule = new dal.TModule();
                        OTModule.setLgMODULEID(key.gettimeid());
                        OTModule.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                        OTModule.setStrVALUE(request.getParameter("str_VALUE").toString());

                        ObllBase.persiste(OTModule);
                        new logger().oCategory.info("Creation  TModule " + OTModule.getLgMODULEID() + " StrLabel " + OTModule.getStrVALUE());


                    } else {
                        new logger().oCategory.info("Ref " + request.getParameter("lg_MODULE_ID").toString());
                        OTModule = OdataManager.getEm().find(dal.TModule.class, request.getParameter("lg_MODULE_ID").toString());
                        OTModule.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                        OTModule.setStrVALUE(request.getParameter("str_VALUE").toString());

                        ObllBase.persiste(OTModule);
                        new logger().oCategory.info("Mise a jour TModule " + OTModule.getLgMODULEID() + " StrLabel " + OTModule.getStrVALUE());

                    }
                } else if (request.getParameter("mode").toString().equals("delete")) {

                    OTModule = ObllBase.getOdataManager().getEm().find(dal.TModule.class, request.getParameter("lg_MODULE_ID"));



                       if( !ObllBase.delete(OTModule)){
                           ObllBase.setDetailmessage("Impossible de supprimer");
                       }


                    new logger().oCategory.info("Suppression du module " + request.getParameter("lg_MODULE_ID").toString());


                } else {
                }

            }



            String result;
            if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
                result = "{success:1,lg_MODULE_ID: \"" + OTModule.getLgMODULEID() + "\" }";
            } else {
                result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
            }
new logger().OCategory.info("JSON "+result);
%>
<%=result%>