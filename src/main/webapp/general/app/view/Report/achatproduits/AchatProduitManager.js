/* global Ext */

Ext.define('testextjs.view.Report.achatproduits.AchatProduitManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'achatproduitManager',
    id: 'achatproduitManagerID',
    requires: [
        'testextjs.view.Report.achatproduits.AchatProduitGrid'
//        'testextjs.view.Report.RuptureStock.action.RuprureCharts'
   ],
    frame: true,
    title: 'Evolution mensuelle des achats ',
    width: '98%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'achatproduct-grid'

        }


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechachatproduct',
                    width: 150,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function (field, e, Familletion) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('AchatproductGrid');
                                var dt_start_vente = Ext.getCmp('dt_start_achatproduct').getSubmitValue();
                                var dt_end_vente = Ext.getCmp('dt_end_achatproduct').getSubmitValue();

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
                    id: 'dt_start_achatproduct',
                    listeners: {
                        change: function () {
                            Ext.getCmp('dt_end_achatproduct').setMinValue(this.getValue());
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
                    id: 'dt_end_achatproduct',
                    listeners: {
                        change: function () {

                            Ext.getCmp('dt_start_achatproduct').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('AchatproductGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_achatproduct').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_achatproduct').getSubmitValue();
                            var search_value = Ext.getCmp('rechachatproduct').getValue();

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
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'printable',
//                    glyph: 0xf1c1,
                    listeners: {
                        click: function () {

                            var dt_start_vente = Ext.getCmp('dt_start_achatproduct').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_achatproduct').getSubmitValue();
                            var search_value = Ext.getCmp('rechachatproduct').getValue();
                            var linkUrl = "../webservices/Report/achatproduits/ws_achatproduct_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente + "&search_value=" + search_value;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        }

    ]

});


