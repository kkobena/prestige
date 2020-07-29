/* global Ext */

Ext.define('testextjs.controller.MargeManagerCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.MargeManager'],
    refs: [{
            ref: 'margeproducts',
            selector: 'margeproducts'
        },
        {
            ref: 'imprimerBtn',
            selector: 'margeproducts #imprimer'
        },
        {
            ref: 'margeGrid',
            selector: 'margeproducts gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'margeproducts gridpanel pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'margeproducts #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'margeproducts #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'margeproducts #rechercher'

        },
        {
            ref: 'rayons',
            selector: 'margeproducts #rayons'
        },
        {
            ref: 'grossiste',
            selector: 'margeproducts #grossiste'
        },
        {
            ref: 'codeFamile',
            selector: 'margeproducts #codeFamile'
        },
        {
            ref: 'critere',
            selector: 'margeproducts #critere'
        },
        {
            ref: 'query',
            selector: 'margeproducts #query'
        },
        {
            ref: 'filtre',
            selector: 'margeproducts #filtre'
        }

    ],
    init: function (application) {
        this.control({
            'margeproducts gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'margeproducts #rechercher': {
                click: this.doSearch
            },
            'margeproducts #imprimer': {
                click: this.onPdfClick
            },
            'margeproducts #rayons': {
                select: this.doSearch
            },
            'margeproducts #filtre': {
                select: this.onFiltre
            },

            'margeproducts #grossiste': {
                select: this.doSearch
            },
            'margeproducts #codeFamile': {
                select: this.doSearch
            },
            'margeproducts gridpanel': {
                viewready: this.doInitStore
            }, 'margeproducts #query': {
                specialkey: this.onQuery
            },
            'margeproducts #critere': {
                specialkey: this.onQuery
            }
        });
    },
    onFiltre: function (cmp) {
        var me = this;
        var critere = me.getCritere();
       
        if (cmp.getValue() !== 'ALL') {
            critere.show();
             critere.focus(50,true);
            if (critere.getValue() !== null && critere.getValue() !=='' ){
                me.doSearch();  
            }
              
        } else {
            critere.setValue('');
            critere.hide();
        }

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
        var critere = me.getCritere().getValue(),
                filtre = me.getFiltre().getValue();
        if (critere == null) {
            critere = '';
        }
         if (filtre == null) {
            filtre = '';
        }
        if (codeFamile == null) {
            codeFamile = '';
        }
        if (codeRayon == null) {
            codeRayon = '';
        }
        if (codeGrossiste == null) {
            codeGrossiste = '';
        }

        var linkUrl = '../DataReportingServlet?mode=MARGE_PRODUITS&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&codeGrossiste=' + codeGrossiste + '&codeRayon=' + codeRayon + '&codeFamile=' + codeFamile
                + '&query=' + query + '&critere=' + critere + '&filtre=' + filtre;
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getMargeGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            codeGrossiste: null,
            codeRayon: null,
            query: true,
            codeFamile: null,
            critere: null,
            filtre: "ALL"

        };
        var codeRayon = me.getRayons().getValue();
        var codeGrossiste = me.getGrossiste().getValue();
        var codeFamile = me.getCodeFamile().getValue();
        var query = me.getQuery().getValue();
        var critere = me.getCritere().getValue(),
                filtre = me.getFiltre().getValue();
        myProxy.setExtraParam('codeRayon', codeRayon);
        myProxy.setExtraParam('codeFamile', codeFamile);
        myProxy.setExtraParam('codeGrossiste', codeGrossiste);
        myProxy.setExtraParam('critere', critere);
        myProxy.setExtraParam('filtre', filtre);
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
        var critere = me.getCritere().getValue();
        var codeFamile = me.getCodeFamile().getValue(),
                filtre = me.getFiltre().getValue();
        me.getMargeGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue(),
                codeFamile: codeFamile,
                codeRayon: codeRayon,
                codeGrossiste: codeGrossiste,
                query: query,
                critere: critere,
                filtre: filtre

            }
        });
    }

});