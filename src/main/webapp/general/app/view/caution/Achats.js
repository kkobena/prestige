Ext.define('testextjs.view.caution.Achats', {
    extend: 'Ext.window.Window',
    xtype: 'cautionAchats',
    autoShow: false,
    height: 500,
    width: '60%',
    modal: true,
    title: 'Liste des achats',
    closeAction: 'hide',
    closable: true,
    layout: 'fit',
    maximizable: true,
    autoScroll: true,
    bodyPadding: 5,
    config: {
        caution: null

    },

    initComponent: function () {
        const me = this;
        const caution = me.getCaution();
        const achatsStore = Ext.create('Ext.data.Store', {

            model: 'testextjs.model.caisse.Vente',
            autoLoad: false,
            pageSize: 999999,

            proxy: {
                type: 'ajax',
                url: '../api/v1/cautions/ventes',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }
        });

        Ext.applyIf(me, {
            dockedItems: [{
                    xtype: 'toolbar',
                    dock: 'top',
                    ui: 'footer',
                    layout: {
                        pack: 'end',
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'datefield',
                            fieldLabel: 'Du',
                            itemId: 'dtStart',
                            margin: '0 10 0 0',
                            submitFormat: 'Y-m-d',
                            flex: 1,
                            labelWidth: 20,
                            maxValue: new Date(),
                            value: new Date(),
                            format: 'd/m/Y'

                        }, {
                            xtype: 'datefield',
                            fieldLabel: 'Au',
                            itemId: 'dtEnd',
                            labelWidth: 20,
                            flex: 1,
                            maxValue: new Date(),
                            value: new Date(),
                            margin: '0 9 0 0',
                            submitFormat: 'Y-m-d',
                            format: 'd/m/Y'
                        },
                        {
                            xtype: 'button',
                            itemId: 'rechercher',
                            iconCls: 'searchicon',
                            text: 'Rechercher'

                        },
                        {
                            xtype: 'button',
                            itemId: 'btnPrint',
                            iconCls: 'printable',
                            text: 'Imprimer'

                        },
                        {
                            xtype: 'hiddenfield',
                            itemId: 'idCaution',
                            value: caution.id
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    layout: {
                        pack: 'end',
                        type: 'hbox'
                    },
                    items: [

                        {
                            xtype: 'button',
                            itemId: 'btnCancel',
                            text: 'Fermer'

                        }
                    ]
                }
            ],
            items: [
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;font-size:14px;">Lieste des achats ' + caution.tiersPayantName + '</span>',
                    collapsible: false,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    bodyPadding: 5,
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'gridpanel',

                            store: achatsStore,

                            columns: [
                                {
                                    xtype: 'rownumberer',
                                    width: 50
                                },

                                {
                                    header: 'Reference',
                                    dataIndex: 'strREF',
                                    flex: 1,
                                    sortable: false,
                                    menuDisabled: true
                                }, {
                                    header: 'Montant',
                                    xtype: 'numbercolumn',
                                    dataIndex: 'intPRICE',
                                    align: 'right',
                                    sortable: false,
                                    menuDisabled: true,
                                    flex: 1,
                                    format: '0,000.'

                                }, {
                                    header: 'Montant caution',
                                    xtype: 'numbercolumn',
                                    dataIndex: 'caution',
                                    align: 'right',
                                    sortable: false,
                                    menuDisabled: true,
                                    flex: 1,
                                    format: '0,000.'

                                },
                                {
                                    header: 'Date',
                                    dataIndex: 'dtUPDATED',
                                    sortable: false,
                                    menuDisabled: true,
                                    flex: 0.6,
                                    align: 'center'
                                }, {
                                    header: 'Heure',
                                    dataIndex: 'heure',
                                    sortable: false,
                                    menuDisabled: true,
                                    flex: 0.6,
                                    align: 'center'
                                }
                                , {
                                    header: 'Vendeur',
                                    sortable: false,
                                    menuDisabled: true,
                                    dataIndex: 'userCaissierName',
                                    flex: 1
                                }


                            ],
                            selModel: {
                                selType: 'cellmodel'

                            },
                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: achatsStore,
                        dock: 'bottom',
                        displayInfo: true

                    }

                        }
                    ]
                }



            ]
        });
        me.callParent(arguments);
    }
});
