Ext.define('testextjs.view.caution.Historiques', {
    extend: 'Ext.window.Window',
    xtype: 'cautionHistoriques',
    autoShow: false,
    height: 500,
    width: '60%',
    modal: true,
    title: 'Historiques de dépôts de caution',
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
        const historiques = Ext.create('Ext.data.Store', {
            idProperty: 'id',
            fields:
                    [
                        {
                            name: 'id',
                            type: 'string'
                        },

                        {
                            name: 'mvtDate',
                            type: 'string'
                        }
                        ,
                        {
                            name: 'user',
                            type: 'string'
                        },

                        {
                            name: 'montant',
                            type: 'int'
                        }

                    ], autoLoad: false,
            pageSize: 999999,

            proxy: {
                type: 'ajax',
                url: '../api/v1/cautions/historiques',
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
                        }, {
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
                    title: '<span style="color:blue;font-size:14px;">Historiques de depôts de ' + caution.tiersPayantName + '</span>',
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

                            store: historiques,

                            columns: [
                                {xtype: 'rownumberer',
                                    width: 50
                                },

                                {
                                    header: 'Montant',
                                    dataIndex: 'montant',
                                    flex: 1,
                                    xtype: 'numbercolumn',
                                    align: 'right',
                                    format: '0,000.'

                                }, {
                                    header: 'Date',
                                    dataIndex: 'mvtDate',
                                    flex: 1

                                }
                                , {
                                    header: 'Opérateur',
                                    dataIndex: 'user',
                                    flex: 1

                                }


                            ],
                            selModel: {
                                selType: 'cellmodel'

                            },
                            bbar: {
                                xtype: 'pagingtoolbar',
                                store: historiques,
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
