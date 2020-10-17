/* global Ext */

Ext.define('testextjs.view.vente.Ordonnancier', {
    extend: 'Ext.panel.Panel',
    xtype: 'ordonnancier',
    requires: [
        'Ext.grid.plugin.RowExpander'
    ],

    frame: true,
    title: 'Liste des Ventes',
    iconCls: 'icon-grid',
    width: '97%',
    height: 'auto',
    minHeight: 570,
    cls: 'custompanel',
    layout: {
        type: 'fit'
    },
    initComponent: function () {
        var store = Ext.create('Ext.data.Store', {
            model: 'testextjs.model.caisse.MedecinModel',
            autoLoad: false,
            pageSize: 15,

            proxy: {
                type: 'ajax',
                url: '../api/v1/medecin/medecins',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }
        });
        var vente = Ext.create('Ext.data.Store', {
            model: 'testextjs.model.caisse.Vente',
            autoLoad: false,
            pageSize: 99999,

            proxy: {
                type: 'ajax',
                url: '../api/v1/ventestats/ventesordonnanciers',
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

                        }
                        , '-', {
                            xtype: 'combobox',
                            fieldLabel: 'Medecins',
                            labelWidth: 65,
                            itemId: 'medecin',
                            store: store,
                            flex: 1,
                            valueField: 'id',
                            displayField: 'nom',
                            typeAhead: false,
                            mode: 'local',
                            minChars: 1,
                            emptyText: 'Selectionner un medecin'

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
                        animCollapse: false,
                        hideable: false,
                        draggable: false
                    },
                    columns: [
                        {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Reference',
                            dataIndex: 'strREF',
                            flex: 0.8
                        },
                        {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Type.Vente',
                            dataIndex: 'strTYPEVENTE',
                            align: 'center',
                            flex: 0.4
                        }

                        , {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Montant',
                            xtype: 'numbercolumn',
                            dataIndex: 'intPRICE',
                            align: 'right',
                            flex: 0.6,
                            format: '0,000.'

                        },
                        {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Montant différé',
                            xtype: 'numbercolumn',
//                            hidden:true,
                            dataIndex: 'intPRICERESTE',
                            align: 'right',
                            flex: 0.6,
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
                        }, {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Medecin',
                            dataIndex: 'nom',
                            flex: 1
                        }

                        , {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Vendeur',
                            dataIndex: 'userVendeurName',
                            flex: 1
                        },

                        {
                            sortable: false,
                            menuDisabled: true,
                            header: 'Caissier',
                            dataIndex: 'userCaissierName',
                            flex: 1
                        }

                    ],

                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: vente,
                        pageSize: 99999,
                        dock: 'bottom',
                        displayInfo: true

                    }
                }]

        });
        me.callParent(arguments);
    }
});


