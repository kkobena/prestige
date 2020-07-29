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

<%!    String str_VALUE = "", lg_MENU_ID = "", str_DESCRIPTION = "", str_TYPE = "", str_Status = "", P_KEY = "";
    Integer int_PRIORITY;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();   
    TRole OTRole = null;
    dal.TMenu OTMenu = null;

%>




<%
    if (request.getParameter("str_VALUE") != null) {
        str_VALUE = request.getParameter("str_VALUE");
    }
    if (request.getParameter("lg_MENU_ID") != null) {
        lg_MENU_ID = request.getParameter("lg_MENU_ID");
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

    ObllBase.setDetailmessage("PAS D'ACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_MENU_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");

            dal.TModule OTModule = ObllBase.getOdataManager().getEm().find(dal.TModule.class, request.getParameter("lg_MODULE_ID").toString());

            OTMenu = new dal.TMenu();
            OTMenu.setLgMENUID(key.getComplexId());
            OTMenu.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
            OTMenu.setStrVALUE(request.getParameter("str_VALUE").toString());
            OTMenu.setPKey(request.getParameter("P_KEY").toString());
            OTMenu.setIntPRIORITY(Integer.parseInt(request.getParameter("int_PRIORITY")));
            OTMenu.setStrStatus(commonparameter.statut_enable);
            OTMenu.setLgMODULEID(OTModule);

            ObllBase.persiste(OTMenu);
            new logger().oCategory.info("Creation  TModule " + OTMenu.getLgMENUID() + " StrLabel " + OTMenu.getStrVALUE());

        } else if (request.getParameter("mode").toString().equals("update")) {

            if (request.getParameter("lg_MENU_ID").toString().equals("init")) {

                dal.TModule OTModule = ObllBase.getOdataManager().getEm().find(dal.TModule.class, request.getParameter("lg_MODULE_ID").toString());

                OTMenu = new dal.TMenu();
                OTMenu.setLgMENUID(key.getComplexId());
                OTMenu.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                OTMenu.setStrVALUE(request.getParameter("str_VALUE").toString());
                OTMenu.setPKey(request.getParameter("P_KEY").toString());
                OTMenu.setStrStatus(commonparameter.statut_enable);
                OTMenu.setIntPRIORITY(Integer.parseInt(request.getParameter("int_PRIORITY")));
                OTMenu.setLgMODULEID(OTModule);

                ObllBase.persiste(OTMenu);
                new logger().oCategory.info("Creation  Tmenu " + OTMenu.getLgMENUID() + " StrLabel " + OTMenu.getStrVALUE());

            } else {
                new logger().oCategory.info("Ref " + request.getParameter("lg_MENU_ID").toString());
                new logger().oCategory.info("str_DESCRIPTION " + request.getParameter("str_DESCRIPTION").toString());

                OTMenu = ObllBase.getOdataManager().getEm().find(dal.TMenu.class, request.getParameter("lg_MENU_ID"));

                try {
                    dal.TModule OTModule = ObllBase.getOdataManager().getEm().find(dal.TModule.class, request.getParameter("lg_MODULE_ID").toString());
                    if (OTModule != null) {
                        OTMenu.setLgMODULEID(OTModule);
                    }

                } catch (Exception e) {

                    new logger().OCategory.info("mauvais schema  : " + request.getParameter("lg_SKIN_ID"));
                    //  new logger().OCategory.info("mauvais schema  : " + request.getParameter("lg_DECOUPE_ID"));
                }

                OTMenu = ObllBase.getOdataManager().getEm().find(dal.TMenu.class, request.getParameter("lg_MENU_ID").toString());
                OTMenu.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                OTMenu.setStrVALUE(request.getParameter("str_VALUE").toString());
                OTMenu.setPKey(request.getParameter("P_KEY").toString());
                OTMenu.setStrStatus(commonparameter.statut_enable);
                OTMenu.setIntPRIORITY(Integer.parseInt(request.getParameter("int_PRIORITY")));

                ObllBase.persiste(OTMenu);
                new logger().oCategory.info("Mise a jour TMenu " + OTMenu.getLgMENUID() + " StrLabel " + OTMenu.getStrVALUE());

            }
        } else if (request.getParameter("mode").toString().equals("delete")) {

            OTMenu = ObllBase.getOdataManager().getEm().find(dal.TMenu.class, request.getParameter("lg_MENU_ID"));

            OTMenu.setStrStatus(commonparameter.statut_delete);
            ObllBase.persiste(OTMenu);
            /* if (!ObllBase.delete(OTMenu)) {
             ObllBase.setDetailmessage("Impossible de supprimer");
             }*/

            new logger().oCategory.info("Suppression du menu " + request.getParameter("lg_MENU_ID").toString());

        } else {
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:1,lg_MENU_ID: \"" + OTMenu.getLgMENUID() + "\" }";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>