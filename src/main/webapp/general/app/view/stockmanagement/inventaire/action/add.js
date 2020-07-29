/* global Ext */

var url_services_transaction_inventaire = '../webservices/stockmanagement/inventaire/ws_transactions.jsp?mode=';

var Oview;
var Omode;
var Me;
var ref;
Ext.define('testextjs.view.stockmanagement.inventaire.action.add', {
    extend: 'Ext.window.Window',
    xtype: 'addinventaire',
    id: 'addinventaireID',
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

        var form = new Ext.form.Panel({
            bodyPadding: 10,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 115,
                msgTarget: 'side'
            },
            items: [{
                    xtype: 'fieldset',
                    title: 'Information ouverture fiche inventaire',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            fieldLabel: 'Libelle',
                            emptyText: 'Libelle',
                            name: 'str_NAME',
                            id: 'str_NAME'
                        },
                        {
                            fieldLabel: 'Commentaire',
                            emptyText: 'Commentaire',
                            name: 'str_DESCRIPTION',
                            id: 'str_DESCRIPTION'
                        }
                    ]
                }
            ]
        });
        var win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            width: 500,
            height: 250,
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
        var internal_url = "";
        var str_TYPE_TRANSACTION = "UNITAIRE";

        if (formulaire.isValid()) {
            internal_url = url_services_transaction_inventaire + 'create';
            formulaire.submit({
                url:"../webservices/stockmanagement/inventaire/ws_transactions.jsp?mode=createbis",
                timeout: 180000,
                method: 'POST',
                params: {
                    str_NAME: Ext.getCmp('str_NAME').getValue(),
                    str_DESCRIPTION: Ext.getCmp('str_DESCRIPTION').getValue(),
                    str_TYPE_TRANSACTION: str_TYPE_TRANSACTION,
                    bool_INVENTAIRE: 0
                },
                waitMsg: 'Veuillez patienter. Traitement en cours...',
                success: function(formulaire, action) {
                   
                  Ext.MessageBox.alert('Infos',  action.result.nombre);
                    Oview.getStore().reload();

                    var bouton = button.up('window');
                    bouton.close();
                },
                failure: function(formulaire, action) {
                     var bouton = button.up('window');
                    bouton.close();
                    Oview.getStore().reload();
                    Ext.MessageBox.alert('Erreur', 'Erreur  ' + action.result.nombre);
                }
            });

        } else {
            Ext.MessageBox.alert('Echec', 'Form non valide');
        }

       
    }
});
