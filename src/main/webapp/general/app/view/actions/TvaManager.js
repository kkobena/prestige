


/* global Ext */

Ext.define('testextjs.view.actions.TvaManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'statistiqueTVA',
    id: 'BalanceAgeGeneraleID',
    requires: [
        'testextjs.view.actions.tvaGrid',
        'testextjs.view.Report.resultatstva.action.TvaCharts',
        'testextjs.view.Report.resultatstva.action.TvaHT',
        'testextjs.view.Report.resultatstva.action.TvaTTC'

    ],
    title: 'R&eacute;sultats par TVA',
    frame: true,
    width: '98%',
    //height:'100%',
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'card',
    items: [{
            xtype: 'tva-grid'

        }, {
            xtype: 'panel',
            width: '100%',
            id: 'grapheTva',
            layout: {
                type: "hbox",
                align: "stretch"
            },
            items: [{
                    xtype: 'panel',
                    frame: true,
                    flex: 1,
                    layout: 'fit',
                    title: 'R&eacute;sultat Taux H.T',
                    items: [{
                            xtype: 'tvaHT-chart',
                        }]

                },
                {
                    xtype: 'panel',
                    frame: true,
                    layout: 'fit',
                    flex: 1,
                    title: 'R&eacute;sultat Tva',
                    items: [{
                            xtype: 'tva-chart'

                        }]

                },
                {
                    xtype: 'panel',
                    frame: true,
                    flex: 1,
                    layout: 'fit',
                    title: 'R&eacute;sultat Taux TTC',
                    items: [{
                            xtype: 'tvaTTC-chart'

                        }]

                }

            ]
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
                    id: 'dt_start_tva',
                    listeners: {
                        change: function() {
                            Ext.getCmp('dt_end_tva').setMinValue(this.getValue());
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
                    id: 'dt_end_tva',
                    listeners: {
                        change: function() {

                            Ext.getCmp('dt_start_tva').setMaxValue(this.getValue());
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
                        click: function() {
                            var grid = Ext.getCmp('tvaGrid');
                            // var india = Ext.create("Country", { name: "India", capital: "New Delhi" });
                            var dt_start_vente = Ext.getCmp('dt_start_tva').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_tva').getSubmitValue();
                            //   localStorage.clear();

                            grid.getStore().load({
                                params: {
                                    dt_start: dt_start_vente,
                                    dt_end: dt_end_vente
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
                    text: 'Voir tableau',
                    iconCls: 'tableauicon',
                    width: 110,
                    handler: function() {
                        Ext.getCmp("BalanceAgeGeneraleID").getLayout().setActiveItem(0);



                    }


                }
                ,
                {
                    xtype: 'tbseparator'
                }
                ,
                {
                    xtype: 'button',
                    text: 'Voir graphes',
                    iconCls: 'charticon16',
                    width: 110,
                    handler: function() {

                        Ext.getCmp("tvahtchartID").bindStore(Ext.getCmp('tvaGrid').getStore());
                        Ext.getCmp("tvachartID").bindStore(Ext.getCmp('tvaGrid').getStore());
                        Ext.getCmp("tvattcchartID").bindStore(Ext.getCmp('tvaGrid').getStore());

                        Ext.getCmp("BalanceAgeGeneraleID").getLayout().setActiveItem(1);


                    }


                }, {
                    xtype: 'tbseparator'
                }
                ,
                {
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'printable',
                    listeners: {
                        click: function() {

                            var dt_start_vente = Ext.getCmp('dt_start_tva').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_tva').getSubmitValue();
                           
                                var linkUrl = "../myBean?action=tvapdf&dt_start=" + dt_start_vente + "&dt_end=" + dt_end_vente;
                                window.open(linkUrl);
                            
                        }
                    }


                }


            ]
        }

    ]

});


