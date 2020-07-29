<%@page import="bll.configManagement.groupeFamilleManagement"%>
<%@page import="dal.TGroupeFamille"%>
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

<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_GROUPE_FAMILLE_ID = "%%", str_CODE_GROUPE_FAMILLE =  "%%", str_LIBELLE = "%%", str_COMMENTAIRE = "%%", str_STATUT = "%%";
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
   dal.TGroupeFamille OTGroupefamille = new dal.TGroupeFamille();
%>




<%
    if (request.getParameter("lg_GROUPE_FAMILLE_ID") != null) {
        lg_GROUPE_FAMILLE_ID = request.getParameter("lg_GROUPE_FAMILLE_ID");
    }
    if (request.getParameter("str_CODE_GROUPE_FAMILLE") != null) {
        str_CODE_GROUPE_FAMILLE = request.getParameter("str_CODE_GROUPE_FAMILLE");
    }
    if (request.getParameter("str_LIBELLE") != null) {
        str_LIBELLE = request.getParameter("str_LIBELLE");
    }
    if (request.getParameter("str_COMMENTAIRE") != null) {
        str_COMMENTAIRE = request.getParameter("str_COMMENTAIRE");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_GROUPE_FAMILLE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");

            groupeFamilleManagement OgroupeFamilleManagement = new groupeFamilleManagement(OdataManager);
            OgroupeFamilleManagement.create(str_CODE_GROUPE_FAMILLE, str_LIBELLE, str_COMMENTAIRE);
            
            new logger().oCategory.info("Creation  OOKKK");

        } else if (request.getParameter("mode").toString().equals("update")) {

                groupeFamilleManagement OgroupeFamilleManagement = new groupeFamilleManagement(OdataManager);
                OgroupeFamilleManagement.update(lg_GROUPE_FAMILLE_ID,str_CODE_GROUPE_FAMILLE, str_LIBELLE, str_COMMENTAIRE);

            new logger().oCategory.info("Modif OK  OOKKK");

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TGroupeFamille OTGroupefamille = null;
            OTGroupefamille = ObllBase.getOdataManager().getEm().find(dal.TGroupeFamille.class, request.getParameter("lg_GROUPE_FAMILLE_ID"));

            OTGroupefamille.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTGroupefamille);

            new logger().oCategory.info("Suppression du GroupeFamille " + request.getParameter("lg_GROUPE_FAMILLE_ID").toString());

        } else {
        }

    }

    String result;
    TGroupeFamille OTGROUPE_FAMILLE = new dal.TGroupeFamille();
    if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:1,lg_GROUPE_FAMILLE_ID: \"" + OTGROUPE_FAMILLE.getLgGROUPEFAMILLEID() + "\" }";
    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }
    new logger().OCategory.info("JSON " + result);
%>
<%=result%>