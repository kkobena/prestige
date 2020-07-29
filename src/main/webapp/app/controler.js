function Doconnexion(str_LOGIN, Str_PASSWORD) {
    //  alert(str_LOGIN);
    $("#loader").show();

    jQuery.get("../webservices/usermanagement/ws_login.jsp?str_LOGIN=" + str_LOGIN + "&Str_PASSWORD=" + Str_PASSWORD, function(json, textStatus) {
        // alert(json);
        var obj = jQuery.parseJSON(json);
        if (obj[0].code_statut == "1") {
            if (Modernizr.localstorage) {
//                alert(obj[0].lg_EMPLACEMENT_ID);
                var xtypeuser = obj[0].xtypeuser;
                localStorage.setItem("xtypeuser", xtypeuser);
                localStorage.setItem("OFFICINE", obj[0].OFFICINE);
                localStorage.setItem("str_PIC", obj[0].str_PIC);
                localStorage.setItem("lg_EMPLACEMENT_ID", obj[0].lg_EMPLACEMENT_ID);
                
                window.location.replace("../general/");
            }

        //    window.location.replace("../general/");
        } else {
            alert(obj[0].desc_statut);

        }
        $("#loader").hide();
    });

}


