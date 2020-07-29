/* global Ext */

var url_services_data_utilisateur = '../webservices/sm_user/utilisateur/ws_data.jsp';
var url_services_data_gestcaisse = '../webservices/sm_user/gestcaisse/ws_data.jsp';
var url_services_transaction_gestcaisse = '../webservices/sm_user/caisse/ws_transaction.jsp?mode=';

var Me;
function amountfarmat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

function amountformatbis(val) {
    return amountformat(val) + " F CFA";
}

Ext.define('testextjs.view.sm_user.gestcaisse.GestCaisseManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'gestcaissemanager',
    id: 'gestcaissemanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'Ext.ux.ProgressBarPager'
    ],
    title: 'Gestion Caisse',
    closable: false,
    frame: true,
    initComponent: function() {

        var itemsPerPage = 20;
        Me = this;
        var storeUser = new Ext.data.Store({
            model: 'testextjs.model.Utilisateur',
            proxy: {
                type: 'ajax',
                url: url_services_data_utilisateur,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }
        });

        var store = new Ext.data.Store({
            model: 'testextjs.model.ResumeCaisse',
            proxy: {
                type: 'ajax',
                url: url_services_data_gestcaisse,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        int_TOTAL_PRICE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    flex: 0.7,
                    fieldLabel: 'Total global caisse::',
                    fieldWidth: 120,
                    name: 'int_TOTAL_PRICE',
                    id: 'int_TOTAL_PRICE',
                    renderer: amountformatbis,
                    fieldStyle: "color:blue;",
                    // margin: '0 5 15 15',
                    value: "0"


                });


        /* store.load({
         params: {
         dt_Date_Debut: dt_Date_Debut,
         dt_Date_Fin: dt_Date_Fin
         },
         callback: function() {
         if (store.getCount() === 0) {
         }
         
         }
         });
         */
        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });



        Ext.apply(this, {
            width: '98%',
            height: 580,
            plugins: [this.cellEditing],
            store: store,
            id: 'OGrid',
            columns: [{
                    header: 'lg_CAISSE_ID',
                    dataIndex: 'lg_CAISSE_ID',
                    hidden: true,
                    flex: 1
                            /* editor: {
                             allowBlank: false
                             }*/
                }, {
                    header: 'Nom.Prenom Caissier',
                    dataIndex: 'str_NAME_USER',
                    flex: 1
                            /*editor: {
                             allowBlank: false
                             }*/
                }, {
                    header: 'Fond De Caisse',
                    dataIndex: 'int_SOLDE_MATIN',
                    renderer: amountfarmat,
                    flex: 1
                }, {
                    header: 'Recette',
                    dataIndex: 'int_SOLDE_STRING',
//                    renderer: amountfarmat,
                    flex: 1
                }, {
                    header: 'Total Caisse',
                    dataIndex: 'int_SOLDE_SOIR_STRING',
//                    renderer: amountfarmat,
                    flex: 1
                }, {
                    header: 'Mt Billetage',
                    dataIndex: 'int_AMOUNT_BILLETAGE',
                    renderer: amountfarmat,
                    flex: 1
                },
                
                {
                    header: 'Mt Ecart',
                    dataIndex: 'int_AMOUNT_ECART_BIS',
                    flex: 1,
//                    renderer: amountfarmat,
                    renderer: function (value, metadata, record) {
                        if (record.get('int_AMOUNT_ECART') < 0) {
                            return '<span style="color: red;">' + value + '</span>';
                        } else if (record.get('int_AMOUNT_ECART') > 0) {
                            return '<span style="color: green;">' + value + '</span>';
                        } else {
                            return value;
                        }
                    }
                },
                
                {
                    header: 'Montant Annulé',
                    dataIndex: 'int_AMOUNT_ANNULE',
                    renderer: amountfarmat,
                    flex: 1
                }
                , {
                    header: 'Date Ouverture',
                    dataIndex: 'dt_CREATED',
                    flex: 1
                            /*editor: {
                             allowBlank: false
                             }*/
                }, {
                    header: 'Date Fermeture',
                    dataIndex: 'dt_UPDATED',
                    flex: 1
                            /*editor: {
                             allowBlank: false
                             }*/
                }, {
                    header: 'Statut',
                    dataIndex: 'str_STATUT',
                    flex: 1

                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/book.png',
                            tooltip: 'Valider la cloture',
                            scope: this,
                            handler: this.onValidateClotureClick,
                            getClass: function(value, metadata, record) {
                                if (record.get('etat') == "is_Using" || record.get('etat') == "is_Process") {  //read your condition from the record
                                    return 'x-display-hide'; //affiche l'icone
                                } else {
                                    return 'x-hide-display'; //cache l'icone
                                }
                            }
                        }]


                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Imprimer le billetage de cette caisse',
                            scope: this,
                            handler: this.onPdfClick,
                            getClass: function(value, metadata, record) {
                                if (record.get('etat') == "is_Process") {  //read your condition from the record
                                    return 'x-display-hide'; //affiche l'icone
                                } else {
                                    return 'x-hide-display'; //cache l'icone
                                }
                            }

                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/delete.gif',
                            tooltip: 'Annuler la cl&ocirc;ture de cette caisse',
                            scope: this,
                            handler: this.onRemoveClick,
                            getClass: function(value, metadata, record) {
                                if (record.get('btn_annulation') === false) {
                                    return 'x-hide-display';
                                } else {
                                    if (record.get('etat') == "is_Process") {  //read your condition from the record
                                        return 'x-display-hide'; //affiche l'icone
                                    } else {
                                        return 'x-hide-display'; //cache l'icone
                                    }
                                }


                            }
                        }]
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    xtype: 'datefield',
                    fieldLabel: 'Du',
                    name: 'dt_debut',
                    id: 'dt_debut',
                    allowBlank: false,
                    submitFormat: 'Y-m-d',
                    maxValue: new Date(),
                    format: 'd/m/Y',
                    listeners: {
                        'change': function(me) {
                            // alert(me.getSubmitValue());
                            // dt_Date_Debut = me.getSubmitValue();
                            Ext.getCmp('dt_fin').setMinValue(me.getValue());
                        }
                    }
                }, {
                    xtype: 'datefield',
                    fieldLabel: 'Au',
                    name: 'dt_fin',
                    id: 'dt_fin',
                    allowBlank: false,
                    maxValue: new Date(),
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function(me) {
                            // dt_Date_Fin = me.getSubmitValue();
                            Ext.getCmp('dt_debut').setMaxValue(me.getValue());
                        }
                    }
                }, {
                    xtype: 'combobox',
                    fieldLabel: 'Utilisateur',
                    name: 'lg_USER_ID',
                    id: 'lg_USER_ID',
                    store: storeUser,
                    valueField: 'lg_USER_ID',
                    displayField: 'str_FIRST_LAST_NAME',
                    typeAhead: true,
                    queryMode: 'remote',
                    emptyText: 'Choisir un utilisateur...',listeners: {
                        select: function(cmp) {
                            Me.onRechClick();

                        }
                    }
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    scope: this,
                    handler: this.onRechClick
                },
                {
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'printable',
//                    glyph: 0xf1c1,
                    listeners: {
                        click: function() {

                            var dt_start_vente = Ext.getCmp('dt_debut').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_fin').getSubmitValue();
                            var search_value = Ext.getCmp('lg_USER_ID').getValue();
                            if (search_value === null) {
                                search_value = "";

                            }
                            var linkUrl = "../webservices/sm_user/gestcaisse/ws_gestioncaisse_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente + "&search_value=" + search_value;
                            window.open(linkUrl);

                        }
                    }


                }



            ],
            bbar: {
                dock: 'bottom',
                items: [
                    {
                        xtype: 'pagingtoolbar',
                        displayInfo: true,
                        flex: 1,
                        pageSize: itemsPerPage,
                        store: store,
                        listeners: {
                            beforechange: function(page, currentPage) {
                                var myProxy = this.store.getProxy();
                                myProxy.params = {
                                    dt_Date_Debut: '',
                                    dt_Date_Fin: '',
                                    lg_USER_ID: ''
                                };
                                var lg_USER_ID = "";
                                if (Ext.getCmp('lg_USER_ID').getValue() != null) {
                                    lg_USER_ID = Ext.getCmp('lg_USER_ID').getValue();
                                }
                                myProxy.setExtraParam('dt_Date_Debut', Ext.getCmp('dt_debut').getSubmitValue());
                                myProxy.setExtraParam('dt_Date_Fin', Ext.getCmp('dt_fin').getSubmitValue());
                                myProxy.setExtraParam('lg_USER_ID', lg_USER_ID);
                            }

                        }
                    },
                    {
                        xtype: 'tbseparator'
                    },
                    int_TOTAL_PRICE
                ]
            }
        });


        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });

    },
    loadStore: function() {
        Ext.getCmp('OGrid').getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function() {
        Ext.getCmp('int_TOTAL_PRICE').setValue(0);
        if (Ext.getCmp('OGrid').getStore().getCount() > 0) {
            var int_TOTAL = 0;
            Ext.getCmp('OGrid').getStore().each(function(rec) {
                if (rec.get('int_SOLDE_SOIR') != '') {
                    int_TOTAL += parseInt(rec.get('int_SOLDE_SOIR'));
                }

            });

            Ext.getCmp('int_TOTAL_PRICE').setValue(int_TOTAL);
        }

    },
    onValidateClotureClick: function(grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Confirmer la validation de la cloture de la caisse de  ' + grid.getStore().getAt(rowIndex).get('str_NAME_USER'),
                function(btn) {
                    if (btn == 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        // alert(rec.get('lg_CAISSE_ID'));
                        testextjs.app.getController('App').ShowWaitingProcess();
                        Ext.Ajax.request({
                            url: url_services_transaction_gestcaisse + 'validate_cloture',
                            params: {
                                lg_RESUME_CAISSE_ID: rec.get('lg_CAISSE_ID')
                            },
                            success: function(response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success == 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }

                                Ext.MessageBox.alert('Success Message', object.errors);
                                grid.getStore().reload();
                            },
                            failure: function(response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                //  alert(object);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    },
    onPdfClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        var linkUrl = '../webservices/sm_user/gestcaisse/ws_generate_pdf.jsp?lg_CAISSE_ID=' + rec.get('lg_CAISSE_ID');
        testextjs.app.getController('App').onLunchPrinter(linkUrl);

    },
    
 
    onRechClick: function() {
        if (new Date(Ext.getCmp('dt_debut').getSubmitValue()) > new Date(Ext.getCmp('dt_fin').getSubmitValue())) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }

        var lg_USER_ID = "";
        if (Ext.getCmp('lg_USER_ID').getValue() != null) {
            lg_USER_ID = Ext.getCmp('lg_USER_ID').getValue();
        }

        this.getStore().load({
            params: {
                dt_Date_Debut: Ext.getCmp('dt_debut').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_fin').getSubmitValue(),
                lg_USER_ID: lg_USER_ID
            }
        }, url_services_data_gestcaisse);
    },
    onRemoveClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        Ext.MessageBox.confirm('Message',
                'Voulez-Vous annuler la cloture de la caisse de ' + rec.get('str_NAME_USER'),
                function(btn) {
                    if (btn === 'yes') {

                        testextjs.app.getController('App').ShowWaitingProcess();
                        Ext.Ajax.request({
                            timeout: 240000,
                            url: url_services_transaction_gestcaisse + 'rollbackclose',
                            params: {
                                lg_RESUME_CAISSE_ID: rec.get('lg_CAISSE_ID')
                            },
                            success: function(response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success == 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }

                                Ext.MessageBox.alert('Success Message', object.errors);
                                grid.getStore().reload();
                            },
                            failure: function(response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                //  alert(object);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    }

});