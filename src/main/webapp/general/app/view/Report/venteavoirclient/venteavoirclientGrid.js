
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.venteavoirclient.venteavoirclientGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.venteavoirclient-grid',
    initComponent: function () {
        var store = Ext.create('testextjs.store.Statistics.venteavoirclient');
        Ext.apply(this, {
            features: [
                {
                    ftype: 'grouping',
                    collapsible: true,
                    groupHeaderTpl: "ANNEE : {[values.rows[0].data.ANNEE]}",
                    hideGroupedHeader: true
                   
                }],
            id: 'venteavoirclientGrid',
            store: store,
            viewConfig: {
                forceFit: true,
                emptyText: '<h1 style="margin:10px 10px 10px 30%">Pas de donn&eacute;es</h1>'
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                            text: 'Avoirs',
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
                                            fields: ['MOIS', 'Montant Achat', 'Montant Avoir',
                                            ],
                                            data: [
                                                {MOIS: '01/' + annee,
                                                    'Montant Achat': rec.get('JANVIER'),
                                                    'Montant Avoir': rec.get('JANVIER_AVOIR')
                                                },
                                                {MOIS: '02/' + annee,
                                                    'Montant Achat': rec.get('FEVRIER'),
                                                    'Montant Avoir': rec.get('FEVRIER_AVOIR')
                                                },
                                                {MOIS: '03/' + annee,
                                                    'Montant Achat': rec.get('MARS')
                                                    , 'Montant Avoir': rec.get('MARS_AVOIR')
                                                },
                                                {MOIS: '04/' + annee,
                                                    'Montant Achat': rec.get('AVRIL'), 'Montant Avoir': rec.get('AVRIL_AVOIR')
                                                },
                                                {MOIS: '05/' + annee,
                                                    'Montant Achat': rec.get('MAI'), 'Montant Avoir': rec.get('MAI_AVOIR')
                                                },
                                                {MOIS: '06/' + annee,
                                                    'Montant Achat': rec.get('JUIN'), 'Montant Avoir': rec.get('JUIN_AVOIR')
                                                },
                                                {MOIS: '07/' + annee,
                                                    'Montant Achat': rec.get('JUIELLET'), 'Montant Avoir': rec.get('JUIELLET_AVOIR')
                                                },
                                                {MOIS: '08/' + annee,
                                                    'Montant Achat': rec.get('AOUT'), 'Montant Avoir': rec.get('AOUT_AVOIR')
                                                }, {MOIS: '09/' + annee,
                                                    'Montant Achat': rec.get('SET'), 'Montant Avoir': rec.get('SET_AVOIR')
                                                },
                                                {MOIS: '10/' + annee,
                                                    'Montant Achat': rec.get('OCT'), 'Montant Avoir': rec.get('OCT_AVOIR')
                                                },
                                                {MOIS: '11/' + annee,
                                                    'Montant Achat': rec.get('NOV'), 'Montant Avoir': rec.get('NOV_AVOIR')
                                                },
                                                {MOIS: '12/' + annee,
                                                    'Montant Achat': rec.get('DEC'), 'Montant Avoir': rec.get('DEC_AVOIR')
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
                                                            fields: ['Montant Achat', 'Montant Avoir'],
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
                                                            yField: 'Montant Achat',
                                                            tips: {
                                                                trackMouse: true,
                                                                width: 350,
                                                                renderer: function (storeItem, item) {
                                                                    this.setTitle("Montant Achat du " + storeItem.get('MOIS') + " est de : " + amountformat(storeItem.get('Montant Achat')) + " F");
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
                                                            yField: 'Montant Avoir',
                                                            tips: {
                                                                trackMouse: true,
                                                                width: 350,
                                                                renderer: function (storeItem, item) {
                                                                    this.setTitle("Montant Avoirs du " + storeItem.get('MOIS') + " est de : " + amountformat(storeItem.get('Montant Avoir')) + " F");
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
                displayInfo: true
            }
        });

        this.callParent();


    }


})


