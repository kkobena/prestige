var url_services_transaction_entreestock = '../webservices/sm_user/entreestock/ws_transaction.jsp?mode=';

var Oview;
var Omode;
var Me;
var ref;
var lg_BON_LIVRAISON_DETAIL;


Ext.define('testextjs.view.stockmanagement.etatstock.action.removeLot', {
    extend: 'Ext.window.Window',
    xtype: 'removeStock',
    id: 'removeStockID',
    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'testextjs.view.commandemanagement.cmde_passees.action.livraison'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        reference: ''
    },
    initComponent: function() {

        Oview = this.getParentview();
        Omode = this.getMode();

        Me = this;


        var form = new Ext.form.Panel({
            bodyPadding: 10,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 115,
                msgTarget: 'side'
            },
            items: [{
                    xtype: 'fieldset',
                    title: 'Information de lot &agrave; r&eacute;tirer',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            fieldLabel: 'Reference Lot',
                            emptyText: 'Reference Lot',
                            name: 'int_NUM_LOT',
                            allowBlank: false,
                            id: 'int_NUM_LOT'
                        }
                    ]
                }
            ]
        });



        //Initialisation des valeur 


        if (Omode === "remove") {
            lg_BON_LIVRAISON_DETAIL = this.getOdatasource().lg_BON_LIVRAISON_DETAIL;
        }



        var win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            width: 500,
            height: 150,
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
        Me_Workflow = Oview;
        var fenetre = button.up('window'),
                formulaire = fenetre.down('form');
        
        testextjs.app.getController('App').ShowWaitingProcess();
        Ext.MessageBox.confirm('Message',
                'Confirmer la suppression du lot ' + Ext.getCmp('int_NUM_LOT').getValue(),
                function(btn) {
                    if (btn === 'yes') {
                        if (formulaire.isValid()) {
                            
                            Ext.Ajax.request({
                                url: url_services_transaction_entreestock + Omode,
                                params: {
                                    lg_BON_LIVRAISON_DETAIL: lg_BON_LIVRAISON_DETAIL,
                                    int_NUM_LOT: Ext.getCmp('int_NUM_LOT').getValue()
                                },
                                success: function(response)
                                {
                                    testextjs.app.getController('App').StopWaitingProcess();
                                    var object = Ext.JSON.decode(response.responseText, false);
                                    if (object.success == 0) {
                                        Ext.MessageBox.alert('Error Message', object.errors);
                                        return;
                                    } else {
                                        var gridpanelID = Ext.getCmp('gridpanelID');
                                        gridpanelID.getStore().reload();
                                    }
                                    var bouton = button.up('window');
                                    bouton.close();

                                },
                                failure: function(response)
                                {
                                    testextjs.app.getController('App').StopWaitingProcess();
                                    var object = Ext.JSON.decode(response.responseText, false);
                                    console.log("Bug " + response.responseText);
                                    Ext.MessageBox.alert('Error Message', response.responseText);

                                }
                            });

                        }
                        else {
                            Ext.MessageBox.alert('Echec', 'Formulaire non valide. Verifiez votre saisie');
                            return;
                        }


                    }
                });

    }
});
