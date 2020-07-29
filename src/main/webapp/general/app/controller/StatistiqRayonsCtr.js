/* global Ext */

Ext.define('testextjs.controller.StatistiqRayonsCtr', {
extend: 'Ext.app.Controller',
        views: ['testextjs.view.Dashboard.StatistiqRayons'],
        refs: [{
        ref: 'statistiqueRayons',
                selector: 'statistiqueRayons'
        },
        {
        ref: 'imprimerBtn',
                selector: 'statistiqueRayons #imprimer'
        },
        {
        ref: 'rayonStatGrid',
                selector: 'statistiqueRayons gridpanel'
        },
        {
        ref: 'pagingtoolbar',
                selector: 'statistiqueRayons gridpanel pagingtoolbar'
        }

        , {
        ref: 'dtStart',
                selector: 'statistiqueRayons #dtStart'
        }, {
        ref: 'dtEnd',
                selector: 'statistiqueRayons #dtEnd'
        },
        {ref: 'rechercherButton',
                selector: 'statistiqueRayons #rechercher'

        }, {
        ref: 'montantNet',
                selector: 'statistiqueRayons #montantNet'
        }, {
        ref: 'montantAchat',
                selector: 'statistiqueRayons #montantAchat'
        },
        {
        ref: 'montantTtc',
                selector: 'statistiqueRayons #montantTtc'
        },
        {
        ref: 'rayons',
                selector: 'statistiqueRayons #rayons'
        },
        {
        ref: 'grossiste',
                selector: 'statistiqueRayons #grossiste'
        },
        {
        ref: 'marge',
                selector: 'statistiqueRayons #marge'
        },
        {
            ref: 'margeRatio',
            selector: 'statistiqueRayons #margeRatio'
        },
        {
            ref: 'query',
            selector: 'statistiqueRayons #query'
        }


        ],
        init: function (application) {
        this.control({
        'statistiqueRayons gridpanel pagingtoolbar': {
        beforechange: this.doBeforechange
        },
                'statistiqueRayons #rechercher': {
                click: this.doSearch
                },
                'statistiqueRayons #imprimer': {
                click: this.onPdfClick
                },
                'statistiqueRayons #rayons': {
                select: this.doSearch
                },
                'statistiqueRayons #grossiste': {
                select: this.doSearch
                },
                 'statistiqueRayons #query': {
                specialkey: this.onQuery
                },
                
                'statistiqueRayons gridpanel': {
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
        var linkUrl = '../BalancePdfServlet?mode=STAT_RAYONS_ARTICLE&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&query=' + query;
        window.open(linkUrl);
    },
        doMetachange: function (store, meta) {
        var me = this;
                me.buildSummary(meta);
        },
        doBeforechange: function (page, currentPage) {
        var me = this;
                var myProxy = me.getRayonStatGrid().getStore().getProxy();
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
                me.getRayonStatGrid().getStore().addListener('metachange', this.doMetachange, this);
                me.doSearch();
        },
        doSearch: function () {
        var me = this;
                var codeRayon = me.getRayons().getValue();
                var codeGrossiste = me.getGrossiste().getValue();
                var query = me.getQuery().getValue();
                me.getRayonStatGrid().getStore().load({
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