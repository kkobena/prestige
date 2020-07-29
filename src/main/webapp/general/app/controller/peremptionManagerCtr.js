/* global Ext */

Ext.define('testextjs.controller.peremptionManagerCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Report.peremptions.peremptionManager'],
    refs: [{
            ref: 'peremptionquery',
            selector: 'peremptionquery'
        },
        {
            ref: 'imprimerBtn',
            selector: 'peremptionquery #imprimer'
        },
        {
            ref: 'peremptionGrid',
            selector: 'peremptionquery gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'peremptionquery gridpanel pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'peremptionquery #dtStart'
        },
        {ref: 'rechercherButton',
            selector: 'peremptionquery #rechercher'

        },
        {
            ref: 'rayons',
            selector: 'peremptionquery #rayons'
        },
        {
            ref: 'filtre',
            selector: 'peremptionquery #filtre'
        },

        {
            ref: 'grossiste',
            selector: 'peremptionquery #grossiste'
        },
        {
            ref: 'codeFamile',
            selector: 'peremptionquery #codeFamile'
        },
        {
            ref: 'stock',
            selector: 'peremptionquery #stock'
        },
        {
            ref: 'achat',
            selector: 'peremptionquery #achat'
        },
        {
            ref: 'vente',
            selector: 'peremptionquery #vente'
        },
        {
            ref: 'query',
            selector: 'peremptionquery #query'
        }





    ],
    init: function (application) {
        this.control({
            'peremptionquery gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'peremptionquery #rechercher': {
                click: this.doSearch
            },
            'peremptionquery #imprimer': {
                click: this.onPdfClick
            },
            'peremptionquery #rayons': {
                select: this.doSearch
            },
            'peremptionquery #grossiste': {
                select: this.doSearch
            },
            'peremptionquery #codeFamile': {
                select: this.doSearch
            },
            'peremptionquery #filtre': {
                select: this.doSearch
            }, 'peremptionquery #query': {
                specialkey: this.onQuery
            },

            'peremptionquery gridpanel': {
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
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var codeFamile = me.getCodeFamile().getValue();
        var filtre = me.getFiltre().getValue();
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
        if (filtre == null) {
            filtre = '';
        }
        var linkUrl = '../BalancePdfServlet?mode=PERIMES&dtStart=' + dtStart + '&query=' + query
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&codeFamile=' + codeFamile + "&filtre=" + filtre;
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getPeremptionGrid().getStore().getProxy();
        myProxy.params = {
            dtStart: null,
            filtre: "PERIME",
            codeFamile: null,
            codeRayon: null,
            codeGrossiste: null,
            query: null

        };
        var dtStart = me.getDtStart().getSubmitValue();
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var codeFamile = me.getCodeFamile().getValue();
        var filtre = me.getFiltre().getValue();
        var query = me.getQuery().getValue();
        myProxy.setExtraParam('codeRayon', codeRayon);
        myProxy.setExtraParam('codeFamile', codeFamile);
        myProxy.setExtraParam('codeGrossiste', codeGrossiste);
        myProxy.setExtraParam('query', query);
        myProxy.setExtraParam('filtre', filtre);
        myProxy.setExtraParam('dtStart', dtStart);
    },
    doInitStore: function () {
        var me = this;
        me.getPeremptionGrid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearch();
    },
    doSearch: function () {
        var me = this;
        var dtStart = me.getDtStart().getSubmitValue();
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var codeFamile = me.getCodeFamile().getValue();
        var filtre = me.getFiltre().getValue();
        var query = me.getQuery().getValue();
        me.getPeremptionGrid().getStore().load({
            params: {
                dtStart: dtStart,
                filtre: filtre,
                codeFamile: codeFamile,
                codeRayon: codeRayon,
                codeGrossiste: codeGrossiste,
                query: query

            }
        });
    },
    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);
    },
    buildSummary: function (rec) {
        var me = this;
        me.getStock().setValue(rec.intQUANTITY);
        me.getAchat().setValue(rec.intPRICEREMISE);
        me.getVente().setValue(rec.intPRICE);
    }
});