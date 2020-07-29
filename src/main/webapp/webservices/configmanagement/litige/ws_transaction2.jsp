<%@page import="com.asc.prestige2.business.litiges.concrete.PrestigeLitige"%>
<%@page import="com.asc.prestige2.business.litiges.LitigeService"%>
<%

    String str_LITIGE_ID = "",
            str_REFERENCE = "",
            str_LIBELLE = "", str_DESCRIPTION = "",
            str_TIERS_PAYANT_ID = "", str_CLIENT_NAME = "",
            str_TYPE_LITIGE = "",
            str_ETAT_LITIGE = "",
            str_COMMENTAIRE_LITIGE = "",
            str_LITIGE_CONSEQUENCE = "";


    if(request.getParameter("str_LITIGE_ID") != null){
      str_LITIGE_ID =  request.getParameter("str_LITIGE_ID");
    }else{
      str_LITIGE_ID = "";
    }
    if(request.getParameter("str_REFERENCE") != null){
        str_REFERENCE = request.getParameter("str_REFERENCE");
    }else{
        str_REFERENCE = "";
    }
    if(request.getParameter("str_LIBELLE") != null){
        str_LIBELLE = request.getParameter("str_LIBELLE");
    }else{
       str_LIBELLE = "";
    }
    if(request.getParameter("str_DESCRIPTION") != null){
        str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
    }else{
        str_DESCRIPTION = "";
    }
    if(request.getParameter("str_TIERS_PAYANT_ID") != null){
        str_TIERS_PAYANT_ID = request.getParameter("str_TIERS_PAYANT_ID");
    }else{
        str_TIERS_PAYANT_ID = "";
    }
    if(request.getParameter("str_CLIENT_NAME") != null){
        str_CLIENT_NAME = request.getParameter("str_CLIENT_NAME");
    }else{
        str_CLIENT_NAME = "";
    }
   
    
    if(request.getParameter("str_TYPE_LITIGE") != null){
        str_TYPE_LITIGE = request.getParameter("str_TYPE_LITIGE");
    }else{
       str_TYPE_LITIGE = "";  
    }
    if(request.getParameter("str_ETAT_LITIGE") != null){
       str_ETAT_LITIGE = request.getParameter("str_ETAT_LITIGE") ;
    }else{
       str_ETAT_LITIGE = "";
    }
    if(request.getParameter("str_COMMENTAIRE_LITIGE") != null){
       str_COMMENTAIRE_LITIGE = request.getParameter("str_COMMENTAIRE_LITIGE");
    }else{
       str_COMMENTAIRE_LITIGE = "";
    }
    if(request.getParameter("str_LITIGE_CONSEQUENCE") != null){
       str_LITIGE_CONSEQUENCE = request.getParameter("str_LITIGE_CONSEQUENCE") ;
    }else{
      str_LITIGE_CONSEQUENCE = "";
    }
    
   LitigeService service = new PrestigeLitige();
   boolean createResult = service.createLitige(str_TYPE_LITIGE ,str_CLIENT_NAME, 
                                         str_REFERENCE, str_TIERS_PAYANT_ID, str_LIBELLE, str_ETAT_LITIGE, str_LITIGE_CONSEQUENCE, 
                                         str_DESCRIPTION, str_COMMENTAIRE_LITIGE);
   
   String result;
   result = "{success:\"" + createResult +  "\"}";
   
   

%>
<%= result %>