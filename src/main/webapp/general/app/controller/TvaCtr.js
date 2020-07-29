/* global Ext */
//Ext.isEmpty(rec)
Ext.define('testextjs.controller.TvaCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.TvaStat'],
    requires: ['Ext.draw.Color', 'Ext.chart.*'],
    refs: [{
            ref: 'tvastat',
            selector: 'tvastat'
        },
        {
            ref: 'imprimerBtn',
            selector: 'tvastat #imprimer'
        },

        {
            ref: 'tvaGrid',
            selector: 'tvastat #tvaGrid'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'tvastat #tvaGrid pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'tvastat #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'tvastat #dtEnd'
        },
        {
            ref: 'ttcChart',
            selector: 'tvastat #ttcChart'
        },

        {ref: 'rechercherButton',
            selector: 'tvastat #rechercher'

        },
        {ref: 'comboRation',
            selector: 'tvastat #comboRation'
        }


    ],
    init: function (application) {
        this.control({
            'tvastat #tvaGrid pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'tvastat #rechercher': {
                click: this.doSearch
            },
            'tvastat #imprimer': {
                click: this.onPdfClick
            },

            'tvastat #tvaGrid': {
                viewready: this.doInitStore
            }

        });
    },
    onPdfClick: function () {
        var me = this;
        var dtStart = me.getDtStart().getSubmitValue();
        var dtEnd = me.getDtEnd().getSubmitValue();
        var linkUrl = '../BalancePdfServlet?mode=TVA&dtStart=' + dtStart + '&dtEnd=' + dtEnd;
        var comboRation=me.getComboRation().getValue();
        if(comboRation==='Journalier'){
            linkUrl='../BalancePdfServlet?mode=TVA_JOUR&dtStart=' + dtStart + '&dtEnd=' + dtEnd;
        }
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getTvaGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null

        };
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },

    doInitStore: function () {
        var me = this;

        me.doSearch();
    },

    doSearch: function () {
        var me = this;
        me.getTvaGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue()

            }, callback: function (r, e) {
//                console.log( me.getTtcChart());
//                me.getTtcChart().setColors(['red','blue']);
//                me.getTtcChart().redraw();

            }

        });
    }

});