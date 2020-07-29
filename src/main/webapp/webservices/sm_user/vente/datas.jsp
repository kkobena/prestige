<%@page import="java.time.LocalDate"%>
<%@page import="dal.TParameters"%>
<%@page import="bll.utils.TparameterManager"%>
<%@page import="bll.common.Parameter"%>
<%@page import="bll.userManagement.privilege"%>
<%@page import="toolkits.utils.conversion"%>
<%@page import="dal.TPreenregistrementDetail"%>
<%@page import="bll.entity.EntityData"%>
<%@page import="bll.preenregistrement.Preenregistrement"%>
<%@page import="java.util.ArrayList"%>
<%@page import="dal.TMotifReglement"%>
<%@page import="dal.TPreenregistrement"%>
<%@page import="dal.TPreenregistrementCompteClientTiersPayent"%>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TRole"  %>
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
<%
    dataManager OdataManager = new dataManager();
    String str_Date_Debut = LocalDate.now().toString(), str_Date_Fin = str_Date_Debut, search_value = "",
            h_debut = "", h_fin = "", str_TYPE_VENTE = "", lg_EMPLACEMENT_ID = "";
    TUser OTUser = OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    lg_EMPLACEMENT_ID = (OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equalsIgnoreCase(commonparameter.PROCESS_SUCCESS) ? "1" : OTUser.getLgEMPLACEMENTID().getLgEMPLACEMENTID());
    int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        
    }

    if (request.getParameter("search_value") != null) {
        search_value = request.getParameter("search_value");
        
    }

    if (request.getParameter("str_TYPE_VENTE") != null && !request.getParameter("str_TYPE_VENTE").equalsIgnoreCase("")) {
        str_TYPE_VENTE = request.getParameter("str_TYPE_VENTE");
        
    }

    if (request.getParameter("h_debut") != null && !request.getParameter("h_debut").equalsIgnoreCase("")) {
        h_debut = request.getParameter("h_debut");
        
    }
    if (request.getParameter("h_fin") != null && !request.getParameter("h_fin").equalsIgnoreCase("")) {
        h_fin = request.getParameter("h_fin");
       
    }
    if (request.getParameter("dt_Date_Debut") != null && !request.getParameter("dt_Date_Debut").equalsIgnoreCase("")) {
        str_Date_Debut = request.getParameter("dt_Date_Debut");
       
    }

    if (request.getParameter("dt_Date_Fin") != null && !request.getParameter("dt_Date_Fin").equalsIgnoreCase("")) {
        str_Date_Fin = request.getParameter("dt_Date_Fin");
        
    }

    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);

    JSONArray data = OPreenregistrement.listPreenregistrements(false, search_value, str_Date_Debut, str_Date_Fin, h_debut, h_fin, str_TYPE_VENTE, start, limit);

    total = OPreenregistrement.listPreenregistrementCount(search_value, str_Date_Debut, str_Date_Fin, h_debut, h_fin, str_TYPE_VENTE);

    JSONObject json = new JSONObject();

    json.put("total", total).put("results", data);


%>

<%= json%>
