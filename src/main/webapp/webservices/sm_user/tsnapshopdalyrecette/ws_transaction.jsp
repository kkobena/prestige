<%@page import="toolkits.utils.logger"  %>
<%@page import="dal.dataManager"  %>
<%@page import="dal.TUser"  %>

<%@page import="bll.bllBase"  %>
<%@page import="java.util.*"  %>
<%@page import="multilangue.Translate"  %>
<%@page import="toolkits.utils.date"  %>
<%@page import="bll.userManagement.privilege"  %>
<%@page import="toolkits.parameters.commonparameter"  %>
<%@page import="java.math.BigInteger"  %>

<%!  String lg_PRODUCT_ITEM_ID = "%%", str_NAME = "%%", str_STATUT = "%%", str_DESCRIPTION = "%%", str_PIC_SMALL;
    Integer int_PRICE;
    Integer int_STOCK_MINIMAL;
    Translate oTranslate = new Translate();
    dataManager OdataManager = new dataManager();
    date key = new date();
//    TProductItem OTProductItem = null;


%>




<%
            if (request.getParameter("lg_PRODUCT_ITEM_ID") != null) {
                lg_PRODUCT_ITEM_ID = request.getParameter("lg_PRODUCT_ITEM_ID");
            }
            if (request.getParameter("str_STATUT") != null) {
                str_STATUT = request.getParameter("str_STATUT");
            }
            if (request.getParameter("str_DESCRIPTION") != null) {
                str_DESCRIPTION = request.getParameter("str_DESCRIPTION");
            }
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();

            bllBase ObllBase = new bllBase();
            ObllBase.setOTUser(OTUser);
            ObllBase.LoadDataManger(OdataManager);
            ObllBase.LoadMultilange(oTranslate);
            ObllBase.setMessage(commonparameter.PROCESS_FAILED);
            new logger().oCategory.info("le mode : " + request.getParameter("mode"));
            //new logger().oCategory.info("ID " + request.getParameter("lg_INSTITUTION_ID"));

            if (request.getParameter("mode") != null) {

                if (request.getParameter("mode").toString().equals("create")) {
                    new logger().oCategory.info("Creation");
                    /*OTProductItem = new dal.TProductItem();
                    OTProductItem.setLgPRODUCTITEMID(key.getComplexId());
                    OTProductItem.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                    OTProductItem.setStrSTATUT(commonparameter.statut_enable);
                    OTProductItem.setStrNAME(request.getParameter("str_NAME").toString());
                    OTProductItem.setIntPRICE(Integer.parseInt(request.getParameter("int_PRICE")));
                    OTProductItem.setIntSTOCKMINIMAL(Integer.parseInt(request.getParameter("int_STOCK_MINIMAL")));
                    OTProductItem.setStrPICBIG("");
                    OTProductItem.setStrPICMIDDLE("");
                    OTProductItem.setStrPICORIGINALE("");
                    OTProductItem.setStrPICSMALL("");*/

                //    ObllBase.persiste(OTProductItem);
                } else if (request.getParameter("mode").toString().equals("update")) {

                    if (request.getParameter("lg_PRODUCT_ITEM_ID").toString().equals("init")) {
                        //new logger().oCategory.info("Creation  TModule " + OTSkin.getLgSKINID() + " StrLabel " + OTSkin.getStrDESCRIPTION());
                    } else {
                        //new logger().oCategory.info("Ref " + request.getParameter("lg_SKIN_ID").toString());
                      /*  OTProductItem = OdataManager.getEm().find(dal.TProductItem.class, request.getParameter("lg_PRODUCT_ITEM_ID").toString());
                        OTProductItem.setStrDESCRIPTION(request.getParameter("str_DESCRIPTION").toString());
                        OTProductItem.setStrSTATUT(commonparameter.statut_enable);
                        OTProductItem.setStrNAME(request.getParameter("str_NAME").toString());
                        OTProductItem.setIntPRICE(Integer.parseInt(request.getParameter("int_PRICE")));
                        OTProductItem.setIntSTOCKMINIMAL(Integer.parseInt(request.getParameter("int_STOCK_MINIMAL")));
                        ObllBase.persiste(OTProductItem);*/
                        //new logger().oCategory.info("Creation  TModule " + OTSkin.getLgSKINID() + " StrLabel " + OTSkin.getStrDESCRIPTION());

                    }
                } else if (request.getParameter("mode").toString().equals("delete")) {

                  /*  OTProductItem = ObllBase.getOdataManager().getEm().find(dal.TProductItem.class, request.getParameter("lg_PRODUCT_ITEM_ID"));

                    OTProductItem.setStrSTATUT(commonparameter.statut_delete);*/
                    ObllBase.persiste(OTUser);

                    new logger().oCategory.info("Suppression de productitem " + request.getParameter("lg_PRODUCT_ITEM_ID").toString());

                } else {
                }

            }

            String result;
            if (ObllBase.getMessage().equals(commonparameter.PROCESS_SUCCESS)) {
                result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";

            } else {
                result = "{success:\"" + ObllBase.getMessage() + "\", errors: \"" + ObllBase.getDetailmessage() + "\"}";
            }
            new logger().OCategory.info("JSON " + result);
%>
<%=result%>