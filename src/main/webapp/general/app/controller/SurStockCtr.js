/* global Ext */

Ext.define('testextjs.controller.SurStockCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.produits.SurStock'],
    refs: [{
            ref: 'SurStock',
            selector: 'SurStock'
        },

        {
            ref: 'surGrid',
            selector: 'SurStock gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'SurStock gridpanel pagingtoolbar'
        }
        ,
        {
            ref: 'rayons',
            selector: 'SurStock #rayons'
        },
        {
            ref: 'grossiste',
            selector: 'SurStock #grossiste'
        },

        {
            ref: 'query',
            selector: 'SurStock #query'
        },
        {
            ref: 'codeFamile',
            selector: 'SurStock #codeFamile'
        },
        {
            ref: 'stock',
            selector: 'SurStock #stock'
        },
        {
            ref: 'nbreConsommation',
            selector: 'SurStock #nbreConsommation'
        }

    ],
    init: function (application) {
        this.control({
            'SurStock gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'SurStock #rechercher': {
                click: this.doSearch
            },
            'SurStock #imprimer': {
                click: this.onPdfClick
            },
            'SurStock #rayons': {
                select: this.doSearch
            }, 'SurStock #codeFamile': {
                select: this.doSearch
            },

            'SurStock #grossiste': {
                select: this.doSearch
            },
            'SurStock #query': {
                specialkey: this.onQuery
            }, 'SurStock #stock': {
                specialkey: this.onQuery
            },

            'SurStock gridpanel': {
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

        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue(),
                codeFamile = me.getCodeFamile().getValue();
        var query = me.getQuery().getValue();
        var nbreMois = me.getStock().getValue();
        var nbreConsommation = me.getNbreConsommation().getValue();
        if (codeFamile == null) {
            codeFamile = '';
        }
        if (codeRayon == null) {
            codeRayon = '';
        }

        if (codeGrossiste == null) {
            codeGrossiste = '';
        }
        if (Ext.isEmpty(nbreConsommation)) {
            nbreConsommation = 3;
        }
        var query = me.getQuery().getValue();
        var linkUrl = '../DataReportingServlet?mode=ARTICLES_SUR_STOCK&dtStart='
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&query=' + query
                + '&codeFamile=' + codeFamile + '&query=' + query + '&nbreMois=' + nbreMois
                + '&nbreConsommation=' + nbreConsommation
                ;
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getSurGrid().getStore().getProxy();
        myProxy.params = {

            query: null,
            codeGrossiste: null,
            codeRayon: null,
            codeFamile: null,
            nbreMois: 0,
            nbreConsommation: 3


        };
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var query = me.getQuery().getValue(),
                codeFamile = me.getCodeFamile().getValue();
        var nbreMois = me.getStock().getValue();
        var nbreConsommation = me.getNbreConsommation().getValue();
        myProxy.setExtraParam('nbreMois', nbreMois);
        myProxy.setExtraParam('codeFamile', codeFamile);
        myProxy.setExtraParam('codeRayon', codeRayon);
        myProxy.setExtraParam('codeGrossiste', codeGrossiste);
        myProxy.setExtraParam('nbreConsommation', nbreConsommation);
        myProxy.setExtraParam('query', query);

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
        var nbreMois = me.getStock().getValue();
        var nbreConsommation = me.getNbreConsommation().getValue();
        if (Ext.isEmpty(nbreMois) || nbreMois == 0) {
            return;
        }
        if (Ext.isEmpty(nbreConsommation)) {
            nbreConsommation = 3;
        }
        me.getSurGrid().getStore().load({
            params: {
                query: query,
                codeRayon: codeRayon,
                codeGrossiste: codeGrossiste,
                nbreMois: nbreMois,
                nbreConsommation: nbreConsommation,
                codeFamile: codeFamile

            }
        });
    }

});