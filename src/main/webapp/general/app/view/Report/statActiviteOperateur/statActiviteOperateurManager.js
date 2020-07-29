/* global Ext */
Ext.define('testextjs.view.Report.statActiviteOperateur.statActiviteOperateurManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'statActiviteOperateurManager',
    id: 'statActiviteOperateurManagerID',
    requires: [
        'testextjs.view.Report.statActiviteOperateur.OperateurGrid',
        'testextjs.view.Report.statActiviteOperateur.action.OperateurCharts'
    ],
    title: 'Statistiques d\'Activit&eacute; par op&eacute;rateur',
    frame: true,
    width: '99.5%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'card',
    items: [{
            xtype: 'op-grid'

        }, {
            xtype: 'panel',
            width: '100%',
            id: 'grapheOp',
            layout: 'fit',
            items: [
                {
                    xtype: 'op-chart'
                }
            ]
        }


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechOP',
                    enableKeyEvents: true,
                    width: 150,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function (field, e, option) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('opGrid');
                                var dt_start_vente = Ext.getCmp('dt_start_op').getSubmitValue();
                                var dt_end_vente = Ext.getCmp('dt_end_op').getSubmitValue();

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
                    id: 'dt_start_op',
                    listeners: {
                        change: function () {
                            Ext.getCmp('dt_end_op').setMinValue(this.getValue());
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
                    id: 'dt_end_op',
                    listeners: {
                        change: function () {

                            Ext.getCmp('dt_start_op').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('opGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_op').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_op').getSubmitValue();
                            var search_value = Ext.getCmp('rechOP').getValue();

                            grid.getStore().load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente,
                                    search_value: search_value
                                },
                                callback: function (record) {
                                    if (record.length > 0)
                                        Ext.getCmp('opBtn').show();
                                    else
                                        Ext.getCmp('opBtn').hide();

                                    // 
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
                        Ext.getCmp("statActiviteOperateurManagerID").getLayout().setActiveItem(0);



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
                    id: 'opBtn',
                    hidden: true,
                    width: 110,
                    handler: function () {
                        var graphestore = Ext.getCmp("opchartID").bindStore(Ext.getCmp('opGrid').getStore());

                        /* var dt_start_vente = Ext.getCmp('dt_start_op').getSubmitValue();
                         var dt_end_vente = Ext.getCmp('dt_end_op').getSubmitValue();
                         var search_value = Ext.getCmp('rechOP').getValue();
                         graphestore.load({
                         params: {
                         dt_start_vente: dt_start_vente,
                         dt_end_vente: dt_end_vente,
                         search_value: search_value
                         }
                         });*/


                        Ext.getCmp("statActiviteOperateurManagerID").getLayout().setActiveItem(1);


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

                            var dt_start_vente = Ext.getCmp('dt_start_op').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_op').getSubmitValue();
                            var search_value = Ext.getCmp('rechOP').getValue();

                            var linkUrl = "../webservices/Report/Operateur/ws_operateur_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente + "&search_value=" + search_value;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        }

    ]

});


