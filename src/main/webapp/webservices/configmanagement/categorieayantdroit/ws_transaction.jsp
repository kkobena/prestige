<%@page import="bll.configManagement.ayantDroitManagement"%>
<%@page import="dal.TCategorieAyantdroit"%>
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
    String lg_CATEGORIE_AYANTDROIT_ID = "%%", str_CODE = "%%", str_LIBELLE_CATEGORIE_AYANTDROIT = "%%", str_STATUT = "%%";
    date key = new date();
    Date dt_CREATED, dt_UPDATED;
    dal.TCategorieAyantdroit OTCategorieAyantdroit = new dal.TCategorieAyantdroit();
    List<TCategorieAyantdroit> lstTCategorieAyantdroit = new ArrayList<TCategorieAyantdroit>();
%>




<%
    if (request.getParameter("lg_CATEGORIE_AYANTDROIT_ID") != null) {
        lg_CATEGORIE_AYANTDROIT_ID = request.getParameter("lg_CATEGORIE_AYANTDROIT_ID");
    }
    if (request.getParameter("str_CODE") != null) {
        str_CODE = request.getParameter("str_CODE");
    }
    if (request.getParameter("str_LIBELLE_CATEGORIE_AYANTDROIT") != null) {
        str_LIBELLE_CATEGORIE_AYANTDROIT = request.getParameter("str_LIBELLE_CATEGORIE_AYANTDROIT");
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
    new logger().oCategory.info("ID " + request.getParameter("lg_CATEGORIE_AYANTDROIT_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {
            new logger().oCategory.info("Creation");

            try {

                OdataManager.initEntityManager();
                lstTCategorieAyantdroit = OdataManager.getEm().createQuery("SELECT t FROM TCategorieAyantdroit t WHERE t.strCODE LIKE ?1  AND t.strSTATUT LIKE ?2")
                        .setParameter(1, str_CODE)
                        .setParameter(2, commonparameter.statut_enable).getResultList();

                new logger().oCategory.info("Resultat " + lstTCategorieAyantdroit.size());

                if (lstTCategorieAyantdroit.size() == 0) {

                    ayantDroitManagement OayantDroitManagement = new ayantDroitManagement(OdataManager);
                    OayantDroitManagement.createCategorieAyantdroit(str_CODE, str_LIBELLE_CATEGORIE_AYANTDROIT);

                    ObllBase.setDetailmessage("CAT Cree avec succes");
                    new logger().oCategory.info("Creation  OOKKK");

                } else {
                    ObllBase.setMessage("0");
                    ObllBase.setDetailmessage("Desole CODE " + request.getParameter("str_CODE") + " Deja Utilise");
                }

            } catch (Exception e) {

                ObllBase.setDetailmessage("CAT Echec de creation");
                new logger().oCategory.info("ERROR   ");

            }

        } else if (request.getParameter("mode").toString().equals("update")) {

            ayantDroitManagement OayantDroitManagement = new ayantDroitManagement(OdataManager);
            OayantDroitManagement.updateCategorieAyantdroit(lg_CATEGORIE_AYANTDROIT_ID, str_LIBELLE_CATEGORIE_AYANTDROIT);

            ObllBase.setDetailmessage("CAT Modifie avec succes");
            new logger().oCategory.info("Modif OK  OOKKK");

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TCategorieAyantdroit OTCategorieAyantdroit = null;
            OTCategorieAyantdroit = ObllBase.getOdataManager().getEm().find(dal.TCategorieAyantdroit.class, request.getParameter("lg_CATEGORIE_AYANTDROIT_ID"));

            OTCategorieAyantdroit.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTCategorieAyantdroit);

            new logger().oCategory.info("Suppression du GroupeFamille " + request.getParameter("lg_CATEGORIE_AYANTDROIT_ID").toString());

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