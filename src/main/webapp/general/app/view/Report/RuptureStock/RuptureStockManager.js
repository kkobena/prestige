/* global Ext */

Ext.define('testextjs.view.Report.RuptureStock.RuptureStockManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'RuptureStockManager',
    id: 'RuptureStockID',
    requires: [
        'testextjs.view.Report.RuptureStock.RuptureGrid',
        'testextjs.view.Report.RuptureStock.action.RuprureCharts'
   ],
    frame: true,
    title: 'Statistiques sur les ruptures de stock',
    width: '99.5%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'card',
    items: [{
            xtype: 'rupture-grid'

        }, {
            xtype: 'panel',
            width: '100%',
            id: 'grapherupture',
            layout: 'fit',
            items: [
                {
                    xtype: 'rupture-chart'
                }
                /* {
                 xtype: 'area-chart'
                 }*/
            ]
        }


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechrupture',
                    width: 150,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function (field, e, Familletion) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('RuptureGrid');
                                var dt_start_vente = Ext.getCmp('dt_start_rupture').getSubmitValue();
                                var dt_end_vente = Ext.getCmp('dt_end_rupture').getSubmitValue();

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
                    id: 'dt_start_rupture',
                    listeners: {
                        change: function () {
                            Ext.getCmp('dt_end_rupture').setMinValue(this.getValue());
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
                    id: 'dt_end_rupture',
                    listeners: {
                        change: function () {

                            Ext.getCmp('dt_start_rupture').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('RuptureGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_rupture').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_rupture').getSubmitValue();
                            var search_value = Ext.getCmp('rechrupture').getValue();

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
                    text: 'Voir tableau',
                    iconCls: 'tableauicon',
                    width: 110,
                    handler: function () {
                        Ext.getCmp("RuptureStockID").getLayout().setActiveItem(0);



                    }


                }

                , {
                    xtype: 'tbseparator'
                }
                ,
                {
                    xtype: 'button',
                    text: 'Voir graphes',
                    iconCls: 'charticon16',
                    width: 110,
                    handler: function () {

                        if (Ext.getCmp('RuptureGrid').getStore().getCount() === 0) {
                            var graphestore = Ext.getCmp("rupturechartID").getStore();
                            var dt_start_vente = Ext.getCmp('dt_start_rupture').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_rupture').getSubmitValue();
                            var search_value = Ext.getCmp('rechrupture').getValue();
                            graphestore.load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente,
                                    search_value: search_value
                                }
                            });
                        } else {

                            Ext.getCmp("rupturechartID").bindStore(Ext.getCmp('RuptureGrid').getStore());
                        }
                        Ext.getCmp("RuptureStockID").getLayout().setActiveItem(1);


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

                            var dt_start_vente = Ext.getCmp('dt_start_rupture').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_rupture').getSubmitValue();
                            var search_value = Ext.getCmp('rechrupture').getValue();
                            var linkUrl = "../webservices/Report/RuptureStock/ws_rupture_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente + "&search_value=" + search_value;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        }

    ]

});


