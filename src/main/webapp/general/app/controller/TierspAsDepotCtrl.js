/* global Ext */

Ext.define('testextjs.controller.TierspAsDepotCtrl', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.Dashboard.TierspAsDepot'],
    refs: [{
            ref: 'tierpayantasdepot',
            selector: 'tierpayantasdepot'
        },

        {
            ref: 'queryCarnet',
            selector: 'tierpayantasdepot #carnetGrid #queryCarnet'
        },
        {
            ref: 'query',
            selector: 'tierpayantasdepot #toExcludeGrid #query'
        },
        {
            ref: 'carnetGrid',
            selector: 'tierpayantasdepot #carnetGrid'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'tierpayantasdepot #carnetGrid pagingtoolbar'
        }
        ,
        {
            ref: 'toExcludeGrid',
            selector: 'tierpayantasdepot #toExcludeGrid'
        }



    ],
    init: function (application) {
        this.control({
            'tierpayantasdepot #carnetGrid pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'tierpayantasdepot #toExcludeGrid pagingtoolbar': {
                beforechange: this.doBeforechange2
            },
            'tierpayantasdepot #toExcludeGrid #rechercher': {
                click: this.doSearch2
            },
            'tierpayantasdepot #carnetGrid #rechercherCarnet': {
                click: this.doSearch
            },

            'tierpayantasdepot #carnetGrid #queryCarnet': {
                specialkey: this.onSpecialKey
            },
            'tierpayantasdepot #toExcludeGrid #query': {
                specialkey: this.onSpecialKey2
            },
            'tierpayantasdepot #carnetGrid': {
                viewready: this.doInitStore
            },
            'tierpayantasdepot #toExcludeGrid': {
                viewready: this.doInitStore2
            },
            'tierpayantasdepot #carnetGrid [xtype=checkcolumn]': {
                checkchange: this.onCheckChange
            },
             'tierpayantasdepot #toExcludeGrid [xtype=checkcolumn]': {
                checkchange: this.onCheckChange2
            }


        });
    },
    onCheckChange2: function (column, rowIndex, checked) {
        let me = this;
        let record = me.getToExcludeGrid().getStore().getAt(rowIndex);
        Ext.Ajax.request({
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v2/tiers-payant/exclure-inclure/' + record.data.id + '/' + checked,
            success: function (response, options) {
                me.getToExcludeGrid().getStore().reload();
            },
            failure: function (response, options) {
                Ext.Msg.alert("Message", 'Erreur  : [code erreur : ' + response.status + ' ]');
                me.getToExcludeGrid().getStore().reload();
            }
        });
    },
    onCheckChange: function (column, rowIndex, checked) {
        let me = this;
        let record = me.getCarnetGrid().getStore().getAt(rowIndex);
        Ext.Ajax.request({
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v2/carnet-depot/exclure-inclure/' + record.data.id + '/' + checked,
            success: function (response, options) {
                me.getCarnetGrid().getStore().reload();
            },
            failure: function (response, options) {
                Ext.Msg.alert("Message", 'Erreur  : [code erreur : ' + response.status + ' ]');
                me.getCarnetGrid().getStore().reload();
            }
        });
    },

    onSpecialKey: function (field, e, options) {
        var me = this;
        if (e.getKey() === e.ENTER) {
            me.doSearch();
        }
    },
    onSpecialKey2: function (field, e, options) {
        var me = this;
        if (e.getKey() === e.ENTER) {
            me.doSearch2();
        }
    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getCarnetGrid().getStore().getProxy();
        myProxy.params = {
            query: ''
        };
        let queryCarnet = me.getQueryCarnet().getValue();
        myProxy.setExtraParam('query', queryCarnet);
    },

    doBeforechange2: function (page, currentPage) {
        var me = this;
        var myProxy = me.getToExcludeGrid().getStore().getProxy();
        myProxy.params = {
            query: ''
        };
        let queryCarnet = me.getQuery().getValue();
        myProxy.setExtraParam('query', queryCarnet);
    },

    doInitStore: function () {
        var me = this;
        me.doSearch();
    },
    doInitStore2: function () {
        var me = this;
        me.doSearch2();
    },
    doSearch: function () {
        var me = this;
        const queryCarnet = me.getQueryCarnet().getValue();
        me.getCarnetGrid().getStore().load({
            params: {
                query: queryCarnet
            }
        });
    },
    doSearch2: function () {
        var me = this;
        const query = me.getQuery().getValue();
        me.getToExcludeGrid().getStore().load({
            params: {
                query: query
            }
        });
    }


});