/* global Ext */

var url_services_data_articlevendu = '../webservices/sm_user/famille/ws_data_article_vendu.jsp';
var url_services_data_articlevendu_generate_pdf = '../webservices/sm_user/famille/ws_generate_articlevendubroup_pdf.jsp';
var url_services_transaction_suggerercde = '../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=';
var url_services_data_utilisateur = '../webservices/sm_user/utilisateur/ws_data.jsp';

var Me;


Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

function amountformatbis(val) {
    return amountformat(val) + " F CFA";
}

Ext.define('testextjs.view.configmanagement.famille.ArticleVendu', {
    extend: 'Ext.grid.Panel',
    xtype: 'articlevendumanager',
    id: 'articlevendumanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Famille',
        'Ext.ux.ProgressBarPager'

    ],
    title: 'Liste des articles vendus',
    plain: true,
    maximizable: true,
    closable: false,
    frame: true,
    initComponent: function () {

        Me = this;
        var lg_EMPLACEMENT_ID = loadEmplacement();

        var itemsPerPage = 20;

        var int_TOTAL = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    flex: 0.7,
                    fieldLabel: 'TOTAL::',
                    fieldWidth: 70,
                    name: 'int_TOTAL',
                    id: 'int_TOTAL',
                    renderer: amountformatbis,
                    fieldStyle: "color:blue;",
                    value: 0
                });

        var storeUser = new Ext.data.Store({
            model: 'testextjs.model.Utilisateur',
            pageSize: itemsPerPage,
            autoLoad: false,
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
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_articlevendu,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [{str_TYPE_TRANSACTION: 'LESS', str_desc: 'Inferieur a'}, {str_TYPE_TRANSACTION: 'MORE', str_desc: 'Superieur a'}, {str_TYPE_TRANSACTION: 'EQUAL', str_desc: 'Egal a'},
                {str_TYPE_TRANSACTION: 'LESSOREQUAL', str_desc: 'Inferieur ou egal a'}, {str_TYPE_TRANSACTION: 'MOREOREQUAL', str_desc: 'Superieur ou egal a'}, {str_TYPE_TRANSACTION: 'SEUIL',
                    str_desc: 'Seuil atteint'},
                {str_TYPE_TRANSACTION: 'ALL', str_desc: "Tous"}
            ]
        });

        var filtreAchat = new Ext.data.Store({
            fields: ['id', 'libelle'],
            data: [
                {id: 'LESS', libelle: "P.U inférieur au P.A"},
//                {id: 'EQUAL', libelle: "P.A égale au  P.U"},
//                {id: 'MORE', libelle: "P.U supérieur au P.A"},
                {id: 'TOUT', libelle: "Tous"}
            ]
        });
        var filtreStock = new Ext.data.Store({
            fields: ['id', 'libelle'],
            data: [
                {id: 'LESS', libelle: "Inférieur"},
                {id: 'MORE', libelle: "Supérieur"},
                {id: 'DIFF', libelle: "Différent"},
                {id: 'MOREOREQUAL', libelle: "Superieur ou égal"},
                {id: 'LESSOREQUAL', libelle: "Inferieur ou égal"},
                {id: 'TOUT', libelle: "Tous"}
            ]
        });



        Ext.apply(this, {
            width: '98%',
            height: valheight,
            store: store,
            id: 'GridArticleID',
            columns: [
                {
                    header: 'lg_FAMILLE_ID',
                    dataIndex: 'lg_FAMILLE_ID',
                    hidden: true,
                    flex: 1
                },
                {
                    xtype: 'rownumberer',
                    text: 'Num',
                    width: 45,
                    sortable: true
                },
                {
                    header: 'CIP',
                    dataIndex: 'int_CIP',
                    flex: 0.8
                },
                {
                    header: 'Designation',
                    dataIndex: 'str_DESCRIPTION',
                    flex: 2.5
                },
                {
                    header: 'Date',
                    dataIndex: 'dt_UPDATED',
                    flex: 0.8
                },
                {
                    header: 'Heure',
                    dataIndex: 'lg_ETAT_ARTICLE_ID',
                    flex: 0.7
                },
                {
                    header: 'Qte Vd',
                    dataIndex: 'int_NUMBER_AVAILABLE',
                    flex: 0.6,
                    align: 'center'
                },
                {
                    header: 'Prix',
                    dataIndex: 'int_PRICE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.8
                }, {
                    header: 'Stock',
                    dataIndex: 'int_NUMBER',
                    flex: 0.6,
                    align: 'center'
                }, {
                    header: 'Ticket',
                    dataIndex: 'int_T',
                    flex: 1
                }, {
                    header: 'Type.Vente',
                    dataIndex: 'str_NAME',
                    flex: 0.7,
                    align: 'center'
                }, {
                    header: 'Avoir',
                    dataIndex: 'int_AVOIR',
                    flex: 0.6,
                    align: 'center'
                },
                {
                    header: 'Operateur',
                    dataIndex: 'lg_AJUSTEMENTDETAIL_ID',
                    flex: 1
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'datefield',
                            fieldLabel: 'Du',
                            name: 'dt_Date_Debut',
                            id: 'dt_Date_Debut',
                            labelWidth: 15,
                            flex: 1,
                            allowBlank: false,
                            submitFormat: 'Y-m-d',
                            maxValue: new Date(),
                            format: 'd/m/Y',
                            listeners: {
                                'change': function (me) {
                                    Ext.getCmp('dt_Date_Fin').setMinValue(me.getValue());
                                }
                            }
                        }, {
                            xtype: 'datefield',
                            fieldLabel: 'Au',
                            name: 'dt_Date_Fin',
                            id: 'dt_Date_Fin',
                            labelWidth: 15,
                            flex: 1,
                            allowBlank: false,
                            maxValue: new Date(),
                            submitFormat: 'Y-m-d',
                            format: 'd/m/Y',
                            listeners: {
                                'change': function (me) {
                                    Ext.getCmp('dt_Date_Debut').setMaxValue(me.getValue());
                                }
                            }
                        }, {
                            xtype: 'timefield',
                            fieldLabel: 'De',
                            // margin: '0 7 0 0',
                            name: 'h_debut',
                            id: 'h_debut',
                            emptyText: 'Heure debut(HH:mm)',
                            allowBlank: false,
                            flex: 1,
                            labelWidth: 15,

                            increment: 30,
                            format: 'H:i'
                        }, '-', {
                            xtype: 'timefield',
                            fieldLabel: 'A',
                            name: 'h_fin',
                            id: 'h_fin',
                            emptyText: 'Heure fin(HH:mm)',
                            allowBlank: false,
                            labelWidth: 10,
                            increment: 30,
                            flex: 1,
                            format: 'H:i'
                        }, '-',
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Utilisateur',
                            name: 'lg_USER_ID',
                            id: 'lg_USER_ID',
                            store: storeUser,
                            labelWidth: 60,
                            hidden: true,
                            flex: 1,
                            pageSize: 20,
                            valueField: 'lg_USER_ID',
                            displayField: 'str_FIRST_LAST_NAME',
                            typeAhead: true,
                            queryMode: 'remote',
                            emptyText: 'Choisir un utilisateur...',
                            listeners: {
                                select: function (cmp) {
                                    Me.onRechClick();
                                }
                            }
                        }, '-',
                        {
                            xtype: 'textfield',
                            id: 'rechecher',
                            name: 'user',
                            flex: 1.3,
                            emptyText: 'Recherche',
                            listeners: {
                                'render': function (cmp) {
                                    cmp.getEl().on('keypress', function (e) {
                                        if (e.getKey() === e.ENTER) {
                                            Me.onRechClick();
                                        }
                                    });
                                }
                            }
                        }, {
                            text: 'rechercher',
                            tooltip: 'rechercher',
                            scope: this,
                            iconCls: 'searchicon',
                            handler: this.onRechClick
                        }, '-', {
                            text: 'Imprimer',
                            tooltip: 'imprimer',
                            iconCls: 'printable',
                            scope: this,
                            handler: this.onPdfClick
                        }, '-',
                        {
                            text: 'Suggerer',
                            tooltip: 'Suggerer',
                            iconCls: 'suggestionreapro',
                            scope: this,
                            handler: this.onSuggereClick
                        }
                    ]},

                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [

                        {
                            xtype: 'combobox',
                            fieldLabel: 'Filtre seuil',
                            name: 'str_TYPE_TRANSACTION',
                            id: 'str_TYPE_TRANSACTION',
                            store: store_type,
                            valueField: 'str_TYPE_TRANSACTION',
                            displayField: 'str_desc',
                            typeAhead: true,
                            flex: 1,
                            queryMode: 'local',
                            emptyText: 'Filtre article...',
                            listeners: {
                                select: function (cmp) {
                                    Me.onRechClick();
                                }
                            }
                        }, '-',
                        {
                            xtype: 'textfield',
                            id: 'int_NUMBER',
                            name: 'int_NUMBER',
                            flex: 0.5,
                            emptyText: 'Quantite',
                            listeners: {
                                'render': function (cmp) {
                                    cmp.getEl().on('keypress', function (e) {
                                        if (e.getKey() === e.ENTER) {
                                            Me.onRechClick();
                                        }
                                    });
                                }
                            }
                        }, '-',
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Filtre sur stock',
                            id: 'stockFiltre',
                            store: filtreStock,
                            valueField: 'id',
                            displayField: 'libelle',
                            typeAhead: true,
                            flex: 1,
                            queryMode: 'local',
                            emptyText: 'Filtre sur stock...',
                            listeners: {
                                select: function (cmp) {
                                    Me.onRechClick();
                                }
                            }
                        }, '-',
                        {
                            xtype: 'textfield',
                            id: 'stock',
                            flex: 0.5,
                            emptyText: 'Stock',
                            listeners: {
                                'render': function (cmp) {
                                    cmp.getEl().on('keypress', function (e) {
                                        if (e.getKey() === e.ENTER) {
                                            Me.onRechClick();
                                        }
                                    });
                                }
                            }
                        }
                        
                        , '-',
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Filtre prix achat',
                            id: 'prixachatFiltre',
                            store: filtreAchat,
                            valueField: 'id',
                            displayField: 'libelle',
                            typeAhead: true,
                            flex: 1,
                            queryMode: 'local',
                            emptyText: 'Filtre par prix achat ...',
                            listeners: {
                                select: function (cmp) {
                                    Me.onRechClick();
                                }
                            }
                        }



                    ]}
            ],
            bbar: {
                dock: 'bottom',
                items: [
                    {
                        xtype: 'pagingtoolbar',
                        displayInfo: true,
                        flex: 2,
                        pageSize: itemsPerPage,
                        store: store, // same store GridPanel is using
                        listeners: {
                            beforechange: function (page, currentPage) {
                                var myProxy = this.store.getProxy();
                                myProxy.params = {
                                    search_value: '',
                                    int_NUMBER: '',
                                    dt_Date_Debut: '',
                                    dt_Date_Fin: '',
                                    h_debut: '',
                                    h_fin: '',
                                    lg_USER_ID: '',
                                    str_TYPE_TRANSACTION: 'ALL'
                                };

                                myProxy.setExtraParam('search_value', Ext.getCmp('rechecher').getValue());
                                myProxy.setExtraParam('int_NUMBER', Ext.getCmp('int_NUMBER').getValue());
                                myProxy.setExtraParam('dt_Date_Debut', Ext.getCmp('dt_Date_Debut').getSubmitValue());
                                myProxy.setExtraParam('dt_Date_Fin', Ext.getCmp('dt_Date_Fin').getSubmitValue());
                                myProxy.setExtraParam('h_debut', Ext.getCmp('h_debut').getSubmitValue());
                                myProxy.setExtraParam('h_fin', Ext.getCmp('h_fin').getSubmitValue());
                                myProxy.setExtraParam('str_TYPE_TRANSACTION', Ext.getCmp('str_TYPE_TRANSACTION').getValue());
                                myProxy.setExtraParam('lg_USER_ID', Ext.getCmp('lg_USER_ID').getValue());
                            }

                        }
                    },
                    {
                        xtype: 'tbseparator'
                    },
                    int_TOTAL
                ]
            },
            listeners: {
                afterrender: function () {
                    if (lg_EMPLACEMENT_ID != "1") {
                        Ext.getCmp('btn_export_suggestion').show();
                    }
                }
            }
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });

    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
        var int_TOTAL = 0;
        if (this.getStore().getCount() > 0) {
            this.getStore().each(function (rec) {
                int_TOTAL += parseInt(rec.get('int_PRICE'));
            });
        }
        Ext.getCmp('int_TOTAL').setValue(int_TOTAL);
    },
    onRechClick: function () {

        if (new Date(Ext.getCmp('dt_Date_Debut').getSubmitValue()) > new Date(Ext.getCmp('dt_Date_Fin').getSubmitValue())) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }

        this.getStore().load({
            params: {
                dt_Date_Debut: Ext.getCmp('dt_Date_Debut').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_Date_Fin').getSubmitValue(),
                h_debut: (Ext.getCmp('h_debut').getSubmitValue() != null ? Ext.getCmp('h_debut').getSubmitValue() : ""),
                h_fin: (Ext.getCmp('h_fin').getSubmitValue() != null ? Ext.getCmp('h_fin').getSubmitValue() : ""),
                lg_USER_ID: (Ext.getCmp('lg_USER_ID').getValue() != null ? Ext.getCmp('lg_USER_ID').getValue() : ""),
                search_value: Ext.getCmp('rechecher').getValue(),
                str_TYPE_TRANSACTION: (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null ? Ext.getCmp('str_TYPE_TRANSACTION').getValue() : "ALL"),
                int_NUMBER: (Ext.getCmp('int_NUMBER').getValue() != null ? Ext.getCmp('int_NUMBER').getValue() : 0),
                prixachatFiltre: Ext.getCmp('prixachatFiltre').getValue(),
                stock: (Ext.getCmp('stock').getValue() != null ? Ext.getCmp('stock').getValue() : 0),
                stockFiltre: Ext.getCmp('stockFiltre').getValue()

            }
        });
    },
    onPdfClick: function () {

        var linkUrl = url_services_data_articlevendu_generate_pdf + '?dt_Date_Debut=' + Ext.getCmp('dt_Date_Debut').getSubmitValue()
                + '&dt_Date_Fin=' + Ext.getCmp('dt_Date_Fin').getSubmitValue()
                + "&h_debut=" + (Ext.getCmp('h_debut').getSubmitValue() != null ? Ext.getCmp('h_debut').getSubmitValue() : "")
                + "&h_fin=" + (Ext.getCmp('h_fin').getSubmitValue() != null ? Ext.getCmp('h_fin').getSubmitValue() : "")
                + '&search_value=' + Ext.getCmp('rechecher').getValue()
                + "&str_TYPE_TRANSACTION=" + (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null ? Ext.getCmp('str_TYPE_TRANSACTION').getValue() : "ALL")
                + '&int_NUMBER=' + (Ext.getCmp('int_NUMBER').getValue() != null ? Ext.getCmp('int_NUMBER').getValue() : 0) + "&prixachatFiltre=" + Ext.getCmp('prixachatFiltre').getValue()
                + '&stock='+(Ext.getCmp('stock').getValue() != null ? Ext.getCmp('stock').getValue() : 0) + "&stockFiltre=" + Ext.getCmp('stockFiltre').getValue()
                ;


        window.open(linkUrl);
    },
    onSuggereClick: function () {
        testextjs.app.getController('App').ShowWaitingProcess();
        Ext.Ajax.request({
            url: '../webservices/sm_user/suggerercde/ws_suggestion.jsp?mode=sendProductSellToSuggestion',
            params: {
                dt_Date_Debut: Ext.getCmp('dt_Date_Debut').getSubmitValue(),
                prixachatFiltre: Ext.getCmp('prixachatFiltre').getValue(),
                dt_Date_Fin: Ext.getCmp('dt_Date_Fin').getSubmitValue(),
                h_debut: (Ext.getCmp('h_debut').getSubmitValue() !== null ? Ext.getCmp('h_debut').getSubmitValue() : ""),
                h_fin: (Ext.getCmp('h_fin').getSubmitValue() !== null ? Ext.getCmp('h_fin').getSubmitValue() : ""),
                lg_USER_ID: (Ext.getCmp('lg_USER_ID').getValue() !== null ? Ext.getCmp('lg_USER_ID').getValue() : ""),
                search_value: Ext.getCmp('rechecher').getValue(),
                str_TYPE_TRANSACTION: (Ext.getCmp('str_TYPE_TRANSACTION').getValue() !== null ? Ext.getCmp('str_TYPE_TRANSACTION').getValue() : "ALL"),
                int_NUMBER: (Ext.getCmp('int_NUMBER').getValue() !== null ? Ext.getCmp('int_NUMBER').getValue() : 0),
                stock: (Ext.getCmp('stock').getValue() != null ? Ext.getCmp('stock').getValue() : 0),
                stockFiltre: Ext.getCmp('stockFiltre').getValue(),
                prixachatFiltre: Ext.getCmp('prixachatFiltre').getValue()

            },
            timeout: 2400000,
            success: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.qty >= 1) {
                    Ext.MessageBox.alert('confirmation', object.result);
                    var OGrid = Ext.getCmp('gridID');
                    OGrid.getStore().reload();
                } else {

                    Ext.MessageBox.alert('Error Message', "Les produits ne sont pas suggerés");
                    return;
                }
            },
            failure: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    }
});

function loadEmplacement() {
    return localStorage.getItem("lg_EMPLACEMENT_ID");
}