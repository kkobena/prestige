
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.ventesocietereglement.ventesocietereglementGrid', {
    extend: 'Ext.grid.Panel', 
    alias: 'widget.ventesociete-grid',
    initComponent: function () {
        var store = Ext.create('testextjs.store.Statistics.ventesocietes');

        Ext.apply(this, {
            features: [
                {
                    ftype: 'grouping',
                    collapsible: true,
                    groupHeaderTpl: "ANNEE : {[values.rows[0].data.ANNEE]}",
                    hideGroupedHeader: true
                   
                }],
            id: 'ventesocieteGrid',
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
                    header: 'Ann&eacute;e',
                    dataIndex: 'ANNEE',
                    width: 60

                },
                {
                    header: 'Client',
                    dataIndex: 'LIBELLE',
                    width: 200
                },
                {
                    text: 'Janvier',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'JANVIER',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'JANVIER_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                }

                ,
                {
                    text: 'F&eacute;vrier',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'FEVRIER',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'FEVRIER_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                }
                ,
                {
                    text: 'Mars',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'MARS',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'MARS_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                },
                {
                    text: 'Avril',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'AVRIL',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'AVRIL_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                }
                ,
                {
                    text: 'Mai',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'MAI',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'MAI_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                },
                {
                    text: 'Juin',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'JUIN',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'JUIN_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                },
                {
                    text: 'Juillet',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'JUIELLET',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'JUIELLET_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                },
                {
                    text: 'Ao&ucirc;t',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'AOUT',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'AOUT_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }



                    ]
                }
                ,
                {
                    text: 'Septembre',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'SET',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'SET_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                }
                ,
                {
                    text: 'Octobre',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'OCT',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'OCT_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                },
                {
                    text: 'Novembre',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'NOV',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'NOV_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        }


                    ]
                },
                {
                    text: 'D&eacute;cembre',
                    columns: [
                        {
                            text: 'Ventes',
                            dataIndex: 'DEC',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1
                        },
                        {
                            text: 'Encais.',
                            dataIndex: 'DEC_AVOIR',
                            align: 'right',
                            flex: 1,
                            renderer: amountformat
                        },
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
                                        var rec = grid.getStore().getAt(rowIndex), id = rec.get('id'),
                                                annee = rec.get('ANNEE');

                                        var chartstore = Ext.create('Ext.data.Store', {
                                            fields: ['MOIS', 'Montant Ventes', 'Montant Encaissement',
                                            ],
                                            data: [
                                                {MOIS: '01/' + annee,
                                                    'Montant Ventes': rec.get('JANVIER'),
                                                    'Montant Encaissement': rec.get('JANVIER_AVOIR')
                                                },
                                                {MOIS: '02/' + annee,
                                                    'Montant Ventes': rec.get('FEVRIER'),
                                                    'Montant Encaissement': rec.get('FEVRIER_AVOIR')
                                                },
                                                {MOIS: '03/' + annee,
                                                    'Montant Ventes': rec.get('MARS')
                                                    , 'Montant Encaissement': rec.get('MARS_AVOIR')
                                                },
                                                {MOIS: '04/' + annee,
                                                    'Montant Ventes': rec.get('AVRIL'), 'Montant Encaissement': rec.get('AVRIL_AVOIR')
                                                },
                                                {MOIS: '05/' + annee,
                                                    'Montant Ventes': rec.get('MAI'), 'Montant Encaissement': rec.get('MAI_AVOIR')
                                                },
                                                {MOIS: '06/' + annee,
                                                    'Montant Ventes': rec.get('JUIN'), 'Montant Encaissement': rec.get('JUIN_AVOIR')
                                                },
                                                {MOIS: '07/' + annee,
                                                    'Montant Ventes': rec.get('JUIELLET'), 'Montant Encaissement': rec.get('JUIELLET_AVOIR')
                                                },
                                                {MOIS: '08/' + annee,
                                                    'Montant Ventes': rec.get('AOUT'), 'Montant Encaissement': rec.get('AOUT_AVOIR')
                                                }, {MOIS: '09/' + annee,
                                                    'Montant Ventes': rec.get('SET'), 'Montant Encaissement': rec.get('SET_AVOIR')
                                                },
                                                {MOIS: '10/' + annee,
                                                    'Montant Ventes': rec.get('OCT'), 'Montant Encaissement': rec.get('OCT_AVOIR')
                                                },
                                                {MOIS: '11/' + annee,
                                                    'Montant Ventes': rec.get('NOV'), 'Montant Encaissement': rec.get('NOV_AVOIR')
                                                },
                                                {MOIS: '12/' + annee,
                                                    'Montant Ventes': rec.get('DEC'), 'Montant Encaissement': rec.get('DEC_AVOIR')
                                                }

                                            ]
                                        });



                                        var win = Ext.create("Ext.window.Window", {
                                            title: "Graphe des Ventes de : [" + rec.get('LIBELLE') + "]",
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
                                                            fields: ['Montant Ventes', 'Montant Encaissement'],
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
                                                            yField: 'Montant Ventes',
                                                            tips: {
                                                                trackMouse: true,
                                                                width: 350,
                                                                renderer: function (storeItem, item) {
                                                                    this.setTitle("Montant Ventes du " + storeItem.get('MOIS') + " est de : " + amountformat(storeItem.get('Montant Ventes')) + " F");
                                                                    // this.update(storeItem.get('CA'));

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
                                                        },
                                                        {
                                                            type: 'line',
                                                            axis: 'left',
                                                            xField: 'MOIS',
                                                            yField: 'Montant Encaissement',
                                                            tips: {
                                                                trackMouse: true,
                                                                width: 350,
                                                                renderer: function (storeItem, item) {
                                                                    this.setTitle("Montant Encaissement du " + storeItem.get('MOIS') + " est de : " + amountformat(storeItem.get('Montant Encaissement')) + " F");
                                                                    // this.update(storeItem.get('CA'));

                                                                }
                                                            },
                                                            style: {
                                                                fill: '#FFDA64',
                                                                stroke: '#FFDA64',
                                                                'stroke-width': 3
                                                            },
                                                            markerConfig: {
                                                                type: 'circle',
                                                                size: 4,
                                                                radius: 4,
                                                                'stroke-width': 0,
                                                                fill: '#FF9F3A ',
                                                                stroke: '#FF9F3A'
                                                            }
                                                        }
                                                    ]


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


                    ]
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, 
                dock: 'bottom',
                displayInfo: true,
                 listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            dt_start_vente: '',
                            dt_end_vente: '',
                            search_value:''
                        };
                        var dt_start = Ext.getCmp('dt_start_ventesociete').getSubmitValue();
                        var dt_end = Ext.getCmp('dt_end_ventesociete').getSubmitValue();
                        var search_value = Ext.getCmp('rechventesociete').getValue();
                        myProxy.setExtraParam('dt_start_vente', dt_start);
                        myProxy.setExtraParam('dt_end_vente', dt_end);
                         myProxy.setExtraParam('search_value', search_value);

                    }

                }
            }
        });
        this.callParent();
    }
});


