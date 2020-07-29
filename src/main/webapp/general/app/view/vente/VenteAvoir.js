/* global Ext */

Ext.define('testextjs.view.vente.VenteAvoir', {
    extend: 'Ext.panel.Panel',
    xtype: 'venteavoirmanager',
    requires: [
        'Ext.grid.plugin.RowExpander'
    ],

    frame: true,
    title: 'Liste des avoirs',
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
                url: '../api/v1/ventestats',
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
                            xtype: 'datefield',
                            fieldLabel: 'Du',
                            itemId: 'dtStart',
//                            height: 30,
                            labelWidth: 15,
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
//                            height: 30,
                            labelWidth: 15,
                            flex: 1,
                            submitFormat: 'Y-m-d',
                            maxValue: new Date(),
                            format: 'd/m/Y',
                            value: new Date()

                        }, '-', {

                            xtype: 'timefield',
                            fieldLabel: 'De',
                            itemId: 'hStart',
                            emptyText: 'Heure debut(HH:mm)',
                            flex: 0.8,
                            labelWidth: 15,
//                            height: 30,
                            increment: 30,
                            value: '00:00',
                            format: 'H:i'
                        }, '-',
                        {

                            xtype: 'timefield',
                            fieldLabel: 'A',
                            itemId: 'hEnd',
                            emptyText: 'Heure fin(HH:mm)',
                            flex: 0.8,
//                            height: 30,
                            labelWidth: 15,
                            increment: 30,
                            value: '23:59',
                            format: 'H:i'
                        }, '-', {
                            xtype: 'combobox',
                            fieldLabel: 'Type.vente',
                            labelWidth: 65,
                            itemId: 'typeVente',
                            store: store,
//                            height: 30,
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
                        },
                        '-',
                        {
                            text: 'Imprimer',
                            tooltip: 'imprimer',
                            scope: this,
                            itemId: 'printPdf',
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
//                        enableLocking: true,

                        animCollapse: false,

                        hideable: false,
                        draggable: false



//                        emptyText: '<h1 style="margin:10px 10px 10px 30%;">Pas de donn&eacute;es</h1>'
                    },
                    columns: [
                        {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Reference',
                            dataIndex: 'strREF',
                            flex: 1
                        },
                        {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Type.Vente',
                            dataIndex: 'strTYPEVENTE',
                            flex: 1
                        }

                        , {
                            sortable: false,
                            menuDisabled: true,
                            header: 'MONTANT',
                            xtype: 'numbercolumn',
                              align: 'right',
                            dataIndex: 'intPRICE',
                            flex: 1,
                            format: '0,000.'

                        },
                        {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Date',
                            dataIndex: 'dtUPDATED',
                            flex: 0.6,
                            align: 'center'
                        }, {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Heure',
                            dataIndex: 'heure',
                            flex: 0.6,
                            align: 'center'
                        }

                        , {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Vendeur',
                            dataIndex: 'userFullName',
                            flex: 1
                        },

                        {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Caissier',
                            dataIndex: 'userCaissierName',
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
                                    scope: this
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


