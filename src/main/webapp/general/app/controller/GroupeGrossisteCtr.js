/* global Ext */

Ext.define('testextjs.controller.GroupeGrossisteCtr', {
    extend: 'Ext.app.Controller',
    views: ['testextjs.view.produits.GroupeGrossiste', 'testextjs.view.produits.form.GroupeGrossisteForm'],
    refs: [{
            ref: 'groupegrossistes',
            selector: 'groupegrossistes'
        },

        {
            ref: 'groupeGrid',
            selector: 'groupegrossistes gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'groupegrossistes gridpanel pagingtoolbar'
        },
        {ref: 'rechercherButton',
            selector: 'groupegrossistes #rechercher'

        },
        {ref: 'query',
            selector: 'groupegrossistes #query'

        },
        {ref: 'groupeGrossisteForm',
            selector: 'groupeGrossisteForm'

        },
        {ref: 'form',
            selector: 'groupeGrossisteForm form'

        },
           {ref: 'btnsave',
            selector: 'groupeGrossisteForm #btnsave'

        },
        {ref: 'btnCancel',
            selector: 'groupeGrossisteForm #btnCancel'

        }
        



    ],
    init: function (application) {
        this.control({
            'groupegrossistes gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'groupegrossistes #rechercher': {
                click: this.doSearch
            },

            'groupegrossistes gridpanel': {
                viewready: this.doInitStore
            }
            ,
            "groupegrossistes gridpanel actioncolumn": {
                remove: this.remove,
                editer: this.editer

            },
            'groupegrossistes #query': {
                specialkey: this.onSpecialKey
            },
            'groupegrossistes #addBtn': {
                click: this.add
            },
             'groupeGrossisteForm #btnsave': {
                click: this.saveRecord
            },
            'groupeGrossisteForm #btnCancel': {
                click: this.closeWindows
            }

        });
    },
    add: function () {
        var formwin = Ext.create('testextjs.view.produits.form.GroupeGrossisteForm');
        formwin.show();
    },
  closeWindows: function () {
        var me = this;
        me.getGroupeGrossisteForm().destroy();
    },
    saveRecord: function () {
        var me = this;
        form = me.getForm();
        if (form.isValid()) {
            let datas = form.getValues();
            var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
            Ext.Ajax.request({
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                url: '../api/v1/groupegrossiste/' ,
                params: Ext.JSON.encode(datas),
                success: function (response, options) {
                    progress.hide();
                    var result = Ext.JSON.decode(response.responseText, true);
                    if (result.success) {
                        me.closeWindows();
                        me.doSearch();
                    } else {
                        Ext.Msg.alert("Message", "L'opérateur a échouée");
                    }

                },
                failure: function (response, options) {
                    progress.hide();
                    Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
                }
            });
        }

    },

    editer: function (view, rowIndex, colIndex, item, e, record, row) {
        var me = this;
        var formwin = Ext.create('testextjs.view.produits.form.GroupeGrossisteForm');
        me.getForm().loadRecord(record);
        formwin.show();

    },

    onSpecialKey: function (field, e, options) {
        if (e.getKey() === e.ENTER) {
            var me = this;
            me.doSearch();
        }
    },
    remove: function (view, rowIndex, colIndex, item, e, record, row) {
        var me = this;
        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            method: 'DELETE',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v1/groupegrossiste/' + record.get('id'),
            success: function (response, options) {
                progress.hide();
                var result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    me.doSearch();
                } else {
                    Ext.Msg.alert("Message", "L'opérateur a échouée");
                }

            },
            failure: function (response, options) {
                progress.hide();
                Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
            }
        });

    },

    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getGroupeGrid().getStore().getProxy();
        myProxy.params = {
            query: null


        };
        var query = me.getQuery().getValue();
        myProxy.setExtraParam('query', query);

    },
    doInitStore: function () {
        var me = this;
        me.doSearch();
    },
    doSearch: function () {
        var me = this;
        var query = me.getQuery().getValue();
        me.getGroupeGrid().getStore().load({
            params: {
                query: query

            }
        });
    }

});