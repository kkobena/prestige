/* global Ext */

Ext.define('testextjs.controller.RecapCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.Recap'],
    refs: [{
            ref: 'recap',
            selector: 'recap'
        }
        , {
            ref: 'dtStart',
            selector: 'recap #dtStart'
        },

        {
            ref: 'dtEnd',
            selector: 'recap #dtEnd'
        },
        {
            ref: 'montantNet',
            selector: 'recap #montantNet'
        }, {
            ref: 'montantTTC',
            selector: 'recap #montantTTC'
        },
        {
            ref: 'marge',
            selector: 'recap #marge'
        }, {
            ref: 'montantHT',
            selector: 'recap #montantHT'
        },
        {
            ref: 'montantTVA',
            selector: 'recap #montantTVA'
        },
        {
            ref: 'montantCredit',
            selector: 'recap #montantCredit'
        }, {
            ref: 'montantRemise',
            selector: 'recap #montantRemise'
        }
        , {
            ref: 'montantEsp',
            selector: 'recap #montantEsp'
        }
        , {
            ref: 'montantTotalHT',
            selector: 'recap #montantTotalHT'
        }
        , {
            ref: 'montantTotalTVA',
            selector: 'recap #montantTotalTVA'
        }
        , {
            ref: 'montantTotalTTC',
            selector: 'recap #montantTotalTTC'
        }
        , {
            ref: 'queryRgl',
            selector: 'recap #queryRgl'
        },
        {
            ref: 'query',
            selector: 'recap #query'
        },
        {
            ref: 'reglementGrid',
            selector: 'recap #reglementGrid'
        },
        {
            ref: 'creditaccorde',
            selector: 'recap #creditaccorde'
        },
        {
            ref: 'totalnb',
            selector: 'recap #totalnb'
        },
        {
            ref: 'totalmontant',
            selector: 'recap #totalmontant'
        },
        {
            ref: 'totalnbclient',
            selector: 'recap #totalnbclient'
        },
        {
            ref: 'recette',
            selector: 'recap #recette'
        },
        {
            ref: 'reglement',
            selector: 'recap #reglement'
        },
        {
            ref: 'ratio',
            selector: 'recap #ratio'
        },
        {
            ref: 'achatGrid',
            selector: 'recap #achatGrid'
        }

    ],
    init: function (application) {
        this.control({
            'recap': {
                render: this.onReady
            },
            'recap #reglementGrid pagingtoolbar': {
                beforechange: this.doBeforechangeRegle
            },
            'recap #creditaccorde pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'recap #rechercher': {
                click: this.doSearch
            },
            'recap #creditbtn': {
                click: this.doSearchCredit
            },
            'recap #reglebtn': {
                click: this.doSearchRecgl
            },
            'recap #imprimer': {
                click: this.onPdfClick
            },
            'recap #query': {
                specialkey: this.onCreditKey
            },
            'recap #queryRgl': {
                specialkey: this.onSpecialSpecialKey
            }
        });
    },
    onPdfClick: function () {
        var me = this;
        var dtStart = me.getDtStart().getSubmitValue();
        var dtEnd = me.getDtEnd().getSubmitValue();
        var linkUrl = '../BalancePdfServlet?mode=RECAP&dtStart=' + dtStart + '&dtEnd=' + dtEnd;
        window.open(linkUrl);
    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getCreditaccorde().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            query: null

        };
        myProxy.setExtraParam('query', me.getQuery().getValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },

    doBeforechangeRegle: function (page, currentPage) {
        var me = this;
        var myProxy = me.getReglementGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            query: null

        };
        myProxy.setExtraParam('query', me.getQueryRgl().getValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },
    doSearchRecgl: function () {
        var me = this;
        me.getReglementGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue(),
                query: me.getQueryRgl().getValue()

            }
        });

    },
    doSearchCredit: function () {
        var me = this,totalnb=me.getTotalnb(),
                totalmontant=me.getTotalmontant(),totalnbclient=me.getTotalnbclient();
        var valueThree=0,valueTwo=0,value=0;
        me.getCreditaccorde().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue(),
                query: me.getQuery().getValue()

            }, callback: function (records, operation, successful) {
                records.forEach(function (e) {
                    valueThree+=e.get('valueThree');
                    valueTwo+=e.get('valueTwo');
                    value+=e.get('value');
                    
                });
                totalnb.setValue(valueThree);
                totalnbclient.setValue(valueTwo);
                totalmontant.setValue(value);
            }
        });

    },
    doSearch: function () {
        var me = this;
        me.doSearchRecgl();
        me.doSearchCredit();
        me.buildSummary();
    },
    buildSummary: function () {
        var me = this, achatGrid = me.getAchatGrid();
  var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            method: 'GET',
            url: '../api/v1/recap/dashboard',
            timeout: 2400000,
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue()
            },
            success: function (response, options) {
                progress.hide();
                var result = Ext.JSON.decode(response.responseText, true);
                var rec = result.data;
                me.getMontantNet().setValue(rec.montantNet);
                me.getMontantCredit().setValue(rec.montantCredit);//pourcentageEsp
                me.getMontantEsp().setValue(rec.montantEsp);
                me.getMontantTTC().setValue(rec.montantTTC);
                me.getMontantRemise().setValue(rec.montantRemise);
                me.getMontantHT().setValue(rec.montantHT);
                me.getMontantTVA().setValue(rec.montantTVA);
                me.getMarge().setValue(rec.marge);
                me.getMontantTotalHT().setValue(rec.montantTotalHT);
                me.getMontantTotalTVA().setValue(rec.montantTotalTVA);
                me.getMontantTotalTTC().setValue(rec.montantTotalTTC);
                me.getRatio().setValue(rec.ratio);
                me.buildRecette(rec.reglements);
                me.buildMvts(rec.mvtsCaisse, rec.montantTotalMvt);
                achatGrid.getStore().loadData(rec.achats);

            }, failure: function (response, options) {
                    progress.hide();
                  
                }

        });
    },
    buildRecette: function (recette) {
        var me = this, cmp = me.getRecette();
        var items = [];
        recette.forEach(function (e) {
            items.push({
                xtype: 'displayfield',
                fieldLabel: e.ref,
                labelWidth: 100,
                flex: 1,
                value: e.value,
                renderer: function (v) {
                    return Ext.util.Format.number(v, '0,000.');
                },
                fieldStyle: "color:blue;text-align:right;"
            });
        });
        cmp.removeAll(true);
        cmp.add(items);
    },

    buildMvts: function (mvt, montantTotalMvt) {
        var me = this, cmp = me.getReglement();
        var items = [];
        mvt.forEach(function (e) {
            items.push({
                xtype: 'displayfield',
                fieldLabel: e.ref,
                labelWidth: 130,

                flex: 1,
                value: e.value,
                renderer: function (v) {
                    return Ext.util.Format.number(v, '0,000.');
                },
                fieldStyle: "color:blue;text-align:right;"
            });
        });
        cmp.removeAll(true);
        cmp.add(items);
        cmp.add({
            xtype: 'displayfield',
            fieldLabel: "Total",
            flex: 1,
            value: montantTotalMvt,
            renderer: function (v) {
                return Ext.util.Format.number(v, '0,000.');
            },
            fieldStyle: "color:blue;text-align:right;"
        });
    },
    onSpecialSpecialKey: function (field, e, options) {
        if (e.getKey() === e.ENTER) {
            var me = this;
            me.doSearchRecgl();
        }
    },
    onCreditKey: function (field, e, options) {
        if (e.getKey() === e.ENTER) {
            var me = this;
            me.doSearchCredit();
        }
    },
    onReady: function () {
        var me = this;
        me.doSearch();

    }
});