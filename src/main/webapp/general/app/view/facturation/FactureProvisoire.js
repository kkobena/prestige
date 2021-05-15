
/* global Ext */

Ext.define('testextjs.view.facturation.FactureProvisoire', {
    extend: 'Ext.panel.Panel',
    xtype: 'factureprovisoire',
    frame: true,
    title: 'Factures provisoires',
    requires: ['testextjs.controller.App'],
    scrollable: true,
    width: '98%',
    minHeight: 500,
    cls: 'custompanel',
    layout: {
        type: 'fit'
    },
    initComponent: function () {
        var groupesStore = Ext.create('Ext.data.Store', {
            idProperty: 'id',
            fields:
                    [
                        {name: 'id',
                            type: 'string'

                        },

                        {name: 'libelle',
                            type: 'string'

                        }

                    ],
            autoLoad: false,
            pageSize: 9999,

            proxy: {
                type: 'ajax',
                url: '../api/v1/facturation/groupetierspayant',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }

        });
        var searchstore = Ext.create('Ext.data.Store', {
            idProperty: 'lgTIERSPAYANTID',
            fields:
                    [
                        {name: 'lgTIERSPAYANTID',
                            type: 'string'

                        },

                        {name: 'strFULLNAME',
                            type: 'string'

                        }

                    ],
            autoLoad: false,
            pageSize: 999,
            proxy: {
                type: 'ajax',
                url: '../api/v1/client/tiers-payants',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }

        });
        var store = Ext.create('Ext.data.Store', {
            idProperty: 'lgFACTUREID',
            fields:
                    [
                        {name: 'lgFACTUREID',
                            type: 'string'

                        },
                        {name: 'strLIBELLETYPETIERSPAYANT',
                            type: 'string'

                        },

                        {name: 'strCODECOMPTABLE',
                            type: 'string'

                        },
                        {name: 'dtDATEFACTURE',
                            type: 'string'

                        },
                        {name: 'periode',
                            type: 'string'

                        },

                        {name: 'dtDEBUTFACTURE',
                            type: 'string'

                        },
                        {name: 'dtFINFACTURE',
                            type: 'string'

                        },
                        {name: 'strCUSTOMER',
                            type: 'string'

                        },
                        {name: 'strFULLNAME',
                            type: 'string'

                        }, {name: 'dtCREATED',
                            type: 'string'

                        },

                        {name: 'nbDossier',
                            type: 'number'

                        },
                        {name: 'dblMONTANTBrut',
                            type: 'number'

                        },
                        {name: 'dblMONTANTFOFETAIRE',
                            type: 'number'

                        },
                        {name: 'dblMONTANTREMISE',
                            type: 'number'

                        },
                        {name: 'dblMONTANTCMDE',
                            type: 'number'

                        }

                    ],
            autoLoad: true,
            pageSize: 20,

            proxy: {
                type: 'ajax',
                url: '../api/v1/facturation/summary/provisoires',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }

        });
        var me = this;
        Ext.applyIf(me, {
            dockedItems: [
                {xtype: 'toolbar',
                    dock: 'top',
//                padding: '8',
                    items: [
                        {
                            text: 'Créer',
                            scope: this,
                            iconCls: 'addicon',
                            itemId: 'newBtn',
                            handler: function () {
                                var xtype = "oneditfacture";
                                testextjs.app.getController('App').onRedirectTo(xtype, {});

                            }
                        }
                        , '-',

                        {
                            xtype: 'combobox',
                            itemId: 'tpCmb',
                            id: 'tpCmb',
                            flex: 2,
                            store: searchstore,
                            pageSize: 999,
                            valueField: 'lgTIERSPAYANTID',
                            displayField: 'strFULLNAME',
                            minChars: 2,
                            queryMode: 'remote',
                            enableKeyEvents: true,
                            emptyText: 'Selectionner tiers payant...',
                            listeners: {
                                select: me.doSearch

                            }

                        }, '-',
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Groupes tiers-payant ',
                            flex: 1,
                            labelWidth:130,
                            margin: '0 5 0 0',
                            itemId: 'groupTp',
                            id: 'groupTp',
                            store: groupesStore,
                            pageSize: 999,
                            valueField: 'id',
                            displayField: 'libelle',
                            typeAhead: true,
                            queryMode: 'remote',
                            minChars: 2,
                            emptyText: 'Sélectionnez un Groupe',
                            listeners: {
                                select: me.doSearch

                            }
                        }, '-',
                        {
                            text: 'rechercher',
                            tooltip: 'rechercher',
                            iconCls: 'searchicon',
                            hidden: true,
                            flex: 0.8,
                            scope: this,
                            itemId: 'btnSearch'
                        }


                    ]
                }

            ],
            items: [
                {
                    xtype: 'gridpanel',
                    id: 'gridFactureProvi',
                    store: store,
                    viewConfig: {
                        forceFit: true,
                        columnLines: true

                    },

                    columns: [
                        {
                            header: 'P&eacute;riode',
                            dataIndex: 'periode',
                            flex: 1.2

                        },

                        {
                            header: 'Organisme',
                            dataIndex: 'strFULLNAME',
                            flex: 1.5
                        },
                        {
                            header: 'Nb dossier',
                            xtype: 'numbercolumn',
                            format: '0,000.',
                            align: 'right',
                            dataIndex: 'nbDossier',
                            flex: 0.5
                        },
                        {
                            header: 'Montant Brut',
                            xtype: 'numbercolumn',
                            format: '0,000.',
                            align: 'right',
                            dataIndex: 'dblMONTANTBrut',
                            flex: 1
                        },
                        {
                            header: 'Montant Remise',
                            dataIndex: 'dblMONTANTREMISE',
                            flex: 0.7,
                            xtype: 'numbercolumn',
                            format: '0,000.',
                            align: 'right'
                        }, {
                            header: 'Montant Forfaitaire',
                            dataIndex: 'dblMONTANTFOFETAIRE',
                            flex: 0.7,
                            xtype: 'numbercolumn',
                            format: '0,000.',
                            align: 'right'
                        }, {
                            header: 'Montant.Net',
                            dataIndex: 'dblMONTANTCMDE',
                            flex: 1,
                            xtype: 'numbercolumn',
                            format: '0,000.',
                            align: 'right'
                        },
                        {
                            header: 'Date',
                            dataIndex: 'dtDATEFACTURE',
                            flex: 0.7

                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                icon: 'resources/images/icons/fam/delete.png',
                                tooltip: 'Editer le réglément',
                                scope: this,
                               handler: this.onRemoveClick
                            }]

                },


                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/grid.png',
                                    tooltip: 'Detail Bordereau',
                                    scope: this,
                                    handler: this.viewdetailFacture

                                }]
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    getClass: function (v, meta, rec) {
                                        return 'printable';
                                    },
                                    getTip: function (v, meta, rec) {
                                        return 'Imprimer Bordereau ';
                                    },
                                    scope: this,
                                    handler: function (grid, rowIndex) {
                                        var rec = grid.getStore().getAt(rowIndex);
                                        var linkUrl = '../webservices/sm_user/facturation/ws_rp_facture_tiers_payant.jsp?lg_FACTURE_ID=' + rec.get('lgFACTUREID');
                                        me.onPrint(linkUrl, true);
//                                   
                                    }

                                }]
                        },

                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    getClass: function (v, meta, rec) {
                                        return 'excel';
                                    },
                                    getTip: function (v, meta, rec) {
                                        return 'Imprimer au format Excel';
                                    },
                                    scope: this,
                                    handler: function (grid, rowIndex) {
                                        var rec = grid.getStore().getAt(rowIndex);
                                        var linkUrl = '../invoiceServlet?action=exls&lg_FACTURE_ID=' + rec.get('lgFACTUREID');
                                        me.onPrint(linkUrl, false);

                                    }
                                }]
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    getClass: function (v, meta, rec) {
                                        return 'word';
                                    },
                                    getTip: function (v, meta, rec) {
                                        return 'Imprimer au format Word';
                                    },
                                    scope: this,
                                    handler: function (grid, rowIndex) {
                                        var rec = grid.getStore().getAt(rowIndex);
                                        var linkUrl = '../invoiceServlet?action=docx&lg_FACTURE_ID=' + rec.get('lgFACTUREID');
                                        me.onPrint(linkUrl, false);
//                                        window.open(linkUrl);
                                    }
                                }]
                        }
                    ],
                    selModel: {
                        selType: 'rowmodel'
//                        mode: 'SINGLE'
                    },
                    dockedItems: [

                        {
                            xtype: 'pagingtoolbar',
                            store: store,
                            dock: 'bottom',
                            displayInfo: true,
                            pageSize: 20,
                            listeners: {
                                beforechange: function (page, currentPage) {
                                    var myProxy = this.store.getProxy();
                                    myProxy.params = {
                                        codegroup: null,
                                        typetp: null,
                                        groupTp: null,
                                        tpid: null
                                    };
                                    myProxy.setExtraParam('codegroup', null);
                                    myProxy.setExtraParam('typetp', null);
                                    myProxy.setExtraParam('groupTp', Ext.getCmp('groupTp').getValue());
                                    myProxy.setExtraParam('tpid', Ext.getCmp('tpCmb').getValue());

                                }

                            }
                        }
                    ]

                }
            ]

        });
        me.callParent(arguments);
    },

    onPrint: function (url, modePdf) {
        var storeMODEL = Ext.create('Ext.data.Store', {
            idProperty: 'id',
            fields:
                    [
                        {name: 'id',
                            type: 'string'

                        },

                        {name: 'libelle',
                            type: 'string'

                        },
                        {name: 'valeur',
                            type: 'string'

                        }

                    ],
            autoLoad: true,
            pageSize: null,

            proxy: {
                type: 'ajax',
                url: '../api/v1/facturation/modelfacture',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }

        });


        var form = Ext.create('Ext.window.Window',
                {

                    autoShow: true,
                    height: 150,
                    width: 450,
                    modal: true,
                    title: 'SELECTION DU MODEL  DE FACTURE A IMPRIMER',
                    closeAction: 'hide',
                    closable: true,
                    maximizable: false,
                    layout: {
                        type: 'fit'
                    },
                    dockedItems: [
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
                                    text: 'Enregistrer',
                                    handler: function (btn) {
                                        var _this = btn.up('window'), _form = _this.down('form');
                                        if (_form.isValid()) {
                                            var values = _form.getValues();
                                            if (modePdf)
                                                window.open(url + '&modeId=' + values.modelId);
                                            else
                                                window.location = url + '&modeId=' + values.modelId;
                                        }
                                        form.destroy();

                                    }
                                },
                                {
                                    xtype: 'button',
                                    iconCls: 'cancelicon',
                                    handler: function (btn) {
                                        form.destroy();
                                    },
                                    text: 'Annuler'

                                }
                            ]
                        }
                    ],
                    items: [

                        {
                            xtype: 'form',

//                              anchor: '100%',
                            layout: 'fit',
                            items: [
                                {
                                    xtype: 'fieldset',

                                    layout: 'anchor',

                                    collapsible: false,
                                    title: 'Information tiers-payant complémentaires',
                                    items: [
                                        {
                                            xtype: 'combobox',
                                            name: 'modelId',

                                            anchor: '100%',
                                            store: storeMODEL,
                                            pageSize: 999,
                                            valueField: 'id',
                                            displayField: 'libelle',
                                            minChars: 2,
                                            queryMode: 'remote',
                                            enableKeyEvents: true,
                                            emptyText: 'Selectionner le modèle'


                                        }


                                    ]
                                }
                            ]
                        }

                    ]
                });

    },
    viewdetailFacture: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.sm_user.editfacture.action.detailTransactionTiersPayant({
            odatasource: rec.data,
            parentview: this,
            mode: "detail_transaction",
            titre: "Detail Bordereau [" + rec.get('strFULLNAME') + "]"
        });
    },
    doSearch: function () {

        Ext.getCmp('gridFactureProvi').getStore().load({
            params: {
                tpid: Ext.getCmp('tpCmb').getValue(),
                codegroup: null,
                typetp: null,
                groupTp: Ext.getCmp('groupTp').getValue()

            }

        });
    },
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
            'confirmer la suppresssion',
            function (btn) {
                if (btn === 'yes') {
                    var rec = grid.getStore().getAt(rowIndex);
                    Ext.Ajax.request({
                        url: '../api/v1/facturation/'+rec.get('lgFACTUREID'),
                        method:'DELETE',

                        success: function (response)
                        {
                            Ext.getCmp('gridFactureProvi').getStore().load({
                                params: {
                                    tpid: Ext.getCmp('tpCmb').getValue(),
                                    codegroup: null,
                                    typetp: null,
                                    groupTp: Ext.getCmp('groupTp').getValue()

                                }

                            });
                        }

                    });

                }
            });
    }
});


