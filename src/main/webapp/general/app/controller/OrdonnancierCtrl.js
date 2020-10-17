/* global Ext */

Ext.define('testextjs.controller.OrdonnancierCtrl', {
    extend: 'Ext.app.Controller',
    requires: [
        'testextjs.model.caisse.Vente'
    ],
    views: ['testextjs.view.vente.Ordonnancier'],
    refs: [{
            ref: 'ordonnancier',
            selector: 'ordonnancier'
        },
        {
            ref: 'queryBtn',
            selector: 'ordonnancier #rechercher'
        },
        {
            ref: 'ordonnancierGrid',
            selector: 'ordonnancier gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'ordonnancier gridpanel pagingtoolbar'
        }
        , {
            ref: 'dtStart',
            selector: 'ordonnancier #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'ordonnancier #dtEnd'
        }

        , {
            ref: 'medecin',
            selector: 'ordonnancier #medecin'
        }


    ],
    config: {
        datemisajour: null
    },
    init: function (application) {
        this.control({
            'ordonnancier gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'ordonnancier #rechercher': {
                click: this.doSearch
            },
            'ordonnancier #medecin': {
                select: this.doSearch
            },
            'ordonnancier gridpanel': {
                viewready: this.doInitStore
            }
            /* "ordonnancier gridpanel actioncolumn": {
             printTicket: this.printTicket,
             remove: this.testSuppression,
             facture: this.onFacture,
             toEdit: this.onEdite,
             toExport: this.onbtnexportCsv,
             onSuggestion: this.onSuggestion
             },*/

        });
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getOrdonnancierGrid().getStore().getProxy();
        myProxy.params = {

            medecinId: null,
            dtStart: null,
            dtEnd: null


        };
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('medecinId', me.getMedecin().getValue());

    },
    doInitStore: function () {
        var me = this;
        me.doSearch();
       
    },
    
    doSearch: function () {
        var me = this;
        me.getOrdonnancierGrid().getStore().load({
            params: {

                "dtStart": me.getDtStart().getSubmitValue(),
                "dtEnd": me.getDtEnd().getSubmitValue(),
                "medecinId": me.getMedecin().getValue()

            }
        });
    }
});