<%@page import="dal.TTiersPayant"%>
<%@page import="dal.TCompteClient"%>
<%@page import="dal.TCompteClientTiersPayant"%>
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


<%!    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String strLASTNAME = "%%", strFIRSTNAME = "%%", strLOGIN = "%%", lg_COMPTE_CLIENT_ID = "", lg_USER_ID = "";
    date key = new date();
    privilege Oprivilege = new privilege();
%>

<%
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();
  TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
            if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
                lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
            }

            bllBase ObllBase = new bllBase();
            ObllBase.setOTUser(user);
            ObllBase.LoadDataManger(OdataManager);
            ObllBase.LoadMultilange(oTranslate);
            //ObllBase.setMessage(commonparameter.PROCESS_SUCCESS);

            if (request.getParameter("mode") != null) {
                if (request.getParameter("mode").toString().equals("create")) {
                    new logger().oCategory.info("Creation");
                    TCompteClientTiersPayant OTCompteClientTiersPayant = new TCompteClientTiersPayant();
                    OTCompteClientTiersPayant.setLgCOMPTECLIENTTIERSPAYANTID(key.gettimeid());
                    TCompteClient OTCompteClient = ObllBase.getOdataManager().getEm().find(TCompteClient.class, lg_COMPTE_CLIENT_ID);
                    TTiersPayant OTTiersPayant = ObllBase.getOdataManager().getEm().find(TTiersPayant.class, request.getParameter("lg_TIERS_PAYANT_ID").toString());
                    OTCompteClientTiersPayant.setLgCOMPTECLIENTID(OTCompteClient);
                    OTCompteClientTiersPayant.setLgTIERSPAYANTID(OTTiersPayant);

                    ObllBase.persiste(OTCompteClientTiersPayant);


                } else if (request.getParameter("mode").toString().equals("update")) {
                } else if (request.getParameter("mode").toString().equals("delete")) {
                    new logger().oCategory.info("delete");
                    new logger().OCategory.info("lg_COMPTE_CLIENT_ID  : " + request.getParameter("lg_COMPTE_CLIENT_ID").toString());
                    new logger().OCategory.info("lg_TIERS_PAYANT_ID  : " + request.getParameter("lg_TIERS_PAYANT_ID").toString());

                    TCompteClientTiersPayant OTCompteClientTiersPayant = null;
                    try {
                        OTCompteClientTiersPayant = (TCompteClientTiersPayant) ObllBase.getOdataManager().getEm().createQuery("SELECT t FROM TCompteClientTiersPayant t WHERE t.lgTIERSPAYANTID.lgTIERSPAYANTID LIKE ?1 AND t.lgCOMPTECLIENTID.lgCOMPTECLIENTID LIKE ?2 ")
                                .setParameter(1, request.getParameter("lg_TIERS_PAYANT_ID").toString()).setParameter(2, request.getParameter("lg_COMPTE_CLIENT_ID").toString()).getSingleResult();

                        ObllBase.delete(OTCompteClientTiersPayant);
                    } catch (Exception e) {

                        ObllBase.buildErrorTraceMessage(e.getMessage());
                    }
                    //new logger().OCategory.info("lg_PRIVILEGE_ID  : " + request.getParameter("lg_PRIVILEGE_ID").toString());

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