<%@page import="dal.TFamillearticle"%>
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
<%@page import="bll.utils.TparameterManager"  %>
<%@page import="bll.configManagement.familleArticleManagement"  %>


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String maxVenteValue;
    String checkValue = " ";
    String ticketCheck = " ";

    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    dal.TFamillearticle OTFamillearticle = new dal.TFamillearticle();

%>

<%
    // lg_FAMILLEARTICLE_ID
     if (request.getParameter("int_value_max") != null) {
        maxVenteValue = request.getParameter("int_value_max");
    }    
     if (request.getParameter("bool_check") != null) {
        checkValue = request.getParameter("bool_check");
        new logger().oCategory.info(" ++++++++++++bool_check :" +checkValue);
    }
     if(request.getParameter("ticket_check") != null)
     {
         ticketCheck = request.getParameter("ticket_check");
     }
    
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
 TUser user = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(user);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("PAS D'ACTION");
   // new logger().oCategory.info("le mode : " + request.getParameter("mode"));
   // new logger().oCategory.info("ID " + request.getParameter("lg_FAMILLEARTICLE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");

            //familleArticleManagement OfamilleArticleManagement = new familleArticleManagement(OdataManager);
            //OfamilleArticleManagement.create(str_CODE_FAMILLE, str_LIBELLE, str_COMMENTAIRE, lg_GROUPE_FAMILLE_ID);

            new logger().oCategory.info("Creation  OOKKK");

        }
        else if (request.getParameter("mode").toString().equals("update")) 
        {
            if(!checkValue.equals(" "))
            {
                String chkVal=" ";
                if(checkValue.equals("true"))
                {
                    chkVal="1";
                }
                else
                {
                    chkVal="0";
                }                
                TparameterManager OTparameterManager = new TparameterManager(OdataManager);
                OTparameterManager.updateParameter("KEY_APPLIQ_REMISE", chkVal, "");
            }
            else if(!ticketCheck.equals(" "))
            {
                String chkVal=" ";
                if(ticketCheck.equals("true"))
                {
                    chkVal="1";
                }
                else
                {
                    chkVal="0";
                }                
                TparameterManager OTparameterManager = new TparameterManager(OdataManager);
                OTparameterManager.updateParameter("KEY_EDIT_TICKET", chkVal, "");
            }
            else
            {               
                TparameterManager OTparameterManager = new TparameterManager(OdataManager);
                OTparameterManager.updateParameter("KEY_MAX_VALUE_VENTE", maxVenteValue, "");

                new logger().oCategory.info("Modif OK  OOKKK");
            }
            

        } 
        else if (request.getParameter("mode").toString().equals("delete")) {

           // TFamillearticle OTFamillearticle = null;
            
            //OTFamillearticle = ObllBase.getOdataManager().getEm().find(TFamillearticle.class, lg_FAMILLEARTICLE_ID);
            
           // OTFamillearticle.setStrSTATUT(commonparameter.statut_delete);
            //ObllBase.persiste(OTFamillearticle);

           // new logger().oCategory.info("Suppression du Grossiste " + request.getParameter("lg_FAMILLEARTICLE_ID").toString());

        } else {
        }

    }

    String result;
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);

%>
<%=result%>