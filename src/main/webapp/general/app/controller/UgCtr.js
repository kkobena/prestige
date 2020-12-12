/* global Ext */

Ext.define('testextjs.controller.UgCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.actions.Ug'],
    refs: [{
            ref: 'venteugs',
            selector: 'venteugs'
        },
      
        {
            ref: 'uggid',
            selector: 'venteugs #uggid'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'venteugs #uggid pagingtoolbar'
        }

        , {
            ref: 'dtStart',
            selector: 'venteugs #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'venteugs #dtEnd'
        },
        {ref: 'rechercherButton',
            selector: 'venteugs #rechercher'

        }, 
        {
            ref: 'montantAchat',
            selector: 'venteugs #montantAchat'
        },
       
        {
            ref: 'nbreVente',
            selector: 'venteugs #nbreVente'
        }
       
    ],
    init: function (application) {
        this.control({
            'venteugs #uggid pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'venteugs #rechercher': {
                click: this.doSearch
            },
           
            'venteugs #uggid': {
                viewready: this.doInitStore
            }

        });
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
            dtStart: null

        };
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
                dtEnd: me.getDtEnd().getSubmitValue()

            }
        });
    },
    buildSummary: function (rec) {
        var me = this;
        
        me.getMontantAchat().setValue(rec.montantAchat);
        me.getNbreVente().setValue(rec.nbreVente);
    }
});