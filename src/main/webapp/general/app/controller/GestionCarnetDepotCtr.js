/* global Ext */

Ext.define('testextjs.controller.GestionCarnetDepotCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.CarnetDepot'],
    refs: [{
            ref: 'reglementdepot',
            selector: 'reglementdepot'
        },

        {
            ref: 'imprimerBtn',
            selector: 'reglementdepot #imprimer'
        }


        , {
            ref: 'dtStart',
            selector: 'reglementdepot #dtStart'
        },

        {
            ref: 'dtEnd',
            selector: 'reglementdepot #dtEnd'
        },
        {
            ref: 'tiersPayantsExclus',
            selector: 'reglementdepot #tiersPayantsExclus'
        },
        {
            ref: 'venteGrid',
            selector: 'reglementdepot #ventePanel [xtype=gridpanel]'
        },
        {
            ref: 'reglementGrid',
            selector: 'reglementdepot #reglementPanel [xtype=gridpanel]'
        },

        {
            ref: 'montant',
            selector: 'reglementdepot #ventePanel [xtype=gridpanel] #montant'
        },
        {
            ref: 'nbreVente',
            selector: 'reglementdepot #ventePanel [xtype=gridpanel] #nbreVente'
        },
        {
            ref: 'montantPayer',
            selector: 'reglementdepot #reglementPanel [xtype=gridpanel] #montantPayer'
        },
        {
            ref: 'montantPaye',
            selector: 'reglementdepot #reglementPanel [xtype=gridpanel] #montantPaye'
        },
        {
            ref: 'accountReglement',
            selector: 'reglementdepot #reglementPanel [xtype=gridpanel] #accountReglement'
        }



    ],
    init: function (application) {
        this.control({
            'reglementdepot #btnVentePanel': {
                click: this.searchAll
            },

            'reglementdepot #imprimer': {
                click: this.onPdfClick
            }, 'reglementdepot #ventePanel [xtype=gridpanel]': {
                viewready: this.doInitVenteStore
            },
            'reglementdepot #reglementPanel [xtype=gridpanel]': {
                viewready: this.doInitReglementStore
            },

            'reglementdepot #ventePanel [xtype=gridpanel] pagingtoolbar': {
                beforechange: this.doVentechange
            },
            'reglementdepot #reglementPanel [xtype=gridpanel] pagingtoolbar': {
                beforechange: this.doReglementchange
            },
            'reglementdepot #tiersPayantsExclus': {
                select: this.onSelectTiersPayant
            },
            'reglementdepot #reglementPanel [xtype=gridpanel] #btnReglement': {
                click: this.reglementForm
            },
            "reglementdepot #reglementPanel [xtype=gridpanel] actioncolumn": {
                printTicket: this.printTicket
            }
        });
    },
    onSelectTiersPayant: function (cmp) {
        let me = this;
        let value = cmp.getValue();
        let record = cmp.findRecord("id" || "nomComplet", value);
        me.getAccountReglement().setValue(record.get('account'));
    },
    printTicket: function (view, rowIndex, colIndex, item, e, rec, row) {
        var me = this;
        me.onPrintTicket(rec.get('idDossier'));
    },
    onPdfClick: function () {
        let me = this;
        let itemId = me.getReglementdepot().getLayout().getActiveItem().getItemId();
        let tiersPayantId = me.getTiersPayantsExclus().getValue();
        if (tiersPayantId === null || tiersPayantId === undefined) {
            tiersPayantId = '';
        }
        let dtStart = me.getDtStart().getSubmitValue();
        let dtEnd = me.getDtEnd().getSubmitValue();
        var linkUrl = "";
        if (itemId === 'ventePanel') {
            linkUrl = '../TiersPayantExcludServlet?mode=RETOUR&dtStart=' + dtStart +
                    '&dtEnd=' + dtEnd + '&tiersPayantId=' + tiersPayantId;
        } else if (itemId === 'reglementPanel') {
            linkUrl = '../TiersPayantExcludServlet?mode=RETOUR&dtStart=' + dtStart +
                    '&dtEnd=' + dtEnd + '&tiersPayantId=' + tiersPayantId;
        } else if (itemId === 'retourPanel') {
            linkUrl = '../TiersPayantExcludServlet?mode=RETOUR&dtStart=' + dtStart +
                    '&dtEnd=' + dtEnd + '&tiersPayantId=' + tiersPayantId;
        }
        window.open(linkUrl);
    },

    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);

    },

    buildSummary: function (rec) {
        var me = this;
        me.getMontant().setValue(rec.chiffreAffaire);
        me.getNbreVente().setValue(rec.nbreVente);
    },
    doVentechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getVenteGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            tiersPayantId: null
        };
        myProxy.setExtraParam('tiersPayantId', me.getTiersPayantsExclus().getValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
    },
    searchAll: function () {
        let me = this;
        let itemId = me.getReglementdepot().getLayout().getActiveItem().getItemId();
        if (itemId === 'ventePanel') {
            me.doSearchVente();
        } else if (itemId === 'reglementPanel') {
            me.doSearchReglement();
        } else if (itemId === 'retourPanel') {

        }
    },
    doSearchVente: function () {
        var me = this;
        me.getVenteGrid().getStore().load({
            params: {
                dtEnd: me.getDtEnd().getSubmitValue(),
                dtStart: me.getDtStart().getSubmitValue(),
                tiersPayantId: me.getTiersPayantsExclus().getValue()
            }
        });
    },
    doInitVenteStore: function () {
        var me = this;
        me.getVenteGrid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearchVente();
    },
    doInitReglementStore: function () {
        var me = this;
        me.getReglementGrid().getStore().addListener('metachange', this.doReglementMetachange, this);
        me.doSearchReglement();
    },
    doReglementMetachange: function (store, meta) {
        var me = this;
        me.buildReglementSummary(meta);

    },
    buildReglementSummary: function (rec) {
        var me = this;
        me.getMontantPaye().setValue(rec.montantPaye);
        me.getMontantPayer().setValue(rec.montantPayer);
    },

    doSearchReglement: function () {
        var me = this;
        me.getReglementGrid().getStore().load({
            params: {
                dtEnd: me.getDtEnd().getSubmitValue(),
                dtStart: me.getDtStart().getSubmitValue(),
                tiersPayantId: me.getTiersPayantsExclus().getValue()
            }
        });
    },
    doReglementchange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getReglementGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            tiersPayantId: null
        };
        myProxy.setExtraParam('tiersPayantId', me.getTiersPayantsExclus().getValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },
    reglementForm: function () {
        var me = this;
        let tiersPayantId = me.getTiersPayantsExclus().getValue();
        if (tiersPayantId) {
            var form = Ext.create('Ext.window.Window',
                    {

                        autoShow: true,
                        height: 280,
                        width: 500,
                        modal: true,
                        title: "Nouveau règlement",
                        closeAction: 'destroy',
                        closable: false,
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
                                                var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
                                                Ext.Ajax.request({
                                                    method: 'PUT',
                                                    headers: {'Content-Type': 'application/json'},
                                                    url: '../api/v2/tiers-payant/regler/' + tiersPayantId,
                                                    params: Ext.JSON.encode(_form.getValues()),
                                                    success: function (response, options) {
                                                        progress.hide();
                                                        var result = Ext.JSON.decode(response.responseText, true);
                                                        if (result.success) {
                                                            form.destroy();
                                                            Ext.Msg.confirm("Information", "Voulez-vous imprimer ?",
                                                                    function (btn) {
                                                                        if (btn === "yes") {
//                                  
                                                                            me.onPrintTicket(result.ref);

                                                                        }
                                                                    });

                                                            me.getReglementGrid().getStore().reload();
                                                        } else {
                                                            Ext.MessageBox.show({
                                                                title: 'Message d\'erreur',
                                                                width: 320,
                                                                msg: result.msg,
                                                                buttons: Ext.MessageBox.OK,
                                                                icon: Ext.MessageBox.ERROR

                                                            });
                                                        }

                                                    },
                                                    failure: function (response, options) {
                                                        progress.hide();
                                                        Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
                                                    }

                                                });
                                            }

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
                        items: [{
                                xtype: 'form',
                                bodyPadding: 5,
                                layout: {
                                    type: 'fit'

                                },
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        title: 'Informations règlements',
                                        defaultType: 'textfield',
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        items: [
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: 'Montant',
                                                emptyText: 'Montant',
                                                name: 'montantPaye',
                                                itemId: 'montantPaye',
                                                height: 30, flex: 1,
                                                allowBlank: false,
                                                enableKeyEvents: true,
                                                listeners: {
                                                    afterrender: function (field) {
                                                        field.focus(false, 100);
                                                    }
                                                }

                                            },
                                            {
                                                xtype: 'combobox',
                                                labelWidth: 120,
                                                fieldLabel: 'Type règlement',
                                                name: 'typeReglement',
                                                flex: 1,
                                                height: 30,
                                                store: Ext.create('Ext.data.Store', {
                                                    autoLoad: true,
                                                    pageSize: 999,

                                                    fields: [
                                                        {name: 'id', type: 'string'},
                                                        {name: 'libelle', type: 'string'}
                                                    ],
                                                    proxy: {
                                                        type: 'ajax',
                                                        url: '../api/v1/common/type-reglements',
                                                        reader: {
                                                            type: 'json',
                                                            root: 'data',
                                                            totalProperty: 'total'
                                                        }
                                                    }

                                                }),
                                                value: '1',
                                                valueField: 'id',
                                                displayField: 'libelle',
                                                typeAhead: true,
                                                queryMode: 'remote',
                                                emptyText: 'Choisir un type de reglement...'},
                                            {
                                                xtype: 'textareafield',
                                                fieldLabel: 'Description',
                                                emptyText: 'Description',
                                                name: 'description',
                                                itemId: 'description',
                                                flex: 1
                                            }

                                        ]
                                    }
                                ]
                            }

                        ]
                    });
        } else {
            Ext.MessageBox.show({
                title: 'Message d\'erreur',
                width: 320,
                msg: 'Veuillez choisir le tiers-payant ',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING

            });
        }
    },
    onPrintTicket: function (id) {
        Ext.Ajax.request({
            url: '../api/v1/reglement/ticket-carnet/' + id,
            method: 'PUT'
        });
    }
});