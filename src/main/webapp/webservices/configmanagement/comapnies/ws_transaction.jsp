<%-- 
    Document   : ws_transaction
    Created on : 10 nov. 2016, 14:21:40
    Author     : KKOFFI
--%>
<%@page import="bll.configManagement.GroupeTierspayantController"%>

<%@page import="org.json.JSONObject"%>

<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%
    String lg_COMPANY_ID = "";
    String str_RAISONSOCIALE = "";
    String str_ADRESS = "";
    String str_PHONE = "", str_CEL = "";

    if (request.getParameter("lg_COMPANY_ID") != null && !"".equals(request.getParameter("lg_COMPANY_ID"))) {
        lg_COMPANY_ID = request.getParameter("lg_COMPANY_ID");

    }

    if (request.getParameter("str_RAISONSOCIALE") != null && !"".equals(request.getParameter("str_RAISONSOCIALE"))) {
        str_RAISONSOCIALE = request.getParameter("str_RAISONSOCIALE");

    }
    if (request.getParameter("str_CEL") != null && !"".equals(request.getParameter("str_CEL"))) {
        str_CEL = request.getParameter("str_CEL");

    }
    if (request.getParameter("str_ADRESS") != null && !"".equals(request.getParameter("str_ADRESS"))) {
        str_ADRESS = request.getParameter("str_ADRESS");

    }
    if (request.getParameter("str_PHONE") != null && !"".equals(request.getParameter("str_PHONE"))) {
        str_PHONE = request.getParameter("str_PHONE");

    }
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    dataManager OdataManager = new dataManager();
    OdataManager.initEntityManager();
    GroupeTierspayantController manager = new GroupeTierspayantController(OdataManager.getEmf());
    JSONObject json = new JSONObject();
    if ("create".equals(request.getParameter("mode"))) {
        if (manager.addCompany(str_RAISONSOCIALE, str_ADRESS, str_PHONE, str_CEL)) {
            json.put("success", 1);
        } else {
            json.put("success", 0);
        }

    } else if ("update".equals(request.getParameter("mode"))) {
        if (manager.updateCompany(lg_COMPANY_ID, str_RAISONSOCIALE, str_ADRESS, str_PHONE, str_CEL)) {
            json.put("success", 1);
        } else {
            json.put("success", 0);
        }
    } else {
        if (manager.deleteCompany(lg_COMPANY_ID)) {
            json.put("success", 1);
        } else {
            json.put("success", 0);
        }
    }


%>
<%=json%>