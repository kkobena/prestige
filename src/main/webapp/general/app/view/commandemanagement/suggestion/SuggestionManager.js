var url_services_data_suggestion = '../webservices/commandemanagement/suggestion/ws_data.jsp';
var url_services_transaction_suggestion = '../webservices/commandemanagement/suggestion/ws_transaction.jsp?mode=';
var Me;

var famille_id_search;

Ext.define('testextjs.view.commandemanagement.suggestion.SuggestionManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'suggestionmanager',
    id: 'suggestionmanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Suggestion',
        //'testextjs.view.commandemanagement.suggestion.action.add',
//        'testextjs.view.commandemanagement.suggestion.action.tranform',
        'Ext.ux.ProgressBarPager'

    ],
    title: 'Gestion des Suggestions de reapprovisionnement',
    plain: true,
    maximizable: true,
    tools: [{type: "pin"}],
    closable: true,
    frame: true,
    initComponent: function () {

        Me = this;

        var itemsPerPage = 20;
        var store_suggestion = new Ext.data.Store({
            model: 'testextjs.model.Suggestion',
            id: 'store_suggestionID',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_suggestion,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        Ext.apply(this, {
            width: 950,
            height: 580,
            store: store_suggestion,
            columns: [
                {
                    header: 'lg_SUGGESTION_ID',
                    dataIndex: 'lg_SUGGESTION_ID',
                    hidden: true,
                    flex: 1,
                    editor: {
                        allowBlank: false
                    }

                },
                {
                    xtype: 'rownumberer',
                    text: 'LG',
                    width: 45,
                    sortable: true/*,
                     locked: true*/
                },
//                {
//                    header: 'REF',
//                    dataIndex: 'str_REF',
//                    flex: 1
//                },
                {
                    header: 'CIP',
                    dataIndex: 'int_CIP',
                    flex: 1
                },
//                {
//                    header: 'EAN',
//                    dataIndex: 'int_EAN13',
//                    flex: 1
//                },
                {
                    header: 'Libelle',
                    dataIndex: 'str_NAME',
                    flex: 1
                },
                // QTE_STOCK
                {
                    header: 'Qte Stock',
                    dataIndex: 'QTE_STOCK',
                    flex: 1
                },
                // int_NUMBER
                {
                    header: 'Qte Suggeree',
                    dataIndex: 'int_NUMBER',
                    flex: 1
                },
                {
                    header: 'Prix Achat',
                    dataIndex: 'int_PRIX_ACHAT',
                    flex: 1
                },
                {
                    header: 'Prix Vente',
                    dataIndex: 'int_PRICE',
                    flex: 1
                },
                // lg_TYPESUGGESTION_ID
//                {
//                    header: 'Type',
//                    dataIndex: 'lg_TYPESUGGESTION_ID',
//                    flex: 1
//                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/page_white_edit.png',
                            tooltip: 'Transformer',
                            scope: this,
                            handler: this.onEditClick
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/delete.gif',
                            tooltip: 'Supprimer',
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
                    handler: this.onAddClick
                },
                '-',
                {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'suggestion',
                    emptyText: 'Rech'
                },
                {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    handler: this.onRechClick
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store_suggestion,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager()
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
    onEditClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

//        new testextjs.view.commandemanagement.suggestion.action.transform({
//            odatasource: '',
//            parentview: this,
//            mode: "create",
//            titre: "Transformer Suggestion::Stock"
//
//        });

    },
    onAddClick: function () {
//        new testextjs.view.commandemanagement.suggestion.action.add({
//            odatasource: '',
//            parentview: this,
//            mode: "create",
//            titre: "Ajouter une suggestion"
//
//        });

    },
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'confirmer la suppresssion',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Ajax.request({
                            url: url_services_transaction_suggestion + 'delete',
                            params: {
                                lg_SUGGESTION_ID: rec.get('lg_SUGGESTION_ID')
                            },
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Suppression ' + '[' + rec.get('str_REF') + ']', 'Impossible de Supprimer cette ligne');
                                    return;
                                } else {
                                    Ext.MessageBox.alert('Suppression ' + '[' + rec.get('str_REF') + ']', 'Suppression effectuee avec succes');
//                                    

                                }
                                grid.getStore().reload();
                            },
                            failure: function (response)
                            {

                                var object = Ext.JSON.decode(response.responseText, false);
                                //  alert(object);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    },
    onRechClick: function () {
        var val = Ext.getCmp('rechecher');
        this.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_suggestion);
    }

});