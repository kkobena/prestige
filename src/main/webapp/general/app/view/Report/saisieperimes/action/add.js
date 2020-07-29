
/* global Ext */


var Me;

var int_montant_achat;
var LaborexWorkFlow_facture;
var listProductSelected;
var myAppController;
var uncheckedList;
var lg_CLIENT_ID = "";
lg_TYPE_TIERS_PAYANT_ID = "";

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.Report.saisieperimes.action.add', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.controller.LaborexWorkFlow',
        'Ext.ux.CheckColumn',
        'Ext.selection.CheckboxModel',
        'testextjs.model.articleSearchModel',
        'testextjs.model.perimesModel'

    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        plain: true,
        maximizable: true,
        closable: false,
        nameintern: ''
        //  headerPosition :'top'
    },
    xtype: 'addPerimer',
    id: 'addPerimerID',
    frame: true,
    title: 'Ajout de produit p&eacute;rim&eacute;s',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function () {

        Me = this;
        var itemsPerPage = 20;

        ref = this.getNameintern();
        titre = this.getTitre();
        ref = this.getNameintern();
        LaborexWorkFlow_facture = Ext.create('testextjs.controller.LaborexWorkFlow', {});
        myAppController = Ext.create('testextjs.controller.App', {});


        var articlestore = new Ext.data.Store({
            model: 'testextjs.model.articleSearchModel',
            pageSize: 20,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/Report/saisieperimes/ws_article_list.jsp',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });
        var pendingStore = new Ext.data.Store({
            model: 'testextjs.model.perimesModel',
            pageSize: 20,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: '../webservices/Report/saisieperimes/ws_data_pending.jsp',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });



        Ext.apply(this, {
            width: '98%',
//            height: 540,
            cls: 'custompanel',
            fieldDefaults: {
                labelAlign: 'left',

                anchor: '100%'

            },
            layout: {
                type: 'vbox',
                align: 'stretch',
                padding: 0
            },
            defaults: {
                flex: 1
            },
            id: 'panelArticlePerimes',
            items: [
                {
                    xtype: 'container',
                    border: false,
                    items: [{
                            xtype: 'fieldset',

                            collapsible: false,
                            padding: '3 15 3 15',
                            defaultType: 'textfield',
                            layout: 'anchor',
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'container',
                                    layout: 'hbox',

                                    items: [

                                        {
                                            xtype: 'combobox',

                                            fieldLabel: 'Article',
                                            flex: 1.5,
                                            margin: '0 15 0 0',
                                            labelWidth: 50,
                                            id: 'cmb_Article',
                                            store: articlestore,
                                            pageSize: 20,
                                            valueField: 'lg_FAMILLE_ID',
                                            displayField: 'str_NAME',
                                            typeAhead: true,
                                            queryMode: 'remote',
                                            minChars: 3,
                                            emptyText: 'Selectionnez un article',
                                            enableKeyEvents: true,
                                            listConfig: {
                                                loadingText: 'Recherche...',
                                                emptyText: 'Pas de donn&eacute;es trouv&eacute;es.',
                                                getInnerTpl: function () {
                                                    return '<span>{str_NAME}</span>';
                                                }

                                            },
                                            listeners: {

                                                keypress: function (field, e, options) {

                                                    if (e.getKey() === e.BACKSPACE || e.getKey() === 46) {

                                                        if (field.getValue().length <= 2) {
                                                            field.getStore().load();
                                                        }

                                                    } else if (e.getKey() === e.ENTER) {
                                                        Ext.getCmp('str_CODE_LOT').focus();

                                                    }

                                                }



                                            }

                                        },
                                        {
                                            xtype: 'textfield',
                                            fieldLabel: 'Lot',
                                            margin: '0 0 0 15',
                                            id: 'str_CODE_LOT',
                                            emptyText: 'Numero Lot',
                                            labelWidth: 40,
                                            flex: 1, enableKeyEvents: true,
                                            listeners: {
                                                keypress: function (field, e, options) {

                                                    if (e.getKey() === e.ENTER) {
                                                        Ext.getCmp('perimeQTY').focus();

                                                    }
                                                }
                                            }},

                                        {
                                            xtype: 'numberfield',
                                            fieldLabel: 'Quantit&eacute;',
                                            id: 'perimeQTY',
                                            labelWidth: 60,
                                            flex: 0.8,
                                            margin: '0 0 0 15',
                                            minValue: 1,
                                            hideTrigger: true,
                                            emptyText: 'Quantite',
                                            enableKeyEvents: true,
                                            listeners: {
                                                keypress: function (field, e, options) {

                                                    if (e.getKey() === e.ENTER) {
                                                        Ext.getCmp('str_DATEPERMTION').focus();

                                                    }
                                                }
                                            }
                                            // limited to the current date or prior

                                        }, {
                                            xtype: 'datefield',
                                            fieldLabel: 'Date P&eacute;remption',
                                            margin: '0 0 0 15',
                                            id: 'str_DATEPERMTION',
                                            emptyText: 'Date de peremption',
                                            labelWidth: 110,
                                            flex: 1.5, enableKeyEvents: true,
                                            listeners: {
                                                keypress: function (field, e, options) {

                                                    if (e.getKey() === e.ENTER) {
                                                        var dt_peremption = Ext.Date.format(Ext.getCmp('str_DATEPERMTION').getValue(), 'Y-m-d');
                                                        var int_NUM_LOT = Ext.getCmp('str_CODE_LOT').getValue();
                                                        var int_NUMBER = Ext.getCmp('perimeQTY').getValue();
                                                        var lg_FAMILLE_ID = Ext.getCmp('cmb_Article').getValue();
                                                        if (lg_FAMILLE_ID === "" || lg_FAMILLE_ID === null) {
                                                            Ext.MessageBox.show({
                                                                title: 'Infos',
                                                                width: 320,
                                                                msg: 'Veuillez s&eacute;lectionnez le produit',
                                                                buttons: Ext.MessageBox.OK,
                                                                icon: Ext.MessageBox.WARNING

                                                            });
                                                            return false;


                                                        }
                                                        if (dt_peremption === "" || dt_peremption === null) {
                                                            Ext.MessageBox.show({
                                                                title: 'Infos',
                                                                width: 320,
                                                                msg: 'Veuillez saissir la date p&eacute;remption',
                                                                buttons: Ext.MessageBox.OK,
                                                                icon: Ext.MessageBox.WARNING

                                                            });
                                                            return false;


                                                        }


                                                        if (int_NUM_LOT === "") {
                                                            Ext.MessageBox.show({
                                                                title: 'Infos',
                                                                width: 320,
                                                                msg: 'Veuillez saissir le num&eacute;ro de lot',
                                                                buttons: Ext.MessageBox.OK,
                                                                icon: Ext.MessageBox.WARNING

                                                            });
                                                            return false;

                                                        }
                                                        if (int_NUMBER === "" || int_NUMBER <= 0) {
                                                            Ext.MessageBox.show({
                                                                title: 'Infos',
                                                                width: 320,
                                                                msg: 'Veuillez saissir la quantit&eacute;',
                                                                buttons: Ext.MessageBox.OK,
                                                                icon: Ext.MessageBox.WARNING

                                                            });
                                                            return false;

                                                        }

                                                        Ext.Ajax.request({
                                                            url: '../webservices/Report/saisieperimes/ws_transaction.jsp',
                                                            method: 'POST',

                                                            params: {
                                                                MODE: 'PENDING',
                                                                dt_peremption: dt_peremption,
                                                                int_NUM_LOT: int_NUM_LOT,
                                                                int_NUMBER: int_NUMBER,
                                                                lg_FAMILLE_ID: lg_FAMILLE_ID


                                                            },
                                                            success: function (response, options) {

                                                                var object = Ext.JSON.decode(response.responseText, false);
                                                                if (object.success === 1) {
                                                                    Ext.getCmp('gridpanelArticlePerimes').getStore().load();
                                                                    Ext.getCmp('str_DATEPERMTION').setValue('');
                                                                    Ext.getCmp('str_CODE_LOT').setValue('');
                                                                    Ext.getCmp('perimeQTY').setValue('');
                                                                    Ext.getCmp('cmb_Article').setValue('');
                                                                } else {
                                                                    Ext.MessageBox.show({
                                                                        title: 'Infos',
                                                                        width: 320,
                                                                        msg: object.message,
                                                                        buttons: Ext.MessageBox.OK,
                                                                        icon: Ext.MessageBox.ERROR

                                                                    });
                                                                }


                                                            }, failure: function (response, options) {

                                                                Ext.MessageBox.show({
                                                                    title: 'Infos',
                                                                    width: 320,
                                                                    msg: "Erreur de serveur",
                                                                    buttons: Ext.MessageBox.OK,
                                                                    icon: Ext.MessageBox.ERROR

                                                                });


                                                            }
                                                        });



                                                    }
                                                }
                                            }


                                        }, {
                                            text: 'Enregistrer',
                                            id: 'btn_add',
                                            margins: '0 0 0 15',
                                            xtype: 'button',
                                            handler: this.onbtnadd

                                        }

                                    ]
                                }]
                        }
                    ]

                },

                {
                    xtype: 'fieldset',
                    padding: '3 15 3 15',
                    collapsible: false,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            columnWidth: 0.65,
                            xtype: 'gridpanel',
                            minHeight: 450,
                            plugins: [
                                {
                                    ptype: "rowediting",
                                    clicksToEdit: 2
                                }
                            ],
                            id: 'gridpanelArticlePerimes',
                            store: pendingStore,

                            columns: [
                                {
                                    header: 'ID',
                                    dataIndex: 'ID',
                                    hidden: true,
                                    width: 40
                                }
                                , {
                                    header: 'CODE CIP',
                                    dataIndex: 'CIP',
                                    flex: 1
                                }
                                , {
                                    header: 'ARTICLE',
                                    dataIndex: 'ARTICLE',
                                    flex: 1
                                }, {
                                    header: 'LOT',
                                    dataIndex: 'LOT',
                                    editor: {
                                        xtype: "textfield",
                                        allowBlank: false

                                    },
                                    flex: 1
                                },
                                {
                                    header: 'QUANTITE',
                                    dataIndex: 'QTY',
                                    renderer: amountformat,
                                    align: 'right',
                                    editor: {
                                        xtype: "numberfield",
                                        minValue: 1,
                                        allowBlank: false
                                    },
                                    flex: 1
                                },
                                {
                                    header: 'DATE ENTREE',
                                    dataIndex: 'DATEENTREE',

                                    flex: 1
                                },
                                {
                                    header: 'DATE PEREMPTION',
                                    dataIndex: 'DATEPEREMPTION',

                                    flex: 1
                                }
                                , {
                                    xtype: 'actioncolumn',
                                    width: 30,
                                    sortable: false,
                                    menuDisabled: true,
                                    items: [{
                                            getClass: function (v, meta, rec) {

                                                return 'unpaid';
                                            },
                                            getTip: function (v, meta, rec) {

                                                return 'Supprimer ';

                                            },
                                            scope: this,
                                            handler: this.onRemoveClick

                                        }

                                    ]
                                }



                            ],
                            listeners: {
                                edit: function (src, e) {
                                    var record = e.record;


                                    Ext.Ajax.request({
                                        url: '../webservices/Report/saisieperimes/ws_transaction.jsp',
                                        method: 'POST',
                                        params: {
                                            MODE: 'UPDATE',
                                            ID: record.get("ID"),
                                            dt_peremption: record.get("DATEPEREMPTION"),
                                            int_NUM_LOT: record.get("LOT"),
                                            int_NUMBER: record.get("QTY")


                                        },
                                        success: function (response)
                                        {
                                            var obj = Ext.decode(response.responseText);

                                            if (obj.success === 1) {

                                                e.record.commit();
                                            } else {

                                                Ext.MessageBox.show({
                                                    title: 'Infos',
                                                    width: 320,
                                                    msg: obj.message,
                                                    buttons: Ext.MessageBox.OK,
                                                    icon: Ext.MessageBox.ERROR


                                                });
                                                Ext.getCmp('gridpanelArticlePerimes').getStore().load();
                                            }


                                        },
                                        failure: function (response)
                                        {

                                        }
                                    });

                                }
                            },

                            //  selModel: selModel,
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 20,
                                store: pendingStore,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager()
                            }
                        }
                        ,
                        {
                            xtype: 'toolbar',
                            ui: 'footer',
                            dock: 'bottom',
                            border: '0',
                            items: ['->',
                                {
                                    text: 'Terminer',
                                    id: 'btn_create_perime',
                                    iconCls: 'icon-clear-group',
                                    scope: this,
                                    hidden: false,
                                    //disabled: true,
                                    handler: this.CreateFacture
                                }, {
                                    text: 'RETOUR',
                                    id: 'btn_cancel',
                                    iconCls: 'icon-clear-group',
                                    scope: this,
                                    hidden: false,
                                    //disabled: true,
                                    handler: this.onbtncancel
                                }
                            ]
                        }
                    ]

                },
                // detail facture fournisseur


                {
                    xtype: 'fieldset',
                    id: 'detailfactureFournisseur',
                    title: 'Detail(s) de la Facture Fournisseur',
                    hidden: true,
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    }


                }
                //fin detail facture fournisseur

            ]


        });

        this.callParent();
