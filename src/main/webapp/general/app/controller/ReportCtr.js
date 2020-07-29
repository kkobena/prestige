/* global Ext */

Ext.define('testextjs.controller.ReportCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.caisseManager.RapportGestion'],
    refs: [{
            ref: 'managementreport',
            selector: 'managementreport'
        },
        {
            ref: 'imprimerBtn',
            selector: 'managementreport #imprimer'
        },

        {
            ref: 'rapportGrid',
            selector: 'managementreport #rapportGrid'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'managementreport #rapportGrid pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'managementreport #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'managementreport #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'managementreport #rechercher'

        }, {
            ref: 'montantDepense',
            selector: 'managementreport #montantDepense'
        }, {
            ref: 'montantCaisse',
            selector: 'managementreport #montantCaisse'
        }


    ],
    init: function (application) {
        this.control({
            'managementreport #rapportGrid pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'managementreport #rechercher': {
                click: this.doSearch
            },
            'managementreport #imprimer': {
                click: this.onPdfClick
            },

            'managementreport #rapportGrid': {
                viewready: this.doInitStore
            }

        });
    },
    onPdfClick: function () {
        var me = this;
        var dtStart = me.getDtStart().getSubmitValue();
        var dtEnd = me.getDtEnd().getSubmitValue();
        var linkUrl = '../BalancePdfServlet?mode=REPORT&dtStart=' + dtStart + '&dtEnd=' + dtEnd;
        window.open(linkUrl);
    },
    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);

    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getRapportGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null

        };
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },

    doInitStore: function () {
        var me = this;
        me.getRapportGrid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearch();
    },

    doSearch: function () {
        var me = this;
        me.getRapportGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue()

            }
        });
    },
    buildSummary: function (rec) {
        var me = this;
        me.getMontantCaisse().setValue(rec.montantCaisse);
        me.getMontantDepense().setValue(rec.montantDepense);

    }
});