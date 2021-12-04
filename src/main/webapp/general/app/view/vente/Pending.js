/* global Ext */

Ext.define('testextjs.view.vente.Pending', {
    extend: 'Ext.panel.Panel',
    xtype: 'cloturerventemanager',
    requires: [
        'Ext.grid.plugin.RowExpander'
    ],

    frame: true,
    title: 'Liste Des Ventes',
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
        var store = Ext.create('Ext.data.ArrayStore', {
            data: [['VNO'], ['VO']],
            fields: [{name: 'typeVente', type: 'string'}]
        });
        var vente = Ext.create('Ext.data.Store', {
            model: 'testextjs.model.caisse.Vente',
            autoLoad: false,
            pageSize: 15,

            proxy: {
                type: 'ajax',
                url: '../api/v1/ventestats/preventes',
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
                            text: 'Nouvelle Vente',
                            scope: this,
                            itemId: 'addBtn',
                            iconCls: 'addicon'

                        }, '-',
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Type.vente',
                            itemId: 'typeVente',
                            store: store,
                            height: 30,
                            flex: 1,
                            valueField: 'typeVente',
                            displayField: 'typeVente',
                            typeAhead: false,
                            mode: 'local',
                            minChars: 1,
                            emptyText: 'Selectionner un type de vente'

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
                        enableColumnHide: false,
                        animCollapse: false
//                        emptyText: '<h1 style="margin:10px 10px 10px 30%;">Pas de donn&eacute;es</h1>'
                    },
                    columns: [

                        {
                            header: 'Reference',
                            dataIndex: 'strREF',
                            flex: 1,
                            sortable: false,
                            menuDisabled: true
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
                            sortable: false,
                            menuDisabled: true,
                            flex: 0.6,
                            align: 'center'
                        }, {
                            header: 'Heure',
                            dataIndex: 'heure',
                            sortable: false,
                            menuDisabled: true,
                            flex: 0.6,
                            align: 'center'
                        },

                        {

                            header: 'Type.vente',
                            dataIndex: 'strTYPEVENTE',
                            sortable: false,
                            menuDisabled: true,
                            flex: 1
                        }, {
                            header: 'Vendeur',
                            sortable: false,
                            menuDisabled: true,
                            dataIndex: 'userFullName',
                            flex: 1
                        }, {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,

                            items: [{
                                    icon: 'resources/images/icons/fam/page_white_edit.png',
                                    tooltip: 'Modifier',
                                    menuDisabled: true,
                                    scope: me/*,
                                     handler: function (view, rowIndex, colIndex, item, e) {
                                     
                                     me.fireEvent('click', item, 'edit', view, rowIndex, colIndex, item, e);
                                     }*/

                                }]
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/trash.png',
                                    tooltip: 'Mettre dans la corbeille',
                                    scope: me/*,
                                     handler: function (view, rowIndex, colIndex, item, e) {
                                     me.fireEvent('click', item, 'corbeille', view, rowIndex, colIndex, item, e);
                                     }*/

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
                                    scope: me/*,
                                     handler: function (view, rowIndex, colIndex, item, e) {
                                     me.fireEvent('itemclick', this, 'delete', view, rowIndex, colIndex, item, e);
                                     }*/

                                }]
                        }
                    ],
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


