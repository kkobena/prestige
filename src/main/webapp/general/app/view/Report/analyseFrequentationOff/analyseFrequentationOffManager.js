/* global Ext */

Ext.define('testextjs.view.Report.analyseFrequentationOff.analyseFrequentationOffManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'analyseFrequentationOffManager',
    id: 'analyseFrequentationOffManagerID',
    requires: [
        'testextjs.view.Report.analyseFrequentationOff.VistorGrid',
//        'testextjs.view.Report.analyseFrequentationOff.action.VisitorCharts',
//              'testextjs.view.Report.analyseFrequentationOff.action.AreaVisitorCharts'

    ],
    title: 'Analyse de fréquentation par plage Horaire',
    frame: true,
    width: '98%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'visitor-grid'

        }/*, {
         xtype: 'panel',
         width: '100%',
         id: 'grapheVisitor',
         layout: 'fit',
         items: [
         
         {
         xtype: 'visitorarea-chart'
         }
         ]
         }*/


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Du',
                    labelWidth: 20,
                    flex: 0.7,
                    id: 'dt_start_Visitor',
                    listeners: {
                        change: function () {
                            Ext.getCmp('dt_end_Visitor').setMinValue(this.getValue());
                        }
                    }

                }, {
                    xtype: 'tbseparator'
                }

                ,
                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date fin',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Au',
                    labelWidth: 20,
                    flex: 0.7,
                    id: 'dt_end_Visitor',
                    listeners: {
                        change: function () {

                            Ext.getCmp('dt_start_Visitor').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('VisitorGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_Visitor').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_Visitor').getSubmitValue();


                            grid.getStore().load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente

                                },
                                callback: function (records) {
                                    if (records.length > 0) {
                                        Ext.getCmp('vistorsbtn').show();


                                    } else {
                                        Ext.getCmp('vistorsbtn').hide();

                                    }



                                }
                            });
                        }
                    }


                }


                , {
                    xtype: 'tbseparator'
                }
                ,
                {
                    xtype: 'button',
                    id: 'vistorsbtn',
                    text: 'Voir graphes',
                   
                    iconCls: 'charticon16',
                    width: 110,
                    handler: function () {
                        var UN_AMOUNT = 0, DEUX_AMOUNT = 0, TROIS_AMOUNT = 0, QUATRE_AMOUNT = 0, CINQ_AMOUNT = 0, SIX_AMOUNT = 0, SEPT_AMOUNT = 0, HUIT_AMOUNT = 0, NEUF_AMOUNT = 0, DIX_AMOUNT;
                        var UN_PANMOY = 0, DEUX_PANMOY = 0, TROIS_PANMOY = 0, QUATRE_PANMOY = 0, CINQ_PANMOY = 0, SIX_PANMOY = 0, SEPT_PANMOY = 0, HUIT_PANMOY = 0, NEUF_PANMOY = 0, DIX_PANMOY;
                        var visitorsStore = Ext.getCmp('VisitorGrid').getStore();
                        visitorsStore.each(function (record) {
                            UN_AMOUNT += Number(record.get("UN").split('_')[0]);
                            UN_PANMOY += Number(record.get("UN").split('_')[2]);
                            DEUX_AMOUNT += Number(record.get("DEUX").split('_')[0]);
                            DEUX_PANMOY += Number(record.get("DEUX").split('_')[2]);
                            TROIS_AMOUNT += Number(record.get("TROIS").split('_')[0]);
                            TROIS_PANMOY += Number(record.get("TROIS").split('_')[2]);
                            QUATRE_AMOUNT += Number(record.get("QUATRE").split('_')[0]);
                            QUATRE_PANMOY += Number(record.get("QUATRE").split('_')[2]);
                            CINQ_AMOUNT += Number(record.get("CINQ").split('_')[0]);
                            CINQ_PANMOY += Number(record.get("CINQ").split('_')[2]);
                            SIX_AMOUNT += Number(record.get("SIX").split('_')[0]);
                            SIX_PANMOY += Number(record.get("SIX").split('_')[2]);
                            SIX_AMOUNT += Number(record.get("SIX").split('_')[0]);
                            SIX_PANMOY += Number(record.get("SIX").split('_')[2]);
                            SEPT_AMOUNT += Number(record.get("SEPT").split('_')[0]);
                            SEPT_PANMOY += Number(record.get("SEPT").split('_')[2]);
                            HUIT_AMOUNT += Number(record.get("HUIT").split('_')[0]);
                            HUIT_PANMOY += Number(record.get("HUIT").split('_')[2]);
                            NEUF_AMOUNT += Number(record.get("NEUF").split('_')[0]);
                            NEUF_PANMOY += Number(record.get("NEUF").split('_')[2]);
                            DIX_AMOUNT += Number(record.get("DIX").split('_')[0]);
                            DIX_PANMOY += Number(record.get("DIX").split('_')[2]);

                        });
                        var chartstore = Ext.create('Ext.data.Store', {
                            fields: ['HORAIRE', 'MONTANT VENTES', 'PANIER MOYEN'],
                            data: [
                                {HORAIRE: '7:00-8:59',
                                    'MONTANT VENTES': UN_AMOUNT,
                                    'PANIER MOYEN': UN_PANMOY

                                }, {HORAIRE: '9:00-10:59',
                                    'MONTANT VENTES': DEUX_AMOUNT,
                                    'PANIER MOYEN': DEUX_PANMOY

                                }, {HORAIRE: '11:00-13:59',
                                    'MONTANT VENTES': TROIS_AMOUNT,
                                    'PANIER MOYEN': TROIS_PANMOY

                                }, {HORAIRE: '14:00-15:59',
                                    'MONTANT VENTES': QUATRE_AMOUNT,
                                    'PANIER MOYEN': QUATRE_PANMOY

                                }, {HORAIRE: '16:00-16:59',
                                    'MONTANT VENTES': CINQ_AMOUNT,
                                    'PANIER MOYEN': CINQ_PANMOY

                                }, {HORAIRE: '17:00-17:59',
                                    'MONTANT VENTES': SIX_AMOUNT,
                                    'PANIER MOYEN': SIX_PANMOY

                                }, {HORAIRE: '18:00-18:59',
                                    'MONTANT VENTES': SEPT_AMOUNT,
                                    'PANIER MOYEN': SEPT_PANMOY

                                }, {HORAIRE: '19:00-19:59',
                                    'MONTANT VENTES': HUIT_AMOUNT,
                                    'PANIER MOYEN': HUIT_PANMOY
                                }, {HORAIRE: '20:00-23:59',
                                    'MONTANT VENTES': NEUF_AMOUNT,
                                    'PANIER MOYEN': NEUF_PANMOY
                                }, {HORAIRE: '00:00-6:59',
                                    'MONTANT VENTES': DIX_AMOUNT,
                                    'PANIER MOYEN': DIX_PANMOY
                                }

                            ]
                        });

                        var win = Ext.create("Ext.window.Window", {
                            title: "Graphe des donn&eacute;es cumul&eacute;es",
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


                },
                {
                    xtype: 'tbseparator'
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

                            var dt_start_vente = Ext.getCmp('dt_start_Visitor').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_Visitor').getSubmitValue();

                            var linkUrl = "../webservices/Report/visitorstatistics/ws_visitorstatistics_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        }

    ]

});


