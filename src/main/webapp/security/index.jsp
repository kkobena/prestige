<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>
<%@page import="toolkits.utils.jdom"  %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    jdom.InitRessource();
    jdom.LoadRessource();
    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    if (OTUser != null) {
%>
<script>
     sessionStorage.setItem("user",  '<%=OTUser%>');
    window.location.replace("../general/index.jsp?content=panelInfos.jsp&lng=fr");
</script>
<%
    }
%>
<!doctype html>
<html>
    <head>
        <meta charset="utf8">
        <link rel="shortcut icon" type="image/x-icon" href="../resources/images/favicon.ico" /> 
      <!-- version standart //--> 
      <link rel="shortcut icon" type="image/png" href="../resources/images/favicon.png" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title><%= jdom.APP_NAME%> :: Ver <%= jdom.APP_VERSION%></title>
        <link rel="stylesheet" href="../resources/css/style.css" />
        <script src="../lib/jquery-1.4.2.js"></script>
        <script src="../app/controler.js"></script>
        <script src="../app/login.js"></script>
        <script src="../app/toolkits.js"></script>
        <script src="../app/static_data.js"></script>
        <script src="../lib/modernizr-1.5.min.js"></script>
    </head>

    <body>
        <div id="bar">
            <div id="container">
                <!-- Login Starts Here -->
                <div id="loginContainer">
                   
                    <form id="loginForm">
                        <fieldset id="body">
                            <fieldset>
                                <label for="str_login">Identifiant</label>
                                <input type="text" name="str_login" id="str_login" autofocus="autofocus"/>
                            </fieldset>
                            <fieldset>
                                <label for="str_password">Mot de passe</label>
                                <input type="password" name="str_password" id="str_password" />
                            </fieldset>
                            <input type="button" id="login" name="login" value="Connexion"  /> 
                            <span ><img src="../resources/images/gears.gif" style="height: 15%;width: 15%;display: none;" id="loader"/></span>

                            <!--   <label for="checkbox"><input type="checkbox" id="checkbox" />Se souvenirde moi</label>-->
                        </fieldset>
                        <!--    <span><a href="#">Mot de passe oublié ?</a></span> -->
                    </form>


                    <!--
                    
                                                <a href="#" id="loginButton"><span>Connexion</span></a>
                                        <div style="clear:both"></div>
                    
                    
                                        <div id="loginBox">
                    -->

                </div>
            </div>
            <!-- Login Ends Here -->
        </div>
   


    <!-- debut Image au minieux -->
    <div id="imageintro">

    </div>
    <!-- debut Image au minieux -->
</body>
</html>