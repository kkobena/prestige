/* global Ext */


var products;

Ext.define('testextjs.view.configmanagement.famille.Products', {
    extend: 'Ext.grid.Panel',
    xtype: 'produitsxx',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Famille'
    ],
    title: 'Gestion des Articles',
    plain: true,
    frame: true,
    initComponent: function () {
        products = this;
        var itemsPerPage = 20;
        var filtre = Ext.create('Ext.data.Store', {
            data: [
                {
                    "libelle": "Produits cochés", "valeur": "Y"
                },
                {
                    "libelle": "Produits non cochés", "valeur": "N"
                },
                {
                    "libelle": "Tous", "valeur": "A"
                }
            ],
            fields: [{name: 'libelle', type: 'string'}, {name: 'valeur', type: 'string'}]
        });
        var rayons = Ext.create('Ext.data.Store', {
            idProperty: 'id',
            fields:
                    [
                        {name: 'id',
                            type: 'string'

                        },

                        {name: 'libelle',
                            type: 'string'

                        }

                    ],
            autoLoad: false,
            pageSize: 9999,
            proxy: {
                type: 'ajax',
                url: '../api/v1/common/rayons',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }

            }

        });
        var store = new Ext.data.Store({
            idProperty: 'lgFAMILLEID',
            fields: [
                {
                    name: 'lgFAMILLEID',
                    type: 'string'
                },

                {
                    name: 'intCIP',
                    type: 'string'
                }, {
                    name: 'strNAME',
                    type: 'string'
                },
                {
                    name: 'intPAF',
                    type: 'number'
                }, {
                    name: 'intPRICE',
                    type: 'number'
                },
                {
                    name: 'stock',
                    type: 'number'
                },
                {
                    name: 'boolACCOUNT',
                    type: 'boolean'
                }

            ],
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/fichearticle/account',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        Ext.apply(this, {
            width: '98%',
            height: valheight,
            store: store,
            id: 'produitsxxID',
            columns: [
                {
                    header: 'lgFAMILLEID',
                    dataIndex: 'lgFAMILLEID',
                    hidden: true,
                    flex: 1
                },
                /*      {
                 
                 header: 'Etat.cmde',
                 dataIndex: 'STATUS',
                 renderer: function (v, m, r) {
                 var STATUS = r.data.STATUS;
                 switch (STATUS) {
                 case 1:
                 m.style = 'background-color:#73C774;';
                 break;
                 case 2:
                 m.style = 'background-color:#5fa2dd;';
                 break;
                 case 3:
                 m.style = 'background-color:#f98012;';
                 break;
                 case 4:
                 m.style = 'background-color:#a62a3e;';
                 break;
                 default:
                 m.style = 'background-color:#d4d4d4;';
                 break;
                 }
                 
                 
                 return v;
                 },
                 width: 35
                 },
                 */
                {
                    header: 'CIP',
                    dataIndex: 'intCIP',
                    flex: 0.7

                },
                {
                    header: 'Designation',
                    dataIndex: 'strNAME',
                    flex: 2.5

                },
                {
                    header: 'Prix Vente',
                    dataIndex: 'intPRICE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.7

                },
                {
                    header: 'Prix Achat F',
                    dataIndex: 'intPAF',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.7

                },
                {
                    header: 'Stock',
                    dataIndex: 'stock',
                    align: 'center',
                    flex: 0.7

                },
                {
                    xtype: 'checkcolumn',
                    header: ' ',
                    dataIndex: 'boolACCOUNT',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    listeners: {checkchange: function (scr, rowIndex, checked, eOpts) {
                            let rec = Ext.getCmp('produitsxxID').getStore().getAt(rowIndex);
                            Ext.Ajax.request({
                                method: 'PUT',
                                headers: {'Content-Type': 'application/json'},
                                params: Ext.JSON.encode({checkug: checked}),
                                url: '../api/v1/fichearticle/account/' + rec.get("lgFAMILLEID"),
                                success: function (response, options) {
                                    var result = Ext.JSON.decode(response.responseText, true);
                                    if (result.success) {
                                        Ext.getCmp('produitsxxID').getStore().reload();
                                    }
                                },
                                failure: function (response, options) {
                                    Ext.Msg.alert("Message", "L'opération a échoué " + response.status);
                                }

                            });

                        }}
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    xtype: 'textfield',
                    id: 'rechecherProductxx',
                    emptyText: 'Recherche',
                    width: 350,
                    listeners: {
                        specialKey: function (field, e, c) {
                            if (e.getKey() === e.ENTER) {
                                products.onRechClick();
                            }
                        }
                    }

                }, '-',

                {
                    xtype: 'combobox',
                    flex: 1,
                    margin: '0 5 0 0',
                    labelWidth: 5,
                    id: 'rayons',
                    store: rayons,
                    pageSize: 99999,
                    valueField: 'id',
                    displayField: 'libelle',
                    typeAhead: false,
                    queryMode: 'remote',
                    minChars: 2,
                    emptyText: 'Sélectionnez un emplacement',
                    listeners: {
                        select: function (cmp) {
                            products.onRechClick();
                        }
                    }
                },
                {
                    xtype: 'tbseparator'
                },

                {
                    xtype: 'combobox',

                    labelWidth: 65,
                    id: 'filtres',
                    store: filtre,
                    flex: 1,
                    valueField: 'valeur',
                    displayField: 'libelle',
                    typeAhead: false,
                    mode: 'local',
                    value: 'A',
                    minChars: 1,
                    listeners: {
                        select: function (cmp) {
                            products.onRechClick();
                        }
                    }


                }, {
                    xtype: 'tbseparator'
                },
                {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    iconCls: 'searchicon',
                    handler: this.onRechClick
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: itemsPerPage,
                store: store,
                displayInfo: true,
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            query: ''
                        };
                        var search_value = Ext.getCmp('rechecherProductxx').getValue();
                        myProxy.setExtraParam('query', search_value);
                    }

                }
            }

        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });

    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {

    },
    onRechClick: function () {
        let val = Ext.getCmp('rechecherProductxx');
        let rayon = Ext.getCmp('rayons').getValue();
        let filtres = Ext.getCmp('filtres').getValue();
        if (rayon == null) {
            rayon = '';
        }
        if (filtres == null) {
            filtres = '';
        }
        Ext.getCmp('produitsxxID').getStore().load({
            params: {
                query: val.getValue(),
                rayon: rayon,
                filtre: filtres
            }
        });
    }
});