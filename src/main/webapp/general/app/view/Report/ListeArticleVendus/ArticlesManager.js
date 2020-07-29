
/* global Ext */


Ext.define('testextjs.view.Report.ListeArticleVendus.ArticlesManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'articlevendu',
    id: "articlevenduID",
    requires: [
        'testextjs.view.Report.ListeArticleVendus.GridArticles'
    ],
//    title: 'Liste des article vendus',
    frame: true,
    width: '99.5%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'articlevendus-grid'

        }
    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'recharticlevendus',
                    width: 250,
                    emptyText: 'Rech',
                    enableKeyEvents: true,
                    listeners: {
                        specialKey: function(field, e, options) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('Gridarticlevendu_ID');
                                var dt_start_Articlevendu = Ext.getCmp('dt_start_Articlevendu').getSubmitValue();
                                var dt_end_Articlevendu = Ext.getCmp('dt_end_Articlevendu').getSubmitValue();


                                grid.getStore().load({
                                    params: {
                                        dt_start_Articlevendu: dt_start_Articlevendu,
                                        dt_end_Articlevendu: dt_end_Articlevendu,
                                        search_value: field.getValue()
                                    }
                                });
                            }
                        }
                    }

                },
                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Du',
                    labelWidth: 20,
                    flex: 0.7,
                    id: 'dt_start_Articlevendu',
                    listeners: {
                        change: function() {
                            Ext.getCmp('dt_end_Articlevendu').setMinValue(this.getValue());
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
                    id: 'dt_end_Articlevendu',
                    listeners: {
                        change: function() {

                            Ext.getCmp('dt_start_Articlevendu').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('Gridarticlevendu_ID');
                            var dt_start_Articlevendu = Ext.getCmp('dt_start_Articlevendu').getSubmitValue();
                            var dt_end_Articlevendu = Ext.getCmp('dt_end_Articlevendu').getSubmitValue();
                            var search_value = Ext.getCmp('recharticlevendus').getValue();

                            grid.getStore().load({
                                params: {
                                    dt_start_Articlevendu: dt_start_Articlevendu,
                                    dt_end_Articlevendu: dt_end_Articlevendu,
                                    search_value: search_value
                                }
                            });
                        }
                    }


                }
                , {
                    xtype: 'tbseparator'
                },
                {
                    text: 'Exporter',
                    scope: this,
                    width: 90,
                    iconCls: 'export_excel_icon',
                    handler: function() {
    
                        var dt_debut = Ext.getCmp('dt_start_Articlevendu').getSubmitValue();
                        var dt_fin = Ext.getCmp('dt_end_Articlevendu').getSubmitValue();
                        var search_value = Ext.getCmp('recharticlevendus').getValue();
                        window.location = "../FactureDataExport?action=ArticleVendu&dt_debut=" + dt_debut + "&dt_fin=" + dt_fin + "&search_value=" + search_value;

                    }
                }

                , {
                    xtype: 'tbseparator'
                }
                ,
                {
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'importicon',
//                    glyph: 0xf1c1,
                    listeners: {
                        click: function() {

                            var dt_start_Articlevendu = Ext.getCmp('dt_start_Articlevendu').getSubmitValue();
                            var dt_end_Articlevendu = Ext.getCmp('dt_end_Articlevendu').getSubmitValue();
                            if (dt_start_Articlevendu !== '' && dt_start_Articlevendu !== null) {
                                var linkUrl = "../webservices/Report/statistiquevente/ws_statistiquevente_pdf.jsp" + "?dt_fin=" + dt_start_Articlevendu + "&dt_debut=" + dt_end_Articlevendu;
                                window.open(linkUrl);
                            } else {
                                Ext.MessageBox.show({
                                    title: 'Avertissement',
                                    width: 320,
                                    msg: 'Veuillez choisir la la date',
                                    buttons: Ext.MessageBox.OK,
                                    icon: Ext.MessageBox.WARNING
                                });

                            }
                        }
                    }


                }


            ]
        }

    ]

});


