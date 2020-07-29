<%-- 
    Document   : index
    Created on : 9 oct. 2013, 10:46:35
    Author     : user
--%>
<%@page import="toolkits.utils.jdom"  %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
            jdom.InitRessource();
            jdom.LoadRessource();

%>
<!doctype html>
<html>
    <head>
        <meta charset="utf8">
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
                    <a href="#" id="loginButton"><span>Connexion</span><em></em></a>
                    <div style="clear:both"></div>
                    <div id="loginBox">
                        <form id="loginForm">
                            <fieldset id="body">
                                <fieldset>
                                    <label for="str_login">Identifiant</label>
                                    <input type="text" name="str_login" id="str_login" />
                                </fieldset>
                                <fieldset>
                                    <label for="str_password">Mot de passe</label>
                                    <input type="password" name="str_password" id="str_password" />
                                </fieldset>
                                <input type="button" id="login" name="login" value="Se connecter"  />
                                <label for="checkbox"><input type="checkbox" id="checkbox" />Se souvenir de moi</label>
                            </fieldset>
                            <span><a href="#">Mot de passe oublié?</a></span>
                        </form>
                    </div>
                </div>
                <!-- Login Ends Here -->
            </div>
        </div>


        <!-- debut Image au minieux -->
        <div id="imageintro">

        </div>
        <!-- debut Image au minieux -->
    </body>
</html>