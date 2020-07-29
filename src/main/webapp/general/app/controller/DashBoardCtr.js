/* global Ext */

Ext.define('testextjs.controller.DashBoardCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.TableauOld'],
    refs: [{
            ref: 'tableauBordPharmacienManager',
            selector: 'tableauBordPharmacienManager'
        },
        {
            ref: 'imprimerBtn',
            selector: 'tableauBordPharmacienManager #imprimer'
        },

        {
            ref: 'tableauGrid',
            selector: 'tableauBordPharmacienManager gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'tableauBordPharmacienManager gridpanel pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'tableauBordPharmacienManager #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'tableauBordPharmacienManager #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'tableauBordPharmacienManager #rechercher'

        }, {
            ref: 'montantNet',
            selector: 'tableauBordPharmacienManager #montantNet'
        }, {
            ref: 'montantAchat',
            selector: 'tableauBordPharmacienManager #montantAchat'
        },
        {
            ref: 'ratioVA',
            selector: 'tableauBordPharmacienManager #ratioVA'
        },
        {
            ref: 'ratioAV',
            selector: 'tableauBordPharmacienManager #ratioAV'
        },
        {
            ref: 'comboRation',
            selector: 'tableauBordPharmacienManager #comboRation'
        },
        {
            ref: 'nbreClient',
            selector: 'tableauBordPharmacienManager #nbreClient'
        },
         {
            ref: 'montantRemise',
            selector: 'tableauBordPharmacienManager #montantRemise'
        }


    ],
    init: function (application) {
        this.control({
            'tableauBordPharmacienManager gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'tableauBordPharmacienManager #rechercher': {
                click: this.doSearch
            },
            'tableauBordPharmacienManager #imprimer': {
                click: this.onPdfClick
            },

            'tableauBordPharmacienManager gridpanel': {
                viewready: this.doInitStore
            }

        });
    },
    onPdfClick: function () {
        var me = this;

        var dtStart = me.getDtStart().getSubmitValue();
        var dtEnd = me.getDtEnd().getSubmitValue();
        var ratio = true;
        var v=me.getComboRation().getValue().split('/');
        if(v[1]==='Achat'){
            ratio = false;
        }
        var linkUrl = '../BalancePdfServlet?mode=TABLEAUOLD&dtStart=' + dtStart + '&dtEnd=' + dtEnd+'&ratio='+ratio;
        window.open(linkUrl);
    },
    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);

    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getTableauGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null

        };
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },

    doInitStore: function () {
        var me = this;
        me.getTableauGrid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearch();
    },

    doSearch: function () {
        var me = this;
        console.log(me.getTableauGrid());
        me.getTableauGrid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue()

            }
        });
    },
    buildSummary: function (rec) {
        var me = this;
        me.getMontantNet().setValue(rec.montantNet);
        me.getMontantAchat().setValue(rec.montantAchat);
        me.getRatioVA().setValue(rec.ratioVA);
        me.getRatioAV().setValue(rec.rationAV);
        me.getMontantRemise().setValue(rec.montantRemise);
        me.getNbreClient().setValue(rec.nbreVente);
    }
});