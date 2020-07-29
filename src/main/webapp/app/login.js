// Login Form

$(function () {

    $('#str_login').val('');
    $('#str_password').val('');


    $('#login').click(function (e) {

        var str_login = $('#str_login').val();
        var str_password = $('#str_password').val();
        Doconnexion(str_login, str_password);
    });

    $("#str_password").keypress(function (e) {
        var key = e.which;

        if (key == 13) {
            var str_login = $('#str_login').val();
            var str_password = $('#str_password').val();
            Doconnexion(str_login, str_password);
        }

    });


    var button = $('#loginButton');
    var box = $('#loginBox');
    var form = $('#loginForm');
    button.removeAttr('href');
    button.mouseup(function (login) {
        box.toggle();
        button.toggleClass('active');
    });
    form.mouseup(function () {
        return false;
    });
    $(this).mouseup(function (login) {
        if (!($(login.target).parent('#loginButton').length > 0)) {
            button.removeClass('active');
            box.hide();
        }
    });
});
