/* global Ext */

Ext.define('testextjs.controller.FamilleArticleStatVetoCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.FamilleArticleStatVeto'],
    refs: [{
            ref: 'statfamilleartveto',
            selector: 'statfamilleartveto'
        },
        {
            ref: 'imprimerBtn',
            selector: 'statfamilleartveto #imprimer'
        },
        {
            ref: 'familleStatVetoGrid',
            selector: 'statfamilleartveto gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'statfamilleartveto gridpanel pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'statfamilleartveto #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'statfamilleartveto #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'statfamilleartveto #rechercher'

        }, {
            ref: 'montantNet',
            selector: 'statfamilleartveto #montantNet'
        }, {
            ref: 'montantAchat',
            selector: 'statfamilleartveto #montantAchat'
        },
        {
            ref: 'montantTtc',
            selector: 'statfamilleartveto #montantTtc'
        },
        {
            ref: 'rayons',
            selector: 'statfamilleartveto #rayons'
        },
        {
            ref: 'grossiste',
            selector: 'statfamilleartveto #grossiste'
        },
        {
            ref: 'marge',
            selector: 'statfamilleartveto #marge'
        },
        {
            ref: 'margeRatio',
            selector: 'statfamilleartveto #margeRatio'
        },
        {
            ref: 'query',
            selector: 'statfamilleartveto #query'
        }


    ],
    init: function (application) {
        this.control({
            'statfamilleartveto gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'statfamilleartveto #rechercher': {
                click: this.doSearch
            },
            'statfamilleartveto #imprimer': {
                click: this.onPdfClick
            },
            'statfamilleartveto #rayons': {
                select: this.doSearch
            },
            'statfamilleartveto #grossiste': {
                select: this.doSearch
            },
            'statfamilleartveto #query': {
                specialkey: this.onQuery
            },

            'statfamilleartveto gridpanel': {
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
        var linkUrl = '../BalancePdfServlet?mode=STAT_FAMILLE_ARTICLE_VETO&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&query=' + query;
        window.open(linkUrl);
    },
    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);
    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getFamilleStatVetoGrid().getStore().getProxy();
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
        me.getFamilleStatVetoGrid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearch();
    },
    doSearch: function () {
        var me = this;
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var query = me.getQuery().getValue();
        me.getFamilleStatVetoGrid().getStore().load({
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