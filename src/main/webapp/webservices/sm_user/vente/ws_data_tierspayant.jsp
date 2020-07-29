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

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%
    dataManager OdataManager = new dataManager();
    TUser OTUser = null;
    TPreenregistrement oTPreenregistrement = null;
    JSONObject json = null;
%>



<%
    List<TPreenregistrementCompteClientTiersPayent> listTPreenregistrementCompteClientTiersPayent = new ArrayList<TPreenregistrementCompteClientTiersPayent>();
    OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    
    String lg_PREENREGISTREMENT_ID = "";
    int start = 0, limit = jdom.int_size_pagination, total = 0;

    if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        new logger().OCategory.info("start " + start);
    }

    if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
        new logger().OCategory.info("limit " + limit);
    }

    if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
        new logger().OCategory.info("lg_PREENREGISTREMENT_ID :" + lg_PREENREGISTREMENT_ID);
    }

   
    Preenregistrement OPreenregistrement = new Preenregistrement(OdataManager, OTUser);
    oTPreenregistrement = OPreenregistrement.getTPreenregistrementByRef(lg_PREENREGISTREMENT_ID);
    listTPreenregistrementCompteClientTiersPayent = OPreenregistrement.getListeTPreenregistrementCompteClientTiersPayent(oTPreenregistrement.getLgPREENREGISTREMENTID(), oTPreenregistrement.getStrSTATUT());
    //  total = OPreenregistrement.getListeTPreenregistrementCompteClientTiersPayent(oTPreenregistrement.getLgPREENREGISTREMENTID(), oTPreenregistrement.getStrSTATUT(), start, limit).size();
 
%>


<%    JSONArray arrayObj = new JSONArray();

    for (int i = 0; i < (listTPreenregistrementCompteClientTiersPayent.size() < limit ? listTPreenregistrementCompteClientTiersPayent.size() : limit); i++) {
       
        json = new JSONObject();
        json.put("lg_COMPTE_CLIENT_TIERS_PAYANT_ID", listTPreenregistrementCompteClientTiersPayent.get(i).getLgPREENREGISTREMENTCOMPTECLIENTPAYENTID());
        json.put("lg_TIERS_PAYANT_ID", listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getLgTIERSPAYANTID());
        json.put("str_CODE_ORGANISME", listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME());
        json.put("str_NAME", listTPreenregistrementCompteClientTiersPayent.get(i).getStrREFBON());
        json.put("int_POURCENTAGE", listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getIntPOURCENTAGE());
        json.put("dbl_PLAFOND_CLIENT", listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getDblPLAFOND());
        json.put("dbl_QUOTA_CONSO_MENSUELLE_CLIENT", listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOMENSUELLE());
        json.put("dbl_PLAFOND_CONSO_DIFFRERENCE_CLIENT", listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getDblPLAFOND() - listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOMENSUELLE());
        json.put("str_NUMERO_SECURITE_SOCIAL", listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getStrNUMEROSECURITESOCIAL());
        json.put("dbl_QUOTA_CONSO_VENTE", listTPreenregistrementCompteClientTiersPayent.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getDblQUOTACONSOVENTE());
        
        arrayObj.put(json);
    }

    String result = "({\"total\":\"" + listTPreenregistrementCompteClientTiersPayent.size() + " \",\"results\":" + arrayObj.toString() + "})";

    System.out.println(result);
%>

<%= result%>
