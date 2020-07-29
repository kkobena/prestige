
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.analyseFrequentationOff.VistorGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.visitor-grid',
    initComponent: function () {
        var store = Ext.create('testextjs.store.Statistics.VISITORSTATISTICS');

        Ext.apply(this, {
            id: 'VisitorGrid',
            store: store,
            features: [
                {
                    ftype: 'grouping',
                    groupHeaderTpl: "{[values.rows[0].data.JOUR]}",
                    hideGroupedHeader: true

                }],
            viewConfig: {
                emptyText: '<h1 style="margin:10px 10px 10px 30%;">Pas de donn&eacute;es</h1>'
            },
            columns: [
                {
                    header: 'Jour',
                    dataIndex: 'JOUR',
                    flex: 0.4
                },
                {
                    header: 'OP',
                    dataIndex: 'OP',
                    flex: 1.4

                },
                {
                    text: '',
                    dataIndex: 'VALUES',
                    flex: 0.7,
                    renderer: function (v) {
                        return v.split('_')[0] + "<br>" + v.split('_')[1] + "<br>" + v.split('_')[2] + "<br>" + v.split('_')[3];

                    }

                }
                , {
                    text: '7:00 - 8:59',
                    dataIndex: 'UN',
                    flex: 1,
                    align: 'right',
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                },
                {
                    text: '9:00 - 10:59',
                    dataIndex: 'DEUX',
                    align: 'right',
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                },
                {
                    text: '11:00 - 13:59',
                    dataIndex: 'TROIS',
                    align: 'right',
                    flex: 1,
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                },
                {
                    text: '14:00 - 15:59',
                    dataIndex: 'QUATRE',
                    align: 'right',
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                }
                ,
                {
                    text: '16:00 - 16:59',
                    dataIndex: 'CINQ',
                    align: 'right',
                    flex: 1,
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                },
                {
                    text: '17:00 - 17:59',
                    dataIndex: 'SIX',
                    flex: 1,
                    align: 'right',
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                },
                {
                    text: '18:00 - 18:59',
                    dataIndex: 'SEPT',
                    flex: 1,
                    align: 'right',
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                },
                {
                    text: '19:00 - 19:59',
                    align: 'right',
                    dataIndex: 'HUIT',
                    flex: 1,
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                }, {
                    text: '20:00 - 23:59',
                    dataIndex: 'NEUF',
                    align: 'right',
                    flex: 1,
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                }, {
                    text: '00:00 - 6:59',
                    dataIndex: 'DIX',
                    align: 'right',
                    flex: 1,
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                }
                , {
                    text: 'Total',
                    dataIndex: 'TOTAL',
                    align: 'right',
                    flex: 1,
                    renderer: function (v) {
                        return amountformat(v.split('_')[0]) + "<br>" + amountformat(v.split('_')[1]) + "<br>" + amountformat(v.split('_')[2]) + "<br>" + amountformat(v.split('_')[3]);

                    }
                }/*,
                {
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
                                var rec = grid.getStore().getAt(rowIndex), id = rec.get('id');
                                var chartstore = Ext.create('Ext.data.Store', {
                                    fields: ['HORAIRE', 'MONTANT VENTES', 'PANIER MOYEN'],
                                    data: [
                                        {HORAIRE: '7:00-8:59',
                                            'MONTANT VENTES': Number(rec.get('UN').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('UN').split('_')[2])

                                        }, {HORAIRE: '9:00-10:59',
                                            'MONTANT VENTES': Number(rec.get('DEUX').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('DEUX').split('_')[2])

                                        }, {HORAIRE: '11:00-13:59',
                                            'MONTANT VENTES': Number(rec.get('TROIS').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('TROIS').split('_')[2])

                                        }, {HORAIRE: '14:00-15:59',
                                            'MONTANT VENTES': Number(rec.get('QUATRE').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('QUATRE').split('_')[2])

                                        }, {HORAIRE: '16:00-16:59',
                                            'MONTANT VENTES': Number(rec.get('CINQ').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('CINQ').split('_')[2])

                                        }, {HORAIRE: '17:00-17:59',
                                            'MONTANT VENTES': Number(rec.get('SIX').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('SIX').split('_')[2])

                                        }, {HORAIRE: '18:00-18:59',
                                            'MONTANT VENTES': Number(rec.get('SEPT').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('SEPT').split('_')[2])

                                        }, {HORAIRE: '19:00-19:59',
                                            'MONTANT VENTES': Number(rec.get('HUIT').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('HUIT').split('_')[2])
                                        }, {HORAIRE: '20:00-23:59',
                                            'MONTANT VENTES': Number(rec.get('NEUF').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('NEUF').split('_')[2])
                                        }, {HORAIRE: '00:00-6:59',
                                            'MONTANT VENTES': Number(rec.get('DIX').split('_')[0]),
                                            'PANIER MOYEN': Number(rec.get('DIX').split('_')[2])
                                        }

                                    ]
                                });



                                var win = Ext.create("Ext.window.Window", {
                                    title: "Graphe de [" + rec.get('JOUR') + "]",
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
                                            legend: {
                                                position: 'bottom'
                                            }, axes: [{
                                                    type: 'Numeric',
                                                    grid: true,
                                                    position: 'left', // the axe position
                                                    fields: ['MONTANT VENTES',
                                                        'PANIER MOYEN'

                                                    ],
//                    title: 'Number of Invoices',
                                                    minimum: 0
                                                }, {
                                                    type: 'Category',
                                                    position: 'bottom', // the axe position
                                                    fields: ['HORAIRE'] // the mapping data for this axe
//                    title: 'Month of the Year'
                                                }],
                                            series: [{
                                                    type: 'column',
                                                    axis: 'left',
                                                    xField: 'HORAIRE',
                                                    yField: ['MONTANT VENTES',
                                                       'PANIER MOYEN'
                                                    ],
                                                    style: {
                                                        opacity: 0.93
                                                    },
                                                    highlight: true,
                                                    tips: {
                                                        trackMouse: true,
                                                        style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                                                        width: 210,
                                                        // height: 48,
                                                        renderer: function (storeItem, item) {
                                                            this.setTitle(storeItem.get('HORAIRE') + ' </br> ' + item.yField + ': ' + storeItem.get(item.yField)
                                                                    );
                                                        }
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
                }*/







            ],
            selModel: {
                selType: 'cellmodel'
            },
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                pageSize: 5,
                displayInfo: true
                ,
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            dt_start_vente: '',
                            dt_end_vente: ''
                        };
                        var dt_start = Ext.getCmp('dt_start_Visitor').getSubmitValue();
                        var dt_end = Ext.getCmp('dt_end_Visitor').getSubmitValue();
                        myProxy.setExtraParam('dt_start_vente', dt_start);
                        myProxy.setExtraParam('dt_end_vente', dt_end);

                    }

                }
            }
        });

        this.callParent();


    }
});


