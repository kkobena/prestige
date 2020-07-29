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

<%!   String lg_TYPE_RECETTE_ID = "%%", str_TYPE_RECETTE = "%%", str_NUMERO_COMPTE = "%%", lg_Participant_ID = "%%";
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TTypeRecette OTTypeRecette = null;

%>




<%
            if (request.getParameter("str_TYPE_RECETTE") != null) {
                str_TYPE_RECETTE = request.getParameter("str_TYPE_RECETTE");
            }
            if (request.getParameter("lg_TYPE_RECETTE_ID") != null) {
                lg_TYPE_RECETTE_ID = request.getParameter("lg_TYPE_RECETTE_ID");
            }
            if (request.getParameter("str_NUMERO_COMPTE") != null) {
                str_NUMERO_COMPTE = request.getParameter("str_NUMERO_COMPTE");
            }
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();


            bllBase ObllBase = new bllBase();
            ObllBase.setOTUser(OTUser);
            ObllBase.LoadDataManger(OdataManager);
            ObllBase.LoadMultilange(oTranslate);
            ObllBase.setMessage(commonparameter.PROCESS_FAILED);
            new logger().oCategory.info("le mode : " + request.getParameter("mode"));
            new logger().oCategory.info("ID " + request.getParameter("lg_TYPE_RECETTE_ID"));


            if (request.getParameter("mode") != null) {




                if (request.getParameter("mode").toString().equals("create")) {
                    new logger().oCategory.info("Creation");



                } else if (request.getParameter("mode").toString().equals("update")) {


                    if (request.getParameter("lg_TYPE_RECETTE_ID").toString().equals("init")) {

                        OTTypeRecette = new dal.TTypeRecette();
                        OTTypeRecette.setLgTYPERECETTEID(key.getComplexId());
                        OTTypeRecette.setStrNUMEROCOMPTE(request.getParameter("str_NUMERO_COMPTE").toString());
                        OTTypeRecette.setStrTYPERECETTE(request.getParameter("str_TYPE_RECETTE").toString());

                        ObllBase.persiste(OTTypeRecette);
                        // new logger().oCategory.info("Creation  TPrivilege " + OTprivilege.getLgPRIVELEGEID() + " StrLabel " + OTprivilege.getStrNAME());


                    } else {
                        // new logger().oCategory.info("Ref " + request.getParameter("lg_PRIVELEGE_ID").toString());
                        OTTypeRecette = OdataManager.getEm().find(dal.TTypeRecette.class, request.getParameter("lg_TYPE_RECETTE_ID").toString());
                        OTTypeRecette.setStrNUMEROCOMPTE(request.getParameter("str_NUMERO_COMPTE").toString());
                        OTTypeRecette.setStrTYPERECETTE(request.getParameter("str_TYPE_RECETTE").toString());

                        ObllBase.persiste(OTTypeRecette);
                        // new logger().oCategory.info("Mise a jour TPrivilege " + OTprivilege.getLgPRIVELEGEID() + " StrLabel " + OTprivilege.getStrNAME());

                    }
                } else if (request.getParameter("mode").toString().equals("delete")) {

                    OTTypeRecette = OdataManager.getEm().find(dal.TTypeRecette.class, request.getParameter("lg_TYPE_RECETTE_ID").toString());

                    if (!ObllBase.delete(OTTypeRecette)) {
                        ObllBase.setDetailmessage("Impossible de supprimer");
                    }


                    new logger().oCategory.info("Suppression de table " + request.getParameter("lg_TYPE_RECETTE_ID").toString());


                    // new logger().oCategory.info("Suppression du privilege " + request.getParameter("lg_PRIVELEGE_ID").toString());


                } else {
                }

            }



            String result;
            if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
                result = "{success:1,lg_TYPE_RECETTE_ID: \"" + OTTypeRecette.getLgTYPERECETTEID() + "\" }";
            } else {
                result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
            }
            new logger().OCategory.info("JSON " + result);
%>
<%=result%>