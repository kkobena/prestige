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


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_TYPE_DEPENSE_ID = "%%", str_TYPE_DEPENSE = "%%", str_NUMERO_COMPTE = "%%", lg_Participant_ID = "%%";
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TTypeDepense> lstTTypeDepense = new ArrayList<dal.TTypeDepense>();
    
%>

<%
            int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
            new logger().OCategory.info("dans ws data");
%>


<!-- logic de gestion des page -->
<%


            String action = request.getParameter("action"); //get parameter ?action=
            int pageAsInt = 0;

            try {
                if ((action != null) && action.equals("filltable")) {
                } else {

                    String p = request.getParameter("start"); // get paramerer ?page=

                    if (p != null) {
                        int int_page = new Integer(p).intValue();
                        int_page = (int_page / DATA_PER_PAGE) + 1;
                        p = new Integer(int_page).toString();

                        // Strip quotation marks

                        StringBuffer buffer = new StringBuffer();
                        for (int index = 0; index < p.length(); index++) {
                            char c = p.charAt(index);
                            if (c != '\\') {
                                buffer.append(c);
                            }
                        }
                        p = buffer.toString();
                        Integer intTemp = new Integer(p);

                        pageAsInt = intTemp.intValue();

                    } else {
                        pageAsInt = 1;
                    }


                }
            } catch (Exception E) {
            }




%>
<!-- fin logic de gestion des page -->

<%
             if (request.getParameter("search_value") != null) {
                Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
                new logger().OCategory.info("Search book " + request.getParameter("search_value"));
            }else{
                Os_Search_poste.setOvalue("%%");
                }


            new logger().OCategory.info("search_value  = " + request.getParameter("search_value"));
            if (request.getParameter("lg_TYPE_DEPENSE_ID") != null) {
                if (request.getParameter("lg_TYPE_DEPENSE_ID").toString().equals("ALL")) {
                    lg_TYPE_DEPENSE_ID = "%%";
                } else {
                    lg_TYPE_DEPENSE_ID = request.getParameter("lg_TYPE_DEPENSE_ID").toString();
                }

            }  

            OdataManager.initEntityManager();
            lstTTypeDepense = OdataManager.getEm().createQuery("SELECT t FROM TTypeDepense t WHERE t.lgTYPEDEPENSEID LIKE ?1 AND t.strTYPEDEPENSE LIKE ?2  ").
                    setParameter(1, lg_TYPE_DEPENSE_ID).setParameter(2,  Os_Search_poste.getOvalue()).getResultList();
            new logger().OCategory.info(lstTTypeDepense.size());
%>

<%
//Filtre de pagination
            try {
                if (DATA_PER_PAGE > lstTTypeDepense.size()) {
                    DATA_PER_PAGE = lstTTypeDepense.size();
                }
            } catch (Exception E) {
            }

            int pgInt = pageAsInt - 1;
            int pgInt_Last = pageAsInt - 1;

            if (pgInt == 0) {
                pgInt_Last = DATA_PER_PAGE;
            } else {

                pgInt_Last = (lstTTypeDepense.size() - (DATA_PER_PAGE * (pgInt)));
                pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
                if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
                    pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
                }
                pgInt = ((DATA_PER_PAGE) * (pgInt));
            }

%>


<%
            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgInt_Last; i++) {
                 try {
                  OdataManager.getEm().refresh(lstTTypeDepense.get(i));
                } catch (Exception er) {
                }

              
               

                JSONObject json = new JSONObject();

                json.put("lg_TYPE_DEPENSE_ID", lstTTypeDepense.get(i).getLgTYPEDEPENSEID());
                json.put("str_TYPE_DEPENSE", lstTTypeDepense.get(i).getStrTYPEDEPENSE());
                json.put("str_NUMERO_COMPTE", lstTTypeDepense.get(i).getStrNUMEROCOMPTE() );
               
                arrayObj.put(json);
            }

             String result = "({\"total\":\"" + lstTTypeDepense.size() + " \",\"results\":" + arrayObj.toString() + "})";
          
%>

<%=result%>