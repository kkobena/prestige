<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>
<%@page import="dal.TRole"  %>
<%@page import="dal.TRoleUser"  %>
<%@page import="bll.userManagement.user"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="toolkits.web.json"  %>
<%@page import=" org.json.JSONObject"  %>
<%@page import="org.json.JSONArray"  %>
<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.TLanguage"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String  lg_Language_ID="%%", str_Local_Cty = "%%", str_Local_Lg = "%%", str_Code = "", str_Description = "%%";

    date key = new date();
    privilege Oprivilege = new privilege();
    json Ojson = new json();
    List<TLanguage> lstTLanguage = new ArrayList<TLanguage>();

%>



<%



            new logger().OCategory.info("dans ws data utilisateur");

            if (request.getParameter("search_value") != null) {
                Os_Search_poste.setOvalue("%" + request.getParameter("search_value") + "%");
                new logger().OCategory.info("Search book " + request.getParameter("search_value"));
            } else {
                Os_Search_poste.setOvalue("%%");
            }
            new logger().OCategory.info("search_value  = " + request.getParameter("search_value"));


            if (request.getParameter("lg_Language_ID") != null) {
                if (request.getParameter("lg_Language_ID").toString().equals("ALL")) {
                    lg_Language_ID = "%%";
                } else {
                    lg_Language_ID = request.getParameter("lg_Language_ID").toString();
                }

            }

            OdataManager.initEntityManager();
            lstTLanguage = OdataManager.getEm().createQuery("SELECT t FROM TLanguage t WHERE t.lgLanguageID LIKE ?1 ").setParameter(1, lg_Language_ID).getResultList();

            JSONArray arrayObj = new JSONArray();
            for (int i = 0; i < lstTLanguage.size(); i++) {
                 try {
                     OdataManager.getEm().refresh(lstTLanguage.get(i));
                } catch (Exception er) {
                }
                
                String Role = "";


                JSONObject json = new JSONObject();
                json.put("lg_Language_ID", lstTLanguage.get(i).getLgLanguageID());
                json.put("str_Local_Cty", lstTLanguage.get(i).getStrLocalCty());
                json.put("str_Local_Lg", lstTLanguage.get(i).getStrLocalLg());
                json.put("str_Code", lstTLanguage.get(i).getStrCode());
                json.put("str_Description", lstTLanguage.get(i).getStrDescription());


                arrayObj.put(json);
            }

%>

<%= arrayObj.toString()%>
