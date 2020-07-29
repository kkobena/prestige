<%@page import="bll.tierspayantManagement.tierspayantManagement"%>
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


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    
%>

<%
    String lg_COMPTE_CLIENT_ID = "", lg_TIERS_PAYANT_ID = "";
    double dbl_QUOTA_CONSO_MENSUELLE = 0.0,dbl_PLAFOND=0;
    int int_POURCENTAGE = 0;
    boolean b_IsAbsolute=false;
    Integer db_PLAFOND_ENCOURS=0;
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();

            if (request.getParameter("dbl_PLAFOND") != null) {
                dbl_PLAFOND = Double.valueOf( request.getParameter("dbl_PLAFOND"));
              
            }
            if (request.getParameter("db_PLAFOND_ENCOURS") != null) {
                db_PLAFOND_ENCOURS = Integer.valueOf( request.getParameter("db_PLAFOND_ENCOURS"));
              
            }
             if (request.getParameter("b_IsAbsolute") != null) {
                b_IsAbsolute = Boolean.valueOf( request.getParameter("b_IsAbsolute"));
              
            }
            
            if (request.getParameter("lg_COMPTE_CLIENT_ID") != null) {
                lg_COMPTE_CLIENT_ID = request.getParameter("lg_COMPTE_CLIENT_ID");
                new logger().OCategory.info("lg_COMPTE_CLIENT_ID "+lg_COMPTE_CLIENT_ID);
            }
             if (request.getParameter("dbl_PLAFOND") != null) {
                dbl_PLAFOND = Double.valueOf( request.getParameter("dbl_PLAFOND"));
              
            }
             if (request.getParameter("dbl_QUOTA_CONSO_MENSUELLE") != null) {
                dbl_QUOTA_CONSO_MENSUELLE = Double.parseDouble(request.getParameter("dbl_QUOTA_CONSO_MENSUELLE"));
                new logger().OCategory.info("dbl_QUOTA_CONSO_MENSUELLE "+dbl_QUOTA_CONSO_MENSUELLE);
            }
            
            if (request.getParameter("lg_TIERS_PAYANT_ID") != null) {
                lg_TIERS_PAYANT_ID = request.getParameter("lg_TIERS_PAYANT_ID");
                new logger().OCategory.info("lg_TIERS_PAYANT_ID "+lg_TIERS_PAYANT_ID);
            }

            bllBase ObllBase = new bllBase();
            ObllBase.setOTUser(OTUser);
            ObllBase.LoadDataManger(OdataManager);
            ObllBase.LoadMultilange(oTranslate);
            //ObllBase.setMessage(commonparameter.PROCESS_SUCCESS);
            tierspayantManagement OtierspayantManagement = new tierspayantManagement(OdataManager);
            if (request.getParameter("mode") != null) {
                if (request.getParameter("mode").toString().equals("create")) {
                    
                   // OtierspayantManagement.create_compteclt_tierspayant(lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID, int_POURCENTAGE, dbl_QUOTA_CONSO_MENSUELLE,db_PLAFOND_ENCOURS, b_IsAbsolute,dbl_PLAFOND)  ;
                    ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
                    ObllBase.setMessage(OtierspayantManagement.getMessage());
                } else if (request.getParameter("mode").toString().equals("delete")) {
                    OtierspayantManagement.delete_compteclt_tierspayant(lg_COMPTE_CLIENT_ID, lg_TIERS_PAYANT_ID);
                    ObllBase.setDetailmessage(OtierspayantManagement.getDetailmessage());
                    ObllBase.setMessage(OtierspayantManagement.getMessage());
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