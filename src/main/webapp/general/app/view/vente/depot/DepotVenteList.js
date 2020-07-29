/* global Ext */

Ext.define('testextjs.view.vente.depot.DepotVenteList', {
    extend: 'Ext.panel.Panel',
    xtype: 'ventedepot',
    requires: [
        'Ext.grid.plugin.RowExpander'
    ],

    frame: true,
    title: 'Liste Des Ventes Dépôt',
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
        var storeTypevente = Ext.create('Ext.data.ArrayStore', {
            data: [['4', 'VNO'], ['5', 'VO']],
            fields: [{name: 'id', type: 'string'}, {name: 'valeur', type: 'string'}]
        });
        var vente = Ext.create('Ext.data.Store', {
            model: 'testextjs.model.caisse.Vente',
            autoLoad: false,
            pageSize: 15,

            proxy: {
                type: 'ajax',
                url: '../api/v1/ventestats/preventes-depot',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
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
                            text: 'Nouvelle vente à un dépôt',
                            scope: this,
                            itemId: 'addBtn',
                            iconCls: 'addicon'

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
                            xtype: 'combobox',
                            fieldLabel: 'Type.vente',
                            itemId: 'typeVente',
                            store: storeTypevente,
                            height: 30,
                            flex: 1,
                            valueField: 'id',
                            displayField: 'valeur',
                            typeAhead: false,
                            mode: 'local',
                            minChars: 1,
                            emptyText: 'Selectionner un type de vente'

                        }, '-',

                        {
                            text: 'rechercher',
                            tooltip: 'rechercher',
                            itemId: 'rechercher',
                            scope: this,
                            iconCls: 'searchicon'
                        }, '-',
                        {
                            text: 'Importer',
                            tooltip: 'Transformer les produits vendus d\'un dépôt en vente',
                            itemId: 'btnImporter',
                            scope: this,
//                            hidden:true,
                            iconCls: 'importicon'
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
//                        enableLocking: true,
                        collapsible: true,
                        animCollapse: false
//                        emptyText: '<h1 style="margin:10px 10px 10px 30%;">Pas de donn&eacute;es</h1>'
                    },
                    columns: [
                        {
                            header: 'Reference',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'strREF',
                            flex: 1
                        }, {
                            header: 'MONTANT',
                            xtype: 'numbercolumn',
                            dataIndex: 'intPRICE',
                             align: 'right',
                            sortable: false,
                            menuDisabled: true,
                            flex: 1,
                            format: '0,000.'

                        },
                        {
                            header: 'Date',
                            dataIndex: 'dtUPDATED',
                            flex: 0.6,
                            sortable: false,
                            menuDisabled: true,
                            align: 'center'
                        }, {
                            header: 'Heure',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'heure',
                            flex: 0.6,
                            align: 'center'
                        }
                        , {
                            header: 'Type dépôt',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'desciptiontypedepot',
                            flex: 1
                        }, {
                            header: 'Dépôt',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'strNAME',
                            flex: 1
                        }
                        , {
                            header: 'Gérant',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'gerantFullName',
                            flex: 1
                        }
                        , {
                            header: 'Vendeur',
                           
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'userFullName',
                            flex: 1
                        },
                       

                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/page_white_edit.png',
                                    tooltip: 'Modifier',
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
//            selModel: {
//                selType: 'cellmodel'
//                selType: 'checkboxmodel',
//            },
                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: vente,
                        dock: 'bottom',
                        displayInfo: true

                    }
                }]

        });
        me.callParent(arguments);
    }
});