//        OCltgridpanelArticlePerimes = Ext.getCmp('gridpanelArticlePerimes');
        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });

    },
    loadStore: function () {

    },
    onStoreLoad: function () {



    },
    onbtncancel: function () {

        var xtype = "saisieperime";
        testextjs.app.getController('App').onLoadNewComponent(xtype, "", "");
    },
    onbtnadd: function () {
        var dt_peremption = Ext.Date.format(Ext.getCmp('str_DATEPERMTION').getValue(), 'Y-m-d');
        var int_NUM_LOT = Ext.getCmp('str_CODE_LOT').getValue();
        var int_NUMBER = Ext.getCmp('perimeQTY').getValue();
        var lg_FAMILLE_ID = Ext.getCmp('cmb_Article').getValue();
        if (lg_FAMILLE_ID === "" || lg_FAMILLE_ID === null) {
            Ext.MessageBox.show({
                title: 'Infos',
                width: 320,
                msg: 'Veuillez s&eacute;lectionnez le produit',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING

            });
            return false;


        }
        if (dt_peremption === "" || dt_peremption === null) {
            Ext.MessageBox.show({
                title: 'Infos',
                width: 320,
                msg: 'Veuillez saissir la date p&eacute;remption',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING

            });
            return false;


        }


        if (int_NUM_LOT === "") {
            Ext.MessageBox.show({
                title: 'Infos',
                width: 320,
                msg: 'Veuillez saissir le num&eacute;ro de lot',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING

            });
            return false;

        }
        if (int_NUMBER === "" || int_NUMBER <= 0) {
            Ext.MessageBox.show({
                title: 'Infos',
                width: 320,
                msg: 'Veuillez saissir la quantit&eacute;',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING

            });
            return false;

        }

        Ext.Ajax.request({
            url: '../webservices/Report/saisieperimes/ws_transaction.jsp',
            method: 'POST',

            params: {
                MODE: 'PENDING',
                dt_peremption: dt_peremption,
                int_NUM_LOT: int_NUM_LOT,
                int_NUMBER: int_NUMBER,
                lg_FAMILLE_ID: lg_FAMILLE_ID


            },
            success: function (response, options) {

                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success === 1) {
                    Ext.getCmp('gridpanelArticlePerimes').getStore().load();
                    Ext.getCmp('str_DATEPERMTION').setValue('');
                    Ext.getCmp('str_CODE_LOT').setValue('');
                    Ext.getCmp('perimeQTY').setValue('');
                    Ext.getCmp('cmb_Article').setValue('');
                } else {
                    Ext.MessageBox.show({
                        title: 'Infos',
                        width: 320,
                        msg: object.message,
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR

                    });
                }


            }, failure: function (response, options) {

                Ext.MessageBox.show({
                    title: 'Infos',
                    width: 320,
                    msg: "Erreur de serveur",
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.ERROR

                });


            }
        });



    },
    onRemoveClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        Ext.Ajax.request({
            url: '../webservices/Report/saisieperimes/ws_transaction.jsp',
            method: 'POST',

            params: {
                MODE: 'REMOVE',
                ID: rec.get('ID')


            },
            success: function (response, options) {

                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success === 1) {
                    Ext.getCmp('gridpanelArticlePerimes').getStore().load();
                    Ext.MessageBox.show({
                        title: 'Infos',
                        width: 320,
                        msg: "Suppression effectuee avec success",
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.INFO

                    });

                } else {
                    Ext.MessageBox.show({
                        title: 'Eurreur',
                        width: 320,
                        msg: 'Eurreur de Suppression',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR

                    });
                }


            }, failure: function (response, options) {

                Ext.MessageBox.show({
                    title: 'Infos',
                    width: 320,
                    msg: "Erreur de serveur",
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.ERROR

                });


            }
        });


    },

    CreateFacture: function (val) {



        var store = Ext.getCmp('gridpanelArticlePerimes').getStore();
        if (store.getCount() <= 0) {

            return;
        } else {
            var record = store.getAt(0);
            myAppController.ShowWaitingProcess();
            Ext.Ajax.request({
                url: '../webservices/Report/saisieperimes/ws_transaction.jsp',
                method: 'POST',
                timeout: 24000000,
                params: {
                    MODE: 'ALL',
                    ID: record.get('ID')
                },
                success: function (response, options) {
                    myAppController.StopWaitingProcess();
                    var object = Ext.JSON.decode(response.responseText, false);
                    if (object.success === 1) {

                        Ext.MessageBox.show({
                            title: 'Infos',
                            width: 320,
                            msg: "Nombre de produit retiré:<span style='font-weight:900;'>" + object.NB + "</span> ",
                            buttons: Ext.MessageBox.OK,
                            icon: Ext.MessageBox.INFO

                        });


                    } else {
                        myAppController.StopWaitingProcess();

                    }

                    store.load();
                }, failure: function (response, options) {
                    myAppController.StopWaitingProcess();

//                    store.rejectChanges();

                }
            });
        }









    },

});


