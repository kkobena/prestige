

/* global Ext */

Ext.define('testextjs.view.Report.venteavoirclient.venteavoirclientkmanager', {
    extend: 'Ext.panel.Panel',
    xtype: 'venteavoirclientkmanager',
    id: 'venteavoirclientkmanagerID',
    requires: [
        'testextjs.view.Report.venteavoirclient.venteavoirclientGrid',
      

    ],
    title: 'Evolution mensuelle ds vente selon client',
    frame: true,
    width: '98%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'venteavoirclient-grid'
                    
        }


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechventeavoirclient',
                    width: 150,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function(field, e, option) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('venteavoirclientGrid');
                                var dt_start_vente = Ext.getCmp('dt_start_venteavoirclient').getSubmitValue();
                                var dt_end_vente = Ext.getCmp('dt_end_venteavoirclient').getSubmitValue();
                              
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
                    id: 'dt_start_venteavoirclient',
                    listeners: {
                        change: function() {
                            Ext.getCmp('dt_end_venteavoirclient').setMinValue(this.getValue());
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
                    id: 'dt_end_venteavoirclient',
                    listeners: {
                        change: function() {

                            Ext.getCmp('dt_start_venteavoirclient').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('venteavoirclientGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_venteavoirclient').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_venteavoirclient').getSubmitValue();
                            var search_value = Ext.getCmp('rechventeavoirclient').getValue();
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
  /*{
                    xtype: 'button',
                    text: 'Voir graphes',
                    iconCls: 'charticon16',
                    width: 110,
                    handler: function() {

                        if (Ext.getCmp('venteavoirclientGrid').getStore().getCount() === 0) {
                            var graphestore = Ext.getCmp("venteavoirclientventechartID").getStore();
                            var dt_start_vente = Ext.getCmp('dt_start_venteavoirclient').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_venteavoirclient').getSubmitValue();
                            var search_value = Ext.getCmp('rechventeavoirclient').getValue();
                            graphestore.load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente,
                                    search_value: search_value
                                }
                            });
                        } else {

                            Ext.getCmp("venteavoirclientkmanagerID").bindStore(Ext.getCmp('venteavoirclientGrid').getStore());
                        }
                        Ext.getCmp("venteavoirclientkmanagerID").getLayout().setActiveItem(1);


                    }


                },*/
                {
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'printable',
//                    glyph: 0xf1c1,
                    listeners: {
                        click: function() {

                            var dt_start_vente = Ext.getCmp('dt_start_venteavoirclient').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_venteavoirclient').getSubmitValue();
                            var search_value = Ext.getCmp('rechventeavoirclient').getValue();
                                var linkUrl = "../webservices/Report/venteavoirclient/ws_venteavoirclient_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente+"&search_value="+search_value;
                                window.open(linkUrl);
                           
                        }
                    }


                }


            ]
        }

    ]

});


