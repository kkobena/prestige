<%@page import="bll.configManagement.EmplacementManagement"%>
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

<%

    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();


%>




<%    String str_FIRST_NAME = "", lg_OFFICINE_ID = "", str_NOM_ABREGE = "", str_NOM_COMPLET = "", str_LAST_NAME = "", str_ADRESSSE_POSTALE = "", str_PHONE = "", str_COMMENTAIRE2 = "", str_COMMENTAIRE1 = "", str_ENTETE = "";
    String str_PHONE_OFFICINE = "",str_COMMENTAIREOFFICINE="",str_COMPTE_BANCAIRE="",str_NUM_COMPTABLE="", str_COMPTE_CONTRIBUABLE = "", str_REGISTRE_COMMERCE = "", str_REGISTRE_IMPOSITION = "", str_CENTRE_IMPOSITION = "";

      if (request.getParameter("str_COMPTE_CONTRIBUABLE") != null && !"".equals(request.getParameter("str_COMPTE_CONTRIBUABLE"))) {
        str_COMPTE_CONTRIBUABLE = request.getParameter("str_COMPTE_CONTRIBUABLE");
    }
    if (request.getParameter("str_PHONE_OFFICINE") != null && !"".equals(request.getParameter("str_PHONE_OFFICINE"))) {
        str_PHONE_OFFICINE = request.getParameter("str_PHONE_OFFICINE");
    }
    
      if (request.getParameter("str_REGISTRE_COMMERCE") != null && !"".equals(request.getParameter("str_REGISTRE_COMMERCE"))) {
        str_REGISTRE_COMMERCE = request.getParameter("str_REGISTRE_COMMERCE");
    }
     if (request.getParameter("str_REGISTRE_IMPOSITION") != null && !"".equals(request.getParameter("str_REGISTRE_IMPOSITION"))) {
        str_REGISTRE_IMPOSITION = request.getParameter("str_REGISTRE_IMPOSITION");
    }
    if (request.getParameter("str_CENTRE_IMPOSITION") != null && !"".equals(request.getParameter("str_CENTRE_IMPOSITION"))) {
        str_CENTRE_IMPOSITION = request.getParameter("str_CENTRE_IMPOSITION");
    }
    
    if (request.getParameter("str_ENTETE") != null && !"".equals(request.getParameter("str_ENTETE"))) {
        str_ENTETE = request.getParameter("str_ENTETE");
    }
    
    if (request.getParameter("str_COMMENTAIRE1") != null && !"".equals(request.getParameter("str_COMMENTAIRE1"))) {
        str_COMMENTAIRE1 = request.getParameter("str_COMMENTAIRE1");
    }
 if (request.getParameter("str_NUM_COMPTABLE") != null && !"".equals(request.getParameter("str_NUM_COMPTABLE"))) {
        str_NUM_COMPTABLE = request.getParameter("str_NUM_COMPTABLE");
    }
if (request.getParameter("str_COMMENTAIREOFFICINE") != null && !"".equals(request.getParameter("str_COMMENTAIREOFFICINE"))) {
        str_COMMENTAIREOFFICINE = request.getParameter("str_COMMENTAIREOFFICINE");
    }
    if (request.getParameter("str_COMMENTAIRE2") != null && !"".equals(request.getParameter("str_COMMENTAIRE2"))) {
        str_COMMENTAIRE2 = request.getParameter("str_COMMENTAIRE2");
    }

    if (request.getParameter("str_NOM_ABREGE") != null) {
        str_NOM_ABREGE = request.getParameter("str_NOM_ABREGE");
    }
    if (request.getParameter("str_ADRESSSE_POSTALE") != null) {
        str_ADRESSSE_POSTALE = request.getParameter("str_ADRESSSE_POSTALE");
    }
    if (request.getParameter("lg_OFFICINE_ID") != null) {
        lg_OFFICINE_ID = request.getParameter("lg_OFFICINE_ID");
    }
    if (request.getParameter("str_NOM_COMPLET") != null) {
        str_NOM_COMPLET = request.getParameter("str_NOM_COMPLET");
    }
    if (request.getParameter("str_LAST_NAME") != null) {
        str_LAST_NAME = request.getParameter("str_LAST_NAME");
    }
    if (request.getParameter("str_FIRST_NAME") != null) {
        str_FIRST_NAME = request.getParameter("str_FIRST_NAME");
    }
    if (request.getParameter("str_PHONE") != null) {
        str_PHONE = request.getParameter("str_PHONE");
    }
     if (request.getParameter("str_COMPTE_BANCAIRE") != null) {
        str_COMPTE_BANCAIRE = request.getParameter("str_COMPTE_BANCAIRE");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_RESUME_CAISSE_ID"));

    EmplacementManagement emplacementManagement = new EmplacementManagement(OdataManager);

    if (request.getParameter("mode") != null) {
        new logger().oCategory.info("update -----------------------------------");

        if (request.getParameter("mode").toString().equals("update")) {
            new logger().oCategory.info("update");
              new logger().oCategory.info("str_CENTRE_IMPOSITION=----------- "+str_CENTRE_IMPOSITION+" str_REGISTRE_IMPOSITION "+str_REGISTRE_IMPOSITION); 
            emplacementManagement.updateOfficne(lg_OFFICINE_ID, str_FIRST_NAME, str_NOM_ABREGE, str_NOM_COMPLET, str_LAST_NAME, str_ADRESSSE_POSTALE, str_PHONE, str_COMMENTAIRE1, str_COMMENTAIRE2, str_ENTETE,str_PHONE_OFFICINE, str_COMPTE_CONTRIBUABLE, str_REGISTRE_COMMERCE, str_REGISTRE_IMPOSITION, str_CENTRE_IMPOSITION,str_NUM_COMPTABLE, str_COMMENTAIREOFFICINE,str_COMPTE_BANCAIRE);

            ObllBase.setMessage(emplacementManagement.getMessage());
            ObllBase.setDetailmessage(emplacementManagement.getDetailmessage());

        } else if (request.getParameter("mode").toString().equals("validate")) {
        } else if (request.getParameter("mode").toString().equals("delete")) {
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