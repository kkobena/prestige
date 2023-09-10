/* global Ext */

var url_services_data_entreestock = '../webservices/sm_user/entreestock/ws_data.jsp';
var url_services_transaction_entreestock = '../webservices/sm_user/entreestock/ws_transaction.jsp?mode=';
var url_services_data_typeetiquette = '../webservices/configmanagement/typeetiquette/ws_data.jsp';

var Oview;
var Omode;
var Me;
var ref;
var lg_GROSSISTE_ID;
var str_REF_LIVRAISON;
var lg_BON_LIVRAISON_DETAIL;
var str_SORTIE_USINE;
var str_PEREMPTION;
var MANQUANT, int_QTE_CMDE;
var checkExpirationdate;
var DISPLAYFILTER;
var index;

Ext.define('testextjs.view.stockmanagement.etatstock.action.add', {
    extend: 'Ext.window.Window',
    xtype: 'addStock',
    id: 'addStockID',
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
        reference: '',
        index: ''
    },
    initComponent: function () {

        Oview = this.getParentview();
        Omode = this.getMode();

        str_REF_LIVRAISON = this.getReference();
        checkExpirationdate = this.getOdatasource().checkExpirationdate;
        DISPLAYFILTER = this.getOdatasource().DISPLAYFILTER;
        int_QTE_CMDE = this.getOdatasource().int_QTE_CMDE;
        index = this.getIndex();

        if (!checkExpirationdate) {

            int_QTE_CMDE = 1;
        }


        Me = this;
        var itemsPerPage = 20;

        var store_etiquette = new Ext.data.Store({
            model: 'testextjs.model.Typeetiquette',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_typeetiquette,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


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
                    title: 'Information prise en compte produit',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [

                        {
                            fieldLabel: 'Quantite',
                            emptyText: 'Quantite',
                            name: 'int_NUMBER',
                            id: 'int_NUMBER',
                            xtype: 'numberfield',
                            readOnly: DISPLAYFILTER,
                            allowBlank: checkExpirationdate,
                            regex: /[0-9.]/,
                            minValue: int_QTE_CMDE

                        },
                        {
                            fieldLabel: 'Quantite gratuite',
                            emptyText: 'Quantite gratuite',
                            name: 'int_QUANTITE_FREE',
                            allowBlank: false,
                            id: 'int_QUANTITE_FREE',
                            xtype: 'numberfield',
                            regex: /[0-9.]/,
                            minValue: 0,
                            value: 0
                        }, {
                            xtype: 'datefield',
                            fieldLabel: 'Date fabrication',
                            name: 'str_SORTIE_USINE',
                            maxValue: new Date(),
                            id: 'str_SORTIE_USINE',
                            submitFormat: 'd/m/Y',
//                            allowBlank: false,
                            listeners: {
                                'change': function (me) {
                                    str_SORTIE_USINE = me.getSubmitValue();
                                }
                            }
                        }, {
                            xtype: 'datefield',
                            fieldLabel: 'Date peremption',
                            name: 'str_PEREMPTION',
                            id: 'str_PEREMPTION',
                            submitFormat: 'Y-m-d',
                            format: 'd/m/Y',
                            minValue: new Date(),
                            hidden: DISPLAYFILTER,
                            allowBlank: checkExpirationdate,
                            listeners: {
                                'change': function (me) {

                                    str_PEREMPTION = me.getSubmitValue();
                                }
                            }
                        },
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Type etiquette',
                            name: 'lg_TYPEETIQUETTE_ID',
                            id: 'lg_TYPEETIQUETTE_ID',
                            store: store_etiquette,
                            valueField: 'lg_TYPEETIQUETTE_ID',
                            displayField: 'str_DESCRIPTION',
                            typeAhead: true,
                            queryMode: 'remote',
                            emptyText: 'Choisir un type d\'etiquette...'
                        },
                        {
                            fieldLabel: 'Reference Lot',
                            emptyText: 'Reference Lot',
                            name: 'int_NUM_LOT',
                            allowBlank: checkExpirationdate,
                            hidden: DISPLAYFILTER,
                            id: 'int_NUM_LOT'
                        }
                    ]
                }
            ]
        });



        //Initialisation des valeur 


        if (Omode === "create") {
            ref = this.getOdatasource().lg_FAMILLE_ID;
            lg_GROSSISTE_ID = this.getOdatasource().lg_GROSSISTE_ID;
            lg_BON_LIVRAISON_DETAIL = this.getOdatasource().lg_BON_LIVRAISON_DETAIL;
            MANQUANT = this.getOdatasource().int_QTE_MANQUANT;

            if (!checkExpirationdate) {
                Ext.getCmp('int_NUMBER').setValue(MANQUANT);
            } else {
                Ext.getCmp('int_NUMBER').setValue(this.getOdatasource().int_QTE_CMDE);

            }

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
                    handler: function () {
                        win.close();
                    }
                }]
        });

    },
    onbtnsave: function (button) {
        Me_Workflow = Oview;
        var fenetre = button.up('window'),
                formulaire = fenetre.down('form');
        var internal_url = "";

        var Qte_cmde = MANQUANT;
        var Qte_livre = Ext.getCmp('int_NUMBER').getValue();


        Ext.MessageBox.confirm('Message',
                'Confirmer la saisie',
                function (btn) {

                    if (btn === 'yes') {

                        if (Qte_livre < Qte_cmde) {

                            Ext.MessageBox.confirm('Message',
                                    'La quantite livree ' + Qte_livre + '<br> est inferieur a celle commandee ' + Qte_cmde,
                                    function (btn) {

                                        if (btn === 'yes') {

                                            if (formulaire.isValid()) {
                                                if (Ext.getCmp('int_NUMBER').getValue() < 0 || Ext.getCmp('int_QUANTITE_FREE').getValue() < 0) {
                                                    Ext.MessageBox.alert('Error Message', 'Quantite inferieure a 0');
                                                    return;
                                                }
                                                if (Omode === "create") {
                                                    internal_url = url_services_transaction_entreestock + 'create';

                                                    button.setDisabled(true);
                                                    Ext.Ajax.request({
                                                        url: internal_url,
                                                        params: {
                                                            // lg_FAMILLE_ID: ref,
                                                            int_QUANTITE_FREE: Ext.getCmp('int_QUANTITE_FREE').getValue(),
                                                            int_NUMBER: Ext.getCmp('int_NUMBER').getValue(),
                                                            //  lg_GROSSISTE_ID: lg_GROSSISTE_ID,
                                                            //                    lg_GROSSISTE_ID: Ext.getCmp('lg_GROSSISTE_ADDSTOCK_ID').getValue(),
                                                            str_REF_LIVRAISON: str_REF_LIVRAISON,
                                                            // str_REF_LIVRAISON: Ext.getCmp('str_REF_LIVRAISON').getValue(),
                                                            str_SORTIE_USINE: str_SORTIE_USINE,
                                                            str_PEREMPTION: Ext.getCmp('str_PEREMPTION').getSubmitValue(),
                                                            lg_TYPEETIQUETTE_ID: Ext.getCmp('lg_TYPEETIQUETTE_ID').getValue(),
                                                            // str_REF_ORDER: Ext.getCmp('str_REF_ORDER').getValue(),
                                                            lg_BON_LIVRAISON_DETAIL: lg_BON_LIVRAISON_DETAIL,
                                                            int_NUM_LOT: Ext.getCmp('int_NUM_LOT').getValue()
                                                        },
                                                        success: function (response)
                                                        {
                                                            button.enable();
                                                            var object = Ext.JSON.decode(response.responseText, false);

                                                            if (object.success == '0') {
                                                                Ext.MessageBox.alert('Error Message', object.errors);
                                                                return;
                                                            } else if (object.success == '2') {
                                                                Ext.MessageBox.alert('Confirmation', object.errors);
                                                                var gridpanelID = Ext.getCmp('gridpanelID');
                                                                return;
                                                            }
//                                                             var rec = Ext.getCmp('gridpanelID').getStore().getAt(Number(index));
//                                                            rec.commit();
                                                            Ext.getCmp('gridpanelID').getStore().load();
                                                            var bouton = button.up('window');
                                                            bouton.close();

                                                        },
                                                        failure: function (response)
                                                        {
                                                            button.enable();
                                                            var object = Ext.JSON.decode(response.responseText, false);
                                                            console.log("Bug " + response.responseText);
                                                            Ext.MessageBox.alert('Error Message', response.responseText);

                                                        }
                                                    });
                                                }
                                            } else {
                                                button.enable();
                                                Ext.MessageBox.alert('Echec', 'Formulaire non valide. Verifiez votre saisie');
                                                return;
                                            }


                                        }
                                    });

                        } else if (Qte_livre > Qte_cmde) {

                            /*  Ext.MessageBox.confirm('Message',
                             'La quantite livree ' + Qte_livre + '<br> est superieur a celle commandee ' + Qte_cmde,
                             function (btn) {
                             
                             if (btn === 'yes') {
                             // ****************
                             
                             
                             }
                             });*/

                            if (formulaire.isValid()) {
                                if (Ext.getCmp('int_NUMBER').getValue() < 0 || Ext.getCmp('int_QUANTITE_FREE').getValue() < 0) {
                                    Ext.MessageBox.alert('Error Message', 'Quantite inferieure a 0');
                                    return;
                                }
                                if (Omode === "create") {
                                    button.setDisabled(true);
                                    internal_url = url_services_transaction_entreestock + 'create';
                                    //alert("str_PEREMPTION " + str_PEREMPTION + " **** str_SORTIE_USINE " + str_SORTIE_USINE);
                                    Ext.Ajax.request({
                                        url: internal_url,
                                        params: {
                                            // lg_FAMILLE_ID: ref,
                                            int_QUANTITE_FREE: Ext.getCmp('int_QUANTITE_FREE').getValue(),
                                            int_NUMBER: Ext.getCmp('int_NUMBER').getValue(),
                                            //  lg_GROSSISTE_ID: lg_GROSSISTE_ID,
                                            //                    lg_GROSSISTE_ID: Ext.getCmp('lg_GROSSISTE_ADDSTOCK_ID').getValue(),
                                            str_REF_LIVRAISON: str_REF_LIVRAISON,
                                            // str_REF_LIVRAISON: Ext.getCmp('str_REF_LIVRAISON').getValue(),
                                            str_SORTIE_USINE: str_SORTIE_USINE,
                                            str_PEREMPTION: Ext.getCmp('str_PEREMPTION').getSubmitValue(),
                                            lg_TYPEETIQUETTE_ID: Ext.getCmp('lg_TYPEETIQUETTE_ID').getValue(),
                                            // str_REF_ORDER: Ext.getCmp('str_REF_ORDER').getValue(),
                                            lg_BON_LIVRAISON_DETAIL: lg_BON_LIVRAISON_DETAIL,
                                            int_NUM_LOT: Ext.getCmp('int_NUM_LOT').getValue()
                                        },
                                        success: function (response)
                                        {
                                            button.enable();
                                            var object = Ext.JSON.decode(response.responseText, false);
                                        
                                            if (object.success == '0') {
                                                Ext.MessageBox.alert('Error Message', object.errors);
                                                return;
                                            } else if (object.success == '2') {
                                                Ext.MessageBox.alert('Confirmation', object.errors);
                                                var gridpanelID = Ext.getCmp('gridpanelID');
                                                return;
                                            } else {

                                                var gridpanelID = Ext.getCmp('gridpanelID');
                                                gridpanelID.getStore().load();
                                             
                                            }
                                            var bouton = button.up('window');
                                            bouton.close();

                                        },
                                        failure: function (response)
                                        {
                                            button.enable();

                                            var object = Ext.JSON.decode(response.responseText, false);
                                            console.log("Bug " + response.responseText);
                                            Ext.MessageBox.alert('Error Message', response.responseText);

                                        }
                                    });
                                }
                            } else {
                                button.enable();
                                Ext.MessageBox.alert('Echec', 'Formulaire non valide. Verifiez votre saisie');
                                return;
                            }









                        } else {
                            if (formulaire.isValid()) {
                                if (Ext.getCmp('int_NUMBER').getValue() < 0 || Ext.getCmp('int_QUANTITE_FREE').getValue() < 0) {
                                    Ext.MessageBox.alert('Error Message', 'Quantite inferieure a 0');
                                    return;
                                }
                                if (Omode === "create") {
                                    internal_url = url_services_transaction_entreestock + 'create';

                                    button.setDisabled(true);
                                    Ext.Ajax.request({
                                        url: internal_url,
                                        params: {
                                            // lg_FAMILLE_ID: ref,
                                            int_QUANTITE_FREE: Ext.getCmp('int_QUANTITE_FREE').getValue(),
                                            int_NUMBER: Ext.getCmp('int_NUMBER').getValue(),
                                            //  lg_GROSSISTE_ID: lg_GROSSISTE_ID,
                                            //                    lg_GROSSISTE_ID: Ext.getCmp('lg_GROSSISTE_ADDSTOCK_ID').getValue(),
                                            str_REF_LIVRAISON: str_REF_LIVRAISON,
                                            // str_REF_LIVRAISON: Ext.getCmp('str_REF_LIVRAISON').getValue(),
                                            str_SORTIE_USINE: str_SORTIE_USINE,
                                            str_PEREMPTION: Ext.getCmp('str_PEREMPTION').getSubmitValue(),
                                            lg_TYPEETIQUETTE_ID: Ext.getCmp('lg_TYPEETIQUETTE_ID').getValue(),
                                            // str_REF_ORDER: Ext.getCmp('str_REF_ORDER').getValue(),
                                            lg_BON_LIVRAISON_DETAIL: lg_BON_LIVRAISON_DETAIL,
                                            int_NUM_LOT: Ext.getCmp('int_NUM_LOT').getValue()
                                        },
                                        success: function (response)
                                        {
                                            button.enable();

                                            var object = Ext.JSON.decode(response.responseText, false);
                                            // alert(object.success);
                                            if (object.success == '0') {
                                                Ext.MessageBox.alert('Error Message', object.errors);
                                                return;
                                            } else if (object.success == '2') {
                                                Ext.MessageBox.alert('Confirmation', object.errors);
                                                var gridpanelID = Ext.getCmp('gridpanelID');
                                                return;
                                            } else {
//                                                 var rec = Ext.getCmp('gridpanelID').getStore().getAt(Number(index));
//                                                            rec.commit();

                                                var gridpanelID = Ext.getCmp('gridpanelID');
                                                gridpanelID.getStore().load();
                                            }
                                            var bouton = button.up('window');
                                            bouton.close();

                                        },
                                        failure: function (response)
                                        {
                                            button.enable();
                                            var object = Ext.JSON.decode(response.responseText, false);
                                            console.log("Bug " + response.responseText);
                                            Ext.MessageBox.alert('Error Message', response.responseText);

                                        }
                                    });
                                }
                            } else {
                                button.enable();
                                Ext.MessageBox.alert('Echec', 'Formulaire non valide. Verifiez votre saisie');
                                return;
                            }
                        }

                    }
                });

    }
});
