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
    String lg_SOUS_MENU_ID = "%%", str_DESCRIPTION = "%%", str_VALUE = "%%", lg_Participant_ID = "%%", str_COMPOSANT = "%%", lg_MENU_ID = "%%", Str_Status = "%%", Str_URL = "%%", P_Key = "%%";
    date key = new date();
    Integer int_PRIORITY;
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TSousMenu> lstTSousMenu = new ArrayList<dal.TSousMenu>();

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
            } else {
                Os_Search_poste.setOvalue("%%");
            }


            new logger().OCategory.info("search_value  = " + request.getParameter("search_value"));
            if (request.getParameter("lg_SOUS_MENU_ID") != null) {
                if (request.getParameter("lg_SOUS_MENU_ID").toString().equals("ALL")) {
                    lg_SOUS_MENU_ID = "%%";
                } else {
                    lg_SOUS_MENU_ID = request.getParameter("lg_SOUS_MENU_ID").toString();
                }

            }

            OdataManager.initEntityManager();
            lstTSousMenu = OdataManager.getEm().createQuery("SELECT t FROM TSousMenu t WHERE t.lgSOUSMENUID LIKE ?1 AND t.strVALUE LIKE ?2 AND t.strStatus LIKE ?3  ").
                    setParameter(1, lg_SOUS_MENU_ID)
                    .setParameter(2, Os_Search_poste.getOvalue())
                    .setParameter(3, commonparameter.statut_enable).getResultList();
            new logger().OCategory.info(lstTSousMenu.size());
%>

<%
//Filtre de pagination
            try {
                if (DATA_PER_PAGE > lstTSousMenu.size()) {
                    DATA_PER_PAGE = lstTSousMenu.size();
                }
            } catch (Exception E) {
            }

            int pgInt = pageAsInt - 1;
            int pgInt_Last = pageAsInt - 1;

            if (pgInt == 0) {
                pgInt_Last = DATA_PER_PAGE;
            } else {

                pgInt_Last = (lstTSousMenu.size() - (DATA_PER_PAGE * (pgInt)));
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
                    OdataManager.getEm().refresh(lstTSousMenu.get(i));
                } catch (Exception er) {
                }


                String Role = "";

                JSONObject json = new JSONObject();

                json.put("lg_SOUS_MENU_ID", lstTSousMenu.get(i).getLgSOUSMENUID());
                json.put("str_VALUE", lstTSousMenu.get(i).getStrVALUE());
                json.put("str_DESCRIPTION", lstTSousMenu.get(i).getStrDESCRIPTION());
                json.put("str_COMPOSANT", lstTSousMenu.get(i).getStrCOMPOSANT());
                json.put("P_Key", lstTSousMenu.get(i).getPKey());
                json.put("Str_URL", lstTSousMenu.get(i).getStrURL());
                json.put("Str_Status", lstTSousMenu.get(i).getStrStatus());
                json.put("lg_MENU_ID", lstTSousMenu.get(i).getLgMENUID().getStrDESCRIPTION());
                json.put("int_PRIORITY", lstTSousMenu.get(i).getIntPRIORITY());

                arrayObj.put(json);
            }
String result = "({\"total\":\"" + lstTSousMenu.size() + " \",\"results\":" + arrayObj.toString() + "})";
%>

<%= result%>