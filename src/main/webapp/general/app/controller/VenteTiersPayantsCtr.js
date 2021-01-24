/* global Ext */

Ext.define('testextjs.controller.VenteTiersPayantsCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.vente.VenteTiersPayant'],
    refs: [{
            ref: 'tpventes',
            selector: 'tpventes'
        },

        {
            ref: 'uggid',
            selector: 'tpventes #uggid'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'tpventes #uggid pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'tpventes #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'tpventes #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'tpventes #rechercher'

        },
        {
            ref: 'montant',
            selector: 'tpventes #montant'
        },

        {
            ref: 'nbre',
            selector: 'tpventes #nbre'
        },

        {
            ref: 'groupTp',
            selector: 'tpventes #groupTp'
        },

        {
            ref: 'tpCmb',
            selector: 'tpventes #tpCmb'
        },
        {
            ref: 'query',
            selector: 'tpventes #query'
        },
       {
            ref: 'importGroup',
            selector: 'tpventes #importicon'
        } 


    ],
    init: function (application) {
        this.control({
            'tpventes #uggid pagingtoolbar': {
                beforechange: this.doBeforechange
            },
             'tpventes #groupTp': {
                select: this.doSearch
            },
            'tpventes #tpCmb': {
                select: this.doSearch
            },
            'tpventes #rechercher': {
                click: this.doSearch
            },
            'tpventes #imprimer': {
                click: this.onPdfClick
            },
             'tpventes #importicon': {
                click: this.onPdfGroup
            },
            
            'tpventes #uggid': {
                viewready: this.doInitStore
            }

        });
    },
    onPdfClick: function () {
        var me = this;
        let dtStart = me.getDtStart().getSubmitValue();
        let dtEnd = me.getDtEnd().getSubmitValue();
        let query = me.getQuery().getValue();
        let tiersPayantId = me.getTpCmb().getValue();
        let groupTp =  groupTp = me.getGroupTp().getValue();
        if (groupTp==null || groupTp== undefined) {
            groupTp = '';
        }
        if (tiersPayantId ==null || tiersPayantId == undefined) {
            tiersPayantId ='' ;
        }
        var linkUrl = '../SockServlet?mode=VENTE_TIERS_PAYANT&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&query=' + query + '&tiersPayantId=' + tiersPayantId + '&groupeId=' + groupTp;
        window.open(linkUrl);
    },
    doMetachange: function (store, meta) {
        var me = this;
        me.buildSummary(meta);

    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getUggid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null,
            groupeId: null,
            tiersPayantId: null,
            query: null

        };

        myProxy.setExtraParam('groupeId', me.getGroupTp().getValue());
        myProxy.setExtraParam('tiersPayantId', me.getTpCmb().getValue());
        myProxy.setExtraParam('query', me.getQuery().getValue());
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());

    },

    doInitStore: function () {
        var me = this;
        me.getUggid().getStore().addListener('metachange', this.doMetachange, this);
        me.doSearch();
    },

    doSearch: function () {
        var me = this;
        me.getUggid().getStore().load({
            params: {
                dtStart: me.getDtStart().getSubmitValue(),
                dtEnd: me.getDtEnd().getSubmitValue(),
                query: me.getQuery().getValue(),
                groupeId: me.getGroupTp().getValue(),
                tiersPayantId: me.getTpCmb().getValue()

            }
        });
    },
    buildSummary: function (rec) {
        var me = this;
        me.getMontant().setValue(rec.montant);
        me.getNbre().setValue(rec.nbre);
    },
    
    
     onPdfGroup: function () {
        var me = this;
        let dtStart = me.getDtStart().getSubmitValue();
        let dtEnd = me.getDtEnd().getSubmitValue();
        let query = me.getQuery().getValue();
        let tiersPayantId =me.getTpCmb().getValue();
        let groupTp = me.getGroupTp().getValue();
         if (groupTp==null || groupTp== undefined) {
            groupTp = '';
        }
        if (tiersPayantId ==null || tiersPayantId == undefined) {
            tiersPayantId ='' ;
         }
        
        var linkUrl = '../SockServlet?mode=VENTE_TIERS_PAYANT_GROUP&dtStart=' + dtStart + '&dtEnd=' + dtEnd
                + '&query=' + query + '&tiersPayantId=' + tiersPayantId + '&groupeId=' + groupTp;
        window.open(linkUrl);
    }
    
});