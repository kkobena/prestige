var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';
var url_services_pdf = '../webservices/commandemanagement/order/ws_generate_pdf.jsp';

var Me;
var store_order;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.cmde_passees.OrderPassManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'orderpassmanager',
    id: 'orderpassmanagerID',
    frame: true,
    animCollapse: false,
    title: 'Liste des commandes passees',
    plain: true,
    maximizable: true,
    closable: false,
    plugins: [{
            ptype: 'rowexpander',
            rowBodyTpl: new Ext.XTemplate(
                    '<p> {str_FAMILLE_ITEM}</p>',
                    {
                        formatChange: function (v) {
                            var color = v >= 0 ? 'green' : 'red';
                            return '<span style="color: ' + color + ';">' + Ext.util.Format.usMoney(v) + '</span>';
                        }
                    })
        }],
    initComponent: function () {

        url_services_pdf = '../webservices/commandemanagement/order/ws_generate_pdf.jsp';
        Me = this;
        let itemsPerPage = 20;
        const store_order = new Ext.data.Store({
            model: 'testextjs.model.Order',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',

                url: '../api/v1/commande/list/passees',
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
            height: Ext.getBody().getViewSize().height * 0.85,
            store: store_order,
            columns: [
                {
                    header: 'lg_ORDER_ID',
                    dataIndex: 'lg_ORDER_ID',
                    hidden: true,
                    flex: 1,
                    editor: {
                        allowBlank: false
                    }

                },
                {
                    xtype: 'rownumberer',
                    text: 'LG',
                    hidden: true,
                    width: 45,
                    sortable: true
                },
                {
                    header: 'Ref.',
                    dataIndex: 'str_REF_ORDER',
                    flex: 1
                },
                {
                    header: 'Grossiste',
                    dataIndex: 'str_GROSSISTE_LIBELLE',
                    flex: 1
                },
                {
                    header: 'Nombre de produits',
                    dataIndex: 'int_NBRE_PRODUIT',
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'Nombre de lignes',
                    dataIndex: 'int_LINE',
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'PRIX.ACHAT',
                    dataIndex: 'PRIX_ACHAT_TOTAL',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1

                },
                {
                    header: 'PRIX.VENTE',
                    dataIndex: 'PRIX_VENTE_TOTAL',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'Op&eacute;rateur',
                    dataIndex: 'lg_USER_ID',
                    align: 'center',
                    flex: 1
                },
                {
                    header: 'Date',
                    dataIndex: 'dt_CREATED',
                    flex: 1
                }, {
                    header: 'Heure',
                    dataIndex: 'dt_UPDATED',
                    flex: 1
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/folder_go.png',
                            tooltip: 'Reception de la commande',
                            scope: this,
                            handler: this.onLivraisonDetailsClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('str_STATUT') == 'is_Waiting') {
                                    return 'x-hide-display';
                                } else {
                                    return 'x-display-hide';

                                }
                            }
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/upload_icone.png',
                            tooltip: 'Importer la reponse du grossiste',
                            scope: this,
                            handler: this.onEditOrderByImportClick
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Imprimer',
                            scope: this,
                            handler: this.onbtnprint,
                            getClass: function (value, metadata, record) {
                                if (record.get('str_STATUT') == 'is_Waiting') {
                                    return 'x-hide-display';
                                } else {
                                    return 'x-display-hide';

                                }
                            }
                        }]
                },

                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/delete.gif',
                            tooltip: 'Annuler la commande',
                            scope: this,
                            handler: this.onRemoveClick
                        }]
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    text: 'Ajouter',
                    scope: this,
                    hidden: true,
                    handler: this.onAddClick
                }, {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'suggestion',
                    emptyText: 'Recherche',
                    listeners: {
                        'render': function (cmp) {
                            cmp.getEl().on('keypress', function (e) {
                                if (e.getKey() === e.ENTER) {
                                    Me.onRechClick();

                                }
                            });
                        }
                    }
                },
                {
                    text: 'rechercher',
                    iconCls: 'searchicon',
                    tooltip: 'rechercher',
                    scope: this,
                    handler: this.onRechClick
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: itemsPerPage,
                store: store_order,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager(),
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            search_value: ''
                        };
                        var search_value = Ext.getCmp('rechecher').getValue();
                        myProxy.setExtraParam('search_value', search_value);

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
        this.getStore().load();
    },

    onEditOrderByImportClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.commandemanagement.order.action.importOrder({
            odatasource: rec.data,
            parentview: this,
            mode: "importfileUpdate",
            titre: "Importation de la reponse de la commande  [" + rec.get('str_REF_ORDER') + "]"
        });
    },
    onLivraisonDetailsClick: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "livraisonDetail";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Rapprochement avec le Bon de Livraison", rec.get('lg_ORDER_ID'), rec.data);

    },
    onManageDetailsClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "ordermanagerlist";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Modifier les informations de la commande", rec.get('lg_ORDER_ID'), rec.data);
        //alert("test"+rec.get('lg_ORDER_ID'));
    },
    onAddClick: function () {
        var xtype = "ordermanagerlist";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Ajouter les articles a une commande", "0", "is_Waiting");
    },

    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Confirmer l\'annulation de cette commande',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        testextjs.app.getController('App').ShowWaitingProcess();
                        Ext.Ajax.request({
                            url: url_services_transaction_order + 'RollBackPasseOrderToCommandeProcess',
                            timeout: 2400000,
                            params: {
                                lg_ORDER_ID: rec.get('lg_ORDER_ID')
                            },
                            success: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success == "0") {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                } else {
                                    Ext.MessageBox.alert('Confirmation', object.errors);
                                    grid.getStore().reload();
                                }
                            },
                            failure: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });

                    }
                });

    },
    onRechClick: function () {
        const val = Ext.getCmp('rechecher');
        this.getStore().load({
            timeout: 240000,
            params: {
                query: val.getValue()
            }
        });
    },
    onbtnprint: function (grid, rowIndex) {

        const rec = grid.getStore().getAt(rowIndex);

        Ext.MessageBox.confirm('Message',
                'Imprimer le bon de commande?',
                function (btn) {
                    if (btn == 'yes') {
                        Me.onPdfClick(rec.get('lg_ORDER_ID'));

                    }
                });

    },
    onPdfClick: function (lg_ORDER_ID) {

        const linkUrl = url_services_pdf + '?lg_ORDER_ID=' + lg_ORDER_ID;

        window.open(linkUrl);

    }
});