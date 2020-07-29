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

<%!   String lg_TYPE_DEPENSE_ID = "%%", str_TYPE_DEPENSE = "%%", str_NUMERO_COMPTE = "%%", lg_Participant_ID = "%%";
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TTypeDepense OTTypeDepense = null;

%>




<%
            if (request.getParameter("str_TYPE_DEPENSE") != null) {
                str_TYPE_DEPENSE = request.getParameter("str_TYPE_DEPENSE");
            }
            if (request.getParameter("lg_TYPE_DEPENSE_ID") != null) {
                lg_TYPE_DEPENSE_ID = request.getParameter("lg_TYPE_DEPENSE_ID");
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
            new logger().oCategory.info("ID " + request.getParameter("lg_TYPE_DEPENSE_ID"));


            if (request.getParameter("mode") != null) {




                if (request.getParameter("mode").toString().equals("create")) {
                    new logger().oCategory.info("Creation");



                } else if (request.getParameter("mode").toString().equals("update")) {


                    if (request.getParameter("lg_TYPE_DEPENSE_ID").toString().equals("init")) {

                        OTTypeDepense = new dal.TTypeDepense();
                        OTTypeDepense.setLgTYPEDEPENSEID(key.getComplexId());
                        OTTypeDepense.setStrNUMEROCOMPTE(request.getParameter("str_NUMERO_COMPTE").toString());
                        OTTypeDepense.setStrTYPEDEPENSE(request.getParameter("str_TYPE_DEPENSE").toString());

                        ObllBase.persiste(OTTypeDepense);
                        // new logger().oCategory.info("Creation  TPrivilege " + OTprivilege.getLgPRIVELEGEID() + " StrLabel " + OTprivilege.getStrNAME());


                    } else {
                        // new logger().oCategory.info("Ref " + request.getParameter("lg_PRIVELEGE_ID").toString());
                        OTTypeDepense = OdataManager.getEm().find(dal.TTypeDepense.class, request.getParameter("lg_TYPE_DEPENSE_ID").toString());
                        OTTypeDepense.setStrNUMEROCOMPTE(request.getParameter("str_NUMERO_COMPTE").toString());
                        OTTypeDepense.setStrTYPEDEPENSE(request.getParameter("str_TYPE_DEPENSE").toString());

                        ObllBase.persiste(OTTypeDepense);
                        // new logger().oCategory.info("Mise a jour TPrivilege " + OTprivilege.getLgPRIVELEGEID() + " StrLabel " + OTprivilege.getStrNAME());

                    }
                } else if (request.getParameter("mode").toString().equals("delete")) {

                    OTTypeDepense = OdataManager.getEm().find(dal.TTypeDepense.class, request.getParameter("lg_TYPE_DEPENSE_ID").toString());

                    if (!ObllBase.delete(OTTypeDepense)) {
                        ObllBase.setDetailmessage("Impossible de supprimer");
                    }


                    new logger().oCategory.info("Suppression de table " + request.getParameter("lg_TYPE_DEPENSE_ID").toString());


                    // new logger().oCategory.info("Suppression du privilege " + request.getParameter("lg_PRIVELEGE_ID").toString());


                } else {
                }

            }



            String result;
            if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
                result = "{success:1,lg_TYPE_DEPENSE_ID: \"" + OTTypeDepense.getLgTYPEDEPENSEID() + "\" }";
            } else {
                result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
            }
            new logger().OCategory.info("JSON " + result);
%>
<%=result%>