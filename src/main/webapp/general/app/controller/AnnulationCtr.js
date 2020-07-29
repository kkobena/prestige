/* global Ext */

Ext.define('testextjs.controller.AnnulationCtr', {
    extend: 'Ext.app.Controller',
    requires: [
        'testextjs.model.caisse.Vente'
    ],
    views: ['testextjs.view.vente.Removed'],
    refs: [{
            ref: 'venteannulerlist',
            selector: 'venteannuler'
        },
        {
            ref: 'queryBtn',
            selector: 'venteannuler #rechercher'
        },

        {
            ref: 'venteannulerGrid',
            selector: 'venteannuler gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'venteannuler gridpanel pagingtoolbar'
        }

        , {
            ref: 'printPdf',
            selector: 'venteannuler #printPdf'
        }, {
            ref: 'dtStart',
            selector: 'venteannuler #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'venteannuler #dtEnd'
        },
         {
            ref: 'totalAmount',
            selector: 'venteannuler #totalAmount'
        }

        , {
            ref: 'queryField',
            selector: 'venteannuler #query'
        }


    ],
    init: function (application) {
        this.control({
            'venteannuler gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'venteannuler #rechercher': {
                click: this.doSearch
            },
            'venteannuler gridpanel': {
                viewready: this.doInitStore
            },

            'venteannuler #query': {
                specialkey: this.onSpecialKey
            },

            'venteannuler #printPdf': {
                click: this.printLit
            },
            "venteannuler gridpanel actioncolumn": {
                click: this.handleActionColumn
            }
        });
    },
    handleActionColumn: function (view, rowIndex, colIndex, item, e, r, row) {
        var me = this;
        var store = me.getVenteannulerGrid().getStore(),
                rec = store.getAt(colIndex);
        me.onPrintTicket(rec.get('lgPREENREGISTREMENTID'), rec.get('lgTYPEVENTEID'));


    },
    onPrintTicket: function (id, lgTYPEVENTEID) {
        var url = (lgTYPEVENTEID === '1') ? '../api/v1/vente/ticket/vno/' + id : '../api/v1/vente/ticket/vo/' + id;
        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            headers: {'Content-Type': 'application/json'},
            method: 'POST',
            url: url,
            success: function (response, options) {
                progress.hide();
            },
            failure: function (response, options) {
                progress.hide();
            }

        });
    },
    printLit: function () {
        var me = this,
                query = me.getQueryField().getValue(),
                dtStart = me.getDtStart().getSubmitValue(),
                dtEnd = me.getDtEnd().getSubmitValue();
        var linkUrl = '../FacturePdfServlet?dtStart=' + dtStart + "&dtEnd=" + dtEnd + "&query=" + query + "&mode=VENTE_ANNULEES";

        window.open(linkUrl);
    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getVenteannulerGrid().getStore().getProxy();

        myProxy.params = {
            query: null,
            dtStart: null,
            dtEnd: null

        };

        myProxy.setExtraParam('query', me.getQueryField().getValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());

    },

    doInitStore: function () {
        var me = this;
         me.getVenteannulerGrid().getStore().addListener('metachange', this.doMetachange, this);
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

        me.getVenteannulerGrid().getStore().load({
            params: {
                "query": me.getQueryField().getValue(),
                "dtStart": me.getDtStart().getSubmitValue(),
                "dtEnd": me.getDtEnd().getSubmitValue()
            }
        });
    },
     doMetachange: function (store, meta) {
        var me = this;
      me.getTotalAmount().setValue(meta);

    }
});