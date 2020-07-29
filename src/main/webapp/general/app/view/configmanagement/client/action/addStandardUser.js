/* global Ext */
var lg_USER_ID;
Ext.define('testextjs.view.configmanagement.client.action.addStandardUser', {
    extend: 'Ext.window.Window',
    alias: 'widget.standarduser-form',
    id: 'standarduserform',
    autoShow: true,
    height: 240,
    width: 450,
    layout: {
        type: 'fit'
    },
    title: "Ajout d'un client standard",
    closable: true,
    modal: true,
    items: [
        {
            xtype: 'form',
            id: 'standardform',
            bodyPadding: 5,
            modelValidation: true,
            layout: {
                type: 'fit'
                        // align: 'stretch'
            },
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Information sur le client',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%',
                        xtype: 'textfield',
                        msgTarget: 'side',
                        labelAlign: 'right',
                        labelWidth: 115
                    },
                    items: [
                        {
                            fieldLabel: 'Nom',
                            emptyText: 'Nom',
                            name: 'str_FIRST_NAME',
                           
                            allowBlank: false,
                            enableKeyEvents: true,
                            listeners: {
                                afterrender: function (field, e, options) {

                                    field.focus(false,true);

                                },
                                specialKey: function (field, e, options) {

                                    if (e.getKey() === e.ENTER) {
                                        if (field.getValue().length > 0)
                                            Ext.getCmp('strLASTNAME').focus();

                                    }
                                }

                            }
                        }, {
                            fieldLabel: 'Pr&eacute;nom',
                            emptyText: 'Prenom',
                            name: 'str_LAST_NAME',
                            id: 'strLASTNAME',
                            allowBlank: false,
                            enableKeyEvents: true,
                            listeners: {

                                specialKey: function (field, e, options) {

                                    if (e.getKey() === e.ENTER) {
                                        if (field.getValue().length > 0)
                                            Ext.getCmp('strADRESSE').focus();

                                    }
                                }

                            }
                        },
                        {
                            fieldLabel: 'T&eacute;l&eacute;phone',
                            emptyText: 'Téléphone',
                            name: 'str_ADRESSE',
                            id: 'strADRESSE',
                            regex: /[0-9.]/,
                            allowBlank: false
                        },
                        {
                            xtype: "radiogroup",
                            fieldLabel: "Genre",
                            vertical: true,
                            items: [
                                {boxLabel: 'F&eacute;minin', name: 'str_SEXE', inputValue: 'F'},
                                {boxLabel: 'Masculin', name: 'str_SEXE', inputValue: 'M'}
                            ]
                        }

                    ]
                }

            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            layout: {
                pack: 'end',
                type: 'hbox'
            },
            items: [
                {
                    xtype: 'button',
                    text: 'Enregistrer',
                    handler: function () {
                        var form = Ext.getCmp('standardform');
                        if (form && form.isValid()) {
                            form.submit({
                                url: '../webservices/configmanagement/client/ws_transaction.jsp?mode=create&lg_TYPE_CLIENT_ID=6',
                                type: 'ajax',
                                success: function (form, action) {
                                    var object = Ext.JSON.decode(action.response.responseText);
                                    if (object.success == 1) { //xc
                                        var form = Ext.getCmp('standarduserform');
                                        form.close();
                                        var OCustomer = object.results[0];

                                        Ext.getCmp('lg_COMPTE_CLIENT_ID').setValue(OCustomer.str_cust_compte_id);
                                        Ext.getCmp('str_FIRST_NAME').setValue(OCustomer.str_first_name);
                                        Ext.getCmp('str_LAST_NAME').setValue(OCustomer.str_last_name);
                                        Ext.getCmp('lg_CLIENT_ID_FIND').setValue(OCustomer.str_cust_id);
                                        Ext.getCmp('lg_CLIENT_ID').setValue(Ext.getCmp('str_FIRST_NAME').getValue() + " " + Ext.getCmp('str_LAST_NAME').getValue());
                                        Ext.getCmp('lg_CLIENT_ID').disable();

                                        Ext.getCmp('authorize_cloture_vente').setValue('0');
                                        Ext.getCmp('btn_loturer').disable();
                                    } else {
                                        Ext.Msg.alert('Error', "Echec de cr&eacute;ation du client");

                                    }

                                },
                                failure: function (form, action) {

                                }
                            });
                        } else {

                        }
                    }
                },
                {
                    xtype: 'button',
                    text: 'Annuler',
                    listeners: {
                        click: function () {
                            var form = Ext.getCmp('standarduserform');
                            form.close();

                        }
                    }
                }
            ]
        }
    ]


});