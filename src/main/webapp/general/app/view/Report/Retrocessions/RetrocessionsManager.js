/* global Ext */

Ext.define('testextjs.view.Report.Retrocessions.RetrocessionsManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'RetrocessionsManager',
    id: 'RetrocessionsManagerID',
    requires: [
        'testextjs.view.Report.Retrocessions.RetrocessionsGrid'

   ],
    frame: true,
    title: 'Evolution mensuelle du solde des rétrocessions selon pharmacie et ann&eacute;e',
    width: '99.5%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'retrocessions-grid'

        }


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechretrocessions',
                    width: 150,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function (field, e, Familletion) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('retrocessionsGrid');
                                var dt_start_vente = Ext.getCmp('dt_start_retrocessions').getSubmitValue();
                                var dt_end_vente = Ext.getCmp('dt_end_retrocessions').getSubmitValue();

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
                    id: 'dt_start_retrocessions',
                    listeners: {
                        change: function () {
                            Ext.getCmp('dt_end_retrocessions').setMinValue(this.getValue());
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
                    id: 'dt_end_retrocessions',
                    listeners: {
                        change: function () {

                            Ext.getCmp('dt_start_retrocessions').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('retrocessionsGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_retrocessions').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_retrocessions').getSubmitValue();
                            var search_value = Ext.getCmp('rechretrocessions').getValue();

                            grid.getStore().load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente,
                                    search_value: search_value
                                }
                            });
                        }
                    }


                }, 

                , {
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

                            var dt_start_vente = Ext.getCmp('dt_start_retrocessions').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_retrocessions').getSubmitValue();
                            var search_value = Ext.getCmp('rechretrocessions').getValue();
                            var linkUrl = "../webservices/Report/Retrocessions/ws_retrocessions_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente + "&search_value=" + search_value;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        }

    ]

});


