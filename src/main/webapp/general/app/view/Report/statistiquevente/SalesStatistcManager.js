
/* global Ext */


Ext.define('testextjs.view.Report.statistiquevente.SalesStatistcManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'statistiqueventemanager',
    id: "salesstat",
    requires: [
        'testextjs.view.Report.statistiquevente.statistiqueventemanager',
        'testextjs.view.Report.statistiquevente.action.VentesCharts',
        'testextjs.view.Report.statistiquevente.action.CumulChart'
    ],
    title: 'Statistiques des ventes',
    frame: true,
    width: '99.5%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'card',
    items: [{
            xtype: 'ventestatic-grid'
                    //flex: 1
        }, {
            xtype: 'panel',
            width: '100%',
            id: 'panelgraphe',
            //height: 570,
            //minHeight: 570,
            //maxHeight: 800,
            layout: 'card',
            items: [
                {
                    xtype: 'ventes-chart'
                }, {
                    xtype: 'cumul-chart'

                }
            ],
            dockedItems: [{
                    xtype: 'toolbar',
                    dock: 'top',//rp_tableau_pharmacien_achat_ventes
                    items: [{
                            xtype: 'combo',
                            value: 'Graphe Mensuel',
                            flex: 1,
                            reference: 'comboFilter',
                            labelWidth: 60,
                            fieldLabel: 'Filtrer par',
                            store: ['Graphe Mensuel', 'Cumul'],
                            listeners: {
                                select: function() {
                                    if (this.getValue() === "Graphe Mensuel") {
                                        Ext.getCmp('panelgraphe').getLayout().setActiveItem(0);
                                    } else {
                                        Ext.getCmp('panelgraphe').getLayout().setActiveItem(1);

                                    }
                                }
                            }

                        }]
                }]



        }


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
                    id: 'dt_start_vente',
                    listeners: {
                        change: function() {
                            Ext.getCmp('dt_end_vente').setMinValue(this.getValue());
                        }
                    }

                }
                , {
                    xtype: 'tbseparator'
                },
                
              {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date fin',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Au',
                    labelWidth: 20,
                    flex: 0.7,
                    id: 'dt_end_vente',
                    listeners: {
                        change: function() {

                            Ext.getCmp('dt_start_vente').setMaxValue(this.getValue());
                        }
                    }

                }, {
                    xtype: 'tbseparator'
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
                        click: function() {
                            var grid = Ext.getCmp('Grid_venteStatistiquevente_ID');
                            var dt_start_vente = Ext.getCmp('dt_start_vente').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_vente').getSubmitValue();

                            grid.getStore().load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente
                                }
                            });
                        }
                    }


                }, {
                    xtype: 'tbseparator'
                }
                ,
                {
                    xtype: 'button',
                    text: 'Voir graphes',
                    iconCls: 'charticon16',
                    width: 110,
                    handler: function() {

                        if (this.text === 'Voir graphes') {
                            //  tabPanelIcon.items.get(0).tab.setIconCls('icon-tick');
                            var graphestore = Ext.getCmp("venteschartID").getStore();
                            var graphecumul = Ext.getCmp("cumulchartID").getStore();
                            var dt_start_vente = Ext.getCmp('dt_start_vente').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_vente').getSubmitValue();

                            graphestore.load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente
                                }
                            });
                            graphecumul.load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente
                                }
                            });

                            Ext.getCmp("salesstat").getLayout().setActiveItem(1);
                            this.setText('Voir tableau');
                        } else {

                            Ext.getCmp("salesstat").getLayout().setActiveItem(0);
                            this.setText('Voir graphes');
                        }

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
                        click: function() {

                            var dt_start_vente = Ext.getCmp('dt_start_vente').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_vente').getSubmitValue();
                            
                                var linkUrl = "../webservices/Report/statistiquevente/ws_statistiquevente_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente;
                                window.open(linkUrl);
                            
                        }
                    }


                }


            ]
        }

    ]

});


