/* global Ext */

Ext.define('testextjs.view.produits.ProduitDesactives', {
    extend: 'Ext.panel.Panel',
    xtype: 'familledisablemanager',
    requires: [

    ],
    //successProperty : 'meta.success'
    frame: true,
    title: 'Liste des produits désactivés',
    width: '97%',
    height: 'auto',
    minHeight: 570,
//    maxHeight: 800,
    cls: 'custompanel',
    layout: {
        type: 'fit'
//        align: 'stretch'
    },
    initComponent: function () {

        var storeProduits = new Ext.data.Store({
            fields:
                    [
                        {
                            name: 'lgFAMILLEID',
                            type: 'string'
                        },
                        {
                            name: 'intCIP',
                            type: 'string'
                        },
                        {
                            name: 'strNAME',
                            type: 'string'
                        }, {
                            name: 'intPRICE',
                            type: 'number'
                        }
                        , {
                            name: 'intNUMBERAVAILABLE',
                            type: 'number'
                        }
                        , {
                            name: 'intPAF',
                            type: 'number'
                        }, {
                            name: 'intNUMBER',
                            type: 'number'
                        }, {
                            name: 'dt_UPDATED',
                            type: 'date'
                        }
                    ],
            pageSize: 15,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/produit/produit-desactives',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                 timeout: 2400000
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
                            xtype: 'textfield',
                            itemId: 'query',
                            width: 350,
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
                        }

                    ]
                }

            ],
            items: [
                {
                    xtype: 'gridpanel',
                    store: storeProduits,
                    viewConfig: {
                        forceFit: true,
                        columnLines: true,
                        enableColumnHide:false

                    },
                    columns: [
                        {
                            header: 'Cip',
                            dataIndex: 'intCIP',
                            sortable: false,
                            menuDisabled: true,
                            flex: 0.7
                        },
                        {
                            header: 'Désignation',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'strNAME',
                            flex: 1.5
                        },

                        {
                            header: 'Prix de vente',
                            xtype: 'numbercolumn',
                            dataIndex: 'intPRICE',
                             align: 'right',
                            sortable: false,
                            menuDisabled: true,
                            flex: 0.6,
                            format: '0,000.'

                        },
                        {
                            header: "Prix d'achat",
                            xtype: 'numbercolumn',
                             align: 'right',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'intPAF',
                            flex: 0.6,
                            format: '0,000.'

                        },
                        {
                            header: "Stock",
                            xtype: 'numbercolumn',
                             align: 'right',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'intNUMBERAVAILABLE',
                            flex: 0.6,
                            format: '0,000.'

                        },
                        
                        {
                            header: "date",
                            xtype: 'numbercolumn',
                             align: 'right',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'dt_UPDATED',
                            flex: 0.6,
                            format: '0,000.'

                        },
                        
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/enable.png',
                                    tooltip: 'Activer le produit',
                                    menuDisabled: true,
                                    scope: me

                                }]
                        },

                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/delete.png',
                                    tooltip: 'Supprimer',
                                    scope: me

                                }]
                        }
                    ],

                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: storeProduits,
                        dock: 'bottom',
                        displayInfo: true
                    }
                }
            ]

        });
        me.callParent(arguments);
    }
    
});


