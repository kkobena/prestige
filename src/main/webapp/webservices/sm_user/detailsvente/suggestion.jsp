
<%@page import="bll.commandeManagement.SuggestionManager"%>

<%@page import="dal.dataManager"%>


<%! 

int success=0;
String message="Erreur de suggestion";
%>

<%
   
    dataManager OdataManager = new dataManager();
    OdataManager.initEntityManager();
    String lg_PREENREGISTREMENT_ID="";
 if (request.getParameter("lg_PREENREGISTREMENT_ID") != null) {
        lg_PREENREGISTREMENT_ID = request.getParameter("lg_PREENREGISTREMENT_ID");
       
    }
 
          SuggestionManager sm=new SuggestionManager(OdataManager);
            if(sm.createsellsuggestion(lg_PREENREGISTREMENT_ID)){
                success=1;
                message="Suggestion effectuée ";
            }
            

%>





<%    

    String result;
  
 result = "{results:" + success + ", success: \"" + success + "\", errors: \"" + message + "\"}";
   
%>
<%=result%>