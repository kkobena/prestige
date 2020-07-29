/* global Ext */

var OTUser;
//var intAmount = 0;  f 

var lg_ROLE_ID_REF = 0;
var url_services_data_myaccount = '../webservices/sm_user/myaccount/ws_data.jsp';
var url_services_transaction_myaccount = '../webservices/sm_user/myaccount/ws_transaction.jsp?mode=';
var Me;


Ext.define('testextjs.view.sm_user.myaccount.MyaccountManager', {
    extend: 'Ext.form.Panel',
    xtype: 'myaccountmanager',
    id: 'myaccountmanagerID',
    frame: true,
    title: 'Mon compte',
    closable: true,
    bodyPadding: 10,
    autoScroll: true,
    width: 355,
    fieldDefaults: {
        labelAlign: 'right',
        labelWidth: 115,
        msgTarget: 'side'
    },
    initComponent: function() {

        Me = this;

        var str_FIRST_NAME = new Ext.form.field.Text(
                {
                    xtype: 'textfield',
                    allowBlank: false,
                    fieldLabel: 'Nom',
                    id: 'str_first_name',
                    name: 'str_FIRST_NAME',
                    emptyText: 'Nom'
                });

        var str_LAST_NAME = new Ext.form.field.Text(
                {
                    xtype: 'textfield',
                    allowBlank: false,
                    fieldLabel: 'Prenom',
                    id: 'str_last_name',
                    name: 'str_LAST_NAME',
                    emptyText: 'Prenom'
                });

        var str_STATUT = new Ext.form.field.Hidden(
                {
                    xtype: 'hiddenfield',
                    allowBlank: false,
                    fieldLabel: 'str_STATUT',
                    name: 'str_STATUT',
                    id: str_STATUT,
                    emptyText: 'str_STATUT'
                });

        var lg_ROLE_ID = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Role',
                    name: 'lg_ROLE_ID',
                    id: lg_ROLE_ID
                            //emptyText: 'lg_ROLE_ID'
                });


        var str_LOGIN = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Login',
                    name: 'str_LOGIN',
                    id: str_LOGIN
                });

        var str_LAST_CONNECTION_DATE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Derniere.Con',
                    name: 'str_LAST_CONNECTION_DATE'
                            //emptyText: 'str_LAST_CONNECTION_DATE'
                });

        var str_PASSWORD = new Ext.form.field.Text(
                {
                    xtype: 'textfield',
                    allowBlank: false,
                    fieldLabel: 'Mot de passe',
                    name: 'str_PASSWORD',
                    id: 'str_password',
                    emptyText: 'Mot de passe',
                    inputType: 'password'
                });

        var str_PASSWORD_CONF = new Ext.form.field.Text(
                {
                    xtype: 'textfield',
                    allowBlank: false,
                    fieldLabel: 'Confirm.Mot.Passe',
                    name: 'str_PASSWORD_CONF',
                    id: 'str_password_conf',
                    emptyText: 'Confirmation mot de passse',
                    inputType: 'password'
                });

        var lg_USER_ID = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Ref.Utilisateur',
                    name: 'lg_USER_ID',
                    id: 'lg_user_id',
                    emptyText: 'lg_USER_ID'
                });

        var store = new Ext.data.Store({
            model: 'testextjs.model.Utilisateur',
            proxy: {
                type: 'ajax',
                url: url_services_data_myaccount
            }
        });


        store.load({
            callback: function() {
                OTUser = store.getAt(0);
                str_LOGIN.setValue(OTUser.get('str_LOGIN'));
                lg_USER_ID.setValue(OTUser.get('lg_USER_ID'));
                str_FIRST_NAME.setValue(OTUser.get('str_FIRST_NAME'));
                str_LAST_NAME.setValue(OTUser.get('str_LAST_NAME'));
                str_LAST_CONNECTION_DATE.setValue(OTUser.get('str_LAST_CONNECTION_DATE'));
                str_STATUT.setValue(OTUser.get('str_STATUT'));
                lg_ROLE_ID.setValue(OTUser.get('lg_ROLE_ID'));
                lg_ROLE_ID_REF = lg_ROLE_ID.getValue();
            }
        });


        this.items = [{
                xtype: 'fieldset',
                title: 'Infos.Utilisateur',
                defaultType: 'textfield',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    str_FIRST_NAME,
                    str_LAST_NAME,
                    str_LOGIN

                ]
            },
            {
                xtype: 'fieldset',
                title: 'Detail.Infos.Utilisateur',
                defaultType: 'textfield',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    lg_ROLE_ID,
                    str_PASSWORD,
                    str_PASSWORD_CONF,
                    str_LAST_CONNECTION_DATE,
                    lg_USER_ID,
                    str_STATUT
                ]
            }];

        this.callParent();
    },
    buttons: [
        {
            text: 'Enregistrer',
            id: 'btn_savemyaccountID',
            name: 'btn_savemyaccount',
            /*disabled: true,
             formBind: true,*/
            handler: function() {
                Me.onbtnsave();
            }

        }],
    onbtnsave: function() {

        var pass1 = Ext.getCmp('str_password').getValue();
        var pass2 = Ext.getCmp('str_password_conf').getValue();
        var str_FIRST_NAME = Ext.getCmp('str_first_name').getValue();
        var str_LAST_NAME = Ext.getCmp('str_last_name').getValue();
        var lg_USER_ID = Ext.getCmp('lg_user_id').getValue();
        var internal_url = url_services_transaction_myaccount + 'update';
        var message = "";
//        alert("debut"+pass1+"fin");
        if (pass1 !== null && pass1 !== "") {
            var nbCaractere = pass1.length;
            if (pass1 === pass2 && (nbCaractere > 8 || nbCaractere === 8)) {
                Ext.Ajax.request({
                    url: internal_url,
                    params: {
                        str_LAST_NAME: str_LAST_NAME,
                        str_FIRST_NAME: str_FIRST_NAME,
                        str_PASSWORD: pass1,
                        lg_USER_ID: lg_USER_ID
                    },
                    success: function(response)
                    {
                        var object = Ext.JSON.decode(response.responseText, false);
                        if (object.success === "0") {
//                            Ext.MessageBox.alert('Error Message', object.errors);
                            Ext.MessageBox.show({
                                title: 'Message d\'erreur',
                                width: 320,
                                msg: object.errors,
                                buttons: Ext.MessageBox.OK,
                                icon: Ext.MessageBox.WARNING
                            });
                            return;
                        }
                        Ext.MessageBox.alert('Confirmation', object.errors);
                  //      testextjs.app.getController('App').onLoadNewComponent("mainmenumanager", "", "");
                        testextjs.app.getController('App').onLoadNewComponent(xtypeload, "", "");

                        //Oview.getStore().reload();

                    },
                    failure: function(response)
                    {

                        var object = Ext.JSON.decode(response.responseText, false);
                        console.log("Bug " + response.responseText);
                        Ext.MessageBox.alert('Error Message', response.responseText);
                        Ext.MessageBox.show({
                            title: 'Message d\'erreur',
                            width: 320,
                            msg: response.responseText,
                            buttons: Ext.MessageBox.OK,
                            icon: Ext.MessageBox.WARNING
                        });

                    }
                });
            } else {
                if (pass1 < 8) {
                    message = "Le mot de passe doit avoir au minimum 8 caracteres";
                }

                if (pass1 !== pass2) {
                    message = "Mot de passe differents";
                }
                Ext.MessageBox.show({
                    title: 'Message d\'erreur',
                    width: 320,
                    msg: message,
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.WARNING
                });
            }
        } else {
            Ext.Ajax.request({
                url: internal_url,
                params: {
                    str_LAST_NAME: str_LAST_NAME,
                    str_FIRST_NAME: str_FIRST_NAME
                },
                success: function(response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    if (object.success === "0") {

                        Ext.MessageBox.show({
                            title: 'Message d\'erreur',
                            width: 320,
                            msg: object.errors,
                            buttons: Ext.MessageBox.OK,
                            icon: Ext.MessageBox.WARNING
                        });
                        return;
                    }
                    Ext.MessageBox.alert('Confirmation', object.errors);
                  //  testextjs.app.getController('App').onLoadNewComponent("mainmenumanager", "", "");
testextjs.app.getController('App').onLoadNewComponent(xtypeload, "", "");

                },
                failure: function(response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);

                }
            });
        }
    }
});
