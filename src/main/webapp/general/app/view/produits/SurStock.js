/* global Ext */

Ext.define('testextjs.view.produits.SurStock', {
    extend: 'Ext.panel.Panel',
    xtype: 'SurStock',
    frame: true,
    title: 'Articles en  sur-stock',
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
                {name: 'filterId', type: 'string'},
                {name: 'filterLibelle', type: 'string'},
                {name: 'stock', type: 'number'},
                {name: 'prixAchat', type: 'number'},
                {name: 'prixVente', type: 'number'},
                {name: 'codeGrossiste', type: 'string'},
                {name: 'lastHour', type: 'string'},
                {name: 'consommation', type: 'number'},
                {name: 'qteSurplus', type: 'number'},
                {name: 'qteVendue', type: 'number'},
                {name: 'coefficient', type: 'number'},
                {name: 'stockMoyen', type: 'number'},
                {name: 'valeurVente', type: 'number'},
                {name: 'valeurQteSurplus', type: 'number'},
                {name: 'consommationsThree', type: 'number'},
                {name: 'consommationsTwo', type: 'number'},
                {name: 'consommationsOne', type: 'number'},
                {name: 'consommationUn', type: 'number'}
               
            ],
            pageSize: 99999,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/fichearticle/surstocks',
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
                            xtype: 'numberfield',
                            itemId: 'stock',
                            flex: 1,
                            fieldLabel: 'Nbre.mois.conso',
                            enableKeyEvents: true,
                            hideTrigger: true,
                            emptyText: 'Taper la nbre de mois ici'

                        }
                        , {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'numberfield',
                            fieldLabel: 'Nbre.mois.stock',
                            itemId: 'nbreConsommation',
                            hideTrigger: true,
                            flex: 1,
                            value: 3,
                            enableKeyEvents: true,
                            emptyText: 'Taper la nbre de mois stock moyen'

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
                    autoScroll: true,
                    store: data,
                    viewConfig: {
                        forceFit: true,
                        columnLines: true

                    },
                    columns: [
                        {
                            header: 'Code CIP',
                            dataIndex: 'code',
                            flex: 0.5
                        },
                        {
                            header: 'Libellé',
                            dataIndex: 'libelle',
                            flex: 1.3
                        },
                        {
                            header: 'Code. Grossiste',
                            dataIndex: 'codeGrossiste',
                            flex: 1
                        },
                        {
                            header: 'Qté.Vendue',
                            dataIndex: 'consommation',
                            flex: 0.4,
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            }

                        },

                        {
                            header: 'Prix.Vente',
                            dataIndex: 'prixVente',
                            flex: 0.4,
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            }

                        }
                        , {
                            header: 'Prix.Achat',
                            dataIndex: 'prixAchat',
                            flex: 0.4,
                            align: 'right',
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            }

                        },
                        {
                            header: 'Qté.Stock',
                            dataIndex: 'stock',
                            align: 'right',
                            flex: 0.5,
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            }

                        },
                        {
                            header: 'Coefficient',
                            dataIndex: 'coefficient',
                            align: 'right',
                            flex: 0.5

                        },
                        {
                            header: 'Stock/Moyen',
                            dataIndex: 'stockMoyen',
                            align: 'right',
                            flex: 0.5

                        },
                        {
                            header: 'Qté.surplus',
                            dataIndex: 'qteSurplus',
                            align: 'right',
                            flex: 0.5,
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            }
                        },
                        {
                            header: 'Valeur.Achat',
                            dataIndex: 'valeurQteSurplus',
                            align: 'right',
                            flex: 1,
                            renderer: function (v) {
                                return Ext.util.Format.number(v, '0,000.');
                            }
                        }

                    ],
                    selModel: {
                        selType: 'cellmodel'
                    },
                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: data,
                        pageSize: 99999,
                        dock: 'bottom',
                        displayInfo: true

                    }
                }
            ]

        });

        me.callParent(arguments);
    }

});



