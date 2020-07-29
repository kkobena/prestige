var url_services_transaction_detailsvente_ventecloturee = '../webservices/sm_user/detailsvente/ws_transaction.jsp?mode=';

var Oview;
var Omode;
var Me;
var ref;


Ext.define('testextjs.view.sm_user.vente.action.add', {
    extend: 'Ext.window.Window',
    xtype: 'updatevente',
    id: 'updateventeID',
    requires: [
        'Ext.form.*',
        'Ext.window.Window'

    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: ''
    },
    initComponent: function() {

        Oview = this.getParentview();
        Omode = this.getMode();
        Me = this;
        var itemsPerPage = 20;
        var url_services_data_tierspayant = '../webservices/sm_user/vente/ws_data_tierspayant.jsp?lg_PREENREGISTREMENT_ID=' + this.getOdatasource().lg_PREENREGISTREMENT_ID;
        var store = new Ext.data.Store({
            model: 'testextjs.model.TiersPayant',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_tierspayant,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        var form = new Ext.form.Panel({
            bodyPadding: 10,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 115,
                msgTarget: 'side'
            },
            items: [{
                    xtype: 'fieldset',
                    title: 'Information sur la vente',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: 'REF. VENTE',
                            name: 'str_REF',
                            fieldStyle: "color:blue;",
                            id: 'str_REF'
                        }, 
                        {
                            xtype: 'displayfield',
                            fieldLabel: 'CLIENT',
                            fieldStyle: "color:blue;",
                            name: 'lg_USER_VENDEUR_ID',
                            id: 'lg_USER_VENDEUR_ID'
                        },
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Tiers.Payant',
                            name: 'lg_TIERS_PAYANT_VENTE_ID',
                            id: 'lg_TIERS_PAYANT_VENTE_ID',
                            store: store,
                            valueField: 'lg_TIERS_PAYANT_ID',
                            displayField: 'str_CODE_ORGANISME',
                            typeAhead: true,
                            queryMode: 'remote',
                            emptyText: 'Choisir un tiers payant...',
                            listeners: {
                                select: function(cmp) {
                                    var value = cmp.getValue();
                                    var record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné
                                    Ext.getCmp('str_REF_BON').setValue(record.get('str_NAME'));
                                    Ext.getCmp('str_REF_BON_BIS').setValue(record.get('str_NAME'));
                                    Ext.getCmp('lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID').setValue(record.get('lg_COMPTE_CLIENT_TIERS_PAYANT_ID'));
                                    
                                }
                            }
                        }, 
                        {
                            xtype: 'displayfield',
                            fieldLabel: 'BON',
                            fieldStyle: "color:blue;",
                            name: 'str_REF_BON',
                            id: 'str_REF_BON'
                        },
                        {
                            fieldLabel: 'NOUVEAU BON',
                            emptyText: 'Nouvelle reference du bon',
                            name: 'str_REF_BON_BIS',
                            id: 'str_REF_BON_BIS',
                            allowBlank: false
                        },
                        {
                            xtype: 'hiddenfield',
                            name: 'lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID',
                            id: 'lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID'
                        }
                    ]
                }]
        });
        //Initialisation des valeur
        if (Omode === "update") {

            ref = this.getOdatasource().lg_PREENREGISTREMENT_ID;
            Ext.getCmp('str_REF').setValue(this.getOdatasource().str_REF);
//            Ext.getCmp('str_REF_BON').setValue(this.getOdatasource().str_REF_BON);
            Ext.getCmp('lg_USER_VENDEUR_ID').setValue(this.getOdatasource().lg_USER_VENDEUR_ID);
//            Ext.getCmp('str_REF_BON_BIS').setValue(this.getOdatasource().str_REF_BON);
        }



        var win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            width: 500,
            height: 300,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'Enregistrer',
                    handler: this.onbtnsave
                }, {
                    text: 'Annuler',
                    handler: function() {
                        win.close();
                    }
                }]
        });

    },
    onbtnsave: function(button) {
        var fenetre = button.up('window'),
                formulaire = fenetre.down('form');

        if (formulaire.isValid()) {
            testextjs.app.getController('App').ShowWaitingProcess();
            Ext.Ajax.request({
                url: url_services_transaction_detailsvente_ventecloturee + "updatebon",
                params: {
//                    lg_PREENREGISTREMENT_ID: ref, //a decommenter en cas de probleme 22/11/2016
                    lg_PREENREGISTREMENT_ID: Ext.getCmp('lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID').getValue(),
                    str_REF_BON: Ext.getCmp('str_REF_BON_BIS').getValue()
                },
                success: function(response)
                {
                    testextjs.app.getController('App').StopWaitingProcess();
                    var object = Ext.JSON.decode(response.responseText, false);

                    if (object.success == "0") {
                        Ext.MessageBox.alert('Error Message', object.errors);
                        return;
                    } else {
                        Ext.MessageBox.alert('Confirmation', object.errors);
                        fenetre.close();
                        Me_Workflow = Oview;
                        Me_Workflow.onRechClick();
                    }

                },
                failure: function(response)
                {
                    testextjs.app.getController('App').StopWaitingProcess();
                    var object = Ext.JSON.decode(response.responseText, false);
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);

                }
            });
        } else {
            Ext.MessageBox.alert('Echec', 'Formulaire non valide');
        }
    }
});