
/* global Ext */

Ext.define('testextjs.controller.ListeAvoirCtr', {
    extend: 'Ext.app.Controller',
    requires: [
        'testextjs.model.caisse.Vente'
    ],
    views: ['testextjs.view.vente.VenteAvoir'],
    refs: [{
            ref: 'venteavoirmanager',
            selector: 'venteavoirmanager'
        },
        {
            ref: 'queryBtn',
            selector: 'venteavoirmanager #rechercher'
        },

        {
            ref: 'venteavoirmanagerGrid',
            selector: 'venteavoirmanager gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'venteavoirmanager gridpanel pagingtoolbar'
        }
        , {
            ref: 'dtStart',
            selector: 'venteavoirmanager #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'venteavoirmanager #dtEnd'
        }

        , {
            ref: 'hStart',
            selector: 'venteavoirmanager #hStart'
        }, {
            ref: 'hEnd',
            selector: 'venteavoirmanager #hEnd'
        }
        , {
            ref: 'queryField',
            selector: 'venteavoirmanager #query'
        }, {
            ref: 'typeVente',
            selector: 'venteavoirmanager #typeVente'
        },
        {
            ref: 'printPdf',
            selector: 'venteavoirmanager #printPdf'
        }


    ],
    init: function (application) {
        this.control({
            'venteavoirmanager gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'venteavoirmanager #rechercher': {
                click: this.doSearch
            },
            'venteavoirmanager #typeVente': {
                select: this.doSearch
            },

            'venteavoirmanager gridpanel': {
                viewready: this.doInitStore
            },
            "venteavoirmanager gridpanel actioncolumn": {
                click: this.handleActionColumn

            },
            'venteavoirmanager #query': {
                specialkey: this.onSpecialKey
            },
            'venteavoirmanager #printPdf': {
                click: this.printLit
            }


        });
    },

    printLit: function () {
        var me = this,
                query = me.getQueryField().getValue(),
                dtStart = me.getDtStart().getSubmitValue(),
                dtEnd = me.getDtEnd().getSubmitValue(),
                typeVenteId = me.getTypeVente().getValue(),
                hStart = me.getHStart().getSubmitValue(),
                hEnd = me.getHEnd().getSubmitValue();
        if(typeVenteId==null){
            typeVenteId='';
        }
        var linkUrl = '../BalancePdfServlet?mode=UNITES_AVOIRS&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&hEnd=' + hEnd + '&hStart=' + hStart + '&query=' + query
                + '&typeVenteId=' + typeVenteId;

        window.open(linkUrl);
    },

    handleActionColumn: function (view, rowIndex, colIndex, item, e, r, row) {
        var me = this;
        var store = me.getVenteavoirmanagerGrid().getStore(),
                rec = store.getAt(colIndex);
        me.onEdite(rec);



    },
    onEdite: function (rec) {
        var data = {'record': rec.data, 'isEdit': true};
        var xtype = "doAvoir";
        testextjs.app.getController('App').onRedirectTo(xtype, data);
    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getVenteavoirmanagerGrid().getStore().getProxy();

        myProxy.params = {
            query: null,
            typeVenteId: null,
            dtStart: null,
            dtEnd: null,
            hEnd: null,
            hStart: null,
            onlyAvoir: true,
            sansBon: false

        };
        myProxy.setExtraParam('sansBon', false);
        myProxy.setExtraParam('onlyAvoir', true);
        myProxy.setExtraParam('query', me.getQueryField().getValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('typeVenteId', me.getTypeVente().getValue());
        myProxy.setExtraParam('hStart', me.getHStart().getSubmitValue());
        myProxy.setExtraParam('hEnd', me.getHEnd().getSubmitValue());

    },

    doInitStore: function () {
        var me = this;
        me.doSearch();

    },
    onSpecialKey: function (field, e, options) {
        if (e.getKey() === e.ENTER) {
            var me = this;
            me.doSearch();

        }
    },
    doSearch: function () {
        var me = this;

        me.getVenteavoirmanagerGrid().getStore().load({
            params: {
                "query": me.getQueryField().getValue(),
                "dtStart": me.getDtStart().getSubmitValue(),
                "dtEnd": me.getDtEnd().getSubmitValue(),
                "typeVenteId": me.getTypeVente().getValue(),
                "onlyAvoir": true,
                "sansBon": false
            }
        });
    }
});