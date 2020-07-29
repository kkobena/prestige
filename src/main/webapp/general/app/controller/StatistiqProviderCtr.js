/* global Ext */

Ext.define('testextjs.controller.StatistiqProviderCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.StatistiqProvider'],
    refs: [{
            ref: 'statistiqueProvider',
            selector: 'statistiqueProvider'
        },
        {
            ref: 'imprimerBtn',
            selector: 'statistiqueProvider #imprimer'
        },
        {
            ref: 'providerStatGrid',
            selector: 'statistiqueProvider gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'statistiqueProvider gridpanel pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'statistiqueProvider #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'statistiqueProvider #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'statistiqueProvider #rechercher'

        }, {
            ref: 'montantNet',
            selector: 'statistiqueProvider #montantNet'
        }, {
            ref: 'montantAchat',
            selector: 'statistiqueProvider #montantAchat'
        },
        {
            ref: 'montantTtc',
            selector: 'statistiqueProvider #montantTtc'
        },
        {
            ref: 'rayons',
            selector: 'statistiqueProvider #rayons'
        },
        {
            ref: 'grossiste',
            selector: 'statistiqueProvider #grossiste'
        },
        {
            ref: 'marge',
            selector: 'statistiqueProvider #marge'
        },
        {
            ref: 'margeRatio',
            selector: 'statistiqueProvider #margeRatio'
        },
        {
            ref: 'query',
            selector: 'statistiqueProvider #query'
        }


    ],
    init: function (application) {
        this.control({
            'statistiqueProvider gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'statistiqueProvider #rechercher': {
                click: this.doSearch
            },
            'statistiqueProvider #imprimer': {
                click: this.onPdfClick
            },
            'statistiqueProvider #rayons': {
                select: this.doSearch
            },
            'statistiqueProvider #grossiste': {
                select: this.doSearch
            },
            'statistiqueProvider #query': {
                specialkey: this.onQuery
            },

            'statistiqueProvider gridpanel': {
                viewready: this.doInitStore
            }

        });
    },
    onQuery: function (field, e, options) {
        if (e.getKey() === e.ENTER) {
            var me = this;
            me.doSearch();
        }
    },
    onPdfClick: function () {
        var me = this;
        var dtStart = me.getDtStart().getSubmitValue();
        var dtEnd = me.getDtEnd().getSubmitValue();
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        if (codeRayon == null) {
            codeRayon = '';
        }
        if (codeGrossiste == null) {
            codeGrossiste = '';
        }
        var query = me.getQuery().getValue();
        var linkUrl = '../BalancePdfServlet?mode=STAT_PROVIDER_ARTICLE&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&query=' + query;
        window.open(linkUrl);
    },
    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);
    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getProviderStatGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            query: null,
            codeGrossiste: null,
            codeRayon: null

        };
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var query = me.getQuery().getValue();
        myProxy.setExtraParam('codeRayon', codeRayon);
        myProxy.setExtraParam('codeGrossiste', codeGrossiste);
        myProxy.setExtraParam('query', query);
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
    },
    doInitStore: function () {
        var me = this;
        me.getProviderStatGrid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearch();
    },
    doSearch: function () {
        var me = this;
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var query = me.getQuery().getValue();
        me.getProviderStatGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue(),
                query: query,
                codeRayon: codeRayon,
                codeGrossiste: codeGrossiste

            }
        });
    },
    buildSummary: function (rec) {
        var me = this;
        me.getMontantNet().setValue(rec.montantCumulHT);
        me.getMontantAchat().setValue(rec.montantCumulAchat);
        me.getMontantTtc().setValue(rec.montantCumulTTC);
        me.getMarge().setValue(rec.montantCumulMarge);
        me.getMargeRatio().setValue(rec.pourcentageCumulMage);

    }
});