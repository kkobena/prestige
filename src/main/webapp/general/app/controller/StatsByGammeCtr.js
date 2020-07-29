/* global Ext */

Ext.define('testextjs.controller.StatsByGammeCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.produits.StatsByGamme'],
    refs: [{
            ref: 'statisgammeproduits',
            selector: 'statisgammeproduits'
        },
        {
            ref: 'imprimerBtn',
            selector: 'statisgammeproduits #imprimer'
        },
        {
            ref: 'gammeGrid',
            selector: 'statisgammeproduits gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'statisgammeproduits gridpanel pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'statisgammeproduits #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'statisgammeproduits #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'statisgammeproduits #rechercher'

        },
        {
            ref: 'rayons',
            selector: 'statisgammeproduits #rayons'
        },
        {
            ref: 'grossiste',
            selector: 'statisgammeproduits #grossiste'
        },
        {
            ref: 'codeFamile',
            selector: 'statisgammeproduits #codeFamile'
        },
        {
            ref: 'gamme',
            selector: 'statisgammeproduits #gamme'
        },
        {
            ref: 'query',
            selector: 'statisgammeproduits #query'
        }

    ],
    init: function (application) {
        this.control({
            'statisgammeproduits gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'statisgammeproduits #rechercher': {
                click: this.doSearch
            },
            'statisgammeproduits #imprimer': {
                click: this.onPdfClick
            },
            'statisgammeproduits #rayons': {
                select: this.doSearch
            },
            'statisgammeproduits #gamme': {
                select: this.doSearch
            },

            'statisgammeproduits #grossiste': {
                select: this.doSearch
            },
            'statisgammeproduits #codeFamile': {
                select: this.doSearch
            },
            'statisgammeproduits gridpanel': {
                viewready: this.doInitStore
            }, 'statisgammeproduits #query': {
                specialkey: this.onQuery
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
        var codeFamile = me.getCodeFamile().getValue();
        var query = me.getQuery().getValue();
        var gamme = me.getGamme().getValue();

        if (codeFamile == null) {
            codeFamile = '';
        }
        if (codeRayon == null) {
            codeRayon = '';
        }
        if (codeGrossiste == null) {
            codeGrossiste = '';
        }
        if (gamme == null) {
            gamme = '';
        }
        var linkUrl = '../DataReportingServlet?mode=UNITES_GAMME&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&codeFamile=' + codeFamile
                + '&query=' + query + '&gammeId=' + gamme;
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getGammeGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            codeGrossiste: null,
            codeRayon: null,
            query: true,
            codeFamile: null,
            gammeId: null

        };
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var codeFamile = me.getCodeFamile().getValue();
        var query = me.getQuery().getValue();
        var gammeId = me.getGamme().getValue();
        myProxy.setExtraParam('codeRayon', codeRayon);
        myProxy.setExtraParam('codeFamile', codeFamile);
        myProxy.setExtraParam('codeGrossiste', codeGrossiste);
        myProxy.setExtraParam('gammeId', gammeId);
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
        var query = me.getQuery().getValue();
        var codeFamile = me.getCodeFamile().getValue(),
                  gammeId = me.getGamme().getValue();
        me.getGammeGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue(),
                codeFamile: codeFamile,
                codeRayon: codeRayon,
                codeGrossiste: codeGrossiste,
                query: query,
                gammeId: gammeId
            }
        });
    }

});