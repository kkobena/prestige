/* global Ext */
var Me;

var myAppController;
var doublonsEmStore;
var doublonOFStore, store_emplacement;

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.actions.Doublons', {
    extend: 'Ext.grid.Panel',
    xtype: 'doublons',
    id: 'doublonsID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'Ext.ux.ProgressBarPager',
        'testextjs.model.Doublons',
        'testextjs.model.Emplacemnt'


    ],
    title: 'Gestion des doublons ',
    frame: true,
    width: "98%",
    height: 580,
    initComponent: function () {
        Me = this;
        var _this = this;
        myAppController = Ext.create('testextjs.controller.App', {});
        doublonOFStore = new Ext.data.Store({
            model: 'testextjs.model.Doublons',
            pageSize: 40,
            proxy: {
                type: 'ajax',
                url: '../Doublons?action=officine',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });

        store_emplacement = new Ext.data.Store({
            model: "testextjs.model.Emplacemnt",
            pageSize: 20,
            autoLoad: true,
            proxy: {
                type: "ajax",
                url: '../webservices/ws_emplacement.jsp',
                reader: {
                    type: "json",
                    root: "data",
                    totalProperty: "total"
                }
            }

        });

        doublonsEmStore = new Ext.data.Store({
            model: 'testextjs.model.Doublons',
            pageSize: 30,
            proxy: {
                type: 'ajax',
                url: '../Doublons?action=stock',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });

        _this.store = doublonOFStore;
        _this.columns = _this.buildDetailsColumns();
        _this.dockedItems = _this.buildDocked();
        this.callParent();



    },
    buildDocked: function () {
        return [
            {xtype: 'toolbar',
                dock: 'top',
//                padding: '8',
                items: [

                    {
                        xtype: 'combo',
                        emptyText: 'Actions',
                        labelWidth: 50,
                        flex: 1,
                        id: 'cmbomode',
                        valueField: 'lgEMPLACEMENTID',
                        displayField: 'strNAME',

                        store: store_emplacement,
                        listeners: {
                            select: function (cmd) {

                                var me = Me,search = Ext.getCmp('rechecherDoublon').getValue(),
                                        grid = Ext.getCmp('doublonsID'), pagingbar = Ext.getCmp('balanceGridpagingbar'),
                                        query = cmd.getValue(), printInvoicereport2pxw = Ext.getCmp('printInvoicereport2pxw'), printInvoicereport = Ext.getCmp('printInvoicereport');

                                if (query === '1') {
//                                    doublonOFStore.loadPage(1);
                                    grid.reconfigure(doublonOFStore, me.buildDetailsColumns());
                                    pagingbar.bindStore(doublonOFStore);
                                } else {
//                                    doublonsEmStore.loadPage(1);
                                    grid.reconfigure(doublonsEmStore, me.buildGroupColumns());
                                    pagingbar.bindStore(doublonsEmStore);
                                }

                                //  var val = Ext.getCmp('rechecherFacture').getValue();

                                grid.getStore().load({
                                    params: {
                                        lgEMPLACEMENTID: this.getValue(),
                                        search:search
                                    }});

                            }
                        }

                    }, '',
                    {
                        xtype: 'textfield',
                        id: 'rechecherDoublon',
                        flex: 1,
                        emptyText: 'Rech',
                        listeners: {
                            specialKey: function (field, e) {

                                if (e.getKey() === e.ENTER) {

                                    var val = field.getValue();
                                    var cmbomode = "";


                                    if (Ext.getCmp('cmbomode').getValue() !== null && Ext.getCmp('cmbomode').getValue() !== "") {
                                        cmbomode = Ext.getCmp('cmbomode').getValue();
                                    }

                                    Ext.getCmp('doublonsID').getStore().load({
                                        params: {
                                            lgEMPLACEMENTID: cmbomode,
                                            search: val
                                        }});


                                }

                            }
                        }
                    }

                ]
            },
            {
                dock: 'bottom',
                xtype: 'pagingtoolbar',
                pageSize: 30,
                id: 'balanceGridpagingbar',
                store: this.store,
                displayInfo: true,
                displayMsg: 'Données affichées {0} - {1} sur {2}',
                emptyMsg: "Pas de donnée à afficher",
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            lgEMPLACEMENTID: '',
                            search: ''

                        };
                        var cmbomode = '', search = Ext.getCmp('rechecherDoublon').getValue();

                        if (Ext.getCmp('cmbomode').getValue() !== null && Ext.getCmp('cmbomode').getValue() !== "") {
                            lgEMPLACEMENTID = Ext.getCmp('cmbomode').getValue();
                        }


                        myProxy.setExtraParam('search', search);
                        myProxy.setExtraParam('lgEMPLACEMENTID', cmbomode);

                    }

                }
            }];
    },

    buildDetailsColumns: function () {
        return [
            {
                header: '#',
                dataIndex: 'lg_FAMILLE_ID',
                hidden: true,
                width: 0.3
            }, {
                header: 'CIP',
                dataIndex: 'CIP',

                flex: 1
            }, {
                header: 'Libellé',
                dataIndex: 'str_LIB',
                flex: 2

            }, {
                header: 'Prix.Achat',
                dataIndex: 'PA',
                align: 'right',
                renderer: amountformat,
                flex: 0.5
            }, {
                header: 'Prix.U',
                align: 'right',
                dataIndex: 'PU',
                renderer: amountformat,
                flex: 0.5
            }, {
                header: 'Stock',
                dataIndex: 'STOCK',
                renderer: amountformat,
                flex: 0.5,
                align: 'right'
            }
            , {
                header: 'Date.Création',
                dataIndex: 'DATECREATION',
                flex: 1

            }
            , {
                header: 'Date.dernière.entrée',
                dataIndex: 'DATEENTREE',
                flex: 1

            }, {
                header: 'Date.Vente',
                dataIndex: 'DATEVENTE',
                flex: 1

            }, {
                header: 'Date.Inventaire',
                dataIndex: 'DATEINVENTAIRE',
                flex: 1

            }
            ,
            {
                xtype: 'actioncolumn',
                width: 30,
                sortable: false,
                menuDisabled: true,
                items: [{
                        icon: 'resources/images/icons/fam/delete.png',
                        tooltip: 'Désactiver',
                        scope: this,
                        handler: this.onUpdate
                    }]
            }

        ];
    },
    reconfigureBalancegrid:
            function () {

                var me = this,
                        grid = Ext.getCmp('doublonsID'), pagingbar = Ext.getCmp('balanceGridpagingbar'), query = Ext.getCmp('cmbomode').getValue();
                if (query === '1') {
                    doublonOFStore.loadPage(1);
                    grid.reconfigure(doublonOFStore, me.buildDetailsColumns());
                    pagingbar.bindStore(doublonOFStore);
                } else {
                    doublonsEmStore.loadPage(1);
                    grid.reconfigure(doublonsEmStore, me.buildGroupColumns());
                    pagingbar.bindStore(doublonsEmStore);
                }


            },
    buildGroupColumns: function () {
        return [

            {
                header: '#',
                dataIndex: 'lg_FAMILLESTOCK_ID',
                hidden: true,
                width: 0.3
            }, {
                header: 'CIP',
                dataIndex: 'CIP',

                flex: 1
            }, {
                header: 'Libellé',
                dataIndex: 'str_LIB',
                flex: 2

            }, {
                header: 'Prix.Achat',
                dataIndex: 'PA',
                align: 'right',
                renderer: amountformat,
                flex: 0.5
            }, {
                header: 'Prix.U',
                align: 'right',
                dataIndex: 'PU',
                renderer: amountformat,
                flex: 0.5
            }, {
                header: 'Stock',
                dataIndex: 'STOCK',
                renderer: amountformat,
                flex: 0.5,
                align: 'right'
            }
            , {
                header: 'Date.Création',
                dataIndex: 'DATECREATION',
                flex: 1

            }
            , {
                header: 'Date.dernière.entrée',
                dataIndex: 'DATEENTREE',
                flex: 1

            }, {
                header: 'Date.Vente',
                dataIndex: 'DATEVENTE',
                flex: 1

            }, {
                header: 'Date.Inventaire',
                dataIndex: 'DATEINVENTAIRE',
                flex: 1

            }

            ,
            {
                xtype: 'actioncolumn',
                width: 30,
                sortable: false,
                menuDisabled: true,
                items: [{
                        icon: 'resources/images/icons/fam/delete.png',
                        tooltip: 'Désactiver',
                        scope: this,
                        handler: this.onUpdateStock
                    }]
            }



        ];
    },

    onUpdate: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        Ext.Ajax.request({
            url: '../Doublons',
            params: {
                action: 'update',
                lg_FAMILLE_STOCK_ID: rec.get('lg_FAMILLE_ID')
            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                grid.getStore().reload();

            },
            failure: function (response)
            {

            }

        });
    },

    onUpdateStock: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        Ext.Ajax.request({
            url: '../Doublons',
            params: {
                action: 'updateStock',
                lg_FAMILLE_STOCK_ID: rec.get('lg_FAMILLESTOCK_ID')
               


            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                grid.getStore().reload();

            },
            failure: function (response)
            {

            }

        });
    }

});