/* global Ext */

Ext.define('testextjs.view.vente.Removed', {
    extend: 'Ext.panel.Panel',
    xtype: 'venteannuler',
    requires: [
        'Ext.grid.plugin.RowExpander'
    ],

    frame: true,
    title: 'Liste des Ventes annulées',
    iconCls: 'icon-grid',
    width: '97%',
    height: 'auto',
    minHeight: 570,
    cls: 'custompanel',
    layout: {
        type: 'fit'
//        align: 'stretch'
    },
    initComponent: function () {

        var vente = Ext.create('Ext.data.Store', {
            model: 'testextjs.model.caisse.Vente',
            autoLoad: false,
            pageSize: 15,

            proxy: {
                type: 'ajax',
                url: '../api/v1/ventestats/annulations',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total',
                    metaProperty: 'metaData'
                }

            }
        });

        var me = this;
        Ext.applyIf(me, {
            dockedItems: [

                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [

                        {
                            xtype: 'datefield',
                            fieldLabel: 'Du',
                            itemId: 'dtStart',
                            height: 30,
                            labelWidth: 30,
                            flex: 1,
                            submitFormat: 'Y-m-d',
                            maxValue: new Date(),
                            format: 'd/m/Y',
                            value: new Date()

                        }, '-',
                        {
                            xtype: 'datefield',
                            fieldLabel: 'Au',
                            itemId: 'dtEnd',
                            height: 30,
                            labelWidth: 30,
                            flex: 1,
                            submitFormat: 'Y-m-d',
                            maxValue: new Date(),
                            format: 'd/m/Y',
                            value: new Date()

                        }, '-',
                        {
                            xtype: 'textfield',
                            itemId: 'query',
                            flex: 1,
                            height: 30,
                            enableKeyEvents: true,
                            emptyText: 'Recherche'
                        }, '-',

                        {
                            text: 'rechercher',
                            tooltip: 'rechercher',
                            itemId: 'rechercher',
                            scope: this,
                            iconCls: 'searchicon'
                        }, '-',
                        {
                            text: 'Imprimer',
                            tooltip: 'imprimer',
                            itemId: 'printPdf',
                            scope: this,
                            iconCls: 'printable'
                        },
                         '-',
                        {
                            text: 'Imprimer +',
                            tooltip: 'imprimer +',
                            itemId: 'printPlus',
                            scope: this,
                            iconCls: 'printable'
                        }
                    ]
                }

            ],
            items: [
                {
                    xtype: 'gridpanel',

                    plugins: [{
                            ptype: 'rowexpander',
                            rowBodyTpl: new Ext.XTemplate(
                                    '<p>{details}</p>'

                                    )
                        }
                    ],
                    store: vente,

                    viewConfig: {
                        forceFit: true,
                        columnLines: true,
                        collapsible: true,
                        animCollapse: false,
                        enableColumnHide: false

                    },
                    columns: [
                        {
                            header: 'Reference',
                            dataIndex: 'strREF',
                            sortable: false,
                            menuDisabled: true,
                            flex: 0.8
                        },
                        {
                            header: 'Type.Vente',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'strTYPEVENTE',
                            flex: 0.5
                        }

                        , {
                            header: 'Montant',
                            xtype: 'numbercolumn',
                            dataIndex: 'intPRICE',
                            flex: 0.8,
                            align: 'right',
                            sortable: false,
                            menuDisabled: true,
                            format: '0,000.'

                        },
                        {
                            header: 'Date.vente',
                            dataIndex: 'dtCREATED',
                            flex: 0.6,
                            sortable: false,
                            menuDisabled: true,
                            align: 'center'
                        }, {
                            header: 'Heure.vente',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'HEUREVENTE',
                            flex: 0.6,
                            align: 'center'
                        }
                        ,

                        {
                            header: 'Date.Ann',
                            dataIndex: 'dateAnnulation',
                            flex: 0.6,
                            sortable: false,
                            menuDisabled: true,
                            align: 'center'
                        }, {
                            header: 'Heure.Ann',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'heureAnnulation',
                            flex: 0.6,
                            align: 'center'
                        },

                        {
                            header: 'Opérateur',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'userFullName',
                            flex: 1
                        }

                        , {
                            header: 'Vendeur',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'userVendeurName',
                            flex: 1
                        }, {
                            header: 'Caissier',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'userCaissierName',
                            flex: 1
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/printer.png',
                                    tooltip: 'Réimprimer le ticket',
                                    scope: me
                                }]
                        }

                    ],
                    bbar: {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        items: [{
                                xtype: 'pagingtoolbar',
                                store: vente,
                                dock: 'bottom',
                                pageSize: 16,
                                flex: 1.3,
                                displayInfo: true
                            }
                            , {
                                xtype: 'tbseparator'
                            },
                            {
                                xtype: 'displayfield',
                                flex: 0.7,
                                fieldLabel: 'Total annulation::',
                                labelWidth: 120,
                                itemId: 'totalAmount',
                                renderer: function (v) {
                                    return Ext.util.Format.number(v, '0,000.');
                                },
                                fieldStyle: "color:blue;font-weight:800;",
                                value: 0
                            }
                        ]
                    }
                }]
        });
        me.callParent(arguments);
    }
});


