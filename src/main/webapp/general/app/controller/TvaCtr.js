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
    config: {
        checkUg: false

    },

    init: function (application) {
        this.control({
            'tvastat': {
                render: this.onReady
            },
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
        let me = this;
        let dtStart = me.getDtStart().getSubmitValue();
        let dtEnd = me.getDtEnd().getSubmitValue();
        let checkug = me.getCheckUg();
        let linkUrl = '../BalancePdfServlet?mode=TVA&dtStart=' + dtStart + '&dtEnd=' + dtEnd + '&checkug=' + checkug;
        let comboRation = me.getComboRation().getValue();
        if (comboRation === 'Journalier') {
            linkUrl = '../BalancePdfServlet?mode=TVA_JOUR&dtStart=' + dtStart + '&dtEnd=' + dtEnd + '&checkug=' + checkug;
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
        if (me.getCheckUg()) {
            myProxy.url = '../api/v2/caisse/tvas';
        }

        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },

    doInitStore: function () {
        var me = this;

        me.doSearch();
    },

    doSearch: function () {
        var me = this;
        let store = me.getTvaGrid().getStore();
        if (me.getCheckUg()) {
            store.getProxy().url = '../api/v2/caisse/tvas';
        }

        store.load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue()

            }

        });
    }, 
    
    
    oncheckUg: function () {
        var me = this;
        Ext.Ajax.request({
            method: 'GET',
            url: '../api/v1/common/checkug',
            success: function (response, options) {
                var result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    me.checkUg = result.data;
                }
            }

        });
    },
    onReady: function () {
        var me = this;
        me.oncheckUg();
    }

});