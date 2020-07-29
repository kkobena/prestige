/* global Ext */

Ext.define('testextjs.controller.FamilleArticleStatsCtr', {
extend: 'Ext.app.Controller',
        views: ['testextjs.view.Dashboard.FamilleArticleStats'],
        refs: [{
        ref: 'statfamilleartcl',
                selector: 'statfamilleartcl'
        },
        {
        ref: 'imprimerBtn',
                selector: 'statfamilleartcl #imprimer'
        },
        {
        ref: 'familleStatGrid',
                selector: 'statfamilleartcl gridpanel'
        },
        {
        ref: 'pagingtoolbar',
                selector: 'statfamilleartcl gridpanel pagingtoolbar'
        }

        , {
        ref: 'dtStart',
                selector: 'statfamilleartcl #dtStart'
        }, {
        ref: 'dtEnd',
                selector: 'statfamilleartcl #dtEnd'
        },
        {ref: 'rechercherButton',
                selector: 'statfamilleartcl #rechercher'

        }, {
        ref: 'montantNet',
                selector: 'statfamilleartcl #montantNet'
        }, {
        ref: 'montantAchat',
                selector: 'statfamilleartcl #montantAchat'
        },
        {
        ref: 'montantTtc',
                selector: 'statfamilleartcl #montantTtc'
        },
        {
        ref: 'rayons',
                selector: 'statfamilleartcl #rayons'
        },
        {
        ref: 'grossiste',
                selector: 'statfamilleartcl #grossiste'
        },
        {
        ref: 'marge',
                selector: 'statfamilleartcl #marge'
        },
        {
            ref: 'margeRatio',
            selector: 'statfamilleartcl #margeRatio'
        },
        {
            ref: 'query',
            selector: 'statfamilleartcl #query'
        }


        ],
        init: function (application) {
        this.control({
        'statfamilleartcl gridpanel pagingtoolbar': {
        beforechange: this.doBeforechange
        },
                'statfamilleartcl #rechercher': {
                click: this.doSearch
                },
                'statfamilleartcl #imprimer': {
                click: this.onPdfClick
                },
                'statfamilleartcl #rayons': {
                select: this.doSearch
                },
                'statfamilleartcl #grossiste': {
                select: this.doSearch
                },
                 'statfamilleartcl #query': {
                specialkey: this.onQuery
                },
                
                'statfamilleartcl gridpanel': {
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
        var linkUrl = '../BalancePdfServlet?mode=STAT_FAMILLE_ARTICLE&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&query=' + query;
        window.open(linkUrl);
    },
        doMetachange: function (store, meta) {
        var me = this;
                me.buildSummary(meta);
        },
        doBeforechange: function (page, currentPage) {
        var me = this;
                var myProxy = me.getFamilleStatGrid().getStore().getProxy();
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
                me.getFamilleStatGrid().getStore().addListener('metachange', this.doMetachange, this);
                me.doSearch();
        },
        doSearch: function () {
        var me = this;
                var codeRayon = me.getRayons().getValue();
                var codeGrossiste = me.getGrossiste().getValue();
                var query = me.getQuery().getValue();
                me.getFamilleStatGrid().getStore().load({
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