<%-- 
    Document   : ws_transaction
    Created on : 10 nov. 2016, 14:21:40
    Author     : KKOFFI
--%>
<%@page import="bll.configManagement.CategoryClientManager"%>
<%@page import="org.json.JSONObject"%>

<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%
    String lg_CATEGORY_CLIENT_ID = "";
    String str_LIBELLE = "";
    String str_ESCRIPTION = "";
    Short int_taux=0;

    if (request.getParameter("lg_CATEGORY_CLIENT_ID") != null && !"".equals(request.getParameter("lg_CATEGORY_CLIENT_ID"))) {
        lg_CATEGORY_CLIENT_ID = request.getParameter("lg_CATEGORY_CLIENT_ID");

    }

    if (request.getParameter("str_LIBELLE") != null && !"".equals(request.getParameter("str_LIBELLE"))) {
        str_LIBELLE = request.getParameter("str_LIBELLE");

    }
    if (request.getParameter("int_taux") != null && !"".equals(request.getParameter("int_taux"))) {
        int_taux =Short.valueOf(request.getParameter("int_taux")) ;

    }
    if (request.getParameter("str_ESCRIPTION") != null && !"".equals(request.getParameter("str_ESCRIPTION"))) {
        str_ESCRIPTION = request.getParameter("str_ESCRIPTION");

    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    dataManager OdataManager = new dataManager();
    OdataManager.initEntityManager();
    CategoryClientManager manager = new CategoryClientManager(OdataManager, OTUser);
    JSONObject json = new JSONObject();
    if ("create".equals(request.getParameter("mode"))) {
        if (manager.create(str_LIBELLE, str_ESCRIPTION,int_taux)) {
            json.put("success", 1);
        } else {
            json.put("success", 0);
        }

    } else if ("update".equals(request.getParameter("mode"))) {
        if (manager.update(lg_CATEGORY_CLIENT_ID, str_LIBELLE, str_ESCRIPTION,int_taux)) {
            json.put("success", 1);
        } else {
            json.put("success", 0);
        }
    } else {
        if (manager.delete(lg_CATEGORY_CLIENT_ID)) {
            json.put("success", 1);
        } else {
            json.put("success", 0);
        }
    }


%>
<%=json%>