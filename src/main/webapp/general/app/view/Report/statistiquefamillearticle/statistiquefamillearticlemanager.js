Ext.define('testextjs.view.Report.statistiquefamillearticle.statistiquefamillearticlemanager', {
    extend: 'Ext.panel.Panel',
    xtype: 'statistiquefamillearticlemanager',
    id: 'statistiquefamillearticlemanagerID',
    requires: [
        'testextjs.view.Report.statistiquefamillearticle.FamilleGrid',
        'testextjs.view.Report.statistiquefamillearticle.action.FamillerCharts'
                /* 'testextjs.view.Report.statistiquefamillearticle.action.AreaCharts'*/

    ],
   title: 'Statistiques d\'Activit&eacute; par Famille',
    frame: true,
    width: '99.5%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'card',
    items: [{
            xtype: 'famillestaistqgrid'

        }, {
            xtype: 'panel',
            width: '100%',
            id: 'grapheFamille',
            layout: 'fit',
            items: [
                {
                    xtype: 'famille-chart'
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
                    id: 'rechFamille',
                    width: 150,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function(field, e, Familletion) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('FamilleGrid');
                                var dt_start_vente = Ext.getCmp('dt_start_Famille').getSubmitValue();
                                var dt_end_vente = Ext.getCmp('dt_end_Famille').getSubmitValue();

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
                    emptyText: 'Date fin',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Du',
                    labelWidth: 20,
                    flex: 0.7,
                    id: 'dt_start_Famille',
                    listeners: {
                        change: function() {
                            Ext.getCmp('dt_end_Famille').setMinValue(this.getValue());
                        }
                    }

                }, {
                    xtype: 'tbseparator'
                }

                ,
                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Au',
                    labelWidth: 20,
                    flex: 0.7,
                    id: 'dt_end_Famille',
                    listeners: {
                        change: function() {

                            Ext.getCmp('dt_start_Famille').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('FamilleGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_Famille').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_Famille').getSubmitValue();
                            var search_value = Ext.getCmp('rechFamille').getValue();

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
                    handler: function() {
                        Ext.getCmp("statistiquefamillearticlemanagerID").getLayout().setActiveItem(0);



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
                    handler: function() {

                        if (Ext.getCmp('FamilleGrid').getStore().getCount() === 0) {
                            var graphestore = Ext.getCmp("famillechartID").getStore();
                            var dt_start_vente = Ext.getCmp('dt_start_Famille').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_Famille').getSubmitValue();
                            var search_value = Ext.getCmp('rechFamille').getValue();
                            graphestore.load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente,
                                    search_value: search_value
                                }
                            });
                        } else {

                            Ext.getCmp("famillechartID").bindStore(Ext.getCmp('FamilleGrid').getStore());
                        }
                        Ext.getCmp("statistiquefamillearticlemanagerID").getLayout().setActiveItem(1);


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

                            var dt_start_vente = Ext.getCmp('dt_start_Famille').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_Famille').getSubmitValue();
                            var search_value = Ext.getCmp('rechFamille').getValue();
                           
                                var linkUrl = "../webservices/Report/statistiquefamillearticle/ws_statistiquefamillearticle_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente+"&search_value="+search_value;
                                window.open(linkUrl);
                            
                        }
                    }


                }


            ]
        }

    ]

});


