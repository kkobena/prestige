
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.statqties.qtygrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.statproduct-grid',

    initComponent: function () {
        var store = Ext.create('testextjs.store.Statistics.OrderQty');
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
        Ext.apply(this, {

            id: 'statproductGrid',
            store: store,
            viewConfig: {
                forceFit: true,
                emptyText: '<h1 style="margin:10px 10px 10px 30%;">Pas de donn&eacute;es</h1>'
            },

            columns: [
                {
                    header: 'id',
                    dataIndex: 'id',
                    hidden: true
                },
                {
                    header: 'CIP',
                    dataIndex: 'CIP',
                    flex: 1

                }, {
                    header: 'LIBELLE',
                    dataIndex: 'DESC',
                    flex: 2

                },
                {
                    header: 'JANVIER',
                    dataIndex: 'JANUARY',
                    flex: 1, renderer: amountformat,
                    align: 'right'

                },
                {
                    header: 'FEVRIER',
                    dataIndex: 'FEBRUARY',
                    flex: 1, renderer: amountformat,
                    align: 'right'
                },
                {
                    text: 'MARS',
                    dataIndex: 'MARCH',
                    flex: 1,
                    renderer: amountformat,
                    align: 'right'
                }
                , {
                    text: 'AVRIL',
                    dataIndex: 'APRIL',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }, {
                    text: 'MAI',
                    dataIndex: 'MAY',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'JUIN',
                    dataIndex: 'JUNE',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'JUILLET',
                    dataIndex: 'JULY',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }
                ,
                {
                    text: 'AOÛT',
                    dataIndex: 'AUGUST',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }
                ,
                {
                    text: 'SEPTEMBRE',
                    dataIndex: 'SEPTEMBER',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'OCTOBRE',
                    dataIndex: 'OCTOBER',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'NOVEMBRE',
                    dataIndex: 'NOVEMBER',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }, {
                    text: 'DECEMBRE',
                    dataIndex: 'DECEMBER',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }, {
                    xtype: 'actioncolumn',
                    width: 40,
                    items: [
                        {
                            getClass: function (v, meta, rec) {
                                return 'charticon16';
                            },
                            getTip: function (v, meta, rec) {

                                return 'Voir graphe';
                            },
                            handler: function (grid, rowIndex, colIndex) {
                                var rec = grid.getStore().getAt(rowIndex), id = rec.get('id'),
                                        annee = rec.get('ANNEE');
                                var chartstore = Ext.create('Ext.data.Store', {
                                    fields: ['MOIS', 'qte'
                                    ],
                                    data: [
                                        {MOIS: 'JAN/' + annee,
                                            'qte': rec.get('JANUARY')
                                        },
                                        {MOIS: 'FEV/' + annee,
                                            'qte': rec.get('FEBRUARY')
                                        },
                                        {MOIS: 'MARS/' + annee,
                                            'qte': rec.get('MARCH')
                                        },
                                        {MOIS: 'AVR/' + annee,
                                            'qte': rec.get('APRIL')
                                        },
                                        {MOIS: 'MAI/' + annee,
                                            'qte': rec.get('MAY')
                                        },
                                        {MOIS: 'JUIN/' + annee,
                                            'qte': rec.get('JUNE')
                                        },
                                        {MOIS: 'JUIL/' + annee,
                                            'qte': rec.get('JULY')
                                        },
                                        {MOIS: 'AOÛT/' + annee,
                                            'qte': rec.get('AUGUST')
                                        }, {MOIS: 'SEPT/' + annee,
                                            'qte': rec.get('SEPTEMBER')
                                        },
                                        {MOIS: 'OCT/' + annee,
                                            'qte': rec.get('OCTOBER')
                                        },
                                        {MOIS: 'NOV/' + annee,
                                            'qte': rec.get('NOVEMBER')
                                        },
                                        {MOIS: 'DEC/' + annee,
                                            'qte': rec.get('DECEMBER')
                                        }

                                    ]
                                });
                                var win = Ext.create("Ext.window.Window", {
                                    title: "Graphe des quantités vendues  de : [" + rec.get('DESC') + "]",
                                    modal: true,
                                    width: '80%',
                                    height: 550,
                                    maximizable: true,
                                    layout: 'fit',
                                    items: [
                                        {
                                            xtype: "chart",
                                            style: 'background:#fff',
                                            store: chartstore,
                                            animate: true,
                                            insetPadding: 30,
                                            legend: {
                                                position: 'bottom'
                                            }, axes: [{
                                                    type: 'Numeric',
                                                    minimum: 0,
                                                    position: 'left',
                                                    fields: ['qte'],
                                                    title: false,
                                                    grid: true,
                                                    label: {
                                                        renderer: Ext.util.Format.numberRenderer('0,0')

                                                    }
                                                }, {
                                                    type: 'Category',
                                                    position: 'bottom',
                                                    fields: ['MOIS'],
                                                    title: false

                                                }],
                                            series: [{
                                                    type: 'line',
                                                    axis: 'left',
                                                    xField: 'MOIS',
                                                    yField: 'qte',
                                                    tips: {
                                                        trackMouse: true,
                                                        width: 350,
                                                        renderer: function (storeItem, item) {
                                                            this.setTitle("Quantité Vente au " + storeItem.get('MOIS') + " est de : " + amountformat(storeItem.get('qte')) + " ");
                                                        }
                                                    },
                                                    style: {
                                                        fill: '#38B8BF',
                                                        stroke: '#38B8BF',
                                                        'stroke-width': 3
                                                    },
                                                    markerConfig: {
                                                        type: 'circle',
                                                        size: 4,
                                                        radius: 4,
                                                        'stroke-width': 0,
                                                        fill: '#38B8BF',
                                                        stroke: '#38B8BF'
                                                    }
                                                }]


                                        }
                                    ],
                                    buttons: [
                                        {
                                            text: "Fermer",
                                            handler: function () {
                                                win.close();
                                            }
                                        }
                                    ]
                                });
                                win.show();
                            }
                        }

                    ]
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
                            xtype: 'textfield',
                            id: 'rechQty',
                            flex: 1,
                            emptyText: 'Recherche',
                            listeners: {
                                specialKey: function (field, e, Familletion) {
                                    if (e.getKey() === e.ENTER) {
                                        var grid = Ext.getCmp('statproductGrid');

                                        var dt_end_vente = Ext.getCmp('cmbyears').getValue();
                                        let rayonId = Ext.getCmp('rayonsZone').getValue();
                                        grid.getStore().load({
                                            params: {
                                                rayonId: rayonId,
                                                year: dt_end_vente,
                                                search_value: field.getValue()
                                            }
                                        });
                                    }

                                }
                            }
                        }, {
                            xtype: 'tbseparator'
                        },
                        {
                            xtype: 'combobox',
                            flex: 1,
                            margin: '0 5 0 0',
                            labelWidth: 5,
                            id: 'rayonsZone',
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
                        }
                        ,
                        {
                            xtype: 'combo',

                            emptyText: 'Sélectionnez une année',

                            fieldLabel: 'Année',
                            labelWidth: 40,
                            flex: 1.5,
                            id: 'cmbyears',

                            value: new Date().getFullYear(),
                            valueField: 'YEAR',
                            displayField: 'YEAR',

                            store: Ext.create("Ext.data.Store", {
                                fields: [
                                    {
                                        name: 'id',
                                        type: 'string'
                                    },
                                    {
                                        name: 'YEAR',
                                        type: 'int'
                                    }


                                ],
                                pageSize: 10,
                                // autoLoad: true,
                                proxy: {
                                    type: 'ajax',
                                    url: '../webservices/Report/qtyorder/ws_years.jsp',
                                    reader: {
                                        type: 'json',
                                        root: 'data',
                                        totalProperty: 'total'
                                    }
                                }
                            }),
                            listeners: {
                                select: function () {

                                    var grid = Ext.getCmp('statproductGrid');

                                    var rechQty = Ext.getCmp('rechQty').getValue();

                                    grid.getStore().load({
                                        params: {

                                            year: this.getValue(),
                                            search_value: rechQty
                                        }
                                    });
                                }
                            }

                        }

                        , {
                            xtype: 'tbseparator'
                        },
                        {
                            // flex: 0.4,
                            width: 100,
                            xtype: 'button',
                            iconCls: 'searchicon',
                            text: 'Rechercher',
                            listeners: {
                                click: function () {
                                    var grid = Ext.getCmp('statproductGrid');

                                    var dt_end_vente = Ext.getCmp('cmbyears').getValue();
                                    var search_value = Ext.getCmp('rechQty').getValue();
                                    let rayonId = Ext.getCmp('rayonsZone').getValue();

                                    grid.getStore().load({
                                        params: {
                                            rayonId: rayonId,
                                            year: dt_end_vente,
                                            search_value: search_value
                                        }
                                    });
                                }
                            }


                        }, {
                            xtype: 'tbseparator'
                        }
                        , {
                            text: 'Exporter en excel',
                            itemId: 'btnExcel',
                            scope: this
                        }

                        ,
                        {
                            width: 100,
                            xtype: 'button',
                            text: 'Imprimer',
                            iconCls: 'printable',
                            
//                    glyph: 0xf1c1,
                            listeners: {
                                click: function () {

                                    var year = Ext.getCmp('cmbyears').getValue();
                                    var search_value = Ext.getCmp('rechQty').getValue();
                                    let rayonId = Ext.getCmp('rayonsZone').getValue();
                                    if (year === null) {
                                        year = '';
                                    }
                                    if (rayonId === null) {
                                        rayonId = '';
                                    }

                                    var linkUrl = "../webservices/Report/qtyorder/ws_generate_pdf.jsp" + "?year=" + year + "&search_value=" + search_value + "&rayonId=" + rayonId;
                                    window.open(linkUrl);

                                }
                            }


                        }


                    ]
                },

                {

                    xtype: 'pagingtoolbar',
                    store: store, // same store GridPanel is using
                    dock: 'bottom',
                    displayInfo: true,
                    listeners: {
                        beforechange: function (page, currentPage) {
                            var myProxy = this.store.getProxy();
                            myProxy.params = {
                                year: '',
                                search_value: '',
                                rayonId: ''
                            };
                            let rayonId = Ext.getCmp('rayonsZone').getValue();
                            var year = Ext.getCmp('cmbyears').getValue();
                            var search_value = Ext.getCmp('rechQty').getValue();
                            myProxy.setExtraParam('rayonId', rayonId);
                            myProxy.setExtraParam('year', year);
                            myProxy.setExtraParam('search_value', search_value);
                        }

                    }
                }]
        });
        this.callParent();
    }

});


