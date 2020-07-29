/* global Ext */

Ext.define('testextjs.view.Report.ventesocietereglement.ventesocietereglementManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'ventesocieteManager',
    id: 'ventesocieteManagerID',
    requires: [
        'testextjs.view.Report.ventesocietereglement.ventesocietereglementGrid'

    ],
    frame: true,
    title: 'Evolution mensuelle des ventes et des encaissements selon soci&eacute;t&eacute;s',
    width: '98%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'ventesociete-grid'

        }


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechventesociete',
                    width: 150,
                    emptyText: 'Recherche',
                    listeners: {
                        specialKey: function (field, e, Familletion) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('ventesocieteGrid');
                                var dt_start_vente = Ext.getCmp('dt_start_ventesociete').getSubmitValue();
                                var dt_end_vente = Ext.getCmp('dt_end_ventesociete').getSubmitValue();

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
                    id: 'dt_start_ventesociete',
                    listeners: {
                        change: function () {
                            Ext.getCmp('dt_end_ventesociete').setMinValue(this.getValue());
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
                    id: 'dt_end_ventesociete',
                    listeners: {
                        change: function () {

                            Ext.getCmp('dt_start_ventesociete').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('ventesocieteGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_ventesociete').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_ventesociete').getSubmitValue();
                            var search_value = Ext.getCmp('rechventesociete').getValue();

                            grid.getStore().load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente,
                                    search_value: search_value
                                }
                            });
                        }
                    }


                }
                , 
                {
                    xtype: 'tbseparator'
                }
                ,
                {
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'printable',
                    listeners: {
                        click: function () {

                            var dt_start_vente = Ext.getCmp('dt_start_ventesociete').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_ventesociete').getSubmitValue();
                            var search_value = Ext.getCmp('rechventesociete').getValue();
                            var linkUrl = "../webservices/Report/ventessocietes/ws_ventessocietes_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente + "&search_value=" + search_value;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        }

    ]

});


