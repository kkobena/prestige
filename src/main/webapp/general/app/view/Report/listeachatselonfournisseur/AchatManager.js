
/* global Ext */


Ext.define('testextjs.view.Report.listeachatselonfournisseur.AchatManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'achatfournisseurs',
    id: "achatfournisseursID",
    requires: [
        'testextjs.view.Report.listeachatselonfournisseur.AchatGrid',
    ],
    title: 'Liste des fournisseurs avec les produits achet&eacute;s',
    frame: true,
    width: '99%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'achatgrid-grid'

        }
    ], dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    id: 'rechachatgrid',
                    width: 250,
                    emptyText: 'Rech',
                    enableKeyEvents: true,
                    listeners: {
                        specialKey: function(field, e, options) {
                            if (e.getKey() === e.ENTER) {
                                var grid = Ext.getCmp('achatgridID');
                                var dt_start_Articlevendu = Ext.getCmp('dt_start_achatgrid').getSubmitValue();
                                var dt_end_Articlevendu = Ext.getCmp('dt_end_achatgrid').getSubmitValue();


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
                    id: 'dt_start_achatgrid',
                    listeners: {
                        change: function() {
                            Ext.getCmp('dt_end_achatgrid').setMinValue(this.getValue());
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
                    id: 'dt_end_achatgrid',
                    listeners: {
                        change: function() {

                            Ext.getCmp('dt_start_achatgrid').setMaxValue(this.getValue());
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
                            var grid = Ext.getCmp('achatgridID');
                            var dt_start_Articlevendu = Ext.getCmp('dt_start_achatgrid').getSubmitValue();
                            var dt_end_Articlevendu = Ext.getCmp('dt_end_achatgrid').getSubmitValue();
                            var search_value = Ext.getCmp('rechachatgrid').getValue();

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
    
                        var dt_debut = Ext.getCmp('dt_start_achatgrid').getSubmitValue();
                        var dt_fin = Ext.getCmp('dt_end_achatgrid').getSubmitValue();
                        var search_value = Ext.getCmp('rechachatgrid').getValue();

                       


                        window.location = "../FactureDataExport?action=Achatfournisseur&dt_debut=" + dt_debut + "&dt_fin=" + dt_fin + "&search_value=" + search_value;

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

                            var dt_start_Articlevendu = Ext.getCmp('dt_start_achatgrid').getSubmitValue();
                            var dt_end_Articlevendu = Ext.getCmp('dt_end_achatgrid').getSubmitValue();
                            if (dt_start_Articlevendu !== '' && dt_start_Articlevendu !== null) {
                                var linkUrl = "../webservices/Report/achatfournisseurs/ws_achatsfournisseurs_pdf.jsp" + "?dt_fin=" + dt_start_Articlevendu + "&dt_debut=" + dt_end_Articlevendu;
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


