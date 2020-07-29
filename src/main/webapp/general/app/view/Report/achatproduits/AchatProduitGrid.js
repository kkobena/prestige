
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.achatproduits.AchatProduitGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.achatproduct-grid',
    initComponent: function () {
        var store = Ext.create('testextjs.store.Statistics.AchatProduits');

        Ext.apply(this, {
             features: [
                {
                    ftype: 'grouping',
                    collapsible: true,
                    groupHeaderTpl: "ANNEE : {[values.rows[0].data.ANNEE]}",
                    hideGroupedHeader: true
                   
                }],
            id: 'AchatproductGrid',
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
                    flex: 1

                },
                {
                    header: 'Libell&eacute; du produit',
                    dataIndex: 'LIBELLE',
                    flex: 2
                },
                {
                    text: 'Janvier',
                    dataIndex: 'JANVIER',
                    flex: 1,
                    renderer: amountformat,
                    align: 'right'
                }
                , {
                    text: 'F&eacute;vrier',
                    dataIndex: 'FEVRIER',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'Mars',
                    dataIndex: 'MARS',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }, {
                    text: 'Avril',
                    dataIndex: 'AVRIL',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }, {
                    text: 'Mai',
                    dataIndex: 'MAI',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'Juin',
                    dataIndex: 'JUIN',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'Juillet',
                    dataIndex: 'JUIELLET',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }
                ,
                {
                    text: 'Ao&ucirc;t',
                    dataIndex: 'AOUT',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }
                ,
                {
                    text: 'Septembre',
                    dataIndex: 'SEPT',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'Octobre',
                    dataIndex: 'OCT',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                },
                {
                    text: 'Novembre',
                    dataIndex: 'NOV',
                    align: 'right',
                    flex: 1,
                    renderer: amountformat
                }, {
                    text: 'D&eacute;cembre',
                    dataIndex: 'DEC',
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
                                    fields: ['MOIS', 'MONTANT'
                                    ],
                                    data: [
                                        {MOIS: '01/' + annee,
                                            'MONTANT': rec.get('JANVIER')
                                        },
                                        {MOIS: '02/' + annee,
                                            'MONTANT': rec.get('FEVRIER')
                                        },
                                        {MOIS: '03/' + annee,
                                            'MONTANT': rec.get('MARS')
                                        },
                                        {MOIS: '04/' + annee,
                                            'MONTANT': rec.get('AVRIL')
                                        },
                                        {MOIS: '05/' + annee,
                                            'MONTANT': rec.get('MAI')
                                        },
                                        {MOIS: '06/' + annee,
                                            'MONTANT': rec.get('JUIN')
                                        },
                                        {MOIS: '07/' + annee,
                                            'MONTANT': rec.get('JUIELLET')
                                        },
                                        {MOIS: '08/' + annee,
                                            'MONTANT': rec.get('AOUT')
                                        }, {MOIS: '09/' + annee,
                                            'MONTANT': rec.get('SEPT')
                                        },
                                        {MOIS: '10/' + annee,
                                            'MONTANT': rec.get('OCT')
                                        },
                                        {MOIS: '11/' + annee,
                                            'MONTANT': rec.get('NOV')
                                        },
                                        {MOIS: '12/' + annee,
                                            'MONTANT': rec.get('DEC')
                                        }

                                    ]
                                });



                                var win = Ext.create("Ext.window.Window", {
                                    title: "Graphe des Achats de : [" + rec.get('LIBELLE') + "]",
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
                                                    fields: ['MONTANT'],
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
                                                    yField: 'MONTANT',
                                                    tips: {
                                                        trackMouse: true,
                                                        width: 350,
                                                        renderer: function (storeItem, item) {
                                                            this.setTitle("Montant Achats du " + storeItem.get('MOIS') + " est de : " + amountformat(storeItem.get('MONTANT')) + " F");
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
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true,
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            dt_start_vente: '',
                            dt_end_vente: '',
                            search_value: ''
                        };
                        var search_value = Ext.getCmp('rechachatproduct').getValue();
                        var dt_start = Ext.getCmp('dt_start_achatproduct').getSubmitValue();
                        var dt_end = Ext.getCmp('dt_end_achatproduct').getSubmitValue();
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


