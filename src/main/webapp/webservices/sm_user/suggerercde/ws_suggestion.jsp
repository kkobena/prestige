<%-- 
    Document   : ws_suggestion
    Created on : 3 juin 2017, 21:05:06
    Author     : KKOFFI
--%>
<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>
<%@page import="org.json.JSONObject"%>
<%@page import="bll.commandeManagement.suggestionManagement"%>
<%@page import="toolkits.utils.logger"%>
<%@page import="bll.common.Parameter"%>
<%@page import="toolkits.utils.date"%>
<%@page import="java.util.Date"%>
<%@page import="dal.dataManager"%>
<%

    dataManager OdataManager = new dataManager();


%>

<%    String modedisplay = "", str_Date_Debut = date.DateToString(new Date(), date.formatterMysqlShort), str_Date_Fin = str_Date_Debut,
            h_debut = "00:00", h_fin = "23:59", search_value = "", lg_USER_ID = "%%", str_TYPE_TRANSACTION = Parameter.LESS, prixachatFiltre = "TOUT", stockFiltre = "TOUT";;

    int int_NUMBER = 0, stock = Integer.MIN_VALUE;;
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    OdataManager.initEntityManager();
    suggestionManagement OsuggestionManagement = new suggestionManagement(OdataManager, OTUser);

    JSONObject json = new JSONObject();
    if (request.getParameter("mode").equals("sendProductSellToSuggestion")) {

        if (request.getParameter("lg_USER_ID") != null && !"".equals(request.getParameter("lg_USER_ID"))) {
            lg_USER_ID = request.getParameter("lg_USER_ID");
            new logger().OCategory.info("search_value :" + search_value);
        }

        if (request.getParameter("str_TYPE_TRANSACTION") != null) {
            str_TYPE_TRANSACTION = request.getParameter("str_TYPE_TRANSACTION");
            new logger().OCategory.info("str_TYPE_TRANSACTION " + str_TYPE_TRANSACTION);
        }

        if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
            str_Date_Debut = request.getParameter("dt_Date_Debut");
            new logger().OCategory.info("str_Date_Debut :" + str_Date_Debut);
        }
        if (request.getParameter("int_NUMBER") != null && !"".equals(request.getParameter("int_NUMBER"))) {
            int_NUMBER = Integer.valueOf(request.getParameter("int_NUMBER"));

        }

        if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
            str_Date_Fin = request.getParameter("dt_Date_Fin");
            new logger().OCategory.info("str_Date_Fin :" + str_Date_Fin);
        }

        if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
            h_debut = request.getParameter("h_debut");
            new logger().OCategory.info("h_debut :" + h_debut);
        }
        if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
            h_fin = request.getParameter("h_fin");
            new logger().OCategory.info("h_fin :" + h_fin);
        }
        if (request.getParameter("modedisplay") != null) {
            modedisplay = request.getParameter("modedisplay");
            new logger().OCategory.info("modedisplay :" + modedisplay);
        }
        if (request.getParameter("search_value") != null) {
            search_value = request.getParameter("search_value");
            new logger().OCategory.info("search_value :" + search_value);
        }
        if (request.getParameter("prixachatFiltre") != null) {
            prixachatFiltre = request.getParameter("prixachatFiltre");
        }
        if (request.getParameter("stockFiltre") != null) {
            stockFiltre = request.getParameter("stockFiltre");
        }
        if (request.getParameter("stock") != null) {
            try {
                stock = Integer.parseInt(request.getParameter("stock"));
            } catch (Exception e) {
            }

        }
        if (modedisplay.equalsIgnoreCase("groupe")) {
            modedisplay = "GROUP BY lg_FAMILLE_ID";
        }
        json = OsuggestionManagement.sendProductSellToSuggestion2(search_value, str_Date_Debut, str_Date_Fin, h_debut, h_fin, lg_USER_ID, str_TYPE_TRANSACTION, int_NUMBER, modedisplay, prixachatFiltre, stock, stockFiltre);

    }


%>
<%= json%>