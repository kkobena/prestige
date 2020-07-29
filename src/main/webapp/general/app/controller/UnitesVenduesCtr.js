/* global Ext */

Ext.define('testextjs.controller.UnitesVenduesCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.UnitesVendues'],
    refs: [{
            ref: 'statistiqueuniteventemanager',
            selector: 'statistiqueuniteventemanager'
        },

        {
            ref: 'unitesGrid',
            selector: 'statistiqueuniteventemanager gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'statistiqueuniteventemanager gridpanel pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'statistiqueuniteventemanager #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'statistiqueuniteventemanager #dtEnd'
        },

        {
            ref: 'rayons',
            selector: 'statistiqueuniteventemanager #rayons'
        },
        {
            ref: 'grossiste',
            selector: 'statistiqueuniteventemanager #grossiste'
        },

        {
            ref: 'query',
            selector: 'statistiqueuniteventemanager #query'
        },
        {
            ref: 'codeFamile',
            selector: 'statistiqueuniteventemanager #codeFamile'
        }


    ],
    init: function (application) {
        this.control({
            'statistiqueuniteventemanager gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'statistiqueuniteventemanager #rechercher': {
                click: this.doSearch
            },
            'statistiqueuniteventemanager #imprimer': {
                click: this.onPdfClick
            },
            'statistiqueuniteventemanager #rayons': {
                select: this.doSearch
            }, 'statistiqueuniteventemanager #codeFamile': {
                select: this.doSearch
            },

            'statistiqueuniteventemanager #grossiste': {
                select: this.doSearch
            },
            'statistiqueuniteventemanager #query': {
                specialkey: this.onQuery
            },

            'statistiqueuniteventemanager gridpanel': {
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
        var codeGrossiste = me.getGrossiste().getValue(),
                codeFamile = me.getCodeFamile().getValue();
        var query = me.getQuery().getValue();
        if (codeFamile == null) {
            codeFamile = '';
        }
        if (codeRayon == null) {
            codeRayon = '';
        }

        if (codeGrossiste == null) {
            codeGrossiste = '';
        }
        var linkUrl = '../DataReportingServlet?mode=UNITES_VENDUES&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&query=' + query
                + '&codeFamile=' + codeFamile 
                ;
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getUnitesGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            query: null,
            codeGrossiste: null,
            codeRayon: null,
            codeFamile: null

        };
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var query = me.getQuery().getValue(),
                codeFamile = me.getCodeFamile().getValue();
        myProxy.setExtraParam('codeFamile', codeFamile);
        myProxy.setExtraParam('codeRayon', codeRayon);
        myProxy.setExtraParam('codeGrossiste', codeGrossiste);
        myProxy.setExtraParam('query', query);
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
    },
    doInitStore: function () {
        var me = this;
        me.doSearch();
    },
    doSearch: function () {
        var me = this;
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var query = me.getQuery().getValue(),
                codeFamile = me.getCodeFamile().getValue();
        me.getUnitesGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue(),
                query: query,
                codeRayon: codeRayon,
                codeGrossiste: codeGrossiste,
                codeFamile: codeFamile

            }
        });
    }

});