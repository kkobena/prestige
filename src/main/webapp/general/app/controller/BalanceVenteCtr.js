/* global Ext */

Ext.define('testextjs.controller.BalanceVenteCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.caisseManager.balance.BalanceSaleCash'],
    refs: [{
            ref: 'balancesalecahs',
            selector: 'balancesalecahs'
        },
        {
            ref: 'imprimerBtn',
            selector: 'balancesalecahs #imprimer'
        },

        {
            ref: 'balanceGrid',
            selector: 'balancesalecahs #balanceGrid'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'balancesalecahs #balanceGrid pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'balancesalecahs #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'balancesalecahs #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'balancesalecahs #rechercher'

        }, {
            ref: 'montantTTC',
            selector: 'balancesalecahs #montantTTC'
        }, {
            ref: 'montantAchat',
            selector: 'balancesalecahs #montantAchat'
        },
        {
            ref: 'ratioVA',
            selector: 'balancesalecahs #ratioVA'
        }, {
            ref: 'fondCaisse',
            selector: 'balancesalecahs #fondCaisse'
        },
        {
            ref: 'montantMobilePayment',
            selector: 'balancesalecahs #montantMobilePayment'
        },
        {
            ref: 'montantRegDiff',
            selector: 'balancesalecahs #montantRegDiff'
        },
        {
            ref: 'montantRegleTp',
            selector: 'balancesalecahs #montantRegleTp'
        },
        {
            ref: 'montantSortie',
            selector: 'balancesalecahs #montantSortie'
        },
        {
            ref: 'montantEntre',
            selector: 'balancesalecahs #montantEntre'
        },
        {
            ref: 'montantEsp',
            selector: 'balancesalecahs #montantEsp'
        },
        {
            ref: 'panierMoyen',
            selector: 'balancesalecahs #panierMoyen'
        },
        {
            ref: 'nbreVente',
            selector: 'balancesalecahs #nbreVente'
        }, {
            ref: 'montantCheque',
            selector: 'balancesalecahs #montantCheque'
        }, {
            ref: 'montantVirement',
            selector: 'balancesalecahs #montantVirement'
        }
        ,
        {
            ref: 'marge',
            selector: 'balancesalecahs #marge'
        }
    ],
    init: function (application) {
        this.control({
            'balancesalecahs #balanceGrid pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'balancesalecahs #rechercher': {
                click: this.doSearch
            },
            'balancesalecahs #imprimer': {
                click: this.onPdfClick
            },

            'balancesalecahs #balanceGrid': {
                viewready: this.doInitStore
            }

        });
    },
    onPdfClick: function () {
        var me = this;

        var dtStart = me.getDtStart().getSubmitValue();
        var dtEnd = me.getDtEnd().getSubmitValue();

        var linkUrl = '../BalancePdfServlet?mode=BALANCE&dtStart=' + dtStart + '&dtEnd=' + dtEnd;
        window.open(linkUrl);
    },
    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);

    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getBalanceGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null

        };
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },

    doInitStore: function () {
        var me = this;
        me.getBalanceGrid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearch();
    },

    doSearch: function () {
        var me = this;
        me.getBalanceGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue()

            }
        });
    },
    buildSummary: function (rec) {
        var me = this;
        me.getMontantTTC().setValue(rec.montantTTC);
        me.getMontantAchat().setValue(rec.montantAchat);
        me.getRatioVA().setValue(rec.ratioVA);
        me.getFondCaisse().setValue(rec.fondCaisse);
        me.getMontantRegDiff().setValue(rec.montantRegDiff);
        me.getMontantRegleTp().setValue(rec.montantRegleTp);
        me.getMontantSortie().setValue(rec.montantSortie);
        me.getMontantEntre().setValue(rec.montantEntre);
        me.getMontantEsp().setValue(rec.montantEsp);
        me.getPanierMoyen().setValue(rec.panierMoyen);
        me.getNbreVente().setValue(rec.nbreVente);
        me.getMontantCheque().setValue(rec.montantCheque);
        me.getMontantVirement().setValue(rec.montantVirement);
        me.getMarge().setValue(rec.marge);
        me.getMontantMobilePayment().setValue(rec.montantMobilePayment);

    }
});