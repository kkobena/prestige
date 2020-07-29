/* global Ext */

Ext.define('testextjs.view.Report.recaptableaupharmacien.RecapTableauParmaManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'recaptableaupharma',
    id: 'recaptableaupharmaID',
    requires: [
       'testextjs.view.Report.recaptableaupharmacien.RecapTableauParmaGrid'
        
   ],
    frame: true,
    title: 'R&eacute;capitulatif du tableau de bord du pharmacien',
    width: '99.5%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'hbox',
    items: [{
            xtype: 'recpatableaupharm-grid'

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
                        Ext.getCmp("recaptableaupharmaID").getLayout().setActiveItem(0);



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
                        Ext.getCmp("recaptableaupharmaID").getLayout().setActiveItem(1);


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
                            var linkUrl = "../webservices/Report/recaptableaupharma/ws_rupture_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente + "&search_value=" + search_value;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        }

    ]

});


