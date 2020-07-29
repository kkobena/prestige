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
    String lg_ORDER_ID = "%%", lg_TABLE_ID = "%%", str_REF = "%%", lg_Participant_ID = "%%", lg_USER_ID = "%%", str_STATUT = "%%";
    Integer int_PRICE;
    date key = new date();
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date, dt_CREATED, dt_VALIDATED;
    json Ojson = new json();
    List<dal.TNotification> lstTNotification = new ArrayList<dal.TNotification>();

%>

<%
    
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);


    OdataManager.initEntityManager();


    //OTUser = OdataManager.getEm().find(TUser.class, OTUser.getLgUSERID());
            int DATA_PER_PAGE = jdom.int_size_pagination, count = 0, pages_curr = 0;
           // new logger().OCategory.info("dans ws data notifiv");
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
               // new logger().OCategory.info("Search book " + request.getParameter("search_value"));
            } else {
                Os_Search_poste.setOvalue("%%");
            }


         //   new logger().OCategory.info("search_value  = " + Os_Search_poste.getOvalue());
            if (request.getParameter("lg_ORDER_ID") != null) {
                if (request.getParameter("lg_ORDER_ID").toString().equals("ALL")) {
                    lg_ORDER_ID = "%%";
                } else {
                    lg_ORDER_ID = request.getParameter("lg_ORDER_ID").toString();
                }

            }

// TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);

            OdataManager.initEntityManager();
            lstTNotification = OdataManager.getEm().createQuery("SELECT t FROM TNotification t WHERE t.lgUSERIDOUT.lgUSERID LIKE ?1 AND t.strSTATUT LIKE ?2 ORDER BY t.dtCREATED DESC").
                    setParameter(1, OTUser.getLgUSERID()).
                    setParameter(2, commonparameter.statut_UnRead).getResultList();
          //  new logger().OCategory.info(lstTNotification.size());
%>

<%
//Filtre de pagination
            try {
                if (DATA_PER_PAGE > lstTNotification.size()) {
                    DATA_PER_PAGE = lstTNotification.size();
                }
            } catch (Exception E) {
            }

            int pgInt = pageAsInt - 1;
            int pgInt_Last = pageAsInt - 1;

            if (pgInt == 0) {
                pgInt_Last = DATA_PER_PAGE;
            } else {

                pgInt_Last = (lstTNotification.size() - (DATA_PER_PAGE * (pgInt)));
                pgInt_Last = (DATA_PER_PAGE * (pgInt) + pgInt_Last);
                if (pgInt_Last > (DATA_PER_PAGE * (pgInt + 1))) {
                    pgInt_Last = DATA_PER_PAGE * (pgInt + 1);
                }
                pgInt = ((DATA_PER_PAGE) * (pgInt));
            }

%>


<%

//             OTUser = (dal.TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();

            JSONArray arrayObj = new JSONArray();
            for (int i = pgInt; i < pgInt_Last; i++) {
                try {
                    OdataManager.getEm().refresh(lstTNotification.get(i));
                } catch (Exception er) {
                }
                


                String Role = "";

                JSONObject json = new JSONObject();

                json.put("lg_ID", lstTNotification.get(i).getLgID());
                json.put("str_CONTENT", lstTNotification.get(i).getStrCONTENT());
                json.put("str_REF_RESSOURCE", lstTNotification.get(i).getStrREFRESSOURCE());
                json.put("lg_USER_ID", lstTNotification.get(i).getLgUSERIDIN().getStrFIRSTNAME());
                json.put("str_STATUT", oTranslate.getValue(lstTNotification.get(i).getStrSTATUT()));
                json.put("str_TYPE", oTranslate.getValue(lstTNotification.get(i).getStrTYPE()));
                json.put("dt_CREATED", date.DateToString(lstTNotification.get(i).getDtCREATED(), date.formatterMysql));

                arrayObj.put(json);
            }   

            String result = "({\"total\":\"" + lstTNotification.size() + " \",\"results\":" + arrayObj.toString() + "})";

%>

<%=result%>