<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TLanguage"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<%!  String lg_Language_ID="%%", str_Local_Cty = "%%", str_Local_Lg = "%%", str_Code = "", str_Description = "%%";
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    TLanguage OTLanguage = null;


%>




<%
            if (request.getParameter("lg_Language_ID") != null) {
                lg_Language_ID = request.getParameter("lg_Language_ID");
            }
            if (request.getParameter("str_Local_Cty") != null) {
                str_Local_Cty = request.getParameter("str_Local_Cty");
            }
            if (request.getParameter("str_Local_Lg") != null) {
                str_Local_Lg = request.getParameter("str_Local_Lg");
            }
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();


            bllBase ObllBase = new bllBase();
            ObllBase.setOTUser(OTUser);
            ObllBase.LoadDataManger(OdataManager);
            ObllBase.LoadMultilange(oTranslate);
            ObllBase.setMessage(commonparameter.PROCESS_FAILED);
            new logger().oCategory.info("le mode : " + request.getParameter("mode"));
            new logger().oCategory.info("ID " + request.getParameter("lg_Language_ID"));


            if (request.getParameter("mode") != null) {




                if (request.getParameter("mode").toString().equals("create")) {
                    new logger().oCategory.info("Creation");



                } else if (request.getParameter("mode").toString().equals("update")) {


                    if (request.getParameter("lg_Language_ID").toString().equals("init")) {

                        OTLanguage = new dal.TLanguage();
                        OTLanguage.setLgLanguageID(key.getComplexId());
                        OTLanguage.setStrLocalCty(request.getParameter("str_Local_Cty").toString());
                        OTLanguage.setStrLocalLg(request.getParameter("str_Local_Lg").toString());
                        OTLanguage.setStrCode(request.getParameter("str_Code").toString());
                        OTLanguage.setStrDescription(request.getParameter("str_Description").toString());


                        ObllBase.persiste(OTLanguage);
                        new logger().oCategory.info("Creation  TModule " + OTLanguage.getLgLanguageID() + " StrLabel " + OTLanguage.getStrDescription());


                    } else {
                        new logger().oCategory.info("Ref " + request.getParameter("lg_Language_ID").toString());
                        OTLanguage = ObllBase.getOdataManager().getEm().find(dal.TLanguage.class, request.getParameter("lg_Language_ID").toString());
                         OTLanguage.setStrLocalCty(request.getParameter("str_Local_Cty").toString());
                        OTLanguage.setStrLocalLg(request.getParameter("str_Local_Lg").toString());
                        OTLanguage.setStrCode(request.getParameter("str_Code").toString());
                        OTLanguage.setStrDescription(request.getParameter("str_Description").toString());



                        ObllBase.persiste(OTLanguage);
                       new logger().oCategory.info("Creation  TModule " + OTLanguage.getLgLanguageID() + " StrLabel " + OTLanguage.getStrDescription());

                    }
                } else if (request.getParameter("mode").toString().equals("delete")) {

                    OTLanguage = ObllBase.getOdataManager().getEm().find(dal.TLanguage.class, request.getParameter("lg_Language_ID").toString());



                    if (!ObllBase.delete(OTLanguage)) {
                        ObllBase.setDetailmessage("Impossible de supprimer");
                    }


                    new logger().oCategory.info("Suppression de institution " + request.getParameter("lg_Language_ID").toString());


                } else {
                }

            }



            String result;
            if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
                result = "{success:1,lg_Language_ID: \"" + OTLanguage.getLgLanguageID() + "\" }";
            } else {
                result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
            }
            new logger().OCategory.info("JSON " + result);
%>
<%=result%>