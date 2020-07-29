<%@page import="dal.TOfficine"%>
<%@page import="bll.configManagement.EmplacementManagement"%>

<%@page import="dal.dataManager" %>

<%@page import="java.util.*"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import="org.json.JSONObject"  %>            
<%@page import="org.json.JSONArray"  %> 
<%@page import="dal.TUser"  %>
<%@page import="toolkits.utils.jdom"  %>
<%@page import="toolkits.utils.logger"  %>


<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<% 
    dataManager OdataManager = new dataManager();
   
    
   


%>

<%    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

    OdataManager.initEntityManager();

    OTUser = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());

    JSONArray arrayObj = new JSONArray();

    try {
        OdataManager.getEm().refresh(OTUser);
    } catch (Exception er) {
    }
    EmplacementManagement em = new EmplacementManagement(OdataManager);
    TOfficine officine = em.getOfficine();

    JSONObject json = new JSONObject();
    json.put("lg_OFFICINE_ID", officine.getLgOFFICINEID());
    json.put("str_FIRST_NAME", officine.getStrFIRSTNAME());
    //json.put("str_PHONE", officine.getStrPHONE());
    json.put("str_PHONE", (officine.getStrAUTRESPHONES()!="" && officine.getStrAUTRESPHONES()!=null )?officine.getStrPHONE()+";"+officine.getStrAUTRESPHONES():officine.getStrPHONE());
    json.put("str_NOM_ABREGE", officine.getStrNOMABREGE());
    json.put("str_NOM_COMPLET", officine.getStrNOMCOMPLET());
    json.put("str_COMMENTAIRE2", officine.getStrCOMMENTAIRE2());
    json.put("str_COMMENTAIRE1", officine.getStrCOMMENTAIRE1());
    json.put("str_ENTETE", officine.getStrENTETE());

    json.put("str_LAST_NAME", officine.getStrLASTNAME());
    json.put("str_ADRESSSE_POSTALE", officine.getStrADRESSSEPOSTALE());
    json.put("str_STATUT", officine.getStrSTATUT());
    json.put("str_NUM_COMPTABLE", officine.getStrNUMCOMPTABLE());
    json.put("str_COMMENTAIREOFFICINE", officine.getStrCOMMENTAIREOFFICINE());
    json.put("str_CENTRE_IMPOSITION", officine.getStrCENTREIMPOSITION());

    json.put("str_REGISTRE_IMPOSITION", officine.getStrREGISTREIMPOSITION());
    json.put("str_REGISTRE_COMMERCE", officine.getStrREGISTRECOMMERCE());
    json.put("str_COMPTE_CONTRIBUABLE", officine.getStrCOMPTECONTRIBUABLE());
    json.put("str_COMPTE_BANCAIRE", officine.getStrCOMPTEBANCAIRE()!=null?officine.getStrCOMPTEBANCAIRE():"");
    

    arrayObj.put(json);

%>

<%= arrayObj.toString()%>