/* global Ext */

Ext.define('testextjs.controller.StatsByLaboratoireCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.produits.StatsByLaboratoire'],
    refs: [{
            ref: 'statislaboratoireproduits',
            selector: 'statislaboratoireproduits'
        },
        {
            ref: 'imprimerBtn',
            selector: 'statislaboratoireproduits #imprimer'
        },
        {
            ref: 'laboratoireGrid',
            selector: 'statislaboratoireproduits gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'statislaboratoireproduits gridpanel pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'statislaboratoireproduits #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'statislaboratoireproduits #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'statislaboratoireproduits #rechercher'

        },
        {
            ref: 'rayons',
            selector: 'statislaboratoireproduits #rayons'
        },
        {
            ref: 'grossiste',
            selector: 'statislaboratoireproduits #grossiste'
        },
        {
            ref: 'codeFamile',
            selector: 'statislaboratoireproduits #codeFamile'
        },
        {
            ref: 'laboratoire',
            selector: 'statislaboratoireproduits #laboratoire'
        },
        {
            ref: 'query',
            selector: 'statislaboratoireproduits #query'
        }

    ],
    init: function (application) {
        this.control({
            'statislaboratoireproduits gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'statislaboratoireproduits #rechercher': {
                click: this.doSearch
            },
            'statislaboratoireproduits #imprimer': {
                click: this.onPdfClick
            },
            'statislaboratoireproduits #rayons': {
                select: this.doSearch
            },
            'statislaboratoireproduits #laboratoire': {
                select: this.doSearch
            },

            'statislaboratoireproduits #grossiste': {
                select: this.doSearch
            },
            'statislaboratoireproduits #codeFamile': {
                select: this.doSearch
            },
            'statislaboratoireproduits gridpanel': {
                viewready: this.doInitStore
            }, 'statislaboratoireproduits #query': {
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
        var laboratoireId = me.getLaboratoire().getValue();

        if (codeFamile == null) {
            codeFamile = '';
        }
        if (codeRayon == null) {
            codeRayon = '';
        }
        if (codeGrossiste == null) {
            codeGrossiste = '';
        }
        if (laboratoireId == null) {
            laboratoireId = '';
        }
        var linkUrl = '../DataReportingServlet?mode=UNITES_LABORATOIRES&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&codeFamile=' + codeFamile
                + '&query=' + query + '&laboratoireId=' + laboratoireId;
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getLaboratoireGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            codeGrossiste: null,
            codeRayon: null,
            query: true,
            codeFamile: null,
            laboratoireId: null

        };
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var codeFamile = me.getCodeFamile().getValue();
        var query = me.getQuery().getValue();
        var laboratoireId = me.getLaboratoire().getValue();
        myProxy.setExtraParam('codeRayon', codeRayon);
        myProxy.setExtraParam('codeFamile', codeFamile);
        myProxy.setExtraParam('codeGrossiste', codeGrossiste);
        myProxy.setExtraParam('laboratoireId', laboratoireId);
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
                     laboratoireId = me.getLaboratoire().getValue();;
        me.getLaboratoireGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue(),
                codeFamile: codeFamile,
                codeRayon: codeRayon,
                codeGrossiste: codeGrossiste,
                query: query,
                laboratoireId: laboratoireId
            }
        });
    }

});