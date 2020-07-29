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
<%@page import="dal.TSkin"  %>
<%@page import="java.text.SimpleDateFormat"  %>

<jsp:useBean id="Os_Search_poste" class="services.Search"  scope="session"/>
<jsp:useBean id="Os_Search_poste_data" class="services.ShowDataBean" scope="session" />


<%! Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    String lg_SKIN_ID = "%%", str_RESOURCE = "%%", str_STATUT = "%%", str_DESCRIPTION = "", str_DETAIL_PATH = "%%";
    date key = new date();
    privilege Oprivilege = new privilege();
    json Ojson = new json();
    List<TSkin> lstTSkin = new ArrayList<TSkin>();

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


            if (request.getParameter("lg_SKIN_ID") != null) {
                if (request.getParameter("lg_SKIN_ID").toString().equals("ALL")) {
                    lg_SKIN_ID = "%%";
                } else {
                    lg_SKIN_ID = request.getParameter("lg_SKIN_ID").toString();
                }

            }

            OdataManager.initEntityManager();
            lstTSkin = OdataManager.getEm().createQuery("SELECT t FROM TSkin t WHERE t.lgSKINID LIKE ?1 AND t.strSTATUT LIKE ?2").setParameter(1, lg_SKIN_ID).setParameter(2, Os_Search_poste.getOvalue()).getResultList();

            JSONArray arrayObj = new JSONArray();
            for (int i = 0; i < lstTSkin.size(); i++) {


                try {
                    OdataManager.getEm().refresh(lstTSkin.get(i));
                } catch (Exception er) {
                }

                String Role = "";


                JSONObject json = new JSONObject();
                json.put("lg_SKIN_ID", lstTSkin.get(i).getLgSKINID());
                json.put("str_RESOURCE", lstTSkin.get(i).getStrRESOURCE());
                json.put("str_STATUT", lstTSkin.get(i).getStrSTATUT());
                json.put("str_DESCRIPTION", lstTSkin.get(i).getStrDESCRIPTION());
                json.put("str_DETAIL_PATH", lstTSkin.get(i).getStrDETAILPATH());


                arrayObj.put(json);
            }

%>

<%= arrayObj.toString()%>
