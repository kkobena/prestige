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
    String lg_MODULE_ID = "%%", str_DESCRIPTION = "%%", str_VALUE = "%%", lg_Participant_ID = "%%";
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TModule> lstTModule = new ArrayList<dal.TModule>();

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
            if (request.getParameter("lg_MODULE_ID") != null) {
                if (request.getParameter("lg_MODULE_ID").toString().equals("ALL")) {
                    lg_MODULE_ID = "%%";
                } else {
                    lg_MODULE_ID = request.getParameter("lg_MODULE_ID").toString();
                }

            }

            OdataManager.initEntityManager();
            lstTModule = OdataManager.getEm().createQuery("SELECT t FROM TModule t WHERE t.lgMODULEID LIKE ?1 AND t.strVALUE LIKE ?2  ").
                    setParameter(1, lg_MODULE_ID).setParameter(2, Os_Search_poste.getOvalue()).getResultList();
            new logger().OCategory.info(lstTModule.size());
%>

<%
//Filtre de pagination
            try {
                if (DATA_PER_PAGE > lstTModule.size()) {
                    DATA_PER_PAGE = lstTModule.size();
                }
            } catch (Exception E) {
            }

            int pgInt = pageAsInt - 1;
            int pgInt_Last = pageAsInt - 1;

            if (pgInt == 0) {
                pgInt_Last = DATA_PER_PAGE;
            } else {

                pgInt_Last = (lstTModule.size() - (DATA_PER_PAGE * (pgInt)));
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
                        OdataManager.getEm().refresh(lstTModule.get(i));
                } catch (Exception er) {
                }

              
                String Role = "";

                JSONObject json = new JSONObject();

                json.put("lg_MODULE_ID", lstTModule.get(i).getLgMODULEID());
                json.put("str_VALUE", lstTModule.get(i).getStrVALUE());
                json.put("str_DESCRIPTION", lstTModule.get(i).getStrDESCRIPTION());

                arrayObj.put(json);
            }

%>

<%= arrayObj.toString()%>