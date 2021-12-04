/* global Ext */

Ext.define('testextjs.controller.GestionCarnetDepotCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.CarnetDepot'],
    refs: [{
            ref: 'reglementdepot',
            selector: 'reglementdepot'
        },
        {
            ref: 'dataPanel',
            selector: 'reglementdepot #dataPanel'
        },

        {
            ref: 'query',
            selector: 'reglementdepot #carnetGrid #query'
        },
        {
            ref: 'imprimerBtn',
            selector: 'reglementdepot #imprimer'
        },

        {
            ref: 'carnetGrid',
            selector: 'reglementdepot #carnetGrid'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'reglementdepot #carnetGrid pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'reglementdepot #dataPanel #dtStart'
        },

        {
            ref: 'dtEnd',
            selector: 'reglementdepot #dataPanel #dtEnd'
        },
        {
            ref: 'tiersPayantsExclus',
            selector: 'reglementdepot #dataPanel #tiersPayantsExclus'
        },
        {
            ref: 'venteGrid',
            selector: 'reglementdepot #dataPanel #ventePanel [xtype=gridpanel]'
        },
        {
            ref: 'reglementGrid',
            selector: 'reglementdepot #dataPanel #reglementPanel [xtype=gridpanel]'
        },

        {
            ref: 'montant',
            selector: 'reglementdepot #dataPanel #ventePanel [xtype=gridpanel] #montant'
        },
        {
            ref: 'nbreVente',
            selector: 'reglementdepot #dataPanel #ventePanel [xtype=gridpanel] #nbreVente'
        },
        {
            ref: 'montantPayer',
            selector: 'reglementdepot #dataPanel #reglementPanel [xtype=gridpanel] #montantPayer'
        },
        {
            ref: 'montantPaye',
            selector: 'reglementdepot #dataPanel #reglementPanel [xtype=gridpanel] #montantPaye'
        },
        {
            ref: 'accountReglement',
            selector: 'reglementdepot #dataPanel #reglementPanel [xtype=gridpanel] #accountReglement'
        }



    ],
    init: function (application) {
        this.control({
            'reglementdepot #carnetGrid pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'reglementdepot #carnetGrid #rechercher': {
                click: this.doSearch
            }, 'reglementdepot #dataPanel #btnVentePanel': {
                click: this.searchAll
            },

            'reglementdepot #carnetGrid #query': {
                specialkey: this.onSpecialKey
            },
            'reglementdepot #dataPanel #imprimer': {
                click: this.onPdfClick
            },

            'reglementdepot #carnetGrid': {
                viewready: this.doInitStore
            }, 'reglementdepot #dataPanel #ventePanel [xtype=gridpanel]': {
                viewready: this.doInitVenteStore
            },
            'reglementdepot #dataPanel #reglementPanel [xtype=gridpanel]': {
                viewready: this.doInitReglementStore
            },

            'reglementdepot #carnetGrid [xtype=checkcolumn]': {
                checkchange: this.onCheckChange
            },
            'reglementdepot #dataPanel #ventePanel [xtype=gridpanel] pagingtoolbar': {
                beforechange: this.doVentechange
            },
            'reglementdepot #dataPanel #reglementPanel [xtype=gridpanel] pagingtoolbar': {
                beforechange: this.doReglementchange
            },
            'reglementdepot #dataPanel #tiersPayantsExclus': {
                select: this.onSelectTiersPayant
            },
            'reglementdepot #dataPanel #reglementPanel [xtype=gridpanel] #btnReglement': {
                click: this.reglementForm
            }
        });
    },
    onSelectTiersPayant: function (cmp) {
        let me = this;
        let value = cmp.getValue();
        let record = cmp.findRecord("id" || "nomComplet", value);
        me.getAccountReglement().setValue(record.get('account'));
    },
    onCheckChange: function (column, rowIndex, checked) {
        let me = this;
        let record = me.getCarnetGrid().getStore().getAt(rowIndex);
        Ext.Ajax.request({
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v2/carnet-depot/exclure-inclure/' + record.data.id + '/' + checked,
            success: function (response, options) {
                me.getCarnetGrid().getStore().reload();
            },
            failure: function (response, options) {
                Ext.Msg.alert("Message", 'Erreur  : [code erreur : ' + response.status + ' ]');
                me.getCarnetGrid().getStore().reload();
            }
        });
    },
    onSelect: function (_this, selected, eOpts) {
        var me = this;
        Ext.Ajax.request({
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v2/carnet-depot/exclure/' + selected.data.id,
            success: function (response, options) {
                me.getCarnetGrid().getStore().reload();
            },
            failure: function (response, options) {
                Ext.Msg.alert("Message", 'Erreur  : [code erreur : ' + response.status + ' ]');
            }
        });
    },
    onDeselect: function (_this, deselected, eOpts) {
        var me = this;
        Ext.Ajax.request({
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v2/carnet-depot/inclure/' + deselected.data.id,
            success: function (response, options) {
                me.getCarnetGrid().getStore().reload();
            },
            failure: function (response, options) {
                Ext.Msg.alert("Message", 'Erreur  : [code erreur : ' + response.status + ' ]');
            }
        });
    },

    onSpecialKey: function (field, e, options) {
        var me = this;
        if (e.getKey() === e.ENTER) {
            me.doSearch();
        }
    },

    onPdfClick: function () {
        let me = this;
        let itemId = me.getDataPanel().getLayout().getActiveItem().getItemId();
        let tiersPayantId = me.getTiersPayantsExclus().getValue();
        if (tiersPayantId === null || tiersPayantId === undefined) {
            tiersPayantId = '';
        }
        let dtStart = me.getDtStart().getSubmitValue();
        let dtEnd = me.getDtEnd().getSubmitValue();
        var linkUrl = "";
        if (itemId === 'ventePanel') {
            linkUrl = '../TiersPayantDepotServlet?mode=VENTE&dtStart=' + dtStart +
                    '&dtEnd=' + dtEnd + '&tiersPayantId=' + tiersPayantId;
        } else if (itemId === 'reglementPanel') {
            linkUrl = '../TiersPayantDepotServlet?mode=REGLEMENTS&dtStart=' + dtStart +
                    '&dtEnd=' + dtEnd + '&tiersPayantId=' + tiersPayantId;
        } else if (itemId === 'retourPanel') {
            linkUrl = '../TiersPayantDepotServlet?mode=RETOUR&dtStart=' + dtStart +
                    '&dtEnd=' + dtEnd + '&tiersPayantId=' + tiersPayantId;
        }
        window.open(linkUrl);
    },

    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);

    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getCarnetGrid().getStore().getProxy();
        myProxy.params = {
            query: ''
        };
        let query = me.getQuery().getValue();
        myProxy.setExtraParam('query', query);
    },

    doInitStore: function () {
        var me = this;
        me.doSearch();
    },
    doSearch: function () {
        var me = this;
        let query = me.getQuery().getValue();
        me.getCarnetGrid().getStore().load({
            params: {
                query: query
            }
        });
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
        let itemId = me.getDataPanel().getLayout().getActiveItem().getItemId();
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
                        height: 260,
                        width: 500,
                        modal: true,
                        title: "Nouvel règlement",
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
    }
});