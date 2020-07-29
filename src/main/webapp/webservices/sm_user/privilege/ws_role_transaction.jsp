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


<%   Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();

%>

<%    String lg_ROLE_ID = "", lg_PRIVELEGE_ID = "";
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();

    if (request.getParameter("lg_ROLE_ID") != null) {
        lg_ROLE_ID = request.getParameter("lg_ROLE_ID");
        new logger().OCategory.info("lg_ROLE_ID " + lg_ROLE_ID);
    }

    if (request.getParameter("lg_PRIVELEGE_ID") != null) {
        lg_PRIVELEGE_ID = request.getParameter("lg_PRIVELEGE_ID");
        new logger().OCategory.info("lg_PRIVELEGE_ID " + lg_PRIVELEGE_ID);
    }

    bllBase ObllBase = new bllBase();
    ObllBase.setOTUser(OTUser);
    ObllBase.LoadDataManger(OdataManager);
    ObllBase.LoadMultilange(oTranslate);
    //ObllBase.setMessage(commonparameter.PROCESS_SUCCESS);

    privilege Oprivilege = new privilege(OdataManager, OTUser);

    if (request.getParameter("mode") != null) {
        if (request.getParameter("mode").toString().equals("create")) {
            Oprivilege.createRolePrivilege(lg_ROLE_ID, lg_PRIVELEGE_ID);
            ObllBase.setMessage(Oprivilege.getMessage());
            ObllBase.setDetailmessage(Oprivilege.getDetailmessage());
        } else if (request.getParameter("mode").toString().equals("delete")) {
            Oprivilege.deleteRolePrivilege(lg_ROLE_ID, lg_PRIVELEGE_ID);
            ObllBase.setDetailmessage(Oprivilege.getDetailmessage());
            ObllBase.setMessage(Oprivilege.getMessage());
        } else {
        }

    }

   String result;

    if (ObllBase.getMessage()
            .equals(commonparameter.PROCESS_SUCCESS)) {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

    } else {
        result = "{success:\"" + ObllBase.getMessage() + "\", errors_code: \"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
    }

    new logger().OCategory.info("JSON " + result);


%>
<%=result%>