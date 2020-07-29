/* global Ext */

Ext.define('testextjs.view.Report.RapportGestions.RapportGestionsManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'RapportGestions',
    id: 'RapportGestionsID',
    requires: [
        'testextjs.view.Report.RapportGestions.RapportGestionsGrid'

    ],
    frame: true,
    title: 'Rapport de gestion',
    width: '98%',
    // height:Ext.getBody().getViewSize().height*0.85,
    minHeight: 570,
    maxHeight: 570,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'rapportGestionGrid-grid'

        }


    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Du',
                    labelWidth: 20,
                    flex: 0.7,
                    id: 'dt_start_rapportGestionGrid',
                    listeners: {
                        change: function () {
                            Ext.getCmp('dt_end_rapportGestionGrid').setMinValue(this.getValue());
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
                    id: 'dt_end_rapportGestionGrid',
                    listeners: {
                        change: function () {

                            Ext.getCmp('dt_start_rapportGestionGrid').setMaxValue(this.getValue());
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
//                            Ext.getCmp('TOTALAMOUNT').setValue('');
                            var grid = Ext.getCmp('rapportGrid');
                            var dt_start_vente = Ext.getCmp('dt_start_rapportGestionGrid').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_rapportGestionGrid').getSubmitValue();


                            grid.getStore().load({
                                params: {
                                    dt_start_vente: dt_start_vente,
                                    dt_end_vente: dt_end_vente

                                }/*,
                                 callback: function (records) {
                                 
                                 Ext.getCmp('TOTALAMOUNT').setValue(records[records.length-1].get('MONTANTDEPENSES'));
                                 
                                 
                                 
                                 }*/
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
                    listeners: {
                        click: function () {

                            var dt_start_vente = Ext.getCmp('dt_start_rapportGestionGrid').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_end_rapportGestionGrid').getSubmitValue();

                            var linkUrl = "../webservices/Report/RapportGestions/ws_rapportgestion_pdf.jsp" + "?dt_start_vente=" + dt_start_vente + "&dt_end_vente=" + dt_end_vente;
                            window.open(linkUrl);

                        }
                    }


                }


            ]
        }

    ]

});


