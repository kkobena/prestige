/* global Ext */

Ext.define('testextjs.controller.DepotListCtr', {
    extend: 'Ext.app.Controller',
    requires: [
        'testextjs.model.caisse.Vente'
    ],
    views: ['testextjs.view.vente.depot.DepotVenteList'],
    refs: [{
            ref: 'depotList',
            selector: 'ventedepot'
        },
        {
            ref: 'queryBtn',
            selector: 'ventedepot #rechercher'
        }, {
            ref: 'addBtn',
            selector: 'ventedepot #addBtn'
        },

        {
            ref: 'depotGrid',
            selector: 'ventedepot gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'ventedepot gridpanel pagingtoolbar'
        }

        , {
            ref: 'typeVente',
            selector: 'ventedepot #typeVente'
        }

        , {
            ref: 'queryField',
            selector: 'ventedepot #query'
        }, {
            ref: 'btnImporter',
            selector: 'ventedepot #btnImporter'
        }


    ],
    init: function (application) {
        this.control({
            'ventedepot gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'ventedepot #rechercher': {
                click: this.doSearch
            },
            'ventedepot #typeVente': {
                select: this.doSearch
            },

            'ventedepot gridpanel': {
                viewready: this.doInitStore
            },
            "ventedepot gridpanel actioncolumn": {
                click: this.handleActionColumn
            },
            'ventedepot #query': {
                specialkey: this.onSpecialKey
            },
            'ventedepot #addBtn': {
                click: this.onAddClick
            },
            'ventedepot #btnImporter': {
                click: this.importLit
            }
        });
    },
    importLit: function () {
        var me = this;
        me.createFormImporter();

    },
    handleActionColumn: function (view, rowIndex, colIndex, item, e) {
        var me = this;
        var store = me.getDepotGrid().getStore(),
                rec = store.getAt(colIndex);
        if (parseInt(item) === 10) {
            me.onDelete(rec.get('lgPREENREGISTREMENTID'));
        } else if (parseInt(item) === 9) {
            me.onEdite(rec);
        }
    }, onAddClick: function () {
        var xtype = "addventedepotbis";
        var data = {'isEdit': false, 'record': {}};
        testextjs.app.getController('App').onRedirectTo(xtype, data);

    },

    onDelete: function (id) {
        var me = this;
        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            method: 'POST',
//            headers: {'Content-Type': 'application/json'},
            url: '../api/v1/ventestats/remove/' + id,
//            params: Ext.JSON.encode(params),
            success: function (response, options) {
                progress.hide();
                var result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    me.doSearch();
                } else {
                    Ext.MessageBox.show({
                        title: 'Message d\'erreur',
                        width: 320,
                        msg: "L'opération a échouée",
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR

                    });
                }
            },
            failure: function (response, options) {
                progress.hide();
                Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
            }

        });
    },

    onEdite: function (rec) {
        var data = {'isEdit': true, 'record': rec.data};
        var xtype = "addventedepotbis";
        testextjs.app.getController('App').onRedirectTo(xtype, data);
    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getDepotGrid().getStore().getProxy();

        myProxy.params = {
            query: null,
            typeVenteId: null,
            statut: 'is_Process'

        };
        myProxy.setExtraParam('statut', 'is_Process');
        myProxy.setExtraParam('query', me.getQueryField().getValue());
        myProxy.setExtraParam('typeVenteId', me.getTypeVente().getValue());

    },

    doInitStore: function () {
        var me = this;
        me.doSearch();

    },
    onSpecialKey: function (field, e, options) {
        if (e.getKey() === e.ENTER) {
            var me = this;
            me.doSearch();
        }
    },
    doSearch: function () {
        var me = this;

        me.getDepotGrid().getStore().load({
            params: {
                "statut": 'is_Process',
                "query": me.getQueryField().getValue(),
                'typeVenteId': me.getTypeVente().getValue()
            }
        });
    },
    createFormImporter: function () {
        var me = this;
        var depotstore = new Ext.data.Store({
            idProperty: 'lgEMPLACEMENTID',
            fields: [
                {name: 'lgEMPLACEMENTID', type: 'string'},
                {name: 'strNAME', type: 'string'}
            ],
            pageSize: null,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/magasin/find-by-type',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }
        });

        var form = Ext.create('Ext.window.Window',
                {
                    autoShow: true,
                    height: 210,
                    width: 500,
                    modal: true,
                    title: 'Importation des différents articles vendus par le dépôt',
                    closeAction: 'hide',
                    closable: false,
                    maximizable: false,
                    layout: {
                        type: 'fit'

                    },
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            ui: 'footer',
                            layout: {
                                pack: 'end',
                                type: 'hbox'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    text: 'Enregistrer',
                                    handler: function (btn) {
                                        var _this = btn.up('window'), _form = _this.down('form');
                                        if (_form.isValid()) {

                                            form.destroy();
                                        }

                                    }
                                },
                                {
                                    xtype: 'button',
                                    iconCls: 'cancelicon',
                                    handler: function (btn) {
                                        form.destroy();
                                    },
                                    text: 'Annuler'

                                }
                            ]
                        }
                    ],
                    items: [{
                            xtype: 'form',
                            bodyPadding: 10,
                            layout: {
                                type: 'fit'

                            },
                            items: [
                                {
                                    xtype: 'fieldset',
                                    bodyPadding: 10,
                                    defaults: {
                                        anchor: '100%'
                                    },
                                    collapsible: false,

                                    items: [
                                        {
                                            xtype: 'combobox',
                                            fieldLabel: 'Dépôts',
                                            name: 'lgEMPLACEMENTID',
                                            flex: 1,
                                            store: depotstore,
                                            valueField: 'lgEMPLACEMENTID',
                                            pageSize: null,
                                            displayField: 'strNAME',
                                            typeAhead: false,
                                            editable: false,
                                            allowBlank: false,
                                            queryMode: 'remote',
                                            margin: '10 0 0 0',
                                            emptyText: 'Choisir un dépôt...'
                                        }, {
                                            xtype: 'filefield',
                                            margin: '10 0 0 0',
                                            fieldLabel: 'Fichier CSV',
                                            emptyText: 'Fichier CSV',
                                            name: 'fichier',
                                            allowBlank: false,
                                            buttonText: 'Choisir un fichier CSV',
                                            flex: 1
                                        }
                                    ]
                                }
                            ]
                        }

                    ]
                });
    }
});