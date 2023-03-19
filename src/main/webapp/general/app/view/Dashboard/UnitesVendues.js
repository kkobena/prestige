/* global Ext */

Ext.define('testextjs.view.Dashboard.UnitesVendues', {
    extend: 'Ext.panel.Panel',
    xtype: 'statistiqueuniteventemanager',
    frame: true,
    title: 'Statistiques sur les unités vendues',
    width: '98%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
    layout: 'fit',
    initComponent: function () {
        var data = new Ext.data.Store({
            idProperty: 'code',
            fields: [
                {name: 'code', type: 'string'},
                {name: 'libelle', type: 'string'},
                {name: 'familleId', type: 'string'},
                {name: 'description', type: 'string'},
                {name: 'montantCumulTTC', type: 'number'},
                {name: 'montantCumulHT', type: 'number'},
                {name: 'montantTva', type: 'number'},
                {name: 'montantCumulTva', type: 'number'},
                {name: 'montantCumulAchat', type: 'number'},
                {name: 'pourcentageCumulMage', type: 'number'},
                {name: 'montantCumulMarge', type: 'number'}
            ],
            pageSize: 15,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/datareporting/unitesvendues',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                timeout: 2400000
            }
        });
        var grossiste = Ext.create('Ext.data.Store', {
            idProperty: 'id',
            fields:
                    [
                        {name: 'id',
                            type: 'string'

                        },

                        {name: 'libelle',
                            type: 'string'

                        }

                    ],
            autoLoad: false,
            pageSize: 9999,

            proxy: {
                type: 'ajax',
                url: '../api/v1/common/grossiste',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }

        });
        var rayons = Ext.create('Ext.data.Store', {
            idProperty: 'id',
            fields:
                    [
                        {name: 'id',
                            type: 'string'

                        },

                        {name: 'libelle',
                            type: 'string'

                        }

                    ],
            autoLoad: false,
            pageSize: 9999,

            proxy: {
                type: 'ajax',
                url: '../api/v1/common/rayons',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }

        });
        var familles = Ext.create('Ext.data.Store', {
            idProperty: 'id',
            fields:
                    [
                        {name: 'id',
                            type: 'string'

                        },

                        {name: 'libelle',
                            type: 'string'

                        }

                    ],
            autoLoad: false,
            pageSize: 9999,

            proxy: {
                type: 'ajax',
                url: '../api/v1/common/famillearticles',
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
                            submitFormat: 'Y-m-d',
                            flex: 0.8,
                            labelWidth: 17,
                            maxValue: new Date(),
                            value: new Date(),
                            format: 'd/m/Y'

                        }, {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'datefield',
                            fieldLabel: 'Au',
                            itemId: 'dtEnd',
                            labelWidth: 17,
                            flex: 1,
                            maxValue: new Date(),
                            value: new Date(),
                            margin: '0 9 0 0',
                            submitFormat: 'Y-m-d',
                            format: 'd/m/Y'

                        }
                        , {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'textfield',
                            itemId: 'query',
                            flex: 1,
                            emptyText: 'Taper pour rechercher',
                            enableKeyEvents: true
                        },

                        {
                            xtype: 'tbseparator'
                        },

                        {
                            xtype: 'combobox',
                            flex: 1,
                            margin: '0 5 0 0',
                            labelWidth: 5,
                            itemId: 'rayons',
                            store: rayons,
                            pageSize: 99999,
                            valueField: 'id',
                            displayField: 'libelle',
                            typeAhead: false,
                            queryMode: 'remote',
                            minChars: 2,
                            emptyText: 'Sélectionnez un emplacement'
                        }, {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'combobox',
                            flex: 1,
                            margin: '0 5 0 0',
                            labelWidth: 5,
                            itemId: 'grossiste',
                            store: grossiste,
                            pageSize: 999,
                            valueField: 'id',
                            displayField: 'libelle',
                            typeAhead: false,
                            queryMode: 'remote',
                            minChars: 2,
                            emptyText: 'Sélectionnez un grossiste'
                        },
                        {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'combobox',
                            flex: 1,
                            margin: '0 5 0 0',
                            labelWidth: 5,
                            itemId: 'codeFamile',
                            store: familles,
                            pageSize: 9999,
                            valueField: 'id',
                            displayField: 'libelle',
                            typeAhead: false,
                            queryMode: 'remote',
                            minChars: 2,
                            emptyText: 'Sélectionnez une famille'
                        },
                        {
                            xtype: 'tbseparator'
                        },
                        {
                            text: 'rechercher',
                            tooltip: 'rechercher',
                            itemId: 'rechercher',
                            scope: this,
                            iconCls: 'searchicon'
                        }
                        , {
                            xtype: 'tbseparator'
                        }, {
                            text: 'imprimer',
                            itemId: 'imprimer',
                            iconCls: 'printable',
                            tooltip: 'imprimer',
                            scope: this
                        }
                    ]
                }

            ],
            items: [
                {
                    xtype: 'gridpanel',
                    store: data,
                    features: [
                        {
                            ftype: 'summary'
                        }],
                    viewConfig: {
                        forceFit: true,
                        columnLines: true

                    },
                    columns: [
                        {
                            header: 'Code CIP',
                            dataIndex: 'code',
                            flex: 0.5,
                            summaryType: "count",
                            summaryRenderer: function (value) {

                                if (value > 0) {
                                    return "<b><span style='color:blue;'>TOTAL: </span></b>";
                                } else {
                                    return '';
                                }
                            }
                        },
                        {
                            header: 'Libellé',
                            dataIndex: 'libelle',
                            flex: 1.3,
                            summaryType: "count",
                            summaryRenderer: function (value) {

                                if (value > 0) {
                                    return "<b><span style='color:blue;'>" + value + " </span></b>";
                                } else {
                                    return '';
                                }
                            }
                        },
                        {
                            header: 'Famille',
                            dataIndex: 'description',
                            flex: 1
                        },
                        {
                            header: 'Nbre.VNO',
                            dataIndex: 'montantCumulTTC',
                            flex: 0.4,
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            },
                            summaryType: "sum",
                            summaryRenderer: function (value) {
                                if (value > 0) {
                                    return "<b><span style='color:blue;'>" + value + "  </span></b>";
                                } else {
                                    return '';
                                }
                            }
                        }
                        , {
                            header: 'Nbre.VO',
                            dataIndex: 'montantCumulHT',
                            flex: 0.4,
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            },
                            summaryType: "sum",
                            summaryRenderer: function (value) {
                                if (value > 0) {
                                    return "<b><span style='color:blue;'>" + value + "  </span></b>";
                                } else {
                                    return '';
                                }
                            }
                        },
                        {
                            header: 'Qté.Vendue',
                            dataIndex: 'montantCumulAchat',
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            },
                            flex: 0.4,
                            summaryType: "sum",
                            summaryRenderer: function (value) {
                                if (value > 0) {
                                    return "<b><span style='color:blue;'>" + value + "  </span></b>";
                                } else {
                                    return '';
                                }
                            }
                        },
                        {
                            header: 'N.Sortie',
                            dataIndex: 'montantCumulMarge',
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            },
                            flex: 0.4,
                            summaryType: "sum",
                            summaryRenderer: function (value) {
                                if (value > 0) {
                                    return "<b><span style='color:blue;'>" + value + "  </span></b>";
                                } else {
                                    return '';
                                }
                            }
                        },
                        {
                            header: 'U.Moy.Vente',
                            dataIndex: 'pourcentageCumulMage',
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            },
                            flex: 0.4
                        },
                        {
                            header: 'M.Vente',
                            dataIndex: 'montantTva',
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            },
                            flex: 0.6,
                            summaryType: "sum",
                            summaryRenderer: function (value) {
                                if (value > 0) {
                                    return "<b><span style='color:blue;'>" + value + "  </span></b>";
                                } else {
                                    return '';
                                }
                            }
                        },
                        {
                            header: 'Stock',
                            dataIndex: 'montantCumulTva',
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            },
                            flex: 0.4
                        }

                    ],
                    selModel: {
                        selType: 'cellmodel'
                    },
                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: data,
                        pageSize: 15,
                        dock: 'bottom',
                        displayInfo: true

                    }
                }
            ]

        });

        me.callParent(arguments);
    }

});
