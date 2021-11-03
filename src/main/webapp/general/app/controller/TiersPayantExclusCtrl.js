/* global Ext */

Ext.define('testextjs.controller.TiersPayantExclusCtrl', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.TiersPayantExclus'],
    refs: [{
            ref: 'tpexclus',
            selector: 'tpexclus'
        },
        {
            ref: 'dataPanel',
            selector: 'tpexclus #dataPanel'
        },

        {
            ref: 'query',
            selector: 'tpexclus #toExcludeGrid #query'
        },
        {
            ref: 'imprimerBtn',
            selector: 'tpexclus #imprimer'
        },

        {
            ref: 'toExcludeGrid',
            selector: 'tpexclus #toExcludeGrid'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'tpexclus #toExcludeGrid pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'tpexclus #dataPanel #dtStart'
        },

        {
            ref: 'dtEnd',
            selector: 'tpexclus #dataPanel #dtEnd'
        },
        {
            ref: 'tiersPayantsExclus',
            selector: 'tpexclus #dataPanel #tiersPayantsExclus'
        },
        {
            ref: 'venteGrid',
            selector: 'tpexclus #dataPanel #ventePanel [xtype=gridpanel]'
        },
        {
            ref: 'reglementGrid',
            selector: 'tpexclus #dataPanel #reglementPanel [xtype=gridpanel]'
        },

        {
            ref: 'montant',
            selector: 'tpexclus #dataPanel #ventePanel [xtype=gridpanel] #montant'
        },
        {
            ref: 'nbreVente',
            selector: 'tpexclus #dataPanel #ventePanel [xtype=gridpanel] #nbreVente'
        },
        {
            ref: 'montantPayer',
            selector: 'tpexclus #dataPanel #reglementPanel [xtype=gridpanel] #montantPayer'
        },
        {
            ref: 'montantPaye',
            selector: 'tpexclus #dataPanel #reglementPanel [xtype=gridpanel] #montantPaye'
        },
        {
            ref: 'accountReglement',
            selector: 'tpexclus #dataPanel #reglementPanel [xtype=gridpanel] #accountReglement'
        }



    ],
    init: function (application) {
        this.control({
            'tpexclus #toExcludeGrid pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'tpexclus #toExcludeGrid #rechercher': {
                click: this.doSearch
            }, 'tpexclus #dataPanel #btnVentePanel': {
                click: this.searchAll
            },

            'tpexclus #toExcludeGrid #query': {
                specialkey: this.onSpecialKey
            },
            'tpexclus #dataPanel #imprimer': {
                click: this.onPdfClick
            },

            'tpexclus #toExcludeGrid': {
                viewready: this.doInitStore
            }, 'tpexclus #dataPanel #ventePanel [xtype=gridpanel]': {
                viewready: this.doInitVenteStore
            },
            'tpexclus #dataPanel #reglementPanel [xtype=gridpanel]': {
                viewready: this.doInitReglementStore
            },

            'tpexclus #toExcludeGrid [xtype=checkcolumn]': {
                checkchange: this.onCheckChange
            },
            'tpexclus #dataPanel #ventePanel [xtype=gridpanel] pagingtoolbar': {
                beforechange: this.doVentechange
            },
            'tpexclus #dataPanel #reglementPanel [xtype=gridpanel] pagingtoolbar': {
                beforechange: this.doReglementchange
            },
            'tpexclus #dataPanel #tiersPayantsExclus': {
                select: this.onSelectTiersPayant
            },
            'tpexclus #dataPanel #reglementPanel [xtype=gridpanel] #btnReglement': {
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
        let record = me.getToExcludeGrid().getStore().getAt(rowIndex);
        Ext.Ajax.request({
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v2/tiers-payant/exclure-inclure/' + record.data.id + '/' + checked,
            success: function (response, options) {
                me.getToExcludeGrid().getStore().reload();
            },
            failure: function (response, options) {
                Ext.Msg.alert("Message", 'Erreur  : [code erreur : ' + response.status + ' ]');
                me.getToExcludeGrid().getStore().reload();
            }
        });
    },
    onSelect: function (_this, selected, eOpts) {
        var me = this;
        Ext.Ajax.request({
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v2/tiers-payant/exclure/' + selected.data.id,
            success: function (response, options) {
                me.getToExcludeGrid().getStore().reload();
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
            url: '../api/v2/tiers-payant/inclure/' + deselected.data.id,
            success: function (response, options) {
                me.getToExcludeGrid().getStore().reload();
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
            linkUrl = '../TiersPayantExcludServlet?mode=VENTE&dtStart=' + dtStart +
                    '&dtEnd=' + dtEnd + '&tiersPayantId=' + tiersPayantId;
        } else if (itemId === 'reglementPanel') {
            linkUrl = '../TiersPayantExcludServlet?mode=REGLEMENTS&dtStart=' + dtStart +
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
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getToExcludeGrid().getStore().getProxy();
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
        me.getToExcludeGrid().getStore().load({
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