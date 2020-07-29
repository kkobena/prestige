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

<%!    String str_VALUE = "", lg_MENU_ID = "", str_DESCRIPTION = "", str_TYPE = "", str_Status = "", P_Key = "", lg_SOUS_MENU_ID = "";
    Integer int_PRIORITY;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TSousMenu OTSousMenu = null;

%>




<%
    if (request.getParameter("str_VALUE") != null) {
        str_VALUE = request.getParameter("str_VALUE");
    }
    if (request.getParameter("lg_SOUS_MENU_ID") != null) {
        lg_MENU_ID = request.getParameter("lg_SOUS_MENU_ID");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_SOUS_MENU_ID"));
    
    if (request.getParameter("mode") != null) {
        
        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");
            
            dal.TMenu OTMenu = ObllBase.getOdataManager().getEm().find(dal.TMenu.class, request.getParameter("lg_MENU_ID").toString());
            
            OTSousMenu = new dal.TSousMenu();
            OTSousMenu.setLgSOUSMENUID(key.getComplexId());
            OTSousMenu.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
            OTSousMenu.setStrVALUE(request.getParameter("str_VALUE").toString());
            OTSousMenu.setStrStatus(commonparameter.statut_enable);
            OTSousMenu.setStrCOMPOSANT(request.getParameter("str_COMPOSANT").toString());
            OTSousMenu.setPKey(request.getParameter("P_Key").toString());
            OTSousMenu.setIntPRIORITY(Integer.parseInt(request.getParameter("int_PRIORITY")));
            
            OTSousMenu.setStrStatus(commonparameter.statut_enable);
            OTSousMenu.setLgMENUID(OTMenu);
            
            ObllBase.persiste(OTSousMenu);
            new logger().oCategory.info("Creation  TSousmenu " + OTSousMenu.getLgSOUSMENUID() + " StrLabel " + OTSousMenu.getStrVALUE());
            
        } else if (request.getParameter("mode").toString().equals("update")) {
            
            if (request.getParameter("lg_SOUS_MENU_ID").toString().equals("init")) {
                
                dal.TMenu OTMenu = ObllBase.getOdataManager().getEm().find(dal.TMenu.class, request.getParameter("lg_MENU_ID").toString());
                
                OTSousMenu = new dal.TSousMenu();
                //OTSousMenu = new dal.TSousMenu();
                OTSousMenu.setLgSOUSMENUID(key.getComplexId());
                OTSousMenu.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                OTSousMenu.setStrVALUE(request.getParameter("str_VALUE").toString());
                OTSousMenu.setStrCOMPOSANT(request.getParameter("str_COMPOSANT").toString());
                OTSousMenu.setPKey(request.getParameter("P_Key").toString());
                OTSousMenu.setStrStatus(commonparameter.statut_enable);
                OTSousMenu.setIntPRIORITY(Integer.parseInt(request.getParameter("int_PRIORITY")));
                OTSousMenu.setLgMENUID(OTMenu);
                
                ObllBase.persiste(OTSousMenu);
                new logger().oCategory.info("Creation  TModule " + OTSousMenu.getLgSOUSMENUID() + " StrLabel " + OTSousMenu.getStrVALUE());
                
            } else {
                new logger().oCategory.info("Ref " + request.getParameter("lg_SOUS_MENU_ID").toString());
                new logger().oCategory.info("str_DESCRIPTION " + request.getParameter("str_DESCRIPTION").toString());
                
                new logger().oCategory.info("P_Key " + request.getParameter("P_Key").toString());
                
                OTSousMenu = ObllBase.getOdataManager().getEm().find(dal.TSousMenu.class, request.getParameter("lg_SOUS_MENU_ID").toString());
                
                try {
                    dal.TMenu OTMenu = ObllBase.getOdataManager().getEm().find(dal.TMenu.class, request.getParameter("lg_MENU_ID").toString());
                    //dal.TDecoupe OTDecoupe = ObllBase.getOdataManager().getEm().find(dal.TDecoupe.class, request.getParameter("lg_DECOUPE_ID").toString());

                    if (OTMenu != null) {
                        OTSousMenu.setLgMENUID(OTMenu);
                    }
                    
                } catch (Exception e) {
                    
                    new logger().OCategory.info("mauvais schema  : " + request.getParameter("lg_MENU_ID"));
                    //  new logger().OCategory.info("mauvais schema  : " + request.getParameter("lg_DECOUPE_ID"));
                }
                
                OTSousMenu.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                OTSousMenu.setStrVALUE(request.getParameter("str_VALUE").toString());
                OTSousMenu.setPKey(request.getParameter("P_Key").toString());
                OTSousMenu.setStrStatus(commonparameter.statut_enable);
                OTSousMenu.setStrCOMPOSANT(request.getParameter("str_COMPOSANT").toString());
                OTSousMenu.setIntPRIORITY(Integer.parseInt(request.getParameter("int_PRIORITY")));

                        //P_Key
                ObllBase.persiste(OTSousMenu);
                new logger().oCategory.info("Mise a jour TMenu " + OTSousMenu.getLgSOUSMENUID() + " StrLabel " + OTSousMenu.getStrVALUE());
                
            }
        } else if (request.getParameter("mode").toString().equals("delete")) {
            
            OTSousMenu = ObllBase.getOdataManager().getEm().find(dal.TSousMenu.class, request.getParameter("lg_SOUS_MENU_ID"));
            OTSousMenu.setStrStatus(commonparameter.statut_delete);
            ObllBase.persiste(OTSousMenu);


            /*  if (!ObllBase.delete(OTSousMenu)) {
             ObllBase.setDetailmessage("Impossible de supprimer");
             }*/
            new logger().oCategory.info("Suppression du menu " + request.getParameter("lg_SOUS_MENU_ID").toString());
            
        } else {
        }
        
    }
    
    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:1,lg_SOUS_MENU_ID: \"" + OTSousMenu.getLgSOUSMENUID() + "\" }";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>