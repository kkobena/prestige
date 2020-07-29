<%-- 
    Document   : index
    Created on : 7 avr. 2016, 11:40:03
    Author     : KKOFFI
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@page import="dal.dataManager"  %>
        <%@page import="dal.TUser"  %>
        <%@page import="java.util.*"  %>
        <%@page import="multilangue.Translate"  %>
        <%@page import="toolkits.utils.jdom"  %>
        <%@page import="dal.TPrivilege"  %>
        <%@page import="bll.userManagement.privilege"  %>

        <%@page import="toolkits.parameters.commonparameter"%>
        <%@page import="bll.entity.EntityData"%>

        <%   Translate OTranslate = new Translate();
            dataManager OdataManager = new dataManager();

            privilege Oprivilege = new privilege();
            TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
            OdataManager.initEntityManager();
            Oprivilege.LoadDataManger(OdataManager);
            Oprivilege.LoadMultilange(OTranslate);
            List<EntityData> Menudatas = Oprivilege.getAllMenuByUser(OTUser.getLgUSERID());

        %>
        <title>UBI-PRESTIGE</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
        <meta content="" name="description" />
        <meta content="" name="author" />
        <!--<script src="assets/plugins/jquery-1.8.3.min.js" type="text/javascript"></script> -->
        <script type="text/javascript" src="../../resources/boostrap/bb/js/jquery.min.js"></script>

        <script src="../../resources/boostrap/bb/js/bootstrap.min.js" type="text/javascript"></script> 


        <link rel="stylesheet" type="text/css" href="../../resources/boostrap/bb/css/bootstrap.css"/>
        <link rel="stylesheet" type="text/css" href="../../resources/boostrap/bb/css/bootstrap-theme.min.css"/>
        <link href="../../resources/font-awesome-4.5.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <link href="assets/css/animate.min.css" rel="stylesheet" type="text/css"/>
        <!-- END CORE CSS FRAMEWORK -->
        <!-- BEGIN CSS TEMPLATE -->
        <link href="assets/css/style.css" rel="stylesheet" type="text/css"/>
        <link href="assets/css/responsive.css" rel="stylesheet" type="text/css"/>
        <link href="assets/css/custom-icon-set.css" rel="stylesheet" type="text/css"/>
        <link href="assets/css/magic_space.css" rel="stylesheet" type="text/css"/>
        <link href="assets/css/tiles_responsive.css" rel="stylesheet" type="text/css"/>
        <!-- END CSS TEMPLATE -->
        <!-- WINDOWS 8 TILES STYLE -->
        <link rel="stylesheet" href="assets/css/metro-styles.css" />

    </head>
    <body class="body-bg" style="background-color: #E5E9EC !important; ">

        <div class="demo-wrapper" id="metro-sub-menu"> 


            <!-- DEBUT - AFFICHAGE MENU METRO PRINCIPALE --> 

            <div class="dashboard clearfix">
                <div class="row">
                    <%                        for (int i = 0; i < Menudatas.size(); i++) {
                            if (i == 4) {
                                break;
                            }
                    %>

                    <div class="col-sm-3 tile tile-big tile-<%=i + 1%> slideTextUp" id="menu-service-client" onClick="AppelerGestionClientele('<%=Menudatas.get(i).getStr_value2()%>');"  style="width: 24%;margin-right: 1%;">
                        <div class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%=Menudatas.get(i).getStr_value1()%></p>
                        </div>
                        <div  class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%= Menudatas.get(i).getStr_value1()%></p>

                        </div>
                    </div>  


                    <%

                        }

                    %>

                </div>


                <div class="row">
                    <%                        for (int i = 4; i < Menudatas.size(); i++) {
                            if (i == 8) {
                                break;
                            }
                    %>

                    <div class="col-sm-3 tile tile-big tile-<%=i + 1%> slideTextUp" id="menu-service-client" onClick="AppelerGestionClientele('<%=Menudatas.get(i).getStr_value2()%>');" style="width: 24%;margin-right: 1%;">
                        <div class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%=Menudatas.get(i).getStr_value1()%></p>
                        </div>
                        <div  class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%= Menudatas.get(i).getStr_value1()%></p>

                        </div>
                    </div>  


                    <%

                        }

                    %>

                </div> 



                <div class="row">
                    <%                        for (int i = 8; i < Menudatas.size(); i++) {
                            if (i == 12) {
                                break;
                            }
                    %>

                    <div class="col-sm-3 tile tile-big tile-<%=i + 1%> slideTextUp" id="menu-service-client" onClick="AppelerGestionClientele('<%=Menudatas.get(i).getStr_value2()%>');" style="width: 24%;margin-right: 1%;">
                        <div class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%=Menudatas.get(i).getStr_value1()%></p>
                        </div>
                        <div  class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%= Menudatas.get(i).getStr_value1()%></p>

                        </div>
                    </div>  


                    <%

                        }

                    %>

                </div>

                <div class="row">
                    <%                        for (int i = 12; i < Menudatas.size(); i++) {
                            if (i == 16) {
                                break;
                            }
                    %>

                    <div class="col-sm-3 tile tile-big tile-<%=i + 1%> slideTextUp" id="menu-service-client" onClick="AppelerGestionClientele('<%=Menudatas.get(i).getStr_value2()%>');" style="width: 24%;margin-right: 1%;">
                        <div class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%=Menudatas.get(i).getStr_value1()%></p>
                        </div>
                        <div  class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%= Menudatas.get(i).getStr_value1()%></p>

                        </div>
                    </div>  


                    <%

                        }

                    %>

                </div> 

                <div class="row">
                    <%                        for (int i = 16; i < Menudatas.size(); i++) {
                            if (i == 20) {
                                break;
                            }
                    %>

                    <div class="col-sm-3 tile tile-big tile-<%=i + 1%> slideTextUp" id="menu-service-client" onClick="AppelerGestionClientele('<%=Menudatas.get(i).getStr_value2()%>');" style="width: 24%;margin-right: 1%;">
                        <div class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%=Menudatas.get(i).getStr_value1()%></p>
                        </div>
                        <div  class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%= Menudatas.get(i).getStr_value1()%></p>

                        </div>
                    </div>  


                    <%

                        }

                    %>

                </div> 


                <div class="row">
                    <%                        for (int i = 16; i < Menudatas.size(); i++) {
                            if (i == 19) {
                                break;
                            }
                    %>

                    <div class="col-sm-3 tile tile-big tile-<%=i + 1%> slideTextUp" id="menu-service-client" onClick="AppelerGestionClientele('<%=Menudatas.get(i).getStr_value2()%>');" style="width: 24%;margin-right: 1%;">
                        <div class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%=Menudatas.get(i).getStr_value1()%></p>
                        </div>
                        <div  class="panel">
                            <p><i class="<%=Menudatas.get(i).getStr_value3()%> text-white text-right fa-lg"></i><%= Menudatas.get(i).getStr_value1()%></p>

                        </div>
                    </div>  


                    <%

                        }

                    %>

                </div> 


            </div>


        </div>




        <!-- WINDOWS 8 TILES --> 
        <!-- CHARGEMENT DU SOUS MENU STYLE METRO --> 
        <script type="text/javascript">


            function AppelerGestionClientele(lg_MENU_ID) {
                $("#metro-sub-menu").load("view/sousmenus.jsp?lg_MENU_ID=" + lg_MENU_ID);
            }
            function ReloadIframe() {

                window.location.reload();

            }
            ;








        </script> 
 
    </body>
</html>
