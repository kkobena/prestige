<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TSkin"  %>
<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<%!  String lg_SKIN_ID = "%%", str_RESOURCE = "%%", str_STATUT = "%%", str_DESCRIPTION = "", str_DETAIL_PATH = "%%";
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    TSkin OTSkin = null;


%>




<%
            if (request.getParameter("lg_SKIN_ID") != null) {
                lg_SKIN_ID = request.getParameter("lg_SKIN_ID");
            }
            if (request.getParameter("str_STATUT") != null) {
                str_STATUT = request.getParameter("str_STATUT");
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
            new logger().oCategory.info("ID " + request.getParameter("lg_INSTITUTION_ID"));


            if (request.getParameter("mode") != null) {




                if (request.getParameter("mode").toString().equals("create")) {
                    new logger().oCategory.info("Creation");



                } else if (request.getParameter("mode").toString().equals("update")) {


                    if (request.getParameter("lg_SKIN_ID").toString().equals("init")) {

                        OTSkin = new dal.TSkin();
                        OTSkin.setLgSKINID(key.getComplexId());
                        OTSkin.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                        OTSkin.setStrSTATUT(request.getParameter("str_STATUT").toString());
                        OTSkin.setStrDETAILPATH(request.getParameter("str_DETAIL_PATH").toString());
                        OTSkin.setStrRESOURCE(request.getParameter("str_RESOURCE").toString());


                        ObllBase.persiste(OTSkin);
                        new logger().oCategory.info("Creation  TModule " + OTSkin.getLgSKINID() + " StrLabel " + OTSkin.getStrDESCRIPTION());


                    } else {
                        new logger().oCategory.info("Ref " + request.getParameter("lg_SKIN_ID").toString());
                        OTSkin = OdataManager.getEm().find(dal.TSkin.class, request.getParameter("lg_SKIN_ID").toString());
                        OTSkin.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                        OTSkin.setStrSTATUT(request.getParameter("str_STATUT").toString());
                        OTSkin.setStrDETAILPATH(request.getParameter("str_DETAIL_PATH").toString());
                        OTSkin.setStrRESOURCE(request.getParameter("str_RESOURCE").toString());

                        ObllBase.persiste(OTSkin);
                       new logger().oCategory.info("Creation  TModule " + OTSkin.getLgSKINID() + " StrLabel " + OTSkin.getStrDESCRIPTION());

                    }
                } else if (request.getParameter("mode").toString().equals("delete")) {

                    OTSkin = ObllBase.getOdataManager().getEm().find(dal.TSkin.class, request.getParameter("lg_SKIN_ID"));



                    if (!ObllBase.delete(OTSkin)) {
                        ObllBase.setDetailmessage("Impossible de supprimer");
                    }


                 //   new logger().oCategory.info("Suppression de institution " + request.getParameter("lg_INSTITUTION_ID").toString());


                } else {
                }

            }



            String result;
            if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
                result = "{success:1,lg_SKIN_ID: \"" + OTSkin.getLgSKINID() + "\" }";
            } else {
                result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
            }
            new logger().OCategory.info("JSON " + result);
%>
<%=result%>