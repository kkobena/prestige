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
    String lg_TYPE_RECETTE_ID = "%%", str_NAME = "%%", str_STATUT = "%%", str_DESCRIPTION = "%%";
    date key = new date();
    Integer int_STOCK_MINIMAL;
    Integer int_PRICE, int_MONTANT;
    Date dt_Validation_Date, dt_Date_Fin_Paris, dt_Course_Date;
    json Ojson = new json();
    List<dal.TSnapShopDalyRecette> lstTSnapShopDalyRecette = new ArrayList<dal.TSnapShopDalyRecette>();

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
            if (request.getParameter("lg_TYPE_RECETTE_ID") != null) {
                if (request.getParameter("lg_TYPE_RECETTE_ID").toString().equals("ALL")) {
                    lg_TYPE_RECETTE_ID = "%%";
                } else {
                    lg_TYPE_RECETTE_ID = request.getParameter("lg_TYPE_RECETTE_ID").toString();
                }

            }

            OdataManager.initEntityManager();
            bll.teller.tellerManagement OtellerManagement = new bll.teller.tellerManagement(OdataManager);
            dal.TParameters OTParameters = OdataManager.getEm().find(dal.TParameters.class, "KEY_TVA_VALUE");


            lstTSnapShopDalyRecette = OdataManager.getEm().createQuery("SELECT t FROM TSnapShopDalyRecette t WHERE t.lgTYPERECETTEID.lgTYPERECETTEID LIKE ?1 ").
                    setParameter(1, lg_TYPE_RECETTE_ID).getResultList();
            new logger().OCategory.info(lstTSnapShopDalyRecette.size());
%>

<%
//Filtre de pagination
            try {
                if (DATA_PER_PAGE > lstTSnapShopDalyRecette.size()) {
                    DATA_PER_PAGE = lstTSnapShopDalyRecette.size();
                }
            } catch (Exception E) {
            }

            int pgInt = pageAsInt - 1;
            int pgInt_Last = pageAsInt - 1;

            if (pgInt == 0) {
                pgInt_Last = DATA_PER_PAGE;
            } else {

                pgInt_Last = (lstTSnapShopDalyRecette.size() - (DATA_PER_PAGE * (pgInt)));
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
                    OdataManager.getEm().refresh(lstTSnapShopDalyRecette.get(i));
                } catch (Exception er) {
                }




                JSONObject json = new JSONObject();

                json.put("lg_ID", lstTSnapShopDalyRecette.get(i).getLgID());
                json.put("lg_TYPE_RECETTE_ID", lstTSnapShopDalyRecette.get(i).getLgTYPERECETTEID().getLgTYPERECETTEID());
                json.put("int_AMOUNT", lstTSnapShopDalyRecette.get(i).getIntAMOUNT());
                json.put("dt_DAY", lstTSnapShopDalyRecette.get(i).getDtDAY());
                json.put("str_STATUT", lstTSnapShopDalyRecette.get(i).getStrSTATUT());
                json.put("int_NUMBER_TRANSACTION", lstTSnapShopDalyRecette.get(i).getIntNUMBERTRANSACTION());

                arrayObj.put(json);
            }
            String result = "({\"total\":\"" + lstTSnapShopDalyRecette.size() + " \",\"results\":" + arrayObj.toString() + "})";

%>

<%= result%>