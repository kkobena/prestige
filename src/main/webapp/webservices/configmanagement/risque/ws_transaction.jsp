<%@page import="dal.TRisque"%>
<%@page import="dal.TTypeRisque"%>
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
<%@page import="bll.configManagement.familleManagement"  %>

<%!
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
    privilege Oprivilege = new privilege();
    TRole OTRole = null;
    dal.TRisque OTRisque = null;
    String lg_RISQUE_ID = "%%", str_CODE_RISQUE = "%%", str_LIBELLE_RISQUE = "%%",
            lg_TYPERISQUE_ID = "%%", str_RISQUE_OFFICIEL = "%%";

%>




<%
    if (request.getParameter("lg_RISQUE_ID") != null) {
        lg_RISQUE_ID = request.getParameter("lg_RISQUE_ID");
    }
    if (request.getParameter("str_CODE_RISQUE") != null) {
        str_CODE_RISQUE = request.getParameter("str_CODE_RISQUE");
    }
    if (request.getParameter("str_LIBELLE_RISQUE") != null) {
        str_LIBELLE_RISQUE = request.getParameter("str_LIBELLE_RISQUE");
    }
    if (request.getParameter("lg_TYPERISQUE_ID") != null) {
        lg_TYPERISQUE_ID = request.getParameter("lg_TYPERISQUE_ID");
    }
    // str_RISQUE_OFFICIEL
    if (request.getParameter("str_RISQUE_OFFICIEL") != null) {
        str_RISQUE_OFFICIEL = request.getParameter("str_RISQUE_OFFICIEL");
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    ObllBase.setMessage(commonparameter.PROCESS_FAILED);

    ObllBase.setDetailmessage("RISQUE WS TRANSACTION");
    new logger().oCategory.info("le mode : " + request.getParameter("mode"));
    new logger().oCategory.info("ID " + request.getParameter("lg_RISQUE_ID"));

    new logger().oCategory.info("lg_TYPERISQUE_ID   @@@@@@@@@@@@@@@@     " + request.getParameter("lg_TYPERISQUE_ID"));

    if (request.getParameter("mode") != null) {

        if (request.getParameter("mode").toString().equals("create")) {

            dal.TRisque OTRisque = new TRisque();
            dal.TTypeRisque OTTypeRisque = ObllBase.getOdataManager().getEm().find(TTypeRisque.class, lg_TYPERISQUE_ID);
            if (OTTypeRisque == null) {
                ObllBase.buildErrorTraceMessage("Impossible de creer un " + OTTypeRisque, "Ref TTypeRisque : " + lg_TYPERISQUE_ID + "  Invalide ");
                return;
            }
            OTRisque.setLgTYPERISQUEID(OTTypeRisque);
            OTRisque.setLgRISQUEID(key.getComplexId());
            OTRisque.setStrCODERISQUE(str_CODE_RISQUE);
            OTRisque.setStrLIBELLERISQUE(str_LIBELLE_RISQUE);
            // str_RISQUE_OFFICIEL
            OTRisque.setStrRISQUEOFFICIEL(str_RISQUE_OFFICIEL);
            OTRisque.setStrSTATUT(commonparameter.statut_enable);
            OTRisque.setDtCREATED(new Date());

            ObllBase.persiste(OTRisque);
            new logger().oCategory.info("Mise a jour OTRisque " + OTRisque.getLgRISQUEID() + " StrName " + OTRisque.getStrCODERISQUE());

        } else if (request.getParameter("mode").toString().equals("update")) {

            dal.TRisque OTRisque = null;
            OTRisque = ObllBase.getOdataManager().getEm().find(dal.TRisque.class, request.getParameter("lg_RISQUE_ID").toString());

            try {

                dal.TTypeRisque OTTypeRisque = ObllBase.getOdataManager().getEm().find(dal.TTypeRisque.class, request.getParameter("lg_TYPERISQUE_ID").toString());
                new logger().oCategory.info("lg_TYPERISQUE_ID     Create   " + lg_TYPERISQUE_ID + "  lg_TYPERISQUE_ID du request       " + request.getParameter("lg_TYPERISQUE_ID").toString());

                if (OTTypeRisque != null) {
                    OTRisque.setLgTYPERISQUEID(OTTypeRisque);
                }
            } catch (Exception e) {

            }

            OTRisque.setStrCODERISQUE(str_CODE_RISQUE);
            OTRisque.setStrLIBELLERISQUE(str_LIBELLE_RISQUE);
            // str_RISQUE_OFFICIEL
            OTRisque.setStrRISQUEOFFICIEL(str_RISQUE_OFFICIEL);
            OTRisque.setStrSTATUT(commonparameter.statut_enable);
            OTRisque.setDtUPDATED(new Date());

            ObllBase.persiste(OTRisque);
            new logger().oCategory.info("Mise a jour OTRisque " + OTRisque.getLgRISQUEID() + " StrLabel " + OTRisque.getStrCODERISQUE());

        } else if (request.getParameter("mode").toString().equals("delete")) {

            dal.TRisque OTRisque = null;
            OTRisque = ObllBase.getOdataManager().getEm().find(dal.TRisque.class, request.getParameter("lg_RISQUE_ID"));

            OTRisque.setStrSTATUT(commonparameter.statut_delete);
            ObllBase.persiste(OTRisque);

            new logger().oCategory.info("Suppression de code gestion " + request.getParameter("lg_RISQUE_ID").toString());

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