

/* global Ext */

Ext.define('testextjs.view.Report.analyseventestock.analyseventestockmanager', {
    extend: 'Ext.panel.Panel',
    xtype: 'analyseventemanager',
    id: 'analyseventemanagerID',
    requires: [
        'testextjs.view.Report.analyseventestock.AnalyseGrid',
        'testextjs.view.Report.analyseventestock.analyseCharts'

    ],
    title: 'Analyse vente/stock d’une famille',
    frame: true,
    width: '99.5%',
    //height:'100%',
    minHeight: 570,
    maxHeight: 800,
    // cls: 'custompanel',
//    autoScroll: true,
    layout: 'card',
    items: [{
            xtype: 'analyse-grid'
                    
        },
        {
            xtype: 'panel',
            width: '100%',
            id: 'grapheFamille',
            layout: 'fit',
            items: [
                {
                    xtype: 'analysestock-chart'
                }
                
            ]
        }


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechanalyse',
                    width: 150,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function(field, e, option) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('analyseGrid');
                                var dt_start_vente = Ext.getCmp('dt_start_analyse').getSubmitValue();
                                var dt_end_vente = Ext.getCmp('dt_end_analyse').getSubmitValue();
                              
                                grid.getStore().load({
                                    params: {
                                        dt_start_vente: dt_start_vente,
                                        dt_end_vente: dt_end_vente,
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
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Du',
                    labelWidth: 20,
                    flex: 0.7,
                    id: 'dt_start_analyse',
                    listeners: {
                        change: function() {
                            Ext.getCmp('dt_end_analyse').setMinValue(this.getValue());
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
                    id: 'dt_end_analyse',
                    listeners: {
                        change: function() {

                            Ext.getCmp('dt_start_analyse').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('analyseGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_analyse').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_analyse').getSubmitValue();
                            var search_value = Ext.getCmp('rechanalyse').getValue();
                            grid.getStore().load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente,
                                    search_value: search_value
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

                        if (Ext.getCmp('analyseGrid').getStore().getCount() === 0) {
                            var graphestore = Ext.getCmp("analyseventechartID").getStore();
                            var dt_start_vente = Ext.getCmp('dt_start_analyse').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_analyse').getSubmitValue();
                            var search_value = Ext.getCmp('rechanalyse').getValue();
                            graphestore.load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente,
                                    search_value: search_value
                                }
                            });
                        } else {

                            Ext.getCmp("analyseventechartID").bindStore(Ext.getCmp('analyseGrid').getStore());
                        }
                        Ext.getCmp("analyseventemanagerID").getLayout().setActiveItem(1);


                    }


                },
                {
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'printable',
//                    glyph: 0xf1c1,
                    listeners: {
                        click: function() {

                            var dt_start_vente = Ext.getCmp('dt_start_analyse').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_analyse').getSubmitValue();
                            var search_value = Ext.getCmp('rechanalyse').getValue();
                                var linkUrl = "../webservices/Report/analyseventestock/ws_analyseventestock_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente+"&search_value="+search_value;
                                window.open(linkUrl);
                           
                        }
                    }


                }


            ]
        }

    ]

});


