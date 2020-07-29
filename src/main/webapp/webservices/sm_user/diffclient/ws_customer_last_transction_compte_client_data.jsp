<%@page import="dal.TParameters"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="bll.stockManagement.StockManager"%>
<%@page import="dal.TTypeStockFamille"%>
<%@page import="bll.teller.tellerManagement"%>
<%@page import="bll.configManagement.familleManagement"%>
<%@page import="bll.bllBase"%>
<%@page import="dal.TFamilleStock"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TFamille"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>          
<%@page import="org.json.JSONArray"  %> 
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();

    date key = new date();

%>

<%    int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
    new logger().OCategory.info("dans ws data famille jdbc *******************************");
%>


<!-- fin logic de gestion des page -->

<%    String lg_FAMILLE_ID = "%%", lg_GROUPE_FAMILLE_ID = "%%", str_NAME = "%%", str_DESCRIPTION = "%%", str_TYPE_TRANSACTION = "%%", lg_TYPE_STOCK_ID = "1", str_KEY = commonparameter.PARAMETER_INDICE_SECURITY;
    if (request.getParameter("search_value") != null) {
        //Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
        Os_Search_poste.setOvalue(request.getParameter("search_value") + "%");
        new logger().OCategory.info("Search book " + request.getParameter("search_value"));
    } else {
        Os_Search_poste.setOvalue("%%");
    }

%>

<%
%>


<%    JSONArray arrayObj = new JSONArray();
    dal.jconnexion Ojconnexion = new dal.jconnexion();
    try {

        Ojconnexion.initConnexion();
        Ojconnexion.OpenConnexion();
        String qry = "SELECT "
                + "  t_compte_client_tiers_payant.lg_COMPTE_CLIENT_ID,"
                + "  t_preenregistrement.lg_PREENREGISTREMENT_ID, "
                + "  t_preenregistrement.str_REF_BON, "
                + "  t_preenregistrement_compte_client_tiers_payent.dt_CREATED, "
                + "  t_compte_client_tiers_payant.lg_TIERS_PAYANT_ID, "
                + "  t_compte_client_tiers_payant.b_IS_RO, "
                + "  t_compte_client_tiers_payant.b_IS_RC1, "
                + "  t_compte_client_tiers_payant.b_IS_RC2, "
                + "  t_tiers_payant.str_NAME ,"
                + " t_compte_client_tiers_payant.int_POURCENTAGE ,"
                + " t_compte_client_tiers_payant.int_PRIORITY ,"
                + " t_compte_client_tiers_payant.lg_COMPTE_CLIENT_TIERS_PAYANT_ID "
                + "FROM "
                + "  t_preenregistrement_compte_client_tiers_payent "
                + "  INNER JOIN t_preenregistrement ON (t_preenregistrement_compte_client_tiers_payent.lg_PREENREGISTREMENT_ID = t_preenregistrement.lg_PREENREGISTREMENT_ID) "
                + "  INNER JOIN t_compte_client_tiers_payant ON (t_preenregistrement_compte_client_tiers_payent.lg_COMPTE_CLIENT_TIERS_PAYANT_ID = t_compte_client_tiers_payant.lg_COMPTE_CLIENT_TIERS_PAYANT_ID) "
                + " INNER JOIN t_tiers_payant ON (t_compte_client_tiers_payant.lg_TIERS_PAYANT_ID = t_tiers_payant.lg_TIERS_PAYANT_ID) "
                + "WHERE t_preenregistrement.lg_PREENREGISTREMENT_ID LIKE  getLAST_PREENREGISTREMENT_COMPTE_CLIENT('" + request.getParameter("lg_COMPTE_CLIENT_ID") + "')  "
                + "ORDER BY "
                + "  t_preenregistrement_compte_client_tiers_payent.dt_CREATED DESC";
        new logger().OCategory.info(qry);
        Ojconnexion.set_Request(qry);
        java.sql.ResultSetMetaData rsmddatas = Ojconnexion.get_resultat().getMetaData();
        while (Ojconnexion.get_resultat().next()) {

            JSONObject json = new JSONObject();
            // System.out.println("178");
            json.put("lg_COMPTE_CLIENT_ID", Ojconnexion.get_resultat().getString("lg_COMPTE_CLIENT_ID"));
            json.put("lg_PREENREGISTREMENT_ID", Ojconnexion.get_resultat().getString("lg_PREENREGISTREMENT_ID"));
            json.put("str_REF_BON", Ojconnexion.get_resultat().getString("str_REF_BON"));
            json.put("dt_CREATED", Ojconnexion.get_resultat().getString("dt_CREATED"));
            json.put("str_NAME", Ojconnexion.get_resultat().getString("str_NAME"));
            json.put("b_IS_RO", Ojconnexion.get_resultat().getString("b_IS_RO"));
            json.put("b_IS_RC1", Ojconnexion.get_resultat().getString("b_IS_RC1"));
            json.put("b_IS_RC2", Ojconnexion.get_resultat().getString("b_IS_RC2"));
            json.put("lg_TIERS_PAYANT_ID", Ojconnexion.get_resultat().getString("lg_TIERS_PAYANT_ID"));
            json.put("int_POURCENTAGE", Ojconnexion.get_resultat().getString("int_POURCENTAGE"));
           // json.put("int_POURCENTAGE", Ojconnexion.get_resultat().getString("int_POURCENTAGE"));
            json.put("int_PRIORITY", Ojconnexion.get_resultat().getString("int_PRIORITY"));
            json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_ID", Ojconnexion.get_resultat().getString("lg_COMPTE_CLIENT_TIERS_PAYANT_ID"));

            if (Ojconnexion.get_resultat().getString("b_IS_RO") == null) {
                json.put("b_IS_RO", "0");
            }
            if (Ojconnexion.get_resultat().getString("b_IS_RC1") == null) {
                json.put("b_IS_RC1", "0");

            }
            if (Ojconnexion.get_resultat().getString("b_IS_RC2") == null) {
                json.put("b_IS_RC2", "0");

            }
            if (Ojconnexion.get_resultat().getString("str_NAME") == null) {

                json.put("str_NAME", "0");

            }

            arrayObj.put(json);

        }
        Ojconnexion.CloseConnexion();
    } catch (Exception ex) {
        new logger().OCategory.fatal(ex.getMessage());
    }

    String result = "({\"total\":\"" + 0 + " \",\"results\":" + arrayObj.toString() + "})";
    new logger().OCategory.info("result   ----  " + result);

    OdataManager = null;
    Os_Search_poste = null;
    Os_Search_poste_data = null;
    Ojconnexion = null;
%>

<%= result%>